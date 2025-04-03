import { request, METHOD } from "@/utils/request";
export function findSprints(projectId, keyword) {
  return request({
    url: "/plan/sprint/list",
    method: METHOD.GET,
    params: { projectId: projectId, keyword: keyword },
  });
}
export function createSprint(parameter) {
  return request({
    url: "/plan/sprint",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateSprint(parameter) {
  return request({
    url: "/plan/sprint",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteSprint(id) {
  return request({
    url: "/plan/sprint/" + id,
    method: METHOD.DELETE,
  });
}
export function convertSprint(parameter,undone,sprintId) {
  return request({
    url: `/plan/sprint/convert?undone=${undone||''}&sprintId=${sprintId||''}`,
    method: METHOD.PUT,
    data: parameter,
  });
}
export function findUnPlanedSprints(projectId) {
  return request({
    url: "/plan/sprint/unPlaned",
    method: METHOD.GET,
    params: { projectId: projectId },
  });
}
