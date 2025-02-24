import { request, METHOD } from "@/utils/request";
export function findViewsByObjectId(objectId, userId, display, isMatter) {
  return request({
    url: "tracker/project/view/list",
    method: METHOD.GET,
    params: { objectId, userId, display, isMatter},
  });
}
export function findOneView(id) {
  return request({
    url: "tracker/project/view/" + id,
    method: METHOD.GET,
  });
}
export function createView(parameter) {
  return request({
    url: "tracker/project/view",
    method: METHOD.POST,
    data: parameter,
  });
}
export function createDefaultView(trackerId) {
  return request({
    url: "tracker/project/view/default",
    method: METHOD.POST,
    params:{trackerId}
  });
}

export function createMattersDefaultView(pageId,userId) {
  return request({
    url: "tracker/project/view/matters/default",
    method: METHOD.POST,
    params:{pageId,userId}
  });
}

export function updateView(parameter) {
  return request({
    url: "tracker/project/view",
    method: METHOD.PUT,
    data: parameter,
  });
}

export function updateViewsOrdinary(parameter) {
  return request({
    url: "tracker/project/view/ordinary",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteView(id) {
  return request({
    url: "tracker/project/view/" + id,
    method: METHOD.DELETE,
  });
}

export function renameView(parameter) {
  return request({
    url: "tracker/project/view/rename",
    method: METHOD.PUT,
    data: parameter,
  });
}
