import Vue from "vue";
import App from "./App.vue";
import "./theme/index.less";
import Antd from "ant-design-vue";
// import '@/mock'
import { initRouter } from "./router";
import store from "./store";
import "animate.css/source/animate.css";
import Plugins from "@/plugins";
import { i18n } from "@/utils/i18n";
import "@/utils/string";
import bootstrap from "@/bootstrap";
import "moment/locale/zh-cn";
import NProgress from "nprogress";
import "./directives";
import "@/components/icon/iconfont";
import CronLight from "@/components/cron/light";
import "@wangeditor/editor/dist/css/style.css";
import "normalize.css/normalize.css";


import { Boot } from "@wangeditor/editor";
import { trackerItemModule, titleModule, slashCommandModule, attachmentModule,aiModule,mentionModule, umlModule, formulaModule } from '@/components/smart-doc/wangeditor/plugins/index.js'

// import docxPlugin from 'wangeditor-plugin-docx'
Boot.registerModule(trackerItemModule)
Boot.registerModule(titleModule)
Boot.registerModule(slashCommandModule)
Boot.registerModule(attachmentModule)
Boot.registerModule(aiModule)
// Boot.registerModule(mentionModule)
// Boot.registerModule(umlModule)
// Boot.registerModule(formulaModule)


NProgress.start();
const router = initRouter();
// const i18n = initI18n("CN", "US");
Vue.use(Antd);
Vue.config.productionTip = false;
Vue.use(Plugins);
Vue.use(CronLight);

import VueColorPicker from "@duoa/vue-color-picker";
// Because this components has its styles, you must also import the css file.
import "@duoa/vue-color-picker/dist/vue-color-picker.css";

// Register
Vue.use(VueColorPicker);

import "vue-resize/dist/vue-resize.css";
import VueResize from "vue-resize";
Vue.use(VueResize);

import HIcon from "@/components/icon/hicon";
Vue.use(HIcon);

import XEUtils from "xe-utils";
import VXEUtils from "vxe-utils";

Vue.use(VXEUtils, XEUtils, { mounts: ["cookie"] });

import VueClipboard from "vue-clipboard2";
Vue.use(VueClipboard);

const app = new Vue({
  router,
  store,
  i18n,
  render: (h) => h(App),
  mounted() {
    NProgress.done();
  },
});

const boot = bootstrap({
  router,
  store,
  i18n,
  message: Vue.prototype.$message,
});
boot.then(() => app.$mount("#app"));
