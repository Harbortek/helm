<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="transtionForm" layout="horizontal" :model="formData" :rules="rules">
            <a-form-model-item ref="name" label="步骤名称" prop="name">
                <a-input v-model="formData.name" />
            </a-form-model-item>
            <a-form-model-item label="开始状态" prop="transitionFrom" v-show="editMode === 'create'">
                <a-select v-model="formData.transitionFrom">
                    <a-select-option :value="item.id" :key="item.id" v-for="item in trackerStatuses">{{
                        item.name }}</a-select-option>
                </a-select>
            </a-form-model-item>
            <a-form-model-item label="结束状态" prop="transitionTo" v-show="editMode === 'create'">
                <a-select v-model="formData.transitionTo" @change="onTransitionToChange">
                    <a-select-option :value="item.id" :key="item.id" v-for="item in trackerStatuses">{{
                        item.name }}</a-select-option>
                </a-select>
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
export default {
    name: "TrackerStateTransitionDialog",
    components: {},
    data() {
        const that = this;
        const nameValidator = (rule, value, callback) => {
            let found = false;
            let i = 0;
            for (i = 0; i < that.transitionNames.length; i++) {
                if (that.transitionNames[i] === value) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return callback();
            } else {
                return callback(new Error("已存在同样名称的数据"));
            }
        };
        return {
            formData: {
                name: '',
                transitionFrom: '',
                transitionTo: '',
            },
            rules: {
                name: [
                    { required: true, message: "请输入步骤名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                    { validator: nameValidator, trigger: "change" },
                ],
                transitionFrom: [
                    { required: true, message: "请选择开始状态", trigger: "blur" },
                ],
                transitionTo: [
                    { required: true, message: "请选择结束状态", trigger: "blur" },
                ]
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
        transition: {
            required: true
        },
        stateTransitions: {
            required: true
        },
        trackerStatuses: {
            required: true,
            type: Array,
            default: () => { return [] }
        }
    },
    computed: {
        title() {
            if (this.editMode === 'create') {
                return '创建工作项步骤'
            } else {
                return '编辑工作项步骤'
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
        transitionNames() {
            let names = []
            this.stateTransitions.forEach(f => {
                if (f.id !== this.transition.id && f.transitionFrom == this.formData.transitionFrom) {
                    names.push(f.name)
                }
            })
            return names
        }
    },
    watch: {
        transition: {
            handler: function (newVal, oldVal) {
                this.loadData();
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
            if (this.transition) {
                this.formData = {
                    name: this.transition.name,
                    transitionFrom: this.transition.transitionFrom.id,
                    transitionTo: this.transition.transitionTo.id
                }
            }
        },
        onTransitionToChange(transitionToId) {
            const transitionTo = this.trackerStatuses.filter(item => { return item.id === transitionToId })[0]
            if (transitionTo && this.formData.name === '') {
                this.formData.name = transitionTo.name
            }
        },
        onOK: function () {
            this.$refs["transtionForm"].validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.transition)
                    result.name = this.formData.name
                    result.transitionFrom.id = this.formData.transitionFrom
                    result.transitionTo.id = this.formData.transitionTo
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
