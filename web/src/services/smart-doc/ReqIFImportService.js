import { request, METHOD } from "@/utils/request";

export function findReqIFImportJob(projectId, pageId) {
  return request({
    url: `/smart-doc/${pageId}/import/reqIf/job`,
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}

export function createReqIFImportJob(pageId, job) {
  return request({
    url: `/smart-doc/${pageId}/import/reqIf/job`,
    method: METHOD.POST,
    data: job,
  });
}

export function updateReqIFImportJob(pageId, job) {
  return request({
    url: `/smart-doc/${pageId}/import/reqIf/job`,
    method: METHOD.PUT,
    data: job,
  });
}

export function completeReqIFImportJob(pageId, job) {
  return request({
    url: `/smart-doc/${pageId}/import/reqIf/job/complete`,
    method: METHOD.POST,
    data: job,
  });
}

export function deleteReqIFImportJob(pageId, id) {
  return request({
    url: `/smart-doc/${pageId}/import/reqIf/job/${id}`,
    method: METHOD.DELETE,
  });
}

export function withdrawReqIFImportJob(pageId, id) {
  return request({
    url: `/smart-doc/${pageId}/import/reqIf/job/${id}/withdraw`,
    method: METHOD.GET,
  });
}

export function findReqIFImportConfigs(projectId, keyword) {
  return request({
    url: `/smart-doc/import/reqIf/config/list`,
    method: METHOD.GET,
    params: { projectId: projectId, keyword: keyword },
  });
}

export function createReqIFImportConfig(parameter) {
  return request({
    url: `/smart-doc/import/reqIf/config/`,
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateReqIFImportConfig(parameter) {
  return request({
    url: `/smart-doc/import/reqIf/config/`,
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteReqIFImportConfig(id) {
  return request({
    url: `/smart-doc/import/reqIf/config/` + id,
    method: METHOD.DELETE,
  });
}
