import Vue from "vue";
import { Spin } from "ant-design-vue";

// 创建一个 Loading 组件的构造函数
const LoadingConstructor = Vue.extend({
  template: `
    <div v-if="visible" class="global-loading">
      <a-spin tip="正在加载，请稍后..."></a-spin>
    </div>
  `,
  components: {
    "a-spin": Spin,
  },
  data() {
    return {
      visible: false,
    };
  },
  methods: {
    show() {
      this.visible = true;
    },
    hide() {
      this.visible = false;
    },
  },
});

// 存储 Loading 实例
let loadingInstance;

// 显示 Loading 的函数
const showLoading = () => {
  if (!loadingInstance) {
    loadingInstance = new LoadingConstructor().$mount();
    document.body.appendChild(loadingInstance.$el);
  }
  loadingInstance.show();
};

// 隐藏 Loading 的函数
const hideLoading = () => {
  if (loadingInstance) {
    loadingInstance.hide();
  }
};

// 导出 Loading 服务
export default {
  show: showLoading,
  hide: hideLoading,
};
