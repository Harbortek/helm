<template>
  <div class="smart-doc-toolbar">
    <div class="smart-doc-toolbar-content">
      <vxe-toolbar size="mini" class="smart-doc-toolbar-left">
        <template #buttons>
          <vxe-button type="text" icon="vxe-icon-edit" @click="onEdit" :disabled="docLoading || !hasEditPerm">{{
            readOnly
              ? `编辑` : `退出编辑`
          }}</vxe-button>
          <PublishButton :pageId="page.id"></PublishButton>
          <vxe-button v-if="!hasWatched" @click="onClickWatch" type="text" icon="vxe-icon-user">未关注</vxe-button>
          <vxe-button v-if="hasWatched" @click="onClickCancelWatch" type="text" icon="vxe-icon-user">已关注{{
            '(' + watchersCount + ')' }}</vxe-button>

          <!-- <vxe-button @click="onSave" :disabled="1 == 1" type="text" icon="vxe-icon-save"
            :loading="docSaving">保存</vxe-button> -->
          <vxe-button v-if="!readOnly" type="text" icon="vxe-icon-upload">
            <template #default>导入</template>
            <template #dropdowns>
              <vxe-button @click="onImportWord">WORD</vxe-button>
              <vxe-button @click="onImportReqIF">REQIF</vxe-button>
            </template>
          </vxe-button>
          <vxe-button type="text" icon="vxe-icon-download">
            <template #default>导出</template>
            <template #dropdowns>
              <vxe-button v-print="printConf">PDF</vxe-button>
              <vxe-button @click="onExportWord">WORD</vxe-button>
              <vxe-button @click="onExportReqIF">REQIF</vxe-button>
            </template>
          </vxe-button>
          <vxe-button @click="onViewModelChange()" type="text" icon="vxe-icon-link">表格模式</vxe-button>
          <!-- <vxe-button type="text" icon="vxe-icon-time">历史记录</vxe-button> -->
          <!-- <vxe-button type="text" icon="vxe-icon-feedback">复审</vxe-button> -->
          <!-- <vxe-button @click="onTrackerItemInsert()" type="text" icon="vxe-icon-swap-right"
            :disabled="readOnly">插入</vxe-button> -->
          <vxe-button :disabled="!hasEditPerm" @click="onDocPropertiesView()" type="text"
            icon="vxe-icon-info-circle">文档属性</vxe-button>
          <search-button :root-element-id="printHolder" :read-only="readOnly"></search-button>
          <BaseLineButton :pageId="page.id"></BaseLineButton>
          <VersionButton :pageId="page.id"></VersionButton>
        </template>
        <template #tools>
          <div style="display:inherit;margin-right:10px;">
            <vxe-button :disabled="!hasEditPerm" type="text" icon="vxe-icon-setting" placement="设置"
              @click="onShowSettings"></vxe-button>
          </div>
        </template>
      </vxe-toolbar>
      <!-- <vxe-toolbar size="mini" class="smart-doc-toolbar-right">
        <template #tools>
          <vxe-button type="text" icon="vxe-icon-setting" placement="设置" @click="onShowSettings"></vxe-button>
          <vxe-button type="text" icon="vxe-icon-ellipsis-h" placement="展开详情" @click="onPropertyCollapse"></vxe-button>

        </template>
      </vxe-toolbar> -->
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import HIcon from '@/components/icon/hicon'
import VXETable from "vxe-table";
import SearchButton from "./SearchButton";
import BaseLineButton from "./BaseLineButton";
import {
  addWatch, cancelWatch
} from "@/services/tracker/ProjectPageService";
import { hasPermission } from '@/utils/permission'
import PublishButton from "./PublishButton.vue";
import VersionButton from "./VersionButton.vue";


export default ({
  name: 'MenuBar',
  components: {
    HIcon, SearchButton, BaseLineButton, PublishButton, VersionButton
  },
  props: {
    printHolder: {
      type: String,
      require: true
    },
    currentBlockId: {
      type: String,
      require: false
    },
    docLoading: {
      type: Boolean,
      require: true
    },
    docSaving: {
      type: Boolean,
      require: false
    },
    page: {
      type: Object,
      require: true
    },
    readOnly: {
      type: Boolean,
      require: false
    }
  },
  computed: {
    hasEditPerm() {
      return hasPermission('PAGE_WRITE',this.page?.id);
    },
    printConf() {
      return {
        id: this.printHolder,
        popTitle: this.printHolder,
        extraCss: "",
        extraHead: '<meta http-equiv="Content-Language"content="zh-cn"/>',
        beforeOpenCallback(vue) {
          vue.printLoading = true
          console.log('打开之前')
        },
        openCallback(vue) {
          vue.printLoading = false
          console.log('执行了打印')
        },
        closeCallback(vue) {
          console.log('关闭了打印工具')
        }
      }
    },
    hasWatched() {
      if (this.page?.watchers && Array.isArray(this.page?.watchers)) {
        const uid = this.user.id
        return this.page.watchers.find(w => w.id === uid)
      }
      return false
    },
    watchersCount() {
      if (this.page?.watchers && Array.isArray(this.page.watchers)) {
        return this.page.watchers.length
      }
      return 0
    },
    ...mapGetters("account", ["user"]),
  },
  data() {
    return {
      onlines: 0
    }
  },
  mounted() {
  },
  methods: {
    onReady() {
    },
    onExportWord() {
      this.$emit('EXPORT_WORD');
    },
    onSave() {
      this.$emit('SAVE');
    },
    onOutlinerCollapse() {
      this.$emit('OUTLINER_COLLAPSE');
    },
    onEdit() {
      this.$emit('DOC_EDIT', !this.readOnly);
    },
    onShowSettings() {
      this.$emit('SHOW_SETTINGS');
    },
    onViewModelChange(readOnly) {
      // this.readOnly = readOnly;
      this.$emit('VIEW_MODEL_CHANGE', readOnly);
    },
    // onTrackerItemInsert() {
    //   this.$emit('TRACKER_ITEM_INSERT');
    // },
    onDocPropertiesView() {
      this.$emit('DOCPROPERTIES_VIEW');
    },

    onClickWatch() {
      if (this.page?.id) {
        addWatch(this.page?.id, this.user).then(res => {
          VXETable.modal.message({ content: '关注成功', status: 'success' })
          this.page.watchers.push(this.user)
        })
      }
    },
    onClickCancelWatch() {
      if (this.page?.id) {
        cancelWatch(this.page?.id, this.user).then(res => {
          VXETable.modal.message({ content: '已取消关注', status: 'success' })
          this.page.watchers = this.page.watchers.filter(item => item.id !== this.user.id)
        })
      }
    },
    onImportWord() {
      this.$emit('IMPORT_WORD');
    },
    onImportReqIF() {
      this.$emit('IMPORT_REQIF');
    },
    onExportReqIF() {
      this.$emit('EXPORT_REQIF');
    }
  }
})
</script>
<style lang="less" scoped>
.smart-doc-toolbar {
  width: 100%;
  min-height: 36px;
  // background-color: whiteSmoke;
  border-bottom: 1px solid #DEDEDE;



  .smart-doc-toolbar-content {
    width: 100%;
    height: 100%;
    position: relative;

    .smart-doc-toolbar-left {
      // position: absolute;
      left: 5px;
      top: 3px;
      bottom: 0;
    }

    .smart-doc-toolbar-right {
      // position: absolute;
      right: 5px;
      top: 3px;
      bottom: 0;
    }

    /deep/ .vxe-toolbar {
      background-color: transparent;
    }



    /deep/ .ant-btn {
      color: #606060;
      background-color: transparent;

      // vertical-align: middle;
      // height: 20px;
      // border-spacing: 2px;
      &:hover {
        background-color: #f8f8f8;
        color: #000;
      }

    }
  }
}
</style>