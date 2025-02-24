import { request, METHOD } from "@/utils/request";

export function isExistsByName(name) {
  return request({
    url: "sys/param/exist-name/" + name,
    method: METHOD.GET,
  });
}
export function isExistsByCode(code) {
  return request({
    url: "sys/param/exist-code/" + code,
    method: METHOD.GET,
  });
}
export function getParams(parameter) {
  return request({
    url: "sys/param/list",
    method: METHOD.GET,
    params: parameter,
  });
}
export function getParam(paramId) {
  return request({
    url: "sys/param/" + paramId,
    method: METHOD.GET,
  });
}
export function saveParam(parameter) {
  return request({
    url: "sys/param",
    method: METHOD.POST,
    params: parameter,
  });
}

export function updateParam(parameter) {
  return request({
    url: "sys/param",
    method: METHOD.PUT,
    params: parameter,
  });
}

export function deleteParam(paramId) {
  return request({
    url: "sys/param/" + paramId,
    method: METHOD.DELETE,
  });
}

export function batchDeleteParam(parameter) {
  return request({
    url: "sys/param/all",
    method: METHOD.DELETE,
    params: {
      paramIds: parameter.join(","),
    },
  });
}
