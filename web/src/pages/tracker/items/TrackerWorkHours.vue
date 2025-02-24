<template>
    <div class="task-detail-module task-desc" :style="{margin: !isToolBar?'0 20px 0':''}">
        <div class="task-detail-module-title">
            <div class="task-detail-module-title-text">工时信息</div>
        </div>
        <a-form-model :layout="'horizontal'" :labelCol="{ span: 8 }"
            :wrapperCol="{ span: 16 }" labelAlign="right" :model="formData">
            <a-row :gutter="15" style="border-bottom: 1px solid #e8e8e8;">
                <a-col :span="12" :xs="!isToolBar?'':24">
                    <a-form-model-item placeholder="未设置(单位:小时)">
                        <template slot="label">
                            预计工时&nbsp;
                            <a-tooltip placement="bottom">
                                <template slot="title">
                                    <div style="font-size:8px;">
                                        用于预估完成该工作项需要花费的总工时
                                    </div>
                                </template>
                                <span><a-icon type="question-circle-o" /></span>
                            </a-tooltip>
                        </template>
                        <a-input-number :default-value="0" style="width:100%"
                            :disabled="trackerItem?.notPagePerm"
                            v-model="formData.estimateHour"
                            :formatter="value => `${value}小时`"
                            :parser="value => value.replace('小时', '')"
                            @blur="onBlurEstimateHour"
                            @change="onChangeEstimateHour" />
                    </a-form-model-item>
                </a-col>
                <a-col :span="12" :xs="!isToolBar?'':24">
                    <a-form-model-item placeholder="未设置(单位:小时)">
                        <template slot="label">
                            已登记工时&nbsp;
                            <a-tooltip placement="bottom">
                                <template slot="title">
                                    <div style="font-size:8px;">
                                        用于记录成员在该工作项下实际花费的工时
                                    </div>
                                </template>
                                <span><a-icon type="question-circle-o" /></span>
                            </a-tooltip>
                        </template>
                        <span style="margin-left:10px">{{ formData.registeredHour
                        }}小时</span>
                    </a-form-model-item>
                </a-col>
                <a-col :span="12" :xs="!isToolBar?'':24">
                    <a-form-model-item placeholder="未设置(单位:小时)">
                        <template slot="label">
                            剩余工时&nbsp;
                            <a-tooltip placement="bottom">
                                <template slot="title">
                                    <div style="font-size:8px;">
                                        用于表示当前继续完成该工作项还需要花费的工时
                                    </div>
                                </template>
                                <span><a-icon type="question-circle-o" /></span>
                            </a-tooltip>
                        </template>
                        <a-input-number :default-value="0" style="width:100%"
                            :disabled="trackerItem?.notPagePerm"
                            v-model="formData.remainHour" :formatter="value => `${value}小时`"
                            :parser="value => value.replace('小时', '')"
                            @blur="onBlurRemainHour"
                            @change="onChangeRemainHour" />
                    </a-form-model-item>
                </a-col>
                <a-col :span="12" :xs="!isToolBar?'':24">
                    <a-form-model-item placeholder="未设置(单位:小时)">
                        <template slot="label">
                            工时进度&nbsp;
                            <a-tooltip placement="bottom">
                                <template slot="title">
                                    <div style="font-size:8px;">
                                        工时进度=已登记工时/ (已登记工时+剩余工时) x 100%，始终按小时计算。
                                    </div>
                                </template>
                                <span><a-icon type="question-circle-o" /></span>
                            </a-tooltip>
                        </template>
                        <span style="margin-left:10px">
                            {{ ((formData.registeredHour / (formData.registeredHour +
                                formData.remainHour) * 100) || 0).toFixed(2) || 0 }}%
                        </span>
                    </a-form-model-item>
                </a-col>
                <a-col :span="12" :xs="!isToolBar?'':24">
                    <a-form-model-item placeholder="未设置(单位:小时)">
                        <template slot="label">
                            预估偏差&nbsp;
                            <a-tooltip placement="bottom">
                                <template slot="title">
                                    <div style="font-size:8px;">
                                        预估偏差=预估工时- (已登记工时+剩余I时),偏差为0表示预估准确,
                                        偏差为正表示预估偏多,偏差为负表示预估偏少
                                    </div>
                                </template>
                                <span><a-icon type="question-circle-o" /></span>
                            </a-tooltip>
                        </template>
                        <span style="margin-left:10px">{{ (formData.estimateHour -
                            (formData.registeredHour + formData.remainHour)) }}小时</span>
                    </a-form-model-item>
                </a-col>
            </a-row>
        </a-form-model>
        <div style="margin:10px 0" class="task-detail-module-title">
            <div class="task-detail-module-title-text">工时</div>
            <div style="">
                <vxe-button v-if="!trackerItem?.notPagePerm"
                 @click="onClickRegisterHour" type="text"
                    icon="vxe-icon-time">登记工时</vxe-button>
            </div>
        </div>
        <a-list item-layout="horizontal" :loading="loading" :data-source="workHoursList">
            <a-list-item @mouseover="registeredMouseOver($event)"
                @mouseleave="registeredMouseLeave($event)" class="register-hour-item"
                slot="renderItem" slot-scope="item">
                <a-list-item-meta>
                    <div slot="title">
                        <h-avatar :name="item.member?.name" :icon="item.member?.icon" :size="16"></h-avatar>
                        &nbsp;
                        <span style="color: #909090;font-size: 12px;">{{ item.startTime }}
                            开始</span>

                        <span style="float:right;margin-top: 10px;">{{ item.actualHour
                        }}小时</span>
                        <span id="register-operate" class="register-hour-item-edit"
                            style="background-color: #f8f8f8;position: absolute;right: 0;margin-top: 10px;margin-right: 10px;">
                            <vxe-button @click="onClickEditWorkHours(item)" title="修改登记工时"
                                type="text" icon="vxe-icon-edit"></vxe-button>
                            <vxe-button @click="onClickDeleteWorkHours(item)" title="删除登记工时"
                                type="text" icon="vxe-icon-delete"></vxe-button>
                        </span>
                    </div>
                </a-list-item-meta>
                <p style="text-indent:28px;">{{ item.description || '无描述' }}</p>
            </a-list-item>
        </a-list>

        <a-modal v-if="deleteWorkHoursVisible" :visible="deleteWorkHoursVisible" okType="danger" centered width="520px"
            title="删除成员登记工时" @ok="onOKDeleteWorkHours" @cancel="deleteWorkHoursVisible = false">
            <a-row :gutter="[24, 16]">
                <a-col :span="8">工作项名称</a-col><a-col :span="16">{{ trackerItem?.name }}</a-col>
                <a-col :span="8">成员名称</a-col><a-col :span="16">{{ formDataHour?.member?.name }}</a-col>
                <a-col :span="8">登记日期</a-col><a-col :span="16">{{ 
                    formDataHour.startTime.format("YYYY-MM-DDHH:mm: ss")}}</a-col>
                <a-col :span="8">登记工时</a-col><a-col :span="16">{{ formDataHour.actualHour }}小时</a-col>
            </a-row>
        </a-modal>

        <register-hour-dialog :isShowDialog="registerHourVisible" :trackerItem="trackerItem" :projectId="projectId"
            :dialogTitleWorkHour="dialogTitleWorkHour" :workHoursItem="workHoursItem" @ok="onOKRegisterHour"
            @close="registerHourVisible = false"></register-hour-dialog>

    </div>
</template>

<script>
import {
     changeSystemField,findWorkHours, createWorkHours, saveWorkHours, deleteWorkHours,
} from "@/services/tracker/TrackerItemService"
import HAvatar from '@/components/avatar/h-avatar.vue';
import VXETable from "vxe-table";
import moment from 'moment'
import RegisterHourDialog from './RegisterHourDialog.vue';
import { hasPermission } from '@/utils/permission'


export default {
    name: 'TrackerWorkHours',
    components: {RegisterHourDialog, HAvatar },
    props: {
        projectId: String,
        itemId: String,
        trackerItem: Object,
        formData: Object,
        currentTab: String,
        isToolBar:Boolean
    },
    data() {
        return {
            loading:false,
            registerHourVisible: false,
            deleteWorkHoursVisible: false,
            statePopoverVisible: false,
            editWorkHourMode: 'create',
            dialogTitleWorkHour: '添加成员登记工时',
            workHoursList: [],
            workHoursItem: {},
            formDataHour: {},
           
        }
    },
    computed:{

    },
    watch: {
        currentTab: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal=='WORK_HOURS') {
                    this.loadHoursData ();
                }
            }
        }
    },
    mounted() {

    },
    methods: {
        //工时   
        onClickDeleteWorkHours(item) {
            this.deleteWorkHoursVisible = true;
            this.formDataHour = Object.assign({}, item);
            console.log("deleteHOw", this.formDataHour,item)
            this.formDataHour.startTime = moment(item.startTime);
        },
        onOKDeleteWorkHours() {
            deleteWorkHours(this.itemId, { id: this.formDataHour.id,memberId: this.formDataHour.member.id}).then(resp => {
                this.loadHoursData(true).then(res=>{
                    VXETable.modal.message({ content: '删除成功', status: 'success' })
                    this.formData.remainHour += this.formDataHour.actualHour;
                    changeSystemField(this.itemId, "remainingWorkingHours", this.formData.remainHour).then(resp => {
                        
                    }).finally(()=>{
                        this.refresh()
                    })
                })
                
            })
            this.deleteWorkHoursVisible = false;
        },
        onClickEditWorkHours(item) {
            this.registerHourVisible = true
            this.editWorkHourMode = 'edit',
                this.dialogTitleWorkHour = '修改成员登记工时',
            this.workHoursItem = Object.assign({}, item)
        },
        onClickRegisterHour() {
            this.registerHourVisible = true
            this.editWorkHourMode = 'create'
            this.dialogTitleWorkHour = '添加成员登记工时'
        },
        onOKRegisterHour(workHours) {
            this.registerHourVisible = false
            this.formData.remainHour = workHours.remainHour
            changeSystemField(this.itemId, "remainingWorkingHours", workHours.remainHour).then(resp => {
                if (this.editWorkHourMode == 'create') {
                    createWorkHours(this.itemId, workHours).then(resp => {
                        VXETable.modal.message({ content: '登记工时添加成功', status: 'success' })
                        this.formData.registeredHour += workHours
                        this.loadHoursData();
                    })
                } else {
                    saveWorkHours(this.itemId, workHours).then(resp => {
                        VXETable.modal.message({ content: '登记工时修改成功', status: 'success' })
                        this.loadHoursData();
                    })
                }
            })
        },
        onChangeEstimateHour(value){
            const reg = /^-?[0-9]*(\.[0-9]*)?$/;
            if (!reg.test(value)) {
                value=parseInt(value);
                this.formDataHour.estimateHour=value.toString().replace(/[^\d\.]/g,"");
            }
        },
        onBlurEstimateHour() {
            changeSystemField(this.itemId, "estimateWorkingHours", this.formData.estimateHour).then(resp => {
            
            }).finally(()=>{
                this.refresh()
            })
        },
        onChangeRemainHour(value){
            const reg = /^-?[0-9]*(\.[0-9]*)?$/;
            if (!reg.test(value)) {
                value=parseInt(value);
                this.formDataHour.remainHour=value.toString().replace(/[^\d\.]/g,"");
            }
        },
        onBlurRemainHour(e) {
            changeSystemField(this.itemId, "remainingWorkingHours", this.formData.remainHour).then(resp => {
            
            }).finally(()=>{
                this.refresh()
            })
        },
        registeredMouseOver(e) {
            e.currentTarget.querySelector("#register-operate").className = ''
        },
        registeredMouseLeave(e) {
            e.currentTarget.querySelector("#register-operate").className = 'register-hour-item-edit'
        },
        async loadHoursData(isRefresh) {
            if(!this.itemId){
                return
            }
            this.loading = true;
            await findWorkHours(this.itemId).then(res => {
                this.workHoursList = res;
                let sum = 0;
                this.workHoursList.forEach(item => {
                    sum += item.actualHour;
                })
                if (this.formData.registeredHour && sum != this.formData.registeredHour) {
                    this.formData.registeredHour = sum;
                    changeSystemField(this.itemId, "registeredWorkingHours", this.formData.registeredHour).then(resp => {
                        
                    }).finally(()=>{
                        if(!isRefresh){
                            this.refresh()
                        }
                    })
                }
            }).finally(() => {
                this.loading = false;
            })
        },
        refresh(){
            this.$emit("refresh");
        },
    },
}
</script>

<style lang="less" scoped>
.task-detail-module {
    // margin: 0 20px 0;
    // padding-top: 25px;

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

        // .task-detail-module-title-text {
        //     flex: 1 1 auto;
        // }
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
.register-hour-item {
    border-bottom: 1px solid #e8e8e8;
    cursor: pointer;
    padding: 12px;
    flex-direction: column;
    align-items: normal;
}

.register-hour-item:hover {
    background-color: #f8f8f8;
}

.register-hour-item-edit {
    display: none;
}
</style>