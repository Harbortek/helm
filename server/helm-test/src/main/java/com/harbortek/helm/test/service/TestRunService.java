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

import com.harbortek.helm.test.vo.TestResultVo;
import com.harbortek.helm.test.vo.TestRunVo;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.tracker.test.TestStepResult;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TestRunService {
    Page<TestRunVo> findTestRuns(Long projectId, Long statusId, String keyword, ObjectFilter filter, Pageable pageable);

    List<TestRunVo> findTestRunByIds(Long projectId,List<Long> testRunIds);

    TestRunVo findOneTestRun(Long id);

    TestRunVo createTestRun(TestRunVo testRunVo);

    TestRunVo updateTestRun(TestRunVo testRunVo);

    void deleteTestRun(Long id);

    List<Long> findTestCaseIds(Long runId);

    void associateTestCases(Long testRunId, List<Long> trackerItems);

    void removeTestResults(Long testRunId, List<Long> resultIds);

    Page<TestResultVo> findTestResults(Long projectId, Long testRunId, ObjectFilter filter, String keyword,
                                       Pageable pageable);

    void batchChangeExecutor(Long testRunId, List<Long> testResultIds, Long executorId);

    void changeResult(Long testRunId, List<Long> testResultIds, Long resultId, String resultDesc);

    TestResultVo saveResult(Long testRunId, Long testResultId, Long resultId, String resultDesc,
                            List<TestStepResult> testStepResults);

    void unlinkTestResultWithDownstreamTrackerItems(Long testResultId);

    void linkTestResultWithDownstreamTrackerItems(Long testResultId, List<Long> trackerItemIds);

    List<TrackerItemVo> findDownstreamTrackerItemsByTestResultId(Long testResultId);

    List<TestResultVo> findUpstreamTestResultsByTrackerItemId(Long itemId);

    List<TestResultVo> findDownstreamTestResultsByTrackerItemId(Long trackerItemId);

    List<TestResultVo> findTestResultsByTrackerItemId(Long trackerItemId);

    void copyTestResults(Long testRunId, List<Long> testResultIds);


}
