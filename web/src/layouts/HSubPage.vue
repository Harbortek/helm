<template>
  <div class="h-sub-container">
    <!-- 
      name(必传)：应用名称
      url(必传)：应用地址，会被自动补全为http://localhost:3000/index.html
      baseroute(可选)：基座应用分配给子应用的基础路由，就是上面的 `/my-page`
     -->
    <a-result v-if="hasErr" status="warning" :title="errMsg">

    </a-result>
    <micro-app :name='appName' :url='appUrl' :baseroute='appRoute' :data='microAppData' @created='handleCreate'
      @beforemount='handleBeforeMount' @mounted='handleMount' @unmount='handleUnmount' @error='handleError'
      @datachange='handleDataChange' class="full"></micro-app>

  </div>
</template>
<script>
import store from '@/store'
import { AppEvents } from '@/utils/event'
import microApp from "@micro-zoe/micro-app";
import { mapState, mapMutations, mapGetters } from 'vuex'
export default {
  name: 'HSubPage',
  props: {
    appName: '', appUrl: '', appRoute: ''
  },
  data() {
    return {
      microAppData: {
        //设置环境用
        type: 'ENV',
        currentUser: store.getters["account/user"],
        appInfo: {
          appName: this.appName,
          appUrl: this.appUrl,
          appRoute: this.appRoute
        },
      },
      hasErr: false,
      errMsg: ''
    }
  },
  computed: {
    ...mapState('setting', ['lang'])
  },
  watch: {
    lang(n, o) {
      //下发国际化
      microApp.setData(this.appName, { type: AppEvents.SET_LANG, n });
    }
  },
  created() {
    console.log(`${this.appName}` + `    ${this.appUrl}` + `    ${this.appRoute}  LOADING...`);
  },
  methods: {
    handleCreate() {
      this.hasErr = false;
      console.log(this.appName + ' 创建了')
    },
    handleBeforeMount() {
      this.hasErr = false;
      console.log(this.appName + '  即将被渲染')
    },
    handleMount() {
      this.hasErr = false;
      console.log(this.appName + ' 已经渲染完成')
    },
    handleUnmount() {
      this.hasErr = false;
      console.log(this.appName + ' 卸载了')
    },
    handleError(e, r) {
      this.hasErr = true;
      this.errMsg = e.detail.error.message;
      this.$bus.$emit(AppEvents.CONTENT_LOADING, {
        type: AppEvents.CONTENT_LOADING,
        data: false
      });
      console.log(this.appName + ' 加载出错了')
    },
    handleDataChange(e) {
      console.log(this.appName + ` 来自子应用 ${this.appName} 的数据:`, e.detail.data);
      const data = e.detail.data
      //**触发指定事件 */
      data && AppEvents[data.type] && this.$bus.$emit(data.type, data);
    }
  }
}
</script>
<style lang="less" scoped>
.h-sub-container {
  width: 100%;
  height: 100%;

  .full {
    width: 100%;
    height: 100%;
  }

  /deep/ micro-app-body {
    width: 100%;
    height: 100%;
  }
}
</style>