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

import com.harbortek.helm.pipeline.dao.PipelineRepositoryDao;
import com.harbortek.helm.pipeline.entity.PipelineRepositoryEntity;
import com.harbortek.helm.pipeline.service.PipelineRepositoryService;
import com.harbortek.helm.pipeline.vo.PipelineRepositoryVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PipelineRepositoryServiceImpl implements PipelineRepositoryService {
    @Autowired
    PipelineRepositoryDao pipelineDao;


    @Override
    public List<PipelineRepositoryVo> findPipelineRepositories() {
        return DataUtils.toVo(pipelineDao.findPipelineRepositories(), PipelineRepositoryVo.class) ;
    }

    @Override
    public PipelineRepositoryVo createPipelineRepository(PipelineRepositoryVo pipelineVo) {
        pipelineVo.setId(IDUtils.getId());
        PipelineRepositoryEntity pipelineEntity = DataUtils.toEntity(pipelineVo, PipelineRepositoryEntity.class);

        pipelineDao.createPipelineRepository(pipelineEntity);
        return pipelineVo;
    }

    @Override
    public void updatePipelineRepository(PipelineRepositoryVo pipelineVo) {
        PipelineRepositoryEntity pipelineEntity = DataUtils.toEntity(pipelineVo, PipelineRepositoryEntity.class);

        pipelineDao.updatePipelineRepository(pipelineEntity);
    }

    @Override
    public void deletePipelineRepository(Long pipelineId) {
        pipelineDao.deletePipelineRepository(pipelineId);
    }

    @Override
    public PipelineRepositoryVo findPipelineRepository(Long pipelineId) {
        return DataUtils.toVo(pipelineDao.findById(pipelineId), PipelineRepositoryVo.class);
    }




}
