<template>
    <config-page :routes="routes()" title="工作项工作流设置"
        description="工作流可以用于定制对应工作项的在不同状态阶段的流转。你可以在表格视图中，通过勾选复选框的方式新建工作流步骤。你也可以切换到详情视图中新建工作流步骤。">
        <vxe-toolbar>
            <template #buttons>
                <vxe-button status="primary" content="添加工作项状态" @click="onCreateTrackerStatus"></vxe-button>
                <vxe-button content="设置初始状态" @click="onShowStatusInitialDialog"></vxe-button>
                <vxe-button content="状态排序" @click="onShowStatusSortDialog"></vxe-button>
                <vxe-button content="新建步骤" @click="onCreateTransition"></vxe-button>
            </template>
            <template #tools>
                <a-radio-group v-model="viewMode" @change="onViewChange">
                    <a-radio-button value="FLOW_VIEW">
                        流程视图
                    </a-radio-button>
                    <a-radio-button value="TABLE_VIEW">
                        表格视图
                    </a-radio-button>
                    <a-radio-button value="DETAIL_VIEW">
                        详情视图
                    </a-radio-button>
                </a-radio-group>
            </template>
        </vxe-toolbar>
        <div id="container" v-show="viewMode === 'FLOW_VIEW'" style="height:calc(100vh - 300px);">

        </div>
        <div v-show="viewMode === 'TABLE_VIEW'" class="table-view">
            <vxe-grid ref="dataTable" v-bind="gridOptions" :loading="loading" :row-config="{ isHover: true }"
                :height="tableHeight">
                <template #title_header>
                    <div class="first-col">
                        <div class="first-col-top">目标状态</div>
                        <div class="first-col-bottom">开始状态</div>
                    </div>
                </template>
                <template #field_default="{ row, column, rowIndex, columnIndex }">
                    <a-tooltip placement="topLeft" :title="getTooltip(row, column)">
                        <vxe-checkbox :value="hasTransition(row, column)" :disabled="rowIndex == (columnIndex - 1)"
                            @change="onCheckChanged(row, column, rowIndex, columnIndex)"></vxe-checkbox>
                    </a-tooltip>
                </template>
            </vxe-grid>
        </div>
        <div v-show="viewMode === 'DETAIL_VIEW'">
            <div class="status-table" :key="item.id" v-for="item in detailTableData">
                <a-row type="flex" class="status-title-container">
                    <a-col class="status-title" flex="auto">
                        <div class="task-status-title">
                            <a-icon type="check-circle" theme="filled"
                                :style="{ fontSize: '18px', color: getTrackerStatusColor(item.status) }" />
                            <div class="task-status-title-content">
                                <span :style="{ color: getTrackerStatusColor(item.status) }"> {{ item.status.name
                                    }}</span>
                            </div>
                        </div>
                        <a-tag v-if="item.status.initial">初始状态</a-tag>
                    </a-col>
                    <a-col width="140px" style="display: flex;">
                        <vxe-button icon="vxe-icon-edit" type="text"
                            @click="onEditTrackStatus(item.status)">编辑</vxe-button>

                        <a-tooltip v-if="item.status.initial" placement="topLeft">
                            <template slot="title">
                                不支持删除初始状态
                            </template>
                            <div style="width:80px">
                                <vxe-button icon="vxe-icon-delete" type="text"
                                    @click="onDeleteTrackerStatus(item.status)" :disabled="true">删除</vxe-button>
                            </div>
                        </a-tooltip>
                        <vxe-button v-else icon="vxe-icon-delete" type="text"
                            @click="onDeleteTrackerStatus(item.status)">删除</vxe-button>
                    </a-col>
                </a-row>
                <vxe-table :data="item.transitions" :loading="loading" :row-config="{ isHover: true }" show-footer
                    :footer-method="footerMethod">
                    <vxe-column field="name" title="步骤名称">
                        <template #default="{ row }">
                            {{ row.name }}
                        </template>
                        <template #footer="{ items }">
                            <vxe-button type="text" status="primary" icon="vxe-icon-add"
                                @click="onCreateTransition(item.status)">新建步骤</vxe-button>
                        </template>
                    </vxe-column>
                    <vxe-column field="transitionFrom" title="开始状态" width="150">
                        <template #default="{ row }">
                            <div class="transition-status">
                                <span class="ui-tag-status"
                                    :style="{ color: getTrackerStatusColor(row.transitionFrom), 'border-color': getTrackerStatusColor(row.transitionFrom) }">{{
        row.transitionFrom.name }}</span>
                            </div>
                        </template>
                    </vxe-column>
                    <vxe-column width="100">
                        <template #default="{ row }">
                            <i class="vxe-icon-arrow-double-right" :style="{ fontSize: '18px', color: '#c7c7c7' }"></i>
                        </template>
                    </vxe-column>
                    <vxe-column field="transitionTo" title="目标状态" width="150">
                        <template #default="{ row }">
                            <div class="transition-status">
                                <span class="ui-tag-status"
                                    :style="{ color: getTrackerStatusColor(row.transitionTo), 'border-color': getTrackerStatusColor(row.transitionTo) }">{{
        row.transitionTo.name }}</span>
                            </div>
                        </template>
                    </vxe-column>
                    <vxe-column field="filter" title="步骤验证" width="200" align="center" header-align="center">
                        <template #default="{ row }">

                        </template>
                    </vxe-column>
                    <vxe-column field="fields" title="步骤属性" width="200" align="center" header-align="center">
                        <template #default="{ row }">

                        </template>
                    </vxe-column>
                    <vxe-column field="actions" title="后置动作" width="200" align="center" header-align="center">
                        <template #default="{ row }">

                        </template>
                    </vxe-column>
                    <vxe-column field="" title="操作" width="200" align="right" header-align="center">
                        <template #default="{ row }">
                            <div>
                                <vxe-button type="text" status="primary"
                                    @click="onRenameTransition(row)">重命名</vxe-button>
                                <vxe-button type="text" status="primary" @click="onEditTransition(row)">编辑</vxe-button>
                                <vxe-button type="text" status="primary"
                                    @click="onDeleteTransition(row)">删除</vxe-button>
                            </div>
                        </template>
                    </vxe-column>
                </vxe-table>
            </div>

        </div>
        <tracker-status-dialog :editMode="editMode" :is-show-dialog="isShowDialog"
            :tracker-statuses="tracker.trackerStatuses" :trackerStatus="currentTrackerStatus"
            @ok="onTrackerStatusDialogOK" @cancel="onTrackerStatusDialogCancel" />

        <tracker-status-initial-dialog :is-show-dialog="isShowInitialDialog" :tracker-statuses="tracker.trackerStatuses"
            @ok="onTrackerStatusInitialDialogOK" @cancel="onTrackerStatusInitialDialogCancel" />

        <tracker-status-replace-dialog :is-show-dialog="isShowReplaceDialog" :tracker-statuses="tracker.trackerStatuses"
            @ok="onTrackerStatusReplaceDialogOK" @cancel="onTrackerStatusReplaceDialogCancel" />

        <tracker-status-sort-dialog :is-show-dialog="isShowSortDialog" :tracker-statuses="tracker.trackerStatuses"
            @ok="onTrackerStatusSortDialogOK" @cancel="onTrackerStatusSortDialogCancel" />

        <tracker-state-transition-dialog :is-show-dialog="isShowTransitionDialog" :editMode="transitionEditMode"
            :tracker-statuses="tracker.trackerStatuses" :state-transitions="tracker.trackerStateTransitions"
            :transition="currentTransition" @ok="onStateTransitiontDialogOK" @cancel="onStateTransitiontDialogCancel" />
    </config-page>
</template>
<script>
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import { Graph, Vector } from "@antv/x6";
import { DagreLayout } from "@antv/layout";
import {
    findOneTracker, findTrackerStatus, createTrackerStatus, updateTrackerStatus, deleteTrackerStatus, sortTrackerStatus,
    setInitialTrackerStatus, trackerStatusUseByTransition, trackerStatusUseByWorkItem, replaceWorkItemStatus,
    createTrackerStateTransition, updateTrackerStateTransition, deleteTrackerStateTransition, renameTrackerStateTransition
} from "@/services/tracker/TrackerService";
import TrackerStatusDialog from './TrackerStatusDialog.vue';
import TrackerStatusInitialDialog from './TrackerStatusInitialDialog.vue';
import TrackerStatusReplaceDialog from './TrackerStatusReplaceDialog.vue';
import TrackerStatusSortDialog from './TrackerStatusSortDialog.vue';
import TrackerStateTransitionDialog from './TrackerStateTransitionDialog.vue';
import VXETable from 'vxe-table';

export default {
    name: "TrackerStateTransitionConfigPage",
    components: { ConfigPage, Graph, TrackerStatusDialog, TrackerStatusInitialDialog, TrackerStatusReplaceDialog, TrackerStatusSortDialog, TrackerStateTransitionDialog, VXETable },
    data() {
        return {
            tableHeight: 400,
            loading: false,
            viewMode: 'TABLE_VIEW',
            tracker: {},
            trackerStatusList: [],
            stateTransitionList: [],
            gridOptions: {
                border: true,
                resizable: true,
                showOverflow: true,
                loading: false,
                height: 300,
                columns: [],
                data: []
            },
            nodeInfo: {
                nodes: [],
                edges: [],
            },
            detailTableData: [],
            editMode: 'edit',
            isShowDialog: false,
            currentTrackerStatus: { name: '', meaning: { id: '' } },
            isShowInitialDialog: false,
            isShowSortDialog: false,
            isShowTransitionDialog: false,
            transitionEditMode: 'edit',
            currentTransition: {
                name: '',
                transitionFrom: { id: '' },
                transitionTo: { id: '' }
            },
            isShowReplaceDialog: false,

        };
    },
    mounted() {
        this.$nextTick(function () {
            this.tableHeight =
                window.innerHeight - this.$refs.dataTable.$el.offsetTop - 100;

            // 监听窗口大小变化
            let self = this;
            window.onresize = function () {
                self.tableHeight =
                    window.innerHeight - self.$refs.dataTable.$el.offsetTop - 100;
            };

        });
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
            }]
        },
        onViewChange: function (e) {
            this.viewMode = e.target.value
            this.loadData();
        },
        loadData: function () {
            this.loading = true
            let trackerId = this.$route.params.trackerId
            findOneTracker(trackerId).then(resp => {
                this.tracker = resp;
                this.trackerStatusList = this.tracker.trackerStatuses
                this.stateTransitionList = this.tracker.trackerStateTransitions

                if (this.viewMode === 'FLOW_VIEW') {
                    this.buildGraphData()
                } else if (this.viewMode === 'TABLE_VIEW') {
                    this.buildTableData()
                } else if (this.viewMode === 'DETAIL_VIEW') {
                    this.buildDetalTableData()
                }
                this.loading = false
                console.log(this.tracker)
            })
            // findTrackerStatus(trackerId).then(resp => {
            //     this.trackerStatusList = resp;
            //     findTrackerStateTransitions(trackerId).then(resp2 => {
            //         this.stateTransitionList = resp2

            //         if (this.viewMode === 'FLOW_VIEW') {
            //             this.buildGraphData()
            //         } else if (this.viewMode === 'TABLE_VIEW') {
            //             this.buildTableData()
            //         } else if (this.viewMode === 'DETAIL_VIEW') {
            //             this.buildDetalTableData()
            //         }
            //         this.tracker = { trackerId: trackerId, trackerStatuses: this.trackerStatusList, trackerStateTransitions: this.stateTransitionList }
            //         this.loading = false
            //     })
            // })
        },
        findTransition: function (from, to) {
            let i = 0
            for (i = 0; i < this.stateTransitionList.length; i++) {
                const t = this.stateTransitionList[i]
                if (t.transitionFrom.id === from && t.transitionTo.id === to) {
                    return t
                }
            }

            return null
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
        hasTransition: function (row, column) {
            const item = row[column.field]
            return item != null
        },
        getTooltip: function (row, column) {
            const item = row[column.field]
            if (item) {
                return item.name
            }
            return ''
        },
        onCheckChanged: function (row, column, rowIndex, columnIndex) {
            if (this.hasTransition(row, column)) {
                const item = row[column.field]
                this.onDeleteTransition(item)
            } else {
                this.transitionEditMode = 'create'
                this.currentTransition = {
                    name: this.trackerStatusList[columnIndex - 1].name,
                    transitionFrom: this.trackerStatusList[rowIndex],
                    transitionTo: this.trackerStatusList[columnIndex - 1],
                }
                this.isShowTransitionDialog = true
            }
        },
        buildTableData: function () {
            console.log(this.trackerStatusList)
            console.log(this.stateTransitionList)
            this.gridOptions.columns = []
            let col = 0
            this.gridOptions.columns.push({
                field: 'title',
                title: 'Name', width: 200, resizable: false, slots: { header: 'title_header' }
            })
            for (col = 0; col < this.trackerStatusList.length; col++) {
                const status = this.trackerStatusList[col]
                this.gridOptions.columns.push({ field: 'f' + status.id, title: status.name, align: 'center', slots: { default: 'field_default' } })
            }
            let row = 0
            let data = []
            for (row = 0; row < this.trackerStatusList.length; row++) {
                const statusFrom = this.trackerStatusList[row]
                let item = { title: statusFrom.name }
                let col = 0
                for (col = 0; col < this.trackerStatusList.length; col++) {
                    const statusTo = this.trackerStatusList[col]
                    const key = 'f' + statusTo.id
                    let value = this.findTransition(statusFrom.id, statusTo.id)
                    item[key] = value
                }
                data.push(item)
            }
            this.gridOptions.data = data


            console.log(this.gridOptions)
        },
        buildDetalTableData: function () {
            this.detailTableData = []
            let i = 0
            for (i = 0; i < this.trackerStatusList.length; i++) {
                const statusFrom = this.trackerStatusList[i]
                let item = { status: statusFrom, transitions: [] }
                let j = 0
                for (j = 0; j < this.stateTransitionList.length; j++) {
                    const transition = this.stateTransitionList[j]
                    if (transition.transitionFrom.id === statusFrom.id) {
                        transition.transitionFrom = statusFrom
                        transition.transitionTo = this.findStatus(transition.transitionTo.id)
                        item.transitions.push(transition)
                    }
                }
                this.detailTableData.push(item)
            }
            console.log('detail:', this.detailTableData)
        },
        footerMethod: function ({ columns, data }) {
            return [
                columns.map(column => {
                    return null
                })
            ]
        },
        buildGraphData: function () {

            let i = 0
            for (i = 0; i < this.trackerStatusList.length; i++) {
                const status = this.trackerStatusList[i]
                this.nodeInfo.nodes.push({
                    id: status.id,
                    label: status.name
                })
            }
            for (i = 0; i < this.stateTransitionList.length; i++) {
                const t = this.stateTransitionList[i]
                this.nodeInfo.edges.push({
                    source: t.transitionFrom.id,
                    target: t.transitionTo.id,
                    lineType: 'solid',
                    // label: t.name
                })
            }
            let that = this
            this.$nextTick(function () {
                if (that.graph == null) {
                    that.init()
                }
                that.graph.centerContent(); // 将画布中元素居中展示
            })
        },
        init: function () {
            //创建一个 Graph 对象
            this.graph = new Graph({
                container: document.getElementById("container"),
                width: '100%', //画布容器宽度
                height: '100vh-300px', //画布容器高度
                mousewheel: true, //滚轮缩放
                panning: true //支持平移拖拽
            });
            //定义层次布局Dagre
            const dagreLayout = new DagreLayout({
                type: "dagre", //布局类型
                rankdir: "LR", //布局的方向。T：top（上）；B：bottom（下）；L：left（左）；R：right（右）
                align: "UL", //节点对齐方式。U：upper（上）；D：down（下）；L：left（左）；R：right（右）；undefined (居中)
                ranksep: 50, //层间距（px）。在 rankdir 为 TB 或 BT 时是竖直方向相邻层间距；在 rankdir 为 LR 或 RL 时代表水平方向相邻层间距
                nodesep: 200, //节点间距（px）。在 rankdir 为 TB 或 BT 时是节点的水平间距；在 rankdir 为 LR 或 RL 时代表节点的竖直方向间距
                controlPoints: true, //是否保留布局连线的控制点
            });
            //分别动态添加节点和边的样式
            this.nodeInfo.nodes.map((item) => {
                Object.assign(item, { width: 130, height: 40 });
            });
            this.nodeInfo.edges.map((item) => {
                if (item.lineType === "solid") {
                    Object.assign(item, {
                        //路由将边的路径点 vertices 做进一步转换处理
                        router: {
                            name: "manhattan", //智能正交路由
                            args: {
                                startDirections: ["right"], // 支持从哪些方向开始路由
                                endDirections: ["left"], // 支持从哪些方向结束路由
                            },
                        },
                        //定义实线样式
                        attrs: {
                            line: { stroke: "#0e639c" }, // line 指代的元素代表了边的主体
                        },
                    });
                } else if (item.lineType === "dotted") {
                    Object.assign(item, {
                        router: {
                            name: "manhattan", //智能正交路由，由水平或垂直的正交线段组成，并自动避开路径上的其他节点（障碍)
                            args: {
                                startDirections: ["bottom"], // 支持从哪些方向开始路由
                                endDirections: ["left"], // 支持从哪些方向结束路由
                            },
                        },
                        //定义虚线样式
                        attrs: {
                            line: { strokeDasharray: "5 5", stroke: "#51ff51" }, // line 指代的 元素代表了边的主体
                        },
                    });
                }
            });
            //判断是否需要dagre布局(通过控制dagreLayout字段)
            if (true) {
                //应用dagre布局
                const model = dagreLayout.layout(this.nodeInfo);
                //渲染画布
                this.graph.fromJSON(model);
            } else {
                this.graph.fromJSON(this.nodeInfo);
            }
        },
        getTrackerStatusColor: function (trackerStatus) {
            return trackerStatus.meaning.color
        },
        onCreateTrackerStatus: function () {
            this.editMode = 'create'
            this.currentTrackerStatus = { name: '', meaning: { id: '' } }
            this.isShowDialog = true
        },
        onEditTrackStatus: function (trackerStatus) {
            this.editMode = 'edit'
            this.currentTrackerStatus = trackerStatus
            this.isShowDialog = true
        },
        onDeleteTrackerStatus: async function (trackerStatus) {
            let used = await trackerStatusUseByTransition(this.tracker.id, trackerStatus)
            if (used) {
                VXETable.modal.alert({
                    status: 'warning',
                    title: '移除工作项状态',
                    content: '「' + trackerStatus.name + '」状态无法移除，当前已被工作项步骤使用，请先删除工作项步骤后再进行移除操作'
                })
            } else {
                used = await trackerStatusUseByWorkItem(this.tracker.id, trackerStatus)
                if (used) {
                    VXETable.modal.confirm({
                        status: 'warning',
                        title: '移除工作项状态',
                        content: '「' + trackerStatus.name + '」状态无法移除，当前已被工作项使用，是否修改相关工作项的状态后再进行移除操作'
                    }).then(type => {
                        if (type == 'confirm') {
                            this.currentTrackerStatus = trackerStatus
                            this.isShowReplaceDialog = true
                        }
                    })

                } else {
                    VXETable.modal.confirm({
                        title: '移除工作项状态',
                        content: '「' + trackerStatus.name + '」将被删除，该动作不可恢复'
                    }).then(type => {
                        if (type === 'confirm') {
                            deleteTrackerStatus(this.tracker.id, trackerStatus).then(resp => {
                                VXETable.modal.message({ content: '「' + trackerStatus.name + '」已被删除', status: 'info' })
                                this.loadData()
                            })
                        }
                    })
                }
            }
        },
        onTrackerStatusDialogOK: function (trackerStatus) {
            if (this.editMode === 'create') {
                createTrackerStatus(this.tracker.id, trackerStatus).then(resp => {
                    this.isShowDialog = false
                    this.loadData()
                })
            } else {
                updateTrackerStatus(this.tracker.id, trackerStatus).then(resp => {
                    this.isShowDialog = false
                    this.loadData()
                })
            }
        },
        onTrackerStatusDialogCancel: function () {
            this.isShowDialog = false
        },
        onShowStatusInitialDialog: function () {
            this.isShowInitialDialog = true
        },
        onTrackerStatusInitialDialogOK: function (trackerStatus) {
            setInitialTrackerStatus(this.tracker.id, trackerStatus).then(resp => {
                this.loadData()
                this.isShowInitialDialog = false
            })
        },
        onTrackerStatusInitialDialogCancel: function (trackerStatus) {
            this.isShowInitialDialog = false
        },
        onShowStatusSortDialog: function () {
            this.isShowSortDialog = true
        },
        onTrackerStatusSortDialogOK: function (statusList) {
            sortTrackerStatus(this.tracker.id, statusList).then(resp => {
                this.loadData()
                this.isShowSortDialog = false
            })
        },
        onTrackerStatusSortDialogCancel: function () {
            this.isShowSortDialog = false
        },
        onCreateTransition: function (existed) {
            this.transitionEditMode = 'create'
            this.currentTransition = {
                name: '',
                transitionFrom: { id: '' },
                transitionTo: { id: '' }
            }
            if (existed) {
                this.currentTransition.transitionFrom = existed
            }
            this.isShowTransitionDialog = true
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
        onEditTransition: function (transition) {
            this.$router.push({ name: 'trackerStateTransitionConfigEdit', params: { trackerId: this.tracker.id, transitionId: transition.id } })
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
            if (this.transitionEditMode === 'create') {
                createTrackerStateTransition(this.tracker.id, transition).then(resp => {
                    this.loadData()
                    this.isShowTransitionDialog = false
                })
            } else {
                renameTrackerStateTransition(this.tracker.id, transition).then(resp => {
                    this.loadData()
                    this.isShowTransitionDialog = false
                })
            }

        },
        onStateTransitiontDialogCancel: function (transition) {
            this.isShowTransitionDialog = false
        },
        onTrackerStatusReplaceDialogOK(newTrackerStatus) {
            replaceWorkItemStatus(this.tracker.id, this.currentTrackerStatus.id, newTrackerStatus.id).then(resp => {
                this.loadData()
                this.isShowReplaceDialog = false
            })
        },
        onTrackerStatusReplaceDialogCancel() {
            this.isShowReplaceDialog = false
        }

    },
};
</script>
<style lang="less" scoped>
.table-view {
    .first-col {
        position: relative;
        height: 20px;
    }

    .first-col:before {
        content: "";
        position: absolute;
        left: -14px;
        top: 10px;
        width: 204px;
        height: 1px;
        transform: rotate(13deg);
        background-color: #e8eaec;
    }

    .first-col .first-col-top {
        position: absolute;
        right: 4px;
        top: -10px;
    }

    .first-col .first-col-bottom {
        position: absolute;
        left: 4px;
        bottom: -10px;
    }
}

.status-table {
    margin-top: 20px;

    .status-title-container {
        height: 48px;
        display: flex;
        align-items: center;

        .status-title {
            font-size: 18px;
            color: #606060;
            display: flex;
            align-items: center;
            flex: auto;

            .task-status-title {
                display: flex;

                .task-status-title-content {
                    font-size: 18px;
                    line-height: 20px;
                    border: 0;
                    max-width: unset;
                    padding-right: 0;
                    border-radius: unset;
                    font-weight: 500 !important;
                    box-sizing: border-box;
                    display: inline-block;
                    height: 20px;
                    line-height: 18px;
                    transition: border-color .2s;
                    border-radius: 20px;
                    padding: 0px 8px;
                }
            }
        }
    }

    .transition-status {
        min-width: 110px;
        display: flex;
        align-items: center;
        margin-right: 20px;
        white-space: nowrap;

        .ui-tag-status {
            max-width: 110px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            font-size: 12px;
            box-sizing: border-box;
            display: inline-block;
            height: 20px;
            line-height: 18px;
            transition: border-color .2s;
            border: 1px solid;
            border-radius: 20px;
            padding: 0px 8px;
        }
    }
}

:tracker-not-start {
    color: #338fe5
}

:tracker-in-progress {
    color: #e39f48;
}

:tracker-resolved {
    color: #24b47e;
}

:tacker-closed {
    color: #606060;
}
</style>
