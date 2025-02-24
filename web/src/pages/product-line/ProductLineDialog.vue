<template>
    <a-modal v-model="visiable" title="产品线" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="xForm" layout="horizontal" :model="formData" :rules="rules">
            <a-form-model-item label="名称" prop="name">
                <a-input v-model="formData.name" />
            </a-form-model-item>
            <a-form-model-item label="负责人" prop="ownerId">
                <global-user-select v-model="formData.ownerId" />
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
import GlobalUserSelect from '@/components/select/GlobalUserSelect.vue';
import { findOneProductLine } from '@/services/product-line/ProductLineService';
export default {
    name: "ProductLineDialog",
    components: { GlobalUserSelect },
    data() {
        const that = this;
        return {
            formData: {
                name: '',
                ownerId: '',
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
        productId: {
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
        this.loadData();
    },
    methods: {
        closeDialog: function () {
            this.$emit("close");
        },
        loadData: function () {
            const _this = this;
            if (this.productId) {
                findOneProductLine(this.productId).then(resp => {
                    this.formData = resp;
                })

            } else {
                this.formData = {
                    name: '',
                    description: '',
                }
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
