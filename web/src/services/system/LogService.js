import { request, METHOD } from "@/utils/request";
export function getLogs(parameter) {
  return request({
    url: "sys/log/list",
    method: METHOD.GET,
    params: parameter,
  });
}
export function getLog(logId) {
  return request({
    url: "sys/log/" + logId,
    method: "GET",
  });
}
