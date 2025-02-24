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
import com.harbortek.helm.tracker.entity.collection.CollectionEntity;
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
public class CollectionDao extends BaseJdbcDao {
    public CollectionEntity createCollection(CollectionEntity collection) {
        return save(collection);
    }

    public CollectionEntity updateCollection(CollectionEntity collection) {
        return save(collection);
    }

    public void deleteCollection(Long id) {
        markAsDeleted(id, CollectionEntity.class);
    }

    public CollectionEntity findOneCollection(Long id) {
        return findById(id, CollectionEntity.class);
    }

    public Page<CollectionEntity> findCollectionsByProjectId(Long projectId, String keyword, Pageable pageable) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(CollectionEntity.Fields.projectId).is(projectId));
        if (StringUtils.isNotEmpty(keyword)){
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%"+keyword+"%"));
        }
        Query query = Query.query(criteria);
        query.with(pageable);
        query.sort(Sort.by(Sort.Direction.DESC,BaseEntity.Fields.createDate));
        return find(query,pageable, CollectionEntity.class);
    }

    public List<CollectionEntity> findCollectionsByDocumentId(Long projectId,Long documentId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(CollectionEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(Criteria.where(CollectionEntity.Fields.documents).is(documentId));
        Query query = Query.query(criteria);
        return find(query, CollectionEntity.class);
    }

    public void batchCreateCollections(List<CollectionEntity> collections) {
        saveAll(collections);
    }
}
