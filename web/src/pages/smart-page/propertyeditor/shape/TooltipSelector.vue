<template>
  <div style="width: 100%">
    <a-col style="flex: 1 1;">
      <a-form-model ref="tooltipForm" :model="tooltipForm" label-width="80px" :layout="'horizontal'"
        :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">
        <a-form-model-item v-show="showProperty('show')" :label="$t('chart.show')" class="form-item">
          <a-checkbox v-model="tooltipForm.show" @change="changeTooltipAttr('show')">{{ $t('chart.show') }}</a-checkbox>
        </a-form-model-item>
        <div v-show="tooltipForm.show">
          <a-form-model-item v-show="showProperty('trigger')" :label="$t('chart.trigger_position')" class="form-item">
            <a-radio-group v-model="tooltipForm.trigger" size="mini" @change="changeTooltipAttr('trigger')">
              <a-radio-button value="item">{{ $t('chart.tooltip_item') }}</a-radio-button>
              <a-radio-button value="axis">{{ $t('chart.tooltip_axis') }}</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('textStyle')" :label="$t('chart.text_fontsize')" class="form-item">
            <a-select v-model="tooltipForm.textStyle.fontSize" :placeholder="$t('chart.text_fontsize')" size="mini"
              @change="changeTooltipAttr('textStyle')">
              <a-select-option v-for="option in fontSize" :key="option.value" :label="option.name"
                :value="option.value" />
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('textStyle')" :label="$t('chart.text_color')" class="form-item">
            <vue-color-picker v-model="tooltipForm.textStyle.color" class="color-picker-style"
              :predefine="predefineColors" @change="changeTooltipAttr('textStyle')" />
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('textStyle')" :label="$t('chart.background')" class="form-item">
            <vue-color-picker v-model="tooltipForm.backgroundColor" class="color-picker-style"
              :predefine="predefineColors" @change="changeTooltipAttr('backgroundColor')" />
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('formatter')" class="form-item">
            <span slot="label">
              <span class="span-box">
                <span>{{ $t('chart.content_formatter') }}</span>
                <a-tooltip class="item" effect="dark" placement="bottom">
                  <div slot="content" v-html="$t('chart.format_tip')" />
                  <i class="el-icon-info" style="cursor: pointer;" />
                </a-tooltip>
              </span>
            </span>
            <a-input v-model="tooltipForm.formatter" type="textarea" :autosize="{ minRows: 4, maxRows: 4 }"
              :placeholder="$t('chart.formatter_plc')" @blur="changeTooltipAttr('formatter')" />
          </a-form-model-item>
        </div>
      </a-form-model>
    </a-col>
  </div>
</template>

<script>
import { COLOR_PANEL, DEFAULT_TOOLTIP } from '@/pages/smart-page/util/chart'

export default {
  name: 'TooltipSelector',
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
      tooltipForm: JSON.parse(JSON.stringify(DEFAULT_TOOLTIP)),
      fontSize: [],
      isSetting: false,
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
      if (chart.customAttr) {
        let customAttr = null
        if (Object.prototype.toString.call(chart.customAttr) === '[object Object]') {
          customAttr = JSON.parse(JSON.stringify(chart.customAttr))
        } else {
          customAttr = JSON.parse(chart.customAttr)
        }
        if (customAttr.tooltip) {
          this.tooltipForm = customAttr.tooltip

          this.tooltipForm.backgroundColor = this.tooltipForm.backgroundColor ? this.tooltipForm.backgroundColor : DEFAULT_TOOLTIP.backgroundColor
        }
      }
    },
    init() {
      const arr = []
      for (let i = 10; i <= 20; i = i + 2) {
        arr.push({
          name: i + '',
          value: i + ''
        })
      }
      this.fontSize = arr
    },
    changeTooltipAttr(modifyName) {
      if (!this.tooltipForm.show) {
        this.isSetting = false
      }
      this.tooltipForm['modifyName'] = modifyName
      this.$emit('onTooltipChange', this.tooltipForm)
    },
    showProperty(property) {
      return this.propertyInner.includes(property)
    }
  }
}
</script>

<style scoped>
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
