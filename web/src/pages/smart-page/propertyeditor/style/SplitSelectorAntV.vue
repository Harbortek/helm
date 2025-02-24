<template>
  <div style="width: 100%">
    <a-col style="flex: 1 1;">
      <a-form-model ref="splitForm" :model="splitForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <a-form-model-item v-show="showProperty('name')" :label="$t('chart.name')" class="form-item">
          <a-checkbox v-model="splitForm.name.show" @change="changeSplitStyle('name')">{{ $t('chart.show')
          }}</a-checkbox>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('name')" :label="$t('chart.color')" class="form-item">
          <vue-color-picker v-model="splitForm.name.color" class="color-picker-style" :predefine="predefineColors"
            @change="changeSplitStyle('name')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('name')" :label="$t('chart.text_fontsize')"
          class="form-item form-item-slider">
          <a-select v-model="splitForm.name.fontSize" :placeholder="$t('chart.text_fontsize')"
            dropdownClassName="props-dropdown" @change="changeSplitStyle('name')">
            <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name" :value="option.value">{{
              option.name }}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('lineStyle')" :label="$t('chart.axis_color')" class="form-item">
          <vue-color-picker v-model="splitForm.axisLine.lineStyle.color" class="color-picker-style"
            :predefine="predefineColors" @change="changeSplitStyle('axisLine')" />
        </a-form-model-item>
      </a-form-model>
    </a-col>
  </div>
</template>

<script>
import { COLOR_PANEL, DEFAULT_SPLIT } from '@/pages/smart-page/util/chart'

export default {
  name: 'SplitSelectorAntV',
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
      splitForm: JSON.parse(JSON.stringify(DEFAULT_SPLIT)),
      isSetting: false,
      fontSize: [],
      predefineColors: COLOR_PANEL
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
    this.init()
    this.initData()
  },
  methods: {
    initData() {
      const chart = JSON.parse(JSON.stringify(this.chart))
      if (chart.customStyle) {
        let customStyle = null
        if (Object.prototype.toString.call(chart.customStyle) === '[object Object]') {
          customStyle = JSON.parse(JSON.stringify(chart.customStyle))
        } else {
          customStyle = JSON.parse(chart.customStyle)
        }
        if (customStyle.split) {
          this.splitForm = customStyle.split
        } else {
          this.splitForm = JSON.parse(JSON.stringify(DEFAULT_SPLIT))
        }
      }
    },
    init() {
      const arr = []
      for (let i = 6; i <= 40; i = i + 2) {
        arr.push({
          name: i + '',
          value: i + ''
        })
      }
      this.fontSize = arr
    },
    changeSplitStyle(modifyName) {
      if (!this.splitForm.show) {
        this.isSetting = false
      }
      this.splitForm['modifyName'] = modifyName
      this.$emit('onChangeSplitForm', this.splitForm)
    },
    showProperty(property) {
      return this.propertyInner.includes(property)
    }
  }
}
</script>

<style lang="less" scoped>
.a-divider--horizontal {
  margin: 10px 0
}

.shape-item {
  padding: 6px;
  border: none;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-item-slider ::v-deep .a-form-model-item__label {
  font-size: 12px;
  line-height: 38px;
}

.form-item ::v-deep .a-form-model-item__label {
  font-size: 12px;
}

.a-select-dropdown__item {
  padding: 0 20px;
}

span {
  font-size: 12px
}

.a-form-model-item {
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
