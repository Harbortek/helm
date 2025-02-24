<template>
    <config-page class="config-page" :autoFit="true" description="一次迭代可能发布某个或某几个功能特性，一次迭代过程即包含这些新功能所需的所有开发工作项及其他工作项。一般情况下，一个迭代周期为 2-4 周。">
        <div class="sprint-container">
            <a-layout style="width: 100%;height: 100%;">
                <a-layout-sider width="200" class="left-sprint-list" style="background: #fff;margin: 10px;"
                    :collapsible="false">
                    <div class="left-left-sprint-list-head">
                        <span>迭代</span>
                        <div>

                        </div>
                    </div>
                    <div class="left-sprint-list-main">
                        <div class="left-task-list-select">
                            <vxe-table ref="categoryTable" :data="trackerCategory" size="small" :show-header="false"
                                row-class-name="left-task-list-item" style="cursor: pointer;"
                                :row-config="{ isHover: true, isCurrent: true, keyField: 'id' }"
                                @current-change="onCategoryChange">
                                <vxe-column field="name" title="项目分类">
                                    <template #default="{ row }">
                                        <span class="">{{ row.name }}</span>
                                    </template>
                                </vxe-column>
                            </vxe-table>
                        </div>

                        <div class="left-left-sprint-list-toolbar">
                            <vxe-toolbar ref="xToolbar" size="medium">
                                <template #buttons>
                                    <vxe-button v-action="'PAGE_WRITE|'+pageId" type="text" icon="vxe-icon-add" 
                                        @click="onCreateSprint">新建迭代</vxe-button>
                                </template>
                                <template #tools>
                                    <a-popover v-model="sprintFilterVisible"  trigger="['click']]" placement="bottomLeft">
                                        <template slot="content">
                                            <div style="font-size:15px;">迭代名称</div>
                                            <a-input placeholder="" v-model="sprintFilter.sprintName" />
                                            <div style="font-size:15px;margin-top:10px;">迭代负责人</div>
                                            <project-user-select style="width: 100%;" v-model="sprintFilter.ownerId" :isShowAll="true" :projectId="projectId" />
                                            <div style="font-size:15px;margin:10px auto;">迭代状态</div>
                                            <a-checkbox-group v-model="sprintFilter.status">
                                                <a-checkbox value="未开始">
                                                未开始
                                                </a-checkbox>
                                                <a-checkbox value="进行中">
                                                进行中
                                                </a-checkbox>
                                                <a-checkbox value="已完成">
                                                已完成
                                                </a-checkbox>
                                            </a-checkbox-group>
                                            <div style="border-top: 1px solid #dadce0;text-align: right;margin-top:10px;padding-top:5px;">
                                                <vxe-button size="small" @click="sprintFilterVisible=false">取消</vxe-button>
                                                <vxe-button status="primary" size="small" @click="onClickFilterOk">确认</vxe-button>
                                            </div>
                                        </template>
                                        
                                        <template slot="title">
                                            <span style="font-size:18px;color:#2d2d2e">筛选迭代</span>
                                        </template>
                                        <a-badge :dot="isFilterSprint">
                                            <!-- <a-button type="link" shape="circle" icon="filter" /> -->
                                            <a-icon style="font-size: 16px;margin-right: 3px;cursor: pointer;color:#338fe5;" type="filter"></a-icon>
                                        </a-badge>
                                    </a-popover>
                                </template>
                            </vxe-toolbar>

                        </div>
                        <div class="left-task-list-select">
                            <vxe-table ref="sprintTable" :data="getSprintTableData" size="small" :show-header="false" style="cursor: pointer;"
                                row-class-name="" :row-config="{ isHover: true, isCurrent: true, keyField: 'id' }"
                                @current-change="onSprintChange">
                                <vxe-column field="name" title="">
                                    <template #default="{ row }">
                                        <div class="sprint-item">
                                            <div class="sprint-item-title">
                                                <span class="">{{ row.name }}</span>
                                            </div>
                                            <div style="display: flex;">
                                                <span style="flex: 1 1 auto;">{{ formatDate(row.planEndDate) }}</span>
                                                <span style="flex: 0 0;" class="sprint-status">
                                                    <span class="ui-tag-status"
                                                        :style="{ color: getStatusColor(row.status), 'border-color': getStatusColor(row.status) }">
                                                        {{ row.status?.name }}</span>
                                                </span>
                                            </div>
                                            <a-progress :percent="row.progress" size="small" />
                                        </div>

                                    </template>
                                </vxe-column>
                            </vxe-table>

                        </div>
                    </div>


                </a-layout-sider>
                <a-layout-content class="layout-content">
                    <div v-show="sprintType" class="toolbar-header" style="height:5%">
                        <div class="title">{{ sprintType == 'All' ? '所有工作项' : '未规划工作项' }}</div>
                    </div>
                    <div style="height:5%" v-if="sprintId && sprintId != '0'" class="toolbar-header">
                        <a-popover v-model="popoverVisible" trigger="click" placement="bottom">
                            <template slot="content">
                                <a-input-search v-model="keyword" placeholder="搜索迭代" style="width: 260px" />
                                <vxe-table ref="popoverTable" border="none" size="mini"
                                    :row-config="{ isCurrent: true, isHover: true }"
                                    :column-config="{ isCurrent: true, isHover: true }" :show-header="false"
                                    @current-change="currentChangeEvent" :data="getSprintData">
                                    <vxe-column field="name" width="260"></vxe-column>

                                </vxe-table>
                            </template>
                            <div style="display: flex;align-items: center;margin: auto 20px;cursor: pointer;">
                                {{ currentSprint?.name }}<a-icon style="margin-left:5px;" type="down" />
                            </div>
                        </a-popover>

                        <a-tabs style="width: 70%;" :tabBarStyle="{ marginBottom: '0' }" @change="onChangeTabs" v-model="trackerId">
                            <a-tab-pane key="" tab="全部"></a-tab-pane>
                            <a-tab-pane style="margin-bottom: 0px;" v-for="item in trackerList" :key="item.id"
                                :tab="item.name">
                            </a-tab-pane>
                            <a-tab-pane key="code" tab="代码"></a-tab-pane>
                        </a-tabs>

                        <div style="position: absolute;right: 20px;">
                            <vxe-button v-if="currentSprint?.meaning != 'ENDED'" status="text"
                                v-action="'PAGE_WRITE|'+pageId" @click="onClickConvertSprint">
                                {{ currentSprint?.meaning == 'NOT_STARTED' ? '开始' : '完成' }}迭代</vxe-button>

                            <a-dropdown :trigger="['click']">
                                <a-menu slot="overlay" @click="handleMenuClick">
                                    <a-menu-item key="edit">编辑迭代</a-menu-item>
                                    <a-menu-item key="delete">删除迭代</a-menu-item>
                                </a-menu>
                                <vxe-button v-action="'PAGE_WRITE|'+pageId" status="text">··· 更多</vxe-button>
                            </a-dropdown>
                        </div>

                    </div>
                    <tracker-items-table ref="itemsTable" style="height:95%;overflow:hidden;" v-show="!codeVisible" :projectId="projectId" :trackerId="trackerId" viewId=""
                        :sprintId="sprintId" :sprintType="sprintType"
                        @refreshSprint="loadData()"></tracker-items-table>

                    <div v-show="codeVisible" style="height:94%">
                        <div style="margin: 8px 15px;font-size: 14px;color: rgb(124,136,138);">提交记录（{{ commitData.length }}）
                        </div>
                        <vxe-table :row-config="{ isHover: true }" :data="commitData" :loading="loading">
                            <vxe-column field="commitId" show-overflow="tooltip" title="提交ID">
                                <template #default="{ row }">
                                    <a>{{ row.commitId }}</a>
                                </template>
                            </vxe-column>
                            <vxe-column field="commitMessage" show-overflow="tooltip" title="提交信息"></vxe-column>
                            <vxe-column field="committer" show-overflow="tooltip" title="提交人"></vxe-column>
                            <vxe-column field="createDate" show-overflow="tooltip" title="提交时间"></vxe-column>
                            <vxe-column field="projectNameOfRepository" title="仓库">
                                <template #default="{ row }">
                                    <h-icon :type="row.repositoryType" />
                                    {{ row.projectNameOfRepository }}
                                </template>
                            </vxe-column>
                            <vxe-column field="branch" title="分支">
                                <template #default="{ row }">
                                    <a-icon type="branches" />
                                    {{ row.branch }}
                                </template>
                            </vxe-column>
                        </vxe-table>
                    </div>

                </a-layout-content>
            </a-layout>
            <sprint-dialog :isShowDialog="showSprintDialog" :editMode="editMode" :title="sprintTitle" :projectId="projectId"
                :currentSprint="currentSprint" @cancel="showSprintDialog = false" @ok="onSprintDialogOK" />

        </div>
    </config-page>
</template>

<script>
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import SprintDialog from './SprintDialog.vue';
import { findSprints, createSprint, updateSprint, deleteSprint, convertSprint } from '@/services/plan/SprintService'
import { findCommitsBySprintId } from '@/services/tracker/TrackerCommitService'
import { findTrackersBySprint } from "@/services/tracker/TrackerItemService"
import { formatDate } from '@/utils/DateUtils'
import TrackerItemsTable from '@/components/table/TrackerItemsTable.vue';
import VXETable from "vxe-table";
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import {
    findTrackers, findOneTracker
} from "@/services/tracker/TrackerService";
export default {
    name: 'SprintList',
    components: { ConfigPage, SprintDialog, TrackerItemsTable, ProjectUserSelect },
    data() {
        return {
            loading: false,
            trackerCategory: [{ id: 1, name: '未规划工作项' }, { id: 2, name: '所有工作项' }],
            currentCategoryId: '',
            tableData: [],
            commitData: [],
            currentSprint: undefined,
            editMode: 'create',
            showSprintDialog: false,
            sprintType: 'Unplanned',
            sprintId: '',
            trackerId: '',
            trackerList: [],
            keyword: '',
            popoverVisible: false,
            sprintTitle: '',
            codeVisible: false,
            trackerItemIds: [],
            sprintFilter:{},
            sprintFilterNow:{},
            sprintFilterVisible:false,
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },
        getSprintData() {
            if (this.keyword) {
                var regexp = new RegExp(this.keyword)
                return this.tableData.filter(item => regexp.test(item.name))
            }
            return this.tableData;
        },
        getSprintTableData(){
            return this.tableData.filter(item=>{
                if(this.sprintFilterNow?.sprintName&&item?.name?.indexOf(this.sprintFilterNow?.sprintName)==-1){
                    return false
                }else if(this.sprintFilterNow?.ownerId&&item.owner.id!=this.sprintFilterNow?.ownerId){
                    console.log(item)
                    return false
                }else if(this.sprintFilterNow?.status&&this.sprintFilterNow.status.length>0
                            &&this.sprintFilterNow?.status.indexOf(item?.status?.name)==-1){
                    return false
                }
                return true;
            })
        },
        isFilterSprint(){
            console.log("???",this.sprintFilterNow)
            if(this.sprintFilterNow.status||this.sprintFilterNow.ownerId||this.sprintFilterNow.sprintName){
                console.log("！！！",this.sprintFilterNow)
                return true;
            }
            return false;
        }
    },
    mounted() {
        this.loadData()
        this.sprintType = 'All'
        this.$refs.categoryTable.setCurrentRow(this.trackerCategory[1]);

    },
    methods: {
        //代码
        onChangeTabs(key) {
            if (key == 'code') {
                this.codeVisible = true;
                this.loading = true;
                findCommitsBySprintId(this.sprintId).then(res => {
                    this.commitData = res;
                    this.loading = false;
                }).finally(() => {
                })
            } else {
                this.commitData = [];
                this.codeVisible = false;
            }
        },
        onClickFilterOk(){
            console.log("this.sprintFI",this.sprintFilter)

            this.sprintFilterNow=Object.assign({},this.sprintFilter)
            this.sprintFilterVisible = false;
        },
        onClickConvertSprint() {
            if (this.currentSprint.meaning == 'NOT_STARTED') {
                this.editMode = "start"
                this.sprintTitle = '开始迭代';
            } else {
                this.editMode = "complete"
                this.sprintTitle = '完成迭代';
            }
            this.showSprintDialog = true;
        },
        handleMenuClick({ key }) {
            if (key == "edit") {
                this.editMode = "edit"
                this.sprintTitle = '编辑迭代';
                this.showSprintDialog = true;
            } else if (key == 'delete') {
                VXETable.modal.confirm({
                    title: '删除迭代',
                    content: '当前正在删除迭代「' + this.currentSprint.name + '」,删除迭代不会删除迭代下的工作项，此操作不可撤销，是否确定?'
                }).then(type => {
                    if (type === 'confirm') {
                        deleteSprint(this.currentSprint.id).then(() => {
                            VXETable.modal.message({ content: '操作成功', status: 'success' })
                            if (this.sprintId == this.currentSprint.id) {
                                this.sprintId = ''
                                this.sprintType = "All"
                                this.$refs.categoryTable.setCurrentRow(this.trackerCategory[1]);
                                this.$refs.sprintTable.clearCurrentRow()
                            }
                            this.loadData();
                        })
                    }
                })
            }
        },
        currentChangeEvent({ newValue }) {
            this.popoverVisible = false;
            this.currentSprint = newValue
            this.sprintId = newValue.id;
            this.$refs.sprintTable.setCurrentRow(newValue)
        },
        formatDate(v) {
            if (!v) return '-'
            else return formatDate(v)
        },
        formatProgress(v) {
            if (!v) return '-'
            else return v + '%'
        },
        getStatusColor(status) {
            if (!status || !status.meaning) {
                return ''
            } else {
                return status.meaning.color
            }
        },
        loadData() {
            findSprints(this.projectId).then(resp => {
                this.tableData = resp
            })
            this.loadTrackersData();
        },
        loadTrackersData() {
            if(this.sprintId&&this.sprintId!== '0')
            findTrackersBySprint(this.sprintId).then(res=>{
                this.trackerList=res;
            })
        },
        onCategoryChange({ newValue }) {
            if(this.$refs.itemsTable.loading){
                if(newValue.id==1){
                    this.$refs.categoryTable?.setCurrentRow(this.trackerCategory[1])
                }else{
                    this.$refs.categoryTable?.setCurrentRow(this.trackerCategory[0])
                }
                return;
            }
            this.currentCategoryId = newValue.id
            this.trackerId = '';
            //code
            this.commitData = [];
            this.codeVisible = false;

            if (newValue.id == '1') {
                this.sprintType = 'Unplanned'
                this.sprintId = '0';//
            } else {
                this.sprintType = 'All'
                this.sprintId = '';
            }

            this.$refs.sprintTable.clearCurrentRow()
        },
        onSprintMenuChange(item) {
            this.currentSprint = item
            this.sprintId = item.id;
            this.$refs.sprintTable.setCurrentRow(item)
        },
        onSprintChange({ newValue }) {
            if(this.$refs.itemsTable.loading){
                this.$refs.sprintTable?.clearCurrentRow()
                return;
            }
            this.currentSprint = newValue
            this.sprintType = '';
            this.sprintId = newValue.id;
            //code
            this.commitData = [];
            this.codeVisible = false;
        
            this.loadTrackersData();
            this.trackerId = '';
            this.$refs.popoverTable?.setCurrentRow(newValue)
            this.$refs.categoryTable.clearCurrentRow()
        },
        onCreateSprint() {
            this.editMode = 'create';
            this.sprintTitle = '新建迭代';
            this.currentSprint = { name: '', projectId: this.projectId }
            this.showSprintDialog = true
        },
        onSprintDialogOK(item) {
            if (this.editMode === 'create') {
                createSprint(item).then(resp => {
                    this.loadData()
                    this.showSprintDialog = false
                    VXETable.modal.message({ content: '新建成功', status: 'success' })
                })
            } else if (this.editMode === 'edit') { 
                updateSprint(item).then(resp => {
                    this.loadData()
                    this.showSprintDialog = false
                    this.currentSprint = resp
                    VXETable.modal.message({ content: '更新成功', status: 'success' })
                })
            } else if (this.editMode === 'start' || this.editMode === 'complete') {
                convertSprint(item).then(resp => {
                    this.loadData()
                    this.showSprintDialog = false
                    this.currentSprint = resp
                    VXETable.modal.message({ content: '操作成功', status: 'success' })
                })
            }
        }
    },
}
</script>

<style lang="less" scoped>
.config-page{
    padding:10px;
    /deep/ .header-footer-panel{
        height: 100%;
    }
}

.toolbar-header {
    height: 48px;
    border-bottom: 1px solid #dfe1e5;
    display: flex;
    align-items: center;

    .title {
        font-size: 18px;
        font-weight: 500;
        margin-left: 10px;
    }
}

.sprint-container {
    background-color: transparent;
    width: 100%;
    height: 100%;

    .left-sprint-list {
        background: #fff;
        margin: 10px;

        .left-left-sprint-list-head {
            border-bottom: 1px solid #dfe1e5;
            height: 48px;
            font-size: 15px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 20px;
        }

        .left-left-sprint-list-toolbar {
            padding: 0 20px;

            ::v-deep  .ant-badge-dot{
                background: #338fe5;
            }
        }

        .left-sprint-list-main {
            .left-task-list-select {
                padding: 10px;

                .left-task-list-item {
                    height: 34px;
                    line-height: 34px;
                    cursor: pointer;
                }
            }

            .sprint-item {
                justify-content: space-between;

                .sprint-item-title {
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }

                .sprint-status {
                    display: flex;
                    align-items: center;
                    white-space: nowrap;

                    .ui-tag-status {
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        font-size: 12px;
                        box-sizing: border-box;
                        display: inline-block;
                        height: 18px;
                        line-height: 18px;
                        transition: border-color .2s;
                        border: 1px solid;
                        border-radius: 20px;
                        padding: 0px 8px;
                    }
                }
            }
        }
    }
}
.layout-content{
    background-color: #fff;
    padding:10px;
}
</style>