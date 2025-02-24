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

package com.harbortek.helm.tracker.util;

import com.harbortek.helm.tracker.constants.Associations;
import com.harbortek.helm.tracker.constants.SystemFields;
import com.harbortek.helm.tracker.entity.link.LinkTypeRule;
import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.vo.link.TrackerLinkTypeVo;
import com.harbortek.helm.tracker.vo.tracker.fields.*;
import com.harbortek.helm.tracker.vo.tracker.nofitication.CustomerTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.nofitication.SystemTrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransition;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import com.harbortek.helm.util.IDUtils;

import java.util.*;

public class TrackerUtils {
    public static List<TrackerField> buildSystemFields() {
        List<TrackerField> systemFields = Arrays.asList(
                WorkItemField.builder().name("父工作项").systemProperty(SystemFields.PARENT).build(),
                WorkItemNoField.builder().name("工作项编号").systemProperty(SystemFields.ITEM_NO).build(),
                TextField.builder().name("标题").systemProperty(SystemFields.NAME).build(),
                WikiTextField.builder().name("描述").systemProperty(SystemFields.DESCRIPTION).build(),
                OptionsField.builder().name("优先级").systemProperty(SystemFields.PRIORITY).build(),
                OptionsField.builder().name("严重级别").systemProperty(SystemFields.SEVERITY).build(),
                UserField.builder().name("负责人").systemProperty(SystemFields.OWNER).build(),
                UserField.builder().name("创建者").systemProperty(SystemFields.CREATE_BY).build(),
                DateField.builder().name("创建日期").systemProperty(SystemFields.CREATE_DATE).build(),
                UserField.builder().name("修改者").systemProperty(SystemFields.LAST_MODIFIED_BY).build(),
                DateField.builder().name("修改日期").systemProperty(SystemFields.LAST_MODIFIED_DATE).build(),
                StatusField.builder().name("状态").systemProperty(SystemFields.STATUS).build(),
                StatusTypeField.builder().name("状态类型").systemProperty(SystemFields.STATUS_TYPE).build(),
                UserField.builder().name("分配给").systemProperty(SystemFields.ASSIGNED_TO).build(),
                DateField.builder().name("分配日期").systemProperty(SystemFields.ASSIGNED_DATE).build(),
                DateField.builder().name("截止日期").systemProperty(SystemFields.DUE_DATE).build(),
                DateField.builder().name("解决方案").systemProperty(SystemFields.RESOLUTION).build(),
                DateField.builder().name("解决日期").systemProperty(SystemFields.RESOLVED_ON).build(),
                ProjectField.builder().name("所属项目").systemProperty(SystemFields.PROJECT).build(),
                WorkItemTypeField.builder().name("所属工作项类型").systemProperty(SystemFields.TRACKER).build(),
                SprintField.builder().name("所属迭代").systemProperty(SystemFields.SPRINT).build(),
                DateField.builder().name("计划开始时间").systemProperty(SystemFields.PLAN_START_DATE).build(),
                DateField.builder().name("计划结束时间").systemProperty(SystemFields.PLAN_END_DATE).build(),
                DateField.builder().name("实际开始时间").systemProperty(SystemFields.REAL_START_DATE).build(),
                DateField.builder().name("实际结束时间").systemProperty(SystemFields.REAL_END_DATE).build(),
                IntegerField.builder().name("进度").systemProperty(SystemFields.PROGRESS).build(),
                DateField.builder().name("关闭时间").systemProperty(SystemFields.CLOSE_DATE).build(),
                IntegerField.builder().name("预计花费工时").systemProperty(SystemFields.ESTIMATE_WORKING_HOURS).build(),
                IntegerField.builder().name("已登记工时").systemProperty(SystemFields.REGISTERED_WORKING_HOURS).build(),
                IntegerField.builder().name("剩余工时").systemProperty(SystemFields.REMAINING_WORKING_HOURS).build()
//                OptionsField.builder().name("测试用例类型").systemProperty(SystemFields.TEST_CASE_TYPE).build(),
//                TextField.builder().name("测试用例前置条件").systemProperty(SystemFields.PRECONDITION).build()
                                                       );

        systemFields.forEach(f -> {
            f.setTitle(f.getName());
            f.setMandatory(true);
            f.setSystem(true);
        });
        return new ArrayList<>(systemFields);
    }

    public static void fillId(TrackerEntity tracker) {
        //1 save tracker status
        Collection<TrackerStatus> trackerStatuses = tracker.getTrackerStatuses();
        trackerStatuses.forEach(trackerStatus -> {
            trackerStatus.setId(Optional.ofNullable(trackerStatus.getId()).orElse(IDUtils.getId()));
        });
        //2 save permissions
//        Collection<TrackerRolePermission> rolePermissions = tracker.getTrackerPermissions();
//        rolePermissions.forEach(trackerRolePermission -> {
//            trackerRolePermission.setId(Optional.ofNullable(trackerRolePermission.getId()).orElse(IDUtils.getId()));
//        });
        //3 save state transitions
        Collection<TrackerStateTransition> trackerStateTransitions = tracker.getTrackerStateTransitions();
        trackerStateTransitions.forEach(trackerStateTransition -> {
            trackerStateTransition.setId(Optional.ofNullable(trackerStateTransition.getId()).orElse(IDUtils.getId()));
        });

        Collection<TrackerField> trackerFields = tracker.getTrackerFields();
        trackerFields.forEach(trackerField -> {
            trackerField.setId(Optional.ofNullable(trackerField.getId()).orElse(IDUtils.getId()));
        });


        Collection<SystemTrackerNotification> systemTrackerNotifications =
                tracker.getTrackerNotification().getSystemTrackerNotifications();
        systemTrackerNotifications.forEach(trackerNotification -> {
            trackerNotification.setId(Optional.ofNullable(trackerNotification.getId()).orElse(IDUtils.getId()));
        });

        Collection<CustomerTrackerNotification> customerTrackerNotifications =
                tracker.getTrackerNotification().getCustomerTrackerNotifications();
        customerTrackerNotifications.forEach(trackerNotification -> {
            trackerNotification.setId(Optional.ofNullable(trackerNotification.getId()).orElse(IDUtils.getId()));
        });
    }

    public static List<TrackerLinkTypeVo> buildDefaultTrackerLinkTypes() {
        int index = 0;
        return Arrays.asList(
                //parent
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("拥有父亲").oppositeName("作为父亲")
                                 .description("父子关系用于构造相同类型的工作项。").ordinary(index++)
                                 .system(Boolean.TRUE)
                                 .rules(Arrays.asList(LinkTypeRule.builder().sameType(Boolean.TRUE).build()))
                                 .parent(Boolean.TRUE).code(Associations.PARENT.getId()).build(),
                //implements
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("实现了").oppositeName("被实现").description(
                                         "用于将实施任务和问题链接到工作包，并将工作包和问题链接到需求。").ordinary(index++)
                                 .system(Boolean.TRUE)
                                 .parent(Boolean.TRUE).code(Associations.IMPLEMENTS.getId()).build(),
                //refines
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("完善").oppositeName("被完善").description(
                                         "将更完善的需求链接到其父需求或变更请求。").ordinary(index++).system(Boolean.TRUE)
                                 .parent(Boolean.TRUE).code(Associations.REFINES.getId()).build(),
                //depends_on。
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("依赖于").oppositeName("阻塞于")
                                 .description("用于标记项目之间的依赖关系。").ordinary(index++).system(Boolean.TRUE)
                                 .code(Associations.DEPENDS_ON.getId()).build(),
                //duplicates
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("重复").oppositeName("被重复").description(
                                         "标记与现有相同类型项目重复的项目。").ordinary(index++).system(Boolean.TRUE)
                                 .rules(Arrays.asList(LinkTypeRule.builder().sameType(Boolean.TRUE).build()))
                                 .code(Associations.DUPLICATES.getId()).build(),
                //follow_up
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("拥有后续").oppositeName("被后续")
                                 .description("标记已完成项目的后续操作。").ordinary(index++).system(Boolean.TRUE)
                                 .code(Associations.FOLLOW_UP.getId()).build(),
                //verifies
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("验证了").oppositeName("被验证").description(
                                         "用于将验证测试用例链接到需求、工作包或问题。").ordinary(index++).system(Boolean.TRUE)
                                 .code(Associations.VERIFIES.getId()).build(),
                //assesses
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("评估了").oppositeName("被评估")
                                 .description("链接评估需求的风险项目。").ordinary(index++).system(Boolean.TRUE)
                                 .code(Associations.ASSESSES.getId()).build(),
                //triggered_by
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("由此触发").oppositeName("被触发")
                                 .description("用于将问题与触发它的测试用例链接起来。").ordinary(index++)
                                 .system(Boolean.TRUE)
                                 .code(Associations.TRIGGERED_BY.getId()).build(),

                //mitigates
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("缓解了").oppositeName("被缓解")
                                 .description("指定减轻风险的任务。").ordinary(index++).system(Boolean.TRUE)
                                 .code(Associations.MITIGATES.getId()).build(),

                //uses
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("引用").oppositeName("被引用").description(
                                         "在发布之间使用").ordinary(index++).system(Boolean.TRUE)
                                 .rules(Arrays.asList(LinkTypeRule.builder().sameType(Boolean.TRUE).build()))
                                 .code(Associations.USES.getId()).build(),

                //affects
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("影响了").oppositeName("被影响")
                                 .description("表示问题正在影响发布或需求。").ordinary(index++).system(Boolean.TRUE)
                                 .code(Associations.AFFECTS.getId()).build(),
                //tracks
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("跟踪了").oppositeName("被跟踪").description(
                                         "暗示变更请求与发布或需求之间的关系。").ordinary(index++).system(Boolean.TRUE)
                                 .code(Associations.TRACKS.getId()).build(),
                //is derived from
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("派生自").oppositeName("被派生")
                                 .description("链接派生工作项。").ordinary(index++).system(Boolean.TRUE)
                                 .code(Associations.DERIVED_FROM.getId()).build(),
                //is branched from
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("分支自").oppositeName("被分支")
                                 .description("链接分支工作项").ordinary(index++).system(Boolean.TRUE)
                                 .code(Associations.BRANCHED_FROM.getId()).build(),
                //relates to
                TrackerLinkTypeVo.builder().id(IDUtils.getId()).name("关联了").oppositeName("关联了").description(
                                         "通用关联关系.").ordinary(index++).system(Boolean.TRUE)
                                 .code(Associations.RELATES_TO.getId()).build()
                            );
    }

    public static TrackerField findTrackerField(List<TrackerField> trackerFields, Long fieldId) {
        for (TrackerField trackerField : trackerFields) {
            if (trackerField.getId().equals(fieldId)) {
                return trackerField;
            }
            if (trackerField instanceof TableField){
                TableField tableField = (TableField) trackerField;
                if (tableField.getColumns() != null) {
                    for (TrackerField column : tableField.getColumns()) {
                        if (column.getId().equals(fieldId)) {
                            return column;
                        }
                    }
                }
            }
        }
        return null;
    }
}
