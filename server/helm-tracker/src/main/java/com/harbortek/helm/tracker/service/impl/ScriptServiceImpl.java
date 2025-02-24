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

package com.harbortek.helm.tracker.service.impl;

import com.harbortek.helm.tracker.service.ScriptService;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.script.*;
import java.util.function.Predicate;

@Service
@Slf4j
public class ScriptServiceImpl implements ScriptService {

    @Override
    public void executeWithTrackerItem(TrackerItemVo trackerItem, String script) {
        ScriptEngine jsEngine =createScriptEngine();

        jsEngine.put("context", SpringContextUtil.getApplicationContext());
        jsEngine.put("trackerItem", trackerItem);

        try {
            jsEngine.eval(script);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private ScriptEngine createScriptEngine() {

        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine jsEngine = engineManager.getEngineByName("graal.js");

        Bindings bindings = jsEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("polyglot.js.allowHostAccess", true);
        bindings.put("polyglot.js.allowNativeAccess", false);
        bindings.put("polyglot.js.allowCreateThread", false);
        bindings.put("polyglot.js.allowIO", true);
        bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
        bindings.put("polyglot.js.allowHostClassLoading", true);
        bindings.put("polyglot.js.allowAllAccess", true);
        bindings.put("polyglot.js.ecmascript-version", "2022");
        return jsEngine;
    }
}
