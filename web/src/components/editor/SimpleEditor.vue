<template>
  <div class="simple-editor" @mousedown="handleMousedown">
    <Toolbar
      ref="toolbarRef"
      v-show="showToolbar"
      class="wang-editor-toolbar"
      style="border-bottom: 1px solid #ccc"
      :editor="editor"
      :defaultConfig="toolbarConfig"
      :mode="mode"
      @click="handleToolbarClick"
    />
    <Editor
      ref="editorRef"
      :style="{ height: height ? height + 'px' : 'auto', overflowY: 'hidden' }"
      :class="[disabled ? 'inactived wang-editor' : 'actived wang-editor']"
      :disabled="disabled"
      v-model="rawValue"
      :defaultConfig="editorConfig"
      :mode="mode"
      @onCreated="onCreated"
      @onChange="onChange"
      @onFocus="onFoucus"
    />
  </div>
</template>

<script>
import { Editor, Toolbar } from "@/components/wangEditor-for-vue";
import { DomEditor, Boot } from "@wangeditor/editor";

export default {
  name: "SimpleEditor",
  components: {
    Editor,
    Toolbar,
  },
  model: {
    prop: "value", //绑定的值，通过父组件传递
    event: "change", //自定义时间名
  },
  props: {
    value: {
      required: true,
    },
    disabled: false,
    showToolbar: true,
    height: false,
    extConfig: {},
  },
  watch: {
    disabled: function (curVal, oldVal) {
      if (this.editor) {
        if (!curVal) {
          this.editor.enable();
          this.editor.focus();
        } else {
          this.editor.disable();
        }
      }
    },
    value: function (curVal, oldVal) {
      this.rawValue = curVal;
    },
    showToolbar: function (curVal, oldVal) {
      this.showToolbar = curVal;
    },
  },
  created() {
    _.merge(this.editorConfig, this.extConfig);
    // this.loadData()
  },
  mounted() {
    // this.rawValue = this.value
    this.loadData();
  },
  beforeDestroy() {
    if (this.editor == null) return;
    this.editor.destroy(); // 组件销毁时，及时销毁编辑器
    this.editor = null;
  },
  data() {
    return {
      rawValue: this.value,
      editor: null,
      html: "<p>hello</p>",
      toolbarConfig: {
        insertKeys: {
          index: 0,
          // keys: ["insertUML", "uploadAttachment", "insertFormula"], // show menu in toolbar
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
        hoverbarKeys: {
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
        EXTEND_CONF: {
          // mentionConfig: {
          //   showModal, // 必须
          //   hideModal, // 必须
          // },
          promptConfig: {},
        },
      },
      mode: "simple", // or 'simple'
    };
  },
  methods: {
    onCreated(editor) {
      this.editor = Object.seal(editor);
      const toolbar = DomEditor.getToolbar(this.editor);
      // this.editor.insertNode({
      //     type: 'paragraph',
      //     children: [
      //         { text: '', fontFamily: '微软雅黑', fontSize: '14px' }
      //     ],
      //     lineHeight: 1.5
      // })
      // const curToolbarConfig = toolbar.getConfig()
      // console.log(curToolbarConfig.toolbarKeys)
      if (this.disabled) {
        this.editor.disable();
      }
    },
    loadData() {},
    onChange(v) {
      this.$emit("change", this.rawValue);
    },
    onFoucus() {
      this.$emit("focus");
    },
    handleToolbarClick(event) {
      // event.stopPropagation(); // 阻止事件传播
    },
    handleMousedown(event) {
      console.log(
        this.$refs.toolbarRef.$el.contains(event.target),
        this.$refs.editorRef.$el.contains(event.target)
      );
      if (
        !this.$refs.toolbarRef.$el.contains(event.target) &&
        !this.$refs.editorRef.$el.contains(event.target)
      ) {
        this.editor.blur();
        // this.$emit("blur", event);
      } else {
        setTimeout(() => {
          this.editor.focus();
          this.$emit("focus");
        }, 0);
      }
    },
    onBlur(event) {
      // this.$emit("blur", event);
    },
    onInitialized(editor) {},
    blur() {
      this.editor.blur();
    },
    foucus() {
      this.editor.focus();
    },
  },
};
</script>
<style lang="less" scoped>
.simple-editor {
  position: relative;
  //   border: 1px solid #ccc;
  /deep/ table td.active {
    display: table-cell;
  }
  /deep/ .w-e-modal {
    max-height: 100%;
    overflow: auto;
  }
  /deep/ .w-e-text-container [data-slate-editor] p {
    margin: 0 0;
  }
  &:has(.actived) {
    border: 1px solid #ccc;
    /deep/ .w-e-text-container {
      // min-height: 120px;
    }
  }

  .wang-editor-toolbar {
    border: 1px solid #ccc;
    position: absolute;
    top: -42px;
    // opacity: 0.8;
  }

  
}
</style>
