<template>
    <config-page style="background-color: transparent;">
        <div class="item-detail item-detail-panel">
            <div class="item-detail-panel-info">
                <div class="item-detail-panel-title">
                    <div class="item-detail-top-bar">
                        <div class="item-detail-top-bar-left">
                            <a-tag color="blue" style="max-width: 122px;margin-left: 10px;">里程碑</a-tag>
                            <a-tag>#{{ milestone?.itemNo }}</a-tag>
                        </div>
                    </div>
                    <div class="item-detail-top-bar-right">
                        <a-space><a-button style="margin-right:10px;" icon="arrow-left"
                                @click="goBack">返回</a-button></a-space>
                    </div>
                </div>
                <div class="item-title">
                    <a-input class="task-basic-title" v-model="formData.name" @blur="onChangeTitle"
                        @pressEnter="onChangeTitle" />
                </div>
                <div class="item-key-field-list">
                    <a-popover trigger="click" v-model="showOwner" placement="bottomLeft"
                        :style="{pointerEvents:hasEditPerm?'':'none'}" overlayClassName="tracker-select-dropdown">
                        <template slot="content">
                            <div @click.stop>
                                <a-input-search placeholder="搜索负责人" style="width: 100%;" />
                                <div class="tracker-select-option"
                                    :style="{ color: p.id === milestone?.owner?.id ? '#338fe5' : '' }"
                                    v-for="p in projectUsers" :key="p.id">
                                    <div class="option-item" @click.stop="onChangeOwner(p)">
                                        <div class="option-item-content">
                                            <h-avatar :name="p.name" :icon="p.icon"></h-avatar>
                                            <span class="option-item-subtext">({{ p.email }})</span>
                                        </div>
                                        <div class="option-item-icon "><a-icon type="check"
                                                v-if="p.id === milestone?.owner?.id" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </template>
                        <quick-picker :title="formData.owner?.name" :sub-title="'负责人'"
                            :icon-url="iconUrl(milestone.owner?.icon)" :icon-text="milestone.owner?.name">
                            <template slot="icon">
                                <h-avatar :name="milestone.owner?.name" :icon="milestone.owner?.icon" :size="32" :isShowName="false"></h-avatar>
                            </template>
                        </quick-picker>
                    </a-popover>
                    <a-popover trigger="click" v-model="showEndDate" placement="bottomLeft"
                        :style="{pointerEvents:hasEditPerm?'':'none'}" overlayClassName="tracker-select-dropdown">
                        <template slot="content">
                            <div @click.stop>
                                <div :style="{ width: '300px', border: '1px solid #d9d9d9', borderRadius: '4px' }">
                                    <a-calendar :fullscreen="false" @select="onEndDateChange"
                                        v-model="formData.planEndDate" />
                                </div>
                            </div>
                        </template>
                        <quick-picker :title="milestone?.planEndDate" :sub-title="'完成日期'">
                            <template slot="icon">
                                <a-avatar :style="{ color: '#fff' }">
                                    <a-icon slot="icon" type="calendar" />
                                </a-avatar>
                            </template>
                        </quick-picker>
                    </a-popover>
                    <a-popover trigger="click" v-model="showStatus" placement="bottomLeft" title="更改状态"
                        :style="{pointerEvents:hasEditPerm?'':'none'}" overlayClassName="tracker-select-dropdown">
                        <template slot="content">
                            <div class="tracker-select-option" @click.stop>
                                <div class="option-item" @click="onChangeStatus(false)">
                                    <div class="option-item-content">
                                        <span class="option-item-text">未完成</span>
                                    </div>
                                    <div class="option-item-icon ">
                                        <h-icon type="check" v-if="!milestone.finished" />
                                    </div>
                                </div>
                                <div class="option-item" @click="onChangeStatus(true)">
                                    <div class="option-item-content">
                                        <span class="option-item-text">已完成</span>
                                    </div>
                                    <div class="option-item-icon ">
                                        <h-icon type="check" v-if="milestone.finished" />
                                    </div>
                                </div>
                            </div>
                        </template>
                        <quick-picker :title="milestone?.finished ? '已完成' : '未完成'" :sub-title="'当前状态'">
                            <template slot="icon">
                                <a-avatar icon="arrow-right" v-if="milestone?.status"
                                    :style="{ color: '#fff', backgroundColor: milestone?.status?.color }" />
                                <a-avatar icon="arrow-right" v-else
                                    :style="{ color: '#fff', backgroundColor: '#338fe5' }" />
                            </template>
                        </quick-picker>
                    </a-popover>
                </div>
            </div>
            <div class="item-detail-panel-content-container">
                <div class="item-detail-panel-attr">
                    <a-tabs v-model="currentTab" default-active-key="DELIVERABLES" style="height: 100%;" :animated="false">
                        <a-tab-pane key="DELIVERABLES" tab="交付物">
                            <div style="display: flex; flex-direction: column; height: calc(100vh - 340px);">
                                <vxe-toolbar style="flex: 0 0;">
                                    <template #tools>
                                        <a-space><a-button v-if="hasEditPerm"
                                                @click="showDeliverableSettingDialog = true">设置交付物</a-button></a-space>
                                    </template>
                                </vxe-toolbar>
                                <vxe-table size="small" ref="xTable" show-overflow border row-key :show-header="true"
                                    :auto-resize="true" :data="tableData" :row-config="{ keyField: 'id', isCurrent: true }"
                                    style="flex: 1 1 auto;">
                                    <vxe-column type="seq" width="50px" />
                                    <vxe-column field="name" title="名称" />
                                    <vxe-column field="type" title="类型" width="80px">
                                        <template #default="{ row }">
                                            {{ $t('deliverable?.type?.' + row?.type) }}
                                        </template>
                                    </vxe-column>
                                    <vxe-column field="url" title="交付情况" width="200px">
                                        <template #default="{ row }">
                                            <template v-if="row?.committed">
                                                <span v-if="row?.type === 'URL'"><a target="_blank"
                                                        @click="openPage(row.url)">
                                                        {{ row.url }}</a> </span>
                                                <span v-if="row?.type === 'FILE'">
                                                    <img :src="getFileIcon(row.attachment?.filePath)" width="16"
                                                        height="16" />
                                                    <a @click="onDownload(row.attachment)"> {{ row.attachment.name }}</a>
                                                </span>
                                            </template>
                                            <template v-else>
                                                -
                                            </template>
                                        </template>
                                    </vxe-column>
                                    <vxe-column field="planEndDate" title="文件大小" width="100px">
                                        <template #default="{ row }">
                                            <template v-if="row?.committed">
                                                <span v-if="row?.type === 'FILE'"> {{ getFileSize(row.attachment?.fileSize)
                                                }}
                                                </span>
                                            </template>
                                            <template v-else>
                                                -
                                            </template>
                                        </template>
                                    </vxe-column>
                                    <vxe-column field="createBy.name" title="交付者" width="100px" :edit-render="{}">
                                        <template #default="{ row }">
                                            <template v-if="row?.committed">
                                                <h-avatar :name="row.lastModifiedBy?.name" :icon="row.lastModifiedBy?.icon"></h-avatar>
                                            </template>
                                            <template v-else>
                                                -
                                            </template>
                                        </template>
                                    </vxe-column>
                                    <vxe-column title="操作" header-align="center" width="100">
                                        <template #default="{ row }">
                                            <vxe-button type="text" v-if="row?.type === 'URL'&&hasEditPerm" size="medium"
                                                @click="onSubmitDeliverable(row)">编辑链接</vxe-button>

                                            <a-upload name="file" v-if="row?.type === 'FILE'" :multiple="false"
                                                :showUploadList="false" :directory="false"
                                                :customRequest="(e) => onUpload(row, e)">
                                                <vxe-button type="text">提交交付物</vxe-button>
                                            </a-upload>
                                        </template>
                                    </vxe-column>
                                </vxe-table>
                            </div>

                        </a-tab-pane>
                        <!-- <a-tab-pane key="DETAIL" tab="详情">
                            <a-form-model ref="planForm" :model="formData" :rules="rules" :layout="'horizontal'"
                                :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">
                                <a-form-model-item ref="name" label="名称" prop="name">
                                    <a-input v-model="formData.name" style="width:300px;" @blur="() => {
                                        $refs.name.onFieldBlur();
                                    }" />
                                </a-form-model-item>
                                <a-form-model-item label="开始日期" prop="planStartDate" v-if="formData.type === 'TASK'">
                                    <a-date-picker v-model="formData.planStartDate" style="width:300px;"
                                        @change="onStartDateChange" />
                                </a-form-model-item>
                                <a-form-model-item label="工时(天)" prop="duration" v-if="formData.type === 'TASK'">
                                    <a-input-number v-model="formData.duration" style="width:300px;"
                                        @change="onDurationChange" />
                                </a-form-model-item>
                                <a-form-model-item label="结束日期" prop="planEndDate"
                                    v-if="formData.type === 'TASK' || formData.type === 'MILE_STONE'">
                                    <a-date-picker v-model="formData.planEndDate" style="width:300px;"
                                        @change="onEndDateChange" />
                                </a-form-model-item>
                                <a-form-model-item label="负责人" prop="ownerId">
                                    <project-user-select v-model="formData.ownerId" :projectId="projectId"
                                        style="width:300px;" />
                                </a-form-model-item>
                            </a-form-model>
                        </a-tab-pane>
                        <a-tab-pane key="PRE" tab="前置任务">

                        </a-tab-pane>
                        <a-tab-pane key="POST" tab="后置任务">

                        </a-tab-pane> -->
                    </a-tabs>
                </div>
                <!-- <div class="item-detail-panel-log">
                </div> -->

            </div>


        </div>
        <deliverable-dialog :isShowDialog="showDeliverableDialog" :projectId="projectId"
            :currentDeliverable="currentDeliverable" @cancel="showDeliverableDialog = false" @ok="onDeliverableDialogOK" />

        <deliverable-setting-dialog :isShowDialog="showDeliverableSettingDialog" :projectId="projectId"
            :milestoneId="milestoneId" :deliverableList="milestone.deliverables"
            @cancel="showDeliverableSettingDialog = false" @ok="onDeliverableSettingDialogOK" />
    </config-page>
</template>
<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import DeliverableDialog from './DeliverableDialog.vue';
import {cloneDeep} from 'lodash'
import { iconUrl } from "@/utils/util"
import QuickPicker from '@/components/select/QuickPicker.vue';
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import moment from 'moment';
import { findOnePlan, updatePlan, updateDeliverables, updateDeliverable, uploadAttachment } from '@/services/plan/PlanService'
import { uploadFile, downloadFile } from '@/services/global/FileService'
import { fileImgMap, unknownImg, calculateFileSize } from "@/utils/fileUtil"
import {
    findProjectUsers
} from "@/services/tracker/ProjectRoleMemberService";
import DeliverableSettingDialog from './DeliverableSettingDialog.vue';
import { hasPermission } from '@/utils/permission'
export default {
    name: "MilestoneDetail",
    components: { ConfigPage, ProjectUserSelect, QuickPicker, DeliverableDialog, DeliverableSettingDialog, HAvatar },
    data() {
        return {
            milestone: {},
            projectUsers: [],
            currentTab: 'DELIVERABLES',
            tableData: [],
            formData: {
                name: '',
                planStartDate: '',
                planEndDate: '',
                ownerId: '',
            },
            rules: {
                name: [
                    { required: true, message: "请输入名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                ],
                type: [{ required: true, message: "请选择类型", trigger: "change" },]
            },
            showDeliverableDialog: false,
            currentDeliverable: undefined,
            showDeliverableSettingDialog: false,
            showOwner: false,
            showEndDate: false,
            showStatus: false,
        };
    },

    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },
        milestoneId() {
            return this.$route.params.itemId
        },
        hasEditPerm(){
            return hasPermission("PAGE_WRITE",this.pageId);
        },  
    },

    mounted() {
        findProjectUsers(this.projectId).then(resp => {
            this.projectUsers = resp;
        })
        this.loadData();
    },
    methods: {
        goBack() {
            this.$router.go(-1)
        },
        iconUrl(icon) {
            return iconUrl(icon);
        },
        loadData() {

            findOnePlan(this.milestoneId).then(resp => {
                this.milestone = resp
                this.formData = this.milestone
                this.tableData = this.milestone.deliverables
            })
        },
        onChangeTitle(e) {
            this.milestone.name = e.target.value
            updatePlan(this.milestone).then(resp => {
                this.loadData()
            })
        },
        onChangeOwner(user) {
            this.showOwner = false
            this.milestone.owner = user
            updatePlan(this.milestone).then(resp => {
                this.loadData()
            })
        },
        onEndDateChange() {
            this.showEndDate = false
            this.milestone.planEndDate = this.formData.planEndDate.format('YYYY-MM-DD')
            updatePlan(this.milestone).then(resp => {
                this.loadData()
            })
        },
        onChangeStatus(status) {
            this.showStatus = false
            this.milestone.finished = status
            updatePlan(this.milestone).then(resp => {
                this.loadData()
            })
        },
        onSubmitDeliverable(row) {
            this.currentDeliverable = row
            this.showDeliverableDialog = true
        },

        onDeliverableDialogOK(item) {
            if(item){
                this.showDeliverableDialog = false
                item.committed = true
                updateDeliverable(this.currentDeliverable.id, item).then(resp => {
                    this.loadData()
                })
            }
        },
        onDeliverableSettingDialogOK(deliverableList) {
            updateDeliverables(this.milestoneId, deliverableList).then(resp => {
                this.showDeliverableSettingDialog = false
                this.loadData()
            })

        },
        onUpload(row, e) {
            uploadFile(e.file).then((resp) => {
                e.onSuccess(resp, e.file)

                let attachment = { name: resp.origin_name, filePath: resp.url, fileSize: resp.fileSize }
                console.log(attachment)
                uploadAttachment(row.id, attachment).then(resp2 => {
                    this.loadData()
                })
            })
        },
        onDownload(attachment) {
            downloadFile({
                name: attachment.name,
                url: attachment.filePath
            })
        },
        getFileIcon(filePath) {
            let index = filePath.lastIndexOf(".");
            //获取后缀
            let ext = filePath.substr(index + 1);

            let iconPath = fileImgMap.get(ext)
            if (!iconPath) {
                return unknownImg
            }
            return iconPath
        },
        getFileSize(fileSize) {
            return calculateFileSize(fileSize)
        },
        openPage(url) {
            window.open(url, '_blank')
        }

    },
    created() { }
};
</script>
<style lang="less">
.item-detail.item-detail-panel {
    position: relative;
    width: 100%;
    height: 100%;
    overflow: initial;
    z-index: 1;
    display: flex;
    flex-direction: column;

    .item-detail-panel-info {
        flex: 0 0 auto;
        background: #fff;
        // box-shadow: 0px 12px 24px -6px #0064ff33, 0px 0px 1px 0px #0064ff40;
        border-radius: 3px;

        .item-detail-panel-title {
            height: 48px;
            display: flex;
            align-items: center;
            padding: 0 20px px;
            border-bottom: solid 1px #dfe1e5;

            .item-detail-top-bar {
                display: flex;
                justify-content: space-between;
                min-height: 21px;
                align-items: center;
                width: 100%;
                padding-right: 0;

                .item-detail-top-bar-left,
                .item-detail-top-bar-right {
                    display: flex;
                    align-items: center;
                }
            }
        }

        .item-title {
            margin-top: 7px;
            margin: 15px 20px 0;

            .task-basic-title {
                position: relative;
                border: none;
                resize: none;
                outline: none;
                box-shadow: none;
                font-size: 18px;
                color: #303030;
                font-weight: 500;
                line-height: 24px;
            }

            .ant-input {
                border: none;
            }

            .ant-input:focus {
                border-color: #5caff2;
                box-shadow: none;
                background-color: #f8f8f8;
            }
        }

        .item-key-field-list {
            margin: 10px 20px;
            display: flex;

            .ant-menu-inline {
                border-right: none;
            }
        }
    }

    .item-detail-panel-content-container {
        margin-top: 10px;
        display: flex;
        flex: 1 1 auto;
        min-height: 0;

        .item-detail-panel-attr {
            flex: 1 1 0;
            width: calc(68% - 10px);
            min-width: 400px;
            min-height: 100%;
            background: #fff;
            // box-shadow: var(--shadow-1);
            border-radius: 2px;
            display: flex;
            flex-direction: column;
        }

        .item-detail-panel-log {
            width: 32%;
            min-width: 340px;
            max-width: 460px;
            margin-left: 10px;
            background: #fff;
            // box-shadow: var(--shadow-1);
            border-radius: 2px;
        }
    }
}


.tracker-select-dropdown {
    .ant-popover-inner-content {
        padding: 0px !important;
    }

    .tracker-select-option {
        font-size: 12px;
        cursor: pointer;
        display: block;
        font-size: 14px;
        font-weight: 400;
        line-height: 22px;
        min-height: 34px;
        min-width: 100px;
        padding: 6px 10px;
        position: relative;
        color: #000;

        .option-item {
            display: flex;

            &:hover {
                background-color: #f8f8f8;
                color: #338fe5;
            }

            .option-item-content {
                flex: auto;
                display: inline-flex;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }

            .option-item-icon {
                flex: none;
                display: inline-flex;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                padding-top: 5px;
                margin-left: 10px;
            }


            &>a {
                color: inherit;
            }

            &>a:hover {
                color: inherit;
            }

            .option-item-text {}

            .option-item-subtext {
                margin-left: 5px;
                font-size: 12px;
                color: #606060;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                max-width: 180px;
                flex: 0 0 auto;
                color: var(--gray-80);
            }
        }
    }

    .status-menu-items {
        .ant-menu-item {
            &:hover {
                background-color: #f8f8f8;
                color: #338fe5;
            }
        }
    }
}
</style>
