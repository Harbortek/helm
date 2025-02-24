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

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.common.vo.BaseVo;
import com.harbortek.helm.scm.dao.CodeRepositoryDao;
import com.harbortek.helm.scm.entity.CodeRepositoryEntity;
import com.harbortek.helm.scm.service.GitlabService;
import com.harbortek.helm.scm.utils.GitLabRequestUtils;
import com.harbortek.helm.scm.vo.*;
import com.harbortek.helm.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GitlabServiceImpl implements GitlabService {
    @Autowired
    CodeRepositoryDao codeRepositoryDao;

    private RestTemplate getRestTemplate(String serverUrl, String token) {

        if (serverUrl.toUpperCase().startsWith("HTTPS")) {
            return GitLabRequestUtils.httpsRestTemplate(token);
        } else {
            return GitLabRequestUtils.httpRestTemplate(token);
        }
    }

    @SneakyThrows
    @Override
    public List<BaseVo> executeFindProjects(CodeRepositoryVo repository, List<ProjectCodeRepositoryVo> excludes) {
        GitLabApi gitLabApi = createGitLabApi(repository);

        List<Long> excludeIds =
                excludes.stream().map(item -> Long.parseLong(item.getProjectIdOfRepository()))
                        .collect(Collectors.toList());
        SSLUtils.ignoreSsl();
        return gitLabApi.getProjectApi().getProjects().stream().map(item -> {
            if (!excludeIds.contains(item.getId())) {
                return BaseVo.builder().id(item.getId()).name(item.getNameWithNamespace()).build();
            }
            return null;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BranchVo> executeFindBranches(CodeRepositoryVo repository, Long gitLabProjectId, String keyword) {
        GitLabApi gitLabApi = createGitLabApi(repository);
        try {
            Pager<Branch> items = gitLabApi.getRepositoryApi().getBranches(gitLabProjectId, keyword, 10);
            return items.stream().map(item -> {
                BranchVo branchVo =
                        BranchVo.builder().name(item.getName()).canPush(item.getCanPush())
                                .developersCanPush(item.getDevelopersCanPush())
                                .developersCanMerge(item.getDevelopersCanMerge())
                                .merged(item.getMerged()).isDefault(item.getDefault()).isProtected(item.getProtected())
                                .build();

                if (item.getCommit() != null) {
                    Commit commit = item.getCommit();
                    CommitVo commitVo = getCommitVo(commit);
                    branchVo.setLastCommit(commitVo);
                }
                return branchVo;
            }).collect(Collectors.toList());
        } catch (GitLabApiException e) {
            log.error(e.getMessage(), e);
            ServiceException.throwException("查询GitLab仓库失败!");
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<TagVo> executeFindTags(CodeRepositoryVo repository, Long gitLabProjectId, String keyword) {
        GitLabApi gitLabApi = createGitLabApi(repository);
        try {
            Pager<Tag> items = gitLabApi.getTagsApi().getTags(gitLabProjectId, Constants.TagOrderBy.UPDATED,
                                                              Constants.SortOrder.DESC, keyword, 10);
            return items.stream().map(item -> {
                TagVo tagVo =
                        TagVo.builder().name(item.getName()).message(item.getMessage()).build();

                if (item.getCommit() != null) {
                    Commit commit = item.getCommit();
                    CommitVo commitVo = getCommitVo(commit);
                    tagVo.setCommit(commitVo);
                }
                return tagVo;
            }).collect(Collectors.toList());
        } catch (GitLabApiException e) {
            log.error(e.getMessage(), e);
            ServiceException.throwException("查询GitLab仓库失败!");
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<FileVo> executeFindFiles(CodeRepositoryVo repository, Long projectId, String path, String branchName,
                                         String parentId) {
        GitLabApi gitLabApi = createGitLabApi(repository);
        try {
            List<TreeItem> items = gitLabApi.getRepositoryApi().getTree(projectId, path, branchName, false);
            List<FileVo> files = items.stream().map(item -> {
                return
                        FileVo.builder().fileId(item.getId()).name(item.getName()).path(item.getPath())
                              .directory(item.getType().equals(
                                      TreeItem.Type.TREE)).parentId(parentId).build();
            }).collect(Collectors.toList());


            for (FileVo file : files) {
                if (file.getDirectory()) {
                    List<Commit> commits = gitLabApi.getCommitsApi().getCommits(projectId, branchName, file.getPath());
                    if (!commits.isEmpty()) {
                        CommitVo commitVo = getCommitVo(commits.get(0));
                        file.setLastCommit(commitVo);
                    }
                } else {
                    Optional<RepositoryFile> repositoryFile =
                            gitLabApi.getRepositoryFileApi().getOptionalFileInfo(projectId,
                                                                                 file.getPath(),
                                                                                 branchName);
                    if (repositoryFile.isPresent()) {
                        String lastCommitId = repositoryFile.get().getLastCommitId();
                        Commit commit = gitLabApi.getCommitsApi().getCommit(projectId, lastCommitId);
                        CommitVo commitVo = getCommitVo(commit);
                        file.setLastCommit(commitVo);
                    }
                }
            }
            return files;

        } catch (GitLabApiException e) {
            log.error(e.getMessage(), e);
            ServiceException.throwException("查询GitLab仓库失败!");
        }
        return Collections.EMPTY_LIST;
    }

    private static CommitVo getCommitVo(Commit commit) {
        return CommitVo.builder().commitId(commit.getId()).shortId(commit.getShortId())
                       .authorName(commit.getAuthorName()).authoredDate(commit.getAuthoredDate())
                       .authorEmail(commit.getAuthorEmail()).committedDate(commit.getCommittedDate())
                       .committerEmail(commit.getCommitterEmail())
                       .committerName(commit.getCommitterName())
                       .message(commit.getMessage()).parentIds(commit.getParentIds()).projectId(
                        commit.getProjectId()).status(commit.getStatus()).title(commit.getTitle())
                       .timestamp(commit.getTimestamp())
                       .url(commit.getUrl()).webUrl(commit.getWebUrl()).build();
    }

    @Override
    @Transactional(readOnly = false)
    public FileVo executeFindFileContent(CodeRepositoryVo repository, Long projectId, String path,
                                         String branchName) {
        GitLabApi gitLabApi = createGitLabApi(repository);

        try {
            RepositoryFile repositoryFile = gitLabApi.getRepositoryFileApi().getFile(projectId, path, branchName);
            FileVo file =
                    FileVo.builder().fileId(repositoryFile.getBlobId()).name(repositoryFile.getFileName())
                          .path(repositoryFile.getFilePath())
                          .directory(false).contentSha256(repositoryFile.getContentSha256())
                          .encoding(repositoryFile.getEncoding().name()).size(repositoryFile.getSize())
                          .content(repositoryFile.getContent()).build();
            String lastCommitId = repositoryFile.getLastCommitId();
            Commit commit = gitLabApi.getCommitsApi().getCommit(projectId, lastCommitId);
            CommitVo commitVo = getCommitVo(commit);
            file.setLastCommit(commitVo);
            InputStream inputSteam = gitLabApi.getRepositoryFileApi().getRawFile(projectId, branchName, path);
            file.setType(FileTypeUtils.isText(inputSteam) ? "text" : "binary");
            inputSteam.close();
            return file;
        } catch (GitLabApiException | IOException e) {
            log.error(e.getMessage(), e);
            ServiceException.throwException("查询GitLab仓库失败!");
        }
        return null;
    }

    @Override
    public ScmProjectVo executeFindProject(CodeRepositoryVo repository, Long projectId) {
        GitLabApi gitLabApi = createGitLabApi(repository);

        try {
            Project project = gitLabApi.getProjectApi().getProject(projectId);
            ScmProjectVo scmProjectVo = new ScmProjectVo();
            BeanCopyUtils.copyWithoutNullProperties(project, scmProjectVo);
            return scmProjectVo;
        } catch (GitLabApiException e) {
            log.error(e.getMessage(), e);
            ServiceException.throwException("查询GitLab仓库失败!");
        }
        return null;
    }

    @Override
    public CommitVo executeFindLastCommit(CodeRepositoryVo repository, Long projectId, String path,
                                          String branchName) {

        GitLabApi gitLabApi = createGitLabApi(repository);
        try {
            if (StringUtils.startsWith(path, "/")) {
                path = StringUtils.removeStart(path, "/");
            }
            Date since = com.harbortek.helm.util.DateUtils.toDate("1900-01-01 00:00:00");
            Date until = new Date();
            List<Commit> commits = gitLabApi.getCommitsApi().getCommits(projectId, branchName, path);
            if (!commits.isEmpty()) {
                return getCommitVo(commits.get(0));
            }
        } catch (GitLabApiException e) {
            log.error(e.getMessage(), e);
            ServiceException.throwException("查询GitLab仓库失败!");
        }
        return null;
    }

    @Override
    public InputStream executeDownloadFile(CodeRepositoryVo repository, Long projectId, String path,
                                           String branchName) {
        GitLabApi gitLabApi = createGitLabApi(repository);
        try {
            return gitLabApi.getRepositoryFileApi().getRawFile(projectId, branchName, path);
        } catch (GitLabApiException e) {
            log.error(e.getMessage(), e);
            ServiceException.throwException("查询GitLab仓库失败!");
        }
        return null;
    }

    @Override
    public void resetWebHook(CodeRepositoryVo repository) {
        GitLabApi gitLabApi = createGitLabApi(repository);
        try {
            List<SystemHook> systemHooks = gitLabApi.getSystemHooksApi().getSystemHooks();
            boolean found = systemHooks.stream().anyMatch(h -> {
                return CompareUtil.compare(repository.getWebHookUrl(), h.getUrl()) == 0;
            });
            if (!found) {
                SystemHook hook = new SystemHook();
                hook.setId(IDUtils.getId());
                hook.setCreatedAt(new Date());
                hook.setUrl(repository.getWebHookUrl());
                hook.setEnableSslVerification(repository.getWebHookUrl().toUpperCase().startsWith("HTTPS"));
                hook.setPushEvents(true);
                hook.setMergeRequestsEvents(true);
                hook.setRepositoryUpdateEvents(true);
                hook.setTagPushEvents(true);
                gitLabApi.getSystemHooksApi()
                         .addSystemHook(repository.getWebHookUrl(), repository.getWebHookToken(), hook);
            }
        } catch (GitLabApiException e) {
            log.error(e.getMessage(), e);
            ServiceException.throwException("重置WebHook失败,您设置的GITLAB账号可能不具备管理员权限");
        }
    }

    private GitLabApi createGitLabApi(CodeRepositoryVo repository) {
        String accessToken = createGitLabAccessToken(repository.getId());
        GitLabApi api = new GitLabApi(GitLabApi.ApiVersion.V4, repository.getHost(),
                                      Constants.TokenType.OAUTH2_ACCESS, accessToken);
        api.setIgnoreCertificateErrors(true);
        return api;
    }

    private synchronized String createGitLabAccessToken(Long repositoryId) {
        CodeRepositoryEntity repository = codeRepositoryDao.findById(repositoryId);
        Objects.requireNonNull(repository, "仓库不存在");
        CodeRepositoryVo repositoryVo = DataUtils.toVo(repository, CodeRepositoryVo.class);
        try {
            if (repositoryVo.getCreatedAt() == null || repositoryVo.getExpiresIn() == null) {
                //refresh accessToken
                createAccessTokenByRefreshToken(repositoryVo);
                return repositoryVo.getAccessToken();
            } else {
                Date expireDate =
                        DateUtils.addSeconds(new Date(repositoryVo.getCreatedAt()), repositoryVo.getExpiresIn());
                Date now = new Date();
                if (!now.after(expireDate)) {
                    return repositoryVo.getAccessToken();
                }

                //refresh accessToken
                createAccessTokenByRefreshToken(repositoryVo);
                return repositoryVo.getAccessToken();
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            ServiceException.throwException("获取GitLab令牌失败!");
        }
        return null;
    }

    public void createAccessTokenByCode(Long repositoryId, String code) {
        CodeRepositoryEntity repository = codeRepositoryDao.findById(repositoryId);
        repository.setCode(code);

        String url = repository.getHost();
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "oauth/token";
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HashMap<String, Object> map = new HashMap<>();
        map.put("client_id", repository.getApplicationId());
        map.put("client_secret", repository.getSecret());
        map.put("code", code);
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", repository.getRedirectUri());
        String stu = JsonUtils.toJSONString(map);
        HttpEntity<String> formEntity = new HttpEntity<String>(stu, headers);
        String response = "";
        if (url.toUpperCase().startsWith("HTTPS")) {
            response = getRestTemplate(url, "").postForObject(url, formEntity, String.class);
        } else {
            response = getRestTemplate(url, "").postForObject(url, formEntity, String.class);
        }

        Map<String, Object> result = new Gson().fromJson(response, HashMap.class);
        repository.setAccessToken((String) result.get("access_token"));
        repository.setRefreshToken((String) result.get("refresh_token"));
        repository.setTokenType((String) result.get("token_type"));
        repository.setExpiresIn(((Double) result.get("expires_in")).intValue());
        repository.setCreatedAt(new Date().getTime());
        codeRepositoryDao.updateCodeRepository(repository);

        try {
            String accessToken = createGitLabAccessToken(repository.getId());
            GitLabApi gitLabApi = new GitLabApi(GitLabApi.ApiVersion.V4, repository.getHost(),
                                                Constants.TokenType.OAUTH2_ACCESS, accessToken);

            User user = gitLabApi.getUserApi().getCurrentUser();
            repository.setGrantUserName(user.getName());
            //set webhook
            String webhookUrl = StringUtils.substringBefore(repository.getRedirectUri(), "/oauth");
            webhookUrl = URLUtil.completeUrl(webhookUrl, "/webhook/gitlab/" + repositoryId);
            String token = UUID.fastUUID().toString();
            repository.setWebHookUrl(webhookUrl);
            repository.setWebHookToken(token);

            codeRepositoryDao.updateCodeRepository(repository);

            resetWebHook(DataUtils.toVo(repository, CodeRepositoryVo.class));


        } catch (GitLabApiException e) {
            log.error(e.getMessage());
        }
    }

    private void createAccessTokenByRefreshToken(CodeRepositoryVo repository) {
        String url = repository.getHost();
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "oauth/token";
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HashMap<String, Object> map = new HashMap<>();
        map.put("client_id", repository.getApplicationId());
        map.put("client_secret", repository.getSecret());
        map.put("refresh_token", repository.getRefreshToken());
        map.put("grant_type", "refresh_token");
        map.put("redirect_uri", repository.getRedirectUri());
        String stu = JsonUtils.toJSONString(map);
        HttpEntity<String> formEntity = new HttpEntity<String>(stu, headers);
        String response = "";
        if (url.toUpperCase().startsWith("HTTPS")) {
            response = getRestTemplate(url, "").postForObject(url, formEntity, String.class);
        } else {
            response = getRestTemplate(url, "").postForObject(url, formEntity, String.class);
        }

        Map<String, Object> result = new Gson().fromJson(response, HashMap.class);
        repository.setAccessToken((String) result.get("access_token"));
        repository.setRefreshToken((String) result.get("refresh_token"));
        repository.setTokenType((String) result.get("token_type"));
        repository.setExpiresIn(((Double) result.get("expires_in")).intValue());
        repository.setCreatedAt(new Date().getTime());
        codeRepositoryDao.updateCodeRepository(DataUtils.toEntity(repository, CodeRepositoryEntity.class));
    }


    private void logTree(String serverUrl, String accessToken, String projectName, String path, String ref,
                         List<FileVo> files) {
        String url = serverUrl;
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += projectName + "/-/refs/" + ref + "/logs_tree/" + path + "?format=json&offset=0";
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HashMap<String, Object> map = new HashMap<>();
        map.put("Authorization", accessToken);
        String stu = JsonUtils.toJSONString(map);
        HttpEntity<String> formEntity = new HttpEntity<String>(stu, headers);
        String response = "";
        response = getRestTemplate(serverUrl, accessToken).getForObject(url, String.class);
        JSONArray resp = JsonUtils.parseArray(response);

        Map<String, FileVo> fileMap = new HashMap<>();
        files.forEach(item -> {fileMap.put(item.getName(), item);});
        for (int i = 0; i < resp.size(); i++) {
            JSONObject jsonObject = resp.getJSONObject(i);
            String fileName = jsonObject.getStr("file_name");
            if (fileMap.containsKey(fileName)) {
                FileVo fileVo = fileMap.get(fileName);
                CommitVo commitVo = new CommitVo();
                commitVo.setCommitId(jsonObject.getJSONObject("commit").getStr("id"));
                commitVo.setCommittedDate(jsonObject.getJSONObject("commit").getDate("committed_date"));
                commitVo.setCommitterName(jsonObject.getJSONObject("commit").getStr("committer_name"));
                commitVo.setMessage(jsonObject.getJSONObject("commit").getStr("message"));
                fileVo.setLastCommit(commitVo);
            }
        }
    }

}
