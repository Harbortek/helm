<template>
    <config-page :routes="routes()" style="padding-top:0;overflow: hidden;">
        <div style="display: flex;flex-direction: column;height:calc(100% + 90px)">
            <div class="toolbar">
                <vxe-toolbar size="medium" style="border-bottom: #d4d6d9 1px solid;">
                    <template #buttons>
                        <span style="margin-left: 10px;font-size: 18px;color: #2d2d2e;">所有用例</span>
                    </template>
                    <template #tools>
                        <!-- <vxe-button icon="vxe-icon-print" style="margin-left:10px;" type="text" content="导出用例" ></vxe-button> -->

                        <a-popover placement="bottomRight" v-model="reportPopover" :overlayStyle="{ width: '160px' }"
                            trigger="['click']]">
                            <template slot="content">
                                <div style="overflow:auto;margin:-5px">
                                    <div style="height:30px;">
                                        <vxe-button style="width:100%;text-align:left;" v-for="item in reportList"
                                            :key="item.id" type="text" @click="onClickReportDetail(item)">
                                            {{ item.name }}
                                        </vxe-button>
                                        <span v-if="reportList?.length == 0"
                                            style="font-size:14px;color:#969696;margin-left:5px;">暂无数据</span>
                                    </div>
                                    <a-divider style="margin:5px 0" />
                                    <div style="height:30px;">
                                        <vxe-button type="text" @click="onClickReportCreate">
                                            <a-icon style="margin-right:5px;" type="plus" />新增测试报告
                                        </vxe-button>
                                    </div>

                                </div>
                            </template>
                            <vxe-button v-action="'PAGE_WRITE|' + pageId" style="margin-left:10px;" type="text">
                                <a-icon style="margin-right:5px;" type="reconciliation" />查看测试报告
                            </vxe-button>
                        </a-popover>



                        <vxe-button v-action="'PAGE_WRITE|' + pageId" @click="onClickAssociateCase"
                            style="margin-left:10px;" type="text">
                            <a-icon style="margin-right:5px;" type="block" />添加关联用例
                        </vxe-button>
                    </template>
                </vxe-toolbar>

                <vxe-toolbar size="medium" style="border-bottom: #d4d6d9 1px solid;">
                    <template #buttons>
                        <div style="margin-left:10px;display:flex;width:500px">
                            <div style="flex:1">
                                通过<span style="font-size:18px;margin:auto 5px;font-weight:600;color: #2d2d2d;">{{
        ((currentTestRun?.passedCount * 100 / currentTestRun?.totalCount) || 0).toFixed(2)
    }}%</span>
                            </div>
                            <div style="flex:1">
                                已测<span style="font-size:18px;margin:auto 5px;font-weight:600;color: #2d2d2d;">{{
            currentTestRun?.executedCount }}/{{ currentTestRun?.totalCount }}</span>
                            </div>
                            <!-- <div class="table-result-percent" style="flex:2">
                                <a-tooltip style="margin-top:10px;">
                                    <template slot="title">
                                        通过:{{ getTestCaseTypeCount.pass }}
                                    </template>
    <div class="result-percent-item" :style="{ flex: getTestCaseTypeCount.pass }" style="background-color:#00a865;">
    </div>
    </a-tooltip>

    <a-tooltip style="margin-top:10px;">
        <template slot="title">
                                        失败:{{ getTestCaseTypeCount.fail }}
                                    </template>
        <div class="result-percent-item" :style="{ flex: getTestCaseTypeCount.fail }" key=""
            style="background-color:#eb3723;"></div>
    </a-tooltip>

    <a-tooltip style="margin-top:10px;">
        <template slot="title">
                                        阻塞:{{ getTestCaseTypeCount.block }}
                                    </template>

        <div class="result-percent-item" :style="{ flex: getTestCaseTypeCount.block }"
            style="background-color:#f59300;">
        </div>
    </a-tooltip>

    <a-tooltip style="margin-top:10px;">
        <template slot="title">
                                        跳过:{{ getTestCaseTypeCount.skip }}
                                    </template>

        <div class="result-percent-item" :style="{ flex: getTestCaseTypeCount.skip }" style="background-color:#87888a;">
        </div>
    </a-tooltip>

    <a-tooltip style="margin-top:10px;">
        <template slot="title">
                                        未执行:{{ getTestCaseTypeCount.init }}
                                    </template>
        <div class="result-percent-item" :style="{ flex: getTestCaseTypeCount.init }" style="background-color:#0064ff;">
        </div>
    </a-tooltip>
    </div> -->
                        </div>



                    </template>
                    <template #tools>
                        <vxe-button v-action="'PAGE_WRITE|' + pageId" status="text"
                            @click="onClickStartTest" :disabled="allTestCaseIds.length == 0">开始测试</vxe-button>
                        <a-dropdown :trigger="['click']">
                            <a-menu slot="overlay">
                                <a-menu-item key="edit" @click="showTestRunDialog = true">编辑测试运行</a-menu-item>
                                <a-menu-item key="delete" @click="onClickDeletePlan">删除测试运行</a-menu-item>
                            </a-menu>
                            <vxe-button v-action="'PAGE_WRITE|' + pageId" status="text">··· 更多</vxe-button>
                        </a-dropdown>
                    </template>
                </vxe-toolbar>

                <vxe-toolbar size="medium">
                    <template #buttons>
                        <vxe-button style="margin-left:10px;" :disabled="selectedRowKeys.length == 0 || !hasEditPerm"
                            @click="onClickOperate('executor')" status="text" content="更改执行人"></vxe-button>
                        <vxe-button style="margin-left:10px;" :disabled="selectedRowKeys.length == 0 || !hasEditPerm"
                            @click="onClickOperate('result')" status="text" content="更改执行结果"></vxe-button>
                        <vxe-button style="margin-left:10px;" :disabled="selectedRowKeys.length == 0 || !hasEditPerm"
                            @click="onClickOperate('testRun')" status="text" content="关联其他测试运行"></vxe-button>
                        <vxe-button style="margin-left:10px;" :disabled="selectedRowKeys.length == 0 || !hasEditPerm"
                            @click="onClickRemove" status="text" content="移除"></vxe-button>
                        <span v-if="selectedRowKeys != 0"
                            style="margin-left: 10px;font-size: 14px; color: #87888a; display: flex;align-items: center;">
                            已选择 {{ selectedRowKeys.length > 0 ? selectedRowKeys.length : 0 }} 个</span>
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

                            <a-badge :number-style="{ backgroundColor: '#338fe5' }" :count="getFilterCount()">
                                <a-button type="text" @click="onClickFilter">筛选<a-icon
                                        :component="filterIcon" /></a-button>
                            </a-badge>

                            <vxe-input v-show="waitInputSearch" v-model="keyword" placeholder="搜索" type="search"
                                className="searchbox" clearable @search-click="onSearch()"
                                @blur="onBlurSearch"></vxe-input>
                            <a-button type="text" v-show="!waitInputSearch" @click="waitInputSearch = true"><a-icon
                                    type="search" />
                            </a-button>


                        </a-space>
                    </template>
                </vxe-toolbar>
            </div>
            <tracker-item-filter style="transition: all .25s;" :members="members" :tracker="trackerFilter"
                :class="activeKey == 'true' ? 'filter-flex-active' : 'filter-flex'" :conditionGroups="conditionGroups"
                :activeKey="activeKey" @refresh="onRefreshFilter"></tracker-item-filter>
            <a-layout style="flex:1 1 65%;">
                <a-layout-content>
                    <div class="table-layout" style="height:calc(100% - 50px);">
                        <vxe-table style="cursor: pointer;" height="auto" :data="testCaseList" :loading="loading" border
                            :row-config="{ isHover: true }"
                            :checkbox-config="{ checkRowKeys: selectedRowKeys, reserve: false, checkField: 'checked', trigger: 'row' }"
                            show-overflow @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent">
                            <vxe-column type="checkbox" title="" width="60" align="center"></vxe-column>
                            <vxe-column field="itemNo" title="编号" width="120" align="center">
                                <template #default="{ row }">
                                    <span v-if="currentProjectKeyName">
                                        {{ currentProjectKeyName + '-' + row.testCase?.itemNo }}
                                    </span>
                                </template>
                            </vxe-column>
                            <vxe-column field="name" title="用例名称" header-align="center">
                                <template #default="{ row }">
                                    <span>
                                        {{ row.testCase?.name }}
                                    </span>
                                </template>
                            </vxe-column>
                            <vxe-column field="owner" title="负责人" width="120" header-align="center" align="center">
                                <template #default="{ row }">
                                    <span v-if="row.testCase?.owner">
                                        <h-avatar :name="row.testCase?.owner?.name"
                                            :icon="row.testCase?.owner?.icon"></h-avatar>
                                    </span>
                                    <span v-else>-</span>
                                </template>
                            </vxe-column>

                            <vxe-column field="status" title="优先级" width="120" header-align="center" align="center">
                                <template #default="{ row }">
                                    <a-tag style="border:none"
                                        :style="{ color: row.testCase?.priority?.color, backgroundColor: row.testCase?.priority?.backgroundColor }">{{
        row.testCase?.priority?.name }}</a-tag>
                                </template>
                            </vxe-column>
                            <!-- <vxe-column field="" title="用例类型">
                                <template #default="{ row }">
                                    <a-tag v-if="row.testCaseType" style="cursor: pointer; border: none;
                                            background-color: rgba(144, 144, 144, .15);">
                                        {{ row.testCaseType.name }}</a-tag>

                                </template>
                            </vxe-column> -->
                            <vxe-column field="executor" title="执行人" width="120" header-align="center" align="center">
                                <template #default="{ row }">
                                    <h-avatar :name="row.executor?.name" :icon="row.executor?.icon"></h-avatar>
                                </template>
                            </vxe-column>
                            <vxe-column field="result" title="执行结果" width="100" header-align="center" align="center">
                                <template #default="{ row }">
                                    <!-- <EnumItemValue :value="row.result?.id" /> -->
                                     {{ void (item = resultDataList.find(item => item.id == row.result?.id)) }}
                                    <span :style="{ color: item?.color, 'background-color': item?.backgroundColor }">{{ item?.name }}</span>
                                </template>
                            </vxe-column>
                        </vxe-table>
                    </div>
                    <vxe-pager :loading="loading" :current-page="pagination.current" :page-size="pagination.pageSize"
                        :total="pagination.total"
                        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                        @page-change="handlePageChange">
                    </vxe-pager>
                </a-layout-content>
            </a-layout>
        </div>

        <test-run-dialog :isShowDialog="showTestRunDialog" :editMode="editMode" :projectId="projectId"
            :currentTestRun="currentTestRun" @cancel="showTestRunDialog = false" @ok="onTestRunDialogOK" />

        <test-run-operate-dialog :isShowDialog="showOperateDialog" :projectId="projectId" :testRunId="testRunId"
            :selectedRowKeys="selectedRowKeys" :editMode="operateEditMode" @cancel="showOperateDialog = false"
            @ok="loadDataTestCase" />

        <tracker-item-select-modal :projectId="projectId" :initalData="allTestCaseIds" :isTest="true"
            ref="trackerItmeSlectModal" @trackerItemSelector="onSelectTrackerItems" />

        <test-report-create-dialog :isShowDialog="showReportCreateDialog" :projectId="projectId"
            @cancel="onCancelReportCreate"></test-report-create-dialog>
    </config-page>
</template>

<script>
import Vue from "vue";
import VXETable from "vxe-table";
import HAvatar from '@/components/avatar/h-avatar.vue';
import { mapGetters } from "vuex";
import ContentPage from '@/components/page/content/ContentPage.vue';
import TrackerItemFilter from '@/components/tool/TrackerItemFilter'
import TestRunDialog from './TestRunDialog.vue';
import TestRunOperateDialog from './TestRunOperateDialog.vue';
import TrackerItemSelectModal from '@/components/dialog/TrackerItemSelectModal'
import TestReportCreateDialog from './TestReportCreateDialog.vue';
import {
    findOneTestRun, updateTestRun, deleteTestRun, associateTestCases,
    removeTestResults, findTestResults,findTestCaseIds
} from '@/services/test/TestRunService'
import ConfigPage from '../../components/config-page/ConfigPage.vue';
import { findProjectRolesAndMembers } from "@/services/tracker/ProjectRoleMemberService";
import { findEnumsByCode, } from "@/services/system/EnumService";
import { findTestReportByTestRunId } from '@/services/test/TestReportService'
import { hasPermission } from '@/utils/permission'
import EnumItemValue from '@/components/enum/EnumItemValue.vue';


export default {
    name: 'TestRunResultList',
    components: {
        ContentPage, TrackerItemFilter, TestRunDialog, ConfigPage, TrackerItemSelectModal,
        TestRunOperateDialog, TestReportCreateDialog, HAvatar, EnumItemValue
    },
    data() {
        return {
            loading: false,
            keyword: '',
            pagination: {
                current: 1,
                pageSize: 10,
            },
            selectedRowKeys: [],
            orderByList: [
                { name: '用例编号', value: 'itemNo' },
                { name: '用例名称', value: 'name' },
                { name: '负责人', value: 'ownerId' },
                { name: '优先级', value: 'priorityId' },
                // { name: '用例类型', value: 'testCaseTypeId' },
                // {name: '执行人', value: 'executor'},
                // {name: '执行结果', value: 'result'},
            ],
            currentTab: '全部测试用例',
            isShowViewConfig: false,
            testCaseList: [],
            filterIcon: "expandDown",
            activeKey: 'false',
            members: [],
            trackerFilter: {},
            conditionGroups: [],
            reportList: [],

            orderBy: 'name',
            orderByType: 'DESC',
            groupBy: '',
            waitInputSearch: false,
            selectItem: '1',
            showTestRunDialog: false,
            showOperateDialog: false,
            showReportCreateDialog: false,
            currentTestRun: {},
            editMode: 'edit',
            operateEditMode: 'executor',
            reportPopover: false,
            allTestCaseIds:[],
            resultDataList:[],
            routes: function () {
                return [{
                    name: "testRun", meta: {
                        icon: "blank",
                        title: "menu.test.testRun",
                        show: false,
                    },
                }, {
                    name: "testRunCase",
                    meta: {
                        icon: "blank",
                        title: this.currentTestRun?.name,
                        show: false,
                    },
                }]
            },
        }
    },
    computed: {
        ...mapGetters("project", ["currentProjectKeyName"]),
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },
        testRunId() {
            return this.$route.params.testRunId
        },
        hasEditPerm() {
            return hasPermission("PAGE_WRITE",this.pageId);
        },
    },
    mounted() {
        this.loadData();
        this.loadDataTrackerFilter();
        this.loadDataReport();
    },
    methods: {
        onClickStartTest() {
            this.$router.push({
                name: 'testRunExecute',
                params: { testRunId: this.testRunId }
            })
        },
        onClickOperate(operate) {
            this.operateEditMode = operate
            this.showOperateDialog = true
        },
        onClickRemove() {
            VXETable.modal.confirm({
                title: '移除用例',
                content: '正在将' + this.selectedRowKeys.length + '个测试用例从测试运行中移除，是否移除?'
            }).then(type => {
                if (type === 'confirm') {
                    removeTestResults(this.testRunId, this.selectedRowKeys).then(() => {
                        VXETable.modal.message({ content: this.selectedRowKeys.length + '个测试用例移除成功', status: 'info' })
                        this.loadDataTestCase();
                    })
                }
            })
        },
        loadData() {
            this.loading = true;
            findOneTestRun(this.testRunId).then(resp => {
                this.currentTestRun = resp;
            })

            this.loadDataTestCase();

        },
        loadDataTestCase() {
            this.selectedRowKeys = []
            this.loading = true;
            let keyword = this.keyword
            let sort = this.orderBy + "," + this.orderByType;
            let pageable = {
                page: this.pagination.current - 1,
                size: this.pagination.pageSize,
            }
            let filter = { "conditionGroups": this.conditionGroups }

            findTestResults(this.projectId, this.testRunId, filter, keyword, pageable, sort).then(resp => {
                this.testCaseList = resp.content
                console.log("all", this.testCaseList)
                this.pagination.total = parseInt(resp.totalElements);
            }).finally(() => {
                this.loading = false;
            })
            findTestCaseIds(this.testRunId).then(resp => {
                this.allTestCaseIds = resp.map(v => {return {id: v}})
            })
        },
        loadDataReport() {
            findTestReportByTestRunId(this.testRunId).then(res => {
                this.reportList = res;
            })
        },
        onClickReportCreate() {
            this.showReportCreateDialog = true;
            this.reportPopover = false;
        },
        onCancelReportCreate() {
            this.showReportCreateDialog = false;
            this.loadDataReport();
        },
        handlePageChange(pagination) {
            this.pagination.current = pagination.currentPage
            this.pagination.pageSize = pagination.pageSize
            this.loadDataTestCase();
        },
        onClickReportDetail(row) {
            const routeData = this.$router.resolve({
                path: `/tracker/project/${this.projectId}/testReport/testReportDetail/${row.id}`
            });
            window.open(routeData.href, '_blank');
            // this.$router.push({
            //     name: 'testReportDetail', 
            //     params: { reportId:row.id }
            // })
        },
        onTestRunDialogOK(item) {
            this.showTestRunDialog = false
            if (this.editMode === 'edit') {
                updateTestRun(item).then(resp => {
                    this.loadData()
                    VXETable.modal.message({ content: '更新成功', status: 'success' })
                })
            }
        },
        onClickDeletePlan() {
            // 
            VXETable.modal.confirm({
                title: '删除测试运行',
                content: '测试运行「' + this.currentTestRun.name + '」的数据将被删除'
            }).then(type => {
                if (type === 'confirm') {
                    deleteTestRun(this.testRunId).then(() => {
                        VXETable.modal.message({ content: '测试运行「' + this.currentTestRun.name + '」已被删除', status: 'info' })
                        this.$router.push({
                            name: 'testRun',
                        })
                    })
                }
            })
        },
        onClickAssociateCase() {
            this.$refs.trackerItmeSlectModal.view();
        },
        selectChangeEvent({ checked, records, reserves }) {
            this.selectedRowKeys = [...reserves.map(v => v.id), ...records.map(v => v.id)];
        },
        onSelectTrackerItems(e) {
            let items = []
            let ids = this.testCaseList.map(item => item.id)
            items = e.filter(item => ids.indexOf(item.id) === -1)
            associateTestCases(this.testRunId, items.map(item => item.id)).then(res => {
                VXETable.modal.message({ content: '操作成功', status: 'success' })
                this.loadDataTestCase();
            })
        },
        onChangeTab: function (activeKey) {
            this.currentTab = activeKey
        },
        onViewConfig: function () {

        },
        onCreateTestRun() {

        },
        getFilterCount() {
            let count = 0;
            this.conditionGroups.forEach(item => {
                item.conditions.forEach(item2 => {
                    if (item2 && item2.value) {
                        count++;
                    }
                })
            })
            return count;
        },
        onClickFilter() {
            if (this.activeKey == 'true') {
                this.activeKey = 'false'
                this.filterIcon = "expandDown"
            } else {
                this.activeKey = 'true'
                this.filterIcon = 'collapseUp'
                if (this.members?.length == 0) {
                    findProjectRolesAndMembers(this.projectId, "").then(res => {
                        this.members = res.members;
                    })
                }
            }
        },
        onSearch() {
            this.loadDataTestCase();
        },
        onBlurSearch() {
            if (!this.keyword) {
                this.waitInputSearch = false
            }
            this.loadDataTestCase();
        },
        onRefreshFilter() {
            this.loadDataTestCase();
        },
        onChangeOrderBy(value) {
            if (this.orderBy == value.key && this.orderByType == "ASC") {
                this.orderByType = "DESC"
            } else {
                this.orderByType = "ASC"
            }
            this.orderBy = value.key
            // this.loadData(this.conditionGroups)
            this.loadDataTestCase();
        },
        onChangeGroupBy() {

        },
        loadDataTrackerFilter() {
            findEnumsByCode('TEST_CASE_RESULT').then((resp) => {
                this.trackerFilter.resultId = Vue.observable([]);
                this.resultDataList = resp;
                resp.forEach(item => {
                    this.trackerFilter.resultId.push({ id: item.id, name: item.name })
                });
            });
            findEnumsByCode('TRACKER_PRIORITY').then((resp) => {
                this.trackerFilter.priority = Vue.observable([]);
                this.prioritys = resp;
                resp.forEach(item => {
                    this.trackerFilter.priority.push({ id: item.id, name: item.name })
                });
            });
            this.trackerFilter.trackerFields = [
                { id: '3', name: "用例名称", systemProperty: "name", inputType: "TEXT" },
                { id: '4', name: "负责人", systemProperty: "ownerId", inputType: "USER" },
                { id: '5', name: "优先级", systemProperty: "priority", inputType: "OPTIONS" },
                { id: '7', name: "执行结果", systemProperty: "resultId", inputType: "OPTIONS" },
                { id: '13', name: "执行人", systemProperty: "executorId", inputType: "USER" },
            ]
        },

    },
}
</script>

<style lang="less" scoped>
.table-result-percent {
    display: flex;
    width: 100%;
    height: 30px;
    line-height: 30px;
    cursor: pointer;

    .result-percent-item {
        height: 8px;
        margin-right: 1px;
    }

    .result-percent-item:first-of-type {
        border-bottom-left-radius: 3px;
        border-top-left-radius: 3px;
    }

    .result-percent-item:last-of-type {
        border-bottom-right-radius: 3px;
        border-top-right-radius: 3px;
    }
}

.filter-flex {
    flex: 0 1 0%;
    height: 0px;
}

.filter-flex-active {
    flex: 2 1 26%;
}
</style>