import { request, METHOD } from "@/utils/request";

export function findProjectRoles(parameter) {
  return request({
    url: "tracker/project/role/list",
    method: METHOD.GET,
    params: parameter,
  });
}
export function findOneProjectRole(id) {
  return request({
    url: "tracker/project/role/" + id,
    method: METHOD.GET,
  });
}
export function createProjectRole(parameter) {
  return request({
    url: "tracker/project/role",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateProjectRole(parameter) {
  return request({
    url: "tracker/project/role",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteProjectRole(id) {
  return request({
    url: "tracker/project/role/" + id,
    method: METHOD.DELETE,
  });
}
export function findProjectRoleFields(id) {
  return request({
    url: "tracker/project/role/" + id + "/fields",
    method: METHOD.GET,
  });
}
