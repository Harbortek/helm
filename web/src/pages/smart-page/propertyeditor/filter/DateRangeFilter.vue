<template>
    <div>
        <a-form-item :label="showLabel?'过滤条件':''" :style="showLabel?'':'margin-left: 90px;width: 100%;'">
            <template v-if="rangeType==='START'||rangeType==='BOTH'||rangeType==='START_WITH'||rangeType==='BETWEEN'">
            开始于
            <a-month-picker v-if="dateMode==='YYYY-MM'" mode="year" v-model="startDate" size="small" @change="sigleDateChange()" style="width: 140px;margin-left: 5px;margin-right: 5px;" />
              <a-date-picker v-else :mode="dateMode==='YYYY' ? 'year' : 'date'" :format="dateMode" v-model="startDate" size="small" 
              :open="startYearShow" @change="sigleDateChange()" @panelChange="e=>onPanelChange(e,'start')" @openChange="e=>onOpenChange(e,'start')" style="width: 140px;margin-left: 5px;margin-right: 5px;" />
            </template>
            <template v-if="rangeType==='END'||rangeType==='BOTH'||rangeType==='END_WITH'||rangeType==='BETWEEN'">
            结束于
            <a-month-picker v-if="dateMode==='YYYY-MM'" v-model="endDate" size="small" @change="sigleDateChange('END')" style="width: 140px;margin-left: 5px;" />
              <a-date-picker v-else :mode="dateMode==='YYYY' ? 'year' : 'date'" :format="dateMode" v-model="endDate" size="small" 
              :open="endYearShow" @change="sigleDateChange()" @panelChange="e=>onPanelChange(e,'end')" @openChange="e=>onOpenChange(e,'end')" style="width: 140px;margin-left: 5px;margin-right: 5px;" />
            </template>
        </a-form-item>

        <a-form-item :style="showLabel?'margin-left: 60px;':'margin-left: 90px;'">
          <div style="display: flex;">
            <div v-if="rangeType==='START'||rangeType==='BOTH'||rangeType==='START_WITH'||rangeType==='BETWEEN'">
              <a-select v-if="conditions" v-model="conditions[0].relative" size="small" defaultValue="true" style="width: 182px; margin-right: 8px;">
                <a-select-option v-for="(opt) in [{value:'true',label:'相对时间'},{value:'false',label:'精确时间'}]" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
              </a-select><br/>
              <template v-if="conditions[0].relative==='true'">
                <span style="font-size: 18px;">T&nbsp;</span>
                  <a-select v-if="conditions" v-model="conditions[0].relativeUnit" size="small" @change="sigleInputChange" defaultValue="DAY" style="width: 50px; margin-right: 8px;">
                    <a-select-option v-for="(opt) in getUnitOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
                  </a-select>
                  <a-select v-if="conditions" v-model="conditions[0].relativeDirection" size="small" @change="sigleInputChange" defaultValue="DAY" style="width: 50px; margin-right: 8px;">
                    <a-select-option v-for="(opt) in [{value:'-',label:'-'},{value:'+',label:'+'}]" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
                  </a-select>
                  <a-input v-model="conditions[0].relativeValue" size="small" @change="sigleInputChange" placeholder="" style="width: 50px; margin-right: 8px;" />
              </template>
            </div>
            <div v-if="rangeType==='END'||rangeType==='BOTH'||rangeType==='END_WITH'||rangeType==='BETWEEN'">
              <a-select v-if="conditions" v-model="conditions[1].relative" size="small" @change="sigleInputChange('END')"  defaultValue="true" style="width: 182px; margin-right: 8px;">
                <a-select-option v-for="(opt) in [{value:'true',label:'相对时间'},{value:'false',label:'精确时间'}]" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
              </a-select><br/>
              <template v-if="conditions[1].relative==='true'">
                <span style="font-size: 18px;">T&nbsp;</span>
                <a-select v-if="conditions" v-model="conditions[1].relativeUnit" size="small" @change="sigleInputChange('END')" defaultValue="DAY" style="width: 50px; margin-right: 8px;">
                  <a-select-option v-for="(opt) in getUnitOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
                </a-select>
                <a-select v-if="conditions" v-model="conditions[1].relativeDirection" size="small" @change="sigleInputChange('END')" defaultValue="DAY" style="width: 50px; margin-right: 8px;">
                  <a-select-option v-for="(opt) in [{value:'-',label:'-'},{value:'+',label:'+'}]" :key="opt.value" :value="opt.value">{{ opt.label }}</a-select-option>
                </a-select>
                <a-input v-model="conditions[1].relativeValue" size="small" @change="sigleInputChange('END')" placeholder="" style="width: 50px; margin-right: 8px;" />
              </template>
            </div>
          </div>
        </a-form-item>
    </div>
</template>


<script>
import moment from 'moment';

export default {
  name: 'DateRangeFilter',
  props: {
    conditions: {
      type: Array,
      required: true
    },
    rangeType: {
      type: String,
      required: true
    },
    showLabel:{
      type:Boolean,
      default:true
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
      startYearShow: false,
      endYearShow: false,
    }
  },
  watch:{
    
  },
  mounted() {
    if(this.conditions){
      if(this.conditions[0].matchValue){
        this.startDate=moment(this.conditions[0].matchValue)
      }
      if(this.conditions[1].matchValue){
        this.endDate=moment(this.conditions[1].matchValue)
      }
    }
  },
  computed: {
    getUnitOptions(){
      let unitOption=[{value:'DAY',label:'日'},{value:'MONTH',label:'月'},{value:'YEAR',label:'年'}]
      if(this.dateMode==='YYYY-MM'){
        this.conditions[0].relativeUnit='MONTH'
        this.conditions[1].relativeUnit='MONTH'
        unitOption= [{value:'MONTH',label:'月'},{value:'YEAR',label:'年'}]
      }else if(this.dateMode==='YYYY'){
        this.conditions[0].relativeUnit='YEAR'
        this.conditions[1].relativeUnit='YEAR'
        unitOption= [{value:'YEAR',label:'年'}]
      }else{
        this.conditions[0].relativeUnit='DAY'
        this.conditions[1].relativeUnit='DAY'
      }
      this.sigleInputChange()
      this.sigleInputChange('END')
      return unitOption
    }
  },
  methods: {
    onPanelChange(value,type){
      if(this.dateMode==='YYYY'){
        if(type=='start'){
          this.startYearShow=false
          this.startDate=value
        }else{
          this.endYearShow=false
          this.endDate=value
        }
      }
    },
    onOpenChange(status,type){
      if(type=='start'){
        this.startYearShow=status
      }else{
        this.endYearShow=status
      }
    },
    sigleDateChange(type){
        let condition=this.conditions[0];
        if(type=='END'){
          this.conditions[1].matchValue=this.endDate.format(this.dateMode)//'YYYY-MM-DD'
            condition=this.conditions[1];
        }else{
          this.conditions[0].matchValue=this.startDate.format(this.dateMode)
        }
        if(condition.matchValue){
            let matchDate=moment(condition.matchValue.format(this.dateMode))
            let diffDate;
            if(condition.relativeDirection === '-'){
            diffDate=moment(moment().format(this.dateMode)).diff(matchDate,condition.relativeUnit)
            }else{
            diffDate=matchDate.diff(moment(moment().format(this.dateMode)),condition.relativeUnit)
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
          let matchDate=null;
            if(condition.relativeDirection === '-'){
              matchDate=moment().subtract(condition.relativeValue,condition.relativeUnit)
            }else{
              matchDate=moment().add(condition.relativeValue,condition.relativeUnit)
            }
            if(type=='END'){
              this.endDate=matchDate;
            }else{
              this.startDate=matchDate;
            }
            condition.matchValue=matchDate.format(this.dateMode)
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