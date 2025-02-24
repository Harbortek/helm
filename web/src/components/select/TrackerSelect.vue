<template>
  <a-select :mode="mode" v-model="selectItem" :placeholder="placeholder" @change="onChange" :disabled="disabled"
    :getPopupContainer="getPopupContainer" allowClear>
    <a-select-option v-for="item in trackers" :key="item.id" :title="item.name" :value="item.id">
      <a-icon v-if="item.trackerType?.icon" :style="{ 'color': item.trackerType?.color }"
        :component="item.trackerType?.icon" />
      &nbsp;{{ item.name }}
    </a-select-option>
  </a-select>
</template>

<script>
import {
  findTrackers
} from "@/services/tracker/TrackerService";
import { getTrackerPermissionIds } from "@/utils/permission";

export default ({
  name: "TrackerSelect",
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
    showInternal: {
      required: false,
      default: false
    },
    trackerPermName: {
      required: false
    },
    getPopupContainer: {
      required: false
    },
    placeholder:false,
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
        if (this.selectFirst && this.trackers.length > 0) {
          this.selectItem = this.trackers[0].id;
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
      trackers: []
    };
  },
  methods: {
    loadData() {
      let that = this;
      that.trackers = [];
      if (this.projectId) {
        findTrackers({ projectId: this.projectId, showInternal: this.showInternal }).then(resp => {
          that.trackers = this.trackers.concat(resp.filter(item => this.excludes.indexOf(item.id) == -1));
          //根据工作项权限过滤
          if (this.trackerPermName) {
            let objectIds = getTrackerPermissionIds(this.trackerPermName)
            that.trackers = that.trackers.filter(item => objectIds?.includes(item.id))
          }

          if (this.selectFirst && this.trackers.length > 0) {
            this.selectItem = this.trackers[0].id;
            this.onChange(this.selectItem);
          }
        });
      }

    },
    onChange(v) {
      this.$emit("input", v);
      this.$emit("change", v);
      this.$emit("tracker-selected", this.trackers.find(item => item.id === v))
    },
    getTrackerName(id) {
      let tracker = this.trackers.find(item => item.id === id);
      return tracker ? tracker.name : "";
    }
  }
});
</script>
