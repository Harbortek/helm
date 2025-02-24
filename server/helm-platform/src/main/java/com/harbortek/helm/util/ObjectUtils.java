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

import cn.hutool.core.comparator.CompareUtil;
import com.harbortek.helm.common.entity.IdName;
import com.harbortek.helm.common.vo.IdNameReference;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectUtils {
    public static List<Long> ids(Collection<? extends IdName> collection) {
        return collection.stream().map(IdName::getId).collect(Collectors.toList());
    }

    public static List<Long> ids(Collection<?> collection,String property) {
        return collection.stream().map(item -> {
            Object value = DataUtils.getProperty(item,property);
            if (value instanceof Long){
                return (Long)value;
            }
            return null;
        }).collect(Collectors.toList());
    }

    public static List<String> names(Collection<?> collection,String property) {
        return collection.stream().map(item -> {
            Object value = DataUtils.getProperty(item,property);
            if (value instanceof String){
                return (String)value;
            }
            return null;
        }).collect(Collectors.toList());
    }

    public static <T extends IdName> void fixIdNameReferenceWith(Object target, T newObj) {
        List<String> properties = DataUtils.getAllProperties(target.getClass());
        properties.forEach(p -> {
            Object propertyValue = DataUtils.getProperty(target, p);
            if (propertyValue == null || BeanUtils.isSimpleProperty(propertyValue.getClass())) {
                //do nothing
            } else if (propertyValue instanceof IdNameReference<?> reference) {
                if (CompareUtil.compare(reference.getId(), newObj.getId()) == 0) {
                    reference = new IdNameReference<>(newObj);
                    DataUtils.setProperty(target,p, reference);
                }
            } else if (propertyValue instanceof Collection<?> collection) {
                collection.forEach(item -> {if (item != null) fixIdNameReferenceWith(item, newObj);});
            } else {
                fixIdNameReferenceWith(propertyValue, newObj);
            }
        });
    }

    public static void deleteNameReferenceWith(Object target, IdName newObj) {
        if (target == null) {
            return;
        }
        List<String> properties = DataUtils.getAllProperties(target.getClass());
        for (String p : properties) {
            Object propertyValue = DataUtils.getProperty(target, p);
            if (propertyValue == null || BeanUtils.isSimpleProperty(propertyValue.getClass())) {
                continue;
            } else if (propertyValue instanceof IdNameReference) {
                IdNameReference reference = (IdNameReference) propertyValue;
                if (CompareUtil.compare(reference.getId(), newObj.getId()) == 0) {
                    DataUtils.setProperty(target, p, null);
                    throw new RuntimeException("can not remove property without holder");
                }
            } else if (propertyValue instanceof Collection) {
                Collection collection = (Collection) propertyValue;
                Class innerTargetFiledType = DataUtils.getInnerPropertyType(target.getClass(), p);
                if (IdNameReference.class.isAssignableFrom(innerTargetFiledType)) {
                    Collection newCollection =
                            (Collection) collection.stream()
                                                   .filter(o -> CompareUtil.compare(((IdNameReference) o).getId(),
                                                                                    newObj.getId()) != 0).collect(
                                            Collectors.toList());
                    DataUtils.setProperty(target, p, newCollection);
                } else {
                    collection.forEach(item -> deleteNameReferenceWith(item, newObj));
                }

            } else if (propertyValue instanceof Set) {
                Set set = (Set) propertyValue;
                Class innerTargetFiledType = DataUtils.getInnerPropertyType(target.getClass(), p);
                if (IdNameReference.class.isAssignableFrom(innerTargetFiledType)) {
                    Set newSet =
                            (Set) set.stream().filter(o -> CompareUtil.compare(((IdNameReference) o).getId(),
                                                                               newObj.getId()) == 0).collect(
                                    Collectors.toSet());
                    DataUtils.setProperty(target, p, newSet);
                } else {
                    set.forEach(item -> deleteNameReferenceWith(item, newObj));
                }
            } else {
                deleteNameReferenceWith(propertyValue, newObj);
            }
        }
    }

    public static <T extends IdName> T findById(Object target, Long id, Class<? extends IdName> claz) {
        if (claz.isAssignableFrom(target.getClass())) {
            if (CompareUtil.compare(((IdName) target).getId(), id) == 0) {
                return (T) target;
            }
        } else if (target instanceof Collection) {
            for (Object item : (Collection)target) {
                if (item != null) {
                    T reference = findById(item, id, claz);
                    if (reference != null) {
                        return reference;
                    }
                }
            }
        } else if (target instanceof Set) {
            for (Object item : (Set)target) {
                if (item != null) {
                    T reference = findById(item, id, claz);
                    if (reference != null) {
                        return reference;
                    }
                }
            }
        } else if (target instanceof Map) {
            for (Object item : ((Map)target).values()) {
                if (item != null) {
                    T reference = findById(item, id, claz);
                    if (reference != null) {
                        return reference;
                    }
                }
            }
        } else {
            List<String> properties = DataUtils.getAllProperties(target.getClass());
            for (String p : properties) {
                Object propertyValue = DataUtils.getProperty(target, p);
                if (propertyValue == null || BeanUtils.isSimpleProperty(propertyValue.getClass()) ||
                        IdNameReference.class.isAssignableFrom(propertyValue.getClass())) {
                    //do nothing
                } else if (claz.isAssignableFrom(propertyValue.getClass())) {
                    if (CompareUtil.compare(((IdName) propertyValue).getId(), id) == 0) {
                        return (T) propertyValue;
                    }
                } else if (propertyValue instanceof Collection) {
                    Collection collection = (Collection) propertyValue;
                    for (Object item : collection) {
                        if (item != null) {
                            T reference = findById(item, id, claz);
                            if (reference != null) {
                                return reference;
                            }
                        }
                    }
                } else if (propertyValue instanceof Set) {
                    Set set = (Set) propertyValue;
                    for (Object item : set) {
                        if (item != null) {
                            T reference = findById(item, id, claz);
                            if (reference != null) {
                                return reference;
                            }
                        }
                    }
                } else {
                    T reference = findById(propertyValue, id, claz);
                    if (reference != null) {
                        return reference;
                    }
                }
            }
        }
        return null;
    }
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof CharSequence) {
            return ((CharSequence)object).length() == 0;
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        } else if (object instanceof Collection) {
            return ((Collection)object).isEmpty();
        } else {
            return (object instanceof Map) && ((Map) object).isEmpty();
        }
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isValid(Long object){
        return object!=null && object >0;
    }
}
