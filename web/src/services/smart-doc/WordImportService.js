import { request, METHOD } from "@/utils/request";

export function findWordImportJob(projectId, pageId) {
  return request({
    url: `/smart-doc/${pageId}/import/word/job`,
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}

export function createWordImportJob(pageId, job) {
  return request({
    url: `/smart-doc/${pageId}/import/word/job`,
    method: METHOD.POST,
    data: job,
  });
}

export function updateWordImportJob(pageId, job) {
  return request({
    url: `/smart-doc/${pageId}/import/word/job`,
    method: METHOD.PUT,
    data: job,
  });
}

export function completeWordImportJob(pageId, job) {
  return request({
    url: `/smart-doc/${pageId}/import/word/job/complete`,
    method: METHOD.POST,
    data: job,
  });
}

export function deleteWordImportJob(pageId, id) {
  return request({
    url: `/smart-doc/${pageId}/import/word/job/${id}`,
    method: METHOD.DELETE,
  });
}

export function withdrawWordImportJob(pageId, id) {
  return request({
    url: `/smart-doc/${pageId}/import/word/job/${id}/withdraw`,
    method: METHOD.GET,
  });
}

export function findWordImportConfigs(projectId, keyword) {
  return request({
    url: `/smart-doc/import/word/config/list`,
    method: METHOD.GET,
    params: { projectId: projectId, keyword: keyword },
  });
}

export function createWordImportConfig(parameter) {
  return request({
    url: `/smart-doc/import/word/config/`,
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateWordImportConfig(parameter) {
  return request({
    url: `/smart-doc/import/word/config/`,
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteWordImportConfig(id) {
  return request({
    url: `/smart-doc/import/word/config/` + id,
    method: METHOD.DELETE,
  });
}
