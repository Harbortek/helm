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

package com.harbortek.helm.tracker.constants;

import java.util.Arrays;
import java.util.List;

public interface RoleCodes {
	String SUPER_ADMIN = "SUPER_ADMIN";
	//团队负责人
	String PROJECT_ADMIN = "PROJECT_ADMIN";
	//团队成员
	String PROJECT_MEMBER = "PROJECT_MEMBER";

	public final static List<String> ALL_ROLE_CODES = Arrays.asList(
			SUPER_ADMIN,PROJECT_ADMIN,PROJECT_MEMBER
	);

}
