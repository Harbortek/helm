<template>
    <config-page description="展示在项目计划下拆解得出的所有计划，是每个计划的追踪工具。">
        <div>
            <vxe-toolbar style="flex: 0 0;">
                <template #buttons>
                    <a-select v-model="taskStatus" @change="onTaskStatusFilter" style="width:200px;">
                        <a-select-option key="ALL" value="ALL">所有</a-select-option>
                        <a-select-option key="NOT_START" value="NOT_START">未开始</a-select-option>
                        <a-select-option key="ON_GOING" value="ON_GOING">进行中</a-select-option>
                        <a-select-option key="FINISHED" value="FINISHED">已完成</a-select-option>
                    </a-select>
                    <span style="margin-left:10px;">已完成 {{ finished }}项，共{{ total }}项</span>
                </template>
                <template #tools>

                </template>
            </vxe-toolbar>
            <vxe-table size="small" ref="xTable" show-overflow border row-key :show-header="true" :auto-resize="true"
                :data="tableData" :row-config="{ keyField: 'id', isCurrent: true }" style="flex: 1 1 auto;">
                <vxe-column field="itemNo" title="ID" header-align="center" align="center" width="50px">
                    <template #default="{ row }">
                        #{{ row.itemNo }}
                    </template>
                </vxe-column>

                <vxe-column field="name" title="名称">
                    <template #default="{ row }">
                        <a @click="gotoTaskDetail(row.id)">{{ row.name }}</a>
                    </template>
                </vxe-column>
                <vxe-column field="sourceType" title="类型" header-align="center" align="center" width="100px">
                    <template #default="{ row }">
                        {{ $t('plan.type.' + row.type) }}
                    </template>
                </vxe-column>
                <vxe-column field="progress" title="进度" header-align="center" align="center" width="100px">
                    <template #default="{ row }">
                        {{ formatProgress(row.progress) }}
                    </template>
                </vxe-column>
                <vxe-column field="owner.name" title="负责人" header-align="center" align="center" width="150px">
                    <template #default="{ row }">
                        <template v-if="row.owner.id">
                            <h-avatar :name="row.owner?.name" :icon="row.owner?.icon"></h-avatar>
                        </template>
                        <template v-else>
                            -
                        </template>
                    </template>
                </vxe-column>
                <vxe-column field="planStartDate" title="开始日期" header-align="center" align="center" width="150">
                    <template #default="{ row }">
                        {{ formatDate(row.planStartDate) }}
                    </template>
                </vxe-column>
                <vxe-column field="planEndDate" title="结束日期" header-align="center" align="center" width="150px">
                    <template #default="{ row }">
                        {{ formatDate(row.planEndDate) }}
                    </template>
                </vxe-column>
            </vxe-table>
        </div>
    </config-page>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import { findWaitExecutePlans } from '@/services/plan/PlanService'
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import { formatDate, isWeekend } from '@/utils/DateUtils'
export default {
    name: 'TaskList',
    components: { ConfigPage, HAvatar },
    data() {
        return {
            allData: [],
            taskStatus: 'ALL',
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        finished() {
            return this.tableData.filter(row => row.finished).length;
        },
        total() {
            return this.tableData.length
        },
        tableData() {
            if (this.taskStatus === 'ALL') {
                return this.allData
            } else if (this.taskStatus === 'NOT_START') {
                return this.allData.filter(item => { return !item.finished && (item.progress === 0 || !item.progress) })
            } else if (this.taskStatus === 'ON_GOING') {
                return this.allData.filter(item => { return !item.finished && item.progress > 0 })
            } else if (this.taskStatus === 'FINISHED') {
                return this.allData.filter(item => { return item.finished })
            }
        }
    }, 
    mounted() {
        this.loadData()
    },
    methods: {
        formatDate(v) {
            if (!v) return '-'
            else return formatDate(v)
        },
        formatProgress(v) {
            if (!v) return '-'
            else return v + '%'
        },
        loadData() {
            findWaitExecutePlans(this.projectId).then(resp => {
                this.allData = resp
            })
        },

        onTaskStatusFilter(status) {

        },
        openPage(url) {
            window.open(url, '_blank')
        },
        gotoTaskDetail(taskId) {
            this.$router.push({ name: 'taskDetail', params: { itemId: taskId } })
        }
    },
}
</script>

<style lang="less" scoped></style>