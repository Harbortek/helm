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

package com.harbortek.helm.tracker.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.tracker.entity.plan.TargetVersionEntity;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class TargetVersionDao extends BaseJdbcDao {

    public TargetVersionEntity createVersion(TargetVersionEntity versionEntity) {
        versionEntity.setId(IDUtils.getId());
        save(versionEntity);
        return versionEntity;
    }

    public TargetVersionEntity updateVersion(TargetVersionEntity versionEntity) {
        save(versionEntity);
        return versionEntity;
    }

    public void deleteVersion(Long versionId) {
        markAsDeleted(versionId, TargetVersionEntity.class);
    }

    public List<TargetVersionEntity> findAll(Long projectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(TargetVersionEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        return find(query, TargetVersionEntity.class);
    }

    public void batchCreateTargetVersions(List<TargetVersionEntity> targetVersions) {
        saveAll(targetVersions);
    }
}
