import { request, METHOD } from "@/utils/request";

export function findProjectCategories() {
  return request({
    url: "/tracker/project/categories",
    method: METHOD.GET,
  });
}

export function findProjects(parameter, filter) {
  return request({
    url: "/tracker/project/list",
    method: METHOD.POST,
    params: parameter,
    data: filter,
  });
}
export function findOneProject(id) {
  return request({
    url: `/tracker/project/${id}`,
    method: METHOD.GET,
  });
}
export function findPermissions() {
  return request({
    url: "/tracker/project/permissions",
    method: METHOD.GET,
  });
}
export function createProject(parameter) {
  return request({
    url: "/tracker/project",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateProject(parameter) {
  return request({
    url: "/tracker/project",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteProject(id) {
  return request({
    url: "tracker/project" + "/" + id,
    method: METHOD.DELETE,
  });
}
export function clearTrackerItemData(id) {
  return request({
    url: "tracker/project/clear-item/" + id,
    method: METHOD.DELETE,
  });
}
export function checkDuplicateProjectName(id, name) {
  return request({
    url: "/tracker/project/checkDuplicateProjectName",
    method: METHOD.GET,
    params: { id: id, name: name },
  });
}
export function checkDuplicateProjectShortName(id, shortName) {
  return request({
    url: "/tracker/project/checkDuplicateProjectShortName",
    method: METHOD.GET,
    params: { id: id, shortName: shortName },
  });
}
export function findNotEndedProjects(keyword) {
  return request({
    url: "tracker/project/notEndedProjects",
    method: METHOD.GET,
    params: { keyword: keyword },
  });
}
export function copyProject(parameter) {
  return request({
    url: "/tracker/project/copyProject",
    method: METHOD.POST,
    data: parameter,
  });
}
export function findRecentProjects(keyword) {
  return request({
    url: "/tracker/project/recentProjects",
    method: METHOD.GET,
    params: { keyword: keyword },
  });
}

export function recordRecentProject(projectId) {
  return request({
    url: "/tracker/project/recordProject",
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}
