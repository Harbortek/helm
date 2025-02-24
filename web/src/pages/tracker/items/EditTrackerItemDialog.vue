<template>
    <a-modal v-model="visiable" v-if="visiable" :closable="true" :footer="null" :centered="true" :destroyOnClose="true" :width="1200"
        :bodyStyle="{ padding: '0px', height: '90vh' }" :dialog-style="{height:'99%'}" :maskClosable='false' @cancel="onClose">
        <template slot="title">
            <div style="float:right;margin-right:30px;">
                <a-popover placement="bottomRight" trigger="['click']]" v-model="moreMenuVisible">
                    <template slot="content">
                        <div class="ui-table" style="overflow:auto">
                            <div style="height:30px;margin-right:10px;">
                                <vxe-button @click="onClickDelete" type="text" content="删除"></vxe-button>
                            </div>
                        </div>
                    </template>
                    <vxe-button type="text" icon="vxe-icon-ellipsis-h"></vxe-button>
                </a-popover>
            </div>


            <div class="ui-task-detail-header__topbar ui-task-detail-header__topbar--without-bottom">
                <div class="ui-task-detail-header__nav">
                    <div class="task-basic-left">
                        <div>
                            <span
                                v-for="item in this.trackerItem?.relatedWorkItems?.filter(e => e.sourceItem.id == trackerItem.id)"
                                :key="item.Id" style="font-size:13px;color: #5d6369;margin-right:5px;">{{
                                    currentProjectKeyName + '-' + item.targetItem?.itemNo }}</span>
                        </div>
                        <div style="width:100%">
                            <a-icon v-if="trackerItem?.relatedWorkItems.length > 0" style="margin-right: 3px;"
                                type="arrow-up" />
                            <a-icon v-if="trackerItem?.tracker?.icon" style="margin-right:3px"
                                :component="trackerItem?.tracker?.icon" />
                            <span style="font-size:17px;">{{ currentProjectKeyName + '-' + trackerItem?.itemNo }}</span>
                            <!-- <a-input v-model="formData.itemNo"
                                style="width:10%;border:none;font-size:18px;color: rgba(0, 0, 0, 0.85);"
                                @blur="changeItemNo" @pressEnter="changeItemNo" /> -->
                            <a-input v-if="formData?.name" style="width:90%;border:none;font-size:18px;color: rgba(0, 0, 0, 0.85);"
                                v-model="formData.name" @blur="changeTitle" @pressEnter="changeTitle" />
                        </div>
                        <div style="margin-left:20px;"
                            v-if="trackerItem?.relatedWorkItems?.filter(e => e.targetItem.id == trackerItem.id).length > 0">
                            <a-icon style="margin-right: 3px;" type="arrow-up" />
                            <span
                                v-for="item in trackerItem?.relatedWorkItems?.filter(e => e.targetItem.id == trackerItem.id)"
                                :key="item.Id" style="font-size:13px;color: #5d6369;margin-right:5px;">{{
                                    currentProjectKeyName + '-' + item.sourceItem?.itemNo }}&nbsp;&nbsp;</span>
                        </div>
                    </div>
                </div>
            </div>
        </template>
        <a-layout v-if="trackerItem&&formData" class="ui-task-detail">
            <a-layout-header class="ui-task-detail__header" theme="light">
                <!-- <a-row class="ui-task-detail-header__summary">
                    <a-input class="task-basic-title" v-model="formData.name" @blur="changeTitle"
                        @pressEnter="changeTitle" />
                </a-row> -->
                <a-row>
                    <tracker-item-key-fields @refresh="loadData" :projectId="projectId" :keyFields="keyFields"
                        :trackerItem="trackerItem" :tracker="tracker"></tracker-item-key-fields>
                </a-row>
            </a-layout-header>
            <a-layout class="ui-task-detail__container">
                <a-layout-content class="ui-task-detail__main">
                    <div class="ui-task-detail__tabs">
                        <a-tabs v-model="currentTab" @change="onChangeTabs" default-active-key="DETAIL">
                            <!-- <a-tab-pane v-for="item in sections" :key="item.value" :tab="item.name"></a-tab-pane> -->
                            <template v-for="item in sections">
                                <a-tab-pane v-if="item == 'DETAIL'" :key="item" tab="详情"></a-tab-pane>
                                <!-- <a-tab-pane v-else-if="item == 'WORK_ITEMS'" :key="item" tab="子工作项">
                                </a-tab-pane> -->
                                <a-tab-pane v-else-if="item == 'CYCLE_PROGRESS'" :key="item" tab="周期与进度">
                                </a-tab-pane>
                                <a-tab-pane v-else-if="item == 'WORK_HOURS'" :key="item" tab="工时">
                                </a-tab-pane>
                                <a-tab-pane v-else-if="item == 'TEST_CASES'" :key="item" tab="测试情况">
                                </a-tab-pane>
                                <a-tab-pane v-else-if="item == 'RELATED_ITEMS'" :key="item" tab="关联工作项">
                                </a-tab-pane>
                                <a-tab-pane v-else-if="item == 'RELATED_CODE'" :key="item" tab="代码关联">
                                </a-tab-pane>
                                <a-tab-pane v-else-if="item == 'RELATED_WIKI'" :key="item" tab="关联Wiki文档">
                                </a-tab-pane>
                                <a-tab-pane v-else-if="item == 'ATTACHMENTS'" :key="item" tab="文件">
                                </a-tab-pane>
                                <a-tab-pane v-else-if="item == 'HYPERLINKS'" :key="item" tab="链接">
                                </a-tab-pane>
                                <a-tab-pane v-else-if="item == 'RELATED_TESTS'" :key="item" tab="关联测试结果">
                                </a-tab-pane>
                                <!-- <a-tab-pane v-else :key="item" :tab="item">
                                </a-tab-pane> -->
                            </template>
                        </a-tabs>
                        <div class="" style="flex: 1 1 0%; min-height: 0px;">
                            <div style="height: 100%; overflow: hidden auto;">
                                <div class="task-detail-module task-desc" v-show="currentTab === 'DETAIL'">
                                    <div class="task-detail-module-title">
                                        <div class="task-detail-module-title-text">描述</div>
                                    </div>
                                    <div class="task-desc-content">
                                        <div
                                            style="position: absolute; width: 0px; height: 0px; visibility: hidden; display: none;">
                                        </div>
                                        <div class="richtext-input ">
                                            <div class="richtext-input-viewer-wrapper">
                                                <div class="richtext-editor">
                                                    <div class="richtext-editor-content"
                                                        :style="{ 'border': editorInEditMode ? 'solid 1px #5caff2' : '' }">
                                                        <simple-editor v-if="formData" ref="editor"
                                                            v-model="formData.description"
                                                            :showToolbar="editorInEditMode"
                                                            @focus="editorInEditMode = true"
                                                            @click.native.capture="editorInEditMode = true" />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="task-desc-btns" v-show="editorInEditMode">
                                            <div class="task-desc-btns-right">
                                                <a-button @click="cancelDescription">取消</a-button>
                                                <a-button type="primary" @click="changeDescription">保存</a-button>
                                            </div>
                                        </div>
                                    </div>
                                    <div v-if="customerFields.length > 0" class="task-detail-module task-desc">
                                        <div class="task-detail-module-title">
                                            <div class="task-detail-module-title-text">自定义属性</div>
                                        </div>
                                        <a-form-model :layout="'horizontal'" :model="formData" :rules="rules">
                                            <a-row :gutter="15">
                                                <a-col :span="isTable(f) ? 24 : 12" v-for="(f) in customerFields"
                                                    :key="f.id">
                                                    <a-form-model-item :label="f.name" :prop="f.name"
                                                        :required="f.required" :labelCol="{ span: isTable(f) ? 4 : 8 }"
                                                        :wrapperCol="{ span: isTable(f) ? 20 : 16 }">
                                                        <TrackerItemFieldsShow :fields="f" :trackerItem="formData" :projectId="projectId" :trackerId="tracker.id"
                                                            :readOnly="false" @change="loadData()" />
                                                            <!-- <ItemCustomFieldsShow :fields="f" v-model="formData.values[f.id]" :projectId="projectId" :trackerId="tracker.id"
                                                                :readOnly="false" @change="onChangeCustomerField"></ItemCustomFieldsShow> -->
                                                    </a-form-model-item>
                                                </a-col>
                                            </a-row>
                                        </a-form-model>
                                    </div>

                                    <div class="task-detail-module task-desc">
                                        <div class="task-detail-module-title">
                                            <div class="task-detail-module-title-text">基础属性</div>
                                        </div>
                                        <a-form-model :layout="'horizontal'" :labelCol="{ span: 8 }"
                                            :wrapperCol="{ span: 16 }">
                                            <a-row :gutter="15">
                                                <a-col :span="12">
                                                    <a-form-model-item label="所属项目">
                                                        {{ trackerItem?.project?.name }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12">
                                                    <a-form-model-item label="工作项类型">
                                                        {{ tracker?.name }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12">
                                                    <a-form-model-item label="创建者">
                                                        {{ trackerItem?.createBy.name }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12">
                                                    <a-form-model-item label="创建日期">
                                                        {{ trackerItem?.createDate }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12">
                                                    <a-form-model-item label="更新者">
                                                        {{ trackerItem?.lastModifiedBy.name }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12">
                                                    <a-form-model-item label="更新日期">
                                                        {{ trackerItem?.lastModifiedDate }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12">
                                                    <a-form-model-item v-if="trackerItem" ref="sprintId" label="所属迭代" prop="sprintId">
                                                        <TrackerItemFieldsShow :fields="getSystemField('sprintId')" :trackerItem="trackerItem" :projectId="projectId"
                                                            :readOnly="false" @change="loadData()" />
                                                    </a-form-model-item>
                                                </a-col>
                                            </a-row>
                                        </a-form-model>
                                    </div>
                                </div>

                                <div v-show="currentTab === 'RELATED_ITEMS'">
                                    <tracker-related-item :projectId="projectId" :tracker-item="trackerItem"
                                        :trackerId="tracker.id" :itemId="itemId"
                                        :relatedWorkItems="formData?.relatedWorkItems" @refresh="loadData" />
                                </div>

                                <div v-show="currentTab === 'RELATED_WIKI'">
                                    <tracker-related-wiki :projectId="projectId" :tracker-item="trackerItem"
                                        @loadComment="loadComment" />
                                </div>

                                <div v-show="currentTab === 'RELATED_CODE'">
                                    <tracker-related-code :tracker-item="trackerItem"
                                        :isShowDialog="currentTab === 'RELATED_CODE'" />
                                </div>

                                <div v-show="currentTab === 'CYCLE_PROGRESS'">
                                    <tracker-cycle-progress :formData="formData" :itemId="itemId"
                                        :tracker-item="trackerItem" @refresh="loadData"></tracker-cycle-progress>
                                </div>
                                <div v-show="currentTab === 'WORK_HOURS'">
                                    <tracker-work-hours :currentTab="currentTab" v-if="formData" :formData="formData"
                                        :trackerItem="trackerItem" :itemId="itemId" :projectId="projectId"
                                        @refresh="loadData"></tracker-work-hours>
                                </div>
                                <div v-show="currentTab === 'ATTACHMENTS'">
                                    <tracker-attachment :tracker-item="trackerItem" @loadComment="loadComment" />
                                </div>

                                <div v-show="currentTab === 'HYPERLINKS'">
                                    <tracker-hyperlinks :tracker-item="trackerItem"
                                        :isShowDialog="currentTab === 'HYPERLINKS'" @loadComment="loadComment" />
                                </div>

                                <div v-if="currentTab === 'TEST_CASES'">
                                    <tracker-test-cases style="margin-left:10px;" :trackerItem="trackerItem"
                                        :formData="trackerItem" :isShowDialog="currentTab == 'TEST_CASES'"
                                        @loadComment="loadComment"></tracker-test-cases>
                                </div>
                                <div v-show="currentTab === 'RELATED_TESTS'">
                                    <tracker-related-test :projectId="projectId" :trackerId="tracker.id"
                                        :itemId="itemId" @refresh="loadData"
                                        :isShowDialog="currentTab == 'RELATED_TESTS'" :trackerItem="trackerItem" />
                                </div>
                            </div>
                        </div>
                    </div>
                </a-layout-content>
                <a-layout-sider class="ui-task-detail__side" theme="light" width="340">
                    <tracker-comment ref="commentRef" :projectId="projectId" :itemId="itemId" :trackerItem="trackerItem"
                        :watchers="trackerItem?.watchers" @refresh="loadData" title="动态" />
                </a-layout-sider>
            </a-layout>
        </a-layout>
    </a-modal>
</template>
<script>
import moment from 'moment'
import _ from 'lodash'
import VXETable from "vxe-table";
import TrackerSelect from '../../../components/select/TrackerSelect.vue';
import SprintSelect from '../../../components/select/SprintSelect.vue';
import ProjectUserSelect from '../../../components/select/ProjectUserSelect.vue';
import SimpleEditor from '../../../components/editor/SimpleEditor.vue';
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
import ItemCustomFieldsShow from '../../../components/tool/ItemCustomFieldsShow.vue';
import TrackerItemFieldsShow from '../../../components/tool/TrackerItemFieldsShow.vue';
import { mapGetters, mapState, mapMutations } from "vuex";
import { hasPermission } from '@/utils/permission'
import {
    findOneTrackerItem, changeSystemField, changeCustomerField, deleteTrackerItem
} from "@/services/tracker/TrackerItemService"
import TrackerComment from './TrackerComment.vue';
import RegisterHourDialog from './RegisterHourDialog.vue';
import TrackerAttachment from './TrackerAttachment.vue';
import TrackerRelatedWiki from './TrackerRelatedWiki.vue';
import TrackerRelatedItem from './TrackerRelatedItem.vue';
import TrackerRelatedCode from './TrackerRelatedCode.vue';
import TrackerWorkHours from './TrackerWorkHours.vue';
import TrackerCycleProgress from './TrackerCycleProgress.vue';
import TrackerHyperlinks from './TrackerHyperlinks.vue';
import TrackerTestCases from './TrackerTestCases.vue';
import TrackerRelatedTest from './TrackerRelatedTest.vue';
import { Graph, Vector } from "@antv/x6";
import TrackerItemKeyFields from '../../../components/select/TrackerItemKeyFields.vue';


export default {
    name: "EditTrackerItemDialog",
    components: {
        TrackerSelect, SprintSelect, ProjectUserSelect, RoleMembersTable, SimpleEditor, TrackerComment, TrackerAttachment,
        TrackerRelatedWiki, Graph, RegisterHourDialog, TrackerItemKeyFields, TrackerRelatedItem, TrackerWorkHours,
        TrackerCycleProgress, TrackerRelatedCode, TrackerHyperlinks, TrackerTestCases, TrackerRelatedTest, ItemCustomFieldsShow,
        TrackerItemFieldsShow,
    },
    data() {
        return {
            loading: false,
            formData: {
                name: '',
                projectId: '',
                trackerId: '',
                values: [],
                owner: { id: '' },
                priority: '',
                relatedWorkItems: [],
                watchers: [],
                datePicker: {},
                timePicker: [],
                progress: '',
                progressShow: '',
                planStartDate: '',
                planEndDate: '',
                estimateHour: 0,
                remainHour: 0,
                registeredHour: 0,
            },
            rules: {
                name: [
                    { required: true, message: "请输入工作项名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                ],
                projectId: [
                    { required: true, message: "请选择所属项", trigger: "blur" },
                ],
                trackerId: [
                    { required: true, message: "请选择工作项类型", trigger: "blur" },
                ]
            },
            rulesHours: {
                actualHour: [{ required: true, validator: this.actualHourValidator, trigger: ['blur', 'change'] }],
            },
            trackerItem: null,
            editorInEditMode: false,
            currentTab: 'DETAIL',
            customerFields: [],
            sections: [],
            keyFields: [],
            listFields: [],
            moreMenuVisible: false,
        };

    },
    props: {
        isShowDialog: {
            required: true
        },
        projectId: {
            required: true
        },
        itemId: {
            required: true
        },
        tracker: {
            required: true
        },
    },
    computed: {
        ...mapGetters("project", ["currentProjectKeyName"]),
        title() {
            return '编辑 ' + this.tracker.name
        },
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
        stateTransitions() {
            const currentStatusId = this.trackerItem?.status?.id
            return this.tracker?.trackerStateTransitions?.filter(s => s.transitionFrom.id === currentStatusId)
        },
        ...mapGetters("account", ["user"]),
    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.initData();
                    this.loadData(true);
                }
            }
        },
    },
    beforeMount() {
    },
    mounted() {

    },
    methods: {
        onChangeTabs(e) {

        },
        initData() {

        },
        onClickDelete() {
            VXETable.modal.confirm({
                title: '删除' + this.tracker?.name,
                content: '删除' + this.tracker?.name + '「' + this.trackerItem?.name + '」，此操作不可撤销，是否确认?'
            }).then(type => {
                if (type === 'confirm') {
                    deleteTrackerItem(this.itemId).then(() => {
                        VXETable.modal.message({ content: '删除成功', status: 'success' })
                        this.onClose();
                    })
                }
            })
        },

        //
        loadData(isFirst) {
            if (this.itemId) {
                this.clearValidates();
                this.editorInEditMode = false
                findOneTrackerItem(this.itemId).then(resp => {
                    this.trackerItem = resp;
                    console.log("loadData", resp)
                }).catch(e=>{
                    this.onClose();
                }).finally(() => {
                    this.prepareFormData()
                    this.initFieldsData("DETAIL");
                    if (!isFirst) {
                        this.loadComment();
                    }
                })
            }

        },
        loadComment() {
            this.$refs['commentRef'].loadData()
        },
        initFieldsData(layoutName) {
            let layout = this.findLayout(layoutName)
            //listFields
            this.listFields = this.findLayout("LIST")?.fields
            if (layout) {
                const fields = layout.fields
                this.sections = layout.sections//模块标签页
                this.customerFields = []
                fields.forEach(f => {
                    let field = this.findFields(f.id)
                    if (field) {
                        this.customerFields.push(field)
                    }
                })
                //keyFields
                this.keyFields = [];
                const keyfields = layout.keyFields
                keyfields.forEach(f => {
                    let field = this.findFields(f.id)
                    if (field) {
                        this.keyFields.push(field)
                    }
                })
                this.markRequired(this.customerFields, this.getRequiredFields())
            }
            console.log("customerFields",this.customerFields)
        },

        findLayout(layoutName) {
            let layout = null;
            if (this.tracker && this.tracker.trackerLayouts && this.tracker.trackerLayouts.length > 0) {
                layout = this.tracker.trackerLayouts.filter(lay => { return lay.name === layoutName })[0]
            }
            if (!layout) {
                layout = { name: layoutName, keyFields: [], fields: [], sections: [] }
                if (!this.tracker.trackerLayouts) {
                    this.tracker.trackerLayouts = [];
                }
                this.tracker.trackerLayouts.push(layout)
            }
            return layout;
        },
        findFields(fieldId) {
            if (this.tracker?.trackerFields?.length > 0) {
                return this.tracker.trackerFields.find(f => f?.id === fieldId)
            }
            return null;
        },
        getSystemField(systemProperty){
            return this.tracker?.trackerFields?.find(f=>
                f.system&&f.systemProperty===systemProperty);
        },
        prepareFormData() {
            console.log("values", this.trackerItem.values)
            this.formData = {
                projectId: this.projectId,
                trackerId: this.trackerItem.trackerId,
                sprintId: this.trackerItem.sprint?.id,
                itemNo: this.trackerItem.itemNo,
                name: this.trackerItem?.name,
                description: this.trackerItem.description,
                values: this.trackerItem.values,
                owner: this.trackerItem.owner,
                priority: this.trackerItem.priority,
                relatedWorkItems: this.trackerItem.relatedWorkItems,
                watchers: this.trackerItem.watchers,
                datePicker: {},
                timePicker: {},
                planStartDate: this.trackerItem.planStartDate ? moment(this.trackerItem.planStartDate) : undefined,
                planEndDate: this.trackerItem.planEndDate ? moment(this.trackerItem.planEndDate) : undefined,
                progressShow: this.trackerItem.progress || 0 + '%',
                progress: this.trackerItem.progress || 0,
                estimateHour: this.trackerItem.estimateWorkingHours || 0,
                remainHour: this.trackerItem.remainingWorkingHours || 0,
                registeredHour: this.trackerItem.registeredWorkingHours || 0,
            }
            this.formData.relatedWorkItems?.forEach(item => {
                if (item.sourceItem && item.sourceItem.id === this.trackerItem.id) {
                    item.linkType.id = 'S' + item.linkType.id;
                } else {
                    item.linkType.id = 'T' + item.linkType.id;
                }
            })
        },
        clearValidates() {
            this.$nextTick(() => {
                if (this.$refs.itemForm) {
                    this.$refs.itemForm.clearValidate();
                }
            })
        },
        getCurrentStatus() {
            return this.trackerItem?.status
        },
        getRequiredFields() {
            const fields = this.tracker.trackerFields
            const firstStatus = this.getCurrentStatus()
            let requiredFields = []
            fields.forEach(f => {
                if (!f.system && f.mandatory) {
                    let found = false
                    for (let i = 0; i < f.exceptStatus.length; i++) {
                        if (f.exceptStatus[i]?.id === firstStatus?.id) {
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
                }
            });
        },
        getFormData() {
            let trackerItem = {
                projectId: this.projectId,
                trackerId: this.tracker.id,
                values: this.formData.values,
                name: this.formData.name,
                description: this.formData.description,
                owner: this.formData.owner,
                priority: { id: this.formData.priority },
                watchers: this.formData.watchers,
                relatedWorkItems: this.formData.relatedWorkItems,
            }
            return trackerItem
        },
        changeTitle(e) {
            if (e.target.value != this.trackerItem?.name) {
                changeSystemField(this.itemId, 'name', e.target.value).then(resp => {
                
                }).finally(()=>{
                    this.loadData()
                })
            }
        },
        changeItemNo(e) {
            if (e.target.value != this.trackerItem?.name) {
                changeSystemField(this.itemId, 'itemNo', e.target.value).then(resp => {
                
                }).finally(()=>{
                    this.loadData()
                })
            }
        },
        cancelDescription() {
            this.editorInEditMode = false
            this.$nextTick(() => {
                this.$refs.editor.blur()
            })
            this.formData.description = this.trackerItem.description
        },
        changeDescription() {
            changeSystemField(this.itemId, 'description', this.formData.description).then(resp => {
                
            }).finally(()=>{
                this.editorInEditMode = false
                this.loadData()
            })
        },


        onChangeCustomerField(fieldId, value) {
            // console.log("customFChange",fieldId, value,this.formData.values[fieldId])
            changeCustomerField(this.itemId, fieldId, value).then(resp => {
                this.loadData()
            }).finally(() => {
                // let field = this.customerFields.find(f => f.id === fieldId)
                // if(field.inputType=="TEST_STEP"){
                //     this.$emit("updateTestStep",value)
                // }
            })
        },
        onSearchOwner() {

        },
        onClose() {
            this.formData.description = undefined
            this.formData.sprintId = undefined;
            this.trackerItem = null
            this.formData = null
            this.currentTab = 'DETAIL'
            this.$emit('cancel')
        },

        isTable(f) {
            return f.inputType === 'TABLE' || f.inputType === 'TEST_STEP' || f.inputType === 'DURATION'
        },
    },
    created() { }
};
</script>
<style lang="less" scoped>
:global(.ant-form-item) {
    margin-bottom: 8px;
}

.ui-task-detail-header__topbar {
    .task-basic-left {
        flex: 1 1 auto;
        display: flex;
        min-width: 0;
        flex-direction: column;
        align-items: flex-start;

        .task-icon {
            line-height: 1;
        }

        .task-basic-task-number {
            flex: 0 0 auto;
            line-height: 1;
            cursor: pointer;
            transition: color .2s;
            color: #303030;
            margin-left: 10px;
        }
    }
}

.ui-task-detail {
    height: 100%;
    display: flex;
    flex-flow: column;
    padding-bottom: 0px;
    border-left: 0;
    flex: 1 1 auto;
    position: relative;
    width: 100%;
    max-width: 100%;

    .ui-task-detail__header {
        border-bottom: 1px solid #f0f0f0;
        border-radius: 4px 4px 0 0;
        background: #fff;
        height: auto;
        padding: 0 10px;

        .ui-task-detail-header__summary {
            margin-bottom: 10px;
            overflow: hidden;
            line-height: 32px;

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
    }

    .ui-task-detail__container {
        height: 100%;
        background-color: #fff;
    }
}

.ui-task-detail__main {
    margin-right: 0;
    border-right: 1px solid #e8e8e8;
    box-shadow: none;

    .ui-task-detail__tabs {
        padding: 0 10px;
        height: 100%;
    }

    .task-desc {
        z-index: 0;
        // padding-top: 20px;
    }

    .task-detail-module {
        margin: 0 20px 0;
        // padding-top: 20px;

        .ant-list-empty-text {
            padding: 0;
            border-bottom: 1px solid rgb(232, 232, 232);
        }

        .task-detail-module-title {
            font-size: 15px;
            color: #303030;
            font-weight: 500;
            display: flex;
            align-items: center;
            justify-content: space-between;


        }

        .task-desc-content {
            position: relative;

            .task-desc .task-desc-fullscreen-btn {
                display: flex;
                justify-content: flex-end;
                margin-top: -20px;
            }

            .richtext-input .richtext-input-editor-wrapper {
                position: relative;
                height: 100%;
            }

            .task-desc-btns {
                display: flex;
                justify-content: flex-end;
                margin-top: 10px;
            }
        }
    }
}

.ui-task-detail__side {
    min-width: 240px;
    max-width: 460px;
}

.ant-input:focus {
    /* 去除获取焦点时的样式 */
    border: none;
    box-shadow: none;
}
</style>
