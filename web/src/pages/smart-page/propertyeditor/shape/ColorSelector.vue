<template>
  <div style="width: 100%">
    <a-form-model ref="colorForm" :model="colorForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
      :wrapperCol="{ span: 16, offset: 1 }">
      <a-form-model-item v-show="showProperty('value') && showProperty('gradient-color')" :label="$t('chart.color_case')"
        class="form-item">
        <gradient-color-selector :color-dto="colorForm" @color-change="gradientColorChange" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('value') && !showProperty('gradient-color')" :label="$t('chart.color_case')"
        class="form-item">
        <a-popover placement="bottom" trigger="click">
          <template slot="content">
            <div style="padding: 6px 0px;width:400px;">
              <div>
                <span class="color-label">{{ $t('chart.system_case') }}</span>
                <a-select v-model="colorForm.value" :placeholder="$t('chart.pls_slc_color_case')"
                  :dropdownMatchSelectWidth="false" @change="changeColorOption('value')" option-label-prop="label"
                  style="width:176px;" :dropdownStyle="{ width: '250px', position: 'relative', top: '0', left: '-20px' }"
                  :getPopupContainer="(trigger) => trigger.parentNode">
                  <a-select-option v-for="option in colorCases" :key="option.value" :title="option.name"
                    :value="option.value" style="display: flex;align-items: center;">
                    <div style="float: left;margin-top: 4px;">
                      <span v-for="(c, index) in option.colors" :key="index"
                        :style="{ width: '20px', height: '20px', float: 'left', backgroundColor: c }" />
                    </div>
                    <span style="margin-left: 4px;">{{ option.name }}</span>
                  </a-select-option>
                </a-select>
                <a-button type="link" size="small" style="margin-left: 2px;" @click="resetCustomColor">{{
                  $t('commons.reset') }}
                </a-button>
              </div>
              <!--自定义配色方案-->
              <div v-show="showProperty('custom')">
                <div style="display: flex;align-items: center;margin-top: 10px;">
                  <span class="color-label">{{ $t('chart.custom_case') }}</span>
                  <span>
                    <a-radio-group v-model="customColor" class="color-type">
                      <a-radio v-for="(c, index) in colorForm.colors" :key="index" :value="c" style="padding: 2px;"
                        @change="switchColor(index)">
                        <span :style="{ width: '20px', height: '20px', display: 'inline-block', backgroundColor: c }" />
                      </a-radio>
                    </a-radio-group>
                  </span>
                </div>
                <div style="display: flex;align-items: center;margin-top: 10px;">
                  <span class="color-label" />
                  <span>
                    <vue-color-picker v-model="customColor" class="color-picker-style" :predefine="predefineColors"
                      @change="switchColorCase" />
                  </span>
                </div>
              </div>
              <!--自定义系列或维度枚举值颜色-->
              <div v-show="showProperty('colorPanel')" style="display: flex;align-items: center;margin-top: 10px;">
                <span class="color-label" />
                <span>
                  <span v-for="(c, index) in colorForm.colors" :key="index" style="padding: 2px;">
                    <span :style="{ width: '20px', height: '20px', display: 'inline-block', backgroundColor: c }" />
                  </span>
                </span>
              </div>
            </div>

            <div v-show="showProperty('customColor')" class="custom-color-style">
              <div v-for="(item, index) in colorForm.seriesColors" :key="index"
                style="display: flex;align-items: center;margin: 2px 0;">
                <vue-color-picker v-model="item.color" class="color-picker-style" :predefine="predefineColors"
                  @change="switchCustomColor(index)" />
                <span class="span-label" :title="item.name">{{ item.name }}</span>
              </div>
            </div>
          </template>
          <div style="cursor: pointer;margin-top: 4px;width: 180px;line-height: 28px;">
            <span v-for="(c, index) in colorForm.colors" :key="index"
              :style="{ width: '20px', height: '20px', display: 'inline-block', backgroundColor: c }" />
          </div>
        </a-popover>
      </a-form-model-item>

      <a-form-model-item v-show="showProperty('gradient')" :label="$t('chart.gradient')" class="form-item">
        <a-checkbox v-model="colorForm.gradient" @change="changeColorCase('gradient')" />
      </a-form-model-item>

      <a-form-model-item v-show="showProperty('quotaColor')" :label="$t('chart.quota_color')" class="form-item">
        <vue-color-picker v-model="colorForm.quotaColor" class="color-picker-style" :predefine="predefineColors"
          @change="changeColorCase('quotaColor')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('dimensionColor')" :label="$t('chart.dimension_color')" class="form-item">
        <vue-color-picker v-model="colorForm.dimensionColor" class="color-picker-style" :predefine="predefineColors"
          @change="changeColorCase('dimensionColor')" />
      </a-form-model-item>

      <a-form-model-item v-show="showProperty('tableHeaderBgColor')" :label="$t('chart.table_header_bg')"
        class="form-item">
        <vue-color-picker v-model="colorForm.tableHeaderBgColor" class="color-picker-style" :predefine="predefineColors"
          @change="changeColorCase('tableHeaderBgColor')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableItemBgColor')" :label="$t('chart.table_item_bg')" class="form-item">
        <vue-color-picker v-model="colorForm.tableItemBgColor" class="color-picker-style" :predefine="predefineColors"
          @change="changeColorCase('tableItemBgColor')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableHeaderFontColor')" :label="$t('chart.table_header_font_color')"
        class="form-item">
        <vue-color-picker v-model="colorForm.tableHeaderFontColor" class="color-picker-style" :predefine="predefineColors"
          @change="changeColorCase('tableHeaderFontColor')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableFontColor')" :label="$t('chart.table_item_font_color')"
        class="form-item">
        <vue-color-picker v-model="colorForm.tableFontColor" class="color-picker-style" :predefine="predefineColors"
          @change="changeColorCase('tableFontColor')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableBorderColor')" :label="$t('chart.table_border_color')"
        class="form-item">
        <vue-color-picker v-model="colorForm.tableBorderColor" class="color-picker-style" :predefine="predefineColors"
          @change="changeColorCase('tableBorderColor')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableScrollBarColor')" :label="$t('chart.table_scroll_bar_color')"
        class="form-item">
        <vue-color-picker v-model="colorForm.tableScrollBarColor" class="color-picker-style" :predefine="predefineColors"
          color-format="rgb" show-alpha @change="changeColorCase('tableScrollBarColor')" />
      </a-form-model-item>


      <a-form-model-item v-show="showProperty('mapStyle')" :label="$t('chart.map_style')" class="form-item">
        <a-select v-model="colorForm.mapStyle" @change="changeColorCase('mapStyle')">
          <a-select-option v-for="item in mapStyleOptions" :key="item.value" :title="item.name" :value="item.value">
            {{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('alpha')" :label="$t('chart.not_alpha')" class="form-item form-item-slider">
        <a-slider v-model="colorForm.alpha" show-input :show-input-controls="false" input-size="mini"
          @change="changeColorCase('alpha')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('mapLineGradient')" :label="$t('chart.gradient')" class="form-item">
        <a-checkbox v-model="colorForm.mapLineGradient" :disabled="checkMapLineGradient"
          @change="changeColorCase('mapLineGradient')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('mapLineSourceColor')"
        :label="colorForm.mapLineGradient ? $t('chart.map_line_color_source_color') : $t('chart.color')"
        class="form-item">
        <vue-color-picker v-model="colorForm.mapLineSourceColor" @change="changeColorCase('mapLineSourceColor')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('mapLineTargetColor')" v-if="colorForm.mapLineGradient"
        :label="$t('chart.map_line_color_target_color')" class="form-item">
        <vue-color-picker v-model="colorForm.mapLineTargetColor" @change="changeColorCase('mapLineTargetColor')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('area-border-color')" :label="$t('chart.area_border_color')"
        class="form-item">
        <vue-color-picker v-model="colorForm.areaBorderColor" class="color-picker-style" :predefine="predefineColors"
          @change="changeColorCase('areaBorderColor')" />
      </a-form-model-item>
    </a-form-model>
  </div>
</template>

<script>
import { COLOR_PANEL, DEFAULT_COLOR_CASE } from '@/pages/smart-page/util/chart'
import { getColors } from '@/pages/smart-page/util/util'
import { mapState } from 'vuex'
import GradientColorSelector from '@/components/gradientColorSelector'
import bus from '@/utils/bus'
import { equalsAny } from '@/utils/StringUtils'

export default {
  name: 'ColorSelector',
  components: { GradientColorSelector },
  props: {
    param: {
      type: Object,
      required: false
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
    },
    sourceType: {
      type: String,
      default: 'view',
      required: false
    }
  },
  data() {
    return {
      colorCases: [
        {
          name: this.$t('chart.color_default'),
          value: 'default',
          colors: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc']
        },
        {
          name: this.$t('chart.color_retro'),
          value: 'retro',
          colors: ['#0780cf', '#765005', '#fa6d1d', '#0e2c82', '#b6b51f', '#da1f18', '#701866', '#f47a75', '#009db2']
        },
        {
          name: this.$t('chart.color_elegant'),
          value: 'elegant',
          colors: ['#95a2ff', '#fa8080', '#ffc076', '#fae768', '#87e885', '#3cb9fc', '#73abf5', '#cb9bff', '#434348']
        },
        {
          name: this.$t('chart.color_future'),
          value: 'future',
          colors: ['#63b2ee', '#76da91', '#f8cb7f', '#f89588', '#7cd6cf', '#9192ab', '#7898e1', '#efa666', '#eddd86']
        },
        {
          name: this.$t('chart.color_gradual'),
          value: 'gradual',
          colors: ['#71ae46', '#96b744', '#c4cc38', '#ebe12a', '#eab026', '#e3852b', '#d85d2a', '#ce2626', '#ac2026']
        },
        {
          name: this.$t('chart.color_simple'),
          value: 'simple',
          colors: ['#929fff', '#9de0ff', '#ffa897', '#af87fe', '#7dc3fe', '#bb60b2', '#433e7c', '#f47a75', '#009db2']
        },
        {
          name: this.$t('chart.color_business'),
          value: 'business',
          colors: ['#194f97', '#555555', '#bd6b08', '#00686b', '#c82d31', '#625ba1', '#898989', '#9c9800', '#007f54']
        },
        {
          name: this.$t('chart.color_gentle'),
          value: 'gentle',
          colors: ['#5b9bd5', '#ed7d31', '#70ad47', '#ffc000', '#4472c4', '#91d024', '#b235e6', '#02ae75', '#5b9bd5']
        },
        {
          name: this.$t('chart.color_technology'),
          value: 'technology',
          colors: ['#05f8d6', '#0082fc', '#fdd845', '#22ed7c', '#09b0d3', '#1d27c9', '#f9e264', '#f47a75', '#009db2']
        },
        {
          name: this.$t('chart.color_light'),
          value: 'light',
          colors: ['#884898', '#808080', '#82ae46', '#00a3af', '#ef8b07', '#007bbb', '#9d775f', '#fae800', '#5f9b3c']
        },
        {
          name: this.$t('chart.color_classical'),
          value: 'classical',
          colors: ['#007bbb', '#ffdb4f', '#dd4b4b', '#2ca9e1', '#ef8b07', '#4a488e', '#82ae46', '#dd4b4b', '#bb9581']
        },
        {
          name: this.$t('chart.color_fresh'),
          value: 'fresh',
          colors: ['#5f9b3c', '#75c24b', '#83d65f', '#aacf53', '#c7dc68', '#d8e698', '#e0ebaf', '#bbc8e6', '#e5e5e5']
        },
        {
          name: this.$t('chart.color_energy'),
          value: 'energy',
          colors: ['#ef8b07', '#2a83a2', '#f07474', '#c55784', '#274a78', '#7058a3', '#0095d9', '#75c24b', '#808080']
        },
        {
          name: this.$t('chart.color_red'),
          value: 'red',
          colors: ['#ff0000', '#ef8b07', '#4c6cb3', '#f8e944', '#69821b', '#9c5ec3', '#00ccdf', '#f07474', '#bb9581']
        },
        {
          name: this.$t('chart.color_fast'),
          value: 'fast',
          colors: ['#fae800', '#00c039', '#0482dc', '#bb9581', '#ff7701', '#9c5ec3', '#00ccdf', '#00c039', '#ff7701']
        },
        {
          name: this.$t('chart.color_spiritual'),
          value: 'spiritual',
          colors: ['#00a3af', '#4da798', '#57baaa', '#62d0bd', '#6ee4d0', '#86e7d6', '#aeede1', '#bde1e6', '#e5e5e5']
        }
      ],
      colorForm: JSON.parse(JSON.stringify(DEFAULT_COLOR_CASE)),
      customColor: null,
      colorIndex: 0,
      predefineColors: COLOR_PANEL,
      mapStyleOptions: [
        { name: this.$t('chart.map_style_normal'), value: 'normal' },
        { name: this.$t('chart.map_style_darkblue'), value: 'darkblue' },
        { name: this.$t('chart.map_style_light'), value: 'light' },
        { name: this.$t('chart.map_style_dark'), value: 'dark' },
        { name: this.$t('chart.map_style_whitesmoke'), value: 'whitesmoke' },
        { name: this.$t('chart.map_style_fresh'), value: 'fresh' },
        { name: this.$t('chart.map_style_grey'), value: 'grey' },
        { name: this.$t('chart.map_style_graffiti'), value: 'graffiti' },
        { name: this.$t('chart.map_style_macaron'), value: 'macaron' },
        { name: this.$t('chart.map_style_blue'), value: 'blue' },
        { name: this.$t('chart.map_style_wine'), value: 'wine' }
      ]
    }
  },
  computed: {
    checkMapLineGradient() {
      const chart = this.chart
      if (chart.type === 'flow-map') {
        let customAttr = null
        if (Object.prototype.toString.call(chart.customAttr) === '[object Object]') {
          customAttr = JSON.parse(JSON.stringify(chart.customAttr))
        } else {
          customAttr = JSON.parse(chart.customAttr)
        }
        return customAttr.size.mapLineAnimate && equalsAny(customAttr.size.mapLineType, 'line', 'arc')
      }
      return false
    },
  },
  watch: {
    'chart.id': {
      handler: function () {
        this.customColor = null
        this.colorIndex = 0
      }
    },
    'chart': {
      handler: function () {
        this.init()
      }
    }
  },
  mounted() {
    this.init()
    bus.$on('prop-change-data', this.initCustomColor)
  },
  beforeDestroy() {
    bus.$off('prop-change-data', this.initCustomColor)
  },
  methods: {
    gradientColorChange(colorDto) {
      const modifyNames = ['value', 'colors', 'seriesColors']
      modifyNames.forEach(item => {
        this.colorForm['modifyName'] = item
        this.$emit('onColorChange', this.colorForm)
      })
    },
    changeColorOption(modifyName = 'value') {
      const that = this
      const items = this.colorCases.filter(ele => {
        return ele.value === that.colorForm.value
      })
      this.colorForm.colors = JSON.parse(JSON.stringify(items[0].colors))

      this.customColor = this.colorForm.colors[0]
      this.colorIndex = 0

      // reset custom color
      this.colorForm.seriesColors = []
      this.initCustomColor(true)

      this.changeColorCase(modifyName)
    },
    changeColorCase(modifyName) {
      this.colorForm['modifyName'] = modifyName
      this.$emit('onColorChange', this.colorForm)
      this.colorForm['modifyName'] = 'colors'
      this.$emit('onColorChange', this.colorForm)
    },
    init() {
      const chart = JSON.parse(JSON.stringify(this.chart))
      if (chart.customAttr) {
        let customAttr = null
        if (Object.prototype.toString.call(chart.customAttr) === '[object Object]') {
          customAttr = JSON.parse(JSON.stringify(chart.customAttr))
        } else {
          customAttr = JSON.parse(chart.customAttr)
        }
        if (customAttr.color) {
          this.colorForm = customAttr.color
          if (!this.customColor) {
            this.customColor = this.colorForm.colors[0]
            this.colorIndex = 0
          }

          this.colorForm.tableBorderColor = this.colorForm.tableBorderColor ? this.colorForm.tableBorderColor : DEFAULT_COLOR_CASE.tableBorderColor
          this.colorForm.tableHeaderFontColor = this.colorForm.tableHeaderFontColor ? this.colorForm.tableHeaderFontColor : this.colorForm.tableFontColor
          this.$set(this.colorForm, 'gradient', this.colorForm.gradient || false)
          this.colorForm.tableScrollBarColor = this.colorForm.tableScrollBarColor ? this.colorForm.tableScrollBarColor : DEFAULT_COLOR_CASE.tableScrollBarColor

          this.colorForm.mapStyle = this.colorForm.mapStyle ? this.colorForm.mapStyle : DEFAULT_COLOR_CASE.mapStyle
          this.colorForm.mapLineGradient = this.colorForm.mapLineGradient ? this.colorForm.mapLineGradient : DEFAULT_COLOR_CASE.mapLineGradient
          this.colorForm.mapLineSourceColor = this.colorForm.mapLineSourceColor ? this.colorForm.mapLineSourceColor : DEFAULT_COLOR_CASE.mapLineSourceColor
          this.colorForm.mapLineTargetColor = this.colorForm.mapLineTargetColor ? this.colorForm.mapLineTargetColor : DEFAULT_COLOR_CASE.mapLineTargetColor

          this.initCustomColor()
        }
      }
    },

    switchColor(index) {
      this.colorIndex = index
    },
    switchColorCase() {
      this.colorForm.colors[this.colorIndex] = this.customColor
      this.colorForm['modifyName'] = 'value'
      this.$emit('onColorChange', this.colorForm)
      this.colorForm['modifyName'] = 'colors'
      this.$emit('onColorChange', this.colorForm)
      this.colorForm['modifyName'] = 'seriesColors'
      this.$emit('onColorChange', this.colorForm)
    },
    resetCustomColor() {
      this.changeColorOption()
    },
    showProperty(property) {
      return this.propertyInner.includes(property)
    },

    switchCustomColor(index) {
      this.colorForm.seriesColors[index].isCustom = true
      this.switchColorCase()
    },

    initCustomColor(reset) {
      // if (!this.batchOptStatus && this.chart.render && this.chart.render === 'antv' &&
      //   (this.chart.type.includes('bar') ||
      //     this.chart.type.includes('line') ||
      //     this.chart.type.includes('area') ||
      //     this.chart.type.includes('pie') ||
      //     this.chart.type === 'funnel' ||
      //     this.chart.type === 'radar' ||
      //     this.chart.type === 'scatter')) {
      //   if (this.componentViewsData[this.chart.id]) {
      //     const chart = JSON.parse(JSON.stringify(this.componentViewsData[this.chart.id]))
      //     this.colorForm.seriesColors = getColors(chart, this.colorForm.colors, reset)
      //   }
      // }
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
  align-items: center
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

.color-label {
  display: inline-block;
  width: 60px;
}

.color-type ::v-deep .a-radio__input {
  display: none;
}

.a-radio {
  margin: 0 2px 0 0 !important;
  border: 1px solid transparent;
}

.a-radio ::v-deep .a-radio__label {
  padding-left: 0;
}

.a-radio.is-checked {
  border: 1px solid #0a7be0;
}

.span-label {
  width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
  padding: 0 8px;
}

.custom-color-style {
  height: 300px;
  overflow-y: auto;
  padding: 4px 12px;
  border: 1px solid #e6e6e6;
}
</style>
