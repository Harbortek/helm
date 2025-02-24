import { request, METHOD } from "@/utils/request";

export function findProjectTemplates(parameter) {
  return request({
    url: "tracker/project/template/list",
    method: METHOD.GET,
    params: parameter,
  });
}
export function findOneProjectTemplate(id) {
  return request({
    url: "tracker/project/template/" + id,
    method: METHOD.GET,
  });
}
export function createProjectTemplate(parameter) {
  return request({
    url: "tracker/project/template",
    method: METHOD.POST,
    data: parameter,
  });
}

export function updateProjectTemplate(parameter) {
  return request({
    url: "tracker/project/template",
    method: METHOD.PUT,
    data: parameter,
  });
}
export function deleteProjectTemplate(id) {
  return request({
    url: "tracker/project/template/" + id,
    method: METHOD.DELETE,
  });
}
export function saveAsTemplate(template) {
  return request({
    url: "tracker/project/template/saveAsTemplate",
    method: METHOD.POST,
    data: template,
  });
}
export function findRoleMembersByTemplate(id) {
  return request({
    url: `tracker/project/template/${id}/roles`,
    method: METHOD.GET,
  });
}
