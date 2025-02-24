import { mergeI18nFromRoutes } from "@/utils/i18n";
import deepMerge from "deepmerge";
import { cloneDeep } from 'lodash';
import basicOptions from "@/router/static-routes";
import { hasPermission } from "../utils/permission";
//应用配置
let appOptions = {
  router: undefined,
  i18n: undefined,
  store: undefined,
};

/**
 * 设置应用配置
 * @param options
 */
function setAppOptions(options) {
  const { router, store, i18n } = options;
  appOptions.router = router;
  appOptions.store = store;
  appOptions.i18n = i18n;
}

function parseRoutes(routesConfig) {
  let routes = [];
  routesConfig.forEach((item) => {
    // 获取注册在 routerMap 中的 router，初始化 routeCfg
    let router = Object.assign({}, item);
    router.meta = router.meta ? router.meta : {};
    const meta = {
      ...router.meta,
      icon: router.meta.icon,
      title: router.meta.title ? router.meta.title : "",
      show: router.meta.show,
    };
    router.meta = meta;
    if (router.children && router.children.length > 0) {
      router.children = parseRoutes(router.children);
    }
    routes.push(router);
  });
  let res = filterRoutes(routes);
  return res;
}

//过滤路由
function filterRoutes(routes) {
  const { store } = appOptions;
  let res = routes.filter((item) => {
    if (item.meta?.permission && !hasPermission(item.meta?.permission)) {
      console.log("no permission", item);
      return false;
    }
    if (item.children?.length == 0) {
      if (
        (item.name =
          "sys" ||
          item.name == "pipeline" ||
          item.name == "scm" ||
          item.name == "tracker")
      ) {
        return false;
      }
    }
    return true;
  });
  return res;
}

/**
 * 根据权限加载路由及菜单
 */
function loadRoutes() {
  const { router, store } = appOptions;
  const routes = parseRoutes(router.options.routes);
  // console.log("routes", routes);
  store.commit("account/setRoutesConfig", routes);
  // 初始化Admin后台菜单数据
  const rootRoute = router.options.routes.find((item) => item.path === "/");
  const menuRoutes = rootRoute && rootRoute.children;
  if (menuRoutes && menuRoutes.length > 0) {
    let menuData = cloneDeep(menuRoutes) || [];
    let configMenuData = null;
    menuData = menuData.filter((element) => {
      if (element.children) {
        delete element.children;
      }
      if (element.name == "config") {
        configMenuData = cloneDeep(element);
        return false;
      }
      return true;
    });
    // console.log("configMenuData", configMenuData);
    store.commit("setting/setMenuData", menuData);
    store.commit("setting/setConfigMenuData", configMenuData || {});
  }
}
/**
 * 深度合并路由
 * @param target {Route[]}
 * @param source {Route[]}
 * @returns {Route[]}
 */
function deepMergeRoutes(target, source) {
  // 映射路由数组
  const mapRoutes = (routes) => {
    const routesMap = {};
    routes.forEach((item) => {
      routesMap[item.path] = {
        ...item,
        children: item.children ? mapRoutes(item.children) : undefined,
      };
    });
    return routesMap;
  };
  const tarMap = mapRoutes(target);
  const srcMap = mapRoutes(source);

  // 合并路由
  const merge = deepMerge(tarMap, srcMap);

  // 转换为 routes 数组
  const parseRoutesMap = (routesMap) => {
    return Object.values(routesMap).map((item) => {
      if (item.children) {
        item.children = parseRoutesMap(item.children);
      } else {
        delete item.children;
      }
      return item;
    });
  };
  return parseRoutesMap(merge);
}

/**
 * 格式化路由
 * @param routes 路由配置
 */
function formatRoutes(routes) {
  routes.forEach((route) => {
    const { path } = route;
    if (!path.startsWith("/") && path !== "*") {
      route.path = "/" + path;
    }
  });
  //formatAuthority(routes);
}

/**
 * 从路由 path 解析 i18n key
 * @param path
 * @returns {*}
 */
function getI18nKey(path) {
  const keys = path
    .split("/")
    .filter((item) => !item.startsWith(":") && item != "");
  keys.push("name");
  return keys.join(".");
}

/**
 * 加载导航守卫
 * @param guards
 * @param options
 */
function loadGuards(guards, options) {
  const { beforeEach, afterEach } = guards;
  const { router } = options;
  beforeEach.forEach((guard) => {
    if (guard && typeof guard === "function") {
      router.beforeEach((to, from, next) => guard(to, from, next, options));
    }
  });
  afterEach.forEach((guard) => {
    if (guard && typeof guard === "function") {
      router.afterEach((to, from) => guard(to, from, options));
    }
  });
}

export {
  loadRoutes,
  // formatAuthority,
  getI18nKey,
  loadGuards,
  deepMergeRoutes,
  formatRoutes,
  setAppOptions,
};
