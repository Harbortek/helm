<template>
    <div v-if="rule.type === 'TABLE'" class="rule-panel">
        <fieldset>
            <legend>工作项来源</legend>
            <a-row type="flex" align="middle">
                <a-col flex="250px">
                    <a-radio-group v-model="rule.scope" size="small" @change="onChange">
                        <a-radio-button value="ALL">
                            从整篇文档
                        </a-radio-button>
                        <a-radio-button value="PARAGRAPHS">
                            从指定段落
                        </a-radio-button>
                    </a-radio-group>
                </a-col>
                <a-col flex="auto">
                    <a-select v-model="rule.source" style="width: 300px;padding-right: 10px;" mode="multiple"
                        v-if="rule.scope === 'PARAGRAPHS'" size="small" @change="onSourceChange">
                        <a-select-option :value="heading.id" v-for="heading in  docProps?.headings" :key="heading.id">{{
        heading.numbering + heading.name }}</a-select-option>
                    </a-select>
                </a-col>
                <a-col flex="80px">中抽取内容</a-col>
            </a-row>
            <a-row type="flex" align="middle" style="margin-top: 5px;">
                <a-col flex="30px">当</a-col>
                <a-col flex="300px">
                    <a-radio-group v-model="rule.conditionsMatchType" size="small" @change="onChange">
                        <a-radio-button value="ALL">
                            所有条件都满足
                        </a-radio-button>
                        <a-radio-button value="ANY">
                            任意一个条件满足
                        </a-radio-button>
                    </a-radio-group>
                </a-col>
                <a-col flex="auto">
                    开始转换工作项
                </a-col>
            </a-row>
            <conditions-table :data="rule.conditions" :docProps="docProps" @change="e => onConditionsChange(rule, e)" />
        </fieldset>
        <fieldset>
            <legend>工作项转换</legend>
            <a-row type="flex" align="middle" style="margin-top: 5px;margin-bottom: 5px;">

                <a-col flex="100px"><vxe-button type="default" icon="vxe-icon-add" size="small"
                        @click="onAddConversion">新增转换</vxe-button></a-col>
                <a-col flex="auto"></a-col>
            </a-row>
            <a-card v-for="(conversion, cIndex) in rule.conversions" :key="cIndex">
                <template #title>
                    <span>转换{{ cIndex + 1 }} </span>
                </template>
                <template #extra>
                    <a-col flex="30px"><vxe-button type="text" icon="vxe-icon-delete"
                            @click="onRemoveConversion(cIndex)"></vxe-button></a-col>
                </template>
                <!-- <a-row type="flex" align="middle" style="margin-top: 5px;">
                    <a-col flex="auto">
                        <a-radio-group v-model="conversion.conversionType" size="small" @change="onChange">
                            <a-radio-button value="BY_TABLE">
                                整个表格转换为一个工作项
                            </a-radio-button>
                            <a-radio-button value="BY_ROW">
                                每行（除表头外）转为为一个工作项
                            </a-radio-button>
                        </a-radio-group>
                    </a-col>
                </a-row> -->
                <a-row type="flex" align="middle" style="margin-top: 5px;">
                    <a-col flex="30px">当</a-col>
                    <a-col flex="300px">
                        <a-radio-group v-model="conversion.conditionsMatchScope" size="small" @change="onChange">
                            <a-radio-button value="ALL">
                                整个表格
                            </a-radio-button>
                            <a-radio-button value="HEADER">
                                表头行
                            </a-radio-button>
                        </a-radio-group>
                    </a-col>
                    <a-col flex="300px">
                        <a-radio-group v-model="conversion.conditionsMatchType" size="small" @change="onChange">
                            <a-radio-button value="ALL">
                                所有条件都满足
                            </a-radio-button>
                            <a-radio-button value="ANY">
                                任意一个条件满足
                            </a-radio-button>
                        </a-radio-group>
                    </a-col>
                    <a-col flex="60px">转换为</a-col>
                    <a-col flex="auto" style="margin-left: 5px;">
                        <tracker-select :projectId="projectId" v-model="conversion.target" style="width:100%;"
                            size="small" @change="onTrackerTypeChange(conversion, cIndex)" />
                    </a-col>
                </a-row>
                <conditions-table :data="conversion.conditions" :docProps="docProps"
                    @change="e => onConditionsChange(conversion, e)" />
                <a-row type="flex" style="margin-top: 5px;margin-bottom: 5px;">
                    <a-col :span="24">并设置表格列转换映射</a-col>
                    <div class="columnmap-container">
                        <a-row type="flex" v-for="key in Object.keys(conversion.columnMap)" :key="key">
                            <a-col :span="12">{{ key }}</a-col>
                            <a-col :span="12">
                                <a-select v-model="conversion.columnMap[key]" size="small" style="width: 100%;"
                                    @change="onChange">
                                    <!-- <a-select-option value="name">名称</a-select-option>
                                    <a-select-option value="description">描述</a-select-option> -->

                                    <a-select-option :value="f.id" v-for="f in fields[cIndex]" :key="f.id">
                                        {{ f.name }}
                                    </a-select-option>

                                </a-select>
                            </a-col>
                        </a-row>
                    </div>
                </a-row>
                <a-row type="flex" style="margin-top: 5px;margin-bottom: 5px;">
                    <actions-table :data="conversion.actions" :tracker="trackers[cIndex]" :docProps="docProps"
                        :projectId="projectId" @change="e => onActionsChange(conversion, e)" />
                </a-row>



            </a-card>

        </fieldset>
    </div>
</template>

<script>
import ActionsTable from './ActionsTable.vue';
import ConditionsTable from './ConditionsTable.vue';
import TrackerSelect from '@/components/select/TrackerSelect.vue';
import { findOneTracker } from '@/services/tracker/TrackerService'
import XEUtils from 'xe-utils';
import {cloneDeep} from 'lodash'
export default {
    name: 'TableRule',
    props: {
        data: {
            required: true,
        },
        docProps: {
            type: Object,
            default: () => ({})
        },
        projectId: {
            type: String,
            default: ''
        }
    },
    components: {
        ConditionsTable, TrackerSelect, ActionsTable
    },
    watch: {
        data: {
            handler(val) {
                this.rule = cloneDeep(val)
                if (this.rule.source && Array.isArray(this.rule.source)) {
                    let newSource = []
                    for (let i = 0; i < this.rule.source.length; i++) {
                        const heading = XEUtils.find(this.docProps.headings, item => item.id === this.rule.source[i])
                        if (heading) {
                            newSource.push(heading.id)
                        }
                    }
                    this.rule.source = newSource
                }
                if (this.rule.conversions) {
                    this.rule.conversions.forEach(conversion => {
                        let columnMap = this.getColumnMap()
                        Object.assign(columnMap, conversion.columnMap)
                        conversion.columnMap = columnMap
                    })
                }
                console.log(this.rule)
            },
            immediate: true,
        }
    },
    data() {
        return {
            rule: {
                type: 'TABLE',
                scope: 'ALL',
                conditionsMatchType: 'ANY',
                conditions: [{ conditionId: '', argument: '' }],
                conversions: [{
                    conditionsMatchScope: 'ALL',
                    target: '',
                    conversionType: 'BY_TABLE',
                    conditionsMatchType: 'ANY',
                    conditions: [{ conditionId: '', argument: '' }],
                    columnMap: {},
                    actions: [],
                }]
            },
            trackers: {},
            fields: {},
        }
    },
    methods: {
        onAddConversion() {
            this.rule.conversions.push({
                conditionsMatchScope: 'ALL',
                target: '',
                conditionsMatchType: 'ANY',
                conditions: [{ conditionId: '', argument: '' }],
                columnMap: this.getColumnMap(),
                actions: [],
            })
            this.onChange()
        },
        onRemoveConversion(index) {
            this.rule.conversions.splice(index, 1)
            this.onChange()
        },
        onTrackerTypeChange(conversion, index) {
            const trackerId = conversion.target
            const that = this
            findOneTracker(trackerId).then(resp => {
                that.trackers[index] = resp
                const fields = resp.trackerFields
                let fieldList = []
                for (let i = 0; i < fields.length; i++) {
                    const f = fields[i]
                    if (f.inputType === 'TABLE' || f.inputType === 'TEST_STEP') {
                        const columns = f.columns
                        for (let j = 0; j < columns.length; j++) {
                            const column = columns[j]
                            column.name = f.name + '.' + column.name
                            fieldList.push(column)
                        }
                    } else {
                        fieldList.push(f)
                    }
                }
                that.fields[index] = fieldList
                this.$set(this.fields, index, fieldList)
                console.log(that.fields[index])
                this.onChange()
            })

        },
        getFields(index) {
            const data = this.fields[index]
            console.log(data)
            return data
        },
        onSourceChange() {
            this.rule.conversions.forEach(conversion => {
                conversion.columnMap = this.getColumnMap()
            })
            this.onChange()
        },
        getColumnMap() {
            let columnMap = {}
            if (this.rule.scope === 'ALL') {
                this.docProps.headings.forEach(item => {
                    if (item.tables && Array.isArray(item.tables) && item.tables.length > 0) {
                        item.tables.forEach(table => {
                            if (table.headers && Array.isArray(table.headers) && table.headers.length >= 0) {
                                table.headers.forEach(header => {
                                    columnMap[header] = ''
                                })
                            }
                        })
                    }
                })
            } else if (this.rule.source) {
                console.log(this.rule.source)
                const headings = this.docProps.headings.filter(item => this.rule.source.indexOf(item.id) >= 0)
                headings.forEach(item => {
                    if (item.tables && Array.isArray(item.tables) && item.tables.length > 0) {
                        item.tables.forEach(table => {
                            if (table.headers && Array.isArray(table.headers) && table.headers.length > 0) {
                                table.headers.forEach(header => {
                                    columnMap[header] = ''
                                })
                            }
                        })
                    }
                })
            }
            console.log(columnMap)
            return columnMap
        },
        onConditionsChange(rule, conditions) {
            rule.conditions = conditions
            this.onChange()
        },
        onActionsChange(conversion, actions) {
            conversion.actions = actions
            this.onChange()
        },
        onChange() {
            console.log('table rule', this.rule)
            this.$emit('change', this.rule)
        }
    },
}
</script>

<style lang="less" scoped>
.rule-panel {
    border: none;
    padding: 0px;

    fieldset {
        border: solid 1px #d9d9d9;
        margin-top: 5px;
    }

    legend {
        font-size: 12px;
        font-weight: bold;
    }

    .columnmap-container {
        width: 100%;
        border: solid 1px #d9d9d9;
        padding: 5px;
    }
}
</style>