<template>
  <a-select v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled">
    <a-select-option v-for="item in projectCategories" :key="item.id" :title="item.name" :value="item.id">
      {{ item.name }}
    </a-select-option>
  </a-select>
</template>

<script>
import {
  findProjectCategories
} from "@/services/tracker/ProjectService";

export default ({
  name: "ProjectCategorySelect",
  model: {
    prop: "value", //绑定的值，通过父组件传递
    event: "change" //自定义时间名
  },
  props: {
    value: {
      required: true
    },
    disabled: false,
    selectFirst: true
  },
  watch: {
    value: function (curVal, oldVal) {
      if (curVal) {
        this.selectItem = curVal;
      } else {
        if (this.selectFirst && this.projectCategories.length > 0) {
          this.selectItem = this.projectCategories[0].id;
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
      projectCategories: []
    };
  },
  methods: {
    loadData() {
      let that = this;
      findProjectCategories().then(resp => {
        that.projectCategories = resp;
        if (this.selectFirst && this.projectCategories.length > 0) {
          this.selectItem = this.projectCategories[0].id;
          this.onChange(this.selectItem);
        }
      });
    },
    onChange(v) {
      this.$emit("input", v);
      this.$emit("change", v);
    }
  }
});
</script>
