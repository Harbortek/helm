<template>
    <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" :centered="true" :maskClosable='false'
        :width="800" :confirm-loading="loading">
        <a-form-model ref="itemForm" layout="horizontal" :model="formData" :rules="rules">
            <!-- <a-row>
                <a-form-model-item ref="itemNo" label="编号" prop="itemNo">
                    <a-input placeholder="工作项编号" v-model="formData.itemNo" />
                </a-form-model-item>
            </a-row> -->
            <a-row>
                <a-form-model-item ref="name" label="标题" prop="name">
                    <a-input placeholder="工作项名称" v-model="formData.name" @blur="() => {
        $refs.name.onFieldBlur();
    }" />
                </a-form-model-item>
            </a-row>
            <a-row :gutter="15">
                <a-col :span="12">
                    <a-form-model-item ref="trackerId" label="工作项类型" placeholder="工作项类型" prop="trackerId">
                        <tracker-select v-model="formData.trackerId" @change="onChangeTracker" :projectId="projectId"
                            :trackerPermName="'ITEM_CREATE'"></tracker-select>
                    </a-form-model-item>
                </a-col>
                <a-col :span="12">
                    <a-form-model-item ref="sprintId" label="所属迭代" placeholder="工作项迭代" prop="sprintId">
                        <sprint-select v-model="formData.sprintId" :projectId="projectId"></sprint-select>
                    </a-form-model-item>
                </a-col>
            </a-row>

            <a-row :gutter="15">
                <a-col :span="f.inputType == 'WIKI' ? 24 : 12" v-for="(f, index) in customerFields" :key="f.id">
                    <a-form-model-item :label="f.name" :prop="f.name" :required="f.required">
                        <ItemCustomFieldsShow :fields="f" v-model="formData.values[f.id]" :projectId="projectId" 
                        :trackerId="trackerCopy?.id"></ItemCustomFieldsShow>
                    </a-form-model-item>
                </a-col>
            </a-row>

            <div v-for="item in sections" :key="item">
                <a-row :gutter="15" v-if="item == 'DETAIL'">
                    <a-col :span="24">
                        <a-form-model-item ref="description" label="描述" prop="description">
                            <simple-editor v-model="formData.description" :showToolbar="editorInEditMode.description"
                                @focus="editorInEditMode.description = true"
                                @blur="editorInEditMode.description = false" />
                        </a-form-model-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-model-item ref="owner.id" label="负责人" prop="owner.id">
                            <project-user-select v-model="formData.owner.id" :projectId="projectId" />
                        </a-form-model-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-model-item ref="priority" label="优先级" prop="priority" :span="12">
                            <priority-select ref="prioritySelectRef" v-model="formData.priority" />
                        </a-form-model-item>
                    </a-col>
                </a-row>

                <fieldset v-else-if="item == 'RELATED_ITEMS'">
                    <TrackerRelatedItem style="margin:0" :projectId="projectId" :trackerId="trackerCopy.id"
                        :relatedWorkItems="formData.relatedWorkItems"></TrackerRelatedItem>
                </fieldset>

                <fieldset v-else-if="item == 'CYCLE_PROGRESS'">
                    <TrackerCycleProgress style="margin:0" :formData="formData"></TrackerCycleProgress>
                </fieldset>
                <fieldset v-else-if="item == 'WORK_HOURS'">
                    <a-form-model-item label="预计工时" prop="watchers" style="width:50%">
                        <a-input-search style="margin-left:10px;" @change="onChangeEstimateHour"
                            v-model="formData.estimateWorkingHours" placeholder="请输入数字">
                            <a-button slot="enterButton">
                                小时
                            </a-button>
                        </a-input-search>
                    </a-form-model-item>
                </fieldset>
                <!-- <fieldset v-else-if="item == 'TEST_CASES'">
                    <div style="font-size:14px;color:#000000D9">测试</div>
                    <tracker-test-cases style="margin-left:10px;" :formData="formData"
                        :isShowDialog="item == 'TEST_CASES'"></tracker-test-cases>
                </fieldset> -->
                <fieldset v-else-if="item == 'RELATED_WIKI'">
                    <TrackerRelatedWiki style="margin:0" :projectId="projectId" :trackerItem="formData">
                    </TrackerRelatedWiki>
                </fieldset>

                <fieldset v-else-if="item == 'ATTACHMENTS'">
                    <TrackerAttachment style="margin:0" :trackerItem="formData"></TrackerAttachment>
                </fieldset>

                <fieldset v-else-if="item === 'HYPERLINKS'">
                    <TrackerHyperlinks style="margin:0" :tracker-item="formData" :isShowDialog="item === 'HYPERLINKS'">
                    </TrackerHyperlinks>
                    <!-- <tracker-hyperlinks :tracker-item="trackerItem" :isShowDialog="currentTab === 'HYPERLINKS'" />  -->
                </fieldset>

            </div>

            <fieldset>
                <a-form-model-item label="关注者" prop="watchers" :span="24">
                    <role-members-table v-model="formData.watchers" :projectId="projectId" scope="SCOPE_TRACKER"
                        @delete="onDeleteWatcher" :title="'添加需要关注的成员'" />
                </a-form-model-item>
            </fieldset>
        </a-form-model>

        <tracker-item-select-modal :projectId="projectId" :trackerId="trackerCopy.id"
            :initalData="formData.relatedWorkItems" ref="trackerItmeSlectModal"
            @trackerItemSelector="trackerItemSelector"></tracker-item-select-modal>
    </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import moment from 'moment'
import ItemCustomFieldsShow from '@/components/tool/ItemCustomFieldsShow.vue';
import ProjectSelect from '../../../components/select/ProjectSelect.vue';
import TrackerSelect from '../../../components/select/TrackerSelect.vue';
import SprintSelect from '../../../components/select/SprintSelect.vue';
import ProjectUserSelect from '../../../components/select/ProjectUserSelect.vue';
import PrioritySelect from '../../../components/select/PrioritySelect.vue';
import SimpleEditor from '../../../components/editor/SimpleEditor.vue';
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
import TrackerItemSelectModal from '@/components/dialog/TrackerItemSelectModal'
import { findTrackerItems } from "@/services/tracker/TrackerItemService";
import TrackerCycleProgress from './TrackerCycleProgress.vue';
import TrackerRelatedItem from './TrackerRelatedItem.vue';
import TrackerRelatedWiki from './TrackerRelatedWiki.vue';
import TrackerHyperlinks from './TrackerHyperlinks.vue';
import TrackerAttachment from './TrackerAttachment.vue';
import TrackerTestCases from './TrackerTestCases.vue';
import { mapGetters, mapState, mapMutations } from "vuex";
import {
    findProjectRolesAndMembers
} from "@/services/tracker/ProjectRoleMemberService";
import {
    findOneTracker
} from "@/services/tracker/TrackerService";
import {
    findEnumsByCode
} from "@/services/system/EnumService";

export default {
    name: "CreateTrackerItemDialog",
    components: {
        ProjectSelect, TrackerSelect, SprintSelect, ProjectUserSelect, PrioritySelect, RoleMembersTable,
        SimpleEditor, TrackerItemSelectModal, TrackerHyperlinks, TrackerTestCases, TrackerCycleProgress, TrackerRelatedItem,
        TrackerRelatedWiki, TrackerAttachment, ItemCustomFieldsShow
    },
    data() {
        return {
            loading: false,
            customerFields: [],
            sections: [],
            trackerCopy: {},
            formData: {
                name: '',
                projectId: '',
                trackerId: '',
                sprintId: undefined,
                values: {},
                owner: { id: '' },
                priority: '',
                relatedWorkItems: [],
                watchers: [],
                userSelect: {},
                datePicker: {},
                progress: '',
                progressShow: '',
                testSteps: [],
                precondition: '',
                testCaseType: { id: '' },
                relatedWikis: [],
                attachments: [],
                hyperlinks: [],
            },
            editorInEditMode: { description: false },
            itemData: [],
            cycleVisible: false,
            priorities: [],
            rules: {
                name: [
                    { required: true, message: "请输入工作项名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 200,
                        message: "长度在2到200之间",
                        trigger: "blur",
                    },
                ],
                projectId: [
                    { required: true, message: "请选择所属项", trigger: "change" },
                ],
                trackerId: [
                    { required: true, message: "请选择工作项类型", trigger: "change" },
                ],
                'owner.id': [
                    { required: true, message: "请选择负责人", trigger: "change" },
                ],
                priority: [
                    { required: true, message: "请选择工作项优先级", placeholder: '请选择', trigger: "change" },
                ],
                "testCaseType.id": [
                    { required: true, message: "请选择测试用例类型", placeholder: '请选择', trigger: "change" },
                ]
            },
        };

    },
    props: {
        isShowDialog: {
            required: true
        },
        projectId: {
            required: true
        },
        tracker: {
            required: false
        },
    },
    computed: {
        title() {
            return '创建 ' + (this.trackerCopy.name || '工作项')
        },
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
        ...mapGetters("account", ["user"]),
    },
    watch: {
        tracker: {
            handler: function (newVal, oldVal) {
                this.trackerCopy = cloneDeep(newVal)
                this.loadData();
            }
        },
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal && this.tracker?.id) {
                    this.loading=false;
                    this.trackerCopy = cloneDeep(this.tracker)
                    this.loadData();
                    this.initData();
                }
            }
        },
    },
    mounted() {
        // this.loadData();
    },
    methods: {
        onDeleteWatcher(row) {
            this.formData.watchers = this.formData.watchers.filter(item => item.id != row.id);
        },
        priorityValidator: function (rule, value, callback) {
            console.log("priorityValidator", value)
            callback();
        },
        onChangeTracker(id) {
            findOneTracker(id).then(res => {
                this.trackerCopy = res;
                console.log("findONeTaracker", res)
            }).finally(() => {
                let name = this.formData.name;
                let sprintId = this.formData.sprintId;
                this.loadData().then(res => {
                    this.formData.name = name
                    this.formData.sprintId = sprintId
                })
                this.initData();

            })
        },
        handleSearch(val) {
            findTrackerItems(this.projectId, this.trackerCopy.id, null, {}, val, {}, []).then(resp => {
                this.itemData = [];
                console.log("items", resp)
                resp.content.forEach(res => {
                    this.itemData.push({ value: res.id, label: res.name })
                })
            }).finally(() => {

            })
        },
        handleChange(val) {
            findTrackerItems(this.projectId, this.trackerCopy.id, null, {}, val, {}, []).then(resp => {
                this.itemData = [];
                console.log("items", resp)
                resp.content.forEach(res => {
                    this.itemData.push({ value: res.id, label: res.name })
                })
            }).finally(() => {

            })
        },
        getShowToolBar(e) {
            return e;
        },
        OnFocusSimpleEditor(editorInEditMode, systemProperty) {
            editorInEditMode[systemProperty] = true;
            console.log("OnFocussimpleEditor", editorInEditMode, systemProperty, editorInEditMode[systemProperty])
        },
        disabledStartDate(startValue) {
            const endValue = this.formData.planEndDate;
            if (!startValue || !endValue) {
                return false;
            }
            return startValue.valueOf() > endValue.valueOf();
        },
        disabledEndDate(endValue) {
            const startValue = this.formData.planStart;
            if (!endValue || !startValue) {
                return false;
            }
            return startValue.valueOf() >= endValue.valueOf();
        },
        onClickProgress() {
            if (!this.formData.progress) {
                this.formData.progress = 0;
            }
            this.formData.progressShow = this.formData.progress + "%";
            this.cycleVisible = !this.cycleVisible
        },
        onClickCycle() {
            this.cycleVisible = !this.cycleVisible
        },
        onChangeProgress() {
            this.formData.progress = parseInt((this.formData.progress + '').replace(/[^\d\.]/g, ""));
            if (this.formData.progress > 100) {
                this.formData.progress = 100;
            } else if (isNaN(this.formData.progress) || !this.formData.progress) {
                this.formData.progress = 0
            }
        },
        onChangeEstimateHour() {
            this.formData.estimateWorkingHours = this.formData.estimateWorkingHours.replace(/[^\d\.]/g, "");
        },
        onDeleteItem(row) {
            for (let index in this.formData.relatedWorkItems) {
                if (row.id == this.formData.relatedWorkItems[index]) {
                    this.$delete(this.formData.relatedWorkItems, index);
                    break;
                }
            }
        },
        trackerItemSelector(selectedRows) {
            let items = [];
            selectedRows.forEach(item => {
                items.push({
                    id: item.id, name: item.name, owner: item.owner, status: item.status,
                    priority: { name: item.priority.name, color: item.priority.color, backgroundColor: item.priority.backgroundColor }
                })
            })
            this.formData.relatedWorkItems = items;
        },
        onSelectTrackerItem() {
            this.$refs.trackerItmeSlectModal.view();
        },
        initData() {
            let layout = this.findLayout()
            this.customerFields = []
            this.sections = []
            //fields
            if (layout) {
                this.sections = layout.sections
                const fields = layout.fields
                fields.forEach(f => {
                    let field = this.findFields(f.id)
                    if (field) {
                        this.customerFields.push(field)
                    }
                })
                this.markRequired(this.customerFields, this.getRequiredFields())
            }
        },
        onChangeDatePicker(systemProperty) {
            if (this.formData.datePicker[systemProperty]) {
                this.formData[systemProperty] = moment(this.formData.datePicker[systemProperty]).format("YYYY-MM-DD 00:00:00")
            } else {
                this.formData[systemProperty] = null;
            }
        },
        onChangeUserSelect(systemProperty) {
            if (this.formData.userSelect[systemProperty]) {
                this.formData[systemProperty] = { id: this.formData.userSelect[systemProperty] }
            } else {
                this.formData[systemProperty] = null;
            }
        },
        async loadData() {
            this.$nextTick(() => {
                if (this.$refs.itemForm) {
                    this.$refs.itemForm.clearValidate();
                    this.prepareFormData()
                }
                if (this.$refs["prioritySelectRef"] && this.$refs["prioritySelectRef"][0]) {
                    this.$refs["prioritySelectRef"][0].initSelect()
                }
            })
        },
        findLayout() {
            let layout = null;
            if (this.trackerCopy && this.trackerCopy.trackerLayouts && this.trackerCopy.trackerLayouts.length > 0) {
                layout = this.trackerCopy.trackerLayouts.filter(lay => { return lay.name === 'NEW' })[0]
            }
            if (!layout) {
                layout = { name: 'NEW', keyFields: [], fields: [], sections: [] }
                if (!this.tracker?.trackerLayouts) {
                    this.tracker.trackerLayouts = []
                }
                this.tracker.trackerLayouts.push(layout)
            }
            return layout;
        },
        findFields(fieldId) {
            if (this.trackerCopy && this.trackerCopy.trackerFields && this.trackerCopy.trackerFields.length > 0) {
                return this.trackerCopy.trackerFields.filter(f => { return f.id === fieldId })[0]
            }
            return null;
        },
        prepareFormData() {
            this.formData = {
                projectId: this.projectId,
                trackerId: this.trackerCopy.id,
                sprintId: undefined,
                name: '',
                description: undefined,
                values: {},
                owner: '',
                priority: undefined,
                relatedWorkItems: [],
                watchers: [],
                userSelect: {},
                datePicker: {},
                testSteps: [],
                precondition: '',
                testCaseType: { id: '' },
                relatedWikis: [],
                attachments: [],
                hyperlinks: [],
            }

            findProjectRolesAndMembers(this.projectId, 'SCOPE_TRACKER').then(resp => {
                let data = resp;
                const all = [].concat(data.projectRoles).concat(data.specialRoles).concat(data.members)
                const valueArray = ['工作项创建者', '工作项负责人']
                let tableData = all.filter(item => { return valueArray.indexOf(item.name) >= 0 })
                this.formData.watchers = tableData
                if (all.find(item => item.id == this.user.id)) {
                    this.formData.owner = this.user;
                } else {
                    this.formData.owner = { id: undefined }
                }
            });
        },
        getFirstInitStatus() {
            const statuses = this.trackerCopy.trackerStatuses || []
            for (let i = 0; i < statuses.length; i++) {
                if (statuses[i].initial) {
                    return statuses[i]
                }
            }
            return
        },
        getRequiredFields() {
            const fields = this.trackerCopy.trackerFields
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
        validate(callback) {
            this.$refs.itemForm.validate(valid => {
                if (valid) {
                    callback.call(this, true)
                }else{
                    this.loading=false;
                }
            });
        },
        getFormData() {
            let trackerItem = Object.assign({}, this.formData);
            for (let item of Object.keys(this.formData.userSelect)) {
                trackerItem[item] = { id: this.formData.userSelect[item] }
            }
            trackerItem.project = { id: this.projectId }
            trackerItem.tracker = { id: this.trackerCopy.id, name: this.trackerCopy.name }
            trackerItem.sprint = { id: this.formData.sprintId }
            trackerItem.priority = { id: this.formData.priority }
            delete trackerItem.projectId
            delete trackerItem.trackerId
            delete trackerItem.sprintId
            delete trackerItem.datePicker
            return trackerItem
        },
        onOK() {
            this.loading=true;
            this.validate(valid => {
                if (valid) {
                    let trackerItem = this.getFormData()
                    trackerItem.planEndDate = trackerItem.planEndDate?.format("YYYY-MM-DD HH:mm:ss")
                    trackerItem.planStartDate = trackerItem.planStartDate?.format("YYYY-MM-DD HH:mm:ss")
                    trackerItem.testSteps = trackerItem.testSteps.filter(item => item.description || item.expectedResult);
                    trackerItem.relatedWorkItems.forEach(v => {
                        v.targetItem = { id: v.targetItem.id }
                        v.linkType = { id: v.linkType.id.substring(1) }
                    })
                    this.$emit('ok', trackerItem)
                }
            })
        },
        onCancel() {
            this.$emit("cancel");
        },
        footerMethod: function ({ columns, data }) {
            return [
                columns.map(column => {
                    return null
                })
            ]
        },
    },
    created() { }
};
</script>
<style lang="less" scoped>
:deep(.ant-form-item) {
    margin-bottom: 8px;
}
</style>
