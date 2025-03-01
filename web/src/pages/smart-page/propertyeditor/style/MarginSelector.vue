<template>
  <div style="width: 100%">
    <a-col style="flex:1 1;">
      <a-form-model ref="marginForm" :model="marginForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 16, offset: 1 }" :rules="rules">
        <a-form-model-item v-show="showProperty('marginModel')" :label="$t('chart.margin_model')" class="form-item">
          <a-radio-group v-model="marginForm.marginModel" size="small"
            @change="changeMarginStyle(marginForm.marginModel, 'marginModel')">
            <a-radio-button value="auto">{{ $t('chart.margin_model_auto') }}</a-radio-button>
            <a-radio-button value="absolute">{{ $t('chart.margin_model_absolute') }}</a-radio-button>
            <a-radio-button value="relative">{{ $t('chart.margin_model_relative') }}</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <div v-show="showProperty('marginModel') && marginForm.marginModel !== 'auto'">
          <a-form-model-item v-show="showProperty('marginTop')" :label="$t('chart.text_pos_top')" class="form-item"
            prop="marginTop">
            <a-input v-model="marginForm.marginTop" :placeholder="placeholder" type="number" class="hide-icon-number"
              @change="changeMarginStyle(marginForm.marginTop, 'marginTop')">
              <template v-if="unitSuffix" slot="append">{{ unitSuffix }}</template>
            </a-input>
          </a-form-model-item>

          <a-form-model-item v-show="showProperty('marginBottom')" :label="$t('chart.text_pos_bottom')" class="form-item"
            prop="marginBottom">
            <a-input v-model="marginForm.marginBottom" :placeholder="placeholder" type="number" class="hide-icon-number"
              @change="changeMarginStyle(marginForm.marginBottom, 'marginBottom')">
              <template v-if="unitSuffix" slot="append">{{ unitSuffix }}</template>
            </a-input>
          </a-form-model-item>

          <a-form-model-item v-show="showProperty('marginLeft')" :label="$t('chart.text_pos_left')" class="form-item"
            prop="marginLeft">
            <a-input v-model="marginForm.marginLeft" :placeholder="placeholder" type="number" class="hide-icon-number"
              @change="changeMarginStyle(marginForm.marginLeft, 'marginLeft')">
              <template v-if="unitSuffix" slot="append">{{ unitSuffix }}</template>
            </a-input>
          </a-form-model-item>

          <a-form-model-item v-show="showProperty('marginRight')" :label="$t('chart.text_pos_right')" class="form-item"
            prop="marginRight">
            <a-input v-model="marginForm.marginRight" :placeholder="placeholder" type="number" class="hide-icon-number"
              @change="changeMarginStyle(marginForm.marginRight, 'marginRight')">
              <template v-if="unitSuffix" slot="append">{{ unitSuffix }}</template>
            </a-input>
          </a-form-model-item>

        </div>
      </a-form-model>
    </a-col>
  </div>
</template>

<script>
import { DEFAULT_MARGIN_STYLE } from '@/pages/smart-page/util/chart'
import { getMarginUnit } from '@/pages/smart-page/components/chart/common/common'
export default {
  name: 'MarginSelector',
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
      marginForm: JSON.parse(JSON.stringify(DEFAULT_MARGIN_STYLE)),
      rules: {
        marginTop: [
          { validator: this.validateMarginNumber, trigger: ['blur', 'change'] }
        ],
        marginBottom: [
          { validator: this.validateMarginNumber, trigger: ['blur', 'change'] }
        ],
        marginLeft: [
          { validator: this.validateMarginNumber, trigger: ['blur', 'change'] }
        ],
        marginRight: [
          { validator: this.validateMarginNumber, trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  computed: {
    unitSuffix() {
      return getMarginUnit(this.marginForm)
    },
    placeholder() {
      if (this.marginForm.marginModel === 'absolute') {
        return this.$t('chart.margin_placeholder')
      } else if (this.marginForm.marginModel === 'relative') {
        return this.$t('chart.margin_absolute_placeholder')
      } else {
        return null
      }
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
      if (chart.customStyle) {
        let customStyle = null
        if (Object.prototype.toString.call(chart.customStyle) === '[object Object]') {
          customStyle = JSON.parse(JSON.stringify(chart.customStyle))
        } else {
          customStyle = JSON.parse(chart.customStyle)
        }
        if (customStyle.margin) {
          this.marginForm = customStyle.margin
        } else {
          this.marginForm = JSON.parse(JSON.stringify(DEFAULT_MARGIN_STYLE))
        }
      } else {
        this.marginForm = JSON.parse(JSON.stringify(DEFAULT_MARGIN_STYLE))
      }
    },

    changeMarginStyle(value, modifyName) {
      if (modifyName === 'marginModel') {
        if (value === 'absolute') {
          this.marginForm.marginTop = JSON.parse(JSON.stringify(DEFAULT_MARGIN_STYLE)).marginTop
          this.marginForm.marginBottom = JSON.parse(JSON.stringify(DEFAULT_MARGIN_STYLE)).marginBottom
          this.marginForm.marginLeft = JSON.parse(JSON.stringify(DEFAULT_MARGIN_STYLE)).marginLeft
          this.marginForm.marginRight = JSON.parse(JSON.stringify(DEFAULT_MARGIN_STYLE)).marginRight
        }
        if (value === 'relative') {
          this.marginForm.marginTop = 15
          this.marginForm.marginBottom = 15
          this.marginForm.marginLeft = 5
          this.marginForm.marginRight = 5
        }
      }
      this.marginForm['modifyName'] = modifyName
      this.$emit('onMarginChange', this.marginForm)
    },
    showProperty(property) {
      return this.propertyInner.includes(property)
    },
    validateMarginNumber(rule, value, callBack) {
      if (value == null || value === '') {
        callBack()
        return
      }
      if (this.marginForm.marginModel === 'absolute' && (value < 0 || value > 100)) {
        callBack(new Error(this.$t('chart.margin_placeholder')))
        this.marginForm[rule.field] = 0
      } else if (this.marginForm.marginModel === 'relative' && (value < 0 || value > 40)) {
        callBack(new Error(this.$t('chart.margin_absolute_placeholder')))
        this.marginForm[rule.field] = 0
      } else {
        callBack()
      }
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

::v-deep .hide-icon-number input::-webkit-outer-spin-button,
::v-deep .hide-icon-number input::-webkit-inner-spin-button {
  -webkit-appearance: none !important;
}

::v-deep .hide-icon-number input[type="number"] {
  -moz-appearance: textfield !important;
}
</style>
