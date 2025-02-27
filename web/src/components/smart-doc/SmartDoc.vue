<template>
  <div class="smart-doc">
    <div :style="`overflow: hidden;height: 100%;width: ${editorWidth};float: left;`">
      <div class="editor-holder-wrapper">
        <menu-bar @EXPORT_WORD="onExportWord" :doc-loading="docLoading" :doc-saving="docSaving"
          :read-only="readOnlyMode" @VIEW_MODEL_CHANGE="onViewModelChange" :print-holder="holder" :page="page"
          :converters="converters" @SHOW_SETTINGS="onShowSettings" @TRACKER_ITEM_INSERT="onTrackerItemInsert"
          @DOCPROPERTIES_VIEW="onDocPropertiesView" @DOC_EDIT="onEditModeChange" v-if="displayMode != 'preview'"
          @IMPORT_WORD="onImportWord" @IMPORT_REQIF="onImportReqIF" @EXPORT_REQIF="onExportReqIF"></menu-bar>

        <h-doc ref="docRef" class="editor-holder" :loading="docLoading" :init-value="doc" :isReadonly="readOnlyMode"
          :holder="holder" @change="onChange" @tracker-item-focus="onBlockFocused" :extConfig="extConfig">
          <template slot="property">
            <h-icon type="document" style="cursor: pointer" @click="() => {
              this.propertyCollapsed = !this.propertyCollapsed;
            }
              " title="属性面板"></h-icon>
          </template>
        </h-doc>
        <CommentBar :page-id="pageId" class="editor-comment-bar" v-if="displayMode != 'preview'"></CommentBar>
      </div>
    </div>
    <div style="height: 100%; width: 400px; float: left" v-if="!propertyCollapsed && displayMode != 'preview'">
      <PropertyView class="wi-property-holder" :work-item-id="currentTrackerItemId" :tracker-item="currentTrackerItem"
        :read-only="readOnlyMode" :current-property-view="currentPropertyView" :doc="doc"
        @changeTrackerItem="onChangeTrackerItem" @refresh="loadTrackerItemData()">
      </PropertyView>
    </div>
    <div style="clear: both"></div>
    <SmartDocSettingsDialog :isShowDialog="isShowSettings" v-if="isShowSettings" :projectId="projectId"
      @CANCEL="onShowSettings" @SAVE_SETTINGS="onSaveSettings" :tracker-id="page.tracker?.id"
      :pageSettingTrackers="page.pageSettingTrackers"></SmartDocSettingsDialog>
    <create-tracker-item-dialog :is-show-dialog="isShowCreateTrackerItemDialog" :projectId="projectId"
      :tracker="showCreateTrackerItem" @ok="onTrackerItemSaved" @cancel="isShowCreateTrackerItemDialog = false" />

    <import-word-dialog :is-show-dialog="showImportWordDialog" :projectId="projectId" :page-id="pageId" @cancel="showImportWordDialog=false" @ok="onImportWordOK"/>
    <import-reqIF-dialog :is-show-dialog="showImportReqIFDialog" :projectId="projectId" :page-id="pageId" @cancel="showImportReqIFDialog=false"/>
    <export-reqIF-dialog :is-show-dialog="showExportReqIFDialog" :projectId="projectId" :page-id="pageId" @cancel="showExportReqIFDialog=false"/>

  </div>
</template>

<script>
import _ from "lodash";
import { nanoid } from "nanoid";
import MenuBar from "./MenuBar.vue";
import CommentBar from "./CommentBar.vue";
import PropertyView from "./PropertyView.vue";
import SmartDocSettingsDialog from "./Settings.vue";
import TrackerItemSelectModal from "@/components/dialog/TrackerItemSelectModal";
import MxGraphEditor from "@/components/mxgraph";
import UserSelectModal from "@/components/dialog/UserSelectModal";
import RemoteApi from "./RemoteApi";
import trackerItemApi from "./trackerItemApi";
import bus from "@/utils/bus";

import CreateTrackerItemDialog from '@/pages/tracker/items/CreateTrackerItemDialog.vue';
import ImportWordDialog from "@/pages/smart-doc/word/ImportWordDialog.vue";
import ImportReqIFDialog from "@/pages/smart-doc/reqif/ImportReqIFDialog.vue";
import ExportReqIFDialog from "@/pages/smart-doc/reqif/ExportReqIFDialog.vue";
import {
  findWordImportJob,
  createWordImportJob,
} from "@/services/smart-doc/WordImportService";
import {
  findReqIFImportJob,
  createReqIFImportJob,
} from "@/services/smart-doc/ReqIFImportService";
import HDoc from "./wangeditor";
import { VXETable } from "vxe-table";
import {
  updateOneProjectPage,
  exportDoc2Word,
  createTrackerItem,
  findOneTrackerItem,
  findTrackerByIds,
  findTrackers,
  findByPageId,
  saveDoc,
  findOneProjectPage4Block,
  createByTrackerItemIds,
} from "./SmartDocService";
import { getTrackerPermissionIds } from "@/utils/permission";
import { version } from "store";
import { id } from "date-fns/locale";
const remoteApi = new RemoteApi();

export default {
  name: "SmartDoc",
  components: {
    HDoc,
    MenuBar,
    CommentBar,
    PropertyView,
    SmartDocSettingsDialog,
    TrackerItemSelectModal,
    MxGraphEditor,
    UserSelectModal,
    ImportWordDialog,
    ImportReqIFDialog,
    CreateTrackerItemDialog,
    ExportReqIFDialog,
  },
  props: {
    pageId: {
      type: String,
      require: true,
    },
    displayMode: {
      type: String,
      require: false,
    },
    autoNumber: {
      type: Boolean,
      default: true,
      require: false,
    },
    previewDoc: {
      type: Object,
      default: null,
      require: false,
    },
    projectId: {
      type: String,
      require: false,
    },
    holder: {
      type: String,
      default: () => "a" + nanoid(),
      require: true,
    },
    initialized: {
      type: Function,
      default: () => { },
    },
  },
  computed: {
    editorWidth() {
      return this.propertyCollapsed ? "100%" : "calc(100% - 400px)";
    },
  },
  watch: {
    previewDoc: {
      handler: function (value) {
        if (value) {
          this.readOnlyMode = false;
          this.initEditorJS();
        }
      },
      immediate: false,
    },
    pageId: {
      handler: function (value) {
        if (!this.previewDoc?.blocks) {
          this.initEditorJS();
        }
      },
      immediate: false,
    },

    currentTrackerItemId: {
      handler: function (value) {
        this.currentPropertyView = "trackerItem";
        this.loadTrackerItemData();
      },
      immediate: false,
    },
  },
  data() {
    return {
      isShowCreateTrackerItemDialog: false,
      showCreateTrackerItem: {},
      page: {},
      settingsTrackerIds: [],
      propertyCollapsed: false,
      isShowSettings: false,
      currentTrackerItemId: null,
      currentTrackerItem: {},
      currentPropertyView: "trackerItem",
      docLoading: true,
      docSaving: false,
      editor: null,
      extConfig: {
        EXTEND_CONF: {
          trackers: [],
          pageConfig: []
        }
      },
      readOnlyMode: false,
      doc: {
        blocks: [],
        elements: [],
        version: 0,
        lastModifiedDate: "",
      },
      showImportWordDialog: false,
      showImportReqIFDialog: false,
      showExportReqIFDialog: false,
      converters: [],
    };
  },
  mounted() {
    if (this.displayMode != "preview") {
      this.initEditorJS();
    }
  },
  beforeDestroy() {
    trackerItemApi.remove(this.doc?.id);
  },
  methods: {
    onBlockFocused(ref, trackerItem) {
      this.currentTrackerItemId = ref;
      // this.currentTrackerItem = trackerItem;
    },
    onChangeTrackerItem(item) {
      const docId = this.doc.id;
      const EVE_NAME = `post-update-tracker-item-by-${docId || ''}`;
      bus.$emit(EVE_NAME, item);
      console.log("currentTrackerItemId", item);
    },
    loadTrackerItemData() {

      if (this.currentTrackerItemId) {
        findOneTrackerItem(this.currentTrackerItemId).then((item) => {
          this.currentTrackerItem = item;
        });
      } else {
        this.currentTrackerItem = {};
      }
    },
    onChange(editor) {
      if (this.readOnlyMode) return;
      // const blocksStr = JSON.stringify(blocks);
      if (this.displayMode != "preview") {
        // remoteApi
        //   .save({
        //     projectId: this.projectId,
        //     pageId: this.pageId,
        //     doc: blocksStr,
        //   })
        //   .then((doc) => {
        //     this.doc.blocks = doc.blocks;
        //     this.doc.version = doc.version;
        //     this.doc.lastModifiedDate = doc.lastModifiedDate;
        //     // this.triggerCurrentBlock();
        //   });
      }
    },

    doSave4Preview() {
      let promise = new Promise((resolve, reject) => {
        remoteApi
          .save({
            projectId: this.projectId,
            pageId: this.pageId,
            doc: JSON.stringify(this.doc.blocks),
          })
          .then((doc) => {
            resolve(doc);
          });
      });
      return promise;
    },
    //更新blocks
    updateDoc(doc) {
      const blocksApi = this.editor.blocks;
      const blocks = doc.blocks || [];
      blocks.forEach((block) => {
        const blockInst = blocksApi.getById(block.id);
        blockInst && blockInst.call("updateRefId", block.data);
      });
    },

    //private 从db获取blocks
    initEditorJSBlocks() {
      if (this.displayMode == "preview") {
        const blocks = this.previewDoc?.blocks || [];

        const trackerItems = [];
        blocks.forEach((block) => {
          if (block.data.type == 'trackerItem') {
            trackerItems.push(block.data.trackerItem);
          }
        });
        trackerItemApi.set(this.doc.id, trackerItems);
        const newDoc = {
          id: this.previewDoc?.id || nanoid(),
          blocks,
          version: this.previewDoc.version || 0,
          elements: this.previewDoc.elements || [],
          lastModifiedDate: this.previewDoc.lastModifiedDate
        }
        return Promise.resolve(newDoc);
      } else {
        return findByPageId(this.projectId, this.pageId).then((doc) => {

          const newDoc = {
            id: doc?.id,
            blocks: doc.blocks || [],
            version: doc.version || 0,
            elements: doc.elements || [],
            lastModifiedDate: doc.lastModifiedDate
          }
          return newDoc;
        });
      }
    },
    //private 获取当前页面及配置信息
    initCurrentPage() {
      return findOneProjectPage4Block(this.projectId, this.pageId).then(
        (vo) => {
          this.page = vo;
          return this.page;
        }
      );
    },
    initTrackers() {
      return findTrackers({ projectId: this.projectId, showInternal: false }).then(trackers => {
        //根据工作项权限过滤
        let objectIds = getTrackerPermissionIds('ITEM_CREATE')
        const newTrackers = trackers.filter(item => objectIds?.includes(item.id))
        return Promise.resolve(newTrackers);
      })
    },

    initEditorJS() {
      const that = this;
      this.docLoading = true;
      this.converters = [];
      if (that.pageId) {
        Promise.all([this.initCurrentPage(), this.initEditorJSBlocks(), this.initTrackers()])
          .then(([page, newDoc, trackers]) => {
            //1、初始话doc
            this.doc = newDoc || { blocks: [] };
            const trackerItems = [];
            this.doc.blocks.forEach((block) => {
              if (block.data.type == 'trackerItem') {
                trackerItems.push(block.data.trackerItem);
              }
            });
            trackerItemApi.set(this.doc.id, trackerItems);

            const { version, blocks, lastModifiedDate } = newDoc;

            //2、设置页面page
            trackerItemApi.setDocConfig(this.doc.id, this.page);
            let trackerIds = [];
            if (page.tracker && page.tracker.id) {
              trackerIds.push(page.tracker.id);
            }
            let settingTrackerIds = page.pageSettingTrackers?.map((t) => {
              if (t.id != page.tracker?.id) {
                trackerIds.push(t.id)
              }
            });
            trackerIds.concat(settingTrackerIds);

            //3、初始化trackers
            trackers.forEach((tracker) => {
              const trackerType = tracker.trackerType;
              const icon = tracker.icon || trackerType.icon;

              const config =
                page.pageSettingTrackers.find((t) => t.id == tracker.id) ||
                null;

              blocks.forEach((block) => {
                if (
                  (block?.data?.trackerId == tracker.id ||
                    block?.data?.trackerItem?.trackerId == tracker.id) &&
                  config
                ) {
                  block.config = config;
                }
              });

              this.converters.push({
                config: config,
                trackerItem: {
                  trackerId: tracker.id,
                  projectKeyName: this.page.projectkeyName,
                  trackerIcon: icon,
                  trackerBackgroundColor: trackerType.backgroundColor,
                  trackerColor: trackerType.color,
                },
                title: tracker.name,
                type: tracker.id,
              });
              this.extConfig.EXTEND_CONF.trackers.push(tracker);
              this.extConfig.EXTEND_CONF.pageConfig = page;
            });
          })
          .finally(() => {
            this.docLoading = false;
            const EVE_NAME = `create-tracker-item-by-${this.doc.id || ''}`;
            bus.$on(EVE_NAME, this.onCreateTrackerItem);
          });
      }
    },
    onCreateTrackerItem(params) {
      this.showCreateTrackerItem = params;
      this.isShowCreateTrackerItemDialog = true;

    },
    onTrackerItemSaved(item) {

      createTrackerItem(item).then(resp => {
        const EVE_NAME = `post-create-tracker-item-by-${this.doc.id || ''}`;
        this.isShowCreateTrackerItemDialog = false
        bus.$emit(EVE_NAME, resp);
      })
    },
    onExportWord() {
      exportDoc2Word(this.projectId, this.pageId);
    },
    onShowSettings() {
      this.isShowSettings = !this.isShowSettings;
    },

    onTrackerItemInsert() {
      if (this.page) {
        if (this.page.pageSettingTrackers) {
          this.settingsTrackerIds = this.page.pageSettingTrackers.map(
            (o) => o.id
          );
        }
        if (this.page?.tracker?.id) {
          this.settingsTrackerIds.push(this.page?.tracker?.id);
        }
      }
      setTimeout(() => {
        this.$refs.trackerItemSelectModal.view();
      }, 0);
    },
    onDocPropertiesView() {
      this.currentPropertyView = "document";
    },
    onEditModeChange(readOnly) {
      Object.assign(this, {
        currentPropertyView: "trackerItem",
        currentTrackerItem: {},
        docLoading: true,
        currentTrackerItemId: null,
        editor: null,
      });
      this.readOnlyMode = readOnly;

      this.initEditorJS();
    },

    trackerItemSelector(items = []) {
      let ob = this.currentBlock;
      let nb = items.map((item) => {
        const idx = this.converters.findIndex(
          (converter) => converter.type == item.tracker.id
        );
        return {
          type: item.tracker.id,
          config:
            this.page.pageSettingTrackers.find(
              (t) => t.id == item.tracker.id
            ) || null,
          data: {
            isTrackerItemLink: true,
            trackerId: item.tracker.id,
            refId: item.id,
            tracker: this.converters[idx],
            trackerItem: {
              ...item,
              projectKeyName: this.page.projectkeyName,
            },
            text: item.description,
            name: item.name,
          },
        };
      });
      console.log("trackerItemSelector", nb);
      this.$refs.docRef.insert(ob, nb);
    },

    userSelector(users) {
      this.editor.events.emit(USER_SELECTOR_COMFIRMED, users);
    },
    onSaveSettings(settings) {
      this.isShowSettings = false;
      updateOneProjectPage(this.pageId, {
        id: this.pageId,
        tracker: { id: settings.trackerId },
        pageSettingTrackers: settings.pageSettingTrackers,
      }).then((res) => {
        this.initCurrentPage();
      });
    },

    onViewModelChange(readOnly) {
      // this.initEditorJS(readOnly);
      this.$emit("viewModelChange");
    },

    onImportWord() {
      findWordImportJob(this.projectId, this.pageId).then((resp) => {
        if (resp.id) {
          VXETable.modal
            .confirm({
              title: "导入Word",
              content: "发现历史导入记录，是否继续?",
              cancelButtonText: "重新导入",
            })
            .then((type) => {
              if (type === "confirm") {
                this.$router.push({
                  name: "importWord",
                  params: {
                    projectId: this.projectId,
                    wikiId: this.pageId,
                  },
                });
              } else {
                this.showImportWordDialog = true;
              }
            });
        } else {
          this.showImportWordDialog = true;
        }
      });
    },
    onImportWordOK(attachment) {
      console.log(this.pageId);
      this.showImportWordDialog = false;

      createWordImportJob(this.pageId, {
        projectId: this.projectId,
        pageId: this.pageId,
        filePath: attachment.filePath,
        autoNumber: false,
      }).then((resp) => {
        this.$router.push({
          name: "importWord",
          params: {
            projectId: this.projectId,
            wikiId: this.pageId,
          },
        });
      });
    },
    onImportReqIF() {
      findReqIFImportJob(this.projectId, this.pageId).then((resp) => {
        if (resp.id) {
          VXETable.modal
            .confirm({
              title: "导入ReqIF",
              content: "发现历史导入记录，是否继续?",
              cancelButtonText: "重新导入",
            })
            .then((type) => {
              if (type === "confirm") {
                this.$router.push({
                  name: "importReqIF",
                  params: {
                    projectId: this.projectId,
                    wikiId: this.pageId,
                  },
                });
              } else {
                this.showImportReqIFDialog = true;
              }
            });
        } else {
          this.showImportReqIFDialog = true;
        }
      });
    },
    onImportReqIFOK(attachment) {
      console.log(this.pageId);
      this.showImportReqIFDialog = false;

      createReqIFImportJob(this.pageId, {
        projectId: this.projectId,
        pageId: this.pageId,
        filePath: attachment.filePath,
        autoNumber: false,
      }).then((resp) => {
        this.$router.push({
          name: "importReqIF",
          params: {
            projectId: this.projectId,
            wikiId: this.pageId,
          },
        });
      });
    },
    onExportReqIF() {
      this.showExportReqIFDialog = true;
    },
    onExportReqIFOK() {
      this.showExportReqIFDialog = false;
      this.$router.push({
        name: "exportReqIF",
        params: {
          projectId: this.projectId,
          wikiId: this.pageId,
        },
      });
    },
    doConverter(block, item) {
      const config =
        this.page.pageSettingTrackers.find((t) => t.id == item.type) || null;
      this.$refs.docRef.convertTo(block, {
        type: item.type,
        config,
        data: { ...item },
      });
    },
    doInsert(block, item) {
      const config =
        this.page.pageSettingTrackers.find((t) => t.id == item.type) || null;
      console.log("doInsert", item, this.page.pageSettingTrackers);
      this.$refs.docRef.insert(block, {
        type: item.type,
        config,
        data: { text: "<p></p>", ...item },
      });
    },
  },
};
</script>
<style lang="less" scoped>
@import "index";
</style>
