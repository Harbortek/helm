<template>
  <div ref="tableContainer" :style="bg_class"
    style="display:flex;padding: 4px;width: 100%;height: 100%;overflow: hidden;">
    <div id="label-content" :style="content_class">
      <span>
        <p ref="textData" :style="label_content_class" @click="textClick">
          {{ textValue }}
        </p>
      </span>
      <span v-show="dimensionShow" :style="label_space">
        <p :style="label_class">
          {{ textLabel }}
        </p>
      </span>
    </div>
  </div>
</template>

<script>
import { getRemark, hexColorToRGBA } from '@/pages/smart-page/util/util'
import { DEFAULT_SIZE, DEFAULT_TITLE_STYLE } from '@/pages/smart-page/util/chart'
import { getData } from '@/services/smart-page/PageComponentService'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'

export default {
  name: 'TextCard',
  components: {},
  props: {
    pageId: {
      required: true
    },
    componentData: {
      type: Object,
      required: true
    },
    params: {
      type: Array,
      required: false,
      default: function () {
        return []
      }
    },
  },
  data() {
    return {
      chart: {
        data: {
          series: []
        }
      },
      textLabel: '',
      textValue: '',
      height: 'auto',
      splitHeight: '10px',
      dimensionShow: true,
      quotaShow: true,
      title_class: {
        margin: '0 0',
        width: '100%',
        fontSize: '18px',
        color: '#303133',
        textAlign: 'left',
        fontStyle: 'normal',
        fontWeight: 'normal'
      },
      content_class: {
        display: 'flex',
        flex: 1,
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        height: 'auto'
      },
      label_class: {
        color: 'black',
        fontSize: '18px',
        fontFamily: DEFAULT_SIZE.dimensionFontFamily,
        fontWeight: 'normal',
        fontStyle: 'normal',
        letterSpacing: DEFAULT_SIZE.dimensionLetterSpace + 'px',
        textShadow: 'none',
        margin: 0
      },
      label_content_class: {
        margin: 0,
        color: 'blue',
        cursor: 'pointer',
        fontSize: '18px',
        fontFamily: DEFAULT_SIZE.dimensionFontFamily,
        fontWeight: 'normal',
        fontStyle: 'normal',
        letterSpacing: DEFAULT_SIZE.dimensionLetterSpace + 'px',
        textShadow: 'none',
      },
      label_space: {
        marginTop: '10px',
        textAlign: 'center'
      },
      bg_class: {
        background: hexColorToRGBA('#ffffff', 0)
      },
      title_show: true,
      borderRadius: '0px',
      trackBarStyle: {
        position: 'absolute',
        left: '0px',
        top: '0px'
      },
      pointParam: null,
      remarkCfg: {
        show: false,
        content: ''
      }
    }
  },
  computed: {
  },
  watch: {
    componentData: {
      handler: function (newVal, oldVal) {
        this.chart = deepCopy(newVal)
        this.loadData()
      },
      deep: true,
      immediate: true,
    }
  },
  mounted() {
    // this.init()
  },
  beforeDestroy() {
  },
  methods: {
    loadData() {
      const data_config = {
        type: this.chart.type,
        datasetId: this.chart.datasetId,
        xaxis: JSON.parse(this.chart.xaxis),
        xaxisExt: JSON.parse(this.chart.xaxisExt),
        yaxis: JSON.parse(this.chart.yaxis),
        yaxisExt: JSON.parse(this.chart.yaxisExt),
        customFilter: JSON.parse(this.chart.customFilter),
        drillFields: JSON.parse(this.chart.drillFields),
      }
      const data = {
        config: JSON.stringify(data_config),
        params: this.params
      }
      if (data_config.datasetId && data_config.xaxis && data_config.xaxis.length > 0) {
        getData(this.pageId, this.chart.id, data).then(resp => {
          this.chart.data = JSON.parse(resp)

          this.textValue = this.chart.data.series[0].data[0]
          this.textLabel = this.chart.data.series[0].name
          console.log(this.textLabel, this.textLabel)
          this.init()
        })
      } else {
        this.textValue = ''
        this.textLabel = ''
        this.chart.data = {
          series: [],
          fields: [],
        }
      }
    },
    init() {
      this.initStyle()
      this.setBackGroundBorder()
    },
    setBackGroundBorder() {
      if (this.chart.customStyle) {
        const customStyle = JSON.parse(this.chart.customStyle)
        if (customStyle.background) {
          this.borderRadius = (customStyle.background.borderRadius || 0) + 'px'
          this.bg_class.borderRadius = this.borderRadius
        }
      }
    },
    // calcHeight() {
    //   const that = this
    //   this.$nextTick(function () {
    //     if (that.$refs.tableContainer) {
    //       const currentHeight = that.$refs.tableContainer.offsetHeight
    //       const contentHeight = currentHeight - that.$refs.title.offsetHeight - 8
    //       that.height = contentHeight + 'px'
    //       that.content_class.height = that.height
    //     }
    //   })
    // },
    initStyle() {
      if (this.chart.customAttr) {
        const customAttr = JSON.parse(this.chart.customAttr)
        console.log(customAttr)
        if (customAttr.color) {
          this.label_class.color = customAttr.color.dimensionColor
          // color threshold
          this.colorThreshold(customAttr.color.quotaColor)
        }
        if (customAttr.size) {
          this.dimensionShow = customAttr.size.dimensionShow
          this.quotaShow = customAttr.size.quotaShow

          this.label_class.fontSize = customAttr.size.dimensionFontSize + 'px'
          this.label_class.fontFamily = customAttr.size.dimensionFontFamily ? customAttr.size.dimensionFontFamily : DEFAULT_SIZE.dimensionFontFamily
          this.label_class.fontWeight = customAttr.size.dimensionFontIsBolder ? 'bold' : 'normal'
          this.label_class.fontStyle = customAttr.size.dimensionFontIsItalic ? 'italic' : 'normal'
          this.label_class.letterSpacing = (customAttr.size.dimensionLetterSpace ? customAttr.size.dimensionLetterSpace : DEFAULT_SIZE.dimensionLetterSpace) + 'px'
          this.label_class.textShadow = customAttr.size.dimensionFontShadow ? '2px 2px 4px' : 'none'

          this.label_content_class.fontSize = customAttr.size.quotaFontSize + 'px'
          this.label_content_class.fontFamily = customAttr.size.quotaFontFamily ? customAttr.size.quotaFontFamily : DEFAULT_SIZE.quotaFontFamily
          this.label_content_class.fontWeight = customAttr.size.quotaFontIsBolder ? 'bold' : 'normal'
          this.label_content_class.fontStyle = customAttr.size.quotaFontIsItalic ? 'italic' : 'normal'
          this.label_content_class.letterSpacing = (customAttr.size.quotaLetterSpace ? customAttr.size.quotaLetterSpace : DEFAULT_SIZE.quotaLetterSpace) + 'px'
          this.label_content_class.textShadow = customAttr.size.quotaFontShadow ? '2px 2px 4px' : 'none'

          this.content_class.alignItems = customAttr.size.hPosition ? customAttr.size.hPosition : DEFAULT_SIZE.hPosition
          this.content_class.justifyContent = customAttr.size.vPosition ? customAttr.size.vPosition : DEFAULT_SIZE.vPosition

          if (!this.dimensionShow) {
            this.label_space.marginTop = '0px'
          } else {
            this.label_space.marginTop = customAttr.size.spaceSplit + 'px'
          }
        }
      }
      if (this.chart.customStyle) {
        const customStyle = JSON.parse(this.chart.customStyle)
        if (customStyle.text) {
          this.title_show = customStyle.text.show
          this.title_class.fontSize = customStyle.text.fontSize + 'px'
          this.title_class.color = customStyle.text.color
          this.title_class.textAlign = customStyle.text.hPosition
          this.title_class.fontStyle = customStyle.text.isItalic ? 'italic' : 'normal'
          this.title_class.fontWeight = customStyle.text.isBolder ? 'bold' : 'normal'

          this.title_class.fontFamily = customStyle.text.fontFamily ? customStyle.text.fontFamily : DEFAULT_TITLE_STYLE.fontFamily
          this.title_class.letterSpacing = (customStyle.text.letterSpace ? customStyle.text.letterSpace : DEFAULT_TITLE_STYLE.letterSpace) + 'px'
          this.title_class.textShadow = customStyle.text.fontShadow ? '2px 2px 4px' : 'none'
        }
        if (customStyle.background) {
          this.bg_class.background = hexColorToRGBA(customStyle.background.color, customStyle.background.alpha)
        }
      }

      console.log(this.label_class)
    },
    // chartResize() {
    //   // 指定图表的配置项和数据
    //   this.calcHeight()
    // },
    // trackClick(trackAction) {
    //   const param = this.pointParam
    //   if (!param || !param.data || !param.data.dimensionList) {
    //     // 地图提示没有关联字段 其他没有维度信息的 直接返回
    //     if (this.chart.type === 'map') {
    //       this.$warning(this.$t('panel.no_drill_field'))
    //     }
    //     return
    //   }
    //   const linkageParam = {
    //     option: 'linkage',
    //     viewId: this.chart.id,
    //     dimensionList: this.pointParam.data.dimensionList,
    //     quotaList: this.pointParam.data.quotaList
    //   }
    //   const jumpParam = {
    //     option: 'jump',
    //     viewId: this.chart.id,
    //     dimensionList: this.pointParam.data.dimensionList,
    //     quotaList: this.pointParam.data.quotaList
    //   }
    //   switch (trackAction) {
    //     case 'drill':
    //       this.$emit('onChartClick', this.pointParam)
    //       break
    //     case 'linkage':
    //       this.$store.commit('addViewTrackFilter', linkageParam)
    //       break
    //     case 'jump':
    //       this.$emit('onJumpClick', jumpParam)
    //       break
    //     default:
    //       break
    //   }
    // },
    textClick() {
      this.pointParam = {
        data: {
          category: this.chart.data.series[0].name,
          dimensionList: [{ id: this.chart.data.fields[0].id, value: this.chart.data.series[0].data[0] }],
          field: this.chart.data.series[0].data[0],
          name: this.chart.data.series[0].data[0],
          popSize: 0,
          quotaList: [],
          value: 0
        }
      }
      this.$refs['textData'].offsetTop
      if (this.trackMenu.length < 2) { // 只有一个事件直接调用
        this.trackClick(this.trackMenu[0])
      } else { // 视图关联多个事件
        this.trackBarStyle.left = (this.$refs['textData'].offsetLeft + this.$refs['textData'].offsetWidth / 2) + 'px'
        this.trackBarStyle.top = (this.$refs['textData'].offsetTop + this.$refs['textData'].offsetHeight + 10) + 'px'
        this.$refs.viewTrack.trackButtonClick()
      }
    },
    // initRemark() {
    //   this.remarkCfg = getRemark(this.chart)
    // },
    colorThreshold(valueColor) {
      if (this.chart.senior) {
        const senior = JSON.parse(this.chart.senior)
        if (senior.threshold && senior.threshold.textLabelThreshold && senior.threshold.textLabelThreshold.length > 0) {
          const value = this.chart.data.series[0].data[0]
          for (let i = 0; i < senior.threshold.textLabelThreshold.length; i++) {
            let flag = false
            const t = senior.threshold.textLabelThreshold[i]
            const tv = t.value
            if (t.term === 'eq') {
              if (value === tv) {
                this.label_content_class.color = t.color
                flag = true
              }
            } else if (t.term === 'not_eq') {
              if (value !== tv) {
                this.label_content_class.color = t.color
                flag = true
              }
            } else if (t.term === 'like') {
              if (value.includes(tv)) {
                this.label_content_class.color = t.color
                flag = true
              }
            } else if (t.term === 'not like') {
              if (!value.includes(tv)) {
                this.label_content_class.color = t.color
                flag = true
              }
            } else if (t.term === 'null') {
              if (value === null || value === undefined || value === '') {
                this.label_content_class.color = t.color
                flag = true
              }
            } else if (t.term === 'not_null') {
              if (value !== null && value !== undefined && value !== '') {
                this.label_content_class.color = t.color
                flag = true
              }
            }
            if (flag) {
              break
            } else if (i === senior.threshold.textLabelThreshold.length - 1) {
              this.label_content_class.color = valueColor
            }
          }
        } else {
          this.label_content_class.color = valueColor
        }
      }
    }
  }
}
</script>

<style scoped>
.table-class ::v-deep .body--wrapper {
  background: rgba(1, 1, 1, 0);
}
</style>
