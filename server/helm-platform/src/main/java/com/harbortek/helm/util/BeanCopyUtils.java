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

import com.harbortek.helm.common.entity.IdName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

@Slf4j
public class BeanCopyUtils {
    public static final void copyWithoutNullProperties(Object src, Object target) {
        try {
            Class sourceClass = src.getClass();
            Class targetClass = target.getClass();
            List<String> sourceProperties = DataUtils.getAllProperties(sourceClass);
            List<String> targetProperties = DataUtils.getAllProperties(targetClass);
            List<String> referenceProperties = DataUtils.getReferenceProperties(targetClass);

            for (String targetProperty : targetProperties) {
                if (sourceProperties.contains(targetProperty)) { //如果源头也有同样的属性
                    String sourceProperty = targetProperty;
                    copyProperty(src, target, sourceClass, targetClass, referenceProperties, sourceProperty,targetProperty);
                }else if (targetProperty.endsWith("Id")){
                    String sourceProperty = StringUtils.removeEnd(targetProperty,"Id");
                    if (sourceProperties.contains(sourceProperty)){
                        copyProperty(src, target, sourceClass, targetClass, referenceProperties, sourceProperty,targetProperty);
                    }
                }else if (targetProperty.endsWith("Ids")){
                    String sourceProperty = StringUtils.removeEnd(targetProperty,"Ids");
                    if (sourceProperties.contains(sourceProperty)){
                        copyProperty(src, target, sourceClass, targetClass, referenceProperties, sourceProperty,targetProperty);
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void copyProperty(Object src, Object target, Class sourceClass, Class targetClass,
                                  List<String> referenceProperties,
                                  String sourceProperty,String targetProperty) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        Class targetType = DataUtils.getPropertyType(targetClass,targetProperty);

        Class sourceType = DataUtils.getPropertyType(sourceClass,  sourceProperty);

        if (referenceProperties.contains(targetProperty)) { //目标值是带EntityReference注解的
            if (sourceType.isAssignableFrom(targetType)) {
                Object v = PropertyUtils.getProperty(src, sourceProperty);
                if (v != null) {
                    PropertyUtils.setProperty(target, targetProperty, v);
                }
            }else if (Long.class.isAssignableFrom(targetType) &&
                    IdName.class.isAssignableFrom(sourceType)){ //单独对象
                Object v = PropertyUtils.getProperty(src, sourceProperty);
                if (v != null) {
                    PropertyUtils.setProperty(target, targetProperty, ((IdName)v).getId());
                }
            }else if (Collection.class.isAssignableFrom(targetType) &&
                    Collection.class.isAssignableFrom(sourceType)) {//集合对象
                Object v = PropertyUtils.getProperty(src, sourceProperty);
                if (v != null) {
                    Class innerType = DataUtils.getInnerPropertyType(sourceClass, sourceProperty);
                    if (IdName.class.isAssignableFrom(innerType)){
                        Collection<Long> ids = ObjectUtils.ids((Collection<? extends IdName>) v);
                        PropertyUtils.setProperty(target, targetProperty, ids);
                    }
                }
            }
        }else {
            if (sourceType.isAssignableFrom(targetType)) { //而且类型一样
                if (Collection.class.isAssignableFrom(sourceType)){ //如果是集合就要判断集合的泛型类型是否一样
                    Class sourceInnerType = DataUtils.getInnerPropertyType(sourceClass, sourceProperty);
                    Class targetInnerType = DataUtils.getInnerPropertyType(targetClass, sourceProperty);
                    if (Long.class.isAssignableFrom(targetInnerType) && IdName.class.isAssignableFrom(sourceInnerType)){
                        Object v = PropertyUtils.getProperty(src, sourceProperty);
                        if (v != null) {
                                Collection<Long> ids = ObjectUtils.ids((Collection<? extends IdName>) v);
                                PropertyUtils.setProperty(target, targetProperty, ids);
                        }
                    }else if (sourceInnerType.isAssignableFrom(targetInnerType)){ //目标泛型类型和源头一样，直接复制
                        Object v = PropertyUtils.getProperty(src, sourceProperty);
                        if (v != null) {
                            PropertyUtils.setProperty(target, targetProperty, v);
                        }
                    }
                }else{ //类型一样且非集合类的
                    Object v = PropertyUtils.getProperty(src, sourceProperty);
                    if (v != null) {
                        PropertyUtils.setProperty(target, targetProperty, v);
                    }
                }
            }
        }
    }
}
