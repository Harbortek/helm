<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="planForm" :model="formData" :rules="rules" :layout="'horizontal'">
            <a-form-model-item style="margin-bottom:0px;" ref="name" label="测试运行名称" prop="name">
                <a-input v-model="formData.name" @blur="() => {
        $refs.name.onFieldBlur();
    }" />
            </a-form-model-item>
            <a-form-model-item style="margin-bottom:0px;" label="测试运行负责人" prop="ownerId">
                <project-user-select v-model="formData.ownerId" :projectId="projectId" />
            </a-form-model-item>
            <a-form-model-item style="margin-bottom:0px;" label="测试阶段" prop="phaseId">
                <test-phase-select v-model="formData.phaseId" />
            </a-form-model-item>
            <a-form-model-item style="margin-bottom:0px;" label="关联迭代" prop="sprintId">
                <sprint-select v-model="formData.sprintId" :projectId="projectId"></sprint-select>
            </a-form-model-item>


        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import SprintSelect from '@/components/select/SprintSelect.vue';
import TestPhaseSelect from '@/components/select/TestPhaseSelect.vue';

import moment from 'moment';
export default {
    name: "TestRunDialog",
    components: { ProjectUserSelect, SprintSelect, TestPhaseSelect, },
    data() {
        return {
            formData: {
                name: '',
                ownerId: '',
                phaseId: '',
                sprintId: '',
            },
            rules: {
                name: [
                    { required: true, message: "请输入名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: ["blur", "change"],
                    },
                ],
                ownerId: [{ required: true, message: "请选择负责人", trigger: "change" },],
                phaseId: [{ required: true, message: "请选择测试阶段", trigger: "change" },],
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
        currentTestRun: {
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
        title: {
            get() {
                if (this.editMode === 'create') {
                    return '新建测试运行'
                } else {
                    return '编辑测试运行'
                }
            },
            set(newValue) {
                return newValue
            }
        }
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

    },
    methods: {
        loadData: function () {
            const _this = this;
            if (this.currentTestRun) {
                this.formData = cloneDeep(this.currentTestRun)
                this.formData.ownerId = this.currentTestRun.owner?.id
                this.formData.phaseId = this.currentTestRun.phase?.id
                this.formData.sprintId = this.currentTestRun.sprint?.id
            }
            console.log("loadData.Dialog", this.formData)
        },
        onOK: function () {
            this.$refs["planForm"].validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.formData)
                    result.owner = { id: this.formData.ownerId }
                    result.phase = { id: this.formData.phaseId }
                    result.sprint = { id: this.formData.sprintId }
                    result.projectId = this.projectId
                    console.log(result)
                    this.$emit("ok", result);
                }
            })

        },
        onCancel: function () {
            this.$emit("cancel");
        },
        closeDialog: function () {
            this.$emit("close");
        },
    },
    created() { }
};
</script>
<style lang="less" scoped></style>
