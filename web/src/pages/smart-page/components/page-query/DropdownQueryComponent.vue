<template>
    <div v-if="field.searchConfig.renderType === 'DROPDOWN'" style="width: 200px;">
        <a-select size="small" v-model="dataFilter.conditions[0].matchValue" @change="onChange" :dropdownMatchSelectWidth="false"
            :style="{ minWidth: '150px' }" :mode="allowMultiple ? 'multiple' : 'default'">
            <a-select-option v-for="item in fieldOptions" :key="item.key" :value="item.key">{{
                item.value
                }}</a-select-option>
        </a-select>
    </div>
</template>

<script>
import { findEnumValues } from '@/services/smart-page/DatasetService'
export default {
    name: 'DropdownQueryComponent',
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
            fieldOptions: [],
            allowMultiple: false,
            dataFilter: {
                filterMethod:'CONDITION',
                conditionMethod: 'SINGLE',
                queryMethod: 'SINGLE',
                conditions: [{
                    matchMethod: 'STRICT_MATCH',
                    matchValue:''
                }]
            }
        }
    },
    watch: {
        field: {
            handler(v) {
                const config = v.searchConfig.dropdownConfig
                this.allowMultiple = config.conditionMethod === 'MULTI'
                if (config.dataSource === 'AUTO') {
                    const datasetId = v.datasetId
                    const fieldName = v.field
                    findEnumValues(this.pageId, datasetId, fieldName).then(res => {
                        console.log("枚举值", res)
                        this.fieldOptions = this.optionData(res)
                        if (config.enableDefault && config.defaultConfig) {
                            const defaultValue = config.defaultConfig.defaultValue
                            if (defaultValue) {
                                this.dataFilter.conditions[0].matchValue = defaultValue
                            }
                        }
                        this.$emit('ready', this.dataFilter)
                    }).catch(err => {
                        console.error(err)
                        this.$emit('ready', this.dataFilter)
                    })
                } else if (config.dataSource === 'MANUAL') {
                    this.fieldOptions = config.options
                    if (config.enableDefault && config.defaultConfig) {
                        const defaultValue = config.defaultConfig.defaultValue
                        if (defaultValue) {
                            const val = this.fieldOptions.filter(item => item.value === defaultValue)
                            if (val && val.length > 0) {
                                this.dataFilter.conditions[0].matchValue = val[0].key    
                            }                            
                        }
                        this.$emit('ready', this.dataFilter)
                    }
                } else if (config.dataSource === 'DATASET') {
                    const datasetId = config.datasetId
                    const fieldName = config.searchField
                    findEnumValues(this.pageId, datasetId, fieldName).then(res => {
                        console.log("枚举值", res)
                        this.fieldOptions = this.optionData(res)
                        if (config.enableDefault && config.defaultConfig) {
                            const defaultValue = config.defaultConfig.defaultValue
                            if (defaultValue) {
                                this.dataFilter.conditions[0].matchValue = defaultValue
                            }
                        }
                        this.$emit('ready', this.dataFilter)
                    }).catch(err => {
                        console.error(err)
                        this.$emit('ready', this.dataFilter)
                    })
                }
            },
            immediate: true
        }
    },
    methods: {
        optionData(data) {
            if (!data) return null
            return data.filter(item => !!item).map(item => {
                return {
                    key: item,
                    value: item
                }
            })
        },
        onChange() {
            this.$emit('change', this.dataFilter)
        }
    }
}
</script>

<style></style>