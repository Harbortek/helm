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

package com.harbortek.helm.common.config.module;

import com.harbortek.helm.common.vo.PermissionDefinition;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ModuleManager implements ApplicationContextAware, SmartInitializingSingleton {

	private ApplicationContext applicationContext;
	private final List<ModuleConfiguration> modules =  new ArrayList<>();

	@Override
	public void afterSingletonsInstantiated() {
		if (applicationContext == null) {
			return;
		}
		// init modules
		Map<String, Object> modulesBeanMap = applicationContext.getBeansWithAnnotation(Module.class);
		if (modulesBeanMap.size() > 0) {
			for (Object moduleBean : modulesBeanMap.values()) {
				if (moduleBean instanceof ModuleConfiguration) {
					ModuleConfiguration module = (ModuleConfiguration) moduleBean;
					modules.add(module);
				}
			}
		}
	}

	@Override
	public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public List<PermissionDefinition> getGlobalPermissionDefinitions(){
        List<PermissionDefinition> permissions = new ArrayList<>();
        for (ModuleConfiguration module : modules) {
            permissions.addAll(module.getGlobalPermissionDefinitions());
        }
        return permissions;
    }

	public List<PermissionLoader> getPermissionLoaders(){
		List<PermissionLoader> loaders = new ArrayList<>();
		for (ModuleConfiguration module : modules) {
			List<PermissionLoader> ls = module.getPermissionLoaders();
			if (ls!=null) {
				loaders.addAll(ls);
			}
		}
		return loaders;
	}

}
