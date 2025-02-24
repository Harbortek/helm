import { request, METHOD } from "@/utils/request";
export function findReportGroups(productLineId) {
  return request({
    url: `/product-line/${productLineId}/reportGroup/list`,
    method: METHOD.GET,
  });
}
export function findOneReportGroup(productLineId, id) {
  return request({
    url: `/product-line/${productLineId}/reportGroup/` + id,
    method: METHOD.GET,
  });
}
export function createReportGroup(productLineId, parameter) {
  return request({
    url: `/product-line/${productLineId}/reportGroup/`,
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateReportGroup(productLineId, parameter) {
  return request({
    url: `/product-line/${productLineId}/reportGroup/`,
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteReportGroup(productLineId, id) {
  return request({
    url: `/product-line/${productLineId}/reportGroup/` + id,
    method: METHOD.DELETE,
  });
}
