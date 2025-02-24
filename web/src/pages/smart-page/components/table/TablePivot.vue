<template>
  <div ref="tableContainer" :style="bg_class" style="padding: 8px;width: 100%;height: 100%;overflow: hidden;">
    <TitleComponent ref="title" :component="componentData" :readOnly="readOnly" @change="onTitleChange" />
    <a-row style="height: 100%;display: flex;flex-direction: column;" :style="cssVars">
      <div style="height:calc(100%);flex: 1 1 auto;">
        <vxe-grid ref="xtable" class="table-class" :border="true" resizable show-overflow show-header-overflow
          :show-footer="totalCfg?.row?.showGrandTotals" height="auto" :columns="tableColumns" :data="data"
          :style="tableStyle" :column-config="{ resizable: true }" :header-cell-style="table_header_class"
          :cell-style="table_item_class" :footer-cell-style="table_header_class" :header-align="tableHeaderAlign"
          :align="tableItemAlign" :span-method="mergeRowMethod" :footer-align="tableItemAlign"
          :footerMethod="footerMethod" :footer-span-method="footerColspanMethod">

        </vxe-grid>
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
  name: 'TablePivot',
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
    showSummary: {
      type: Boolean,
      required: false,
      default: true
    },
    enableScroll: {
      type: Boolean,
      required: false,
      default: true
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      chart: undefined,
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
        'background-color': '##f8f8f8',
        height: '36px'
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
      rowFields: [],
      tableColumns: [],
      data: [],
      totalCfg: {},
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
        const attr = JSON.parse(this.chart.customAttr)
        this.currentPage.page = 1
        this.currentPage.pageSize = parseInt(attr.size.tablePageSize ? attr.size.tablePageSize : 20)
      },
      deep: true,
      immediate: false,
    },
  },
  mounted() {
    this.chart = deepCopy(this.componentData)
    const attr = JSON.parse(this.chart.customAttr)
    this.currentPage.page = 1
    this.currentPage.pageSize = parseInt(attr.size.tablePageSize ? attr.size.tablePageSize : 20)
  },
  beforeDestroy() {
  },
  methods: {
    onComponentDataReload() {
      this.loadData()
    },
    loadData() {
      this.calcHeightDelay()

      const customAttr = JSON.parse(this.chart.customAttr) || {}
      this.totalCfg = customAttr.totalCfg || {}
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
        totalConfig: customAttr.totalCfg,
      }
      const data = {
        config: JSON.stringify(data_config),
        params: this.params
      }
      if (data_config.datasetId && data_config.xaxis && data_config.xaxis.length > 0) {
        getData(this.pageId, this.chart.id, data).then(resp => {
          this.chart.data = JSON.parse(resp) || []
          this.init()
        })
      } else {
        this.chart.data = {
          data: [],
          tableColumns: [],
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
      if (this.chart.data) {
        this.data = this.chart.data.data || []
        this.rowFields = JSON.parse(this.chart.xaxisExt) || []
        this.columnFields = this.chart.data.tableColumns

        this.tableColumns = this.rowFields.concat(this.chart.data.tableColumns)

        console.log(this.data, this.tableColumns)

      } else {
        this.data = []
        this.tableColumns = []
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
          // 表格总计与分页颜色，取标题颜色
          this.totalStyle.color = customStyle.text.color
        }
        if (customStyle.background) {
          this.bg_class.background = hexColorToRGBA(customStyle.background.color, customStyle.background.alpha)
        }
      }
    },
    mergeRowMethod({ row, _rowIndex, column, visibleData }) {
      const fields = []
      for (let i = 0; i < this.rowFields.length; i++) {
        const field = this.tableColumns[i]
        if (!field.children) {
          fields.push(field.field)
        }
      }

      const cellValue = row[column.field]
      if (cellValue && fields.includes(column.field)) {
        const prevRow = visibleData[_rowIndex - 1]
        let nextRow = visibleData[_rowIndex + 1]
        if (prevRow && prevRow[column.field] === cellValue) {
          return { rowspan: 0, colspan: 0 }
        } else {
          let countRowspan = 1
          while (nextRow && nextRow[column.field] === cellValue) {
            nextRow = visibleData[++countRowspan + _rowIndex]
          }
          if (countRowspan > 1) {
            return { rowspan: countRowspan, colspan: 1 }
          }
        }
      }
    },
    aggSum(list, field) {
      let total = new Decimal(0)
      list.forEach(item => {
        if (!item['_SUB_TOTAL_ROW']) {
          total = total.add(new Decimal(item[field]))
        }
      })
      return total
    },
    aggAvg(list, field) {
      let total = new Decimal(0)
      list.forEach(item => {
        if (!item['_SUB_TOTAL_ROW']) {
          total = total.add(new Decimal(item[field]))
        }
      })
      return total.dividedBy(list.length).toDecimalPlaces(2, Decimal.ROUND_UP)
    },
    aggMax(list, field) {
      let total = new Decimal(list[0][field])
      list.forEach(item => {
        if (!item['_SUB_TOTAL_ROW']) {
          total = Decimal.max(total, new Decimal(item[field]))
        }
      })
      return total
    },
    aggMin(list, field) {
      let total = new Decimal(list[0][field])
      list.forEach(item => {
        if (!item['_SUB_TOTAL_ROW']) {
          total = Decimal.min(total, new Decimal(item[field]))
        }
      })
      return total
    },
    footerMethod({ columns, data }) {
      const sum = []
      if (!this.totalCfg.col.showGrandTotals) {
        return [[]]
      }
      const _that = this
      columns.forEach((column, columnIndex) => {
        if (columnIndex == 0) {
          sum.push(_that.totalCfg.col.label)
        } else if (columnIndex < this.rowFields.length) {
          sum.push("")
        } else {
          if (column?.type == 'NUM') {
            const aggregation = _that.totalCfg.col.calcTotals.aggregation
            if (aggregation === 'SUM') {
              sum.push(this.aggSum(data, column.field))
            } else if (aggregation === 'AVG') {
              sum.push(this.aggAvg(data, column.field))
            } else if (aggregation === 'MAX') {
              sum.push(this.aggMax(data, column.field))
            } else if (aggregation === 'MIN') {
              sum.push(this.aggMin(data, column.field))
            }

          } else {
            sum.push('')
          }
        }
      })
      return [sum]
    },
    footerColspanMethod({ _columnIndex }) {
      if (_columnIndex === 0) {
        return {
          rowspan: 1,
          colspan: this.rowFields.length
        }
      } else if (_columnIndex < this.rowFields.length) {
        return {
          rowspan: 0,
          colspan: 0
        }
      }
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
}
</style>
