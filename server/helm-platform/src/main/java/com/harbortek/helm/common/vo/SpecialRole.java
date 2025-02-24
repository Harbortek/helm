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

package com.harbortek.helm.common.vo;

public interface SpecialRole {
    String PROJECT_OWNER = "PROJECT_OWNER";
    String PROJECT_ALL_MEMBERS = "PROJECT_ALL_MEMBERS";
    String ALL_USERS = "ALL_USERS";
    String TRACKER_OWNER = "TRACKER_OWNER";
    String TRACKER_CREATOR = "TRACKER_CREATOR";
    String TRACKER_WATCHER = "TRACKER_WATCHER";

    String PRODUCT_LINE_OWNER = "PRODUCT_LINE_OWNER";


    String SCOPE_PRODUCT_LINE = "SCOPE_PRODUCT_LINE";


    String SCOPE_GLOBAL = "SCOPE_GLOBAL";

    String SCOPE_PROJECT = "SCOPE_PROJECT";
    String SCOPE_TRACKER = "SCOPE_TRACKER";

}
