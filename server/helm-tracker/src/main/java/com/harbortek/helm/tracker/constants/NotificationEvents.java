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

public interface NotificationEvents {
    String CREATE_ITEM = "CREATE_ITEM";

    String CHANGE_ITEM_OWNER = "CHANGE_ITEM_OWNER";

    String CHANGE_ITEM_STATUS = "CHANGE_ITEM_STATUS";

    String CHANGE_ITEM_PRIORITY = "CHANGE_ITEM_PRIORITY";

    String CHANGE_ITEM_TITLE = "CHANGE_ITEM_TITLE";


    String CHANGE_ITEM_DESCRIPTION = "CHANGE_ITEM_DESCRIPTION";

    String CREATE_COMMENTS = "CREATE_COMMENTS";

    String CHANGE_ITEM_WATCHER = "CHANGE_ITEM_WATCHER";

    String CHANGE_ITEM_ASSOCIATIONS = "CHANGE_ITEM_ASSOCIATIONS";

    String CHANGE_ITEM_ATTACHMENT = "CHANGE_ITEM_ATTACHMENT";

    String CHANGE_ITEM_PROGRESS = "CHANGE_ITEM_PROGRESS";
    String CHANGE_ITEM_ESTIMATE_WORKING_HOURS = "CHANGE_ITEM_ESTIMATE_WORKING_HOURS";
    String CHANGE_ITEM_REGISTERED_WORKING_HOURS = "CHANGE_ITEM_REGISTERED_WORKING_HOURS";
    String CHANGE_ITEM_REMAINING_WORKING_HOURS = "CHANGE_ITEM_REMAINING_WORKING_HOURS";
    String CHANGE_ITEM_PLAN_START_DATE = "CHANGE_ITEM_PLAN_START_DATE";
    String CHANGE_ITEM_PLAN_END_DATE = "CHANGE_ITEM_PLAN_END_DATE";
    String CHANGE_ITEM_WIKI = "CHANGE_ITEM_WIKI";
    String CHANGE_ITEM_CUSTOMER_PROPERTY = "CHANGE_ITEM_CUSTOMER_PROPERTY";
    String CHANGE_ITEM_SPRINT = "CHANGE_ITEM_SPRINT";

    List<String> ALL_EVENTS = Arrays.asList(
            CREATE_ITEM,
            CHANGE_ITEM_OWNER,
            CHANGE_ITEM_STATUS,
            CHANGE_ITEM_PRIORITY,
            CHANGE_ITEM_TITLE,
            CHANGE_ITEM_DESCRIPTION,
            CREATE_COMMENTS,
            CHANGE_ITEM_WATCHER,
            CHANGE_ITEM_ASSOCIATIONS,
            CHANGE_ITEM_ATTACHMENT,
            CHANGE_ITEM_PROGRESS,
            CHANGE_ITEM_ESTIMATE_WORKING_HOURS,
            CHANGE_ITEM_REGISTERED_WORKING_HOURS,
            CHANGE_ITEM_REMAINING_WORKING_HOURS,
            CHANGE_ITEM_PLAN_START_DATE,
            CHANGE_ITEM_PLAN_END_DATE,
            CHANGE_ITEM_WIKI,
            CHANGE_ITEM_SPRINT
    );

}
