<template>
    <a-modal v-model="visiable" :title="'新建测试报告'" width="1000px" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="reportForm" :model="formData" :colon="false" :rules="rules" :layout="'horizontal'">
            <a-form-model-item style="margin-bottom:0px;" ref="name" label="测试报告名称" prop="name">
                <a-input v-model="formData.name" style="width:50%" @blur="() => {
        $refs.name.onFieldBlur();
    }" />
            </a-form-model-item>
            <a-form-model-item style="margin-bottom:0px;" prop="testRunIds">
                <template #label>
                    <span>测试运行列表</span>
                    <span style="color:#87888a">(已选 {{ selectedRowKeys.length }} 条)</span>
                </template>
                <vxe-input style="position:absolute;right:0px;top:-40px;width:210px;" v-model="keyword"
                    placeholder="搜索测试运行" type="search" clearable @search-click="loadData"></vxe-input>
                <!-- <a-input v-model="formData.testRunList"/> -->
                <div class="table-layout" style="height:500px;margin-bottom:15px;">
                    <vxe-table style="cursor: pointer;" height="auto" :data="testRunList" :loading="loading"
                        show-overflow :row-config="{ isHover: true }"
                        :checkbox-config="{ checkRowKeys: selectedRowKeys, reserve: true, checkField: 'checked', trigger: 'row' }"
                        @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent">
                        <vxe-column type="checkbox" title="" width="30"></vxe-column>
                        <vxe-column field="name" title="测试运行名称"></vxe-column>
                        <vxe-column field="owner" title="负责人">
                            <template #default="{ row }">
                                <h-avatar :name="row.owner?.name" :icon="row.owner?.icon"></h-avatar>
                            </template>
                        </vxe-column>

                        <vxe-column field="status" title="状态">
                            <template #default="{ row }">
                                <a-tag style="border-radius: 10px;font-size: 13px;"
                                    :style="{ color: row.status?.color, borderColor: row.status?.color }">
                                    {{ row.status?.name }}
                                </a-tag>
                            </template>
                        </vxe-column>

                        <vxe-column field="phase" title="测试阶段">
                            <template #default="{ row }">
                                {{ row.phase?.name }}
                            </template>
                        </vxe-column>
                        <!-- <vxe-column field="project.name" title="关联项目"></vxe-column> -->
                        <vxe-column field="sprint.name" title="关联迭代">
                            <template #default="{ row }">
                                {{ row.sprint?.name }}
                            </template>
                        </vxe-column>
                    </vxe-table>
                    <vxe-pager :loading="loading" :current-page="pagination.current" :page-size="pagination.pageSize"
                        :total="pagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="handlePageChange" style="margin-left: 200px;">
                    </vxe-pager>
                </div>
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import HAvatar from '@/components/avatar/h-avatar.vue';
import { findTestRuns } from '@/services/test/TestRunService'
import { createTestReport } from '@/services/test/TestReportService'

import moment from 'moment';
import { VXETable } from 'vxe-table';
export default {
    name: "TestReportCreateDialog",
    components: { HAvatar },
    data() {
        return {
            keyword: '',
            pagination: {
                current: 1,
                pageSize: 10,
            },
            testRunList: [],
            loading: false,
            orderBy: 'name',
            orderByType: 'DESC',
            selectedRowKeys: [],
            formData: {
                name: '',
                testRunIds: '',
            },
            rules: {
                name: [
                    { required: true, message: "请输入名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: ["blur", "change"],
                    },
                ],
                testRunIds: [{ required: true, message: "请选择测试运行", trigger: "change" },],
            },
        };
    },
    props: {
        isShowDialog: {
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

    },
    methods: {
        loadData() {
            this.loading = true
            let pageable = {
                page: this.pagination.current - 1,
                size: this.pagination.pageSize,
            }
            let sort = this.orderBy + "," + this.orderByType;
            findTestRuns(this.projectId, null, this.keyword, {}, { pageable }, sort).then(res => {
                this.testRunList = res.content
                this.pagination.total = parseInt(res.totalElements);
            }).finally(() => {
                this.loading = false
            })
        },
        handlePageChange(pagination) {
            this.pagination.current = pagination.currentPage
            this.pagination.pageSize = pagination.pageSize
            this.loadData();
        },
        selectChangeEvent({ checked, records, reserves }) {
            this.selectedRowKeys = [...reserves.map(v => v.id), ...records.map(v => v.id)];
            this.formData.testRunIds = this.selectedRowKeys;
            this.$refs["reportForm"].validate();
        },
        onOK: function () {
            this.$refs["reportForm"].validate((valid) => {
                if (valid) {
                    console.log("onOK", this.formData)
                    this.formData.testRuns = [];
                    this.formData.testRunIds.forEach(element => {
                        this.formData.testRuns.push({ id: element })
                    });
                    this.formData.project = { id: this.projectId };
                    createTestReport(this.formData).then(res => {
                        VXETable.modal.message({ status: 'success', content: '新增成功' });
                        this.$emit("ok");
                        this.onCancel();
                    })
                }
            })

        },
        onCancel: function () {
            this.formData = {
                name: '',
                testRunIds: []
            }
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
