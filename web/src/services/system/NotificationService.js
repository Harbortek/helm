import { request, METHOD } from "@/utils/request";

export function findUnreadNotifications(page, pageSize) {
  const paramData = {
    page: page,
    size: pageSize,
  };
  return request({
    url: "/sys/notification/list/unread",
    method: METHOD.GET,
    params: paramData,
  });
}
export function findAllNotifications(page, pageSize) {
  const paramData = {
    page: page,
    size: pageSize,
  };
  return request({
    url: `/sys/notification/list/all`,
    method: METHOD.GET,
    params: paramData,
  });
}
export function readIt(id) {
  return request({
    url: `/sys/notification/read/${id}`,
    method: METHOD.POST,
  });
}

export function readAll() {
  return request({
    url: `/sys/notification/read/all`,
    method: METHOD.POST,
  });
}
