<template>
  <a-modal v-model="visiable" width="510px" @ok="onOK" @cancel="onCancel">      
    <template slot="title">
      <span style="font-size: 14px;font-weight: 600;">{{title}}</span>
    </template>
    <template slot="footer">
      <vxe-button content="取消" size="small" @click="onCancel"></vxe-button>
      <vxe-button status="primary" size="small" @click="onOK" content="确认"></vxe-button>
    </template>
    <a-form layout="inline" :colon="false" ref="xForm">
      <div style="width: 100%;display: flex;">
        <a-form-item label="数据集" style="width: 45%;">
          <div  style="width: 100%;">
            <a-icon type="table" style="margin-right: 5px;" />
            <span class="item-span-style">{{dataset.name}}</span>
          </div>
          
        </a-form-item>
        <a-form-item label="选中字段" style="width: 45%;">
          <!-- <a-icon component="field_type_text" style="color: #222222;" v-show="item.type === 'TEXT'" />
          <a-icon component="field_type_number" style="color: cadetblue;" v-show="item.type === 'NUM'" />
          <a-icon component="date" style="color:cadetblue;" v-show="item.type === 'DATE'" />&nbsp;
          <span>{{ item.name }}</span> -->
          <div style="width: 140px">
            <a-tag size="small" class="item-axis" color="blue">
              <span style="float: left">
                <a-icon component="field_type_text" style="color: #222222;" v-show="item.type === 'TEXT'" />
                <a-icon component="date" style="color:cadetblue;" v-show="item.type === 'DATE'" />
                <a-icon component="field_type_number" style="color: cadetblue;" v-show="item.type === 'NUM'" />
              </span>
              <span class="item-span-style" :title="item.name">{{ item.name }}</span>
            </a-tag>
          </div>
          
        </a-form-item>
      </div>
      <div style="margin-left: 15px; margin-top: 5px;" v-if="item.type === 'NUM'">
        <a-form-item label="条件形式">
          <a-radio-group v-model="conditionMethod" :defaultValue="'SINGLE'" @change="conditionMethodChange">
            <a-radio value="SINGLE">单条件</a-radio>
            <a-radio value="OR">或条件</a-radio>
            <a-radio value="AND">且条件</a-radio>
          </a-radio-group>
        </a-form-item><br/>
        <NumConditionFilter :conditionMethod="conditionMethod" :conditions="conditions" :options="options"></NumConditionFilter>
      </div>

      <div style="margin-left: 15px; margin-top: 5px;" v-if="item.type === 'DATE'">
        <a-form-item label="过滤方式">
          <a-radio-group v-model="filterMethod" :defaultValue="'SINGLE_DATE'" @change="filterMethodChange">
            <a-radio value="SINGLE_DATE">单日</a-radio>
            <a-radio value="DATE_RANGE">日区间</a-radio>
          </a-radio-group>
        </a-form-item><br/>
        <a-form-item v-if="filterMethod === 'DATE_RANGE'" label="区间类型">
          <a-radio-group v-model="rangeType" :defaultValue="'START'" @change="rangeTypeChange">
            <a-radio value="START">开始于</a-radio>
            <a-radio value="END">结束于</a-radio>
            <a-radio value="BOTH">时间区间</a-radio>
          </a-radio-group>
        </a-form-item><br v-if="filterMethod === 'DATE_RANGE'" />

        <single-date-filter v-if="filterMethod === 'SINGLE_DATE'" :conditions="conditions" :options="options"></single-date-filter>
        <date-range-filter v-if="filterMethod === 'DATE_RANGE'" :rangeType="rangeType" :conditions="conditions"
          ></date-range-filter>
      </div>

      <!-- enum -->
      <div style="margin-left: 15px; margin-top: 5px;" v-if="item.type === 'TEXT'">
        <a-form-item label="过滤方式">
          <a-radio-group v-model="filterMethod" :defaultValue="'SINGLE_DATE'" @change="filterMethodChange">
            <a-radio value="CONDITION">按条件过滤</a-radio>
            <a-radio value="ENUM">按枚举过滤</a-radio>
          </a-radio-group>
        </a-form-item><br/>
        <a-form-item v-if="filterMethod === 'CONDITION'" label="条件形式">
          <a-radio-group v-model="conditionMethod" :defaultValue="'SINGLE'" @change="conditionMethodChange">
            <a-radio value="SINGLE">单条件</a-radio>
            <a-radio value="OR">或条件</a-radio>
            <a-radio value="AND">且条件</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item v-if="filterMethod === 'ENUM'" label="查询方式">
          <a-radio-group v-model="queryMethod" :defaultValue="'SINGLE'">
            <a-radio value="SINGLE">单选</a-radio>
            <a-radio value="MULTI">多选</a-radio>
          </a-radio-group>
        </a-form-item><br/>
        
        <TextConditionFilter :filterMethod="filterMethod" :conditionMethod="conditionMethod" :conditions="conditions" 
          :queryMethod="queryMethod" :fieldOptions="fieldOptions" :options="options"></TextConditionFilter>
      </div>
    </a-form>

  </a-modal>
</template>

<script>
import { findEnumValues } from '@/services/smart-page/DatasetService'
import DateRangeFilter from './DateRangeFilter.vue';
import SingleDateFilter from './SingleDateFilter.vue';
import NumConditionFilter from './NumConditionFilter.vue';
import TextConditionFilter from './TextConditionFilter.vue';
import moment from 'moment';

export default {
  name: 'ResultFilterEditor',
  components: {
    DateRangeFilter,NumConditionFilter,TextConditionFilter,SingleDateFilter
  },
  props: {
    isShowDialog: {
      required: true
    },
    item: {
      type: Object,
      required: true
    },
    dataset: {
      type: Object,
      required: true
    },
    pageId: {
      type: String,
    }
  },
  data() {
    return {
      textOptions: [
        {
          value: 'STRICT_MATCH',
          label: this.$t('精确匹配')
        }, {
          value: 'NOT_MATCH',
          label: this.$t('不匹配')
        }, {
          value: 'CONTAINS',
          label: this.$t('chart.filter_like')
        }, {
          value: 'NOT_CONTAINS',
          label: this.$t('chart.filter_not_like')
        }, {
          value: 'START_WITH',
          label: this.$t('开头是')
        }, {
          value: 'END_WITH',
          label: this.$t('结尾是')
        },{
          value: 'IS_NULL',
          label: this.$t('chart.filter_null')
        }, {
          value: 'IS_NOT_NULL',
          label: this.$t('chart.filter_not_null')
        }, {
          value: 'IS_EMPTY',
          label: this.$t('chart.filter_empty')
        }, {
          value: 'IS_NOT_EMPTY',
          label: this.$t('chart.filter_not_empty')
        }],
      dateOptions: [
        {
          value: 'EQUAL',
          label: this.$t('chart.filter_eq')
        }, {
          value: 'NOT_EQUAL',
          label: this.$t('chart.filter_not_eq')
        }, {
          value: 'LESS_THAN',
          label: this.$t('chart.filter_lt')
        }, {
          value: 'GREATER_THAN',
          label: this.$t('chart.filter_gt')
        }, {
          value: 'LESS_THAN_OR_EQUAL',
          label: this.$t('chart.filter_le')
        }, {
          value: 'GREATER_THAN_OR_EQUAL',
          label: this.$t('chart.filter_ge')
        }],
      valueOptions: [
        {
          value: 'EQUAL',
          label: this.$t('chart.filter_eq')
        }, {
          value: 'NOT_EQUAL',
          label: this.$t('chart.filter_not_eq')
        }, {
          value: 'LESS_THAN',
          label: this.$t('chart.filter_lt')
        }, {
          value: 'GREATER_THAN',
          label: this.$t('chart.filter_gt')
        }, {
          value: 'LESS_THAN_OR_EQUAL',
          label: this.$t('chart.filter_le')
        }, {
          value: 'GREATER_THAN_OR_EQUAL',
          label: this.$t('chart.filter_ge')
        }],
      options: [],
      logic: '',
      filterType: '',
      enumCheckField: [],
      fieldOptions: [],

      visible: true,
      selectedDataset: '任务明细',
      selectedField: '任务明细',
      selectedField2: '实际工时',
      conditionMethod: 'SINGLE', //MULTI RANGE AND OR
      conditions: [{matchMethod:undefined,matchValue: undefined},{matchMethod:undefined,matchValue: undefined}],
      filterMethod: undefined,// ENUM SINGLE_DATE DATE_RANGE
      queryMethod: 'SINGLE', //SINGLE MULTI
      rangeType: undefined,
      loading: false,
    }
  },
  computed: {
    visiable: {
      get() {
        return this.isShowDialog
      },
      set(newValue) {
        return newValue
      }
    },
    title() {
      return this.$t('chart.add_filter') + ' - ' + this.item.name
    }
  },
  watch: {
    'item': function () {
      console.log("this.item",this.item,this.dataset)

      this.conditions=this.item.conditions||[{matchMethod:undefined,matchValue: undefined,relative: 'true',relativeUnit: 'DAY',relativeDirection: '-',relativeValue: ''}]
      if(this.conditions.length === 1){
        this.conditions.push({matchMethod:undefined,matchValue: undefined,relative: 'true',relativeUnit: 'DAY',relativeDirection: '-',relativeValue: ''})
      }
      if(this.item.type === 'DATE'){
        this.filterMethod=this.item.filterMethod||'SINGLE_DATE'
        this.rangeType=this.item.rangeType||'START'
        this.conditions.forEach((item)=>{
          if(item.matchValue){
            item.matchValue=moment(item.matchValue)
          }
        })
        if(this.rangeType === 'END'){
          this.conditions[1]=this.conditions[0]
          this.conditions.splice(0,1,{matchMethod:undefined,matchValue: undefined,relative: 'true',relativeUnit: 'DAY',relativeDirection: '-',relativeValue: ''})
        }
      }else if(this.item.type === 'NUM'){
        this.conditionMethod=this.item.conditionMethod||'SINGLE'
        this.conditions.forEach((item)=>{
          if(!item.matchMethod){
            item.matchMethod='EQUAL'
          }
        })
      }else if(this.item.type === 'TEXT'){
        this.conditionMethod=this.item.conditionMethod||'SINGLE'
        this.filterMethod=this.item.filterMethod||'CONDITION'
        this.queryMethod=this.item.queryMethod||'SINGLE'
        this.conditions.forEach((item)=>{
          if(!item.matchMethod){
            item.matchMethod='STRICT_MATCH'
          }
          if(this.queryMethod === 'MULTI' && item.matchValue){
            item.matchValue=JSON.parse(item.matchValue)
          }
        })
      }
      this.initOptions()
      this.initEnumOptions();
    }
  },
  mounted() {
    this.initOptions()
    this.init()
    this.initEnumOptions()
  },
  methods: {
    // addCondition() {
    //   this.conditions.push({});
    // },
    initOptions() {
      if (this.item) {
        if (this.item.type == 'TEXT') {
          this.options = JSON.parse(JSON.stringify(this.textOptions))
        } else if (this.item.type == 'DATE') {
          this.options = JSON.parse(JSON.stringify(this.dateOptions))
        } else {
          this.options = JSON.parse(JSON.stringify(this.valueOptions))
        }
      }
    },
    init() {
      this.logic = this.item.logic
      this.filterType = this.item.filterType
      this.enumCheckField = this.item.enumCheckField
    },
    initEnumOptions() {
      // 查找枚举值
      if (this.item.type === 'TEXT') {
        findEnumValues(this.pageId,this.dataset.id,this.item.field).then(res => {
          console.log("枚举值",res)
          this.fieldOptions = this.optionData(res)
        })
      }
    },

    optionData(data) {
      if (!data) return null
      return data.filter(item => !!item).map(item => {
        return {
          id: item,
          text: item
        }
      })
    },
    // addFilter() {
    //   this.item.filter.push({
    //     fieldId: this.item.id,
    //     term: 'eq',
    //     value: ''
    //   })
    // },
    // removeFilter(index) {
    //   this.item.filter.splice(index, 1)
    // },
    conditionMethodChange(e){
      let value=e.target.value
      console.log("val",value,this.conditions)
      this.conditionMethod = value
      if(this.item.type == "TEXT"){
        this.conditions.forEach((item)=>{
          item.matchMethod='STRICT_MATCH'
          item.matchValue=undefined
        })
      }
    },
    filterMethodChange(){
      this.rangeType='START'
      if(this.filterMethod === 'SINGLE_DATE'){
        this.conditions=[{matchMethod:undefined,matchValue: undefined,relative: 'true',relativeUnit: 'DAY',relativeDirection: '-',relativeValue: ''}]
      }else{
        this.conditions=[{matchMethod:undefined,matchValue: undefined,relative: 'true',relativeUnit: 'DAY',relativeDirection: '-',relativeValue: ''}]
        this.conditions.push({matchMethod:undefined,matchValue: undefined,relative: 'true',relativeUnit: 'DAY',relativeDirection: '-',relativeValue: ''})
      }
      if(this.item.type === 'TEXT'){
        this.conditions.forEach((item)=>{
          if(!item.matchMethod){
            item.matchMethod='STRICT_MATCH'
          }
        })
      }

    },
    rangeTypeChange(){
      if(this.rangeType === 'END'||this.rangeType === 'BOTH'){
        this.conditions.push({matchMethod:undefined,matchValue: '',relative: 'true',relativeUnit: 'DAY',relativeDirection: '-',relativeValue: ''})
      }
    },
    // logicChange(val) {
    //   this.item.logic = val
    // },
    // filterTypeChange(val) {
    //   this.item.filterType = val
    // },
    // enumChange(val) {
    //   this.item.enumCheckField = this.enumCheckField
    // },
    onOK() {
      this.item.conditions = this.conditions
      if(this.item.type === 'NUM'){
        // this.item.filterMethod=this.item.filterMethod||'CONDITION'
        this.item.conditionMethod = this.conditionMethod
      }else if(this.item.type === 'DATE'){
        this.item.filterMethod=this.filterMethod
        this.item.rangeType=this.rangeType
        this.item.conditions.forEach((item)=>{
          item.matchValue=item.matchValue?.format('YYYY-MM-DD')
        })
        if(this.rangeType === 'END'){
          this.item.conditions=[this.item.conditions[1]]
        }else if(this.rangeType === 'START'){
          this.item.conditions=[this.item.conditions[0]]
        }
      }else if(this.item.type === 'TEXT'){
        this.item.filterMethod=this.filterMethod
        this.item.conditionMethod=this.conditionMethod
        this.item.queryMethod=this.queryMethod
        this.item.conditions.forEach((item)=>{
          if(item.matchValue&&item.matchValue instanceof Array){
            item.matchValue=JSON.stringify(item.matchValue)
          }
        })
      }
      console.log("onOk",this.item)
      this.$emit("ok", this.item);
    },
    onCancel() {
      this.$emit("cancel");
    }
  }
}
</script>

<style scoped>
.filter-item {
  width: 100%;
  border-radius: 4px;
  border: 1px solid #DCDFE6;
  padding: 4px 14px;
  margin-bottom: 10px;
  display: flex;
  justify-content: left;
  align-items: center;
}

.form-item ::v-deep .a-form-item__label {
  font-size: 12px;
}

span {
  font-size: 12px;
}

.value-item ::v-deep .a-input {
  position: relative;
  display: inline-block;
  width: 80px !important;
}
.item-axis {
  margin: 0 3px 2px 3px;
}
.item-span-style {
  display: inline-block;
  width: 140px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  vertical-align: bottom;
}
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
