<template>
    <content-page>
        <div style="display: flex;flex-direction: column;height: 100%;">
            <a-tabs style="height:45px;" :active-key="currentTab" @change="onChangeTab">
                <a-tab-pane :key="view.id" :tab="view.name" v-for="view in views">
                </a-tab-pane>
            </a-tabs>
            <div class="toolbar">
                <vxe-toolbar size="medium">
                    <template #buttons>
                        <vxe-button style="margin-left:10px;" v-action="'PAGE_WRITE|' + pageId" status="primary"
                            content="新增 测试运行" @click="onCreateTestRun"></vxe-button>
                        <!-- <span style="margin-left: 10px;font-size: 14px; color: #87888a; display: flex;align-items: center;">共{{
                            testRunList.length }}个</span> -->
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

                            <!-- 分组
                            <a-select :value="groupBy" style="width: 120px" @change="onChangeGroupBy">
                                <a-select-option value="">不分组</a-select-option>
                                <a-select-option value="project">关联项目</a-select-option>
                                <a-select-option value="srpint">关联迭代</a-select-option>
                                <a-select-option value="status">状态</a-select-option>
                            </a-select> -->


                            <a-badge :number-style="{ backgroundColor: '#338fe5' }" :count="getFilterCount()">
                                <a-button type="text" @click="onClickFilter">筛选<a-icon
                                        :component="filterIcon" /></a-button>
                            </a-badge>

                            <vxe-input v-model="keyword" placeholder="搜索测试运行" type="search" className="searchbox"
                                clearable @search-click="onSearch()"></vxe-input>
                        </a-space>
                    </template>
                </vxe-toolbar>
            </div>
            <tracker-item-filter style="transition: all .25s;"
                :class="activeKey == 'true' ? 'filter-flex-active' : 'filter-flex'" :members="members"
                :tracker="trackerFilter" :conditionGroups="conditionGroups" :activeKey="activeKey"
                @refresh="onRefreshFilter"></tracker-item-filter>
            <a-layout style="flex:1 1 65%;">
                <a-layout-content style="height:100%;">
                    <div style="height:95%;">
                        <vxe-table style="cursor: pointer;" height="auto" :data="testRunList" :loading="loading" border
                            show-overflow @current-change="currentChangeEvent"
                            :row-config="{ isHover: true, isCurrent: true }">
                            <vxe-column type="seq" width="60"></vxe-column>
                            <vxe-column field="name" title="测试运行名称" header-align="center"></vxe-column>
                            <vxe-column field="owner" title="负责人" align="center" width="120">
                                <template #default="{ row }">
                                    <h-avatar :name="row.owner?.name" :icon="row.owner?.icon"></h-avatar>
                                </template>
                            </vxe-column>

                            <vxe-column field="status" title="状态" align="center" width="120">
                                <template #default="{ row }">
                                    <a-tag style="border-radius: 10px;font-size: 13px;"
                                        :style="{ color: row.status?.color, borderColor: row.status?.color }">
                                        {{ row.status?.name }}
                                    </a-tag>
                                </template>
                            </vxe-column>
                            <vxe-column field="passRate" title="通过率" align="center" width="100">
                                <template #default="{ row }">
                                    {{ row.passRate || 0.0 }}%
                                </template>
                            </vxe-column>
                            <vxe-column field="" title="已测用例" align="center" width="100">
                                <template #default="{ row }">
                                    {{ row.executedCount }}/{{ row.totalCount }}
                                </template>
                            </vxe-column>
                            <!-- <vxe-column field="" title="结果分布" align="center" width="200">
                                <template #default="{ row }">
                                    <div class="table-result-percent" style="">
                                        <a-tooltip v-if="item.null">
                                            <template slot="title">
                                                暂无测试用例
                                            </template>
                                            <div class="result-percent-item" style="flex:1;background-color:#dfe1e5;">
                                            </div>
                                        </a-tooltip>
                                        <a-tooltip v-if="item.pass != 0">
                                            <template slot="title">
                                                通过:{{ item.pass }}
                                            </template>
                                            <div class="result-percent-item" :style="{ flex: item.pass }"
                                                style="background-color:#00a865;">
                                            </div>
                                        </a-tooltip>

                                        <a-tooltip v-if="item.fail != 0">
                                            <template slot="title">
                                                失败:{{ item.fail }}
                                            </template>
                                            <div class="result-percent-item" :style="{ flex: item.fail }"
                                                style="background-color:#eb3723;">
                                            </div>
                                        </a-tooltip>

                                        <a-tooltip v-if="item.block != 0">
                                            <template slot="title">
                                                阻塞:{{ item.block }}
                                            </template>

                                            <div class="result-percent-item" :style="{ flex: item.block }"
                                                style="background-color:#f59300;">
                                            </div>
                                        </a-tooltip>
                                        <a-tooltip v-if="item.skip != 0">
                                            <template slot="title">
                                                跳过:{{ item.skip }}
                                            </template>

                                            <div class="result-percent-item" :style="{ flex: item.skip }"
                                                style="background-color:#87888a;">
                                            </div>
                                        </a-tooltip>


                                        <a-tooltip v-if="item.init != 0">
                                            <template slot="title">
                                                未执行:{{ item.init }}
                                            </template>
                                            <div class="result-percent-item" :style="{ flex: item.init }"
                                                style="background-color:#0064ff;">
                                            </div>
                                        </a-tooltip>
                                    </div>
                                </template>
                            </vxe-column> -->
                            <vxe-column field="phase" title="测试阶段" align="center" width="120">
                                <template #default="{ row }">
                                    {{ row.phase?.name }}
                                </template>
                            </vxe-column>
                            <vxe-column field="sprint.name" title="关联迭代" align="center" width="120">
                                <template #default="{ row }">
                                    {{ row.sprint?.name }}
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
        <test-run-dialog :isShowDialog="showTestRunDialog" :editMode="editMode" :projectId="projectId"
            :currentTestRun="currentTestRun" @cancel="showTestRunDialog = false" @ok="onTestRunDialogOK" />
    </content-page>
</template>

<script>

import ContentPage from '@/components/page/content/ContentPage.vue';
import TrackerItemFilter from '@/components/tool/TrackerItemFilter'
import TestRunDialog from './TestRunDialog.vue';
import VXETable from "vxe-table";
import Vue from "vue";
import HAvatar from '@/components/avatar/h-avatar.vue';


import { findEnumsByCode, } from "@/services/system/EnumService";
import { findProjects } from "@/services/tracker/ProjectService";
import { findSprints } from "@/services/plan/SprintService";
import { findTestRuns, createTestRun, updateTestRun } from '@/services/test/TestRunService'
import { findProjectRolesAndMembers } from "@/services/tracker/ProjectRoleMemberService";


export default {
    name: 'TestRunList',
    components: { ContentPage, TrackerItemFilter, TestRunDialog, HAvatar },
    data() {
        return {
            loading: false,
            keyword: '',
            views: [],
            orderByList: [
                { name: '状态', value: 'status' },
                { name: '测试运行名称', value: 'name' },
                { name: '测试阶段', value: 'phaseId' },
                { name: '通过率', value: 'passRate' },
                { name: '关联迭代', value: 'sprintId' },
                // {name: '关联项目', value: 'projectId'},
            ],
            pagination: {
                current: 1,
                pageSize: 10,
            },
            currentTab: 0,
            isShowViewConfig: false,
            testRunList: [],
            filterIcon: "expandDown",
            activeKey: 'false',
            members: [],
            trackerFilter: {},
            conditionGroups: [],
            resultList: [],

            orderBy: 'name',
            orderByType: 'DESC',
            groupBy: '',
            editMode: 'create',
            showTestRunDialog: false,
            currentTestRun: undefined,
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
        this.loadTrackerFilter();

    },
    methods: {
        trackerItemSelector(e) {
            console.log(e)
        },
        loadData() {
            this.loading = true
            let pageable = {
                page: this.pagination.current - 1,
                size: this.pagination.pageSize,
            }
            let sort = this.orderBy + "," + this.orderByType;
            let statusId = this.currentTab == 0 ? null : this.currentTab;
            findTestRuns(this.projectId, statusId, this.keyword, { "conditionGroups": this.conditionGroups }, pageable, sort).then(res => {
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
        onTestRunDialogOK(item) {
            this.showTestRunDialog = false
            if (this.editMode === 'create') {
                createTestRun(item).then(resp => {
                    this.loadData()
                    VXETable.modal.message({ content: '新建成功', status: 'success' })
                })
            }
        },
        currentChangeEvent({ newValue }) {
            this.$router.push({
                name: 'testRunCase',
                params: { testRunId: newValue.id }
            })
        },
        onChangeTab: function (activeKey) {
            this.currentTab = activeKey
            this.loadData();
        },
        onViewConfig: function () {

        },
        onCreateTestRun() {
            this.showTestRunDialog = true;
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
            this.loadData();
        },
        onRefreshFilter() {
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
        onChangeGroupBy() {

        },
        loadTrackerFilter() {
            findEnumsByCode('TEST_PLAN_PHASE').then((resp) => {
                this.trackerFilter.phaseId = Vue.observable([]);
                resp.forEach(item => {
                    this.trackerFilter.phaseId.push({ id: item.id, name: item.name })
                });
            });
            findEnumsByCode('TEST_RUN_STATUS_MEANING').then((resp) => {
                this.trackerFilter.status = Vue.observable([]);
                this.views = Vue.observable([{ id: 0, name: "全部测试运行" }]);
                resp.forEach(item => {
                    this.trackerFilter.status.push({ id: item.id, name: item.name })
                    this.views.push({ id: item.id, name: item.name })
                });
            });
            findProjects({}, {}).then(resp => {
                this.trackerFilter.projectId = Vue.observable([]);
                resp.content.forEach(item => {
                    this.trackerFilter.projectId.push({ id: item.id, name: item.name })
                });
            })
            findSprints(this.projectId).then(resp => {
                this.trackerFilter.sprintId = Vue.observable([]);
                resp.forEach(item => {
                    this.trackerFilter.sprintId.push({ id: item.id, name: item.name })
                });
            })
            this.trackerFilter.trackerFields = [
                { id: 1, name: "测试运行名称", systemProperty: "name", inputType: "TEXT" },
                { id: 2, name: "负责人", systemProperty: "ownerId", inputType: "USER" },
                { id: 3, name: "状态", systemProperty: "status", inputType: "OPTIONS" },
                { id: 4, name: "测试阶段", systemProperty: "phaseId", inputType: "OPTIONS" },
                { id: 5, name: "关联项目", systemProperty: "projectId", inputType: "PROJECT" },
                { id: 6, name: "关联迭代", systemProperty: "sprintId", inputType: "OPTIONS" },
            ]
        },

    },
}
</script>

<style lang="less" scoped>
.table-result-percent {
    display: flex;
    width: 100%;
    height: 10px;
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