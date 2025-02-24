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

package com.harbortek.helm.tracker.util;


import com.harbortek.helm.common.vo.BaseVo;
import org.springframework.beans.BeanUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class VoUtils {


	public static BaseVo findById(BaseVo root, Long id, Class<? extends BaseVo> clz) {

		if (root.getClass().isAssignableFrom(clz) && root.getId() == id) {
			return root;
		}

		Collection<Field> fields = findFields(root.getClass(), new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				return field.getType().isAssignableFrom(BaseVo.class)
						|| field.getType().isAssignableFrom(Collection.class)
						|| field.getType().isAssignableFrom(Set.class);
			}
		});

		for (Field field : fields) {
			if (field.getType().isAssignableFrom(BaseVo.class)) {
				BaseVo t = null;
				try {
					t = findById((BaseVo) getFieldValue(field, root), id, clz);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (t != null) {
					return t;
				}
			} else if (field.getType().isAssignableFrom(Collection.class)) {
				Collection collection = null;
				try {
					collection = (Collection) getFieldValue(field, root);
				} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
					e.printStackTrace();
				}
				if (collection != null) {
					for (Object o : collection) {
						if (o instanceof BaseVo) {
							BaseVo t = findById((BaseVo) o, id, clz);
							if (t != null) {
								return t;
							}
						}
					}
				}

			} else if (field.getType().isAssignableFrom(Set.class)) {
				Set set = null;
				try {
					set = (Set) getFieldValue(field, root);
				} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
					e.printStackTrace();
				}
				if (set != null) {
					for (Object o : set) {
						if (o instanceof BaseVo) {
							BaseVo t = findById((BaseVo) o, id, clz);
							if (t != null) {
								return t;
							}
						}
					}
				}

			}
		}

		return null;
	}


	public static BaseVo findByName(BaseVo root, String name, Class<? extends BaseVo> clz) {

		if (root.getClass().isAssignableFrom(clz) && root.getName().equals(name)) {
			return root;
		}

		Collection<Field> fields = findFields(root.getClass(), new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				return field.getType().isAssignableFrom(BaseVo.class)
						|| field.getType().isAssignableFrom(Collection.class)
						|| field.getType().isAssignableFrom(Set.class);
			}
		});

		for (Field field : fields) {
			if (field.getType().isAssignableFrom(BaseVo.class)) {
				BaseVo t = null;
				try {
					t = findByName((BaseVo) getFieldValue(field, root), name, clz);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (t != null) {
					return t;
				}
			} else if (field.getType().isAssignableFrom(Collection.class)) {
				Collection collection = null;
				try {
					collection = (Collection) getFieldValue(field, root);
				} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
					e.printStackTrace();
				}
				if (collection != null) {
					for (Object o : collection) {
						if (o instanceof BaseVo) {
							BaseVo t = findByName((BaseVo) o, name, clz);
							if (t != null) {
								return t;
							}
						}
					}
				}

			} else if (field.getType().isAssignableFrom(Set.class)) {
				Set set = null;
				try {
					set = (Set) getFieldValue(field, root);
				} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
					e.printStackTrace();
				}
				if (set != null) {
					for (Object o : set) {
						if (o instanceof BaseVo) {
							BaseVo t = findByName((BaseVo) o, name, clz);
							if (t != null) {
								return t;
							}
						}
					}
				}

			}
		}

		return null;
	}


	public static Collection<BaseVo> findByType(BaseVo root, Class<? extends BaseVo> clz) {

		ArrayList<BaseVo> list = new ArrayList<>();

		findByType(root, clz, list);

		return list;

	}

	private static void findByType(BaseVo root, Class<? extends BaseVo> clz, ArrayList<BaseVo> list) {

		if (root.getClass().isAssignableFrom(clz)) {
			list.add(root);
		}

		Collection<Field> fields = findFields(root.getClass(), new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				return field.getType().isAssignableFrom(BaseVo.class)
						|| field.getType().isAssignableFrom(Collection.class)
						|| field.getType().isAssignableFrom(Set.class);
			}
		});

		for (Field field : fields) {
			if (field.getType().isAssignableFrom(BaseVo.class)) {
				try {
					findByType((BaseVo) getFieldValue(field, root), clz, list);
				} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
					e.printStackTrace();
				}
			} else if (field.getType().isAssignableFrom(Collection.class)) {
				Collection collection = null;
				try {
					collection = (Collection) getFieldValue(field, root);
				} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
					e.printStackTrace();
				}
				if (collection != null) {
					for (Object o : collection) {
						if (o instanceof BaseVo) {
							findByType((BaseVo) o, clz, list);
						}
					}
				}

			} else if (field.getType().isAssignableFrom(Set.class)) {
				Set set = null;
				try {
					set = (Set) getFieldValue(field, root);
				} catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
					e.printStackTrace();
				}
				if (set != null) {
					for (Object o : set) {
						if (o instanceof BaseVo) {
							findByType((BaseVo) o, clz, list);
						}
					}
				}

			}
		}
	}

	private static Object getFieldValue(Field field, Object target) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
		PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(target.getClass(),field.getName());
		Method readMethod = descriptor.getReadMethod();
		Object o = readMethod.invoke(target);
		return o;
	}

	public static Collection<Field> findFields(Class<?> clazz, FieldFilter ff) {
		Class targetClass = clazz;
		ArrayList<Field> fieldList = new ArrayList<>();

		do {
			Field[] fields = targetClass.getDeclaredFields();
			Field[] var5 = fields;
			int var6 = fields.length;

			for (int var7 = 0; var7 < var6; ++var7) {
				Field field = var5[var7];
				if (ff == null || ff.matches(field)) {
					fieldList.add(field);
				}
			}

			targetClass = targetClass.getSuperclass();
		} while (targetClass != null && targetClass != Object.class);
		return fieldList;
	}

	public interface FieldFilter {
		boolean matches(Field field);
	}
}
