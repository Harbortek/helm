<template>
    <div>
        <a-form-model-item label="设置状态值">
            <vxe-toolbar size="medium" v-if="false">
                <template #buttons>
                    <a-select v-model="trackerField" style="width:350px;">
                        <a-select-option v-for="item in trackerFields" :key="item.id" :value="item.id" :title="item.name">
                            <div class="field-wrapper">
                                <div class="field-name">{{ item.name }}</div>
                                <div class="field-type">{{ $t('tracker.field.type.' + item.inputType) }}</div>
                            </div>

                        </a-select-option>
                    </a-select>
                </template>
                <template #tools>
                    <vxe-button status="default" content="添加" @click="onAddField" style="width: 100px;"></vxe-button>
                </template>
            </vxe-toolbar>
            <vxe-table ref="xtable" :data="tableData" width="auto" height="200"
                :edit-config="{ trigger: 'click', mode: 'cell', icon: 'vxe-icon-edit', showStatus: true }">
                <vxe-column field="fieldName" title="属性名称" width="100">
                    <template #default="{ row }">
                        {{ row.field.name }}
                    </template>
                </vxe-column>
                <vxe-column field="fieldName" title="属性类型" width="100">
                    <template #default="{ row }">
                        {{ $t('tracker.field.type.' + row.field.inputType) }}
                    </template>
                </vxe-column>
                <vxe-column field="setMethod" title="设置方式" :edit-render="{}" width="150">
                    <template #edit="{ row }">
                        <vxe-select v-model="row.setMethod" transfer @change="onSetMethodChange(row)">
                            <vxe-option key="'SET_VALUE'" :value="'SET_VALUE'"
                                :label="$t('setMethod.SET_VALUE')"></vxe-option>
                            <!-- <vxe-option key="'VALUE_OF'" :value="'VALUE_OF'" :label="$t('setMethod.VALUE_OF')"></vxe-option> -->
                            <vxe-option key="'RESULT_OF'" :value="'RESULT_OF'"
                                :label="$t('setMethod.RESULT_OF')"></vxe-option>
                        </vxe-select>
                    </template>
                    <template #default="{ row }">
                        {{ $t('setMethod.' + row.setMethod) }}
                    </template>
                </vxe-column>
                <vxe-column field="valueFrom" title="值/值来源" :edit-render="{}">
                    <template #edit="{ row }">
                        <template v-if="row.setMethod === 'SET_VALUE'">

                            <vxe-input style="width:100%" type="date" v-model="row.valueFrom"
                                v-if="row.field.inputType == 'DATE'" @change="console.log(row)" transfer />

                            <project-user-select v-else-if="row.field.inputType == 'USER'" v-model="row.valueFrom"
                                :projectId="projectId" />
                            <vxe-select v-else-if="row.field.inputType == 'OPTIONS'" v-model="row.valueFrom" transfer>
                                <vxe-option :value="item.name" v-for="item in row.field.items" :key="item.id"
                                    :label="item.name"></vxe-option>
                            </vxe-select>

                            <vxe-select v-else-if="row.field.inputType == 'STATUS'" v-model="row.valueFrom" transfer>
                                <vxe-option v-for="item in trackerStatuses" :key="item.id" :value="item.id"
                                    :label="item.name"></vxe-option>
                            </vxe-select>

                            <vxe-input v-else v-model="row.valueFrom" />
                        </template>
                        <template v-if="row.setMethod === 'VALUE_OF'">
                            <vxe-select v-model="row.valueFrom" style="width:100%;" transfer
                                @change="(e) => onValueOfChange(e, row)">
                                <vxe-option v-for="item in getSameTypeFields(row)" :key="item.id" :value="item.id"
                                    :label="item.name">
                                    {{ item.name }}
                                </vxe-option>
                            </vxe-select>
                        </template>
                        <template v-if="row.setMethod === 'RESULT_OF'">
                            <vxe-input v-model="row.valueFrom" />
                        </template>
                    </template>
                    <template #default="{ row }">
                        <template v-if="row.setMethod === 'SET_VALUE'">
                            {{ findStatus(row.valueFrom)?.name }}
                        </template>
                        <template v-if="row.setMethod === 'VALUE_OF'">
                            {{ row?.targetField?.name }}
                        </template>
                        <template v-if="row.setMethod === 'RESULT_OF'">
                            {{ row.valueFrom }}
                        </template>
                    </template>
                </vxe-column>
                <!-- <vxe-column title="操作" width="60">
                    <template #default="{ row }">
                        <vxe-button type="text" icon="vxe-icon-delete" @click="onDeleteRow(row)" />
                    </template>
                </vxe-column> -->
            </vxe-table>
        </a-form-model-item>
    </div>
</template>

<script>
import {cloneDeep} from 'lodash'
export default {
    name: 'TriggerStateTransitionPanel',
    props: {
        tracker: {
            type: Object
        },
        action: {
            type: Object
        }
    },
    components: {

    },
    watch: {
        action: {
            handler: function (newVal, oldVal) {
                if (newVal.cotent) {
                    this.tableData = JSON.parse(newVal.cotent)
                    this.loadData()
                } else {
                    this.tableData = []
                }
            },
            immediate: true,
        },
    },
    data() {
        return {
            trackerField: '',
            tableData: [],
        }
    },
    computed: {
        projectId() {
            return this.tracker.projectId
        },
        trackerFields() {
            return this.tracker.trackerFields.filter(f => {
                return f.inputType === 'STATUS'
            })
        },
        trackerStatuses() {
            return this.tracker.trackerStatuses
        }
    },
    mounted() {

    },
    methods: {
        loadData() {
            if (this.tableData.length == 0) {
                const field = this.trackerFields[0]
                this.tableData.push({
                    field: field,
                    setMethod: 'SET_VALUE',
                    valueFrom: '',
                })
            }
        },
        getData() {
            return JSON.stringify(this.tableData)
        },
        getDescription() {
            let description = ''
            this.tableData.forEach(row => {
                let line = '设置「' + row.field.name + '」为 '
                if (row.setMethod === 'SET_VALUE') {
                    line += this.findStatus(row.valueFrom)?.name
                } else if (row.setMethod === 'VALUE_OF') {
                    line += row?.targetField?.name
                } else if (row.setMethod === 'RESULT_OF') {
                    line += row.valueFrom
                }
                description += line
            })
            return description
        },
        validate(callback) {
            const item = this.tableData[0]

            if (item.valueFrom) {
                callback(true)
            } else {
                this.$message.error('需要设置状态取值')
                callback(false)
            }
        },
        onAddField() {
            if (this.trackerField) {
                const fid = this.trackerField
                const field = this.trackerFields.filter(f => { return f.id === fid })[0]
                this.tableData.push({
                    field: field,
                    setMethod: 'SET_VALUE',
                    valueFrom: '',
                })
                this.trackerField = ''
            }
        },
        getSameTypeFields(row) {
            return this.trackerFields.filter(f => { return f.inputType === row.field.inputType })
        },
        onSetMethodChange(row) {
            row.valueFrom = ''
            row.targetField = undefined
        },
        onValueOfChange(e, row) {
            const value = e.value
            const targetField = this.getSameTypeFields(row).filter(f => { return f.id === value })[0]
            row.targetField = targetField
        },
        onDeleteRow(row) {
            this.$refs.xtable.remove(row)
        },
        getTrackerStatusColor: function (trackerStatus) {
            if (!trackerStatus || !trackerStatus.meaning) {
                return ''
            } else {
                return trackerStatus.meaning.color
            }
        },
        findStatus: function (id) {
            let i = 0
            for (i = 0; i < this.trackerStatuses.length; i++) {
                const s = this.trackerStatuses[i]
                if (s.id === id) {
                    return s
                }
            }
            return null
        },

    },
}
</script>

<style lang="less" scoped>
.field-wrapper {
    width: 300px;
    display: flex;
    flex-direction: row;

    .field-name {
        flex: 1 1 auto;
    }

    .field-type {
        color: #909090;
        width: 80px;
        flex: 0 0 auto;
        text-align: right;
    }
}
</style>