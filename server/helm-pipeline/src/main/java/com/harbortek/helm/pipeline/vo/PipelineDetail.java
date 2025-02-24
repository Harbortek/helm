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

package com.harbortek.helm.pipeline.vo;

import lombok.Data;

import java.util.List;

@Data
public class PipelineDetail {
    static final String MULTI_BRANCH_PROJECT =
            "org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject";
    static final String BRANCH_JOB_PROPERTY = "org.jenkinsci.plugins.workflow.multibranch.BranchJobProperty";

    String _class;

    Long repositoryId;

    String description;
    String displayName;
    String fullDisplayName;
    String fullName;
    String name;
    String url;
    Boolean buildable;
    List<BuildInfo> builds;

    BuildInfo lastBuild;
    BuildInfo lastCompletedBuild;
    BuildInfo lastFailedBuild;
    BuildInfo lastStableBuild;
    BuildInfo lastSuccessfulBuild;
    BuildInfo lastUnsuccessfulBuild;
    Integer nextBuildNumber;
    Boolean concurrentBuild;
    List<Property> property;

    List<PipelineDetail> jobs;


    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
        if (jobs!=null){
            jobs.forEach(child->{child.setRepositoryId(repositoryId);});
        }
    }

    public boolean isMultiBranch() {
        return MULTI_BRANCH_PROJECT.equals(_class);
    }

    public boolean isBranchJob() {
        if (property != null) {
            return property.stream().anyMatch(p -> {return BRANCH_JOB_PROPERTY.equals(p.get_class());});
        }
        return false;
    }

    public String getStatus(){
        if(isMultiBranch()){
            if (jobs!=null){
                boolean result =
                        jobs.stream().anyMatch(job->{return job.getLastBuild()!=null && !"SUCCESS".equals(job.getLastBuild().getResult());});
                return result?"FAILURE":"SUCCESS";
            }
            return null;
        }else{
            if (lastBuild!=null) {
                return lastBuild.getResult();
            }
            return null;
        }
    }
}
