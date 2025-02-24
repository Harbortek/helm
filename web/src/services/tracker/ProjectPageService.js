import { request, METHOD } from "@/utils/request";
import Cookies from "js-cookie";

export function findByProjectId(projectId) {
  return request({
    url: "/tracker/project/page/list",
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}
export function findByPageIds(pageIds) {
  return request({
    url: "/tracker/project/page/byIds",
    method: METHOD.POST,
    data:pageIds
  });
}
export function createProjectPage(parameter) {
  return request({
    url: "/tracker/project/page",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateProjectPageInfo(parameter) {
  return request({
    url: "/tracker/project/page/info",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteProjectPage(parameter) {
  return request({
    url: "/tracker/project/page",
    method: METHOD.DELETE,
    data: parameter,
  });
}

export function changeProjectPageOrder(list) {
  return request({
    url: "/tracker/project/page/changeOrder",
    method: METHOD.POST,
    data: list,
  });
}

export function findOneProjectPage(id) {
  return request({
    url: "tracker/project/page/" + id,
    method: METHOD.GET,
  });
}

export function findOneAndChildrenByPageId(id) {
  return request({
    url: "tracker/project/page/" + id + "/children",
    method: METHOD.GET,
  });
}
export function findTrackerItemsByPageIdWithoutInternalTrackers(projectId,pageId,pageable,sort) {
  return request({
    url: `/smart-doc/${projectId}/${pageId}/doc/trackerItemsWithoutInternalTrackers?`+sort?.map((_) => `sort=${_}`).join("&"),
    method: METHOD.GET,
    params: { ...pageable },
  });
}
export function findTrackerItemsByPageId(id) {
  return request({
    url: `/smart-doc/${id}/block/trackerItems`,
    method: METHOD.GET,
  });
}
export function updateOneProjectPage(id, parameter) {
  return request({
    url: "tracker/project/page/" + id,
    method: METHOD.PUT,
    data: parameter,
  });
}


export function findPageByComponentType(projectId, componentType) {
  return request({
    url: `/tracker/project/page/byComponentType`,
    method: METHOD.GET,
    params: {projectId,componentType},
  });
}

export function addWatch(pageId, newVal) {
  return request({
    url: `/tracker/project/page/${pageId}/add-watch`,
    method: METHOD.PUT,
    data: newVal,
  });
}
export function cancelWatch(pageId, newVal) {
  return request({
    url: `/tracker/project/page/${pageId}/cancel-watch`,
    method: METHOD.PUT,
    data: newVal,
  });
}
