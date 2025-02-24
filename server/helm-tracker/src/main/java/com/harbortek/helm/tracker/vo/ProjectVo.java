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

package com.harbortek.helm.tracker.vo;

import com.harbortek.helm.common.annotation.EntityInclude;
import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.system.vo.UserVo;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import com.harbortek.helm.tracker.vo.tracker.TrackerVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
public class ProjectVo extends BaseVo {
	private String keyName;
	@EntityInclude({"id","name","email","icon"})
	private UserVo owner;

	private String templateName;

	private Date startDate;
	private Date endDate;

	private Integer progress;

	private EnumItemVo category;

	private EnumItemVo status;

	private Integer version = 1;

	List<ProjectRoleMemberVo> roleMembers = new ArrayList<>();

	List<ProjectPageVo> pages = new ArrayList<>();

	List<TrackerVo> trackers = new ArrayList<>();

}
