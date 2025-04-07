<template>
    <div ref="tableContainer" :style="bg_class" style="padding: 8px;width: 100%;height: 100%;overflow: hidden;">
        <TitleComponent ref="title" :component="componentData" :readOnly="readOnly" @change="onTitleChange" />
        <a-row style="display: flex;flex-direction: column;" :style="cssVars">
            <div style="flex: 1 1 auto;">
                <vxe-toolbar>
                    <template #buttons>
                        <a-form ref="searchForm" layout="inline">
                            <a-form-item label="目标版本">
                                <TargetVersionSelect v-model="targetVersionId" :projectId="projectId"
                                    :selectFirst="true" style="width: 200px;" @change="loadData" />
                            </a-form-item>
                            <a-form-item label="目标版本">
                                <a-select v-model="showType" style="width: 200px;" @change="loadData">
                                    <a-select-option value="SHOW_ALL">显示全部</a-select-option>
                                    <a-select-option value="SHOW_LINKS">仅显示有追溯关系的工作项</a-select-option>
                                    <a-select-option value="SHOW_PROBLEMS">仅显示无追溯关系的工作项</a-select-option>
                                </a-select>
                            </a-form-item>
                        </a-form>
                    </template>
                </vxe-toolbar>
                <vxe-table ref="xtable" class="table-class" :border="true" resizable show-header-overflow height="auto"
                    :data="tableData" :style="tableStyle" :column-config="{ resizable: true }"
                    :header-cell-style="table_header_class" :cell-style="table_item_class"
                    :footer-cell-style="table_header_class" :header-align="tableHeaderAlign" :align="tableItemAlign">
                    <vxe-column field="mainTrackerTitle" type="expand" :title="mainTrackerTitle">
                        <template #default="{ row }">
                            <div class="tracker-container">
                                <div class="tracker-no">
                                    <ItemNo :trackerItem="row" />
                                </div>
                                <div class="tracker-title" :title="row.name">
                                    <span>{{ row.name }}</span>
                                </div>
                            </div>
                        </template>
                        <template #content="{ row: parentRow, rowIndex: parentRowIndex }">
                            <vxe-table :data="parentRow.children" :show-header="false" size="mini" :min-height="58"
                                :row-config="{ keyField: 'id', isCurrent: true }">
                                <vxe-column field="mainTrackerTitle" :title="mainTrackerTitle">                                    
                                </vxe-column>
                                <vxe-column field="linkTracker" :title="linkTrackerTitle">
                                    <template #default="{ row }">
                                        <div class="tracker-container">
                                            <div class="tracker-no">
                                                <ItemNo :trackerItem="row" />
                                            </div>
                                            <div class="tracker-title" :title="row.name">
                                                <span>{{ row.name }}</span>
                                            </div>
                                        </div>
                                    </template>
                                </vxe-column>
                                <vxe-column field="secondLinkTracker" :title="secondLinkTrackerTitle">
                                    <template #default="{ row }">
                                        <div style="display: flex;flex-direction: column;" v-for="subItem in row.childnren" :key="subItem.id">
                                            <div class="tracker-no">
                                                <ItemNo :trackerItem="subItem" />
                                            </div>
                                            <div class="tracker-title" :title="subItem.name">
                                                <span>{{ subItem.name }}</span>
                                            </div>
                                        </div>
                                    </template>
                                </vxe-column>
                            </vxe-table>
                        </template>
                    </vxe-column>
                    <vxe-column field="linkTracker"  :title="linkTrackerTitle">
                    </vxe-column>
                    <vxe-column field="secondLinkTracker"  :title="secondLinkTrackerTitle">                        
                    </vxe-column>
                    <!-- <vxe-column field="priority" title="优先级" width="100" header-align="center">
                        <template #default="{ row }">
                            <a-tag style="border:none"
                                :style="{ color: row.priority?.color, backgroundColor: row.priority?.backgroundColor }">{{
                                    row.priority?.name }}</a-tag>
                        </template>
                    </vxe-column>
                    <vxe-column field="status" title="状态" width="100" header-align="center">
                        <template #default="{ row }">
                            <div style="cursor: pointer" class="transition-status">
                                <span class="ui-tag-status"
                                    :style="{ color: row.status?.meaning?.color, 'border-color': row.status?.meaning?.color }">{{
                                        row.status?.name }}</span>
                            </div>
                        </template>
                    </vxe-column>
                    <vxe-column field="severity" title="严重级别" width="100" header-align="center">
                        <template #default="{ row }">
                            <a-tag
                                :style="{ color: row.severity?.color, backgroundColor: row.severity?.backgroundColor }">{{ row.severity?.name }}</a-tag>
                        </template>
                    </vxe-column>
                    <vxe-column field="owner" title="负责人" width="100" header-align="center">
                        <template #default="{ row }">
                            <h-avatar :name="row.owner.name" :icon="row.owner.icon"></h-avatar>
                        </template>
                    </vxe-column> -->
                </vxe-table>
            </div>
        </a-row>
    </div>
</template>

<script>
import { hexColorToRGBA, getBindingParameterValue } from '@/pages/smart-page/util/util'
import { DEFAULT_COLOR_CASE, DEFAULT_SCROLL, DEFAULT_SIZE, NOT_SUPPORT_PAGE_DATASET } from '@/pages/smart-page/util/chart'
import { getData } from '@/services/smart-page/PageComponentService'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import XEUtils from 'xe-utils'
import TitleComponent from '../title/TitleComponent.vue'
import TargetVersionSelect from '@/components/select/TargetVersionSelect.vue'
import ItemNo from '@/components/table/itemNo/ItemNo.vue'
import HAvatar from '@/components/avatar/h-avatar.vue';

export default {
    name: 'TraceabilityTable',
    components: {
        TitleComponent, TargetVersionSelect, ItemNo, HAvatar
    },
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
            required: true,
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
            tableData: [],
            keyMap: {},
            mainTrackerTitle: '主工作项',
            linkTrackerTitle: '关联工作项',
            secondLinkTrackerTitle: '二级关联工作项',
            targetVersionId: '',
            showType: 'SHOW_ALL',
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
        },
        projectId() {
            return this.$route.params.projectId
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
    methods: {
        onComponentDataReload() {
            this.loadData()
        },
        loadData() {
            this.calcHeightDelay()

            const customAttr = JSON.parse(this.chart.customAttr)
            this.mainTrackerTitle = this.chart.mainTrackerTitle || '主工作项'
            this.linkTrackerTitle = this.chart.linkTrackerTitle || '关联工作项'
            this.secondLinkTrackerTitle = this.chart.secondLinkTrackerTitle || '二级关联工作项'
            const bindingParameters = this.chart.bindingParameters || []

            const data_config = {
                type: this.chart.type,
                projectId: this.projectId,
                targetVersionId: this.targetVersionId,
                mainTrackerId: this.chart.mainTrackerId,
                linkTrackerId: this.chart.linkTrackerId,
                secondLinkTrackerId: this.chart.secondLinkTrackerId,
                linkTypeId: this.chart.linkTypeId,
                secondLinkTypeId: this.chart.secondLinkTypeId,
                showType: this.showType,
            }
            const data = {
                config: JSON.stringify(data_config),
                params: this.params
            }
            console.log('reload component' + this.chart.title + ' data', data)
            if (data_config.targetVersionId && data_config.mainTrackerId && data_config.linkTrackerId && data_config.linkTypeId) {
                getData(this.pageId, this.chart.id, data).then(resp => {
                    this.chart.data = JSON.parse(resp) || []
                    console.log(this.chart.data)
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
                this.tableData = this.chart.data.data || []
                // const treeData = XEUtils.toArrayTree(this.data)
                // this.toColTreeData(treeData)

            } else {
                this.tableData = []
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
        // mergeRowMethod({ row, _rowIndex, column, visibleData }) {
        //     const fields = []
        //     for (let i = 0; i < this.rowFields.length; i++) {
        //         const field = this.tableColumns[i]
        //         if (!field.children) {
        //             fields.push(field.field)
        //         }
        //     }

        //     const cellValue = row[column.field]
        //     if (cellValue && fields.includes(column.field)) {
        //         const prevRow = visibleData[_rowIndex - 1]
        //         let nextRow = visibleData[_rowIndex + 1]
        //         if (prevRow && prevRow[column.field] === cellValue) {
        //             return { rowspan: 0, colspan: 0 }
        //         } else {
        //             let countRowspan = 1
        //             while (nextRow && nextRow[column.field] === cellValue) {
        //                 nextRow = visibleData[++countRowspan + _rowIndex]
        //             }
        //             if (countRowspan > 1) {
        //                 return { rowspan: countRowspan, colspan: 1 }
        //             }
        //         }
        //     }
        // },
        // toColTreeData(treeData) {
        //     const options = { children: 'children' }
        //     const list = []
        //     const keyMap = {}
        //     XEUtils.eachTree(treeData, (item, index, result, paths, parent) => {
        //         keyMap[item.id] = item
        //         item.keys = parent ? parent.keys.concat([item.id]) : [item.id]
        //         if (!item.children || !item.children.length) {
        //             const row = {}
        //             item.keys.forEach((key, index) => {
        //                 const level = index + 1
        //                 const obj = keyMap[key]
        //                 row[`check${level}`] = false
        //                 row[`id${level}`] = obj.id
        //                 row[`itemNo${level}`] = obj.itemNo
        //                 row[`name${level}`] = obj.name
        //                 row[`icon${level}`] = obj.icon
        //             })
        //             list.push(row)
        //         }
        //     }, options)
        //     this.keyMap = keyMap
        //     this.tableData = list
        //     console.log(this.tableData)
        // },
        // // 通用行合并函数（将相同多列数据合并为一行）
        // rowspanMethod({ row, _rowIndex, column, visibleData }) {
        //     const fields = ['name1', 'name2', 'name3']
        //     const cellValue = row[column.property]
        //     if (cellValue && fields.includes(column.property)) {
        //         const prevRow = visibleData[_rowIndex - 1]
        //         let nextRow = visibleData[_rowIndex + 1]
        //         if (prevRow && prevRow[column.property] === cellValue) {
        //             return { rowspan: 0, colspan: 0 }
        //         } else {
        //             let countRowspan = 1
        //             while (nextRow && nextRow[column.property] === cellValue) {
        //                 nextRow = visibleData[++countRowspan + _rowIndex]
        //             }
        //             if (countRowspan > 1) {
        //                 return { rowspan: countRowspan, colspan: 1 }
        //             }
        //         }
        //     }
        // },
        onTitleChange(title) {
            let chart = deepCopy(this.chart)
            chart.title = title
            this.$emit('change', chart)
        },

    },
}
</script>

<style lang="less" scoped>
.tracker-container {
    display: inline-flex;
    flex-direction: row;
    width: 90%;

    .tracker-icon {
        flex: 0;
        min-width: 20px;
    }

    .tracker-no {
        margin-left: 5px;
        flex: 0;
        min-width: 60px;
    }

    .tracker-title {
        margin-left: 5px;
        flex: 1 1 auto;
        text-align: left;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

}
</style>