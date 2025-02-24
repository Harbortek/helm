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

package com.harbortek.helm.tracker.entity.project;

import com.harbortek.helm.common.annotation.EntityReference;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.system.entity.EnumItemEntity;
import com.harbortek.helm.system.entity.UserEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
@NoArgsConstructor
@Table(value = "projects")
public class ProjectEntity extends BaseEntity {
	private String keyName;

	@Builder.Default
	private Long maxItemNo = 0L; //所有工作项在项目中统一编号

	private String templateName;

	private Date startDate;
	private Date endDate;

	private Integer progress;

	@EntityReference(UserEntity.class)
	private Long ownerId;

	@EntityReference(EnumItemEntity.class)
	private Long categoryId;

	@EntityReference(EnumItemEntity.class)
	private Long statusId;

	private String meaning;


}
