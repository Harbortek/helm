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

import com.harbortek.helm.tracker.service.TrackerLinkTypeService;
import com.harbortek.helm.tracker.util.TrackerUtils;
import com.harbortek.helm.tracker.vo.link.TrackerLinkTypeVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "工作项关联类型管理")
@RequestMapping(value = "/tracker/project/{projectId}/linkType")
public class TrackerLinkTypeApi {
    @Autowired
    TrackerLinkTypeService trackerLinkTypeService;


    @Parameter(name="查询工作项关联类型列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<TrackerLinkTypeVo>> findLinkTypes(@PathVariable Long projectId) {
        List<TrackerLinkTypeVo> linkTypes = trackerLinkTypeService.findLinkTypes(projectId);
        if (linkTypes.isEmpty()){//如果为空，则创建默认关联类型
            linkTypes = TrackerUtils.buildDefaultTrackerLinkTypes();
            linkTypes.forEach(linkType->linkType.setProjectId(projectId));
            trackerLinkTypeService.batchCreateLinkTypes(linkTypes);
        }
        return ResponseEntity.ok(linkTypes);
    }

    @Parameter(name="查询工作项关联类型")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TrackerLinkTypeVo> findOneLinkTypes(@PathVariable Long projectId,Long id) {
        TrackerLinkTypeVo linkType = trackerLinkTypeService.findOneLinkType(id);
        return ResponseEntity.ok(linkType);
    }

    @Parameter(name="创建工作项关联类型")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Void> createLinkType(@RequestBody TrackerLinkTypeVo linkTypeVo) {
        trackerLinkTypeService.createLinkType(linkTypeVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新工作项关联类型")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateLinkType(@RequestBody TrackerLinkTypeVo linkTypeVo) {
        trackerLinkTypeService.updateLinkType(linkTypeVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除工作项关联类型")
    @RequestMapping(value = "/{linkTypeId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteLinkType(@PathVariable Long linkTypeId) {
        trackerLinkTypeService.deleteLinkType(linkTypeId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="调整工作项关联类型排序")
    @RequestMapping(value = "/changeOrder", method = RequestMethod.POST)
    public ResponseEntity<Void> changeOrder(@RequestBody List<TrackerLinkTypeVo> linkTypeVos) {
        trackerLinkTypeService.changeOrder(linkTypeVos);
        return ResponseEntity.ok().build();
    }

}
