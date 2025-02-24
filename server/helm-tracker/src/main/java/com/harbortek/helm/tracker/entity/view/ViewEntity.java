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

package com.harbortek.helm.tracker.entity.view;

import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.tracker.vo.view.ViewConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
@NoArgsConstructor
@Table(value = "views")
public class ViewEntity extends BaseEntity {

    String viewType = "PRIVATE";

    Boolean display = true;
    Long objectId;

    Integer ordinary;

    Boolean system = false;

    @Column("view_config")
    ViewConfig viewConfig;

}
