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

import java.util.Collection;
import java.util.List;

import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.pipeline.entity.PipelineEntity;
import com.harbortek.helm.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class PipelineDao extends BaseJdbcDao {

    /**
     * 创建流水线
     * @param projectId
     * @param pipelines
     */
    public void createPipelines(Long projectId, List<PipelineEntity> pipelines) {
        saveAll(pipelines);
    }

    /**
     *  删除流水线 
     * @param projectId
     * @param pipelines
     */
    public void deletePipelines(Long projectId, List<PipelineEntity> pipelines) {
        if (pipelines.isEmpty()){
            return;
        }
        Collection<String> names = ObjectUtils.names(pipelines,"name");
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(PipelineEntity.Fields.projectId).is(projectId));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).in(names));
        Query query = Query.query(criteria);
        delete(query, PipelineEntity.class);
    }

    /**
     * 查询项目绑定的流水线
     * @param projectId
     * @return
     */
    public List<PipelineEntity> findBindingPipelines(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(PipelineEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        return find(query, PipelineEntity.class);
    }
}
