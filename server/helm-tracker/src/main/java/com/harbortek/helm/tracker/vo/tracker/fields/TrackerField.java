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

/*
 * 项目名称:浩博ELM协同平台
 * Copyright 南京浩博科技有限公司 2023 版权所有
 *
 */

package com.harbortek.helm.tracker.vo.tracker.fields;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.tracker.anotation.FieldType;
import com.harbortek.helm.tracker.rule.aggregation.AggregationRule;
import com.harbortek.helm.tracker.rule.distribution.DistributionRule;
import com.harbortek.helm.tracker.vo.tracker.permissions.FieldPermission;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import lombok.Builder;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "inputType", visible = true)
public abstract class TrackerField extends BaseVo {
    String inputType;

    @Builder.Default
    Boolean system = Boolean.FALSE;
    String systemProperty;

    Boolean showInLayout;

    String title;

    Boolean hidden;
    Boolean showInList;

    Boolean omitSuspectedWhenChanging;
    Boolean omitMerge;

    Boolean mandatory;
    List<IdNameReference<TrackerStatus>> exceptStatus = new ArrayList<>();

    FieldPermission permission;


    DistributionRule distributionRule;
    AggregationRule aggregationRule;

    String hideIfExpression;
    String computedAsExpression;

    Object defaultValue;


    public String getInputType() {
        JsonTypeName type = this.getClass().getAnnotation(JsonTypeName.class);
        return type != null ? type.value() : null;
    }

    public boolean isChoiceField() {
        return (this instanceof ChoiceField) || (this instanceof TableField);
    }

    public boolean isCalcField() {
        return (this instanceof TrackerCalcField);
    }

    public boolean isSystem() {return this.system;}

}
