<template>
  <div class="vue-friendly-iframe">
  </div>
</template>

<script>
import { nanoid } from 'nanoid'

import { debounce } from 'throttle-debounce'
function generateGuid() {
  return nanoid();
}
export default {
  name: 'h-iframe',
  props: {
    src: {
      type: String,
      required: false
    },
    srcdoc: {
      type: String,
      required: false
    },
    crossorigin: {
      type: String,
      required: false,
      default: 'anonymous'
    },
    target: {
      type: String,
      required: false,
      default: '_parent'
    },
    className: {
      type: String,
      required: false
    },
    allow: {
      type: String,
      required: false
    },
    name: {
      type: String,
      required: false
    },
    title: {
      type: String,
      required: false
    },
    sandbox: {
      type: String,
      required: false
    }
  },
  data() {
    return {
      iframeEl: null,
      iframeloading: true,
    };
  },
  computed: {},
  watch: {
    src() {
      this.reinitIframe();
    },
    srcdoc() {
      this.reinitIframe();
    }
  },
  methods: {
    removeIframe() {
      while (this.$el.firstChild) {
        this.$el.removeChild(this.$el.firstChild);
      }
    },
    reinitIframe() {
      this.removeIframe();
      this.initIframe();
    },
    initIframe() {
      this.iframeEl = document.createElement('iframe');
      this.iframeLoad();
      this.iframeEl.setAttribute('style', 'width:100%;height:100%;visibility: visible; border: none;');
      if (this.src) this.iframeEl.setAttribute('src', this.src);
      if (this.srcdoc) this.iframeEl.setAttribute('srcdoc', this.srcdoc);
      if (this.className) this.iframeEl.setAttribute('class', this.className);
      if (this.class) this.iframeEl.setAttribute('class', this.class);
      if (this.crossorigin) this.iframeEl.setAttribute('crossorigin', this.crossorigin);
      if (this.target) this.iframeEl.setAttribute('target', this.target);
      if (this.allow) this.iframeEl.setAttribute('allow', this.allow);
      if (this.name) this.iframeEl.setAttribute('name', this.name);
      if (this.title) this.iframeEl.setAttribute('title', this.title);
      if (this.sandbox) this.iframeEl.setAttribute('sandbox', this.sandbox);
      this.$el.appendChild(this.iframeEl);
    },
    iframeLoad() {
      const that = this;
      // 处理兼容行问题 兼容IE
      if (this.iframeEl.attachEvent) {
        this.iframeEl.attachEvent('onload', function () {
          // iframe加载完毕以后执行操作
          that.iframeloading = false;
          that.$emit('load');
        })
      } else {
        this.iframeEl.onload = function () {
          // iframe加载完毕以后执行操作
          that.iframeloading = false;
          that.$emit('load');
        }
      }
    }
  },
  mounted() {
    this.initIframe();
  }
};
</script>
<style lang="less" scoped>
.vue-friendly-iframe {
  width: 100%;
  height: 100%;
}
</style>