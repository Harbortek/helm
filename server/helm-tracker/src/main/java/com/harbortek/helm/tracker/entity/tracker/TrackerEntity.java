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

package com.harbortek.helm.tracker.entity.tracker;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.harbortek.helm.common.annotation.EntityReference;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.system.entity.EnumItemEntity;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.layout.TrackerLayout;
import com.harbortek.helm.tracker.vo.tracker.nofitication.TrackerNotification;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransition;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.ValueConverter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
@NoArgsConstructor
@Table(value = "trackers")
public class TrackerEntity extends BaseEntity {

	@EntityReference(EnumItemEntity.class)
	Long trackerTypeId;

	Long projectId;

	Integer ordinary;


	Long groupId;

	String defaultLayout;

//	@Builder.Default
//	List<TrackerRolePermission> trackerPermissions = new ArrayList<>();

	@Builder.Default
	List<TrackerStatus> trackerStatuses = new ArrayList<>();
	@Builder.Default
	List<TrackerStateTransition> trackerStateTransitions = new ArrayList<>();
	List<TrackerField> trackerFields = new ArrayList<>();
	@Builder.Default
	List<TrackerLayout> trackerLayouts = new ArrayList<>();

	TrackerNotification trackerNotification;


}
