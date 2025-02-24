<template>
    <config-page style="background-color: transparent;">
        <div class="item-detail item-detail-panel">
            <div class="item-detail-panel-info">
                <div class="item-detail-panel-title">
                    <div class="item-detail-top-bar">
                        <div class="item-detail-top-bar-left">
                            <a-tag color="blue" style="max-width: 122px;margin-left: 10px;">计划</a-tag>
                            <a-tag>#{{ task?.itemNo }}</a-tag>
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
                        :style="{ pointerEvents: hasEditPerm ? '' : 'none' }" overlayClassName="tracker-select-dropdown">
                        <template slot="content">
                            <div @click.stop>
                                <a-input-search placeholder="搜索负责人" style="width: 100%;" />
                                <div class="tracker-select-option"
                                    :style="{ color: p.id === task?.owner?.id ? '#338fe5' : '' }"
                                    v-for="p in projectUsers" :key="p.id">
                                    <div class="option-item" @click.stop="onChangeOwner(p)">
                                        <div class="option-item-content">
                                            <h-avatar :name="p.name" :icon="p.icon"></h-avatar>
                                            <span class="option-item-subtext">({{ p.email }})</span>
                                        </div>
                                        <div class="option-item-icon "><a-icon type="check"
                                                v-if="p.id === task?.owner?.id" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </template>
                        <quick-picker :title="formData.owner?.name" :sub-title="'负责人'"
                            :icon-url="iconUrl(task.owner?.icon)" :icon-text="task.owner?.name">
                            <template slot="icon">
                                <h-avatar :name="task.owner?.name" :icon="task.owner?.icon" :size="32"
                                    :isShowName="false"></h-avatar>
                            </template>
                        </quick-picker>
                    </a-popover>
                    <a-popover trigger="click" v-model="showProgress" placement="bottomLeft" title="更改进度"
                        :style="{ pointerEvents: hasEditPerm ? '' : 'none' }" overlayClassName="tracker-select-dropdown">
                        <template slot="content">
                            <div @click.stop style="margin: 10px;height:44px;">
                                <a-input-number v-model="task.progress" :min="0" :max="100" />
                                <a-button @click="onChangeProgress">设置</a-button>
                            </div>
                        </template>
                        <quick-picker :title="formatProgress(task.progress)" :sub-title="'当前进度'">
                            <template slot="icon">
                                <a-avatar :style="{ color: '#fff', backgroundColor: '#F59300' }">
                                    <h-icon slot="icon" type="item-progress" style="width:30;height: 32px;"></h-icon>
                                </a-avatar>
                            </template>
                        </quick-picker>
                    </a-popover>
                    <a-popover trigger="click" v-model="showStartDate" placement="bottomLeft"
                        :style="{ pointerEvents: hasEditPerm ? '' : 'none' }" overlayClassName="tracker-select-dropdown">
                        <template slot="content">
                            <div @click.stop>
                                <div :style="{ width: '300px', border: '1px solid #d9d9d9', borderRadius: '4px' }">
                                    <a-calendar :fullscreen="false" @select="onStartDateChange"
                                        v-model="formData.planStartDate" />
                                </div>
                            </div>
                        </template>
                        <quick-picker :title="task?.planStartDate" :sub-title="'开始日期'">
                            <template slot="icon">
                                <a-avatar :style="{ color: '#fff' }">
                                    <a-icon slot="icon" type="calendar" />
                                </a-avatar>
                            </template>
                        </quick-picker>
                    </a-popover>
                    <a-popover trigger="click" v-model="showEndDate" placement="bottomLeft"
                        :style="{ pointerEvents: hasEditPerm ? '' : 'none' }" overlayClassName="tracker-select-dropdown">
                        <template slot="content">
                            <div @click.stop>
                                <div :style="{ width: '300px', border: '1px solid #d9d9d9', borderRadius: '4px' }">
                                    <a-calendar :fullscreen="false" @select="onEndDateChange"
                                        v-model="formData.planEndDate" />
                                </div>
                            </div>
                        </template>
                        <quick-picker :title="task?.planEndDate" :sub-title="'完成日期'">
                            <template slot="icon">
                                <a-avatar :style="{ color: '#fff' }">
                                    <a-icon slot="icon" type="calendar" />
                                </a-avatar>
                            </template>
                        </quick-picker>
                    </a-popover>
                </div>
            </div>
            <div class="item-detail-panel-content-container">
                <div class="item-detail-panel-attr">
                    <a-tabs default-active-key="SPRINTS" style="height: 100%;" :animated="false">
                        <a-tab-pane key="SPRINTS" tab="关联内容">
                            <div style="display: flex; flex-direction: column; height: calc(100vh - 340px);">
                                <div>
                                    <vxe-toolbar style="flex: 0 0;">
                                        <template #tools>
                                            <a-space><a-button v-if="hasEditPerm"
                                                    @click="showSprintDialog = true">关联已有迭代</a-button></a-space>
                                        </template>
                                    </vxe-toolbar>
                                    <vxe-table size="small" ref="sprintsTable" show-overflow border row-key
                                        :show-header="true" :auto-resize="true" :data="task.sprints"
                                        :checkbox-config="{ labelField: 'name' }"
                                        :row-config="{ keyField: 'id', isCurrent: true }" style="flex: 1 1 auto;">
                                        <vxe-column field="itemNo" title="ID" header-align="center" align="center"
                                            width="50px">
                                            <template #default="{ row }">
                                                #{{ row.itemNo }}
                                            </template>
                                        </vxe-column>
                                        <vxe-column field="name" title="名称" />

                                        <vxe-column field="owner.name" title="负责人" header-align="center" align="center"
                                            width="150px">
                                            <template #default="{ row }">
                                                <template v-if="row.owner.id">
                                                    <h-avatar :name="row.owner?.name"
                                                        :icon="row.owner?.icon"></h-avatar>
                                                </template>
                                                <template v-else>
                                                    -
                                                </template>
                                            </template>
                                        </vxe-column>
                                        <vxe-column field="planStartDate" title="开始日期" header-align="center"
                                            align="center" width="150">
                                            <template #default="{ row }">
                                                {{ formatDate(row.planStartDate) }}
                                            </template>
                                        </vxe-column>
                                        <vxe-column field="planEndDate" title="结束日期" header-align="center"
                                            align="center" width="150px">
                                            <template #default="{ row }">
                                                {{ formatDate(row.planEndDate) }}
                                            </template>
                                        </vxe-column>
                                        <vxe-column title="操作" header-align="center" width="100">
                                            <template #default="{ row }">
                                                <vxe-button type="text" size="medium" v-if="hasEditPerm"
                                                    @click="onDeleteSprint(row)">解除关联</vxe-button>
                                            </template>
                                        </vxe-column>
                                    </vxe-table>
                                </div>
                                <div>
                                    <vxe-toolbar style="flex: 0 0;">
                                        <template #tools>
                                            <a-space><a-button v-if="hasEditPerm"
                                                    @click="onAssociateTrackerItems">关联已有工作项</a-button></a-space>
                                        </template>
                                    </vxe-toolbar>
                                    <vxe-table size="small" ref="itemsTable" show-overflow border row-key
                                        :show-header="true" :auto-resize="true" :data="task.items"
                                        :checkbox-config="{ labelField: 'name' }"
                                        :row-config="{ keyField: 'id', isCurrent: true }" style="flex: 1 1 auto;">
                                        <vxe-column field="itemNo" title="ID" header-align="center" align="center"
                                            width="50px">
                                            <template #default="{ row }">
                                                #{{ row.itemNo }}
                                            </template>
                                        </vxe-column>
                                        <vxe-column field="name" title="名称" />

                                        <vxe-column field="owner.name" title="负责人" header-align="center" align="center"
                                            width="150px">
                                            <template #default="{ row }">
                                                <template v-if="row.owner.id">
                                                    <h-avatar :name="row.owner?.name"
                                                        :icon="row.owner?.icon"></h-avatar>
                                                </template>
                                                <template v-else>
                                                    -
                                                </template>
                                            </template>
                                        </vxe-column>
                                        <vxe-column field="planStartDate" title="开始日期" header-align="center"
                                            align="center" width="150">
                                            <template #default="{ row }">
                                                {{ formatDate(row.planStartDate) }}
                                            </template>
                                        </vxe-column>
                                        <vxe-column field="planEndDate" title="结束日期" header-align="center"
                                            align="center" width="150px">
                                            <template #default="{ row }">
                                                {{ formatDate(row.planEndDate) }}
                                            </template>
                                        </vxe-column>
                                        <vxe-column title="操作" header-align="center" width="100">
                                            <template #default="{ row }">
                                                <vxe-button type="text" size="medium" v-if="hasEditPerm"
                                                    @click="onDeleteTrackerItems(row)">解除关联</vxe-button>
                                            </template>
                                        </vxe-column>
                                    </vxe-table>
                                </div>
                            </div>
                        </a-tab-pane>
                        <a-tab-pane key="PRE" tab="前置任务">
                            <plan-dependency :currentPlan="task" :projectId="projectId" mode="preTasks"
                                @change="loadData" :hasEditPerm="hasEditPerm" />
                        </a-tab-pane>
                        <a-tab-pane key="POST" tab="后续任务">
                            <plan-dependency :currentPlan="task" :projectId="projectId" mode="postTasks"
                                @change="loadData" :hasEditPerm="hasEditPerm" />
                        </a-tab-pane>
                    </a-tabs>
                </div>
                <!-- <div class="item-detail-panel-log">
                </div> -->

            </div>
        </div>
        <sprint-select-dialog :isShowDialog="showSprintDialog" :projectId="projectId" @cancel="showSprintDialog = false"
            @ok="onSelectSprintOK" />
        <tracker-item-select-modal :projectId="projectId" :initalData=[] ref="trackerItmeSlectModal"
            @trackerItemSelector="onSelectTrackerItems" />
    </config-page>
</template>
<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import {cloneDeep} from 'lodash'
import VXETable from 'vxe-table';
import { iconUrl } from "@/utils/util"
import { hasPermission } from '@/utils/permission'
import QuickPicker from '@/components/select/QuickPicker.vue';
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import { findOnePlan, updatePlan, associateSprints, dissociateSprints, associateTrackerItems, dissociateTrackerItems } from '@/services/plan/PlanService'
import { formatDate } from '@/utils/DateUtils'
import {
    findProjectUsers
} from "@/services/tracker/ProjectRoleMemberService";
import SprintSelectDialog from './SprintSelectDialog.vue'
import TrackerItemSelectModal from '@/components/dialog/TrackerItemSelectModal'
import PlanDependency from './PlanDependency.vue';

export default {
    name: "TaskDetail",
    components: {
        ConfigPage, ProjectUserSelect, QuickPicker, SprintSelectDialog, TrackerItemSelectModal, PlanDependency,
        HAvatar
    },
    data() {
        return {
            task: {},
            projectUsers: [],
            formData: {
                name: '',
                planStartDate: '',
                planEndDate: '',
                ownerId: '',
            },
            showSprintDialog: false,
            showTrackerItemDialog: false,
            showOwner: false,
            showStartDate: false,
            showEndDate: false,
            showProgress: false,
        };
    },

    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },
        taskId() {
            return this.$route.params.itemId
        },
        hasEditPerm() {
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
        formatDate(v) {
            if (!v) return '-'
            else return formatDate(v)
        },
        formatProgress(v) {
            if (!v) return '-'
            else return v + '%'
        },
        loadData() {

            findOnePlan(this.taskId).then(resp => {
                this.task = resp
                this.formData = this.task
                console.log(this.task)
            })
        },
        onChangeTitle(e) {
            this.task.name = e.target.value
            updatePlan(this.task).then(resp => {
                this.loadData()
            })
        },
        onChangeOwner(user) {
            this.showOwner = false
            this.task.owner = user
            updatePlan(this.task).then(resp => {
                this.loadData()
            })
        },
        onStartDateChange() {
            this.showStartDate = false
            this.task.planStartDate = this.formData.planStartDate.format('YYYY-MM-DD')
            updatePlan(this.task).then(resp => {
                this.loadData()
            })
        },
        onEndDateChange() {
            this.showEndDate = false
            this.task.planEndDate = this.formData.planEndDate.format('YYYY-MM-DD')
            updatePlan(this.task).then(resp => {
                this.loadData()
            })
        },
        onChangeProgress() {
            this.showProgress = false
            updatePlan(this.task).then(resp => {
                this.loadData()
            })
        },
        onSelectSprintOK(sprints) {
            associateSprints(this.taskId, sprints).then(resp => {
                this.loadData()
            })
            this.showSprintDialog = false
        },
        onDeleteSprint(row) {
            VXETable.modal.confirm({
                title: '解除关联',
                content: '正在将迭代「' + row.name + '」个解除关联，是否解除?'
            }).then(type => {
                if (type === 'confirm') {
                    dissociateSprints(this.taskId, [row]).then(resp => {
                        this.loadData()
                        VXETable.modal.message({ content: '迭代解除关联成功', status: 'success' })
                    })
                }
            })
        },
        onAssociateTrackerItems() {
            this.$refs.trackerItmeSlectModal.view();
        },
        onSelectTrackerItems(items) {
            associateTrackerItems(this.taskId, items).then(resp => {
                this.loadData()
            })
        },
        onDeleteTrackerItems(row) {
            VXETable.modal.confirm({
                title: '解除关联',
                content: '正在将工作项「' + row.name + '」解除关联，是否解除?'
            }).then(type => {
                if (type === 'confirm') {
                    dissociateTrackerItems(this.taskId, [row]).then(resp => {
                        this.loadData()
                        VXETable.modal.message({ content: '工作项解除关联成功', status: 'success' })
                    })
                }
            })

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
