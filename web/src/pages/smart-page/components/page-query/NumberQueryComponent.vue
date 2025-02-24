<template>
    <div style="width: 310px;display: inline-flex;">
        <a-select v-model="dataFilter.conditions[0].matchMethod" size="small" defaultValue="EQUAL"
            style="width: 110px; margin-right: 8px;">
            <a-select-option v-for="(opt) in options" :key="opt.value" :value="opt.value">{{ opt.label
                }}</a-select-option>
        </a-select>
        <a-input-number size="small" v-model="dataFilter.conditions[0].matchValue" @change="onChange" style="width: 200px;"/>
    </div>
</template>

<script>
export default {
    name: 'NumberQueryComponent',
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
            options: [
                {
                    value: 'EQUAL',
                    label: this.$t('chart.filter_eq')
                }, {
                    value: 'NOT_EQUAL',
                    label: this.$t('chart.filter_not_eq')
                }, {
                    value: 'LESS_THAN',
                    label: this.$t('chart.filter_lt')
                }, {
                    value: 'GREATER_THAN',
                    label: this.$t('chart.filter_gt')
                }, {
                    value: 'LESS_THAN_OR_EQUAL',
                    label: this.$t('chart.filter_le')
                }, {
                    value: 'GREATER_THAN_OR_EQUAL',
                    label: this.$t('chart.filter_ge')
                }],
            dataFilter: {
                filterMethod: 'CONDITION',  
                conditionMethod: 'SINGLE',
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
                const config = v.searchConfig.inputNumberConfig
                this.dataFilter.conditionMethod =config.conditionMethod

                if (v.searchConfig.inputNumberConfig.enableDefault) {
                    this.dataFilter.conditions[0].matchMethod =config.defaultConfig.defaultValue[0].matchMethod
                    this.dataFilter.conditions[0].matchValue =config.defaultConfig.defaultValue[0].matchValue
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
        onChange() {
            this.$emit('change', this.dataFilter)
        }
    }
}
</script>

<style>

</style>