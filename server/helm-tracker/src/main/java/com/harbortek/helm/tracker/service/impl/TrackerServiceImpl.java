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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.harbortek.helm.common.constants.CacheConstants;
import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.dao.UserDao;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.service.PermissionService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.PermissionVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.EnumCodes;
import com.harbortek.helm.tracker.constants.InternalTrackers;
import com.harbortek.helm.tracker.constants.NotificationEvents;
import com.harbortek.helm.tracker.constants.ObjectTypes;
import com.harbortek.helm.tracker.constants.SystemFields;
import com.harbortek.helm.tracker.constants.TrackerLayoutSections;
import com.harbortek.helm.tracker.constants.TrackerLayouts;
import com.harbortek.helm.tracker.constants.TrackerPermissions;
import com.harbortek.helm.tracker.dao.ProjectPageDao;
import com.harbortek.helm.tracker.dao.TrackerDao;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.dao.ViewDao;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.service.ProjectRoleMemberService;
import com.harbortek.helm.tracker.service.SprintService;
import com.harbortek.helm.tracker.service.TrackerService;
import com.harbortek.helm.tracker.template.builder.EntityResolver;
import com.harbortek.helm.tracker.template.builder.TrackerXmlReader;
import com.harbortek.helm.tracker.template.builder.TrackerXmlWriter;
import com.harbortek.helm.tracker.util.TrackerUtils;
import com.harbortek.helm.tracker.vo.ProjectRoleMemberVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.tracker.vo.plan.SprintVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.MultiOptionsField;
import com.harbortek.helm.tracker.vo.tracker.fields.OptionsField;
import com.harbortek.helm.tracker.vo.tracker.fields.StatusField;
import com.harbortek.helm.tracker.vo.tracker.fields.TableField;
import com.harbortek.helm.tracker.vo.tracker.fields.TestStepField;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.layout.TrackerLayout;
import com.harbortek.helm.tracker.vo.tracker.nofitication.CustomerTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.SystemTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.TrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.permissions.FieldPermission;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransition;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.PermissionCacheUtils;
import com.harbortek.helm.util.SecurityUtils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.comparator.CompareUtil;
import lombok.extern.slf4j.Slf4j;

@Service("trackerService")
@Slf4j
public class TrackerServiceImpl implements TrackerService {
    @Autowired
    TrackerDao trackerDao;

    @Autowired
    EnumService enumService;

    @Autowired
    UserDao userDao;

    @Autowired
    ViewDao viewDao;

    @Autowired
    ProjectRoleMemberService projectRoleMemberService;
    @Autowired
    ProjectPageDao projectPageDao;
    @Autowired
    TrackerItemDao trackerItemDao;

    @Autowired
    SprintService sprintService;

    @Autowired
    PermissionService  permissionService;

    @Autowired
    RoleService roleService;


    public TrackerVo createTracker(TrackerVo tracker) {
        TrackerEntity trackerEntity = DataUtils.toEntity(tracker, TrackerEntity.class);
        trackerEntity.setId(IDUtils.getId());
        if (trackerEntity.getTrackerFields().isEmpty()) {
            trackerEntity.setTrackerFields(TrackerUtils.buildSystemFields());
        }
        TrackerEntity newTracker = trackerDao.createTracker(trackerEntity);
        //布局
        createDefaultLayouts(newTracker);

        trackerDao.updateTracker(newTracker);
        //初始化权限  项目负责人，项目所有成员
        createDefaultPermissions(newTracker);

        return tracker;
    }

    private void createDefaultLayouts(TrackerEntity tracker) {
        List<TrackerField> trackerFields = tracker.getTrackerFields();
        List<IdNameReference> keyFields =
                trackerFields.stream().filter(field -> field.getSystemProperty().equals(SystemFields.OWNER) ||
                                     field.getSystemProperty().equals(SystemFields.PRIORITY) ||
                                     field.getSystemProperty().equals(SystemFields.SEVERITY) ||
                                     field.getSystemProperty().equals(SystemFields.STATUS)).
                             map(IdNameReference::new).collect(Collectors.toList());

        TrackerLayout newLayout = TrackerLayout.builder().name(TrackerLayouts.NEW).keyFields(keyFields).
                                               sections(Arrays.asList(TrackerLayoutSections.DETAIL,
                                                                      TrackerLayoutSections.RELATED_ITEMS,
                                                                      TrackerLayoutSections.ATTACHMENTS))
                                               .fields(Collections.emptyList()).build();
        TrackerLayout detailLayout = TrackerLayout.builder().name(TrackerLayouts.DETAIL).keyFields(keyFields).
                                                  sections(newLayout.getSections()).fields(Collections.emptyList())
                                                  .build();
        TrackerLayout listLayout = TrackerLayout.builder().name(TrackerLayouts.LIST).
                                                keyFields(Collections.emptyList()).sections(Collections.emptyList()).
                                                fields(Collections.emptyList()).build();
        tracker.setTrackerLayouts(Arrays.asList(newLayout, detailLayout, listLayout));
    }

    private void createDefaultPermissions(TrackerEntity newTracker) {
        List<PermissionVo> permissionVos = new ArrayList<>();
        List<RoleVo> roles = roleService.findRoles(SpecialRole.SCOPE_PROJECT, newTracker.getProjectId());
        List<RoleVo> roleVos = roles.stream().filter(role -> SpecialRole.PROJECT_OWNER.equals(role.getSpecialRoleType()) ||
                SpecialRole.PROJECT_ALL_MEMBERS.equals(role.getSpecialRoleType())).toList();
        roleVos.forEach(role -> {
            BaseIdentity identity = new BaseIdentity();
            identity.setId(role.getId());
            identity.setType(IdentityTypes.SPECIAL_ROLE);
            TrackerPermissions.TRACKER_PERMISSIONS.forEach(permission -> {
                PermissionVo permissionVo = new PermissionVo();
                permissionVo.setId(IDUtils.getId());
                permissionVo.setName(permission);
                permissionVo.setIdentity(identity);
                permissionVo.setResourceId(newTracker.getId());
                permissionVos.add(permissionVo);
            });
        });
        permissionService.grant(permissionVos);
    }

    @CacheEvict(value = CacheConstants.OBJECT_CACHE_NAME, key = "'objects_TrackerVo_'+#tracker.id")
    public TrackerVo updateTracker(TrackerVo tracker) {
        TrackerEntity trackerEntity = DataUtils.toEntity(tracker, TrackerEntity.class);
        trackerDao.updateTracker(trackerEntity);
        return tracker;
    }

    @CacheEvict(value = CacheConstants.OBJECT_CACHE_NAME, key = "'objects_TrackerVo_'+#tracker.id")
    public void deleteTracker(TrackerVo tracker) {
        TrackerEntity trackerEntity = DataUtils.toEntity(tracker, TrackerEntity.class);
        List<ProjectPageEntity> pages = projectPageDao.findPageByTrackerId(tracker.getId());
        if (ObjectUtils.isNotEmpty(pages) && pages.size() > 0) {
            ServiceException.throwException("删除失败！工作项类型存在关联页面。");
        }
        //List<TrackerItemEntity> items =trackerItemDao.findItemByTrackerId(tracker.getId(),null);
        //if(ObjectUtils.isNotEmpty(items)&&items.size()>0){
        //    ServiceException.throwException("删除失败！工作项类型存在工作项。");
        //}
        trackerItemDao.deleteByTrackerIds(Collections.singletonList(tracker.getId()));
        trackerDao.deleteTracker(trackerEntity);
    }

    @Cacheable(value = CacheConstants.OBJECT_CACHE_NAME, key = "'objects_TrackerVo_'+#trackerId")
    public TrackerVo findOneTracker(Long trackerId) {
        TrackerVo trackerVo = InternalTrackers.getTrackerVo(trackerId);
        if (trackerVo != null) {
            return trackerVo;
        } else {
            TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);

            //自动填充属性枚举值
            fillTrackerFields(trackerEntity.getTrackerFields(),trackerEntity.getTrackerStatuses(),trackerEntity.getProjectId());

            TrackerVo vo = DataUtils.toVo(trackerEntity, TrackerVo.class);
            vo.setTrackerPermissions(findTrackerPermissions(trackerId));

            return vo;
        }
    }

    //自动填充属性枚举值
    public void fillTrackerFields(List<TrackerField> trackerFields,List<TrackerStatus> trackerStatuses,Long projectId) {
        if(ObjectUtils.isNotEmpty(trackerFields)) {
            for (TrackerField trackerField : trackerFields) {
                if (trackerField instanceof OptionsField || trackerField instanceof MultiOptionsField) {
                    Long enumId;
                    List<OptionsField.OptionItem> optionItems = null;

                    if (trackerField instanceof OptionsField) {
                        enumId = ((OptionsField) trackerField).getEnumId();
                    } else {
                        enumId = ((MultiOptionsField) trackerField).getEnumId();
                    }
                    if (ObjectUtils.isValid(enumId)) {
                        List<EnumItemVo> enumVos = enumService.findEnumItems(enumId, projectId);
                        optionItems = enumVos.stream().map(item -> OptionsField.OptionItem.builder().id((item.getId()))
                                        .name(item.getName())
                                        .description(
                                                item.getDescription())
                                        .color(item.getColor())
                                        .backgroundColor(
                                                item.getBackgroundColor())
                                        .build())
                                .collect(Collectors.toList());
                    } else if (trackerField.isSystem()) {
                        String systemProperty = trackerField.getSystemProperty();
                        if (SystemFields.PRIORITY.equals(systemProperty)) {
                            List<EnumItemVo> enumVos =
                                    enumService.findEnumItemsByCode(projectId,
                                            EnumCodes.TRACKER_PRIORITY);
                            optionItems =
                                    enumVos.stream().map(item -> OptionsField.OptionItem.builder().id((item.getId()))
                                                    .name(item.getName())
                                                    .description(
                                                            item.getDescription())
                                                    .color(item.getColor())
                                                    .backgroundColor(
                                                            item.getBackgroundColor())
                                                    .build())
                                            .collect(Collectors.toList());
                        } else if (SystemFields.SEVERITY.equals(systemProperty)) {
                            List<EnumItemVo> enumVos =
                                    enumService.findEnumItemsByCode(projectId,
                                            EnumCodes.TRACKER_SEVERITY);
                            optionItems =
                                    enumVos.stream().map(item -> OptionsField.OptionItem.builder().id((item.getId()))
                                                    .name(item.getName())
                                                    .description(
                                                            item.getDescription())
                                                    .color(item.getColor())
                                                    .backgroundColor(
                                                            item.getBackgroundColor())
                                                    .build())
                                            .collect(Collectors.toList());
                        } else if (SystemFields.SPRINT.equals(systemProperty)) {
                            Collection<SprintVo> sprintVos = sprintService.findSprints(projectId);
                            optionItems =
                                    sprintVos.stream().map(item -> OptionsField.OptionItem.builder().id((item.getId()))
                                                    .name(item.getName())
                                                    .description(
                                                            item.getDescription())
                                                    .build())
                                            .collect(Collectors.toList());
                        } else if (SystemFields.STATUS_TYPE.equals(systemProperty)) {
                            List<EnumItemVo> enumVos =
                                    enumService.findEnumItemsByCode(projectId,
                                            EnumCodes.TRACKER_STATUS_MEANING);
                            optionItems =
                                    enumVos.stream().map(item -> OptionsField.OptionItem.builder().id((item.getId()))
                                                    .name(item.getName())
                                                    .description(
                                                            item.getDescription())
                                                    .color(item.getColor())
                                                    .backgroundColor(
                                                            item.getBackgroundColor())
                                                    .build())
                                            .collect(Collectors.toList());
                            //                        } else if (SystemFields.TEST_CASE_TYPE.equals(systemProperty)) {
                            //                            List<EnumItemVo> enumVos =
                            //                                    enumService.findEnumItemsByCode(trackerEntity.getProjectId(),
                            //                                                                    EnumCodes.TEST_CASE_TYPE);
                            //                            optionItems =
                            //                                    enumVos.stream().map(item -> OptionsField.OptionItem.builder().id((item.getId()))
                            //                                                                                        .name(item.getName())
                            //                                                                                        .description(
                            //                                                                                                item.getDescription())
                            //                                                                                        .color(item.getColor())
                            //                                                                                        .backgroundColor(
                            //                                                                                                item.getBackgroundColor())
                            //                                                                                        .build())
                            //                                           .collect(Collectors.toList());
                        }else if (trackerField instanceof StatusField &&ObjectUtils.isNotEmpty(trackerStatuses)) {
                            //自动填充状态字段的选项值
                            AtomicInteger index = new AtomicInteger(1);
                            optionItems =
                                    trackerStatuses.stream().map((s) ->
                                         OptionsField.OptionItem.builder().id((long) index.getAndIncrement())
                                                .name(s.getName()).build()
                                    ).collect(Collectors.toList());
                        }
                    }
                    if (trackerField instanceof OptionsField) {
                        ((OptionsField) trackerField).setItems(optionItems);
                    } else {
                        ((MultiOptionsField) trackerField).setItems(optionItems);
                    }
                }
            }
        }
    }

    public List<TrackerVo> findByProject(Long projectId) {
        return findByProject(projectId, false);
    }

    public List<TrackerVo> findByProject(Long projectId, boolean showInternal) {
        List<TrackerVo> trackerVos = new ArrayList<>();
        if (showInternal) {
            trackerVos.add(InternalTrackers.TITLE);
            trackerVos.add(InternalTrackers.HEADING);
            trackerVos.add(InternalTrackers.PARAGRAPH);
        }
        List<TrackerEntity> byProject = trackerDao.findByProject(projectId);
        trackerVos.addAll(DataUtils.toVo(byProject, TrackerVo.class));

        return trackerVos;
    }

    @Override
    public List<TrackerVo> findTrackerByIds(List<Long> trackerIds) {
        return DataUtils.toVo(trackerDao.findByIds(trackerIds, TrackerEntity.class,true), TrackerVo.class);
    }

    @Override
    public void updateTrackerLayout(Long trackerId, TrackerLayout trackerLayout) {
        trackerDao.updateTrackerLayout(trackerId, trackerLayout);
    }

//    @Override
//    public void updateTrackerPermission(Long trackerId, TrackerRolePermission trackerRolePermission) {
//        trackerDao.updateTrackerRolePermission(trackerId,trackerRolePermission);
//    }


    @Override
    public TrackerLayout findOneTrackerLayout(Long trackerId, String layoutName) {
        return trackerDao.findOneTrackerLayout(trackerId, layoutName);
    }

    @Override
    public List<TrackerStateTransition> findTrackerStateTransitions(Long trackerId) {
        return trackerDao.findTrackerStateTransitions(trackerId);
    }

    @Override
    public void updateTrackerStateTransitions(Long trackerId, List<TrackerStateTransition> trackerStateTransitions) {
        trackerDao.updateTrackerStateTransitions(trackerId, trackerStateTransitions);
    }

    @Override
    public void updateTrackerStatus(Long trackerId, TrackerStatus trackerStatus) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerStatus> trackerStatuses = trackerEntity.getTrackerStatuses();
        TrackerStatus existed =
                trackerStatuses.stream().filter(s -> {
                                   return s.getId().equals(trackerStatus.getId());
                               }).findFirst()
                               .orElse(trackerStatus);

        existed.setName(trackerStatus.getName());
        existed.setMeaning(trackerStatus.getMeaning());
        existed.setDescription(trackerStatus.getDescription());
        if (existed.getId() == null) { //新增状态
            existed.setId(IDUtils.getId());
            trackerStatuses.add(existed);
            trackerDao.updateTrackerStatus(trackerId, trackerStatuses);
        } else { //修改状态
            //更新所有相关的数据
            ObjectUtils.fixIdNameReferenceWith(trackerEntity, existed);
            trackerDao.updateTracker(trackerEntity);
        }
    }

    @Override
    public void deleteTrackerStatus(Long trackerId, TrackerStatus trackerStatus) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerStatus> trackerStatuses = trackerEntity.getTrackerStatuses();

        trackerStatuses =
                trackerStatuses.stream().filter(s -> CompareUtil.compare(s.getId(), trackerStatus.getId()) != 0)
                               .collect(
                                       Collectors.toList());
        trackerEntity.setTrackerStatuses(trackerStatuses);

//        trackerDao.updateTrackerStatus(trackerId, trackerStatuses);
        ObjectUtils.deleteNameReferenceWith(trackerEntity, trackerStatus);
        trackerDao.updateTracker(trackerEntity);
    }

    @Override
    public List<TrackerStatus> findTrackerStatus(Long trackerId) {
        return trackerDao.findTrackerStatus(trackerId);
    }

    @Override
    public TrackerStatus createTrackerStatus(Long trackerId, TrackerStatus trackerStatus) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerStatus> trackerStatuses = trackerEntity.getTrackerStatuses();
        trackerStatus.setId(IDUtils.getId());
        trackerStatus.setInitial(false);
        trackerStatus.setOrdinary(trackerStatuses.size() + 1);
        trackerStatuses.add(trackerStatus);
        trackerDao.updateTrackerStatus(trackerId, trackerStatuses);
        return trackerStatus;
    }

    @Override
    public void sortTrackerStatus(Long trackerId, List<TrackerStatus> trackerStatuses) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerStatus> trackerStatusList = trackerEntity.getTrackerStatuses();

        Map<Long, TrackerStatus> map = new HashMap<>();
        trackerStatuses.forEach(s -> {
            map.put(s.getId(), s);
        });

        trackerStatusList.forEach(existed -> {
            existed.setOrdinary(map.get(existed.getId()).getOrdinary());
        });
        trackerStatusList.sort((o1, o2) -> {
            return o1.getOrdinary() - o2.getOrdinary();
        });

        trackerDao.updateTrackerStatus(trackerId, trackerStatusList);
    }

    @Override
    public void setInitialTrackerStatus(Long trackerId, TrackerStatus trackerStatus) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerStatus> trackerStatuses = trackerEntity.getTrackerStatuses();
        trackerStatuses.forEach(s -> {
            if (CompareUtil.compare(s.getId(), trackerStatus.getId()) == 0) {
                s.setInitial(true);
            } else {
                s.setInitial(false);
            }
        });

        trackerDao.updateTrackerStatus(trackerId, trackerStatuses);
    }

    @Override
    public boolean trackerStatusUseByTransition(Long trackerId, TrackerStatus trackerStatus) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerStateTransition> stateTransitions = trackerEntity.getTrackerStateTransitions();
        return stateTransitions.stream().anyMatch(trackerStateTransition -> {
            if (CompareUtil.compare(trackerStateTransition.getTransitionFrom().getId(), trackerStatus.getId()) == 0 &&
                    !trackerStateTransition.getDeleted()) {
                return true;
            } else
                return CompareUtil.compare(trackerStateTransition.getTransitionTo().getId(), trackerStatus.getId()) ==
                        0 &&
                        !trackerStateTransition.getDeleted();
        });
    }

    @Override
    public boolean trackerStatusUseByWorkItem(Long trackerId, TrackerStatus trackerStatus) {
        List<Long> itemList = trackerItemDao.findItemIdsByTrackerId(trackerId, trackerStatus.getId());
        return !itemList.isEmpty();
    }

    @Override
    public void replaceWorkItemStatus(Long trackerId, Long oldTrackerStatusId, Long newTrackerStatusId) {
        Optional<TrackerStatus> oldTrackerStatus =
                this.findTrackerStatus(trackerId).stream().filter(s -> s.getId().equals(oldTrackerStatusId))
                    .findFirst();

        Optional<TrackerStatus> newTrackerStatus =
                this.findTrackerStatus(trackerId).stream().filter(s -> s.getId().equals(newTrackerStatusId))
                    .findFirst();

        if (oldTrackerStatus.isPresent() && newTrackerStatus.isPresent()) {
            trackerItemDao.replaceWorkItemStatus(trackerId, oldTrackerStatus.get(), newTrackerStatus.get());
        }
    }

    @Override
    public void createTrackers(Collection<TrackerVo> trackers) {
        List<TrackerEntity> trackerEntities = new ArrayList<>();
        trackers.forEach(tracker -> {
            TrackerEntity trackerEntity = TrackerEntity.builder()
                                                       .id(tracker.getId())
                                                       .name(tracker.getName())
                                                       .description(tracker.getDescription())
                                                       .icon(tracker.getIcon())
                                                       .trackerTypeId(tracker.getTrackerType().getId())
                                                       .projectId(tracker.getProjectId())
                                                       .defaultLayout(tracker.getDefaultLayout())
                                                       .trackerStatuses(tracker.getTrackerStatuses())
//                    .trackerPermissions(tracker.getTrackerPermissions())
                                                       .trackerStateTransitions(
                                                               tracker.getTrackerStateTransitions())
                                                       .trackerFields(tracker.getTrackerFields())
                                                       .trackerLayouts(tracker.getTrackerLayouts())
                                                       .trackerNotification(tracker.getTrackerNotification())
                                                       .build();
            if(ObjectUtils.isEmpty(trackerEntity.getId())){
                trackerEntity.setId(IDUtils.getId());
            }
            trackerEntities.add(trackerEntity);

            tracker.getTrackerPermissions().forEach(trackerPermission -> {
                permissionService.grant(trackerPermission.getPermissionName(),trackerPermission.getGranted(),
                                        tracker.getId());
            });

        });
        trackerDao.createTrackers(trackerEntities);


    }

    @Override
    public TrackerVo findOneTrackerByName(Long projectId, String trackerName) {
        TrackerEntity trackerEntity = trackerDao.findOneTrackerByName(projectId,trackerName);
        return DataUtils.toVo(trackerEntity, TrackerVo.class);
    }

    /******  工作项步骤 ******/
    @Override
    public TrackerStateTransition createTrackerStateTransition(Long trackerId,
                                                               TrackerStateTransition trackerStateTransition) {

        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);


        IdNameReference start = new IdNameReference(ObjectUtils.findById(trackerEntity,
                                                                         trackerStateTransition.getTransitionFrom()
                                                                                               .getId(),
                                                                         TrackerStatus.class));
        IdNameReference end = new IdNameReference(ObjectUtils.findById(trackerEntity,
                                                                       trackerStateTransition.getTransitionTo().getId(),
                                                                       TrackerStatus.class));

        trackerStateTransition.setId(IDUtils.getId());
        trackerStateTransition.setTransitionFrom(start);
        trackerStateTransition.setTransitionTo(end);
        trackerEntity.getTrackerStateTransitions().add(trackerStateTransition);
        trackerDao.updateTrackerStateTransitions(trackerId, trackerEntity.getTrackerStateTransitions());
        return trackerStateTransition;
    }

    @Override
    public void updateTrackerStateTransition(Long trackerId, TrackerStateTransition trackerStateTransition) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerStateTransition existed = ObjectUtils.findById(trackerEntity,
                                                              trackerStateTransition.getId(),
                                                              TrackerStateTransition.class);
        IdNameReference start = new IdNameReference(ObjectUtils.findById(trackerEntity,
                                                                         trackerStateTransition.getTransitionFrom()
                                                                                               .getId(),
                                                                         TrackerStatus.class));
        IdNameReference end = new IdNameReference(ObjectUtils.findById(trackerEntity,
                                                                       trackerStateTransition.getTransitionTo().getId(),
                                                                       TrackerStatus.class));

        existed.setName(trackerStateTransition.getName());
        existed.setTransitionFrom(start);
        existed.setTransitionTo(end);
        existed.setPermitted(trackerStateTransition.getPermitted());
        existed.setValidators(trackerStateTransition.getValidators());
        existed.setActions(trackerStateTransition.getActions());

        trackerDao.updateTrackerStateTransitions(trackerId, trackerEntity.getTrackerStateTransitions());
    }

    @Override
    public void deleteTrackerStateTransition(Long trackerId, TrackerStateTransition trackerStateTransition) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerStateTransition existed = ObjectUtils.findById(trackerEntity,
                                                              trackerStateTransition.getId(),
                                                              TrackerStateTransition.class);
        trackerEntity.getTrackerStateTransitions().remove(existed);
        trackerDao.updateTrackerStateTransitions(trackerId, trackerEntity.getTrackerStateTransitions());
    }

    @Override
    public void renameTrackerStateTransition(Long trackerId, TrackerStateTransition trackerStateTransition) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerStateTransition existed = ObjectUtils.findById(trackerEntity,
                                                              trackerStateTransition.getId(),
                                                              TrackerStateTransition.class);
        existed.setName(trackerStateTransition.getName());

        trackerDao.updateTrackerStateTransitions(trackerId, trackerEntity.getTrackerStateTransitions());
    }

    @Override
    public void grantTrackerStateTransitionPermitted(Long trackerId, Long transitionId, BaseIdentity identity) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerStateTransition> trackerStateTransitions = trackerEntity.getTrackerStateTransitions();
        Optional<TrackerStateTransition> existed =
                trackerStateTransitions.stream().filter(item -> {
                    return item.getId().equals(transitionId);
                }).findFirst();
        if (existed.isPresent()) {
            existed.get().getPermitted().add(identity);
        }
        trackerDao.updateTrackerStateTransitions(trackerId, trackerStateTransitions);
    }

    @Override
    public void unGrantTrackerStateTransitionPermitted(Long trackerId, Long transitionId, BaseIdentity identity) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerStateTransition> trackerStateTransitions = trackerEntity.getTrackerStateTransitions();
        Optional<TrackerStateTransition> existedStateTransition =
                trackerStateTransitions.stream().filter(item -> {
                    return item.getId().equals(transitionId);
                }).findFirst();
        if (existedStateTransition.isPresent()) {
            List<BaseIdentity> identityList = existedStateTransition.get().getPermitted();
            BaseIdentity existed =
                    identityList.stream().filter(item -> {
                                    return item.getId().equals(identity.getId());
                                }).findFirst()
                                .get();
            identityList.remove(existed);
        }
        trackerDao.updateTrackerStateTransitions(trackerId, trackerStateTransitions);
    }

    @Override
    public void updateTrackerOrdinary(List<TrackerVo> trackerVos) {
        Collection<TrackerEntity> trackerEntities = DataUtils.toEntity(trackerVos, TrackerEntity.class);
        trackerDao.updateTrackerOrdinary(trackerEntities);
    }

    @Override
    public void copyTracker(TrackerVo trackerVo) {
        TrackerVo sourceTracker = DataUtils.toVo(trackerDao.findOneTracker(trackerVo.getId()), TrackerVo.class);
        Long projectId = trackerVo.getProjectId();

        TrackerXmlWriter writer = new TrackerXmlWriter(sourceTracker);
        File tempFile = null;
        try {
            tempFile = File.createTempFile("tracker", ".xml");
            writer.write(tempFile);

            EntityResolver entityResolver = new EntityResolver();
            Collection<EnumItemVo> meanings =
                    enumService.findEnumItemsByCode(projectId, EnumCodes.TRACKER_STATUS_MEANING);
            meanings.forEach(meaning -> {
                entityResolver.register(meaning, ObjectTypes.TRACKER_STATUS_MEANING);
            });

            Collection<EnumItemVo> trackerPriorities = enumService.findEnumItemsByCode(projectId,
                                                                                       EnumCodes.TRACKER_PRIORITY);
            trackerPriorities.forEach(priority -> {
                entityResolver.register(priority, ObjectTypes.TRACKER_PRIORITY);
            });

            Collection<EnumItemVo> trackerTypeVos = enumService.findEnumItemsByCode(projectId, EnumCodes.TRACKER_TYPE);
            trackerTypeVos.forEach(trackerTypeVo -> {
                entityResolver.register(trackerTypeVo, ObjectTypes.TRACKER_TYPE);
            });

            //用户
            Page<UserEntity> users = userDao.findUsers(null, Pageable.unpaged(), null);
            users.forEach(user -> {
                entityResolver.register(DataUtils.toVo(user, UserVo.class), ObjectTypes.USER);
            });

            List<ProjectRoleMemberVo> roleMembers = projectRoleMemberService.findProjectRoleMembers(projectId, true);
            roleMembers.stream().forEach(r -> {
                entityResolver.register(r, ObjectTypes.PROJECT_ROLE_MEMBER);
            });

            Resource resource = new FileSystemResource(tempFile);
            TrackerVo targetTracker = new TrackerXmlReader(entityResolver).build(resource);

            TrackerEntity trackerEntity = DataUtils.toEntity(targetTracker, TrackerEntity.class);
            trackerEntity.setId(IDUtils.getId());
            trackerEntity.setProjectId(projectId);
            trackerDao.createTracker(trackerEntity);
            //复制权限
            copyTrackerPermissions(sourceTracker.getId(), trackerEntity.getId());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            ServiceException.throwException("复制工作项失败");
        }
    }

    private void copyTrackerPermissions(Long sourceTrackerId, Long targetTrackerId) {
        List<PermissionVo> permissions = permissionService.findPermissionsByResourceId(sourceTrackerId);
        permissions.forEach(p->{
            p.setId(IDUtils.getId());
            p.setResourceId(targetTrackerId);
        });
        permissionService.grant(permissions);
    }

    @Override
    public void copyTrackerFields(Long trackerId, List<TrackerField> trackerFields) {
        TrackerEntity tracker = trackerDao.findOneTracker(trackerId);
        List<TrackerField> trackerFieldList = tracker.getTrackerFields();
        trackerFields.forEach(trackerField -> {
            trackerField.setId(IDUtils.getId());
            trackerField.setExceptStatus(null);
            trackerField.setPermission(FieldPermission.builder().type(FieldPermission.UNRESTRICTED).build());
            trackerFieldList.add(trackerField);
        });
        trackerDao.updateTrackerFields(trackerId, trackerFieldList);
    }

    @Override
    public void updateTrackerNameAndType(TrackerVo trackerVo) {
        TrackerEntity oldTracker = trackerDao.findOneTracker(trackerVo.getId());
        oldTracker.setName(trackerVo.getName());
        oldTracker.setTrackerTypeId(trackerVo.getTrackerType().getId());
        trackerDao.updateNameAndType(oldTracker);
    }


    /**
     * 工作项属性
     */

    @Override
    public List<TrackerField> findTrackerFields(Long trackerId) {
        return trackerDao.findTrackerFields(trackerId);
    }

    @Override
    public TrackerField createTrackerField(Long trackerId, TrackerField trackerField) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerField> trackerFields = trackerEntity.getTrackerFields();
        trackerField.setId(IDUtils.getId());
        trackerField.setPermission(FieldPermission.builder().type(FieldPermission.UNRESTRICTED).build());
        trackerFields.add(trackerField);

        if (trackerField instanceof TableField) {
            List<TrackerField> columns = ((TableField) trackerField).getColumns();
            if (trackerField instanceof TestStepField) {
                columns = ((TestStepField) trackerField).createDefaultColumns();
                ((TableField) trackerField).setColumns(columns);
            }
            columns.forEach(column -> {
                column.setId(IDUtils.getId());
            });
        }
        trackerDao.updateTrackerFields(trackerId, trackerFields);
        return trackerField;
    }

    @Override
    public void updateTrackerField(Long trackerId, TrackerField trackerField) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerField> trackerFields = trackerEntity.getTrackerFields();
        TrackerField existed = ObjectUtils.findById(trackerEntity.getTrackerFields(),
                                                    trackerField.getId(),
                                                    TrackerField.class);

        existed.setName(trackerField.getName());
        existed.setTitle(trackerField.getTitle());
        existed.setMandatory(trackerField.getMandatory());
        existed.setExceptStatus(trackerField.getExceptStatus());
        existed.setPermission(trackerField.getPermission());
        existed.setDefaultValue(trackerField.getDefaultValue());
        if (existed instanceof OptionsField) {
            ((OptionsField) existed).setEnumId(((OptionsField) trackerField).getEnumId());
        }
        if (existed instanceof TableField) {
            List<TrackerField> columns = ((TableField) trackerField).getColumns();
            if (trackerField instanceof TestStepField && columns.size() == 0) {
                columns = ((TestStepField) trackerField).createDefaultColumns();

            }
           columns.forEach(column -> {
               if (!ObjectUtils.isValid(column.getId())) {
                   column.setId(IDUtils.getId());
               }
            });
            ((TableField) existed).setColumns(columns);
        }

        //HACK 更新布局中的字段LABEL
        List<TrackerLayout> layouts = trackerEntity.getTrackerLayouts();
        layouts.forEach(layout -> {
            layout.updateField(trackerField);
        });

        trackerDao.updateTracker(trackerEntity);
    }

    @Override
    public void deleteTrackerField(Long trackerId, TrackerField trackerField) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        List<TrackerField> trackerFields = trackerEntity.getTrackerFields();
        TrackerField existed = ObjectUtils.findById(trackerEntity,
                                                    trackerField.getId(),
                                                    TrackerField.class);

        trackerFields.remove(existed);
        ObjectUtils.deleteNameReferenceWith(trackerEntity, existed);
        //删除布局中的数据
        List<TrackerLayout> layouts = trackerEntity.getTrackerLayouts();
        layouts.forEach(layout -> {
            layout.deleteField(trackerField);
        });

        trackerDao.updateTracker(trackerEntity);
    }

    @Override
    public List<PermissionGrantVo> findTrackerPermissions(Long trackerId) {
        List<PermissionVo> permissionVos = permissionService.findPermissionsByResourceId(trackerId);
        Map<String,List<PermissionVo>> permissionMap =
                permissionVos.stream().collect(Collectors.groupingBy(PermissionVo::getName));

        List<PermissionGrantVo> permissions = new ArrayList<>();
        permissionMap.keySet().forEach(permissionName -> {
            List<PermissionVo> permissionVoList = permissionMap.get(permissionName);
            PermissionGrantVo trackerRolePermission = PermissionGrantVo.builder()
                                                                       .permissionName(permissionName)
                                                                       .granted(permissionVoList.stream()
                                                                                                          .map(PermissionVo::getIdentity)
                                                                                                          .collect(Collectors.toList()))
                                                                       .build();
            permissions.add(trackerRolePermission);
        });


        return permissions;
    }

    @Override
    public void grantTrackerPermission(Long trackerId, String permissionName, BaseIdentity identity) {
        permissionService.grant(permissionName, identity,trackerId);
//        TrackerPermissions.TRACKER_PERMISSIONS.forEach(permission -> {
//            permissionService.grant(permission, identity,trackerId);
//        });
        PermissionCacheUtils.evictPermissions(trackerId);
        PermissionCacheUtils.evictGrantedPermissions((Long) SecurityUtils.get(SecurityUtils.PROJECT_ID));
    }

    @Override
    public void unGrantTrackerPermission(Long trackerId, String permissionName, BaseIdentity identity) {
        permissionService.unGrant(permissionName, identity,trackerId);
        PermissionCacheUtils.evictPermissions(trackerId);
        PermissionCacheUtils.evictGrantedPermissions((Long) SecurityUtils.get(SecurityUtils.PROJECT_ID));
    }

    @Override
    public TrackerNotification findTrackerNotification(Long trackerId) {
        return trackerDao.findTrackerNotification(trackerId);
    }

    @Override
    public void subscribeSystemEvent(Long trackerId, String eventName, BaseIdentity identity) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification notification = trackerEntity.getTrackerNotification();
        List<SystemTrackerNotification> trackerNotifications =
                trackerEntity.getTrackerNotification().getSystemTrackerNotifications();
        Optional<SystemTrackerNotification> existed =
                trackerNotifications.stream().filter(item -> {
                    return item.getEventName().equals(eventName);
                }).findFirst();
        if (existed.isPresent()) {
            if (ObjectUtils.isNotEmpty(existed.get().getSubscribers())) {
                existed.get().getSubscribers().add(identity);
            } else {
                existed.get().setSubscribers(CollectionUtil.newArrayList(identity));
            }
        } else {
            trackerNotifications.add(SystemTrackerNotification.builder()
                                                              .id(IDUtils.getId()).eventName(eventName)
                                                              .subscribers(CollectionUtil.newArrayList(identity))
                                                              .build());
        }
        trackerDao.updateTrackerNotification(trackerId, notification);
    }

    @Override
    public void unSubscribeSystemEvent(Long trackerId, String eventName, BaseIdentity identity) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification notification = trackerEntity.getTrackerNotification();
        List<SystemTrackerNotification> trackerNotifications =
                trackerEntity.getTrackerNotification().getSystemTrackerNotifications();
        Optional<SystemTrackerNotification> existedNotification =
                trackerNotifications.stream().filter(item -> {
                    return item.getEventName().equals(eventName);
                }).findFirst();
        if (existedNotification.isPresent()) {
            List<BaseIdentity> identityList = existedNotification.get().getSubscribers();
            BaseIdentity existed =
                    identityList.stream().filter(item -> {
                                    return item.getId().equals(identity.getId());
                                }).findFirst()
                                .get();
            identityList.remove(existed);
        }
        trackerDao.updateTrackerNotification(trackerId, notification);
    }

    public void subscribeCustomerEvent(Long trackerId, Long fieldId, BaseIdentity identity) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification notification = trackerEntity.getTrackerNotification();
        List<CustomerTrackerNotification> trackerNotifications =
                trackerEntity.getTrackerNotification().getCustomerTrackerNotifications();
        Optional<CustomerTrackerNotification> existedNotification =
                trackerNotifications.stream().filter(item -> {
                    return item.getTrackerField().getId().equals(fieldId);
                }).findFirst();
        if (existedNotification.isPresent()) {
            existedNotification.get().getSubscribers().add(identity);
        }
        trackerDao.updateTrackerNotification(trackerId, notification);
    }

    @Override
    public void unSubscribeCustomerEvent(Long trackerId, Long fieldId, BaseIdentity identity) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification notification = trackerEntity.getTrackerNotification();
        List<CustomerTrackerNotification> trackerNotifications =
                trackerEntity.getTrackerNotification().getCustomerTrackerNotifications();
        Optional<CustomerTrackerNotification> existedNotification =
                trackerNotifications.stream().filter(item -> {
                    return item.getTrackerField().getId().equals(fieldId);
                }).findFirst();
        if (existedNotification.isPresent()) {
            List<BaseIdentity> identityList = existedNotification.get().getSubscribers();
            BaseIdentity existed =
                    identityList.stream().filter(item -> {
                                    return item.getId().equals(identity.getId());
                                }).findFirst()
                                .get();
            identityList.remove(existed);
        }

        trackerDao.updateTrackerNotification(trackerId, notification);
    }

    public void subscribeAllCustomerEvent(Long trackerId, BaseIdentity identity) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification notification = trackerEntity.getTrackerNotification();
        notification.setUsedForAllCustomerFields(true);
        CustomerTrackerNotification defaultNotification = notification.getDefaultNotification();
        defaultNotification.getSubscribers().add(identity);

        trackerDao.updateTrackerNotification(trackerId, notification);
    }

    @Override
    public void unSubscribeAllCustomerEvent(Long trackerId, BaseIdentity identity) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification notification = trackerEntity.getTrackerNotification();
        notification.setUsedForAllCustomerFields(true);
        CustomerTrackerNotification defaultNotification = notification.getDefaultNotification();
        List<BaseIdentity> identityList = defaultNotification.getSubscribers();
        BaseIdentity existed =
                identityList.stream().filter(item -> {
                                return item.getId().equals(identity.getId());
                            }).findFirst()
                            .get();
        identityList.remove(existed);

        trackerDao.updateTrackerNotification(trackerId, notification);
    }

    @Override
    public void updateSystemEventNoticeType(Long trackerId, String eventName,
                                            SystemTrackerNotification systemTrackerNotification) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification notification = trackerEntity.getTrackerNotification();
        List<SystemTrackerNotification> trackerNotifications =
                trackerEntity.getTrackerNotification().getSystemTrackerNotifications();
        if (!eventName.isEmpty()) {
            Optional<SystemTrackerNotification> existed =
                    trackerNotifications.stream().filter(item -> {
                        return item.getEventName().equals(eventName);
                    }).findFirst();
            if (existed.isPresent()) {
                existed.get().setUseEmail(systemTrackerNotification.getUseEmail());
                existed.get().setUseMessage(systemTrackerNotification.getUseMessage());
            } else {
                trackerNotifications.add(SystemTrackerNotification.builder().id(IDUtils.getId())
                                                                  .eventName(eventName)
                                                                  .useEmail(systemTrackerNotification.getUseEmail())
                                                                  .useMessage(systemTrackerNotification.getUseMessage())
                                                                  .build());
            }
        } else {//全选
            List<String> existedEventNames = trackerNotifications.stream()
                                                                 .map(SystemTrackerNotification::getEventName)
                                                                 .collect(Collectors.toList());
            List<String> eventNames = NotificationEvents.ALL_EVENTS.stream()
                                                                   .filter(item -> !existedEventNames.contains(item))
                                                                   .collect(Collectors.toList());
            trackerNotifications.forEach(item -> {
                if (systemTrackerNotification.getUseEmail() != null) {
                    item.setUseEmail(systemTrackerNotification.getUseEmail());
                }
                if (systemTrackerNotification.getUseMessage() != null) {
                    item.setUseMessage(systemTrackerNotification.getUseMessage());
                }
            });
            eventNames.forEach(item -> {
                trackerNotifications.add(SystemTrackerNotification.builder().id(IDUtils.getId())
                                                                  .eventName(item)
                                                                  .useEmail(systemTrackerNotification.getUseEmail())
                                                                  .useMessage(systemTrackerNotification.getUseMessage())
                                                                  .build());
            });
        }
        trackerDao.updateTrackerNotification(trackerId, notification);
    }

    @Override
    public void updateCustomerEventNoticeType(Long trackerId, Long fieldId,
                                              CustomerTrackerNotification customerNotice) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification notification = trackerEntity.getTrackerNotification();
        notification.setUsedForAllCustomerFields(false);
        List<CustomerTrackerNotification> trackerNotifications =
                trackerEntity.getTrackerNotification().getCustomerTrackerNotifications();
        if (fieldId != null) {
            Optional<CustomerTrackerNotification> existedNotification =
                    trackerNotifications.stream().filter(item -> {
                        return item.getTrackerField().getId().equals(fieldId);
                    }).findFirst();
            if (existedNotification.isPresent()) {
                existedNotification.get().setUseEmail(customerNotice.getUseEmail());
                existedNotification.get().setUseMessage(customerNotice.getUseMessage());
            }
        } else { //全选
            trackerNotifications.forEach(item -> {
                if (customerNotice.getUseEmail() != null) {
                    item.setUseEmail(customerNotice.getUseEmail());
                }
                if (customerNotice.getUseMessage() != null) {
                    item.setUseMessage(customerNotice.getUseMessage());
                }
            });
        }

        trackerDao.updateTrackerNotification(trackerId, notification);
    }

    @Override
    public void updateAllCustomerEventNoticeType(Long trackerId, CustomerTrackerNotification customerNotice) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification notification = trackerEntity.getTrackerNotification();
        notification.setUsedForAllCustomerFields(true);
        if (customerNotice.getUseMessage() != null && customerNotice.getUseEmail() != null) {
            CustomerTrackerNotification defaultNotification = notification.getDefaultNotification();
            defaultNotification.setUseEmail(customerNotice.getUseEmail());
            defaultNotification.setUseMessage(customerNotice.getUseMessage());
        }
        trackerDao.updateTrackerNotification(trackerId, notification);
    }

    @Override
    public void createCustomerNotification(Long trackerId, CustomerTrackerNotification notification) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification trackerNotification = trackerEntity.getTrackerNotification();
        trackerNotification.setUsedForAllCustomerFields(false);
        List<CustomerTrackerNotification> trackerNotifications =
                trackerEntity.getTrackerNotification().getCustomerTrackerNotifications();
        notification.setId(IDUtils.getId());
        trackerNotifications.add(notification);
        trackerDao.updateTrackerNotification(trackerId, trackerNotification);
    }

    @Override
    public void deleteCustomerNotification(Long trackerId, CustomerTrackerNotification notification) {
        TrackerEntity trackerEntity = trackerDao.findOneTracker(trackerId);
        TrackerNotification trackerNotification = trackerEntity.getTrackerNotification();
        trackerNotification.setUsedForAllCustomerFields(false);
        List<CustomerTrackerNotification> trackerNotifications =
                trackerEntity.getTrackerNotification().getCustomerTrackerNotifications();
        CustomerTrackerNotification existed = ObjectUtils.findById(trackerEntity,
                                                                   notification.getId(),
                                                                   CustomerTrackerNotification.class);

        trackerNotifications.remove(existed);
        trackerDao.updateTrackerNotification(trackerId, trackerNotification);
    }

}
