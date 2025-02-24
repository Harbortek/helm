<template>
  <div class="config-page">
    <div class="page-header">
      <div class="page-description page-description-info manager-desc" v-if="description">
        <div class="page-description-border"></div>
        <div class="page-description-container">
          <p class="page-description-p style--font-14">
            {{ description }}
          </p>
        </div>
      </div>
    </div>
    <div :class="autoFit ? 'page-content autofit' : 'page-content'">
      <div class="header-footer-panel">
        <div class="panel-header">
          <slot name="header"></slot>
        </div>
        <div class="panel-body">
          <slot></slot>
        </div>
        <div class="panel-footer">
          <slot name="footer"></slot>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState, mapMutations } from "vuex";
export default ({
  name: 'ConfigPage',
  props: {
    title: { type: String },
    description: { type: String },
    routes: { type: Array, default: () => [] },
    autoFit: { type: Boolean, default: false }
  },
  computed: {
    // routes: function () {
    //   let all = [];
    //   if (this.$route.matched.length > 6) {
    //     console.log(this.$route.matched)
    //     all.push(this.$route.matched[5])
    //     all.push(this.$route.matched[6])
    //   }else {
    //     all.push(this.$route.matched[this.$route.matched.length-1])
    //   }
    //   return all;
    // }
  },
  data() {
    return {
    };
  },
  watch: {
    routes: {
      immediate: true,
      handler: function (newVal, oldVal) {
        this.setBreadcrumb(newVal)
      }
    },
    // topTitle: {
    //   handler(v) {
    //     document.title = process.env.VUE_APP_NAME + ' | ' + v;
    //   },
    //   immediate: true
    // }
  },
  computed: {
    ...mapState("setting", [, "topTitle"]),

  },
  mounted() {

  },
  methods: {
    ...mapMutations('setting', ['setBreadcrumb']),
  }
})
</script>
<style lang="less" scoped>
.config-page {
  margin: 0;
  overflow: auto;
  padding: 20px;
  background: #fff;
  height: 100%;
  flex: 1 0 auto;
  display: flex;
  flex-direction: column;

  .page-header {
    width: 100%;
    flex: 0 0 auto;
    line-height: 1;

    .ant-breadcrumb {
      color: rgba(93, 99, 105);
      font-size: 18px;
      line-height: 26px;
    }

    .ant-breadcrumb a {
      color: rgba(93, 99, 105);
      font-size: 18px;
      line-height: 26px;
    }

  }

  .page-content {
    padding-top: 10px;
  }

  .page-content.autofit {
    height: 100%;
  }

  .header-footer-panel {
    // height: 100%;
    height: calc(100vh - 162px);
    display: flex;
    flex-direction: column;

    .panel-header {}

    .panel-body {
      // height: 100%;/
      height: calc(100vh - 240px);
      flex: 1 1 auto;
    }

    .panel-footer {}
  }
}



.page-description {
  display: flex;
  margin-top: 20px;
  line-height: 1.5;
  box-sizing: border-box;
  border-bottom: 1px solid #e8e8e8;
  border-radius: 0;
  border-right: 1px solid #e8e8e8;
  border-top: 1px solid #e8e8e8;
}

.page-description-border {
  border-radius: 0;
  flex: none;
  width: 4px;
}

.page-description-info .page-description-border {
  background: #338fe5;
}

.page-description-container {
  background: #fff;
  display: flex;
  flex: auto;
  padding: 10px;
}

.page-description-p {
  margin: 0 0 0 0;
  padding: 0;
  white-space: pre-wrap;
}

.style--font-14 {
  font-size: 14px;
  line-height: 22px;
}
</style>

