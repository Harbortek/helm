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

package com.harbortek.helm.tracker.vo.smartpage;

import com.harbortek.helm.common.vo.BaseVo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
public class DatasetField extends BaseVo {
    String field;
    String title;
    String type;
    @Builder.Default
    String sort = "none";
    @Builder.Default
    String dateStyle = "y_M_d";
    String datePattern = "";
    String dateFill = "none";
    String formatterType;

    // 聚合方式
    String summary="sum";

    // 快速计算
    String quickCalc="none";
}
