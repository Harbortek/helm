<template>
  <div style="width: 100%">
    <a-col style="flex:1 1;">
      <a-form-model ref="colorForm" :model="colorForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <a-form-model-item :label="$t('chart.color')" class="form-item">
          <vue-color-picker v-model="colorForm.color" class="color-picker-style" :predefine="predefineColors"
            @change="changeBackgroundStyle" />
        </a-form-model-item>
        <a-form-model-item :label="$t('chart.not_alpha')" class="form-item form-item-slider">
          <a-slider v-model="colorForm.alpha" show-input :show-input-controls="false" input-size="mini"
            @change="changeBackgroundStyle" />
        </a-form-model-item>

        <a-form-model-item :label="$t('chart.border_radius')" class="form-item form-item-slider">
          <a-slider v-model="colorForm.borderRadius" show-input :show-input-controls="false" input-size="mini"
            @change="changeBackgroundStyle" />
        </a-form-model-item>
      </a-form-model>
    </a-col>
  </div>
</template>

<script>
import { COLOR_PANEL, DEFAULT_BACKGROUND_COLOR } from '@/pages/smart-page/util/chart'

export default {
  name: 'BackgroundColorSelector',
  props: {
    param: {
      type: Object,
      required: false
    },
    chart: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      colorForm: JSON.parse(JSON.stringify(DEFAULT_BACKGROUND_COLOR)),
      predefineColors: COLOR_PANEL
    }
  },
  watch: {
    'chart': {
      handler: function () {
        this.init()
      }
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    changeBackgroundStyle() {
      this.$emit('onChangeBackgroundForm', this.colorForm)
    },
    init() {
      const chart = JSON.parse(JSON.stringify(this.chart))
      if (chart.customStyle) {
        let customStyle = null
        if (Object.prototype.toString.call(chart.customStyle) === '[object Object]') {
          customStyle = JSON.parse(JSON.stringify(chart.customStyle))
        } else {
          customStyle = JSON.parse(chart.customStyle)
        }
        if (customStyle.background) {
          this.colorForm = customStyle.background
        }
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

.color-picker-style {
  cursor: pointer;
  z-index: 1003;
}
</style>
