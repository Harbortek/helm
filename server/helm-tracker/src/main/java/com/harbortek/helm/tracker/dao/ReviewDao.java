/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.tracker.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.tracker.constants.ReviewStatusTypes;
import com.harbortek.helm.tracker.entity.review.ReviewEntity;
import com.harbortek.helm.tracker.vo.review.ReviewSearchVo;
import com.harbortek.helm.tracker.vo.review.ReviewStatus;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.JsonUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ReviewDao extends BaseJdbcDao {
    public Page<ReviewEntity> findByProjectId(Long projectId, ReviewSearchVo reviewSearchVo, Pageable pageable) {
        Criteria criteria = Criteria.empty();

        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ReviewEntity.Fields.projectId).is(projectId));
        if (ObjectUtils.isNotEmpty(reviewSearchVo)) {
            if (reviewSearchVo.getReviewer() != null) {
                criteria = criteria.and(Criteria.where(ReviewEntity.Fields.reviewers).is(reviewSearchVo.getReviewer()));
            }
            if (reviewSearchVo.getCreator() != null) {
                criteria = criteria.and(Criteria.where(BaseEntity.Fields.createBy).is(reviewSearchVo.getCreator()));
            }
            if (StringUtils.isNotEmpty(reviewSearchVo.getStatus())) {
                criteria = criteria.and(Criteria.where(ReviewEntity.Fields.status).is(reviewSearchVo.getStatus()));
            }
            if (StringUtils.isNotEmpty(reviewSearchVo.getTitle())) {
                criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%" + reviewSearchVo.getTitle() + "%"));
            }
        }
        Query query = Query.query(criteria);
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        query.with(pageable);

        return find(query,pageable, ReviewEntity.class);
    }

    public Page<ReviewEntity> findAll(ReviewSearchVo reviewSearchVo, Pageable pageable) {
        String sql = "select * from reviews where deleted = 'false' AND" +
                "(creator = :userId or json_search(reviewers,'one',(:reviewers)))";

        Map<String,Object> params = new HashMap<String, Object>();
        Long userId = SecurityUtils.getCurrentUser().getId();
        //登录人作为发起人或者登录人作为评审人
        params.put("userId", userId);
        params.put("reviewers", userId);

        if (ObjectUtils.isNotEmpty(reviewSearchVo)) {
            if (reviewSearchVo.getProjectId() != null) {
                sql+=" and project_id = "+reviewSearchVo.getProjectId();
            }
            if (StringUtils.isNotEmpty(reviewSearchVo.getStatus())) {
                sql+=" and status = '"+reviewSearchVo.getStatus()+"'";
            }
            if (StringUtils.isNotEmpty(reviewSearchVo.getTitle())) {
                sql+=" and name like '%"+reviewSearchVo.getTitle()+"%'";
            }
        }
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        sql+=" order by create_date desc";
        sql+=" limit "+pageable.getPageSize()*(pageable.getPageNumber())+","+pageable.getPageSize();

        List<ReviewEntity> query = find(sql, params, ReviewEntity.class);

        String sqlTotal = "select count(*) from reviews where deleted = 'false' AND" +
                "(creator = :userId or json_search(reviewers,'one',(:reviewers))) ";
        Long total = count(sqlTotal, params);
        if(ObjectUtils.isEmpty(total)){
            total = 0L;
        }
        return new PageImpl<>(query, pageable, total);
    }

    public ReviewEntity findOne(Long reviewId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).is(reviewId));
        Query query = Query.query(criteria);
        return findOne(query, ReviewEntity.class);
    }

    public List<ReviewEntity> findByName(String name) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).is(name));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        Query query = Query.query(criteria);
        return find(query, ReviewEntity.class);
    }

    public ReviewEntity updateReview(ReviewEntity reviewEntity) {
        if (reviewEntity.getId() == null) {
            if (!findByName(reviewEntity.getName()).isEmpty()) {
                throw new RuntimeException("评审名称重复！");
            }
            reviewEntity.setId(IDUtils.getId());
            reviewEntity.setStatus(ReviewStatusTypes.IN_PROGRESS);
//            reviewEntity.setCreateBy(SecurityUtils.getCurrentUser().getId());
            reviewEntity.setCreateDate(null);
            reviewEntity.setLastModifiedBy(SecurityUtils.getCurrentUser().getId());
            reviewEntity.setLastModifiedDate(new Date());
            reviewEntity.setDones(null);
            reviewEntity.setReviewStatuses(null);
            reviewEntity.setPassRate(null);
        }
        return save(reviewEntity);
    }

    public void deleteOne(Long reviewId) {
        markAsDeleted(reviewId, ReviewEntity.class);
    }

    public void updateReviewStatus(Long reviewId, String status) {
        getDslContext().update(getTable(ReviewEntity.class))
                       .set(getField(ReviewEntity.Fields.status), status)
                       .where(getField(BaseEntity.Fields.id).eq(reviewId))
                       .execute();

    }

    public void updateReviewDones(Long reviewId, Long userId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).is(reviewId));
        Query query = Query.query(criteria);
        ReviewEntity entity = findOne(query, ReviewEntity.class);
        if (!entity.getDones().contains(userId)) {
            entity.getDones().add(userId);
            save(entity);
        }
    }

    public void updateReviewersByReviewId(Long reviewId, List<Long> reviewers) {
//        Criteria criteria = Criteria.empty();
//        criteria.and(Criteria.where(BaseEntity.Fields.id).is(reviewId));
//        Query query = Query.query(criteria);
//
//        Update update = Update.from(new HashMap<>());
//        update.set(ReviewEntity.Fields.reviewers, reviewers);
//        updateFirst(query, update, ReviewEntity.class);

        getDslContext().update(getTable(ReviewEntity.class))
                .set(getField(ReviewEntity.Fields.reviewers), JsonUtils.toJSONString(reviewers))
                .where(getField(BaseEntity.Fields.id).eq(reviewId))
                .execute();
    }

    public void batchCreateReview(List<ReviewEntity> reviews) {
        saveAll(reviews);
    }
}
