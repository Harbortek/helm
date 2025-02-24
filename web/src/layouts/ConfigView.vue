<template>
  <div class="layout-full-height config-center" style="margin-top: 10px">
    <div class="left-nav beauty-scroll" style="position: relative;margin-top:0px">
      <div class="left-block-container">
        <div class="left-block_search-input_wrapper" style="margin-top:5px">
          <a-input-search v-model="keyword" placeholder="搜索配置项" @search="onSearch" />
        </div>
        <div class="left-block_action">
          <span class="desc">配置项</span>
        </div>
        <div class="left-navs-tree">
          <a-collapse :active-key="activeIndex" :bordered="false">
            <a-collapse-panel :key="item.name" v-for="(item) in filterMenus" style="border: none">
              <div class="navs-header" slot="header">
                {{ $t(item.meta.title) }}
              </div>
              <a :class="selectedItem === child.name
            ? 'left-nav-link-selected'
            : 'left-nav-link'
            " :key="child.name" v-for="(child) in visibleMenus(item.children)" @click="handleSelect(child)">{{
            $t(child.meta.title) }}</a>
            </a-collapse-panel>
          </a-collapse>

          <!-- <a-menu
            :default-selected-keys="defaultSelectedKeys"
            :default-open-keys="defaultOpenKeys"
            mode="inline"
            @select="handleSelect"
          >
            <a-sub-menu :key="item.name" v-for="(item, index) in menus">
              <span slot="title"
                ><a-icon :type="item.meta.icon" /><span>{{
                  $t(item.meta.title)
                }}</span></span
              >
              <a-menu-item
                :key="child.name"
                v-for="(child, idx) in item.children"
              >
                {{ $t(child.meta.title) }}
              </a-menu-item>
            </a-sub-menu>
          </a-menu> -->
        </div>
      </div>
    </div>
    <div class="scroll-div setting-main_wrapper">
      <div class="setting-main">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from "vuex";
import IMenu from "@/components/menu/menu";
import XEUtils from 'xe-utils'
export default {
  name: "ConfigView",
  components: { IMenu },
  data() {
    return {
      openKeys: [],
      selectedItem: "",
      activeIndex: '',
      keyword: '',
    };
  },
  computed: {
    ...mapState("account", ["routesConfig"]),
    filterMenus() {
      let that = this
      const keyword = this.keyword
      if (keyword && keyword !== '') {
        console.log(this.menus)
        const result = XEUtils.searchTree(this.menus, item => {
          const title = that.$t(item?.meta?.title)
          return title.indexOf(keyword) >= 0
        }, { children: 'children' })
        console.log(result)
        return result
      }
      return this.menus
    },
    menus() {
      let all = [];
      function configMenus(routes, name) {
        for (const item of routes) {
          if (item.name === name) {
            all = item.children ? item.children : []
            return;
          } else {
            if (!item.children || item.children.length > 0) {
              configMenus(item.children || [], name);
            }
          }
        }
      }
      configMenus(this.routesConfig, "config");
      return all;
    },
    defaultOpenKeys() {
      return [this.$route.matched[2].name];
    },
    defaultSelectedKeys() {
      return [this.$route.name];
    },

  },
  mounted: function () {
    if (!this.defaultSelectedKeys || this.defaultSelectedKeys[0] === 'config') {
      const first = this.getFirstMenu(this.menus);
      this.handleSelect(first);
    } else {
      this.selectedItem = this.defaultSelectedKeys[0];
      this.activeIndex = this.defaultOpenKeys[0];
    }
  },
  methods: {
    /**
     * 第一个目录
     */
    getFirstMenu: function (ms) {
      let firstMenu;
      if (ms instanceof Array) {
        for (const item of ms) {
          firstMenu = this.getFirstMenu(item);
          if (firstMenu) {
            return firstMenu;
          }
        }
      }
      if (!ms.children || ms.children.length == 0) {
        firstMenu = ms;
        return firstMenu;
      } else {
        for (const item of ms.children) {
          firstMenu = this.getFirstMenu(item);
          if (firstMenu) {
            return firstMenu;
          }
        }
      }
    },
    getActiveIndex(menus, selectedItem) {
      return this.$route.matched[2].name
    },
    handleSelect: function (item) {
      this.selectedItem = item.name;
      this.$router.push({
        name: item.name,
      });
    },
    onSearch: function () {

    },
    visibleMenus(items) {
      return items.filter(item => item.meta?.show)
    }
  },
};
</script>
<style lang="less" scoped>
.layout-full-height {
  height: 100%;
  display: flex;
  flex-direction: row;

  overflow-y: hidden;
}

.config-center {
  width: 100%;

  .left-nav {
    background-color: #f8f8f8;
    width: 240px;
    -webkit-user-select: none;
    -moz-user-select: none;
    user-select: none;
    overflow: hidden !important;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    margin-top: 20px;
    min-width: 240px;

    .left-block-container {
      display: flex;
      flex-direction: column;

      flex: 1 1 auto;
      min-height: 0;
      margin-bottom: 25px;
      overflow: auto;

      .left-block_search-input_wrapper {
        width: 200px;
        height: 34px;
        margin-left: 20px;
        margin-right: 20px;
        margin-bottom: 10px;
        flex-shrink: 0;
      }

      .left-block_action {
        display: flex;
        justify-content: space-between;
        height: 30px;
        align-items: center;
        margin: 0px 20px;
        flex-shrink: 0;

        .desc {
          color: #606060;
          font-size: 12px;
        }
      }

      .left-navs-tree {
        flex-grow: 1;
        overflow: auto;

        .left-nav-link {
          margin: 0;
          padding-left: 41px;
          height: 30px;
          line-height: 30px;
          display: flex;
          justify-content: space-between;
          padding-right: 20px;
          color: #303030;
          position: relative;
          display: flex;
          align-items: center;
          padding-left: 40px;
          margin-right: 0px;
          border-top-right-radius: 3px;
          border-bottom-right-radius: 3px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          height: 34px;
          line-height: 34px;
          outline: none;
          border-radius: 0px;
        }

        .left-nav-link:hover {
          cursor: pointer;
        }

        .left-nav-link-selected {
          margin: 0;
          padding-left: 41px;
          height: 30px;
          line-height: 30px;
          display: flex;
          justify-content: space-between;
          padding-right: 20px;
          position: relative;
          display: flex;
          align-items: center;
          padding-left: 35px;
          margin-right: 0px;
          border-top-right-radius: 3px;
          border-bottom-right-radius: 3px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          height: 34px;
          line-height: 34px;
          outline: none;
          border-radius: 0px;
          background-color: #eaf3fc;
          color: #338fe5;
          border-left: solid 4px #338fe5;
        }

        .left-nav-link-selected:hover {
          background: #e8e8e8;
        }
      }

      :global(.ant-collapse-header) {
        font-size: 15px;
        font-weight: 500;
        color: #303030;
        padding-top: 4px !important;
        padding-bottom: 4px !important;
        margin-top: 10px;
        padding-left: 41px;
        padding-right: 20px;
        margin-right: 0px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      :global(.left-navs-tree .ant-collapse-content > .ant-collapse-content-box) {
        padding: 0 !important;
      }
    }
  }

  .setting-main_wrapper {
    flex-grow: 1;
    box-shadow: 0 0 2px 0 rgba(31, 31, 31, 0.05),
      0 4px 6px 0 rgba(31, 31, 31, 0.2);
    background-color: #fff;
    margin: 10px 10px 10px 0;
    min-width: 666px;
    width: auto;
    border-radius: 3px;
    margin: 0;
    padding: 0;

    .setting-main {
      height: 100%;
      overflow: auto;
      flex: 1 1 auto !important;
      width: auto;
      min-width: 0;
      margin-left: -4px;
      padding-left: 4px;
      display: flex;
      flex-direction: column;
    }
  }
}

.scroll-div {
  position: relative;
}

.div1 {
  position: relative;
  width: 256px;
  z-index: 10;

  ul {
    height: 100%;
  }
}

micro-app-body {
  height: 100%;
}

.div2 {
  position: absolute;
  z-index: 9;
  left: 0x;
  top: 0px;

  overflow-y: hidden;

  padding-left: 256px;
}

.div3 {
  clear: both;
}
</style>
