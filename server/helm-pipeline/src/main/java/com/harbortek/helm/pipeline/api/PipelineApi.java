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

package com.harbortek.helm.pipeline.api;

import com.harbortek.helm.pipeline.service.JenkinsService;
import com.harbortek.helm.pipeline.service.PipelineRepositoryService;
import com.harbortek.helm.pipeline.service.PipelineService;
import com.harbortek.helm.pipeline.vo.PipelineDetail;
import com.harbortek.helm.pipeline.vo.PipelineRepositoryVo;
import com.harbortek.helm.pipeline.vo.PipelineVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/pipeline/project/{projectId}/")
public class PipelineApi {
    @Autowired
    PipelineService pipelineService;

    @Autowired
    PipelineRepositoryService pipelineRepositoryService;

    @Autowired
    JenkinsService jenkinsService;

    @Parameter(name="查询项目流水线列表")
    @RequestMapping(value = "/pipelines", method = RequestMethod.GET)
    ResponseEntity<List<PipelineDetail>> findBindingPipelines(@PathVariable Long projectId) {
        List<PipelineVo> bindingPipelines = pipelineService.findBindingPipelines(projectId);
        List<PipelineDetail> details = bindingPipelines.stream().map(pipelineVo -> {
            PipelineRepositoryVo repository =
                    pipelineRepositoryService.findPipelineRepository(pipelineVo.getRepositoryId());
            return jenkinsService.getPipelineLastRunInfo(repository, pipelineVo);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(details);
    }

    @Parameter(name="绑定项目流水线")
    @RequestMapping(value = "/bind", method = RequestMethod.POST)
    ResponseEntity<Void> bindingPipelines(@PathVariable Long projectId, @RequestBody List<PipelineVo> pipelines) {
        pipelineService.bindPipelines(projectId, pipelines);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="解除绑定项目流水线")
    @RequestMapping(value = "/unbind", method = RequestMethod.POST)
    ResponseEntity<Void> unbindingPipelines(@PathVariable Long projectId, @RequestBody List<PipelineVo> pipelines) {
        pipelineService.unbindPipelines(projectId, pipelines);
        return ResponseEntity.ok().build();
    }
}
