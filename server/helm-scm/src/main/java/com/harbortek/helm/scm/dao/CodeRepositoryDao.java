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

package com.harbortek.helm.scm.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.scm.entity.CodeRepositoryEntity;
import com.harbortek.helm.scm.entity.ProjectCodeRepositoryEntity;
import com.harbortek.helm.scm.vo.ProjectCodeRepositoryVo;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class CodeRepositoryDao extends BaseJdbcDao {

    public void createCodeRepository(CodeRepositoryEntity codeRepository) {
        save(codeRepository);
    }

    public CodeRepositoryEntity findById(Long id) {
        return findById(id, CodeRepositoryEntity.class);
    }

    public void updateCodeRepository(CodeRepositoryEntity codeRepository) {
        save(codeRepository);
    }

    public List<CodeRepositoryEntity> findCodeRepositories() {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        Query query = Query.query(criteria);
        return find(query, CodeRepositoryEntity.class);

    }

    public void deleteCodeRepository(Long repositoryId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(ProjectCodeRepositoryEntity.Fields.repositoryId).is(repositoryId));
        Query query = Query.query(criteria);
        delete(query, ProjectCodeRepositoryEntity.class);

        markAsDeleted(repositoryId, CodeRepositoryEntity.class);
    }

    public ProjectCodeRepositoryEntity findProjectCodeRepositoryByProjectId(Long projectId){
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectCodeRepositoryEntity.Fields.projectId).is(projectId));
        Query query = Query.query(criteria);
        return findOne(query, ProjectCodeRepositoryEntity.class);
    }

    public void bindProject(Long repositoryId, ProjectCodeRepositoryVo projectCodeRepositoryVo) {
        ProjectCodeRepositoryEntity entity = findProjectCodeRepositoryByProjectId(projectCodeRepositoryVo.getProjectId());
        if (entity==null){
            entity =
                    ProjectCodeRepositoryEntity.builder().id(IDUtils.getId())
                                               .projectId(projectCodeRepositoryVo.getProjectId()).build();
        }
        entity.setRepositoryId(repositoryId);
        entity.setProjectNameOfRepository(projectCodeRepositoryVo.getProjectNameOfRepository());
        entity.setProjectIdOfRepository(projectCodeRepositoryVo.getProjectIdOfRepository());
        save(entity);
    }

    public void unbindProject(Long repositoryId, Long projectId) {
        ProjectCodeRepositoryEntity entity = findProjectCodeRepositoryByProjectId(projectId);
        if (entity!=null){
            delete(entity.getId(), ProjectCodeRepositoryEntity.class);
        }
    }

    public Boolean checkRepositoryUsedInProject(Long repositoryId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectCodeRepositoryEntity.Fields.repositoryId).is(repositoryId));
        Query query = Query.query(criteria);
        return exists(query, ProjectCodeRepositoryEntity.class);
    }

    public Long getProjectId(Long repositoryId, String gitlabProjectId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectCodeRepositoryEntity.Fields.repositoryId).is(repositoryId));
        criteria = criteria.and(Criteria.where(ProjectCodeRepositoryEntity.Fields.projectIdOfRepository).is(gitlabProjectId));
        Query query = Query.query(criteria);
        return Objects.requireNonNull(findOne(query, ProjectCodeRepositoryEntity.class)).getProjectId();
    }

    public List<ProjectCodeRepositoryEntity> findProjectCodeRepositoryByRepositoryId(Long repositoryId) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(Boolean.FALSE));
        criteria = criteria.and(Criteria.where(ProjectCodeRepositoryEntity.Fields.repositoryId).is(repositoryId));
        Query query = Query.query(criteria);
        return find(query, ProjectCodeRepositoryEntity.class);
    }
}
