<template>
  <div style="width: 100%">
    <a-col style="flex:1 1;">
      <a-form-model ref="legendForm" :model="legendForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <a-form-model-item v-show="showProperty('show')" :label="$t('chart.show')" class="form-item">
          <a-checkbox v-model="legendForm.show" @change="changeLegendStyle('show')">{{ $t('chart.show') }}</a-checkbox>
        </a-form-model-item>
        <div v-show="legendForm.show">
          <a-form-model-item v-show="showProperty('icon')" :label="$t('chart.icon')" class="form-item">
            <a-select v-model="legendForm.icon" :placeholder="$t('chart.icon')" @change="changeLegendStyle('icon')"
              dropdownClassName="props-dropdown">
              <a-select-option v-for="item in iconSymbolOptions" :key="item.value" :title="item.name"
                :value="item.value">{{ item.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('orient')" :label="$t('chart.orient')" class="form-item">
            <a-radio-group v-model="legendForm.orient" size="small" @change="changeLegendStyle('orient')">
              <a-radio-button value="horizontal">{{ $t('chart.horizontal') }}</a-radio-button>
              <a-radio-button value="vertical">{{ $t('chart.vertical') }}</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('textStyle')" :label="$t('chart.text_fontsize')" class="form-item">
            <a-select v-model="legendForm.textStyle.fontSize" :placeholder="$t('chart.text_fontsize')" size="small"
              dropdownClassName="props-dropdown" @change="changeLegendStyle('textStyle')">
              <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('textStyle')" :label="$t('chart.text_color')" class="form-item">
            <vue-color-picker v-model="legendForm.textStyle.color" class="color-picker-style" :predefine="predefineColors"
              @change="changeLegendStyle('textStyle')" />
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('hPosition')" :label="$t('chart.text_h_position')" class="form-item">
            <a-radio-group v-model="legendForm.hPosition" size="small" @change="changeLegendStyle('hPosition')">
              <a-radio-button value="left">{{ $t('chart.text_pos_left') }}</a-radio-button>
              <a-radio-button :disabled="legendForm.vPosition === 'center'" label="center">{{ $t('chart.text_pos_center')
              }}</a-radio-button>
              <a-radio-button value="right">{{ $t('chart.text_pos_right') }}</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('vPosition')" :label="$t('chart.text_v_position')" class="form-item">
            <a-radio-group v-model="legendForm.vPosition" size="small" @change="changeLegendStyle('vPosition')">
              <a-radio-button value="top">{{ $t('chart.text_pos_top') }}</a-radio-button>
              <a-radio-button :disabled="legendForm.hPosition === 'center'" label="center">{{ $t('chart.text_pos_center')
              }}</a-radio-button>
              <a-radio-button value="bottom">{{ $t('chart.text_pos_bottom') }}</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
        </div>
      </a-form-model>
    </a-col>
  </div>
</template>

<script>
import { COLOR_PANEL, DEFAULT_LEGEND_STYLE } from '@/pages/smart-page/util/chart'

export default {
  name: 'LegendSelectorAntV',
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
      legendForm: JSON.parse(JSON.stringify(DEFAULT_LEGEND_STYLE)),
      fontSize: [],
      iconSymbolOptions: [
        { name: this.$t('chart.line_symbol_circle'), value: 'circle' },
        { name: this.$t('chart.line_symbol_rect'), value: 'square' },
        { name: this.$t('chart.line_symbol_triangle'), value: 'triangle' },
        { name: this.$t('chart.line_symbol_diamond'), value: 'diamond' }
      ],
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
      if (chart.customStyle) {
        let customStyle = null
        if (Object.prototype.toString.call(chart.customStyle) === '[object Object]') {
          customStyle = JSON.parse(JSON.stringify(chart.customStyle))
        } else {
          customStyle = JSON.parse(chart.customStyle)
        }
        if (customStyle.legend) {
          this.legendForm = customStyle.legend
        }
      }
    },
    init() {
      const arr = []
      for (let i = 10; i <= 60; i = i + 2) {
        arr.push({
          name: i + '',
          value: i + ''
        })
      }
      this.fontSize = arr
    },
    changeLegendStyle(modifyName) {
      if (!this.legendForm.show) {
        this.isSetting = false
      }
      this.legendForm['modifyName'] = modifyName
      this.$emit('onLegendChange', this.legendForm)
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
