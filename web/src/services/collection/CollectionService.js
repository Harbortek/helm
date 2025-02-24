import { request, METHOD } from "@/utils/request";
export function findCollectionsByProjectId(projectId, keyword,pageable) {
  return request({
    url: "/tracker/collection/listByProject",
    method: METHOD.GET,
    params: { projectId: projectId, keyword: keyword ,...pageable},
  });
}
export function listByDocument(projectId, documentId) {
  return request({
    url: "/tracker/collection/listByDocument",
    method: METHOD.GET,
    params: { projectId: projectId, documentId: documentId },
  });
}
export function createCollection(parameter) {
  return request({
    url: "/tracker/collection",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateCollection(parameter) {
  return request({
    url: "/tracker/collection",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteCollection(id) {
  return request({
    url: "/tracker/collection/" + id,
    method: METHOD.DELETE,
  });
}

