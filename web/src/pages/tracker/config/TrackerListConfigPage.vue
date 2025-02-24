<template>
    <config-page title=""
        description="工作项类型用于在同一个项目下使用不同类型的工作项，不同的工作项类型可以定义不同的属性字段、权限和工作流等。项目下的工作项类型配置的修改不会同步到配置中心下的工作项类型中。“项目组件”中增加的工作项类型组件会同时增加到这里。">
        <vxe-toolbar size="medium">
            <template #buttons>
                <vxe-input v-model="keyword" placeholder="搜索工作项类型" type="search" className="searchbox" width="240"
                    clearable @search-click="onSearch()"></vxe-input>
            </template>
            <template #tools>
                <vxe-button content="排序" @click="showSortDialog = true"></vxe-button>
                <vxe-button content="从已有项目复制" @click="onCopyFromProject"></vxe-button>
                <vxe-button status="primary" content="添加工作项类型" @click="onCreateTracker"></vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-table :data="trackers.filter(v => (new RegExp(keyword)).test(v.name))" :loading="loading"
            :row-config="{ isHover: true }">
            <vxe-column title="以下为当前项目使用的工作项类型" :width="600">
                <template #default="{ row }">
                    <div class="icon-title-desc-item" @click="onClickRow(row)">
                        <div class="icon-container"><h-icon :type="row.trackerType.icon"
                                :style="{ color: row.trackerType.color, backgroundColor: row.trackerType.backgroundColor }" />
                        </div>
                        <div class="text-container">
                            <div class="ui-font-h3">
                                {{ row.name }}
                            </div>
                            <div class="desc-container">
                                <span class="field-count">{{ row.trackerFields.length }} 工作项属性</span>
                                <span class="desc-seperate">|</span>
                                <span class="status-count">{{ row.trackerStatuses.length }} 工作项状态</span>
                            </div>
                        </div>
                    </div>
                </template>
            </vxe-column>
            <vxe-column title="操作" header-align="right" align="right" show-overflow>
                <template #default="{ row }">
                    <vxe-button type="text" icon="vxe-icon-edit" @click="onChangeName(row)">重命名</vxe-button>
                    <vxe-button type="text" icon="vxe-icon-menu" @click="onFieldsPage(row)">工作项属性</vxe-button>
                    <vxe-button type="text" icon="vxe-icon-table" @click="onLayoutPage(row)">工作项布局</vxe-button>
                    <vxe-button type="text" icon="vxe-icon-user" @click="onPermissionPage(row)">工作项权限</vxe-button>
                    <vxe-button type="text" icon="vxe-icon-flow-branch"
                        @click="onStateTransitionPage(row)">工作项工作流</vxe-button>
                    <vxe-button type="text" icon="vxe-icon-bell" @click="onNotificationPage(row)">工作项通知</vxe-button>
                </template>
            </vxe-column>
        </vxe-table>

        <a-modal v-if="showCreateTrackerDialog" :visible="showCreateTrackerDialog" centered
            :bodyStyle="{ padding: '0 20px' }" width="640px" title="添加工作项类型" @cancel="showCreateTrackerDialog = false"
            @ok="onCreateTrackerOK">

            <a-form-model ref="trackerForm" :model="formData" layout="horizontal" :rules="rules"
                style="box-sizing: border-box; direction: ltr;  height: 380px;position: relative;will-change: transform;overflow: auto;">
                <a-form-model-item label="工作项类型名称（本项目内）" prop="name">
                    <vxe-input v-model="formData.name" placeholder="输入工作项类型名称" style="width:100%;"></vxe-input>
                </a-form-model-item>
                <a-form-model-item label="全局工作项类型（统计用）" prop="trackerTypeId">
                    <a-select v-model="formData.trackerTypeId">
                        <a-select-option v-for="item in  trackerTypesFilter" :key="item.id" :value="item.id">
                            <div style="font-size:!4px;line-height:28px;">
                                <h-icon v-if="item.icon" :type="item.icon" style="bottom: 0px;" />
                                <span style="margin: 0px 15px;">{{ item.name }}</span>
                                <a-tag v-if="item.system" class="tag-box">系统</a-tag>
                            </div>
                        </a-select-option>
                    </a-select>
                </a-form-model-item>
            </a-form-model>
        </a-modal>
        <a-modal v-if="showEditTrackerDialog" :visible="showEditTrackerDialog" centered
            :bodyStyle="{ padding: '0 20px' }" width="640px" title="编辑工作项类型" @cancel="showEditTrackerDialog = false"
            @ok="onEditTrackerOK">

            <a-form-model ref="trackerForm" :model="formData" layout="horizontal" :rules="rules"
                style="box-sizing: border-box; direction: ltr;  height: 380px;position: relative;will-change: transform;overflow: auto;">
                <a-form-model-item label="工作项类型名称（本项目内）" prop="name">
                    <vxe-input v-model="formData.name" placeholder="输入工作项类型名称" style="width:100%;"></vxe-input>
                </a-form-model-item>
                <a-form-model-item label="全局工作项类型（统计用）" prop="trackerTypeId">
                    <a-select v-model="formData.trackerTypeId">
                        <a-select-option v-for="item in  trackerTypesFilter" :key="item.id" :value="item.id">
                            <div style="font-size:!4px;line-height:28px;">
                                <h-icon v-if="item.icon" :type="item.icon" style="bottom: 0px;" />
                                <span style="margin: 0px 15px;">{{ item.name }}</span>
                                <a-tag v-if="item.system" class="tag-box">系统</a-tag>
                            </div>
                        </a-select-option>
                    </a-select>
                </a-form-model-item>
            </a-form-model>
        </a-modal>
        <tracker-list-sort-dialog :isShowDialog="showSortDialog" :trackerList="trackers"
            @cancel="showSortDialog = false" @ok="onSortOK" />
        <tracker-list-copy-dialog :isShowDialog="showCopyDialog" :projectId="projectId" @cancel="showCopyDialog = false"
            @ok="onCopyOK" />

    </config-page>
</template>
<script>
import {cloneDeep} from 'lodash'
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import SelectableList from '../../../components/list/SelectableList.vue';
import TrackerListSortDialog from './TrackerListSortDialog.vue'
import TrackerListCopyDialog from './TrackerListCopyDialog.vue';
import {
    findTrackers, createTracker, copyTracker, updateTrackerName
} from "@/services/tracker/TrackerService";
import {
    findEnumsByCode,
} from "@/services/system/EnumService";
export default {
    name: "TrackerConfigMainPage",
    components: { ConfigPage, SelectableList, TrackerListSortDialog, TrackerListCopyDialog },
    data() {
        let that = this
        const nameValidator = (rule, value, callback) => {
            let found = false;
            let i = 0;
            for (i = 0; i < that.trackerTypeNames.length; i++) {
                if (that.trackerTypeNames[i] === value) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return callback();
            } else {
                return callback(new Error("已存在同样名称的数据"));
            }
        };
        return {
            loading: false,
            keyword: '',
            trackers: [],
            showCreateTrackerDialog: false,
            formData: {},
            trackerTypes: [],
            selectedRows: [],
            selectedRowKeys: [],
            rules: {
                name: [{ required: true, message: "请输入工作项类型名称", trigger: "blur" },
                {
                    min: 2,
                    max: 20,
                    message: "长度在2到20之间",
                    trigger: "blur",
                },
                { validator: nameValidator, trigger: "change" },],
                trackerTypeId: [{ required: true, message: "请选择工作项类型母版", trigger: "change" }]
            },
            showSortDialog: false,
            showCopyDialog: false,
            showEditTrackerDialog: false,
        };
    },
    mounted() {
        this.loadData()
    },
    computed: {
        trackerTypesFilter: function () {
            // let names = [];
            // names = this.trackers.map(f => f.name)

            // let result = this.trackerTypes.filter(f => { return names.indexOf(f.name) < 0 })
            // return result
            return this.trackerTypes
        },
        trackerTypeNames() {
            let names = [];
            names = this.trackers.map(f => f.name)
            return names
        },
        projectId() {
            return this.$route.params.projectId
        }
    },
    methods: {
        loadData() {
            this.loading = true
            findTrackers({ projectId: this.projectId }).then(resp => {
                this.trackers = resp;
                this.loading = false
            })
        },

        onSearch() {
        },
        onCopyFromProject() {
            this.showCopyDialog = true
        },
        onCopyOK(tracker) {
            tracker.projectId = this.projectId
            copyTracker(tracker).then(resp => {
                this.showCopyDialog = false
                this.loadData()
            })
        },
        onSortOK() {
            this.showSortDialog = false
            this.loadData()
        },
        onCreateTracker() {
            this.formData = { name: '', trackerTypeId: '' }
            this.showCreateTrackerDialog = true;
            this.loading = true;

            findEnumsByCode('TRACKER_TYPE').then((resp) => {
                this.trackerTypes = resp;
                this.loading = false;
            });
        },
        onCreateTrackerOK() {
            this.$refs.trackerForm.validate((valid) => {
                if (valid) {
                    let result = { projectId: this.projectId }
                    result.name = this.formData.name
                    result.trackerType = { id: this.formData.trackerTypeId }
                    createTracker(result).then(resp => {
                        this.showCreateTrackerDialog = false
                        this.loadData()
                    })
                }
            })
        },

        selectChangeEvent({ checked, records, reserves }) {
            this.selectedRowKeys = [...reserves.map(v => v.id), ...records.map(v => v.id)];
            this.selectedRows = [...reserves, ...records];
            console.log("selectRows", this.selectedRows)
        },
        onClickRow(row) {
            this.$router.push({ name: 'trackerConfigPortal', params: { trackerId: row.id } })
        },
        onFieldsPage(tracker) {
            this.$router.push({
                name: 'trackerFieldsConfig', params: {
                    trackerId: tracker.id
                }
            })
        },
        onLayoutPage(tracker) {
            this.$router.push({
                name: 'trackerLayoutConfig', params: {
                    trackerId: tracker.id
                }
            })
        },
        onPermissionPage(tracker) {
            this.$router.push({
                name: 'trackerPermissionConfig', params: {
                    trackerId: tracker.id
                }
            })
        },
        onStateTransitionPage(tracker) {
            this.$router.push({
                name: 'trackerStateTransitionConfig', params: {
                    trackerId: tracker.id
                }
            })
        },
        onNotificationPage(tracker) {
            this.$router.push({
                name: 'trackerNotificationConfig', params: {
                    trackerId: tracker.id
                }
            })
        },
        onChangeName(tracker) {
            console.log(tracker)
            this.formData = cloneDeep(tracker);
            this.formData.trackerTypeId = tracker.trackerType.id;
            this.showEditTrackerDialog = true
            this.loading = true;
            findEnumsByCode('TRACKER_TYPE').then((resp) => {
                this.trackerTypes = resp;
                this.loading = false;
            });
        },
        onEditTrackerOK() {
            this.$refs.trackerForm.validate((valid) => {
                if (valid) {
                    const trackerId = this.formData.id
                    let result = { id: trackerId }
                    result.name = this.formData.name
                    result.trackerType = { id: this.formData.trackerTypeId }
                    updateTrackerName(trackerId, result).then(resp => {
                        this.showEditTrackerDialog = false
                        this.loadData()
                    })
                }
            })
        }
    },
};
</script>
<style lang="less" scoped>
.icon-title-desc-item {
    height: 69px;
    padding: 15px 20px;
    align-items: center;
    height: auto;
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    padding: 0px;
    line-height: 1.4;
    cursor: pointer;
}

.icon-container {
    align-self: flex-start;
    line-height: 0;
    padding-top: 3px;
    margin-right: 10px;
}

.text-container {
    margin: 0;

    .ui-font-h3 {
        font-size: 15px;
        color: #303030;
        font-weight: 500;
    }

    .desc-container {
        color: #909090;
        font-size: 12px;
        line-height: 20px;

        .desc-seperate {
            margin: 0 10px;
        }
    }
}

.tag-box {
    margin-left: 10px;
    height: 18px;
    line-height: 15px;
    border-radius: 10px;
    color: #909090;
    background: #0000;
}
</style>