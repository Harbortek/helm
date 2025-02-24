import Cookie from "js-cookie";
import { cond } from "lodash";
import store from '@/store'

// 401拦截
const resp401 = {
  /**
   * 响应数据之前做点什么
   * @param response 响应对象
   * @param options 应用配置 包含: {router, i18n, store, message}
   * @returns {*}
   */
  onFulfilled(response, options) {
    const { message } = options;
    if (response.status === 401) {
      // message.error("无此权限");
    }
    return response;
  },
  /**
   * 响应出错时执行
   * @param error 错误对象
   * @param options 应用配置 包含: {router, i18n, store, message}
   * @returns {Promise<never>}
   */
  onRejected(error, options) {
    const { message, router } = options;
    const { response } = error;
    if (response.status === 401) {
      // message.error("无此权限");
    }
    return Promise.reject(error);
  },
};

const resp403 = {
  onFulfilled(response, options) {
    const { message } = options;
    if (response.status === 403) {
      message.error("请求被拒绝");
    }
    return response;
  },
  onRejected(error, options) {
    const { message } = options;
    const { response } = error;
    if (response.status === 403) {
      message.error("请求被拒绝");
    }
    return Promise.reject(error);
  },
};
const resp500 = {
  onFulfilled(response, options) {
    const { message } = options;
    if (response.status === 500 && response.data && response.data.message) {
      message.error(response.data.message);
    }
    return response;
  },
  onRejected(error, options) {
    const { message } = options;
    const { response } = error;
    if (response.status === 500 && response.data && response.data.message) {
      message.error(response.data.message);
    }else if (response.status === 500 && response.data instanceof Blob){
      response.data.text().then(res=>{
        if(JSON.parse(res)?.message){
          message.error(JSON.parse(res)?.message);
        }
      }
      )
    }
    return Promise.reject(error);
  },
};

const reqCommon = {
  /**
   * 发送请求之前做些什么
   * @param config axios config
   * @param options 应用配置 包含: {router, i18n, store, message}
   * @returns {*}
   */
  onFulfilled(config, options) {
    const { message, router } = options;
    const { url, xsrfCookieName } = config;
    let currentProjectId=store.getters["project/currentProjectId"];
    if(currentProjectId){
      config.headers['X-HELM-PROJECT-ID'] = currentProjectId;
    }
    // if (
    //   url.indexOf("login") === -1 &&
    //   xsrfCookieName &&
    //   !Cookie.get(xsrfCookieName)
    // ) {
    //   message.warning("认证 token 已过期，请重新登录");
    //   router.push({ path: "/login" });
    // }
    return config;
  },
  /**
   * 请求出错时做点什么
   * @param error 错误对象
   * @param options 应用配置 包含: {router, i18n, store, message}
   * @returns {Promise<never>}
   */
  onRejected(error, options) {
    const { message } = options;
    message.error(error.message);
    return Promise.reject(error);
  },
};

export default {
  request: [reqCommon], // 请求拦截
  response: [resp401, resp403, resp500], // 响应拦截
};
