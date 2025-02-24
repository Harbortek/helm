import isInWhiteList from "./white-list";
import { checkAuthorization, removeAuthorization } from "@/utils/request";
import NProgress from "nprogress";
import store from "@/store";
import { hasPermission } from "@/utils/permission";


NProgress.configure({ showSpinner: false });

/**
 * 进度条开始
 * @param to
 * @param form
 * @param next
 */
const progressStart = (to, from, next) => {
  // start progress bar
  if (!NProgress.isStarted()) {
    NProgress.start();
  }
  store.commit("setting/setTopMenuComponent", false);
  next();
};

/**
 * 登录守卫
 * @param to
 * @param form
 * @param next
 * @param options
 */
const loginGuard = (to, from, next, options) => {
  const { message } = options;
  //路由不在白名单，且cookie里没有token
  if (!isInWhiteList(to) && !checkAuthorization()) {
    message.warning("登录已失效，请重新登录");
    next({ path: "/login" });
  } else {
    next();
  }
};
/**
 * 获取登录用户信息，含permission及授权菜单
 *
 * @param to
 * @param from
 * @param next
 * @param options
 */
const userGuard = (to, from, next, options) => {
  const { store, message } = options;
  if (!isInWhiteList(to)) {
    const user = store.getters["account/user"];
    if (!user) {
      store
        .dispatch("account/getInfo")
        .then((res) => {
          next();
        })
        .catch((error) => {
          next(error);
        });
    } else {
      next();
    }
  } else {
    next();
  }
};
/**
 * 权限守卫
 * @param to
 * @param form
 * @param next
 * @param options
 */
const authorityGuard = (to, from, next, options) => {
  const { store, message } = options;
  let noPerm=false;
  if(to.params?.projectId){
    if(!hasPermission("PROJECT_VIEW",to.params.projectId)){
      noPerm=true
    }
    if((Object.keys(to.params).length==1||to.path.includes("/config/"))
        &&!hasPermission("PROJECT_ADMIN",to.params.projectId)
      ){
        noPerm=true
    }else if(to.params?.pageId&&!hasPermission("PAGE_READ",to.params.pageId)){
      noPerm=true
    }
  }else if(to.meta?.permission&&!hasPermission(to.meta.permission)){
    noPerm=true
  }
  if(noPerm){
    message.warning(`对不起，您无权访问页面: ${to.fullPath}，请联系管理员`)
    // next('/login')
    // next(-1)
    return;
  }
  next();
  

  // const permissions = store.getters['account/user'].permissions;
  // const roles = []
  // if (!hasAuthority(to, permissions, roles)) {
  //   message.warning(`对不起，您无权访问页面: ${to.fullPath}，请联系管理员`)
  //   next({path: '/403'})
  //   // NProgress.done()
  // } else {
  // }
  // next();
};


/**
 * 进度条结束
 * @param to
 * @param form
 * @param options
 */
const progressDone = () => {
  // finish progress bar
  NProgress.done();
};

export default {
  beforeEach: [progressStart, loginGuard, userGuard, authorityGuard],
  afterEach: [progressDone],
};
