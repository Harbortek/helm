<template>
    <div class="task-detail-module" :style="{margin: !isToolBar?'0 20px 0':''}">
        <div class="task-detail-module-title">
            <div class="task-detail-module-title-text">周期与进度</div>
        </div>
        <a-form-model style="margin-left:10px;" :layout="'horizontal'" :labelCol="{ span: 8 }"
            :wrapperCol="{ span: 16 }" labelAlign="right" :model="formData">
            <a-row :gutter="15" :style="{width:!isToolBar?'60%':''}">
                <a-form-model-item label="计划开始日期" placeholder="未设置">
                    <a-date-picker @change="onChangePlanStart" :disabled="trackerItem?.notPagePerm"
                        :disabled-date="disabledStartDate" v-model="formData.planStartDate"
                        style="width:100%" />
                </a-form-model-item>
                <a-form-model-item label="计划完成日期" placeholder="未设置">
                    <a-date-picker @change="onChangePlanEnd" :disabled="trackerItem?.notPagePerm"
                        :disabled-date="disabledEndDate" v-model="formData.planEndDate"
                        style="width:100%" />
                </a-form-model-item>
                <a-form-model-item>
                    <template slot="label">
                        进度
                        <a-tooltip placement="bottom" :overlayStyle="{ minWidth: '380px' }">
                            <template slot="title">
                                <div style="font-size:8px;">
                                    进度计算公式:<br>
                                    父工作项进度=SUM(子工作项周期时长*进度)/SUM(子工作项周期时长)
                                    <br><br>
                                    根据以上公式自动汇总当前工作项的进度。如果没有子工作项,则需要
                                    手动填写进度。<br><br>
                                    注意，在计算进度时:<br>
                                    ·若子工作项未设置进度,则该子工作项进度视为0%;<br>
                                    ·若子工作项开始或完成日期为空,则该子工作项周期时长视为一天。
                                </div>
                            </template>
                            <span><a-icon type="question-circle-o" /></span>
                        </a-tooltip>
                    </template>
                    <a-popover trigger="click" :visible="cycleVisible" 
                        @visibleChange="onClickCycle" placement="bottom"
                        :style="{pointerEvents: !trackerItem?.notPagePerm?'':'none'}"
                        :overlayStyle="{ minWidth: '300px' }">
                        <template slot="title">
                            更新进度
                        </template>
                        <div slot="content">
                            <div>
                                <a-row>
                                    <a-col :span="15">
                                        <a-slider v-model="formData.progress" :min="0"
                                            :max="100" />
                                    </a-col>
                                    <a-col :span="8">
                                        <a-input v-model="formData.progress"
                                            @change="onChangeProgress" :min="0" :max="100"
                                            style="marginLeft: 16px" addon-after="%" />
                                    </a-col>
                                </a-row>
                            </div>
                            <div
                                style="margin-top:10px;display: flex;justify-content: flex-end;">
                                <a-button size="small" @click="onClickCycle">取消</a-button>
                                <a-button style="margin-left:10px;" size="small"
                                    type="primary" @click="onClickProgress">确认</a-button>
                            </div>
                        </div>
                        <a-select :open="false" v-model="formData.progressShow">
                            <template slot="notFoundContent"></template>
                        </a-select>
                        <!-- <a-input v-model="formData.progressShow" /> -->
                    </a-popover>

                </a-form-model-item>
            </a-row>
        </a-form-model>
    </div>
</template>

<script>
import {
     changeSystemField,findWorkHours, createWorkHours, saveWorkHours, deleteWorkHours,
} from "@/services/tracker/TrackerItemService"
import VXETable from "vxe-table";
import moment from 'moment'
import RegisterHourDialog from './RegisterHourDialog.vue';

export default {
    name: 'TrackerCycleProgress',
    components: {RegisterHourDialog },
    props: {
        itemId: String,
        trackerItem: Object,
        formData: Object,
        isToolBar:Boolean
    },
    data() {
        return {
            loading:false,
            cycleVisible: false,
           
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
        //周期与进度
        disabledStartDate(startValue) {
            const endValue = this.formData.planEndDate;
            if (!startValue || !endValue) {
                return false;
            }
            return startValue.valueOf() > endValue.valueOf();
        },
        disabledEndDate(endValue) {
            const startValue = this.formData.planStartDate;
            if (!endValue || !startValue) {
                return false;
            }
            return startValue.valueOf() >= endValue.valueOf();
        },
        onChangePlanStart(e) {
            let planStartTime = moment(this.formData.planStartDate).format("YYYY-MM-DD 00:00:00")
            if(this.itemId){
                changeSystemField(this.itemId, "planStartDate", planStartTime).then(resp => {
                    
                }).finally(()=>{
                    this.refresh()
                })
            }
        },
        onChangePlanEnd(e) {
            let planEndTime = moment(this.formData.planEndDate).format("YYYY-MM-DD 00:00:00")
            if(this.itemId){
                changeSystemField(this.itemId, "planEndDate", planEndTime).then(resp => {
                    
                }).finally(()=>{
                    this.refresh()
                })
            }
        },
        onClickProgress() {
            this.formData.progressShow = this.formData.progress + "%";
            this.cycleVisible = !this.cycleVisible
            if(this.itemId){
                changeSystemField(this.itemId, "progress", this.formData.progress).then(resp => {
                    
                }).finally(()=>{
                    this.refresh()
                })
            }
        },
        onChangeProgress() {
            this.formData.progress = parseInt((this.formData.progress + '').replace(/[^\d\.]/g, ""));
            if (this.formData.progress > 100) {
                this.formData.progress = 100;
            } else if (isNaN(this.formData.progress)) {
                this.formData.progress = 0
            }
        },
        onClickCycle() {
            this.cycleVisible = !this.cycleVisible
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
        //     // flex: 1 1 auto;
        // }
    }
}

</style>