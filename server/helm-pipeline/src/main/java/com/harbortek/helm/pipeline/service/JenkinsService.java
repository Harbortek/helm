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

package com.harbortek.helm.pipeline.service;

import com.harbortek.helm.pipeline.vo.*;

import java.util.List;

public interface JenkinsService {
    /**
     * 测试连接
     *
     * @param repository 管道仓库信息
     * @return 连接是否成功
     */
    Boolean testConnection(PipelineRepositoryVo repository);

    /**
     * 获取流水线列表
     *
     * @param repository 管道仓库信息
     * @return 流水线值对象列表
     */
    List<PipelineVo> findPipelines(PipelineRepositoryVo repository);

    /**
     * 获取流水线最后一次运行信息
     *
     * @param repository 管道仓库信息
     * @param pipelineVo 流水线值对象
     * @return 流水线详细信息
     */
    PipelineDetail getPipelineLastRunInfo(PipelineRepositoryVo repository, PipelineVo pipelineVo);

    /**
     * 获取流水线的阶段信息
     *
     * @param repository 管道仓库信息
     * @param pipeline 流水线详细信息
     * @param buildInfo 构建信息
     * @return 阶段信息列表
     */
    List<Stage> findStages(PipelineRepositoryVo repository, PipelineDetail pipeline, BuildInfo buildInfo);

    /**
     * 获取步骤信息
     *
     * @param repository 管道仓库信息
     * @param step 步骤信息
     * @return 步骤信息
     */
    Step findStep(PipelineRepositoryVo repository, Step step);

    /**
     * 获取步骤日志
     *
     * @param repository 管道仓库信息
     * @param url 日志 URL
     * @return 步骤日志
     */
    String findStepLog(PipelineRepositoryVo repository, String url);

    /**
     * 获取执行的完整日志
     *
     * @param repository 管道仓库信息
     * @param pipelineName 流水线名称
     * @param buildNo 构建编号
     * @return 完整日志
     */
    String findExecutionFullLog(PipelineRepositoryVo repository, String pipelineName, String buildNo);
}