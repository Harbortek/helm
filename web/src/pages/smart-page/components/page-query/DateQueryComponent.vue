<template>
    <div style="width: 200px;">
        <a-date-picker size="small" v-model="dataFilter.conditions[0].matchValue" @change="onChange" 
            :format="field.searchConfig.datePickerConfig.dateFormat" value-format="YYYY-MM-DD HH:mm:ss"/>

    </div>
</template>

<script>
import moment from 'moment';
import { getDefaultValue, getDayStart, getDayEnd } from '@/utils/DateUtils'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'

export default {
    name: 'DateQueryComponent',
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
                filterMethod: 'SINGLE_DATE',
                conditions: [{
                    matchMethod: 'EQUAL',
                    matchValue: ''
                }]
            }
        }
    },
    watch: {
        field: {
            handler(v) {
                const config = v.searchConfig.datePickerConfig
                if (config.enableDefault) {                    
                    this.dataFilter.conditions[0].matchValue = this.buildDefaultDate( config.defaultConfig.defaultValue[0]).format('YYYY-MM-DD HH:mm:ss')
                } else {
                    this.dataFilter.conditions = [{
                        matchMethod: 'EQUAL',
                        matchValue: ''
                    }]
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
                } else if ("MONTH"===condition.relativeUnit) {
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
        onChange() {
            this.$emit('change', this.dataFilter)
        }
    }
}
</script>

<style>

</style>