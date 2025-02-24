<template>
  <a-layout-sider :theme="sideTheme" :class="['side-menu', 'beauty-scroll', 'shadow']" :collapsible="collapsible"
    v-model="collapsed" :trigger="null" :width="sideMenuWidth" collapsedWidth="60px">
    <div :class="['logo', 'light']" :style="`width:${sideMenuWidth}`">
      <a href="/">
        <img :src="systemLogo">
      </a>
      <h1 v-if="!collapsed" style="max-height: 40px;text-overflow: ellipsis;white-space: nowrap;overflow: hidden;"
        :title="systemName">
        {{ systemName }}</h1>
      <a-icon v-if="!collapsed" class="trigger collapse"
        style="font-size:14px;line-height:14px;padding: 0px 5px 0 0;color:#acb0b7"
        :type="collapsed ? 'menu-unfold' : 'menu-fold'" @click="toggleCollapse" />
    </div>
    <div class="main-menu-container" :style="{'height': 'calc(100% - 60px - '+ 50 * (menuConfig.length)+'px)'}">
      <ul class="project-top-menu menu ant-menu ant-menu-inline ant-menu-root ant-menu-dark">
        <a-dropdown :trigger="['click']" :visible="dropdownVisible" @visibleChange="onClickMenu">
          <li class="ant-menu-submenu ant-menu-submenu-inline">
            <div class="ant-menu-submenu-title" style="padding-left: 24px;">
              <span style="overflow:hidden;white-space:normal;text-overflow:clip;">
                <i class="anticon">
                  <a-avatar v-if="currentProject?.icon" shape="square" size="small"
                    :src="`data:image/png;base64,${currentProject.icon}`"
                    style="backgroundColor:transparent;width:18px;height:18px;"></a-avatar>
                </i>
                项目：{{ currentProject?.name }}</span>
              <i class="ant-menu-submenu-arrow"></i>
            </div>
          </li>
          <a-menu slot="overlay" class="project-list-dropdown" :selectable="true" :selectedKeys="[recentProjects.length > 0 ? recentProjects[0].project?.id : '0']"
            style="padding-left: 10px;padding-right: 10px;padding-top: 10px; " @select="onProjectClick">
            <a-input-search placeholder="项目名称" style="width: 200px" @search="onSearch" v-model="projectSearch" />

            <a-menu-item-group title="最近访问的项目">
              <a-menu-item v-for="p in  getRecentProjects" :key="p.project?.id">
                <a-avatar  shape="square" size="small" :src="`data:image/png;base64,${p.project.icon}`"
                  style="backgroundColor:transparent;width:18px;height:18px;"> {{ p.project.name
                  }}</a-avatar>{{ p.project.name }} </a-menu-item>
            </a-menu-item-group>

            <a-menu-item key="0"><a-icon type="unordered-list" />查看所有项目({{ totalCount
              }})</a-menu-item>
          </a-menu>
        </a-dropdown>
      </ul>
      <i-menu ref="iMenuRef" :openKeys="openKeys" @openChange="openChangeMenu" :theme="theme" :collapsed="collapsed"
        :options="menuDataWithProject" @select="onSelect" class="menu" />
      <!-- <div></div>
      <i-menu :theme="theme" :collapsed="collapsed" :options="menuDataWithProject" @select="onSelect" class="menu" /> -->
    </div>
    <div class="config">
      <i-menu v-if="configMenuData" :theme="theme" :collapsed="collapsed" :options="menuConfig" @select="onSelect"
        class="menu-config" />
      <div :class="!collapsed ? 'version' : 'version-2'">{{ !collapsed ? `当前版本: v${version()}` : 'v' + version() }}
      </div>
    </div>
  </a-layout-sider>
</template>

<script>
import IMenu from './menu'
import { mapState, mapMutations, mapGetters } from 'vuex'
import { cloneDeep } from 'lodash'
import PAGE_COMPONENTS from '@/components/menu/menu-with-project'
import { hasPermission,getTrackerPermissionIds } from "@/utils/permission";
import {
  findProjects,
  findRecentProjects,
  findOneProject,
  recordRecentProject,
} from "@/services/tracker/ProjectService";
import { project } from '@antv/l7';


export default {
  name: 'SideMenu',
  components: { IMenu },
  props: {
    collapsible: {
      type: Boolean,
      required: false,
      default: false
    },
    collapsed: {
      type: Boolean,
      required: false,
      default: false
    },
    menuData: {
      type: Array,
      required: true
    },
    theme: {
      type: String,
      required: false,
      default: 'dark'
    },
    sideMenuWidth: {
      type: String,
      required: true,
      default: '256px'
    }
  },
  data() {
    return {
      menuNoProject: [],
      menuWithProject: [],
      menuConfig: [],
      projectMenus: [],
      projectConfigMenus: [],
      trackerMenus: [],
      currentProject: {},
      keyword: '',
      projectSearch:'',
      recentProjects: [],
      onGoingProjects: [],
      totalCount: 0,
      openKeys: [],
      dropdownVisible:false,
    }
  },
  watch: {
    '$route'(to, from) {
      if (this.currentProjectId != this.currentProject?.id) {
        this.currentProject = undefined
        this.loadData()
      }
      this.setBreadcrumb([])
    }
  },
  computed: {
    sideTheme() {
      return 'dark'
    },
    getRecentProjects() {
      return this.recentProjects.filter(item => item.project.name.includes(this.projectSearch))
    },
    ...mapState('setting', ['isMobile', 'systemName', 'systemLogo', 'configMenuData']),
    ...mapGetters("project", ["currentProjectId"]),

    menuDataWithProject: {
      get: function () {
        if (this.currentProjectId > 0) {
          let menus = []
          let index = 99
          const projectId = this.currentProjectId
          menus = menus.concat(this.projectMenus)
          if (hasPermission("PROJECT_VIEW",projectId)&&this.trackerMenus) {
            menus.push(
              this.trackerMenus
            )
          }
          if (hasPermission("PROJECT_VIEW",this.currentProjectId)
              &&hasPermission("PROJECT_ADMIN",projectId)) {
            menus.push({
              id: index++, name: '设置', title: '设置', path: `/tracker/project/${projectId}/config`, icon: 'setting', children: [
                { id: index++, name: '页面设置', path: `/tracker/project/${projectId}/config/projectPageConfig` },
                { id: index++, name: '工作项设置', path: `/tracker/project/${projectId}/config/trackerConfig` },
                { id: index++, name: '权限设置', path: `/tracker/project/${projectId}/config/projectPermmissionConfig` },
                { id: index++, name: '项目成员', path: `/tracker/project/${projectId}/config/projectRoleConfig` },
                { id: index++, name: '枚举值设置', path: `/tracker/project/${projectId}/config/projectEnumConfig` },
                { id: index++, name: '关联类型设置', path: `/tracker/project/${projectId}/config/linkTypeConfig` },
                { id: index++, name: '代码仓设置', path: `/tracker/project/${projectId}/config/projectCodeConfig` },
                { id: index++, name: '项目操作', path: `/tracker/project/${projectId}/config/projectOperationConfig` }
              ]
            })
          }

          this.setSideMenu(menus.filter(v =>v!=this.trackerMenus))
          return menus
        } else {
          this.setSideMenu(this.menuNoProject)
          return this.menuNoProject;
        }
      }
    },
  },
  mounted() {
    this.menuNoProject = require("./menu-no-project.json")
    this.menuConfig=require('./menu-config.json')?.filter(v=>{
      if(v.permission){
        return hasPermission(v.permission)
      }
      return true
    })
    this.loadData()
  },
  methods: {
    ...mapMutations('setting', ['setSystemName', 'setSystemLogo', 'setBreadcrumb']),
    ...mapMutations('project', ["setCurrentProjectId", "setCurrentProjectKeyName"]),
    ...mapMutations('account', ["setSideMenu"]),


    loadData() {
      this.projectMenus = [];
      if (this.currentProjectId) {
        findOneProject(this.currentProjectId).then(resp => {
          if(!hasPermission("PROJECT_VIEW",this.currentProjectId)){
            return;
          }
          this.setCurrentProjectKeyName(resp.keyName)
          this.currentProject = resp
          const projectId = this.currentProjectId
          const trackers = resp.trackers
          //构建项目主体菜单
          let pages = resp.pages;
          let index = 1;
          // this.projectMenus.push({
          //   id: index++,
          //   name: "工作区",
          //   icon: "project",
          //   path: "/tracker/projectList"
          // })
          // this.projectMenus.push({ id: index++, name: '总览', path: `/tracker/project/${projectId}/summary`, icon: 'home' })
          for (let j = 0; j < pages?.length; j++) {
            const page = pages[j]
            if (page.folder) {
              let menuItem = { id: index++, name: page.name, path: page.name, icon: page.icon, children: [] }
              for (let k = 0; k < page.children.length; k++) {
                if (!hasPermission("PAGE_READ",page.children[k].id)) {
                  continue;
                }
                let childMenuItem = {}
                const childPage = page.children[k]
                const pageId = childPage.id
                if (childPage.type === 'wiki') {
                  childMenuItem = { id: index++, name: childPage.name, path: `/tracker/project/${projectId}/wiki/${pageId}` }
                } else if (childPage.type === 'report') {
                  childMenuItem = { id: index++, name: childPage.name, path: `/tracker/project/${projectId}/smartPage/${pageId}` }
                } else if (childPage.type === 'component') {
                  childMenuItem = { id: index++, name: childPage.name, path: this.getComponentPagePath(childPage, projectId), meta: { pageId: childPage.id } }
                } else if (childPage.type === 'tracker') {
                  childMenuItem = { id: index++, name: childPage.name, path: this.getTrackerPagePath(childPage, projectId) }
                } else if (childPage.type === 'url') {
                  childMenuItem = { id: index++, name: childPage.name, path: childPage.url }
                }
                menuItem.children.push(childMenuItem)
              }
              if (menuItem.children.length > 0) {
                this.projectMenus.push(menuItem)
              }
            } else {
              let menuItem = {}
              if (!hasPermission("PAGE_READ",page.id)) {
                continue;
              }
              const pageId = page.id
              if (page.type === 'wiki') {
                menuItem = { id: index++, name: page.name, path: `/tracker/project/${projectId}/wiki${pageId}`, icon: page.icon ? page.icon : 'form' }
              } else if (page.type === 'report') {
                menuItem = { id: index++, name: page.name, path: `/tracker/project/${projectId}/smartPage/${pageId}`, icon: page.icon ? page.icon : 'bar-chart' }
              } else if (page.type === 'component') {
                menuItem = { id: index++, name: page.name, path: this.getComponentPagePath(page, projectId), icon: page.icon || 'form', meta: { pageId: page.id } }
              } else if (page.type === 'tracker') {
                menuItem = { id: index++, name: page.name, path: this.getTrackerPagePath(page, projectId) }
              } else if (page.type === 'url') {
                menuItem = { id: index++, name: page.name, path: page.url }
              }
              this.projectMenus.push(menuItem)
            }
          }

          if (hasPermission("PROJECT_ADMIN", projectId)) {
            this.projectConfigMenus.push({
              id: index++, name: '设置', title: '设置', path: `/tracker/project/${projectId}/config`, icon: 'setting', children: [
                { id: index++, name: '页面设置', path: `/tracker/project/${projectId}/config/projectPageConfig` },
                { id: index++, name: '工作项设置', path: `/tracker/project/${projectId}/config/trackerConfig` },
                { id: index++, name: '权限设置', path: `/tracker/project/${projectId}/config/projectPermmissionConfig` },
                { id: index++, name: '项目成员', path: `/tracker/project/${projectId}/config/projectRoleConfig` },
                { id: index++, name: '枚举值设置', path: `/tracker/project/${projectId}/config/projectEnumConfig` },
                { id: index++, name: '关联类型设置', path: `/tracker/project/${projectId}/config/linkTypeConfig` },
                { id: index++, name: '代码仓设置', path: `/tracker/project/${projectId}/config/projectCodeConfig` },
                { id: index++, name: '项目操作', path: `/tracker/project/${projectId}/config/projectOperationConfig` }
              ]
            })
          }

          //构建工作项菜单
          this.trackerMenus = { id: index++, name: '工作项', path: '工作项', icon: 'interaction', children: [] }
          let hasItemViewPerms=getTrackerPermissionIds("ITEM_VIEW")
          for (let i = 0; i < trackers.length; i++) {
            const tracker = trackers[i]
            const trackerId = tracker.id
            if(hasItemViewPerms.includes(trackerId)){
              this.trackerMenus.children.push({ id: index++, name: tracker.name, path: `/tracker/project/${projectId}/trackerItems/${trackerId}`, icon: tracker.icon })
            }
          }
          if(this.trackerMenus.children.length==0){
            this.trackerMenus=null
          }

          // this.projectMenus.push({
          //   id: index++, name: '计划', icon: 'schedule', children: [
          //     { id: index++, name: '项目计划', path: `/tracker/project/${projectId}/plan`, icon: '' },
          //     { id: index++, name: '迭代', path: `/tracker/project/${projectId}/sprints`, icon: '' },
          //     { id: index++, name: '里程碑', path: `/tracker/project/${projectId}/milestones`, icon: '' },
          //     { id: index++, name: '交付物', path: `/tracker/project/${projectId}/deliverables`, icon: '' },
          //     { id: index++, name: '计划执行', path: `/tracker/project/${projectId}/tasks`, icon: '' },
          //   ]
          // })

          // this.projectMenus.push({
          //   id: index++, name: '测试', icon: 'test', children: [
          //     { id: index++, name: '测试运行', path: `/tracker/project/${projectId}/testplan`, icon: '' },
          //     { id: index++, name: '测试用例', path: `/tracker/project/${projectId}/testCase`, icon: '' },
          //   ]
          // })

          // this.projectMenus.push({
          //   id: index++, name: '代码仓库', path: `/tracker/project/${projectId}/repository`, icon: 'code'
          // })
          // this.projectMenus.push({
          //   id: index++, name: '流水线', path: `/tracker/project/${projectId}/pipeline`, icon: 'pipeline'
          // })


          // this.projectMenus.push({
          //   id: index++, name: '基线', path: `/tracker/project/${projectId}/baseline`, icon: 'history'
          // })


          // console.log(this.trackerMenus)
        })
      }
      findRecentProjects(this.keyword).then(resp => {
        this.recentProjects = resp.recentProjects;
        this.totalCount = resp.totalCount;
      })
    },
    getComponentPagePath(page, projectId) {
      const comp_page = PAGE_COMPONENTS.filter(item => item.id === page.componentType)[0]
      if (comp_page) {
        return comp_page.path(page, projectId)
      }
    },
    getTrackerPagePath(page, projectId) {
      const trackerId = page.tracker?.id
      return `/tracker/project/${projectId}/trackerItems/${trackerId}`
    },
    version() {
      return process.env.VUE_APP_VERSION;
    },
    onSelect(obj) {
      this.$emit('menuSelect', obj)
    },
    toggleCollapse() {
      this.$emit('toggleCollapse')
    },
    onSearch() {

    },
    onShowAllProjects() {
      this.setCurrentProjectId(null);
      this.$router.push({
        name: 'projectList'
      })
    },
    onClickMenu(){
      this.dropdownVisible=!this.dropdownVisible;
    },
    onProjectClick({ key }) {
      this.projectSearch='';
      if (key === '0') {
        this.dropdownVisible=false;
        this.setCurrentProjectId(null);
        this.$router.push({
          name: 'projectList'
        })
      } else if(key) {
        this.dropdownVisible=false;
        this.setCurrentProjectId(key);
        //记录最近访问项目
        recordRecentProject(key).then(() => { })
        this.$store.dispatch('account/getInfo').then(() => {
          this.$router.push({
            name: 'projectSummary',
            params: {
              projectId: key
            }
          })
        })
       
      }
    },
    openChangeMenu(val) {//只展示当前父级菜单
      if (val.length > 1) {
        const latestOpenKey = val.find(key => this.openKeys.indexOf(key) === -1);
        this.openKeys = latestOpenKey ? [latestOpenKey] : [];
      } else {
        this.openKeys = val
      }

    }
  }
}
</script>

<style lang="less">
@import "index";

.side-menu {
  .main-menu-container {
    overflow: auto;
    height: calc(100% - 260px);

    &::-webkit-scrollbar {
      width: 0px;
    }
  }

  .project-top-menu {

    .ant-menu-submenu-title {
      color: #acb0b7
    }

    .ant-menu-submenu-title:hover {
      color: #ffff
    }
  }

  .ant-menu-inline-collapsed {
    width: 60px;
  }

  .ant-menu-inline-collapsed>.ant-menu-item,
  .ant-menu-inline-collapsed>.ant-menu-item-group>.ant-menu-item-group-list>.ant-menu-item,
  .ant-menu-inline-collapsed>.ant-menu-item-group>.ant-menu-item-group-list>.ant-menu-submenu>.ant-menu-submenu-title,
  .ant-menu-inline-collapsed>.ant-menu-submenu>.ant-menu-submenu-title {
    left: 0;
    padding: 0 22px !important;
    text-overflow: clip;
  }
}
</style>
<style scoped lang="less">
.project-list-dropdown /deep/ .ant-dropdown-menu-item-group-list{
  max-height: 400px;
  overflow: auto;
}
</style>
