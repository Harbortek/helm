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

package com.harbortek.helm.smartdoc.constants;

public interface ConditionTypes {
    String CONTAINS_WORDS = "containsWords";
    String NOT_CONTAINS_WORDS = "notContainsWords";

    String CONTAINS_TEXT_MATCHING_REGEX = "containsTextMatchingRegex";

    String NOT_CONTAINS_TEXT_MATCHING_REGEX = "notContainsTextMatchingRegex";

    String HAS_STYLE = "hasStyle";
    String NOT_HAS_STYLE = "notHasStyle";

    String IS_EMPTY = "isEmpty";

    String IS_NOT_EMPTY = "isNotEmpty";

    String IS_HEADING = "isHeading";

    String IS_NOT_HEADING = "isNotHeading";

    String IS_LIST_ITEM = "isListItem";

    String IS_NOT_LIST_ITEM = "isNotListItem";

    String IS_TABLE = "isTable";

    String IS_NOT_TABLE = "isNotTable";
}
