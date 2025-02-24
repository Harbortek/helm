import { request, METHOD } from "@/utils/request";

export function findByPageId(projectId, pageId) {
  return request({
    url: `/smart-doc/${projectId}/${pageId}/doc/list`,
    method: METHOD.GET,
  });
}

export function saveDoc(projectId, pageId, doc) {
  return request({
    url: `/smart-doc/${projectId}/${pageId}/doc`,
    method: METHOD.POST,
    data: doc
  });
}

export function findOneProjectPage4Block(projectId,pageId) {
  return request({
    url: `/smart-doc/${projectId}/${pageId}/doc/findOneProjectPage4Block`,
    method: METHOD.GET,
  });
}
export function exportDoc2Word(projectId ,pageId) {
  const downloadUrl = process.env.VUE_APP_API_BASE_URL + `/smart-doc/${projectId}/${pageId}/doc/doc2word?pageId=${pageId}`;
  var iframe = document.createElement("iframe");
  iframe.style.display = "none";
  iframe.src = downloadUrl;
  iframe.target="_blank";
  iframe.onload = function () {
    document.body.removeChild(iframe);
  };
  document.body.appendChild(iframe);
}