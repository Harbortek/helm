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

package com.harbortek.helm.tracker.config;

import com.harbortek.helm.common.config.module.Module;
import com.harbortek.helm.common.config.module.ModuleConfiguration;
import com.harbortek.helm.common.config.module.PermissionLoader;
import com.harbortek.helm.common.vo.PermissionDefinition;
import com.harbortek.helm.tracker.service.impl.ProjectPermissionLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Module
public class TrackerModuleConfiguration implements ModuleConfiguration {
	@Autowired
	TrackerMessages trackerMessages;

	@Autowired
	ProjectPermissionLoader projectPermissionLoader;

	@Override
	public List<PermissionDefinition> getGlobalPermissionDefinitions() {
		return Arrays.asList(TrackerModulePermissions.TRACKER_MODUlE_PERMISSIONS);
	}

	@Override
	public List<PermissionLoader> getPermissionLoaders() {
		return List.of(projectPermissionLoader);
	}
}
