<template>
  <div ref="tableContainer" :style="bg_class" style="padding: 8px;width: 100%;height: 100%;overflow: hidden;">
    <TitleComponent ref="title" :component="componentData" :readOnly="readOnly" @change="onTitleChange" />
    <a-row style="display: flex;flex-direction: column;" :style="cssVars">
      <div style="height:100%;flex: 1 1 auto;">
        <vxe-table ref="xtable" class="table-class" border resizable show-overflow show-header-overflow show-footer
          height="auto" :data="chart?.data?.data" :style="tableStyle" :column-config="{ resizable: true }"
          :header-cell-style="table_header_class" :cell-style="table_item_class" :footer-cell-style="table_header_class"
          :header-align="tableHeaderAlign" :align="tableItemAlign" :footer-align="tableHeaderAlign"
          :footerMethod="footerMethod">
          <vxe-column v-for="config in fields" :key="config.name" :type="config.type" :field="config.name"
            :title="config.title" :fixed="config.fixed" :width="columnWidth" :filters="config.filters">
            <template #header="{ column }">
              {{ column.title }}
            </template>
          </vxe-column>
        </vxe-table>
      </div>
    </a-row>
  </div>
</template>

<script>
import { hexColorToRGBA } from '@/pages/smart-page/util/util'
import { DEFAULT_COLOR_CASE, DEFAULT_SCROLL, DEFAULT_SIZE, NOT_SUPPORT_PAGE_DATASET } from '@/pages/smart-page/util/chart'
import { getData } from '@/services/smart-page/PageComponentService'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import Decimal from "decimal.js"
import TitleComponent from '../title/TitleComponent.vue'
export default {
  name: 'TableNormal',
  components: { TitleComponent },
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
    readOnly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      chart: { data: { data: [], fields: [], total: 0 } },
      fields: [],
      detailFields: [],
      tableHeight: 'auto',
      bg_class: {
        background: hexColorToRGBA('#ffffff', 0),
        borderRadius: this.borderRadius
      },
      table_header_class: {
        fontSize: '12px',
        color: '#606266',
        'background-color': '#e8eaec',
        height: '36px',
        padding: 0,
      },
      table_item_class: {
        fontSize: '12px',
        color: '#606266',
        'background-color': '#ffffff',
        height: '36px'
      },
      table_item_class_stripe: {
        fontSize: '12px',
        color: '#606266',
        background: '#ffffff',
        height: '36px'
      },
      // title_show: true,
      borderRadius: '0px',
      currentPage: {
        page: 1,
        pageSize: 20,
        total: 0,
      },
      showPage: false,
      columnWidth: '',//DEFAULT_SIZE.tableColumnWidth,
      scrollTop: 0,
      showIndex: false,
      indexLabel: '序号',
      scrollBarColor: DEFAULT_COLOR_CASE.tableScrollBarColor,
      scrollBarHoverColor: DEFAULT_COLOR_CASE.tableScrollBarHoverColor,
      totalStyle: {
        color: '#606266'
      },
      cssStyleParams: {
        borderColor: DEFAULT_COLOR_CASE.tableBorderColor,
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap'
      },
      tableHeaderAlign: 'left',
      tableItemAlign: 'left',
      tableWidth: '100%',
    }
  },
  computed: {
    tableStyle() {
      if (this.tableWidth === '100%') {
        return {
          width: '100%',
          '--scroll-bar-color': this.scrollBarColor
        }
      } else {
        return {
          // width: this.tableWidth,
          '--scroll-bar-color': this.scrollBarColor
        }
      }
    },
    cssVars() {
      return {
        height: this.tableHeight,
        '--color': this.cssStyleParams.borderColor,
        '--overflow': this.cssStyleParams.overflow,
        '--text-overflow': this.cssStyleParams.textOverflow,
        '--white-space': this.cssStyleParams.whiteSpace
      }
    }

  },
  watch: {
    componentData: {
      handler: function (newVal, oldVal) {
        const data = this.chart?.data || {}
        this.chart = deepCopy(newVal)
        this.chart.data = data
        this.currentPage.page = 1
      },
      deep: true,
      immediate: false,
    },
  },
  mounted() {
    this.chart = deepCopy(this.componentData)
    this.currentPage.page = 1
  },
  beforeDestroy() {
  },
  methods: {
    onComponentDataReload() {      
      this.loadData()
    },
    loadData() {
      this.calcHeightDelay()

      const data_config = {
        type: this.chart.type,
        datasetId: this.chart.datasetId,
        xaxis: JSON.parse(this.chart.xaxis),
        xaxisExt: JSON.parse(this.chart.xaxisExt),
        yaxis: JSON.parse(this.chart.yaxis),
        yaxisExt: JSON.parse(this.chart.yaxisExt),
        customFilter: JSON.parse(this.chart.customFilter),
        drillFields: JSON.parse(this.chart.drillFields),
        page: this.currentPage.page,
        pageSize: this.currentPage.pageSize,
      }
      const data = {
        config: JSON.stringify(data_config),
        params: this.params
      }
      if (data_config.datasetId && data_config.xaxis && data_config.xaxis.length > 0) {
        getData(this.pageId, this.chart.id, data).then(resp => {
          this.chart.data = JSON.parse(resp)
          console.log(this.chart)
          this.init()
        })
      } else {
        this.chart.data = {
          data: [],
          fields: [],
          total: 0,
        }
      }
    },
    init() {
      this.initData()
      this.setBackGroundBorder()
    },
    setBackGroundBorder() {
      if (this.chart.customStyle) {
        const customStyle = JSON.parse(this.chart.customStyle)
        if (customStyle.background) {
          this.borderRadius = (customStyle.background.borderRadius || 0) + 'px'
        }
      }
    },
    initData() {
      this.showPage = false

      if (this.chart.data) {

        this.fields = this.chart.data.fields
        const attr = JSON.parse(this.chart.customAttr)

        //判断是否需要分页
        this.currentPage.pageSize = parseInt(attr.size.tablePageSize ? attr.size.tablePageSize : 20)
        if (this.chart.type === 'table-info' && attr.size.tablePageMode === 'page' && this.chart.data.total > this.currentPage.pageSize) {
          this.currentPage.total = this.chart.data.total
          this.showPage = true
        }

      } else {
        this.fields = []
      }

      this.initStyle()
    },
    initStyle() {
      if (this.chart.customAttr) {
        const customAttr = JSON.parse(this.chart.customAttr)
        if (customAttr.color) {
          this.table_header_class.color = customAttr.color.tableHeaderFontColor ? customAttr.color.tableHeaderFontColor : customAttr.color.tableFontColor
          this.table_header_class['background-color'] = hexColorToRGBA(customAttr.color.tableHeaderBgColor, customAttr.color.alpha)
          this.table_item_class.color = customAttr.color.tableFontColor
          this.table_item_class['background-color'] = hexColorToRGBA(customAttr.color.tableItemBgColor, customAttr.color.alpha)
          this.scrollBarColor = customAttr.color.tableScrollBarColor ? customAttr.color.tableScrollBarColor : DEFAULT_COLOR_CASE.tableScrollBarColor
          this.cssStyleParams.borderColor = customAttr.color.tableBorderColor ? customAttr.color.tableBorderColor : DEFAULT_COLOR_CASE.tableBorderColor
        }
        if (customAttr.size) {
          this.table_header_class.fontSize = customAttr.size.tableTitleFontSize + 'px'
          this.table_item_class.fontSize = customAttr.size.tableItemFontSize + 'px'
          this.table_header_class.height = customAttr.size.tableTitleHeight + 'px'
          this.table_item_class.height = customAttr.size.tableItemHeight + 'px'

          this.tableHeaderAlign = customAttr.size.tableHeaderAlign || 'left'
          this.tableItemAlign = customAttr.size.tableItemAlign || 'left'

          // column width
          if (customAttr.size.tableColumnMode && customAttr.size.tableColumnMode === "adapt") {
            this.tableWidth = '100%'
            const containerWidth = this.$refs.tableContainer.offsetWidth
            this.columnWidth = containerWidth / this.fields.length
            this.fields.forEach(f => {
              delete f.width
            })
            this.columnWidth = ''
          } else {
            const containerWidth = this.$refs.tableContainer.offsetWidth
            const columnWidth = customAttr.size.tableColumnWidth ? customAttr.size.tableColumnWidth : this.columnWidth
            this.columnWidth = columnWidth
            this.tableWidth = containerWidth + 'px'
          }
          const columns = []
          if (customAttr.size.showIndex) {
            columns.push({
              width: 60,
              type: 'seq',
              title: customAttr.size.indexLabel,
              resizable: true,
              fixed: false,
            })
          }
          this.fields.forEach(f => {
            columns.push({
              width: this.columnWidth,
              field: f.name,
              title: f.title,
              resizable: true,
              fixed: false,
              type: f.type,
            })
          })
          this.$refs.xtable.reloadColumn(columns)

          const autoBreakLine = customAttr.size.tableAutoBreakLine ? customAttr.size.tableAutoBreakLine : DEFAULT_SIZE.tableAutoBreakLine
          if (autoBreakLine) {
            this.cssStyleParams.overflow = 'hidden'
            this.cssStyleParams.textOverflow = 'auto'
            this.cssStyleParams.whiteSpace = 'normal'
          } else {
            this.cssStyleParams.overflow = 'hidden'
            this.cssStyleParams.textOverflow = 'ellipsis'
            this.cssStyleParams.whiteSpace = 'nowrap'
          }
        }
        this.table_item_class_stripe = JSON.parse(JSON.stringify(this.table_item_class))
      }
      if (this.chart.customStyle) {
        const customStyle = JSON.parse(this.chart.customStyle)
        if (customStyle.text) {
          this.totalStyle.color = customStyle.text.color
        }
        if (customStyle.background) {
          this.bg_class.background = hexColorToRGBA(customStyle.background.color, customStyle.background.alpha)
        }
      }
    },
    sumNum(list, field) {
      let total = new Decimal(0)
      list.forEach(item => {
        if (item[field]) {
          total = total.add(new Decimal(item[field]))
        }
      })
      return total
    },
    footerMethod: function ({ columns, data }) {
      const sum = []
      columns.forEach((column, columnIndex) => {
        if (columnIndex == 0) {
          sum.push("合计")
        } else {
          if (column?.type == 'NUM') {
            sum.push(this.sumNum(data, column.field))
          } else {
            sum.push('')
          }
        }
      })
      console.log(sum)
      return [sum]
    },
    calcHeightRightNow() {
      this.$nextTick(() => {
        if (this.$refs.tableContainer) {
          const currentHeight = this.$refs.tableContainer.offsetHeight
          if (this.$refs.title) {
            const titleHeight = this.$refs.title.$el.offsetHeight
            this.tableHeight = (currentHeight - titleHeight - 20) + 'px'
            // this.$refs.chartContainer.style.height = this.chartHeight
            // console.log(this.chartHeight)
          }
        }
      })
    },
    calcHeightDelay() {
      setTimeout(() => {
        this.calcHeightRightNow()
      }, 100)
    },
    onTitleChange(title) {
      let chart = deepCopy(this.chart)
      chart.title = title
      this.$emit('change', chart)
    },
  }
}
</script>

<style scoped lang="less">
@scroll-bar-color: var(--scroll-bar-color);

.table-class {
  .vxe-table--body-wrapper {
    ::-webkit-scrollbar-thumb {
      background: @scroll-bar-color;
    }

    scrollbar-color: @scroll-bar-color transparent;
  }

  .vxe-table--render-default .vxe-footer--column:not(.col--ellipsis) {
    padding: 0;
  }

  .vxe-footer--column {
    padding: 0;
  }
}
</style>
