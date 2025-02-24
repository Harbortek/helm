<template>
    <div style="width: 310px;display: inline-flex;">
        <a-select v-model="dataFilter.conditions[0].matchMethod" size="small" defaultValue="STRICT_MATCH"
            style="width: 110px; margin-right: 8px;">
            <a-select-option v-for="(opt) in options" :key="opt.value" :value="opt.value">{{ opt.label
                }}</a-select-option>
        </a-select>
        <a-input size="small" v-model="dataFilter.conditions[0].matchValue" @change="onChange" style="width: 200px;" />
    </div>
</template>

<script>
export default {
    name: 'TextQueryComponent',
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
                    value: 'STRICT_MATCH',
                    label: this.$t('精确匹配')
                }, {
                    value: 'NOT_MATCH',
                    label: this.$t('不匹配')
                }, {
                    value: 'CONTAINS',
                    label: this.$t('chart.filter_like')
                }, {
                    value: 'NOT_CONTAINS',
                    label: this.$t('chart.filter_not_like')
                }, {
                    value: 'START_WITH',
                    label: this.$t('开头是')
                }, {
                    value: 'END_WITH',
                    label: this.$t('结尾是')
                }, {
                    value: 'IS_NULL',
                    label: this.$t('chart.filter_null')
                }, {
                    value: 'IS_NOT_NULL',
                    label: this.$t('chart.filter_not_null')
                }, {
                    value: 'IS_EMPTY',
                    label: this.$t('chart.filter_empty')
                }, {
                    value: 'IS_NOT_EMPTY',
                    label: this.$t('chart.filter_not_empty')
                }],
            dataFilter: {
                filterMethod: 'CONDITION',
                conditionMethod: 'SGINGLE',
                conditions: [{
                    matchMethod: 'STRICT_MATCH',
                    matchValue: ''
                }]
            }
        }
    },
    watch: {
        field: {
            handler(v) {
                const config = v.searchConfig.inputTextConfig
                this.dataFilter.conditionMethod = config.conditionMethod

                if (config.enableDefault) {
                    this.dataFilter.conditions[0].matchMethod = config.defaultConfig.defaultValue[0].matchMethod
                    this.dataFilter.conditions[0].matchValue = config.defaultConfig.defaultValue[0].matchValue
                } else {
                    this.dataFilter.conditions = [{
                        matchMethod: 'STRICT_MATCH',
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

<style></style>