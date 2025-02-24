import config from "@/config";
import { ADMIN } from "@/config/default";
import { formatFullPath } from "@/utils/i18n";
import { filterMenu } from "@/utils/authority-utils";
import { getLocalSetting } from "@/utils/themeUtil";
import { cloneDeep } from 'lodash';

const localSetting = getLocalSetting(true);

export default {
  namespaced: true,
  state: {
    isMobile: false,
    animates: ADMIN.animates,
    palettes: ADMIN.palettes,
    pageMinHeight: 0,
    menuData: [],
    topMenuData: [],
    topMenuComponent: [],
    configMenuData: {},
    activatedFirst: undefined,
    topTitle: "",
    breadcrumb: [],
    ...config,
    collapsed: false,
    ...localSetting,
  },
  getters: {
    collapsed(state, getters, rootState) {
      return state.collapsed;
    },
    menuData(state, getters, rootState) {
      if (state.filterMenu) {
        const { permissions, roles } = rootState.account;
        return filterMenu(cloneDeep(state.menuData), permissions, roles);
      }
      return state.menuData;
    },
    configMenuData(state, getters, rootState) {
      return state.configMenuData || {};
    },
    topMenuData(state, getters, rootState) {
      return state.topMenuData || {};
    },
    topMenuComponent(state, getters, rootState) {
      return state.topMenuComponent || {};
    },
    firstMenu(state, getters) {
      const { menuData } = getters;
      if (menuData.length > 0 && !menuData[0].fullPath) {
        formatFullPath(menuData);
      }
      return menuData.map((item) => {
        const menuItem = { ...item };
        delete menuItem.children;
        return menuItem;
      });
    },
    subMenu(state) {
      const { menuData, activatedFirst } = state;
      if (menuData.length > 0 && !menuData[0].fullPath) {
        formatFullPath(menuData);
      }
      const current = menuData.find((menu) => menu.fullPath === activatedFirst);
      return (current && current.children) || [];
    },
    topTitle(state) {
      return state.topTitle || "";
    },
    breadcrumb(state) {
      return state.breadcrumb || [];
    },
  },
  mutations: {
    setDevice(state, isMobile) {
      state.isMobile = isMobile;
    },
    setCollapsed(state, collapsed) {
      state.collapsed = collapsed;
    },
    setTheme(state, theme) {
      state.theme = theme;
    },
    setLayout(state, layout) {
      state.layout = layout;
    },
    setMultiPage(state, multiPage) {
      state.multiPage = multiPage;
    },
    setAnimate(state, animate) {
      state.animate = animate;
    },
    setWeekMode(state, weekMode) {
      state.weekMode = weekMode;
    },
    setFixedHeader(state, fixedHeader) {
      state.fixedHeader = fixedHeader;
    },
    setFixedSideBar(state, fixedSideBar) {
      state.fixedSideBar = fixedSideBar;
    },
    setLang(state, lang) {
      state.lang = lang;
    },
    setHideSetting(state, hideSetting) {
      state.hideSetting = hideSetting;
    },
    correctPageMinHeight(state, minHeight) {
      state.pageMinHeight += minHeight;
    },
    setMenuData(state, menuData) {
      state.menuData = menuData;
    },
    setTopMenuData(state, topMenuData) {
      state.topMenuData = topMenuData;
    },
    setTopMenuComponent(state, topMenuComponent) {
      state.topMenuComponent = topMenuComponent;
    },

    setConfigMenuData(state, configMenuData) {
      state.configMenuData = configMenuData;
    },
    setAsyncRoutes(state, asyncRoutes) {
      state.asyncRoutes = asyncRoutes;
    },
    setPageWidth(state, pageWidth) {
      state.pageWidth = pageWidth;
    },
    setActivatedFirst(state, activatedFirst) {
      state.activatedFirst = activatedFirst;
    },
    setSystemName(state, systemName) {
      state.systemName = systemName;
    },
    setSystemLogo(state, systemLogo) {
      state.systemLogo = systemLogo;
    },
    setTopTitle(state, topTitle) {
      state.topTitle = topTitle;
      document.title = "HELM | " + topTitle;
    },
    setBreadcrumb(state, breadcrumb) {
      state.breadcrumb = breadcrumb;
    },
  },
};
