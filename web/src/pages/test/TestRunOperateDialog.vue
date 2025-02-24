<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="planForm" :model="formData" :rules="rules" :layout="'horizontal'">

            <a-form-model-item v-if="editMode == 'executor'" style="margin-bottom:0px;" label="" prop="executor">
                <project-user-select v-model="formData.executor" :placeholder="'未选择'" :projectId="projectId" />
            </a-form-model-item>
            <a-form-model-item v-if="editMode == 'result'" style="margin-bottom:0px;" label="执行结果" prop="result">
                <EnumItemSelect v-model="formData.result" :projectId="projectId" :categoryCode="'TEST_CASE_RESULT'"
                    style="width: 200px;" @change="isChange = true" />
            </a-form-model-item>
            <a-form-model-item v-if="editMode == 'result'" style="margin-bottom:0px;" label="执行结果备注" prop="resultDesc">
                <a-textarea v-model="formData.resultDesc" :rows="3" placeholder="添加执行结果备注" />
            </a-form-model-item>

            <a-form-model-item v-if="editMode == 'testRun'" style="margin-bottom:0px;" label="关联其他测试运行"
                prop="testRunId">
                <a-select v-model="formData.testRunId" show-search optionFilterProp='children' placeholder="请选择">
                    <a-select-option v-for="item in testRunList.filter(t => t.id != testRunId)" :key="item.id"
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
import VXETable from "vxe-table";
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import { findTestRuns, changeExecutor, batchChangeResult, copyTestResults } from '@/services/test/TestRunService'
import moment from 'moment';
import EnumItemSelect from '@/components/select/EnumItemSelect.vue';
export default {
    name: "TestRunOperateDialog",
    components: { ProjectUserSelect, EnumItemSelect },
    data() {
        return {
            formData: {
                executor: undefined,
                result: undefined,
                resultDesc: undefined,
                testRunId: undefined,
            },
            rules: {
                executor: [{ required: true, message: "请选择执行人", trigger: "change" },],
                result: [{ required: true, message: "请选择执行结果", trigger: "change" },],
                testRunId: [{ required: true, message: "请选择测试运行", trigger: "change" },],
            },
            testRunList: [],
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },
        selectedRowKeys: {
            required: true
        },
        projectId: {
            required: true
        },
        testRunId: {
            required: true
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
        title: {
            get() {
                if (this.editMode === 'executor') {
                    return '更改执行人'
                } else if (this.editMode == "result") {
                    return '更改执行结果'
                } else if (this.editMode == "testRun") {
                    return '关联其他测试运行'
                }
            },
            set(newValue) {
                return newValue
            }
        }
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
        loadData: function () {
            this.formData = {
                executor: undefined,
                result: undefined,
                resultDesc: undefined,
                testRunId: undefined,
            }
            if (this.editMode == 'testRun') {
                findTestRuns(this.projectId, null, null, {}, { page: 0, size: 10000 }, null).then(res => {
                    this.testRunList = res.content
                })
            }
        },
        onOK: function () {
            this.$refs["planForm"].validate((valid) => {
                if (valid) {
                    if (this.editMode === 'executor') {
                        changeExecutor(this.testRunId, this.selectedRowKeys, this.formData.executor).then(res => {
                            VXETable.modal.message({ content: '操作成功', status: 'success' })
                            this.$emit("ok");
                        })
                    } else if (this.editMode === 'result') {
                        console.log(this.selectedRowKeys)
                        batchChangeResult(this.testRunId, this.selectedRowKeys, this.formData.result, this.formData.resultDesc).then(res => {
                            VXETable.modal.message({ content: '操作成功', status: 'success' })
                            this.$emit("ok");
                        })
                        console.log("//")
                    } else if (this.editMode === 'testRun') {
                        copyTestResults(this.formData.testRunId, this.selectedRowKeys).then(res => {
                            VXETable.modal.message({ content: '操作成功', status: 'success' })
                            this.$emit("ok");
                        })
                    }
                    this.$emit("cancel");

                }
            })

        },
        onCancel: function () {
            this.$emit("cancel");
        },
        closeDialog: function () {
            this.$emit("close");
        },
    },
    created() { }
};
</script>
<style lang="less" scoped></style>
