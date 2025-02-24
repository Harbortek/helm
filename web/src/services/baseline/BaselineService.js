import { request, METHOD } from "@/utils/request";
export function findBaselinesByProjectId(projectId, keyword) {
  return request({
    url: "/tracker/baseline/listByProject",
    method: METHOD.GET,
    params: { projectId: projectId, keyword: keyword },
  });
}
export function listByDocument(projectId, documentId) {
  return request({
    url: "/tracker/baseline/listByDocument",
    method: METHOD.GET,
    params: { projectId: projectId, documentId: documentId },
  });
}
export function createBaseline(parameter) {
  return request({
    url: "/tracker/baseline",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateBaseline(parameter) {
  return request({
    url: "/tracker/baseline",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteBaseline(id) {
  return request({
    url: "/tracker/baseline/" + id,
    method: METHOD.DELETE,
  });
}

export function compareBaseline(projectId, lefrId, rightId) {
  return request({
    url: "/tracker/baseline/compare",
    method: METHOD.GET,
    params: { projectId: projectId, leftId: lefrId, rightId: rightId },
  });
}

export function findItemsHistory(historyIds) {
  return request({
    url: "/tracker/baseline/item-history",
    method: METHOD.POST,
    data: historyIds,
  });
}
export function findDocumentHistory(historyIds) {
  return request({
    url: "/tracker/baseline/document-history",
    method: METHOD.POST,
    data: historyIds,
  });
}
