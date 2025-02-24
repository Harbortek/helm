import { request, METHOD } from "@/utils/request";

export function findReqIFFiles(pageId, filePath) {
  return request({
    url: `/smart-doc/${pageId}/export/reqIf/job/files`,
    method: METHOD.GET,
    params: { filePath: filePath },
  });
}

export function loadExportJob(pageId) {
  return request({
    url: `/smart-doc/${pageId}/export/reqIf/job`,
    method: METHOD.GET,
  });
}

export function saveExportJob(pageId, job) {
  return request({
    url: `/smart-doc/${pageId}/export/reqIf/job`,
    method: METHOD.POST,
    data: job,
  });
}

export function findReqIFExportTemplates(projectId, keyword) {
  return request({
    url: `/smart-doc/export/reqIf/template/list`,
    method: METHOD.GET,
    params: { projectId: projectId, keyword: keyword },
  });
}

export function createReqIFExportTemplate(parameter) {
  return request({
    url: `/smart-doc/export/reqIf/template/`,
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateReqIFExportTemplate(parameter) {
  return request({
    url: `/smart-doc/export/reqIf/template/`,
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteReqIFExportTemplate(id) {
  return request({
    url: `/smart-doc/export/reqIf/template/` + id,
    method: METHOD.DELETE,
  });
}
export function exportReqIF(pageId) {
  const downloadUrl =
    process.env.VUE_APP_API_BASE_URL +
    `/smart-doc/${pageId}/export/reqIf/job/export`;
  var iframe = document.createElement("iframe");
  iframe.style.display = "none";
  iframe.src = downloadUrl;
  iframe.target = "_blank";
  iframe.onload = function () {
    document.body.removeChild(iframe);
  };
  document.body.appendChild(iframe);
}
