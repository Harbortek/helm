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

import com.harbortek.helm.system.vo.UserVo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.Date;
@SuperBuilder
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BaseVo extends IdNameVo{
	@Builder.Default
	private Boolean deleted = Boolean.FALSE;


	private IdNameReference<UserVo> createBy;

	private Date createDate;

	private IdNameReference<UserVo> lastModifiedBy;

	private Date lastModifiedDate;
}
