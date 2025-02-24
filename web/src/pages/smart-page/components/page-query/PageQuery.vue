<template>
    <div class="page-query-bar">
        <div :style="containerStyle">
            <div v-for="(field, index) in queryFields" :key="field.id"
                style="display: flex;flex-direction: row;margin-top: 10px;">
                <span style="margin-left: 5px;margin-right: 5px;margin-top: 1px;text-wrap-mode: nowrap;">{{ field.title }}:</span>

                <text-query-component
                    v-if="field.searchConfig.renderType === 'TEXT' && field.searchConfig.inputTextConfig.conditionMethod === 'SINGLE'"
                    :field="field" :pageId="pageId" @change="e => onChange(index, e)" @ready="e => onReady(index, e)" />
                <text-multiple-query-component
                    v-if="field.searchConfig.renderType === 'TEXT' && field.searchConfig.inputTextConfig.conditionMethod !== 'SINGLE'"
                    :field="field" :pageId="pageId" @change="e => onChange(index, e)" @ready="e => onReady(index, e)" />
                <number-query-component
                    v-if="field.searchConfig.renderType === 'INPUT_NUMBER' && field.searchConfig.inputNumberConfig.conditionMethod === 'SINGLE'"
                    :field="field" :pageId="pageId" @change="e => onChange(index, e)" @ready="e => onReady(index, e)" />
                <number-range-query-component
                    v-if="field.searchConfig.renderType === 'INPUT_NUMBER' && field.searchConfig.inputNumberConfig.conditionMethod !== 'SINGLE'"
                    :field="field" :pageId="pageId" @change="e => onChange(index, e)" @ready="e => onReady(index, e)" />
                <date-query-component
                    v-if="field.searchConfig.renderType === 'DATE_PICKER' && field.searchConfig.datePickerConfig.conditionMethod === 'SINGLE_DATE'"
                    :field="field" :pageId="pageId" @change="e => onChange(index, e)" @ready="e => onReady(index, e)" />
                <date-range-query-component
                    v-if="field.searchConfig.renderType === 'DATE_PICKER' && field.searchConfig.datePickerConfig.conditionMethod === 'DATE_RANGE'"
                    :field="field" :pageId="pageId" @change="e => onChange(index, e)" @ready="e => onReady(index, e)" />

                <dropdown-query-component v-if="field.searchConfig.renderType === 'DROPDOWN'" :field="field"
                    :pageId="pageId" @change="e => onChange(index, e)" @ready="e => onReady(index, e)" />
            </div>
            <div style="width: 150px;margin-top: 10px;margin-left: 10px;">
                <a-space>
                    <a-button type="primary" icon="search" size="small" @click="onQuery">查询
                    </a-button>
                    <a-button type="default" size="small" @click="onReset">重置
                    </a-button>
                </a-space>
            </div>
        </div>
    </div>
</template>

<script>
import moment from "moment";
import { getDefaultValue, getDayStart, getDayEnd } from '@/utils/DateUtils'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import { isValidJson } from '@/pages/smart-page/util/util'
import { CHART_FONT_FAMILY, CHART_FONT_LETTER_SPACE, COLOR_PANEL, DEFAULT_FORM_STYLE } from '@/pages/smart-page/util/chart'
import XEUtils from 'xe-utils'
import DropdownQueryComponent from "./DropdownQueryComponent.vue";
import DateQueryComponent from "./DateQueryComponent.vue";
import DateRangeQueryComponent from "./DateRangeQueryComponent.vue";
import TextQueryComponent from "./TextQueryComponent.vue";
import TextMultipleQueryComponent from "./TextMultipleQueryComponent.vue";
import NumberQueryComponent from "./NumberQueryComponent.vue";
import NumberRangeQueryComponent from "./NumberRangeQueryComponent.vue";

export default {
    name: 'PageQuery',
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
        canvasStyleData: {
            type: Object,
            required: true
        }
    },
    components: {
        DropdownQueryComponent,
        DateQueryComponent,
        DateRangeQueryComponent,
        TextQueryComponent,
        TextMultipleQueryComponent,
        NumberQueryComponent,
        NumberRangeQueryComponent,
    },
    data() {
        return {
            customStyle: {
                form: DEFAULT_FORM_STYLE,
            },
            containerStyle: {
                display: 'flex',
                flexDirection: 'row',
            },
            parameterList: [],
            queryFields: [],
            dataFilters: [],
        }
    },
    watch: {
        componentData: {
            handler(newVal, oldVla) {
                this.customStyle = JSON.parse(newVal.customStyle) || {}
                if (!this.customStyle.form) {
                    this.customStyle.form = DEFAULT_FORM_STYLE
                }
                this.containerStyle.flexDirection = this.customStyle.form.layout === 'horizontal' ? 'row' : 'column'
                if (isValidJson(newVal.fields)) {
                    this.queryFields = JSON.parse(newVal.fields) || []
                }

                this.loadData()
            },
            deep: true,
            immediate: true,
        },
    },
    computed: {
        formItemLayout() {
            const { layout, labelCol, wrapperCol } = this.customStyle.form;
            return layout === 'horizontal'
                ? {
                    labelCol: { span: labelCol },
                    wrapperCol: { span: wrapperCol },
                }
                : {};
        },
        formItemColSpan() {
            const { layout } = this.customStyle.form;
            const columnCount = XEUtils.toNumber(this.customStyle.form.columnCount) || 2

            return layout === 'horizontal' || layout === 'inline' ? 24 / columnCount : '24';
        }
    },
    mounted() {
    },
    methods: {
        loadData() {
            this.dataFilters = []
            this.queryFields.forEach((f, idx) => {
                this.dataFilters.push({
                    field: f.field,
                    type: f.type,
                    datasetId: f.datasetId,
                    autoConfig: f.autoConfig,
                    linkConfig: f.linkConfig,
                    ready: false,
                })
            })
            console.log('init dataFilters', this.dataFilters)
        },
        isValidQueryCondition(dataFilter) {
            if (dataFilter.filterMethod === 'SINGLE_DATE') {
                if (dataFilter.conditions.length > 0) {
                    if (dataFilter.conditions[0].matchValue && dataFilter.conditions[0].matchValue.length > 0) {
                        return true
                    }
                }
            } else if (dataFilter.filterMethod === 'DATE_RANGE') {
                if (dataFilter.conditions.length > 0 && dataFilter.conditions.length <= 1) {
                    if (dataFilter.conditions[0].matchValue && dataFilter.conditions[0].matchValue.length > 0) {
                        return true
                    }
                } else if (dataFilter.conditions.length > 1) {
                    if (dataFilter.conditions[0].matchValue && dataFilter.conditions[0].matchValue.length > 0 &&
                        dataFilter.conditions[1].matchValue && dataFilter.conditions[1].matchValue.length > 0) {
                        return true
                    }
                }
            }else if (dataFilter.filterMethod === 'CONDITION') {
                if (dataFilter.conditionMethod === 'SINGLE') {
                    if (dataFilter.conditions.length > 0) {
                        //1文本类型 不是UNDEFINED和空字符串                        
                        if (XEUtils.isString(dataFilter.conditions[0].matchValue) &&  dataFilter.conditions[0].matchValue && dataFilter.conditions[0].matchValue.length > 0) {
                            return true
                        }
                        //2数字类型 不是UNDEFINED和0
                        else if (XEUtils.isNumber(dataFilter.conditions[0].matchValue) &&  dataFilter.conditions[0].matchValue ) {
                            return true
                        }
                        
                    }
                } else if (dataFilter.conditionMethod === 'AND' || dataFilter.conditionMethod === 'OR') {
                    // if (dataFilter.conditions.length > 0 && dataFilter.conditions.length <= 1) {
                    //     if (dataFilter.conditions[0].matchValue && dataFilter.conditions[0].matchValue.length > 0) {
                    //         return true
                    //     }
                    // } else if (dataFilter.conditions.length > 1) {
                    //     if (dataFilter.conditions[0].matchValue && dataFilter.conditions[0].matchValue.length > 0 &&
                    //         dataFilter.conditions[1].matchValue && dataFilter.conditions[1].matchValue.length > 0) {
                    //         return true
                    //     }
                    // }
                    return true
                }
            }

            
            return false
        },
        emitQuery() {
            let allReady = true
            this.dataFilters.forEach((f, idx) => {
                if (!f.ready) {
                    allReady = false
                    return
                }
            })
            if (!allReady) {
                return
            }
            let dataFilters = []
            this.dataFilters.forEach((f, idx) => {
                if (this.isValidQueryCondition(f)) {
                    dataFilters.push(f)
                }
            })

            this.$nextTick(() => {
                this.$emit('pageQuery', dataFilters)
            })
        },
        onReady(index, dataFilter) {
            this.dataFilters[index].filterMethod = dataFilter.filterMethod
            this.dataFilters[index].conditionMethod = dataFilter.conditionMethod
            this.dataFilters[index].queryMethod = dataFilter.queryMethod
            this.dataFilters[index].rangeType = dataFilter.rangeType
            this.dataFilters[index].conditions = dataFilter.conditions
            this.dataFilters[index].ready = true
            this.emitQuery()
        },
        onChange(index, dataFilter) {
            this.dataFilters[index].filterMethod = dataFilter.filterMethod
            this.dataFilters[index].conditionMethod = dataFilter.conditionMethod
            this.dataFilters[index].queryMethod = dataFilter.queryMethod
            this.dataFilters[index].rangeType = dataFilter.rangeType
            this.dataFilters[index].conditions = dataFilter.conditions
        },
        onQuery() {
            this.emitQuery()
        },
        onReset() {
            this.loadData()
        }

    },
}
</script>

<style lang="less" scoped>
/deep/.ant-form-item {
    margin-bottom: 0px;
}

.page-query-bar {
    width: 100%;
}
</style>