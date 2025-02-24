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

package com.harbortek.helm.smartdoc.importer.word.extractors;

import lombok.Synchronized;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExtractorRegistry {
    private static final Map<String, IValueExtractor> valueExtractorMap = new HashMap<>();

    private static boolean initialized = false;

    @Synchronized
    public static void init() {
        // init modules
        if (initialized) {
            return;
        }

        Reflections reflections = new Reflections("com.harbortek.helm.smartdoc.importer.word.extractors");

        Set<Class<?>> types = reflections.getTypesAnnotatedWith(ValueExtractor.class);
        types
                .forEach(clazz -> {
                    String id = clazz.getAnnotation(ValueExtractor.class).id();
                    if (!valueExtractorMap.containsKey(id)) {
                        try {
                            Constructor<?> constructor = clazz.getConstructor();
                            Object r = constructor.newInstance();
                            if (r instanceof IValueExtractor) {
                                valueExtractorMap.put(id, (IValueExtractor) r);
                            }
                        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                                 InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
        initialized = true;
    }

    public static IValueExtractor getValueExtractor(String id) {
        init();
        return valueExtractorMap.get(id);
    }
}
