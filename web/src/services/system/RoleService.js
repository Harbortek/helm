import { request, METHOD } from "@/utils/request";

export function findRolePerms(roleId) {
  return request({
    url: "sys/role/perms/" + roleId,
    method: METHOD.GET,
  });
}
export function saveRolePerms(roleId, perms) {
  return request({
    url: "sys/role/perms/" + roleId,
    method: METHOD.POST,
    data: perms,
  });
}
export function isExistsByName(name, scope, ownerResourceId) {
  return request({
    url: "sys/role/exist-name/",
    method: METHOD.POST,
    params: { name, scope, ownerResourceId },
  });
}
export function isExistsByCode(code) {
  return request({
    url: "sys/role/exist-code/" + code,
    method: METHOD.GET,
  });
}
export function getRoles(parameter) {
  return request({
    url: "sys/role/list",
    method: METHOD.GET,
    params: parameter,
  });
}
export function getRolesNoPage(parameter) {
  return request({
    url: "sys/role/list-all",
    method: METHOD.GET,
    params: parameter,
  });
}
export function getRole(roleId) {
  return request({
    url: "sys/role/" + roleId,
    method: METHOD.GET,
  });
}
export function saveRole(parameter) {
  return request({
    url: "sys/role",
    method: METHOD.POST,
    params: parameter,
  });
}
export function updateRole(parameter) {
  return request({
    url: "sys/role",
    method: METHOD.PUT,
    params: parameter,
  });
}

export function deleteRole(roleId) {
  return request({
    url: "sys/role/" + roleId,
    method: METHOD.DELETE,
  });
}

export function batchDeleteRole(parameter) {
  return request({
    url: "sys/role/all",
    method: METHOD.DELETE,
    params: {
      roleIds: parameter.join(","),
    },
  });
}
