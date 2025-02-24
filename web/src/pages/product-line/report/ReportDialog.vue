<template>
    <a-modal v-model="visiable" title="报表" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="xForm" layout="horizontal" :model="formData" :rules="rules" :labelCol="{ span: 5 }"
            :wrapperCol="{ span: 18, offset: 1 }">
            <a-form-model-item label="分组" prop="groupId">
                <a-select v-model="formData.groupId" placeholder="请选择" :allowClear="true">
                    <a-select-option v-for="item in groups" :key="item.id" :title="item.name" :value="item.id">
                        {{ item.name }}
                    </a-select-option>
                </a-select>
            </a-form-model-item>
            <a-form-model-item label="名称" prop="name">
                <a-input v-model="formData.name" />
            </a-form-model-item>
            <a-form-model-item label="描述" prop="description">
                <a-input type="textarea" v-model="formData.description" :auto-size="{ minRows: 3, maxRows: 5 }" />
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import XEUtils from "xe-utils";
import { findReportGroups } from '@/services/product-line/ReportGroupService';
export default {
    name: "ReportDialog",
    components: {},
    data() {
        const that = this;
        return {
            formData: {
                name: '',
                description: '',
            },
            rules: {
                groupId: [
                    { required: true, message: "请选择分组", trigger: "change" },
                ],
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
            groups: [],
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },
        report: {
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
            findReportGroups(this.productLineId).then(resp => {
                this.groups = resp;
            })

            if (this.report?.id) {
                this.formData = cloneDeep(this.report);
            } else {
                this.formData = {
                    name: '',
                    description: '',
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
