<template>
    <a-modal v-model="visiable" title="分组" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="xForm" layout="horizontal" :model="formData" :rules="rules" :labelCol="{ span: 5 }"
            :wrapperCol="{ span: 18, offset: 1 }">
            <a-form-model-item label="分组名称" prop="name">
                <a-input v-model="formData.name" />
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import XEUtils from "xe-utils";
import ProjectSelect from '@/components/select/ProjectSelect.vue';
export default {
    name: "ReportGroupDialog",
    components: { ProjectSelect },
    data() {
        const that = this;
        return {
            projects: [],
            formData: {
                name: '',
                description: '',
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
        reportGroup: {
            required: false
        },
        productLineId: {
            required: false
        },
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
                    this.loadData();
                }

            }
        }
    },
    mounted() {

    },
    methods: {
        closeDialog: function () {
            this.$emit("close");
        },
        loadData: function () {
            if (this.reportGroup?.id) {
                this.formData = cloneDeep(this.reportGroup);
            } else {
                this.formData = {
                    name: '',
                }
            }
            if (this.$refs["xForm"]) {
                this.$refs["xForm"].resetFields();
            }
        },
        onOK: function () {
            this.$refs["xForm"].validate((valid) => {
                if (valid) {
                    this.$emit("ok", this.formData);
                }
            })
        },
        onCancel: function () {
            this.$emit("close");
        }
    },
    created() { }
};
</script>
<style lang="less" scoped></style>
