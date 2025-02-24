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

import com.harbortek.helm.scm.constants.CodeRepositoryTypes;
import com.harbortek.helm.scm.service.CodeRepositoryService;
import com.harbortek.helm.scm.service.GitlabService;
import com.harbortek.helm.scm.utils.WebHookUtils;
import com.harbortek.helm.tracker.dao.TrackerCommitDao;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.dao.TrackerMergeRequestDao;
import com.harbortek.helm.tracker.entity.code.TrackerCommitEntity;
import com.harbortek.helm.tracker.entity.code.TrackerMergeRequestEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.systemhooks.*;
import org.gitlab4j.api.webhook.EventCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class GitlabWebHookListener implements SystemHookListener {

    @Autowired
    CodeRepositoryService codeRepositoryService;
    @Autowired
    GitlabService gitlabService;

    @Autowired
    TrackerItemDao trackerItemDao;

    @Autowired
    TrackerCommitDao trackerCommitDao;

    @Autowired
    TrackerMergeRequestDao trackerMergeRequestDao;

    @Override
    public void onRepositoryEvent(RepositorySystemHookEvent event) {
        log.debug("Received GitLab WebHook Event " + event.toString());
    }

    @Override
    public void onPushEvent(PushSystemHookEvent event) {
        log.debug("Received GitLab WebHook Event " + event.toString());
        Long gitlabProjectId = event.getProjectId();
        String gitlabProjectName = event.getProject().getPathWithNamespace();
        Long repositoryId = WebHookUtils.getCurrentRepository();

        Long projectId = codeRepositoryService.getProjectId(repositoryId, String.valueOf(gitlabProjectId));
        List<EventCommit> commits = event.getCommits();
        List<TrackerCommitEntity> commitEntities = new ArrayList<>();
        commits.forEach(commit -> {
            String message = commit.getMessage();

            Pattern r = Pattern.compile("(#\\d+)");
            Matcher m = r.matcher(message);
            while (m.find()) {
                String itemNo = m.group(0);
                if (StringUtils.isNotEmpty(itemNo) && itemNo.startsWith("#")) {
                    itemNo = StringUtils.removeStart(itemNo, "#");
                    TrackerItemEntity trackerItem = trackerItemDao.findOneByItemNo(projectId, itemNo);

                    TrackerCommitEntity commitEntity = TrackerCommitEntity.builder().id(IDUtils.getId())
                                                                          .name("Commit By GitLab")
                                                                          .projectId(projectId)
                                                                          .trackerId(trackerItem.getTrackerId())
                                                                          .itemId(trackerItem.getId())
                                                                          .repositoryId(repositoryId)
                                                                          .repositoryType(CodeRepositoryTypes.GITLAB)
                                                                          .commitId(commit.getId())
                                                                          .commitMessage(commit.getMessage())
                                                                          .committer(commit.getAuthor().getUsername())
                                                                          .commitOn(commit.getTimestamp())
                                                                          .branch(event.getBranch())
                                                                          .projectNameOfRepository(gitlabProjectName)
                                                                          .projectUrl(event.getProject().getWebUrl())
                                                                          .committer(commit.getUrl())
                                                                          .added(commit.getAdded())
                                                                          .modified(commit.getModified())
                                                                          .removed(commit.getRemoved())
                                                                          .build();
                    commitEntities.add(commitEntity);
                }
            }
        });
        trackerCommitDao.createCommits(commitEntities);
    }

    @Override
    public void onMergeRequestEvent(MergeRequestSystemHookEvent event) {
        log.debug("Received GitLab WebHook Event " + event.toString());
        Long gitlabProjectId = event.getProject().getId();
        String gitlabProjectName = event.getProject().getPathWithNamespace();
        Long repositoryId = WebHookUtils.getCurrentRepository();
        Long projectId = codeRepositoryService.getProjectId(repositoryId, String.valueOf(gitlabProjectId));

        String title = event.getObjectAttributes().getTitle();
        Pattern r = Pattern.compile("(#\\d+)");
        Matcher m = r.matcher(title);
        while (m.find()) {
            String itemNo = m.group(0);
            if (StringUtils.isNotEmpty(itemNo) && itemNo.startsWith("#")) {
                itemNo = StringUtils.removeStart(itemNo, "#");
                TrackerItemEntity trackerItem = trackerItemDao.findOneByItemNo(projectId, itemNo);
                String action = event.getObjectAttributes().getAction();


                if ("open".equals(action)) {
                    String assignees = StringUtils.join(ObjectUtils.names(event.getAssignees(), "name"));
                    String reviewers = StringUtils.join(ObjectUtils.names(event.getReviewers(), "name"));
                    TrackerMergeRequestEntity mergeRequestEntity =
                            TrackerMergeRequestEntity.builder().id(IDUtils.getId())
                                                     .name(title)
                                                     .description(event.getObjectAttributes().getDescription())
                                                     .projectId(projectId)
                                                     .trackerId(trackerItem.getTrackerId())
                                                     .itemId(trackerItem.getId())
                                                     .repositoryId(repositoryId)
                                                     .repositoryType(CodeRepositoryTypes.GITLAB)
                                                     .mergeRequestId(
                                                             String.valueOf(event.getObjectAttributes().getId()))
                                                     .sourceBranch(event.getObjectAttributes().getSourceBranch())
                                                     .targetBranch(event.getObjectAttributes().getTargetBranch())
                                                     .requestUrl(event.getRequestUrl())
                                                     .assignees(assignees).reviewers(reviewers)
//                                                     .createDate(event.getObjectAttributes().getCreatedAt())
                                                     .mergeStatus(event.getObjectAttributes().getMergeStatus())
                                                     .status(event.getObjectAttributes().getState())
                                                     .submitUser(event.getUser().getName())
                                                     .build();
                    trackerMergeRequestDao.createMergeRequest(mergeRequestEntity);
                } else  {
                    String mergeRequestId =
                            String.valueOf(event.getObjectAttributes().getId());
                    TrackerMergeRequestEntity mergeRequestEntity =
                            trackerMergeRequestDao.findByMergeRequestId(projectId, mergeRequestId);
                    if (mergeRequestEntity!=null){
                        mergeRequestEntity.setMergeStatus(event.getObjectAttributes().getMergeStatus());
                        mergeRequestEntity.setStatus(event.getObjectAttributes().getState());
                        mergeRequestEntity.setAuditor(event.getUser().getName());
                        mergeRequestEntity.setLastModifiedDate(event.getObjectAttributes().getUpdatedAt());
                        trackerMergeRequestDao.updateMergeRequest(mergeRequestEntity);
                    }

                }
            }
        }
    }

    @Override
    public void onProjectEvent(ProjectSystemHookEvent event) {
        log.debug("Received GitLab WebHook Event " + event.toString());
    }
}
