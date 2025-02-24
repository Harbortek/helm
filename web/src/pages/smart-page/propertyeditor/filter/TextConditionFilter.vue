<template>
  <a-form-item l:label="showLabel?'过滤条件':''" :style="showLabel?'':'margin-left: 80px;'">
          <div style="margin-left: 10px;" v-if="filterMethod=== 'CONDITION' && conditionMethod === 'SINGLE'">
            <a-select v-if="options" v-model="conditions[0].matchMethod" size="small" defaultValue="STRICT_MATCH" :style="{width: showLabel?'110px':'90px'}" style=" margin-right: 8px;">
              <a-select-option v-for="(opt) in options" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
            </a-select>
            <a-input v-model="conditions[0].matchValue" size="small" placeholder="字符或值" :style="{width: showLabel?'210px':'150px'}" style="margin-right: 8px;" />
          </div>
          <div v-else-if="filterMethod=== 'CONDITION'" style="display: flex;">
            <div>
              <div style="border: 1px dashed #ccc; border-radius: 4px; padding: 20px 30px;border-right: none;margin: 20px; margin-right: 10px;
                line-height:5px;">
                <div style="position:absolute;left: 13px;top:37px;height: 12px;line-height: 7px;background:#fff;">
                {{ conditionMethod=="OR"?"或":"且" }}
                </div>
              </div>
            </div>
            <div>
              <a-select v-model="conditions[0].matchMethod" size="small" defaultValue="STRICT_MATCH" :style="{width: showLabel?'110px':'90px'}" style=" margin-right: 8px;">
                <a-select-option v-for="opt in options" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
              </a-select>
              <a-input v-model="conditions[0].matchValue" size="small" placeholder="字符或值" :style="{width: showLabel?'210px':'150px'}" style="margin-right: 8px;" /><br/>

              <a-select v-model="conditions[1].matchMethod" size="small" defaultValue="STRICT_MATCH" :style="{width: showLabel?'110px':'90px'}" style=" margin-right: 8px;">
                <a-select-option v-for="opt in options" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
              </a-select>
              <a-input v-model="conditions[1].matchValue" size="small" placeholder="字符或值" :style="{width: showLabel?'210px':'150px'}" style="margin-right: 8px;" />
            </div>
          </div>          
          <div v-else-if="queryMethod === 'SINGLE'">
            <a-select v-model="conditions[0].matchMethod" size="small" defaultValue="STRICT_MATCH" :style="{width: showLabel?'110px':'90px'}" style=" margin-right: 8px;">
              <a-select-option value="STRICT_MATCH">{{ '精确匹配' }}</a-select-option>
              <a-select-option value="NOT_MATCH">{{ '不匹配' }}</a-select-option>
            </a-select>
            <a-select v-model="conditions[0].matchValue" size="small" :style="{width: showLabel?'210px':'150px'}" style="margin-right: 8px;">
              <a-select-option v-for="opt in fieldOptions" :key="opt.id" :value="opt.text">{{ opt.text }}</a-select-option>
            </a-select>
          </div>
          <div v-else-if="queryMethod === 'MULTI'">
            <a-select v-model="conditions[0].matchMethod" size="small" defaultValue="STRICT_MATCH" :style="{width: showLabel?'110px':'90px'}" style=" margin-right: 8px;">
              <a-select-option value="STRICT_MATCH">{{ '精确匹配' }}</a-select-option>
              <a-select-option value="NOT_MATCH">{{ '不匹配' }}</a-select-option>
            </a-select>
            <a-select mode="multiple" size="small" v-model="conditions[0].matchValue" :style="{width: showLabel?'210px':'150px'}" style="margin-right: 8px;">
              <a-select-option v-for="opt in fieldOptions" :key="opt.id" :value="opt.text">{{ opt.text }}</a-select-option>
            </a-select>
          </div>
  </a-form-item>
</template>


<script>
import moment from 'moment';

export default {
  name: 'TextConditionFilter',
  props: {
    options:{
      type:Array,
      required:false,
    },
    conditions: {
      type: Array,
      required: true
    },
    conditionMethod: {
      type: String,
      required: false
    },
    filterMethod:{
      type:String,
      default:'CONDITION'
    },
    queryMethod:{
      type:String,
      required:false
    },
    fieldOptions:{
      type:Array,
      required:false
    },
    showLabel:{
      type:Boolean,
      default:true
    }
  },
  data() {
    return {
      
    }
  },
  mounted() {
  },
  methods: {
    sigleDateChange(type){
        let condition=this.conditions[0];
        if(type=='END'){
            condition=this.conditions[1];
        }
        if(condition.matchValue){
            let matchDate=moment(condition.matchValue.format('YYYY-MM-DD'))
            let diffDate;
            if(condition.relativeDirection === '-'){
            diffDate=moment(moment().format('YYYY-MM-DD')).diff(matchDate,condition.relativeUnit)
            }else{
            diffDate=matchDate.diff(moment(moment().format('YYYY-MM-DD')),condition.relativeUnit)
            }

            condition.relativeValue=diffDate
            console.log("aa",condition.relativeValue)
        }
    },
    //时间单日
    sigleInputChange(type){
        let condition=this.conditions[0];
        if(type=='END'){
            condition=this.conditions[1];
        }
        if(condition.relativeValue){
            if(condition.relativeDirection === '-'){
            condition.matchValue=moment().subtract(condition.relativeValue,condition.relativeUnit)
            }else{
            condition.matchValue=moment().add(condition.relativeValue,condition.relativeUnit)
            }
        }
    },
    
  }
}
</script>

<style scoped>
::v-deep .ant-form-item-label > label {
  font-size: 12px;
}
::v-deep span{
  font-size: 12px;
}
::v-deep input,div{
  font-size: 12px;
}
</style>