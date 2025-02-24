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

package com.harbortek.helm.tracker.template.builder;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.constants.FieldTypes;
import com.harbortek.helm.tracker.constants.NotificationEvents;
import com.harbortek.helm.tracker.constants.ObjectTypes;
import com.harbortek.helm.tracker.rule.RuleEngine;
import com.harbortek.helm.tracker.util.TrackerUtils;
import com.harbortek.helm.tracker.vo.ProjectRoleMemberVo;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.*;
import com.harbortek.helm.tracker.vo.tracker.layout.TrackerLayout;
import com.harbortek.helm.tracker.vo.tracker.nofitication.CustomerTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.SystemTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.TrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.permissions.FieldPermission;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransition;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.Cleanup;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TrackerXmlReader {
    private final Logger logger = LoggerFactory.getLogger(TrackerXmlReader.class);
    private EntityResolver entityResolver;

    private EntityResolver trackerEntityResolver = new EntityResolver();
    Node parent;

    public TrackerXmlReader(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

//    public TrackerXmlReader(EntityResolver entityResolver, Reader reader) {
//        super(entityResolver);
//        SAXReader saxReader = new SAXReader();
//        try {
//            Document document = saxReader.read(reader);
//            parent = document.selectSingleNode("tracker");
//
//            List<ProjectRoleMemberVo> roles = buildSystemSpecialRoles();
//            roles.forEach(r->{
//                register(r, ProjectRoleMemberVo.class);
//            });
//
//            List<TrackerField> systemTrackerFields = buildSystemFields();
//            systemTrackerFields.forEach(f->{
//                register(f,TrackerField.class);
//            });
//
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//    }

    public void register(Object o, String clzName) {
        trackerEntityResolver.register(o, clzName);
    }

    public <T> T findById(Long id, String clzName, Class<T> clz) {
        T result = (T) trackerEntityResolver.findById(id,clzName, clz);
        if (result != null) {
            return result;
        }
        return (T) entityResolver.findById(id, clzName,clz);
    }

    public <T> T findByName(String name,String clzName, Class<T> clz) {
        T result = (T) trackerEntityResolver.findByName(name, clzName,clz);
        if (result != null) {
            return result;
        }
        return (T) entityResolver.findByName(name,clzName, clz);
    }

    public <T> Collection<T> findByType(String clzName,Class<T> clz) {
        Collection<T> result = trackerEntityResolver.findByType(clzName,clz);
        if (result != null) {
            return result;
        }
        return entityResolver.findByType(clzName,clz);
    }

    public TrackerVo build(Resource resource) {
        SAXReader saxReader = new SAXReader();
        try {
            @Cleanup InputStream is = resource.getInputStream();
            Document document = saxReader.read(is);
            this.parent = document.selectSingleNode("/tracker");
            TrackerVo tracker = createTracker();

            List<TrackerStatus> trackerStatuses = createTrackerStatus();
            trackerStatuses.forEach(status -> {
                register(status, ObjectTypes.TRACKER_STATUS);
            });

            List<PermissionGrantVo> trackerRolePermissions = createTrackerPermissions();
            trackerRolePermissions.forEach(trackerRolePermission -> {
                register(trackerRolePermission, ObjectTypes.TRACKER_ROLE_PERMISSION);
            });

            List<TrackerStateTransition> trackerStateTransitions = createStateTransitions();
            trackerStateTransitions.forEach(stateTransition -> {
                register(stateTransition, ObjectTypes.TRACKER_STATE_TRANSITION);
            });

            List<TrackerField> trackerFields = createTrackerFields();
            trackerFields.forEach(field -> {
                register(field, ObjectTypes.TRACKER_FIELD);
            });

            List<TrackerLayout> trackerLayouts = createTrackerLayouts();


            TrackerNotification trackerNotification = createTrackerNotification();

            tracker.setTrackerStatuses(trackerStatuses);
            tracker.setTrackerPermissions(trackerRolePermissions);
            tracker.setTrackerStateTransitions(trackerStateTransitions);
            tracker.setTrackerFields(trackerFields);
            tracker.setTrackerLayouts(trackerLayouts);
            tracker.setTrackerNotification(trackerNotification);
            return tracker;
        } catch (IOException | DocumentException e) {
            logger.error("无法加载项目模版文件,{0}", resource.getFilename());
        }
        return null;

    }

    private List<ProjectRoleMemberVo> buildSystemSpecialRoles() {
        List<ProjectRoleMemberVo> specialRoles = Arrays.asList(
                ProjectRoleMemberVo.builder().name("项目负责人").specialRoleType(SpecialRole.PROJECT_OWNER)
                                   .scope(SpecialRole.SCOPE_PROJECT).build(),
                ProjectRoleMemberVo.builder().name("所有项目成员").specialRoleType(SpecialRole.PROJECT_ALL_MEMBERS)
                                   .scope(SpecialRole.SCOPE_PROJECT)
                                   .build(),
                ProjectRoleMemberVo.builder().name("所有人").specialRoleType(SpecialRole.ALL_USERS)
                                   .scope(SpecialRole.SCOPE_PROJECT).build(),
                ProjectRoleMemberVo.builder().name("工作项负责人").specialRoleType(SpecialRole.TRACKER_OWNER)
                                   .scope(SpecialRole.SCOPE_TRACKER).build(),
                ProjectRoleMemberVo.builder().name("工作项创建者").specialRoleType(SpecialRole.TRACKER_CREATOR)
                                   .scope(SpecialRole.SCOPE_TRACKER).build(),
                ProjectRoleMemberVo.builder().name("工作项关注者").specialRoleType(SpecialRole.TRACKER_WATCHER)
                                   .scope(SpecialRole.SCOPE_TRACKER).build());
        specialRoles.forEach(f -> {
            f.setSpecialRole(true);
        });
        return specialRoles;
    }



    public TrackerVo createTracker() {
        TrackerVo tracker = new TrackerVo();
        tracker.setName(parent.valueOf("@name"));
        tracker.setDescription(parent.valueOf("@description"));
//        tracker.setShortName(parent.valueOf("@shortName"));
        EnumItemVo trackerType = findByName(parent.valueOf("@type"), ObjectTypes.TRACKER_TYPE, EnumItemVo.class);
        tracker.setTrackerType(trackerType);
        if(ObjectUtils.isNotEmpty(trackerType)){
            tracker.setIcon(trackerType.getIcon());
        }
//        tracker.setVisibleInd(Boolean.valueOf(parent.valueOf("@visible")));
//        tracker.setWorkflowInd(Boolean.valueOf(parent.valueOf("@workflow")));
        return tracker;
    }

    public List<TrackerStatus> createTrackerStatus() {
        List<Node> nodes = parent.selectNodes("statuses/status");
        ArrayList<TrackerStatus> statuses = new ArrayList<>();
        int index = 1;
        for (Node node : nodes) {
            TrackerStatus status = new TrackerStatus();
            status.setName(node.valueOf("@name"));
            status.setInitial(Boolean.valueOf(node.valueOf("@initial")));
            status.setDescription(node.valueOf("@description"));
            status.setMeaning(findByName(node.valueOf("@meaning"), ObjectTypes.TRACKER_STATUS_MEANING,
                                         EnumItemVo.class));
            status.setId(IDUtils.getId());//先给id保证item.tracker.statusId存在
            status.setOrdinary(index++);
            statuses.add(status);
        }
        return statuses;
    }

    public List<PermissionGrantVo> createTrackerPermissions() {
        List<Node> nodes = parent.selectNodes("permissions/permission");
        ArrayList<PermissionGrantVo> permissions = new ArrayList<>();
        for (Node node : nodes) {
            PermissionGrantVo grantVo = new PermissionGrantVo();
            String permissionName = node.valueOf("@name");
            grantVo.setPermissionName(permissionName);
            grantVo.setGranted(readPermissionGrant(node));
            permissions.add(grantVo);
        }
        return permissions;
    }

    public List<TrackerStateTransition> createStateTransitions() {
        List<Node> nodes = parent.selectNodes("state-transitions/state-transition");
        ArrayList<TrackerStateTransition> trackerStateTransitions = new ArrayList<>();
        for (Node node : nodes) {
            TrackerStateTransition stateTransition = new TrackerStateTransition();
            stateTransition.setName(node.valueOf("@name"));
//            stateTransition.setHidden(Boolean.valueOf(node.valueOf("@hidden")));
            String from = node.valueOf("@from");
            String to = node.valueOf("@to");
            stateTransition.setTransitionFrom(new IdNameReference<>(findByName(from,
                                                                               ObjectTypes.TRACKER_STATUS,
                                                                               TrackerStatus.class)));
            stateTransition.setTransitionTo(new IdNameReference<>(findByName(to, ObjectTypes.TRACKER_STATUS,TrackerStatus.class)));

            List<BaseIdentity> granted = readPermissionGrant(node);
            stateTransition.setPermitted(granted);

            trackerStateTransitions.add(stateTransition);
        }
        return trackerStateTransitions;
    }

    public List<TrackerLayout> createTrackerLayouts() {
        List<Node> nodes = parent.selectNodes("layouts/layout");
        ArrayList<TrackerLayout> trackerLayouts = new ArrayList<>();
        for (Node node : nodes) {
            String name = node.valueOf("@name");
            TrackerLayout trackerLayout = new TrackerLayout();
            trackerLayout.setName(name);

            List<Node> fieldNodes = node.selectNodes("fields/field");
            for (Node fieldNode : fieldNodes) {
                String fieldName = fieldNode.valueOf("@name");
                TrackerField trackerField = findByName(fieldName,ObjectTypes.TRACKER_FIELD, TrackerField.class);
                trackerLayout.getFields().add(new IdNameReference<>(trackerField));
            }

            List<Node> sectionNodes = node.selectNodes("sections/section");
            for (Node sectionNode : sectionNodes) {
                String sectionName = sectionNode.valueOf("@name");
                trackerLayout.getSections().add(sectionName);
            }

            List<Node> keyFieldNodes = node.selectNodes("key-fields/field");
            for (Node fieldNode : keyFieldNodes) {
                String fieldName = fieldNode.valueOf("@name");
                TrackerField trackerField = findByName(fieldName,ObjectTypes.TRACKER_FIELD,  TrackerField.class);
                trackerLayout.getKeyFields().add(new IdNameReference<>(trackerField));
            }
            trackerLayouts.add(trackerLayout);
        }
        return trackerLayouts;
    }

    public List<TrackerField> createTrackerFields() {
        List<Node> nodes = parent.selectNodes("fields/field");
        ArrayList<TrackerField> trackerFields = new ArrayList<>();
        for (Node node : nodes) {
            String type = node.valueOf("@type");
            TrackerField trackerField = null;
            if (FieldTypes.STATUS.equals(type)) {
                trackerField = StatusField.builder().build();
            } else if (FieldTypes.DECIMAL.equals(type)) {
                trackerField = DecimalField.builder().build();
            } else if (FieldTypes.INTEGER.equals(type)) {
                trackerField = IntegerField.builder().build();
            } else if (FieldTypes.DURATION.equals(type)) {
                trackerField = DurationField.builder().build();
            } else if (FieldTypes.BOOLEAN.equals(type)) {
                trackerField = BoolField.builder().build();
            } else if (FieldTypes.DATE.equals(type)) {
                trackerField = DateField.builder().build();
            } else if (FieldTypes.REFERENCE.equals(type)) {
                trackerField = ReferenceField.builder().build();
            } else if (FieldTypes.TEXT.equals(type)) {
                trackerField = TextField.builder().build();
            } else if (FieldTypes.WIKITEXT.equals(type)) {
                trackerField = WikiTextField.builder().build();
            } else if (FieldTypes.COLOR.equals(type)) {
                trackerField = ColorField.builder().build();
            } else if (FieldTypes.COUNTRY.equals(type)) {
                trackerField = CountryField.builder().build();
            } else if (FieldTypes.LANGUAGE.equals(type)) {
                trackerField = LanguageField.builder().build();
            } else if (FieldTypes.MEMBERS.equals(type)) {
                trackerField = MembersField.builder().build();
            } else if (FieldTypes.USER.equals(type)) {
                trackerField = UserField.builder().build();
            } else if (FieldTypes.URL.equals(type)) {
                trackerField = WikiTextField.builder().build();
            } else if (FieldTypes.SINGLE_OPTIONS.equals(type)) {
                trackerField = OptionsField.builder().enumName(node.valueOf("@enumName")).build();
            } else if (FieldTypes.MULTI_OPTIONS.equals(type)) {
                trackerField = MultiOptionsField.builder().enumName(node.valueOf("@enumName")).build();
            } else if (FieldTypes.TABLE.equals(type)) {
                trackerField = TableField.builder().build();
            } else if (FieldTypes.WORK_ITEM.equals(type)) {
                trackerField = WorkItemField.builder().build();
            } else if (FieldTypes.WORK_ITEM_NO.equals(type)) {
                trackerField = WorkItemNoField.builder().build();
            } else if (FieldTypes.STATUS_TYPE.equals(type)) {
                trackerField = StatusTypeField.builder().build();
            } else if (FieldTypes.PROJECT.equals(type)) {
                trackerField = ProjectField.builder().build();
            } else if (FieldTypes.WORK_ITEM_TYPE.equals(type)) {
                trackerField = WorkItemTypeField.builder().build();
            } else if(FieldTypes.TEST_STEP.equals(type)){
                trackerField = TestStepField.builder().build();
                List<TrackerField> columns= ((TestStepField) trackerField).createDefaultColumns();
                ((TableField) trackerField).setColumns(columns);
                columns.forEach(column -> {
                    column.setId(IDUtils.getId());
                });
            }

            if(ObjectUtils.isNotEmpty(node.valueOf("@system"))){
                trackerField.setSystem(Boolean.valueOf(node.valueOf("@system")));
            }else{
                trackerField.setSystem(Boolean.FALSE);
            }
            trackerField.setName(node.valueOf("@name"));
            trackerField.setTitle(node.valueOf("@title"));
            trackerField.setShowInList(Boolean.valueOf(node.valueOf("@showInList")));
            trackerField.setDistributionRule(RuleEngine.getDistributionRule(node.valueOf("distributionRule")));
            trackerField.setAggregationRule(RuleEngine.getAggregationRule(node.valueOf("aggregationRule")));

            if(ObjectUtils.isNotEmpty(node.selectSingleNode("permission"))){

                String permissionType = node.selectSingleNode("permission").valueOf("@type");
                FieldPermission fieldPermission = FieldPermission.builder().type(permissionType).build();
                if (FieldPermission.SINGLE.equals(permissionType)) {
                    List<BaseIdentity> granted = readPermissionGrant(node);
                    fieldPermission.setSinglePermissions(granted);
                } else if (FieldPermission.UNRESTRICTED.equals(permissionType)) {

                } else if (FieldPermission.SAME_AS.equals(permissionType)) {

                } else if (FieldPermission.PER_STATUS.equals(permissionType)) {
                    Map<Object, List<BaseIdentity>> map = new HashMap<>();
                    List<Node> statusNodes = node.selectNodes("status");
                    for (Node statusNode : statusNodes) {
                        String statusName = statusNode.valueOf("statusName");
                        List<BaseIdentity> granted = readPermissionGrant(node);
                        map.put(statusName, granted);
                    }
                    fieldPermission.setStatusPermissions(map);
                }
                trackerField.setPermission(fieldPermission);
            }

            trackerFields.add(trackerField);
        }

        trackerFields.addAll(TrackerUtils.buildSystemFields());
        return trackerFields;
    }

    public TrackerNotification createTrackerNotification() {

        TrackerNotification trackerNotification = new TrackerNotification();

        //读取系统事件通知
        List<String> systemEvents = NotificationEvents.ALL_EVENTS;
        Map<String, SystemTrackerNotification> map = new LinkedHashMap<>();
        systemEvents.forEach(e -> {
            SystemTrackerNotification notification = new SystemTrackerNotification();
            notification.setEventName(e);
            map.put(e, notification);
        });

        List<Node> systemNodes = parent.selectNodes("notifications/system-config/notification");
        for (Node node : systemNodes) {
            String eventName = node.valueOf("@eventName");
            SystemTrackerNotification notification = map.get(eventName);
            if(ObjectUtils.isNotEmpty(notification)){
                notification.setUseMessage(Boolean.valueOf(node.valueOf("@useMessage")));
                notification.setUseEmail(Boolean.valueOf(node.valueOf("@useEmail")));

                List<BaseIdentity> granted = readPermissionGrant(node);
                notification.setSubscribers(granted);
            }
        }
        map.values().forEach(n -> {
            trackerNotification.getSystemTrackerNotifications().add(n);
        });


        //读取自定义字段通知
        Node customerConfigNode = parent.selectSingleNode("notifications/customer-config");
        Boolean useDefault = Boolean.valueOf(customerConfigNode.valueOf("@useDefault"));
        trackerNotification.setUsedForAllCustomerFields(useDefault);
        if (useDefault) {
            Node node = customerConfigNode.selectSingleNode("notification");
            CustomerTrackerNotification notification = new CustomerTrackerNotification();
            notification.setUseMessage(Boolean.valueOf(node.valueOf("@useMessage")));
            notification.setUseEmail(Boolean.valueOf(node.valueOf("@useEmail")));
            List<BaseIdentity> granted = readPermissionGrant(node);
            notification.setSubscribers(granted);
            trackerNotification.setDefaultNotification(notification);
        } else {
            List<Node> customerNodes = parent.selectNodes("notifications/customer-config/notification");
            for (Node node : customerNodes) {
                CustomerTrackerNotification notification = new CustomerTrackerNotification();
                String fieldName = node.valueOf("@fieldName");

                TrackerField field = findByName(fieldName,ObjectTypes.TRACKER_FIELD,  TrackerField.class);
                notification.setTrackerField(new IdNameReference<>(field));
                notification.setUseMessage(Boolean.valueOf(node.valueOf("@useMessage")));
                notification.setUseEmail(Boolean.valueOf(node.valueOf("@useEmail")));

                List<BaseIdentity> granted = readPermissionGrant(node);
                notification.setSubscribers(granted);

                trackerNotification.getCustomerTrackerNotifications().add(notification);
            }
        }

        return trackerNotification;
    }


    private List<BaseIdentity> readPermissionGrant(Node parent) {
        ArrayList<BaseIdentity> granted = new ArrayList<>();
        List<Node> roleNodes = parent.selectNodes("grant-to-role");
        for (Node child : roleNodes) {
            String roleName = child.getText();
            ProjectRoleMemberVo role =
                    findByName(roleName, ObjectTypes.PROJECT_ROLE_MEMBER, ProjectRoleMemberVo.class);
            BaseIdentity identity = new BaseIdentity(new IdNameReference(role));
            identity.setType(IdentityTypes.ROLE);
            granted.add(identity);
        }
        List<Node> specialRoleNodes = parent.selectNodes("grant-to-special-role");
        for (Node child : specialRoleNodes) {
            String roleName = child.getText();
            ProjectRoleMemberVo role =
                    findByName(roleName, ObjectTypes.PROJECT_ROLE_MEMBER, ProjectRoleMemberVo.class);
            BaseIdentity identity = new BaseIdentity(new IdNameReference(role));
            identity.setType(IdentityTypes.SPECIAL_ROLE);
            granted.add(identity);
        }
        List<Node> userNodes = parent.selectNodes("grant-to-user");
        for (Node child : userNodes) {
            String userName = child.getText();
            UserVo userVo = entityResolver.findByName(userName, ObjectTypes.USER, UserVo.class);
            BaseIdentity identity = new BaseIdentity(new IdNameReference(userVo));
            identity.setType(IdentityTypes.USER);
            granted.add(identity);
        }
        return granted;
    }
}
