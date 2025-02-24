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

package com.harbortek.helm.tracker.api;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.tracker.constants.NotificationEvents;
import com.harbortek.helm.tracker.constants.TrackerPermissions;
import com.harbortek.helm.tracker.service.TrackerService;
import com.harbortek.helm.tracker.vo.permission.PermissionGrantVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.layout.TrackerLayout;
import com.harbortek.helm.tracker.vo.tracker.nofitication.CustomerTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.SystemTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransition;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Tag(name = "项目管理")
@RequestMapping(value = "/tracker/project/tracker")
public class TrackerApi {

    private final Logger logger = LoggerFactory.getLogger(TrackerApi.class);
    @Autowired
    TrackerService trackerService;

    @Parameter(name="查询项目工作项定义列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<List<TrackerVo>> findTrackersByProject(
            @RequestParam(value = "projectId", required = true) Long projectId,boolean showInternal) {
        List<TrackerVo> trackers = trackerService.findByProject(projectId,showInternal);
        return ResponseEntity.ok(trackers);
    }

    @Parameter(name="查询项目工作项")
    @RequestMapping(value = "/{trackerId}", method = RequestMethod.GET)
    ResponseEntity<TrackerVo> findTracker(@PathVariable(value = "trackerId", required = true) Long trackerId) {
        TrackerVo vo = trackerService.findOneTracker(trackerId);
        return ResponseEntity.ok(vo);
    }

    @Parameter(name="查询项目工作项")
    @RequestMapping(value = "/findByIds", method = RequestMethod.GET)
    ResponseEntity<List<TrackerVo>> findTrackerByIds(@RequestParam List<Long> trackerIds) {
        List<TrackerVo> vos = trackerService.findTrackerByIds(trackerIds);
        return ResponseEntity.ok(vos);
    }


    @Parameter(name="保存工作项")
    @RequestMapping(value = "/{trackerId}", method = RequestMethod.PUT)
    ResponseEntity<TrackerVo> updateTracker(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                            @RequestBody TrackerVo trackerVo) {
        trackerVo.setId(trackerId);
        TrackerVo vo = trackerService.updateTracker(trackerVo);
        return ResponseEntity.ok(vo);
    }

    @Parameter(name="保存工作项")
    @RequestMapping(value = "/{trackerId}/nameAndType", method = RequestMethod.PUT)
    ResponseEntity<TrackerVo> updateTrackerName(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                            @RequestBody TrackerVo trackerVo) {
        trackerVo.setId(trackerId);
        trackerService.updateTrackerNameAndType(trackerVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="保存工作项排序")
    @RequestMapping(value = "/sort", method = RequestMethod.POST)
    ResponseEntity<Void> updateTrackerOrdinary(@RequestBody List<TrackerVo> trackerVos) {
        trackerService.updateTrackerOrdinary(trackerVos);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="从其他项目复制工作项")
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    ResponseEntity<Void> copyTracker(@RequestBody TrackerVo trackerVo) {
        trackerService.copyTracker(trackerVo);
        return ResponseEntity.ok().build();
    }
    @Parameter(name="从其他工作项复制工作项属性")
    @RequestMapping(value = "/{trackerId}/copy-fields", method = RequestMethod.POST)
    ResponseEntity<Void> copyTrackerFields(@PathVariable Long trackerId,@RequestBody List<TrackerField> trackerFields) {
        trackerService.copyTrackerFields(trackerId,trackerFields);
        return ResponseEntity.ok().build();
    }


    @Parameter(name="删除工作项")
    @RequestMapping(value = "/{trackerId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteTracker(@PathVariable Long trackerId) {
        TrackerVo tracker = new TrackerVo();
        tracker.setId(trackerId);
        trackerService.deleteTracker(tracker);
        return ResponseEntity.ok().build();
    }


    @Parameter(name="查询所有工作项权限")
    @RequestMapping(value = "/{trackerId}/permissions", method = RequestMethod.GET)
    ResponseEntity<List<PermissionGrantVo>> findTrackerPermissionsDefault(
            @PathVariable(value = "trackerId", required = true) Long trackerId) {
        List<PermissionGrantVo> result = trackerService.findTrackerPermissions(trackerId);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="查询所有默认工作项权限")
    @RequestMapping(value = "/permissions/default", method = RequestMethod.GET)
    ResponseEntity<List<String>> findPermissions() {
        List<String> result = TrackerPermissions.TRACKER_PERMISSIONS;
        return ResponseEntity.ok(result);
    }

    @Parameter(name="增加工作项权限")
    @RequestMapping(value = "/{trackerId}/permissions/grant", method = RequestMethod.POST)
    ResponseEntity<Void> grantTrackerPermission(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                @RequestParam String permissionName,
                                                @RequestBody BaseIdentity identity) {
        trackerService.grantTrackerPermission(trackerId, permissionName, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除工作项权限")
    @RequestMapping(value = "/{trackerId}/permissions/unGrant", method = RequestMethod.POST)
    ResponseEntity<Void> unGrantTrackerPermission(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                  @RequestParam String permissionName,
                                                  @RequestBody BaseIdentity identity) {
        trackerService.unGrantTrackerPermission(trackerId, permissionName, identity);
        return ResponseEntity.ok().build();
    }




    @Parameter(name="查询工作项属性")
    @RequestMapping(value = "/{trackerId}/fields", method = RequestMethod.GET)
    ResponseEntity<List<TrackerField>> findTrackerFields(
            @PathVariable(value = "trackerId", required = true) Long trackerId) {
        List<TrackerField> trackerFields = trackerService.findTrackerFields(trackerId);
        return ResponseEntity.ok(trackerFields);
    }

    @Parameter(name="创建工作项属性")
    @RequestMapping(value = "/{trackerId}/fields", method = RequestMethod.POST)
    ResponseEntity<Void> createTrackerField(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                            @RequestBody TrackerField trackerField) {
        trackerService.createTrackerField(trackerId, trackerField);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新工作项属性")
    @RequestMapping(value = "/{trackerId}/fields", method = RequestMethod.PUT)
    ResponseEntity<Void> updateTrackerField(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                            @RequestBody TrackerField trackerField) {
        trackerService.updateTrackerField(trackerId, trackerField);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除工作项属性")
    @RequestMapping(value = "/{trackerId}/fields", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteTrackerField(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                            @RequestBody TrackerField trackerField) {
        trackerService.deleteTrackerField(trackerId, trackerField);
        return ResponseEntity.ok().build();
    }


    @Parameter(name="保存工作项布局")
    @RequestMapping(value = "/{trackerId}/updateLayout", method = RequestMethod.PUT)
    ResponseEntity<Void> updateTrackerLayout(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                             @RequestBody TrackerLayout trackerLayout) {

        trackerService.updateTrackerLayout(trackerId, trackerLayout);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询工作项布局")
    @RequestMapping(value = "/{trackerId}/findLayout", method = RequestMethod.GET)
    ResponseEntity<TrackerLayout> findTrackerLayout(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                    @RequestParam(value = "layoutName", required = true) String layoutName) {

        TrackerLayout trackerLayout = trackerService.findOneTrackerLayout(trackerId, layoutName);
        return ResponseEntity.ok(trackerLayout);
    }

    @Parameter(name="保存工作项状态")
    @RequestMapping(value = "/{trackerId}/status", method = RequestMethod.POST)
    ResponseEntity<TrackerStatus> createTrackerStatus(
            @PathVariable(value = "trackerId", required = true) Long trackerId,
            @RequestBody TrackerStatus trackerStatus) {

        TrackerStatus result = trackerService.createTrackerStatus(trackerId, trackerStatus);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="保存工作项状态")
    @RequestMapping(value = "/{trackerId}/status", method = RequestMethod.PUT)
    ResponseEntity<Void> updateTrackerStatus(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                             @RequestBody TrackerStatus trackerStatus) {

        trackerService.updateTrackerStatus(trackerId, trackerStatus);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除工作项状态")
    @RequestMapping(value = "/{trackerId}/status", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteTrackerStatus(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                             @RequestBody TrackerStatus trackerStatus) {

        trackerService.deleteTrackerStatus(trackerId, trackerStatus);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="检查工作项状态是否被状态迁移使用")
    @RequestMapping(value = "/{trackerId}/status/useByTransition", method = RequestMethod.POST)
    ResponseEntity<Boolean> trackerStatusUseByTransition(
            @PathVariable(value = "trackerId", required = true) Long trackerId,
            @RequestBody TrackerStatus trackerStatus) {

        boolean result = trackerService.trackerStatusUseByTransition(trackerId, trackerStatus);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="检查工作项状态是否被工作项使用")
    @RequestMapping(value = "/{trackerId}/status/useByWorkItem", method = RequestMethod.POST)
    ResponseEntity<Boolean> trackerStatusUseByWorkItem(
            @PathVariable(value = "trackerId", required = true) Long trackerId,
            @RequestBody TrackerStatus trackerStatus) {

        boolean result = trackerService.trackerStatusUseByWorkItem(trackerId, trackerStatus);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="改变已使用工作项状态的工作项为另外状态")
    @RequestMapping(value = "/{trackerId}/status/replaceWorkItemStatus", method = RequestMethod.POST)
    ResponseEntity<Boolean> replaceWorkItemStatus(
            @PathVariable(value = "trackerId", required = true) Long trackerId,
            Long oldTrackerStatusId, Long newTrackerStatusId) {

        trackerService.replaceWorkItemStatus(trackerId, oldTrackerStatusId,newTrackerStatusId);
        return ResponseEntity.ok().build();
    }


    @Parameter(name="排序工作项状态")
    @RequestMapping(value = "/{trackerId}/status/sort", method = RequestMethod.POST)
    ResponseEntity<Void> sortTrackerStatus(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                           @RequestBody List<TrackerStatus> trackerStatuses) {

        trackerService.sortTrackerStatus(trackerId, trackerStatuses);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="设置初始工作项状态")
    @RequestMapping(value = "/{trackerId}/status/setInitial", method = RequestMethod.POST)
    ResponseEntity<Void> setInitialTrackerStatus(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                 @RequestBody TrackerStatus trackerStatus) {

        trackerService.setInitialTrackerStatus(trackerId, trackerStatus);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询工作项状态")
    @RequestMapping(value = "/{trackerId}/status", method = RequestMethod.GET)
    ResponseEntity<List<TrackerStatus>> findTrackerStatus(@PathVariable(value = "trackerId",
            required = true) Long trackerId) {

        List<TrackerStatus> trackerStatuses = trackerService.findTrackerStatus(trackerId);
        return ResponseEntity.ok(trackerStatuses);
    }


    @Parameter(name="创建一个工作项定义")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<TrackerVo> createTracker(@RequestBody TrackerVo trackerVo) {
        TrackerVo result = trackerService.createTracker(trackerVo);
        return ResponseEntity.ok(result);
    }


    @Parameter(name="保存工作项工作项步骤")
    @RequestMapping(value = "/{trackerId}/stateTransitions", method = RequestMethod.PUT)
    ResponseEntity<Void> updateTrackerStateTransitions(
            @PathVariable(value = "trackerId", required = true) Long trackerId,
            @RequestBody List<TrackerStateTransition> trackerStateTransitions) {

        trackerService.updateTrackerStateTransitions(trackerId, trackerStateTransitions);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询工作项工作项步骤")
    @RequestMapping(value = "/{trackerId}/stateTransitions", method = RequestMethod.GET)
    ResponseEntity<List<TrackerStateTransition>> findTrackerStateTransitions(
            @PathVariable(value = "trackerId", required = true) Long trackerId) {

        List<TrackerStateTransition> trackerStateTransitions = trackerService.findTrackerStateTransitions(trackerId);
        return ResponseEntity.ok(trackerStateTransitions);
    }

    @Parameter(name="创建工作项步骤")
    @RequestMapping(value = "/{trackerId}/transition", method = RequestMethod.POST)
    ResponseEntity<TrackerStateTransition> createTrackerStateTransition(
            @PathVariable(value = "trackerId", required = true) Long trackerId,
            @RequestBody TrackerStateTransition trackerStateTransition) {

        TrackerStateTransition result = trackerService.createTrackerStateTransition(trackerId, trackerStateTransition);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="保存工作项步骤")
    @RequestMapping(value = "/{trackerId}/transition", method = RequestMethod.PUT)
    ResponseEntity<Void> updateTrackerStateTransition(
            @PathVariable(value = "trackerId", required = true) Long trackerId,
            @RequestBody TrackerStateTransition trackerStateTransition) {

        trackerService.updateTrackerStateTransition(trackerId, trackerStateTransition);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="重命名工作项步骤")
    @RequestMapping(value = "/{trackerId}/transition/rename", method = RequestMethod.POST)
    ResponseEntity<Void> renameTrackerStateTransition(
            @PathVariable(value = "trackerId", required = true) Long trackerId,
            @RequestBody TrackerStateTransition trackerStateTransition) {

        trackerService.renameTrackerStateTransition(trackerId, trackerStateTransition);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="授权工作项步骤执行权限")
    @RequestMapping(value = "/{trackerId}/transition/grant", method = RequestMethod.POST)
    ResponseEntity<Void> grantTrackerStateTransitionPermitted(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                            @RequestParam Long transitionId, @RequestBody BaseIdentity identity) {
        trackerService.grantTrackerStateTransitionPermitted(trackerId, transitionId, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="取消工作项步骤执行权限")
    @RequestMapping(value = "/{trackerId}/transition/unGrant", method = RequestMethod.POST)
    ResponseEntity<Void> unGrantTrackerStateTransitionPermitted(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                            @RequestParam Long transitionId, @RequestBody BaseIdentity identity) {
        trackerService.unGrantTrackerStateTransitionPermitted(trackerId, transitionId, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除工作项步骤")
    @RequestMapping(value = "/{trackerId}/transition", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteTrackerStateTransition(
            @PathVariable(value = "trackerId", required = true) Long trackerId,
            @RequestBody TrackerStateTransition trackerStateTransition) {

        trackerService.deleteTrackerStateTransition(trackerId, trackerStateTransition);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询所有默认工作项系统事件通知")
    @RequestMapping(value = "/notifications/system/default", method = RequestMethod.GET)
    ResponseEntity<List<String>> findSystemEventDefault() {
        List<String> result = NotificationEvents.ALL_EVENTS;
        return ResponseEntity.ok(result);
    }

    @Parameter(name="订阅工作项系统事件通知")
    @RequestMapping(value = "/{trackerId}/notifications/system/subscribe", method = RequestMethod.POST)
    ResponseEntity<Void> subscribeSystemEvent(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                              @RequestParam String eventName, @RequestBody BaseIdentity identity) {
        trackerService.subscribeSystemEvent(trackerId, eventName, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="取消订阅工作项系统事件通知")
    @RequestMapping(value = "/{trackerId}/notifications/system/unsubscribe", method = RequestMethod.POST)
    ResponseEntity<Void> unSubscribeSystemEvent(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                @RequestParam String eventName, @RequestBody BaseIdentity identity) {
        trackerService.unSubscribeSystemEvent(trackerId, eventName, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="订阅工作项自定义事件通知")
    @RequestMapping(value = "/{trackerId}/notifications/customer/subscribe", method = RequestMethod.POST)
    ResponseEntity<Void> subscribeCustomerEvent(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                @RequestParam Long fieldId, @RequestBody BaseIdentity identity) {
        trackerService.subscribeCustomerEvent(trackerId, fieldId, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="取消订阅工作项自定义事件通知")
    @RequestMapping(value = "/{trackerId}/notifications/customer/unsubscribe", method = RequestMethod.POST)
    ResponseEntity<Void> unSubscribeCustomerEvent(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                  @RequestParam Long fieldId, @RequestBody BaseIdentity identity) {
        trackerService.unSubscribeCustomerEvent(trackerId, fieldId, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="创建工作项自定义事件订阅通知")
    @RequestMapping(value = "/{trackerId}/notifications/customer/", method = RequestMethod.POST)
    ResponseEntity<Void> createCustomerNotification(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                    @RequestBody CustomerTrackerNotification notification) {
        trackerService.createCustomerNotification(trackerId, notification);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除工作项自定义事件订阅通知")
    @RequestMapping(value = "/{trackerId}/notifications/customer/", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteCustomerNotification(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                    @RequestBody CustomerTrackerNotification notification) {
        trackerService.deleteCustomerNotification(trackerId, notification);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="订阅工作项全部自定义事件通知")
    @RequestMapping(value = "/{trackerId}/notifications/customer/all/subscribe", method = RequestMethod.POST)
    ResponseEntity<Void> subscribeAllCustomerEvent(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                @RequestBody BaseIdentity identity) {
        trackerService.subscribeAllCustomerEvent(trackerId, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="取消订阅工作项全部自定义事件通知")
    @RequestMapping(value = "/{trackerId}/notifications/customer/all/unsubscribe", method = RequestMethod.POST)
    ResponseEntity<Void> unSubscribeAllCustomerEvent(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                     @RequestBody BaseIdentity identity) {
        trackerService.unSubscribeAllCustomerEvent(trackerId, identity);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新工作项系统事件通知方式")
    @RequestMapping(value = "/{trackerId}/notifications/system/noticeType", method = RequestMethod.POST)
    ResponseEntity<Void> updateSystemEventNoticeType(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                     @RequestParam(required = false) String eventName,@RequestBody SystemTrackerNotification systemTrackerNotification) {
        trackerService.updateSystemEventNoticeType(trackerId, eventName, systemTrackerNotification);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新工作项指定自定义事件通知方式")
    @RequestMapping(value = "/{trackerId}/notifications/customer/noticeType", method = RequestMethod.POST)
    ResponseEntity<Void> updateCustomerEventNoticeType(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                       @RequestParam(required = false) Long fieldId,@RequestBody CustomerTrackerNotification customerTrackerNotification) {
        trackerService.updateCustomerEventNoticeType(trackerId, fieldId, customerTrackerNotification);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新工作项全部自定义事件通知方式")
    @RequestMapping(value = "/{trackerId}/notifications/customer/all/noticeType", method = RequestMethod.POST)
    ResponseEntity<Void> updateAllCustomerEventNoticeType(@PathVariable(value = "trackerId", required = true) Long trackerId,
                                                          @RequestBody CustomerTrackerNotification customerTrackerNotification) {
        trackerService.updateAllCustomerEventNoticeType(trackerId, customerTrackerNotification);
        return ResponseEntity.ok().build();
    }

//    @Parameter(name="查询工作项自定义属性权限")
//    @RequestMapping(value = "/{trackerId}/fields/customer/permission", method = RequestMethod.GET)
//    ResponseEntity<TrackerFieldsPermissions> findTrackerItemCustomFieldPerms(@PathVariable(value = "trackerId", required = true) Long trackerId,
//                                                          @RequestParam Long projectId) {
//        TrackerFieldsPermissions trackerItemCustomFieldPerms = trackerService.findTrackerItemCustomFieldPerms(projectId, trackerId);
//        return ResponseEntity.ok(trackerItemCustomFieldPerms);
//    }

}
