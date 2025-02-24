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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.harbortek.helm.pipeline.dao.PipelineDao;
import com.harbortek.helm.pipeline.entity.PipelineEntity;
import com.harbortek.helm.pipeline.service.PipelineService;
import com.harbortek.helm.pipeline.vo.PipelineVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PipelineServiceImpl implements PipelineService {
    @Autowired
    PipelineDao pipelineDao;

    @Override
    public List<PipelineVo> findBindingPipelines(Long projectId) {
        List<PipelineEntity> pipelineEntities = pipelineDao.findBindingPipelines(projectId);

        return DataUtils.toVo(pipelineEntities, PipelineVo.class);
    }

    @Override
    public void bindPipelines(Long projectId, List<PipelineVo> pipelines) {
        List<PipelineEntity> pipelineEntities =
                pipelines.stream().map(item -> {
                    return PipelineEntity.builder().id(IDUtils.getId()).name(item.getName()).projectId(projectId)
                                         .repositoryId(item.getRepositoryId()).multiBranch(item.isMultiBranch())
                                         .url(item.getUrl()).build();
                }).collect(Collectors.toList());
        if (!pipelineEntities.isEmpty()) {
            pipelineDao.createPipelines(projectId, pipelineEntities);
        }
    }

    @Override
    public void unbindPipelines(Long projectId, List<PipelineVo> pipelines) {
        List<PipelineEntity> pipelineEntities =
                pipelines.stream().map(item -> {
                    return PipelineEntity.builder().id(IDUtils.getId()).name(item.getName()).projectId(projectId)
                                         .repositoryId(item.getRepositoryId()).build();
                }).collect(Collectors.toList());
        if (!pipelineEntities.isEmpty()) {
            pipelineDao.deletePipelines(projectId, pipelineEntities);
        }
    }
}
