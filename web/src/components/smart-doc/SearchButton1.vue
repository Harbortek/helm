<!-- 方案1 兼容所有浏览器：性能差 -->
<template>
  <vxe-button type="text" icon="vxe-icon-search" @click="doOpenSearchingModal">
    查询
    <vxe-modal ref="searchModal" width="600" @close="onClose" :show-close="false" show-footer>
      <template #title>
        <span>查找/替换</span>
      </template>
      <template #default>
        <vxe-form :data="searchKey" :rules="formRules" ref="form">
          <vxe-form-item title="查找" field="key" span="16" title-width="60" title-align="right">
            <template #default="params">
              <vxe-input v-model="params.data.key" placeholder="搜索" clearable></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item field="caseMatch" span="8">
            <template #default="params">
              <vxe-checkbox v-model="params.data.caseMatch" content="忽略大小写" :checked-value="false"
                :unchecked-value="true"></vxe-checkbox>
            </template>
          </vxe-form-item>
          <vxe-form-item title="替换" span="24" title-width="60" title-align="right">
            <template #default="params">
              <vxe-input v-model="params.data.replacer" placeholder="替换" clearable></vxe-input>
            </template>
          </vxe-form-item>
        </vxe-form>
      </template>
      <template #footer>
        <vxe-button  @click="doClose">关闭</vxe-button>
        <vxe-button status="primary" @click="doPreOne()" v-if="searchedEls.length > 0">上一个</vxe-button>
        <vxe-button status="primary" @click="doNextOne()" v-if="searchedEls.length > 0">下一个</vxe-button>
        <vxe-button status="primary" @click="doReplaceOne()" v-if="searchedEls.length > 0 && !readOnly">替换</vxe-button>
        <vxe-button status="primary" @click="doReplaceAll()" v-if="searchedEls.length > 0 && !readOnly">替换所有</vxe-button>
        <vxe-button status="primary" @click="doSearching()">查找</vxe-button>
      </template>
    </vxe-modal>
  </vxe-button>
</template>

<script>
//html 标签对正则表达式，中间不含任何字符
const basicPattern = "<([a-z][a-z0-9]*)\\b[^>]*>\\s*<\\/\\1>";
export default {
  name: 'SearchButton',
  components: {
  },
  props: {
    /**
     * 搜索主体元素
     */
    rootElementId: {
      type: String,
      require: true
    },
    readOnly: {
      type: Boolean,
      require: false
    }
  },
  computed: {

  },
  data() {
    return {
      searchKey: {
        //大小写敏感
        caseMatch: false,
        key: '',
        replacer: ''
      },
      //当前选中的Element
      currentElementIdx: 0,
      searchedEls: [],
      formRules: {
        key: [
          { required: true, message: '请输入查找内容' }
        ]
      }
    }
  },
  mounted() {

  },
  methods: {
    searchRegex() {
      //遍历字符串每个字母
      let pattern = '';
      let keyLen = this.searchKey.key.length;
      if (keyLen == 0) { return null; }
      for (let i = 0; i < keyLen; i++) {
        pattern += this.searchKey.key[i] + (i < keyLen - 1) ? basicPattern : '';
      }
      // return pattern;
      return this.searchKey.key
    },
    doOpenSearchingModal() {
      this.$refs.searchModal.open();
    },
    //find 查找，并高亮选中所有
    doSearching() {
      this.reset();
      this.$refs.form.validate().then((err) => {
        console.log(err);
        if (err) return;
        const pattern = new RegExp(this.searchRegex(), this.caseMatch ? "g" : "gi");
        this.traverseNodes(this.rootElement(), (node) => {
          const innerHTML = node.innerHTML;
          let match = null;
          let searchedElsInNode = [];
          while ((match = pattern.exec(innerHTML)) !== null) {
            const item = { node: node, start: match.index, end: match[0].length, innerHTML: innerHTML };
            searchedElsInNode.push(item);
          }
          for (let i = 0; i < searchedElsInNode.length; i++) {
            const elInNode = searchedElsInNode[i];
            this.highlight(elInNode, i);
            this.searchedEls.push(elInNode);
          }
        });
      });

    },
    rootElement() {
      return document.getElementById(this.rootElementId);
    },
    highlight(sn, i = 0) {
      const leftPadding = 30;
      const rightPadding = 4;
      const innerHTML = sn.node.innerHTML;
      let start = i * (leftPadding + rightPadding) + sn.start;
      sn.node.innerHTML = `${innerHTML.substring(0, start)}<i class="searched-highlight">${innerHTML.substring(start, start + sn.end)}</i>${innerHTML.substring(start + sn.end)}`;
    },
    //移除查询到的高亮标记
    removeHighlight(sn) {
      // const reg = new RegExp(`<i class="searched-highlight">.+</i>`, "gi");
      // sn.node.innerHTML = sn.node.innerHTML.replace(reg, function (x) {
      //   return x.substring(30, x.length - 4);
      // });
      sn.node.innerHTML = sn.innerHTML;
    },
    doPreOne() {
      if (this.searchedEls.length > 0) {
        this.currentElementIdx = this.currentElementIdx > 0 ? this.currentElementIdx - 1 : 0;
        const currentElement = this.searchedEls[this.currentElementIdx];
        currentElement && currentElement.node.scrollIntoView();
      }
    },
    //下一个查找
    doNextOne() {
      if (this.searchedEls.length > 0) {
        this.currentElementIdx = this.currentElementIdx < this.searchedEls.length - 1 ? this.currentElementIdx + 1 : this.searchedEls.length - 1;
        const currentElement = this.searchedEls[this.currentElementIdx]
        currentElement && currentElement.node.scrollIntoView();
      }
    },
    //替换当前查找
    doReplaceOne(sn) {
      if (!sn) {
        sn = this.searchedEls ? this.searchedEls[this.currentElementIdx] : null;
      }
      if (sn) {
        const innerHTML = sn.node.innerHTML;
        sn.node.innerHTML = `${innerHTML.substring(0, sn.start)}${this.replacer || ''}${innerHTML.substring(sn.highlightStart + sn.end)}`;
      }
    },
    reset() {
      this.searchedEls.forEach(item => {
        this.removeHighlight(item);
      });
      this.currentElementIdx = 0;
      this.searchedEls = [];
    },
    doClose() {
      this.reset();
      this.searchKey = {
        caseMatch: false,
        key: '',
        replacer: ''
      };
      this.$refs.searchModal.close();
    },
    onClose() {

    },
    doReplaceAll() {
      this.searchedEls.forEach(item => {
        this.doReplaceOne(item);
      });
    },

    traverseNodes(node, op) {
      if (this.hasTextNode(node)) {
        // 处理当前节点
        op && op.call(this, node);
        return;
      }
      let childNodes = node.childNodes;
      // 遍历所有子节点
      for (let i = 0; i < childNodes.length; i++) {
        this.traverseNodes(childNodes[i], op);
      }
    },
    hasTextNode(node) {
      // 遍历元素的所有子节点
      for (let i = 0; i < node.childNodes.length; i++) {
        let child = node.childNodes[i];
        // 检查节点类型是否为文本节点
        if (child.nodeType === Node.TEXT_NODE && child.textContent.trim() !== '') {
          return true; // 找到一个非空的文本节点
        }
      }
      return false; // 没有找到文本节点
    }
  }
}
</script>
<style lang="less">
.searched-highlight {
  background-color: red;
}
</style>