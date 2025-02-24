<template>
  <div style="width: 100%;">
    <a-col>
      <a-form-model ref="labelForm" :model="labelForm" label-width="80px" size="mini">
        <a-form-model-item v-show="showProperty('show')" :label="$t('chart.show')" class="form-item">
          <a-checkbox v-model="labelForm.show" @change="changeLabelAttr('show')">{{ $t('chart.show') }}</a-checkbox>
        </a-form-model-item>
        <div v-show="labelForm.show">
          <a-form-model-item v-show="showProperty('labelLine')" :label="$t('chart.pie_label_line_show')"
            class="form-item">
            <a-checkbox v-model="labelForm.labelLine.show" @change="changeLabelAttr('labelLine')">{{
              $t('chart.pie_label_line_show') }}</a-checkbox>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('fontSize')" :label="$t('chart.text_fontsize')" class="form-item">
            <a-select v-model="labelForm.fontSize" :placeholder="$t('chart.text_fontsize')" size="mini"
              @change="changeLabelAttr('fontSize')">
              <a-select-option v-for="option in fontSize" :key="option.value" :label="option.name"
                :value="option.value" />
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('color')" :label="$t('chart.text_color')" class="form-item">
            <vue-color-picker v-model="labelForm.color" class="color-picker-style" :predefine="predefineColors"
              @change="changeLabelAttr('color')" />
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('position-pie')" :label="$t('chart.label_position')" class="form-item">
            <a-select v-model="labelForm.position" :placeholder="$t('chart.label_position')"
              @change="changeLabelAttr('position')">
              <a-select-option v-for="option in labelPositionPie" :key="option.value" :label="option.name"
                :value="option.value" />
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('position-h')" :label="$t('chart.label_position')" class="form-item">
            <a-select v-model="labelForm.position" :placeholder="$t('chart.label_position')"
              @change="changeLabelAttr('position')">
              <a-select-option v-for="option in labelPositionH" :key="option.value" :label="option.name"
                :value="option.value" />
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('position-v')" :label="$t('chart.label_position')" class="form-item">
            <a-select v-model="labelForm.position" :placeholder="$t('chart.label_position')"
              @change="changeLabelAttr('position')">
              <a-select-option v-for="option in labelPositionV" :key="option.value" :label="option.name"
                :value="option.value" />
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('label-bg')" :label="$t('chart.label_bg')" class="form-item">
            <vue-color-picker v-model="labelForm.bgColor" class="color-picker-style" :predefine="predefineColors"
              @change="changeLabelAttr('bgColor')" />
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('label-show-shadow')" :label="$t('chart.label_shadow')"
            class="form-item">
            <a-checkbox v-model="labelForm.showShadow" @change="changeLabelAttr('showShadow')">{{ $t('chart.show')
            }}</a-checkbox>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('label-shadow-color') && labelForm.showShadow"
            :label="$t('chart.label_shadow_color')" class="form-item">
            <vue-color-picker v-model="labelForm.shadowColor" class="color-picker-style" :predefine="predefineColors"
              @change="changeLabelAttr('shadowColor')" />
          </a-form-model-item>

          <a-form-model-item v-show="showProperty('formatter')" class="form-item">
            <span slot="label">
              <span class="span-box">
                <span>{{ $t('chart.content_formatter') }}</span>
                <a-tooltip class="item" effect="dark" placement="bottom">
                  <div slot="content">
                    字符串模板 模板变量有：<br>{a}：系列名。<br>{b}：数据名。<br>{c}：数据值。<br>{d}：百分比（用于饼图等）。
                  </div>
                  <i class="el-icon-info" style="cursor: pointer;" />
                </a-tooltip>
              </span>
            </span>
            <a-input v-model="labelForm.formatter" type="textarea" :autosize="{ minRows: 4, maxRows: 4 }"
              @blur="changeLabelAttr('formatter')" />
          </a-form-model-item>
        </div>
        <a-form-model-item v-show="showProperty('gaugeFormatter')" class="form-item">
          <span slot="label">
            <span class="span-box">
              <span>{{ $t('chart.content_formatter') }}</span>
            </span>
          </span>
          <a-input v-model="labelForm.gaugeFormatter" type="textarea" :autosize="{ minRows: 4, maxRows: 4 }"
            @blur="changeLabelAttr('gaugeFormatter')" />
        </a-form-model-item>
      </a-form-model>
    </a-col>
  </div>
</template>

<script>
import { COLOR_PANEL, DEFAULT_LABEL } from '@/pages/smart-page/util/chart'

export default {
  name: 'LabelSelector',
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
      labelForm: JSON.parse(JSON.stringify(DEFAULT_LABEL)),
      fontSize: [],
      isSetting: false,
      labelPosition: [],
      labelPositionPie: [
        { name: this.$t('chart.inside'), value: 'inside' },
        { name: this.$t('chart.outside'), value: 'outside' }
      ],
      labelPositionH: [
        { name: this.$t('chart.text_pos_left'), value: 'left' },
        { name: this.$t('chart.center'), value: 'inside' },
        { name: this.$t('chart.text_pos_right'), value: 'right' }
      ],
      labelPositionV: [
        { name: this.$t('chart.text_pos_top'), value: 'top' },
        { name: this.$t('chart.center'), value: 'inside' },
        { name: this.$t('chart.text_pos_bottom'), value: 'bottom' }
      ],
      predefineColors: COLOR_PANEL
    }
  },
  watch: {
    'chart': {
      handler: function () {
        this.initOptions()
        this.initData()
      }
    }
  },
  mounted() {
    if (this.showProperty('position-pie')) {
      this.labelForm.position = 'outside'
    }
    this.init()
    this.initOptions()
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
        if (customAttr.label) {
          this.labelForm = customAttr.label
          if (!this.labelForm.labelLine) {
            this.labelForm.labelLine = JSON.parse(JSON.stringify(DEFAULT_LABEL.labelLine))
          }
        }
      }
    },
    init() {
      const arr = []
      for (let i = 10; i <= 40; i = i + 2) {
        arr.push({
          name: i + '',
          value: i + ''
        })
      }
      this.fontSize = arr
    },
    changeLabelAttr(modifyName) {
      if (!this.labelForm.show) {
        this.isSetting = false
      }
      this.labelForm['modifyName'] = modifyName
      this.$emit('onLabelChange', this.labelForm)
    },
    initOptions() {
      const type = this.chart.type
      if (type) {
        if (type.includes('horizontal') || type === 'funnel') {
          this.labelPosition = this.labelPositionH
        } else if (type.includes('pie')) {
          this.labelPosition = this.labelPositionPie
        } else {
          this.labelPosition = this.labelPositionV
        }
      }
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
