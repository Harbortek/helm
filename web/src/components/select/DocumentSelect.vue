<template>
  <a-select :mode="mode" v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled"
    :getPopupContainer="getPopupContainer" allowClear>
    <a-select-option v-for="item in documentList" :key="item.id" :title="item.name" :value="item.id">
      {{ item.name }}
    </a-select-option>
  </a-select>
</template>

<script>
import { findByProjectId } from "@/services/tracker/ProjectPageService";
export default ({
  name: "DocumentSelect",
  model: {
    prop: "value", //绑定的值，通过父组件传递
    event: "change" //自定义时间名
  },
  props: {
    projectId: {
      required: true
    },
    value: {
      required: true
    },
    excludes: {
      required: false,
      default: () => { return [] }
    },
    disabled: false,
    selectFirst: false,
    mode: {
      //'default' | 'multiple' | 'tags' | 'combobox'
      required: false
    },
    getPopupContainer: {
      required: false
    }
  },
  watch: {
    projectId: {
      handler: function (newVal, oldVal) {
        this.loadData();
      }
    },
    value: function (curVal, oldVal) {
      if (curVal) {
        this.selectItem = curVal;
      } else {
        if (this.selectFirst && this.documentList.length > 0) {
          this.selectItem = this.documentList[0].id;
          this.onChange(this.selectItem);
        } else {
          this.selectItem = undefined;
        }
      }
    }
  },
  mounted() {
    this.selectItem = this.value
    this.loadData()
  },
  data() {
    return {
      selectItem: "",
      documentList: []
    };
  },
  methods: {
    loadData() {
      let that = this;
      if (this.projectId) {
        findByProjectId(this.projectId).then(resp => {
          that.documentList = resp.filter(item => this.excludes.indexOf(item.id) == -1&&item.type=='wiki');
          
          if (this.selectFirst && this.documentList.length > 0) {
            this.selectItem = this.documentList[0].id;
            this.onChange(this.selectItem);
          }
        });
      }

    },
    onChange(v) {
      this.$emit("input", v);
      this.$emit("change", v);
    },
  }
});
</script>
