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

package com.harbortek.helm.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harbortek.helm.common.vo.BaseVo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserVo extends BaseVo {
    String loginName;

    @JsonIgnore
    String password;

    Date lastLogin;

    String userCode;

    String email;

    String mobilePhone;

    String avatar;
    Long orgId;
    String wechatOpenId;
    String dingtalkOpenId;
    String feiShuOpenId;
    String language = "zh_CN";
    String remoteAddress;

    Boolean disabled;

    @Builder.Default
    List<RoleVo> roles = new ArrayList<>();

    List<GrantedPermission> permissions;
}
