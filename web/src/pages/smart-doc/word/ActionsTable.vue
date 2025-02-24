<template>
    <div class="contains-container">
        <a-row type="flex" align="middle" style="margin-top: 5px;">
            <a-col flex="auto">执行以下动作</a-col>
            <a-col flex="30px"><vxe-button type="text" icon="vxe-icon-add" @click="onAddAction"></vxe-button></a-col>
        </a-row>
        <a-card type="flex" align="middle" style="margin-top: 5px;" v-for="(action, actionIndex) in actions"
            :key="actionIndex">
            <template #title>
                <a-row type="flex" align="middle" style="font-size: 14px;font-weight: normal;">
                    <a-col flex="80px">
                        设置属性
                    </a-col>
                    <a-col flex="200px" style="padding-left: 5px;">
                        <a-select v-model="action.fieldId" style="width: 200px;" size="small"
                            @change="onPropertyChange(action)">
                            <a-select-option :value="item.id" v-for="item in  trackerFields()" :key="item.id">{{
                item.name }}</a-select-option>
                        </a-select>
                    </a-col>
                </a-row>
            </template>
            <template #extra>
                <a-col flex="30px">
                    <vxe-button type="text" icon="vxe-icon-close" size="small"
                        @click="onRemoveAction(actionIndex)"></vxe-button>
                </a-col>
            </template>
            <a-row type="flex" align="middle">
                <a-col flex="80px" style="text-align: left;">
                    取值为
                </a-col>
                <a-col flex="200px">
                    <a-select v-model="action.actionType" style="width: 200px;" size="small"
                        @change="onChangeActionType(action)">
                        <a-select-option :value="item.id" v-for="item in  propertyActionTypes(action)" :key="item.id">{{
                item.name }}</a-select-option>
                    </a-select>
                </a-col>
                <a-col flex="auto" style="padding-left: 5px;">
                    <a-input
                        v-if="action.argumentType == 'INTEGER' || action.argumentType == 'TEXT' || action.argumentType == 'WORK_ITEM_NO'"
                        placeholder="请输入..." v-model="action.argument" style="width:100%;" size="small"
                        @change="onChange" />

                    <a-date-picker v-if="action.argumentType == 'DATE'" style="width:100%;" size="small"
                        v-model="action.argument" placeholder="请选择日期" @change="onChange" />

                    <a-select v-if="action.argumentType == 'STATUS'" mode="multiple" optionFilterProp="label"
                        v-model="action.argument" style="width:100%;" placeholder="请选择..." size="small"
                        @change="onChange">
                        <a-select-option v-for="status in tracker?.trackerStatuses" :key="status.id"
                            :label="status.name">
                            <a-icon style="margin-right:5px;" v-if="status.icon" :component="status.icon" />{{
                status.name }}
                        </a-select-option>
                    </a-select>

                    <a-select v-if="action.argumentType == 'OPTIONS' || action.argumentType == 'WORK_ITEM' || action.argumentType == 'STATUS_TYPE' ||
                action.argumentType == 'WORK_ITEM_TYPE'" optionFilterProp="label" v-model="action.argument"
                        style="width: 100%;" placeholder="请选择..." size="small" @change="onChange">
                        <a-select-option v-for="status in trackerOptionValues(action)" :key="status.id"
                            :label="status.name">
                            <a-icon style="margin-right:5px;" v-if="status.icon" :component="status.icon" />{{
                status.name }}
                        </a-select-option>
                    </a-select>

                    <sprint-select v-model="action.argument" :projectId="projectId" size="small" style="width: 100%;"
                        v-if="action.argumentType == 'SPRINT'" @change="onChange" />

                    <project-user-select v-model="action.argument" :projectId="projectId" size="small"
                        style="width: 100%;" v-if="action.argumentType == 'USER'"
                        @change="onChange"></project-user-select>

                    <div class="columnmap-container"
                        v-if="action.argumentType === 'TABLE' || action.argumentType === 'TEST_STEP'">
                        <a-row type="flex" v-for="key in Object.keys(action.columnMap)" :key="key">
                            <a-col :span="12">{{ key }}</a-col>
                            <a-col :span="12">
                                <a-select v-model="action.columnMap[key]" size="small" style="width: 100%;"
                                    @change="onColumnMapChange(action)">
                                    <a-select-option :value="f.id" v-for="f in action.field.columns" :key="f.id">
                                        {{ f.name }}
                                    </a-select-option>
                                </a-select>
                            </a-col>
                        </a-row>
                    </div>
                </a-col>
            </a-row>
            <a-row type="flex" align="middle" style="margin-top: 5px;">
                <a-col flex="80px" style="text-align: left;">执行条件</a-col>
                <a-col flex="400px" style="text-align: left;">
                    <a-radio-group v-model="action.conditionsMatchType" size="small" @change="onChange">
                        <a-radio-button value="ALWAYS">
                            无条件执行
                        </a-radio-button>
                        <a-radio-button value="ALL">
                            所有条件都满足
                        </a-radio-button>
                        <a-radio-button value="ANY">
                            任意一个条件满足
                        </a-radio-button>
                    </a-radio-group>
                </a-col>
                <a-col flex="auto"></a-col>
            </a-row>
            <conditions-table :data="action.conditions" :docProps="docProps"
                @change="e => onConditionsChange(action, e)" />
        </a-card>
    </div>
</template>

<script>
import ConditionsTable from './ConditionsTable.vue';
import {cloneDeep} from 'lodash'
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import SprintSelect from '@/components/select/SprintSelect.vue';
import { ACTION_TYPES } from '@/pages/smart-doc/util/rules';

export default {
    name: 'ActionsTable',
    props: {
        rule: {
            type: Object,
            required: true,
        },
        tracker: {
            type: Object,
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
        ConditionsTable, ProjectUserSelect, SprintSelect
    },
    data() {
        return {
            actions: [],
            actionTypes: cloneDeep(ACTION_TYPES),
        }
    },
    watch: {
        rule: {
            handler(newVal, oldVal) {
                this.actions = cloneDeep(newVal.actions);
                for (let i = 0; i < this.actions.length; i++) {
                    if (this.actions[i].argumentType === 'TABLE' || this.actions[i].argumentType === 'TEST_STEP') {
                        if (this.actions[i].argument && this.actions[i].argument !== '') {
                            this.actions[i].columnMap = JSON.parse(this.actions[i].argument)
                        } else {
                            this.actions[i].columnMap = this.getColumnMap()
                        }

                    }
                }
                console.log(this.actions)
            },
            immediate: true,
        }
    },
    methods: {
        onAddAction() {
            let action = { fieldId: '', actionType: this.actionTypes[0].id, argument: '', argumentType: 'TEXT', conditionsMatchType: 'ALWAYS', conditions: [{ conditionId: '', argument: '' }], columnMap: this.getColumnMap() }
            this.actions.push(action)
            this.onChange()
        },
        onRemoveAction(index) {
            this.actions.splice(index, 1)
            this.onChange()
        },
        onChangeActionType(action) {
            let actionType = this.actionTypes.filter(item => item.id === action.actionType)[0]
            action.hasArgument = actionType.hasArgument
            this.onChange()
        },
        onPropertyChange(action) {
            if (this.tracker && action.fieldId && this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                const field = this?.tracker?.trackerFields.filter(item => item.id === action.fieldId)[0]
                if (field) {
                    action.argumentType = field.inputType
                    action.actionType = ''
                    action.field = field
                }
            }
            action.argument = ''
            this.onChange()
        },
        onColumnMapChange(action) {
            action.argument = JSON.stringify(action.columnMap)
            this.onChange()
        },
        trackerFields() {
            if (this.tracker && this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                return this.tracker.trackerFields.filter(item => item.inputType !== 'WORK_ITEM_NO' && item.inputType !== 'WIKI' && item.inputType !== 'WORK_ITEM' && item.inputType !== 'STATUS_TYPE' && item.inputType !== 'WORK_ITEM_TYPE' && item.inputType !== 'PROJECT' && item.systemProperty !== 'name')
            }
            return []
        },

        propertyActionTypes(action) {
            let actionTypes = this.actionTypes.filter(item => item.argumentType === action.argumentType);

            if (actionTypes.length === 0) {
                actionTypes = [{ id: 'VALUE', name: '值', hasArgument: true, argumentType: action.argumentType }]
            }
            console.log(action)
            return actionTypes
        },
        trackerOptionValues(action) {
            if (this.tracker && action.fieldId && this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                const field = this?.tracker?.trackerFields.filter(item => item.id === action.fieldId)[0]
                if (field && field.items) {
                    return field.items
                }
            }
            return []
        },
        onConditionsChange(action, conditions) {
            action.conditions = conditions
            this.onChange()
        },
        onChange() {
            this.$emit('change', this.actions)
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
    },
}
</script>

<style>
.columnmap-container {
    width: 100%;
    border: solid 1px #d9d9d9;
    padding: 5px;
}
</style>