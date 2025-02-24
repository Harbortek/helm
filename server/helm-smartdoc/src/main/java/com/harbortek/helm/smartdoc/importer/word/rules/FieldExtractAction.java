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

package com.harbortek.helm.smartdoc.importer.word.rules;

import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FieldExtractAction {
    Long fieldId;

    TrackerField field;

    String actionType;

    String argument;

    String argumentType;
    String conditionsMatchType;

    List<ConditionWithArgument> conditions = new ArrayList<>();
    Map<String,String> columnMap = new HashMap<>();

}
