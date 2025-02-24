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

package com.harbortek.helm.pipeline.service.impl;

import com.harbortek.helm.pipeline.jenkins.JenkinsApi;
import com.harbortek.helm.pipeline.service.JenkinsService;
import com.harbortek.helm.pipeline.vo.*;
import com.harbortek.helm.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JenkinsServiceImpl implements JenkinsService {

    private JenkinsApi getApi(PipelineRepositoryVo repository){
        return new JenkinsApi(repository.getServerUrl(),repository.getUserName(), repository.getPassword());
    }


    @Override
    public Boolean testConnection(PipelineRepositoryVo repository) {
        JenkinsApi jenkinsApi = getApi(repository);
        return StringUtils.isNotEmpty(jenkinsApi.getVersion()) ;
    }


    @Override
    public List<PipelineVo> findPipelines(PipelineRepositoryVo repository) {
        JenkinsApi jenkinsApi = getApi(repository);
        return jenkinsApi.findPipelines();
    }

    @Override
    public PipelineDetail getPipelineLastRunInfo(PipelineRepositoryVo repository, PipelineVo pipelineVo) {
        JenkinsApi jenkinsApi = getApi(repository);
        PipelineDetail detail = jenkinsApi.findPipeline(pipelineVo.getUrl());
        detail.setRepositoryId(repository.getId());
        return detail;
    }

    @Override
    public List<Stage> findStages(PipelineRepositoryVo repository, PipelineDetail pipeline, BuildInfo buildInfo) {
        JenkinsApi jenkinsApi = getApi(repository);
        List<Stage> stages =  jenkinsApi.findStages(pipeline,buildInfo);
        stages.forEach(stage -> {
            List<Step> steps = stage.getSteps();
            List<Step> newSteps =
            steps.stream().map(step->{
                Step newStep = jenkinsApi.findStep(step.getUrl());
                BeanCopyUtils.copyWithoutNullProperties(newStep,step);
                return step;
            }).collect(Collectors.toList());
            stage.setSteps(newSteps);
        });
        return stages;
    }

    @Override
    public Step findStep(PipelineRepositoryVo repository, Step step) {
        JenkinsApi jenkinsApi = getApi(repository);
        return jenkinsApi.findStep(step.getUrl());
    }

    @Override
    public String findStepLog(PipelineRepositoryVo repository, String logUrl) {
        JenkinsApi jenkinsApi = getApi(repository);
        return jenkinsApi.findStepLog(logUrl);
    }

    @Override
    public String findExecutionFullLog(PipelineRepositoryVo repository,String pipelineName, String buildNo) {
        JenkinsApi jenkinsApi = getApi(repository);
        return jenkinsApi.findExecutionFullLog(pipelineName,buildNo);
    }


}
