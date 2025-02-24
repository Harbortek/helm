<template>
    <span style="position: relative;display: inline-block;width:100%;">

        <a-icon type="delete" style="position: absolute;top: 6px;right: 24px;color: #878d9f;cursor: pointer;z-index: 1;"
            @click="removeItem" />

        <a-dropdown :trigger="['click']" size="mini" :overlayStyle="{ 'min-width': '200px', left: '20px' }">

            <span class="ant-dropdown-link">
                <a-tag size="small" class="item-axis" color="blue">
                    <span style="float: left">
                        <a-icon component="field_type_text" style="color: #222222;" v-show="item.type === 'TEXT'" />
                        <a-icon component="date" style="color:cadetblue;" v-show="item.type === 'DATE'" />
                        <a-icon component="field_type_number" style="color: cadetblue;" v-show="item.type === 'NUM'" />
                    </span>
                    <span class="item-span-style" :title="item.name">{{ item.name }}</span>

                </a-tag>
                <a class="ant-dropdown-link" @click="e => e.preventDefault()"
                    style="position: absolute;top: 4px;right: 6px;">
                    <a-icon type="down" />
                </a>
            </span>

            <a-menu slot="overlay" @click="clickItem">
                <a-menu-item icon="el-icon-files" key="edit" style="font-size:12px;">
                    <span>{{ $t('chart.edit') }}...</span>
                </a-menu-item>
                <a-menu-item icon="el-icon-delete" divided key="remove" style="font-size:12px;">
                    <span>{{ $t('chart.delete') }}</span>
                </a-menu-item>
            </a-menu>
        </a-dropdown>
    </span>
</template>

<script>
import { TYPE_CONFIGS } from '@/pages/smart-page/components/componentRegistry'

export default {
    name: 'QueryItem',
    components: {},
    props: {
        pageQueryComponent: {
            type: Object,
            required: true
        },
        item: {
            type: Object,
            required: true
        },
        index: {
            type: Number,
            required: true
        },
        datasets: {
            type: Array,
            required: true
        },
        charts: {
            type: Array,
            required: true
        },
    },
    data() {
        return {
            componentTypes: [...TYPE_CONFIGS],
            queryItem: {},
            tagType: 'success'
        }
    },
    watch: {
        item: {
            handler(newVal, oldVal) {
                this.queryItem = newVal
                if (!this.queryItem.searchConfig || !this.queryItem.linkConfig) {
                    this.prepareDefaultQueryConfig(this.queryItem)
                    this.item.index = this.index
                    this.$emit('onQueryItemChange', this.item)
                }
            },
            deep: true,
            immediate: true
        }
    },
    mounted() {
    },
    beforeDestroy() {
    },
    methods: {
        prepareDefaultQueryConfig(queryField) {
            if (queryField.type === 'TEXT') {
                queryField.autoConfig = 'AUTO'
                queryField.searchConfig = {
                    renderType: 'DROPDOWN',
                    dropdownConfig: {
                        dataSource: 'DATASET',
                        datasetId: queryField.datasetId,
                        searchField: queryField.field,
                        displayField: queryField.field,
                        sortField: '',
                        sortDirection: '',
                        searchType: 'SINGLE',
                        searchTrigger: 'ON_CLICK',
                        enableDefault: false,
                        defaultConfig: {
                            defaultValue: ''
                        }    
                    }                    
                }
            } else if (queryField.type === 'NUM') {
                queryField.autoConfig = 'AUTO'
                queryField.searchConfig = {
                    renderType: 'INPUT_NUMBER',
                    inputNumberConfig: {
                        aggregationType: "SUM", // 聚合类型 SUM, AVG, MAX, MIN, COUNT,
                        conditionMethod: "SINGLE", // 条件形式  SINGLE，AND, OR
                        enableDefault: false, // 是否启用默认值
                        defaultConfig: {
                        },
                    },
                }
            } else if (queryField.type === 'DATE') {
                queryField.autoConfig = 'AUTO'
                queryField.searchConfig = {
                    renderType: 'DATE_PICKER',
                    datePickerConfig: {
                        dateFormat: "YYYY-MM-DD", // 时间粒度
                        dataSource: "DEFAULT", // 数据源类型 DEFAULT,DATASET
                        searchType: "SINGLE", // 搜索类型 SINGLE, RANGE
                        rangeType: "", // 范围类型 START_WITH, END_WITH, BETWEEN,QUICK
                        quickConfig: [],
                        enableDefault: false, // 是否启用默认值
                        defaultConfig: {
                        },    
                    }                    
                }
            }
            this.prepareDefaultLinkage(queryField)
        },
        prepareDefaultLinkage(queryField) {
            queryField.linkConfig = []
            for (let i = 0; i < this.charts.length - 1; i++) {
                let chart = this.charts[i]
                let chartType = this.getChartTypeConfig(chart.type)

                if (chartType && chartType.category !== 'chart.chart_type_others') {
                    let linkage = {
                        chartId: chart.id,
                        chartName: chart.title,
                        chartIcon: this.getChartIcon(chart.type),
                        datasetId: chart.datasetId,
                        datasetName: this.getDatasetName(chart.datasetId),
                        linkageField: this.getSameNameFieldFromAnotherDataset(chart.datasetId, queryField.field),
                    }
                    linkage.selected = linkage.linkageField !== ''
                    queryField.linkConfig.push(linkage)
                }
            }
        },
        getSameNameFieldFromAnotherDataset(datasetId, field) {
            if (datasetId && this.datasets && this.datasets.length > 0) {
                let filterd = this.datasets.filter(item => { return item.id === datasetId })
                if (filterd.length > 0) {
                    let fields = filterd.fields || []
                    let matchedField = fields.filter(f => { return f.field === field })
                    if (matchedField.length > 0) {
                        return matchedField[0]
                    }
                }
            }
            return ''
        },
        getChartTypeConfig(chartType) {
            for (let i = 0; i < this.componentTypes.length - 1; i++) {
                if (this.componentTypes[i].value === chartType) {
                    return this.componentTypes[i]
                }
            }
        },
        getChartIcon(type) {
            let chartType = this.getChartTypeConfig(type)
            return chartType ? chartType.icon : ''
        },
        getDatasetName(datasetId) {
            if (datasetId && this.datasets && this.datasets.length > 0) {
                let filterd = this.datasets.filter(item => { return item.id === datasetId })
                let datasetName = filterd.length > 0 ? filterd[0].name : ''
                return datasetName
            }
            return ''
        },
        clickItem(e) {
            if (!e) {
                return
            }
            const command = e.key
            switch (command) {
                case 'remove':
                    this.removeItem()
                    break
                case 'edit':
                    this.editItem()
                    break
                default:
                    break
            }
        },
        editItem() {
            this.item.index = this.index
            this.$emit('editQueryItem', this.item)
        },
        removeItem() {
            this.item.index = this.index
            this.$emit('onQueryItemRemove', this.item)
        },
    }
}
</script>

<style lang="less" scoped>
.item-axis {
    padding: 1px 6px;
    margin: 0 3px 2px 3px;
    text-align: left;
    height: 24px;
    line-height: 22px;
    display: flex;
    border-radius: 4px;
    box-sizing: border-box;
    white-space: nowrap;
}

.item-axis:hover {
    background-color: #fdfdfd;
    cursor: pointer;
}

span {
    font-size: 12px;
}

.summary-span {
    margin-left: 4px;
    color: #878d9f;
    position: absolute;
    right: 25px;
}

.inner-dropdown-menu {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%
}

.item-span-style {
    display: inline-block;
    width: 100px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
}

.summary-span-item {
    margin-left: 4px;
    color: #878d9f;
}
</style>
