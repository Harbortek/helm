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

import com.harbortek.helm.tracker.service.TargetVersionService;
import com.harbortek.helm.tracker.vo.plan.TargetVersionVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/plan/version")
public class TargetVersionApi {
    @Autowired
    TargetVersionService versionService;

    @Parameter(name="查询版本列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<Collection<TargetVersionVo>> findVersions(
            @RequestParam(value = "projectId", required = false) Long projectId) {
        Collection<TargetVersionVo> Versions = versionService.findVersions(projectId);
        return ResponseEntity.ok(Versions);
    }

    @Parameter(name="查询一个版本")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<TargetVersionVo> findOneVersion(@PathVariable Long id) {
        TargetVersionVo Version = versionService.findOneVersion(id);
        return ResponseEntity.ok(Version);
    }

    @Parameter(name="创建一个版本")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<TargetVersionVo> createVersion(@RequestBody TargetVersionVo VersionVo) {
        TargetVersionVo result = versionService.createVersion(VersionVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个版本")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<TargetVersionVo> updateVersion(@RequestBody TargetVersionVo VersionVo) {
        TargetVersionVo result = versionService.updateVersion(VersionVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个版本")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteVersion(@PathVariable Long id) {
        versionService.deleteVersion(id);
        return ResponseEntity.ok().build();
    }
}
