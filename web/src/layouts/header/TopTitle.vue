<template>
  <!-- <span v-if="title" :class="!collapsed ? 'top-title' : 'top-title-2'">
    {{ title }}</span> -->
  <div class="page-header">
    <a-breadcrumb :routes="routes" separator=">">
      <template slot="itemRender" slot-scope="{route, params, routes, paths}" v-if="routes.length > 0">
        <span v-if="routes.indexOf(route) === routes.length - 1">
          {{ $t(route.meta.title) }}
        </span>
        <router-link v-else :to="route">
          {{ $t(route.meta.title) }}
        </router-link>
      </template>
      <a-breadcrumb-item v-if="routes.length === 0">{{ title }}</a-breadcrumb-item>
    </a-breadcrumb>
  </div>
</template>

<script>
import { mapState } from "vuex";
export default {
  name: "TopTitle",
  computed: {
    ...mapState("setting", [, "collapsed", "topTitle", "breadcrumb"]),
    title() {
      // const route = this.$route;
      // if (route.matched.length > 0) {
      //   return this.$t(route.matched[1]?.meta?.title);
      // }
      // return "";
      return this.topTitle
    },
    routes() {
      if (this.breadcrumb && this.breadcrumb.length > 0) {
        // console.log(this.breadcrumb)
        return this.breadcrumb
      } else {
        return []
      }
    }
  },
};
</script>

<style lang="less" scoped>
.top-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;

  height: 24px;
  line-height: 24px;
  font-size: 18px;
  margin: 0 20px;
  border-right: 1px;
  border-color: solid;
}

.top-title-2 {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
  font-size: 18px;
  height: 24px;
  line-height: 24px;
  margin: 0 20px 0 0;
  border-right: 1px;
  border-color: solid;
}

.page-header {
  // width: 100%;
  float: left;
  flex: 0 0 auto;
  line-height: 48px;
  margin-top: 10px;
  margin-left: 20px;

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
</style>
