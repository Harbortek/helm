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

package com.harbortek.helm.scm.service;

import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.scm.vo.*;

import java.io.InputStream;
import java.util.List;

public interface CodeRepositoryService {
    List<CodeRepositoryVo> findCodeRepositories();
    CodeRepositoryVo createCodeRepository(CodeRepositoryVo codeRepositoryVo);

    void updateCodeRepository(CodeRepositoryVo codeRepositoryVo);

    CodeRepositoryVo findCodeRepository(Long repositoryId);


    List<BaseVo> executeFindProjects(Long repositoryId);

    void deleteCodeRepository(Long repositoryId);

    void bindProject(Long repositoryId, ProjectCodeRepositoryVo projectCodeRepositoryVo);

    void unbindProject(Long repositoryId, Long projectId);

    ProjectCodeRepositoryVo findCurrentBindProject(Long projectId);

    void resetWebHook(Long repositoryId);

    Boolean checkRepositoryUsedInProject(Long repositoryId);

    Long getProjectId(Long repositoryId, String gitlabProjectId);

    List<BranchVo> executeFindBranches(Long projectId, String keyword);

    List<FileVo> executeFindFiles(Long projectId, String path, String branchName, String parentId);

    List<TagVo> executeFindTags(Long projectId, String keyword);

    FileVo executeFindFileContent(Long projectId, String path, String branchName);

    ScmProjectVo executeFindProject(Long projectId);

    CommitVo executeFindLastCommit(Long projectId, String path, String branchName);

    InputStream executeDownloadFile(Long projectId, String path, String branchName);
}
