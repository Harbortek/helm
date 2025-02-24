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

package com.harbortek.helm.smartdoc.api;

import com.harbortek.helm.smartdoc.service.ReqIFExportTemplateService;
import com.harbortek.helm.smartdoc.vo.ReqIFExportTemplateVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
@RequestMapping("/smart-doc/export/reqIf/template")
public class ReqIFExportTemplateApi {
    @Autowired
    ReqIFExportTemplateService reqIFExportTemplateService;

    @Parameter(name="查询导出模版列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<ReqIFExportTemplateVo>> findTemplates(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false,defaultValue = "") String keyword) {
        Collection<ReqIFExportTemplateVo> configs = reqIFExportTemplateService.findTemplates(projectId, keyword);
        return ResponseEntity.ok(configs);
    }


    @Parameter(name="查询一个导出模版")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<ReqIFExportTemplateVo> findOneTemplate(@PathVariable Long id) {
        ReqIFExportTemplateVo config = reqIFExportTemplateService.findOneTemplate(id);
        return ResponseEntity.ok(config);
    }

    @Parameter(name="创建一个导出模版")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<ReqIFExportTemplateVo> createTemplate(@RequestBody ReqIFExportTemplateVo configVo) {
        ReqIFExportTemplateVo config = reqIFExportTemplateService.createTemplate(configVo);
        return ResponseEntity.ok(config);
    }

    @Parameter(name="更新一个导出模版")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<ReqIFExportTemplateVo> updateTemplate(@RequestBody ReqIFExportTemplateVo configVo) {
        ReqIFExportTemplateVo result = reqIFExportTemplateService.updateTemplate(configVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个导出模版")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        reqIFExportTemplateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }
}
