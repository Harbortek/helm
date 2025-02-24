<template>
    <a-form-item :label="showLabel?'过滤条件':''" :style="showLabel?'':'margin-left: 90px;'">
      <div v-if="conditionMethod === 'SINGLE'">
        <a-select v-if="options" v-model="conditions[0].matchMethod" size="small" defaultValue="EQUAL" :style="{width: showLabel?'110px':'80px'}" style="width: 110px; margin-right: 8px;">
          <a-select-option v-for="(opt) in options" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
        </a-select>
        <a-input v-model="conditions[0].matchValue" size="small" placeholder="数值" :style="{width: showLabel?'200px':'150px'}" style="width: 200px; margin-right: 8px;" />
      </div>
      <div v-else style="display: flex;">
        <div>
          <div style="border: 1px dashed #ccc; border-radius: 4px; padding: 20px 30px;border-right: none;margin: 20px; margin-right: 10px;
            line-height:5px;">
            <div style="position:absolute;left: 13px;top:37px;height: 12px;line-height: 7px;background:#fff;">
            {{ conditionMethod=="OR"?"或":"且" }}
            </div>
          </div>
        </div>
        <div>
          <a-select v-model="conditions[0].matchMethod" size="small" defaultValue="EQUAL" :style="{width: showLabel?'110px':'80px'}" style="width: 110px; margin-right: 8px;">
            <a-select-option v-for="opt in options" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
          </a-select>
          <a-input v-model="conditions[0].matchValue" size="small" placeholder="数值" :style="{width: showLabel?'200px':'150px'}" style="width: 200px; margin-right: 8px;" /><br/>

          <a-select v-model="conditions[1].matchMethod" size="small" defaultValue="EQUAL" :style="{width: showLabel?'110px':'80px'}" style="width: 110px; margin-right: 8px;">
            <a-select-option v-for="opt in options" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
          </a-select>
          <a-input v-model="conditions[1].matchValue" size="small" placeholder="数值" :style="{width: showLabel?'200px':'150px'}" style="width: 200px; margin-right: 8px;" />
        </div>
        
      </div>
    </a-form-item>
</template>


<script>
import { left } from '@antv/g2plot/lib/plots/sankey/sankey';
import moment from 'moment';

export default {
  name: 'NumConditionFilter',
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
      required: true
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