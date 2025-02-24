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

public interface StateTransitionActions {
    String ACTION_TYPE_NONE = "None";
    String ACTION_TYPE_UPDATE_ITEM_PROPERTIES = "ACTION_TYPE_UPDATE_ITEM_PROPERTIES";
    String ACTION_TYPE_TRIGGER_STATE_TRANSITION = "ACTION_TYPE_TRIGGER_STATE_TRANSITION";
    String ACTION_TYPE_UPDATE_REFERRING_ITEMS = "ACTION_TYPE_UPDATE_REFERRING_ITEMS";
    String ACTION_TYPE_CREATE_NEW_UP_DOWNSTREAM_REFERRING_ITEMS =
            "ACTION_TYPE_CREATE_NEW_UP_DOWNSTREAM_REFERRING_ITEMS";
    String ACTION_TYPE_CREATE_A_NEW_SEQUENTIAL_ID = "ACTION_TYPE_CREATE_A_NEW_SEQUENTIAL_ID";
    String ACTION_TYPE_EXECUTE_A_CUSTOM_SCRIPT = "ACTION_TYPE_EXECUTE_A_CUSTOM_SCRIPT";
    String ACTION_TYPE_EXPORT_WORK_ITEM = "ACTION_TYPE_EXPORT_WORK_ITEM";
    String ACTION_TYPE_NEW_BASELINE = "ACTION_TYPE_NEW_BASELINE";
    String ACTION_TYPE_NEW_PROJECT_BASELINE = "ACTION_TYPE_NEW_PROJECT_BASELINE";
    String ACTION_TYPE_NEW_TRACKER_BASELINE = "ACTION_TYPE_NEW_TRACKER_BASELINE";
    String ACTION_TYPE_REMOVE_ATTACHMENTS_OF_THE_ITEM = "ACTION_TYPE_REMOVE_ATTACHMENTS_OF_THE_ITEM";
    String ACTION_TYPE_SEND_A_CUSTOMER_EMAIL = "ACTION_TYPE_SEND_A_CUSTOMER_EMAIL";
    String ACTION_TYPE_START_A_NEW_REVIEW = "ACTION_TYPE_START_A_NEW_REVIEW";
    String ACTION_TYPE_VALIDATE_THE_USER_SIGNATURE = "ACTION_TYPE_VALIDATE_THE_USER_SIGNATURE";
    String ACTION_TYPE_WEBHOOK = "ACTION_TYPE_WEBHOOK";

}
