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

package com.harbortek.helm.tracker.api;

import com.harbortek.helm.common.vo.BaseIdentity;
import com.harbortek.helm.tracker.entity.project.ProjectPageEntity;
import com.harbortek.helm.tracker.service.ProjectPageService;
import com.harbortek.helm.tracker.service.TrackerItemService;
import com.harbortek.helm.tracker.vo.pages.ProjectPageVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Controller
@Tag(name = "项目管理")
@RequestMapping(value = "/tracker/project/page")
public class ProjectPageApi {
    @Autowired
    ProjectPageService projectPageService;

    @Autowired
    TrackerItemService  trackerItemService;
    @Parameter(name="查询项目页面列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<ProjectPageVo>> findPagesByProjectId(
            @RequestParam(value = "projectId", required = false) Long projectId) {
        Collection<ProjectPageVo> pages = projectPageService.findByProjectId(projectId);
//        List<ProjectPageVo> flatPages = new ArrayList<>();
//        pages.forEach(p -> {
//            p.setFolder(true);
//            flatPages.add(p);
//            List<ProjectPageVo> children = p.getChildren();
//            children.forEach(c -> {
//                c.setParentId(p.getId());
//                c.setFolder(false);
//            });
//            p.setChildren(null);
//            flatPages.addAll(children);
//        });

//        flatPages.forEach(pageVo -> { projectPageService.updateProjectPageBasicInfo(pageVo);});

        return ResponseEntity.ok(pages);
    }

    @Parameter(name="查询项目页面列表")
    @RequestMapping(value = "/byIds", method = RequestMethod.POST)
    ResponseEntity<List<ProjectPageVo>> findPagesByPageIds(
            @RequestBody List<Long> pageIds) {
        List<ProjectPageVo> pages = projectPageService.findPagesByIds(pageIds);

        return ResponseEntity.ok(pages);
    }

    @Parameter(name="创建一个项目页面")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<ProjectPageVo> createProjectPage(@RequestBody ProjectPageVo pageVo) {
        ProjectPageVo result = projectPageService.createProjectPage(pageVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个项目页面")
    @RequestMapping(value = "/info", method = RequestMethod.PUT)
    ResponseEntity<Void> updateProjectPageInfo(@RequestBody ProjectPageVo pageVo) {
        projectPageService.updateProjectPageBasicInfo(pageVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除一个项目页面")
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteProjectPage(@RequestBody ProjectPageVo pageVo) {
        projectPageService.deleteProjectPage(pageVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新页面排序")
    @RequestMapping(value = "/changeOrder", method = RequestMethod.POST)
    ResponseEntity<Void> changeProjectPageOrder(@RequestBody List<ProjectPageVo> pageList) {
        projectPageService.changeProjectPageOrder(pageList);
        return ResponseEntity.ok().build();
    }


    @Parameter(name="查询WIKI Page")
    @RequestMapping(value = "/{pageId}", method = RequestMethod.GET)
    ResponseEntity<ProjectPageVo> findOne(@PathVariable Long pageId) {
        ProjectPageVo page = projectPageService.findOneProjectPage(pageId);
        return ResponseEntity.ok(page);
    }
    @Parameter(name="查询page及子page")
    @RequestMapping(value = "/{pageId}/children", method = RequestMethod.GET)
    ResponseEntity<List<ProjectPageEntity>> findOneAndChildrenByPageId(@PathVariable Long pageId) {
        List<ProjectPageEntity> entities = projectPageService.findOneAndChildrenByProjectPage(pageId);
        return ResponseEntity.ok(entities);
    }
    @Parameter(name="更新WIKI Page")
    @RequestMapping(value = "/{pageId}", method = RequestMethod.PUT)
    ResponseEntity<Void> updateOne(@PathVariable Long pageId, @RequestBody ProjectPageVo pageVo) {
        pageVo.setId(pageId);
        projectPageService.updateProjectPage(pageVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="根据componentType查询page")
    @RequestMapping(value = "/byComponentType", method = RequestMethod.GET)
    ResponseEntity<ProjectPageVo> findPageByComponentType(@RequestParam Long projectId,@RequestParam String componentType) {
        ProjectPageVo pageVo = projectPageService.findPageByComponentType(projectId,componentType);
        return ResponseEntity.ok(pageVo);
    }

    @Parameter(name="关注Page")
    @RequestMapping(value = "/{pageId}/add-watch", method = RequestMethod.PUT)
    ResponseEntity<Void> addWatch(@PathVariable Long pageId,@RequestBody BaseIdentity watch) {
        projectPageService.watch(pageId,watch);
        return ResponseEntity.ok().build();
    }
    @Parameter(name="取消关注")
    @RequestMapping(value = "/{pageId}/cancel-watch", method = RequestMethod.PUT)
    ResponseEntity<Void> cancelWatch(@PathVariable Long pageId,@RequestBody BaseIdentity watch) {
        projectPageService.cancelWatch(pageId,watch);
        return ResponseEntity.ok().build();
    }
}
