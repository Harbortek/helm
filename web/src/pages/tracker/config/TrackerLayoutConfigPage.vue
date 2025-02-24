<template>
    <config-page :routes="routes()" title="" description="属性与视图可以定制工作项类型显示哪些功能标签页、详情标签页里显示哪些属性，并配置属性的默认值和是否必填。">
        <a-tabs default-active-key="NEW" :active-key="currentTab" @change="onChangeTab"
            style="overflow:visible;height:82vh">
            <a-tab-pane key="NEW" tab="新建页">
                <div class="preview-content">
                    <div class="preview-header" style="">
                        <span style="color: #909090;">预览效果</span>
                        <a-button style="" type="primary" @click="onClickEditNew">编辑新建表单</a-button>
                    </div>
                    <tracker-layout-edit-new class="preview-card" isPreview="true" :tracker="tracker"
                        :customerFields="customerFields" :sections="sections"></tracker-layout-edit-new>
                </div>
            </a-tab-pane>
            <a-tab-pane key="DETAIL" tab="详情页">
                <div class="preview-content">
                    <div class="preview-header">
                        <span style="color: #909090;">预览效果</span>
                        <a-button style="" type="primary" @click="onClickEditDetail">编辑详情表单</a-button>
                    </div>
                    <tracker-layout-edit-detail isPreview="true" :projectId="projectId" :tracker="tracker"
                        :trackerItem="trackerItem" :keyFields="keyFields" :customerFields="customerFields"
                        :sections="sections"></tracker-layout-edit-detail>
                </div>
            </a-tab-pane>
            <a-tab-pane key="LIST" tab="列表属性">
                <div class="preview-content" style="height:76vh;">
                    <div class="page-header">
                        <div class="page-description page-description-info manager-desc">
                            <div class="page-description-border"></div>
                            <div class="page-description-container">
                                <p class="page-description-p style--font-14">
                                    定义该工作项类型下的工作项在“看板”视图可以显示的属性信息。除标题，描述和状态外，最多可以添加 5 个属性。选项配色请到配置中心的工作项属性里调整。
                                </p>
                            </div>
                        </div>
                    </div>

                    <div style="width: 100%; margin: 20px 0; display:flex;justify-content:space-between;">
                        <span style="color: #909090;">预览效果</span>
                        <a-button style="" type="primary" @click="listFieldsVisible = true">编辑列表属性</a-button>
                    </div>
                    <vxe-table style="background-color: #fff;width:95%;" :show-header="false"
                        :data="[...itemData, ...itemData, ...itemData]">
                        <template v-for="item in listFields">
                            <vxe-column v-if="item.systemProperty == 'priority'" :field="item.systemProperty"
                                :key="item.id" title="" :width="60">
                                <template #default="{ row }">
                                    <a-tooltip :title="'优先级：' + row.priority.name" :overlayStyle="{ fontSize: '10px' }">
                                        <a-tag style="border:none;cursor: pointer;"
                                            :style="{ color: row.priority.color, backgroundColor: row.priority.backgroundColor }">{{
        row.priority.name }}</a-tag>
                                    </a-tooltip>
                                </template>
                            </vxe-column>
                            <vxe-column v-else :field="item.systemProperty" :key="item.id" title=""
                                :width="item.name.length * 10 + 30">
                                <template #default="{ row }">
                                    <a-tooltip
                                        :title="item.name + '：' + (row[getsystemProperty(item.systemProperty)]?.name || item.name)"
                                        :overlayStyle="{ fontSize: '10px' }">
                                        <a-tag class="tag-box">
                                            {{ row[getsystemProperty(item.systemProperty)]?.name || item.name }}</a-tag>
                                    </a-tooltip>
                                </template>
                            </vxe-column>
                        </template>
                        <vxe-column field="name" title="">
                            <template #default="{ row }">{{ row.name }}</template>
                        </vxe-column>
                        <vxe-column field="status.name" title="">
                            <template #default="{ row }">
                                <div class="transition-status">
                                    <span class="ui-tag-status"
                                        :style="{ color: row.meaning.color, 'border-color': row.meaning.color }">{{
        row.status.name }}</span>
                                </div>
                            </template>
                        </vxe-column>
                    </vxe-table>
                </div>
            </a-tab-pane>
        </a-tabs>
        <tracker-layout-list-fields-dialog :isShowDialog="listFieldsVisible" :tracker="tracker"
            @refresh="reloadListFields" @cancel="listFieldsVisible = false"></tracker-layout-list-fields-dialog>

    </config-page>
</template>
<script>
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import {
    findOneTracker, updateTrackerLayout, findTrackerLayout
} from "@/services/tracker/TrackerService";
import QuickPicker from '@/components/select/QuickPicker.vue';
import TrackerComment from '../items/TrackerComment.vue';
import TrackerAttachment from '../items/TrackerAttachment.vue';
import TrackerItemSelectModal from '@/components/dialog/TrackerItemSelectModal'
import ProjectSelect from '../../../components/select/ProjectSelect.vue';
import TrackerSelect from '../../../components/select/TrackerSelect.vue';
import ProjectUserSelect from '../../../components/select/ProjectUserSelect.vue';
import PrioritySelect from '../../../components/select/PrioritySelect.vue';
import SimpleEditor from '../../../components/editor/SimpleEditor.vue';
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
import { mapGetters, mapState, mapMutations } from "vuex";
import {
    findProjectRolesAndMembers
} from "@/services/tracker/ProjectRoleMemberService";
import TrackerLayoutListFieldsDialog from './TrackerLayoutListFieldsDialog.vue';
import TrackerItemKeyFields from '@/components/select/TrackerItemKeyFields.vue';
import TrackerLayoutEditNew from './TrackerLayoutEditNew.vue'
import TrackerLayoutEditDetail from './TrackerLayoutEditDetail.vue';


export default {
    name: "TrackerLayoutConfigPage",
    components: {
        ConfigPage, ProjectSelect, TrackerSelect, ProjectUserSelect, PrioritySelect, RoleMembersTable, SimpleEditor,
        QuickPicker, TrackerComment, TrackerAttachment, TrackerItemSelectModal, TrackerLayoutListFieldsDialog, TrackerItemKeyFields,
        TrackerLayoutEditNew, TrackerLayoutEditDetail,

    },
    data() {
        return {
            currentTab: 'NEW',
            projectId: '',
            trackerId: '',
            tracker: {},
            formData: {
                name: '',
                projectId: '',
                trackerId: '',
                values: {},
                owner: { id: '' },
                priority: '',
                relatedWorkItems: [],
                watchers: []
            },
            listFieldsVisible: false,
            keyFields: [],
            sections: [],
            listFields: [],
            trackerItem: {},
            itemData: [
                {
                    "id": "1183942482254106624",
                    "name": "这是一个工作项示例",
                    "createBy": {
                        "name": "胡隽",
                        "id": "1150207089906290688",
                        "icon": "/helm/2023-06-12/59c86c4f-13da-4283-b4df-b1bfc26f2b28.blob"
                    },
                    "createDate": "2023-06-07 16:59:39",
                    "lastModifiedBy": {
                        "name": "胡隽",
                        "id": "1150207089906290688",
                    },
                    "lastModifiedDate": "2023-06-07 16:59:39",
                    "project": {
                        "id": "1183917925904420864",
                        "name": "需求管理项目",
                    },
                    "itemNo": "1",
                    "owner": {
                        "name": "胡隽",
                        "id": "1150207089906290688",
                    },
                    "priority": {
                        "name": "最高",
                        "color": "rgb(255, 255, 255)",
                        "backgroundColor": "rgb(230, 52, 34)",
                    },
                    "status": {
                        "id": "1183917933584191500",
                        "name": "未开始",
                    },
                    "meaning": {
                        "id": "1",
                        "name": "未开始",
                        "color": "#338fe5",
                    },
                    "watchers": [
                        {
                            "type": "IDENTITY_SPECIAL_ROLE",
                            "name": "工作项负责人",
                            "id": "1183917930480406537"
                        },
                        {
                            "type": "IDENTITY_SPECIAL_ROLE",
                            "name": "工作项创建者",
                            "id": "1183917930480406538"
                        }
                    ],
                    "relatedWorkItems": [],
                    "attachments": []
                },
            ],
            sectionList: [
                { value: "DETAIL", name: "详情", desc: "显示当前工作项下的描述、属性和基础信息。" },
                { value: "CYCLE_PROGRESS", name: "周期与进度", desc: "显示当前工作项下的进度、计划开始日期、计划完成日期。" },
                { value: "WORK_HOURS", name: "工时", desc: "显示当前工作项的工时信息和关联的工作记录。" },
                { value: "TEST_CASES", name: "测试情况", desc: "显示当前工作项作为测试用例的测试信息。" },
                { value: "RELATED_ITEMS", name: "关联工作项", desc: "显示当前工作项关联的工作项列表。" },
                { value: "RELATED_CODE", name: "代码关联", desc: "显示当前工作项关联的代码提交、合并请求、代码分支信息。" },
                { value: "RELATED_WIKI", name: "关联Wiki页面", desc: "显示当前工作项关联的Wiki页面。" },
                { value: "ATTACHMENTS", name: "文件", desc: "显示当前工作项关联的文件；支持通过上传添加关联文件。" },
                { value: "HYPERLINKS", name: "链接", desc: "显示当前工作项关联的链接；包括内部链接和外部链接" },
                { value: "RELATED_TESTS", name: "关联测试结果", desc: "显示当前工作项关联的用例测试结果。" }
            ],
        };
    },
    computed: {
        customerFields() {
            let layout = this.findLayout()
            let customerFields = []
            if (layout) {
                const fields = layout.fields || []
                fields.forEach(f => {
                    let field = this.findFields(f.id)
                    if (field) {
                        customerFields.push(field)
                    }
                })
                this.markRequired(customerFields, this.getRequiredFields())
            }
            return customerFields
        },
        ...mapGetters("account", ["user"]),
    },
    mounted() {
        this.projectId = this.$route.params.projectId
        this.trackerId = this.$route.params.trackerId
        if (this.$route.params.selectTab) {
            this.currentTab = this.$route.params.selectTab
        }

        findOneTracker(this.trackerId).then(resp => {
            this.tracker = resp;
            this.tracker.trackerFields.forEach(x => {
                x.trackerId = this.trackerId
            })
            this.reload()
            this.initData();
        })
    },
    methods: {
        getsystemProperty(systemProperty) {
            if (systemProperty) {
                systemProperty = systemProperty.toString();
                if (systemProperty.endsWith('Id')) {
                    systemProperty = systemProperty.substring(0, systemProperty.length - 2)
                }
            }
            return systemProperty
        },
        initData() {
            let layout = this.findLayout()
            //listFields
            if (layout) {
                const fields = layout.fields || []
                this.listFields = []
                fields.forEach(f => {
                    let field = this.findFields(f.id)
                    if (field && field.name != '标题' && field.name != '状态' && field.name != '描述') {
                        this.listFields.push(field)
                    }
                })
            }
            //keyFields
            if (layout) {
                const fields = layout.keyFields
                this.keyFields = []
                fields.forEach(f => {
                    let field = this.findFields(f.id)
                    if (field) {
                        this.keyFields.push(field)
                    }
                })
            }

            if (layout) {
                //sections
                const fields = layout.fields
                this.sections = []
                for (let item of layout.sections) {//模块标签页
                    for (let item2 of this.sectionList) {
                        if (item == item2.value) {
                            this.sections.push(item2)
                        }
                    }
                }

            }

        },
        reloadListFields(listFields) {
            this.listFields = listFields
        },
        onClickEditNew() {
            this.$router.push({ path: "trackerLayoutEditNew" });
        },
        onClickEditDetail() {
            this.$router.push({ path: "trackerLayoutEditDetail" });
        },
        routes: function () {
            return [{
                name: "trackerListConfig", meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerListConfig",
                    show: false,
                },
            }, {
                name: "trackerConfigPortal",
                meta: {
                    icon: "blank",
                    title: this.tracker.name,
                    show: false,
                },
            }, {
                name: 'trackerLayoutConfig', meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerLayoutConfig",
                    show: false,
                },
            }]
        },
        onChangeTab(activeKey) {
            this.currentTab = activeKey
            this.initData();
        },
        reload() {
            this.$nextTick(() => {
                this.prepareFormData()
            })
        },
        findLayout() {
            if (this.tracker && this.tracker.trackerLayouts && this.tracker.trackerLayouts.length > 0) {
                let layout = this.tracker.trackerLayouts.filter(lay => { return lay.name === this.currentTab })[0]
                return layout;
            }
            return null;
        },
        findFields(fieldId) {
            if (this.tracker && this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                return this.tracker.trackerFields.filter(f => { return f.id === fieldId })[0]
            }
            return null;
        },
        getFirstInitStatus() {
            const statuses = this.tracker.trackerStatuses
            for (let i = 0; i < statuses.length; i++) {
                if (statuses[i].initial) {
                    return statuses[i]
                }
            }
            return
        },
        getRequiredFields() {
            const fields = this.tracker.trackerFields
            const firstStatus = this.getFirstInitStatus()
            let requiredFields = []
            fields.forEach(f => {
                if (!f.system && f.mandatory) {
                    let found = false
                    for (let i = 0; i < f.exceptStatus.length; i++) {
                        if (f.exceptStatus[i].id === firstStatus.id) {
                            found = true
                            break
                        }
                    }
                    if (!found) {
                        requiredFields.push(f.id)
                    }
                }
            })
            return requiredFields
        },
        markRequired(componentList, requiredFields) {
            componentList.forEach(cur => {

                if (requiredFields.indexOf(cur.id) >= 0) {
                    cur.required = true
                }

            })
        },
        prepareFormData() {
            this.trackerItem = {
                itemNo: "1",
                name: "第一个需求",
                priority: {
                    backgroundColor: "rgb(224, 236, 251)",
                    color: "rgb(48, 127, 226)",
                    name: "优先级"
                },
                status: {
                    name: "状态"
                },
                owner: this.user,
                values: {},
            },
                this.formData = {
                    projectId: this.projectId,
                    trackerId: this.tracker.id,
                    name: '这是一条示例数据',
                    description: '描述',
                    values: {},
                    owner: this.user,
                    priority: '3',
                    relatedWorkItems: [],
                    watchers: []
                }

            findProjectRolesAndMembers(this.projectId, 'SCOPE_TRACKER').then(resp => {
                let data = resp;
                const all = [].concat(data.projectRoles).concat(data.specialRoles).concat(data.members)
                const valueArray = ['工作项创建者', '工作项负责人']
                let tableData = all.filter(item => { return valueArray.indexOf(item.name) >= 0 })
                this.formData.watchers = tableData
            });
        },
    },
};
</script>
<style lang="less" scoped>
:deep(.ant-form-item) {
    margin-bottom: 8px;
}

.page-header {
    width: 100%;
    flex: 0 0 auto;
    line-height: 1;

    .ant-breadcrumb {
        color: rgba(93, 99, 105);
        font-size: 18px;
        line-height: 26px;
    }

    .ant-breadcrumb a {
        color: rgba(93, 99, 105);
        font-size: 18px;
        line-height: 26px;
    }

}

.page-description {
    display: flex;
    line-height: 1.5;
    box-sizing: border-box;
    border-bottom: 1px solid #e8e8e8;
    border-radius: 0;
    border-right: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}

.page-description-border {
    border-radius: 0;
    flex: none;
    width: 4px;
}

.page-description-info .page-description-border {
    background: #338fe5;
}

.page-description-container {
    background: #fff;
    display: flex;
    flex: auto;
    padding: 10px;
}

.page-description-p {
    margin: 0 0 0 0;
    padding: 0;
    white-space: pre-wrap;
}

.style--font-14 {
    font-size: 14px;
    line-height: 22px;
}

.transition-status {
    float: right;
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

.preview-content {
    width: 100%;
    padding: 20px;
    background: #f8f8f8;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.preview-header {
    width: 100%;
    margin-bottom: 10px;
    display: flex;
    justify-content: space-between;
}

.preview-card {
    background-color: #fff;
    max-width: 860px;
    min-width: 760px;
    box-shadow: 0px 0px 1px rgba(48, 48, 48, .25), 0px 12px 24px rgba(48, 48, 48, .15);
}

.tag-box {
    cursor: pointer;
    border: none;
    background-color: rgba(144, 144, 144, .15);
}
</style>
