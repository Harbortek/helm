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

package com.harbortek.helm.test.api;

import com.harbortek.helm.test.service.TestRunService;
import com.harbortek.helm.test.vo.TestResultVo;
import com.harbortek.helm.test.vo.TestRunVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.tracker.test.TestStepResult;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/test/run")
public class TestRunApi {
    @Autowired
    TestRunService testRunService;

    @Parameter(name="查询测试运行列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    ResponseEntity<Page<TestRunVo>> findTestRuns(@RequestParam Long projectId,
                                                 @RequestParam(required = false) Long statusId,
                                                 @RequestParam(required = false) String keyword,
                                                 @RequestBody(required = false) ObjectFilter filter,
                                                 @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        Page<TestRunVo> testRuns = testRunService.findTestRuns(projectId, statusId, keyword, filter, pageable);
        return ResponseEntity.ok(testRuns);
    }

    @Parameter(name="查询一个测试运行")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<TestRunVo> findOneTestRun(@PathVariable Long id) {
        TestRunVo testRun = testRunService.findOneTestRun(id);
        return ResponseEntity.ok(testRun);
    }

    @Parameter(name="查询多个测试运行")
    @RequestMapping(value = "listByIds", method = RequestMethod.GET)
    ResponseEntity<List<TestRunVo>> findTestRuns(@RequestParam Long projectId, @RequestParam List<Long> testRunIds) {
        List<TestRunVo> testRuns = testRunService.findTestRunByIds(projectId,testRunIds);
        return ResponseEntity.ok(testRuns);
    }

    @Parameter(name="创建一个测试运行")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<TestRunVo> createTestRun(@RequestBody TestRunVo TestRunVo) {
        TestRunVo result = testRunService.createTestRun(TestRunVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个测试运行")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<TestRunVo> updateTestRun(@RequestBody TestRunVo TestRunVo) {
        TestRunVo result = testRunService.updateTestRun(TestRunVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个测试运行")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteTestRun(@PathVariable Long id) {
        testRunService.deleteTestRun(id);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="根据测试运行查所有用例testCaseIds")
    @RequestMapping(value = "/testCaseIds/{id}", method = RequestMethod.GET)
    ResponseEntity<List<Long>> findTestCaseIds(@PathVariable Long id) {//runId
        List<Long> testCaseIds= testRunService.findTestCaseIds(id);
        return ResponseEntity.ok(testCaseIds);
    }

    @Parameter(name="关联测试用例")
    @RequestMapping(value = "/{testRunId}/associate/testCases", method = RequestMethod.PUT)
    ResponseEntity<Void> associateTestCases(@PathVariable Long testRunId, @RequestBody List<Long> testCaseIds) {
        testRunService.associateTestCases(testRunId, testCaseIds);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="移除测试用例")
    @RequestMapping(value = "/{testRunId}/remove/testResults", method = RequestMethod.PUT)
    ResponseEntity<Void> removeTestResults(@PathVariable Long testRunId, @RequestBody List<Long> testResultIds) {
        testRunService.removeTestResults(testRunId, testResultIds);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="复制测试用例到另一个测试运行")
    @RequestMapping(value = "/{testRunId}/copy/testResults", method = RequestMethod.PUT)
    ResponseEntity<Void> copyTestResults(@PathVariable Long testRunId, @RequestBody List<Long> testResultIds) {
        testRunService.copyTestResults(testRunId, testResultIds);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="查询关联测试用例")
    @RequestMapping(value = "/testResults", method = RequestMethod.POST)
    ResponseEntity<Page<TestResultVo>> findTestResults(
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "testRunId", required = false) Long testRunId,
            @RequestBody(required = false) ObjectFilter filter,
            @RequestParam(value = "keyword", required = false) String keyword,
            Pageable pageable) {
        Page<TestResultVo> testCases = testRunService.findTestResults(projectId, testRunId, filter, keyword,
                                                                      pageable);
        return ResponseEntity.ok(testCases);
    }

    @Parameter(name="更改执行人")
    @RequestMapping(value = "/{testRunId}/changeExecutor", method = RequestMethod.PUT)
    ResponseEntity<Void> batchChangeExecutor(@PathVariable Long testRunId, @RequestBody List<Long> testResultIds,
                                             @RequestParam Long executorId) {
        testRunService.batchChangeExecutor(testRunId, testResultIds, executorId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="批量更改执行结果")
    @RequestMapping(value = "/{testRunId}/changeResult", method = RequestMethod.PUT)
    ResponseEntity<Void> batchChangeResult(@PathVariable Long testRunId, @RequestBody List<Long> testResultIds,
                                           @RequestParam Long resultId,
                                           @RequestParam(required = false) String resultDesc) {
        testRunService.changeResult(testRunId, testResultIds, resultId, resultDesc);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="保存执行结果")
    @RequestMapping(value = "/{testRunId}/saveResult", method = RequestMethod.PUT)
    ResponseEntity<TestResultVo> saveResult(@PathVariable Long testRunId, @RequestParam Long testResultId,
                                    @RequestParam(required = false) Long resultId, @RequestParam(required = false) String resultDesc,
                                    @RequestBody List<TestStepResult> testStepResults) {
        TestResultVo result = testRunService.saveResult(testRunId, testResultId, resultId, resultDesc, testStepResults);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="测试结果关联工作项")
    @RequestMapping(value = "/trackerTestResultLink/link", method = RequestMethod.POST)
    ResponseEntity<Void> linkTestResultWithDownstreamTrackerItems(@RequestParam Long testResultId,
                                                                  @RequestBody List<Long> trackerItemIds) {
        testRunService.linkTestResultWithDownstreamTrackerItems(testResultId, trackerItemIds);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="移除测试结果关联工作项")
    @RequestMapping(value = "/trackerTestResultLink/unlink", method = RequestMethod.POST)
    ResponseEntity<Void> unlinkTestResultWithDownstreamTrackerItems(@RequestParam Long testResultId) {
        testRunService.unlinkTestResultWithDownstreamTrackerItems(testResultId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="根据测试执行结果查询关联工作项")
    @RequestMapping(value = "/{testResultId}/downstreamTrackerItems", method = RequestMethod.GET)
    public ResponseEntity<List<TrackerItemVo>> findDownstreamTrackerItemsByTestResultId(
            @PathVariable Long testResultId) {
        List<TrackerItemVo> resultsVos = testRunService.findDownstreamTrackerItemsByTestResultId(testResultId);
        return ResponseEntity.ok(resultsVos);
    }

    @Parameter(name="根据工作项查询关联上游测试执行结果")
    @RequestMapping(value = "/{trackerItemId}/upstreamTestResults", method = RequestMethod.GET)
    public ResponseEntity<List<TestResultVo>> findUpstreamTestResultsByTrackerItemId(@PathVariable Long trackerItemId) {
        List<TestResultVo> resultsVos = testRunService.findUpstreamTestResultsByTrackerItemId(trackerItemId);
        return ResponseEntity.ok(resultsVos);
    }

    @Parameter(name="根据工作项查询关联下游测试执行结果")
    @RequestMapping(value = "/{trackerItemId}/downstreamTestResults", method = RequestMethod.GET)
    public ResponseEntity<List<TestResultVo>> findDownstreamTestResultsByTrackerItemId(
            @PathVariable Long trackerItemId) {
        List<TestResultVo> resultsVos = testRunService.findDownstreamTestResultsByTrackerItemId(trackerItemId);
        return ResponseEntity.ok(resultsVos);
    }


    @Parameter(name="根据工作项查询关联上/下游测试执行结果")
    @RequestMapping(value = "/{trackerItemId}/testResults", method = RequestMethod.GET)
    public ResponseEntity<List<TestResultVo>> findTestResultsByTrackerItemId(@PathVariable Long trackerItemId) {
        List<TestResultVo> resultsVos = testRunService.findTestResultsByTrackerItemId(trackerItemId);
        return ResponseEntity.ok(resultsVos);
    }



}
