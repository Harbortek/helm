import { request, METHOD } from "@/utils/request";

export function findPermissionGrants(projectId) {
  return request({
    url: "/tracker/project/permission/list-grant",
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}
export function grantPermission(projectId, permission, identity) {
  return request({
    url: "/tracker/project/permission/grant",
    method: METHOD.POST,
    params: { projectId: projectId, permission: permission },
    data: identity,
  });
}
export function unGrantPermission(projectId, permission, identity) {
  return request({
    url: "/tracker/project/permission/unGrant",
    method: METHOD.POST,
    params: { projectId: projectId, permission: permission },
    data: identity,
  });
}

export function findProjectIdsByPermission(permission) {
  return request({
    url: "/tracker/project/permission/projectIds/"+permission,
    method: METHOD.GET,
  });
}
