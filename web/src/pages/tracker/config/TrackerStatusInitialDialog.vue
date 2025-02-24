<template>
    <a-modal v-model="visiable" title="设置初始状态" @ok="onOK" @cancel="onCancel">
        <a-form-model class="status-form" layout="horizontal" :model="formData" :rules="rules">
            <a-form-model-item label="状态名称" prop="selectedStatus">
                <a-select v-model="formData.selectedStatus.id">

                    <a-select-option :value="item.id" :key="item.id" v-for="item in trackerStatuses">{{ item.name
                    }}</a-select-option>

                </a-select>
                <div class="input-module-tips">选择的状态为创建需求后默认的初始状态</div>
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
export default {
    name: "TrackerStatusInitialDialog",
    components: {},
    data() {
        return {
            formData: {
                selectedStatus: {},
            },
            rules: {
                name: [
                    { required: true, message: "请输入状态名称", trigger: "blur" },
                ]
            },
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        trackerStatuses: {
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
        trackerStatuses: {
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
            if (this.trackerStatuses) {
                let i = 0;
                for (i = 0; i < this.trackerStatuses.length; i++) {
                    const status = this.trackerStatuses[i]
                    if (status.initial) {
                        this.formData = {
                            selectedStatus: status,
                        }
                        break
                    }
                }
            }
        },
        onOK() {
            let result = cloneDeep(this.formData.selectedStatus)
            this.$emit("ok", result);
        },
        onCancel() {
            this.$emit("cancel");
        }
    },
    created() { }
};
</script>
<style lang="less" scoped>
.input-module-tips {
    font-size: 12px;
    color: #909090;
    margin-top: 5px;
    height: 30px;
}
</style>
