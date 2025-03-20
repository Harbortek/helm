import { request, METHOD } from "@/utils/request";

export function findProjectRoles(projectId, parameter) {
  return request({
    url: `tracker/project/${projectId}/role/list`,
    method: METHOD.GET,
    params: parameter,
  });
}

export function findOneProjectRole(projectId, id) {
  return request({
    url: `tracker/project/${projectId}/role/${id}`,
    method: METHOD.GET,
  });
}

export function createProjectRole(projectId, parameter) {
  return request({
    url: `tracker/project/${projectId}/role`,
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateProjectRole(projectId, parameter) {
  return request({
    url: `tracker/project/${projectId}/role`,
    method: METHOD.PUT,
    data: parameter,
  });
}

export function deleteProjectRole(projectId, id) {
  return request({
    url: `tracker/project/${projectId}/role/${id}`,
    method: METHOD.DELETE,
  });
}

export function findProjectRoleFields(projectId, id) {
  return request({
    url: `tracker/project/${projectId}/role/${id}/fields`,
    method: METHOD.GET,
  });
}