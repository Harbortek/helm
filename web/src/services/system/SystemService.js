import { request, METHOD } from "@/utils/request";

export function isExistsByName(name) {
  return request({
    url: "sys/system/exist-name/" + name,
    method: METHOD.GET,
  });
}
export function getSystems(parameter) {
  return request({
    url: "sys/system/list",
    method: METHOD.GET,
    params: parameter,
  });
}
export function getSystemsNoPage(parameter) {
  return request({
    url: "sys/system/list-all",
    method: METHOD.GET,
    params: parameter,
  });
}
export function getSystem(systemId) {
  return request({
    url: "sys/system/" + systemId,
    method: METHOD.GET,
  });
}
export function saveSystem(parameter) {
  return request({
    url: "sys/system",
    method: METHOD.POST,
    params: parameter,
  });
}
export function updateSystem(parameter) {
  return request({
    url: "sys/system",
    method: METHOD.PUT,
    data: parameter,
  });
}

export function deleteSystem(systemId) {
  return request({
    url: "sys/system/" + systemId,
    method: METHOD.DELETE,
  });
}
