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

package com.harbortek.helm.scm.vo;

import com.harbortek.helm.common.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
public class ScmProjectVo extends BaseVo {
    private Integer approvalsBeforeMerge;
    private Boolean archived;
    private String avatarUrl;
    private Boolean containerRegistryEnabled;
    private Date createdAt;
    private Long creatorId;
    private String defaultBranch;
    private Integer forksCount;
    private String httpUrlToRepo;
    private Boolean isPublic;
    private Boolean issuesEnabled;
    private Boolean jobsEnabled;
    private Date lastActivityAt;
    private Boolean lfsEnabled;
    private Boolean mergeRequestsEnabled;
    private String nameWithNamespace;
    private Boolean onlyAllowMergeIfPipelineSucceeds;
    private Boolean allowMergeOnSkippedPipeline;
    private Boolean onlyAllowMergeIfAllDiscussionsAreResolved;
    private Integer openIssuesCount;
    private String owner;
    private String path;
    private String pathWithNamespace;
    private String repositoryStorage;
    private Boolean requestAccessEnabled;
    private String sshUrlToRepo;
    private Integer starCount;
    private List<String> tagList;
    private Integer visibilityLevel;
    private String webUrl;
    private Boolean wikiEnabled;
    private Boolean initializeWithReadme;
    private Boolean packagesEnabled;
    private Boolean emptyRepo;
    private String licenseUrl;
    private String buildCoverageRegex;
    private String readmeUrl;
    private Boolean canCreateMergeRequestIn;
    private Integer ciDefaultGitDepth;
    private Boolean ciForwardDeploymentEnabled;
    private String ciConfigPath;
    private Boolean removeSourceBranchAfterMerge;
    private Boolean autoDevopsEnabled;
    private Boolean autocloseReferencedIssues;
    private Boolean emailsDisabled;
    private String suggestionCommitMessage;
}
