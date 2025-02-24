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

package com.harbortek.helm.smartdoc.importer.word.conditions;

import com.harbortek.helm.smartdoc.vo.ConditionType;
import lombok.Synchronized;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
public class ConditionsRegistry {
    private static Map<String, IParagraphCondition> conditionHashMap =  new HashMap<>();
    private static List<ConditionType> conditionTypes =  new ArrayList<>();

    private static boolean initialized = false;
    @Synchronized
    public static void init() {
        // init modules
        if (initialized){
            return;
        }

        Reflections reflections = new Reflections("com.harbortek.helm.smartdoc.importer.word.conditions");

        Set<Class<?>> types = reflections
                .getTypesAnnotatedWith(ParagraphCondition.class);
        types
             .forEach(clazz -> {
                 String id = clazz.getAnnotation(ParagraphCondition.class).id();
                 boolean hasArg = clazz.getAnnotation(ParagraphCondition.class).hasArgument();
                 if (!conditionHashMap.containsKey(id)){
                     try {
                         Constructor<?> constructor = clazz.getConstructor();
                         Object r = constructor.newInstance();
                         if (r instanceof IParagraphCondition){
                             IParagraphCondition condition = (IParagraphCondition)r;
                             conditionHashMap.put(id, condition);
                         }
                         ConditionType type = new ConditionType();
                         type.setId(id);
                         type.setHasArgument(hasArg);
                         conditionTypes.add(type);
                     } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                              InvocationTargetException e) {
                         e.printStackTrace();
                     }
                 }
             });
        initialized = true;
    }

    public static IParagraphCondition getTextCondition(String id){
        init();
        return conditionHashMap.get(id);
    }

    public static List<ConditionType> getConditionTypes(){
        init();
        return conditionTypes;
    }
}
