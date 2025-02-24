<template>
    <a-modal v-model="visiable" title="复制工作项" @ok="onOK" @cancel="onCancel">
        <a-form-model ref="xForm" class="status-form" :model="formData" :rules="rules">
            <a-form-model-item label="项目" prop="projectId">
                <project-select v-model="formData.projectId" :excludes="[this.projectId]" />
            </a-form-model-item>
            <a-form-model-item label="工作项类型" prop="id">
                <tracker-select v-model="formData.id" :projectId="formData.projectId" />
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import ProjectSelect from '../../../components/select/ProjectSelect.vue';
import TrackerSelect from '../../../components/select/TrackerSelect.vue';
export default {
    name: "TrackerStatusInitialDialog",
    components: { ProjectSelect, TrackerSelect },
    data() {
        return {
            formData: {
                projectId: '',
                id: '',
            },
            rules: {
                projectId: [
                    { required: true, message: "请选择项目", trigger: "change" },
                ],
                id: [
                    { required: true, message: "请选择工作项类型", trigger: "change" },
                ]
            },
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        projectId: {
            required: true,
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

    },
    mounted() {
        this.loadData();
    },
    methods: {
        closeDialog() {
            this.$emit("close");
        },
        loadData() {
        },
        onOK() {
            this.$refs.xForm.validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.formData)
                    this.$emit("ok", result);
                }
            })

        },
        onCancel() {
            this.$emit("cancel");
        }
    },
    created() { }
};
</script>
<style lang="less" scoped></style>
