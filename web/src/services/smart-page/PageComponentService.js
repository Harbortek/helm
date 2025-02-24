import { request, METHOD } from "@/utils/request";
export function findComponentsByPageId(pageId) {
  return request({
    url: `/smartPage/${pageId}/component/list`,
    method: METHOD.GET,
  });
}
export function findOneComponent(pageId, id) {
  return request({
    url: `/smartPage/${pageId}/component/${id}`,
    method: METHOD.GET,
  });
}
export function createComponent(pageId, component) {
  return request({
    url: `/smartPage/${pageId}/component`,
    method: METHOD.POST,
    data: component,
  });
}

export function getCompoenentId(pageId) {
  return request({
    url: `/smartPage/${pageId}/component/id`,
    method: METHOD.GET,
  });
}
export function getData(pageId, componentId, data) {
  return request({
    url: `/smartPage/${pageId}/component/${componentId}/data`,
    method: METHOD.POST,
    data: data,
  });
}
