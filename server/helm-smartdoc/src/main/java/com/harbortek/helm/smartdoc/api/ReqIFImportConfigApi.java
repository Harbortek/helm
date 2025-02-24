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

import com.harbortek.helm.smartdoc.service.ReqIFImportConfigService;
import com.harbortek.helm.smartdoc.vo.ReqIFImportConfigVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
@RequestMapping("/smart-doc/import/reqIf/config")
public class ReqIFImportConfigApi {
    @Autowired
    ReqIFImportConfigService reqIFImportConfigService;

    @Parameter(name="查询配置列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<ReqIFImportConfigVo>> findConfigs(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false,defaultValue = "") String keyword) {
        Collection<ReqIFImportConfigVo> configs = reqIFImportConfigService.findConfigs(projectId, keyword);
        return ResponseEntity.ok(configs);
    }


    @Parameter(name="查询一个配置")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<ReqIFImportConfigVo> findOneConfig(@PathVariable Long id) {
        ReqIFImportConfigVo config = reqIFImportConfigService.findOneConfig(id);
        return ResponseEntity.ok(config);
    }

    @Parameter(name="创建一个配置")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<ReqIFImportConfigVo> createConfig(@RequestBody ReqIFImportConfigVo configVo) {
        ReqIFImportConfigVo config = reqIFImportConfigService.createConfig(configVo);
        return ResponseEntity.ok(config);
    }

    @Parameter(name="更新一个配置")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<ReqIFImportConfigVo> updateConfig(@RequestBody ReqIFImportConfigVo configVo) {
        ReqIFImportConfigVo result = reqIFImportConfigService.updateConfig(configVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个配置")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        reqIFImportConfigService.deleteConfig(id);
        return ResponseEntity.ok().build();
    }
}
