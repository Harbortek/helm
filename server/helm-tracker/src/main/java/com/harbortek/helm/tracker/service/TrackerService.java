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

package com.harbortek.helm.tracker.service;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.system.vo.TrackerFieldsPermissions;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.layout.TrackerLayout;
import com.harbortek.helm.tracker.vo.tracker.nofitication.CustomerTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.SystemTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.TrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.permissions.TrackerRolePermission;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransition;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;

import java.util.Collection;
import java.util.List;

public interface TrackerService {

	TrackerVo createTracker(TrackerVo tracker);

	TrackerVo updateTracker(TrackerVo tracker);

	void deleteTracker(TrackerVo tracker);


	TrackerVo findOneTracker(Long id);

	List<TrackerVo> findByProject(Long projectId);

	List<TrackerVo> findByProject(Long projectId, boolean showInternal);

	List<TrackerVo> findTrackerByIds(List<Long> trackerIds);

	void updateTrackerLayout(Long trackerId, TrackerLayout trackerLayout);

	void fillTrackerFields(List<TrackerField> trackerFields, List<TrackerStatus> trackerStatuses, Long projectId);

//	void updateTrackerPermission(Long trackerId, TrackerRolePermission trackerRolePermission);


	TrackerLayout findOneTrackerLayout(Long trackerId, String layoutName);

	List<TrackerStateTransition> findTrackerStateTransitions(Long trackerId);

	void updateTrackerStateTransitions(Long trackerId, List<TrackerStateTransition> trackerStateTransitions);

	void updateTrackerStatus(Long trackerId, TrackerStatus trackerStatus);

	void deleteTrackerStatus(Long trackerId, TrackerStatus trackerStatus);

	List<TrackerStatus> findTrackerStatus(Long trackerId);

	TrackerStatus createTrackerStatus(Long trackerId, TrackerStatus trackerStatus);

	void sortTrackerStatus(Long trackerId, List<TrackerStatus> trackerStatuses);

	void setInitialTrackerStatus(Long trackerId, TrackerStatus trackerStatus);

	boolean trackerStatusUseByTransition(Long trackerId, TrackerStatus trackerStatus);

	boolean trackerStatusUseByWorkItem(Long trackerId, TrackerStatus trackerStatus);

	TrackerStateTransition createTrackerStateTransition(Long trackerId, TrackerStateTransition trackerStateTransition);

	void updateTrackerStateTransition(Long trackerId, TrackerStateTransition trackerStateTransition);

	void deleteTrackerStateTransition(Long trackerId, TrackerStateTransition trackerStateTransition);

	void renameTrackerStateTransition(Long trackerId, TrackerStateTransition trackerStateTransition);

    List<TrackerField> findTrackerFields(Long trackerId);

	TrackerField createTrackerField(Long trackerId, TrackerField trackerField);

	void updateTrackerField(Long trackerId, TrackerField trackerField);

	void deleteTrackerField(Long trackerId, TrackerField trackerField);

    List<PermissionGrantVo> findTrackerPermissions(Long trackerId);

	void grantTrackerPermission(Long trackerId, String permissionName, BaseIdentity identity);

	void unGrantTrackerPermission(Long trackerId, String permissionName, BaseIdentity identity);


	TrackerNotification findTrackerNotification(Long trackerId);

	void subscribeSystemEvent(Long trackerId, String eventName, BaseIdentity identity);

	void unSubscribeSystemEvent(Long trackerId, String eventName, BaseIdentity identity);

	void subscribeCustomerEvent(Long trackerId, Long fieldId, BaseIdentity identity);

	void unSubscribeCustomerEvent(Long trackerId, Long fieldId, BaseIdentity identity);

	void createCustomerNotification(Long trackerId, CustomerTrackerNotification notification);

	void deleteCustomerNotification(Long trackerId, CustomerTrackerNotification notification);

	void subscribeAllCustomerEvent(Long trackerId, BaseIdentity identity);

	void unSubscribeAllCustomerEvent(Long trackerId, BaseIdentity identity);

	void updateSystemEventNoticeType(Long trackerId, String eventName, SystemTrackerNotification systemTrackerNotification);

	void updateCustomerEventNoticeType(Long trackerId, Long fieldId, CustomerTrackerNotification customerTrackerNotification);

	void updateAllCustomerEventNoticeType(Long trackerId, CustomerTrackerNotification customerTrackerNotification);

	void grantTrackerStateTransitionPermitted(Long trackerId, Long transitionId, BaseIdentity identity);

	void unGrantTrackerStateTransitionPermitted(Long trackerId, Long transitionId, BaseIdentity identity);

    void updateTrackerOrdinary(List<TrackerVo> trackerVos);

	void copyTracker(TrackerVo trackerVo);

	void copyTrackerFields(Long trackerId, List<TrackerField> trackerFields);

	void updateTrackerNameAndType(TrackerVo trackerVo);

	void replaceWorkItemStatus(Long trackerId, Long oldTrackerStatusId, Long newTrackerStatusId);

    void createTrackers(Collection<TrackerVo> trackers);

    TrackerVo findOneTrackerByName(Long projectId, String trackerName);
}
