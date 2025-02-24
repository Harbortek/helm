<template>
  <a-select v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled" allowClear>
    <a-select-option v-for="item in getDataList" :key="item.id" :title="item.name" :value="item.id">
      {{ item.name }}
    </a-select-option>
  </a-select>
</template>

<script>
import {
  findBaselinesByProjectId
} from "@/services/baseline/BaselineService";

export default ({
  name: "ProjectBaselineSelect",
  model: {
    prop: "value", //绑定的值，通过父组件传递
    event: "change" //自定义时间名
  },
  props: {
    value: {
      required: true
    },
    projectId: {
      required: true
    },
    disabled: false,
    type: {
      required: false,
      type: String,
    }
  },
  computed: {
    getDataList(){
      if(this.type){
        return this.dataList.filter(item => item.type === this.type)
      }
      return this.dataList;
    }
  },
  watch: {
    value: function (curVal, oldVal) {
      this.selectItem = curVal;
    }
  },
  mounted() {
    this.selectItem = this.value
    this.loadData()
  },
  data() {
    return {
      selectItem: "",
      dataList: []
    };
  },
  methods: {
    loadData() {
      let that = this;
      findBaselinesByProjectId(this.projectId).then(resp => {
        that.dataList = resp;
      });
    },
    onChange(v) {
      this.$emit("input", v);
      let value=this.dataList.filter(item => item.id === v)
      this.$emit("change", v,value);
    }
  }
});
</script>
