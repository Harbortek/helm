<template>
    <config-page :routes="routes()" style="padding:10px;padding-top:0px;">
        <a-layout style="width: 100%;height: calc(100% + 90px);">
            <a-layout-sider width="500" style="background-color: #fff;margin: 15px;padding:10px;" :collapsible="false">
                <vxe-input class-name="vxe-input-no-border" v-model="keyword" placeholder="搜索" clearable>
                    <template #prefix>
                        <i class="vxe-icon-search"></i>
                    </template>
                </vxe-input>
                <!-- <vxe-toolbar ref="xToolbar" size="medium" style="margin-right: 5px;">
                    <template #tools>
                        <a-space>
                             执行结果
                            <a-select v-model="filterResult" :maxTagCount="1" mode="multiple" placeholder="全部" style="min-width:120px;max-width: 170px;">
                                <a-select-option v-for="item in resultList" :key="item.id"
                                    :value="item.id" :label="item.name">{{ item.name }}
                                </a-select-option>
                            </a-select>
                            优先级
                            <a-select v-model="filterPriority" :maxTagCount="1" mode="multiple" placeholder="全部" style="min-width:120px;max-width: 160px">
                                <a-select-option v-for="item in priorityList" :key="item.id"
                                    :value="item.id" :label="item.name">{{ item.name }}
                                </a-select-option>
                            </a-select>
                        </a-space>

                    </template>
                </vxe-toolbar> -->
                <div style="height: calc(100% - 85px);">
                    <vxe-table ref="vxeTable" style="cursor: pointer;" height="auto" :data="getTestCaseList" border
                        size="mini" show-overflow :row-config="{ isHover: true, isCurrent: true }"
                        @current-change="currentChangeEvent">
                        <vxe-column field="itemNo" title="编号" header-align="center" width="100">
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

                        <!-- <vxe-column field="executor" title="执行人">
                            <template #default="{ row }">
                                <h-avatar :name="row.executor?.name" :icon="row.executor?.icon"></h-avatar>
                            </template>
                        </vxe-column> -->
                        <vxe-column field="result" title="执行结果" width="100" align="center">
                            <template #default="{ row }">
                                <a-tag v-if="row.result" style="border-radius: 10px;font-size: 13px;"
                                    :style="{ color: row.result?.color, borderColor: row.result?.color }">
                                    {{ row.result?.name }}
                                </a-tag>
                            </template>
                        </vxe-column>
                    </vxe-table>
                </div>
            </a-layout-sider>
            <a-layout style="margin:15px;">
                <a-layout-content style="background-color: #fff;padding:10px;overflow: auto;">
                    <div style="background-color: #fff;padding:16px;border-bottom: 1px solid #f0f0f0;">
                        <vxe-button style="float:right" @click="onClickEditCase" type="text"
                            icon="vxe-icon-edit">编辑用例</vxe-button>
                        <div style="font-size:20px;font-weight: bold;">{{ currentTestResult?.testCase?.name }}</div>
                    </div>
                    <a-row class="ui-task-primary-fields" v-if="currentTestResult">
                        <!-- 
                        <tracker-item-user-select :projectId="projectId" :field="{id:'1',systemProperty:'assignedToId',title:'执行人'}"
                            :trackerItem="currentTestResult"></tracker-item-user-select> -->

                        <quick-picker :title="currentTestResult?.executor?.name" :sub-title="'执行人'"
                            :icon-url="iconUrl(currentTestResult?.executor?.icon)"
                            :icon-text="currentTestResult?.executor?.name">
                            <template slot="icon">
                                <h-avatar :name="currentTestResult?.executor?.name"
                                    :icon="currentTestResult?.executor?.icon" :isShowName="false" :size="32"></h-avatar>
                            </template>
                        </quick-picker>

                        <quick-picker :title="currentTestResult?.testCase?.priority?.name" :sub-title="'优先级'">
                            <template slot="icon">
                                <a-avatar
                                    :style="{ color: '#fff', backgroundColor: currentTestResult?.testCase?.priority?.name == '最高' ? currentTestResult?.testCase?.priority?.backgroundColor : currentTestResult?.testCase?.priority?.color }">
                                    <a-icon slot="icon" component="priority" />
                                </a-avatar>
                            </template>
                        </quick-picker>

                        <quick-picker :title="currentTestResult?.testCaseType?.name" :sub-title="'用例类型'">
                            <template slot="icon">
                                <a-avatar :style="{ color: '#606060', backgroundColor: '#e8e8e8' }">
                                    <a-icon type="book" />
                                </a-avatar>
                            </template>
                        </quick-picker>
                    </a-row>

                    <div class="ui-task-detail__tabs">
                        <a-tabs v-model="currentTab" default-active-key="DETAIL" @change="onChangeTab">
                            <template #tabBarExtraContent>
                                <vxe-button style="float:right;z-index: 1;" @click="onClickCreateItems"
                                    icon="vxe-icon-add">提缺陷</vxe-button>

                                <vxe-button style="float:right;z-index: 1;margin-right:10px;"
                                    @click="onClickRelatedItems" icon="vxe-icon-paste">关联缺陷</vxe-button>
                            </template>
                            <a-tab-pane key="DETAIL" tab="用例执行" />
                            <a-tab-pane key="RESULT_RELATED_ITEMS" tab="执行结果关联工作项" />
                            <a-tab-pane key="RELATED_ITEMS" tab="用例关联工作项" />
                            <a-tab-pane key="RELATED_WIKI" tab="用例关联文档" />
                            <a-tab-pane key="ATTACHMENTS" tab="用例关联文件" />
                        </a-tabs>

                        <div v-show="currentTab === 'DETAIL'" style="margin: 0 20px 0;">
                            <!-- <a-form-model-item label="用例名称" prop="name" style="width:50%;margin-bottom: 0px;">
                                <span v-if="currentTestResult?.testCase?.name">{{ currentTestResult?.testCase?.name
                                    }}</span>
                                <span v-else>无</span>
                            </a-form-model-item>
                            <a-form-model-item label="描述" style="width:50%;margin-bottom: 0px;">
                                <div v-if="currentTestResult?.testCase?.description"
                                    v-html="currentTestResult?.testCase?.description"></div>
                                <span v-else>无</span>
                            </a-form-model-item> -->

                            <a-form-model-item label="操作步骤" prop="testStepResults" style="margin-bottom: 0px;" :span="24">
                                <vxe-table :loading="loadingStep" :data="currentTestResult.testStepResults" size="mini" align="center" row-id="id" :row-config="{ isHover: true }"
                                    :edit-config="{ trigger: 'click', mode: 'cell',activeMethod: activeCellMethod,beforeEditMethod: beforeEditMethod}">
                                    <vxe-column type="seq" header-align="center" width="60" />
                                    <vxe-column field="name" title="步骤" header-align="center" />
                                    <vxe-column field="description" title="步骤描述" header-align="center" />
                                    <vxe-column field="expectedResult" title="预期结果" header-align="center" />
                                    <vxe-column field="actualResult" title="实际结果" header-align="center"
                                        :edit-render="{}">
                                        <template #default="{ row }">
                                            {{ row.actualResult }}
                                        </template>
                                        <template #edit="{ row }">
                                            <a-textarea auto-size size="small" v-model="row.actualResult"
                                                placeholder="填写实际结果" @change="isChange = true" />
                                        </template>
                                    </vxe-column>
                                    <vxe-column field="stepResult" title="步骤执行结果" header-align="center" align="center"
                                        :edit-render="{}">
                                        <template #default="{ row }">
                                            <EnumItemValue :value="row.stepResult" />
                                        </template>
                                        <template #edit="{ row }">
                                            <EnumItemSelect v-model="row.stepResult" :projectId="projectId"
                                                :categoryCode="'TEST_CASE_RESULT'" style="width: 120px;"
                                                @change="isChange = true" />
                                        </template>
                                    </vxe-column>
                                </vxe-table>
                            </a-form-model-item>

                        </div>

                        <div v-show="currentTab === 'RESULT_RELATED_ITEMS'" style="margin: 0 20px 0;">


                            <a-form-model-item label="执行结果关联的下游工作项" prop="relatedWorkItems" :span="24">
                                <vxe-table ref="optionsTable" :loading="loadingLink" :show-header="true" size="mini"
                                    :data="relateItemData" show-overflow :row-config="{ isHover: true }" stripe>
                                    <vxe-column type="seq" width="60" />
                                    <vxe-column title="编号" width="100" align="center">
                                        <template #default="{ row }">
                                            <div style="display: inline-flex;flex-direction: row;">
                                                <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                                                    <template slot="title">
                                                        <span>工作项类型：{{ row.tracker?.name }}</span>
                                                    </template>
                                                    <div v-if="row?.icon" style="margin-right:5px;"><a-icon
                                                            :component="row?.icon" /></div>
                                                </a-tooltip>
                                                <span>{{ currentProjectKeyName + '-' + row.itemNo }}</span>
                                            </div>
                                        </template>
                                    </vxe-column>
                                    <vxe-column title="名称" show-overflow>
                                        <template #default="{ row }">
                                            {{ row.name }}
                                        </template>
                                    </vxe-column>

                                    <vxe-column title="状态" width="120" align="center">
                                        <template #default="{ row }">
                                            <template>
                                                <div v-if="row.status" class="transition-status">
                                                    <span class="ui-tag-status"
                                                        :style="{ color: row.status?.meaning?.color, 'border-color': row.status?.meaning?.color }">{{
        row.status?.name }}</span>
                                                </div>
                                            </template>
                                        </template>
                                    </vxe-column>
                                    <vxe-column field="owner" title="负责人" width="100" align="center">
                                        <template #default="{ row }">
                                            <h-avatar :name="row.owner?.name" :icon="row.owner?.icon"></h-avatar>
                                        </template>
                                    </vxe-column>
                                    <vxe-column field="" title="操作" align="center" width="100">
                                        <template #default="{ row }">
                                            <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                                                <template slot="title">
                                                    <span>新窗口打开</span>
                                                </template>
                                                <vxe-button type="text" icon="vxe-icon-edit"
                                                    @click="onEditItem(row)"></vxe-button>
                                            </a-tooltip>
                                            <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                                                <template slot="title">
                                                    <span>删除</span>
                                                </template>
                                                <vxe-button type="text" icon="vxe-icon-delete"
                                                    @click="onDeleteItem(row)"></vxe-button>
                                            </a-tooltip>
                                        </template>
                                    </vxe-column>

                                </vxe-table>
                            </a-form-model-item>
                        </div>

                        <div v-show="currentTab === 'RELATED_ITEMS'">
                            <tracker-related-item :projectId="projectId"
                                :trackerId="currentTestResult.testCase?.tracker?.id"
                                :itemId="currentTestResult.testCase?.id"
                                :relatedWorkItems="currentTestResult.testCase?.relatedWorkItems" />
                        </div>
                        <div v-show="currentTab === 'RELATED_WIKI'">
                            <tracker-related-wiki :projectId="projectId" :tracker-item="currentTestResult.testCase" />
                        </div>
                        <div v-show="currentTab === 'ATTACHMENTS'">
                            <tracker-attachment :tracker-item="currentTestResult.testCase" />
                        </div>
                    </div>



                </a-layout-content>
                <a-layout-footer style="background-color: #fff;padding:10px;max-height: 500px;overflow: auto;">
                    <!-- 执行结果    -->
                    <div style="border-top: 1px solid #f0f0f0;">
                        <a-form-model-item label="执行结果" :colon="false" style="margin-bottom: 0px;">
                            <div style="display:flex;margin-bottom:10px;">
                                <div :class="['execute-result-all', testCaseResult == item.id ? 'result-selected' : '']"
                                    :style="{ borderColor: item.color, backgroundColor: testCaseResult == item.id ? item.color : '#fff', color: testCaseResult == item.id ? '#fff' : item.color }"
                                    @click="onClickTestResult(item.id)" v-for="item in resultList" :key="item.id">
                                    {{ item.name }}<i v-if="testCaseResult == item.id" style="margin-left:20px;"
                                        class="vxe-icon-success-circle-fill"></i>
                                </div>
                            </div>
                        </a-form-model-item>
                        <a-form-model-item label="执行结果备注" :colon="false" style="margin-bottom: 0px;">
                            <div style="width:100%;">
                                <simple-editor v-if="currentTestResultId == currentTestResult.id" ref="simpleEditor"
                                    height="120" v-model="currentTestResult.resultDesc" @change="isChange = true"
                                    :showToolbar="editorInEditMode" @focus="editorInEditMode = true"
                                    @blur="editorInEditMode = false" />
                            </div>
                        </a-form-model-item>
                        <div style="float:right;margin-top:25px;">
                            <vxe-button @click="changeCurrentCase(-1)" :disabled="curretnTestCaseIndex == 0" size="mini"
                                icon="vxe-icon-arrow-up"></vxe-button>
                            <span style="margin:0px 15px;">{{ curretnTestCaseIndex + 1 }} /
                                {{ getTestCaseList.length }}</span>
                            <vxe-button @click="changeCurrentCase(1)"
                                :disabled="curretnTestCaseIndex == getTestCaseList.length - 1" size="mini"
                                icon="vxe-icon-arrow-down"></vxe-button>
                            <vxe-button :disabled="!isChange" @click="onClickTestCaseSave" content="保存"></vxe-button>
                            <vxe-button :disabled="!isChange" @click="onClickTestCaseSaveAndNext" status="primary"
                                content="保存并下一条"></vxe-button>
                        </div>

                    </div>
                </a-layout-footer>
            </a-layout>
        </a-layout>
        <edit-tracker-item-dialog :is-show-dialog="isShowEditTrackerItemDialog" :projectId="projectId"
            :tracker="tracker" :itemId="currentTestResult?.testCase?.id" @cancel="onEditTrackerItemCancel" />
        <tracker-item-select-modal :projectId="projectId" :initalData="[]" ref="trackerItmeSlectModal"
            @trackerItemSelector="trackerItemSelector"></tracker-item-select-modal>
        <create-tracker-item-dialog :is-show-dialog="isShowCreateTrackerItemDialog" :projectId="projectId" :tracker="{}"
            @ok="onCreateTrackerItemOK" @cancel="isShowCreateTrackerItemDialog = false" />
    </config-page>
</template>

<script>
import Vue from "vue";
import VXETable from "vxe-table";
import draggable from 'vuedraggable'
import { iconUrl } from "@/utils/util"
import { mapGetters } from "vuex";
import HAvatar from '@/components/avatar/h-avatar.vue';
import ContentPage from '@/components/page/content/ContentPage.vue';
import TrackerItemFilter from '@/components/tool/TrackerItemFilter'
import TestRunDialog from './TestRunDialog.vue';
import TestRunOperateDialog from './TestRunOperateDialog.vue';
import TrackerItemSelectModal from '@/components/dialog/TrackerItemSelectModal'
import QuickPicker from '@/components/select/QuickPicker.vue';
import TrackerItemUserSelect from '@/components/select/TrackerItemUserSelect.vue';
import TrackerAttachment from '@/pages/tracker/items/TrackerAttachment.vue';
import TrackerRelatedWiki from '@/pages/tracker/items/TrackerRelatedWiki.vue';
import TrackerRelatedItem from '@/pages/tracker/items/TrackerRelatedItem.vue';
import TrackerTestCases from '@/pages/tracker/items/TrackerTestCases.vue';
import SimpleEditor from '@/components/editor/SimpleEditor.vue';
import EditTrackerItemDialog from '@/pages/tracker/items/EditTrackerItemDialog.vue'
import CreateTrackerItemDialog from '@/pages/tracker/items/CreateTrackerItemDialog.vue';
import EnumItemSelect from '@/components/select/EnumItemSelect.vue';
import EnumItemValue from '@/components/enum/EnumItemValue.vue'

import { createTrackerItem, findTrackerItemByIds } from "@/services/tracker/TrackerItemService";
import { findOneTracker,findTrackerFields } from "@/services/tracker/TrackerService";
import { findOneTrackerItem } from "@/services/tracker/TrackerItemService"
import {
    findOneTestRun, updateTestRun, linkTestResultWithDownstreamTrackerItems, unlinkTestResultWithDownstreamTrackerItems, findTestResults,
    saveResult, findDownstreamTrackerItemsByTestResultId
} from '@/services/test/TestRunService'
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import { findEnumsByCode, } from "@/services/system/EnumService";
import { GaodeMapV1 } from "@antv/l7-maps";



export default {
    name: 'TestRunExecutePage',
    components: {
        ContentPage, TrackerItemFilter, TestRunDialog, ConfigPage, TrackerItemSelectModal,
        TestRunOperateDialog, QuickPicker, TrackerItemUserSelect, TrackerAttachment, TrackerRelatedWiki,
        TrackerRelatedItem, TrackerTestCases, draggable, SimpleEditor, HAvatar, EditTrackerItemDialog,
        CreateTrackerItemDialog, EnumItemSelect, EnumItemValue,
        EnumItemSelect
    },
    data() {
        return {
            loading: false,
            loadingLink: false,
            loadingStep: false,
            keyword: '',
            testCaseList: [],
            trackerFilter: {},
            conditionGroups: [],
            resultList: [],
            priorityList: [],
            waitInputSearch: false,
            currentTestRun: {},
            currentTestResult: {},
            curretnTestCaseIndex: undefined,
            currentTestResultId: undefined,
            filterResult: undefined,
            filterPriority: undefined,
            currentTab: 'DETAIL',
            testCaseResult: '',
            isChange: false,
            isShowEditTrackerItemDialog: false,
            isShowCreateTrackerItemDialog: false,
            tracker: {},
            relateItemData: [],
            editorInEditMode: false,

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
                }, {
                    name: "testRunExecute",
                    meta: {
                        icon: "blank",
                        title: '执行测试',
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
        testRunId() {
            return this.$route.params.testRunId
        },
        getTestCaseList() {
            let testCaseList = this.testCaseList;
            if (this.keyword) {
                testCaseList = testCaseList.filter(v => (new RegExp(this.keyword)).test(v.name))
            }
            if (this.filterPriority?.length > 0) {
                testCaseList = testCaseList.filter(v => this.filterPriority.indexOf(v.priority?.id) > -1)
            }
            if (this.filterResult?.length > 0) {
                testCaseList = testCaseList.filter(v => this.filterResult.indexOf(v.result?.id) > -1)
            }
            return testCaseList;
        },
    },
    mounted() {
        this.loadData();
        this.loadDataTracker();

    },
    methods: {
        async onClickTestCaseSave() {
            // if (this.testCaseResult) {
            console.log("testStepResult", this.currentTestResult.testStepResults)
                await saveResult(this.testRunId, this.currentTestResult.id, this.testCaseResult,
                    this.currentTestResult.resultDesc, this.currentTestResult.testStepResults).then(resp => {
                        VXETable.modal.message({ content: '保存成功', status: 'success' })
                        this.isChange = false
                        let trackerId=this.currentTestResult?.testCase?.tracker?.id
                        let testStepResults=this.currentTestResult.testStepResults
                        this.currentTestResult = resp
                        this.currentTestResult.testStepResults=testStepResults
                        if(!this.currentTestResult?.testCase?.tracker?.id){
                            this.currentTestResult.testCase.tracker = { id: trackerId }
                        }

                        let index = this.testCaseList.findIndex(item => item.id == resp.id);
                        if (index >= 0) {
                            this.testCaseList[index] = this.currentTestResult
                            this.$set(this.testCaseList, index, this.currentTestResult)
                            this.$refs.vxeTable.setCurrentRow(this.testCaseList[index])

                        }

                        // this.loadDataTestCase();
                    })
            // }
        },
        async onClickTestCaseSaveAndNext() {
            await this.onClickTestCaseSave()
            if (this.curretnTestCaseIndex < this.getTestCaseList.length - 1) {
                this.changeCurrentCase(1)
            } else {
                VXETable.modal.alert({ content: '当前已为最后一条测试用例！', status: 'info' })
            }
        },
        changeCurrentCase(num) {
            this.currentTab = "DETAIL"
            this.curretnTestCaseIndex += num;
            this.currentTestResult = this.getTestCaseList[this.curretnTestCaseIndex];
            this.loadingStep=true;
            findOneTrackerItem(this.currentTestResult.testCase.id).then(resp => {
                this.currentTestResult.testCase = resp;
            }).finally(() => {
                this.loadStepData();
            })
            this.$nextTick(() => {
                this.currentTestResultId = this.currentTestResult.id;
            })
            this.$refs.vxeTable.setCurrentRow(this.currentTestResult)
            this.testCaseResult = this.currentTestResult?.result?.id
        },
        onClickTestResult(result) {
            this.testCaseResult = result;
            this.isChange = true;
        },
        loadData() {
            this.loading = true;
            findOneTestRun(this.testRunId).then(resp => {
                this.currentTestRun = resp;
            })
            this.loadDataTestCase();
        },
        loadDataTestCase() {
            this.loading = true;
            let keyword = this.keyword
            let sort="";
            if(this.orderBy){
                sort = this.orderBy + "," + this.orderByType;
            }
            let filter = { "conditionGroups": this.conditionGroups }
            let pageable = {
                page: 0,
                size: 100000,
            }

            findTestResults(this.projectId, this.testRunId, filter, keyword, pageable, sort).then(resp => {
                this.testCaseList = resp.content
            }).finally(() => {
                this.loading = false;
                this.$nextTick(() => {
                    let caseId = this.$route.params.caseId;
                    let index = 0;
                    if (caseId) {
                        index = this.testCaseList.findIndex(item => item.id == caseId);
                    }
                    if (this.testCaseList.length > 0) {
                        this.$refs.vxeTable.setCurrentRow(this.testCaseList[index])
                        this.currentTestResult = this.testCaseList[index]
                        this.loadingStep = true
                        findOneTrackerItem(this.currentTestResult.testCase.id).then(resp => {
                            this.currentTestResult.testCase = resp;
                        }).finally(() => {
                            this.loadStepData();
                        })
                        this.$nextTick(() => {
                            this.currentTestResultId = this.currentTestResult.id;
                        })
                        this.curretnTestCaseIndex = index;
                        this.testCaseResult = this.currentTestResult?.result?.id
                        this.isChange = false
                    }
                })
            })
        },
        currentChangeEvent({ rowIndex, newValue }) {
            this.currentTab = "DETAIL"
            this.currentTestResult = newValue
            this.loadingStep=true;
            findOneTrackerItem(this.currentTestResult.testCase.id).then(resp => {
                this.currentTestResult.testCase = resp;
            }).finally(() => {
                this.loadStepData();
            })
            this.$nextTick(() => {
                this.currentTestResultId = this.currentTestResult.id;
            })
            this.curretnTestCaseIndex = rowIndex
            this.testCaseResult = this.currentTestResult?.result?.id
            this.isChange = false
        },
        loadStepData(){
            findTrackerFields(this.currentTestResult?.testCase?.tracker?.id).then(res => {
                    let trackerField = res.find(v => v.inputType==="TEST_STEP")
                    if(trackerField&&this.currentTestResult.testCase.values[trackerField.id]){
                        let value=JSON.parse(this.currentTestResult.testCase.values[trackerField.id])||[];
                        let columns = trackerField.columns;
                        let stepField=columns.find(v => v.systemProperty == "step")
                        let stepDescField=columns.find(v => v.systemProperty == "stepDescription")
                        let expectResultField = columns.find(v => v.systemProperty == "expectResult")
                        
                        value.forEach((v,i) => {
                            v.id=i+1
                            this.currentTestResult.testStepResults?.forEach((v1,i1) => {
                                if(v1.id==i+1&&!v1.flag){
                                    if(v1.name&&v1.name!=v[stepField.id]){
                                        v1.actualResult=null;
                                        v1.stepResult=null
                                    }
                                    this.$set(v1,"name",v[stepField.id])
                                    this.$set(v1,"description",v[stepDescField.id])
                                    this.$set(v1,"expectedResult",v[expectResultField.id])
                                    v.flag=true
                                    v1.flag=true
                                }
                            })
                        })
                        this.currentTestResult.testStepResults=this.currentTestResult.testStepResults?.filter(v => v.flag);
                        let index=this.currentTestResult.testStepResults.length+1;
                        value.filter(v => !v.flag).forEach(v =>
                            this.currentTestResult.testStepResults.push({
                                "id":index++,
                                "name": v[stepField.id],
                                "description": v[stepDescField.id],
                                "expectedResult": v[expectResultField.id],
                            })
                        )
                        this.currentTestResult.testStepResults.forEach(v=>this.$delete(v,"flag"))
                    }
                    this.loadingStep = false
                    
                })
        },
        loadDataTracker() {
            findEnumsByCode('TRACKER_PRIORITY').then((resp) => {
                this.priorityList = resp;
            });
            findEnumsByCode('TEST_CASE_RESULT').then((resp) => {
                this.resultList = resp;
            });
        },
        loadRelatedItemData() {
            this.loadingLink = true;
            findDownstreamTrackerItemsByTestResultId(this.currentTestResultId).then(res => {
                this.relateItemData = res;
                this.loadingLink = false;
            })
        },
        onChangeTab() {
            if (this.currentTab == 'RESULT_RELATED_ITEMS') {
                this.loadRelatedItemData();
            }
        },
        onEditTrackerItemCancel() {
            this.isShowEditTrackerItemDialog = false
            // this.loadDataTestCase();
            this.changeCurrentCase(0);
        },
        onClickEditCase() {
            findOneTracker(this.currentTestResult?.testCase?.tracker?.id).then(resp => {
                this.tracker = resp;
            }).finally(() => {
                this.isShowEditTrackerItemDialog = true;
            })
        },
        onSelectTrackerItem() {

        },
        onEditItem(row) {
            const routeData = this.$router.resolve({
                path: `/tracker/project/${this.projectId}/trackerItems/${row.tracker.id}/${row.id}`
            });
            window.open(routeData.href, '_blank');
        },
        onDeleteItem(row) {

            unlinkTestResultWithDownstreamTrackerItems(this.currentTestResultId, row.id).then(resp => {
                VXETable.modal.message({ content: '删除成功', status: 'success' })
                this.relateItemData = this.relateItemData.filter(v => v.id != row.id)
                // this.loadDataTestCase();
            })

        },
        trackerItemSelector(items) {
            let itemIds = []
            items.forEach(item => {
                itemIds.push(item.id)
            })
            linkTestResultWithDownstreamTrackerItems(this.currentTestResult.id, itemIds).then(resp => {
                // this.loadDataTestCase()
                this.relateItemData = items;
                VXETable.modal.message({ content: '关联成功', status: 'success' })
            })
        },
        onCreateTrackerItemOK(trackerItem) {
            this.isShowCreateTrackerItemDialog = false;
            createTrackerItem(trackerItem).then(resp => {
                linkTestResultWithDownstreamTrackerItems(this.currentTestResult.id, [resp.id]).then(resp => {
                    VXETable.modal.message({ content: '添加成功', status: 'success' })
                    this.loadRelatedItemData()
                })
            })
        },
        onClickCreateItems() {
            this.isShowCreateTrackerItemDialog = true;
        },
        onClickRelatedItems() {
            this.$refs.trackerItmeSlectModal.view();
        },
        iconUrl(icon) {
            return iconUrl(icon);
        },
        activeCellMethod(row) {
            return true
        },
        beforeEditMethod({ row, rowIndex, column, columnIndex }) {
            return true;
        },

    },
}
</script>

<style lang="less" scoped>
.vxe-input-no-border {
    width: 100%;

    /deep/ input {
        border: none;
    }
}

.ui-task-primary-fields {
    display: flex;
    justify-content: flex-start;
    flex-wrap: wrap;
    padding-left: 10px;
    padding-right: 10px;
    border-bottom: 1px solid #f0f0f0;
}

.ui-table {
    height: calc(100% - 200px);
    overflow-y: auto;
    border: solid 1px #dedede;
    border-radius: 3px;
    background-color: #fff;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.layout-fields-table-row {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    min-height: 48px;
    padding-right: 20px;
    padding-left: 20px;
    padding-top: 10px;
    padding-bottom: 10px;
}

.table-head {
    width: 100%;
    padding: 0 20px;
    font-size: 14px;
    color: #303030;
    font-weight: 500;
    line-height: 48px;
    background-color: #f8f8f8;
    border-bottom: solid 1px #dedede;
    box-sizing: border-box;
}

.table-row {
    width: 100%;
    padding: 0 20px;
    // height: 48px;
    // line-height: 48px;
    border-bottom: solid 1px #dedede;
    font-size: 14px;
    color: #303030;
    box-sizing: border-box;
}

.execute-result-all {
    width: 90px;
    height: 30px;
    line-height: 20px;
    margin-right: 10px;
    padding: 5px 10px;
    border: 1px solid;
    border-radius: 100px;
    cursor: pointer;
}

.execute-result-pass {
    color: #00a865;
    border-color: #00a865;
}

.execute-result-pass:hover,
.execute-result-pass.result-selected {
    color: #fff;
    background: #00a865;
}

.execute-result-fail {
    color: #eb3723;
    border-color: #eb3723;
}

.execute-result-fail:hover,
.execute-result-fail.result-selected {
    color: #fff;
    background: #eb3723;
}

.execute-result-block {
    color: #f89300;
    border-color: #f89300;
}

.execute-result-block:hover,
.execute-result-block.result-selected {
    color: #fff;
    background: #f89300;
}

.execute-result-skip {
    color: #87888a;
    border-color: #87888a;
}

.execute-result-skip:hover,
.execute-result-skip.result-selected {
    color: #fff;
    background: #87888a;
}

.transition-status {
    min-width: 110px;
    display: flex;
    align-items: center;
    margin-right: 20px;
    white-space: nowrap;

    .ui-tag-status {
        max-width: 110px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 12px;
        box-sizing: border-box;
        display: inline-block;
        height: 20px;
        line-height: 18px;
        transition: border-color .2s;
        border: 1px solid;
        border-radius: 20px;
        padding: 0px 8px;
    }
}
</style>