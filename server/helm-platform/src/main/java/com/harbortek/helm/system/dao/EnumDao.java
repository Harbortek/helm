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
import com.harbortek.helm.system.entity.EnumItemEntity;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Slf4j
public class EnumDao extends BaseJdbcDao {


    public EnumItemEntity findById(Long enumId) {
        return findById(enumId, EnumItemEntity.class);
    }

    public List<EnumItemEntity> findEnums(Long categoryId, Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(EnumItemEntity.Fields.categoryId).is(categoryId));

        Criteria projectIdCriteria = null;
        if (ObjectUtils.isValid(projectId)) {
            projectIdCriteria = Criteria.where(EnumItemEntity.Fields.projectId).isNull().or(
                    Criteria.where(EnumItemEntity.Fields.projectId)
                            .is(projectId));
        } else {
            projectIdCriteria = Criteria.where(EnumItemEntity.Fields.projectId).isNull();
        }
        criteria = criteria.and(projectIdCriteria);
        Query query = Query.query(criteria);
        query.sort(Sort.by(EnumItemEntity.Fields.ordinary).ascending());

        return find(query, EnumItemEntity.class);
    }

    public List<EnumItemEntity> findEnumsByProjectId(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        criteria = criteria.and(Criteria.where(EnumItemEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        return find(query, EnumItemEntity.class);
    }

    public void updateEnums(Long categoryId, Long projectId, Collection<EnumItemEntity> enums) {

        List<EnumItemEntity> oldValues = findEnums(categoryId, projectId);
        List<EnumItemEntity> newValues = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(0);
        Long currentUserId = SecurityUtils.getCurrentUser().getId();
        enums.forEach(enumEntity -> {
            EnumItemEntity existed =
                    oldValues.stream().filter(s -> {return s.getId().equals(enumEntity.getId());}).findFirst()
                             .orElse(EnumItemEntity.builder().id(IDUtils.getId()).projectId(projectId)
                                                   .categoryId(categoryId).createBy(currentUserId)
                                                   .lastModifiedBy(currentUserId)
                                                   .lastModifiedDate(new Date()).build());
            existed.setName(enumEntity.getName());
            existed.setOrdinary(index.getAndIncrement());
            existed.setIcon(enumEntity.getIcon());
            existed.setColor(enumEntity.getColor());
            existed.setBackgroundColor(enumEntity.getBackgroundColor());
            newValues.add(existed);
        });

        List<EnumItemEntity> deleted = new ArrayList<>(CollectionUtils.subtract(oldValues, newValues));
        deleted.forEach(s -> s.setDeleted(true));
        newValues.addAll(deleted);

        if (!newValues.isEmpty()) {
            batchUpdateEnums(newValues);
        }
    }

    private void batchUpdateEnums(Collection<EnumItemEntity> enumEntities) {
        saveAll(enumEntities);
        CacheUtils.evict(ObjectUtils.ids(enumEntities), EnumItemEntity.class);
    }

    public void batchCreateEnums(Collection<EnumItemEntity> entities) {
        saveAll(entities);
    }

    public void deleteByCategoryId(Long categoryId) {
        markAsDeleted(categoryId, EnumItemEntity.class);
    }

    public Object findByCode(Long categoryId, String itemCode) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(EnumItemEntity.Fields.categoryId).is(categoryId));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(EnumItemEntity.Fields.code).is(itemCode));
        Query query = Query.query(criteria);
        return findOne(query, EnumItemEntity.class);
    }
}
