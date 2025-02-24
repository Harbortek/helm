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

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.tracker.entity.log.CommentEntity;
import com.harbortek.helm.tracker.vo.log.CommentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class CommentDao extends BaseJdbcDao {
    public CommentEntity createComment(CommentEntity comment) {
        return save(comment);
    }

    public void batchCreateComment(List<CommentEntity> commentList) {
        saveAll(commentList);
    }

    public void saveComment(CommentEntity comment) {
                save(comment);
    }

    public List<CommentEntity> findByObjectId(Long objectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(CommentEntity.Fields.objectId).is(objectId));
        criteria = criteria.and(Criteria.where(CommentEntity.Fields.replyToId).isNull());
        Query query = Query.query(criteria);
        return find(query, CommentEntity.class);
    }
    public List<CommentEntity> findByObjectIds(List<Long> objectIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(CommentEntity.Fields.objectId).in(objectIds));
        criteria = criteria.and(Criteria.where(CommentEntity.Fields.replyToId).isNull());
        Query query = Query.query(criteria);
        return find(query, CommentEntity.class);
    }

    public CommentEntity findById(Long id) {
        return findById(id,CommentEntity.class);
    }

    public void deleteComment(CommentVo comment) {
        markAsDeleted(comment.getId(), CommentEntity.class);
    }

}
