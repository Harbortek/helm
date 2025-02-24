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

import com.harbortek.helm.common.annotation.EntityInclude;
import com.harbortek.helm.common.annotation.EntityReference;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.entity.IdName;
import com.harbortek.helm.common.vo.IdNameReference;
import com.harbortek.helm.common.vo.IdNameVo;
import com.harbortek.helm.system.dao.UserDao;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.repository.util.ClassUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.repository.util.ClassUtils.hasProperty;

public class DataUtils {

    public static <E, V> Page<V> toVo(Page<E> sourcePage, Class<V> targetClass) {
        return toVo(sourcePage, targetClass, true);
    }

    public static <E, V> Page<V> toVo(Page<E> sourcePage, Class<V> targetClass, boolean fullFields) {
        List<E> source = sourcePage.getContent();
        List<V> target = toVo(source, targetClass, fullFields);
        return new PageImpl<>(target, sourcePage.getPageable(), sourcePage.getTotalElements());
    }

    public static <E,V> List<V> toVo(List<E> source, Class<V> targetClass) {
        return toVo(source, targetClass, true);
    }

    public static <E,V> List<V> toVo(List<E> source, Class<V> targetClass, boolean fullFields) {

        List<V> target = new ArrayList<>(); //createList(source, targetClass);
        if (source.isEmpty()) {
            return target;
        }

        for (E sourceItem : source) {
            V targetItem = toVo(sourceItem, targetClass, fullFields);
            target.add(targetItem);
        }

//        Class<?> sourceClass = source.stream().findFirst().get().getClass();
//        List<String> referenceProperties = getReferenceProperties(sourceClass);
//
//        referenceProperties.forEach(field -> {
//            String targetField = guessTargetPropertyName(targetClass, field);
//            if (ClassUtils.hasProperty(targetClass, targetField)) {
//                toVo(source, sourceClass, field, target, targetClass, targetField, fullFields);
//            }
//        });

        return target;
    }

    public static String guessTargetPropertyName(Class<?> targetType, String sourceFieldName) {
        String targetFieldName = sourceFieldName;
        if (sourceFieldName.endsWith("Id")) {
            String possibleName = StringUtils.substring(sourceFieldName, 0, sourceFieldName.length() - 2);
            if (hasProperty(targetType, possibleName)) {
                targetFieldName = possibleName;
            }
        } else if (sourceFieldName.endsWith("Ids")) {
            String possibleName = StringUtils.substring(sourceFieldName, 0, sourceFieldName.length() - 3);
            possibleName = Inflector.getInstance().pluralize(possibleName);
            if (hasProperty(targetType, possibleName)) {
                targetFieldName = possibleName;
            }
        }
        return targetFieldName;
    }

    @NotNull
    private static <E,V> List<V> createList(Collection<E> source, Class<V> targetType) {
        List<V> target = new ArrayList<>();
        source.forEach(sourceItem -> {
            V targetItem = BeanUtils.instantiateClass(targetType);
            BeanUtils.copyProperties(sourceItem, targetItem);
            target.add(targetItem);
        });
        return target;
    }

//    /**
//     * @param source
//     * @param sourceClass
//     * @param sourceProperty
//     * @param target         target数组里面已经准备好了其他字段数据，等待填充关联实体对象
//     * @param targetClass
//     * @param targetProperty
//     */
//    public static <E,V> void toVo(List<T> source, Class<T> sourceClass, String sourceProperty, List<K> target,
//                                   Class<K> targetClass, String targetProperty, boolean fullFields) {
//        Class<? extends BaseEntity> referenceType = getReferenceClass(sourceClass, sourceProperty);
//
//        Class sourceFieldType = getPropertyType(sourceClass, sourceProperty);
//        Class targetFieldType = getPropertyType(targetClass, targetProperty);
//
//        if (Long.class.isAssignableFrom(sourceFieldType)) {
//            Set<Long> ids = source.stream().map((item) -> {
//                return (Long) getProperty(item, sourceProperty);
//            }).filter(ObjectUtils::isValid).collect(Collectors.toSet());
//
//            List<? extends BaseEntity> referenceList = findObjects(ids, referenceType, targetFieldType, fullFields);
//
//            Map<Long, BaseEntity> referenceMap = new HashMap();
//            referenceList.forEach(item -> {
//                referenceMap.put(item.getId(), item);
//            });
//
//            for (int i = 0; i < source.size(); i++) {
//                T sourceItem = source.get(i);
//                K targetItem = target.get(i);
//                Long id = (Long) getProperty(sourceItem, sourceProperty);
//                if (id == null || id.longValue() == 0) {
//                    continue;
//                }
//                BaseEntity r = referenceMap.get(id);
//                if (r == null) {
//                    continue;
//                }
//
//                copyToTargetProperty(targetItem, targetClass, targetProperty, targetFieldType, r);
//
//            }
//        } else if (Collection.class.isAssignableFrom(sourceFieldType)) {
//            Class innerTargetFiledType = getInnerPropertyType(targetClass, targetProperty);
//
//            Set<Long> ids = new HashSet<>();
//            source.forEach((item) -> {
//                Collection<Long> collection = (Collection<Long>) getProperty(item, sourceProperty);
//                ids.addAll(collection);
//            });
//            List<? extends BaseEntity> referenceList = findObjects(ids, referenceType, targetFieldType, fullFields);
//            Map<Long, BaseEntity> referenceMap = new HashMap<>();
//            referenceList.forEach(item -> {
//                referenceMap.put(item.getId(), item);
//            });
//
//            for (int i = 0; i < source.size(); i++) {
//                T sourceItem = source.get(i);
//                K targetItem = target.get(i);
//                Collection<Long> collection = (Collection<Long>) getProperty(sourceItem, sourceProperty);
//                List value = new ArrayList<>();
//                collection.forEach(s -> {
//                    BaseEntity entity = referenceMap.get(s);
//                    if (entity != null) {
//                        Object vo = toVo(entity, innerTargetFiledType); //数组里面的泛型类型
//                        value.add(vo);
//                    }
//                });
//
//                setProperty(targetItem, targetProperty, value);
//            }
//        } else if (Map.class.isAssignableFrom(sourceFieldType)) {
//
//        } else if (Set.class.isAssignableFrom(sourceFieldType)) {
//
//        }
//    }

    public static <V> V toVo(Object sourceItem, Class<V> targetClass) {
        if (sourceItem==null) return null;
        return toVo(sourceItem, targetClass, true);
    }

    public static <V> V toVo(Object sourceItem, Class<V> targetClass, boolean fullFields) {
        Class sourceClass = sourceItem.getClass();
        List<String> referenceProperties = getReferenceProperties(sourceClass);
        V targetItem = BeanUtils.instantiateClass(targetClass);
        BeanUtils.copyProperties(sourceItem, targetItem);

        referenceProperties.forEach(field -> {
            String targetField = guessTargetPropertyName(targetClass, field);
            if (ClassUtils.hasProperty(targetClass, targetField)) {
                toVo(sourceItem, sourceClass, field, targetItem, targetClass, targetField, fullFields);
            }
        });
        return targetItem;
    }

    public static <E,V> void toVo(Object sourceItem, Class<E> sourceClass, String sourceProperty, Object targetItem,
                            Class<V> targetClass, String targetProperty, boolean fullFields) {
        Class<? extends BaseEntity> referenceType = getReferenceClass(sourceClass, sourceProperty);

        Class<?> sourceFieldType = getPropertyType(sourceClass, sourceProperty);
        Class<?> targetFiledType = getPropertyType(targetClass, targetProperty);

        if (Long.class.isAssignableFrom(sourceFieldType)) {
            Long referenceId = (Long) getProperty(sourceItem, sourceProperty);
            if (referenceId == null || referenceId == 0) {
                return;
            }
            BaseEntity reference = findObject(referenceId, referenceType, targetFiledType, fullFields);
            if (reference == null) {
                return;
            }
            copyToTargetProperty(targetItem, targetClass, targetProperty, targetFiledType, reference);
        } else if (Collection.class.isAssignableFrom(sourceFieldType)) {
            Class<?> innerTargetFiledType = getInnerPropertyType(targetClass, targetProperty);
            Set<Long> ids = new HashSet<>();
            Collection<Long> collection = (Collection<Long>) getProperty(sourceItem, sourceProperty);
            ids.addAll(collection);

            List<? extends BaseEntity> referenceList =
                    findObjects(ids, referenceType, innerTargetFiledType, fullFields);
            Map<Long, BaseEntity> referenceMap = new HashMap<>();
            referenceList.forEach(item -> {
                referenceMap.put(item.getId(), item);
            });

            List value = new ArrayList<>();
            collection.forEach(s -> {
                BaseEntity entity = referenceMap.get(s);
                value.add(toVo(entity, innerTargetFiledType));
            });

            setProperty(targetItem, targetProperty, value);
        }
    }

    private static <E,V> void copyToTargetProperty(Object targetItem, Class<E> targetClass, String targetProperty,
                                             Class<V> targetFieldType,
                                             BaseEntity reference) {

        if (IdNameReference.class.isAssignableFrom(targetFieldType)) {
            Class innerType = getInnerPropertyType(targetClass, targetProperty);
            if (innerType != null) {
                Object value = BeanUtils.instantiateClass(innerType);
                Collection<String> ignoreProperties = getIgnoreProperties(targetClass, targetProperty);
                copyProperties(reference, value, ignoreProperties);
                setProperty(targetItem, targetProperty, new IdNameReference<>((IdName) value));
            } else {
                Object value = BeanUtils.instantiateClass(targetFieldType);
                Collection<String> ignoreProperties = getIgnoreProperties(targetClass, targetProperty);
                copyProperties(reference, value, ignoreProperties);
                setProperty(targetItem, targetProperty, value);
            }
        } else {
            Object value = BeanUtils.instantiateClass(targetFieldType);
            Collection<String> ignoreProperties = getIgnoreProperties(targetClass, targetProperty);
            copyProperties(reference, value, ignoreProperties);
            setProperty(targetItem, targetProperty, value);
        }
    }

//	private static void fixIdNameReference(Object target){
//		List<String> properties = getAllProperties(target.getClass());
//		IdNameResolver idNameResolver = SpringContextUtil.getBean(IdNameResolver.class);
//		properties.forEach(p->{
//			Object propertyValue = getProperty(target,p);
//			if ( BeanUtils.isSimpleProperty(propertyValue.getClass())){
//				//do nothing
//			}
//			else if (propertyValue instanceof  IdNameReference){
//				IdNameReference reference = (IdNameReference)propertyValue;
//				BaseEntity baseEntity = idNameResolver.findById(reference.getId(),);
//				if (baseEntity!=null) {
//					reference.setReferTo(baseEntity);
//				}
//			}else if (propertyValue instanceof Collection){
//				Collection  collection = (Collection)propertyValue;
//				collection.forEach(item->fixIdNameReference(item));
//			}else if (propertyValue instanceof Set){
//				Set set = (Set) propertyValue;
//				set.forEach(item->fixIdNameReference(item));
//			}
//		});
//	}

    public static List<String> getAllProperties(Class clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<String> properties = Arrays.stream(BeanUtils.getPropertyDescriptors(clazz))
                                        .map(FeatureDescriptor::getName)
                                        .collect(Collectors.toList());
        properties.remove("class");
        properties.remove("new");
        return properties;
    }

    public static List<String> getReferenceProperties(Class clz) {
        if (clz == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>(getReferenceProperties(clz.getSuperclass()));

        List<String> filteredFields = Arrays.stream(clz.getDeclaredFields())
                                            .filter(field -> {
                                                return field.getAnnotation(EntityReference.class) != null;
                                            })
                                            .map(Field::getName)
                                            .collect(Collectors.toList());
        result.addAll(filteredFields);
        return result;
    }

    public static Class<? extends BaseEntity> getReferenceClass(Class sourceClass, String sourceProperty) {
        Field field = ReflectionUtils.findField(sourceClass, sourceProperty);
        Annotation[] annotations = field.getAnnotationsByType(EntityReference.class);
        if (annotations.length == 0) {
            return null;
        }
        return (Class<? extends BaseEntity>) ((EntityReference) annotations[0]).value();
    }

    public static Class getPropertyType(Class clz, String property) {
        return ReflectionUtils.findField(clz, property).getType();
    }

    public static <K> Class getInnerPropertyType(Class<K> clz, String property) {
        Field field = ReflectionUtils.findField(clz, property);
        field.setAccessible(true);
        Type genericType = field.getGenericType();
        if (null == genericType) {
            return IdNameVo.class;
        }
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            // 得到泛型里的class类型对象
            if (pt.getActualTypeArguments()[0] instanceof ParameterizedType) {
                ParameterizedType pt2 = (ParameterizedType) pt.getActualTypeArguments()[0];
                return (Class) pt2.getRawType();
            } else {
                Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                return actualTypeArgument;
            }
        }
        return IdNameVo.class;
    }

    private static Collection<String> getIncludeProperties(Class clz, String property) {
        EntityInclude entityInclude = ReflectionUtils.findField(clz, property).getAnnotation(EntityInclude.class);
        if (entityInclude == null) {
            Class propertyType = getPropertyType(clz, property);
            List<String> allProperties = getAllProperties(propertyType);
            return allProperties;
        }
        return Arrays.asList(entityInclude.value());
    }

    private static Collection<String> getIgnoreProperties(Class clz, String targetProperty) {
        Collection<String> includes = getIncludeProperties(clz, targetProperty);
        Class propertyType = getPropertyType(clz, targetProperty);
        List<String> allProperties = getAllProperties(propertyType);
        return CollectionUtils.subtract(allProperties, includes);
    }


    public static Object getProperty(Object object, String property) {

        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(object.getClass(), property);
        if (descriptor == null){
            return null;
        }
        Assert.notNull(descriptor, "property " + property + " not found");
        Method readMethod = descriptor.getReadMethod();
        Object o = null;
        try {
            o = readMethod.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
        }
        return o;
    }

    public static void setProperty(Object object, String property, Object value) {
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(object.getClass(), property);
        if (descriptor == null) {
            return;
        }
        Method writeMethod = descriptor.getWriteMethod();
        try {
            writeMethod.invoke(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyProperties(Object source, Object target,
                                      @Nullable Collection<String> ignoreList) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();

        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        PropertyDescriptor[] var7 = targetPds;
        int var8 = targetPds.length;

        for (int var9 = 0; var9 < var8; ++var9) {
            PropertyDescriptor targetPd = var7[var9];
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null) {
                        ResolvableType sourceResolvableType = ResolvableType.forMethodReturnType(readMethod);
                        ResolvableType targetResolvableType = ResolvableType.forMethodParameter(writeMethod, 0);
                        boolean isAssignable = !sourceResolvableType.hasUnresolvableGenerics() &&
                                !targetResolvableType.hasUnresolvableGenerics() ?
                                targetResolvableType.isAssignableFrom(sourceResolvableType) :
                                org.springframework.util.ClassUtils.isAssignable(writeMethod.getParameterTypes()[0],
                                                                                 readMethod.getReturnType());
                        if (isAssignable) {
                            try {
                                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                    readMethod.setAccessible(true);
                                }

                                Object value = readMethod.invoke(source);
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }

                                writeMethod.invoke(target, value);
                            } catch (Throwable var18) {
                                throw new FatalBeanException(
                                        "Could not copy property '" + targetPd.getName() + "' from source to target",
                                        var18);
                            }
                        }
                    }
                }
            }
        }

    }


    public static void reload(List<? extends IdName> data,
                              Class<? extends BaseEntity> sourceClass, String[] fields) {
        Collection<Long> ids = ObjectUtils.ids(data);
        JdbcAggregateTemplate template = SpringContextUtil.getBean(JdbcAggregateTemplate.class);
        Query query = Query.query(Criteria.where(BaseEntity.Fields.id).in(ids));
        query.columns(Arrays.asList(fields));
        Iterable<? extends BaseEntity> referenceList = template.findAll(query, sourceClass);
        Map<Long, BaseEntity> referenceMap = new HashMap<>();
        referenceList.forEach(item -> {
            referenceMap.put(item.getId(), item);
        });
        data.forEach(item -> {
            for (String field : fields) {
                BaseEntity source = referenceMap.get(item.getId());
                Object value = getProperty(source, field);
                setProperty(item, field, value);
            }
        });
    }

    private static BaseEntity findObject(Long id, Class<? extends BaseEntity> sourceClass, Class targetClass,
                                         boolean fullFields) {
//		Query query = new Query(Criteria.where(BaseEntity.Fields.id).is(id));
//		if (!fullFields || IdNameReference.class.isAssignableFrom(targetClass)){
//			query.fields().include(BaseEntity.Fields.id).include(BaseEntity.Fields.name);
//		}
//		MongoTemplate mongoTemplate = SpringContextUtil.getBean(MongoTemplate.class);
//		BaseEntity object = mongoTemplate.findOne(query, sourceClass);
//		return object;


        UserDao idNameResolver = SpringContextUtil.getBean(UserDao.class);
        return idNameResolver.findById(id, sourceClass);
    }

    private static List<? extends BaseEntity> findObjects(Set<Long> ids, Class<? extends BaseEntity> sourceClass,
                                                          Class targetClass,
                                                          boolean fullFields) {
//		Query query = new Query(Criteria.where(BaseEntity.Fields.id).in(ids));
//		if (!fullFields || IdNameReference.class.isAssignableFrom(targetClass)){
//			query.fields().include(BaseEntity.Fields.id).include(BaseEntity.Fields.name);
//		}
//		MongoTemplate mongoTemplate = SpringContextUtil.getBean(MongoTemplate.class);
//		List<? extends BaseEntity> referenceList = mongoTemplate.find(query, sourceClass);
//		return referenceList;
        UserDao idNameResolver = SpringContextUtil.getBean(UserDao.class);
        return idNameResolver.findByIds(ids, sourceClass);
    }

    public static <S, T> List<T> toEntity(Collection<S> voCollection, Class<T> entityClass) {
        return voCollection.stream().map(voItem -> {
            return toEntity(voItem, entityClass);
        }).collect(Collectors.toList());
    }

    public static <T> T toEntity(Object voItem, Class<T> entityClass) {
        Class<?> voClass = voItem.getClass();
        T entityItem = BeanUtils.instantiateClass(entityClass);
        BeanCopyUtils.copyWithoutNullProperties(voItem, entityItem);

        List<String> referenceProperties = getReferenceProperties(entityClass);

        referenceProperties.forEach(entityField -> {
            String voField = guessTargetPropertyName(voClass, entityField);
            if (hasProperty(voClass, voField)) {
                if (entityField.endsWith("Id")){
                    Object value = getProperty(voItem, voField);
                    if (value instanceof IdName) {
                        Long id = ((IdName) value).getId();
                        setProperty(entityItem, entityField, id);
                    }
                }else if (entityField.endsWith("Ids")){
                    Object value = getProperty(voItem, voField);
                    if (value instanceof Collection) {
                        Collection<Long> ids = ObjectUtils.ids((Collection<?>) value, "id");
                        setProperty(entityItem, entityField, ids);
                    }
                }
            }
        });

        return entityItem;
    }
}
