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
import com.harbortek.helm.system.constants.IdentityTypes;
import com.harbortek.helm.tracker.vo.template.ProjectTemplateVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.permissions.FieldPermission;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransitionAction;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateXmlWriter {
	ProjectTemplateVo template;

	public TemplateXmlWriter(ProjectTemplateVo template) {
		this.template = template;
	}

	public void write(Writer writer) throws Exception {

		// 1、创建document对象
		Document document = DocumentHelper.createDocument();

		// 2、创建根节点
		Element rootElement = document.addElement("project-template");

		// 3、为节点添加属性
		rootElement.addAttribute("name", template.getName());
		rootElement.addAttribute("description", template.getDescription());


		// 4、生成子节点及子节点内容
		writeProjectRoles(rootElement);

		writeTrackers(rootElement);

		// 5、设置生成xml的格式
		OutputFormat format = OutputFormat.createPrettyPrint();
		// 设置编码格式
		format.setEncoding("UTF-8");

		// 6、生成xml文件
		XMLWriter xmmlWriter = new XMLWriter(writer, format);
		// 设置是否转义，默认使用转义字符
		xmmlWriter.setEscapeText(false);
		xmmlWriter.write(document);
		xmmlWriter.close();
	}


	void writeProjectRoles(Element rootElement) {
		Element rolesElement = rootElement.addElement("roles");
		template.getRoles().forEach(role -> {
			Element roleElement = rolesElement.addElement("role");
			roleElement.addAttribute("name", role.getName());
			roleElement.addAttribute("description", role.getDescription());
			//TODO 要写入成员信息
			//roleElement.addAttribute("displayAsRole", toString(role.getDisplayAsRole()));
//			List<String> permissions = role.getPermissions().stream().map(permission -> {
//				return permission.getName();
//			}).collect(Collectors.toList());
//			roleElement.addAttribute("permissions", StringUtils.join(permissions.toArray(), ","));
		});
	}

	void writeProjectPermissionGrant(Element rootElement){
		Element permisssionsElement = rootElement.addElement("permissions");
		template.getPermissionGrants().forEach(grantVo -> {
			Element permissionElement = permisssionsElement.addElement("permission");
			permissionElement.addAttribute("name", grantVo.getPermissionName());
			writePermissionGrant(permissionElement,grantVo.getGranted());
		});
	}

	void writeTrackers(Element rootElement) {
		Element trackersElement = rootElement.addElement("trackers");
		template.getTrackers().forEach(tracker -> {
			Element trackerElement = trackersElement.addElement("tracker");
			trackerElement.addAttribute("name", tracker.getName());
//			trackerElement.addAttribute("shortName", tracker.getShortName());
//			trackerElement.addAttribute("visible", toString(tracker.getVisibleInd()));
//			trackerElement.addAttribute("workflow", toString(tracker.getWorkflowInd()));

			writeTrackerStatus(trackerElement, tracker);

			writeTrackerPermissions(trackerElement, tracker);

			writeTrackerStateTransitions(trackerElement, tracker);

			writeTrackerFields(trackerElement, tracker);

			writeTrackerLayouts(trackerElement, tracker);

			writeTrackerNotifications(trackerElement, tracker);

		});
	}

	private void writeTrackerStatus(Element trackerElement, TrackerVo tracker) {
		Element statusesElement = trackerElement.addElement("statuses");
		tracker.getTrackerStatuses().forEach(trackerStatus -> {
			Element statusElement = statusesElement.addElement("status");
			statusElement.addAttribute("name", trackerStatus.getName());
			statusElement.addAttribute("description", trackerStatus.getDescription());
//			statusElement.addAttribute("color", trackerStatus.getColor());
			statusElement.addAttribute("meaning", trackerStatus.getMeaning().getName());
		});
	}


	private void writeTrackerPermissions(Element trackerElement, TrackerVo tracker) {
		Element permissionsElement = trackerElement.addElement("permissions");

		tracker.getTrackerPermissions().forEach(trackerRolePermission -> {
			Element permissionElement = permissionsElement.addElement("permission");
			permissionElement.addAttribute("name", trackerRolePermission.getPermissionName());

			List<BaseIdentity>  identityList = trackerRolePermission.getGranted();
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
			Element transitionElement = null;
//			if (TrackerStateTransition.TRANSITION_TYPE_STATE_TRANSITION.equals(trackerStateTransition.getType())) {
				transitionElement = stateTransitionElement.addElement("state-transition");
				transitionElement.addAttribute("name", trackerStateTransition.getName());
//				transitionElement.addAttribute("hidden", toString(trackerStateTransition.getHidden()));
				transitionElement.addAttribute("from", trackerStateTransition.getTransitionFrom().getName());
				transitionElement.addAttribute("to", trackerStateTransition.getTransitionTo().getName());
				List<String> roles = trackerStateTransition.getPermitted().stream().map(permission -> {
					return permission.getName();
				}).collect(Collectors.toList());
				Element permissionElement = stateTransitionElement.addElement("permission");
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
				fieldElement.addAttribute("type", trackerField.getInputType());
				fieldElement.addAttribute("name", trackerField.getName());
				fieldElement.addAttribute("description", trackerField.getDescription());
				fieldElement.addAttribute("name", trackerField.getName());
				fieldElement.addAttribute("title", trackerField.getTitle());
				fieldElement.addAttribute("showInList", toString(trackerField.getShowInList()));

				if (trackerField.getAggregationRule() != null) {
					fieldElement.addAttribute("aggregationRule", trackerField.getAggregationRule().getName());
				}

				if (trackerField.getDistributionRule() != null) {
					fieldElement.addAttribute("distributionRule", trackerField.getDistributionRule().getName());
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
				Element statusElement = fieldElement.addElement("status");
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
	}

	private void writeTrackerNotifications(Element trackerElement, TrackerVo tracker) {
	}

	private String toString(Boolean value) {
		if (Boolean.TRUE.equals(value)) {
			return "true";
		}
		return "false";
	}
}
