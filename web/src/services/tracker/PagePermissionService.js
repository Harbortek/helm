import { request, METHOD } from "@/utils/request";

export function batchUpdatePagePermission(pagePermissions) {
  return request({
    url: "/tracker/project/page/permission/batch",
    method: METHOD.PUT,
    data: pagePermissions,
  });
}
export function findPagePermissionPerm() {
  return request({
    url: "/tracker/project/page/permission/perm",
    method: METHOD.GET,
  });
}