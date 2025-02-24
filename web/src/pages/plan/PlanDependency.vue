<template>
    <div style="display: flex; flex-direction: column; height: 300px;">
        <vxe-toolbar style="flex: 0 0;">
            <template #tools>
                <a-space><a-button v-if="hasEditPerm" @click="showPlanDialog = true">{{ buttonText }}</a-button></a-space>
            </template>
        </vxe-toolbar>
        <vxe-table size="small" ref="preTaskTable" show-overflow border row-key :show-header="true"
            :seq-config="{ seqMethod: seqMethod }" :auto-resize="true" :data="tableData"
            :row-config="{ keyField: 'id', isCurrent: true }"
            :edit-config="{ trigger: 'dblclick', mode: 'cell', showStatus: false }" style="flex: 1 1 auto;">
            <vxe-column type="seq" width="50px" />
            <vxe-column field="name" title="名称" :edit-render="{ name: 'select' }">
                <template #default="{ row }">
                    {{ row.name }}
                </template>
            </vxe-column>
            <vxe-column field="linkType" title="关系" width="100px" header-align="center" align="center"
                :edit-render="{ name: 'select' }">
                <template #default="{ row }">
                    <a-tooltip :title="getTaskDescription(row)">
                        {{ $t('plan.linktype.' + row.linkType) }}
                    </a-tooltip>
                </template>
            </vxe-column>
            <vxe-column title="操作" header-align="center" align="center" width="100">
                <template #default="{ row }">
                    <vxe-button type="text" v-if="hasEditPerm" circle icon="vxe-icon-delete" @click="onDeleteTask(row)">移除</vxe-button>
                </template>
            </vxe-column>
        </vxe-table>
        <plan-dependency-dialog :isShowDialog="showPlanDialog" :projectId="projectId" :data="availableTasks"
            @cancel="showPlanDialog = false" @ok="onAddTask" />
    </div>
</template>

<script>
import XEUtils from "xe-utils";
import VXETable from "vxe-table";
import { findPlans, updatePlan } from '@/services/plan/PlanService'
import PlanDependencyDialog from "./PlanDependencyDialog.vue";
export default {
    name: 'PlanDependency',
    components: { PlanDependencyDialog },
    props: {
        currentPlan: {
            required: true
        },
        projectId: {
            required: true
        },
        mode: {
            required: true,
        },
        hasEditPerm: {
            required: true,
        }
    },
    data() {
        return {
            plans: [],
            tableData: [],
            showPlanDialog: false,
        }
    },
    computed: {
        buttonText() {
            if (this.mode === 'preTasks') {
                return '新增前置任务'
            } else {
                return '新增后续任务'
            }
        },
        availableTasks() {
            const currentPlanId = this.currentPlan?.id
            const totalPlans = this.plans || []

            return totalPlans.filter(p => {
                return (p.type === 'TASK' || p.type === 'MILE_STONE')
                    && p.id !== currentPlanId && p.parentId !== currentPlanId
                    && !this.containsPaln(this.tableData, p.id)
            })
        },
    },
    watch: {
        currentPlan: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        }
    },
    mounted() {
        findPlans(this.projectId).then(resp => {
            this.plans = resp
        })
        this.loadData()
    },
    methods: {
        loadData() {
            if (this.currentPlan) {
                if (this.mode === 'preTasks') {
                    this.tableData = this.currentPlan.preTasks || []
                } else {
                    this.tableData = this.currentPlan.postTasks || []
                }
                console.log(this.tableData)
            } else {
                this.tableData = []
            }
        },
        onAddTask(items) {
            if (this.mode === 'preTasks') {
                items.forEach(item => {
                    this.currentPlan.preTasks.push(item)
                })
            } else {
                items.forEach(item => {
                    this.currentPlan.postTasks.push(item)
                })
            }
            updatePlan(this.currentPlan).then().then(resp => {
                this.showPlanDialog = false
                this.$emit('change')
            })
        },
        onDeleteTask(row) {
            VXETable.modal.confirm({
                title: '移除前后置关系',
                content: '确定移除计划「' + this.formatPlanName(row.id) + '」和「' + this.currentPlan.name + '」的前后置关系么?'
            }).then(type => {
                if (type === 'confirm') {
                    this.$refs.preTaskTable.remove(row)
                    this.tableData = this.$refs.preTaskTable.getTableData().tableData
                    if (this.mode === 'preTasks') {
                        this.currentPlan.preTasks = this.tableData
                    } else {
                        this.currentPlan.postTasks = this.tableData
                    }
                    updatePlan(this.currentPlan).then().then(resp => {
                        VXETable.modal.message({ content: '移除前后置关系成功', status: 'success' })
                        this.$emit('change')
                    })
                }
            })

        },

        formatPlanName(currentPlanId) {
            if (currentPlanId && this.plans && this.plans.length > 0) {
                const plans = this.plans.filter(p => { return p.id === currentPlanId })
                return plans.length > 0 ? plans[0].name : ''
            }
            return ''
        },
        containsPaln(array, id) {
            return array.findIndex(item => item.id === id) >= 0
        },
        seqMethod({ rowIndex }) {
            return `${rowIndex + 1}`
        },
        getTaskDescription(row) {
            if (this.mode === 'preTasks') {
                return XEUtils.template(this.$t('pretask.description.' + row.linkType), { taskName: this.currentPlan.name })
            } else {
                return XEUtils.template(this.$t('posttask.description.' + row.linkType), { taskName: this.currentPlan.name })
            }
        },
    },
}
</script>

<style></style>