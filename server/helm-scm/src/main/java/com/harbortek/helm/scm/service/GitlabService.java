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

/**
 * 用于与 GitLab 仓库交互的服务接口。
 */
public interface GitlabService {

    /**
     * 使用提供的授权码获取访问令牌。
     *
     * @param repositoryId 仓库的 ID
     * @param code 授权码
     */
    void createAccessTokenByCode(Long repositoryId, String code);

    /**
     * 查找指定仓库中的项目，排除提供的项目列表。
     *
     * @param repository 仓库信息
     * @param excludes 要排除的项目列表
     * @return 项目值对象列表
     */
    List<BaseVo> executeFindProjects(CodeRepositoryVo repository, List<ProjectCodeRepositoryVo> excludes);

    /**
     * 重置指定仓库的 Webhook。
     *
     * @param repository 仓库信息
     */
    void resetWebHook(CodeRepositoryVo repository);

    /**
     * 查找指定仓库和项目中的文件。
     *
     * @param repository 仓库信息
     * @param projectId 项目的 ID
     * @param path 查找文件的路径
     * @param branchName 分支名称
     * @param parentId 父目录的 ID
     * @return 文件值对象列表
     */
    List<FileVo> executeFindFiles(CodeRepositoryVo repository, Long projectId, String path, String branchName, String parentId);

    /**
     * 查找指定仓库和项目中的分支。
     *
     * @param repository 仓库信息
     * @param gitLabProjectId GitLab 项目的 ID
     * @param keyword 查找分支的关键字
     * @return 分支值对象列表
     */
    List<BranchVo> executeFindBranches(CodeRepositoryVo repository, Long gitLabProjectId, String keyword);

    /**
     * 查找指定仓库和项目中的标签。
     *
     * @param repository 仓库信息
     * @param gitLabProjectId GitLab 项目的 ID
     * @param keyword 查找标签的关键字
     * @return 标签值对象列表
     */
    List<TagVo> executeFindTags(CodeRepositoryVo repository, Long gitLabProjectId, String keyword);

    /**
     * 查找指定仓库和项目中文件的内容。
     *
     * @param repository 仓库信息
     * @param gitLabProjectId GitLab 项目的 ID
     * @param path 文件路径
     * @param branchName 分支名称
     * @return 文件值对象
     */
    FileVo executeFindFileContent(CodeRepositoryVo repository, Long gitLabProjectId, String path, String branchName);

    /**
     * 查找指定仓库中的项目。
     *
     * @param repository 仓库信息
     * @param gitLabProjectId GitLab 项目的 ID
     * @return SCM 项目值对象
     */
    ScmProjectVo executeFindProject(CodeRepositoryVo repository, Long gitLabProjectId);

    /**
     * 查找指定仓库和项目中文件的最后一次提交。
     *
     * @param repository 仓库信息
     * @param gitLabProjectId GitLab 项目的 ID
     * @param path 文件路径
     * @param branchName 分支名称
     * @return 提交值对象
     */
    CommitVo executeFindLastCommit(CodeRepositoryVo repository, Long gitLabProjectId, String path, String branchName);

    /**
     * 从指定仓库和项目中下载文件。
     *
     * @param repository 仓库信息
     * @param gitLabProjectId GitLab 项目的 ID
     * @param path 文件路径
     * @param branchName 分支名称
     * @return 用于读取文件内容的 InputStream
     */
    InputStream executeDownloadFile(CodeRepositoryVo repository, Long gitLabProjectId, String path, String branchName);
}