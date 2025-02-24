import { removeAuthorization } from "@/utils/request";
import { info } from "@/services/global/LoginService";
export default {
  namespaced: true,
  state: {
    user: undefined,
    // permissions: null,
    roles: null,
    routesConfig: null,
    sideMenu: null,
  },
  getters: {
    user: (state) => {
      if (!state.user) {
        try {
          const user = localStorage.getItem(process.env.VUE_APP_USER_KEY);
          state.user = JSON.parse(user);
        } catch (e) {
          console.error(e);
        }
      }
      return state.user;
    },
    // permissions: (state) => {
    //   if (!state.permissions) {
    //     try {
    //       const permissions = localStorage.getItem(
    //         process.env.VUE_APP_PERMISSIONS_KEY
    //       );
    //       state.permissions = JSON.parse(permissions);
    //       state.permissions = state.permissions ? state.permissions : [];
    //     } catch (e) {
    //       console.error(e.message);
    //     }
    //   }
    //   return state.permissions;
    // },
    routesConfig: (state) => {
      if (!state.routesConfig) {
        try {
          const routesConfig = localStorage.getItem(
            process.env.VUE_APP_ROUTES_KEY
          );
          state.routesConfig = JSON.parse(routesConfig);
          state.routesConfig = state.routesConfig ? state.routesConfig : [];
        } catch (e) {
          console.error(e.message);
        }
      }
      return state.routesConfig;
    },
    sideMenu: (state) => {
      return state.sideMenu || [];
    },
  },
  mutations: {
    setUser(state, user) {
      state.user = user;
      // localStorage.setItem(process.env.VUE_APP_USER_KEY, JSON.stringify(user))
    },
    // setPermissions(state, permissions) {
    //   state.permissions = permissions;
    //   localStorage.setItem(
    //     process.env.VUE_APP_PERMISSIONS_KEY,
    //     JSON.stringify(permissions)
    //   );
    // },
    setRoutesConfig(state, routesConfig) {
      state.routesConfig = routesConfig;
      // window.localStorage.setItem(process.env.VUE_APP_ROUTES_KEY, JSON.stringify(routesConfig))
    },
    setSideMenu(state, sideMenu) {
      state.sideMenu = sideMenu;
    },
  },
  actions: {
    getInfo({ commit }) {
      return new Promise((resolve, reject) => {
        info()
          .then(function (res) {
            commit("setUser", res);
            resolve(res);
          })
          .catch(function (error) {
            removeAuthorization();
            reject(error);
          });
      });
    },
  },
};
