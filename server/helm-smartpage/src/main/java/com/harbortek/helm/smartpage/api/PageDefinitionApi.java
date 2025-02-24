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

import com.harbortek.helm.smartpage.service.PageDefinitionService;
import com.harbortek.helm.tracker.vo.smartpage.PageDefinitionVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/smartPage/{pageId}/pageDefinition")
public class PageDefinitionApi {
    @Autowired
    PageDefinitionService pageDefinitionService;

    @Parameter(name="查询一个页面定义")
    @RequestMapping(value = "", method = RequestMethod.GET)
    ResponseEntity<PageDefinitionVo> loadPageDefinition(@PathVariable Long pageId) {
        PageDefinitionVo pageDefinitionVo = pageDefinitionService.loadPageDefinition(pageId);
        return ResponseEntity.ok(pageDefinitionVo);
    }

    @Parameter(name="保存一个页面定义")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<Void> savePageDefinition(@PathVariable Long pageId, @RequestBody PageDefinitionVo pageDefinitionVo) {
        //页面保存只保存组件信息和样式信息
        //数据集和参数信息由额外接口保存
        PageDefinitionVo existed = pageDefinitionService.loadPageDefinition(pageId);
        existed.setPageStyle(pageDefinitionVo.getPageStyle());
        existed.setPageComponents(pageDefinitionVo.getPageComponents());
        pageDefinitionService.savePageDefinition(pageId, existed);
        return ResponseEntity.ok().build();
    }

}
