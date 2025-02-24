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

package com.harbortek.helm.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CommonBeanFactory implements ApplicationContextAware {
    private static ApplicationContext context;

    public CommonBeanFactory() {
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
    }

    public static Object getBean(String beanName) {
        try {
            return context != null && !StringUtils.isBlank(beanName) ? context.getBean(beanName) : null;
        } catch (BeansException e) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> className) {
        try {
            return context != null && className != null ? context.getBean(className) : null;
        } catch (BeansException e) {
            return null;
        }
    }
}

