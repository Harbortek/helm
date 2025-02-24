import store from "@/store";

export function hasPermission(permission, resourceId) {
  const permissions = store.getters["account/user"]?.permissions || [];
  let matched = permissions?.find((v) => {
    if (v.name === permission) {
      if (resourceId) {
        return v.resourceId === resourceId;
      }
      return true;
    } else {
      return false;
    }
  });
  return matched;
}

//获取拥有权限的trackerIds
export function getTrackerPermissionIds(trackerPermName) {
  const permissions = store.getters["account/user"]?.permissions || [];
  let trackerPerm = permissions?.filter(
    (v) => v.name == trackerPermName
  )||[];
  return trackerPerm?.map(v => v.resourceId) || [];
}
