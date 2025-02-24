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

import com.harbortek.helm.tracker.service.SprintService;
import com.harbortek.helm.tracker.vo.plan.SprintVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/plan/sprint")
public class SprintApi {
    @Autowired
    SprintService sprintService;

    @Parameter(name="查询迭代列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<SprintVo>> findSprints(
            @RequestParam(value = "projectId", required = false) Long projectId) {
        Collection<SprintVo> sprints = sprintService.findSprints(projectId);
        return ResponseEntity.ok(sprints);
    }

    @Parameter(name="查询一个迭代")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<SprintVo> findOneSprint(@PathVariable Long id) {
        SprintVo sprint = sprintService.findOneSprint(id);
        return ResponseEntity.ok(sprint);
    }

    @Parameter(name="创建一个迭代")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<SprintVo> createSprint(@RequestBody SprintVo sprintVo) {
        SprintVo result = sprintService.createSprint(sprintVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个迭代")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<SprintVo> updateSprint(@RequestBody SprintVo sprintVo) {
        SprintVo result = sprintService.updateSprint(sprintVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个迭代")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteSprint(@PathVariable Long id) {
        sprintService.deleteSprint(id);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="转换迭代")
    @RequestMapping(value = "/convert", method = RequestMethod.PUT)
    ResponseEntity<SprintVo> convertSprint(@RequestBody SprintVo sprintVo) {
        SprintVo result = sprintService.convertSprint(sprintVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="查询未关联计划的迭代")
    @RequestMapping(value = "/unPlaned", method = RequestMethod.GET)
    ResponseEntity<Collection<SprintVo>> findUnPlanedSprints(@RequestParam(value = "projectId") Long projectId) {
        Collection<SprintVo> result = sprintService.findUnPlanedSprints(projectId);
        return ResponseEntity.ok(result);
    }

}
