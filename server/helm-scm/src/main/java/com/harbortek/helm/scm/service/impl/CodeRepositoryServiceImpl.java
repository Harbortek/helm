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

package com.harbortek.helm.scm.service.impl;

import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.scm.constants.CodeRepositoryTypes;
import com.harbortek.helm.scm.dao.CodeRepositoryDao;
import com.harbortek.helm.scm.entity.CodeRepositoryEntity;
import com.harbortek.helm.scm.entity.ProjectCodeRepositoryEntity;
import com.harbortek.helm.scm.service.CodeRepositoryService;
import com.harbortek.helm.scm.service.GitlabService;
import com.harbortek.helm.scm.vo.*;
import com.harbortek.helm.util.BeanCopyUtils;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CodeRepositoryServiceImpl implements CodeRepositoryService {

    @Autowired
    CodeRepositoryDao codeRepositoryDao;

    @Autowired
    GitlabService gitlabService;

    @Override
    public List<CodeRepositoryVo> findCodeRepositories() {
        List<CodeRepositoryEntity> entities =  codeRepositoryDao.findCodeRepositories();
        return DataUtils.toVo(entities,CodeRepositoryVo.class);
    }

    @Override
    public CodeRepositoryVo createCodeRepository(CodeRepositoryVo codeRepositoryVo) {
        codeRepositoryVo.setId(IDUtils.getId());
        CodeRepositoryEntity entity = DataUtils.toEntity(codeRepositoryVo, CodeRepositoryEntity.class);
        codeRepositoryDao.createCodeRepository(entity);
        return codeRepositoryVo;
    }

    @Override
    public void updateCodeRepository(CodeRepositoryVo codeRepositoryVo) {
        CodeRepositoryEntity existed = codeRepositoryDao.findById(codeRepositoryVo.getId());
        BeanCopyUtils.copyWithoutNullProperties(codeRepositoryVo,existed);
        codeRepositoryDao.updateCodeRepository(existed);
    }

    @Override
    public CodeRepositoryVo findCodeRepository(Long repositoryId) {
        CodeRepositoryEntity entity = codeRepositoryDao.findById(repositoryId);

        return DataUtils.toVo(entity, CodeRepositoryVo.class);
    }

    @Override
    public List<BaseVo> executeFindProjects(Long repositoryId) {
        //要排除已被绑定的项目

        CodeRepositoryEntity repository = codeRepositoryDao.findById(repositoryId);

        List<ProjectCodeRepositoryVo> excludes = DataUtils.toVo(
                codeRepositoryDao.findProjectCodeRepositoryByRepositoryId(repository.getId()),ProjectCodeRepositoryVo.class);

        List<BaseVo> projectNames = new ArrayList<>();
        if (CodeRepositoryTypes.GITLAB.equals(repository.getType())) {
            projectNames = gitlabService.executeFindProjects(DataUtils.toVo(repository, CodeRepositoryVo.class), excludes);
        }

        return projectNames;
    }

    @Override
    public void deleteCodeRepository(Long repositoryId) {
        codeRepositoryDao.deleteCodeRepository(repositoryId);
    }

    @Override
    public void bindProject(Long repositoryId, ProjectCodeRepositoryVo projectCodeRepositoryVo) {
        codeRepositoryDao.bindProject(repositoryId,projectCodeRepositoryVo);
    }

    @Override
    public void unbindProject(Long repositoryId, Long projectId) {
        codeRepositoryDao.unbindProject(repositoryId,projectId);
    }

    @Override
    public ProjectCodeRepositoryVo findCurrentBindProject(Long projectId) {
        ProjectCodeRepositoryEntity entity = codeRepositoryDao.findProjectCodeRepositoryByProjectId(projectId);
        if (entity==null){
            return null;
        }
        return DataUtils.toVo(entity,ProjectCodeRepositoryVo.class);
    }

    @Override
    public void resetWebHook(Long repositoryId) {
        CodeRepositoryEntity repository = codeRepositoryDao.findById(repositoryId);
        if (CodeRepositoryTypes.GITLAB.equals(repository.getType())) {
            gitlabService.resetWebHook(DataUtils.toVo(repository,CodeRepositoryVo.class));
        }
    }

    @Override
    public Boolean checkRepositoryUsedInProject(Long repositoryId) {
        return codeRepositoryDao.checkRepositoryUsedInProject(repositoryId);
    }

    @Override
    public Long getProjectId(Long repositoryId, String gitlabProjectId) {
        return codeRepositoryDao.getProjectId(repositoryId,gitlabProjectId);
    }

    @Override
    public List<BranchVo> executeFindBranches(Long projectId, String keyword) {
        ProjectCodeRepositoryEntity entity = codeRepositoryDao.findProjectCodeRepositoryByProjectId(projectId);
        if(ObjectUtils.isNotEmpty(entity)){
            CodeRepositoryEntity repository = codeRepositoryDao.findById(entity.getRepositoryId());

            if (CodeRepositoryTypes.GITLAB.equals(repository.getType())){
                Long gitLabProjectId = Long.valueOf(entity.getProjectIdOfRepository());
                return gitlabService.executeFindBranches(DataUtils.toVo(repository, CodeRepositoryVo.class) , gitLabProjectId,
                                                         keyword);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<FileVo> executeFindFiles(Long projectId, String path, String branchName, String parentId) {
        ProjectCodeRepositoryEntity entity = codeRepositoryDao.findProjectCodeRepositoryByProjectId(projectId);
        CodeRepositoryEntity repository = codeRepositoryDao.findById(entity.getRepositoryId());

        if (CodeRepositoryTypes.GITLAB.equals(repository.getType())){
            Long gitLabProjectId = Long.valueOf(entity.getProjectIdOfRepository());
            return gitlabService.executeFindFiles(DataUtils.toVo(repository, CodeRepositoryVo.class) , gitLabProjectId, path,
                                                  branchName, parentId);
        }
        return new ArrayList<>();
    }

    @Override
    public List<TagVo> executeFindTags(Long projectId, String keyword) {
        ProjectCodeRepositoryEntity entity = codeRepositoryDao.findProjectCodeRepositoryByProjectId(projectId);
        if(ObjectUtils.isNotEmpty(entity)){
            CodeRepositoryEntity repository = codeRepositoryDao.findById(entity.getRepositoryId());

            if (CodeRepositoryTypes.GITLAB.equals(repository.getType())){
                Long gitLabProjectId = Long.valueOf(entity.getProjectIdOfRepository());
                return gitlabService.executeFindTags(DataUtils.toVo(repository, CodeRepositoryVo.class) , gitLabProjectId, keyword);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public FileVo executeFindFileContent(Long projectId, String path, String branchName) {
        ProjectCodeRepositoryEntity entity = codeRepositoryDao.findProjectCodeRepositoryByProjectId(projectId);
        CodeRepositoryEntity repository = codeRepositoryDao.findById(entity.getRepositoryId());

        if (CodeRepositoryTypes.GITLAB.equals(repository.getType())){
            Long gitLabProjectId = Long.valueOf(entity.getProjectIdOfRepository());
            return gitlabService.executeFindFileContent(DataUtils.toVo(repository, CodeRepositoryVo.class) , gitLabProjectId, path, branchName);
        }
        return null;
    }

    @Override
    public ScmProjectVo executeFindProject(Long projectId) {
        ProjectCodeRepositoryEntity entity = codeRepositoryDao.findProjectCodeRepositoryByProjectId(projectId);
        if(ObjectUtils.isNotEmpty(entity)){
            CodeRepositoryEntity repository = codeRepositoryDao.findById(entity.getRepositoryId());

            if (CodeRepositoryTypes.GITLAB.equals(repository.getType())){
                Long gitLabProjectId = Long.valueOf(entity.getProjectIdOfRepository());
                return gitlabService.executeFindProject(DataUtils.toVo(repository, CodeRepositoryVo.class) , gitLabProjectId);
            }
        }
        return null;
    }

    @Override
    public CommitVo executeFindLastCommit(Long projectId, String path, String branchName) {
        ProjectCodeRepositoryEntity entity = codeRepositoryDao.findProjectCodeRepositoryByProjectId(projectId);
        CodeRepositoryEntity repository = codeRepositoryDao.findById(entity.getRepositoryId());

        if (CodeRepositoryTypes.GITLAB.equals(repository.getType())){
            Long gitLabProjectId = Long.valueOf(entity.getProjectIdOfRepository());
            return gitlabService.executeFindLastCommit(DataUtils.toVo(repository, CodeRepositoryVo.class) , gitLabProjectId, path, branchName);
        }
        return null;
    }

    @Override
    public InputStream executeDownloadFile(Long projectId, String path, String branchName) {
        ProjectCodeRepositoryEntity entity = codeRepositoryDao.findProjectCodeRepositoryByProjectId(projectId);
        CodeRepositoryEntity repository = codeRepositoryDao.findById(entity.getRepositoryId());

        if (CodeRepositoryTypes.GITLAB.equals(repository.getType())){
            Long gitLabProjectId = Long.valueOf(entity.getProjectIdOfRepository());
            return gitlabService.executeDownloadFile(DataUtils.toVo(repository, CodeRepositoryVo.class) , gitLabProjectId, path, branchName);
        }
        return null;
    }

}
