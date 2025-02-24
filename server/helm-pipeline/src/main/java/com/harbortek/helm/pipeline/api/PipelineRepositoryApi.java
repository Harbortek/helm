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
import com.harbortek.helm.pipeline.vo.PipelineRepositoryVo;
import com.harbortek.helm.pipeline.vo.PipelineVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping(value = "/pipeline/repository")
public class PipelineRepositoryApi {
    @Autowired
    PipelineRepositoryService pipelineRepositoryService;

    @Autowired
    JenkinsService jenkinsService;

    @Parameter(name="查询流水线仓库列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<List<PipelineRepositoryVo>> findPipelineRepositories() {
        List<PipelineRepositoryVo> result = pipelineRepositoryService.findPipelineRepositories();
        return ResponseEntity.ok(result);
    }


    @Parameter(name="创建流水线仓库")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    ResponseEntity<PipelineRepositoryVo> createCPipelineRepository(@RequestBody PipelineRepositoryVo pipelineVo) {
        PipelineRepositoryVo result = pipelineRepositoryService.createPipelineRepository(pipelineVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新流水线仓库")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<Void> updatePipelineRepository(@RequestBody PipelineRepositoryVo pipelineVo) {
        pipelineRepositoryService.updatePipelineRepository(pipelineVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除流水线仓库")
    @RequestMapping(value = "{repositoryId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deletePipelineRepository(@PathVariable Long repositoryId) {
        pipelineRepositoryService.deletePipelineRepository(repositoryId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询流水线仓库")
    @RequestMapping(value = "/{repositoryId}", method = RequestMethod.GET)
    ResponseEntity<PipelineRepositoryVo> findPipelineRepository(@PathVariable Long repositoryId) {
        PipelineRepositoryVo pipeline = pipelineRepositoryService.findPipelineRepository(repositoryId);
        return ResponseEntity.ok(pipeline);
    }

    @Parameter(name="测试流水线仓库")
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    ResponseEntity<Boolean> testPipelineRepository(@RequestBody PipelineRepositoryVo pipelineVo) {
        Boolean success = jenkinsService.testConnection(pipelineVo);
        return ResponseEntity.ok(success);
    }

    @Parameter(name="查询流水线列表")
    @RequestMapping(value = "/{repositoryId}/pipelines", method = RequestMethod.GET)
    ResponseEntity<List<PipelineVo>> findPipelines(@PathVariable Long repositoryId) {
        PipelineRepositoryVo pipeline = pipelineRepositoryService.findPipelineRepository(repositoryId);
        List<PipelineVo> jobs = jenkinsService.findPipelines(pipeline);
        return ResponseEntity.ok(jobs);
    }
}
