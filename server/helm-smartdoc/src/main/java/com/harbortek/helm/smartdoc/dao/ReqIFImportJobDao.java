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

package com.harbortek.helm.smartdoc.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.entity.HistoryBaseEntity;
import com.harbortek.helm.smartdoc.entity.ReqIFImportJobEntity;
import com.harbortek.helm.smartdoc.entity.ReqIFImportJobHistoryEntity;
import com.harbortek.helm.smartdoc.utils.DocUtils;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ReqIFImportJobDao extends BaseJdbcDao {
    public void createJob(ReqIFImportJobEntity job) {
        job.setRevision(1L);
        insert(job);
    }

    public void updateJob(ReqIFImportJobEntity job) {
        ReqIFImportJobEntity old = findOneJob(job.getId());
        if (DocUtils.diff(old.getBlocksJSON(),job.getBlocksJSON())){
            job.setRevision(job.getRevision()+1);
            //将老数据保存到历史表
            ReqIFImportJobHistoryEntity history =
                    DataUtils.toEntity(old, ReqIFImportJobHistoryEntity.class);
            history.setId(IDUtils.getId());
            history.setObjectId(job.getId());
            insert(history);

            update(job);
        }
    }

    public void deleteJob(Long id) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).is(id));
        Query query = Query.query(criteria);
        delete(query, ReqIFImportJobHistoryEntity.class);
        delete(id, ReqIFImportJobEntity.class);
    }

    public ReqIFImportJobEntity findOneJob(Long id) {
        return findById(id, ReqIFImportJobEntity.class);
    }

    public List<ReqIFImportJobEntity> findJobs(Long projectId, Long pageId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));

        if (ObjectUtils.isValid(projectId)) {
            criteria = criteria.and(Criteria.where(ReqIFImportJobEntity.Fields.projectId).is(projectId));

        }
        if (ObjectUtils.isValid(pageId)){
            criteria = criteria.and(Criteria.where(ReqIFImportJobEntity.Fields.pageId).is(pageId));
        }
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.DESC, BaseEntity.Fields.createDate));
        return find(query, ReqIFImportJobEntity.class);
    }


    public void deleteJobs(Long projectId, Long pageId) {
        List<ReqIFImportJobEntity> jobs = findJobs(projectId,pageId);
        for (ReqIFImportJobEntity job : jobs){
            deleteJob(job.getId());
        }
    }

    public void withdraw(Long id) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(HistoryBaseEntity.Fields.objectId).is(id));
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.DESC, HistoryBaseEntity.Fields.revision));
        query.limit(1);
        ReqIFImportJobHistoryEntity history = findOne(query,ReqIFImportJobHistoryEntity.class);
        if (history!=null) {
            delete(history);
            ReqIFImportJobEntity job = DataUtils.toEntity(history, ReqIFImportJobEntity.class);
            job.setId(id);
            update(job);
            CacheUtils.evict(id,ReqIFImportJobEntity.class);
        }
    }
}
