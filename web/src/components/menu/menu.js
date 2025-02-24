/**
 * 该插件可根据菜单配置自动生成 ANTD menu组件
 * menuOptions示例：
 * [
 *  {
 *    name: '菜单名称',
 *    path: '菜单路由',
 *    meta: {
 *      title: ''
 *      icon: '菜单图标',
 *      show: 'boolean, 是否不可见, 默认 false',
 *    },
 *    children: [子菜单配置]
 *  },
 *  {
 *    name: '菜单名称',
 *    path: '菜单路由',
 *    meta: {
 *      title: ''
 *      icon: '菜单图标',
 *      show: 'boolean, 是否不可见, 默认 false',
 *    },
 *    children: [子菜单配置]
 *  }
 * ]
 *
 * i18n: 国际化配置。系统默认会根据 options route配置的 path 和 name 生成英文以及中文的国际化配置，如需自定义或增加其他语言，配置
 * 此项即可。如：
 * i18n: {
 *   messages: {
 *     CN: {dashboard: {name: '监控中心'}}
 *     HK: {dashboard: {name: '監控中心'}}
 *   }
 * }
 **/
import Menu from "ant-design-vue/es/menu";
import Icon from "ant-design-vue/es/icon";
import Vue from "vue";
import fastEqual from "fast-deep-equal";
import { mapState, mapMutations, mapGetters } from "vuex";

const { Item, SubMenu } = Menu;

const resolvePath = (path, params = {}) => {
  let _path = path;
  Object.entries(params).forEach(([key, value]) => {
    _path = _path.replace(new RegExp(`:${key}`, "g"), value);
  });
  return _path;
};

const toRoutesMap = (routes) => {
  const map = {};
  routes.forEach((route) => {
    map[route.path] = route;
    if (route.children && route.children.length > 0) {
      const childrenMap = toRoutesMap(route.children);
      Object.assign(map, childrenMap);
    }
  });
  return map;
};

export default {
  name: "IMenu",
  props: {
    options: {
      type: Array,
      required: true,
    },
    theme: {
      type: String,
      required: false,
      default: "dark",
    },
    mode: {
      type: String,
      required: false,
      default: "inline",
    },
    collapsed: {
      type: Boolean,
      required: false,
      default: false,
    },
    i18n: Object,
    openKeys: Array,
  },
  data() {
    return {
      selectedKeys: [],
      sOpenKeys: [],
      cachedOpenKeys: [],
    };
  },
  computed: {
    menuTheme() {
      return this.theme == "light" ? this.theme : "dark";
    },
    routesMap() {
      return toRoutesMap(this.options);
    },
  },
  created() {
    this.updateMenu();
    // if (this.options.length > 0 && !this.options[0].fullPath) {
    //   this.formatOptions(this.options, "");
    // }
    // 自定义国际化配置
    if (this.i18n && this.i18n.messages) {
      const messages = this.i18n.messages;
      Object.keys(messages).forEach((key) => {
        this.$i18n.mergeLocaleMessage(key, messages[key]);
      });
    }
  },
  watch: {
    options(val) {
      this.updateMenu();
    },
    i18n(val) {
      if (val && val.messages) {
        const messages = this.i18n.messages;
        Object.keys(messages).forEach((key) => {
          this.$i18n.mergeLocaleMessage(key, messages[key]);
        });
      }
    },
    collapsed(val) {
      if (val) {
        this.cachedOpenKeys = this.sOpenKeys;
        this.sOpenKeys = [];
      } else {
        this.sOpenKeys = this.cachedOpenKeys;
      }
    },
    $route: function () {
      this.updateMenu();
    },
    sOpenKeys(val) {
      this.$emit("openChange", val);
      this.$emit("update:openKeys", val);
    },
  },
  methods: {
    ...mapMutations("setting", ["setTopTitle"]),
    renderIcon: function (h, icon, key) {
      if (this.$scopedSlots.icon && icon && icon !== "none") {
        const vnodes = this.$scopedSlots.icon({ icon, key });
        vnodes.forEach((vnode) => {
          vnode.data.class = vnode.data.class ? vnode.data.class : [];
          vnode.data.class.push("anticon");
        });
        return vnodes;
      }

      if (icon && icon !== "none") {
        const comp = Vue.options.components[icon];
        // console.log("render " + icon, comp);
        if (comp) {
          return h(
            "i",
            {
              attrs: {
                style: "width:14px;height:14px;margin-right:10px;",
              },
            },
            [h(comp, { props: { type: icon } })]
          );
        } else {
          return h(Icon, { props: { type: icon } });
        }
      }
    },
    renderMenuItem: function (h, menu) {
      let tag = "router-link";
      const path = resolvePath(menu.path, menu.params);
      let config = {
        props: { to: { path, query: menu.query } },
        attrs: {
          style: "overflow:hidden;white-space:normal;text-overflow:clip;",
          title: menu.name,
        },
      };
      if (menu.meta && menu.meta.link) {
        tag = "a";
        config = {
          attrs: {
            style: "overflow:hidden;white-space:normal;text-overflow:clip;",
            href: menu.meta.link,
            target: "_blank",
            title: menu.name,
          },
        };
      }
      return h(Item, { key: menu.id }, [
        h(tag, config, [
          this.renderIcon(h, menu.icon ? menu.icon : "none", menu.id),
          this.$t(menu.name),
        ]),
      ]);
    },
    renderSubMenu: function (h, menu) {
      let this_ = this;
      let subItem = [
        h(
          "span",
          {
            slot: "title",
            attrs: {
              style: "overflow:hidden;white-space:normal;text-overflow:clip;",
              title: menu.name,
            },
          },
          [
            this.renderIcon(h, menu.icon ? menu.icon : "none", menu.id),
            this.$t(menu.name),
          ]
        ),
      ];
      let itemArr = [];
      menu.children.forEach(function (item) {
        itemArr.push(this_.renderItem(h, item));
      });
      return h(SubMenu, { key: menu.id }, subItem.concat(itemArr));
    },
    renderItem: function (h, menu) {
      let renderChildren = false;
      const children = menu.children;
      if (children != undefined) {
        renderChildren = children.length > 0;
      }
      return menu.children && renderChildren
        ? this.renderSubMenu(h, menu)
        : this.renderMenuItem(h, menu);
    },
    renderMenu: function (h, menuTree) {
      let this_ = this;
      let menuArr = [];
      menuTree.forEach(function (menu, i) {
        // if (menu.meta.show) {
        menuArr.push(this_.renderItem(h, menu, "0", i));
        // }
      });
      return menuArr;
    },
    formatOptions(options, parentPath) {
      // options.forEach(route => {
      //   let isFullPath = route.path.substring(0, 1) == '/'
      //   route.fullPath = isFullPath ? route.path : parentPath + '/' + route.path
      //   if (route.children) {
      //     this.formatOptions(route.children, route.fullPath)
      //   }
      // })
    },
    updateMenu() {
      this.selectedKeys = this.getSelectedKeys();
      // console.log("selectedKeys", this.selectedKeys);
      let openKeys = this.selectedKeys.filter((item) => item !== "");
      openKeys = openKeys.slice(0, openKeys.length - 1);
      // console.log("openKeys", openKeys);
      if (!fastEqual(openKeys, this.sOpenKeys)) {
        this.collapsed || this.mode === "horizontal"
          ? (this.cachedOpenKeys = openKeys)
          : (this.sOpenKeys = openKeys);
      }
    },
    getSelectedKeys() {
      let currentPath = document.location.hash;
      if (currentPath.startsWith("#/")) {
        currentPath = currentPath.substring(1);
      }
      for (let i = 0; i < this.options.length; i++) {
        if (this.options[i].children) {
          for (let j = 0; j < this.options[i].children.length; j++) {
            if (currentPath.startsWith(this.options[i].children[j].path)) {
              this.setTopTitle(this.options[i].children[j].name);
              return [this.options[i].id, this.options[i].children[j].id];
            }
          }
        } else if (currentPath.startsWith(this.options[i].path)) {
          this.setTopTitle(this.options[i].name);
          return [this.options[i].id];
        }
      }
      return [];
    },
    getOpenKeys(selectId) {
      for (let i = 0; i < this.options.length; i++) {
        if (this.options[i].children) {
          for (let j = 0; j < this.options[i].children.length; j++) {
            if (this.options[i].children[j].id === selectId) {
              return [this.options[i].id, this.options[i].children[j].id];
            }
          }
        } else if (this.options[i].id === selectId) {
          return [this.options[i].id];
        }
      }
      return [];
    },
  },
  render(h) {
    return h(
      Menu,
      {
        props: {
          theme: this.menuTheme,
          mode: this.$props.mode,
          selectedKeys: this.selectedKeys,
          openKeys: this.openKeys ? this.openKeys : this.sOpenKeys,
        },
        on: {
          "update:openKeys": (val) => {
            this.sOpenKeys = val;
          },
          click: (obj) => {
            obj.selectedKeys = [obj.key];
            this.$emit("select", obj);
          },
        },
      },
      this.renderMenu(h, this.options)
    );
  },
};
