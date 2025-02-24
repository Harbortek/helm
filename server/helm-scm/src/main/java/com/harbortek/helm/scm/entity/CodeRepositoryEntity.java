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

package com.harbortek.helm.scm.entity;

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
@Table(value = "code_repositories")
public class CodeRepositoryEntity extends BaseEntity {
    String type;
    String applicationId;
    String secret;
    String code;
    String host;
    String redirectUri;

    String accessToken;
    String tokenType;
    Integer expiresIn;
    String refreshToken;
    Long createdAt;

    String grantUserName;

    String webHookUrl;
    String webHookToken;
}
