<template>
  <a-select :mode="mode" v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled"
    :getPopupContainer="getPopupContainer" allowClear>
    <a-select-option v-for="item in collections" :key="item.id" :title="item.name" :value="item.id">
      {{ item.name }}
    </a-select-option>
  </a-select>
</template>

<script>
import { findCollectionsByProjectId, } from '@/services/collection/CollectionService'
export default ({
  name: "CollectionSelect",
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
    trackerPermName: {
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
        if (this.selectFirst && this.collections.length > 0) {
          this.selectItem = this.collections[0].id;
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
      collections: []
    };
  },
  methods: {
    loadData() {
      let that = this;
      if (this.projectId) {
        findCollectionsByProjectId(this.projectId,null, { page: 0, size: 1000, }).then(resp => {
          that.collections = resp.content.filter(item => this.excludes.indexOf(item.id) == -1);
          
          if (this.selectFirst && this.collections.length > 0) {
            this.selectItem = this.collections[0].id;
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
