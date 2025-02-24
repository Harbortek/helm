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
import com.harbortek.helm.test.dao.TestReportDao;
import com.harbortek.helm.test.dao.TestResultDao;
import com.harbortek.helm.test.dao.TestRunDao;
import com.harbortek.helm.test.entity.TestReportEntity;
import com.harbortek.helm.test.entity.TestResultEntity;
import com.harbortek.helm.test.service.TestReportService;
import com.harbortek.helm.test.service.TestRunService;
import com.harbortek.helm.test.vo.TestReportVo;
import com.harbortek.helm.test.vo.TestResultStat;
import com.harbortek.helm.test.vo.TestRunVo;
import com.harbortek.helm.tracker.constants.EnumCodes;
import com.harbortek.helm.tracker.constants.TestResults;
import com.harbortek.helm.test.dao.TrackerTestResultLinkDao;
import com.harbortek.helm.test.entity.TrackerTestResultLinkEntity;
import com.harbortek.helm.tracker.service.TrackerItemService;
import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import com.harbortek.helm.util.DataUtils;
import com.harbortek.helm.util.IDUtils;
import com.harbortek.helm.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TestReportServiceImpl implements TestReportService {

    @Autowired
    private TestReportDao testReportDao;

    @Autowired
    private TestRunDao testRunDao;

    @Autowired
    private TestResultDao testResultDao;

    @Autowired
    private TrackerTestResultLinkDao trackerTestResultLinkDao;

    @Autowired
    private TestRunService testRunService;

    @Autowired
    private EnumService enumService;

    @Autowired
    private TrackerItemService trackerItemService;

    @Override
    public Page<TestReportVo> findTestReports(Long projectId, String keyword, Pageable pageable) {
        Page<TestReportEntity> testReports = testReportDao.findTestReports(projectId, keyword, pageable);
        List<TestReportVo> vos = DataUtils.toVo(testReports.getContent(), TestReportVo.class);
        return new PageImpl<>(vos,pageable, testReports.getTotalPages());
    }

    @Override
    public TestReportVo findOneTestReport(Long reportId) {
        TestReportEntity testReportEntity = testReportDao.findById(reportId);
        TestReportVo testReportVo = DataUtils.toVo(testReportEntity,TestReportVo.class);
        testReportVo.setTotalCount(testResultDao.countTestResultsByTestReportId(reportId));
        EnumItemVo passed = enumService.findOneEnumItemByCode(testReportEntity.getProjectId(),
                                                             EnumCodes.TEST_CASE_RESULT,
                                                     TestResults.PASSED);
        testReportVo.setPassedCount(testResultDao.countPassedTestResultsByTestReportId(reportId,passed.getId()));
        testReportVo.setFailedCount(testReportVo.getTotalCount() - testReportVo.getPassedCount());

        testReportVo.setTestRuns( testRunService.findTestRunByIds(testReportEntity.getProjectId(),
                                                      testReportEntity.getTestRunIds()) );


        return testReportVo;
    }

    @Override
    public TestReportVo createTestReport(TestReportVo testReport) {
        testReport.setId(IDUtils.getId());
        TestReportEntity testReportEntity = DataUtils.toEntity(testReport, TestReportEntity.class);
        if (ObjectUtils.isNotEmpty(testReport.getTestRuns())) {
            testReportEntity.setTestRunIds(testReport.getTestRuns().stream().map(TestRunVo::getId)
                                                     .collect(java.util.stream.Collectors.toList()));
        }
        testReportEntity = testReportDao.createTestReport(testReportEntity);
        return DataUtils.toVo(testReportEntity,TestReportVo.class);
    }

    @Override
    public TestReportVo updateTestReport(TestReportVo testReport) {
        TestReportEntity testReportEntity = DataUtils.toEntity(testReport, TestReportEntity.class);
        testReportDao.updateTestReport(testReportEntity);
        return testReport;
    }

    @Override
    public void deleteTestReport(Long id) {
        testReportDao.deleteTestReport(id);
    }

    @Override
    public List<TestReportVo> findTestReportByTestRunId(Long testRunId) {
        List<TestReportEntity> testReports = testReportDao.findByTestRunId(testRunId);
        return DataUtils.toVo(testReports,TestReportVo.class);
    }

    @Override
    public List<TrackerItemVo> findDownstreamTrackerItemsByTestReportId(Long testReportId) {

        TestReportEntity testReportEntity = testReportDao.findById(testReportId, TestReportEntity.class);
        List<Long> testRunIds = testReportEntity.getTestRunIds();
        List<TestResultEntity> testResults = testResultDao.findByTestRunIds(testRunIds);

        Collection<Long> resultIds = testResults.stream().map(TestResultEntity::getId).collect(
                Collectors.toList());

        List<TrackerTestResultLinkEntity> trackerTestResultLinkEntities =
                trackerTestResultLinkDao.findByTestResultIds(resultIds);
        List<Long> trackerItemIds = trackerTestResultLinkEntities.stream().map(TrackerTestResultLinkEntity::getTrackerItemId).collect(
                Collectors.toList());
        return trackerItemService.findTrackerItemByIds(trackerItemIds);
    }

    @Override
    public List<TestResultStat> countTestResultsGroupByResult(Long testReportId) {
        TestReportEntity testReportEntity = testReportDao.findById(testReportId, TestReportEntity.class);
        List<Long> testRunIds = testReportEntity.getTestRunIds();
        List<TestResultStat> testResults = testResultDao.countTestResultsGroupByResult(testRunIds);
        List<EnumItemVo> enumItemVos = enumService.findEnumItemsByCode(testReportEntity.getProjectId(),
                                                                      EnumCodes.TEST_CASE_RESULT);

        Map<Long,EnumItemVo> enumItemVoMap = new HashMap<>();
        enumItemVos.forEach(vo->{
            enumItemVoMap.putIfAbsent(vo.getId(),vo);
        });

        testResults.forEach(testResultStat->{
            if (ObjectUtils.isValid(testResultStat.getId())) {
                EnumItemVo enumItemVo = enumItemVoMap.get(testResultStat.getId());
                if (enumItemVo != null) {
                    testResultStat.setColor(enumItemVo.getColor());
                    testResultStat.setName(enumItemVo.getName());
                }
            }else{
                testResultStat.setId(0L);
                testResultStat.setColor("#F8F8F9");
                testResultStat.setName("未运行");
            }
        });


        return testResults;
    }
}
