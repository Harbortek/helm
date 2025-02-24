<template>
    <config-page title="" description="项目计划主要用于规划项目计划和跟踪项目进度，进行WBS 计划拆解和里程碑、交付物管理，将项目计划与迭代、任务管理工具紧密结合，确保计划稳步推进。">
        <a-layout style="width: 100%;height: 100%;">
            <a-layout-sider width="900" style="background-color: #fff;" :collapsible="false">
                <vxe-toolbar ref="xToolbar" size="medium" style="margin-right: 5px;">
                    <template #buttons>
                        <a-space>
                            <a-button @click="onClapseAll"><h-icon type="collapse_all" />收缩</a-button>
                            <a-button @click="onExpandAll"><h-icon type="expand_all" />展开</a-button>
                        </a-space>
                    </template>
                    <template #tools>
                        <a-space>
                            <a-dropdown>
                                <vxe-button v-action="'PAGE_WRITE|' + pageId" type="primary"
                                    icon="vxe-icon-add">新建</vxe-button>
                                <a-menu slot="overlay">
                                    <a-menu-item>
                                        <a @click="onCreateFolder()"><a-space><a-icon
                                                    type="folder-add" />新增计划组</a-space></a>
                                    </a-menu-item>
                                    <a-menu-item>
                                        <a @click="onCreateTask()"> <a-space><a-icon type="file-add" />新增计划</a-space>
                                        </a>
                                    </a-menu-item>
                                    <a-menu-item>
                                        <a @click="onCreateMilestone()"><a-space> <a-icon
                                                    type="file-add" />新增里程碑</a-space></a>
                                    </a-menu-item>
                                </a-menu>
                            </a-dropdown>
                            <vxe-button v-action="'PAGE_WRITE|' + pageId" type="primary" @click="onAutoPlan"
                                icon="vxe-icon-import">自动排期</vxe-button>
                            <a-upload name="file" :multiple="false" :showUploadList="false" :directory="false"
                                :beforeUpload="beforeUpload" :customRequest="onUpload">
                                <vxe-button v-action="'PAGE_WRITE|' + pageId" type="primary"
                                    icon="vxe-icon-import">导入</vxe-button>
                            </a-upload>


                            <vxe-button v-action="'PAGE_WRITE|' + pageId" type="primary" @click="onExport"
                                icon="vxe-icon-export">导出</vxe-button>
                        </a-space>

                    </template>
                </vxe-toolbar>
                <div style="height: calc(100% - 48px);">
                    <vxe-table size="mini" ref="xTable" show-overflow border row-key :show-header="true" height="100%"
                        keep-source :data="tableData" :checkbox-config="{ labelField: 'name' }"
                        :row-config="{ keyField: 'id', isCurrent: true, isHover: true }"
                        :tree-config="{ transform: true, accordion: false, line: true, rowField: 'id', parentField: 'parentId', iconOpen: 'vxe-icon-square-minus', iconClose: 'vxe-icon-square-plus' }"
                        :edit-config="{ trigger: 'click', mode: 'cell', showStatus: true, showUpdateStatus: true, beforeEditMethod: beforeEditMethod, icon: 'vxe-icon-edit' }"
                        :edit-rules="{}" :row-class-name="rowClassName" @current-change="tableRowSelected"
                        @toggle-tree-expand="toogleTreeExpand" @edit-closed="tableEditClosed">
                        <vxe-column title="" field="seqNumber" width="70px">
                            <template #default="{ row }">
                                <span class="dragIcon"><h-icon type="drag"></h-icon></span>
                                <span>{{ row.seqNumber }}</span>
                            </template>
                        </vxe-column>
                        <vxe-column field="name" title="名称" tree-node :edit-render="{}">
                            <template #default="{ row }">
                                <div style="display: inline-flex;">
                                    <div style="width:20px;">
                                        <h-icon type="plan-group" v-if="row.type === 'GROUP'" />
                                        <h-icon type="plan-tasks" v-else-if="row.type === 'TASK'" />
                                        <h-icon type="flag" v-else-if="row.type === 'MILE_STONE'" />
                                    </div> {{ row.name }}
                                </div>
                            </template>
                            <template #edit="scope">
                                <vxe-input v-model="scope.row.name" placeholder="请输入名称" @change="onNameChange(scope)" />
                            </template>
                        </vxe-column>
                        <!-- <vxe-column field="type" title="类型" width="120px">
                            <template #default="{ row }">
                                {{ $t('plan.type.' + row.type) }}
                            </template>
                        </vxe-column> -->
                        <vxe-column field="progress" title="进度" width="80px" :formatter="formatProgress"
                            :edit-render="{}">
                            <template #edit="scope">
                                <vxe-input v-model="scope.row.progress" type="integer" min="0" max="100"
                                    @input="onProgressChange(scope)" />
                            </template>
                            <!-- <template #default="{ row }">
                                {{ formatProgress(row.progress) }}
                            </template> -->
                        </vxe-column>
                        <vxe-column field="owner.name" title="负责人" width="100px" :edit-render="{ name: 'select' }">
                            <template #edit="scope">
                                <project-user-select :projectId="projectId" v-model="scope.row.owner.id"
                                    style="wdith:100px;" transfer @change="onOwnerChange(scope)" />
                            </template>
                            <template #default="{ row }">
                                <template v-if="row.owner.id">
                                    <h-avatar :name="row.owner?.name" :icon="row.owner?.icon"></h-avatar>
                                </template>
                                <template v-else>
                                    -
                                </template>
                            </template>
                        </vxe-column>
                        <vxe-column field="planStartDate" title="开始日期" width="100" :edit-render="{}">
                            <template #edit="scope">
                                <vxe-input v-model="scope.row.planStartDate" type="date" transfer
                                    @change="onPlanStartDateChange(scope)"></vxe-input>
                            </template>
                            <template #default="{ row }">
                                {{ formatDate(row.planStartDate) }}
                            </template>
                        </vxe-column>
                        <vxe-column field="planEndDate" title="结束日期" width="100px" :edit-render="{}">
                            <template #edit="scope">
                                <vxe-input v-model="scope.row.planEndDate" type="date" transfer
                                    @change="onPlanEndDateChange(scope)"></vxe-input>
                            </template>
                            <template #default="{ row }">
                                {{ formatDate(row.planEndDate) }}
                            </template>
                        </vxe-column>
                        <vxe-column field="duration" title="工期(天)" width="100px" :edit-render="{}" align="right">
                            <template #edit="scope">
                                <vxe-input v-model="scope.row.duration" type="integer" min="0" max="1000"
                                    @change="onDurationChange(scope)"></vxe-input>
                            </template>
                            <template #default="{ row }">
                                {{ row.duration }}
                            </template>
                        </vxe-column>
                        <vxe-column field="duration" title="前置计划" width="100px">
                            <template #default="{ row }">
                                {{ formatTasks(row.preTasks) }}
                            </template>
                        </vxe-column>


                        <vxe-column title="" header-align="center" width="40">
                            <template #header>
                                <a-icon type="setting" />
                            </template>
                            <template #default="{ row }">
                                <a-dropdown>
                                    <vxe-button v-action="'PAGE_WRITE|' + pageId" type="text"
                                        icon="vxe-icon-ellipsis-v"></vxe-button>
                                    <a-menu slot="overlay">
                                        <template v-if="row.type === 'GROUP'">
                                            <a-menu-item>
                                                <a @click="onCreateFolder(row.id)"><a-space><a-icon
                                                            type="folder-add" />新增下级计划组</a-space></a>
                                            </a-menu-item>
                                            <a-menu-item>
                                                <a @click="onCreateTask(row.id)"><a-space> <a-icon
                                                            type="file-add" />新增下级计划</a-space></a>
                                            </a-menu-item>
                                            <a-menu-item>
                                                <a @click="onCreateMilestone(row.id)"><a-space><a-icon
                                                            type="file-add" />新增下级里程碑</a-space></a>
                                            </a-menu-item>
                                            <a-menu-divider />
                                        </template>
                                        <a-menu-item v-if="row.type === 'TASK' || row.type === 'MILE_STONE'">
                                            <a @click="showDetail(row)"> <a-space><a-icon type="form" />详情</a-space></a>
                                        </a-menu-item>


                                        <a-menu-item>
                                            <a @click="onEditPlan(row)"> <a-space><a-icon type="edit" />编辑</a-space></a>
                                        </a-menu-item>
                                        <a-menu-item>
                                            <a @click="onDeletePlan(row)"> <a-space><a-icon
                                                        type="delete" />删除</a-space></a>
                                        </a-menu-item>
                                    </a-menu>
                                </a-dropdown>
                            </template>
                        </vxe-column>
                    </vxe-table>
                </div>

            </a-layout-sider>
            <a-layout-content>
                <div ref="gantt" style='height:calc(100% );background-color: #fff;'></div>

            </a-layout-content>
        </a-layout>
        <plan-dialog :isShowDialog="showCreateDialog" :projectId="projectId" :editMode="editMode"
            :currentPlan="currentPlan" :plans="tableData" @ok="onCreatePlanOK" @cancel="showCreateDialog = false" />
        <div :w="240" :h="42" :isResizable="false" :x="zoomControlBarX" :y="zoomControlBarY" :z="1000"
            :sticks="['mr']">
            <div class="gantt-zoomControl-bar">
                <a-space>
                    <a-button @click="moveToday">今天</a-button>
                    <a-select v-model="scales" @change="onScaleChange" style="width:80px;">
                        <a-select-option key="day" value="day">天</a-select-option>
                        <a-select-option key="week" value="week">周</a-select-option>
                        <a-select-option key="month" value="month">月</a-select-option>
                        <a-select-option key="quarter" value="quarter">季度</a-select-option>
                        <a-select-option key="year" value="year">年</a-select-option>
                    </a-select>
                    <a-button icon="minus" @click="zoomOut"></a-button>
                    <a-button icon="plus" @click="zoomIn"></a-button></a-space>
            </div>
        </div>
    </config-page>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import VXETable from "vxe-table";
import Sortable from "sortablejs"
import XEUtils from "xe-utils";
import { hasPermission } from '@/utils/permission'
import { buildGantt, autoPlan, createPlan, updatePlan, deletePlan, changePlanOrders, importMPP, exportPlans } from '@/services/plan/PlanService'
import { gantt } from 'dhtmlx-gantt';
import PlanDialog from './PlanDialog.vue';
import moment from "moment";
import "moment/locale/zh-cn";
moment.locale("zh-cn");
import { formatDate, isWeekend } from '@/utils/DateUtils'
import ProjectUserSelect from '@/components/select/ProjectUserSelect2.vue';
import { uploadFile, downloadFile } from '@/services/global/FileService'
export default {
    name: 'PlanList',
    components: { ConfigPage, gantt, PlanDialog, ProjectUserSelect, HAvatar },
    data() {
        return {
            loading: false,
            tableData: [],
            editMode: 'create',
            showDialog: false,
            currentPlan: undefined,
            tasks: {
                data: [
                    { id: 1, text: 'Task #1', planStartDate: '2020-01-17', duration: 3, progress: 0.6 },
                    { id: 2, text: 'Task #2', planStartDate: '2020-01-20', duration: 3, progress: 0.4 }
                ],
                links: [
                    { id: 1, source: 1, target: 2, type: '0' }
                ]
            },
            // 展示单位
            scales: 'week',
            showCreateDialog: false,
            currentPlan: undefined,
            zoomControlBarX: 1000,
            zoomControlBarY: 500,
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },

    },
    mounted() {
        this.initGantt()

        this.loadData()
    },
    beforeDestroy() {
        if (this.sortable) {
            this.sortable.destroy();
        }
    },
    methods: {
        formatDate(v) {
            if (!v) return '-'
            else return formatDate(v)
        },
        formatProgress({ cellValue }) {
            if (!cellValue) return '-'
            else return cellValue + '%'
        },
        initGantt() {
            gantt.config.order_branch = false; //左边表格 列可以拖动排序
            gantt.config.show_progress = true; //取消右边表格进度条显示与拖动
            gantt.config.min_grid_column_width = 120; //设置左侧表格宽度
            gantt.config.show_links = true; // 禁用连线 
            gantt.config.drag_progress = false; //禁用工作进度拖拽
            gantt.config.duration_unit = 'day'; // 设置任务分段参数以及单位
            gantt.config.duration_step = 1;
            gantt.config.drag_resize = false; // 可以通过拖放来调整任务大小
            gantt.config.drag_move = false; // 可以通过拖放来移动任务

            gantt.config.open_split_tasks = true;
            gantt.config.open_tree_initially = true;

            gantt.i18n.setLocale("cn");//设置语音为中文


            gantt.config.show_grid = false

            gantt.config.xml_date = '%Y-%m-%d'; // 日期格式化的匹配格式
            gantt.config.scale_height = 86; // 日期栏的高度
            gantt.config.row_height = 36;

            gantt.config.work_time = true;
            gantt.templates.timeline_cell_class = function (task, date) {
                if (!gantt.isWorkTime(date) || isWeekend(date)) {
                    return "week_end";
                }


                return "";
            };

            gantt.config.readonly = true;

            // const scalesInfo = {
            //     week: [
            //         { unit: 'year', step: 1, format: '%Y' },
            //         { unit: 'week', step: 1, format: this.weekScaleTemplate },
            //     ],
            //     month: [
            //         { unit: 'year', step: 1, format: '%Y' },
            //         { unit: 'month', step: 1, format: '%M' },
            //     ],
            //     year: [
            //         { unit: "year", step: 1, format: "%Y" },
            //         { unit: "month", step: 1, format: "%M" }
            //     ],
            // }
            // gantt.config.scales = scalesInfo[this.scales];

            gantt.config.layout = {
                css: "gantt_container",
                rows: [
                    {
                        cols: [
                            {
                                // the default grid view  
                                view: "grid",
                                scrollX: "scrollHor",
                                scrollY: "scrollVer"
                            },
                            { resizer: true, width: 1 },
                            {
                                // the default timeline view
                                view: "timeline",
                                scrollX: "scrollHor",
                                scrollY: "scrollVer"
                            },
                            {
                                view: "scrollbar",
                                id: "scrollVer"
                            }
                        ]
                    },
                    {
                        view: "scrollbar",
                        id: "scrollHor"
                    }
                ]
            }

            gantt.config.types["taskgroup"] = "taskgroup";
            gantt.locale.labels["type_taskgroup"] = "计划组";
            gantt.templates.task_class = function (start, end, task) {
                if (task.type === gantt.config.types.taskgroup) {
                    return "task_group";
                }
                return "";
            };

            var zoomConfig = {
                levels: [
                    {
                        name: "day",
                        scale_height: 27,
                        min_column_width: 80,
                        scales: [
                            { unit: "day", step: 1, format: "%M%d" }
                        ]
                    },
                    {
                        name: "week",
                        scale_height: 50,
                        min_column_width: 50,
                        scales: [
                            {
                                unit: "week", step: 1, format: this.weekScaleTemplate
                            },
                            { unit: "day", step: 1, format: "%d" }
                        ]
                    },
                    {
                        name: "month",
                        scale_height: 50,
                        min_column_width: 120,
                        scales: [
                            { unit: "month", format: "%Y %F" },
                            { unit: "week", format: "第#%W周" }
                        ]
                    },
                    {
                        name: "quarter",
                        height: 50,
                        min_column_width: 90,
                        scales: [
                            { unit: "month", step: 1, format: "%M" },
                            {
                                unit: "quarter", step: 1, format: function (date) {
                                    var dateToStr = gantt.date.date_to_str("%M");
                                    var endDate = gantt.date.add(gantt.date.add(date, 3, "month"), -1, "day");
                                    return dateToStr(date) + " - " + dateToStr(endDate);
                                }
                            }
                        ]
                    },
                    {
                        name: "year",
                        scale_height: 50,
                        min_column_width: 30,
                        scales: [
                            { unit: "year", step: 1, format: "%Y" }
                        ]
                    }
                ]
            };

            gantt.ext.zoom.init(zoomConfig);
            gantt.ext.zoom.setLevel(this.scales);
            let that = this
            gantt.ext.zoom.attachEvent("onAfterZoom", function (level, config) {
                that.scales = config.name
            })

            // 添加今日的Marker
            gantt.plugins({
                // marker: true，
                drag_timeline: true
            });
            // gantt.addMarker({
            //     start_date: new Date(),
            //     text: '今日'
            // });
            // 添加今日的Marker end
            //其他事件 (禁用原来自带的弹窗)
            gantt.attachEvent(
                'onBeforeLightbox',
                function (id) {
                    return false; // 返回 false
                },
                {}
            );
            //禁用任务双击进入编辑事件
            gantt.attachEvent(
                'onTaskDblClick',
                function (id, e) {
                    console.log('id', id, e);
                    dialogVisible.value = true;
                    return false;
                },
                {}
            );

            const xTable = this.$refs.xTable

            gantt.attachEvent('onTaskSelected', (id) => {

                xTable.setCurrentRow(this.findRowById(id))
            });
            this.zoomControlBarX = window.innerWidth - 500
            this.zoomControlBarY = window.innerHeight - 100
            // 给年月展示框加类名
            // gantt.templates.scale_cell_class = () => 'month-box';
            gantt.init(this.$refs.gantt); //这里可以通过ref挂载 或者 id选择器挂载

        },
        weekScaleTemplate(date) {
            // 可以时使用dayjs 处理返回
            const dateToStr = gantt.date.date_to_str('%d');
            const mToStr = gantt.date.date_to_str('%M');
            const planEndDate = gantt.date.add(gantt.date.add(date, 1, 'week'), -1, 'day');
            // 处理一下星期
            return `${mToStr(planEndDate)}${dateToStr(planEndDate)}日`;
        },
        loadData() {
            this.loading = true
            buildGantt(this.projectId).then(resp => {
                this.tableData = resp.tasks || []
                this.tableData.forEach(f => { f.owner = f.owner || { id: '' } })
                this.loading = false
                if (!this.sortable) {
                    this.rowDrop()
                }
                this.tasks = { data: [], links: resp.links }
                this.tableData.forEach(item => {
                    this.tasks.data.push({
                        id: item.id,
                        parent: item.parentId,
                        text: item.name,
                        start_date: moment(item.planStartDate).toDate(),
                        end_date: moment(item.planEndDate).toDate(),
                        duration: item.duration,
                        progress: (item.progress || 0) / 100,
                        type: this.getGanttType(item.type)
                    })
                })
                console.log(this.tasks)
                gantt.parse(this.tasks);
                this.$nextTick(() => {
                    this.$refs.xTable.setAllTreeExpand(true)
                    if (this.currentPlan) {
                        this.$refs.xTable.setCurrentRow(this.findRowById(this.currentPlan.id))
                    }
                })
            })
        },
        getGanttType(type) {
            if (type === 'GROUP') return 'taskgroup'
            if (type === 'TASK') return 'task'
            if (type === 'MILE_STONE') return 'milestone'
        },
        rowClassName() {
            return 'plan_row'
        },
        formatTasks(tasks) {
            let taskNos = tasks.map(item => { return item.seqNumber })
            const str = taskNos.join(',')
            return str
        },
        findRowById(id) {
            return this.tableData.filter(item => { return item.id === id })[0]
        },
        tableRowSelected({ newValue, oldValue, row, rowIndex, $rowIndex, column, columnIndex, $columnIndex, $event }) {
            this.currentPlan = row
            gantt.selectTask(newValue.id);
            if (newValue.planStartDate) {
                gantt.showDate(moment(newValue.planStartDate).toDate());
            } else if (newValue.planEndDate) {
                gantt.showDate(moment(newValue.planEndDate).toDate());
            }
        },
        toogleTreeExpand({ expanded, row, column, columnIndex, $columnIndex, $event }) {
            if (expanded) {
                gantt.open(row.id)
            } else {
                gantt.close(row.id)
            }
        },
        beforeEditMethod({ row, rowIndex, column, columnIndex }) {
            if (!hasPermission("PAGE_WRITE", this.pageId)) return false;
            if (row.type === 'GROUP') return column.field === 'owner.name' || column.field === 'name'
            else if (row.type === 'TASK') return true
            else if (row.type === 'MILE_STONE') return column.field === 'planEndDate' || column.field === 'owner.name' || column.field === 'name'
        },
        tableEditClosed({ row, column }) {
            const $table = this.$refs.xTable
            const field = column.property
            const cellValue = row[field]
            this.currentPlan = row
            // 判断单元格值是否被修改
            if (row.updated) {
                updatePlan(row).then(resp => {
                    for (let i = 0; i < this.tableData.length; i++) {
                        if (this.tableData[i].id === row.id) {
                            this.tableData[i] = resp
                            const item = this.tableData[i]
                            this.tasks.data[i] = {
                                id: item.id,
                                parent: item.parentId,
                                text: item.name,
                                start_date: moment(item.planStartDate).toDate(),
                                end_date: moment(item.planEndDate).toDate(),
                                duration: item.duration,
                                progress: (item.progress || 0) / 100,
                                type: this.getGanttType(item.type)
                            }
                            break
                        }
                    }
                    gantt.parse(this.tasks);
                    Object.assign(row, resp)
                    row.updated = false
                    $table.reloadRow(row, resp, column.field)
                })
            }
        },
        onCellDblclick({ row, column }) {
            if (column.field === 'name') {
                this.onEditPlan(row)
            }
        },
        moveToday() {
            gantt.showDate(new Date());
        },
        zoomOut() {
            gantt.ext.zoom.zoomOut()
        },
        zoomIn() {
            gantt.ext.zoom.zoomIn()
        },
        onScaleChange(level) {
            gantt.ext.zoom.setLevel(level)
        },
        onNameChange(scope) {
            scope.row.updated = true
        },
        onProgressChange(scope) {
            scope.row.updated = true
        },
        onOwnerChange(scope) {
            scope.row.updated = true
        },
        onPlanStartDateChange(scope) {
            const { row } = scope
            if (row.planStartDate && row.duration) {
                row.planEndDate = moment(row.planStartDate).clone().add(row.duration, 'days').format('YYYY-MM-DD')
            }
            scope.row.updated = true
        },
        onPlanEndDateChange(scope) {
            const { row } = scope
            if (row.planStartDate && row.planEndDate) {
                let duration = moment(row.planEndDate).diff(moment(row.planStartDate), 'days')
                row.duration = duration
            }
            scope.row.updated = true
        },
        onDurationChange(scope) {
            const { row } = scope
            if (row.planStartDate && row.duration) {
                row.planEndDate = moment(row.planStartDate).clone().add(row.duration, 'days').format('YYYY-MM-DD')
            }
            scope.row.updated = true
        },
        onCreateFolder(parentId) {
            this.editMode = 'create'
            this.currentPlan = { projectId: this.projectId, name: '', type: 'GROUP', parentId: parentId, owner: { id: '' } }
            this.showCreateDialog = true
        },
        onCreateTask(parentId) {
            this.editMode = 'create'
            this.currentPlan = { projectId: this.projectId, name: '', type: 'TASK', parentId: parentId, planStartDate: moment().format('YYYY-MM-DD'), duration: 1, owner: { id: '' } }
            this.showCreateDialog = true
        },
        onCreateMilestone(parentId) {
            this.editMode = 'create'
            this.currentPlan = { projectId: this.projectId, name: '', type: 'MILE_STONE', parentId: parentId, planEndDate: moment().format('YYYY-MM-DD'), owner: { id: '' } }
            this.showCreateDialog = true
        },
        onEditPlan(item) {
            this.editMode = 'edit'
            this.currentPlan = item
            this.showCreateDialog = true
        },
        onDeletePlan(row) {
            VXETable.modal.confirm({
                title: '删除计划',
                content: '「' + row.name + '」的数据将被删除'
            }).then(type => {
                if (type === 'confirm') {
                    deletePlan(row.id).then(resp => {
                        this.loadData()
                    })
                }
            })
        },
        onCreatePlanOK(item) {
            if (this.editMode === 'create') {
                createPlan(item).then(resp => {
                    this.loadData()
                    this.showCreateDialog = false
                })
            } else {
                updatePlan(item).then(resp => {
                    this.loadData()
                    this.showCreateDialog = false
                })
            }
        },
        showDetail(row) {
            if (row.type == 'TASK') {
                this.$router.push({ name: 'taskDetail', params: { itemId: row.id } })
            } else if (row.type === 'MILE_STONE') {
                this.$router.push({ name: 'milestoneDetail', params: { itemId: row.id } })
            }
        },
        rowDrop() {
            let that = this
            this.$nextTick(() => {
                let xTable = that.$refs.xTable;
                if (xTable) {
                    that.sortable = Sortable.create(
                        xTable.$el.querySelector(".body--wrapper>.vxe-table--body tbody"),
                        {
                            handle: ".vxe-body--row",
                            filter: '.no-drag',
                            animation: 150,
                            onEnd: ({ item, oldIndex }) => {
                                const options = { children: 'children' }
                                const targetTrElem = item
                                const wrapperElem = targetTrElem.parentNode
                                const prevTrElem = targetTrElem.previousElementSibling
                                const tableData = that.tableData
                                const selfRow = xTable.getRowNode(targetTrElem).item
                                const selfNode = XEUtils.findTree(tableData, row => row === selfRow, options)
                                if (prevTrElem) {
                                    // 移动到节点
                                    const prevRow = xTable.getRowNode(prevTrElem).item
                                    const prevNode = XEUtils.findTree(tableData, row => row === prevRow, options)
                                    if (XEUtils.findTree(selfRow[options.children], row => prevRow === row, options)) {
                                        // 错误的移动
                                        const oldTrElem = wrapperElem.children[oldIndex]
                                        wrapperElem.insertBefore(targetTrElem, oldTrElem)
                                        return this.$XModal.message({ content: '不允许自己给自己拖动！', status: 'error' })
                                    }
                                    const currRow = selfNode.items[selfNode.index]

                                    if (currRow.type === 'TASK' || currRow.type === 'MILE_STONE') {
                                        const currnetRowIndex = XEUtils.findIndexOf(tableData, row => {
                                            return row.id === currRow.id
                                        })
                                        tableData.splice(currnetRowIndex, 1)
                                        const parentIndex = XEUtils.findIndexOf(tableData, row => {
                                            return row.id === prevRow.parentId
                                        })
                                        const prevIndex = XEUtils.findIndexOf(tableData, row => {
                                            return row.id === prevRow.id
                                        })
                                        if (prevRow.type === 'TASK' || prevRow.type === 'MILE_STONE') {
                                            //移动到某个父节点下
                                            currRow.parentId = prevRow.parentId
                                            tableData.splice(prevIndex + 1, 0, currRow)
                                        } else if (prevRow.type === 'GROUP') {
                                            //移动到顶层节点后
                                            if (xTable.isTreeExpandByRow(prevRow)) {
                                                // 移动到prevRow下作为子节点
                                                currRow.parentId = prevRow.id
                                                tableData.splice(prevIndex + 1, 0, currRow)
                                            }
                                            // else if (currRow.level == 2) {
                                            //     // prevRow是个空的父节点
                                            //     currRow.parentId = prevRow.id
                                            //     tableData.splice(prevIndex + 1, 0, currRow)
                                            //     xTable.setTreeExpand([currRow], true)
                                            // }
                                            else {
                                                // 移动到prevRow后作为平行节点
                                                currRow.parentId = null
                                                tableData.splice(prevIndex + 1, 0, currRow)
                                            }
                                        }
                                    } else {
                                        const prevParentId = (prevRow.type === 'GROUP') ? prevRow.id : prevRow.parentId
                                        const startIndex = XEUtils.findIndexOf(tableData, row => { return row.id === currRow.id || row.parentId == currRow.id })
                                        const endIndex = XEUtils.findLastIndexOf(tableData, row => { return row.id === currRow.id || row.parentId == currRow.id })
                                        const toInsert = tableData.splice(startIndex, endIndex - startIndex + 1)

                                        const endIndexToInsert = XEUtils.findLastIndexOf(tableData, row => { return row.id === prevParentId || row.parentId == prevParentId })

                                        for (let i = 0; i < toInsert.length; i++) {
                                            tableData.splice(endIndexToInsert + 1 + i, 0, toInsert[i])
                                        }

                                    }

                                } else {
                                    // 移动到第一行

                                    const currRow = selfNode.items[selfNode.index]
                                    if (currRow.type === 'GROUP') {
                                        const startIndex = XEUtils.findIndexOf(tableData, row => { return row.id === currRow.id || row.parentId == currRow.id })
                                        const endIndex = XEUtils.findLastIndexOf(tableData, row => { return row.id === currRow.id || row.parentId == currRow.id })
                                        const toInsert = tableData.splice(startIndex, endIndex - startIndex + 1)
                                        for (let i = 0; i < toInsert.length; i++) {
                                            tableData.splice(i, 0, toInsert[i])
                                        }
                                    } else {
                                        const currnetRowIndex = XEUtils.findIndexOf(this.tableData, row => {
                                            return row.id === currRow.id
                                        })
                                        tableData.splice(currnetRowIndex, 1)
                                        currRow.parentId = null
                                        tableData.splice(0, 0, currRow)
                                    }

                                }
                                // 如果变动了树层级，需要刷新数据
                                that.tableData = [...tableData]
                                // this.tableData = xTable.getTableData().tableData
                                console.log(that.tableData)
                                changePlanOrders(this.tableData).then(resp => {
                                    this.loadData()
                                })
                            }
                        }
                    );
                }
            });
        },
        onClapseAll() {
            this.$nextTick(() => {
                this.$refs.xTable.setAllTreeExpand(false)
                const tableData = this.$refs.xTable.getTableData().tableData
                tableData.forEach(row => {
                    if (row.type === 'GROUP') {
                        gantt.close(row.id)
                    }
                })
            })
        },
        onExpandAll() {
            this.$nextTick(() => {
                this.$refs.xTable.setAllTreeExpand(true)
                const tableData = this.$refs.xTable.getTableData().tableData
                tableData.forEach(row => {
                    if (row.type === 'GROUP') {
                        gantt.open(row.id)
                    }
                })
            })
        },
        beforeUpload(file) {
            return file.name.endsWith('.mpp')
        },
        async onUpload(e) {
            const type = await VXETable.modal.confirm('上传文件将清楚当前所有项目计划数据?')
            if (type === 'confirm') {
                uploadFile(e.file).then((resp) => {
                    e.onSuccess(resp, e.file)
                    this.attachment = { name: resp.origin_name, filePath: resp.url, fileSize: resp.fileSize }
                    importMPP(this.projectId, resp.url).then(resp => {
                        this.$message.success('导入成功')
                        this.loadData()
                    })
                })
            }
        },
        onImport() {

        },
        onExport() {
            exportPlans(this.projectId)
        },
        onAutoPlan() {
            autoPlan(this.projectId).then(resp => {
                this.loadData()
            })
        }
    },
}
</script>

<style lang="less">
@import "~dhtmlx-gantt/codebase/dhtmlxgantt.css";

.plan_row {
    .dragIcon {
        visibility: hidden;
    }
}

.plan_row.row--hover {
    .dragIcon {
        visibility: visible;
    }
}

.gantt_task {
    .gantt_task_scale {
        height: 86px !important;

        .gantt_scale_line {
            height: 43px !important;
            line-height: 43px !important;

            .gantt_scale_cell {
                height: 43px !important;
                line-height: 43px !important;
            }
        }
    }

    .gantt_grid_data .gantt_row.gantt_selected,
    .gantt_grid_data .gantt_row.odd.gantt_selected,
    .gantt_task_row.gantt_selected {
        background-color: #e6f7ff !important;
    }

    .gantt_task_row.gantt_selected .gantt_task_cell {
        border-right-color: #e6f7ff !important;
    }

    .gantt_bars_area {
        .task_group {
            background-color: unset;
            border: 1px solid transparent;
            border-left: none;
            border-right: none;


            .gantt_task_progress_wrapper {
                width: calc(100% - 2px) !important;
                height: calc(100% - 3px) !important;
                border-radius: 0 !important;
                border: none !important;
                transform: translateX(1px) translateY(1px);
                clip-path: polygon(100% 0, 100% 100%, calc(100% - 5px) 72%, 5px 72%, 0 100%, 0 0);

                background-color: #bebfc2;

                .gantt_task_progress {
                    background-color: #575859 !important;
                }
            }
        }

        .gantt_task_line.gantt_bar_task {
            border: solid 1px transparent;
            border-left: none;
            border-right: none;
            border-radius: 2px;
            background-color: transparent;

            .gantt_task_progress_wrapper {

                background-color: #7aafff !important;
                border-color: #0064ff !important;
                border-radius: 2px;
                border: solid 1px v#0064ff;

                .gantt_task_progress {
                    background-color: #0064ff !important;
                }
            }
        }

        .gantt_task_line.gantt_milestone {
            visibility: hidden;
            background-color: #7aafff;
            border: 0 solid #0064ff;
            -webkit-box-sizing: content-box;
            box-sizing: content-box;
            -moz-box-sizing: content-box;
        }

        .task_group.gantt_task_line.gantt_selected {
            -webkit-box-shadow: none;
            box-shadow: none;
        }


    }

    .gantt_task_cell.week_end {
        background-color: #EFF5FD;
    }

    .gantt_task_row.gantt_selected .gantt_task_cell.week_end {
        background-color: #e6f7ff;
    }
}

.gantt-zoomControl-bar {
    padding: 5px;
    border: solid 1px #e6f7ff;
    background-color: #e6f7ff;
    box-shadow: #575859;
}
</style>