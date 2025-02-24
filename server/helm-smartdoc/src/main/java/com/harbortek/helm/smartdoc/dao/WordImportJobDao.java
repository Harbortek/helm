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
import com.harbortek.helm.smartdoc.entity.WordImportJobEntity;
import com.harbortek.helm.smartdoc.entity.WordImportJobHistoryEntity;
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
public class WordImportJobDao extends BaseJdbcDao {
    public void createJob(WordImportJobEntity job) {
        job.setRevision(1L);
        save(job);
    }

    public void updateJob(WordImportJobEntity job) {
        WordImportJobEntity old = findOneJob(job.getId());
        if (DocUtils.diff(old.getBlocksJSON(),job.getBlocksJSON())){
            job.setRevision(job.getRevision()+1);
            //将老数据保存到历史表
            WordImportJobHistoryEntity history =
                    DataUtils.toEntity(old, WordImportJobHistoryEntity.class);
            history.setId(IDUtils.getId());
            history.setObjectId(job.getId());

            insert(history);

            save(job);
        }
    }

    public void deleteJob(Long id) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).is(id));
        Query query = Query.query(criteria);
        delete(query, WordImportJobHistoryEntity.class);
        delete(id, WordImportJobEntity.class);
    }

    public WordImportJobEntity findOneJob(Long id) {
        return findById(id, WordImportJobEntity.class);
    }

    public List<WordImportJobEntity> findJobs(Long projectId, Long pageId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));

        if (ObjectUtils.isValid(projectId)) {
            criteria = criteria.and(Criteria.where(WordImportJobEntity.Fields.projectId).is(projectId));

        }
        if (ObjectUtils.isValid(pageId)){
            criteria = criteria.and(Criteria.where(WordImportJobEntity.Fields.pageId).is(pageId));
        }
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.DESC, BaseEntity.Fields.createDate));
        return find(query, WordImportJobEntity.class);
    }


    public void deleteJobs(Long projectId, Long pageId) {
        List<WordImportJobEntity> jobs = findJobs(projectId,pageId);
        for (WordImportJobEntity job : jobs){
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
        WordImportJobHistoryEntity history = findOne(query,WordImportJobHistoryEntity.class);
        if (history!=null) {
            delete(history);
            WordImportJobEntity job = DataUtils.toEntity(history, WordImportJobEntity.class);
            job.setId(id);
            update(job);
            CacheUtils.evict(id,WordImportJobEntity.class);
        }
    }
}
