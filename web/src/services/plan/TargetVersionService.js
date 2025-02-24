import { request, METHOD } from "@/utils/request";
export function findVersions(projectId) {
  return request({
    url: "/plan/version/list",
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}
export function createVersion(parameter) {
  return request({
    url: "/plan/version",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateVersion(parameter) {
  return request({
    url: "/plan/version",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteVersion(id) {
  return request({
    url: "/plan/version/" + id,
    method: METHOD.DELETE,
  });
}
