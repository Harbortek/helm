import { request, METHOD } from "@/utils/request";
export function getSQLPreview(pageId, dataset) {
  return request({
    url: `/smartPage/${pageId}/dataset/preview`,
    method: METHOD.POST,
    data: dataset,
  });
}

export function findDatasetsByPageId(pageId) {
  return request({
    url: `/smartPage/${pageId}/dataset/list`,
    method: METHOD.GET,
  });
}
export function findOneDataset(pageId, id) {
  return request({
    url: `/smartPage/${pageId}/dataset/${id}`,
    method: METHOD.GET,
  });
}
export function createDataset(pageId, dataset) {
  return request({
    url: `/smartPage/${pageId}/dataset`,
    method: METHOD.POST,
    data: dataset,
  });
}

export function updateDataset(pageId, dataset) {
  return request({
    url: `/smartPage/${pageId}/dataset`,
    method: METHOD.PUT,
    data: dataset,
  });
}
export function deleteDataset(pageId, id) {
  return request({
    url: `/smartPage/${pageId}/dataset/${id}`,
    method: METHOD.DELETE,
  });
}

export function renameDataset(pageId, dataset) {
  return request({
    url: `/smartPage/${pageId}/dataset/rename`,
    method: METHOD.PUT,
    data: dataset,
  });
}

export function findEnumValues(pageId, datasetId,field) {
  return request({
    url: `/smartPage/${pageId}/dataset/${datasetId}/enum`,
    method: METHOD.GET,
    params: {field},
  });
}
