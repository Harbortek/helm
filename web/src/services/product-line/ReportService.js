import { request, METHOD } from "@/utils/request";
export function findReports(productLineId, keyword) {
  return request({
    url: `/product-line/${productLineId}/report/list`,
    method: METHOD.GET,
    params: { keyword: keyword },
  });
}
export function findOneReport(productLineId, id) {
  return request({
    url: `/product-line/${productLineId}/report/` + id,
    method: METHOD.GET,
  });
}
export function createReport(productLineId, parameter) {
  return request({
    url: `/product-line/${productLineId}/report/`,
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateReport(productLineId, parameter) {
  return request({
    url: `/product-line/${productLineId}/report/`,
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteReport(productLineId, id) {
  return request({
    url: `/product-line/${productLineId}/report/` + id,
    method: METHOD.DELETE,
  });
}
