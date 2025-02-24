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

package com.harbortek.helm.tracker.service;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;

import java.util.Collection;
import java.util.List;

public interface ProjectPageService {
    ProjectPageVo findByDocId(Long docId);
    ProjectPageVo findOneProjectPage(Long id);
    List<ProjectPageEntity> findOneAndChildrenByProjectPage(Long id);

    void updateProjectPage(ProjectPageVo page);

    void updateProjectPageBasicInfo(ProjectPageVo page);

    List<ProjectPageVo> findByProjectId(Long projectId);

    List<ProjectPageVo> findPagesByIds(List<Long> pageIds);

    ProjectPageVo createProjectPage(ProjectPageVo pageVo);

    void deleteProjectPage(ProjectPageVo pageVo);

    void changeProjectPageOrder(List<ProjectPageVo> pageList);

    void batchCreateProjectPages(List<ProjectPageVo> pageEntities);

    List<ProjectPageVo> buildTree(Long projectId);

    void watch(Long pageId, BaseIdentity watch);

    void cancelWatch(Long pageId, BaseIdentity watch);

    ProjectPageVo findPageByComponentType(Long projectId, String componentType);

    List<Long> findPageIdsByProjectId(Long projectId);
}
