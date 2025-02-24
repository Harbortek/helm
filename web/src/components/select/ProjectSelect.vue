<template>
  <a-select v-model="selectItem" :placeholder="placeholder" @change="onChange" :disabled="disabled"
    :allowClear="allowClear" :getPopupContainer="getPopupContainer">
    <a-select-option v-for="item in projects" :key="item.id" :title="item.name" :value="item.id">
      {{ item.name }}
    </a-select-option>
  </a-select>
</template>

<script>
import {
  findProjects
} from "@/services/tracker/ProjectService";

export default ({
  name: "ProjectSelect",
  model: {
    prop: "value", //绑定的值，通过父组件传递
    event: "change" //自定义时间名
  },
  props: {
    value: {
      required: true
    },
    allowClear:{
      required: false,
      default: false
    },
    excludes: {
      required: false,
      default: () => { return [] }
    },
    disabled: false,
    selectFirst: true,
    getPopupContainer:false,
    placeholder:false,
  },
  watch: {
    value: function (curVal, oldVal) {
      if (curVal) {
        this.selectItem = curVal;
      } else {
        if (this.selectFirst && this.projects.length > 0) {
          this.selectItem = this.projects[0].id;
          this.onChange(this.selectItem);
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
      projects: []
    };
  },
  methods: {
    loadData() {
      let that = this;
      findProjects({size:10000}).then(resp => {
        that.projects = resp.content.filter(item => { return this.excludes.indexOf(item.id) < 0 });
        if (!this.value&&this.selectFirst && this.projects.length > 0) {
          this.selectItem = this.projects[0].id;
          this.onChange(this.selectItem);
        }
      });
    },
    onChange(v) {
      let project = this.projects.find(item => { return item.id === v });
      this.$emit("input", v);
      this.$emit("change", v,project);
    }
  }
});
</script>
