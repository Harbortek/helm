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

package com.harbortek.helm.tracker.service.impl;

import com.harbortek.helm.tracker.dao.CollectionDao;
import com.harbortek.helm.tracker.entity.collection.CollectionEntity;
import com.harbortek.helm.tracker.service.CollectionService;
import com.harbortek.helm.tracker.vo.collection.CollectionVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("collectionService")
@Slf4j
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionDao collectionDao;

    @Override
    public CollectionVo createCollection(CollectionVo collection) {
        CollectionEntity entity = DataUtils.toEntity(collection, CollectionEntity.class);
        entity.setDocuments(collection.getDocuments().stream().map(ProjectPageVo::getId).collect(Collectors.toList()));
        entity.setId(IDUtils.getId());
        CollectionEntity collectionEntity = collectionDao.createCollection(entity);
        return DataUtils.toVo(collectionEntity, CollectionVo.class);
    }

    @Override
    public CollectionVo updateCollection(CollectionVo collection) {
        CollectionEntity entity = DataUtils.toEntity(collection, CollectionEntity.class);
        entity.setDocuments(collection.getDocuments().stream().map(ProjectPageVo::getId).collect(Collectors.toList()));
        CollectionEntity collectionEntity = collectionDao.updateCollection(entity);
        return DataUtils.toVo(collectionEntity, CollectionVo.class);
    }

    @Override
    public void deleteCollection(Long id) {
        collectionDao.deleteCollection(id);
    }

    @Override
    public CollectionVo findOneCollection(Long id) {
        return DataUtils.toVo(collectionDao.findOneCollection(id), CollectionVo.class);
    }

    @Override
    public Page<CollectionVo> findCollectionsByProjectId(Long projectId, String keyword, Pageable pageable) {
        return DataUtils.toVo(collectionDao.findCollectionsByProjectId(projectId,keyword,pageable), CollectionVo.class);
    }

    @Override
    public List<CollectionVo> findCollectionsByDocumentId(Long projectId, Long documentId) {
        return DataUtils.toVo(collectionDao.findCollectionsByDocumentId(projectId,documentId), CollectionVo.class);
    }
}
