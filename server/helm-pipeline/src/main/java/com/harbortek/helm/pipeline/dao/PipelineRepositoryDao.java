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

package com.harbortek.helm.pipeline.dao;

import java.util.List;

import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.pipeline.entity.PipelineRepositoryEntity;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PipelineRepositoryDao extends BaseJdbcDao {

    /**
     * 创建流水线仓库。
     * @param pipelineEntity
     */
    public void createPipelineRepository(PipelineRepositoryEntity pipelineEntity) {
        save(pipelineEntity);
    }

    /**
     *  查询流水线仓库。
     * @param id
     * @return
     */
    public PipelineRepositoryEntity findById(Long id) {
        return findById(id, PipelineRepositoryEntity.class);
    }

    /**
     * 更新流水线仓库。
     * @param pipelineEntity
     */
    public void updatePipelineRepository(PipelineRepositoryEntity pipelineEntity) {
        save(pipelineEntity);
    }

    /**
     * 查询流水线仓库。
     * @return
     */
    public List<PipelineRepositoryEntity> findPipelineRepositories() {

        Criteria criteria = Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE);
        Query query = Query.query(criteria);
        return find(query, PipelineRepositoryEntity.class);

    }

    /**
     * 删除流水线仓库。
     * @param id
     */
    public void deletePipelineRepository(Long id) {
        markAsDeleted(id, PipelineRepositoryEntity.class);
    }
}
