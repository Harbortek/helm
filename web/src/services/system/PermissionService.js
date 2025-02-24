import { request, METHOD } from "@/utils/request";

export function getPerms() {
  return request({
    url: "sys/perm/list",
    method: METHOD.GET,
  });
}
