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

package com.harbortek.helm.tracker.vo.tracker.stateTransition.actions;

import com.harbortek.helm.tracker.anotation.ActionType;
import com.harbortek.helm.tracker.util.ExecuteContext;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransitionAction;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.scheduling.annotation.Async;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ActionManager {


    private static final Map<String, TrackerStateTransitionAction> actionMap =  new HashMap<>();

    private static boolean initialized = false;

    @Synchronized
    public static void init() {
        // init modules
        if (initialized){
            return;
        }

        Reflections reflections = new Reflections("com.harbortek.helm.tracker.vo.tracker.stateTransition.actions");

        Set<Class<?>> types = reflections
                .getTypesAnnotatedWith(ActionType.class);
        types
             .forEach(clazz -> {
                 String type = clazz.getAnnotation(ActionType.class).type();
                 if (!actionMap.containsKey(type)){
                     try {
                         Constructor<?> constructor = clazz.getConstructor();
                         Object r = constructor.newInstance();
                         if (r instanceof TrackerStateTransitionAction){
                             TrackerStateTransitionAction action = (TrackerStateTransitionAction)r;
                             actionMap.put(type, action );
                         }
                     } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                              InvocationTargetException e) {
                         log.error(e.getMessage());
                     }
                 }
             });
        initialized = true;
    }

    public static TrackerStateTransitionAction getAction(String type){
        init();
        return actionMap.get(type);
    }

    @Async
    public static void executeActions(List<TrackerStateTransitionAction> actions, ExecuteContext context){
        actions.forEach(action -> {
            TrackerStateTransitionAction transitionAction = ActionManager.getAction(action.getType());
            if (transitionAction!=null){
                transitionAction.execute(context);
            }
        });
    }
}
