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

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.dao.UserDao;
import com.harbortek.helm.system.entity.UserEntity;
import com.harbortek.helm.system.service.EmailService;
import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.service.NotificationService;
import com.harbortek.helm.system.service.RoleService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.NotificationVo;
import com.harbortek.helm.system.vo.RoleVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.NotificationEvents;
import com.harbortek.helm.tracker.dao.ProjectDao;
import com.harbortek.helm.tracker.dao.TrackerDao;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.service.ProjectRoleMemberService;
import com.harbortek.helm.tracker.service.TrackerNotificationService;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.nofitication.CustomerTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.SystemTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.TrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TrackerNotificationServiceImpl implements TrackerNotificationService {
    @Autowired
    ProjectDao projectDao;
    @Autowired
    TrackerItemDao trackerItemDao;

    @Autowired
    TrackerDao trackerDao;

    @Autowired
    ProjectRoleMemberService projectRoleMemberService;

    @Autowired
    RoleService roleService;
    @Autowired
    EnumService enumService;
    @Autowired
    NotificationService notificationService;

    @Autowired
    UserDao userDao;

    @Autowired
    EmailService emailService;

    @Value("${server.export.url}")
    String serverUrl;

    @Async
    public void sendSystemNotification(String eventType, TrackerEntity tracker, TrackerItemEntity item,
                                       UserVo currentUser) {
        ProjectEntity project = projectDao.findOneProject(tracker.getProjectId());

        TrackerNotification notification = tracker.getTrackerNotification();
        List<SystemTrackerNotification> systemTrackerNotifications = notification.getSystemTrackerNotifications();
        Optional<SystemTrackerNotification> sysNotification =
                systemTrackerNotifications.stream().filter(n -> {
                    return CompareUtils.compare(n.getEventName(),
                                                eventType) == 0;
                }).findFirst();

        sysNotification.ifPresent(
                systemTrackerNotification -> sendNotification(eventType, systemTrackerNotification.getUseMessage(),
                                                              systemTrackerNotification.getUseEmail(),
                                                              systemTrackerNotification.getSubscribers(), project,
                                                              tracker, item, null,currentUser));
    }

    @Async
    public void sendCustomerNotification(TrackerField field, TrackerEntity tracker,
                                         TrackerItemEntity item,UserVo currentUser) {
        TrackerNotification notification = tracker.getTrackerNotification();
        ProjectEntity project = projectDao.findOneProject(tracker.getProjectId());
        if (notification.getUsedForAllCustomerFields()) {
            CustomerTrackerNotification ctn = notification.getDefaultNotification();
            sendNotification(NotificationEvents.CHANGE_ITEM_CUSTOMER_PROPERTY, ctn.getUseMessage(), ctn.getUseEmail(),
                             ctn.getSubscribers(), project,
                             tracker, item, field,currentUser);
        } else {
            List<CustomerTrackerNotification> customerTrackerNotifications =
                    notification.getCustomerTrackerNotifications();
            Optional<CustomerTrackerNotification> customerTrackerNotification =
                    customerTrackerNotifications.stream().filter(n -> {
                        return CompareUtils.compare(n.getTrackerField().getId(), field.getId()) == 0;
                    }).findFirst();
            if (customerTrackerNotification.isPresent()) {
                CustomerTrackerNotification ctn = customerTrackerNotification.get();
                sendNotification(NotificationEvents.CHANGE_ITEM_CUSTOMER_PROPERTY, ctn.getUseMessage(),
                                 ctn.getUseEmail(), ctn.getSubscribers(), project,
                                 tracker, item, field,currentUser);
            }
        }
    }

    private void sendNotification(String eventType, Boolean sendMessage, Boolean sendMail,
                                  List<BaseIdentity> identities,
                                  ProjectEntity project,
                                  TrackerEntity tracker,
                                  TrackerItemEntity item, TrackerField field,UserVo currentUser) {
        List<Long> userIds = getAllUserIdsFromBaseIdentity(identities, project, tracker, item);
        if (sendMessage) {//发送通知
            String title = getNotificationTitle(eventType, project, tracker, item);
            String icon = tracker.getIcon();
            String href = getNotificationLink(item);
            String content = getNotificationContent(eventType, project, tracker, item, field,currentUser);
            List<NotificationVo> notificationEntities = new ArrayList<>();
            userIds.forEach(userId -> {
                NotificationVo notificationEntity =
                        NotificationVo.builder().title(title).icon(icon).href(href).description(content)
                                          .receiver(UserVo.builder().id(userId).build()).build();
                notificationEntities.add(notificationEntity);
            });
            notificationService.sendNotifications(notificationEntities);
        }

        if (sendMail) {//发送邮件
            List<UserEntity> users = userDao.findUsersByIds(userIds);
            String title = getNotificationContent(eventType, project, tracker, item, field,currentUser);
            String href = getNotificationLink(item);
            String content = getEmailContent(title, href, project, tracker, item,currentUser);
            users.forEach(user -> {
                if (StringUtils.isNotEmpty(user.getEmail()) && RegexUtils.checkEmail(user.getEmail())) {
                    emailService.sendHtmlMail(user.getEmail(), title, content);
                }
            });
        }
    }


    private List<Long> getAllUserIdsFromBaseIdentity(List<BaseIdentity> identities, ProjectEntity project,
                                                     TrackerEntity tracker, TrackerItemEntity item) {
        HashSet<Long> userIds = new HashSet<>();
        if (identities == null) {
            return new ArrayList<>();
        }

        //单独用户
        List<BaseIdentity> members =
                identities.stream().filter(obj -> IdentityTypes.USER.equals(obj.getType())).collect(
                        Collectors.toList());
        members.forEach(member -> {
            userIds.add(member.getId());
        });


        //特殊角色
        List<BaseIdentity> specialRoles =
                identities.stream().filter(obj -> IdentityTypes.SPECIAL_ROLE.equals(obj.getType())).collect(
                        Collectors.toList());
        specialRoles.forEach(sr -> {
            RoleVo role = roleService.findOneRole(sr.getId());
            String roleType = role.getSpecialRoleType();
            if (SpecialRole.PROJECT_OWNER.equals(roleType)) {
                userIds.add(project.getOwnerId());
            } else if (SpecialRole.PROJECT_ALL_MEMBERS.equals(roleType)) {
                List<UserVo> userEntities =
                        projectRoleMemberService.findProjectUsers(tracker.getProjectId());
                userIds.addAll(userEntities.stream().map(UserVo::getId).collect(Collectors.toList()));
            } else if (SpecialRole.ALL_USERS.equals(roleType)) {
                List<UserEntity> userEntities = userDao.findUsersForExport();
                userIds.addAll(userEntities.stream().map(UserEntity::getId).collect(Collectors.toList()));
            } else if (SpecialRole.TRACKER_OWNER.equals(roleType)) {
                userIds.add(item.getOwnerId());
            } else if (SpecialRole.TRACKER_CREATOR.equals(roleType)) {
                userIds.add(item.getCreateBy());
            } else if (SpecialRole.TRACKER_WATCHER.equals(roleType)) {
                List<BaseIdentity> watchers = item.getWatchers();
                userIds.addAll(getAllUserIdsFromBaseIdentity(watchers, project, tracker, item));
            }
        });

        List<BaseIdentity> roles =
                identities.stream().filter(obj -> IdentityTypes.ROLE.equals(obj.getType())).collect(
                        Collectors.toList());
        List<Long> roleIds = ObjectUtils.ids(roles);
        List<UserVo> userEntities =
                projectRoleMemberService.findProjectUsers(tracker.getProjectId(), roleIds);
        userIds.addAll(userEntities.stream().map(UserVo::getId).collect(Collectors.toList()));
        return new ArrayList<>(userIds);
    }

    private String getNotificationTitle(String eventType, ProjectEntity project, TrackerEntity tracker,
                                        TrackerItemEntity item) {
        return project.getName() + " " + tracker.getName() + " #" + item.getItemNo();
    }

    private String getNotificationLink(TrackerItemEntity item) {
        return MessageFormat.format("/tracker/project/{0}/trackerItems/{1}/{2}", String.valueOf(item.getProjectId()),
                                    String.valueOf(item.getTrackerId()), String.valueOf(item.getId()));
    }


    private String getNotificationContent(String eventType, ProjectEntity project, TrackerEntity tracker,
                                          TrackerItemEntity item, TrackerField field,UserVo currentUser) {
        String itemName = tracker.getName() + "「" + item.getName() + "」";
        String content = "";
        String sendUserName = currentUser.getName();
        if (NotificationEvents.CREATE_ITEM.equalsIgnoreCase(eventType)) {
            content = sendUserName + " 创建了" + itemName;
        } else if (NotificationEvents.CHANGE_ITEM_OWNER.equalsIgnoreCase(eventType)) {
            String ownerName = userDao.findOneUser(item.getOwnerId()).getName();
            content =
                    sendUserName + " 更改" + itemName + "所有者为 " + ownerName;
        } else if (NotificationEvents.CHANGE_ITEM_STATUS.equalsIgnoreCase(eventType)) {
            TrackerStatus trackerStatus =
                    Objects.requireNonNull(
                            ObjectUtils.findById(tracker.getTrackerStatuses(), item.getStatusId(),
                                                 TrackerStatus.class));
            String targetStatusName = trackerStatus.getName();
            content =
                    sendUserName + " 更改" + itemName + "状态为 " + targetStatusName;
        } else if (NotificationEvents.CHANGE_ITEM_PRIORITY.equalsIgnoreCase(eventType)) {
            EnumItemVo trackerPriorityVo = enumService.findOneEnumItemById(item.getPriorityId());
            String name = trackerPriorityVo.getName();
            content =
                    sendUserName + " 更改" + itemName + "的优先级为 " + name;
        } else if (NotificationEvents.CHANGE_ITEM_TITLE.equalsIgnoreCase(eventType)) {
            content =
                    sendUserName + " 更改了" + itemName + "的标题 ";
            ;
        } else if (NotificationEvents.CHANGE_ITEM_DESCRIPTION.equalsIgnoreCase(eventType)) {
            content =
                    sendUserName + " 更改了" + itemName + "的描述 ";
        } else if (NotificationEvents.CREATE_COMMENTS.equalsIgnoreCase(eventType)) {
            content =
                    sendUserName + " 发表了新的评价";
        } else if (NotificationEvents.CHANGE_ITEM_WATCHER.equalsIgnoreCase(eventType)) {
            content =
                    sendUserName + " 更改了" + itemName + "的关注者 ";
        } else if (NotificationEvents.CHANGE_ITEM_ASSOCIATIONS.equalsIgnoreCase(eventType)) {
            content =
                    sendUserName + " 更改了" + itemName + "的关联工作项 ";
        } else if (NotificationEvents.CHANGE_ITEM_ATTACHMENT.equalsIgnoreCase(eventType)) {
            content = sendUserName + " 上传了附件 ";
        } else if (NotificationEvents.CHANGE_ITEM_CUSTOMER_PROPERTY.equalsIgnoreCase(eventType)) {
            content = sendUserName + " 更改了自定义属性「 " + field.getName() + "」";
        }
        return content;
    }

    private String getEmailContent(String title, String href, ProjectEntity project, TrackerEntity tracker,
                                   TrackerItemEntity item,UserVo currentUser) {
        String opUserName = currentUser.getName();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("opUserName", opUserName);
        parameters.put("title", title);
        parameters.put("href", serverUrl + href);
        parameters.put("project", project);
        parameters.put("tracker", tracker);
        parameters.put("item", item);
        StringWriter writer = new StringWriter();
        try {
            VelocityUtils.export("/email-template/tracker-item-notification.vm", parameters, writer);
        } catch (Exception e) {
            log.error("邮件内容生产失败", e);
        }
        return writer.toString();
    }
}
