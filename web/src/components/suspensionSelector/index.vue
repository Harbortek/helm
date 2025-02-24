<template>
  <div style="width: 100%;">
    <a-col>
      <a-form-model ref="suspensionForm" :model="suspensionForm" label-width="80px" size="mini">
        <a-form-model-item v-show="showProperty('show')" :label="$t('chart.show')" class="form-item">
          <a-checkbox v-model="suspensionForm.show" @change="changeSuspensionAttr('show')">{{ $t('chart.show') }}
          </a-checkbox>
        </a-form-model-item>

      </a-form-model>
    </a-col>
  </div>
</template>

<script>
import { DEFAULT_SUSPENSION } from '@/pages/smart-page/util/chart'

export default {
  name: 'SuspensionForm',
  props: {
    param: {
      type: Object,
      required: true
    },
    chart: {
      type: Object,
      required: true
    },
    propertyInner: {
      type: Array,
      required: false,
      default: function () {
        return []
      }
    }
  },
  data() {
    return {
      suspensionForm: JSON.parse(JSON.stringify(DEFAULT_SUSPENSION)),
      fontSize: []

    }
  },
  watch: {
    'chart': {
      handler: function () {
        this.initData()
      }
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    initData() {
      const chart = JSON.parse(JSON.stringify(this.chart))
      if (chart.customAttr) {
        let customAttr = null
        if (Object.prototype.toString.call(chart.customAttr) === '[object Object]') {
          customAttr = JSON.parse(JSON.stringify(chart.customAttr))
        } else {
          customAttr = JSON.parse(chart.customAttr)
        }
        if (customAttr.suspension) {
          this.suspensionForm = customAttr.suspension
        }
      }
    },

    changeSuspensionAttr(modifyName) {
      this.suspensionForm['modifyName'] = modifyName
      this.$emit('onSuspensionChange', this.suspensionForm)
    },

    showProperty(property) {
      return this.propertyInner.includes(property)
    }
  }
}
</script>

<style lang="less" scoped>
.shape-item {
  padding: 6px;
  border: none;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-item-slider ::v-deep .el-form-item__label {
  font-size: 12px;
  line-height: 38px;
}

.form-item ::v-deep .el-form-item__label {
  font-size: 12px;
}

.a-select-dropdown__item {
  padding: 0 20px;
}

span {
  font-size: 12px
}

.el-form-item {
  margin-bottom: 6px;
}

.switch-style {
  position: absolute;
  right: 10px;
  margin-top: -4px;
}

.color-picker-style {
  cursor: pointer;
  z-index: 1003;
}
</style>
