<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="xForm" layout="horizontal" :model="formData" :rules="rules" :labelCol="{ span: 5 }"
            :wrapperCol="{ span: 18, offset: 1 }">
            <a-form-model-item :label="label" prop="name">
                <a-input v-model="formData.name" />
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import XEUtils from "xe-utils";
export default {
    name: "RenameDialog",
    components: {},
    data() {
        return {
            visiable: false,
            title: '',
            label: '',
            formData: {
                name: '',
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
    methods: {
        show({ title, label, name }) {
            this.title = title
            this.label = label
            this.formData = { name: name }
            this.visiable = true
            return new Promise((resolve, reject) => {
                this.resolve = resolve;
                this.reject = reject;
            });
        },
        handleResolve(...params) {
            delete this.reject;
            this.visiable = false;
            if (this.resolve) {
                this.resolve(...params);
            }
        },
        closeDialog: function () {
            this.visiable = false;
        },
        onOK: function () {
            this.$refs["xForm"].validate((valid) => {
                if (valid) {
                    this.handleResolve(this.formData.name)
                }
            })
        },
        onCancel: function () {
            this.closeDialog()
        }
    },
    created() { }
};
</script>
<style lang="less" scoped></style>
