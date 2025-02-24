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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.harbortek.helm.tracker.constants.FieldTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
@JsonTypeName(value = FieldTypes.CHOICE)
public class ChoiceField extends TrackerField{

	public final static String DATASOURCE_OPTIONS = "OPTIONS";
	public final static String DATASOURCE_USERS = "USERS";
	public final static String DATASOURCE_PROJECTS = "PROJECTS";
	public final static String DATASOURCE_TRACKERS = "TRACKERS";
	public final static String DATASOURCE_WORK_ITEMS = "WORK_ITEMS";
	public final static String DATASOURCE_REPOSITORIES = "REPOSITORIES";
	public final static String DATASOURCE_TABLE = "TABLE";
	public final static String DATASOURCE_REST = "REST";

	String dataSource = DATASOURCE_OPTIONS;


	Boolean multiple;
}
