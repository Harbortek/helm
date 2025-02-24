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

import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.entity.HistoryBaseEntity;
import com.harbortek.helm.tracker.constants.BaselineTypes;
import com.harbortek.helm.tracker.constants.BlockTypes;
import com.harbortek.helm.tracker.dao.*;
import com.harbortek.helm.tracker.entity.baseline.BaselineEntity;
import com.harbortek.helm.tracker.entity.block.DocBlockData;
import com.harbortek.helm.tracker.entity.block.DocEntity;
import com.harbortek.helm.tracker.entity.block.TrackerItemBlockData;
import com.harbortek.helm.tracker.entity.collection.CollectionEntity;
import com.harbortek.helm.tracker.entity.document.DocumentHistoryEntity;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemHistoryEntity;
import com.harbortek.helm.tracker.service.BaselineService;
import com.harbortek.helm.tracker.service.DocService;
import com.harbortek.helm.tracker.vo.baseline.Artifact;
import com.harbortek.helm.tracker.vo.baseline.BaselineCompare;
import com.harbortek.helm.tracker.vo.baseline.BaselineVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemHistoryVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("baselineService")
@Slf4j
public class BaselineServiceImpl implements BaselineService {
    @Autowired
    BaselineDao baselineDao;

    @Autowired
    TrackerItemHistoryDao trackerItemHistoryDao;

    @Autowired
    ProjectPageDao projectPageDao;

    @Autowired
    DocumentHistoryDao documentHistoryDao;
    @Autowired
    TrackerDao trackerDao;

    @Autowired
    TrackerItemDao trackerItemDao;

    @Autowired
    DocDao docDao;
    @Autowired
    CollectionDao collectionDao;

    @Autowired
    DocService docService;
    @Override
    public BaselineVo createBaseline(BaselineVo baseline) {
        Long projectId = baseline.getProjectId();
        baseline.setId(IDUtils.getId());
        BaselineEntity entity = DataUtils.toEntity(baseline, BaselineEntity.class);

        if (BaselineTypes.TYPE_PROJECT.equalsIgnoreCase(baseline.getType())) {
            Map<Long, Long> itemMaps = new HashMap<>();//itemId-historyId

            //获取doc中标题等工作项id
            List<Long> docIds = projectPageDao.findDocumentsByProject(baseline.getProjectId())
                                              .stream().map(ProjectPageEntity::getSmartDocId)
                                              .collect(Collectors.toList());
            List<DocEntity> docEntities = docDao.findByIds(docIds);

            //根据 tracker保存item
            List<TrackerEntity> trackerEntities = trackerDao.findByProject(projectId);
            for (TrackerEntity tracker : trackerEntities) {
                List<TrackerItemHistoryEntity> trackerItemHistoryEntities =
                        trackerItemHistoryDao.copyByTracker(tracker.getId());

                trackerItemHistoryEntities.forEach(trackerItemHistory -> {
                    itemMaps.put(trackerItemHistory.getObjectId(), trackerItemHistory.getId());
                });
            }
            entity.setTrackerItemHistoryIds(new ArrayList<>(itemMaps.values()));

            List<DocumentHistoryEntity> documentHistoryEntities =
                    documentHistoryDao.copyMany(docEntities, projectId, itemMaps);
            entity.setDocumentHistoryIds(ObjectUtils.ids(documentHistoryEntities));
        } else if (BaselineTypes.TYPE_TRACKER.equalsIgnoreCase(baseline.getType())) {
            List<TrackerEntity> trackerEntities = trackerDao.findByProject(projectId);
            List<TrackerItemHistoryEntity> trackerItemHistoryEntities = new ArrayList<>();

            for (TrackerEntity tracker : trackerEntities) {
                trackerItemHistoryEntities.addAll(
                        trackerItemHistoryDao.copyByTracker(tracker.getId()));
            }
            entity.setTrackerItemHistoryIds(ObjectUtils.ids(trackerItemHistoryEntities));
        } else if (BaselineTypes.TYPE_COLLECTION.equalsIgnoreCase(baseline.getType())) {
            List<Long> itemIds = new ArrayList<>();
            Map<Long, Long> itemMaps = new HashMap<>();//itemId-historyId
            CollectionEntity oneCollection = collectionDao.findOneCollection(baseline.getCollectionId());
            //获取doc中标题等工作项id
            List<DocEntity> docEntities = docDao.findByIds(oneCollection.getDocuments());
            docEntities.forEach(doc -> {
                doc.getBlocks().forEach(block -> {
                    itemIds.add(block.getData().getRefId());
                });
            });

            //根据itemIds保存item
            List<TrackerItemHistoryEntity> trackerItemHistoryEntities = trackerItemHistoryDao.copyByIdList(itemIds);
            trackerItemHistoryEntities.forEach(trackerItemHistory -> {
                itemMaps.put(trackerItemHistory.getObjectId(), trackerItemHistory.getId());
            });
            List<DocumentHistoryEntity> documentHistoryEntities =
                    documentHistoryDao.copyMany(docEntities, projectId, itemMaps);
            entity.setDocumentHistoryIds(ObjectUtils.ids(documentHistoryEntities));
        } else if (BaselineTypes.TYPE_DOCUMENT.equalsIgnoreCase(baseline.getType())) {
            List<Long> itemIds = new ArrayList<>();
            Map<Long, Long> itemMaps = new HashMap<>();//itemId-version
            //获取doc中标题等工作项id
            DocEntity docEntity = docDao.findDocByPageId(baseline.getDocumentId());
            docEntity.getBlocks().forEach(block -> {
                itemIds.add(block.getData().getRefId());
            });

            //根据itemIds保存item
            List<TrackerItemHistoryEntity> trackerItemHistoryEntities = trackerItemHistoryDao.copyByIdList(itemIds);
            trackerItemHistoryEntities.forEach(trackerItemHistory -> {
                itemMaps.put(trackerItemHistory.getObjectId(), trackerItemHistory.getId());
            });
            List<DocumentHistoryEntity> documentHistoryEntities =
                    documentHistoryDao.copyMany(List.of(docEntity), projectId, itemMaps);
            entity.setDocumentHistoryIds(ObjectUtils.ids(documentHistoryEntities));
        }
        entity = baselineDao.createBaseline(entity);
        return DataUtils.toVo(entity, BaselineVo.class);
    }

    @Override
    public void createDocumentVersion(Long documentId, Long projectId) {
        List<Long> itemIds = new ArrayList<>();
        Map<Long, Long> itemMaps = new HashMap<>();//itemId-version
        //获取doc中标题等工作项id
        List<DocEntity> docEntities = docDao.findByIds(Collections.singletonList(documentId));
        docEntities.forEach(doc -> {
            doc.getBlocks().forEach(block -> {
                itemIds.add(block.getData().getRefId());
            });
        });

        //根据itemIds保存item
        List<TrackerItemHistoryEntity> trackerItemHistoryEntities = trackerItemHistoryDao.copyByIdList(itemIds);
        trackerItemHistoryEntities.forEach(trackerItemHistory -> {
            itemMaps.put(trackerItemHistory.getObjectId(), trackerItemHistory.getId());
        });
        documentHistoryDao.copyMany(docEntities, projectId, itemMaps);
    }

    public BaselineVo createBaseline(BaselineVo baseline, Long documentId) {
        baseline.setId(IDUtils.getId());
        baseline.setType(BaselineTypes.TYPE_DOCUMENT);
        BaselineEntity entity = DataUtils.toEntity(baseline, BaselineEntity.class);

        List<DocumentHistoryEntity> documentHistoryEntities =
                documentHistoryDao.copyOne(documentId, baseline.getProjectId());
        entity.setDocumentHistoryIds(ObjectUtils.ids(documentHistoryEntities));

        entity = baselineDao.createBaseline(entity);
        return DataUtils.toVo(entity, BaselineVo.class);
    }

    @Override
    public BaselineVo updateBaseline(BaselineVo baseline) {
        BaselineEntity entity = DataUtils.toEntity(baseline, BaselineEntity.class);
        baselineDao.updateBaseline(entity);
        return baseline;
    }

    @Override
    public void deleteBaseline(Long id) {
        baselineDao.deleteBaseline(id);
    }

    @Override
    public BaselineVo findOneBaseline(Long id) {
        BaselineEntity entity = baselineDao.findOneBaseline(id);
        return DataUtils.toVo(entity, BaselineVo.class);
    }

    @Override
    public List<BaselineVo> findBaselinesByProjectId(Long projectId, String keyword) {
        List<BaselineEntity> entities = baselineDao.findBaselinesByProjectId(projectId, keyword);
        List<BaselineVo> baselineVos = new ArrayList<>();
        entities.forEach(baseline -> {
            BaselineVo baselineVo = DataUtils.toVo(baseline, BaselineVo.class);
            baselineVo.setDocumentIncludes(baseline.getDocumentHistoryIds().size());
            baselineVo.setWorkItemIncludes(baseline.getTrackerItemHistoryIds().size());
            baselineVos.add(baselineVo);
        });
        return baselineVos;
    }

    @Override
    public List<BaselineVo> findBaselinesByDocumentId(Long projectId, Long documentId) {
        List<BaselineEntity> entities = baselineDao.findBaselinesByDocumentId(projectId, documentId);
        return DataUtils.toVo(entities, BaselineVo.class);
    }

    @Override
    public List<BaselineCompare> compareBaseline(Long projectId, Long leftId, Long rightId) {
        List<BaselineCompare> result = new ArrayList<>();

        BaselineCompare docFolder =
                BaselineCompare.builder().id(IDUtils.getId()).name("文档").icon("document").folder(true).build();
        result.add(docFolder);

        BaselineEntity leftBaseline = baselineDao.findOneBaseline(leftId);
        BaselineEntity rightBaseline = baselineDao.findOneBaseline(rightId);

        //比较文档
        {
            List<Long> leftDocumentHistoryIds = leftBaseline.getDocumentHistoryIds();
            List<Long> rightDocumentHistoryIds = rightBaseline.getDocumentHistoryIds();

            List<Long> diffHistoryIds = new ArrayList<>(CollectionUtils.disjunction(leftDocumentHistoryIds,
                                                                                    rightDocumentHistoryIds));
            Collections.sort(diffHistoryIds);
            List<? extends HistoryBaseEntity> historyEntities = documentHistoryDao.find(diffHistoryIds);
            List<BaselineCompare> diffItems = buildBaselineCompare(docFolder, leftDocumentHistoryIds,
                                                                   rightDocumentHistoryIds,
                                                                   historyEntities);
            diffItems.forEach(item -> item.setType("wiki"));
            if (!diffItems.isEmpty()) {
                result.addAll(diffItems);
            }

        }

        //比较工作项
        BaselineCompare workItemFolder =
                BaselineCompare.builder().id(IDUtils.getId()).name("工作项").icon("tracker").folder(true).build();
        result.add(workItemFolder);

        List<TrackerEntity> trackers = trackerDao.findByProject(projectId);


        final List<Long> leftHistoryIds =
                Objects.requireNonNullElse(leftBaseline.getTrackerItemHistoryIds(), new ArrayList<>());
        final List<Long> rightHistoryIds =
                Objects.requireNonNullElse(rightBaseline.getTrackerItemHistoryIds(), new ArrayList<>());

        List<Long> diffHistoryIds = new ArrayList<>(CollectionUtils.disjunction(leftHistoryIds,
                                                                                rightHistoryIds));

        Collections.sort(diffHistoryIds);

        List<TrackerItemHistoryEntity> historyEntities =
                trackerItemHistoryDao.findByHistoryIdsForCompare(diffHistoryIds);

        List<TrackerItemHistoryEntity> leftItems =
                historyEntities.stream().filter(item -> leftHistoryIds.contains(item.getId())).toList();
        List<TrackerItemHistoryEntity> rightItems =
                historyEntities.stream().filter(item -> rightHistoryIds.contains(item.getId())).toList();

        for (TrackerEntity tracker : trackers) {
            BaselineCompare trackerFolder =
                    BaselineCompare.builder().id(tracker.getId()).parentId(workItemFolder.getId())
                                   .name(tracker.getName()).icon(tracker.getIcon())
                                   .folder(true).build();

            List<Long> leftHistoryUnderTrackerIds =
                    leftItems.stream().filter(item -> item.getTrackerId().equals(tracker.getId()))
                             .map(TrackerItemHistoryEntity::getId).toList();
            ;
            List<Long> rightHistoryUnderTrackerIds =
                    rightItems.stream().filter(item -> item.getTrackerId().equals(tracker.getId()))
                              .map(TrackerItemHistoryEntity::getId).toList();
            ;

            List<BaselineCompare> diffItems =
                    buildBaselineCompare(trackerFolder, leftHistoryUnderTrackerIds, rightHistoryUnderTrackerIds,
                                         historyEntities);
            diffItems.forEach(item -> item.setType("tracker"));
            if (!diffItems.isEmpty()) {
                result.add(trackerFolder);
                result.addAll(diffItems);
            }
        }


        return result;
    }

    @Override
    public List<TrackerItemHistoryVo> findItemsHistory(List<Long> historyIds) {
        return DataUtils.toVo(trackerItemHistoryDao.findByHistoryIds(historyIds), TrackerItemHistoryVo.class);
    }

    @Override
    public List<DocumentHistoryEntity> findDocumentHistory(List<Long> historyIds) {
        List<DocumentHistoryEntity> byHistoryIds = documentHistoryDao.findByHistoryIds(historyIds);
        List<Long> docIds = byHistoryIds.stream().map(HistoryBaseEntity::getObjectId).toList();
        Map<Long, Long> docMap = projectPageDao.findByDocIds(docIds).stream()
                .collect(Collectors.toMap(ProjectPageEntity::getSmartDocId, BaseEntity::getId));

        byHistoryIds.forEach(document -> {
            if (document != null) {
                List<Long> refIds = new ArrayList<>();
                document.setPageId(docMap.get(document.getObjectId()));
                document.getBlocks().forEach(block -> {
                    DocBlockData data = block.getData();
                    if (ObjectUtils.isValid(data.getRefHistoryId())) {
                        refIds.add( data.getRefHistoryId());
                    }
                });
                //List<TrackerItemVo> trackerItemVos = trackerItemService.findTrackerItemByIds(refIds);
                List<TrackerItemHistoryVo> trackerItemVos = DataUtils.toVo(trackerItemHistoryDao.findByHistoryIds(refIds), TrackerItemHistoryVo.class);
                Map<Long, TrackerItemHistoryVo> trackerItemVoMap = trackerItemVos.stream().collect(Collectors.toMap(TrackerItemHistoryVo::getId, Function.identity()));
                document.getBlocks().forEach(block -> {
                    TrackerItemHistoryVo trackerItemVo = trackerItemVoMap.get(block.getData().getRefId());
                    if(ObjectUtils.isNotEmpty(trackerItemVo)){
                        if (BlockTypes.TITLE.equals(block.getType())) {

                        } else if (BlockTypes.HEADING.equals(block.getType())) {

                        } else if (BlockTypes.PARAGRAPH.equals(block.getType())) {

                        } else {
                            //BlockTypes.TRACKER_ITEM
                            TrackerItemBlockData trackerItemBlockData = ((TrackerItemBlockData)block.getData());
                            trackerItemBlockData.setName(trackerItemVo.getName());
                            trackerItemBlockData.setTrackerId(trackerItemVo.getTracker().getId());
                            TrackerItemVo trackerItem = TrackerItemVo.builder()
                                    .id(trackerItemVo.getId()).itemNo(trackerItemVo.getItemNo())
                                    .name(trackerItemVo.getName()).tracker(trackerItemVo.getTracker())
                                    .project(trackerItemVo.getProject()).owner(trackerItemVo.getOwner()).build();
//                        BeanCopyUtils.copyWithoutNullProperties(trackerItemVo, trackerItem);
                            trackerItemBlockData.setTrackerItem(docService.fillTrackerItemVo(trackerItem));
                            block.setData(trackerItemBlockData);
                        }
                        block.getData().setText(trackerItemVo.getDescription());
                    }else{
                        log.info("trackerItemVo is null");
                    }
                });
            }
        });
        return byHistoryIds;
    }

    private List<BaselineCompare> buildBaselineCompare(BaselineCompare parentFolder, List<Long> leftHistoryIds,
                                                       List<Long> rightHistoryIds,
                                                       List<? extends HistoryBaseEntity> historyEntities) {
        HashMap<Long, BaselineCompare> nodeMap = new LinkedHashMap<>();
        for (HistoryBaseEntity history : historyEntities) {
            Long historyId = history.getId();
            if (leftHistoryIds.contains(historyId)) {
                Long id = history.getObjectId();
                Long revision = history.getRevision();

                BaselineCompare node = nodeMap.get(id);
                if (node == null) {
                    node = BaselineCompare.builder().id(IDUtils.getId()).parentId(parentFolder.getId()).build();
                    node.setLeft(
                            Artifact.builder().id(id).historyId(historyId).name(history.getName()).icon(history.getIcon()).revision(revision)
                                    .build());
                    nodeMap.put(id, node);
                } else {
                    node.setLeft(
                            Artifact.builder().id(id).historyId(historyId).name(history.getName()).icon(history.getIcon()).revision(revision)
                                    .build());
                }
            } else if (rightHistoryIds.contains(historyId)) {
                Long id = history.getObjectId();
                Long revision = history.getRevision();
                BaselineCompare node = nodeMap.get(id);
                if (node == null) {
                    node = BaselineCompare.builder().id(IDUtils.getId()).parentId(parentFolder.getId()).build();
                    node.setRight(
                            Artifact.builder().id(id).historyId(historyId).name(history.getName()).icon(history.getIcon()).revision(revision)
                                    .build());
                    nodeMap.put(id, node);
                } else {
                    node.setRight(
                            Artifact.builder().id(id).historyId(historyId).name(history.getName()).icon(history.getIcon()).revision(revision)
                                    .build());
                }
            }
        }

        List<Long> sameIds = new ArrayList<>();
        nodeMap.keySet().forEach(objectId -> {
            BaselineCompare row = nodeMap.get(objectId);
            if (row.getLeft() != null && row.getRight() == null) {
                row.setMode("ADD");
            } else if (row.getLeft() == null && row.getRight() != null) {
                row.setMode("DELETE");
            } else if (!Objects.equals(Objects.requireNonNull(row.getLeft()).getRevision(),
                                       Objects.requireNonNull(row.getRight().getRevision()))) {
                row.setMode("UPDATE");
            } else if (Objects.equals(Objects.requireNonNull(row.getLeft()).getRevision(),
                                      Objects.requireNonNull(row.getRight().getRevision()))) {
                sameIds.add(objectId);
            }
        });
        sameIds.forEach(nodeMap::remove);

        return new ArrayList<>(nodeMap.values());
    }
}
