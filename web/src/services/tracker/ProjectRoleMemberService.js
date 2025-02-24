import { request, METHOD } from "@/utils/request";

export function findProjectRoleMembers(parameter) {
  return request({
    url: "tracker/project/role/member/list",
    method: METHOD.GET,
    params: parameter,
  });
}

export function updateProjectRoleMember(params, parameter) {
  return request({
    url: "tracker/project/role/member",
    method: METHOD.PUT,
    params: params,
    data: parameter,
  });
}
export function deleteProjectRoleMember(params, parameter) {
  return request({
    url: "tracker/project/role/member",
    method: METHOD.DELETE,
    params: params,
    data: parameter,
  });
}

export function findProjectUsers(proectId) {
  return request({
    url: "tracker/project/role/member/users",
    method: METHOD.GET,
    params: { projectId: proectId },
  });
}
export function findProjectUserByIds(ids) {
  return request({
    url: "tracker/project/role/member/user-by-ids",
    method: METHOD.GET,
    params: { ids: ids },
  });
}

export function findProjectRolesAndMembers(proectId, scope) {
  return request({
    url: "tracker/project/role/member/rolesAndMembers",
    method: METHOD.GET,
    params: { projectId: proectId, scope: scope },
  });
}
