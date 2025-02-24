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

import com.harbortek.helm.test.service.TestReportService;
import com.harbortek.helm.test.vo.TestReportVo;
import com.harbortek.helm.test.vo.TestResultStat;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/test/report")
public class TestReportApi {
    @Autowired
    TestReportService testReportService;

    @Parameter(name="查询测试报告列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    ResponseEntity<Page<TestReportVo>> findTestReports(@RequestParam Long projectId, @RequestParam(required = false) String keyword,
                                                        @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        Page<TestReportVo> testReports = testReportService.findTestReports(projectId,keyword,pageable);
        return ResponseEntity.ok(testReports);
    }

    @Parameter(name="查询一个测试报告")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<TestReportVo> findOneTestReport(@PathVariable Long id) {
        TestReportVo sprint = testReportService.findOneTestReport(id);
        return ResponseEntity.ok(sprint);
    }

    @Parameter(name="创建一个测试报告")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<TestReportVo> createTestReport(@RequestBody TestReportVo TestReportVo) {
        TestReportVo result = testReportService.createTestReport(TestReportVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个测试报告")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<TestReportVo> updateTestReport(@RequestBody TestReportVo TestReportVo) {
        TestReportVo result = testReportService.updateTestReport(TestReportVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个测试报告")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteTestReport(@PathVariable Long id) {
        testReportService.deleteTestReport(id);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="根据TestRunId查询报告")
    @RequestMapping(value = "/testRun/{testRunId}", method = RequestMethod.GET)
    ResponseEntity<List<TestReportVo>> findTestReportByTestRunId(@PathVariable Long testRunId) {
        List<TestReportVo> testReportVos = testReportService.findTestReportByTestRunId(testRunId);
        return ResponseEntity.ok(testReportVos);
    }


    @Parameter(name="根据测试执行结果查询关联工作项")
    @RequestMapping(value = "/downstreamTrackerItems", method = RequestMethod.GET)
    public ResponseEntity<List<TrackerItemVo>> findDownstreamTrackerItems(
            @RequestParam Long testReportId) {
        List<TrackerItemVo> resultsVos = testReportService.findDownstreamTrackerItemsByTestReportId(testReportId);
        return ResponseEntity.ok(resultsVos);
    }


    @Parameter(name="根据测试报告统计测试用例的执行结果")
    @RequestMapping(value = "/testResults", method = RequestMethod.GET)
    public ResponseEntity<List<TestResultStat>> countTestResultsGroupByResult(@RequestParam Long testReportId) {
        List<TestResultStat> resultsVos = testReportService.countTestResultsGroupByResult(testReportId);
        return ResponseEntity.ok(resultsVos);
    }

    
}
