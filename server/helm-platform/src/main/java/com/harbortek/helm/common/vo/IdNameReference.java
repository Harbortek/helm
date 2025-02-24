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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harbortek.helm.common.entity.IdName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;

@SuperBuilder
@Data
@EqualsAndHashCode
@FieldNameConstants
public class IdNameReference<T extends IdName> implements IdName {
    @JsonIgnore
    @Transient
    private T referTo;

    public IdNameReference() {
        referTo = (T) IdNameVo.builder().build();
    }

    public IdNameReference(T target) {
        this.referTo = target;
    }

    @Override
    public Long getId() {
        return referTo.getId();
    }

    @Override
    public void setId(Long id) {
        referTo.setId(id);
    }

    @Override
    public String getName() {
        return referTo.getName();
    }

    @Override
    public void setName(String name) {
        referTo.setName(name);
    }

    @Override
    public String getDescription() {
        return referTo.getDescription();
    }

    @Override
    public void setDescription(String description) {
        referTo.setDescription(description);
    }

    @Override
    public String getIcon() {
        return referTo.getIcon();
    }

    @Override
    public void setIcon(String icon) {
        referTo.setIcon(icon);
    }
}
