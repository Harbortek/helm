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

package com.harbortek.helm.tracker.vo.smartpage;

import java.util.HashMap;
import java.util.Map;

public class SmartPageContext {
    private Long pageId;
    private Map<String,Object> attributes = new HashMap<>();

    public SmartPageContext() {}

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Object getAttribute(String key){
        return attributes.get(key);
    }
    public void setAttribute(String key,Object value){
        attributes.put(key,value);
    }

}
