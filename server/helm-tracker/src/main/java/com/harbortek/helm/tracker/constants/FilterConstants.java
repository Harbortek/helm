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

public interface FilterConstants {
    String FILTER_METHOD_CONDITION = "CONDITION";
    String FILTER_METHOD_ENUM = "ENUM";
    String FILTER_METHOD_SINGLE_DATE = "SINGLE_DATE";
    String FILTER_METHOD_DATE_RANGE = "DATE_RANGE";

    String RANGE_TYPE_START = "START";
    String RANGE_TYPE_END = "END";
    String RANGE_TYPE_BOTH = "BOTH";

    String CONDITION_METHOD_SINGLE = "SINGLE";
    String CONDITION_METHOD_MULTI = "MULTI";
    String CONDITION_METHOD_RANGE = "RANGE";
    String CONDITION_METHOD_AND = "AND";
    String CONDITION_METHOD_OR = "OR";

    String QUERY_METHOD_SINGLE = "SINGLE";
    String QUERY_METHOD_MULTI = "MULTI";


    String MATCH_METHOD_STRICT_MATCH= "STRICT_MATCH";
    String MATCH_METHOD_NOT_MATCH= "NOT_MATCH";
    String MATCH_METHOD_CONTAINS= "CONTAINS";
    String MATCH_METHOD_NOT_CONTAINS= "NOT_CONTAINS";
    String MATCH_METHOD_START_WITH= "START_WITH";
    String MATCH_METHOD_END_WITH= "END_WITH";
    String MATCH_METHOD_IS_NULL= "IS_NULL";
    String MATCH_METHOD_IS_NOT_NULL= "IS_NOT_NULL";
    String MATCH_METHOD_IS_EMPTY= "IS_EMPTY";
    String MATCH_METHOD_IS_NOT_EMPTY= "IS_NOT_EMPTY";
    String MATCH_METHOD_EQUAL= "EQUAL";
    String MATCH_METHOD_NOT_EQUAL= "NOT_EQUAL";
    String MATCH_METHOD_GREATER_THAN= "GREATER_THAN";
    String MATCH_METHOD_GREATER_THAN_OR_EQUAL= "GREATER_THAN_OR_EQUAL";
    String MATCH_METHOD_LESS_THAN= "LESS_THAN";
    String MATCH_METHOD_LESS_THAN_OR_EQUAL= "LESS_THAN_OR_EQUAL";

    String RELATIVE_UNIT_DAY = "DAY";
    String RELATIVE_UNIT_MONTH = "MONTH";
    String RELATIVE_UNIT_YEAR = "YEAR";

    String RELATIVE_DIRECTION_BEFORE = "-";
    String RELATIVE_DIRECTION_AFTER = "+";

}
