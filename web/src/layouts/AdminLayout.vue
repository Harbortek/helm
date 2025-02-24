<template>
  <a-layout style="height: 100%; overflow-y: hidden">
    <side-menu :sideMenuWidth="sideMenuWidth" :theme="'dark'" :menuData="sideMenuData" :collapsed="collapsed"
      :collapsible="true" @toggleCollapse="toggleCollapse" />

    <a-layout style="height: 100%; overflow-y: hidden">
      <admin-header class="admin-layout beauty-scroll" :style="headerStyle" :collapsed="collapsed"
        @toggleCollapse="toggleCollapse" :logoWidth="sideMenuWidth">
        <router-view slot="title" name="TopTitle"></router-view>
        <router-view slot="topMenu" name="TopMenu"></router-view>
      </admin-header>
      <a-layout-content class="admin-layout-content beauty-scroll" id="helmAdminLayoutContent">
        <router-view class="viewport" style="position: relative; height: 100%"></router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script>
import AdminHeader from "./header/AdminHeader";
import SideMenu from "../components/menu/SideMenu";
import { mapState, mapMutations, mapGetters } from "vuex";
import watermark from '../utils/watermark';

// const minHeight = window.innerHeight - 64 - 122

export default {
  name: "AdminLayout",
  components: { SideMenu, AdminHeader },
  data() {
    return {
      minHeight: window.innerHeight - 48,
      showSetting: false,
      drawerOpen: false,
    };
  },
  provide() {
    return {
      adminLayout: this,
    };
  },
  watch: {
    $route(val) {
      this.setActivated(val);
    },
    layout() {
      this.setActivated(this.$route);
    },
  },
  mounted() {
    watermark.set('helmAdminLayoutContent', this.user?.name);
  },
  computed: {
    ...mapState("setting", ["theme", "collapsed"]),
    ...mapGetters("setting", ["menuData"]),
    ...mapGetters("account", ["user"]),
    sideMenuWidth() {
      return this.collapsed ? "60px" : "200px";
    },
    headerStyle() {
      // let width = `calc(100% - ${this.sideMenuWidth})`;
      let width = `100%`;
      return `width: ${width};`;
    },
    sideMenuData() {
      const { menuData } = this;
      return menuData;
    },
  },
  methods: {
    ...mapMutations('setting', ['setCollapsed']),
    toggleCollapse() {
      this.setCollapsed(!this.collapsed);
    },
    onMenuSelect() {
      this.toggleCollapse();
    },
    setActivated(route) {
      if (this.layout === "mix") {
        let matched = route.matched;
        matched = matched.slice(0, matched.length - 1);
        const { firstMenu } = this;
        for (let menu of firstMenu) {
          if (matched.findIndex((item) => item.path === menu.fullPath) !== -1) {
            this.setActivatedFirst(menu.fullPath);
            break;
          }
        }
      }
    },
  },
};
</script>

<style lang="less">
.admin-layout {
  .side-menu {
    &.fixed-side {
      position: fixed;
      height: 100vh;
      left: 0;
      top: 0;
    }
  }

  .ant-layout-header {
    padding: 0px;
  }

  .ant-layout-content {
    padding: 0px;
  }

  .ant-layout-sider {
    padding: 0px;
  }

  .virtual-side {
    transition: all 0.2s;
  }

  .virtual-header {
    transition: all 0.2s;
    opacity: 0;
  }

  .admin-layout-main {
    .admin-header {
      top: 0;
      right: 0;
      overflow: hidden;
      transition: all 0.2s;
    }
  }

  .admin-layout-content {
    padding: 0 0;
    margin: 0 0;

    *overflow-x: hidden;
    height: calc(100% - 48px);
    min-height: calc(100% - 48px);

    .viewport {
      overflow-y: hidden;
      padding: 1px 0px 0px 1px;
    }
  }
}
</style>
