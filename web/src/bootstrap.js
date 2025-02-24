import { loadRoutes, loadGuards, setAppOptions } from '@/router/router-helper'
import { loadInterceptors, checkAuthorization } from '@/utils/request'
import guards from '@/router/guards'
import interceptors from '@/utils/axios-interceptors'
import initEnv from './env'
/**
 * 启动引导方法
 * 应用启动时需要执行的操作放在这里
 * @param router 应用的路由实例
 * @param store 应用的 vuex.store 实例
 * @param i18n 应用的 vue-i18n 实例
 * @param i18n 应用的 message 实例
 */
async function bootstrap({ router, store, i18n, message }) {
  //开发环境设置 微应用HTTP地址
  initEnv();
  // 设置应用配置
  setAppOptions({ router, store, i18n })
  // 加载 axios 拦截器
  loadInterceptors(interceptors, { router, store, i18n, message })

  // 加载路由守卫
  loadGuards(guards, { router, store, i18n, message })

  //获取登录用户信息
  if (checkAuthorization()) {
    await store.dispatch('account/getInfo');
  } else {
    router.push({ path: '/login' });
  }

  //加载路由
  loadRoutes();


}

export default bootstrap
