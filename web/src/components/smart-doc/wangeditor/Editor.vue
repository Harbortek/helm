<template>
  <div class="editor" ref="box" @click="handleClick">

  </div>
</template>
<script>
import Vue, { ref } from "vue";
import { createEditor, SlateEditor, SlateTransforms, SlateNode, DomEditor } from "@wangeditor/editor";
import _ from 'lodash';


function genErrorInfo(fnName) {
  let info = `请使用 '@${fnName}' 事件，不要放在 props 中`;
  info += `\nPlease use '@${fnName}' event instead of props`;
  return info;
}

export default Vue.extend({
  render(h) {
    return h("div", { class: "editor", ref: "box" });
  },
  name: "Editor",
  data() {
    return {
      preValue: [],
      editor: null,
    };
  },
  computed: {
  },
  props: ["initConfig", "isReadonly", "initValue", "editorMode", "loading"],
  mounted() {
    this.create();
  },
  watch: {
    initValue(newVal) {
      // 重置 HTML
      // this.setContent(newVal);
    },
  },
  methods: {
    findTrackerItemParent(event) {
      // 获取事件的目标元素
      const target = event.target;
      // 使用 closest 方法查找包含 weType="tracker-item" 的父元素
      const trackerItemParent = target.closest('[data-w-e-type="tracker-item"]');
      if (trackerItemParent) {
        const childNode = DomEditor.toSlateNode(this.editor, target);
        const topNode = DomEditor.getTopNode(this.editor, childNode);
        const trackerItem = topNode.trackerItem;
        return {
          ref: trackerItemParent.getAttribute('data-x-ref'),
          trackerItem: trackerItem
          // type:trackerItemParent.getAttribute('data-w-e-type')
        };
      } else {
        return {
          ref: null,
          trackerItem: null
        };
      }
    },

    handleClick(event) {
      const { ref, trackerItem } = this.findTrackerItemParent(event);
      //根据事件event,寻找find 父元素含属性 weType 是tracker-item 的 元素
      if (ref) {
        this.$emit("tracker-item-focus", ref, trackerItem);
      } else {
        this.$emit("tracker-item-focus", null, null);
      }
      // console.log(event);
      // if (e.target.tagName === 'IMG') {
      //   console.log('图片被点击', e.target);
      // } else if (e.target.tagName === 'A') {
      //   console.log('链接被点击', e.target);
      // }
    },
    //当前selection的节点
    findParentNode(editor) {
      let childNode = SlateNode.get(editor, editor.selection?.focus.path);
      while (true) {
        let p = editor.getParentNode(childNode);
        if (!p.type) {
          break;
        }
        childNode = p;
      }
      return childNode;
    },
    setContent(content) {
      if (_.isEqual(this.preValue, content)) {
        return;
      }
      const editor = this.editor;
      if (editor == null) return;
      editor.clear()
      SlateTransforms.insertNodes(editor, content, { at: [0] });
      this.preValue = _.cloneDeep(editor.children);
      editor.history.undos = [];
      editor.history.redos = [];

    },
    numberHeaders(doc) {
      // 定义一个递归函数来编号标题
      function numberElements(elements, counters) {
        elements.forEach((element) => {
          // 获取当前元素的标签名，并转换为小写
          const tagName = element.tagName.toLowerCase();
          // 根据标签名确定编号
          let level = parseInt(tagName[1], 10); // 从h1, h2, ...中提取数字
          let number = '';

          // 检查前一个层级是否有编号，如果没有则从1开始
          for (let i = 1; i < level; i++) {
            if (counters[i] === 0) {
              counters[i] = 1;
            }
            number += counters[i] + '.';
          }

          number += (counters[level] += 1);
          const oldNumber = element.getAttribute('data-x-index');
          // 将编号设置为元素的内容，并附加原始内容
          // if (oldNumber != number) {
          element.setAttribute('data-x-index', number);
          element.children[0].setAttribute('data-x-index', number);
          // }
          // 如果当前元素有子元素，并且子元素也是标题，则递归编号
          if (element.children.length > 0) {
            numberElements(Array.from(element.children).filter(child => /^h[1-5]$/.test(child.tagName)), counters);
          }
        });
      }

      // 初始化计数器数组
      let counters = [0, 0, 0, 0, 0, 0]; // 对应h1, h2, h3, h4, h5

      // 获取所有标题元素
      const headers = Array.from(doc.querySelectorAll('h1, h2, h3, h4, h5'));
      // 从1开始编号
      numberElements(headers, counters);
      this.$emit("onOutliners");
    },
    // 创建 editor
    create() {
      if (this.$refs.box == null) return;

      const defaultConfig = this.initConfig || {};
      defaultConfig.readOnly = this.isReadonly;
      const defaultContent = _.cloneDeep(this.initValue) || [];
      // this.preValue = _.cloneDeep(defaultContent);
      createEditor({
        selector: this.$refs.box,
        // content: defaultContent || [],
        config: {
          ...defaultConfig,


          onCreated: (editor) => {
            this.editor = Object.seal(editor); // 注意，一定要用 Object.seal() 否则会报错
            this.$emit("editor-created", editor);
            if (defaultConfig.onCreated) {
              const info = genErrorInfo("editor-created");
              throw new Error(info);
            }
            this.setContent(defaultContent);
            //更新标题编号
            this.numberHeaders(this.$refs.box);
          },
          onChange: (editor) => {
            this.numberHeaders(this.$refs.box);
            const curValue = editor.children;
            if (_.isEqual(this.preValue, curValue)) {
              return;
            }
            
            this.$emit("onChange", editor.operations);
            this.preValue = _.cloneDeep(curValue);
            // const currentNode = this.findParentNode(editor);
            // const children = editor.children;
            // if (children && this.preValue === children) {
            //   return;
            // } else {
            //   this.preValue = children;
            // }
            // console.log(currentNode);
            // const editorHtml = editor.getHtml();
            // this.$emit("input", children); // 用于自定义 v-model

            // if (defaultConfig.onChange) {
            //   const info = genErrorInfo("onChange");
            //   throw new Error(info);
            // }
          },
          onDestroyed: (editor) => {
            this.$emit("onDestroyed", editor);
            if (defaultConfig.onDestroyed) {
              const info = genErrorInfo("onDestroyed");
              throw new Error(info);
            }
          },
          onMaxLength: (editor) => {
            this.$emit("onMaxLength", editor);
            if (defaultConfig.onMaxLength) {
              const info = genErrorInfo("onMaxLength");
              throw new Error(info);
            }
          },
          onFocus: (editor) => {
            this.$emit("onFocus", editor);
            if (defaultConfig.onFocus) {
              const info = genErrorInfo("onFocus");
              throw new Error(info);
            }
          },
          onBlur: (editor) => {
            this.$emit("onBlur", editor);
            if (defaultConfig.onBlur) {
              const info = genErrorInfo("onBlur");
              throw new Error(info);
            }
          },
          customAlert: (info, type) => {
            this.$emit("customAlert", info, type);
            if (defaultConfig.customAlert) {
              const info = genErrorInfo("customAlert");
              throw new Error(info);
            }
          },
          customPaste: (editor, event) => {
            if (defaultConfig.customPaste) {
              const info = genErrorInfo("customPaste");
              throw new Error(info);
            }
            let res;
            this.$emit("customPaste", editor, event, (val) => {
              res = val;
            });
            return res;
          },
        },
        mode: this.editorMode || "default",
      });
    },
  },
});
</script>
<style lang="less" scoped>
.block {
  position: relative;
  margin: 0;
  padding: 0px 0 0px 0;

  &[data-x-index] span[data-x-index]::before {
    content: attr(data-x-index) ". ";
  }

  &:hover {
    background-color: #EFF7FF;
    /* 红色边框 */
    // transition: background-color 0.3s ease;
  }
}

.editor {
  width: 100%;
  height: 100%;
  cursor: text;
  caret-color: #06f;

  /deep/ .w-e-text-container div[data-slate-editor] {
    padding: 10px 40px 10px 40px;

    p {
      margin: 0;
    }

    >h1 {

      .block;

      &:hover:before {
        content: "H1";
        position: absolute;
        font-size: 12px;
        color: gray;
        margin-left: -20px;
        margin-top: 5px;
      }
    }

    >h2 {

      .block;

      &:hover:before {
        content: "H2";
        position: absolute;
        font-size: 12px;
        color: gray;
        margin-left: -20px;
        margin-top: 5px;
      }
    }

    >h3 {


      .block;

      &:hover:before {
        content: "H3";
        position: absolute;
        font-size: 12px;
        color: gray;
        margin-left: -20px;
        margin-top: 5px;
      }
    }

    >h4 {

      .block;

      &:hover:before {
        content: "H4";
        position: absolute;
        font-size: 12px;
        color: gray;
        margin-left: -20px;
        margin-top: 5px;
      }
    }

    >h5 {
      .block;

      &:hover:before {
        content: "H5";
        position: absolute;
        font-size: 12px;
        color: gray;
        margin-left: -20px;
        margin-top: 5px;
      }
    }

    >p {
      .block;

      &:hover:before {
        position: absolute;
        margin-left: -20px;
        margin-top: 0px;
      }

      &:not([data-w-e-type]) {
        &:hover:before {
          content: "P";
          font-size: 12px;
          color: gray;
        }
      }

      &[data-w-e-type="tracker-item"] {

        &:hover:before {
          content: "";
          display: inline-block;
          width: 14px;
          height: 14px;
          background-image: var(--tracker-icon);
          background-size: cover;
        }
      }
    }
  }
}
</style>