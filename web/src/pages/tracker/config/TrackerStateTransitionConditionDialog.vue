<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" centered :width="800">
        <a-form-model ref="actionForm" layout="horizontal" :model="formData" :rules="rules" class="action-form"
            size="small">
            <a-form-model-item ref="type" label="名称" prop="name">
                <a-input v-model="formData.name" />
            </a-form-model-item>
            <a-form-model-item ref="expressionEL" label="条件表达式" prop="expressionEL" help="表达式必须返回布尔值">
                <a-input v-model="formData.expressionEL" />
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'


export default {
    name: "TrackerStateTransitionConditionDialog",
    components: {},
    data() {
        return {
            formData: {
                name: '',
                expressionEL: '',
            },
            rules: {
                name: [
                    { required: true, message: "请输入名称", trigger: "blur" },
                ],
                expressionEL: [
                    { required: true, message: "请输入表达式", trigger: "blur" },
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
        condition: {
            required: true
        },
        tracker: {
            type: Object,
            required: true,
        }
    },
    computed: {
        title() {
            if (this.editMode === 'create') {
                return '创建条件'
            } else {
                return '编辑条件'
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
        transition: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        condition: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
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
            if (this.condition) {
                this.formData = cloneDeep(this.condition)
            } else {
                this.formData = {
                    name: '',
                    expressionEL: '',
                }
            }
        },
        onOK: function () {
            this.$refs["actionForm"].validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.formData)
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
<style lang="less" scoped>
.action-form {
    .ant-form-item {
        margin-bottom: 8px;
    }
}
</style>
