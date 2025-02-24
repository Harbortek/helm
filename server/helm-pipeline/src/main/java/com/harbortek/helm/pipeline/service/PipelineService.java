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

import com.harbortek.helm.pipeline.vo.PipelineVo;

import java.util.List;

public interface PipelineService {

    /**
     * 查找绑定到指定项目的所有流水线。
     *
     * @param projectId 项目的 ID
     * @return 流水线值对象列表
     */
    List<PipelineVo> findBindingPipelines(Long projectId);

    /**
     * 将流水线绑定到指定项目。
     *
     * @param projectId 项目的 ID
     * @param pipelines 要绑定的流水线列表
     */
    void bindPipelines(Long projectId, List<PipelineVo> pipelines);

    /**
     * 解除指定项目的流水线绑定。
     *
     * @param projectId 项目的 ID
     * @param pipelines 要解除绑定的流水线列表
     */
    void unbindPipelines(Long projectId, List<PipelineVo> pipelines);
}