<template>
  <div class="h-editor" :loading="loading">
    <a-spin size="small" :spinning="loading" class="wang-editor-loading"></a-spin>
    <Outliner class="outliner-holder" :directory="tocData" v-if="!loading && showOutLiner"
      style="overflow: hidden;height: 100%;width: 250px;float: left;">
    </Outliner>
    <div :style="`overflow: hidden;height: 100%;width:${!showOutLiner ? '100%' : 'calc(100% - 250px)'};`"
      v-if="!loading">
      <Toolbar ref="toolbarRef" class="wang-editor-toolbar" style="border-bottom: 1px solid #ccc;height: 42px;"
        :init-config="toolbarConfig" :editor-mode="mode" :loading="loading" />
      <Editor :id="holder" ref="editorRef" :init-value="rawValue" :init-config="editorConfig" :is-readonly="isReadonly"
        style="height: calc(100% - 62px);" @editor-created="handleCreated" @onChange="onChange" @onFocus="onFoucus"
        @onBlur="onBlur" :editor-mode="mode" :loading="loading" @onOutliners="queryOutliner"
        @tracker-item-focus="handleTrackerItem" />
    </div>
    <div class="h-op" v-if="!loading">
      <!-- <h-icon
        type="stretch"
        style="cursor: pointer"
        @click="doToggleFull"
        title="宽窄屏"
      /> -->
      <a-icon type="unordered-list" style="cursor: pointer" @click="doToggleOutliner" title="大纲" />

      <slot name="property" style="cursor: pointer"></slot>
      <a-icon slot="indicator" type="loading" spin v-show="saving" />
    </div>
  </div>
</template>

<script>
import Editor from "./Editor.vue";
import Toolbar from "./Toolbar.vue";
import { DomEditor, Boot, SlateNode, SlateTransforms } from "@wangeditor/editor";
import Outliner from "./Outliner.vue";
import store from "@/store";
const STORE_CURRENT_USER = "account/user";
import _ from "lodash";
import bus from "@/utils/bus";
import { text } from "dom7";
// Boot.registerModule(docxPlugin)

export default {
  name: "HEditor",
  components: {
    Editor,
    Toolbar,
    Outliner
  },
  props: {
    initValue: {},
    isReadonly: {
      type: Boolean,
      default: false,
    },
    loading: false,
    holder: {
      type: String,
      require: true,
    },
    extConfig: {},
  },
  watch: {
    initValue: {
      handler: function (curVal, oldVal) {
        this.rawValue = curVal?.elements || [];
        this.editorConfig.docId = this.initValue.id
        if (curVal?.id) {
          this.initWebsocket();
          this.initEvents();
        }
      },
      immediate: true,
    },
    extConfig: {
      handler(newValue) {
        this.editorConfig = _.merge(this.editorConfig, newValue);
        const { EXTEND_CONF } = this.editorConfig;
        this.editor && this.editor.getConfig && (this.editor.getConfig().EXTEND_CONF = EXTEND_CONF);
      },
      deep: true,
      immediate: true,
    },
  },
  created() {
    // this.loadData()
    this.debouncedSave = _.debounce(() => {
      this.doSave()
    }, 300);
  },
  mounted() {
  },
  beforeDestroy() {
    if (this.editor == null) return;
    this.editor.destroy(); // 组件销毁时，及时销毁编辑器
    this.editor = null;
    this.closeSocket();
  },
  computed: {
    styleFull() {
      return this.showFull
        ? { width: "100%" }
        : {
          "max-width": "780px",
        };
    },
  },
  data() {
    return {
      queuedData: [],
      tocData: [],
      mode: "default",
      rawValue: [],
      editor: {},
      showOutLiner: true,
      showFull: false,
      saving: false,
      isWsReady: false,
      toolbarConfig: {
        insertKeys: {
          index: 0,
          // keys: ["insertUML", "uploadAttachment", "insertFormula"], // show menu in toolbar
          keys: ["uploadAttachment"], // show menu in toolbar

        },
        toolbarKeys: [
          // 'headerSelect',
          "bold",
          "clearStyle",
          "color",
          "bgColor",
          "|",
          // 菜单组，包含多个菜单
          {
            key: "group-image", // 必填，要以 group 开头
            title: "图片", // 必填
            // iconSvg: '<svg></svg>',
            menuKeys: [
              "uploadImage",
              "insertImage",
              "deleteImage",
              "editImage",
              "viewImageLink",
            ],
          },
          // {
          //   key: "group-image", // 必填，要以 group 开头
          //   title: "AI", // 必填
          //   // iconSvg: '<svg></svg>',
          //   menuKeys: [
          //     "aiTranslate",
          //     "aiProofread",
          //   ],
          // },
          // {
          //     key: 'group-video',
          //     title: '视频',
          //     iconSvg: '',
          //     menuKeys: ['insertVideo',
          //         'uploadVideo']
          // },
          // {
          //     key: 'group-link',
          //     title: '链接',
          //     menuKeys: ['insertLink', 'editLink', 'unLink', 'viewLink']
          // },
          {
            key: "group-table",
            title: "表格",
            menuKeys: [
              "insertTable",
              "deleteTable",
              "insertTableRow",
              "deleteTableRow",
              "insertTableCol",
              "deleteTableCol",
              "tableHeader",
              "tableFullWidth",
            ],
          },
          // 'divider',
          // 'emotion',
          // 'blockquote',

          // 'redo',
          // 'undo',
          // 'fullScreen'
        ],
      },
      editorConfig: {
        placeholder: "请输入内容...",
        onchangeTimeout: 500,
        autoFocus: false,
        docId: this.initValue.id,
        showMenuTooltips: false,
        MENU_CONF: {
          uploadImage: {
            server: process.env.VUE_APP_API_BASE_URL + "/uploadFile2",
            timeout: 5 * 1000, // 5s
            fieldName: "file",
            metaWithUrl: true, // join params to url
            maxFileSize: 10 * 1024 * 1024, // 10M
            base64LimitSize: 5 * 1024, // insert base64 format, if file's size less than 5kb
            onBeforeUpload(file) {
              console.log("onBeforeUpload", file);
              return file; // will upload this file
              // return false // prevent upload
            },
            onProgress(progress) {
              console.log("onProgress", progress);
            },
            onSuccess(file, res) {
              console.log("onSuccess", file, res);
            },
            onFailed(file, res) {
              console.log("onFailed", file, res);
            },
            onError(file, err, res) {
              console.error("onError", file, err, res);
            },
          },
          uploadAttachment: {
            server: process.env.VUE_APP_API_BASE_URL + "/uploadFile2",
            timeout: 5 * 1000, // 5s

            fieldName: "file",

            maxFileSize: 30 * 1024 * 1024, // 10M

            onBeforeUpload(file) {
              console.log("onBeforeUpload", file);
              return file; // 上传 file 文件
              // return false // 会阻止上传
            },
            onProgress(progress) {
              console.log("onProgress", progress);
            },
            onSuccess(file, res) {
              console.log("onSuccess", file, res);
            },
            onFailed(file, res) {
              console.log("onFailed", file, res);
            },
            onError(file, err, res) {
              alert(err.message);
              console.error("onError", file, err, res);
            },

            onInsertedAttachment(elem) {
              console.log("inserted attachment", elem);
            },
          },
        },
        hoverbarKeys: {
          text: {
            // menuKeys: ["aiTranslate", "aiProofread", "bold", "underline", "italic"], // “加粗”、“下划线”、“斜体”、“删除线”菜单
            menuKeys: ["aiTranslate", "bold", "underline", "italic"],
          },
          attachment: {
            menuKeys: ["downloadAttachment"], // “下载附件”菜单
          },
          formula: {
            menuKeys: ["editFormula"], // “编辑公式”菜单
          },
          uml: {
            menuKeys: ["editUML"],
          },
        },
      },
    };
  },
  methods: {
    initEvents() {
      const docId = this.initValue.id;
      const EVE_NAME = `post-update-tracker-item-by-${docId || ''}`;
      bus.$on(EVE_NAME, this.handlePostUpdateTrackerItem.bind(this));
    },
    handlePostUpdateTrackerItem(item) {

      // this.editor.children.forEach((child, index) => {
      //   if (child.type == "tracker-item" && child.ref == item.id) {
      //     const path = [index];
      //     const newNode = {
      //       "type": "tracker-item",
      //       "children": [
      //         {
      //           "type": "tracker-item-title",
      //           "children": [
      //             {
      //               "type": "text",
      //               "text": item.name,
      //             }
      //           ],
      //           "ref": item.id
      //         },
      //         {
      //           "type": "tracker-item-description",
      //           "children": JSON.parse(item.description),
      //           "ref": item.id
      //         },
      //         {
      //           "type": "tracker-item-extra",
      //           "children": [
      //             {
      //               "type": "text",
      //               "text": "",
      //             }
      //           ],
      //           "ref": item.id
      //         }
      //       ],
      //       "ref": item.id,
      //       "trackerItem": item
      //     }

      //     SlateTransforms.setNodes(this.editor, {
      //       children: newNode.children,
      //       trackerItem: newNode.trackerItem,
      //     }, { at: path });
      //   }
      // });

    },
    doToggleOutliner() {
      this.showOutLiner = !this.showOutLiner;
    },
    doToggleFull() {
      this.showFull = !this.showFull;

    },
    doSave(data) {
      if (this.isReadonly) {
        return;
      }
      if (this.isWsReady) {
        // const toDel = data.filter((item) => {
        //   return (item.type == "remove_node" && item.node.type == 'tracker-item');
        // }).map((item) => {
        //   return item.node.ref;
        // });

        if (this.queuedData.length > 0) {
          console.log('300ms debounce', this.queuedData);
          this.saving = true;
          this.$hws.send(`/smart-doc/modified/${this.initValue.id}`, this.queuedData);
          this.queuedData = []; // 清空队列
        }

        // this.$hws.send(`/smart-doc/modified/${this.initValue.id}`, data);
      }
    },
    initWebsocket() {
      if (this.isReadonly) {
        return;
      }
      this.$hws.reconnect(() => {
        this.isWsReady = true;
        this.$hws.subscribe(
          `/user/${store.getters[STORE_CURRENT_USER].id}/smart-doc/notice/${this.initValue.id}`,
          (body) => {
            this.saving = false;
            this.subscribeHook(JSON.parse(body));
          }
        );
      }, () => {
        this.$message.error("远程连接失败，请尝试刷新页面重试！", 10);
      });
    },
    subscribeHook(message) {
      let resPayloads = message?.payload || [];
      console.log('websokcet', resPayloads);
      // resPayloads.forEach((resPayload) => {
      //   this.blockDatas.forEach((item, idx) => {
      //     if (resPayload.data.id == item.id) {
      //       switch (resPayload.type) {
      //         case "DELETE":
      //           break;
      //         default:
      //           item = resPayload.data;
      //           console.log("ws payload", item);
      //           Emitter.$emit(`block-${item.id}-updated`, resPayload.data);
      //           break;
      //       }
      //     }
      //   });
      // });
    },
    closeSocket() {
      if (this.isReadonly) {
        return;
      }
      this.$hws.destroy();
    },
    handleTrackerItem(ref, tradkerItem) {
      this.$emit("tracker-item-focus", ref, tradkerItem);
    },
    handleCreated(editor) {
      this.$refs.toolbarRef.create(editor);
      this.editor = editor;
    },
    onChange(data) {
      this.$emit("change", data);
      this.queuedData.push(...data);

      this.debouncedSave();
    },
    onFoucus() {
      this.$emit("focus");
    },
    onBlur(event) {
      this.$emit("blur", event);
    },
    queryOutliner() {
      const doc = this.$refs.editorRef.$el;
      const arrEles = doc.querySelectorAll('h1, h2, h3, h4, h5');
      this.tocData = [];
      arrEles.forEach(ele => {
        const id = ele.getAttribute('id');
        const title = ele.innerText;
        const idx = ele.dataset.xIndex;
        this.tocData.push({
          id,
          title,
          level: ele.nodeName.slice(1),
          idx
        })
      });
    }
  },
};
</script>
<style lang="less" scoped>
.h-editor {
  .wang-editor-loading {
    top: 20%;
    left: 50%;
    position: absolute;
  }

  position: relative;
  //   border: 1px solid #ccc;
  height: 100%;

  .h-op {
    position: absolute;
    right: 20px;
    top: 50px;

    margin: 0 auto;

    i {
      width: 15px;
      height: 15px;
      display: block;
      margin-top: 8px;
    }

    svg {
      width: 15px;
      height: 15px;
      display: block;
      margin-top: 8px;
    }
  }
}

.directory-list {}
</style>