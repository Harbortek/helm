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

import com.harbortek.helm.smartdoc.config.SmartDocMessages;
import com.harbortek.helm.smartdoc.service.ReqIFExportJobService;
import com.harbortek.helm.smartdoc.vo.ReqIFExportJobVo;
import com.harbortek.helm.tracker.service.*;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/smart-doc/{pageId}/export/reqIf/job")
public class ReqIFExportJobApi {

    @Autowired
    ReqIFExportJobService reqIFExportJobService;

    @Autowired
    private DocService docService;
    @Autowired
    private TrackerItemService trackerItemService;
    @Autowired
    private TrackerService trackerService;
    @Autowired
    private ProjectPageService projectPageService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    SmartDocMessages smartDocMessages;

    @Parameter(name="查找 ReqIFz 文件中的 ReqIF 文件")
    @RequestMapping(value = "files", method = RequestMethod.GET)
    ResponseEntity<List<String>> findReqIFFiles(@PathVariable Long pageId, String filePath) {
        List<String> files = reqIFExportJobService.findReqIFFiles(filePath);
        return ResponseEntity.ok(files);
    }

    @Parameter(name="获取导出映射")
    @RequestMapping(value = "", method = RequestMethod.GET)
    ResponseEntity<ReqIFExportJobVo> loadExportJob(@PathVariable Long pageId) {
        ReqIFExportJobVo job = reqIFExportJobService.loadExportJob(pageId);
        return ResponseEntity.ok(job);
    }

    @Parameter(name="保存导出映射")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<ReqIFExportJobVo> saveExportJob(@PathVariable Long pageId, @RequestBody ReqIFExportJobVo job) {
        reqIFExportJobService.saveExportJob(pageId, job);
        return ResponseEntity.ok(job);
    }


    @Parameter(name="导出ReqIF文件")
    @RequestMapping(value = "export", method = RequestMethod.GET)
    ResponseEntity<Void> export(@PathVariable Long pageId,
                                                     HttpServletResponse response) {
        ReqIFExportJobVo job = reqIFExportJobService.loadExportJob(pageId);
        reqIFExportJobService.export(pageId, job, response);
        return ResponseEntity.ok().build();
    }
}
