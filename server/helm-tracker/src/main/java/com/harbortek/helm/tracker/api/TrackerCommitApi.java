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

import com.harbortek.helm.tracker.entity.code.TrackerCommitEntity;
import com.harbortek.helm.tracker.service.TrackerCommitService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/tracker/commit")
public class TrackerCommitApi {
    @Autowired
    TrackerCommitService trackerCommitService;

    @Parameter(name="创建代码提交记录")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity createCommits(@RequestBody List<TrackerCommitEntity> commitEntities) {
        trackerCommitService.createCommits(commitEntities);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询代码提交列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<TrackerCommitEntity>> findCommitsByItemIds(
            @RequestParam(value = "itemId")Long itemId) {
        List<TrackerCommitEntity> trackerCommits = trackerCommitService.findByItemId(itemId);
        return ResponseEntity.ok(trackerCommits);
    }

    @Parameter(name="根据迭代ID查询代码提交列表")
    @RequestMapping(value = "/list-sprint", method = RequestMethod.GET)
    ResponseEntity<Collection<TrackerCommitEntity>> findCommitsBySprintId(
            @RequestParam(value = "sprintId")Long sprintId) {
        List<TrackerCommitEntity> trackerCommits = trackerCommitService.findBySprintId(sprintId);
        return ResponseEntity.ok(trackerCommits);
    }

}
