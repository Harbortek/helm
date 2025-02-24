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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.tracker.util.ResourceUtils;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.MultiOptionsField;
import com.harbortek.helm.tracker.vo.tracker.fields.OptionsField;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.nofitication.CustomerTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.SystemTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.TrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.permissions.FieldPermission;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransitionAction;
import com.harbortek.helm.util.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrackerXmlWriter {
    TrackerVo tracker;

    public TrackerXmlWriter(TrackerVo vo){
        this.tracker = vo;
    }


    public void write(File outputFile) {
        Document document = DocumentHelper.createDocument();

        Element trackerElement = document.addElement("tracker");
        trackerElement.addAttribute("name", tracker.getName());
        trackerElement.addAttribute("type", tracker.getTrackerType().getName());

        writeTrackerStatus(trackerElement, tracker);

        writeTrackerPermissions(trackerElement, tracker);

        writeTrackerStateTransitions(trackerElement, tracker);

        writeTrackerFields(trackerElement, tracker);

        writeTrackerLayouts(trackerElement, tracker);

        writeTrackerNotifications(trackerElement, tracker);

        ResourceUtils.writeXml(outputFile,document);

    }

    private void writeTrackerStatus(Element trackerElement, TrackerVo tracker) {
        Element statusesElement = trackerElement.addElement("statuses");
        tracker.getTrackerStatuses().forEach(trackerStatus -> {
            Element statusElement = statusesElement.addElement("status");
            statusElement.addAttribute("name", trackerStatus.getName());
            statusElement.addAttribute("description", trackerStatus.getDescription());
//			statusElement.addAttribute("color", trackerStatus.getColor());
            statusElement.addAttribute("meaning", trackerStatus.getMeaning().getName());
            statusElement.addAttribute("initial", trackerStatus.getInitial().toString());
        });
    }


    private void writeTrackerPermissions(Element trackerElement, TrackerVo tracker) {
        Element permissionsElement = trackerElement.addElement("permissions");

        tracker.getTrackerPermissions().forEach(trackerRolePermission -> {
            Element permissionElement = permissionsElement.addElement("permission");
            permissionElement.addAttribute("name", trackerRolePermission.getPermissionName());

            List<BaseIdentity> identityList = trackerRolePermission.getGranted();
            identityList.forEach(identity -> {
                if (IdentityTypes.ROLE.equals(identity.getType())){
                    Element grantElement =  permissionElement.addElement("grant-to-role");
                    grantElement.setText(identity.getName());
                }else if (IdentityTypes.SPECIAL_ROLE.equals(identity.getType())){
                    Element grantElement =  permissionElement.addElement("grant-to-special-role");
                    grantElement.setText(identity.getName());
                }else if (IdentityTypes.USER.equals(identity.getType())){
                    Element grantElement =  permissionElement.addElement("grant-to-user");
                    grantElement.setText(identity.getName());
                }
            });
        });
    }

    private void writeTrackerStateTransitions(Element trackerElement, TrackerVo tracker) {
        Element stateTransitionElement = trackerElement.addElement("state-transitions");
        tracker.getTrackerStateTransitions().forEach(trackerStateTransition -> {
//			if (TrackerStateTransition.TRANSITION_TYPE_STATE_TRANSITION.equals(trackerStateTransition.getType())) {
            Element transitionElement = stateTransitionElement.addElement("state-transition");
            transitionElement.addAttribute("name", trackerStateTransition.getName());
//				transitionElement.addAttribute("hidden", toString(trackerStateTransition.getHidden()));
            transitionElement.addAttribute("from", trackerStateTransition.getTransitionFrom().getName());
            transitionElement.addAttribute("to", trackerStateTransition.getTransitionTo().getName());
            List<String> roles = trackerStateTransition.getPermitted().stream().map(permission -> {
                return permission.getName();
            }).collect(Collectors.toList());
            Element permissionElement = transitionElement.addElement("permission");
            writePermissionGrant(permissionElement,trackerStateTransition.getPermitted());
            //transitionElement.addAttribute("permitted", StringUtils.join(roles.toArray(), ","));
//				writeStateGuard(transitionElement, trackerStateTransition);
//				writeStateCondition(transitionElement, trackerStateTransition);

//			} else if (TrackerStateTransition.TRANSITION_TYPE_STATE_ENTRY.equals(trackerStateTransition.getType())) {
//				transitionElement = stateTransitionElement.addElement("state-entry");
//				transitionElement.addAttribute("status", trackerStateTransition.getStatus().getName());
//			} else if (TrackerStateTransition.TRANSITION_TYPE_STATE_EXIT.equals(trackerStateTransition.getType())) {
//				transitionElement = stateTransitionElement.addElement("state-exit");
//				transitionElement.addAttribute("status", trackerStateTransition.getStatus().getName());
//			} else if (TrackerStateTransition.TRANSITION_TYPE_CHANGE_HANDLER.equals(trackerStateTransition.getType())) {
//				transitionElement = stateTransitionElement.addElement("change-handler");
//				transitionElement.addAttribute("status", trackerStateTransition.getStatus().getName());
//				writeStateChange(transitionElement, trackerStateTransition);
//			}


            if (transitionElement != null && trackerStateTransition.getActions() != null && !trackerStateTransition.getActions().isEmpty()) {
                Element actionsElement = transitionElement.addElement("actions");
                trackerStateTransition.getActions().forEach(trackerStateTransitionAction -> {
                    writeStateActions(actionsElement, trackerStateTransitionAction);
                });
            }

        });
    }

//	private void writeStateChange(Element transitionElement, TrackerStateTransition trackerStateTransition) {
//		if (trackerStateTransition.getGuard() == null) {
//			return;
//		}
//		Element guardElement = transitionElement.addElement("change-condition");
//		guardElement.addAttribute("name", trackerStateTransition.getGuard().getName());
//		//TODO
//	}
//
//	private void writeStateCondition(Element transitionElement, TrackerStateTransition trackerStateTransition) {
//		if (trackerStateTransition.getConditionType() == null) {
//			return;
//		}
//
//		Element conditionElement = transitionElement.addElement("condition");
//		conditionElement.addAttribute("type", trackerStateTransition.getConditionType());
//		if (TrackerStateTransition.CONDITION_TYPE_EXPRESSION.equals(trackerStateTransition.getConditionType())) {
//			conditionElement.addAttribute("expression", trackerStateTransition.getExpressionEL());
//		}
//	}
//
//	private void writeStateGuard(Element transitionElement, TrackerStateTransition trackerStateTransition) {
//		if (trackerStateTransition.getGuard() == null) {
//			return;
//		}
//		Element guardElement = transitionElement.addElement("guard");
//		guardElement.addAttribute("name", trackerStateTransition.getGuard().getName());
//		//TODO
//	}

    private void writeStateActions(Element actionsElement, TrackerStateTransitionAction trackerStateTransitionAction) {
        Element actionElement = actionsElement.addElement("action");
        actionElement.addAttribute("type", trackerStateTransitionAction.getType());
        //TODO
    }

    void writeTrackerFields(Element trackerElement, TrackerVo tracker) {
        Element fieldsElement = trackerElement.addElement("fields");
        tracker.getTrackerFields().forEach(trackerField -> {
            if (!trackerField.getSystem()){
                Element fieldElement = fieldsElement.addElement("field");
                fieldElement.addAttribute("type", trackerField.getClass().getAnnotation(JsonTypeName.class).value());
                fieldElement.addAttribute("name", trackerField.getName());
                fieldElement.addAttribute("system", trackerField.getSystem().toString());
                fieldElement.addAttribute("description", trackerField.getDescription());
                fieldElement.addAttribute("name", trackerField.getName());
                fieldElement.addAttribute("title", trackerField.getTitle());
                if (trackerField.getAggregationRule() != null) {
                    fieldElement.addAttribute("aggregationRule", trackerField.getAggregationRule().getName());
                }
                if (trackerField.getDistributionRule() != null) {
                    fieldElement.addAttribute("distributionRule", trackerField.getDistributionRule().getName());
                }
                if(!trackerField.isSystem()&& trackerField instanceof OptionsField){
                    fieldElement.addAttribute("enumName", ((OptionsField) trackerField).getEnumName());
                }else if(!trackerField.isSystem()&& trackerField instanceof MultiOptionsField){
                    fieldElement.addAttribute("enumName", ((MultiOptionsField) trackerField).getEnumName());
                }
                writeTrackerFieldPermission(fieldElement, trackerField);

            }
        });
    }

    private void writeTrackerFieldPermission(Element fieldElement, TrackerField trackerField) {
        FieldPermission fieldPermission = trackerField.getPermission();
        if (fieldPermission == null) {
            return;
        }
        Element permissionElement = fieldElement.addElement("permission");
        permissionElement.addAttribute("type", trackerField.getPermission().getType());
        if (FieldPermission.UNRESTRICTED.equals(fieldPermission.getType())) {

        } else if (FieldPermission.SINGLE.equals(fieldPermission.getType())) {
            writePermissionGrant(permissionElement, fieldPermission.getSinglePermissions());
        } else if (FieldPermission.SAME_AS.equals(fieldPermission.getType())) {
            permissionElement.addAttribute("fieldName", fieldPermission.getSameAsField().getName());
        } else if (FieldPermission.PER_STATUS.equals(fieldPermission.getType())) {
            Map<Object, List<BaseIdentity>> statusMap = fieldPermission.getStatusPermissions();
            statusMap.forEach((status, urre) -> {
                Element statusElement = permissionElement.addElement("status");
                if (status instanceof String) { //name
                    String statusName = (String)status;
                    statusElement.addAttribute("statusName", statusName);
                }else if (status instanceof Long){ //id

                }
                writePermissionGrant(statusElement, urre);
            });
        }

    }

    private void writePermissionGrant(Element permissionElement, List<BaseIdentity> identityList) {
        identityList.forEach(identity -> {
            if (IdentityTypes.ROLE.equals(identity.getType())){
                Element grantElement =  permissionElement.addElement("grant-to-role");
                grantElement.setText(identity.getName());
            }else if (IdentityTypes.SPECIAL_ROLE.equals(identity.getType())){
                Element grantElement =  permissionElement.addElement("grant-to-special-role");
                grantElement.setText(identity.getName());
            }else if (IdentityTypes.USER.equals(identity.getType())){
                Element grantElement =  permissionElement.addElement("grant-to-user");
                grantElement.setText(identity.getName());
            }
        });
    }

    private void writeTrackerLayouts(Element trackerElement, TrackerVo tracker) {
        Element layoutsElement = trackerElement.addElement("layouts");
        tracker.getTrackerLayouts().forEach(trackerLayout -> {
            Element layoutElement = layoutsElement.addElement("layout");
            layoutElement.addAttribute("name",trackerLayout.getName());
            List<IdNameReference> fields = trackerLayout.getFields();
            if(!fields.isEmpty()){
                Element fieldsElement = layoutElement.addElement("fields");
                for (IdNameReference field : fields) {
                    Element fieldElement = fieldsElement.addElement("field");
                    fieldElement.addAttribute("name", field.getName());
                }
            }
            List<String> sections = trackerLayout.getSections();
            if(!sections.isEmpty()){
                Element sectionsElement = layoutElement.addElement("sections");
                for (String section : sections) {
                    Element sectionElement = sectionsElement.addElement("section");
                    sectionElement.addAttribute("name", section);
                }
            }
            List<IdNameReference> keyFields = trackerLayout.getKeyFields();
            if(!keyFields.isEmpty()){
                Element keyFieldsElement = layoutElement.addElement("key-fields");
                for (IdNameReference keyField : keyFields) {
                    Element fieldElement = keyFieldsElement.addElement("field");
                    fieldElement.addAttribute("name", keyField.getName());
                }
            }
        });
    }

    private void writeTrackerNotifications(Element trackerElement, TrackerVo tracker) {
        Element notificationsElement = trackerElement.addElement("notifications");
        TrackerNotification trackerNotification = tracker.getTrackerNotification();
        if(ObjectUtils.isEmpty(trackerNotification)){
            return;
        }
        List<SystemTrackerNotification> systemTrackerNotifications = trackerNotification.getSystemTrackerNotifications();
        Element systemConfigElement = notificationsElement.addElement("system-config");

        for (SystemTrackerNotification notification : systemTrackerNotifications) {
            Element notificationElement = systemConfigElement.addElement("notification");
            notificationElement.addAttribute("eventName",notification.getEventName());
            notificationElement.addAttribute("useMessage",notification.getUseMessage().toString());
            notificationElement.addAttribute("useEmail",notification.getUseEmail().toString());
            writePermissionGrant(notificationElement,notification.getSubscribers());
        }
        Boolean useDefault = trackerNotification.getUsedForAllCustomerFields();
        Element customerConfigElement = notificationsElement.addElement("customer-config");
        customerConfigElement.addAttribute("useDefault",useDefault.toString());
        if(useDefault){
            CustomerTrackerNotification notification = trackerNotification.getDefaultNotification();
            Element notificationElement = customerConfigElement.addElement("notification");
            notificationElement.addAttribute("useMessage",notification.getUseMessage().toString());
            notificationElement.addAttribute("useEmail",notification.getUseEmail().toString());
            writePermissionGrant(notificationElement,notification.getSubscribers());
        }else{
            List<CustomerTrackerNotification> customerTrackerNotifications = trackerNotification.getCustomerTrackerNotifications();
            for (CustomerTrackerNotification notification : customerTrackerNotifications) {
                Element notificationElement = customerConfigElement.addElement("notification");
                notificationElement.addAttribute("fieldName",notification.getTrackerField().getName());
                notificationElement.addAttribute("useMessage",notification.getUseMessage().toString());
                notificationElement.addAttribute("useEmail",notification.getUseEmail().toString());
                writePermissionGrant(notificationElement,notification.getSubscribers());
            }
        }


    }
}
