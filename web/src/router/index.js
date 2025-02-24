import Vue from 'vue'
import Router from 'vue-router'
import {formatRoutes} from '@/router/router-helper'
import isInWhiteList from './white-list'
import staticRoutes from './static-routes'
Vue.use(Router)

/**
 * 初始化路由实例
 * @param isAsync 是否异步路由模式
 * @returns {VueRouter}
 */
function initRouter() {
  const options = staticRoutes;
  formatRoutes(options.routes)
  console.log('路由',options);
  return new Router(options)
}
export {isInWhiteList, initRouter}
