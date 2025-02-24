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

package com.harbortek.helm.test.service.impl;

import com.harbortek.helm.system.service.EnumService;
import com.harbortek.helm.system.vo.EnumItemVo;
import com.harbortek.helm.test.dao.TestResultDao;
import com.harbortek.helm.test.dao.TestRunDao;
import com.harbortek.helm.test.dao.TrackerTestResultLinkDao;
import com.harbortek.helm.test.entity.TestResultEntity;
import com.harbortek.helm.test.entity.TestRunEntity;
import com.harbortek.helm.test.entity.TrackerTestResultLinkEntity;
import com.harbortek.helm.test.service.TestRunService;
import com.harbortek.helm.test.vo.TestResultVo;
import com.harbortek.helm.test.vo.TestRunVo;
import com.harbortek.helm.tracker.constants.EnumCodes;
import com.harbortek.helm.tracker.constants.ProjectStatusMeaning;
import com.harbortek.helm.tracker.constants.TestResults;
import com.harbortek.helm.tracker.constants.TestRunStatus;
import com.harbortek.helm.tracker.dao.TrackerItemDao;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.service.TrackerItemService;
import com.harbortek.helm.tracker.service.TrackerService;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.tracker.vo.tracker.fields.TestStepField;
import com.harbortek.helm.tracker.vo.tracker.fields.TrackerField;
import com.harbortek.helm.tracker.vo.tracker.test.TestStepResult;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import com.harbortek.helm.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("testRunService")
@Slf4j
public class TestRunServiceImpl implements TestRunService {

    @Autowired
    private TestRunDao testRunDao;

    @Autowired
    private TestResultDao testResultDao;
    @Autowired
    private EnumService enumService;

    @Autowired
    private TrackerService trackerService;

    @Autowired
    private TrackerItemService trackerItemService;

    @Autowired
    private TrackerItemDao trackerItemDao;

    @Autowired
    private TrackerTestResultLinkDao trackerLinkResultsDao;

    @Override
    public Page<TestRunVo> findTestRuns(Long projectId, Long statusId, String keyword, ObjectFilter filter,
                                        Pageable pageable) {
        Page<TestRunEntity> testplanPage = testRunDao.findTestRuns(projectId, statusId, keyword, filter, pageable);
        List<TestRunVo> vos = DataUtils.toVo(testplanPage.getContent(), TestRunVo.class);
        Long passedEnumItem = enumService.findOneEnumItemByCode(projectId, EnumCodes.TEST_CASE_RESULT,
                                                                TestResults.PASSED).getId();
        vos.forEach(testRunVo->{
            testRunVo.setTotalCount(testResultDao.countTestResultsByTestRunId(testRunVo.getId()));
            testRunVo.setExecutedCount(testResultDao.countExecutedTestResultsByTestRunId(testRunVo.getId()));
            testRunVo.setPassedCount(testResultDao.countPassedTestResultsByTestRunId(testRunVo.getId(),passedEnumItem));
            if (testRunVo.getTotalCount()>0) {
                testRunVo.setProgress(testRunVo.getExecutedCount() * 100 / testRunVo.getTotalCount());
                testRunVo.setPassRate(testRunVo.getPassedCount() * 100 / testRunVo.getTotalCount());
            }
        });
        return new PageImpl<>(vos, pageable, testplanPage.getTotalElements());
    }

    @Override
    public List<TestRunVo> findTestRunByIds(Long projectId,List<Long> testRunIds) {
        List<TestRunEntity> testRunEntities = testRunDao.findByIds(testRunIds);
        List<TestRunVo> vos = DataUtils.toVo(testRunEntities, TestRunVo.class);
        Long passedEnumItem = enumService.findOneEnumItemByCode(projectId, EnumCodes.TEST_CASE_RESULT,
                                                                TestResults.PASSED).getId();
        vos.forEach(testRunVo->{
            testRunVo.setTotalCount(testResultDao.countTestResultsByTestRunId(testRunVo.getId()));
            testRunVo.setExecutedCount(testResultDao.countExecutedTestResultsByTestRunId(testRunVo.getId()));
            testRunVo.setPassedCount(testResultDao.countPassedTestResultsByTestRunId(testRunVo.getId(),passedEnumItem));
            if (testRunVo.getTotalCount()>0) {
                testRunVo.setProgress(testRunVo.getExecutedCount() * 100 / testRunVo.getTotalCount());
                testRunVo.setPassRate(testRunVo.getPassedCount() * 100 / testRunVo.getTotalCount());
            }
        });
        return vos;
    }

    @Override
    public TestRunVo findOneTestRun(Long testRunId) {
        TestRunEntity testRunEntity = testRunDao.findById(testRunId);
        TestRunVo testRunVo = DataUtils.toVo(testRunEntity, TestRunVo.class);

        Long passedEnumItem = enumService.findOneEnumItemByCode(testRunVo.getProjectId(), EnumCodes.TEST_CASE_RESULT,
                TestResults.PASSED).getId();

        testRunVo.setTotalCount(testResultDao.countTestResultsByTestRunId(testRunVo.getId()));
        testRunVo.setExecutedCount(testResultDao.countExecutedTestResultsByTestRunId(testRunVo.getId()));
        testRunVo.setPassedCount(testResultDao.countPassedTestResultsByTestRunId(testRunVo.getId(),passedEnumItem));
        if (testRunVo.getTotalCount()>0) {
            testRunVo.setProgress(testRunVo.getExecutedCount() * 100 / testRunVo.getTotalCount());
            testRunVo.setPassRate(testRunVo.getPassedCount() * 100 / testRunVo.getTotalCount());
        }
        return testRunVo;
    }

    @Override
    public TestRunVo createTestRun(TestRunVo testRunVo) {
        testRunVo.setId(IDUtils.getId());
        TestRunEntity testRunEntity = DataUtils.toEntity(testRunVo, TestRunEntity.class);
        testRunEntity.setStatusId(
                enumService.findOneEnumItemByCode(testRunVo.getProjectId(), EnumCodes.TEST_RUN_STATUS,
                                                  TestRunStatus.NOT_STARTED).getId());
        testRunEntity.setMeaning(TestRunStatus.NOT_STARTED);
        testRunEntity = testRunDao.createTestRun(testRunEntity);
        return DataUtils.toVo(testRunEntity, TestRunVo.class);
    }

    @Override
    public TestRunVo updateTestRun(TestRunVo testRunVo) {
        TestRunEntity testRunEntity = DataUtils.toEntity(testRunVo, TestRunEntity.class);
        testRunEntity.setStatusId(testRunVo.getStatus().getId());
        testRunEntity = testRunDao.createTestRun(testRunEntity);

        changeTestRunStatus(testRunVo.getProjectId(),testRunVo.getId());
        return DataUtils.toVo(testRunEntity, TestRunVo.class);
    }

    @Override
    public void deleteTestRun(Long id) {
        testRunDao.deleteTestRun(id);
    }

    @Override
    public List<Long> findTestCaseIds(Long runId) {
        List<Long> testCaseIds=testRunDao.findTestCaseIds(runId);
        return testCaseIds;
    }

    @Override
    public void associateTestCases(Long testRunId, List<Long> testCaseIds) {
        List<TestResultEntity> testResultEntities = testResultDao.findTestResultsByTestRunId(testRunId);
        List<Long> existedTestIds =
                testResultEntities.stream().map(TestResultEntity::getTestCaseId).distinct().toList();
        List<Long> newTestIds = new ArrayList<>(testCaseIds);
        newTestIds.removeAll(existedTestIds);

        List<TrackerItemEntity> trackerItemEntities = trackerItemDao.findByIds(newTestIds);


        List<TestResultEntity> items = trackerItemEntities.stream().map(item -> {
            TestResultEntity testResultEntity = new TestResultEntity();
            testResultEntity.setId(IDUtils.getId());
            testResultEntity.setTestCaseId(item.getId());
            testResultEntity.setItemNo(item.getItemNo());
            testResultEntity.setName(item.getName());
            testResultEntity.setDescription(item.getDescription());
            testResultEntity.setTestRunId(testRunId);
            testResultEntity.setExecutorId(SecurityUtils.getCurrentUser().getId());

            List<TrackerField> trackerFields = trackerService.findTrackerFields(item.getTrackerId());
            TrackerField trackerField =
                    trackerFields.stream().filter(field -> field instanceof TestStepField).findFirst().orElse(null);
            if (trackerField instanceof TestStepField) {
//                List<TrackerField> columns = ((TestStepField) trackerField).getColumns();
//                TrackerField stepField =
//                        columns.stream().filter(column -> TestStepField.STEP.equals(column.getSystemProperty()))
//                               .findFirst().orElse(null);
//                TrackerField stepDescriptionField =
//                        columns.stream()
//                               .filter(column -> TestStepField.STEP_DESCRIPTION.equals(column.getSystemProperty()))
//                               .findFirst().orElse(null);
//                TrackerField expectResultField =
//                        columns.stream()
//                               .filter(column -> TestStepField.EXPECT_RESULT.equals(column.getSystemProperty()))
//                               .findFirst().orElse(null);

                Object value = item.getCustomerFieldValue(trackerField);
                List<Map> list = JsonUtils.toList(String.valueOf(value), Map.class);
//                List<TrackerTestStep> steps = new ArrayList<>();
                List<TestStepResult> stepResults = new ArrayList<>();

                if (ObjectUtils.isNotEmpty(list)) {
                    list.forEach(row -> {
//                        TrackerTestStep testStep = new TrackerTestStep();
//                        if (stepField != null) {
//                            String name = (String) row.getOrDefault(stepField.getId(), "");
//                            if (StringUtils.isEmpty(name)){
//                                name = (String) row.getOrDefault(String.valueOf(stepField.getId()), "");
//                            }
//                            testStep.setName(name);
//                        }
//                        if (stepDescriptionField != null) {
//                            String description = (String) row.getOrDefault(stepDescriptionField.getId(), "");
//                            if (StringUtils.isEmpty(description)){
//                                description = (String) row.getOrDefault(String.valueOf(stepDescriptionField.getId()), "");
//                            }
//                            testStep.setDescription(description);
//                        }
//                        if (expectResultField != null) {
//                            String expectResult = (String) row.getOrDefault(expectResultField.getId(), "");
//                            if (StringUtils.isEmpty(expectResult)){
//                                expectResult = (String) row.getOrDefault(String.valueOf(expectResultField.getId()), "");
//                            }
//                            testStep.setExpectedResult(expectResult);
//                        }
//                        testStep.setId((long) list.indexOf(row)+1);
//                        steps.add(testStep);
                        TestStepResult testStepResult = new TestStepResult();
                        testStepResult.setId((long) list.indexOf(row)+1);
                        stepResults.add(testStepResult);
                    });
                }
//                testResultEntity.setTestSteps(steps);
                testResultEntity.setTestStepResults(stepResults);
            }
            return testResultEntity;
        }).collect(Collectors.toList());
        testResultDao.batchCreateTestResults(items);
        TestRunEntity testRunEntity = testRunDao.findById(testRunId);
        changeTestRunStatus(testRunEntity.getProjectId(),testRunId);
    }

    @Override
    public void removeTestResults(Long testRunId, List<Long> testResultIds) {

        testResultDao.markAdDeleted(testResultIds, TestResultEntity.class);

        TestRunEntity testRunEntity = testRunDao.findById(testRunId);
        changeTestRunStatus(testRunEntity.getProjectId(),testRunId);
    }

    @Override
    public void copyTestResults(Long testRunId, List<Long> testResultIds) {
        if (ObjectUtils.isNotEmpty(testResultIds)) {
            List<TestResultEntity> source = testResultDao.findTestResultsByIds(testResultIds);
            List<TestResultEntity> target = new ArrayList<>();
            source.forEach(item -> {
                TestResultEntity testResultEntity = new TestResultEntity();
                testResultEntity.setId(IDUtils.getId());
                testResultEntity.setTestCaseId(item.getTestCaseId());
                testResultEntity.setItemNo(item.getItemNo());
                testResultEntity.setName(item.getName());
                testResultEntity.setDescription(item.getDescription());
                testResultEntity.setTestRunId(testRunId);
                testResultEntity.setExecutorId(item.getExecutorId());
//                item.getTestSteps().forEach(step -> {
//                    step.setActualResult(null);
//                    step.setStepResult(null);
//                });
//                testResultEntity.setTestSteps(item.getTestSteps());
                testResultEntity.setTestStepResults(item.getTestStepResults());
                target.add(testResultEntity);
            });
            testResultDao.batchCreateTestResults(target);
        }
    }



    @Override
    public Page<TestResultVo> findTestResults(Long projectId, Long testRunId, ObjectFilter filter, String keyword,
                                              Pageable pageable) {
        Page<TestResultEntity> items = testResultDao.findTestResults(testRunId,filter,keyword,pageable);

        List<Long> testCaseIds =
                items.getContent().stream().map(TestResultEntity::getTestCaseId).collect(Collectors.toList());
        List<TrackerItemVo> trackerItemVos = trackerItemService.findTrackerItemByIds(testCaseIds);

        Page<TestResultVo> vos = DataUtils.toVo(items, TestResultVo.class);

        Map<Long, TrackerItemVo> trackerItemMap = trackerItemVos.stream().collect(Collectors.toMap(TrackerItemVo::getId, item -> item));
        vos.getContent().forEach(item -> {
            TrackerItemVo trackerItemVo = trackerItemMap.get(item.getTestCase().getId());
            item.setTestCase(trackerItemVo);
        });

        return vos;
    }

    @Override
    public void batchChangeExecutor(Long testRunId, List<Long> testResultIds, Long executorId) {
        if (ObjectUtils.isNotEmpty(testResultIds)) {
            List<TestResultEntity> testResults = testResultDao.findTestResultsByIds(testResultIds);
            testResults.forEach(item -> {
                item.setExecutorId(executorId);
            });
            testResultDao.batchUpdateTestResults(testResults);
        }
    }

    @Override
    public void changeResult(Long testRunId, List<Long> testResultIds, Long resultId, String resultDesc) {
        if (ObjectUtils.isNotEmpty(testResultIds)) {
            List<TestResultEntity> resultEntities = testResultDao.findTestResultsByIds(testResultIds);
            resultEntities.forEach(item -> {
                item.setResultId(resultId);
                item.setResultDesc(resultDesc);
            });
            testResultDao.batchUpdateTestResults(resultEntities);

            TestRunEntity testRunEntity = testRunDao.findById(testRunId);
            changeTestRunStatus(testRunEntity.getProjectId(),testRunId);
        }
    }

    @Override
    public TestResultVo saveResult(Long testRunId, Long testResultId, Long resultId, String resultDesc,
                                   List<TestStepResult> testStepResults) {

        TestResultEntity testResult = testResultDao.findById(testResultId);
        if (testResult != null) {
            testResult.setResultId(resultId);
            testResult.setResultDesc(resultDesc);
            testResult.setTestStepResults(testStepResults);
            testResultDao.updateTestResult(testResult);

            TestRunEntity testRunEntity = testRunDao.findById(testRunId);
            changeTestRunStatus(testRunEntity.getProjectId(),testRunId);
        }
        return DataUtils.toVo(testResult,TestResultVo.class);
    }

    @Override
    public void unlinkTestResultWithDownstreamTrackerItems(Long linkResultId) {
        trackerLinkResultsDao.deleteTrackerTestResultLink(linkResultId);
    }

    @Override
    public void linkTestResultWithDownstreamTrackerItems(Long testResultId, List<Long> trackerItemIds) {
        if (ObjectUtils.isNotEmpty(trackerItemIds)) {
            List<TrackerTestResultLinkEntity> entities = new ArrayList<>();
            trackerItemIds.forEach(item -> {
                entities.add(TrackerTestResultLinkEntity.builder().id(IDUtils.getId())
                                                        .testResultId(testResultId).trackerItemId(item).build());
            });
            trackerLinkResultsDao.deleteByTrackerItemIds(testResultId,trackerItemIds);
            trackerLinkResultsDao.batchCreateTrackerTestResultLinks(entities);
        }
    }

    @Override
    public List<TrackerItemVo> findDownstreamTrackerItemsByTestResultId(Long testResultId) {
        List<TrackerTestResultLinkEntity> trackerTestResultLinkEntities =
                trackerLinkResultsDao.findByTestResultId(testResultId);
        List<Long> trackerItemIds = trackerTestResultLinkEntities.stream().map(TrackerTestResultLinkEntity::getTrackerItemId).collect(Collectors.toList());
        return trackerItemService.findTrackerItemByIds(trackerItemIds);
    }

    @Override
    public List<TestResultVo> findUpstreamTestResultsByTrackerItemId(Long trackerItemId) {
        List<TrackerTestResultLinkEntity> trackerTestResultLinkEntities =
                trackerLinkResultsDao.findByTrackerItemId(trackerItemId);
        List<Long> testResultIds = trackerTestResultLinkEntities.stream().map(TrackerTestResultLinkEntity::getTestResultId).collect(Collectors.toList());

        return DataUtils.toVo(testResultDao.findTestResultsByIds(testResultIds), TestResultVo.class);
    }

    @Override
    public List<TestResultVo> findDownstreamTestResultsByTrackerItemId(Long trackerItemId) {
        List<TestResultEntity> testResultEntities = testResultDao.findTestResultsByTrackerItemId(trackerItemId);

        return DataUtils.toVo(testResultEntities, TestResultVo.class);
    }

    @Override
    public List<TestResultVo> findTestResultsByTrackerItemId(Long trackerItemId) {
        List<TestResultVo> upstreamTestResults = findUpstreamTestResultsByTrackerItemId(trackerItemId);
        List<TestResultVo> downstreamTestResults = findDownstreamTestResultsByTrackerItemId(trackerItemId);

        return ListUtils.union(upstreamTestResults, downstreamTestResults);
    }



    private void changeTestRunStatus(Long projectId,Long testRunId) {
        Integer totalCount = testResultDao.countTestResultsByTestRunId(testRunId);
        Integer executedCount = testResultDao.countExecutedTestResultsByTestRunId(testRunId);
        if (executedCount==0){
            updateTestRunStatus(projectId,testRunId,ProjectStatusMeaning.NOT_STARTED);
        }else if (executedCount<totalCount){
            updateTestRunStatus(projectId,testRunId,ProjectStatusMeaning.ONGOING);
        }else if (executedCount.equals(totalCount)){
            updateTestRunStatus(projectId,testRunId,ProjectStatusMeaning.ENDED);
        }

    }

    private void updateTestRunStatus(Long projectId,Long testRunId, String meaning) {
        EnumItemVo status = enumService.findOneEnumItemByCode(projectId, EnumCodes.TEST_RUN_STATUS, meaning);
        if (status!=null) {
            TestRunEntity testRunEntity = testRunDao.findById(testRunId);
            testRunEntity.setStatusId(status.getId());
            testRunEntity.setMeaning(meaning);
            testRunDao.updateTestRun(testRunEntity);
        }
    }
}
