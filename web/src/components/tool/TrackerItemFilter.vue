<template>
  <a-collapse v-model="activeKey" :bordered="false" style="padding:0px 20px;">
        <template #expandIcon></template>
        <a-collapse-panel key="true" :disabled="true">
          <div ref="filterRef" style="overflow-y:auto;height:200px;width: 100%;">
            <div style="height:30px;">筛选条件</div>
            <div >
                <div v-for="(item,idx) in conditionGroups" :key="idx">
                  <div v-if="idx>0" class="group-line">
                    <span class="group-or">或</span>
                  </div>

                  <div class="order-row" :key="index"
                      v-for=" (condition, index) in item.conditions">
                      <a-row>
                        <a-col :span="24">
                      <a-space align="start" style="margin-bottom:10px;">
                          <a-icon style="padding-top: 7px;" type="minus-circle" class="icon-circle"
                            @click="removeCondition(condition, idx, index)" />
                          
                          <div style="line-height: 30px;width:15px;">
                            <span v-if="index!=0">且</span>
                          </div>
                          <a-select show-search option-filter-prop="children"  v-model="condition.field" style="width:180px;height: auto;"
                              >
                              <a-select-option v-for="f in conditionByFields(condition,idx)"
                                :value="f.systemProperty" :key="f.systemProperty" @click="onChangeCondition(condition,f)">{{
                                      f.name
                                  }}</a-select-option>
                          </a-select>

                          <a-select @change="onChangeConditionOperator(condition)" 
                              v-if="condition.type=='INTEGER'||condition.type=='DATE'"
                            v-model="condition.operator" style="width:90px;">
                              <a-select-option value="EQ" >等于</a-select-option>
                              <a-select-option value="NEQ" >不等于</a-select-option>
                              <a-select-option value="GEQ" >大于等于</a-select-option>
                              <a-select-option value="LEQ" >小于等于</a-select-option>
                              <a-select-option value="BETWEEN">介于</a-select-option>
                          </a-select>

                          <a-select @change="onChangeConditionOperator(condition)" 
                              v-if="condition.type=='STATUS'||condition.type=='OPTIONS'||condition.type=='WORK_ITEM'||
                              condition.type=='WORK_ITEM_TYPE'||condition.type=='USER'||condition.type=='STATUS_TYPE'"
                            v-model="condition.operator" style="width:90px;">
                              <a-select-option value="INCL">包含</a-select-option>
                              <a-select-option value="EXCL">不包含</a-select-option>
                              <a-select-option value="NULL">为空</a-select-option>
                              <a-select-option value="NN">不为空</a-select-option>
                          </a-select>

                          <a-select @change="onChangeConditionOperator(condition)" v-if="condition.type=='TEXT'||condition.type=='WORK_ITEM_NO'"
                            v-model="condition.operator" style="width:90px;">
                              <a-select-option value="INCL">包含</a-select-option>
                              <a-select-option value="EXCL">不包含</a-select-option>
                              <a-select-option value="EQ">等于</a-select-option>
                              <a-select-option value="NEQ">不等于</a-select-option>
                              <a-select-option value="NULL">为空</a-select-option>
                              <a-select-option value="NN">不为空</a-select-option>
                          </a-select>

                          <div v-if="condition.operator!='NULL'&&condition.operator!='NN'">
                            <div style="display: flex;align-items: center;position: relative;">
                              <a-input v-if="(condition.type=='INTEGER'&&condition.operator!='BETWEEN')||condition.type=='TEXT'||condition.type=='WORK_ITEM_NO'" placeholder="请输入..." v-model="condition.value" @change="onChangeConditionInput(condition)" style="width:260px;"/>                          
                              <a-space direction="vertical" v-else-if="condition.type=='INTEGER'&&condition.operator=='BETWEEN'">
                                <a-input placeholder="请输入..." v-model="condition.value1" @change="onChangeConditionInput(condition)" style="width:260px;"/>                          
                                <a-input placeholder="请输入..." v-model="condition.value2" @change="onChangeConditionInput(condition)" style="width:260px;"/>                          
                              </a-space>
                              <div v-if="condition.type=='INTEGER'&&condition.operator=='BETWEEN'" class="input-separate"><span class="input-separate-label">至</span></div>
                            </div>

                            <a-date-picker v-if="condition.type=='DATE'&&condition.operator!='BETWEEN'" style="width:260px;" v-model="condition.conditionDate" placeholder="请选择日期" @change="onChangeConditionDate(condition)" />
                            <a-range-picker v-else-if="condition.type=='DATE'&&condition.operator=='BETWEEN'" style="width:260px;" v-model="condition.conditionRangeDate" @change="onChangeConditionRangeDate(condition)" />

                            <a-select v-if="condition.type=='STATUS'" mode="multiple" optionFilterProp="label" v-model="condition.value" style="width: 260px" placeholder="请选择..." @change="onChangeConditionSelect(condition)">
                              <a-select-option v-for="status in tracker.trackerStatuses" :key="status.id" :label="status.name">
                                <a-icon style="margin-right:5px;" v-if="status.icon" :component="status.icon"/>{{ status.name }}
                              </a-select-option>
                            </a-select>

                            <a-select v-if="condition.type=='OPTIONS'||condition.type=='WORK_ITEM'||condition.type=='STATUS_TYPE'||
                              condition.type=='WORK_ITEM_TYPE'" mode="multiple" optionFilterProp="label" 
                              v-model="condition.value" style="width: 260px" placeholder="请选择..." @change="onChangeConditionSelect(condition)">
                              <a-select-option v-for="status in tracker[condition.field]" :key="status.id" :label="status.name">
                                <a-icon style="margin-right:5px;" v-if="status.icon" :component="status.icon"/>{{ status.name }}
                              </a-select-option>
                            </a-select>

                            <a-select
                                v-if="condition.type=='USER'"
                                mode="multiple"
                                optionFilterProp="label"
                                style="width:260px;"
                                v-model="condition.value"
                                placeholder="请选择..."
                                @change="onChangeConditionSelect(condition)"
                            >
                              <a-select-option v-for="member,index in members" :key="member.id" :label="member.name+index">
                                  <h-avatar :name="member.name" :icon="member.icon"></h-avatar>
                                  <span class="domain-list-cell-subtext"> {{ member.description }} </span>
                              </a-select-option>  
                            </a-select>
                          </div>
                      </a-space>
                        </a-col>
                      </a-row>
                  </div>
                
                 <a-row v-if="conditionGroups[idx].conditions.length!=precondFields.length">
                    <a-col :span="24">
                      <div class="order-row order-row--bottom">
                          <a-icon type="plus-circle" style="font-size:18px;margin-left: 3px;" @click="onAddCondition(idx)" />
                          <vxe-button type="text" content="添加条件" style="margin-left: 10px;"
                              @click="onAddCondition(idx)" />
                      </div>
                    </a-col>
                 </a-row>
                </div>
            </div>
            <a-row style="border-top: 1px solid #e8e8e8;height:40px">
              <div @click="onAddConditionGroup" class="add-condition-group"><i class="vxe-icon-add"></i>添加条件组</div>
            </a-row>
          </div>         
        </a-collapse-panel>
  </a-collapse>
</template>

<script>

import moment from 'moment';
import XEUtils from "xe-utils";
import HAvatar from '@/components/avatar/h-avatar.vue';
export default {
  name: 'TrackerItemFilter',
  props: {
    conditionGroups: Array,
    activeKey: String,
    members: Array,
    tracker: Object,
  },
  components: {
    HAvatar
  },
  computed:{
    precondFields(){
      let fields=this.tracker.trackerFields || []
      this.isChange  //更新computed
      fields = fields.filter(f => {
        return f.systemProperty&&(f.inputType === 'INTEGER' || f.inputType === 'TEXT' || 
            f.inputType === 'STATUS'|| f.inputType === 'DATE' || f.inputType === 'USER'||
            f.inputType === 'OPTIONS'||f.inputType === 'WORK_ITEM_TYPE'||  f.inputType ==='SPRINT' ||
            f.inputType === 'STATUS_TYPE'||f.inputType === 'WORK_ITEM_NO')
      })
      return fields;
    },
  },
  data() {
    return {
      isChange:false,
    }
  },
  mounted() {
    
  },
  watch: {
    activeKey: {
        handler: function (newVal, oldVal) {
          if(newVal){
            if(!this.isChange){
              this.isChange=true;
            }
          }
        }
    },
    conditionGroups: {
      handler: function (newVal, oldVal) {
        if(newVal){
          this.conditionGroups.forEach(conditionGroup => {
            conditionGroup.conditions.forEach(condition => {
              if(condition.type=='DATE'){
                if(condition.operator!='BETWEEN'){
                  condition.conditionDate=moment(condition.value);
                }else{
                  condition.conditionRangeDate=[]
                  condition.value.forEach(value=>{
                    condition.conditionRangeDate.push(moment(value))
                  })
                }
              }
            })
          });
        }
      }
    }
  },
  methods: {
    conditionByFields: function (condition,idx) {
        let ids=[];
        if(condition){
          ids=this.conditionGroups[idx].conditions.map(f=>f.field).filter(f=>f!=condition.field);
        }else{
          ids=this.conditionGroups[idx].conditions.map(f=>f.field)
        }
        let result = this.precondFields.filter(f => { return ids.indexOf(f.systemProperty) < 0 })
        return result || []
    },
    onChangeConditionDate(row){ 
      if(row.conditionDate){
        row.value=moment(row.conditionDate).format("YYYY-MM-DD")
      }else{
        row.value=null;
      }
      this.refresh();
    },
    onChangeConditionRangeDate(row){
      row.value=[];
      row.value.push(moment(row.conditionRangeDate[0]).format("YYYY-MM-DD"))
      row.value.push(moment(row.conditionRangeDate[1]).format("YYYY-MM-DD"))
  
      this.refresh();
    },
     onChangeConditionInput(row){
      if(!this.inputDebounce){
          this.inputDebounce=XEUtils.debounce(function(){
            this.handlerConditionInput(row);
          },300)
      }
      this.inputDebounce(function(){
        this.handlerConditionInput(row);
      },300)
      
    },
    handlerConditionInput(row){
      if(row.operator=='BETWEEN'){
        row.value=null;
        if(row.value1&&row.value2){
          row.value=[row.value1,row.value2]
          this.refresh();
        }
      }else{
        this.refresh();
      }
    },
    onChangeConditionSelect(row){ 
      if(row.value.length==0){
        row.value=undefined;
      }
      this.refresh();
    },
    onChangeConditionOperator(row,a){
      if(row.operator=='BETWEEN'||row.conditionRangeDate){
        row.value=undefined;
        delete row.conditionDate;
        delete row.conditionRangeDate;
      }
      delete row.value1;
      delete row.value2;
      this.refresh();
    },
    onChangeCondition(row,field){
      row.type=field.inputType;
      // row.field=field.systemProperty;

      row.value=undefined;
      if(field.inputType==='INTEGER'||field.inputType==='DATE'){
        row.operator="EQ";
      }else if(field.inputType==='TEXT'||field.inputType==='WORK_ITEM_NO'||field.inputType==='STATUS'||field.inputType==='USER') {
        row.operator="INCL";
      }else{
        row.operator="INCL";
      } 

      delete row.conditionDate;
      delete row.conditionRangeDate;
      this.refresh();
    },
    removeCondition: function(orderByItem, idx,index){
        this.conditionGroups[idx].conditions.splice(index,1)
        if(this.conditionGroups[idx].conditions.length==0){
          this.conditionGroups.splice(idx,1)
        }
        this.refresh();
    },
    onAddConditionGroup(){
      let idx=this.conditionGroups.length;
      this.conditionGroups.push({
        conditions:[        
        ],
      })
      this.onAddCondition(idx);
      this.$nextTick(() => {
        this.$refs.filterRef.scrollTop=this.$refs.filterRef.scrollHeight
      })
    },
    onAddCondition:function(idx){
      let fields=this.conditionByFields(null,idx);
      if(fields.length>0){
        let len=this.conditionGroups[idx].conditions.length;
        this.conditionGroups[idx].conditions.push({
          id:fields[0].id,field:fields[0].systemProperty,type:'',value:'',operator:'',
        })
        this.onChangeCondition(this.conditionGroups[idx].conditions[len],fields[0])   
      }
         
    },
    refresh(){
      this.$emit('refresh');
    },
  },

}
</script>

<style scoped lang="less">
/deep/.ant-modal-body {
  height: 750px;
}
/deep/.ant-collapse-content > .ant-collapse-content-box {
    padding: 0;
}
.order-row {
    width: 440px;
    height: auto;
    cursor: move;

    .icon-circle {
        font-size: 18px;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 22px;
        height: 22px;
        // border: solid 1px currentColor;
        border-radius: 50%;
        cursor: pointer;
    }

    .icon-circle .disabled {
        font-size: 18px;
        opacity: .5;
        color: #909090;
        cursor: not-allowed;
        background-color: #e8e8e8;
        border-color: #979797;
    }
}
.add-condition-group{
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.add-condition-group:hover{
  color: #338fe5;
  background-color: #f3f3f3;
}
.group-line{
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  padding-bottom: 5px;
  .group-or{
    padding: 0 10px;
    background-color: rgb(250,250,250);
    position: relative;
    z-index: 1;
  }
}
.group-line::after{
    content: "";
    display: block;
    height: 1px;
    width: 100%;
    background-color: #e8e8e8;
    position: absolute;
}
.domain-list-cell-subtext {
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
.input-separate {
    color: #303030;
    position: relative;
    display: flex;
    align-items: center;
}
.input-separate::before {
    content: "";
    display: block;
    width: 13px;
    height: 39px;
    border: 1px solid #c8c8c8;
    border-left: transparent;
    position: absolute;
    left: -5px;
}
.input-separate .input-separate-label {
    width: 100%;
    background-color: #fff;
    position: relative;
    z-index: 1;
}
</style>
