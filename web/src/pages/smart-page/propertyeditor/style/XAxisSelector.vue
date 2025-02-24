<template>
  <div style="width: 100%">
    <a-col style="flex: 1 1;">
      <a-form-model ref="axisForm" :model="axisForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <a-form-model-item v-show="showProperty('show')" :label="$t('chart.show')" class="form-item">
          <a-checkbox v-model="axisForm.show" @change="changeXAxisStyle('show')">{{ $t('chart.show') }}</a-checkbox>
        </a-form-model-item>
        <div v-show="axisForm.show">
          <a-form-model-item v-show="showProperty('position')" :label="$t('chart.position')" class="form-item">
            <a-radio-group v-model="axisForm.position" size="mini" @change="changeXAxisStyle('position')">
              <a-radio-button value="top">{{ $t('chart.text_pos_top') }}</a-radio-button>
              <a-radio-button value="bottom">{{ $t('chart.text_pos_bottom') }}</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('name')" :label="$t('chart.name')" class="form-item">
            <a-input v-model="axisForm.name" size="mini" @blur="changeXAxisStyle('name')" />
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('nameTextStyle')" :label="$t('chart.axis_name_color')"
            class="form-item">
            <vue-color-picker v-model="axisForm.nameTextStyle.color" class="color-picker-style"
              :predefine="predefineColors" @change="changeXAxisStyle('nameTextStyle')" />
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('nameTextStyle')" :label="$t('chart.axis_name_fontsize')"
            class="form-item">
            <a-select v-model="axisForm.nameTextStyle.fontSize" :placeholder="$t('chart.axis_name_fontsize')"
              dropdownClassName="props-dropdown" @change="changeXAxisStyle('nameTextStyle')">
              <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <span v-show="showProperty('axisValue')">
            <a-divider />
            <a-form-model-item class="form-item">
              <span slot="label">
                <span class="span-box">
                  <span>{{ $t('chart.axis_value') }}</span>
                  <a-tooltip class="item" effect="dark" placement="bottom">
                    <div slot="content" v-html="$t('chart.axis_tip')" />
                    <i class="el-icon-info" style="cursor: pointer;" />
                  </a-tooltip>
                </span>
              </span>
              <a-checkbox v-model="axisForm.axisValue.auto" @change="changeXAxisStyle('axisValue')">{{
                $t('chart.axis_auto') }}</a-checkbox>
            </a-form-model-item>
            <span v-show="!axisForm.axisValue.auto">
              <a-form-model-item :label="$t('chart.axis_value_min')" class="form-item">
                <a-input v-model="axisForm.axisValue.min" @blur="changeXAxisStyle('axisValue')" />
              </a-form-model-item>
              <a-form-model-item :label="$t('chart.axis_value_max')" class="form-item">
                <a-input v-model="axisForm.axisValue.max" @blur="changeXAxisStyle('axisValue')" />
              </a-form-model-item>
              <a-form-model-item :label="$t('chart.axis_value_split')" class="form-item">
                <span slot="label">
                  <span class="span-box">
                    <span>{{ $t('chart.axis_value_split_space') }}</span>
                    <a-tooltip class="item" effect="dark" placement="bottom">
                      <div slot="content">
                        间隔表示两个刻度之间的单位长度。
                      </div>
                      <i class="el-icon-info" style="cursor: pointer;" />
                    </a-tooltip>
                  </span>
                </span>
                <a-input v-model="axisForm.axisValue.split" @blur="changeXAxisStyle('axisValue')" />
              </a-form-model-item>
            </span>
          </span>
          <a-divider v-if="showProperty('splitLine')" />
          <a-form-model-item v-show="showProperty('splitLine')" :label="$t('chart.axis_show')" class="form-item">
            <a-checkbox v-model="axisForm.axisLine.show" @change="changeXAxisStyle('axisLine')">{{ $t('chart.axis_show')
            }}</a-checkbox>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('splitLine')" :label="$t('chart.grid_show')" class="form-item">
            <a-checkbox v-model="axisForm.splitLine.show" @change="changeXAxisStyle('splitLine')">{{
              $t('chart.grid_show') }}</a-checkbox>
          </a-form-model-item>
          <span v-show="showProperty('splitLine') && axisForm.splitLine.show">
            <a-form-model-item :label="$t('chart.grid_color')" class="form-item">
              <vue-color-picker v-model="axisForm.splitLine.lineStyle.color" class="vue-color-picker"
                :predefine="predefineColors" @change="changeXAxisStyle('splitLine')" />
            </a-form-model-item>
            <a-form-model-item :label="$t('chart.grid_width')" class="form-item form-item-slider">
              <a-slider v-model="axisForm.splitLine.lineStyle.width" :min="1" :max="10" show-input
                :show-input-controls="false" input-size="mini" @change="changeXAxisStyle('splitLine')" />
            </a-form-model-item>
            <a-form-model-item :label="$t('chart.grid_type')" class="form-item">
              <a-radio-group v-model="axisForm.splitLine.lineStyle.type" size="mini"
                @change="changeXAxisStyle('splitLine')">
                <a-radio-button value="solid">{{ $t('chart.axis_type_solid') }}</a-radio-button>
                <a-radio-button value="dashed">{{ $t('chart.axis_type_dashed') }}</a-radio-button>
                <a-radio-button value="dotted">{{ $t('chart.axis_type_dotted') }}</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
          </span>
          <a-divider v-if="showProperty('axisLabel')" />
          <a-form-model-item v-show="showProperty('axisLabel')" :label="$t('chart.axis_label_show')" class="form-item">
            <a-checkbox v-model="axisForm.axisLabel.show" @change="changeXAxisStyle('axisLabel')">{{
              $t('chart.axis_label_show') }}</a-checkbox>
          </a-form-model-item>
          <span v-show="showProperty('axisLabel') && axisForm.axisLabel.show">
            <a-form-model-item :label="$t('chart.axis_label_color')" class="form-item">
              <vue-color-picker v-model="axisForm.axisLabel.color" class="vue-color-picker" :predefine="predefineColors"
                @change="changeXAxisStyle('axisLabel')" />
            </a-form-model-item>
            <a-form-model-item :label="$t('chart.axis_label_rotate')" class="form-item form-item-slider">
              <a-slider v-model="axisForm.axisLabel.rotate" show-input :show-input-controls="false" :min="-90" :max="90"
                input-size="mini" @change="changeXAxisStyle('axisLabel')" />
            </a-form-model-item>
            <a-form-model-item :label="$t('chart.axis_label_fontsize')" class="form-item">
              <a-select v-model="axisForm.axisLabel.fontSize" :placeholder="$t('chart.axis_label_fontsize')"
                dropdownClassName="props-dropdown" @change="changeXAxisStyle('axisLabel')">
                <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name"
                  :value="option.value">{{ option.name }}</a-select-option>
              </a-select>
            </a-form-model-item>

            <span v-show="chart.type && chart.type.includes('horizontal')">
              <a-form-model-item :label="$t('chart.value_formatter_type')" class="form-item">
                <a-select v-model="axisForm.axisLabelFormatter.type" @change="changeXAxisStyle('axisLabelFormatter')"
                  dropdownClassName="props-dropdown">
                  <a-select-option v-for="type in typeList" :key="type.value" :title="$t('chart.' + type.name)"
                    :value="type.value">{{ $t('chart.' + type.name) }}</a-select-option>
                </a-select>
              </a-form-model-item>

              <a-form-model-item v-show="axisForm.axisLabelFormatter.type !== 'auto'"
                :label="$t('chart.value_formatter_decimal_count')" class="form-item">
                <a-input-number v-model="axisForm.axisLabelFormatter.decimalCount" :precision="0" :min="0" :max="10"
                  size="mini" @change="changeXAxisStyle('axisLabelFormatter')" />
              </a-form-model-item>

              <a-form-model-item v-show="axisForm.axisLabelFormatter.type !== 'percent'"
                :label="$t('chart.value_formatter_unit')" class="form-item">
                <a-select v-model="axisForm.axisLabelFormatter.unit" :placeholder="$t('chart.pls_select_field')"
                  dropdownClassName="props-dropdown" size="mini" @change="changeXAxisStyle('axisLabelFormatter')">
                  <a-select-option v-for="item in unitList" :key="item.value" :title="$t('chart.' + item.name)"
                    :value="item.value">{{ $t('chart.' + item.name) }}</a-select-option>
                </a-select>
              </a-form-model-item>

              <a-form-model-item :label="$t('chart.value_formatter_suffix')" class="form-item">
                <a-input v-model="axisForm.axisLabelFormatter.suffix" size="mini" clearable
                  :placeholder="$t('commons.input_content')" @change="changeXAxisStyle('axisLabelFormatter')" />
              </a-form-model-item>

              <a-form-model-item :label="$t('chart.value_formatter_thousand_separator')" class="form-item">
                <a-checkbox v-model="axisForm.axisLabelFormatter.thousandSeparator"
                  @change="changeXAxisStyle('axisLabelFormatter')" />
              </a-form-model-item>
            </span>
          </span>
          <a-divider v-if="showProperty('axisLabel')" />
          <a-form-model-item v-show="showProperty('axisLabel')" :label="$t('chart.content_formatter')" class="form-item">
            <a-input v-model="axisForm.axisLabel.formatter" type="textarea" :autosize="{ minRows: 4, maxRows: 4 }"
              @blur="changeXAxisStyle('axisLabel')" />
          </a-form-model-item>
        </div>
      </a-form-model>
    </a-col>
  </div>
</template>

<script>
import { COLOR_PANEL, DEFAULT_XAXIS_STYLE } from '@/pages/smart-page/util/chart'
import { formatterType, unitList } from '@/pages/smart-page/util/formatter'

export default {
  name: 'XAxisSelector',
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
      axisForm: JSON.parse(JSON.stringify(DEFAULT_XAXIS_STYLE)),
      isSetting: false,
      fontSize: [],
      predefineColors: COLOR_PANEL,
      typeList: formatterType,
      unitList: unitList
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
        if (customStyle.xAxis) {
          this.axisForm = customStyle.xAxis
          if (!this.axisForm.splitLine) {
            this.axisForm.splitLine = JSON.parse(JSON.stringify(DEFAULT_XAXIS_STYLE.splitLine))
          }
          if (!this.axisForm.nameTextStyle) {
            this.axisForm.nameTextStyle = JSON.parse(JSON.stringify(DEFAULT_XAXIS_STYLE.nameTextStyle))
          }
          if (!this.axisForm.axisValue) {
            this.axisForm.axisValue = JSON.parse(JSON.stringify(DEFAULT_XAXIS_STYLE.axisValue))
          }
          if (!this.axisForm.axisLabelFormatter) {
            this.axisForm.axisLabelFormatter = JSON.parse(JSON.stringify(DEFAULT_XAXIS_STYLE.axisLabelFormatter))
          }
          if (!this.axisForm.axisLine) {
            this.axisForm.axisLine = JSON.parse(JSON.stringify(DEFAULT_XAXIS_STYLE.axisLine))
          }
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
    changeXAxisStyle(modifyName) {
      if (!this.axisForm.show) {
        this.isSetting = false
      }
      this.axisForm['modifyName'] = modifyName
      this.$emit('onChangeXAxisForm', this.axisForm)
    },
    showProperty(property) {
      return this.propertyInner.includes(property)
    }
  }
}
</script>

<style scoped>
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
