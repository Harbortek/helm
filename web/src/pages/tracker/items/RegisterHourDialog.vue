<template>
    <a-modal v-if="visiable" v-model="visiable" centered :title="dialogTitleWorkHour" width="520px" @ok="onOK" @cancel="onClose">
        <div style="border-bottom: 1px solid #e8e8e8;" class="task-detail-module-title">
            <div class="task-detail-module-title-text">工作项名称</div>
            #{{currentProjectKeyName+'-'+trackerItem?.itemNo}}&nbsp;&nbsp;{{trackerItem?.name}}
        </div>
        <a-form-model :layout="'horizontal'" ref="formDataHour" :model="formDataHour" :rules="rulesHours">
            <a-row :gutter="15">
                <a-form-model-item label="成员" prop="memberId" >
                    <project-user-select @change="onChangeMemberId" v-model="formDataHour.memberId" :projectId="projectId" />
                </a-form-model-item>
                    <a-descriptions title="" :column="5" layout="vertical" size="middle">
                    <a-descriptions-item :span="1">
                        <template slot="label">
                            <span style="font-size:12px;color:#606060">预计工时</span>
                        </template>
                    <div v-if="formDataHour.estimateHour">
                        <span style="font-size:20px;color:#303030;font-weight:500;margin-right: 5px;">{{formDataHour.estimateHour}}</span>小时
                    </div>                        
                    <span v-else style="font-size:14px;color:#909090">暂无</span>
                    </a-descriptions-item>
                    <a-descriptions-item :span="1">
                        <template slot="label">
                                <span style="font-size:12px;color:#606060">剩余工时</span>
                        </template>
                    <div v-if="formDataHour.previousRemainHour">
                        <span style="font-size:20px;color:#303030;font-weight:500;margin-right: 5px;">{{formDataHour.previousRemainHour}}</span>小时
                    </div>   
                    <span v-else style="font-size:14px;color:#909090">暂无</span>
                    </a-descriptions-item>
                    <a-descriptions-item :span="3">
                        <template slot="label">
                            <span style="font-size:12px;color:#606060">成员当日剩余可登记工时 &nbsp;</span>
                            <a-tooltip placement="bottom">
                                <template slot="title">
                                    <div style="font-size:8px;">
                                        成员每日登记工时上限为24小时
                                    </div>
                                </template>
                                <span><a-icon type="exclamation-circle" /></span>
                            </a-tooltip>
                        </template>
                    <div>
                        <span style="font-size:20px;color:#303030;font-weight:500;margin-right: 5px;">{{this.remainingRegistrableTime}}</span>小时
                    </div>   
                    </a-descriptions-item>
                    </a-descriptions>
                    <a-col :span="12" style="padding:0">
                    <a-form-model-item label="开始时间" placeholder="请选择日期" required>
                        <a-date-picker :allowClear="false" @change="onChangeWorkHourStartTime" v-model="formDataHour.startTime" show-time style="width:100%" />
                    </a-form-model-item>
                    </a-col>
                    <a-col :span="12">
                    <a-form-model-item label="实际投入时长" prop="actualHour"  placeholder="请输入数字">
                        <a-input placeholder="请输入数字" v-model="formDataHour.actualHour" @change="onChangeActualHour" style="marginLeft: 16px"  addon-after="小时"/>
                    </a-form-model-item>
                    </a-col>
                <div style="margin:10px 0">剩余工时
                    <a-tooltip placement="bottom">
                        <template slot="title">
                            <div style="font-size:8px;">
                                用于表示当前继续完成该工作项还需要花费的工时
                            </div>
                        </template>
                        <span><a-icon type="exclamation-circle" /></span>
                    </a-tooltip>
                </div>
                <a-col :span="24">
                    <a-radio-group name="radioGroup" v-model="remainHourRadio">
                        <a-radio :value="1">
                        自动计算
                        </a-radio>
                        <a-radio :value="2">
                        手动计算
                        </a-radio>
                    </a-radio-group>
                </a-col>
                <div v-if="remainHourRadio=='1'" style="margin:10px 0">
                    <div>
                        <span style="font-size:20px;color:#303030;font-weight:500;margin-right: 5px;">{{formDataHour.remainHour}}</span>小时
                    </div> 
                    <div style="font-size: 12px;color: #909090;">剩余工时 = 登记前剩余工时 - 登记时长</div>
                </div>
                <a-col v-else :span="12" style="padding:0">
                    <div style="margin:10px 0">
                        <a-form-model-item >
                            <a-input v-model="formDataHour.remainHour" placeholder="请输入数字" addon-after="小时"/>
                        </a-form-model-item>
                    </div>
                </a-col>
                <a-col :span="24" style="padding:0">
                    <a-form-model-item label="描述"  placeholder="在这期间我做了什么">
                        <a-textarea
                        v-model="formDataHour.description"
                        placeholder="在这期间我做了什么"
                        :auto-size="{ minRows: 2, maxRows: 6 }"
                        />
                    </a-form-model-item>
                </a-col>
            </a-row>
        </a-form-model>
    </a-modal>
</template>
<script>
import moment from 'moment'
import VXETable from 'vxe-table';
import { mapGetters } from "vuex";
import ProjectUserSelect from '../../../components/select/ProjectUserSelect.vue';
import { remainingRegistrableTime } from "@/services/tracker/TrackerItemService"
import { hasPermission } from '@/utils/permission'

export default {
    name: "RegisterHourDialog",
    components: { ProjectUserSelect },
    data() {
        return {
            remainHourRadio:1,
            remainingRegistrableTime:'',
            loading: false,
            rulesHours: {
                actualHour: [{required: true, validator: this.actualHourValidator, trigger: ['blur','change'] }],
                memberId: {required: true, message: '请选择成员', trigger: ['change']}
            },
            formDataHour: {
                memberId:'',
                startTime:'',
                actualHour:'',
                remainHour:'',
                previousRemainHour:'',
                description:'',
            },
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        projectId: {
            require: true
        },
        dialogTitleWorkHour: {
            required: true
        },
        trackerItem: {
            require: true
        },
        workHoursItem: {
            require: false
        },
    },
    computed: {
        ...mapGetters("project", ["currentProjectKeyName"]),
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
    },
    watch: {
        objectId: {
            handler: function (newVal, oldVal) {
                // this.loadData();
            }
        },
        isShowDialog: {
            handler: function (newVal, oldVal) {
                
                if(newVal){
                    this.remainHourRadio=1;
                    this.loadData();
                }
                
            }
        },
    },
    mounted() {
    },
    methods: {
         //工时
        actualHourValidator(rule, value, callback) {
            const reg = /^-?[0-9]*(\.[0-9]*)?$/;
            if (!reg.test(value)) {
                this.formDataHour.actualHour=value.replace(/[^\d\.]/g,"");
                this.onChangeActualHour();
            }
            if (!!!value) {
                return callback(new Error('必填项不能为空'));
            }else if(value>this.remainingRegistrableTime){
                return callback(new Error('该成员当日可登记工时已超出上限，请重新输入'));
            }else if(value<=0){
                return callback(new Error('请输入大于0的数'));
            }else{
                return callback();
            }
        },
        onChangeActualHour(){
            this.formDataHour.remainHour=this.formDataHour.previousRemainHour - this.formDataHour.actualHour
            if(this.formDataHour.remainHour<0){
                this.formDataHour.remainHour=0;
            }
        },
        onChangeWorkHourStartTime(){
            if(this.formDataHour.memberId){
                remainingRegistrableTime(this.formDataHour.memberId,{beginDate:this.formDataHour.startTime.format("YYYY-MM-DD HH:mm:ss")}).then(resp => {
                    this.remainingRegistrableTime=resp;
                    if(this.workHoursItem?.id){
                        this.remainingRegistrableTime+=this.workHoursItem.actualHour
                    }
                })
            }
            
            // let planStartTime=moment(this.formData.planStart).format("YYYY-MM-DD 00:00:00")
        },
        onChangeMemberId(){
            remainingRegistrableTime(this.formDataHour.memberId,{beginDate:this.formDataHour.startTime.format("YYYY-MM-DD HH:mm:ss")}).then(resp => {
                this.remainingRegistrableTime=resp;
                if(this.workHoursItem?.id){
                    this.remainingRegistrableTime+=this.workHoursItem.actualHour
                }
            })
        },
        loadData() {
            if(this.dialogTitleWorkHour=='添加成员登记工时'){
                this.formDataHour={
                    memberId:'',
                    startTime:'',
                    actualHour:'',
                    remainHour:'',
                    description:'',
                }
                this.formDataHour.previousRemainHour=this.trackerItem.remainingWorkingHours||0;
                this.formDataHour.estimateHour=this.trackerItem.estimateWorkingHours||0;
                this.formDataHour.remainHour=this.trackerItem.remainingWorkingHours||0;
                this.formDataHour.memberId=this.$store.getters['account/user']?.id
                this.formDataHour.startTime=moment(moment(new Date()).format("YYYY-MM-DD HH:00:00"));
            }else{
                this.formDataHour=Object.assign({},this.workHoursItem);
                this.formDataHour.estimateHour=this.trackerItem.estimateWorkingHours||0;
                this.formDataHour.memberId=this.workHoursItem.member.id;
                this.formDataHour.previousRemainHour=(this.trackerItem.remainingWorkingHours||0) + this.formDataHour.actualHour;
                this.formDataHour.remainHour=this.trackerItem.remainingWorkingHours||0;
                this.formDataHour.startTime=moment(this.workHoursItem.startTime);
            }
            if(this.formDataHour.memberId){
                remainingRegistrableTime(this.formDataHour.memberId,{beginDate:this.formDataHour.startTime.format("YYYY-MM-DD HH:mm:ss")}).then(resp => {
                    this.remainingRegistrableTime=resp;
                    if(this.workHoursItem?.id){
                        this.remainingRegistrableTime+=this.workHoursItem.actualHour
                    }
                })
            }
            
        },
        onOK() {
            this.$refs.formDataHour.validate((valid) => {
                if(valid){
                    console.log("onOk",this.formDataHour)
                    let workHours=Object.assign({},this.formDataHour)
                    workHours.startTime=this.formDataHour.startTime.format("YYYY-MM-DD HH:mm:ss")
                    delete workHours.member
                    delete workHours.createBy
                    delete workHours.lastModifiedBy
                    this.$emit("ok",workHours);
                }
            })
        },
        onClose() {
            this.$emit("close");
        },
    },
    created() {

    },
    beforeDestroy() {

    },
};
</script>
<style lang="less" scoped>
.task-detail-module-title-text{
    font-size: 15px;
    margin-top: -12px;
    margin-bottom: 10px;
    font-weight: 500;
    color: #494949;
}
.status-sorting-item {
    max-width: 478px;
    // border-bottom: 1px solid #dedede;
    cursor: move;
    line-height: 34px;
    padding: 0 20px;

    &:hover {
        background: #f8f8f8;
    }

    .pin-status-tag {
        line-height: 34px;
        font-size: 12px;
        color: #909090;
        margin-left: 5px;
        padding: 1px 7px;
        border-radius: 100px;
        border: solid 1px #c7c7c7;
    }
}

.start-state {
    cursor: default;
}

.dialog-description {
    display: flex;
    margin-bottom: 10px;
    line-height: 1.5;
    box-sizing: border-box;
    border-bottom: 1px solid #e8e8e8;
    border-radius: 0;
    border-right: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}

.dialog-description-border {
    border-radius: 0;
    flex: none;
    width: 4px;
}

.dialog-description-info .dialog-description-border {
    background: #338fe5;
}

.dialog-description-container {
    background: #fff;
    display: flex;
    flex: auto;
    padding: 10px;
}

.dialog-description-p {
    margin: 0 0 0 0;
    padding: 0;
    white-space: pre-wrap;
}

.style--font-14 {
    font-size: 14px;
    line-height: 22px;
}
</style>
