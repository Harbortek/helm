<template>
    <a-modal v-model="visiable" title="迭代" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="deliverableForm" :model="formData" :rules="rules" :layout="'horizontal'" :labelCol="{ span: 7 }"
            :wrapperCol="{ span: 16, offset: 1 }">
            <a-form-model-item ref="name" label="名称" prop="name">
                {{ currentDeliverable?.name }}
            </a-form-model-item>

            <a-form-model-item label="链接" prop="url" v-if="currentDeliverable?.type === 'URL'">
                <a-input v-model="formData.url" style="width:300px;" />
            </a-form-model-item>

        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
export default {
    name: "DeliverableDialog",
    components: {},
    data() {
        return {
            formData: {
                url: '',
            },
            rules: {
                url: [{ required: true, message: "请输入链接地址", trigger: "change" },],
            },
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        currentDeliverable: {
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
            if (this.currentDeliverable) {
                this.formData = cloneDeep(this.currentDeliverable)

            } else {
                this.formData = {
                }
            }
        },
        onOK: function () {
            this.$refs["deliverableForm"].validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.formData)
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
