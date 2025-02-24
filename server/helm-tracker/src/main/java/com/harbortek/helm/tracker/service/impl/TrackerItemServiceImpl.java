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

import cn.hutool.core.comparator.CompareUtil;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.entity.IdName;
import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.dao.PermissionDao;
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.service.FileService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.service.UserService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.*;
import com.harbortek.helm.tracker.dao.*;
import com.harbortek.helm.tracker.entity.link.TrackerLinkEntity;
import com.harbortek.helm.tracker.entity.link.TrackerLinkTypeEntity;
import com.harbortek.helm.tracker.entity.log.*;
import com.harbortek.helm.tracker.entity.plan.SprintEntity;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.service.*;
import com.harbortek.helm.tracker.util.ExecuteContext;
import com.harbortek.helm.tracker.vo.chart.ProjectCardInfo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.link.TrackerLinkVo;
import com.harbortek.helm.tracker.vo.log.*;
import com.harbortek.helm.tracker.vo.plan.SprintVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.IntegerField;
import com.harbortek.helm.tracker.vo.tracker.fields.TestStepField;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.permissions.FieldPermission;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransition;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransitionAction;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransitionValidator;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.actions.ActionManager;
import com.harbortek.helm.tracker.vo.view.FilterCondition;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import com.harbortek.helm.util.*;
import com.harbortek.helm.util.excel.ExcelLog;
import com.harbortek.helm.util.excel.ExcelLogs;
import com.harbortek.helm.util.excel.ExcelUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service("trackerItemService")
@Slf4j
public class TrackerItemServiceImpl implements TrackerItemService {
    private final Logger logger = LoggerFactory.getLogger(TrackerItemServiceImpl.class);

    @Autowired
    ProjectDao projectDao;
    @Autowired
    TrackerItemDao trackerItemDao;
    @Autowired
    TrackerDao trackerDao;
    @Autowired
    ProjectRoleMemberService projectRoleMemberService;

    @Autowired
    ActionExecuteService actionExecuteService;

    @Autowired
    ChangeLogDao changeLogDao;

    @Autowired
    AttachmentDao attachmentDao;

    @Autowired
    CommentDao commentDao;
    @Autowired
    WorkHoursDao workHoursDao;

    @Autowired
    SprintDao sprintDao;

    @Autowired
    TrackerLinkDao trackerLinkDao;

    @Autowired
    TrackerLinkTypeDao trackerLinkTypeDao;


    @Autowired
    EnumService enumService;

    @Autowired
    RoleService roleService;

    @Autowired
    TrackerNotificationService trackerNotificationService;
    @Autowired
    UserService userService;
    @Autowired
    HyperlinkDao hyperlinkDao;
    @Autowired
    private FileService fileService;

    @Autowired
    private TrackerPermissionService trackerPermissionService;
    @Autowired
    private DocDao docDao;
    @Autowired
    private PermissionDao permissionDao;

    @Override
    public Page<TrackerItemVo> findTrackerItems(Long projectId, Long trackerId, Long sprintId, ObjectFilter filter,
                                                String keyword, Boolean isTest,Pageable pageable) {
        //过滤测试用例工作项
        List<Long> testTrackerIds = new ArrayList<>();
        if(Boolean.TRUE.equals(isTest)){
            List<TrackerEntity> trackerList = trackerDao.findByProject(projectId);
                testTrackerIds=trackerList.stream().filter(t->
                        t.getTrackerFields().stream().anyMatch(trackerField -> trackerField instanceof TestStepField)
                ).map(TrackerEntity::getId).collect(Collectors.toList());
        }
        Page<TrackerItemEntity> entities =
                trackerItemDao.findTrackerItems(projectId, trackerId, sprintId, filter, keyword,testTrackerIds,pageable);
        if(entities.isEmpty()){
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<TrackerItemVo> vos = DataUtils.toVo(entities.getContent(), TrackerItemVo.class);

        List<Long> trackerIds = entities.getContent().stream().map(TrackerItemEntity::getTrackerId).collect(Collectors.toList());
        List<TrackerVo> trackerVos = DataUtils.toVo(trackerDao.findByIds(trackerIds, TrackerEntity.class,true),
                                                    TrackerVo.class);
        HashMap<Long, EnumItemVo> trackerMap = new HashMap<>();
        for (TrackerVo trackerVo : trackerVos) {
            trackerMap.put(trackerVo.getId(), trackerVo.getTrackerType());
        }

        for (TrackerItemVo itemVo : vos) {
            if (ObjectUtils.isNotEmpty(itemVo.getTracker())) {
                EnumItemVo enumItemVo = trackerMap.get(itemVo.getTracker().getId());
                itemVo.getTracker().setIcon(enumItemVo.getIcon());
            }
        }
        return new PageImpl<>(vos, pageable, entities.getTotalElements());
    }

//    private Pair<List<Long>,List<TrackerSpecialRole>> getItemPermission(Long projectId, Long trackerId,Boolean isTest){
//        List<Long> trackerIdsPerm=new ArrayList<>();
//        if(ObjectUtils.isEmpty(trackerId)){
//            List<TrackerEntity> trackerList = trackerDao.findByProject(projectId);
//            if(isTest!=null && isTest){//测试用例
//                trackerIdsPerm=trackerList.stream().filter(t->
//                        t.getTrackerFields().stream().anyMatch(trackerField -> trackerField instanceof TestStepField)
//                ).map(TrackerEntity::getId).collect(Collectors.toList());
//            }else{
//                trackerIdsPerm = trackerList.stream().map(TrackerEntity::getId).collect(Collectors.toList());
//            }
//        }else{
//            trackerIdsPerm.add(trackerId);
//        }
//        Long userId = SecurityUtils.getCurrentUser().getId();
//        List<TrackerSpecialRole> trackerSpecialRoles = trackerPermissionService.findTrackerSpecialRoles(List.of(TrackerPermissions.ITEM_VIEW), trackerIdsPerm);
//        trackerIdsPerm = trackerPermissionService.findTrackerIdsByPermission(userId, TrackerPermissions.ITEM_VIEW, projectId, trackerIdsPerm);
//        return Pair.of(trackerIdsPerm,trackerSpecialRoles);
//    }

    @Override
    public Page<TrackerItemVo> findTrackerItemsTree(Long projectId, Long trackerId, Long sprintId, ObjectFilter filter,
                                                String keyword,Boolean LinkDire,Long linkType,Integer linkLevel,Pageable pageable) {
        Page<TrackerItemEntity> entities =
                trackerItemDao.findTrackerItems(projectId, trackerId, sprintId, filter, keyword, null, pageable);
        List<TrackerLinkEntity> links = new ArrayList<>();
        List<Long> itemAllIds = new ArrayList<>();
        List<Long> itemIds = entities.map(BaseEntity::getId).stream().collect(Collectors.toList());
        List<TrackerLinkEntity> trackerLinks;
        for (int i = 0; i < linkLevel; i++) {
            if(Boolean.TRUE.equals(LinkDire)){
                trackerLinks = trackerLinkDao.findBySourceItemIds(itemIds, linkType);
                itemIds = trackerLinks.stream().map(TrackerLinkEntity::getTargetItemId).collect(Collectors.toList());
            }else{
                trackerLinks = trackerLinkDao.findByTargetIds(itemIds, linkType);
                itemIds = trackerLinks.stream().map(TrackerLinkEntity::getSourceItemId).collect(Collectors.toList());
            }
            links.addAll(trackerLinks);
            itemAllIds.addAll(itemIds);
        }
        itemAllIds=itemAllIds.stream().distinct().collect(Collectors.toList());
        List<TrackerItemEntity> itemTreeAll = trackerItemDao.findByIds(itemAllIds);
        Map<Long, Long> itemIdMap;
        if(Boolean.TRUE.equals(LinkDire)){
            itemIdMap = links.stream().collect(Collectors.toMap(TrackerLinkEntity::getTargetItemId, TrackerLinkEntity::getSourceItemId,(value1, value2) -> value1));
        }else{
            itemIdMap = links.stream().collect(Collectors.toMap(TrackerLinkEntity::getSourceItemId, TrackerLinkEntity::getTargetItemId,(value1, value2) -> value1));
        }
        itemTreeAll.forEach(item -> item.setParentId(itemIdMap.get(item.getId())));
        itemTreeAll.addAll(entities.getContent());

        List<TrackerItemVo> vos = DataUtils.toVo(itemTreeAll, TrackerItemVo.class);
        List<Long> trackerIds = itemTreeAll.stream().map(TrackerItemEntity::getTrackerId).collect(Collectors.toList());
        List<TrackerVo> trackerVos = DataUtils.toVo(trackerDao.findByIds(trackerIds, TrackerEntity.class,true),
                                                    TrackerVo.class);
        HashMap<Long, EnumItemVo> trackerMap = new HashMap<>();
        for (TrackerVo trackerVo : trackerVos) {
            trackerMap.put(trackerVo.getId(), trackerVo.getTrackerType());
        }
        for (TrackerItemVo itemVo : vos) {
            if (ObjectUtils.isNotEmpty(itemVo.getTracker())) {
                EnumItemVo enumItemVo = trackerMap.get(itemVo.getTracker().getId());
                itemVo.getTracker().setIcon(enumItemVo.getIcon());
            }
        }
        return new PageImpl(vos, pageable, entities.getTotalElements());
    }

    @Override
    public TrackerItemVo createTrackerItem(TrackerItemVo trackerItemVo) {
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_CREATE),trackerItemVo.getTracker().getId(),
                trackerItemVo.getId(),null,"没有创建工作项权限");

        trackerItemVo.setId(IDUtils.getId());
        TrackerItemEntity item = DataUtils.toEntity(trackerItemVo, TrackerItemEntity.class);
        item.setProjectId(trackerItemVo.getProject().getId());
        item.setTrackerId(trackerItemVo.getTracker().getId());
//        if (trackerItemVo.getSprint() != null) {
//            item.setSprintId(trackerItemVo.getSprint().getId());
//        }
        if (trackerItemVo.getOwner() != null) {
            item.setOwnerId(trackerItemVo.getOwner().getId());
        }
        if (trackerItemVo.getPriority() != null && trackerItemVo.getPriority().getId() != null) {
            item.setPriorityId(trackerItemVo.getPriority().getId());
        }
//        item.setRelatedWorkItems(trackerItemVo.getRelatedWorkItems());
        if (trackerItemVo.getAssignedTo() != null) {
            item.setAssignedToId(trackerItemVo.getAssignedTo().getId());
        }
        //关联工作项
        if (ObjectUtils.isNotEmpty(trackerItemVo.getRelatedWorkItems())) {
            List<TrackerLinkVo> relatedWorkItems = trackerItemVo.getRelatedWorkItems();
            Collection<TrackerLinkEntity> trackerLinkEntities = DataUtils.toEntity(relatedWorkItems, TrackerLinkEntity.class);
            trackerLinkEntities.forEach(v -> {
                Long id = IDUtils.getId();
                v.setId(id);
                v.setSourceItemId(item.getId());
            });
            trackerLinkDao.batchCreateTrackerLinks(trackerLinkEntities);
        }
        //关联文件
        if (ObjectUtils.isNotEmpty(trackerItemVo.getAttachments())) {
            Collection<AttachmentEntity> attachments = DataUtils.toEntity(trackerItemVo.getAttachments(), AttachmentEntity.class);
            attachments.forEach(v -> {
                v.setId(IDUtils.getId());
                v.setObjectId(item.getId());
            });
            attachmentDao.batchCreateAttachment(attachments);
        }
        //关联链接
        if (ObjectUtils.isNotEmpty(trackerItemVo.getHyperlinks())) {
            List<HyperlinkEntity> hyperlinks = trackerItemVo.getHyperlinks();
            hyperlinks.forEach(v -> {
                v.setId(IDUtils.getId());
                v.setObjectId(item.getId());
            });
            hyperlinkDao.batchCreateHyperlink(hyperlinks);
        }

        TrackerEntity tracker = trackerDao.findOneTracker(trackerItemVo.getTracker().getId());

        if (tracker == null) {
            TrackerVo trackerVo = InternalTrackers.getTrackerVo(item.getTrackerId());
            tracker = new TrackerEntity();

            DataUtils.copyProperties(trackerVo, tracker, new ArrayList<>());
        }
        tracker.setProjectId(item.getProjectId());
        List<TrackerStatus> trackerStatuses = tracker.getTrackerStatuses();
        trackerStatuses = trackerStatuses == null ? new ArrayList<>() : trackerStatuses;
        //设置初始状态
        Optional<TrackerStatus> startStatus = trackerStatuses.stream().filter(TrackerStatus::getInitial).findFirst();
        if (startStatus.isPresent()) {
            item.setStatusId(startStatus.get().getId());
            item.setMeaningId(startStatus.get().getMeaning().getId());
        } else if (!Arrays.asList(InternalTrackers.HEADING.getId(), InternalTrackers.PARAGRAPH.getId(), InternalTrackers.TITLE.getId()).contains(tracker.getId())) {
            ServiceException.throwException("创建失败，请配置工作项初始状态");
        }

        if (StringUtils.isEmpty(item.getItemNo())) {
            item.setItemNo(projectDao.getNextItemNo(trackerItemVo.getProject().getId()));
        }
        trackerItemDefaultValue(item,tracker);

        item.setRemainingWorkingHours(item.getEstimateWorkingHours());

        TrackerItemEntity newItem = trackerItemDao.createTrackerItemEntity(item);

        //记录日志
        changeLogDao.createChangeLog(item.getId(), ChangeLogMessages.TRACKER_ITEM_LIFE_CYCLE, "创建了工作项",
                item, null, null);

        //发送通知
        trackerNotificationService.sendSystemNotification(NotificationEvents.CREATE_ITEM, tracker, item, SecurityUtils.getCurrentUser());

        return DataUtils.toVo(newItem,TrackerItemVo.class);
    }
    private void trackerItemDefaultValue(TrackerItemEntity item,TrackerEntity tracker){
        List<String> allProperties = DataUtils.getAllProperties(TrackerItemEntity.class);
        tracker.getTrackerFields().forEach(field -> {
            if(ObjectUtils.isNotEmpty(field.getDefaultValue())){
                if(field.getSystem()){
                    String systemProperty = field.getSystemProperty();
                    if(!allProperties.contains(systemProperty)){
                        systemProperty+="Id";
                    }
                    if(ObjectUtils.isEmpty(DataUtils.getProperty(item, systemProperty))){
                        if(DataUtils.getPropertyType(TrackerItemEntity.class, systemProperty).equals(Long.class)){
                            DataUtils.setProperty(item, systemProperty, Long.valueOf(field.getDefaultValue().toString()));
                        }else if(DataUtils.getPropertyType(TrackerItemEntity.class, systemProperty).equals(Date.class)){
                            DataUtils.setProperty(item, systemProperty, DateUtils.toDate(field.getDefaultValue().toString()));
                        }else{
                            DataUtils.setProperty(item, systemProperty, field.getDefaultValue());
                        }
                    }
                }else{
                    item.getValues().put(field.getId(), field.getDefaultValue().toString());
                }
            }

        });
    }

    @Override
    public void updateTrackerItem(TrackerItemVo trackerItemVo) {
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT),trackerItemVo.getTracker().getId(),
                trackerItemVo.getId(),null,"没有修改工作项权限");

        TrackerItemEntity entity = DataUtils.toEntity(trackerItemVo, TrackerItemEntity.class);
        trackerItemDao.updateTrackerItemEntity(entity);
//
//        if(!entity.getRelatedWikis().isEmpty()){
//            docDao.incVersion(entity.getRelatedWikis());
//        }
    }

    @Override
    public TrackerItemVo findOneTrackerItem(Long itemId) {
        TrackerItemEntity entity = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_VIEW),entity.getTrackerId(),entity.getId(),
                null,"没有查看工作项权限");

        if (ObjectUtils.isEmpty(entity))
            return null;
        TrackerItemVo trackerItemVo = DataUtils.toVo(entity, TrackerItemVo.class);
        TrackerEntity tracker = null;
        if (ObjectUtils.isNotEmpty(entity.getTrackerId())) {
            tracker = trackerDao.findOneTracker(entity.getTrackerId());
        }
//        Map<Long, TrackerStatus> statusMap = new HashMap<>();
//        tracker.getTrackerStatuses().forEach(s -> {
//            statusMap.put(s.getId(), s);
//        });
//        trackerItemVo.setStatus(statusMap.get(entity.getStatusId()));
//        if(ObjectUtils.isNotEmpty(statusMap.get(entity.getStatusId()))){
//            trackerItemVo.setMeaning(statusMap.get(entity.getStatusId()).getMeaning());
//        }
        if (tracker == null) {
            InternalTrackers.fillInternalTracker(trackerItemVo, entity);
        } else {
            trackerItemVo.setTracker(new IdNameReference<>(DataUtils.toVo(tracker, TrackerVo.class)));
        }
        trackerItemVo.setRelatedWorkItems(DataUtils.toVo(trackerLinkDao.findByItemId(itemId), TrackerLinkVo.class));
        return trackerItemVo;
    }

    @Override
    public List<TrackerItemVo> findTrackerItemByIds(List<Long> itemIds) {
        itemIds= itemIds.stream().filter(ObjectUtils::isNotEmpty).toList();
        if(ObjectUtils.isEmpty(itemIds)){
            return new ArrayList<>();
        }
        List<TrackerItemEntity> trackerItemEntities = trackerItemDao.findByIds(itemIds);
        //权限判断
        List<Long> trackerIds = trackerItemEntities.stream().map(item -> item.getTrackerId()).collect(Collectors.toList());
        List<TrackerVo> trackerVos = DataUtils.toVo(trackerDao.findByIds(trackerIds, TrackerEntity.class,true),
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
    public List<TrackerItemVo> findByTrackerIds(Long projectId, List<Long> trackerIds) {
        //权限判断

        List<TrackerItemEntity> trackerItems = trackerItemDao.findItemByTrackerIds(projectId,trackerIds);
        List<Long> internalTrackerIds = Arrays.asList(InternalTrackers.HEADING.getId(), InternalTrackers.PARAGRAPH.getId(),
                InternalTrackers.TITLE.getId());
        //文档 中标题工作项
        HashMap<Long,Long> internalTrackerMap = new HashMap<>();
        trackerItems.forEach(item->{
            if(internalTrackerIds.contains(item.getTrackerId())){
                internalTrackerMap.put(item.getId(),item.getTrackerId());
            }
        });
        List<TrackerItemVo> trackerItemVos = DataUtils.toVo(trackerItems, TrackerItemVo.class);
        trackerItemVos.forEach(item->{
            if(ObjectUtils.isEmpty(item.getTracker())&&internalTrackerMap.containsKey(item.getId())){
                item.setTracker(new IdNameReference<>(TrackerVo.builder().id(internalTrackerMap.get(item.getId())).build()));
            }
        });
        return trackerItemVos;
    }

    public void deleteOneTrackerItem(Long itemId) {
        TrackerItemEntity itemEntity = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_DELETE),itemEntity.getTrackerId(),
                itemEntity.getId(),null,"没有删除工作项权限");
        trackerLinkDao.deleteByItemId(itemId);
        trackerItemDao.deleteTrackerItemEntity(itemId);
    }

    @Override
    public void batchDeleteTrackerItem(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return;
        }
        itemIds=itemIds.stream().filter(ObjectUtils::isNotEmpty).toList();
        List<TrackerItemEntity> itemList = trackerItemDao.findByIds(itemIds);
        itemList.forEach(item->{
            checkTrackerPermission(List.of(TrackerPermissions.ITEM_DELETE),item.getTrackerId(),
                    item.getId(),null,"没有删除工作项权限");
        });
        trackerItemDao.batchDeleteTrackerItem(itemIds);
        trackerLinkDao.deleteByItemIds(itemIds);

    }

    @Override
    public TrackerStatus stateChanged(Long itemId, Long stateTransitionId) {
        TrackerItemEntity item = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT,TrackerPermissions.ITEM_CHANGE_STATUS),item.getTrackerId(),
                item.getId(),null,"没有更新工作项状态权限");
        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
        TrackerStateTransition stateTransition = ObjectUtils.findById(tracker, stateTransitionId,
                TrackerStateTransition.class);
        if (stateTransition == null) {
            ServiceException.throwException("没有找到指定的步骤");
        }
        //检查是否符合数据完整性等校验要求
        List<TrackerStateTransitionValidator> validators = stateTransition.getValidators();
        for (TrackerStateTransitionValidator validator : validators) {
            boolean valid = validator.validate(item, tracker);
            if (!valid) {
                ServiceException.throwException("无法满足执行条件 " + validator.getName());
            }
        }

        //检查是否具有状态变化权限
        List<BaseIdentity> permitted = stateTransition.getPermitted();
        if(ObjectUtils.isNotEmpty(permitted)){
            Long userId = SecurityUtils.getCurrentUser().getId();
            boolean exists = checkPermissionGranted(permitted, tracker, item, userId);
            if (!exists) {
                ServiceException.throwException("没有该步骤的执行权限");
            }
        }

        //改变状态
        IdNameReference<TrackerStatus> to = stateTransition.getTransitionTo();
        item.setStatusId(to.getId());
        Optional<TrackerStatus> startStatus = tracker.getTrackerStatuses().stream()
                .filter(s -> s.getId().equals(to.getId())).findFirst();
        item.setMeaningId(startStatus.get().getMeaning().getId());
        trackerItemDao.updateStatus(item);
        if(!item.getRelatedWikis().isEmpty()){
            docDao.incVersion(item.getRelatedWikis());
        }

        //执行后置动作
        List<TrackerStateTransitionAction> actions = stateTransition.getActions();
        ExecuteContext context = new ExecuteContext();
        context.setTrackerItem(item);
        context.setTracker(tracker);
        context.setActionExecuteService(actionExecuteService);

        ActionManager.executeActions(actions, context);

        //记录日志
        changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_STATUS_CHANGE, "更新了工作项状态",
                item, stateTransition.getTransitionFrom(),
                stateTransition.getTransitionTo());
        //发送通知
        trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_STATUS, tracker, item, SecurityUtils.getCurrentUser());

        //返回改变后的status
        TrackerStatus trackerStatus = ObjectUtils.findById(tracker, to.getId(), TrackerStatus.class);
        return trackerStatus;
    }


    public void systemFieldChanged(Long itemId, String systemProperty, Object newValue) {
        TrackerItemEntity item = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(null,item.getTrackerId(),
                item.getId(),systemProperty,null);
        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
        if (tracker == null) {
            TrackerVo trackerVo = InternalTrackers.getTrackerVo(item.getTrackerId());
            tracker = new TrackerEntity();
            DataUtils.copyProperties(trackerVo, tracker, new ArrayList<>());
        }
        List<TrackerField> trackerFields = tracker.getTrackerFields();
        Optional<TrackerField> trackerField =
                trackerFields.stream()
                        .filter(f -> {
                            return f.getSystem() && f.getSystemProperty().equals(systemProperty);
                        })
                        .findFirst();

        if (trackerField.isPresent()) {
            //检查是否具有权限变化权限
            Long userId = SecurityUtils.getCurrentUser().getId();
//            boolean hasPermission = checkFieldPermission(trackerField.getPermission(), tracker, item, userId);
//            if (!hasPermission) {
//                throw new ServiceException("");
//            }
            Object oldValue = null;
            if (SystemFields.NAME.equals(systemProperty)) {
                oldValue = item.getName();
                item.setName(String.valueOf(newValue));
                trackerItemDao.updateSystemField(item, BaseEntity.Fields.name, item.getName());

                //发送通知
                trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_TITLE, tracker, item, SecurityUtils.getCurrentUser());
            } else if (SystemFields.DESCRIPTION.equals(systemProperty)) {
                oldValue = item.getDescription();
                item.setDescription(String.valueOf(newValue));
                trackerItemDao.updateSystemField(item, BaseEntity.Fields.description, item.getDescription());

                //发送通知
                trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_DESCRIPTION, tracker, item, SecurityUtils.getCurrentUser());
            } else if (SystemFields.PRIORITY.equals(systemProperty)) {
                oldValue = item.getPriorityId();
                item.setPriorityId(Long.valueOf(String.valueOf(newValue)));
                trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.priorityId, item.getPriorityId());

                if (ObjectUtils.isNotEmpty(oldValue)) {
                    oldValue = enumService.findOneEnumItemById((Long) oldValue).getName();
                }
                newValue = enumService.findOneEnumItemById(item.getPriorityId()).getName();

                //发送通知
                trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_PRIORITY, tracker, item, SecurityUtils.getCurrentUser());

            } else if (SystemFields.SEVERITY.equals(systemProperty)) {
                oldValue = item.getSeverityId();
                item.setSeverityId(Long.valueOf(String.valueOf(newValue)));
                trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.severityId, item.getSeverityId());

                if (ObjectUtils.isNotEmpty(oldValue)) {
                    oldValue = enumService.findOneEnumItemById((Long) oldValue).getName();
                }
                newValue = enumService.findOneEnumItemById(item.getPriorityId()).getName();

                //发送通知
                trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_PRIORITY, tracker, item, SecurityUtils.getCurrentUser());

            }else if (SystemFields.OWNER.equals(systemProperty)) {

                oldValue = item.getOwnerId();
                item.setOwnerId(Long.valueOf(String.valueOf(newValue)));
                trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.ownerId, item.getOwnerId());

                if (ObjectUtils.isNotEmpty(oldValue)) {
                    oldValue = userService.findOneUser(Long.valueOf(String.valueOf(oldValue))).getName();
                }
                newValue = userService.findOneUser(item.getOwnerId()).getName();

                //发送通知
                trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_OWNER, tracker, item, SecurityUtils.getCurrentUser());

            } else if (SystemFields.STATUS.equals(systemProperty)) {
                oldValue = ObjectUtils.findById(tracker, item.getStatusId(), TrackerStatus.class).getName();
                Long oldStatusId = item.getStatusId();
                Long newStatusId = Long.valueOf(String.valueOf(newValue));
                newValue =
                        ObjectUtils.findById(tracker, newStatusId, TrackerStatus.class).getName();

                Optional<TrackerStateTransition> stateTransition =
                        tracker.getTrackerStateTransitions().stream().filter(s -> {
                            return s.getTransitionFrom().getId().equals(oldStatusId) &&
                                    s.getTransitionTo().getId().equals(newStatusId);
                        }).findFirst();

                if (stateTransition.isPresent()) {
                    stateChanged(itemId, stateTransition.get().getId());
                } else {
                    throw new ServiceException("未找到对应的状态迁移步骤");
                }
            } else if (SystemFields.ASSIGNED_TO.equals(systemProperty)) {
                oldValue = item.getAssignedToId();
                item.setAssignedToId(Long.parseLong(newValue.toString()));
                if (ObjectUtils.isNotEmpty(oldValue)) {
                    oldValue = userService.findOneUser(Long.valueOf(String.valueOf(oldValue))).getName();
                }
                newValue = userService.findOneUser(item.getAssignedToId()).getName();
                trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.assignedToId, item.getAssignedToId());
            } else if (SystemFields.PLAN_END_DATE.equals(systemProperty)) {
                oldValue = item.getPlanEndDate();
                try {
                    item.setPlanEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(newValue.toString()));
                } catch (Exception e1) {
                    logger.error("Exception", e1);
                }
                trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.planEndDate, item.getPlanEndDate());

                //发送通知
                trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_PLAN_END_DATE,
                        tracker, item, SecurityUtils.getCurrentUser());
            } else if (SystemFields.PLAN_START_DATE.equals(systemProperty)) {
                oldValue = item.getPlanStartDate();
                try {
                    item.setPlanStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(newValue.toString()));
                } catch (Exception e1) {
                    logger.error("Exception", e1);
                }
                trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.planStartDate, item.getPlanStartDate());

                //发送通知
                trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_PLAN_START_DATE, tracker, item, SecurityUtils.getCurrentUser());
            } else if (SystemFields.SPRINT.equals(systemProperty)) {
                oldValue = item.getSprintId();
                trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.sprintId, Long.parseLong(newValue.toString()));
                if (ObjectUtils.isNotEmpty(oldValue)) {
                    SprintEntity sprintEntity = sprintDao.findById((Long) oldValue);
                    if (ObjectUtils.isNotEmpty(sprintEntity)) {
                        oldValue = sprintEntity.getName();
                    }
                }
                newValue = sprintDao.findById(Long.parseLong(newValue.toString())).getName();

//            } else if (SystemFields.TEST_CASE_TYPE.equals(systemProperty)) {
//                oldValue = item.getTestCaseTypeId();
//                trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.testCaseTypeId, Long.parseLong(newValue.toString()));
//                if (ObjectUtils.isNotEmpty(oldValue)) {
//                    oldValue = enumService.findOneEnumItemById((Long) oldValue).getName();
//                }
//                newValue = enumService.findOneEnumItemById(Long.parseLong(newValue.toString())).getName();
//            } else if (SystemFields.PRECONDITION.equals(systemProperty)) {
//                oldValue = item.getPrecondition();
//                if (ObjectUtils.isEmpty(newValue)) {
//                    newValue = "";
//                }
//                if (newValue.equals(oldValue)) {
//                    return;
//                }
//                trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.precondition, newValue);
            } else {
                if (systemProperty.endsWith("Date")) {
                    try {
                        trackerItemDao.updateSystemField(item, systemProperty,
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                                        newValue.toString()));
                    } catch (Exception e1) {
                        logger.error("Exception", e1);
                    }

                } else {
                    trackerItemDao.updateSystemField(item, systemProperty, newValue);

                    if (SystemFields.PROGRESS.equals(systemProperty)) {
                        oldValue = item.getProgress();
                        //发送通知
                        trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_PROGRESS, tracker, item, SecurityUtils.getCurrentUser());
                    } else if (SystemFields.ESTIMATE_WORKING_HOURS.equals(systemProperty)) {
                        oldValue = item.getEstimateWorkingHours();
                        //发送通知
                        trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_ESTIMATE_WORKING_HOURS, tracker, item, SecurityUtils.getCurrentUser());
                    } else if (SystemFields.REGISTERED_WORKING_HOURS.equals(systemProperty)) {
                        oldValue = item.getRegisteredWorkingHours();
                        //发送通知
                        trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_REGISTERED_WORKING_HOURS, tracker, item, SecurityUtils.getCurrentUser());
                    } else if (SystemFields.REMAINING_WORKING_HOURS.equals(systemProperty)) {
                        oldValue = item.getRemainingWorkingHours();
                        //发送通知
                        trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_REMAINING_WORKING_HOURS, tracker, item, SecurityUtils.getCurrentUser());
                    }

                }

            }

            //更新关联文档版本
            if(!item.getRelatedWikis().isEmpty()){
                docDao.incVersion(item.getRelatedWikis());
            }

            //记录日志
            changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_FIELD_CHANGE, "编辑了工作项属性",
                    trackerField.get(), oldValue, newValue);


        } else {
            ServiceException.throwException("更新失败!");
        }
    }

    public void batchCreateTrackerLinks(Long itemId, List<TrackerLinkVo> trackerLinks) {
        TrackerItemEntity item = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT,TrackerPermissions.ITEM_EDIT_ASSOCIATIONS),
                item.getTrackerId(),
                item.getId(),"","没有编辑关联工作项权限");
        Collection<TrackerLinkEntity> entities = DataUtils.toEntity(trackerLinks, TrackerLinkEntity.class);
        entities.forEach(link -> {
            link.setId(IDUtils.getId());
        });
        trackerLinkDao.batchCreateTrackerLinks(entities);

        trackerLinks.forEach(trackerLink -> {
            changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_ASSOCIATION, "增加了关联工作项",
                    item, "", trackerLink.getTargetItem().getItemNo());
        });

    }

    public void deleteTrackerLink(Long itemId, TrackerLinkVo trackerLink) {
        TrackerItemEntity item = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT_ASSOCIATIONS),item.getTrackerId(),
                item.getId(),"","没有编辑关联工作项权限");
        trackerLinkDao.deleteTrackerLink(trackerLink.getId());
        TrackerLinkTypeEntity linkTypeEntity = trackerLinkTypeDao.findById(trackerLink.getLinkType().getId());

        changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_ASSOCIATION, "删除了关联工作项",
                item, "", trackerLink.getTargetItem().getItemNo());
    }

    public boolean updateTrackerLink(Long itemId, TrackerLinkVo trackerLink) {
        TrackerItemEntity item = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT_ASSOCIATIONS),item.getTrackerId(),
                item.getId(),"","没有编辑关联工作项权限");
        TrackerLinkEntity entity = DataUtils.toEntity(trackerLink, TrackerLinkEntity.class);
        entity.setCreateDate(new Date());
        trackerLinkDao.updateTrackerLink(entity);
        changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_ASSOCIATION, "修改了关联工作项",
                item, "", trackerLink.getTargetItem().getItemNo());
        return true;
    }


//    public void relatedWorkItems(Long itemId, List<ItemRelation> relatedWorkItems) {
//        TrackerItemEntity item = trackerItemDao.findOneById(itemId);
//
//        item.setRelatedWorkItems(relatedWorkItems);
//        relatedWorkItems.forEach(itemRelation -> {
//            if (itemRelation.getRelation().equals(Associations.HAS_PARENT)) {
//                trackerItemDao.updateSystemField(TrackerItemEntity.builder().id(itemRelation.getId()).build(),
//                        TrackerItemEntity.Fields.parentId, itemId);
//            } else {
//                trackerItemDao.updateSystemField(TrackerItemEntity.builder().id(itemRelation.getId()).build(),
//                        TrackerItemEntity.Fields.parentId, null);
//            }
//        });
//        trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.relatedWorkItems, item.getRelatedWorkItems());
//
//        changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_ASSOCIATION, "更新了关联工作项",
//                item, item.getRelatedWorkItems(), relatedWorkItems);
//    }

    @Override
    public List<HyperlinkEntity> findHyperlinkByObjectId(Long objectId) {
        return hyperlinkDao.findHyperlinkByObjectId(objectId);
    }

    @Override
    public HyperlinkEntity updateHyperlink(HyperlinkEntity hyperlink) {
        TrackerItemEntity item = trackerItemDao.findOneById(hyperlink.getObjectId());
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT),item.getTrackerId(),
                item.getId(),"","没有修改工作项权限");
        if (ObjectUtils.isNotEmpty(item)) {
            changeLogDao.createChangeLog(hyperlink.getObjectId(), ChangeLogMessages.TRACKER_ITEM_NOTIFICATION, "更新了关联链接",
                    item, "", "");
        }
        return hyperlinkDao.updateHyperlink(hyperlink);
    }

    @Override
    public void deleteHyperlink(Long hyperlinkId) {

        HyperlinkEntity hyperlinkEntity = hyperlinkDao.findById(hyperlinkId);
        TrackerItemEntity item = trackerItemDao.findOneById(hyperlinkEntity.getObjectId());
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT),item.getTrackerId(),
                item.getId(),"","没有修改工作项权限");
        hyperlinkDao.deleteHyperlink(hyperlinkId);

        if (ObjectUtils.isNotEmpty(item)) {
            changeLogDao.createChangeLog(hyperlinkEntity.getObjectId(), ChangeLogMessages.TRACKER_ITEM_NOTIFICATION, "删除了关联链接",
                    item, "", "");
        }
    }

    public void relatedWikis(Long itemId, List<Long> relatedWikiIds) {
        TrackerItemEntity item = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT),item.getTrackerId(),
                item.getId(),"","没有修改工作项权限");

        item.setRelatedWikis(relatedWikiIds);
        trackerItemDao.updateSystemField(item, TrackerItemEntity.Fields.relatedWikis, item.getRelatedWikis());
        if(!item.getRelatedWikis().isEmpty()){
            docDao.incVersion(item.getRelatedWikis());
        }

        changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_ASSOCIATION, "更新了关联Wiki文档",
                item, item.getRelatedWikis(), relatedWikiIds);
    }

    @Override
    public void exportTrackerItems(HttpServletRequest request, HttpServletResponse response, Long projectId, Long trackerId, Long sprintId, List<Long> selectedRowIds, ObjectFilter filter, PageRequest pageRequest) {
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EXPORT),trackerId,
                null,"","没有导出工作项权限");

        Page<TrackerItemEntity> trackerItems = trackerItemDao.findTrackerItems(projectId, trackerId, sprintId, filter,
                null, null, pageRequest);
        List<TrackerItemVo> trackerItemVos = DataUtils.toVo(trackerItems.getContent(), TrackerItemVo.class);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        TrackerEntity tracker = null;
        if (ObjectUtils.isNotEmpty(trackerId)) {
            tracker = trackerDao.findOneTracker(trackerId);
            List<TrackerField> selectedRows = tracker.getTrackerFields().stream().filter(field -> selectedRowIds.contains(field.getId())).collect(Collectors.toList());
            for (int i = 0; i < selectedRows.size(); i++) {
                int index = selectedRowIds.indexOf(selectedRows.get(i).getId());
                if (index != i) {
                    TrackerField t = selectedRows.get(i);
                    selectedRows.set(i, selectedRows.get(index));
                    selectedRows.set(index, t);
                }
            }
            selectedRows.forEach(item -> {
                if (ObjectUtils.isNotEmpty(item.getSystemProperty())) {
                    headers.put(item.getSystemProperty(), item.getName());
                } else {
                    headers.put(item.getId().toString(), item.getName());
                }
            });
        } else {
            tracker = TrackerEntity.builder().name("工作项").build();
            headers.put(SystemFields.ITEM_NO, "编号");
            headers.put(SystemFields.NAME, "标题");
            headers.put(SystemFields.DESCRIPTION, "描述");
            headers.put(SystemFields.OWNER, "负责人");
            headers.put(SystemFields.STATUS, "状态");
            headers.put(SystemFields.PROJECT, "所属项目");
            headers.put(SystemFields.TRACKER, "所属工作项类型");
            headers.put(SystemFields.SPRINT, "所属迭代");
            headers.put(SystemFields.PRIORITY, "优先级");
            headers.put(SystemFields.SEVERITY, "严重级别");
            headers.put(SystemFields.ESTIMATE_WORKING_HOURS, "预估工时");
        }

        List<HashMap> itemMapList = getTrackerItemMapList(trackerItemVos, headers);

        try {
            ExcelUtil.processResponseHeader(request, response, tracker.getName());
            ExcelUtil.exportExcel(headers, itemMapList, response.getOutputStream());

        } catch (Exception e) {
        }
    }

    @Override
    public void downloadImportDome(HttpServletRequest request, HttpServletResponse response, Long trackerId) {
        TrackerEntity tracker = trackerDao.findOneTracker(trackerId);
        String trackerStatusNames = String.join(",", tracker.getTrackerStatuses().stream().map(i -> i.getName()).collect(Collectors.toList()));
        List<EnumItemVo> trackerPriority = enumService.findEnumItemsByCode(tracker.getProjectId(), EnumCodes.TRACKER_PRIORITY);
        List<EnumItemVo> trackerSeverity = enumService.findEnumItemsByCode(tracker.getProjectId(), EnumCodes.TRACKER_SEVERITY);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(SystemFields.NAME, "标题");
        headers.put(SystemFields.DESCRIPTION, "描述");
        headers.put(SystemFields.OWNER, "负责人");
        headers.put(SystemFields.STATUS, "状态");
        headers.put(SystemFields.PROJECT, "所属项目");
        headers.put(SystemFields.TRACKER, "所属工作项类型");
        headers.put(SystemFields.SPRINT, "所属迭代");
        headers.put(SystemFields.PRIORITY, "优先级");
        headers.put(SystemFields.SEVERITY, "严重级别");
        headers.put(SystemFields.ESTIMATE_WORKING_HOURS, "预估工时");

        List<HashMap<String,String>> itemMapList = new ArrayList<>();
        HashMap<String, String> itemMap = new HashMap<>();
        itemMap.put(SystemFields.NAME, "必填项不能为空，否则整行不予导入");
        itemMap.put(SystemFields.DESCRIPTION, "可选项。");
        itemMap.put(SystemFields.OWNER, "可选项。");
        itemMap.put(SystemFields.STATUS, "必填项。可选值：" + trackerStatusNames);
        itemMap.put(SystemFields.PROJECT, "必填项。");
        itemMap.put(SystemFields.TRACKER, "必填项。");
        itemMap.put(SystemFields.SPRINT, "可选项。");
        itemMap.put(SystemFields.PRIORITY, "必填项。可选值：" +  StringUtils.join(trackerPriority.stream().map(EnumItemVo::getName).collect(
                Collectors.toList()), ","));
        itemMap.put(SystemFields.SEVERITY,
                    "可选项。可选值：" + StringUtils.join(trackerSeverity.stream().map(EnumItemVo::getName).collect(
                            Collectors.toList()), ","));
        itemMap.put(SystemFields.ESTIMATE_WORKING_HOURS, "可选项。");
        itemMapList.add(itemMap);

        try {
            ExcelUtil.processResponseHeader(request, response, tracker.getName());
            ExcelUtil.exportExcel(headers, itemMapList, response.getOutputStream());

        } catch (Exception e) {
        }
    }

    @Override
    public ExcelLogs importTrackerItems(Long trackerId, String uploadedFile) {
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT),trackerId,
                null,"","没有修改工作项权限");
        TrackerEntity tracker = trackerDao.findOneTracker(trackerId);
        InputStream inputStream = null;
        ExcelLogs logs = new ExcelLogs();
        try {
            inputStream = fileService.download(uploadedFile);
            List<Map> itemVoMap = (List<Map>) ExcelUtil.importExcel(Map.class, inputStream, "", logs);
            List<String> rowNames = new ArrayList<>();
            itemVoMap.get(0).keySet().forEach(itemSet -> {
                rowNames.add(itemSet.toString());
            });
            List<TrackerField> selectedRows = tracker.getTrackerFields().stream().filter(field -> rowNames.contains(field.getName())).collect(Collectors.toList());
            if (selectedRows.isEmpty()) {
                String rowName = String.join(",", rowNames);
                logs.getLogList().add(new ExcelLog(null, "列【" + rowName + "】无法匹配，请修改文件后重新导入。"));
                logs.setHasError(Boolean.TRUE);
                return logs;
            }
            ArrayList<TrackerItemEntity> entities = new ArrayList<>();

            for (Map itemVo : itemVoMap) {
                TrackerItemEntity itemEntity = new TrackerItemEntity();
                for (TrackerField selectedRow : selectedRows) {
                    Object value = itemVo.get(selectedRow.getName());
                    if (ObjectUtils.isNotEmpty(value)) {
                        this.getFieldValue(selectedRow, value.toString(), itemEntity, tracker);
                    }
                }
                if (ObjectUtils.isNotEmpty(itemEntity) &&
                        ObjectUtils.isNotEmpty(itemEntity.getName()) &&
                        ObjectUtils.isNotEmpty(itemEntity.getOwnerId()) &&
                        ObjectUtils.isNotEmpty(itemEntity.getProjectId()) &&
                        ObjectUtils.isNotEmpty(itemEntity.getTrackerId()) &&
                        ObjectUtils.isNotEmpty(itemEntity.getPriorityId())) {
                    itemEntity.setId(IDUtils.getId());
                    itemEntity.setItemNo(projectDao.getNextItemNo(tracker.getProjectId()));
                    entities.add(itemEntity);
                } else {
                    int index = itemVoMap.indexOf(itemVo) + 2;
                    logs.getLogList().add(new ExcelLog(null, "第 " + index + " 行，缺少必填项，请修改文件后重新导入。"));
                    logs.setHasError(Boolean.TRUE);
                    return logs;
                }
            }
            if (ObjectUtils.isNotEmpty(entities)) {
                trackerItemDao.createTrackerItems(entities);
            } else {
                logs.getLogList().add(new ExcelLog(null, "数据为空，请修改文件后重新导入。"));
                logs.setHasError(Boolean.TRUE);
            }
        } catch (Exception ex) {
            logger.info("----", ex);
            logs.setHasError(true);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return logs;
    }

    @Override
    public void watch(Long itemId, BaseIdentity watch) {
        TrackerItemEntity itemEntity = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_MANAGE_WATCHER), itemEntity.getTrackerId(),
                itemEntity.getId(),"","没有管理关注着权限");
        if (ObjectUtils.isNotEmpty(itemEntity.getWatchers())) {
            itemEntity.getWatchers().add(watch);
        } else {
            itemEntity.setWatchers(new ArrayList<>(Collections.singletonList(watch)));
        }
        trackerItemDao.updateSystemField(itemEntity, TrackerItemEntity.Fields.watchers, itemEntity.getWatchers());
        changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_NOTIFICATION, "关注了工作项",
                itemEntity, "", "");
        if(!itemEntity.getRelatedWikis().isEmpty()){
            docDao.incVersion(itemEntity.getRelatedWikis());
        }
    }

    @Override
    public void cancelWatch(Long itemId, BaseIdentity watch) {
        TrackerItemEntity itemEntity = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_MANAGE_WATCHER), itemEntity.getTrackerId(),
                itemEntity.getId(),"","没有管理关注者权限");
        if (ObjectUtils.isNotEmpty(itemEntity.getWatchers())) {
            itemEntity.getWatchers().remove(watch);
            trackerItemDao.updateSystemField(itemEntity, TrackerItemEntity.Fields.watchers, itemEntity.getWatchers());
            changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_NOTIFICATION, "取消关注了工作项",
                    itemEntity, "", "");

            if(!itemEntity.getRelatedWikis().isEmpty()){
                docDao.incVersion(itemEntity.getRelatedWikis());
            }
        }
    }

    @Override
    public void batchUpdateTrackerItem(List<TrackerItemVo> trackerItemVos) {
        trackerItemVos.forEach(item->{
            checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT), item.getTracker().getId(),
                    item.getId(),"","没有修改工作项权限");
        });
        Collection<TrackerItemEntity> trackerItemEntities = DataUtils.toEntity(trackerItemVos, TrackerItemEntity.class);
        trackerItemDao.batchUpdateTrackerItem((List<TrackerItemEntity>) trackerItemEntities);
    }

    private void getFieldValue(TrackerField field, String value, TrackerItemEntity item, TrackerEntity tracker) throws ParseException {
        if (ObjectUtils.isEmpty(field.getSystemProperty())) {
            return;
        }
        List<EnumItemVo> trackerPriority = enumService.findEnumItemsByCode(tracker.getProjectId(), "TRACKER_PRIORITY");
        if (ObjectUtils.isEmpty(field.getInputType())) {
            field.setInputType(field.getClass().getAnnotation(JsonTypeName.class).value());
        }
        if (field.getInputType().equals(FieldTypes.DATE)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DataUtils.setProperty(item, field.getSystemProperty(), sdf.parse(value));
        } else if (field.getInputType().equals(FieldTypes.SINGLE_OPTIONS)) {
            if (field.getSystemProperty().equals(SystemFields.PRIORITY)) {
                Optional<EnumItemVo> first = trackerPriority.stream().filter(priority -> priority.getName().equals(value)).findFirst();
                if (first.isPresent()) {
                    item.setPriorityId(first.get().getId());
                }
            }else if (field.getSystemProperty().equals(SystemFields.SEVERITY)) {
                Optional<EnumItemVo> first = trackerPriority.stream().filter(priority -> priority.getName().equals(value)).findFirst();
                if (first.isPresent()) {
                    item.setPriorityId(first.get().getId());
                }
            }
        } else if (field.getInputType().equals(FieldTypes.USER)) {
            Page<UserVo> users = userService.findUsers(value, Pageable.unpaged(), null);
            Optional<UserVo> first = users.getContent().stream().filter(user -> user.getName().equals(value)).findFirst();
            first.ifPresent(userVo -> DataUtils.setProperty(item, field.getSystemProperty(), userVo.getId()));
        } else if (field.getInputType().equals(FieldTypes.STATUS)) {
            Optional<TrackerStatus> first = tracker.getTrackerStatuses().stream().filter(status -> status.getName().equals(value)).findFirst();
            if (first.isPresent()) {
                if (field.getSystemProperty().equals(SystemFields.STATUS)) {
                    item.setStatusId(first.get().getId());
                    item.setMeaningId(first.get().getMeaning().getId());
                }
            }
        } else if (field.getInputType().equals(FieldTypes.PROJECT)) {
            ProjectEntity project = projectDao.findOneProject(tracker.getProjectId());
            if (ObjectUtils.isNotEmpty(project) && project.getName().equals(value))
                DataUtils.setProperty(item, field.getSystemProperty(), project.getId());
        } else if (field.getInputType().equals(FieldTypes.WORK_ITEM_TYPE)) {
            if (tracker.getName().equals(value)) {
                DataUtils.setProperty(item, field.getSystemProperty(), tracker.getId());
            }
        }else if(field instanceof IntegerField){
            DataUtils.setProperty(item, field.getSystemProperty(), Integer.valueOf(value));
        }else {
            DataUtils.setProperty(item, field.getSystemProperty(), value);
        }

    }

    private List<HashMap> getTrackerItemMapList(List<TrackerItemVo> trackerItemVos, LinkedHashMap<String, String> headers) {
        List<HashMap> itemMapList = new ArrayList<>();

        trackerItemVos.forEach(itemVo -> {
            HashMap<String, String> itemMap = new HashMap<>();
            headers.keySet().forEach(mapKey -> {
                //system
                if (SystemFields.ALL_SYSTEM_FIELDS.contains(mapKey)) {
                    if (SystemFields.STATUS.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getStatus()));
                    } else if (SystemFields.PRIORITY.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getPriority()));
                    }else if (SystemFields.SEVERITY.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getPriority()));
                    } else if (SystemFields.STATUS_TYPE.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getMeaning()));
                    } else if (SystemFields.DESCRIPTION.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getDescription()));
                    } else if (SystemFields.ITEM_NO.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getItemNo()));
                    } else if (SystemFields.NAME.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo));
                    } else if (SystemFields.ASSIGNED_DATE.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getAssignedDate()));
                    } else if (SystemFields.ASSIGNED_TO.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getAssignedTo()));
                    } else if (SystemFields.CREATE_BY.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getCreateBy()));
                    } else if (SystemFields.CREATE_DATE.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getCreateDate()));
                    } else if (SystemFields.LAST_MODIFIED_BY.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getLastModifiedBy()));
                    } else if (SystemFields.LAST_MODIFIED_DATE.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getLastModifiedDate()));
                    } else if (SystemFields.OWNER.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getOwner()));
                    } else if (SystemFields.PLAN_START_DATE.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getPlanStartDate()));
                    } else if (SystemFields.PLAN_END_DATE.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getPlanEndDate()));
                    } else if (SystemFields.CLOSE_DATE.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getCloseDate()));
                    } else if (SystemFields.REAL_START_DATE.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getRealStartDate()));
                    } else if (SystemFields.REAL_END_DATE.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getRealEndDate()));
                    } else if (SystemFields.ESTIMATE_WORKING_HOURS.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getEstimateWorkingHours()));
                    } else if (SystemFields.REGISTERED_WORKING_HOURS.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getRegisteredWorkingHours()));
                    } else if (SystemFields.REMAINING_WORKING_HOURS.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getRemainingWorkingHours()));
                    } else if (SystemFields.PROGRESS.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getProgress()));
                    } else if (SystemFields.PROJECT.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getProject()));
                    } else if (SystemFields.TRACKER.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getTracker()));
                    } else if (SystemFields.SPRINT.equals(mapKey)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getSprint()));
                    }
                } else {//custom
                    if (!mapKey.equals(SystemFields.PARENT)) {
                        itemMap.put(mapKey, getNotNullData(itemVo.getValues().get(Long.parseLong(mapKey))));
                    }
                }
            });
            itemMapList.add(itemMap);
        });
        return itemMapList;
    }

    private String getNotNullData(Object data) {
        if (ObjectUtils.isEmpty(data)) {
            return null;
        } else if (data instanceof Date) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(data);
        } else if (data instanceof IdName) {
            return ((IdName) data).getName();
        }
        return data.toString();
    }

    @Override
    public List<TrackerLogVo> findCTrackerLogs(Long itemId, Boolean includeComment, Boolean includeChangeLog,
                                               Boolean includeAttachment, Boolean includeAssociation) {
        List<TrackerLogVo> logs = new ArrayList<>();
        if (includeComment) {
            List<CommentEntity> comments = commentDao.findByObjectId(itemId);
            List<CommentVo> commentVos = DataUtils.toVo(comments, CommentVo.class);
            logs.addAll(commentVos);
        }
        if (includeChangeLog) {
            List<ChangeLogEntity> changeLogs = changeLogDao.findByObjectId(itemId);
            logs.addAll(DataUtils.toVo(changeLogs, ChangeLogVo.class));
        }

        if (includeAttachment) {
            List<AttachmentEntity> attachments = attachmentDao.findByObjectId(itemId);
            logs.addAll(DataUtils.toVo(attachments, AttachmentVo.class));
        }
        logs.sort(new Comparator<TrackerLogVo>() {
            @Override
            public int compare(TrackerLogVo o1, TrackerLogVo o2) {
                return CompareUtil.compare(o1.getCreateDate(), o2.getCreateDate());
            }
        });

        return logs;
    }

    @Override
    public void createComment(CommentVo comment) {
        TrackerItemEntity item = trackerItemDao.findOneById(comment.getObjectId());
//        checkTrackerPermission(TrackerPermissions.ITEM_EDIT, item.getTrackerId(),
//                item.getId(),"","没有修改工作项权限");

        CommentEntity entity =
                CommentEntity.builder().id(IDUtils.getId()).objectId(comment.getObjectId())
                        .message(comment.getMessage()).replies(new ArrayList<>()).build();
        commentDao.createComment(entity);

        if (ObjectUtils.isNotEmpty(item)) {
            TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
            //发送通知
            trackerNotificationService.sendSystemNotification(NotificationEvents.CREATE_COMMENTS, tracker, item, SecurityUtils.getCurrentUser());
        }
    }

    @Override
    public void saveComment(CommentVo comment) {
        CommentEntity entity = commentDao.findById(comment.getId());
        entity.setMessage(comment.getMessage());
        commentDao.saveComment(entity);
    }

    @Override
    public void deleteComment(CommentVo comment) {
        CommentEntity entity = commentDao.findById(comment.getId());
        entity.setMarkDeleted(true);
        commentDao.saveComment(entity);
    }

    @Override
    public Double remainingRegistrableTime(Long memberId, Date beginDate) {
        Double remainingWorkToday = workHoursDao.findRemainingWorkToday(memberId, beginDate);
        if(remainingWorkToday == null) {
            return 24d;
        }else{
            return 24d - remainingWorkToday;
        }
    }

    @Override
    public List<WorkHoursVo> findWorkHours(Long itemId) {
        List<WorkHoursEntity> workHoursEntities = workHoursDao.findByObjectId(itemId);
        List<WorkHoursVo> workHoursVos = DataUtils.toVo(workHoursEntities, WorkHoursVo.class);
        return workHoursVos;
    }

    @Override
    public void updateTrackerItemSprint(Long sprintId, List<Long> trackerItemIds) {
        SprintEntity sprintEntity = null;
        if (sprintId != null) {
            sprintEntity = sprintDao.findById(sprintId);
        }
        //记录日志
        List<TrackerItemEntity> trackerItemEntities = trackerItemDao.findByIds(trackerItemIds);
        trackerItemEntities.forEach(item -> {
            checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT), item.getTrackerId(),
                    item.getId(),"","没有修改工作项权限");
        });
        Map<Long, SprintEntity> springMap = new HashMap<>();
        Map<Long, TrackerEntity> trackerMap = new HashMap<>();


        if (!trackerItemEntities.isEmpty()) {
            List<Long> sprintIds = trackerItemEntities.stream()
                    .map(TrackerItemEntity::getSprintId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            List<Long> trackerIds = trackerItemEntities.stream()
                    .map(TrackerItemEntity::getTrackerId).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            List<SprintEntity> sprintList = sprintDao.findSprintsByIds(sprintIds);
            List<TrackerEntity> trackerList = trackerDao.findByIds(trackerIds, TrackerEntity.class,true);
            for (SprintEntity entity : sprintList) {
                springMap.put(entity.getId(), entity);
            }
            for (TrackerEntity entity : trackerList) {
                trackerMap.put(entity.getId(), entity);
            }
        }
        Object oldValue = null;
        Object newValue = null;
        if (sprintEntity != null) {
            newValue = sprintEntity.getName();
        }
        for (TrackerItemEntity trackerItemEntity : trackerItemEntities) {
            if (sprintId != null && sprintId.equals(trackerItemEntity.getSprintId())) {
                continue;
            }
            if (springMap.get(trackerItemEntity.getSprintId()) != null) {
                oldValue = springMap.get(trackerItemEntity.getSprintId()).getName();
            }
            //记录日志
            changeLogDao.createChangeLog(trackerItemEntity.getId(), ChangeLogMessages.TRACKER_ITEM_FIELD_CHANGE, "所属迭代",
                    trackerItemEntity, oldValue, newValue);

            if (ObjectUtils.isNotEmpty(trackerMap.get(trackerItemEntity.getTrackerId()))) {
                //发送通知
                trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_SPRINT,
                        trackerMap.get(trackerItemEntity.getTrackerId()), trackerItemEntity, SecurityUtils.getCurrentUser());
            }

        }
        trackerItemDao.updateTrackerItemSprint(sprintId, (String) newValue, trackerItemIds);

    }

    @Override
    public void createWorkHours(WorkHoursEntity workHours) {
        checkWorkHoursPermissions(workHours.getObjectId(),workHours.getMemberId());
        workHours.setId(IDUtils.getId());
        workHoursDao.createWorkHours(workHours);
    }

    @Override
    public void saveWorkHours(WorkHoursEntity workHours) {
        checkWorkHoursPermissions(workHours.getObjectId(),workHours.getMemberId());
        workHoursDao.saveWorkHours(workHours);
    }

    @Override
    public void deleteWorkHours(WorkHoursEntity workHours) {
        checkWorkHoursPermissions(workHours.getObjectId(),workHours.getMemberId());
        workHoursDao.deleteWorkHours(workHours);
    }

    private void checkWorkHoursPermissions(Long objectId, Long memberId){
        TrackerItemEntity item = trackerItemDao.findOneById(objectId);
        Long currentId = SecurityUtils.getCurrentUser().getId();
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT),
                item.getTrackerId(),
                item.getId(),"","没有修改工作项权限");
        boolean hasAllPermission;
        try{
            hasAllPermission=checkTrackerPermission(List.of(TrackerPermissions.ITEM_MANAGE_ALL_REGISTERED_WORK_HOURS),
                    item.getTrackerId(),
                    item.getId(),"","没有管理所有登记工时权限");
        }catch (Exception e){
            hasAllPermission=false;
        }
        if(!hasAllPermission&&currentId.equals(memberId)){
            checkTrackerPermission(List.of(TrackerPermissions.ITEM_MANAGE_OWN_REGISTERED_WORK_HOURS),
                    item.getTrackerId(),
                    item.getId(),"","没有管理登记工时权限");
        }else if(!hasAllPermission){
            ServiceException.throwException("没有管理所有登记工时权限");
        }
    }

    @Override
    public void replyComment(CommentVo comment) {
        CommentEntity entity = commentDao.findById(comment.getParentId());
        CommentEntity reply =
                CommentEntity.builder().id(IDUtils.getId()).objectId(comment.getObjectId())
                        .message(comment.getMessage()).replyToId(comment.getReplyTo().getId())
                        .replies(new ArrayList<>()).build();
        commentDao.createComment(reply);
        if (ObjectUtils.isEmpty(entity.getReplies())) {
            entity.setReplies(new ArrayList<>(Arrays.asList(reply.getId())));
        } else {
            entity.getReplies().add(reply.getId());
        }
        commentDao.saveComment(entity);
    }

    @Override
    public AttachmentVo uploadAttachment(Long itemId, AttachmentVo attachmentVo) {
        TrackerItemEntity item = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT), item.getTrackerId(),
                item.getId(),"","没有修改工作项权限");
        AttachmentEntity attachment =
                AttachmentEntity.builder().id(IDUtils.getId()).name(attachmentVo.getName()).objectId(itemId)
                        .filePath(attachmentVo.getFilePath()).fileSize(attachmentVo.getFileSize()).build();
        attachmentDao.createAttachment(attachment);

//        item.getAttachments().add(attachment.getId());
//        trackerItemDao.updateAttachments(item);
        if (ObjectUtils.isNotEmpty(item)) {
            TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
            //发送通知
            trackerNotificationService.sendSystemNotification(NotificationEvents.CHANGE_ITEM_ATTACHMENT, tracker, item, SecurityUtils.getCurrentUser());
        }
        return DataUtils.toVo(attachmentDao.fineById(attachment.getId()), AttachmentVo.class);
    }

    @Override
    public void deleteAttachment(Long itemId, AttachmentVo attachmentVo) {
        TrackerItemEntity item = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT), item.getTrackerId(),
                item.getId(),"","没有修改工作项权限");
//        item.getAttachments().remove(attachmentVo.getId());
//        trackerItemDao.updateAttachments(item);

        attachmentDao.deleteAttachment(attachmentVo.getId());
    }

    @Override
    public List<AttachmentVo> findAttachmentsByObjectId(Long objectId) {
        List<AttachmentEntity> attachmentEntities = attachmentDao.findByObjectId(objectId);
        return DataUtils.toVo(attachmentEntities, AttachmentVo.class);
    }

    @Override
    public List<TrackerEntity> findTrackersBySprint(Long sprintId) {
        List<Long> trackerIds = trackerItemDao.findTrackersBySprint(sprintId);
        List<TrackerEntity> trackers = trackerDao.findByIds(trackerIds, TrackerEntity.class,true);
        return trackers;
    }

    @Override
    public ProjectCardInfo findProjectCardInfo(Long projectId) {
        ProjectCardInfo cardInfo = new ProjectCardInfo();

        EnumItemVo enumItemVo = enumService.findOneEnumItemByCode(projectId, EnumCodes.TRACKER_STATUS_MEANING,
                TrackerStatusMeaning.CLOSED);
        cardInfo.setTrendList(trackerItemDao.findDailyTrend(projectId, enumItemVo));
        cardInfo.setLastModified(trackerItemDao.findLastModified(projectId));
        return cardInfo;
    }

//    @Override
//    public void updateTrackerItemTestStep(Long itemId, TrackerTestStep testStep) {
//        TrackerItemEntity trackerItem = trackerItemDao.findOneById(itemId);
//
//        if (ObjectUtils.isNotEmpty(testStep) && ObjectUtils.isNotEmpty(testStep.getId())) {
//            List<TrackerTestStep> testSteps = trackerItem.getTestSteps();
//            Optional<TrackerTestStep> first = testSteps.stream().filter(item -> item.getId().equals(testStep.getId())).findFirst();
//            if (first.isPresent()) {
//                BeanUtils.copyProperties(testStep, first.get());
//            } else {
//                testSteps.add(testStep);
//            }
//            trackerItem.setTestSteps(testSteps.stream().filter(item ->
//                            ObjectUtils.isNotEmpty(item.getDescription()) || ObjectUtils.isNotEmpty(item.getExpectedResult()))
//                    .collect(Collectors.toList()));
//            if (ObjectUtils.isNotEmpty(testStep.getDescription())
//                    || ObjectUtils.isNotEmpty(testStep.getExpectedResult())
//                    || first.isPresent()) {
//                trackerItemDao.updateSystemField(trackerItem, TrackerItemEntity.Fields.testSteps, trackerItem.getTestSteps());
//                if(!trackerItem.getRelatedWikis().isEmpty()){
//                    docDao.incVersion(trackerItem.getRelatedWikis());
//                }
////                changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_FIELD_CHANGE, "更新了测试用例测试步骤",
////                        trackerItem, null, null);
//            }
//        }
//    }
//
//    @Override
//    public void updateTrackerItemTestSteps(Long itemId, List<TrackerTestStep> testSteps) {
//        TrackerItemEntity trackerItem = trackerItemDao.findOneById(itemId);
//        trackerItem.setTestSteps(testSteps.stream().filter(item ->
//                        ObjectUtils.isNotEmpty(item.getDescription()) || ObjectUtils.isNotEmpty(item.getExpectedResult()))
//                .collect(Collectors.toList()));
//        trackerItemDao.updateSystemField(trackerItem, TrackerItemEntity.Fields.testSteps, trackerItem.getTestSteps());
//        if(!trackerItem.getRelatedWikis().isEmpty()){
//            docDao.incVersion(trackerItem.getRelatedWikis());
//        }
//        changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_FIELD_CHANGE, "更新了测试用例测试步骤",
//                trackerItem, null, null);
//    }

    @Override
    public List<FilterCondition> findGroupItems(Long projectId, Long trackerId, Long sprintId, ObjectFilter filter, String keyword) {

        return trackerItemDao.findGroupItems(projectId, trackerId, sprintId, filter, keyword);
    }

    @Override
    public void deleteTrackerItemsByTrackerId(Long trackerId) {
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_DELETE), trackerId,
                null,"","没有删除工作项权限");
        List<TrackerItemEntity> trackerItemEntities = trackerItemDao.findItemByTrackerIds(null,Collections.singletonList(trackerId));
        Collection<Long> itemIds = ObjectUtils.ids(trackerItemEntities);
        trackerLinkDao.deleteByItemIds(itemIds);
        trackerItemDao.deleteByTrackerIds(Collections.singletonList(trackerId));
    }

    public void fieldChanged(Long itemId, Long fieldId, Object newValue) {
        TrackerItemEntity item = trackerItemDao.findOneById(itemId);
        checkTrackerPermission(List.of(TrackerPermissions.ITEM_EDIT), item.getTrackerId(),
                item.getId(),"","没有修改工作项权限");
        TrackerEntity tracker = trackerDao.findOneTracker(item.getTrackerId());
        TrackerField trackerField = ObjectUtils.findById(tracker, fieldId, TrackerField.class);
        if (trackerField != null) {
            //检查是否具有权限变化权限
            Long userId = SecurityUtils.getCurrentUser().getId();
            boolean hasPermission = checkFieldPermission(trackerField.getPermission(), tracker, item, userId);
            if (!hasPermission) {
                throw new ServiceException("没有该属性修改权限");
            }

            //改变工作项属性值
            Object oldValue = item.getCustomerFieldValue(trackerField);
            item.setCustomerFieldValue(trackerField, newValue);
            trackerItemDao.updateCustomField(item);
            if(!item.getRelatedWikis().isEmpty()){
                docDao.incVersion(item.getRelatedWikis());
            }

            //记录日志
            changeLogDao.createChangeLog(itemId, ChangeLogMessages.TRACKER_ITEM_FIELD_CHANGE, "编辑了工作项属性",
                    trackerField, oldValue, newValue);

            //发送通知
            trackerNotificationService.sendCustomerNotification(trackerField, tracker, item, SecurityUtils.getCurrentUser());
        }
    }

    @Override
    public List<TrackerLinkVo> findTrackerLinksByItemIds(List<Long> itemIds) {
        return DataUtils.toVo(trackerLinkDao.findByItemIds(itemIds),TrackerLinkVo.class);
    }

    @Override
    public void updateMatrixLinks(List<TrackerLinkEntity> trackerLinks) {
        List<Long> linkIds = trackerLinks.stream().map(TrackerLinkEntity::getId)
                .filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        List<TrackerLinkEntity> newTrackerLinks = trackerLinks.stream().filter(item -> ObjectUtils.isEmpty(item.getId())).collect(Collectors.toList());
        if(ObjectUtils.isNotEmpty(linkIds)){
            trackerLinkDao.deleteByIds(linkIds);
        }
        newTrackerLinks.forEach(item -> item.setId(IDUtils.getId()));
        if(ObjectUtils.isNotEmpty(newTrackerLinks)){
            trackerLinkDao.batchCreateTrackerLinks(newTrackerLinks);
        }
    }

    private boolean checkFieldPermission(FieldPermission permission, TrackerEntity tracker, TrackerItemEntity item,
                                         Long userId) {
        if (FieldPermission.UNRESTRICTED.equals(permission.getType())) {
            return true;
        } else if (FieldPermission.SINGLE.equals(permission.getType())) {
            return checkPermissionGranted(permission.getSinglePermissions(), tracker, item, userId);
        } else if (FieldPermission.PER_STATUS.equals(permission.getType())) {
            Long currentStatus = item.getStatusId();
            List<BaseIdentity> userRoleReadEdit =
                    permission.getStatusPermissions().get(currentStatus.toString());
            return checkPermissionGranted(userRoleReadEdit, tracker, item, userId);

        } else if (FieldPermission.SAME_AS.equals(permission.getType())) {
            TrackerField trackerField = ObjectUtils.findById(tracker, permission.getSameAsField().getId(),
                    TrackerField.class);
            return checkFieldPermission(trackerField.getPermission(), tracker, item, userId);
        }
        return false;
    }


    private boolean checkPermissionGranted(List<BaseIdentity> identities, TrackerEntity tracker,
                                           TrackerItemEntity item, Long userId) {
        if (identities == null || identities.isEmpty()) {
            return false;
        }
        List<BaseIdentity> members =
                identities.stream().filter(obj -> IdentityTypes.USER.equals(obj.getType())).collect(
                        Collectors.toList());
        BaseIdentity user = ObjectUtils.findById(members, userId, BaseIdentity.class);
        if (user != null) {
            return true;
        }


        List<BaseIdentity> specialRoles =
                identities.stream().filter(obj -> IdentityTypes.SPECIAL_ROLE.equals(obj.getType())).collect(
                        Collectors.toList());
        boolean exists = specialRoles.stream().anyMatch(sr -> {
            return checkSpecialRolePermissionGranted(sr, tracker, item, userId);
        });
        if (exists) {
            return true;
        }


        List<BaseIdentity> roles =
                identities.stream().filter(obj -> IdentityTypes.ROLE.equals(obj.getType())).collect(
                        Collectors.toList());
        List<Long> roleIds = ObjectUtils.ids(roles);
        exists = projectRoleMemberService.existsUserRoles(tracker.getProjectId(), roleIds, userId);
        return exists;
    }

    private boolean checkTrackerPermission(List<String> permissionNames,Long trackerId,Long itemId,String systemProperty,String message){
        if (Arrays.asList(InternalTrackers.HEADING.getId(), InternalTrackers.PARAGRAPH.getId(),
                InternalTrackers.TITLE.getId()).contains(trackerId)) {
            return true; //文档tracker
        }

        if(ObjectUtils.isNotEmpty(permissionNames)){
            if(!trackerPermissionService.hasTrackerPermission(permissionNames,trackerId,itemId)){
                    ServiceException.throwException(message);
                }
        }else{
            if(SystemFields.STATUS.equals(systemProperty)){
                if(!trackerPermissionService.hasTrackerPermission(
                        List.of(TrackerPermissions.ITEM_EDIT,TrackerPermissions.ITEM_CHANGE_STATUS),
                        trackerId,itemId)){
                    ServiceException.throwException("没有更新工作项状态权限");
                }
            }else if(SystemFields.ESTIMATE_WORKING_HOURS.equals(systemProperty)){
                if(!trackerPermissionService.hasTrackerPermission(
                        List.of(TrackerPermissions.ITEM_EDIT,TrackerPermissions.ITEM_MANAGE_ESTIMATE_WORK_HOURS),
                        trackerId,itemId)){
                    ServiceException.throwException("没有管理预估工时权限");
                }
            }
        }
        return true;
    }

    private boolean checkSpecialRolePermissionGranted(BaseIdentity sr, TrackerEntity tracker, TrackerItemEntity item,
                                                      Long userId) {
        boolean result = false;
        RoleVo role = roleService.findOneRole(sr.getId());
        String roleType = role.getSpecialRoleType();
        if (SpecialRole.PROJECT_OWNER.equals(roleType)) {
            ProjectEntity project = projectDao.findOneProject(tracker.getProjectId());
            result = project.getOwnerId().equals(userId);
        } else if (SpecialRole.PROJECT_ALL_MEMBERS.equals(roleType)) {
            List<UserVo> userEntities = projectRoleMemberService.findProjectUsers(tracker.getProjectId());
            UserVo user = ObjectUtils.findById(userEntities, userId, UserVo.class);
            result = (user != null);
        } else if (SpecialRole.ALL_USERS.equals(roleType)) {
            result = true;
        } else if (SpecialRole.TRACKER_OWNER.equals(roleType)) {
            result = item.getOwnerId().equals(userId);
        } else if (SpecialRole.TRACKER_CREATOR.equals(roleType)) {
            result = item.getCreateBy().equals(userId);
        } else if (SpecialRole.TRACKER_WATCHER.equals(roleType)) {
            List<BaseIdentity> identities = item.getWatchers();
            result = checkPermissionGranted(identities, tracker, item, userId);
        }
        return result;
    }

    public void setFieldValue(TrackerItemVo trackerItem, Long fieldId, Object value) {
        Optional<TrackerField> trackerField =
                trackerItem.getTracker().getReferTo().getTrackerFields().stream()
                           .filter(tf -> tf.getId().equals(fieldId)).findFirst();
        if (trackerField.isEmpty()) {
            return;
        }
        String field = trackerField.get().getSystemProperty();
        if (SystemFields.NAME.equals(field)) {
            trackerItem.setName(value.toString());
        } else if (SystemFields.ITEM_NO.equals(field)) {
            trackerItem.setItemNo(value.toString());
        } else if (SystemFields.DESCRIPTION.equals(field)) {
            trackerItem.setDescription(value.toString());
        } else if (SystemFields.OWNER.equals(field)) {
            trackerItem.setOwner(new IdNameReference<>(UserVo.builder().id(Long.valueOf(value.toString())).build()));
        } else if (SystemFields.SPRINT.equals(field)) {
            trackerItem.setSprint(new IdNameReference<>(SprintVo.builder().id(Long.valueOf(value.toString())).build()));
        } else if (SystemFields.CREATE_BY.equals(field)) {
            trackerItem.setCreateBy(new IdNameReference<>(UserVo.builder().id(Long.valueOf(value.toString())).build()));
        } else if (SystemFields.CREATE_DATE.equals(field)) {
            trackerItem.setCreateDate(DateUtils.strToDate(value.toString()));
        } else if (SystemFields.LAST_MODIFIED_BY.equals(field)) {
            trackerItem.setLastModifiedBy(
                    new IdNameReference<>(UserVo.builder().id(Long.valueOf(value.toString())).build()));
        } else if (SystemFields.LAST_MODIFIED_DATE.equals(field)) {
            trackerItem.setLastModifiedDate(DateUtils.strToDate(value.toString()));
        } else if (SystemFields.STATUS.equals(field)) {
            trackerItem.setStatusId(Long.valueOf(value.toString()));
            for (TrackerStatus status : trackerItem.getTracker().getReferTo().getTrackerStatuses()) {
                if (status.getId().equals(trackerItem.getStatusId())) {
                    trackerItem.setStatus(status);
                    break;
                }
            }
        } else if (SystemFields.ASSIGNED_TO.equals(field)) {
            trackerItem.setAssignedTo(new IdNameReference<>(UserVo.builder().id(Long.valueOf(value.toString())).build()));
        } else if (SystemFields.ASSIGNED_DATE.equals(field)) {
            trackerItem.setAssignedDate(DateUtils.strToDate(value.toString()));
        } else if (SystemFields.PRIORITY.equals(field)) {
            EnumItemVo enumItemVo = enumService.findOneEnumItemById(Long.valueOf(value.toString()));
            trackerItem.setPriority(enumItemVo);
        } else if (SystemFields.SEVERITY.equals(field)) {
            EnumItemVo enumItemVo = enumService.findOneEnumItemById(Long.valueOf(value.toString()));
            trackerItem.setSeverity(enumItemVo);
        } else if (SystemFields.PLAN_START_DATE.equals(field)) {
            trackerItem.setPlanStartDate(DateUtils.strToDate(value.toString()));
        } else if (SystemFields.PLAN_END_DATE.equals(field)) {
            trackerItem.setPlanEndDate(DateUtils.strToDate(value.toString()));
        } else if (SystemFields.REAL_START_DATE.equals(field)) {
            trackerItem.setRealStartDate(DateUtils.strToDate(value.toString()));
        } else if (SystemFields.REAL_END_DATE.equals(field)) {
            trackerItem.setRealEndDate(DateUtils.strToDate(value.toString()));
        } else if (SystemFields.PROGRESS.equals(field)) {
            trackerItem.setProgress(Integer.valueOf(value.toString()));
        } else if (SystemFields.CLOSE_DATE.equals(field)) {
            trackerItem.setCloseDate(DateUtils.strToDate(value.toString()));
        } else if (SystemFields.ESTIMATE_WORKING_HOURS.equals(field)) {
            trackerItem.setEstimateWorkingHours(Double.valueOf(value.toString()));
        } else if (SystemFields.REGISTERED_WORKING_HOURS.equals(field)) {
            trackerItem.setRegisteredWorkingHours(Double.valueOf(value.toString()));
        } else if (SystemFields.REMAINING_WORKING_HOURS.equals(field)) {
            trackerItem.setRemainingWorkingHours(Double.valueOf(value.toString()));
//        } else if (SystemFields.TEST_CASE_TYPE.equals(field)) {
//            EnumItemVo enumItemVo = enumService.findOneEnumItemById(Long.valueOf(value.toString()));
//            trackerItem.setTestCaseType(enumItemVo);
        } else {
            trackerItem.setCustomerFieldValue(trackerField.get(), value);
        }
    }
}
