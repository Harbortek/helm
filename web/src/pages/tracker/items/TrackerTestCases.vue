<template>
    <div v-if="formData">
            <a-form-model-item label="测试用例类型" prop="testCaseTypeId" style="width:50%">
                <test-case-type-select style="width:100%" v-model="formData.testCaseType.id" 
                     @onInit="onInitTestCaseType" @change="onChangeTestCaseType" />
            </a-form-model-item>
            <a-form-model-item label="前置条件" prop="precondition" style="width:50%">
                <a-textarea auto-size v-model="formData.precondition" @blur="onBlurPrecondition"
                 placeholder="请输入前置条件" />
            </a-form-model-item>

            <a-form-model-item label="操作步骤" prop="relatedWorkItems" :span="24">
                <div class="ui-table">
                    <div class="table-head layout-fields-table-row">
                        <div style="width:10%">编号</div>
                        <div style="width:35%">步骤描述</div>
                        <div style="width:35%">预期结果</div>
                        <div style="width:10%">操作</div>
                    </div>
                    <draggable v-model="formData.testSteps" filter=".forbid" @start="onStart" @end="onEnd" 
                        :sort="true" animation="300" dragClass="dragClass" ghostClass="ghostClass">
                        <div v-for="(item,index) in formData.testSteps" :key="item.id">
                                <div class="table-row layout-fields-table-row">   
                                    <div style="width:10%">{{index+1}}</div>
                                    <div style="width:35%">
                                        <a-textarea @blur="onBlurStep(item)" auto-size v-model="item.expectedResult"
                                            placeholder="未填写" />
                                    </div>
                                    <div style="width:35%">
                                        <a-textarea @blur="onBlurStep(item)" auto-size v-model="item.description"
                                            placeholder="未填写" />
                                    </div>
                                    <div :style="{'width':isToolBar?'14%':'10%'}">
                                        <i class="vxe-icon--menu"></i>
                                        <vxe-button style="font-size: 14px;" @click="onClickRemoveStep(item.id)"
                                            title="移除" type="text" icon="vxe-icon-close"
                                        ></vxe-button>
                                    </div>
                                    
                                </div>
                        </div>
                    </draggable>
                    <div class="table-row layout-fields-table-row">
                        <div style="width:100%;display: flex;justify-content: center;">
                            <vxe-button @click="onClickAddStep" style="font-size: 14px;" title="新增操作步骤" type="text" icon="vxe-icon-add"
                                status="primary">新增操作步骤</vxe-button>
                        </div>
                    </div>
                </div>
                
            </a-form-model-item>
        <!-- <a-form-model-item label="操作步骤" prop="relatedWorkItems" :span="24">
            <vxe-table ref="optionsTest" :show-header="false" :data="[]"
                :row-config="{ isHover: true }" stripe>
                
            </vxe-table>
        </a-form-model-item> -->

        
    </div>
</template>

<script>
import VXETable from "vxe-table";
import draggable from 'vuedraggable'
import Vue from "vue";
import TestCaseTypeSelect from '@/components/select/TestCaseTypeSelect.vue';
import { updateTrackerItemTestStep,updateTrackerItemTestSteps,
        changeSystemField } from "@/services/tracker/TrackerItemService"


export default {
    name: 'TrackerTestCases',
    components: {draggable,TestCaseTypeSelect},
    props: {
        isShowDialog: {
            required: false
        },
        formData: {
            required: true
        },
        isToolBar:Boolean,
        trackerItem: Object,
    },
    data() {
        return {
        
            loading: false,
        }
    },
    watch: {
        formData: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    if(!this.formData.testCaseType){
                        this.$set(this.formData,"testCaseType",{id:''})
                    }
                    // this.loadData();
                    // if(this.trackerItem){
                    //     this.$set(this.formData, 'testSteps', this.trackerItem.testSteps)
                    //     this.$set(this.formData, 'precondition', this.trackerItem.precondition)
                    //     console.log("this.formData",this.formData)
                    // }
                }
            }
        }
    },
    mounted() {

    },
    methods: {
        onStart(e) {
        },
        onEnd(e) {
            if(e.newIndex!==e.oldIndex){
                if(this.formData.id){
                    updateTrackerItemTestSteps(this.formData.id,this.formData.testSteps).then(res=>{
                        this.$emit("loadComment")
                    })
                }
                
            }
        },
        onClickAddStep(){
            if(!this.formData.testSteps||this.formData.testSteps.length==0){
                this.formData.testSteps=Vue.observable([{description:'',expectedResult:'',id:1}]);
            }else{
                let maxId=Math.max(...this.formData.testSteps.map(item=>item.id))
                this.formData.testSteps.push({description:'',expectedResult:'',id:maxId+1||1})
            }
        },
        onClickRemoveStep(id){
            this.formData.testSteps=this.formData.testSteps.filter(item=>item.id!=id)
            if(this.formData.id){
                updateTrackerItemTestStep(this.formData.id,{id}).then(res=>{
                    this.$emit("loadComment")
                })
            }
        },
        onBlurStep(item){
            if(this.formData.id){
                updateTrackerItemTestStep(this.formData.id,item).then(res=>{
                    this.$emit("loadComment")
                })
            }
        },
        onBlurPrecondition(){
            if(this.formData.id){
                changeSystemField(this.formData.id,"precondition",this.formData.precondition).then(res=>{
                    this.$emit("loadComment")
                })
            }
        },
        onInitTestCaseType(value){
            if(!this.formData.id){
                this.formData.testCaseType.id=value
            }
        },
        onChangeTestCaseType(){
            if(this.formData.id){
                changeSystemField(this.formData.id, "testCaseTypeId", this.formData.testCaseType.id).then(resp => {
                    this.$emit("loadComment")
                })
            }
        },
    },
}
</script>

<style lang="less" scoped>

/*覆盖ant-design-vue disabled 样式**/
// /deep/ .ant-select-disabled .ant-select-selection {
//     background: #ffffff !important;
// }

.ui-table {
    border: solid 1px #dedede;
    border-radius: 3px;
    background-color: #fff;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}
.layout-fields-table-row{
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    min-height: 48px;
    padding-right: 20px;
    padding-left: 20px;
    padding-top: 10px;
    padding-bottom: 10px;
}
.table-head {
    width: 100%;
    padding: 0 20px;
    font-size: 14px;
    color: #303030;
    font-weight: 500;
    line-height: 48px;
    background-color: #f8f8f8;
    border-bottom: solid 1px #dedede;
    box-sizing: border-box;
}
.table-row {
    width: 100%;
    padding: 0 20px;
    // height: 48px;
    // line-height: 48px;
    border-bottom: solid 1px #dedede;
    font-size: 14px;
    color: #303030;
    box-sizing: border-box;
}
</style>