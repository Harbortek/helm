import { request, METHOD } from "@/utils/request";

export function updateAvatar(formData) {
  return request({
    url: "sys/user/avatar",
    method: METHOD.POST,
    data: formData,
  });
}
export function updatePassword(parameter) {
  return request({
    url: "sys/user/password",
    method: METHOD.PUT,
    params: parameter,
  });
}
export function getUserRoles(userId) {
  return request({
    url: "sys/user/roles/" + userId,
    method: METHOD.GET,
  });
}
export function updateRoles(userId, roles) {
  return request({
    url: "sys/user/roles/" + userId,
    method: METHOD.PUT,
    data: roles,
  });
}
export function isExistsByName(name) {
  return request({
    url: "sys/user/exist-name/" + name,
    method: METHOD.GET,
  });
}
export function getUsers(parameter) {
  return request({
    url: "sys/user/list",
    method: METHOD.GET,
    params: parameter,
  });
}
export function getUser(userId) {
  return request({
    url: "sys/user/" + userId,
    method: METHOD.GET,
  });
}
export function saveUser(parameter, password) {
  return request({
    url: "sys/user",
    method: METHOD.POST,
    data: parameter,
    params: { password },
  });
}
export function resetPwd(userId) {
  return request({
    url: "sys/user/reset-pwd/" + userId,
    method: METHOD.POST,
  });
}
export function updateUser(parameter) {
  return request({
    url: "sys/user",
    method: METHOD.PUT,
    data: parameter,
  });
}

export function deleteUser(userId) {
  return request({
    url: "sys/user/" + userId,
    method: METHOD.DELETE,
  });
}

export function batchDeleteUser(parameter) {
  return request({
    url: "sys/user/all",
    method: METHOD.DELETE,
    params: {
      userIds: parameter.join(","),
    },
  });
}
