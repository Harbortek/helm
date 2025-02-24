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

package com.harbortek.helm.smartdoc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.smartdoc.utils.DocUtils;
import com.harbortek.helm.system.entity.EnumItemEntity;
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.tracker.constants.Associations;
import com.harbortek.helm.tracker.constants.BlockTypes;
import com.harbortek.helm.tracker.constants.InternalTrackers;
import com.harbortek.helm.tracker.dao.*;
import com.harbortek.helm.tracker.entity.block.*;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.service.*;
import com.harbortek.helm.tracker.vo.ProjectVo;
import com.harbortek.helm.tracker.vo.block.DocVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkTypeVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocServiceImpl implements DocService {
    @Autowired
    DocDao docDao;

    @Autowired
    TrackerItemService trackerItemService;
    @Autowired
    TrackerService trackerService;

    @Autowired
    TrackerLinkTypeDao trackerLinkTypeDao;
    @Autowired
    EnumService enumService;
    @Autowired
    private TrackerLinkTypeService trackerLinkTypeService;
    @Autowired
    private TrackerLinkDao trackerLinkDao;
    @Autowired
    private ProjectPageService projectPageService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    TrackerItemDao trackerItemDao;
    @Autowired
    TrackerDao trackerDao;

    @Override
    public DocEntity saveBlocksAndTrackerItems(Long projectId, Long pageId, List<DocBlock> docBlocks) {
        /**
         * docBlockLinks 为 null ，自动构建工作项目关系
         */
        return this.saveBlocksAndTrackerItems(projectId, pageId, docBlocks, null);
    }

    @Override
    public DocEntity saveBlocksAndTrackerItemsByPart(DocEntity modifiedDoc, List<DocBlock> updateBlocks) {
        ProjectPageVo pageVo = projectPageService.findByDocId(modifiedDoc.getId());
        List<DocBlock> modifiedDocBlocks = modifiedDoc.getBlocks();
        //拿到库存 trackerItem
        Map<Long, TrackerItemVo> id2TrackerItemVo = new HashMap<>();
//        for (int i = 0; i < modifiedDocBlocks.size(); i++) {
//            DocBlock docBlock = modifiedDocBlocks.get(i);
//            Optional.ofNullable(docBlock.getData().getRefId())
//                    .ifPresent(value -> id2TrackerItemVo.put(value, null));
//        }
//        List<Long> refIds = id2TrackerItemVo.keySet().stream().toList();
//        List<TrackerItemVo> trackerItemVos = refIds.size() > 0
//                ? trackerItemService.findTrackerItemByIds(refIds)
//                : new ArrayList<>();
//        for (TrackerItemVo trackerItemVo : trackerItemVos) {
//            id2TrackerItemVo.put(trackerItemVo.getId(), trackerItemVo);
//        }

        //saveTrackerItems (新增、更新、删除)
        for (int i = 0; i < modifiedDocBlocks.size(); i++) {

            DocBlock docBlock = modifiedDocBlocks.get(i);
            Long refId = docBlock.getData().getRefId();
//            TrackerItemVo trackerItemVo = id2TrackerItemVo.get(refId);
            TrackerItemVo trackerItemVo = null;

            if (refId == null) {
                //新增
                trackerItemVo = this.block2TrackerItem(pageVo.getProjectId(), docBlock, trackerItemVo);
                //工作项关联wiki
                if (!trackerItemVo.getRelatedWikis().contains(pageVo.getId())) {
                    trackerItemVo.getRelatedWikis().add(pageVo.getId());
                }

                trackerItemVo = trackerItemService.createTrackerItem(trackerItemVo);
                refId = trackerItemVo.getId();
                docBlock.getData().setRefId(refId);
            } else {
                for (DocBlock blockItem : updateBlocks) {
                    if (blockItem.getId().equals(docBlock.getId())) {
                        //更新
                        trackerItemVo = trackerItemService.findOneTrackerItem(refId);
                        trackerItemVo = this.block2TrackerItem(pageVo.getProjectId(), docBlock, trackerItemVo);
                        //工作项关联wiki
                        if (!trackerItemVo.getRelatedWikis().contains(pageVo.getId())) {
                            trackerItemVo.getRelatedWikis().add(pageVo.getId());
                        }
                        trackerItemService.updateTrackerItem(trackerItemVo);
                        break;
                    }
                }
                if (trackerItemVo == null) {
                    trackerItemVo = this.block2TrackerItem(pageVo.getProjectId(), docBlock, trackerItemVo);
                }
            }
            //更新map
            id2TrackerItemVo.put(refId, trackerItemVo);

            if (docBlock.getData() instanceof TrackerItemBlockData) {
                TrackerVo trackerVo = trackerService.findOneTracker(Long.parseLong(docBlock.getType()));
                TrackerItemBlockData trackerItemBlockData = (TrackerItemBlockData) (docBlock.getData());
                trackerItemBlockData.setName(trackerItemVo.getName());
                TrackerItemVo finalTrackerItemVo = trackerItemVo;
                updateBlocks.stream().filter(blockItem -> blockItem.getId().equals(docBlock.getId()))
                        .forEach(blockItem -> {
                            trackerItemBlockData.setTrackerItem(fillTrackerItemVo(finalTrackerItemVo));
                        });
                trackerItemBlockData.getTrackerItem().getValues().forEach((key, value) -> {
                    trackerVo.getTrackerFields().stream().filter(field -> field.getId().equals(key));
                });
            }
        }

        //删除

        DocEntity docEntity = docDao.findById(modifiedDoc.getId(), DocEntity.class);
        if (ObjectUtils.isEmpty(docEntity)) {
            docEntity = DocEntity.builder().id(IDUtils.getId()).name(pageVo.getName()).build();
            pageVo.setSmartDocId(docEntity.getId());
            projectPageService.updateProjectPageBasicInfo(pageVo);
        }
        List<Long> oldRefIds = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(docEntity.getBlocks())) {
            docEntity.getBlocks().forEach(docBlock -> {
                if (!id2TrackerItemVo.keySet().contains(docBlock.getData().getRefId())) {
                    if (docBlock.getData() instanceof TrackerItemBlockData) {
                        TrackerItemBlockData trackerItemBlockData = (TrackerItemBlockData) (docBlock.getData());
                        //link 不删
                        if (!trackerItemBlockData.getIsTrackerItemLink()) {
                            oldRefIds.add(docBlock.getData().getRefId());
                        }
                    } else {
                        oldRefIds.add(docBlock.getData().getRefId());
                    }
                }
            });
        }
        if (!oldRefIds.isEmpty()) {
            trackerItemService.batchDeleteTrackerItem(oldRefIds);
        }
        //save
        docEntity.setBlocks(modifiedDocBlocks);
//        docEntity.setPageId(pageId);
        docEntity.setVersion(docEntity.getVersion() == null ? 0L : docEntity.getVersion() + 1);

//        ProjectPageVo projectPageVo = projectPageService.findOneProjectPage(pageId);
//        docEntity.setName(projectPageVo.getName());

        docEntity = docDao.saveDoc(docEntity);

        this.buildLinks(pageVo.getProjectId(), docEntity, id2TrackerItemVo);
        return docEntity;
    }

    @Override
    public DocEntity saveDoc(DocEntity docEntity) {
        /**
         * docBlockLinks 为 null ，自动构建工作项目关系
         */
        return docDao.saveDoc(docEntity);
    }

    @Override
    public void saveBlockAndTrackerItemV2(Long projectId, DocEntity doc, List<DocBlock> toAdd, List<DocBlock> toUpdate, List<DocBlock> toDelete) {
        for (DocBlock docBlock : toAdd) {
            TrackerItemBlockData item = (TrackerItemBlockData) docBlock.getData();
            TrackerItemVo trackerItemVo = this.block2TrackerItem(projectId, docBlock, null);
            trackerItemService.createTrackerItem(trackerItemVo);
            item.setRefId(trackerItemVo.getId());
        }
        for (DocBlock docBlock : toUpdate) {
            TrackerItemBlockData item = (TrackerItemBlockData) docBlock.getData();
            TrackerItemVo trackerItemVo = trackerItemService.findOneTrackerItem(item.getRefId());
            trackerItemVo = this.block2TrackerItem(projectId, docBlock, trackerItemVo);
            trackerItemService.updateTrackerItem(trackerItemVo);
        }
        for (DocBlock docBlock : toDelete) {
            TrackerItemBlockData item = (TrackerItemBlockData) docBlock.getData();
            trackerItemService.deleteOneTrackerItem(item.getRefId());
        }
        saveDoc(doc);
    }

    @Override
    public DocBlock saveBlockAndTrackerItem(Long docId, DocBlock docBlock) {
        ProjectPageVo pageVo = projectPageService.findByDocId(docId);
        TrackerItemVo trackerItemVo = this.block2TrackerItem(pageVo.getProjectId(), docBlock, null);
        //工作项关联wiki
        if (!trackerItemVo.getRelatedWikis().contains(pageVo.getId())) {
            trackerItemVo.getRelatedWikis().add(pageVo.getId());
        }
        trackerItemVo = trackerItemService.createTrackerItem(trackerItemVo);
        Long refId = trackerItemVo.getId();
        docBlock.getData().setRefId(refId);
        return docBlock;
    }

    private void buildLinks(Long projectId, DocEntity entity, Map<Long, TrackerItemVo> id2TrackerItemVo) {
        Map<String, TrackerLinkTypeVo> trackerLinkTypeMap = trackerLinkTypeService.findLinkTypes(projectId).stream()
                .collect(Collectors.toMap(TrackerLinkTypeVo::getCode, Function.identity()));

        Map<String, String> allLinks = new HashMap<>();
        Long parentLinkTypeId = trackerLinkTypeMap.get(Associations.PARENT.getId()).getId();
        trackerLinkDao.findByTargetIds(id2TrackerItemVo.keySet().stream().collect(Collectors.toList()), parentLinkTypeId).forEach(trackerLinkEntity -> {
            allLinks.put(trackerLinkEntity.getSourceItemId() + "-" + parentLinkTypeId, trackerLinkEntity.getTargetItemId().toString());
        });

        List<TrackerLinkEntity> addLinks = new ArrayList<>();
        List<TrackerLinkEntity> delLinks = new ArrayList<>();
        List<DocBlock> docBlocks = entity.getBlocks();
        for (int i = 0; i < docBlocks.size(); i++) {
            DocBlock sourceBlock = docBlocks.get(i);
//            if (!(sourceBlock.getData() instanceof TrackerItemBlockData)) {
//                continue;
//            }
            boolean sourceIsHeading = BlockTypes.HEADING.equals(sourceBlock.getType());
            TrackerLinkEntity linkEntity = TrackerLinkEntity.builder()
                    .id(IDUtils.getId())
                    .sourceItemId(sourceBlock.getData().getRefId())
                    .linkTypeId(parentLinkTypeId)
                    .build();
            for (int j = i - 1; j >= 0; j--) {
                DocBlock targetBlock = docBlocks.get(j);

                String parentItemId = allLinks.get(linkEntity.getSourceItemId() + "-" + parentLinkTypeId);
//                    if (StringUtils.isNotEmpty(parentItemId) && !parentItemId.equals(linkEntity.getTargetItemId().toString())) {
                if (StringUtils.isNotEmpty(parentItemId)) {

                    delLinks.add(TrackerLinkEntity.builder()
                            .sourceItemId(linkEntity.getSourceItemId())
                            .targetItemId(Long.parseLong(parentItemId))
                            .linkTypeId(parentLinkTypeId)
                            .build());
                }

                if (BlockTypes.HEADING.equals(targetBlock.getType())) {
                    if (sourceIsHeading) {
                        HeaderBlockData sourceHeaderBlockData = (HeaderBlockData) sourceBlock.getData();
                        HeaderBlockData targetHeaderBlockData = (HeaderBlockData) targetBlock.getData();
                        if (sourceHeaderBlockData.getLevel() <= targetHeaderBlockData.getLevel()) {
                            continue;
                        }
                    }
                    linkEntity.setTargetItemId(targetBlock.getData().getRefId());
                    TrackerItemVo targetTrackerItemVo = id2TrackerItemVo.get(targetBlock.getData().getRefId());
                    boolean hasDuplicateRelatedWorkItem = targetTrackerItemVo.hasDuplicateRelatedWorkItem(
                            TrackerLinkVo.builder()
                                    .sourceItem(TrackerItemVo.builder().id(linkEntity.getSourceItemId()).build())
                                    .targetItem(TrackerItemVo.builder().id(linkEntity.getTargetItemId()).build())
                                    .linkType(TrackerLinkTypeVo.builder().code(Associations.PARENT.getId()).build())
                                    .build());


                    if (!hasDuplicateRelatedWorkItem) {

                        addLinks.add(linkEntity);
                    }
                    break;
                }
            }
        }
        Map<String, TrackerLinkEntity> delLinksMap = new HashMap<>();
        delLinks.forEach(trackerLinkEntity ->
                delLinksMap.put(
                        trackerLinkEntity.getSourceItemId() + "-"
                                + trackerLinkEntity.getTargetItemId() + "-"
                                + trackerLinkEntity.getLinkTypeId(), trackerLinkEntity));
        //delLinks去重
        trackerLinkDao.batchDelete(delLinksMap.values().stream().toList());
        trackerLinkDao.batchCreateTrackerLinks(addLinks);
    }

    /**
     * 根据全新的docBLockLinks 更新工作项关系
     *
     * @param projectId
     * @param pageId
     * @param docBlocks
     * @param docBlockLinks
     * @return
     */
    @Override
    @Transactional
    public DocEntity saveBlocksAndTrackerItems(Long projectId, Long pageId, List<DocBlock> docBlocks, List<DocBlockLink> docBlockLinks) {
        //拿到库存 trackerItem
        Map<Long, TrackerItemVo> id2TrackerItemVo = new HashMap<>();
        for (int i = 0; i < docBlocks.size(); i++) {
            DocBlock docBlock = docBlocks.get(i);
            if (docBlock.getData().getRefId() != null) {
                id2TrackerItemVo.put(docBlock.getData().getRefId(), null);
            }
        }
        List<Long> refIds = new ArrayList<>();
        for (Long key : id2TrackerItemVo.keySet()) {
            refIds.add(key);
        }
        List<TrackerItemVo> trackerItemVos = refIds.size() > 0 ? trackerItemService.findTrackerItemByIds(refIds) : new ArrayList<>();
        for (TrackerItemVo trackerItemVo : trackerItemVos) {
            id2TrackerItemVo.put(trackerItemVo.getId(), trackerItemVo);
        }
        //saveTrackerItems (新增、更新、删除)
        for (int i = 0; i < docBlocks.size(); i++) {
            DocBlock docBlock = docBlocks.get(i);
            Long refId = docBlock.getData().getRefId();
            TrackerItemVo trackerItemVo = id2TrackerItemVo.get(refId);
            trackerItemVo = this.block2TrackerItem(projectId, docBlock, trackerItemVo);
            //工作项关联wiki
            if (!trackerItemVo.getRelatedWikis().contains(pageId)) {
                trackerItemVo.getRelatedWikis().add(pageId);
            }
            if (refId == null) {
                //新增
                trackerItemVo = trackerItemService.createTrackerItem(trackerItemVo);
                refId = trackerItemVo.getId();
            } else {
                //更新
                trackerItemService.updateTrackerItem(trackerItemVo);
            }
            //更新map
            id2TrackerItemVo.put(refId, trackerItemVo);

            docBlock.getData().setRefId(trackerItemVo.getId());
            if (docBlock.getData() instanceof TrackerItemBlockData) {
                TrackerItemBlockData trackerItemBlockData = (TrackerItemBlockData) (docBlock.getData());
                trackerItemBlockData.setName(trackerItemVo.getName());
                trackerItemBlockData.setTrackerItem(fillTrackerItemVo(trackerItemVo));
            }
        }
        //删除
        ProjectPageVo oneProjectPage = projectPageService.findOneProjectPage(pageId);
        DocEntity docEntity = docDao.findById(oneProjectPage.getSmartDocId(), DocEntity.class);
        if (ObjectUtils.isEmpty(docEntity)) {
            docEntity = DocEntity.builder().id(IDUtils.getId()).name(oneProjectPage.getName()).build();
            oneProjectPage.setSmartDocId(docEntity.getId());
            projectPageService.updateProjectPageBasicInfo(oneProjectPage);
        }
        List<Long> oldRefIds = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(docEntity.getBlocks())) {
            docEntity.getBlocks().forEach(docBlock -> {
                if (!id2TrackerItemVo.keySet().contains(docBlock.getData().getRefId())) {
                    if (docBlock.getData() instanceof TrackerItemBlockData) {
                        TrackerItemBlockData trackerItemBlockData = (TrackerItemBlockData) (docBlock.getData());
                        //link 不删
                        if (!trackerItemBlockData.getIsTrackerItemLink()) {
                            oldRefIds.add(docBlock.getData().getRefId());
                        }
                    } else {
                        oldRefIds.add(docBlock.getData().getRefId());
                    }
                }
            });
        }
        trackerItemService.batchDeleteTrackerItem(oldRefIds);
        //save
        docEntity.setBlocks(docBlocks);
//        docEntity.setPageId(pageId);
        docEntity.setVersion(docEntity.getVersion() == null ? 0L : docEntity.getVersion() + 1);

//        ProjectPageVo projectPageVo = projectPageService.findOneProjectPage(pageId);
//        docEntity.setName(projectPageVo.getName());

        docEntity = docDao.saveDoc(docEntity);

        if (docBlockLinks == null) {
            this.buildLinks(projectId, docEntity, id2TrackerItemVo);
            return docEntity;
        }

        //以下根据 docBlockLinks 构建工作项关系
        Map<String, TrackerLinkTypeVo> trackerLinkTypeMap = trackerLinkTypeService.findLinkTypes(projectId).stream()
                .collect(Collectors.toMap(TrackerLinkTypeVo::getCode, Function.identity()));
//        Map<Long, TrackerItemVo> trackerItemVoMap = trackerItemService.findTrackerItemByIds
//                        (docEntity.getBlocks().stream().map(docBlock -> docBlock.getData().getRefId()).collect(Collectors.toList()))
//                .stream().collect(Collectors.toMap(TrackerItemVo::getId, Function.identity()));
        Map<String, Long> blockRefMap = docEntity.getBlocks().stream()
                .collect(Collectors.toMap(docBlock -> docBlock.getId(), docBlock -> docBlock.getData().getRefId()));

        List<TrackerLinkEntity> toUpdateLinks = new ArrayList<>();
        docBlockLinks.stream().forEach(docBlockLink -> {
            toUpdateLinks.add(TrackerLinkEntity.builder()
                    .id(IDUtils.getId())
                    .sourceItemId(blockRefMap.get(docBlockLink.getSourceBlockId()))
                    .targetItemId(blockRefMap.get(docBlockLink.getTargetBlockId()))
                    .linkTypeId(trackerLinkTypeMap.get(docBlockLink.getLinkCode()).getId())
                    .build());
        });
        trackerLinkDao.batchCreateTrackerLinks(toUpdateLinks);
        return docEntity;
    }

    public List<TrackerItemVo> findTrackerItemByIds(List<Long> itemIds) {
        itemIds = itemIds.stream().filter(ObjectUtils::isNotEmpty).toList();
        if (ObjectUtils.isEmpty(itemIds)) {
            return new ArrayList<>();
        }
        List<TrackerItemEntity> trackerItemEntities = trackerItemDao.findDocItemsByIds(itemIds);
        //权限判断
        List<Long> trackerIds = trackerItemEntities.stream().map(item -> item.getTrackerId()).collect(Collectors.toList());
        List<TrackerVo> trackerVos = DataUtils.toVo(trackerDao.findByIds(trackerIds, TrackerEntity.class, true),
                TrackerVo.class);
        HashMap<Long, EnumItemVo> trackerMap = new HashMap<>();
        for (TrackerVo trackerVo : trackerVos) {
            trackerMap.put(trackerVo.getId(), trackerVo.getTrackerType());
        }
        List<TrackerItemVo> vos = DataUtils.toVo(trackerItemEntities, TrackerItemVo.class);

        for (TrackerItemVo itemVo : vos) {
            if (ObjectUtils.isNotEmpty(itemVo.getTracker())) {
                EnumItemVo enumItemVo = trackerMap.get(itemVo.getTracker().getId());
                itemVo.getTracker().setIcon(enumItemVo.getIcon());
            }

        }
        return InternalTrackers.fillInternalTracker(vos, trackerItemEntities);
    }

    @Override
    public DocVo findDocByPageId(Long pageId) {
        //SQL 1
        DocEntity docEntity = docDao.findDocByPageId(pageId);
        DocVo doc = DataUtils.toVo(docEntity, DocVo.class);
        if (doc == null) {
            return null;
        }


        List<Long> refIds = doc.getBlocks().stream()
                .filter(docBlock -> docBlock.getData().getRefId() != null)
                .map(docBlock -> docBlock.getData().getRefId())
                .toList();
        //SQL 2
        List<TrackerItemVo> trackerItemVos = this.findTrackerItemByIds(refIds);
        //SQL 3
//        List<TrackerVo> trackerVos = trackerService.findTrackerByIds(
//                trackerItemVos.stream().filter(item -> item.getTracker() != null).toList()
//                        .stream()
//                        .map(item -> item.getTracker().getId()).toList());
        Map<Long, TrackerItemVo> trackerItemVoMap = trackerItemVos.stream().collect(Collectors.toMap(TrackerItemVo::getId, Function.identity()));
//        Map<Long, TrackerVo> trackerMap = trackerVos.stream().collect(Collectors.toMap(TrackerVo::getId, Function.identity()));
        doc.getBlocks().forEach(block -> {
            TrackerItemVo trackerItemEntity = trackerItemVoMap.get(block.getData().getRefId());
            if (ObjectUtils.isNotEmpty(trackerItemEntity)) {
                if (BlockTypes.TITLE.equals(block.getType())) {

                } else if (BlockTypes.HEADING.equals(block.getType())) {

                } else if (BlockTypes.PARAGRAPH.equals(block.getType())) {

                } else {
                    TrackerVo tracker = trackerService.findOneTracker(trackerItemEntity.getTracker().getId());
                    TrackerItemBlockData trackerItemBlockData = ((TrackerItemBlockData) (block.getData()));
                    trackerItemBlockData.setName(trackerItemEntity.getName());
                    trackerItemBlockData.setTrackerId(tracker.getId());
                    trackerItemBlockData.setTrackerItem(this.fillTrackerItemVo(trackerItemEntity));
                    trackerItemBlockData.setUserPerm(trackerItemEntity.getPermission());
                }
                block.getData().setText(trackerItemEntity.getDescription());
            }
        });
        return doc;
    }

    public TrackerItemBlockData.InnerTrackerItemVo fillTrackerItemVo(TrackerItemVo trackerItemVo) {
        TrackerVo tracker = trackerService.findOneTracker(trackerItemVo.getTracker().getId());
        ProjectVo projectVo = projectService.findOneProject(trackerItemVo.getProject().getId());
        EnumItemVo trackerType = tracker.getTrackerType();
        TrackerItemBlockData.InnerTrackerItemVo trackerItemVo2 = new TrackerItemBlockData.InnerTrackerItemVo();
        trackerItemVo2.setProjectId(trackerItemVo.getProject().getId());
        trackerItemVo2.setId(trackerItemVo.getId());
        trackerItemVo2.setTrackerIcon(tracker.getIcon());
        trackerItemVo2.setTrackerColor(trackerType.getColor());
        trackerItemVo2.setTrackerBackgroundColor(trackerType.getBackgroundColor());
        trackerItemVo2.setRealEndDate(trackerItemVo.getRealEndDate());
        trackerItemVo2.setItemNo(trackerItemVo.getItemNo());
        trackerItemVo2.setProjectKeyName(projectVo.getKeyName());
        trackerItemVo2.setAssignedDate(trackerItemVo.getAssignedDate());
        trackerItemVo2.setTrackerId(trackerItemVo.getTracker().getId());
        Optional.ofNullable(trackerItemVo.getSprint())
                .ifPresent(value -> trackerItemVo2.setSprintId(value.getId()));
        Optional.ofNullable(trackerItemVo.getOwner())
                .ifPresent(value -> trackerItemVo2.setOwnerId(value.getId()));
        Optional.ofNullable(trackerItemVo.getMeaning())
                .ifPresent(value -> trackerItemVo2.setMeaningId(value.getId()));
        Optional.ofNullable(trackerItemVo.getPriority())
                .ifPresent(value -> trackerItemVo2.setPriorityId(value.getId()));
        Optional.ofNullable(trackerItemVo.getAssignedTo())
                .ifPresent(value -> trackerItemVo2.setAssignedTo(value.getId()));
        Optional.ofNullable(trackerItemVo.getSeverity())
                .ifPresent(value -> trackerItemVo2.setSeverityId(value.getId()));

        trackerItemVo2.setProgress(trackerItemVo.getProgress());
        trackerItemVo2.setCloseDate(trackerItemVo.getCloseDate());
        trackerItemVo2.setEstimateWorkingHours(trackerItemVo.getEstimateWorkingHours());
        trackerItemVo2.setPlanEndDate(trackerItemVo.getPlanEndDate());
        trackerItemVo2.setPlanStartDate(trackerItemVo.getPlanStartDate());
        trackerItemVo2.setRegisteredWorkingHours(trackerItemVo.getRegisteredWorkingHours());
        trackerItemVo2.setRemainingWorkingHours(trackerItemVo.getRemainingWorkingHours());
        trackerItemVo2.setRevision(trackerItemVo.getRevision());
        trackerItemVo2.setStatusId(trackerItemVo.getStatusId());
        trackerItemVo2.setValues(trackerItemVo.getValues());
        return trackerItemVo2;
    }

    public DocEntity findOneDoc(Long docId) {
        return docDao.findById(docId, DocEntity.class);
    }

    @Override
    public List<DocVo> findDocByIds(List<Long> docIds) {
        List<DocEntity> docEntities = docDao.findByIds(docIds);
        List<DocVo> docVos = DataUtils.toVo(docEntities, DocVo.class);
        List<Long> refIds = docEntities.stream().map(DocEntity::getBlocks).flatMap(
                blocks -> blocks.stream().map(block -> block.getData().getRefId())).collect(Collectors.toList());
        List<TrackerItemVo> trackerItemVos = trackerItemService.findTrackerItemByIds(refIds);
        Map<Long, TrackerItemVo> trackerItemVoMap = trackerItemVos.stream().collect(Collectors.toMap(TrackerItemVo::getId, Function.identity()));

        docVos.forEach(docVo -> {
            docVo.getBlocks().forEach(block -> {
                TrackerItemVo trackerItemVo = trackerItemVoMap.get(block.getData().getRefId());
                if (BlockTypes.TITLE.equals(block.getType())) {

                } else if (BlockTypes.HEADING.equals(block.getType())) {

                } else if (BlockTypes.PARAGRAPH.equals(block.getType())) {

                } else {
                    //BlockTypes.TRACKER_ITEM
                    TrackerItemBlockData trackerItemBlockData = ((TrackerItemBlockData) (block.getData()));
                    trackerItemBlockData.setName(trackerItemVo.getName());
                    trackerItemBlockData.setTrackerItem(fillTrackerItemVo(trackerItemVo));
                }
                block.getData().setText(trackerItemVo.getDescription());
            });
        });
        return docVos;
    }

    @Override
    public void createDoc(DocVo doc) {
        DocEntity entity = DataUtils.toEntity(doc, DocEntity.class);
        docDao.save(entity);
    }

    private TrackerItemVo block2TrackerItem(Long projectId, DocBlock docBlock, TrackerItemVo trackerItemVo) {
        boolean isNew = docBlock.getData().getRefId() == null ? true : false;
        if (trackerItemVo == null) {
            trackerItemVo = new TrackerItemVo();
        }
        IdNameReference<TrackerVo> trackerVoIdNameReference = new IdNameReference<>();
        IdNameReference<ProjectVo> projectVoIdNameReference = new IdNameReference<>();
        projectVoIdNameReference.setId(projectId);
        trackerItemVo.setId(docBlock.getData().getRefId());
        trackerItemVo.setTracker(trackerVoIdNameReference);
        trackerItemVo.setProject(projectVoIdNameReference);

        if (BlockTypes.TITLE.equals(docBlock.getType())) {
            trackerVoIdNameReference.setId(InternalTrackers.TITLE.getId());
            TitleBlockData titleBlockData = (TitleBlockData) docBlock.getData();
            trackerItemVo.setDescription(titleBlockData.getText());
//            if (StringUtils.isEmpty(trackerItemVo.getName())) {
            String cleanText = HtmlUtil.cleanHtmlTag(StrUtil.emptyIfNull(titleBlockData.getText()));
            if (StringUtils.isNotEmpty(cleanText)) {
                trackerItemVo.setName(cleanText.substring(0, cleanText.length() > 50 ? 50 : cleanText.length()));
            }
//            }
        } else if (BlockTypes.HEADING.equals(docBlock.getType())) {
            trackerVoIdNameReference.setId(InternalTrackers.HEADING.getId());
            HeaderBlockData headerBlockData = (HeaderBlockData) docBlock.getData();
            trackerItemVo.setDescription(headerBlockData.getText());
//            if (StringUtils.isEmpty(trackerItemVo.getName())) {
            String cleanText = HtmlUtil.cleanHtmlTag(headerBlockData.getText());
            if (StringUtils.isNotEmpty(cleanText)) {
                trackerItemVo.setName(cleanText.substring(0, cleanText.length() > 50 ? 50 : cleanText.length()));
            }
//            }
        } else if (BlockTypes.PARAGRAPH.equals(docBlock.getType())) {
            trackerVoIdNameReference.setId(InternalTrackers.PARAGRAPH.getId());
            ParagraphBlockData paragraphBlockData = (ParagraphBlockData) docBlock.getData();
            trackerItemVo.setDescription(paragraphBlockData.getText());
//            if (StringUtils.isEmpty(trackerItemVo.getName())) {
            String cleanText = HtmlUtil.cleanHtmlTag(StringUtils.isEmpty(paragraphBlockData.getText()) ? "" : paragraphBlockData.getText());
            if (StringUtils.isNotEmpty(cleanText)) {
                trackerItemVo.setName(cleanText.substring(0, cleanText.length() > 50 ? 50 : cleanText.length()));
            }
//            }
        } else
        //
//        if (BlockTypes.TRACKER_ITEM.equals(docBlock.getType()))
        {
            TrackerItemBlockData trackerItemBlockData = (TrackerItemBlockData) docBlock.getData();
            TrackerItemBlockData.InnerTrackerItemVo innerTrackerItemVo = trackerItemBlockData.getTrackerItem();
            if (innerTrackerItemVo != null) {

//                trackerItemBlockData.getTrackerItem().setName(trackerItemBlockData.getName());
//                trackerItemBlockData.getTrackerItem().setDescription(trackerItemBlockData.getText());
//                DocUtils.copyTrackerItem(trackerItemBlockData.getTrackerItem(), trackerItemVo);
                TrackerItemVo finalTrackerItemVo = trackerItemVo;
                Optional.ofNullable(innerTrackerItemVo.getAssignedDate()).ifPresent(value -> finalTrackerItemVo.setAssignedDate(value));
            }
            trackerVoIdNameReference.setId(Long.parseLong(docBlock.getType()));
            trackerItemVo.setDescription(trackerItemBlockData.getText());
            trackerItemVo.setName(trackerItemBlockData.getName());
        }

        trackerItemVo.setTracker(trackerVoIdNameReference);

        return trackerItemVo;
    }

}
