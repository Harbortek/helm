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
import com.harbortek.helm.smartdoc.entity.ReqIFExportJobEntity;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ReqIFExportJobDao extends BaseJdbcDao {
    public void saveJob(ReqIFExportJobEntity job) {
        if (job.getId()==null){
            deleteJobByPageId(job.getPageId());
            job.setId(IDUtils.getId());
        }

        save(job);
    }

    public void deleteJob(Long id) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.id).is(id));
        Query query = Query.query(criteria);
        delete(id, ReqIFExportJobEntity.class);
    }

    public ReqIFExportJobEntity findOneJob(Long id) {
        return findById(id, ReqIFExportJobEntity.class);
    }

    public ReqIFExportJobEntity findJobByPageId(Long pageId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));

        if (ObjectUtils.isValid(pageId)){
            criteria = criteria.and(Criteria.where(ReqIFExportJobEntity.Fields.pageId).is(pageId));
        }
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.DESC, BaseEntity.Fields.createDate));

        return findOne(query, ReqIFExportJobEntity.class);
    }


    public void deleteJobByPageId(Long pageId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));

        if (ObjectUtils.isValid(pageId)){
            criteria = criteria.and(Criteria.where(ReqIFExportJobEntity.Fields.pageId).is(pageId));
        }
        Query query = Query.query(criteria);
        delete(query, ReqIFExportJobEntity.class);
    }

}
