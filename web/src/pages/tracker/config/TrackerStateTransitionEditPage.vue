<template>
    <config-page :routes="routes()" title="工作项步骤设置">
        <a-card title="步骤流程">
            <a-row class="transition-status-detail-body" type="flex">

                <a-col flex="auto" style="display: flex;">
                    <span class="transition-status">
                        <span class="ui-tag-status"
                            :style="{ color: getTrackerStatusColor(transition.transitionFrom), 'border-color': getTrackerStatusColor(transition.transitionFrom) }">
                            {{ transition.transitionFrom.name || {} }}</span>
                    </span>
                    <span class="transition-name">
                        <span class="transition-name-label">
                            开始实现
                        </span>
                    </span>
                    <span class="transition-status">
                        <span class="ui-tag-status"
                            :style="{ color: getTrackerStatusColor(transition.transitionto), 'border-color': getTrackerStatusColor(transition.transitionFrom) }">
                            {{ transition.transitionTo.name || {} }}</span>
                    </span>
                </a-col>

                <a-col flex="220px" justify="center" style="margin-left: 40px;">
                    <a-space>
                        <vxe-button content="重命名步骤" @click="onRenameTransition(transition)" />
                        <vxe-button status="danger" content="删除步骤" @click="onDeleteTransition(transition)" />
                    </a-space>
                </a-col>
            </a-row>
        </a-card>

        <a-tabs default-active-key="CONDITION" :active-key="currentTab" @change="onChangeTab" :animated="false">
            <a-tab-pane key="CONDITION" tab="前置条件">
                <div class="dialog-description dialog-description-info manager-desc">
                    <div class="dialog-description-border"></div>
                    <div class="dialog-description-container">
                        <p class="dialog-description-p style--font-14">
                            前置条件可以用于执行步骤时的条件验证，拥有以下条件才可以执行对应步骤。
                        </p>
                    </div>
                </div>
                <vxe-table ref="conditionTable" :data="transition.validators" :loading="loading"
                    :row-config="{ isHover: true }" show-footer :footer-method="footerMethod">
                    <vxe-column type="seq" title="序号" width="60">
                        <template #footer="{}">
                            <vxe-button type="text" status="primary" icon="vxe-icon-add"
                                @click="onCreateCondition()">新建条件</vxe-button>
                        </template>
                    </vxe-column>
                    <vxe-column field="name" title="条件名称" width="200">
                        <template #default="{ row }">
                            {{ row.name }}
                        </template>
                    </vxe-column>
                    <vxe-column field="description" title="条件内容">
                        <template #default="{ row }">
                            {{ row.expressionEL }}
                        </template>
                    </vxe-column>
                    <vxe-column field="" title="操作" width="200" align="center" header-align="center">
                        <template #default="{ row }">
                            <div>
                                <vxe-button type="text" status="primary" @click="onEditCondition(row)">编辑</vxe-button>
                                <vxe-button type="text" status="primary" @click="onDeleteCondition(row)">删除</vxe-button>
                            </div>
                        </template>
                    </vxe-column>
                </vxe-table>
            </a-tab-pane>
            <a-tab-pane key="PERMITED" tab="执行权限">
                <div class="dialog-description dialog-description-info manager-desc">
                    <div class="dialog-description-border"></div>
                    <div class="dialog-description-container">
                        <p class="dialog-description-p style--font-14">
                            执行权限可以用于执行步骤时的条件验证，拥有以下条件才可以执行对应步骤。
                        </p>
                    </div>
                </div>
                <role-members-table :projectId="projectId" :value="transition.permitted" scope="SCOPE_TRACKER"
                    :title="'以下成员域允许执行工作流步骤「' + transition.name + '」'" emptyText="所有成员" tableHeight="auto"
                    @delete="e => onDeletePermitted(e, transition)" @add="e => onAddPermitted(e, transition)" />
            </a-tab-pane>
            <a-tab-pane key="POST" tab="后置动作">
                <div class="dialog-description dialog-description-info manager-desc">
                    <div class="dialog-description-border"></div>
                    <div class="dialog-description-container">
                        <p class="dialog-description-p style--font-14">
                            成功执行某步骤后，系统将按照以下列表的顺序，从上到下执行已配置的后置动作（比如更新工作项负责人、发送邮件通知、更新父工作项的工作项状态）。后置动作以系统权限执行。后置动作的执行结果不会影响当前步骤的执行结果。
                        </p>
                    </div>
                </div>
                <vxe-table ref="actionTable" :data="transition.actions" :loading="loading" :row-config="{ isHover: true }"
                    show-footer :footer-method="footerMethod">
                    <vxe-column type="seq" title="序号" width="60">
                        <template #footer="{}">
                            <vxe-button type="text" status="primary" icon="vxe-icon-add"
                                @click="onCreateAction()">新建动作</vxe-button>
                        </template>
                    </vxe-column>
                    <vxe-column field="type" title="动作类型" width="150">
                        <template #default="{ row }">
                            {{ $t(row.type) }}
                        </template>
                    </vxe-column>
                    <vxe-column field="description" title="动作内容">
                        <template #default="{ row }">
                            {{ row.description }}
                        </template>
                    </vxe-column>
                    <vxe-column field="" title="操作" width="200" align="center" header-align="center">
                        <template #default="{ row }">
                            <div>
                                <vxe-button type="text" status="primary" @click="onEditAction(row)">编辑</vxe-button>
                                <vxe-button type="text" status="primary" @click="onDeleteAction(row)">删除</vxe-button>
                            </div>
                        </template>
                    </vxe-column>
                </vxe-table>
            </a-tab-pane>
        </a-tabs>
        <tracker-state-transition-dialog :is-show-dialog="isShowTransitionDialog" :editMode="transitionEditMode"
            :tracker-statuses="tracker.trackerStatuses" :state-transitions="tracker.trackerStateTransitions"
            :transition="currentTransition" @ok="onStateTransitiontDialogOK" @cancel="onStateTransitiontDialogCancel" />

        <tracker-state-transition-action-dialog :is-show-dialog="isShowActionDialog" :editMode="actionEditMode"
            :action="currentAction" :tracker="tracker" @ok="onTrackerStateTransitionActionDialogOK"
            @cancel="onTrackerStateTransitionActionDialogCancel" />

        <tracker-state-transition-condition-dialog :is-show-dialog="isShowConditionDialog" :editMode="conditionEditMode"
            :condition="currentCondition" :tracker="tracker" @ok="onTrackerStateTransitionConditionDialogOK"
            @cancel="onTrackerStateTransitionConditionDialogCancel" />
    </config-page>
</template>
<script>
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import TrackerStateTransitionDialog from './TrackerStateTransitionDialog.vue';
import TrackerStateTransitionActionDialog from './TrackerStateTransitionActionDialog.vue';
import TrackerStateTransitionConditionDialog from './TrackerStateTransitionConditionDialog.vue';
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
import VXETable from 'vxe-table';
import {
    findOneTracker, updateTrackerStateTransition, deleteTrackerStateTransition, renameTrackerStateTransition,
    grantTrackerStateTransitionPermitted, unGrantTrackerStateTransitionPermitted
} from "@/services/tracker/TrackerService";
export default {
    name: "TrackerStateTransitionEditPage",
    components: { ConfigPage, TrackerStateTransitionDialog, TrackerStateTransitionActionDialog, TrackerStateTransitionConditionDialog, RoleMembersTable },
    data() {
        return {
            loading: false,
            projectId: '',
            tracker: {},
            transition: { name: '', transitionFrom: { name: '' }, transitionTo: { name: '' } },
            currentTab: 'CONDITION',
            isShowTransitionDialog: false,
            transitionEditMode: 'edit',
            currentTransition: {
                name: '',
                transitionFrom: { id: '' },
                transitionTo: { id: '' }
            },
            isShowActionDialog: false,
            actionEditMode: 'create',
            currentAction: undefined,
            isShowConditionDialog: false,
            conditionEditMode: 'create',
            currentCondition: undefined

        };
    },
    computed: {
        trackerId() {
            return this.$route.params.trackerId
        }
    },
    mounted() {
        this.projectId = this.$route.params.projectId
        this.loadData();
    },
    methods: {
        routes: function () {
            return [{
                name: "trackerListConfig", meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerListConfig",
                    show: false,
                },
            }, {
                name: "trackerConfigPortal",
                meta: {
                    icon: "blank",
                    title: this.tracker.name,
                    show: false,
                },
            }, {
                name: 'trackerStateTransitionConfig', meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerStateTransitionConfig",
                    show: false,
                },
            }, {
                name: 'trackerStateTransitionConfigEdit', meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerStateTransitionEdit",
                    show: false,
                },
            }]
        },
        loadData: function () {
            let trackerId = this.$route.params.trackerId
            let transitionId = this.$route.params.transitionId
            findOneTracker(trackerId).then(resp => {
                this.tracker = resp;
                this.trackerStatusList = this.tracker.trackerStatuses
                this.stateTransitionList = this.tracker.trackerStateTransitions
                let i = 0;
                for (i = 0; i < this.stateTransitionList.length; i++) {
                    const ts = this.stateTransitionList[i]
                    if (ts.id === transitionId) {
                        this.transition = ts
                        this.transition.transitionFrom = this.findStatus(ts.transitionFrom.id)
                        this.transition.transitionTo = this.findStatus(ts.transitionTo.id)
                        break
                    }
                }
                console.log(this.transition)
                this.loading = false
            })
        },
        onAddPermitted(row, transition) {
            grantTrackerStateTransitionPermitted(this.tracker.id, transition.id, row).then(resp => {
                this.loadData()
            })
        },
        onDeletePermitted(row, transition) {
            VXETable.modal.confirm({
                title: '取消授权',
                content: '「' + row.name + '」的授权将被取消'
            }).then(type => {
                if (type === 'confirm') {
                    unGrantTrackerStateTransitionPermitted(this.tracker.id, transition.id, row).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」授权已被取消', status: 'info' })
                        this.loadData()
                    })
                }
            })
            // transition.permitted=transition.permitted.filter(item => { return item.id != e.id })
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
            for (i = 0; i < this.trackerStatusList.length; i++) {
                const s = this.trackerStatusList[i]
                if (s.id === id) {
                    return s
                }
            }

            return null
        },
        onChangeTab: function (activeKey) {
            this.currentTab = activeKey
            this.reload()
        },
        reload: function () {

        },
        footerMethod: function ({ columns, data }) {
            return [
                columns.map(column => {
                    return null
                })
            ]
        },
        onRenameTransition: function (transition) {
            this.transitionEditMode === 'edit'
            this.currentTransition = {
                id: transition.id,
                name: transition.name,
                transitionFrom: transition.transitionFrom,
                transitionTo: transition.transitionTo
            }
            this.isShowTransitionDialog = true
        },
        onDeleteTransition: function (transition) {
            VXETable.modal.confirm({
                title: '删除工作项步骤',
                content: '「' + transition.name + '」将被删除，该动作不可恢复'
            }).then(type => {
                if (type === 'confirm') {
                    deleteTrackerStateTransition(this.tracker.id, transition).then(resp => {
                        VXETable.modal.message({ content: '「' + transition.name + '」已被删除', status: 'info' })
                        this.loadData()
                    })
                }
            })

        },
        onStateTransitiontDialogOK: function (transition) {
            if (this.transitionEditMode === 'edit') {
                renameTrackerStateTransition(this.tracker.id, transition).then(resp => {
                    this.loadData()
                    this.isShowTransitionDialog = false
                })
            }
        },
        onStateTransitiontDialogCancel: function (transition) {
            this.isShowTransitionDialog = false
        },
        onCreateAction: function () {
            this.actionEditMode = 'create'
            this.currentAction = undefined
            this.isShowActionDialog = true
        },
        onEditAction: function (action) {
            this.actionEditMode = 'edit'
            this.currentAction = action
            this.isShowActionDialog = true
        },
        onDeleteAction: function (action) {
            VXETable.modal.confirm({
                title: '删除后续动作',
                content: '「' + this.$t(action.type) + '」将被删除，该动作不可恢复'
            }).then(type => {
                if (type === 'confirm') {
                    this.$refs.actionTable.remove(action)
                    this.transition.actions = this.$refs.actionTable.getTableData().tableData
                    updateTrackerStateTransition(this.trackerId, this.transition).then(resp => {
                        this.loadData()
                    })
                }
            })
        },
        onTrackerStateTransitionActionDialogOK: function (action) {
            if (this.currentAction) {
                Object.assign(this.currentAction, action)
            } else {
                this.transition.actions.push(action)
            }
            updateTrackerStateTransition(this.trackerId, this.transition).then(resp => {
                this.loadData()
                this.isShowActionDialog = false
            })
        },
        onTrackerStateTransitionActionDialogCancel: function (action) {
            this.isShowActionDialog = false
        },
        /** 条件编辑 */
        onCreateCondition: function () {
            this.conditionEditMode = 'create'
            this.currentCondition = undefined
            this.isShowConditionDialog = true
        },
        onEditCondition: function (condition) {
            this.conditionEditMode = 'edit'
            this.currentCondition = condition
            this.isShowConditionDialog = true
        },
        onDeleteCondition: function (condition) {
            VXETable.modal.confirm({
                title: '删除条件',
                content: '「' + condition.name + '」将被删除，该动作不可恢复'
            }).then(type => {
                if (type === 'confirm') {
                    this.$refs.conditionTable.remove(condition)
                    this.transition.validators = this.$refs.conditionTable.getTableData().tableData
                    updateTrackerStateTransition(this.trackerId, this.transition).then(resp => {
                        this.loadData()
                    })
                }
            })
        },
        onTrackerStateTransitionConditionDialogOK: function (condition) {
            if (this.currentCondition) {
                Object.assign(this.currentCondition, condition)
            } else {
                this.transition.validators.push(condition)
            }

            updateTrackerStateTransition(this.trackerId, this.transition).then(resp => {
                this.loadData()
                this.isShowConditionDialog = false
            })

        },
        onTrackerStateTransitionConditionDialogCancel: function (condition) {
            this.isShowConditionDialog = false
        }
    },
    created() { }
};
</script>
<style lang="less" scoped>
.transition-status {
    display: flex;
    align-items: center;
    white-space: nowrap;

    .ui-tag-status {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 14px;
        box-sizing: border-box;
        display: inline-block;
        height: 28px;
        line-height: 28px;
        transition: border-color .2s;
        border: 1px solid;
        border-radius: 20px;
        padding: 0px 8px;
    }
}

.transition-name {
    flex: 1;
    display: flex;
    text-align: center;
    line-height: 20px;
    border-width: 12px;
    border-style: solid;
    border-color: #fff;
    margin-top: 5px;
    margin-bottom: 5px;
    margin-left: 10px;
    margin-right: 10px;
    border-image: url(data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48cGF0aCBmaWxsPSIjODY5M0EwIiBvcGFjaXR5PSIuNSIgZD0iTTQgOWg3Ljk4NXYySDR6Ii8+PHBhdGggZD0ibTE0LjA2IDcuMjY0LS4wMy0uMDN2LjAyNGwuMDMuMDA2em0tLjYzNS0zLjE4NWEuOTcxLjk3MSAwIDAgMSAuNDU2LjI1OGw0LjgzNSA0LjgzNWEuOTczLjk3MyAwIDAgMSAuMjguODEuOTczLjk3MyAwIDAgMS0uMjguODFsLTQuODM1IDQuODM1YS45NzQuOTc0IDAgMCAxLS4yODMuMTk3Ljk3Ny45NzcgMCAwIDEtLjU1OS4xNzZoLS4wNDdjLS41NDggMC0uOTkyLS40NTYtLjk5Mi0xLjAwMlY1LjAwMkMxMiA0LjQ1IDEyLjQ1IDQgMTIuOTkyIDRoLjA0N2EuOTcuOTcgMCAwIDEgLjM4Ni4wOHoiIGZpbGw9IiM0MjVBNzAiLz48Y2lyY2xlIGZpbGw9IiNDMkM5Q0YiIGN4PSI0IiBjeT0iMTAiIHI9IjQiLz48L2c+PC9zdmc+) 10 9;
    position: relative;

    .transition-name-label {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        top: -10px;
        background-color: #fff;
        padding-left: 10px;
        padding-right: 10px;
        max-width: 80%;
    }
}

.dialog-description {
    display: flex;
    margin-bottom: 10px;
    line-height: 1.5;
    box-sizing: border-box;
    border-bottom: 1px solid #e8e8e8;
    border-radius: 0;
    border-right: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}

.dialog-description-border {
    border-radius: 0;
    flex: none;
    width: 4px;
}

.dialog-description-info .dialog-description-border {
    background: #338fe5;
}

.dialog-description-container {
    background: #fff;
    display: flex;
    flex: auto;
    padding: 10px;
}

.dialog-description-p {
    margin: 0 0 0 0;
    padding: 0;
    white-space: pre-wrap;
}

.style--font-14 {
    font-size: 14px;
    line-height: 22px;
}
</style>
