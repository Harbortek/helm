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

package com.harbortek.helm.system.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.system.entity.EnumCategoryEntity;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class EnumCategoryDao extends BaseJdbcDao {


    public EnumCategoryEntity createEnumCategory(EnumCategoryEntity entity) {
        return save(entity);
    }


    public EnumCategoryEntity updateEnumCategory(EnumCategoryEntity entity) {
        return save(entity);
    }

    public void deleteEnumCategory(Long categoryId) {
        markAsDeleted(categoryId, EnumCategoryEntity.class);
    }

    public EnumCategoryEntity findById(Long categoryId) {
        return findById(categoryId,EnumCategoryEntity.class);
    }

    public Page<EnumCategoryEntity> findEnumCategories(String keyword, Long projectId, Pageable pageable) {

        Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(false);
        if (StringUtils.isNotBlank(keyword)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%" + keyword + "%"));
        }

        if (projectId != null) {
            criteria = criteria.and(Criteria.where(EnumCategoryEntity.Fields.projectId).isNull().or(
                                                        Criteria.where(EnumCategoryEntity.Fields.projectId)
                                                                .is(projectId)));
        } else {
            criteria = criteria.and(Criteria.where(EnumCategoryEntity.Fields.projectId).isNull());
        }
        Query query = Query.query(criteria);
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        query.with(pageable);

        query.sort(Sort.by(Sort.Order.desc(EnumCategoryEntity.Fields.system),
                           Sort.Order.asc(BaseEntity.Fields.name)));

        return find(query,pageable, EnumCategoryEntity.class);
    }

    public boolean existCategory(String code) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(EnumCategoryEntity.Fields.code).is(code));
        Query query = Query.query(criteria);
        return exists(query, EnumCategoryEntity.class);
    }

    public Boolean checkExistsByName(Long projectId, String name) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).is(name));
        if (projectId != null) {
            criteria = criteria.and(Criteria.where(EnumCategoryEntity.Fields.projectId).is(projectId));
        }
        Query query = Query.query(criteria);
        return exists(query, EnumCategoryEntity.class);
    }

    public EnumCategoryEntity findByCode(Long projectId, String code) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(EnumCategoryEntity.Fields.code).is(code));
        if (ObjectUtils.isValid(projectId)) {
            criteria = criteria.and(Criteria.where(EnumCategoryEntity.Fields.projectId).isNull().or(
                                                        Criteria.where(EnumCategoryEntity.Fields.projectId)
                                                                .is(projectId)));
        } else {
            criteria = criteria.and(Criteria.where(EnumCategoryEntity.Fields.projectId).isNull());
        }
        Query query = Query.query(criteria);
        return findOne(query, EnumCategoryEntity.class);
    }

    public List<EnumCategoryEntity> findEnumCategoriesByProjectId(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        criteria = criteria.and(Criteria.where(EnumCategoryEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        return find(query, EnumCategoryEntity.class);
    }

    public void batchCreateEnumCategories(List<EnumCategoryEntity> enumCategoryEntities) {
        saveAll(enumCategoryEntities);
    }
}
