import { request, METHOD, removeAuthorization } from "@/utils/request";

/**
 * 登录服务
 * @param name 账户名
 * @param password 账户密码
 * @returns {Promise<AxiosResponse<T>>}
 */
export async function login(username, password) {
  return request("/auth/login", METHOD.POST, {
    data: {
      username: username,
      password: password,
    },
  });
}
/**
 * 获取用户信息
 * @returns
 */
export async function info() {
  return request("/user/info", METHOD.GET);
}
export async function getRoutesConfig() {
  return request(ROUTES, METHOD.GET);
}

/**
 * 更新用户
 * @returns
 */
export async function updateAccount(params) {
  return request("/sys/user", METHOD.PUT, {params});
}
/**
 * 密码重置
 * @param {*} userId
 * @returns
 */
export function resetPassword(userId) {
  return request(`sys/user/reset-pwd/${userId}`, METHOD.POST);
}

/**
 * 密码更新
 * @param {*} params --userId,password,oldPwd
 * @returns
 */
export function updatePassword(params) {
  return request("sys/user/password", METHOD.PUT, { params });
}
/**
 * 头像更新
 * @param {*} data
 * @returns
 */
export function updateAvatar(data) {
  return request("sys/user/avatar", METHOD.POST, { data });
}
/**
 * 退出登录
 */
export function logout() {
  removeAuthorization();
}
export default {
  login,
  logout,
  getRoutesConfig,
  updateAccount,
};
