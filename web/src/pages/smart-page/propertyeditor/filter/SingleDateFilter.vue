<template>
    <div>
      <a-form-item :label="label">
            <a-select v-if="options" v-model="conditions[0].relative" size="small" defaultValue="true" style="width: 182px; margin-right: 8px;">
              <a-select-option v-for="(opt) in [{value:'true',label:'相对时间'},{value:'false',label:'精确时间'}]" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
            </a-select>
      </a-form-item>
      <a-form-item :style="label=='过滤条件' ? 'margin-left: 60px;' : 'margin-left: 90px;'" style="width: 100%;">
            <template v-if="conditions[0].relative==='true'">
              <span style="font-size: 18px;">T&nbsp;</span>
              <a-select v-if="options" v-model="conditions[0].relativeUnit" size="small" @change="sigleInputChange" defaultValue="DAY" style="width: 50px; margin-right: 8px;">
                <a-select-option v-for="(opt) in getUnitOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
              </a-select>
              <a-select v-if="options" v-model="conditions[0].relativeDirection" size="small" @change="sigleInputChange" defaultValue="DAY" style="width: 50px; margin-right: 8px;">
                <a-select-option v-for="(opt) in [{value:'-',label:'-'},{value:'+',label:'+'}]" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
              </a-select>
              <a-input v-model="conditions[0].relativeValue" size="small" @change="sigleInputChange" placeholder="" style="width: 50px; margin-right: 8px;" />
            </template>
            <!-- <a-date-picker v-if="dateMode==='YYYY-MM-DD'" v-model="startDate" size="small" @change="sigleDateChange"/> -->
            <a-month-picker v-if="dateMode==='YYYY-MM'" v-model="startDate" size="small" @change="sigleDateChange"/>
            <!-- <a-date-picker v-show="dateMode==='YYYY'" mode="year" format="YYYY" v-model="startDate" size="small" 
              :open="yearShow" @change="sigleDateChange()" @panelChange="onPanelChange" @openChange="onOpenChange" /> -->
            <a-date-picker v-else :mode="dateMode==='YYYY' ? 'year' : 'date'" :format="dateMode" v-model="startDate" size="small" 
              :open="yearShow" @change="sigleDateChange()" @panelChange="onPanelChange" @openChange="onOpenChange" />

      </a-form-item>
    </div>
</template>


<script>
import moment from 'moment';

export default {
  name: 'DateRangeFilter',
  props: {
    options:{
      type: Array,
    },
    conditions: {
      type: Array,
      required: true
    },
    label:{
      type:String,
      default:'过滤条件'
    },
    dateMode:{
      type:String,
      default:'YYYY-MM-DD',
      required:false
    }
  },
  data() {
    return {
      startDate: null,
      endDate: null,
      yearShow: false,
    }
  },
  computed:{
    getUnitOptions(){
      let unitOption=[{value:'DAY',label:'日'},{value:'MONTH',label:'月'},{value:'YEAR',label:'年'}]
      if(this.dateMode==='YYYY-MM'){
        this.conditions[0].relativeUnit='MONTH'
        unitOption = [{value:'MONTH',label:'月'},{value:'YEAR',label:'年'}]
      }else if(this.dateMode==='YYYY'){
        this.conditions[0].relativeUnit='YEAR'
        unitOption = [{value:'YEAR',label:'年'}]
      }else{
        this.conditions[0].relativeUnit='DAY'
      }
      this.sigleInputChange()
      return unitOption;
    }
  },
  watch:{
    
  },
  mounted() {
    if(this.conditions){
      if(this.conditions[0].matchValue){
        this.startDate=moment(this.conditions[0].matchValue)
      }
    }
  },
  methods: {
    onPanelChange(value){

      if(this.dateMode==='YYYY'){
        this.startDate=value
        this.yearShow=false
        this.sigleDateChange();
      }
    },
    onOpenChange(status){
      this.yearShow=status
    },
    sigleDateChange(){
      this.conditions[0].matchValue=this.startDate?.format(this.dateMode) //'YYYY-MM-DD'

      let condition=this.conditions[0];
      if(this.startDate){
        let matchDate=moment(this.startDate.format(this.dateMode))
        let diffDate;
        if(condition.relativeDirection === '-'){
          diffDate=moment(moment().format(this.dateMode)).diff(matchDate,condition.relativeUnit)
        }else{
          diffDate=matchDate.diff(moment(moment().format(this.dateMode)),condition.relativeUnit)
        }
        console.log("aa",diffDate)
        condition.relativeValue=diffDate
      }
    },
    //时间单日
    sigleInputChange(){
      let condition=this.conditions[0];
      if(condition.relativeValue){
        if(condition.relativeDirection === '-'){
          console.log("---",condition.relativeValue,condition.relativeUnit)
          this.startDate=moment().subtract(condition.relativeValue,condition.relativeUnit)
        }else{
          this.startDate=moment().add(condition.relativeValue,condition.relativeUnit)
        }
      }
      console.log("cocococ",condition,this.startDate)
      condition.matchValue=this.startDate.format(this.dateMode)
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