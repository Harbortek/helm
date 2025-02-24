<template>
    <div :style="{width: componentWidth}">
        <span v-if="field.searchConfig.datePickerConfig.rangeType === 'START_WITH'">开始于</span>

        <span v-if="field.searchConfig.datePickerConfig.rangeType === 'END_WITH'">结束于</span>

        <a-date-picker
            v-if="(field.searchConfig.datePickerConfig.rangeType === 'START_WITH' || field.searchConfig.datePickerConfig.rangeType === 'END_WITH')"
            size="small" v-model="dataFilter.conditions[0].matchValue" @change="onChange"
            :format="field.searchConfig.datePickerConfig.dateFormat" value-format="YYYY-MM-DD HH:mm:ss" />


        <a-select size="small" v-model="quickDate" @change="quickDateChange()"
            v-if="field.searchConfig.datePickerConfig.rangeType === 'QUICK'" :dropdownMatchSelectWidth="false"
            style="width: 100px;margin-right: 5px;">
            <a-select-option v-for="item in quickDateConfig(field.searchConfig.datePickerConfig.quickConfig)"
                :key="item.name" :value="item.name">{{
                item.title
                }}</a-select-option>
        </a-select>

        <a-range-picker size="small" v-model="dateRange" @change="onRangeChange" style="width: 250px;"
            v-if=" (field.searchConfig.datePickerConfig.rangeType==='BETWEEN' ||
            field.searchConfig.datePickerConfig.rangeType==='QUICK' )"
            :format="field.searchConfig.datePickerConfig.dateFormat" value-format="YYYY-MM-DD HH:mm:ss" />
    </div>
</template>

<script>
import moment from 'moment';
import { getDefaultValue, getDayStart, getDayEnd } from '@/utils/DateUtils'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'

export default {
    name: 'DateRangeQueryComponent',
    props: {
        pageId: {
            required: true
        },
        field: {
            type: Object,
            required: true
        },
    },
    data() {
        return {
            dataFilter: {
                filterMethod: 'DATE_RANGE',
                rangeType: 'BOTH',
                conditions: [{
                    matchMethod: 'GREATER_THAN_OR_EQUAL',
                    matchValue: ''
                }, {
                    matchMethod: 'LESS_THAN_OR_EQUAL',
                    matchValue: ''
                }]
            },
            dateRange: [],
            quickDate:'',
        }
    },
    computed: {
        componentWidth() {
            if (this.field.searchConfig.datePickerConfig.rangeType === 'START_WITH' || this.field.searchConfig.datePickerConfig.rangeType === 'END_WITH') {
                return '250px'
            }else if (this.field.searchConfig.datePickerConfig.rangeType === 'QUICK') {
                return '360px'
            }else {
                return '250px'
            }
        }
    },
    watch: {
        field: {
            handler(v) {
                console.log(v)
                const config = v.searchConfig.datePickerConfig
                if (config.rangeType === 'START_WITH') {
                    this.dataFilter.rangeType = 'START'
                    this.dataFilter.conditions = [{
                        matchMethod: 'GREATER_THAN_OR_EQUAL',
                        matchValue: config.enableDefault ? getDayStart(this.buildDefaultDate(config.defaultConfig.defaultValue[0])).format('YYYY-MM-DD HH:mm:ss') : ''
                    }]
                } else if (v.searchConfig.datePickerConfig.rangeType === 'END_WITH') {
                    this.dataFilter.rangeType = 'END'
                    this.dataFilter.conditions = [{
                        matchMethod: 'LESS_THAN_OR_EQUAL',
                        matchValue: config.enableDefault ? getDayEnd(this.buildDefaultDate(config.defaultConfig.defaultValue[1])).format('YYYY-MM-DD HH:mm:ss') : ''
                    }]
                } else if (v.searchConfig.datePickerConfig.rangeType === 'BETWEEN') {
                    this.dataFilter.rangeType = 'BOTH'
                    this.dataFilter.conditions = [{
                        matchMethod: 'GREATER_THAN_OR_EQUAL',
                        matchValue: config.enableDefault ? getDayStart(this.buildDefaultDate(config.defaultConfig.defaultValue[0])).format('YYYY-MM-DD HH:mm:ss') : ''
                    }, {
                        matchMethod: 'LESS_THAN_OR_EQUAL',
                        matchValue: config.enableDefault ? getDayEnd(this.buildDefaultDate(config.defaultConfig.defaultValue[1])).format('YYYY-MM-DD HH:mm:ss') : ''
                        }]
                    this.dateRange = [this.dataFilter.conditions[0].matchValue, this.dataFilter.conditions[1].matchValue]
                } else if (v.searchConfig.datePickerConfig.rangeType === 'QUICK') {
                    this.dataFilter.rangeType = 'BOTH'
                    this.dataFilter.conditions = [{
                        matchMethod: 'GREATER_THAN_OR_EQUAL',
                        matchValue: ''
                    }, {
                        matchMethod: 'LESS_THAN_OR_EQUAL',
                        matchValue: ''
                    }]
                    if (config.enableDefault) {
                        this.quickDate = config.defaultConfig.defaultValue
                        this.quickDateChange()
                    } else {
                        this.quickDate = ''
                        this.dateRange = [this.dataFilter.conditions[0].matchValue, this.dataFilter.conditions[1].matchValue]
                    }
                }
                this.$emit('ready', this.dataFilter)
            },
            immediate: true
        }
    },
    methods: {
        buildDefaultDate(condition) {
            if (condition.relative) {
                if ("DAY" === condition.relativeUnit) {
                    if ("+" === condition.relativeDirection) {
                        return new moment().add(condition.relativeValue, 'days')
                    } else {
                        return new moment().subtract(condition.relativeValue, 'days')
                    }
                } else if ("MONTH" === condition.relativeUnit) {
                    if ("+" === condition.relativeDirection) {
                        return new moment().add(condition.relativeValue, 'months')
                    } else {
                        return new moment().subtract(condition.relativeValue, 'months')
                    }
                } else if ("YEAR" === condition.relativeUnit) {
                    if ("+" === condition.relativeDirection) {
                        return new moment().add(condition.relativeValue, 'years')
                    } else {
                        return new moment().subtract(condition.relativeValue, 'years')
                    }
                }
                return new moment();
            } else {
                return new moment(condition.matchValue);
            }
        },
        quickDateConfig(quickConfig) {
            let config = []
            if (quickConfig && quickConfig.length > 0) {
                quickConfig.forEach(item => {
                    config.push({
                        name: item,
                        title: this.$t('chart.' + item),
                    })
                })
            }
            return config
        },
        quickDateChange() {
            const val = this.quickDate
            let value = []
            if (val === 'TODAY') {
                value = [getDayStart(getDefaultValue('TODAY')).format('YYYY-MM-DD HH:mm:ss'), getDayEnd(getDefaultValue('TODAY')).format('YYYY-MM-DD HH:mm:ss')]
            } else if (val === 'YESTERDAY') {
                value = [getDayStart(getDefaultValue('YESTERDAY')).format('YYYY-MM-DD HH:mm:ss'), getDayEnd(getDefaultValue('YESTERDAY')).format('YYYY-MM-DD HH:mm:ss')]
            } else if (val === 'LAST_7_DAYS') {
                value = [getDayStart(moment().subtract(7,'days')).format('YYYY-MM-DD HH:mm:ss'), getDayEnd(getDefaultValue('TODAY')).format('YYYY-MM-DD HH:mm:ss')]
            } else if (val === 'LAST_30_DAYS') {
                value = [getDayStart(moment().subtract(30, 'days')).format('YYYY-MM-DD HH:mm:ss'), getDayEnd(getDefaultValue('TODAY')).format('YYYY-MM-DD HH:mm:ss')]
            } else if (val === 'LAST_60_DAYS') {
                value = [getDayStart(moment().subtract(60, 'days')).format('YYYY-MM-DD HH:mm:ss'), getDayEnd(getDefaultValue('TODAY')).format('YYYY-MM-DD HH:mm:ss')]
            } else if (val === 'LAST_90_DAYS') {
                value = [getDayStart(moment().subtract(90, 'days')).format('YYYY-MM-DD HH:mm:ss'), getDayEnd(getDefaultValue('TODAY')).format('YYYY-MM-DD HH:mm:ss')]
            } else if (val === 'THIS_MONTH') {
                value = [getDayStart(moment().startOf('month')).format('YYYY-MM-DD HH:mm:ss'), getDayEnd(getDefaultValue('TODAY')).format('YYYY-MM-DD HH:mm:ss')]
            } else if (val === 'LAST_MONTH') {
                value = [getDayStart(moment().month(-1).startOf("month")).format('YYYY-MM-DD HH:mm:ss'), getDayEnd(moment().month(-1).endOf('month')).format('YYYY-MM-DD HH:mm:ss')]
            } else if (val === 'THIS_YEAR') {
                value = [getDayStart(moment().startOf('year')).format('YYYY-MM-DD HH:mm:ss'), getDayEnd(getDefaultValue('TODAY')).format('YYYY-MM-DD HH:mm:ss')]
            }
            this.dateRange = value
            this.onRangeChange()
        },
        onChange() {
            this.$emit('change', this.dataFilter)
        },
        onRangeChange() {
            this.dataFilter.conditions[0].matchValue = this.dateRange[0]
            this.dataFilter.conditions[1].matchValue = this.dateRange[1]
            this.$emit('change', this.dataFilter)
        }
    }
}
</script>

<style></style>