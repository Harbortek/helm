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

import com.harbortek.helm.pipeline.vo.PipelineRepositoryVo;

import java.util.List;

public interface PipelineRepositoryService {

    /**
     * 查找所有管道仓库。
     *
     * @return 管道仓库值对象列表
     */
    List<PipelineRepositoryVo> findPipelineRepositories();

    /**
     * 创建一个新的管道仓库。
     *
     * @param pipelineRepositoryVo 管道仓库值对象
     * @return 创建的管道仓库值对象
     */
    PipelineRepositoryVo createPipelineRepository(PipelineRepositoryVo pipelineRepositoryVo);

    /**
     * 更新管道仓库。
     *
     * @param pipelineRepositoryVo 管道仓库值对象
     */
    void updatePipelineRepository(PipelineRepositoryVo pipelineRepositoryVo);

    /**
     * 删除管道仓库。
     *
     * @param pipelineRepositoryId 管道仓库的 ID
     */
    void deletePipelineRepository(Long pipelineRepositoryId);

    /**
     * 查找指定 ID 的管道仓库。
     *
     * @param pipelineRepositoryId 管道仓库的 ID
     * @return 管道仓库值对象
     */
    PipelineRepositoryVo findPipelineRepository(Long pipelineRepositoryId);

}