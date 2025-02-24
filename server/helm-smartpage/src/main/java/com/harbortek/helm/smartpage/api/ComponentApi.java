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

package com.harbortek.helm.smartpage.api;

import com.harbortek.helm.smartpage.service.PageComponentService;
import com.harbortek.helm.smartpage.vo.DataRequest;
import com.harbortek.helm.util.IDUtils;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/smartPage/{pageId}/component")
public class ComponentApi {
    @Autowired
    PageComponentService pageComponentService;

    @Parameter(name="获取新的组件ID")
    @RequestMapping(value = "/id", method = RequestMethod.GET)
    ResponseEntity<Long> createId(@PathVariable Long pageId){
        return ResponseEntity.ok(IDUtils.getId());
    }

    @Parameter(name="获取数据")
    @RequestMapping(value = "/{componentId}/data",method = RequestMethod.POST)
    ResponseEntity<String> getData(@PathVariable Long pageId, @PathVariable Long componentId,
                                            @RequestBody DataRequest request){
        String result = pageComponentService.getData(pageId,componentId,request);
        return ResponseEntity.ok(result);
    }
}
