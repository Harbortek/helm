<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" centered>
        <a-tabs default-active-key="BASIC">
            <a-tab-pane key="BASIC" tab="基本信息">
                <div style="display: flex; flex-direction: column; height: 300px;">
                    <a-form-model ref="planForm" :model="formData" :rules="rules" :layout="'horizontal'"
                        :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">
                        <a-form-model-item ref="name" label="名称" prop="name">
                            <a-input v-model="formData.name" style="width:300px;" @blur="() => {
                                $refs.name.onFieldBlur();
                            }" />
                        </a-form-model-item>
                        <a-form-model-item label="开始日期" prop="planStartDate" v-if="formData.type === 'TASK'">
                            <a-date-picker v-model="formData.planStartDate" style="width:300px;"
                                @change="onStartDateChange" />
                        </a-form-model-item>
                        <a-form-model-item label="工期(天)" prop="duration" v-if="formData.type === 'TASK'">
                            <a-input-number v-model="formData.duration" style="width:300px;"
                                @change="onDurationChange" />
                        </a-form-model-item>
                        <a-form-model-item label="结束日期" prop="planEndDate"
                            v-if="formData.type === 'TASK' || formData.type === 'MILE_STONE'">
                            <a-date-picker v-model="formData.planEndDate" style="width:300px;"
                                @change="onEndDateChange" />
                        </a-form-model-item>
                        <a-form-model-item label="负责人" prop="owner.id">
                            <project-user-select v-model="formData.owner.id" :projectId="projectId"
                                style="width:300px;" />
                        </a-form-model-item>
                    </a-form-model>
                </div>
            </a-tab-pane>
            <a-tab-pane key="PRE_TASK" tab="前置计划">
                <div style="display: flex; flex-direction: column; height: 300px;">
                    <vxe-toolbar style="flex: 0 0;">
                        <template #tools>
                            <a-space><a-button @click="onAddPreTask">新增前置计划</a-button></a-space>
                        </template>
                    </vxe-toolbar>
                    <vxe-table size="small" ref="preTaskTable" show-overflow border row-key :show-header="true"
                        :seq-config="{ seqMethod: seqMethod }" :auto-resize="true" :data="preTasks"
                        :row-config="{ keyField: 'id', isCurrent: true }"
                        :edit-config="{ trigger: 'dblclick', mode: 'cell', showStatus: false }" style="flex: 1 1 auto;">
                        <vxe-column type="seq" width="50px" />
                        <vxe-column field="name" title="名称" :edit-render="{ name: 'select' }">
                            <template #edit="{ row }">
                                <vxe-select v-model="row.id" transfer>
                                    <vxe-option v-for="item in availablePreTasks(row)" :key="item.id" :value="item.id"
                                        :label="item.name"></vxe-option>
                                </vxe-select>
                            </template>
                            <template #default="{ row }">
                                {{ formatPlanName(row.id) }}
                            </template>
                        </vxe-column>
                        <vxe-column field="linkType" title="关系" width="100px" header-align="center" align="center"
                            :edit-render="{ name: 'select' }">
                            <template #edit="{ row }">
                                <vxe-select v-model="row.linkType" transfer>
                                    <vxe-option key="FS" value="FS" :label="$t('plan.linktype.FS')"></vxe-option>
                                    <vxe-option key="FS" value="FF" :label="$t('plan.linktype.FF')"></vxe-option>
                                    <vxe-option key="FS" value="SS" :label="$t('plan.linktype.SS')"></vxe-option>
                                    <vxe-option key="FS" value="SF" :label="$t('plan.linktype.SF')"></vxe-option>
                                </vxe-select>
                            </template>
                            <template #default="{ row }">
                                <a-tooltip :title="getPreTaskDescription(row)">
                                    {{ $t('plan.linktype.' + row.linkType) }}
                                </a-tooltip>
                            </template>
                        </vxe-column>
                        <vxe-column title="操作" header-align="center" align="center" width="100">
                            <template #default="{ row }">
                                <vxe-button type="text" circle icon="vxe-icon-delete" @click="onDeletePreTask(row)" />
                            </template>
                        </vxe-column>
                    </vxe-table>
                </div>
            </a-tab-pane>
            <a-tab-pane key="POST_TASK" tab="后置计划">
                <div style="display: flex; flex-direction: column; height: 300px;">
                    <vxe-toolbar style="flex: 0 0;">
                        <template #tools>
                            <a-space><a-button @click="onAddPostTask">新增后置计划</a-button></a-space>
                        </template>
                    </vxe-toolbar>
                    <vxe-table size="small" ref="postTaskTable" show-overflow border row-key :show-header="true"
                        :seq-config="{ seqMethod: seqMethod }" :auto-resize="true" :data="postTasks"
                        :row-config="{ keyField: 'id', isCurrent: true }"
                        :edit-config="{ trigger: 'dblclick', mode: 'cell', showStatus: false }" style="flex: 1 1 auto;">
                        <vxe-column type="seq" width="50px" />
                        <vxe-column field="name" title="名称" :edit-render="{ name: 'select' }">
                            <template #edit="{ row }">
                                <vxe-select v-model="row.id" transfer>
                                    <vxe-option v-for="item in availablePostTasks(row)" :key="item.id" :value="item.id"
                                        :label="item.name"></vxe-option>
                                </vxe-select>
                            </template>
                            <template #default="{ row }">
                                {{ formatPlanName(row.id) }}
                            </template>
                        </vxe-column>
                        <vxe-column field="linkType" title="关系" width="100px" header-align="center" align="center"
                            :edit-render="{ name: 'select' }">
                            <template #edit="{ row }">
                                <vxe-select v-model="row.linkType" transfer>
                                    <vxe-option key="FS" value="FS" :label="$t('plan.linktype.FS')"></vxe-option>
                                    <vxe-option key="FS" value="FF" :label="$t('plan.linktype.FF')"></vxe-option>
                                    <vxe-option key="FS" value="SS" :label="$t('plan.linktype.SS')"></vxe-option>
                                    <vxe-option key="FS" value="SF" :label="$t('plan.linktype.SF')"></vxe-option>
                                </vxe-select>
                            </template>
                            <template #default="{ row }">
                                <a-tooltip :title="getPostTaskDescription(row)">
                                    {{ $t('plan.linktype.' + row.linkType) }}
                                </a-tooltip>
                            </template>
                        </vxe-column>
                        <vxe-column title="操作" header-align="center" align="center" width="100">
                            <template #default="{ row }">
                                <vxe-button type="text" circle icon="vxe-icon-delete" @click="onDeletePostTask(row)" />
                            </template>
                        </vxe-column>
                    </vxe-table>
                </div>
            </a-tab-pane>
        </a-tabs>

    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import moment from 'moment';
import XEUtils from "xe-utils";
export default {
    name: "PlanDialog",
    components: { ProjectUserSelect },
    data() {
        return {
            formData: {
                name: '',
                planStartDate: '',
                planEndDate: '',
                owner: { id: '' },
            },
            rules: {
                name: [
                    { required: true, message: "请输入名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                ],
                type: [{ required: true, message: "请选择类型", trigger: "change" },]
            },
            preTasks: [],
            postTasks: [],
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },
        currentPlan: {
            required: true
        },
        projectId: {
            required: true
        },
        plans: {
            required: true
        }
    },
    computed: {
        title() {
            if (this.editMode === 'create') {
                return '新建' + this.$t('plan.type.' + this.currentPlan?.type)
            } else {
                return '编辑' + this.$t('plan.type.' + this.currentPlan?.type) + '「' + this.currentPlan?.name + '」'
            }

        },
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },

    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }

            }
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        loadData() {
            const _this = this;
            if (this.currentPlan) {
                this.formData = cloneDeep(this.currentPlan)
                if (this.formData.planStartDate && this.formData.duration) {
                    this.formData.planStartDate = moment(this.formData.planStartDate)
                    this.formData.planEndDate = this.formData.planStartDate.clone().add(this.formData.duration, 'days')
                }
                else if (this.formData.planEndDate) {
                    this.formData.planEndDate = moment(this.formData.planEndDate)
                }
                this.preTasks = this.formData.preTasks || []
                this.postTasks = this.formData.postTasks || []
            } else {
                this.formData = {
                    name: '',
                    type: 'group',
                    owner: {}
                }
                this.preTasks = []
                this.postTasks = []
            }
        },
        onStartDateChange() {
            if (this.formData.planStartDate && this.formData.duration) {
                this.formData.planEndDate = this.formData.planStartDate.clone().add(this.formData.duration, 'days')
                this.$set(this.formData, 'planEndDate', this.formData.planEndDate)
            } ``
        },
        onDurationChange() {
            if (this.formData.planStartDate && this.formData.duration) {
                this.formData.planEndDate = this.formData.planStartDate.clone().add(this.formData.duration, 'days')
                this.$set(this.formData, 'planEndDate', this.formData.planEndDate)
                console.log(this.formData.planEndDate.format('YYYY MM DD'))
            }
        },
        onEndDateChange() {
            if (this.formData.planStartDate && this.formData.planEndDate) {
                let duration = this.formData.planEndDate.diff(this.formData.planStartDate, 'days')
                this.formData.duration = duration
            }
        },
        onAddPreTask() {
            this.preTasks.push({ id: '-', linkType: 'FS' })
        },
        onDeletePreTask(row) {
            this.$refs.preTaskTable.remove(row)
            this.preTasks = this.$refs.preTaskTable.getTableData().tableData
        },
        availablePreTasks(row) {
            const currentPlanId = this.currentPlan?.id
            const totalPlans = this.plans || []

            return totalPlans.filter(p => { return p.id !== currentPlanId && p.parentId !== currentPlanId && !this.containsPaln(this.preTasks, p.id) || p.id === row.id })
        },
        onAddPostTask() {
            this.postTasks.push({ id: '-', linkType: 'FS' })
        },
        onDeletePostTask(row) {
            this.$refs.postTaskTable.remove(row)
            this.postTasks = this.$refs.postTaskTable.getTableData().tableData
        },
        availablePostTasks(row) {
            const currentPlanId = this.currentPlan?.id
            const totalPlans = this.plans || []

            return totalPlans.filter(p => { return p.id !== currentPlanId && p.parentId !== currentPlanId && !this.containsPaln(this.postTasks, p.id) || p.id === row.id })
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
        getPreTaskDescription(row) {
            return XEUtils.template(this.$t('pretask.description.' + row.linkType), { taskName: this.currentPlan.name })
        },
        getPostTaskDescription(row) {
            return XEUtils.template(this.$t('posttask.description.' + row.linkType), { taskName: this.currentPlan.name })
        },
        onOK: function () {
            this.$refs["planForm"].validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.formData)
                    if (result.type === 'TASK') {
                        result.planStartDate = this.formData.planStartDate.format('YYYY-MM-DD')
                        result.planEndDate = this.formData.planEndDate.format('YYYY-MM-DD')
                    } else if (result.type === 'MILE_STONE') {
                        result.planEndDate = this.formData.planEndDate.format('YYYY-MM-DD')
                    }
                    result.preTasks = this.preTasks
                    result.postTasks = this.postTasks
                    console.log(result)
                    this.$emit("ok", result);
                }
            })

        },
        onCancel: function () {
            this.$emit("cancel");
        }
    },
    created() { }
};
</script>
<style lang="less" scoped></style>
