import { request, METHOD } from "@/utils/request";

export function findTestRuns(
  projectId,
  statusId,
  keyword,
  filter,
  pageable,
  sort
) {
  return request({
    url: "/test/run/list",
    method: METHOD.POST,
    params: {
      projectId,
      statusId,
      keyword,
      ...pageable,
      sort,
    },
    data: filter,
  });
}
export function findTestRunsByIds(projectId, testRunIds) {
  return request({
    url: "/test/run/listByIds",
    method: METHOD.GET,
    params: { projectId, testRunIds },
  });
}
export function findOneTestRun(id) {
  return request({
    url: "/test/run/" + id,
    method: METHOD.GET,
  });
}
export function createTestRun(parameter) {
  return request({
    url: "/test/run",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateTestRun(parameter) {
  return request({
    url: "/test/run",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteTestRun(id) {
  return request({
    url: "/test/run/" + id,
    method: METHOD.DELETE,
  });
}

export function findTestCaseIds(id) {
  return request({
    url: "/test/run/testCaseIds/" + id,
    method: METHOD.GET,
  });
}

export function associateTestCases(testRunId, testCaseIds) {
  return request({
    url: `/test/run/${testRunId}/associate/testCases?`,
    method: METHOD.PUT,
    data: testCaseIds,
  });
}
export function removeTestResults(testRunId, testResultIds) {
  return request({
    url: `/test/run/${testRunId}/remove/testResults?`,
    method: METHOD.PUT,
    data: testResultIds,
  });
}

export function copyTestResults(testRunId, testResultIds) {
  return request({
    url: `/test/run/${testRunId}/copy/testResults?`,
    method: METHOD.PUT,
    data: testResultIds,
  });
}

export function findTestResults(
  projectId,
  testRunId,
  filter,
  keyword,
  pageable,
  sort
) {
  return request({
    url: "/test/run/testResults",
    method: METHOD.POST,
    params: {
      projectId: projectId,
      testRunId: testRunId,
      keyword: keyword,
      ...pageable,
      sort,
    },
    data: filter,
  });
}

export function changeExecutor(testRunId, testResultIds, executorId) {
  return request({
    url: `/test/run/${testRunId}/changeExecutor`,
    method: METHOD.PUT,
    params: {
      executorId,
    },
    data: testResultIds,
  });
}

export function batchChangeResult(
  testRunId,
  testResultIds,
  resultId,
  resultDesc
) {
  return request({
    url: `/test/run/${testRunId}/changeResult`,
    method: METHOD.PUT,
    params: {
      resultId,
      resultDesc,
    },
    data: testResultIds,
  });
}

export function saveResult(
  testRunId,
  testResultId,
  resultId,
  resultDesc,
  testStepResults
) {
  return request({
    url: `/test/run/${testRunId}/saveResult`,
    method: METHOD.PUT,
    params: {
      testResultId,
      resultId,
      resultDesc,
    },
    data: testStepResults,
  });
}

export function linkTestResultWithDownstreamTrackerItems(
  testResultId,
  trackerItemIds
) {
  return request({
    url: `/test/run/trackerTestResultLink/link`,
    method: METHOD.POST,
    params: {
      testResultId,
    },
    data: trackerItemIds,
  });
}

export function unlinkTestResultWithDownstreamTrackerItems(
  testResultId,
  trackerItemIds
) {
  return request({
    url: `/test/run/trackerTestResultLink/unlink`,
    method: METHOD.POST,
    params: {
      testResultId,
      trackerItemIds,
    },
  });
}
export function findDownstreamTrackerItemsByTestResultId(testResultId) {
  return request({
    url: `/test/run/${testResultId}/downstreamTrackerItems`,
    method: METHOD.GET,
  });
}
export function findUpstreamTrackerTestResultsByTrackerItemId(itemId) {
  return request({
    url: `/test/run/${itemId}/upstreamTestResults`,
    method: METHOD.GET,
  });
}

export function findDownstreamTestResultsByTrackerItemId(itemId) {
  return request({
    url: `/test/run/${itemId}/downstreamTestResults`,
    method: METHOD.GET,
  });
}

export function findTestResultsByTrackerItemId(itemId) {
  return request({
    url: `/test/run/${itemId}/testResults`,
    method: METHOD.GET,
  });
}
