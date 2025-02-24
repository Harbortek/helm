<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel">
        <a-form-model ref="status-form" layout="horizontal" :model="formData" :rules="rules">
            <a-form-model-item ref="name" label="状态名称" prop="name">
                <a-input v-model="formData.name" @blur="() => {
                    $refs.name.onFieldBlur();
                }" />
            </a-form-model-item>
            <a-form-model-item ref="meaning" label="状态类型" prop="meaning" help="便于对工作项进度进行追踪管理">
                <a-select default-value="" v-model="formData.meaning">

                    <a-select-option :value="item.id" :key="item.id" v-for="item in meanings">{{ item.name
                    }}</a-select-option>
                </a-select>
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import { findEnumsByCode } from "@/services/system/EnumService.js"
export default {
    name: "TrackerStatusDialog",
    components: {},
    data() {
        const that = this;
        const nameValidator = (rule, value, callback) => {
            let found = false;
            let i = 0;
            for (i = 0; i < that.trackerStatusNames.length; i++) {
                if (that.trackerStatusNames[i] === value) {
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
            meanings: [],
            formData: {
                name: '',
                meaning: '',
            },
            rules: {
                name: [
                    { required: true, message: "请输入状态名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                    { validator: nameValidator, trigger: "change" },
                ],
                meaning: [
                    { required: true, message: "请输入状态类型", trigger: "blur" },
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
        trackerStatus: {
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
                return '创建工作项状态'
            } else {
                return '编辑工作项状态'
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
        trackerStatusNames() {
            let names = []
            this.trackerStatuses.forEach(f => {
                if (f.id !== this.trackerStatus.id) {
                    names.push(f.name)
                }
            })
            return names
        }
    },
    watch: {
        trackerStatus: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        }
    },
    mounted() {
        this.loadData();
    },
    methods: {
        closeDialog() {
            this.$emit("close");
        },
        loadData() {
            findEnumsByCode('TRACKER_STATUS_MEANING').then(resp => {
                this.meanings = resp
                if (this.trackerStatus) {
                    this.formData = {
                        name: this.trackerStatus.name,
                        meaning: this.trackerStatus.meaning.id
                    }
                }
            })

        },
        findMeaning(id) {
            for (let i = 0; i < this.meanings.length; i++) {
                if (id === this.meanings[i].id) {
                    return this.meanings[i]
                }
            }
            return
        },
        onOK() {
            this.$refs["status-form"].validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.trackerStatus)
                    result.name = this.formData.name
                    result.meaning = this.findMeaning(this.formData.meaning)
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
