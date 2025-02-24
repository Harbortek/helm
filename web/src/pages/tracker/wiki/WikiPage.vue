<template>
  <div class="wiki-page">
    <teleport to="#topMenuComponenet">
      <a-row type="flex" class="top-menu" style="margin-left: 20px;">
        <a-col flex="180px" style="overflow: hidden;white-space: nowrap;  text-overflow: ellipsis;">
          <a-dropdown :trigger="['click']">
            <a class="ant-dropdown-link" @click="e => e.preventDefault()" style="color:inherit">
              文档：{{ currentPage?.name }} <a-icon type="down" />
            </a>
            <a-menu slot="overlay">
              <a-menu-item :key="page.id" v-if="currentPage.id !== page.id" v-for="page in pages"
                @click="changePage(page)" class="top-menu-item">
                {{ page.name }}
              </a-menu-item>
              <a-menu-divider v-if="pages.length > 1" />
              <a-menu-item @click="addPage()" style="color:#5caff2">
                <a-icon type="plus-circle"></a-icon>新增子文档
              </a-menu-item>
            </a-menu>
          </a-dropdown>

        </a-col>
      </a-row>
    </teleport>
    <smart-doc v-if="!isShowTableView" :pageId="pageId" :projectId="projectId"
      @viewModelChange="viewModelChange"></smart-doc>
    <div style="padding:10px;height:100%" v-else>
      <vxe-button style="height:3%;" @click="viewModelChange" icon="vxe-icon-arrow-left" type="text"
        content="返回文档模式"></vxe-button>
      <!-- <tracker-items-table :projectId="projectId" :pageId="pageId"/> -->
      <tracker-items-main-page style="height:97%;" :projectId="projectId" :pageId="pageId"></tracker-items-main-page>
    </div>
    <project-page-dialog :isShowDialog="showDialog" :projectId="projectId" :editMode="true" :current-page="{
            type: 'wiki',
            tracker: {
              id: pages.length > 0 ? pages[0]?.trackerId : null
            },
            trackerIds: pages.length > 0 ? JSON.stringify(pages[0].pageSettingTrackers?.map((item) => item.id)) : JSON.stringify([])
          }" :pageList="tableData" @cancel="showDialog = false" @ok="onEditOK" />
  </div>
</template>

<script>
import { Card } from "ant-design-vue";
import SmartDoc from "@/components/smart-doc/SmartDoc.vue";
import { mapState, mapMutations, mapGetters } from "vuex";
import TrackerItemsTable from '@/components/table/TrackerItemsTable.vue';
import TrackerItemsMainPage from '../items/TrackerItemsMainPage.vue';
import Teleport from 'vue2-teleport';
import ProjectPageDialog from '@/pages/tracker/config/ProjectPageDialog.vue';
import { findOneAndChildrenByPageId, createProjectPage } from "@/components/smart-doc/SmartDocService";
export default {

  name: "WikiPage",
  components: {
    Card, SmartDoc, TrackerItemsTable,
    TrackerItemsMainPage, Teleport, ProjectPageDialog
  },
  computed: {
    ...mapState("setting", [, "topTitle"]),
    // pageId() {
    //   return this.$route.params.wikiId;
    // },

    projectId() {
      return this.$route.params.projectId;
    }
  },
  beforeDestroy() {
    document.getElementById("topMenuComponenet").innerHTML = '';
  },
  watch: {
    '$route': { // $route可以用引号，也可以不用引号  监听的对象
      handler(to, from) {
        this.pageId = to.params.pageId;
        this.loadPages();
      },
      deep: true, // 深度观察监听 设置为 true
      immediate: true, // 第一次初始化渲染就可以监听到
    },
  },
  data() {
    return {
      isShowTableView: false,
      pages: [],
      currentPage: null,
      showDialog: false,
      tableData: [],
    }
  },
  mounted() {
  },
  created() {
  },
  methods: {
    ...mapMutations('setting', ["setTopMenuData"]),
    viewModelChange() {
      this.isShowTableView = !this.isShowTableView;
    },
    loadPages() {
      findOneAndChildrenByPageId(this.pageId).then((res) => {
        this.currentPage = res[0];
        this.pages = res;
      });
    },
    changePage(page) {
      this.currentPage = page;
      this.pageId = page.id;
    },
    addPage() {
      this.showDialog = true;
    },
    onEditOK(row) {
      row.projectId = this.projectId;
      row.parentId = this.pages[0].id;
      createProjectPage(row).then(resp => {
        this.currentPage = resp;
        this.showDialog = false;
        this.loadPages();
      })
    }
  },
};
</script>

<style lang="less" scoped>
.wiki-page {
  width: 100%;
  height: 100%;
  background-color: #fff;
  overflow: none;

  .top-menu {
    color: #606060;
    margin: left 20px !important;
    ;

    &:hover {
      color: #338fe5;
      font-weight: 500;
      // border-top: 3px solid #338fe5;
    }

  }
}
</style>