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

package com.harbortek.helm.common.constants;

import com.harbortek.helm.system.config.SystemModulePermissions;

public enum Roles {


	ADMIN("管理员", new String[]{
            SystemModulePermissions.PERSONAL_SETTINGS,
            }, new String[]{
            SystemModulePermissions.PERSONAL_SETTINGS,
            }),

	ENGINEER("工程师", new String[]{
            SystemModulePermissions.PERSONAL_SETTINGS
	}, new String[]{});

	private String roleName;
	private String[] authorized;
	private String[] mayAuthorize;

	Roles(String roleName, String[] authorized, String[] mayAuthorize) {
		this.roleName = roleName;
		this.authorized = authorized;
		this.mayAuthorize = mayAuthorize;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String[] getAuthorized() {
		return authorized;
	}

	public void setAuthorized(String[] authorized) {
		this.authorized = authorized;
	}

	public String[] getMayAuthorize() {
		return mayAuthorize;
	}

	public void setMayAuthorize(String[] mayAuthorize) {
		this.mayAuthorize = mayAuthorize;
	}
}
