import { request, download, METHOD } from "@/utils/request";

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
    data: doc,
  });
}

export function findOneProjectPage4Block(projectId, pageId) {
  return request({
    url: `/smart-doc/${projectId}/${pageId}/doc/findOneProjectPage4Block`,
    method: METHOD.GET,
  });
}
export function exportDoc2Word(projectId, pageId) {
  let url = `/smart-doc/${projectId}/${pageId}/doc/doc2word`;
  download(url, { pageId: pageId });
}
 