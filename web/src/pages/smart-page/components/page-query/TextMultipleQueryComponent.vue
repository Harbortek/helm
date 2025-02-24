<template>
    <div :style="{ width: componentWidth }">
        <a-select v-model="dataFilter.conditions[0].matchMethod" size="small" defaultValue="STRICT_MATCH"
            style="width: 100px; margin-right: 8px;">
            <a-select-option v-for="(opt) in options" :key="opt.value" :value="opt.value">{{ opt.label
                }}</a-select-option>
        </a-select>
        <a-input size="small" v-model="dataFilter.conditions[0].matchValue" @change="onChange" style="width: 100px;" />

        <span v-if="dataFilter.conditionMethod === 'AND'" style="margin-left: 5px;margin-right: 5px;">并且</span>
        <span v-if="dataFilter.conditionMethod === 'OR'" style="margin-left: 5px;margin-right: 5px;">或者</span>
        <template v-if="dataFilter.conditionMethod !== 'SINGLE'">
            <a-select v-model="dataFilter.conditions[1].matchMethod" size="small" defaultValue="STRICT_MATCH"
                style="width: 100px; margin-right: 8px;">
                <a-select-option v-for="(opt) in options" :key="opt.value" :value="opt.value">{{ opt.label
                    }}</a-select-option>
            </a-select>
            <a-input size="small" v-model="dataFilter.conditions[1].matchValue" @change="onChange"
                style="width: 100px;" />
        </template>
    </div>
</template>

<script>
export default {
    name: 'TextMultipleQueryComponent',
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
                conditionMethod: 'AND',
                conditions: [{
                    matchMethod: 'STRICT_MATCH',
                    matchValue: ''
                }, {
                    matchMethod: 'STRICT_MATCH',
                    matchValue: ''
                }]
            }
        }
    },
    computed: {
        componentWidth() {
            const config = this.field.searchConfig.inputTextConfig
            if (config.conditionMethod === 'AND' || config.conditionMethod === 'OR') {
                return '470px'
            } else {
                return '200px'
            }
        }
    },
    watch: {
        field: {
            handler(v) {
                const config = v.searchConfig.inputTextConfig
                this.dataFilter.conditionMethod = config.conditionMethod

                if (config.enableDefault) {
                    if (config.conditionMethod === 'SINGLE') {
                        this.dataFilter.conditions[0].matchMethod = config.defaultConfig.defaultValue[0].matchMethod
                        this.dataFilter.conditions[0].matchValue = config.defaultConfig.defaultValue[0].matchValue
                    } else if (config.conditionMethod === 'AND' || config.conditionMethod === 'OR') {
                        this.dataFilter.conditions[0].matchMethod = config.defaultConfig.defaultValue[0].matchMethod
                        this.dataFilter.conditions[0].matchValue = config.defaultConfig.defaultValue[0].matchValue
                        this.dataFilter.conditions[1].matchMethod = config.defaultConfig.defaultValue[1].matchMethod
                        this.dataFilter.conditions[1].matchValue = config.defaultConfig.defaultValue[1].matchValue
                    }

                } else {
                    this.dataFilter.conditions = [{
                        matchMethod: 'STRICT_MATCH',
                        matchValue: ''
                    }, {
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