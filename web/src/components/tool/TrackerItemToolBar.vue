<template>
    <div style="height:100%;" class="ui-task-detail">
        <a-tabs :loading="loading" default-active-key="1" size="small" v-if="itemId&&trackerItem?.id">
            <a-tab-pane key="1">
                <span slot="tab">
                    <div style="display: flex;justify-content: center;">工作项详情</div>
                </span>
                <a-spin :spinning="loading">
                    <a-row>
                        <a-tooltip v-if="trackerItem?.trackerType?.icon" :title="'工作项类型：' + trackerItem.tracker?.name" :overlayStyle="{ fontSize: '10px' }">
                            <a-icon :style="{marginRight:'3px',color:trackerItem?.trackerType?.color,backgroundColor:trackerItem?.trackerType?.backgroundColor}"
                                :component="trackerItem?.trackerType?.icon" />
                        </a-tooltip>
                        <a-tooltip v-else-if="trackerItem?.icon" :title="'工作项类型：' + trackerItem.tracker?.name" :overlayStyle="{ fontSize: '10px' }">
                            <a-icon style="margin-right:3px"
                                    :component="trackerItem?.icon" />
                        </a-tooltip>
                        <span v-if="trackerItem?.itemNo" style="font-size:14px;">{{ currentProjectKeyName + '-' + trackerItem?.itemNo }}</span>
                    </a-row>
                    <a-layout-header v-if="tracker.id>0" class="ui-task-detail__header" theme="light">
                        <a-row class="ui-task-detail-header__summary">
                            <a-input :disabled="!hasItemEditPerm" class="task-basic-title" v-model="formData.name" @blur="changeTitle"
                                @pressEnter="changeTitle" />
                        </a-row>
                    </a-layout-header>

                    <div class="ui-task-detail__main" style="margin-top: -20px;background-color: #fff;">
                        <template v-for="item in sections">
                            <div v-if="item == 'DETAIL'" :key="item">
                                <div style="overflowX: hidden">
                                    <tracker-item-key-fields @refresh="refresh" :projectId="projectId" :keyFields="keyFields" 
                                        :trackerItem="trackerItem" :tracker="tracker" :disabled=!hasItemEditPerm />

                                    <div v-if="tracker.id<0" style="margin-top:10px;" class="task-detail-module task-desc">
                                        <a-form-model :layout="'horizontal'" labelAlign="left"  :model="formData" :rules="rules">
                                            <a-row :gutter="15">
                                                <a-col :span="24" :xs="48" v-for="(f) in customerFields" :key="f.id">
                                                    <a-form-model-item :label="f.name" :prop="f.name" :required="f.required">
                                                        <a-input v-if="f.systemProperty=='name'" v-model="formData.name" @blur="changeTitle"
                                                            :disabled="!hasItemEditPerm" @pressEnter="changeTitle" />

                                                        <div v-else-if="f.inputType=='WIKI'" class="task-desc-content">
                                                            <div
                                                                style="position: absolute; width: 0px; height: 0px; visibility: hidden; display: none;">
                                                            </div>
                                                            <div class="richtext-input ">
                                                                <div class="richtext-input-viewer-wrapper">
                                                                    <div class="richtext-editor">
                                                                        <div class="richtext-editor-content"
                                                                            :style="{ 'border': editorInEditMode ? 'solid 1px #5caff2' : '' }">
                                                                            <simple-editor ref="editor" :disabled="!hasItemEditPerm"
                                                                                v-if="trackerItem?.id==itemId&&!editorReadOnly" v-model="formData.description"
                                                                                :showToolbar="editorInEditMode" @click.native.capture="onClickDescription"/>
                                                                            <div style="border: 1px solid #ccc;height:202px" v-else></div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="task-desc-btns" v-show="editorInEditMode">
                                                                <div class="task-desc-btns-right">
                                                                    <vxe-button @click="cancelDescription">取消</vxe-button>
                                                                    <a-button type="primary" @click="changeDescription">保存</a-button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <a-input v-else-if="f.inputType=='TEXT'" @blur="onBlurTextField(f.id)" 
                                                            :disabled="!hasItemEditPerm" :placeholder="f.name" v-model="formData[f.systemProperty]"/>
                                                    </a-form-model-item>
                                                </a-col>
                                            </a-row>
                                        </a-form-model>
                                    </div>

                                    <template v-if="tracker.id>0">
                                        <div class="task-detail-module task-desc">
                                            <div class="task-detail-module-title">
                                                <div class="task-detail-module-title-text">描述</div>
                                            </div>
                                            <div class="task-desc-content">
                                                <div
                                                    style="position: absolute; width: 0px; height: 0px; visibility: hidden; display: none;">
                                                </div>
                                                <div class="richtext-input">
                                                    <div class="richtext-input-viewer-wrapper">
                                                        <div class="richtext-editor">
                                                            <div class="richtext-editor-content"
                                                                :style="{ 'border': editorInEditMode ? 'solid 1px #5caff2' : '' }">
                                                                <simple-editor ref="editor2"  :disabled="!hasItemEditPerm" v-model="formData.description"
                                                                    v-if="trackerItem?.id==itemId&&!editorReadOnly" :showToolbar="editorInEditMode" @click.native.capture="onClickDescription"/>
                                                                <div style="border: 1px solid #ccc;height:202px" v-else></div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="task-desc-btns" v-show="editorInEditMode">
                                                    <div class="task-desc-btns-right">
                                                        <vxe-button @click="cancelDescription">取消</vxe-button>
                                                        <a-button type="primary" @click="changeDescription">保存</a-button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div v-if="trackerItem?.id" class="task-detail-module task-desc">
                                            <div v-if="customerFields?.length>0" class="task-detail-module-title">
                                                <div class="task-detail-module-title-text">自定义属性</div>
                                            </div>
                                            <a-form-model :layout="'horizontal'" labelAlign="left" :labelCol="{ span: 8 }"
                                                :wrapperCol="{ span: 16 }" :model="formData" :rules="rules">
                                                <a-row :gutter="15">
                                                    <a-col :span="12" :xs="24" v-for="(f) in customerFields" :key="f.id">
                                                        <!-- {{void(perm=!hasTrackerItemCustomFieldPerm(f.id,customerFieldsPerm)||!hasItemEditPerm) }} -->
                                                        <a-form-model-item :label="f.name" :prop="f.name"
                                                            :required="f.required">
                                                            <ItemCustomFieldsShow :fields="f" v-model="formData.values[f.id]" :projectId="projectId" :trackerId="tracker.id"
                                                                :readOnly="!hasItemEditPerm" @change="onChangeCustomerField"></ItemCustomFieldsShow>
                                                        </a-form-model-item>
                                                    </a-col>
                                                </a-row>
                                            </a-form-model>
                                        </div>
                                                                
                                        <a-form-model :layout="'horizontal'" labelAlign="left" :labelCol="{ span: 8 }"
                                            :wrapperCol="{ span: 16 }">
                                            <a-row :gutter="15">
                                                <a-col :span="12" :xs="24">
                                                    <a-form-model-item label="所属项目">
                                                        {{ trackerItem?.project?.name }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12" :xs="24">
                                                    <a-form-model-item label="工作项类型">
                                                        {{ tracker?.name }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12" :xs="24">
                                                    <a-form-model-item label="创建者">
                                                        {{ trackerItem?.createBy?.name }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12" :xs="24">
                                                    <a-form-model-item label="创建日期">
                                                        {{ trackerItem?.createDate }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12" :xs="24">
                                                    <a-form-model-item label="更新者">
                                                        {{ trackerItem?.lastModifiedBy?.name }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12" :xs="24">
                                                    <a-form-model-item label="更新日期">
                                                        {{ trackerItem?.lastModifiedDate }}
                                                    </a-form-model-item>
                                                </a-col>
                                                <a-col :span="12" :xs="24">
                                                    <a-form-model-item ref="sprintId" label="所属迭代" prop="sprintId">
                                                            <TrackerItemFieldsShow :fields="getSystemField('sprintId')" :trackerItem="trackerItem" :projectId="projectId"
                                                                :readOnly="!hasItemEditPerm" @change="(e,v)=>onChangeSprintId(v)" />
                                                    </a-form-model-item>
                                                </a-col>
                                            </a-row>
                                        </a-form-model>
                                    </template>
                                </div>
                            </div>
                            
                            <div  v-else-if="item == 'CYCLE_PROGRESS'" :key="item" class="section-item-class">
                                <tracker-cycle-progress  :formData="formData" :itemId="itemId" 
                                    :tracker-item="trackerItem" @refresh="refresh" :isToolBar="true"></tracker-cycle-progress>
                            </div>

                            <div v-else-if="item == 'WORK_HOURS'" :key="item" class="section-item-class">
                                <tracker-work-hours  :currentTab="item" :formData="formData" :trackerItem="trackerItem" :itemId="itemId" 
                                    :projectId="projectId" @refresh="refresh" :isToolBar="true"></tracker-work-hours>
                            </div>

                            <div v-else-if="item == 'RELATED_ITEMS'" :key="item" class="section-item-class">
                                <tracker-related-item   :projectId="projectId" :trackerId="tracker.id " :itemId="itemId"  :isToolBar="true"
                                    :trackerItem='trackerItem' :relatedWorkItems="formData.relatedWorkItems" @refresh="refresh"></tracker-related-item>
                            </div>

                            <div v-else-if="item == 'RELATED_CODE'" :key="item" class="section-item-class">
                                <tracker-related-code  :tracker-item="trackerItem" :isShowDialog="true" :isToolBar="true"/>
                            </div>

                            <div v-else-if="item == 'RELATED_WIKI'" :key="item" class="section-item-class">
                                <tracker-related-wiki  :projectId="projectId" :tracker-item="trackerItem" :isToolBar="true"/>
                            </div>

                            <div  v-else-if="item == 'HYPERLINKS'" :key="item" class="section-item-class">
                                <tracker-hyperlinks  :tracker-item="trackerItem" :isShowDialog="true" :isToolBar="true"/>
                            </div>

                            <div v-else-if="item == 'ATTACHMENTS'" :key="item" class="section-item-class">
                                <tracker-attachment  :tracker-item="trackerItem" :isToolBar="true"/>
                            </div>
                            <div v-else-if="item == 'TEST_CASES'" :key="item">
                                <tracker-test-cases :formData="trackerItem" :isToolBar="true" :tracker-item="trackerItem" 
                                    :isShowDialog="item=='TEST_CASES'"></tracker-test-cases>
                            </div>
                            <div v-else-if="item == 'RELATED_TESTS'" :key="item">
                                <tracker-related-test  :projectId="projectId" :trackerId="tracker.id" :isToolBar="true" :itemId="itemId" @refresh="loadData"
                                    :isShowDialog="item=='RELATED_TESTS'" :tracker-item="trackerItem" />
                            </div>

                            <div v-else :key="item" :header="item">
                                <div ></div>
                            </div>
                        </template>
                    </div>
                </a-spin>
            </a-tab-pane>
            <a-tab-pane key="2">
                <span slot="tab">
                    <div style="display: flex;justify-content: center;">动态</div>
                </span>
                <diV style="margin-top:-15px;">
                    <tracker-comment :itemId="itemId" :projectId="projectId" :watchers="trackerItem?.watchers" @refresh="refresh"/></diV>
            </a-tab-pane>
        </a-tabs>
        <a-empty style="margin-top:300px;" v-else />


    </div>
</template>
<script>
import moment from 'moment'
import TrackerSelect from '@/components/select/TrackerSelect.vue';
import SprintSelect from '@/components/select/SprintSelect.vue';
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import SimpleEditor from '@/components/editor/SimpleEditor.vue';
import RoleMembersTable from '@/components/table/RoleMembersTable.vue';
import { mapGetters } from "vuex";
import { hasPermission } from '@/utils/permission'
import {  changeSystemField, changeCustomerField } from "@/services/tracker/TrackerItemService"
import TrackerComment from '@/pages/tracker/items/TrackerComment.vue';
import RegisterHourDialog from '@/pages/tracker/items/RegisterHourDialog.vue';
import TrackerAttachment from '@/pages/tracker/items/TrackerAttachment.vue';
import { Graph} from "@antv/x6";
import TrackerItemKeyFields from '@/components/select/TrackerItemKeyFields.vue';
import { findOneTracker} from "@/services/tracker/TrackerService";
import TrackerRelatedItem from '@/pages/tracker/items/TrackerRelatedItem.vue';
import TrackerRelatedWiki from '@/pages/tracker/items/TrackerRelatedWiki.vue';
import TrackerRelatedCode from '@/pages/tracker/items/TrackerRelatedCode.vue';
import TrackerWorkHours from '@/pages/tracker/items/TrackerWorkHours.vue';
import TrackerCycleProgress from '@/pages/tracker/items/TrackerCycleProgress.vue';
import TrackerHyperlinks from '@/pages/tracker/items/TrackerHyperlinks.vue';
import TrackerTestCases from '@/pages/tracker/items/TrackerTestCases.vue';
import TrackerRelatedTest from '@/pages/tracker/items/TrackerRelatedTest.vue';
import ItemCustomFieldsShow from '@/components/tool/ItemCustomFieldsShow.vue';
import TrackerItemFieldsShow from '@/components/tool/TrackerItemFieldsShow.vue';
import { conforms } from 'lodash';



export default {
    name: "TrackerItemToolBar",
    components: {
        TrackerSelect, SprintSelect, ProjectUserSelect, RoleMembersTable, SimpleEditor,
        TrackerComment, TrackerAttachment, Graph, RegisterHourDialog,TrackerItemKeyFields,
        TrackerRelatedWiki,TrackerRelatedCode,TrackerHyperlinks,TrackerRelatedItem,TrackerWorkHours,
        TrackerCycleProgress,TrackerTestCases,TrackerRelatedTest,ItemCustomFieldsShow,
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
                userSelect: {},
                datePicker: {},
                timePicker: {},
                progress: '',
                progressShow: '',
                planStart: '',
                planEnd: '',
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
            editorInEditMode: false,
            editorInEditModeList: [],
            customerFields: [],
            sections: [],
            keyFields: [],
            listFields: [],

            itemIdChildren: '',
            current: '',
            tracker: {},
            projectId:'',
            trackerId:'',
            hasItemEditPerm: false,
            editorReadOnly: false,
        };

    },
    props: {
        itemId: {
            required: true
        },
        trackerItem:{
            required: true
        },
        readOnly: Boolean,
    },
    computed: {
        ...mapGetters("account", ["user"]),
        ...mapGetters("project", ["currentProjectKeyName"]),
        pageId(){
            return this.$route.params.pageId
        },
        hasEditPerm(){
            return hasPermission("PAGE_WRITE",this.pageId);
        },
        title() {
            return '编辑 ' + this.tracker.name
        },
        stateTransitions() {
            const currentStatusId = this.trackerItem?.status?.id
            return this.tracker?.trackerStateTransitions?.filter(s => s.transitionFrom.id === currentStatusId)
        },
        getCollapseHeight() {
            if(this.sections?.length>1){
                return 720-this.sections.length*30;
            }
            return 770
        },
    },
    watch: {
        "trackerItem.id": {
            // immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loading=true
                    this.initItemPerm();
                    this.prepareFormData()
                    this.loadData();
                }
            }
        },
        "trackerItem.description": {
            handler: function(newVal,oldVal){
                if(newVal){
                    this.editorReadOnly=true
                    this.formData.description=newVal
                    setTimeout(() => {
                        this.editorReadOnly=false
                    }, 0);

                }
            }
        },
        // trackerItem:{
        //     handler: function(newVal){
        //         this.initItemPerm();
        //     }
        // },
        // itemId:{
        //     handler: function(newVal,oldVal){
        //         this.loading=true;
        //     }
        // },
    },
    beforeMount() {
    },
    mounted() {        
        this.projectId = this.$route.params.projectId
    },
    methods: {
        initItemPerm(){
            this.trackerId=this.trackerItem?.tracker?.id;
            // let writePerm=this.trackerItem?.permission?.includes("w")
            console.log(this.trackerItem)
            this.$set(this.trackerItem,"notPagePerm",!this.hasEditPerm||this.readOnly)
            if(this.hasEditPerm&&!this.readOnly){
                if(this.trackerId||this.trackerId<0){
                    this.hasItemEditPerm=true
                }
            }
        },
        onBlurTextField(fieldId){
            if(this.newValue){
                this.onChangeCustomerField(fieldId, this.newValue)
            }
        },

        onChangeSprintId(sprintId) {
            this.loadData()
            this.$set(this.trackerItem,"sprintId",sprintId)
            this.changeTrackerItem();
        },
        refresh(){
            this.$emit("refresh")
        },
        changeTrackerItem(){
            this.$emit('changeTrackerItem',this.trackerItem)
        },
        loadData() {
            this.loading = true
            if (this.itemId) {
                this.clearValidates();
                this.editorInEditMode = false
                if(((!this.tracker||this.tracker.id!=this.trackerId)&&this.trackerId)){
                    findOneTracker(this.trackerId).then(resp => {
                        this.tracker = resp;
                        this.tracker.keyFieldSmall=true
                        this.initFieldsData("DETAIL");
                        this.loading=false;
                        console.log("loadData", this.hasItemEditPerm)
                    })
                }else{
                    this.initFieldsData("DETAIL");
                    this.loading=false;
                }
            }else{
                this.loading=false;
            }

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
                        if (field.inputType == 'DATE' && this.trackerItem.values[field.id]) {
                            this.formData.datePicker[field.id] = moment(this.trackerItem.values[field.id],"YYYY-MM-DD");
                        } else if (field.inputType == "TIME" && this.trackerItem.values[field.id]) {
                            this.formData.timePicker[field.id] = moment(this.trackerItem.values[field.id],'HH:mm:ss');
                        }
                        if(!field.name){
                            field.name=f.name;
                        }
                        this.customerFields.push(field)
                    }
                })
                //keyFields
                this.keyFields = [];
                const keyfields = layout.keyFields||[]
                keyfields.forEach(f => {
                    let field = this.findFields(f.id)
                    if (field) {
                        this.keyFields.push(field)
                    }
                })
                this.markRequired(this.customerFields, this.getRequiredFields())
            }
        },
        findLayout(layoutName) {
            let layout = null;
            if (this.tracker && this.tracker.trackerLayouts && this.tracker.trackerLayouts?.length > 0) {
                layout = this.tracker.trackerLayouts.filter(lay => { return lay.name === layoutName })[0]
            }
            if(!layout){
                layout = {name:layoutName,keyFields:[],fields:[],sections:[]}
                this.tracker?.trackerLayouts?.push(layout)
            }
            return layout;
        },
        findFields(fieldId) {
            if (this.tracker && this.tracker.trackerFields && this.tracker.trackerFields?.length > 0) {
                return this.tracker.trackerFields.filter(f => { return f.id === fieldId })[0]
            }
            return null;
        },
        getSystemField(systemProperty){
            return this.tracker?.trackerFields?.find(f=>
                f.system&&f.systemProperty===systemProperty);
        },
        prepareFormData() {
            this.formData = {
                projectId: this.projectId,
                trackerId: this.trackerItem.trackerId,
                sprintId: this.trackerItem.sprint?.id,
                name: this.trackerItem.name,
                description: this.trackerItem.description,
                values: this.trackerItem.values,
                owner: this.trackerItem.owner,
                priority: this.trackerItem.priority,
                relatedWorkItems: this.trackerItem.relatedWorkItems,
                relatedWikis: this.trackerItem.relatedWikis,
                watchers: this.trackerItem.watchers,
                values: this.trackerItem.values,
                userSelect: {},
                datePicker: {},
                timePicker: {},
                planStart: moment(this.trackerItem.planStartDate),
                planEnd: moment(this.trackerItem.planEndDate),
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
            const fields = this.tracker?.trackerFields
            const firstStatus = this.getCurrentStatus()
            let requiredFields = []
            fields?.forEach(f => {
                if (!f.system && f.mandatory) {
                    let found = false
                    for (let i = 0; i < f.exceptStatus?.length; i++) {
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
            if (e.target.value != this.trackerItem.name) {
                changeSystemField(this.itemId, 'name', e.target.value).then(resp => {
                    this.$set(this.trackerItem,"name",e.target.value)
                    this.changeTrackerItem();
                    this.loadData()
                })
            }
        },
        onClickDescription(){
            if(this.hasItemEditPerm){
                this.editorInEditMode=true;
            }
        },
        cancelDescription() {
            this.editorInEditMode = false
            this.$nextTick(() => {
                if(this.$refs.editor&&this.$refs.editor[0]){
                    this.$refs.editor[0].blur()
                }
                if(this.$refs.editor2&&this.$refs.editor2[0]){
                    this.$refs.editor2[0].blur()
                }
            })
        
            this.formData.description = this.trackerItem.description
        },
        changeDescription() {
            changeSystemField(this.itemId, 'description', this.formData.description).then(resp => {
                this.editorInEditMode = false
                this.$set(this.trackerItem,"description",this.formData.description)
                this.changeTrackerItem();
                this.loadData()
            })
        },


        onChangeCustomerField(id, value) {
            changeCustomerField(this.itemId, id, value).then(resp => {
                this.trackerItem.values[id]=value
                this.changeTrackerItem();
                this.loadData()
            })
        },
        onSearchOwner() {

        },


        onClose() {
            this.formData.description = undefined
            this.graph = null
            this.$emit('cancel')
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

.ui-task-detail {
    position: relative;
    height:100%;
    overflow:auto;
    padding:0px;

    .ui-task-detail__header {
        border-bottom: 1px solid #f0f0f0;
        border-radius: 4px 4px 0 0;
        background: #fff;
        height: 60px;
        padding: 0;
        margin-top: 10px;

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
                padding-left: 5px;
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
    .task-desc {    
        z-index: 0;
        padding-top: 20px;
    }

    .task-detail-module {

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

            .task-detail-module-title-text {
                flex: 1 1 auto;
                padding:10px 0;
            }
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
.section-item-class{
    border-top: 1px solid rgb(232, 232, 232);
    padding-top: 15px;
    padding-bottom: 20px;
}
</style>
