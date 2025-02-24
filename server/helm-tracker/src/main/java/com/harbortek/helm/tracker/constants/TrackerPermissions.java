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

/*
 * 项目名称:浩博ELM协同平台
 * Copyright 南京浩博科技有限公司 2023 版权所有
 *
 */

package com.harbortek.helm.tracker.constants;

import java.util.Arrays;
import java.util.List;

public interface TrackerPermissions {
    String ITEM_VIEW = "ITEM_VIEW";
    String ITEM_CREATE = "ITEM_CREATE";
    String ITEM_EDIT = "ITEM_EDIT";

    String ITEM_CHANGE_STATUS = "ITEM_CHANGE_STATUS";
    String ITEM_DELETE = "ITEM_DELETE";
    String ITEM_MANAGE_WATCHER = "ITEM_MANAGE_WATCHER";

    String ITEM_EDIT_ASSOCIATIONS = "ITEM_EDIT_ASSOCIATIONS";
    String ITEM_EXPORT = "ITEM_EXPORT";

    String ITEM_MANAGE_ESTIMATE_WORK_HOURS = "ITEM_MANAGE_ESTIMATE_WORK_HOURS";

    String ITEM_MANAGE_ALL_REGISTERED_WORK_HOURS = "ITEM_MANAGE_ALL_REGISTERED_WORK_HOURS";

    String ITEM_MANAGE_OWN_REGISTERED_WORK_HOURS = "ITEM_MANAGE_OWN_REGISTERED_WORK_HOURS";

    String FIELD_EDIT = "FIELD_EDIT";

    List<String> TRACKER_PERMISSIONS = Arrays.asList(
            ITEM_CREATE,
            ITEM_VIEW,
            ITEM_EDIT,
            ITEM_DELETE,
            ITEM_CHANGE_STATUS,
            ITEM_MANAGE_WATCHER,
            ITEM_EDIT_ASSOCIATIONS,
            ITEM_EXPORT,
            ITEM_MANAGE_ESTIMATE_WORK_HOURS,
            ITEM_MANAGE_ALL_REGISTERED_WORK_HOURS,
            ITEM_MANAGE_OWN_REGISTERED_WORK_HOURS);

}
