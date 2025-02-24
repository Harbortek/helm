import VueI18nPlugin from "./i18n-extend";
import AuthorityPlugin from "./authority-plugin";
import BusPlugin from "./bus-plugin";
import VXETablePlugin from "./VXETable-plugin";
import Print from "vue-print-nb";
import HWebsocket from "../utils/websocket/index";
const Plugins = {
  install: function (Vue) {
    Vue.use(VueI18nPlugin);
    Vue.use(AuthorityPlugin);
    Vue.use(BusPlugin);
    Vue.use(VXETablePlugin);
    Vue.use(Print);
    Vue.use(HWebsocket);
  },
};
export default Plugins;
