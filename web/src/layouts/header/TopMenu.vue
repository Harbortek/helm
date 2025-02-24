<template>
  <!-- <span class="top-menu-left-border">
    <router-link :to="item.path" :key="index" v-for="(item, index) in topMenus" class="top-menu"
      v-show="!topMenuComponent">
      <div class="top-menu-item" v-if="item.meta.show != false">
        <a-icon v-if="item.meta.icon" :type="item.meta.icon" />
        {{ $t(item.meta?.title) }}
      </div>
    </router-link>
    <a-row type="flex" v-show="topMenuComponent">
      <a-col flex="auto">
        <div id="topMenuComponent"></div>
      </a-col>
      <a-col flex="auto">
          <router-link :to="item.path" :key="index" v-for="(item, index) in topMenuData" class="top-menu">
            <div class="top-menu-item" v-if="item.meta.show != false">
              <a-icon v-if="item.meta.icon" :type="item.meta.icon" />
              {{ item.meta?.title }}
            </div>
          </router-link>
        </a-col>
    </a-row>
  </span> -->

  <div class="top-menu-left-border" style="flex:1 1 auto;">
    <div id="topMenuComponenet"></div>
    <!-- <a-col flex="auto">
          <router-link :to="item.path" :key="index" v-for="(item, index) in topMenuData" class="top-menu">
            <div class="top-menu-item" v-if="item.meta.show != false">
              <a-icon v-if="item.meta.icon" :type="item.meta.icon" />
              {{ item.meta?.title }}
            </div>
          </router-link>
        </a-col> -->
  </div>
</template>

<script>
// import { mapState, mapMutations, mapGetters } from "vuex";
export default {
  name: "TopMenuView",

  computed: {
    // ...mapState("account", ["routesConfig"]),
    // ...mapState("setting", ["topMenuData", "topMenuComponent"]),
    // topMenus() {
    //   let all = [];
    //   if (this.$route.matched.length > 0) {
    //     const moduleName = this.$route.matched[1].name;
    //     function filterMenu(routes, name) {
    //       for (const item of routes) {
    //         if (item.name === name) {
    //           all = item.children ? item.children : [];
    //           return;
    //         } else {
    //           if (!item.children || item.children.length > 0) {
    //             filterMenu(item.children || [], name);
    //           }
    //         }
    //       }
    //     }
    //     filterMenu(this.routesConfig, moduleName);
    //   }
    //   return all;
    // },
  },
};
</script>

<style lang="less" scoped>
.top-menu-left-border {
  float: left;
  margin-left: 20px;

  &::before {
    position: relative;
    display: block;
    content: "";
    float: left;
    left: 0;
    background: #c7c7c7;
    height: 24px;
    top: 12px;
    width: 1px;
  }

  .top-menu {
    color: #606060;

    &:hover {
      color: #338fe5;
      font-weight: 500;
      // border-top: 3px solid #338fe5;
    }

    &.router-link-active {
      .top-menu-item {
        border-top: 3px solid #338fe5;
        background-color: #f8f8f8;
        transition: all 0.2s;
        color: #338fe5;
        font-size: 500;
      }
    }

    &:first-child {
      margin-left: 20px;
    }

    .top-menu-item {
      display: inline-block;
      max-width: 230px;
      height: 48px;
      border-top: 3px solid transparent;
      border-bottom: 3px solid transparent;
      font-weight: 500;
      padding: 0 15px;

      background: transparent;

      &::before {
        position: absolute;
        content: "";
        height: 20px;
        left: 0;
        width: 1px;
        background: #c7c7c7;
        top: 50%;
        margin-top: -10px;
      }
    }
  }
}
</style>
