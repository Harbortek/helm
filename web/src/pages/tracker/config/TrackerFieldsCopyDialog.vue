<template>
    <a-modal v-model="visiable" title="复制工作项属性" @ok="onOK" @cancel="onCancel">
        <a-form-model ref="xForm" class="status-form" :model="formData" :rules="rules">
            <a-form-model-item label="工作项类型" prop="trackerId">
                <tracker-select v-model="formData.trackerId" :projectId="projectId" :excludes="[this.trackerId]"
                    @tracker-selected="onChangeTracker" />
            </a-form-model-item>
            <a-form-model-item label="工作项属性" prop="fields">
                <a-select mode="multiple" v-model="formData.fields" placeholder="请选择">
                    <a-select-option v-for="item in tracker?.trackerFields?.filter(v => !v.system)" :key="item.id"
                        :title="item.name" :value="item.id">
                        {{ item.name }}
                    </a-select-option>
                </a-select>
            </a-form-model-item>

        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import ProjectSelect from '@/components/select/ProjectSelect.vue';
import TrackerSelect from '@/components/select/TrackerSelect.vue';
export default {
    name: "TrackerFieldsCopyDialog",
    components: { ProjectSelect, TrackerSelect },
    data() {
        return {
            tracker: {},
            formData: {
                fields: undefined,
                trackerId: undefined,
            },
            rules: {
                fields: [
                    { required: true, message: "请选择工作项属性", trigger: "change" },
                ],
                trackerId: [
                    { required: true, message: "请选择工作项类型", trigger: "change" },
                ]
            },
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        projectId: {
            required: true,
        },
        trackerId: {
            required: true,
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

    },
    mounted() {
        this.loadData();
    },
    methods: {
        closeDialog() {
            this.$emit("close");
        },
        loadData() {
        },
        onChangeTracker(tracker) {
            this.formData.fields = undefined
            this.tracker = tracker
        },
        onOK() {
            this.$refs.xForm.validate((valid) => {
                if (valid) {
                    let result = cloneDeep(this.formData)
                    result.fields = this.tracker.trackerFields.filter(item => this.formData.fields.includes(item.id))
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
