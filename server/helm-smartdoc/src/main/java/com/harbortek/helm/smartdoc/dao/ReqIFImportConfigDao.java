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
import com.harbortek.helm.smartdoc.entity.ReqIFImportConfigEntity;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ReqIFImportConfigDao extends BaseJdbcDao {
    public ReqIFImportConfigEntity createConfig(ReqIFImportConfigEntity config) {
        return save(config);
    }

    public ReqIFImportConfigEntity updateConfig(ReqIFImportConfigEntity config) {
        return save(config);
    }

    public void deleteConfig(Long id) {
        markAsDeleted(id, ReqIFImportConfigEntity.class);
    }

    public ReqIFImportConfigEntity findOneConfig(Long id) {
        return findById(id, ReqIFImportConfigEntity.class);
    }

    public List<ReqIFImportConfigEntity> findConfigs(Long projectId, String keyword) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        if (StringUtils.isNotEmpty(keyword)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%" + keyword + "%"));
        }
        if (ObjectUtils.isValid(projectId)) {
            criteria = criteria.and(
                    Criteria.where(ReqIFImportConfigEntity.Fields.projectId).is(projectId)
                            .and(ReqIFImportConfigEntity.Fields.scope)
                            .is(ReqIFImportConfigEntity.SCOPE_PROJECT).or(
                                    Criteria.where(ReqIFImportConfigEntity.Fields.scope)
                                            .is(ReqIFImportConfigEntity.SCOPE_GLOBAL))

                        );
        }
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.DESC, BaseEntity.Fields.createDate));
        return find(query, ReqIFImportConfigEntity.class);
    }


}
