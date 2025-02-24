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

package com.harbortek.helm.smartpage.components;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class ComponentRegistry implements ApplicationContextAware, SmartInitializingSingleton {

    ApplicationContext applicationContext;
    private static final Map<String, Component> componentMap =  new HashMap<>();

    public static Component getComponent(String name){
        return componentMap.get(name);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (applicationContext == null) {
            return;
        }
        String[] beanNames =  applicationContext.getBeanNamesForType(Component.class);
        for (String beanName : beanNames) {
            Component component = applicationContext.getBean(beanName, Component.class);
            componentMap.put(component.getName(), component);
        }
    }
}
