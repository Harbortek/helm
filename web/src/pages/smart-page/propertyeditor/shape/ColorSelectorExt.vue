<!--暂时没用，但保留-->
<template>
  <div style="width: 100%">
    <a-col>
      <a-form-model ref="colorForm" :model="colorForm" label-width="80px" size="mini">
        <div>
          <a-form-model-item
            v-show="chart.type && !chart.type.includes('table') && !chart.type.includes('text') && chart.type !== 'text-card'"
            :label="$t('chart.color_case')" class="form-item">
            <a-popover placement="bottom" width="400" trigger="click">
              <div style="padding: 6px 10px;">
                <div>
                  <span class="color-label">{{ $t('chart.system_case') }}</span>
                  <a-select v-model="colorForm.value" :placeholder="$t('chart.pls_slc_color_case')" size="mini"
                    @change="changeColorOption">
                    <a-select-option v-for="option in colorCases" :key="option.value" :title="option.name"
                      :value="option.value" style="display: flex;align-items: center;">
                      <div style="float: left">
                        <span v-for="(c, index) in option.colors" :key="index"
                          :style="{ width: '20px', height: '20px', float: 'left', backgroundColor: c }" />
                      </div>
                      <span style="margin-left: 4px;">{{ option.name }}</span>
                    </a-select-option>
                  </a-select>
                  <a-button size="mini" type="text" style="margin-left: 2px;" @click="resetCustomColor">
                    {{ $t('commons.reset') }}
                  </a-button>
                </div>
                <div style="display: flex;align-items: center;margin-top: 10px;">
                  <span class="color-label" />
                  <span>
                    <span v-for="(c, index) in colorForm.colors" :key="index" style="padding: 2px;">
                      <span :style="{ width: '20px', height: '20px', display: 'inline-block', backgroundColor: c }" />
                    </span>
                  </span>
                </div>
                <div class="custom-color-style">
                  <div v-for="(item, index) in colorForm.seriesColors" :key="index"
                    style="display: flex;align-items: center;margin: 2px 0;">
                    <span class="span-label" :title="item.name">{{ item.name }}</span>
                    <vue-color-picker v-model="item.color" class="color-picker-style" :predefine="predefineColors"
                      @change="switchCustomColor(index)" />
                  </div>
                </div>
              </div>

              <div slot="reference" style="cursor: pointer;margin-top: 2px;width: 180px;">
                <span v-for="(c, index) in colorForm.colors" :key="index"
                  :style="{ width: '20px', height: '20px', display: 'inline-block', backgroundColor: c }" />
              </div>
            </a-popover>
          </a-form-model-item>

          <a-form-model-item
            v-show="(chart.type && (chart.type.includes('text') || chart.type === 'text-card')) || sourceType === 'panelTable'"
            :label="$t('chart.quota_color')" class="form-item">
            <vue-color-picker v-model="colorForm.quotaColor" class="color-picker-style" :predefine="predefineColors"
              @change="changeColorCase" />
          </a-form-model-item>
          <a-form-model-item
            v-show="(chart.type && chart.type.includes('text') || chart.type === 'text-card') || sourceType === 'panelTable'"
            :label="$t('chart.dimension_color')" class="form-item">
            <vue-color-picker v-model="colorForm.dimensionColor" class="color-picker-style" :predefine="predefineColors"
              @change="changeColorCase" />
          </a-form-model-item>
        </div>

        <a-form-model-item v-show="chart.type && !chart.type.includes('text') && chart.type !== 'text-card'"
          :label="$t('chart.not_alpha')" class="form-item form-item-slider">
          <a-slider v-model="colorForm.alpha" show-input :show-input-controls="false" input-size="mini"
            @change="changeColorCase" />
        </a-form-model-item>
      </a-form-model>
    </a-col>
  </div>
</template>

<script>
import { COLOR_PANEL, DEFAULT_COLOR_CASE } from '@/pages/smart-page/util/chart'
import { getColors } from '@/pages/smart-page/util/util'

export default {
  name: 'ColorSelectorExt',
  props: {
    param: {
      type: Object,
      required: false
    },
    chart: {
      type: Object,
      required: true
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
      predefineColors: COLOR_PANEL
    }
  },
  computed: {
    panelInfo() {
      return this.$store.state.panel.panelInfo
    }
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
  },
  methods: {
    changeColorOption() {
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

      this.changeColorCase()
    },
    changeColorCase() {
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

          this.initCustomColor()
        }
      }
    },
    switchColor(index) {
      this.colorIndex = index
    },
    switchColorCase() {
      this.colorForm.colors[this.colorIndex] = this.customColor
      this.$emit('onColorChange', this.colorForm)
    },

    switchCustomColor(index) {
      this.colorForm.seriesColors[index].isCustom = true
      this.switchColorCase()
    },

    resetCustomColor() {
      this.changeColorOption()
    },

    initCustomColor(reset) {
      const chart = JSON.parse(JSON.stringify(this.chart))
      this.colorForm.seriesColors = getColors(chart, this.colorForm.colors, reset)
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
  width: 100px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
}

.custom-color-style {
  height: 300px;
  overflow-y: auto;
  padding: 4px;
  border: 1px solid #e6e6e6;
}
</style>
