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

package com.harbortek.helm.tracker.constants;

import java.util.Arrays;
import java.util.List;

public interface SystemFields {

    String PARENT = "parent";
    String ITEM_NO = "itemNo";

    String NAME = "name";

    String DESCRIPTION = "description";

    String OWNER = "ownerId";
    String SPRINT = "sprintId";

    String CREATE_BY = "createBy";

    String CREATE_DATE = "createDate";


    String LAST_MODIFIED_BY = "lastModifiedBy";

    String LAST_MODIFIED_DATE = "lastModifiedDate";

    String STATUS = "status";

    String STATUS_TYPE = "meaning";

    String ASSIGNED_TO = "assignedToId";

    String ASSIGNED_DATE = "assignedDate";

    String PROJECT = "projectId";

    String TRACKER = "trackerId";

    String PRIORITY = "priority";

    /**
     * 计划开始时间
     */
    String PLAN_START_DATE = "planStartDate";

    /**
     * 计划结束时间
     */
    String PLAN_END_DATE = "planEndDate";

    /**
     * 实际开始时间
     */
    String REAL_START_DATE = "realStartDate";
    /**
     * 实际结束时间
     */
    String REAL_END_DATE = "realEndDate";

    /**
     * 进度
     */
    String PROGRESS = "progress";

    /**
     * 关闭时间
     */
    String CLOSE_DATE = "closeDate";

    /**
     * 预计花费工时
     */
    String ESTIMATE_WORKING_HOURS = "estimateWorkingHours";
    /**
     * 已登记工时
     */
    String REGISTERED_WORKING_HOURS = "registeredWorkingHours";

    /**
     * 剩余工时
     */
    String REMAINING_WORKING_HOURS = "remainingWorkingHours";

    /**
     * 测试用例类型
     */
//    String TEST_CASE_TYPE = "testCaseTypeId";
//
//    String PRECONDITION = "precondition";

    String SEVERITY = "severity";
    String DUE_DATE = "dueDate";

    String RESOLUTION = "resolution";

    String RESOLVED_ON = "resolvedOn";


    List<String> ALL_SYSTEM_FIELDS = Arrays.asList(
//            PARENT,
            ITEM_NO,
            NAME,
            DESCRIPTION,
            OWNER,
            SPRINT,
            CREATE_BY,
            CREATE_DATE,
            LAST_MODIFIED_BY,
            LAST_MODIFIED_DATE,
            STATUS,
            STATUS_TYPE,
            ASSIGNED_TO,
            ASSIGNED_DATE,
            SEVERITY,
            DUE_DATE,
            PROJECT,
            TRACKER,
            PRIORITY,
            SEVERITY,
            PLAN_START_DATE,
            PLAN_END_DATE,
            REAL_START_DATE,
            REAL_END_DATE,
            PROGRESS,
            CLOSE_DATE,
            RESOLUTION,
            RESOLVED_ON,
            ESTIMATE_WORKING_HOURS,
            REGISTERED_WORKING_HOURS,
            REMAINING_WORKING_HOURS
    );


}
