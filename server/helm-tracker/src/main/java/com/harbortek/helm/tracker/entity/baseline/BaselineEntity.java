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

package com.harbortek.helm.tracker.entity.baseline;

import com.harbortek.helm.common.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@FieldNameConstants
@Table(value = "baselines")
public class BaselineEntity extends BaseEntity {
    Long projectId;

    String type;

    Long collectionId;

    /**
     * 文档历史ID
     */
    @Builder.Default
    List<Long> documentHistoryIds = new ArrayList<>();

    /**
     * 工作项历史ID
     */
    @Builder.Default
    List<Long> trackerItemHistoryIds = new ArrayList<>();

}
