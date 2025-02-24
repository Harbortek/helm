import { request, METHOD } from "@/utils/request";
export function findCommitsByItemId(itemId) {
  return request({
    url: "/tracker/commit/list",
    method: METHOD.GET,
    params:{itemId}
  });
}
export function findCommitsBySprintId(sprintId) {
  return request({
    url: "/tracker/commit/list-sprint",
    method: METHOD.GET,
    params:{sprintId}
  });
}
export function createCommits(parameter) {
  return request({
    url: "/tracker/commit",
    method: METHOD.POST,
    data: parameter,
  });
}
