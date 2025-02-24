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

import com.harbortek.helm.tracker.entity.document.DocumentHistoryEntity;
import com.harbortek.helm.tracker.service.BaselineService;
import com.harbortek.helm.tracker.vo.baseline.BaselineCompare;
import com.harbortek.helm.tracker.vo.baseline.BaselineVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemHistoryVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/tracker/baseline")
public class BaselineApi {

    @Autowired
    BaselineService baselineService;

    @Parameter(name="查询基线列表")
    @RequestMapping(value = "/listByProject", method = RequestMethod.GET)
    ResponseEntity<Collection<BaselineVo>> findBaselinesByProjectId(
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(required = false,defaultValue = "") String keyword) {
        Collection<BaselineVo> baselines = baselineService.findBaselinesByProjectId(projectId,keyword);
        return ResponseEntity.ok(baselines);
    }

    @Parameter(name="查询文档基线列表")
    @RequestMapping(value = "/listByDocument", method = RequestMethod.GET)
    ResponseEntity<Collection<BaselineVo>> findBaselinesByDocumentId(@RequestParam(value = "projectId") Long projectId,
                                                                     @RequestParam(value = "documentId") Long documentId) {
        Collection<BaselineVo> baselines = baselineService.findBaselinesByDocumentId(projectId, documentId);
        return ResponseEntity.ok(baselines);
    }

    @Parameter(name="查询一个基线")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<BaselineVo> findOneBaseline(@PathVariable Long id) {
        BaselineVo baseline = baselineService.findOneBaseline(id);
        return ResponseEntity.ok(baseline);
    }

    @Parameter(name="创建一个基线")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<BaselineVo> createBaseline(@RequestBody BaselineVo baselineVo) {
        BaselineVo result = baselineService.createBaseline(baselineVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个基线")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<BaselineVo> updateBaseline(@RequestBody BaselineVo baselineVo) {
        BaselineVo result = baselineService.updateBaseline(baselineVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个基线")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteBaseline(@PathVariable Long id) {
        baselineService.deleteBaseline(id);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="比较两个基线")
    @RequestMapping(value = "/compare", method = RequestMethod.GET)
    ResponseEntity<List<BaselineCompare>> compareBaseline(@RequestParam Long projectId,@RequestParam Long leftId,
                                                          @RequestParam Long rightId) {
        List<BaselineCompare> result = baselineService.compareBaseline(projectId,leftId, rightId);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="历史工作项详情")
    @RequestMapping(value = "/item-history", method = RequestMethod.POST)
    ResponseEntity<List<TrackerItemHistoryVo>> findItemsHistory(@RequestBody List<Long> historyIds) {
        List<TrackerItemHistoryVo> result = baselineService.findItemsHistory(historyIds);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="历史文档详情")
    @RequestMapping(value = "/document-history", method = RequestMethod.POST)
    ResponseEntity<List<DocumentHistoryEntity>> findDocumentHistory(@RequestBody List<Long> historyIds) {
        List<DocumentHistoryEntity> result = baselineService.findDocumentHistory(historyIds);
        return ResponseEntity.ok(result);
    }
}
