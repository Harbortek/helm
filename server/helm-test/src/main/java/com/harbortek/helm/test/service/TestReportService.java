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

package com.harbortek.helm.test.service;

import com.harbortek.helm.test.vo.TestReportVo;
import com.harbortek.helm.test.vo.TestResultStat;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TestReportService {
    Page<TestReportVo> findTestReports(Long projectId, String keyword, Pageable pageable);

    TestReportVo findOneTestReport(Long id);

    TestReportVo createTestReport(TestReportVo testReportVo);

    TestReportVo updateTestReport(TestReportVo testReportVo);

    void deleteTestReport(Long id);
    
    List<TestReportVo> findTestReportByTestRunId(Long planId);


    List<TrackerItemVo> findDownstreamTrackerItemsByTestReportId(Long testReportId);

    List<TestResultStat> countTestResultsGroupByResult(Long testReportId);
}
