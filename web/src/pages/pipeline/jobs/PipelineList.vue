<template>
    <div style="margin:5px;background-color: white;">
        <div v-show="!showDetail">
            <a-tabs v-model="activeTab" default-active-key="ALL" @change="onTabChange">
                <a-tab-pane key="ALL" tab="全部流水线">
                </a-tab-pane>
                <a-tab-pane key="FAIL" tab="最近失败"></a-tab-pane>
                <a-tab-pane key="SUCCESS" tab="最近成功"></a-tab-pane>
            </a-tabs>
            <vxe-toolbar>
                <template #buttons>
                    <a-space>
                        <a-button v-action="'PAGE_WRITE|'+pageId" @click="onBinding">关联已有流水线</a-button>
                        <a-button v-action="'PAGE_WRITE|'+pageId" @click="onUnbinding">取消关联流水线</a-button>
                    </a-space>
                </template>
            </vxe-toolbar>
            <vxe-table ref="xTable" show-overflow border row-key :show-header="true" :auto-resize="true" :data="tableData"
                :tree-config="{ transform: false, accordion: false, children: 'jobs', expandAll: true }"
                :row-config="{ isCurrent: true }">
                <vxe-column type="seq" title="ID" width="50px" header-align="center" align="center">
                </vxe-column>
                <vxe-column field="name" title="流水线" tree-node>
                    <template #default="{ row }">
                        <a-icon v-if="row.branchJob" type="branches" /> {{ row.name }} <a-tag
                            v-if="row.multiBranch">多分支流水线</a-tag>
                    </template>
                </vxe-column>
                <vxe-column field="status" title="最近运行状态" width="300" header-align="center" align="center">
                    <template #default="{ row }">
                        <span v-if="row.lastBuild?.result">
                            <span v-if="row.status === 'SUCCESS'">
                                <a-icon type="check-circle" theme="filled" style="color:green;" />
                                运行成功
                                <a @click="showBuild(row)">#{{ row.lastBuild?.number }}</a>
                            </span>
                            <span v-if="row.status === 'FAILURE'">
                                <a-icon type="close-circle" theme="filled" style="color:red;" />
                                运行失败
                                <a @click="showBuild(row)">#{{ row.lastBuild?.number }}</a>
                            </span>
                        </span>
                        <span v-else>-</span>
                    </template>
                </vxe-column>

                <vxe-column field="duration" title="持续时间" width="150" header-align="center" align="center">
                    <template #default="{ row }">
                        <span v-if="row.lastBuild?.duration"> {{ formatElipse(row.lastBuild?.duration) }}</span>
                        <span v-else>-</span>
                    </template>
                </vxe-column>
                <vxe-column field="userName" title="最近运行人" width="150" header-align="center" align="center">
                    <template #default="{ row }">
                        {{ row.lastBuild?.buildUser }}
                    </template>
                </vxe-column>
                <vxe-column field="timestamp" width="250" title="最近运行时间" header-align="center" align="center">
                    <template #default="{ row }">
                        {{ formatLongDate(row.lastBuild?.timestamp) }}
                    </template>
                </vxe-column>
            </vxe-table>
            <binding-pipeline-dialog :isShowDialog="showBindingDialog" :bindingPipelines="tableData"
                @cancel="showBindingDialog = false" @ok="onBindingOK" />
            <unbinding-pipeline-dialog :isShowDialog="showUnbingDialog" :bindingPipelines="tableData"
                @cancel="showUnbingDialog = false" @ok="onUnbindingOK" />
        </div>
        <pipeline-jobs ref="jobPanel" v-show="showDetail" @close="showDetail = false" />
    </div>
</template>

<script>
import BindingPipelineDialog from './BindingPipelineDialog.vue';
import UnbindingPipelineDialog from './UnbindingPipelineDialog.vue'
import PipelineJobs from './PipelineJobs.vue';
import { findBindingPipelines, bindingPipeline, unbindingPipeline } from '@/services/pipeline/PipelineService'
import { formatLongDate, formatElipse } from '@/utils/DateUtils'

export default {
    name: 'PipelineList',
    components: { BindingPipelineDialog, UnbindingPipelineDialog, PipelineJobs },
    data() {
        return {
            bindingPipelines: [],
            showBindingDialog: false,
            showUnbingDialog: false,
            activeTab: 'ALL',
            showDetail: false,
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },
        tableData() {
            if (this.activeTab === 'ALL') {
                return this.bindingPipelines
            } else if (this.activeTab === 'FAIL') {
                return this.bindingPipelines.filter(item => { return item.status === 'FAILURE' })
            } else if (this.activeTab === 'SUCCESS') {
                return this.bindingPipelines.filter(item => { return item.status === 'SUCCESS' })
            }
        }
    },
    mounted() {
        this.loadData()
    },
    methods: {
        formatLongDate(d) {
            return formatLongDate(d)
        },
        formatElipse(d) {
            return formatElipse(d)
        },
        loadData() {
            findBindingPipelines(this.projectId).then(resp => {
                this.bindingPipelines = resp
                this.$nextTick(() => {
                    this.$refs.xTable.setAllTreeExpand(true)
                })
            })
        },
        onBinding() {
            this.showBindingDialog = true
        },
        onBindingOK(pipelines) {
            bindingPipeline(this.projectId, pipelines).then(resp => {
                this.loadData()
                this.showBindingDialog = false
            })
        },
        onTabChange(activeKey) {
            this.loadData()
        },
        showBuild(pipeline) {
            const buildInfo = pipeline.lastBuild
            this.showDetail = true
            this.$refs.jobPanel.loadData(pipeline, buildInfo)
        },
        onUnbinding() {
            this.showUnbingDialog = true
        },
        onUnbindingOK(pipelines) {
            unbindingPipeline(this.projectId, pipelines).then(resp => {
                this.loadData()
                this.showUnbingDialog = false
            })
        }

    },
}
</script>

<style lang="less" scoped></style>