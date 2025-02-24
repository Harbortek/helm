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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;

@Data
@SuperBuilder
@EqualsAndHashCode
@FieldNameConstants
public class BaseIdentity implements IdName {
    @JsonIgnore
    @Transient
    IdNameReference<?> referTo;

    String type; //角色, 项目成员, 特殊角色 当type是角色的时候，id就是角色id，当type是项目成员时，id就是用户id

    @Builder.Default
    Integer count = 0;

    public BaseIdentity(){
        referTo = new IdNameReference<>();
    }
    public BaseIdentity(IdNameReference<?> target){
        this.referTo = target;
    }

    public Long getId() {
        return referTo.getId();
    }
    public void setId(Long id){
        referTo.setId(id);
    }

    public String getName() {
        return referTo.getName();
    }

    public void setName(String name) {
        referTo.setName(name);
    }

    public String getDescription() {
        return referTo.getDescription();
    }

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
