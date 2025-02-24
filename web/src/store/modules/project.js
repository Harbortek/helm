export default {
  namespaced: true,
  state: {
    currentProjectId: undefined,
    currentProjectKeyName:undefined,
  },
  getters: {
    currentProjectId: (state) => {
      if (!state.currentProjectId) {
        try {
          const currentProjectId = localStorage.getItem(
            process.env.VUE_APP_CURRENT_PROJECT_ID
          );
          state.currentProjectId = currentProjectId;
        } catch (e) {
          console.error(e);
        }
      }
      return state.currentProjectId;
    },
    currentProjectKeyName: (state) => {
      return state.currentProjectKeyName;
    }
  },
  mutations: {
    setCurrentProjectId(state, projectId) {
      state.currentProjectId = projectId;
      if (!projectId) {
        localStorage.removeItem(process.env.VUE_APP_CURRENT_PROJECT_ID);
      } else {
        localStorage.setItem(process.env.VUE_APP_CURRENT_PROJECT_ID, projectId);
      }
    },
    setCurrentProjectKeyName(state, projectKeyName) {
      state.currentProjectKeyName = projectKeyName;
    },
  },
  actions: {},
};
