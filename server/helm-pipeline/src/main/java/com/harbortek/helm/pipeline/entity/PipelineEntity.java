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

package com.harbortek.helm.pipeline.entity;

import com.harbortek.helm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@FieldNameConstants
@Table("pipelines")
public class PipelineEntity extends BaseEntity {
    /**
     * 项目 ID。
     */
    Long projectId;

    /**
     * 流水线仓库 ID。
     */
    Long repositoryId;

    /**
     * 是否是多分支流水线。
     */
    boolean multiBranch;

    /**
     * 流水线URL。
     */
    String url;
}
