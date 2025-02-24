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
import com.harbortek.helm.pipeline.vo.Stage;
import com.harbortek.helm.pipeline.vo.request.BuildInfoRequest;
import com.harbortek.helm.pipeline.vo.request.StepInfoRequest;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/pipeline/job/")
public class JobApi {
    @Autowired
    PipelineRepositoryService pipelineRepositoryService;

    @Autowired
    JenkinsService jenkinsService;

    @Parameter(name="查询项目流水线Build阶段")
    @RequestMapping(value = "/stages", method = RequestMethod.POST)
    ResponseEntity<List<Stage>> findExecutionStages(@RequestBody BuildInfoRequest request) {
        PipelineRepositoryVo repository =
                pipelineRepositoryService.findPipelineRepository(request.getRepositoryId());
        List<Stage> stages = jenkinsService.findStages(repository,request.getPipeline(),  request.getBuildInfo());

        return ResponseEntity.ok(stages);
    }

    @Parameter(name="查询项目流水线步骤执行日志")
    @RequestMapping(value = "/log", method = RequestMethod.POST)
    ResponseEntity<String> findExecutionLog(@RequestBody StepInfoRequest request) {
        PipelineRepositoryVo repository =
                pipelineRepositoryService.findPipelineRepository(request.getRepositoryId());
        String log = "";
        if (request.getStage()!=null){
             log = jenkinsService.findStepLog(repository, request.getStage().getLogUrl());
        }else{
             log = jenkinsService.findStepLog(repository, request.getStep().getLogUrl());
        }


        return ResponseEntity.ok(log);
    }

    @Parameter(name="查询项目流水线完整执行日志")
    @RequestMapping(value = "/fullLog", method = RequestMethod.GET,produces = "text/plain;charset=UTF-8")
    ResponseEntity<String> findExecutionFullLog(@RequestParam Long repositoryId,@RequestParam String pipelineName,
                                                @RequestParam String buildNo) {
        PipelineRepositoryVo repository =
                pipelineRepositoryService.findPipelineRepository(repositoryId);
        String log = "";
        log = jenkinsService.findExecutionFullLog(repository,pipelineName, buildNo);
        return ResponseEntity.ok(log);
    }
}
