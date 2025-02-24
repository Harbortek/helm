import { request, METHOD } from "@/utils/request";
export function findPermissions(productLineId) {
  return request({
    url: `/product-line/${productLineId}/permission/list`,
    method: METHOD.GET,
  });
}
export function findPermissionGrants(productLineId) {
  return request({
    url: `/product-line/${productLineId}/permission/list-grant`,
    method: METHOD.GET,
  });
}
export function grantPermission(productLineId, permission, identity) {
  return request({
    url: `/product-line/${productLineId}/permission/grant`,
    method: METHOD.POST,
    params: { permission: permission },
    data: identity,
  });
}
export function unGrantPermission(productLineId, permission, identity) {
  return request({
    url: `/product-line/${productLineId}/permission/unGrant`,
    method: METHOD.POST,
    params: { permission: permission },
    data: identity,
  });
}

export function findProductLineRolesAndMembers(productLineId, scope) {
  return request({
    url: `/product-line/${productLineId}/permission/rolesAndMembers`,
    method: METHOD.GET,
    params: { scope: scope },
  });
}
