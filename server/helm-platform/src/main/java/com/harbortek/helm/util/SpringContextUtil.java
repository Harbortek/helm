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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    //获取bean工厂，用来实现动态注入bean
    //不能使用其他类加载器加载bean
    //否则会出现异常:类未找到，类未定义
    public static DefaultListableBeanFactory getBeanFactory(){
        return (DefaultListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
    }



    public static List<Map<String, Object>> getAllBean() {


        List<Map<String, Object>> list = new ArrayList<>();


        String[] beans = getApplicationContext()
                .getBeanDefinitionNames();

        for (String beanName : beans) {
            Class<?> beanType = getApplicationContext()
                    .getType(beanName);

            Map<String, Object> map = new HashMap<>();

            map.put("BeanName", beanName);
            map.put("beanType", beanType);
            map.put("package", beanType.getPackage());
            list.add(map);

        }

        return list;
    }




    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
