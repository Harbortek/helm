<template>
    
    <div :class="{'read-only-div':readOnly,'col-full':isTable(fields),'wrapper':true}">
        <a-input v-if="fields.inputType == 'TEXT'" :bordered="false" :disabled="readOnly"
            @blur="onBlurTextField(fields.id)" :placeholder="readOnly?'':fields.name"
            v-model="newValue" />
        <a-textarea v-else-if="fields.inputType == 'TEXT_AREA'"
            @blur="onBlurTextField(fields.id)" :disabled="readOnly"
            :placeholder="readOnly?'':fields.name" v-model="newValue"
            auto-size />
        <simple-editor v-else-if="fields.inputType == 'WIKI'"
            v-model="newValue" :disabled="readOnly" style="margin-top: 10px"
            :showToolbar="editorInEditMode"
            @focus="editorInEditMode = true"
            @blur="onBlurSimpleEditor(fields.id)" />

        <tracker-status-select v-else-if="fields.inputType == 'STATUS'&&trackerId" 
            v-model="newValue" :disabled="readOnly" :trackerId="trackerId" :placeholder="readOnly?'':fields.name"
            :selectMultiple="false" @change="(v)=>onChangeCustomerField(fields.id,v)" />
        <tracker-status-type-select v-else-if="fields.inputType == 'STATUS_TYPE'"
            v-model="newValue" :disabled="readOnly" :placeholder="readOnly?'':fields.name"
            @change="(e,v)=>onChangeCustomerField(fields.id,v)"/>
        <project-select v-else-if="fields.inputType == 'PROJECT'"  
            v-model="newValue" :disabled="readOnly"  :placeholder="readOnly?'':fields.name"
            @change="(v)=>onChangeCustomerField(fields.id,v)"></project-select>
        
        <sprint-select v-else-if="fields.system&&fields.systemProperty=='sprintId'"
            v-model="newValue" :projectId="projectId" :disabled="readOnly" :placeholder="readOnly?'':fields.name"
                @change="(v,e)=>onChangeCustomerField(fields.id,v)"/>
        <a-select v-else-if="fields.inputType == 'OPTIONS'&&fields.system" allowClear
            :placeholder="readOnly?'':fields.name"  :disabled="readOnly"
            v-model="newValue">
            <a-select-option
                @click="onChangeCustomerField(fields.id, item.id)"
                :value="item.id" v-for="item in fields.items"
                :key="item.id">{{item.name}}</a-select-option>
        </a-select>
        <a-select v-else-if="fields.inputType == 'OPTIONS'" allowClear
              :placeholder="readOnly?'':fields.name"  :disabled="readOnly"
            v-model="newValue">
            <a-select-option
                @click="onChangeCustomerField(fields.id, item.name)"
                :value="item.name" v-for="item in fields.items"
                :key="item.id">{{item.name}}</a-select-option>
        </a-select>
        <a-select v-else-if="fields.inputType == 'MULTI_OPTIONS'"
            :mode="'multiple'" :disabled="readOnly"
            @change="e => onChangeCustomerField(fields.id, e)"
            :placeholder="readOnly?'':fields.name" v-model="newValue">
            <a-select-option :value="item.name" v-for="item in fields.items"
                :key="item.id">{{ item.name }}</a-select-option>
        </a-select>
        <a-select v-else-if="fields.inputType == 'BOOL'"
            @change="onBlurTextField(fields.id)" :disabled="readOnly"
            v-model="newValue" :placeholder="readOnly?'':fields.name">
            <a-select-option value="ture">是</a-select-option>
            <a-select-option value="false">否</a-select-option>
        </a-select>

        <a-date-picker v-else-if="fields.inputType == 'DATE'"
            style="width:100%;min-width: 10px;" v-model="newValue" show-time :disabled="readOnly"
            @change="onChangeDatePicker(fields.id)"
            :placeholder="readOnly?'':fields.name" />
        <a-time-picker v-else-if="fields.inputType == 'TIME'"
            style="width:100%;min-width: 10px;" use24-hours :disabled="readOnly"
            v-model="newValue"
            @change="onChangeTimePicker(fields.id)" />
        <a-range-picker v-else-if="fields.inputType == 'DURATION'"
            format="YYYY-MM-DD HH:mm:ss"  style="width:90%;min-width: 10px;"
            :disabled="readOnly"
            v-model="newValue"
            :show-time="{ format: 'HH:mm:ss' }"
            @change="onChangeRangePicker(fields.id)" />

        <project-user-select v-else-if="fields.inputType == 'USER'" :placeholder="readOnly?'':fields.name"
            @change="onChangeUserSelect(fields.id)" :disabled="readOnly"
            v-model="newValue" :projectId="projectId" />
        <project-user-select v-else-if="fields.inputType == 'MEMBERS'" :placeholder="readOnly?'':fields.name"
            @change="onChangeUserSelect(fields.id)" :disabled="readOnly"
            v-model="newValue" :mode="'multiple'"
            :projectId="projectId" />
        <tracker-select v-else-if="fields.inputType == 'WORK_ITEM'||fields.inputType == 'WORK_ITEM_TYPE'"
            v-model="newValue" :disabled="readOnly" :placeholder="readOnly?'':fields.name"
            :projectId="projectId"
            @change="onChangeCustomerField(fields.id,newValue)"></tracker-select>
        <div v-else-if="isTable(fields)">
            <vxe-toolbar v-if="!readOnly">
                <template #tools>
                    <vxe-button type="primary"
                        @click="onAddTableColumn(fields)"
                        size="mini">添加</vxe-button>
                    <vxe-button type="primary"
                        @click="onTableDataSave(fields)"
                        size="mini">保存</vxe-button>
                </template>
            </vxe-toolbar>
            <vxe-table :ref="'customerFiled-' + fields.id" size="mini"
                :data="transformTableData(newValue)"
                :row-config="{ isHover: true }" height="200"
                :edit-config="{ trigger: 'click', mode: 'cell', icon: 'vxe-icon-edit', showStatus: true }">
                <vxe-column type="seq" title="序号" header-align="center"
                    align="left" width="60">
                </vxe-column>
                <vxe-column :title="column.name" :field="column.id"
                    v-for="column in fields.columns" :key="column.id"
                    header-align="center" align="center"
                    :edit-render="{}">
                    <template #default="{ row }">
                        {{ row[column.id] }}
                    </template>
                    <template #edit="{ row }">
                        <a-input v-if="column.inputType == 'TEXT'"
                            :placeholder="readOnly?'':fields.name"
                            v-model="row[column.id]" />
                        <a-textarea
                            v-else-if="column.inputType == 'TEXT_AREA'"
                            :placeholder="readOnly?'':fields.name"
                            v-model="row[column.id]" auto-size />
                        <simple-editor v-else-if="fields.inputType == 'WIKI'"
                            v-model="row[column.id]"
                            :showToolbar="false" />

                        <a-select
                            v-else-if="column.inputType == 'OPTIONS'"
                            :placeholder="readOnly?'':fields.name"
                            v-model="row[column.id]">
                            <a-select-option :value="item.name"
                                v-for="item in column.items"
                                :key="item.id">{{ item.name }}</a-select-option>
                        </a-select>
                        <a-select
                            v-else-if="column.inputType == 'MULTI_OPTIONS'"
                            :mode="'multiple'" :placeholder="readOnly?'':fields.name"
                            v-model="row[column.id]">
                            <a-select-option :value="item.name"
                                v-for="item in column.items"
                                :key="item.id">{{ item.name }}</a-select-option>
                        </a-select>
                        <a-select v-else-if="column.inputType == 'BOOL'"
                            v-model="row[column.id]"
                            :placeholder="readOnly?'':fields.name">
                            <a-select-option
                                value="ture">是</a-select-option>
                            <a-select-option
                                value="false">否</a-select-option>
                        </a-select>

                        <a-date-picker
                            v-else-if="column.inputType == 'DATE'"
                            style="width:100%" v-model="row[column.id]"
                            :placeholder="readOnly?'':fields.name" />
                        <a-time-picker
                            v-else-if="column.inputType == 'TIME'"
                            style="width:100%" use24-hours
                            v-model="row[column.id]" />

                        <project-user-select
                            v-else-if="column.inputType == 'USER'"
                            v-model="row[column.id]"
                            :projectId="projectId" />
                        <project-user-select
                            v-else-if="column.inputType == 'MEMBERS'"
                            v-model="row[column.id]" :mode="'multiple'"
                            :projectId="projectId" />
                        <tracker-select
                            v-else-if="column.inputType == 'WORK_ITEM'"
                            v-model="frow[column.id]"
                            :projectId="projectId">
                        </tracker-select>
                    </template>
                </vxe-column>

                <vxe-column title="操作" header-align="center" v-if="!readOnly"
                    align="center" width="60">
                    <template #default="{ row }">
                        <vxe-button type="text" circle
                            icon="vxe-icon-delete"
                            @click="onDeleteTableColumn(fields, row)" />
                    </template>
                </vxe-column>
            </vxe-table>
        </div>
        <!--aaa-->
        <a-input v-else :bordered="false" :disabled="readOnly"
            @blur="onBlurTextField(fields.id)" :placeholder="readOnly?'':fields.name" 
            v-model="newValue" />
    </div>
</template>

<script>
import moment from 'moment'
import SimpleEditor from '@/components/editor/SimpleEditor.vue';
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import SprintSelect from '@/components/select/SprintSelect.vue';
import TrackerSelect from '@/components/select/TrackerSelect.vue';
import ProjectSelect from "@/components/select/ProjectSelect.vue";
import TrackerStatusSelect from '@/components/select/TrackerStatusSelect.vue';
import TrackerStatusTypeSelect from '@/components/select/TrackerStatusTypeSelect.vue';
export default {
  name: 'ItemCustomFieldsShow',
  components: {SimpleEditor,ProjectUserSelect,TrackerSelect,SprintSelect,ProjectSelect,
    TrackerStatusSelect, TrackerStatusTypeSelect},
  model: {
    prop: "value", 
    event: "input"
  },
  props: {
    projectId: {
      type: String,
    },
    trackerId: {
      type: String,
      required: false,
    },
    value:{
      required: true,
    },
    fields: {
      type: Object,
      required: true,
    },
    readOnly: {
      type: Boolean,
      default: false,
    }
  },
  data () {
    return {
        editorInEditMode: false,
        newValue: undefined,
      showTrigger: false,
      collapsed: true,
      screenWidth: document.body.clientWidth,
      checkAll: false
    }
  },
  computed: {
   
  },
  watch: {
    value: function (newVal) {
        this.loadData();
    },
    // newValue: function (newVal) {
    //   this.$emit('input', newVal)
    // }
    // screenWidth: function () {
    //   this.showTrigger = this.needTrigger()
    // },
  },
  mounted () {
    this.loadData();
  },
  methods: {
    transformTableData(data) {
        if(typeof data == 'string'){
            try {
                return JSON.parse(data);
            } catch (error) {
                console.log(error);
                return []
            }
        }
        return data;
    },
    loadData(){
        if(this.fields.inputType == 'DATE'&&this.value){
            this.newValue = moment(this.value,"YYYY-MM-DD HH:mm:ss")
        }else if(this.fields.inputType == 'TIME'&&this.value){
            this.newValue = moment(this.value,"HH:mm:ss")
        }else if(this.fields.inputType == 'DURATION'&&this.value){
            this.newValue=[];
            if(Array.isArray(this.value)){
                this.value?.forEach(item=>{
                    this.newValue.push(moment(item))
                })
            }else{
                this.value?.replace(']','').replace('[','').split(',')?.forEach(item=>{
                    this.newValue.push(moment(item))
                })
            }
        }else if(this.fields.inputType=="TEST_STEP"&&this.value){
            this.newValue = JSON.parse(this.value)
        }else if(this.value==0&&this.fields.inputType!="INTEGER"&&this.fields.inputType!="DECIMAL"){
            this.newValue = undefined
        }else{
            this.newValue = this.value
        }
    },
    isTable(fields) {
        return fields.inputType === 'TABLE' || fields.inputType === 'TEST_STEP' || fields.inputType === 'DURATION'
    },
    onChangeCustomerField(fieldId,value) {
        this.$emit('input', value)
        this.$emit('change', fieldId, value)
    },
    onBlurTextField(fieldId){
        if(this.newValue){
            this.onChangeCustomerField(fieldId, this.newValue)
        }
    },
    onBlurSimpleEditor(fieldId){
        this.editorInEditMode=false;
        if(this.value!=this.newValue){
            this.onBlurTextField(fieldId);
        }
    },
    onChangeDatePicker(fieldId) {
        if (this.newValue) {
            let datePicker = moment(this.newValue).format("YYYY-MM-DD HH:mm:ss")
            this.onChangeCustomerField(fieldId, datePicker);
        } else {
            this.newValue = null;
        }
    },
    onChangeTimePicker(fieldId) {
        if (this.newValue) {
            let timePicker = moment(this.newValue).format("HH:mm:ss")
            this.onChangeCustomerField(fieldId, timePicker);
        } else {
            this.newValue = null;
        }
    },
    onChangeRangePicker(fieldId) {
        if (this.newValue) {
            let rangePicker=[];
            for(let i=0;i<this.newValue.length;i++){
                rangePicker.push(moment(this.newValue[i]).format("YYYY-MM-DD HH:mm:ss"))
            }
            this.onChangeCustomerField(fieldId, rangePicker);
        } else {
            this.newValue = null;
        }
    },
    onChangeUserSelect(fieldId) {
        if (this.newValue) {
            this.onChangeCustomerField(fieldId, this.newValue)
        } else {
            this.newValue = null;
        }
    },
    onAddTableColumn(trackerField) {
        let columns = trackerField.columns || []
        let row = {}
        for (let i = 0; i < columns.length; i++) {
            row[columns[i].name] = ''
        }

        let xTable = this.$refs['customerFiled-' + trackerField.id]
        if (xTable) {
            xTable.insertAt(row, -1)
            this.newValue = xTable.getTableData().tableData
        }
    },
    onDeleteTableColumn(trackerField, row) {
        let xTable = this.$refs['customerFiled-' + trackerField.id]
        if (xTable) {
            xTable.remove(row)
            this.newValue = xTable.getTableData().tableData
        }
    },
    onTableDataSave(trackerField) {
        let trackerFieldId = trackerField.id
        let xTable = this.$refs['customerFiled-' + trackerField.id]
        if (xTable) {
            let oldValue = xTable.getTableData().tableData
            let newValue2 = []
            for (let i = 0; i < oldValue.length; i++) {
                let oldRow = oldValue[i]
                let newRow = {}
                for (let j = 0; j < trackerField.columns.length; j++) {
                    let key = trackerField.columns[j]
                    newRow[key.id] = oldRow[key.id]
                }
                delete newRow['_X_ROW_KEY']
                newValue2.push(newRow)
            }
            console.log(newValue2)
            this.onChangeCustomerField(trackerFieldId, JSON.stringify(newValue2))
            // changeCustomerField(this.itemId, id, newValue2).then(resp => {
            //     this.loadData()
            // }).catch(e => {
            //     this.loadData()
            //     VXETable.modal.message({ content: '更新失败', status: 'error' })
            // })
        }
    }
  }
}
</script>

<style lang="less" scoped>
  .read-only-div /deep/ .ant-input[disabled],.ant-input[disabled]:hover{
    color: #5d6369;
    background-color: #ffffff;  
    border:none;
    resize: none;
    cursor:auto;
}
.read-only-div /deep/.ant-select-disabled .ant-select-selection{
    color: #5d6369;
    background-color: #ffffff;  
    border:none;
    resize: none;
    cursor:auto;
}
.read-only-div /deep/ .ant-time-picker-input[disabled],/deep/.ant-input-disabled{
    color: #5d6369;
    background-color: #ffffff;  
    border:none;
    resize: none;
    cursor:auto;
}
.read-only-div /deep/.ant-select-arrow,/deep/.ant-calendar-picker-icon,/deep/.ant-time-picker-icon{
    display: none;
}
.read-only-div /deep/ .ant-select-disabled .ant-select-selection--multiple .ant-select-selection__choice{
    color: rgba(93, 99, 105);
    background-color: #fafafa;
    resize: none;
}
.read-only-div /deep/ .simple-editor{
    border:none;
}
.wrapper >div{
    width: 100%;
}
</style>
