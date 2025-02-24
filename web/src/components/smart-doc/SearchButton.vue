<!-- 浏览器最新highlight规范：性能优 -->
<template>
  <vxe-button type="text" size="mini" icon="vxe-icon-search" @click="doOpenSearchingModal">
    替换
    <vxe-modal ref="searchModal" width="600" @close="onClose" :show-close="false" show-footer :mask="false">
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
          <vxe-form-item title="替换" span="24" title-width="60" title-align="right" v-if="searchedEls.length > 0 && !readOnly">
            <template #default="params">
              <vxe-input v-model="params.data.replacer" placeholder="替换" clearable></vxe-input>
            </template>
          </vxe-form-item>
        </vxe-form>
      </template>
      <template #footer>
        <vxe-button @click="doClose">关闭</vxe-button>
        <vxe-button status="primary" @click="doPreOne()" v-if="searchedEls.length > 0">上一个</vxe-button>
        <vxe-button status="primary" @click="doNextOne()" v-if="searchedEls.length > 0">下一个</vxe-button>
        <vxe-button status="primary" @click="doReplaceOne()" v-if="searchedEls.length > 0 && !readOnly">替换</vxe-button>
        <vxe-button status="primary" @click="doReplaceAll()"
          v-if="searchedEls.length > 0 && !readOnly">替换所有</vxe-button>
        <!-- <vxe-button status="primary" @click="doSearching()">查找</vxe-button> -->
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
  watch: {
    'searchKey.key': function (newVal, oldVal) {
      this.doSearching();
    },
    'searchKey.caseMatch': function (newVal, oldVal) {
      this.doSearching();
    }
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

    doOpenSearchingModal() {
      this.$refs.searchModal.open();
    },
    //find 查找，并高亮选中所有
    doSearching() {
      this.reset();
      this.$refs.form.validate().then((err) => {
        console.log(err);
        if (err) return;
        this.highlight(this.searchedRanges(this.getAllTextNodes(this.rootElement())));
      });

    },
    rootElement() {
      return document.getElementById(this.rootElementId);
    },
    doPreOne() {
      if (this.searchedEls.length > 0) {
        this.currentElementIdx = this.currentElementIdx > 0 ? this.currentElementIdx - 1 : 0;
        const currentElement = this.searchedEls[this.currentElementIdx];
        this.activeOne(currentElement);
      }
    },
    //下一个查找
    doNextOne() {
      if (this.searchedEls.length > 0) {
        this.currentElementIdx = this.currentElementIdx < this.searchedEls.length - 1 ? this.currentElementIdx + 1 : this.searchedEls.length - 1;
        const currentElement = this.searchedEls[this.currentElementIdx]
        this.activeOne(currentElement);
      }
    },
    activeOne(ele) {
      ele && ele.startContainer.parentNode.scrollIntoView();
      let selection = window.getSelection();
      // 移除所有现有的选择范围
      selection.removeAllRanges();
      // 添加新的选择范围
      selection.addRange(ele);
      // this.applyCssToSelection(selection);
    },
    applyCssToSelection(selection) {
      // 遍历每个范围（通常只有一个范围，但用户可以选择多个范围）
      for (var i = 0; i < selection.rangeCount; i++) {
        var range = selection.getRangeAt(i);
        var span = document.createElement('span');
        span.style.backgroundColor = 'yellow'; // 例如，改变背景颜色

        // 将span元素插入到Range中
        range.surroundContents(span);
      }
    },
    //替换当前查找
    doReplaceOne(sn) {
      if (!sn) {
        sn = this.searchedEls ? this.searchedEls[this.currentElementIdx] : null;
      }
      if (sn) {
        const range = sn;
        range.deleteContents();
        range.insertNode(document.createTextNode(this.searchKey.replacer));
      }
    },
    reset() {
      CSS.highlights.clear();
      this.currentElementIdx = 0;
      this.searchedEls = [];

    },
    doClose() {
      // this.reset();
      // this.searchKey = {
      //   caseMatch: false,
      //   key: '',
      //   replacer: ''
      // };
      this.$refs.searchModal.close();
    },
    onClose() {

    },
    doReplaceAll() {
      this.searchedEls.forEach(item => {
        this.doReplaceOne(item);
      });
    },
    highlight(ranges) {
      const allRanges = ranges.flat();
      const highlight = new Highlight(...allRanges);
      CSS.highlights.set('my-custom-highlight', highlight);
      this.searchedEls = allRanges;
    },
    searchRegex() {
      return this.searchWrapper(this.searchKey.key);
    },
    searchWrapper(text) {
      if (this.searchKey.caseMatch) {
        return text;
      } else {
        return text.toLowerCase();
      }

    },
    searchedRanges(allTextNodes) {
      const keyword = this.searchRegex();
      const tNodes = allTextNodes.map((el) => {
        return { el, text: this.searchWrapper(el.textContent) };
      });
      const ranges = tNodes.map(({ text, el }) => {
        const indices = [];
        let startPos = 0;
        while (startPos < text.length) {
          const index = text.indexOf(keyword, startPos); // keyword 即为需要匹配的关键字
          if (index === -1) break;
          indices.push(index);
          startPos = index + keyword.length;
        }
        // 为文本节点中找到的每个匹配项创建一个范围（Range）对象
        return indices.map((index) => {
          const range = new Range();
          range.setStart(el, index);
          range.setEnd(el, index + keyword.length);
          return range;
        });
      });
      return ranges.filter((r) => r.length > 0);
    },
    getAllTextNodes(element) {
      const treeWalker = document.createTreeWalker(element, NodeFilter.SHOW_TEXT);
      const textNodes = [];
      let currentNode = treeWalker.nextNode();
      while (currentNode) {
        textNodes.push(currentNode);
        currentNode = treeWalker.nextNode();
      }
      return textNodes;
    }
  }
}
</script>
<style lang="less">
::highlight(my-custom-highlight) {
  background-color: yellow;
  color: black;

}
</style>