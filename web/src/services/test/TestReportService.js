import { request, METHOD } from "@/utils/request";

export function findTestReports(projectId, keyword, pageable, sort) {
  return request({
    url: "/test/report/list",
    method: METHOD.POST,
    params: {
      projectId: projectId,
      keyword: keyword,
      ...pageable,
      sort,
    },
  });
}
export function findOneTestReport(id) {
  return request({
    url: "/test/report/" + id,
    method: METHOD.GET,
  });
}
export function createTestReport(parameter) {
  return request({
    url: "/test/report",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateTestReport(parameter) {
  return request({
    url: "/test/report",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteTestReport(id) {
  return request({
    url: "/test/report/" + id,
    method: METHOD.DELETE,
  });
}

export function findTestReportByTestRunId(testRunId) {
  return request({
    url: "/test/report/testRun/" + testRunId,
    method: METHOD.GET,
  });
}

export function findDownstreamTrackerItems(testReportId) {
  return request({
    url: "/test/report/downstreamTrackerItems",
    method: METHOD.GET,
    params: { testReportId },
  });
}

export function countTestResultsGroupByResult(testReportId) {
  return request({
    url: "/test/report/testResults",
    method: METHOD.GET,
    params: { testReportId },
  });
}
