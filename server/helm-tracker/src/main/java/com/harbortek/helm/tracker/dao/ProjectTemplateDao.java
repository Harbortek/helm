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
import com.harbortek.helm.tracker.entity.project.ProjectEntity;
import com.harbortek.helm.tracker.entity.template.ProjectTemplateEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ProjectTemplateDao extends BaseJdbcDao {

    public ProjectTemplateEntity createProjectTemplate(ProjectTemplateEntity template) {
        return save(template);
    }

    public ProjectTemplateEntity updateProjectTemplate(ProjectTemplateEntity template) {
        return save(template);
    }

    public void deleteProjectTemplate(ProjectTemplateEntity template) {
        markAsDeleted(template.getId(), ProjectTemplateEntity.class);
    }


    public ProjectTemplateEntity findById(Long id) {
        return findById(id, ProjectTemplateEntity.class);
    }

    public ProjectTemplateEntity findByName(String name) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.name).is(name));
        Query query = Query.query(criteria);
        return findOne(query, ProjectTemplateEntity.class);
    }


    public Page<ProjectTemplateEntity> findAll(Pageable pageable) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        Query query = Query.query(criteria);
        return find(query,pageable, ProjectTemplateEntity.class);
    }


    public void deleteAll() {
        deleteAll(ProjectTemplateEntity.class);
    }
}
