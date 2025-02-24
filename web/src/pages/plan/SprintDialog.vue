<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" :confirm-loading="loading" centered>
        <a-form-model ref="planForm" :model="formData" :rules="rules" :layout="'horizontal'" :labelCol="{ span: 7 }"
            :wrapperCol="{ span: 16, offset: 1 }">
            <a-form-model-item v-if="editMode != 'start' && editMode != 'complete'" ref="name" label="名称" prop="name">
                <a-input v-model="formData.name" style="width:300px;" @blur="() => {
                    $refs.name.onFieldBlur();
                }" />
            </a-form-model-item>
            <a-form-model-item v-if="editMode != 'complete'" label="开始日期" prop="planStartDate">
                <a-date-picker v-model="formData.planStartDate" style="width:300px;" @change="onStartDateChange" />
            </a-form-model-item>
            <a-form-model-item v-if="editMode != 'complete'" label="工时(天)" prop="duration">
                <a-input-number v-model="formData.duration" style="width:300px;" @change="onDurationChange" />
            </a-form-model-item>
            <a-form-model-item label="结束日期" prop="planEndDate">
                <a-date-picker v-model="formData.planEndDate" style="width:300px;" @change="onEndDateChange" />
            </a-form-model-item>
            <a-form-model-item v-if="editMode != 'complete'" label="负责人" prop="ownerId">
                <project-user-select v-model="formData.ownerId" :projectId="projectId" style="width:300px;" />
            </a-form-model-item>

            <a-form-model-item v-if="editMode != 'complete'" label="目标版本" prop="targetVersion">
                <target-version-select v-model="formData.targetVersionId" :projectId="projectId" style="width:300px;" />
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import TargetVersionSelect from '@/components/select/TargetVersionSelect.vue';
import moment from 'moment';
export default {
    name: "SprintDialog",
    components: { ProjectUserSelect, TargetVersionSelect },
    data() {
        return {
            loading:false,
            formData: {
                name: '',
                planStartDate: '',
                planEndDate: '',
                ownerId: '',
                targetVersionId: '',
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
                planStartDate: [{ required: true, message: "请选择开始日期", trigger: "change" },],
                planEndDate: [{ required: true, message: "请选择结束日期", trigger: "change" },],
                ownerId: [{ required: true, message: "请选择负责人", trigger: "change" },]
            },
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },
        title: {
            required: true
        },
        currentSprint: {
            required: true
        },
        projectId: {
            required: true
        }
    },
    computed: {
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
                    this.loading=false;
                    this.loadData();
                }

            }
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        closeDialog: function () {
            this.$emit("close");
        },
        loadData: function () {
            const _this = this;
            if (this.currentSprint) {
                this.formData = cloneDeep(this.currentSprint)
                if (this.formData.planStartDate && this.formData.duration) {
                    this.formData.planStartDate = moment(this.formData.planStartDate)
                    this.formData.planEndDate = this.formData.planStartDate.clone().add(this.formData.duration, 'days')
                }
                else if (this.formData.planEndDate) {
                    this.formData.planEndDate = moment(this.formData.planEndDate)
                }
                this.formData.ownerId = this.currentSprint.owner?.id
                this.formData.targetVersionId = this.currentSprint.targetVersion?.id

            } else {
                this.formData = {
                    name: '',
                    planStartDate: '',
                    planEndDate: '',
                    ownerId: '',
                    targetVersionId: '',
                }
            }
            console.log("loadData.Dialog", this.formData)
        },
        onStartDateChange() {
            if (this.formData.planStartDate && this.formData.duration) {
                this.formData.planEndDate = this.formData.planStartDate.clone().add(this.formData.duration, 'days')
                this.$set(this.formData, 'planEndDate', this.formData.planEndDate)
            }
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
        onOK: function () {
            this.loading=true;
            this.$refs["planForm"].validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.formData)
                    if (result.type === 'TASK') {
                        result.planStartDate = this.formData.planStartDate.format('YYYY-MM-DD')
                        result.planEndDate = this.formData.planEndDate.format('YYYY-MM-DD')
                    } else if (result.type === 'MILE_STONE') {
                        result.planEndDate = this.formData.planEndDate.format('YYYY-MM-DD')
                    }
                    result.targetVersion = { id: this.formData.targetVersionId }
                    result.owner = { id: this.formData.ownerId }
                    console.log(result)
                    this.$emit("ok", result);
                }else{
                    this.loading=false;
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
