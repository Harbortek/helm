<template>
    <a-modal v-model="visiable" title="产品" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="xForm" layout="horizontal" :model="formData" :rules="rules" :labelCol="{ span: 5 }"
            :wrapperCol="{ span: 18, offset: 1 }">
            <a-form-model-item label="上级" prop="parent" v-if="formData.parent">
                {{ formData.parent.name }}
            </a-form-model-item>
            <a-form-model-item label="名称" prop="name">
                <a-input v-model="formData.name" />
            </a-form-model-item>
            <a-form-model-item label="关联项目" prop="projectId">
                <a-select v-model="formData.projectId" placeholder="请选择" :allowClear="true">
                    <a-select-option v-for="item in projects" :key="item.id" :title="item.name" :value="item.id">
                        {{ item.name }}
                    </a-select-option>
                </a-select>
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
import ProjectSelect from '@/components/select/ProjectSelect.vue';
import { findAvailableProjects } from '@/services/product-line/ProductService';
export default {
    name: "ProductDialog",
    components: { ProjectSelect },
    data() {
        const that = this;
        return {
            projects: [],
            formData: {
                name: '',
                projectId: undefined,
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
        product: {
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
            findAvailableProjects(this.productLineId, this.product.id || 0).then(resp => {
                this.projects = resp;
                if (this.product?.id) {
                    this.formData = { id: this.product.id, name: this.product.name, description: this.product.description };
                    if (this.product.project) {
                        this.formData.projectId = this.product.project.id;
                    }
                } else {
                    this.formData = {
                        parent: this.product.parent,
                        name: '',
                        projectId: undefined,
                        description: '',
                    }
                }
                this.formData =
                    { ...this.formData };
                if (this.$refs["xForm"]) {
                    this.$refs["xForm"].resetFields();
                }
            })

        },
        onOK: function () {
            this.$refs["xForm"].validate((valid) => {
                if (valid) {
                    if (this.formData.projectId) {
                        this.formData.project = { id: this.formData.projectId }
                    }

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
