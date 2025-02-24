<template>
    <content-page>
        <div style="display: flex;flex-direction: column;height: 100%;">
            <div class="toolbar">
                <vxe-toolbar size="medium">
                    <template #buttons>
                        <vxe-button v-action="'PAGE_WRITE|' + pageId" style="margin-left:10px;" status="primary"
                            @click="showReportCreateDialog = true" content="新建测试报告"></vxe-button>
                    </template>
                    <template #tools>
                        <a-space>
                            排序
                            <a-select ref="orderBy" optionLabelProp="label" :value="orderBy" style="width: 130px">

                                <a-select-option v-for="item in orderByList" :key="item.value" :value="item.value"
                                    :label="item.name" @click="onChangeOrderBy">{{ item.name }}
                                    <template v-if="item.value == orderBy">
                                        <a-icon v-if="orderByType == 'ASC'" type="arrow-up"
                                            style="float:right;margin-top:5px;color:#338fe5" />
                                        <a-icon v-else type="arrow-down"
                                            style="float:right;margin-top:5px;color:#338fe5" />
                                    </template>
                                </a-select-option>
                            </a-select>

                            <vxe-input v-model="keyword" placeholder="搜索测试报告名称" type="search" className="searchbox"
                                clearable @search-click="onSearch()"></vxe-input>


                        </a-space>
                    </template>
                </vxe-toolbar>
            </div>
            <a-layout style="margin-top:10px">
                <a-layout-content style="height:100%">
                    <div class="table-layout" style="height:95%;">
                        <vxe-table style="cursor: pointer;" height="auto" :data="testReportList" :loading="loading"
                            show-overflow :row-config="{ isHover: true, isCurrent: true }"
                            @current-change="currentChangeEvent">
                            <vxe-column field="name" title="测试报告名称"></vxe-column>
                            <vxe-column field="createBy" title="创建者">
                                <template #default="{ row }">
                                    <h-avatar :name="row.createBy?.name" :icon="row.createBy?.icon"></h-avatar>
                                </template>
                            </vxe-column>
                            <vxe-column field="createDate" title="创建时间"></vxe-column>
                            <vxe-column field="testRuns" title="关联测试运行">
                                <template #default="{ row }">
                                    <div style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">
                                        <span v-for="item, index in row.testRuns" :key="item.id">
                                            {{ item.name }}<span v-if="index != row.testRuns.length - 1">,</span>
                                        </span>
                                    </div>
                                </template>
                            </vxe-column>
                            <vxe-column field="testRuns" title="操作">
                                <template #default="{ row }">
                                    <div @click.stop>
                                        <vxe-button style="padding-left:0" status="primary" type="text" content="下载"
                                            @click="onClickExportPDF(row.id)"></vxe-button>
                                        <vxe-button v-action="'PAGE_WRITE|' + pageId" status="danger" type="text"
                                            content="删除" @click="onClickDelete(row)"></vxe-button>
                                    </div>
                                </template>
                            </vxe-column>

                        </vxe-table>
                        <vxe-pager :loading="loading" :current-page="pagination.current"
                            :page-size="pagination.pageSize" :total="pagination.total"
                            :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                            @page-change="handlePageChange">
                        </vxe-pager>

                    </div>
                </a-layout-content>
            </a-layout>
        </div>
        <test-report-create-dialog :isShowDialog="showReportCreateDialog" :projectId="projectId" @ok="loadData"
            @cancel="onClickReportCreate"></test-report-create-dialog>
    </content-page>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import { VXETable } from 'vxe-table';
import ContentPage from '@/components/page/content/ContentPage.vue';
import TrackerItemFilter from '@/components/tool/TrackerItemFilter'
import { findTestReports, deleteTestReport } from '@/services/test/TestReportService'
import TestReportCreateDialog from './TestReportCreateDialog.vue';


export default {
    name: 'TestReportList',
    components: { ContentPage, TrackerItemFilter, HAvatar, TestReportCreateDialog },
    data() {
        return {
            loading: false,
            keyword: '',
            orderByList: [
                { name: '创建时间', value: 'createDate' },
                { name: '创建人', value: 'createBy' },
                { name: '测试运行名称', value: 'name' },
            ],
            pagination: {
                current: 1,
                pageSize: 10,
            },
            testReportList: [],
            orderBy: 'createDate',
            orderByType: 'DESC',
            showReportCreateDialog: false,
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },
    },
    mounted() {
        this.loadData();

    },
    methods: {
        onClickExportPDF(id) {
            console.log("newValue", id)
            this.$router.push({
                name: 'testReportDetail',
                params: { reportId: id }
            })
        },
        onClickDelete(row) {
            VXETable.modal.confirm({
                title: '删除测试报告',
                content: '删除测试报告「' + row.name + '」，此操作无法撤销，是否删除？'
            }).then(type => {
                if (type === 'confirm') {
                    deleteTestReport(row.id).then(res => {
                        VXETable.modal.message({ content: '「' + row.name + '」删除成功', status: 'info' })
                        this.loadData();
                    })
                }
            })
        },
        loadData() {
            this.loading = true
            let pageable = {
                page: this.pagination.current - 1,
                size: this.pagination.pageSize,
            }
            let sort = this.orderBy + "," + this.orderByType;
            findTestReports(this.projectId, this.keyword, pageable, sort).then(res => {
                this.testReportList = res.content
                console.log("data", res)
                this.pagination.total = parseInt(res.totalElements);
            }).finally(() => {
                this.loading = false
            })
        },
        currentChangeEvent({ rowIndex, newValue }) {
            console.log("newValue", newValue)
            this.$router.push({
                name: 'testReportDetail',
                params: { reportId: newValue.id }
            })
        },
        handlePageChange(pagination) {
            this.pagination.current = pagination.currentPage
            this.pagination.pageSize = pagination.pageSize
            this.loadData();
        },
        onSearch() {
            this.loadData();
        },
        onChangeOrderBy(value) {
            if (this.orderBy == value.key && this.orderByType == "ASC") {
                this.orderByType = "DESC"
            } else {
                this.orderByType = "ASC"
            }
            this.orderBy = value.key
            this.loadData()
        },
        onClickReportCreate() {
            this.showReportCreateDialog = false
        },
    },
}
</script>

<style lang="less" scoped></style>