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
import com.harbortek.helm.common.entity.HistoryBaseEntity;
import com.harbortek.helm.tracker.entity.block.DocBlockData;
import com.harbortek.helm.tracker.entity.block.DocumentEntity;
import com.harbortek.helm.tracker.entity.document.DocumentHistoryEntity;
import com.harbortek.helm.util.BeanCopyUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DocumentHistoryDao extends BaseJdbcDao {


    /**
     * 往历史表中复制revision不同的Doc
     */
    public List<DocumentHistoryEntity> copyMany(List<DocumentEntity> source, Long projectId, Map<Long, Long> itemMaps) {

        List<Long> objectIds = source.stream().map(BaseEntity::getId).toList();
        Map<Long, List<DocumentHistoryEntity>> historyMap = findByObjectIds(objectIds).stream().collect(Collectors.groupingBy(DocumentHistoryEntity::getObjectId));
        List<DocumentHistoryEntity> existDocuments=new ArrayList<>();

        List<DocumentHistoryEntity> historyEntities = new ArrayList<>();
        for (DocumentEntity item : source) {
            DocumentHistoryEntity history = new DocumentHistoryEntity();
            BeanCopyUtils.copyWithoutNullProperties(item, history);
            history.setObjectId(item.getId());
            history.setId(IDUtils.getId());
            history.setName(item.getName());
            history.setRevision(item.getRevision());
            if(ObjectUtils.isNotEmpty(historyMap.get(item.getId()))){
                boolean exist= historyMap.get(item.getId()).stream().anyMatch(historyEntity -> historyEntity.getRevision().equals(item.getRevision()));
                if (exist) {
                    existDocuments.add(history);
                }
            }else{
                historyEntities.add(history);
                history.getBlocks().forEach(block -> {
                    DocBlockData data = block.getData();
                    data.setRefHistoryId(itemMaps.get(data.getRefId()));
                });
                history.setCreateDate(null);
            }
        }
        saveAll(historyEntities);

        historyEntities.addAll(existDocuments);
        return historyEntities;
    }


    public List<DocumentHistoryEntity> copyOne(Long documentId, Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).is(documentId));
        Query query = Query.query(criteria);
        DocumentEntity item = findOne(query, DocumentEntity.class);
        Assert.notNull(item, "document item must not be null!");
        DocumentHistoryEntity history = new DocumentHistoryEntity();
        BeanCopyUtils.copyWithoutNullProperties(item, history);
        history.setObjectId(item.getId());
        history.setId(IDUtils.getId());
        history.setCreateDate(null);
        save(history);

        return List.of(history);
    }

    public List<DocumentHistoryEntity> findByObjectIds(List<Long> objectIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(HistoryBaseEntity.Fields.objectId).in(objectIds));
        Query query = Query.query(criteria);
        query.columns(BaseEntity.Fields.id, BaseEntity.Fields.name, BaseEntity.Fields.icon,
                HistoryBaseEntity.Fields.objectId,HistoryBaseEntity.Fields.revision);
        return find(query, DocumentHistoryEntity.class);
    }

    public List<DocumentHistoryEntity> find(List<Long> diffHistoryIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).in(diffHistoryIds));
        Query query = Query.query(criteria);
        query.columns(BaseEntity.Fields.id, BaseEntity.Fields.name, BaseEntity.Fields.icon,
                      HistoryBaseEntity.Fields.objectId);
        return find(query, DocumentHistoryEntity.class);
    }

    public List<DocumentHistoryEntity> findByHistoryIds(List<Long> diffHistoryIds) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).in(diffHistoryIds));
        Query query = Query.query(criteria);
        return find(query, DocumentHistoryEntity.class);
    }

//    public List<DocumentHistoryEntity> findByProjectId(Long projectId) {
//        Criteria criteria = Criteria.empty();
//        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
//        criteria = criteria.and(Criteria.where(DocumentHistoryEntity.Fields.projectId).is(projectId));
//        Query query = Query.query(criteria);
//        return find(query, DocumentHistoryEntity.class);
//    }
//
//    public void createDocumentHistories(List<DocumentHistoryEntity> documentHistoryList) {
//        saveAll(documentHistoryList);
//    }
}
