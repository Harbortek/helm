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

package com.harbortek.helm.tracker.vo.tracker.fields;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.tracker.constants.FieldTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@NoArgsConstructor
@JsonTypeName(value = FieldTypes.TEST_STEP)
public class TestStepField extends TableField{
    public static final String STEP = "step";
    public static final String STEP_DESCRIPTION = "stepDescription";
    public static final String EXPECT_RESULT = "expectResult";

    public List<TrackerField> createDefaultColumns() {
        if (this.columns == null || this.columns.isEmpty()) {
            this.columns = new ArrayList<>();
            this.columns.add(TextField.builder().name("步骤").systemProperty("step").build());
            this.columns.add(TextField.builder().name("步骤描述").systemProperty("stepDescription").build());
            this.columns.add(TextField.builder().name("期望结果").systemProperty("expectResult").build());
        }
        return super.getColumns();
    }
}
