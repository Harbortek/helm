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

package com.harbortek.helm.tracker.template.builder;

import com.harbortek.helm.util.DataUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EntityResolver {

	private final Logger logger = LoggerFactory.getLogger(EntityResolver.class);

	private Map<String, Set> cache = new HashMap<>();

	public void register(Object o, String clzName) {
		if (!cache.containsKey(clzName)) {
			cache.put(clzName, new LinkedHashSet<>());
		}
		Set values = cache.get(clzName);
		values.add(o);
	}

	public <T> T findById(Long id,String clzName, Class<T> clz) {
		Set<T> values = cache.get(clzName);
		if (values == null || values.isEmpty()) {
			return null;
		}

		Optional<T> object =  values.stream().filter(o -> {
			Long oid = (Long)DataUtils.getProperty(o,"id");
			return id.equals(oid);
		}).findFirst();
		return object.orElse(null);
	}

	public  <T> T findByName(String name,String clzName, Class<T> clz) {
		Set<T> values = cache.get(clzName);
		if (values == null || values.isEmpty()) {
			return null;
		}
		Optional<T> optional = values.stream().filter(o -> {
			String oname = (String)DataUtils.getProperty(o,"name");
			return StringUtils.equals(StringUtils.trim(name),oname);
		}).findFirst();
		if (optional.isPresent()) {
			return  optional.get();
		}
		logger.warn("entity {} not found.",name);
		return null;
	}

	public <T> Collection<T> findByType(String clzName, Class<T> clz) {
		Set<T> values = cache.get(clzName);
		if (values == null || values.isEmpty()) {
			return null;
		}
		return  values;
	}


}
