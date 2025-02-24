import { request, METHOD } from "@/utils/request";
import Cookies from "js-cookie";

export function findLinkTypes(projectId) {
  return request({
    url: `/tracker/project/${projectId}/linkType/list`,
    method: METHOD.GET,
  });
}
export function createLinkType(projectId, parameter) {
  return request({
    url: `/tracker/project/${projectId}/linkType`,
    method: METHOD.POST,
    data: parameter,
  });
}

export function deleteLinkType(projectId, id) {
  return request({
    url: `/tracker/project/${projectId}/linkType/${id}`,
    method: METHOD.DELETE,
  });
}

export function changeLinkTypeOrder(projectId, list) {
  return request({
    url: `/tracker/project/${projectId}/linkType/changeOrder`,
    method: METHOD.POST,
    data: list,
  });
}

export function findOneLinkType(projectId, id) {
  return request({
    url: `/tracker/project/${projectId}/linkType/${id}`,
    method: METHOD.GET,
  });
}

export function updateOneLinkType(projectId, data) {
  return request({
    url: `/tracker/project/${projectId}/linkType`,
    method: METHOD.PUT,
    data: data,
  });
}
