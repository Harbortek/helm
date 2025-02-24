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
import com.harbortek.helm.smartdoc.entity.ReqIFExportTemplateEntity;
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
public class ReqIFExportTemplateDao extends BaseJdbcDao {
    public ReqIFExportTemplateEntity createTemplate(ReqIFExportTemplateEntity template) {
        return save(template);
    }

    public ReqIFExportTemplateEntity updateTemplate(ReqIFExportTemplateEntity template) {
        return save(template);
    }

    public void deleteTemplate(Long id) {
        markAsDeleted(id, ReqIFExportTemplateEntity.class);
    }

    public ReqIFExportTemplateEntity findOneTemplate(Long id) {
        return findById(id, ReqIFExportTemplateEntity.class);
    }

    public List<ReqIFExportTemplateEntity> findTemplates(Long projectId, String keyword) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        if (StringUtils.isNotEmpty(keyword)) {
            criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).like("%" + keyword + "%"));
        }
        if (ObjectUtils.isValid(projectId)) {
            criteria = criteria.and(
                    Criteria.where(ReqIFExportTemplateEntity.Fields.projectId).is(projectId)
                            .and(ReqIFExportTemplateEntity.Fields.scope)
                            .is(ReqIFExportTemplateEntity.SCOPE_PROJECT)
                    .or(Criteria.where(ReqIFExportTemplateEntity.Fields.scope)
                                .is(ReqIFExportTemplateEntity.SCOPE_GLOBAL))

                        );
        }
        Query query = Query.query(criteria);
        query.sort(Sort.by(Sort.Direction.DESC, BaseEntity.Fields.createDate));
        return find(query, ReqIFExportTemplateEntity.class);
    }


}
