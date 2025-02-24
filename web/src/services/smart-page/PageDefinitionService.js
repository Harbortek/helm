import { request, METHOD } from "@/utils/request";
export function loadPageDefinition(pageId) {
  return request({
    url: `/smartPage/${pageId}/pageDefinition`,
    method: METHOD.GET,
  });
}
export function savePageDefinition(pageId, pageDefinition) {
  return request({
    url: `/smartPage/${pageId}/pageDefinition`,
    method: METHOD.POST,
    data: pageDefinition,
  });
}
