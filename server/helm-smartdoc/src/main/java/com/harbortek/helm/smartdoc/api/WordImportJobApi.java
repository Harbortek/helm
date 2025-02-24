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
import com.harbortek.helm.smartdoc.service.WordImportJobService;
import com.harbortek.helm.smartdoc.vo.WordImportJobVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/smart-doc/{pageId}/import/word/job")
public class WordImportJobApi {

    @Autowired
    WordImportJobService wordImportJobService;

    @Autowired
    SmartDocMessages smartDocMessages;


    @Parameter(name="查找历史导入")
    @RequestMapping(value = "", method = RequestMethod.GET)
    ResponseEntity<WordImportJobVo> findJob(@PathVariable Long pageId,Long projectId) {
        WordImportJobVo job=  wordImportJobService.findExistedJob(projectId,pageId);
        return ResponseEntity.ok(job);
    }


    @Parameter(name="初始导入")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<WordImportJobVo> createJob(@PathVariable Long pageId, @RequestBody WordImportJobVo job) {
        job.setPageId(pageId);
        job = wordImportJobService.createJob(job);
        return ResponseEntity.ok(job);
    }


    @Parameter(name="执行导入规则")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<WordImportJobVo> updateJob(@PathVariable Long pageId, @RequestBody WordImportJobVo job) {
        job = wordImportJobService.updateJob(job);
        return ResponseEntity.ok(job);
    }

    @Parameter(name="完成导入")
    @RequestMapping(value = "/complete", method = RequestMethod.POST)
    ResponseEntity<Void> completeJob(@PathVariable Long pageId, @RequestBody WordImportJobVo job) {
        wordImportJobService.completeJob(job);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="取消导入")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteJob(@PathVariable Long pageId,@PathVariable Long id) {
        wordImportJobService.deleteJob(id);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="返回上一次保存的数据")
    @RequestMapping(value = "/{id}/withdraw", method = RequestMethod.GET)
    ResponseEntity<WordImportJobVo> withdrawJob(@PathVariable Long pageId,@PathVariable Long id) {
        WordImportJobVo job = wordImportJobService.withdrawJob(id);
        return ResponseEntity.ok(job);
    }
}
