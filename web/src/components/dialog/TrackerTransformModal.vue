<template>
  <div>
    <a-modal
      v-if="visiable"
      v-model="visiable"
      width="1200px"
      title="批量修改工作项属性"
      centered
      @ok="onOK"
      @cancel="onCancel"
    >
      <template slot="footer">
        <vxe-button content="取消" @click="onCancel"></vxe-button>
        <vxe-button @click="onPrevStep" content="上一步"></vxe-button>
        <vxe-button v-if="!isShowNextStep" status="primary" @click="onNextStep" content="下一步"></vxe-button>
        <vxe-button v-else status="primary" @click="onOK" content="确认修改"></vxe-button>
      </template>
      <div class="table-box">
          <span style="color: #87888a"
              >已选择 1 个项目，1 个工作项类型，共 {{this.selectedRows.length}} 个工作项，以下操作将批量修改所选工作项的工作项属性值。</span>
          

          <div class="step-bar">
              <div :class="['step-item',isShowNextStep?'curr opa':'curr']">
                  <div class="step-label">
                      <span class="step-label-tip">
                          <i style="margin-right:5px;" class="vxe-icon-radio-checked"></i>
                          <span>1.修改目标工作项属性值</span>
                      </span>
                  </div>
              </div>
              <a-icon style="font-size: 35px;color: #e2e3e6;" type="right" />
              <div :class="['step-item',isShowNextStep?'curr':'']">
                  <div class="step-label">
                      <span class="step-label-tip">
                          <i style="margin-right:5px;" class="vxe-icon-radio-checked"></i>
                          <span>2.确认修改</span>
                      </span>
                  </div>
              </div>
          </div>
          <template v-if="!isShowNextStep">
            <div>请修改目标工作项属性值。</div>
            <div class="page-header">
              <div class="page-description page-description-info manager-desc">
                <div class="page-description-border"></div>
                <div class="page-description-container">
                  <p class="page-description-p style--font-14">
                    只能选择工作项所属项目下应用的工作项属性，属性值为系统自动获取的工作项属性不支持修改。
                  </p>
                </div>
              </div>
            </div>
            <div style="border: 1px solid #d4d6d9;margin-top:10px;height: 70%;">
              <a-row style="height: 48px;font-size: 15px;font-weight: 500;line-height:48px;padding:0 10px;border-bottom: 1px solid #d4d6d9;
              background-color:#f7f8fa;">
                <a-col :span="4">所属项目</a-col>
                <a-col :span="4">工作项类型</a-col>
                <a-col :span="6">工作项属性</a-col>
                <a-col :span="7">选择操作</a-col>
                <a-col :span="3">影响工作项</a-col>
              </a-row>
              <a-row style="font-size: 14px;font-weight: 500;padding:0 10px;margin-top:10px;height: 40px;line-height: 40px;">
                <a-col :span="4">【示例】敏捷式研发管理</a-col>
                <a-col :span="4">需求</a-col>
                <a-col :span="13">
                  <a-form-model ref="fieldsForm" layout="horizontal" :model="formData" :rules="rules">
                    <div class="order-row" :key="field.id"
                        v-for=" (field,index) in fieldDatas">
                        <a-space style="height:100%" align="start">
                            <a-icon @click="removeFieldItem(index)" :theme="fieldDatas.length>1?'outlined':'filled'" 
                                :style="{fontSize:'18px',marginTop: '10px',color:fieldDatas.length>1?'#000':'#bfbfbf'}" type="minus-circle" />
                            <a-select v-model="field.id" style="width:180px;margin-top: 4px;">
                                <a-select-option v-for="f in getFields(field)" :key="f.name" :value="f.id" :title="f.name"
                                  @click="onChangeField(field, f)">{{f.name}}</a-select-option>
                            </a-select>
                            <a-select v-model="field.type" style="margin-top: 4px;">
                                <a-select-option value="modify">修改为</a-select-option>
                                <a-select-option value="clear">清空</a-select-option>
                            </a-select>
                            <template v-if="field.type!='clear'">
                              <a-form-model-item v-if="field.systemProperty=='priority'" prop="priority">
                                <priority-select  style="width:180px" v-model="field.value" 
                                  @change="(v,e)=>onChangeFieldValue(v,e,field)"/>
                              </a-form-model-item>

                              <a-form-model-item v-else-if="field.inputType=='USER'" :prop="field.systemProperty">
                                <project-user-select  style="width:180px;" v-model="field.value" :projectId="projectId" 
                                    @change="(v,e)=>onChangeFieldValue(v,e,field)"/>
                              </a-form-model-item>

                              <a-form-model-item v-else-if="field.inputType=='DATE'" :prop="field.systemProperty">
                                <a-date-picker  style="width:180px" v-model="field.value" :placeholder="'未设置'"
                                    @change="formData[field.systemProperty]=field.value" />
                              </a-form-model-item>

                              <a-form-model-item v-else-if="field.systemProperty=='sprintId'" prop="sprintId">
                                <sprint-select  style="width:180px" v-model="field.value" :projectId="projectId"
                                  @change="(v,e)=>onChangeFieldValue(v,e,field)"/>
                              </a-form-model-item>
                            </template>
                        </a-space>
                    </div>
                    <div v-if="getFields()?.length!=0">
                        <a-icon type="plus-circle" style="font-size:18px" @click="addFieldItem" />
                        <vxe-button type="text" status="primary" content="添加条件" style="margin-left: 10px;"
                            @click="addFieldItem" />
                    </div>
                  </a-form-model>
                </a-col>
                <a-col :span="3"> {{this.selectedRows?.length}} 个</a-col>
              </a-row>
            </div>
          </template>
          <template v-else>
            <span>以下 {{this.selectedRows.length}} 个工作项为实际执行变更操作的工作项，请确认变更后的工作项信息，确认无误后点击「确认修改」。</span>
            <vxe-table ref="vxeTable" row-id="id" height="80%" :loading="loading" style="cursor: pointer;" :data="selectedRows"
              :row-config="{ isHover: true }">
                  <vxe-column field="itemNo" title="编号" width="100px">
                      <template #default="{ row }">
                          {{projectKeyName+'-'+row.itemNo}}
                      </template>
                  </vxe-column>
                  <vxe-column field="name" title="标题" min-width="200" show-overflow>
                    <template #default="{ row }">
                      <div>
                        <a-icon :component="row.icon"/>&nbsp;
                        <span>{{ row.name }}</span>
                      </div>
                    </template>
                  </vxe-column>
                  <vxe-column field="tracker.name" title="工作项类型" min-width="100"></vxe-column>
                  <vxe-column field="createDate" title="创建时间" min-width="100" show-overflow></vxe-column>
                  <vxe-column field="createBy.name" title="创建者" min-width="100">
                    <template #default="{ row }">
                        <h-avatar :name="row.createBy?.name" :icon="row.createBy?.icon"></h-avatar>
                    </template>
                  </vxe-column>
                  <vxe-column field="owner.name" title="负责人" min-width="100">
                    <template #default="{ row }">
                        <div v-if="row.owner">
                          <h-avatar :name="row.owner?.name" :icon="row.owner?.icon"></h-avatar>
                        </div>
                        <div v-else>未分配</div>
                    </template>
                  </vxe-column>
                  <vxe-column field="lastModifiedDate" title="更新时间" min-width="100" show-overflow></vxe-column>
                  <vxe-column field="project.name" title="所属项目" min-width="100"></vxe-column>
                  <vxe-column field="priority.name" title="优先级" min-width="100">
                    <template #default="{ row }">
                        <a-tag style="border:none"
                            :style="{ color: row.priority.color, backgroundColor: row.priority.backgroundColor }">{{
                                row.priority.name }}</a-tag>
                    </template>
                  </vxe-column>
                  <vxe-column field="status.name" title="状态" min-width="100">
                    <template #default="{ row }">
                        <div class="transition-status">
                            <span class="ui-tag-status"
                                :style="{ color: row.meaning?.color, 'border-color': row.meaning?.color }">{{
                                    row.status?.name }}</span>
                        </div>
                    </template>
                  </vxe-column>
                  <vxe-column field="sprint.name" title="所属迭代" min-width="100"></vxe-column>
                  <vxe-column field="closeDate" title="关闭时间" min-width="100"></vxe-column>
              </vxe-table>
          </template>
      </div>
    </a-modal>
    <vxe-modal v-model="isShowLoading" top="50" id='loadMessage' :duration="-1" type="message" :showHeader="false" :showFooter="false" :maskClosable="false">
        <a-icon style="font-size:20px;" type="loading" />
        <span style="margin-left:10px;font-size:18px;color: #202d40;line-height: 24px;font-weight: 600;">正在加载，请稍等...</span>
    </vxe-modal>
  </div>
</template>
<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import moment from 'moment'
import {cloneDeep} from "lodash";
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import PrioritySelect from '@/components/select/PrioritySelect.vue';
import SprintSelect from '@/components/select/SprintSelect.vue';
import { batchUpdateTrackerItem} from "@/services/tracker/TrackerItemService";
import { VXETable } from 'vxe-table';
import { tr } from 'date-fns/locale';


export default {
  name: "TrackerTransformModal",
  components: {ConfigPage,ProjectUserSelect, PrioritySelect, SprintSelect, HAvatar},
  data() {
    return {
      loading: false,
      selectedRows: [],
      isShowNextStep: false,
      isShowLoading: false,
      fieldDatas: [],
      fieldList:[
        { id: '5', name: "优先级", systemProperty: "priority", inputType: "OPTIONS" },
        { id: '6', name: "负责人", systemProperty: "ownerId", inputType: "USER" },
        // { id: '7', name: "创建者", systemProperty: "createBy", inputType: "USER" },
        { id: '25', name: "关注者", systemProperty: "watchers", inputType: "USER" },
        { id: '21', name: "关闭时间", systemProperty: "closeDate", inputType: "DATE" },
        { id: '26', name: "所属迭代", systemProperty: "sprintId", inputType: "OPTIONS" },
      ],
      formData:{
        priority:'',
        ownerId:'',
        createBy:'',
        watchers:'',
        closeDate:'',
        sprintId:'',
      },
      rules: {
          priority: [ { required: true, message: "请输入值", trigger: "change" }],
          ownerId: [ { required: true, message: "请输入值", trigger: "change" }],
          createBy:[ { required: true, message: "请输入值", trigger: "change" }],
          watchers: [ { required: true, message: "请输入值", trigger: "change" }],
          closeDate: [ { required: true, message: "请输入值", trigger: "change" }],
          sprintId:[ { required: true, message: "请输入值", trigger: "change" }],
      },
    };
  },
  props: {
    projectId: {
      required: true,
    },
    isShowDialog: {
      required: true,
    },
    initalData: {
      required: false,
    },
    projectKeyName: {
      required: false,
    }
  },
  computed: {
    visiable: {
      get() {
        return this.isShowDialog;
      },
      set(newValue) {
        return newValue;
      },
    },
  },
  watch: {
    isShowDialog: {
      handler: function (newVal, oldVal) {
        if (newVal) {
          this.selectedRows=cloneDeep(this.initalData);
          if(this.fieldDatas.length===0){
            this.addFieldItem();
          }
        }
      },
    },
  },
  mounted() {},
  methods: {
    onChangeFieldValue(v,e,field){
      this.formData[field.systemProperty]=e
    },
    onChangeField(row,f){
      if (f) {
        row.id=f.id;
        row.name=f.name;
        row.systemProperty=f.systemProperty;
        row.inputType=f.inputType;
        row.value=undefined;
      }
      console.log("row",row)
    },
    removeFieldItem(index){
      if(this.fieldDatas.length>1){
        this.fieldDatas.splice(index, 1)
      }
      
    },
    addFieldItem: function () {
        let fields = this.getFields();
        this.fieldDatas.push({
          id: fields[0]?.id,
          name: fields[0]?.name,
          systemProperty: fields[0]?.systemProperty,
          inputType: fields[0]?.inputType,
          type:'modify',
          value: undefined,
        })
    },
    getFields(fieldItem){
        let ids = this.fieldDatas?.map(f => f?.id);
        if(!ids) ids=[];
        if (fieldItem) {
            ids = ids.filter(f => {
                return f !== fieldItem?.id
            })
        }
        let result = this.fieldList.filter(f => { return ids.indexOf(f.id) < 0 })
        return result
    },

    loadData() {},
    onPrevStep(){
      if(!this.isShowNextStep){
        this.$emit("onPrevStep")
      }else{
        this.isShowNextStep=false;
      }
    },
    onNextStep(){
      this.$refs.fieldsForm.validate(valid => {
          if (valid) {
              this.isShowNextStep=true;
              this.selectedRows.forEach(row =>{
                this.fieldDatas.forEach(field=>{
                  if(field.type=='modify'){
                    if(field.systemProperty.endsWith("Id")){
                      let columnField = field.systemProperty.slice(0, field.systemProperty.length - 2)
                      row[columnField]=this.formData[field.systemProperty]
                    }else if(field.systemProperty=='watchers'){
                      let ids =row.watchers.map(_=>_.id)
                      if(ids.indexOf(field.value)<0){
                        row.watchers.push(this.formData[field.systemProperty])
                      }
                    }else if(field.inputType=='DATE'){
                      row[field.systemProperty]=moment(field.value).format("YYYY-MM-DD HH:mm:ss")
                    }else{
                      row[field.systemProperty]=this.formData[field.systemProperty]
                    }
                  }
                })
              })
          }else{
          }
      });
    },
    onOK() {
      this.isShowLoading=true;
      batchUpdateTrackerItem(this.selectedRows).then(res=>{
         this.isShowLoading=false;
         this.$emit("refresh");
         this.onCancel();
        VXETable.modal.message({ status: 'success', content: '批量更新成功' })
      }).catch((error) => {
        VXETable.modal.message({ status: 'error', content: '批量更新失败' })
        this.isShowLoading=false;
      })
      // this.onCancel();
    },
    onCancel() {
      this.selectedRowKeys = [];
      this.selectedRows = [];
      this.fieldDatas=[];
      this.isShowNextStep=false;
      this.$emit("cancel");
    },
  },
  created() {},
};
</script>
<style lang="less" scoped>
.avatar-uploader {
  padding: 0 30px;
  /deep/ .ant-upload {
    width: 100%;
    height: 128px;
  }
}
.table-box {
  height: 650px;
  box-sizing: border-box;
  direction: ltr;
  position: relative;
  will-change: transform;
  overflow-y: auto;
  transition: height 0.25s;

  .step-bar{
    margin:20px auto;
    background-color: #f7f8fa;
    display: flex;
    flex-direction: row;
    align-items: stretch;
    height: 34px;
    line-height: 34px;
    border: solid 1px var(--color-border-medium);
    overflow: hidden;
    .curr {
        color: #0064ff;
    }
    .opa{
      opacity: .5
    }
    .step-item {
        position: relative;
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        flex: 1 1;
        background-color: transparent;
        z-index: 1;
        .step-label {
            z-index: 1;
            padding: 0 20px;
        }
    }
  }
}


.config-page {
  margin: 0;
  overflow: auto;
  padding: 20px;
  background: #fff;
  height: 100%;
  flex: 1 0 auto;
  display: flex;
  flex-direction: column;

  .page-header {
    width: 100%;
    flex: 0 0 auto;
    line-height: 1;

    .ant-breadcrumb {
      color: rgba(93, 99, 105);
      font-size: 18px;
      line-height: 26px;
    }

    .ant-breadcrumb a {
      color: rgba(93, 99, 105);
      font-size: 18px;
      line-height: 26px;
    }

  }

  .page-content {
    padding-top: 10px;
  }

  .page-content.autofit {
    height: 100%;
  }

  .header-footer-panel {
    // height: 100%;
    height: calc(100vh - 162px);
    display: flex;
    flex-direction: column;

    .panel-header {}

    .panel-body {
      // height: 100%;/
      height: calc(100vh - 240px);
      flex: 1 1 auto;
    }

    .panel-footer {}
  }
}



.page-description {
  display: flex;
  margin-top: 20px;
  line-height: 1.5;
  box-sizing: border-box;
  border-bottom: 1px solid #e8e8e8;
  border-radius: 0;
  border-right: 1px solid #e8e8e8;
  border-top: 1px solid #e8e8e8;
}

.page-description-border {
  border-radius: 0;
  flex: none;
  width: 4px;
}

.page-description-info .page-description-border {
  background: #338fe5;
}

.page-description-container {
  background: #fff;
  display: flex;
  flex: auto;
  padding: 10px;
}

.page-description-p {
  margin: 0 0 0 0;
  padding: 0;
  white-space: pre-wrap;
}

.style--font-14 {
  font-size: 14px;
  line-height: 22px;
}
.transform-list-header {
    height: 48px;
    font-size: 15px;
    font-weight: 500;
    position: relative;
    display: flex;
    align-items: center;
    border-bottom: 1px solid #dfe1e5;
    border-top: 1px solid #d4d6d9;
    .header-item{
        align-items: center;
        width: 320px;
        padding-right: 10px;
        text-overflow: ellipsis;
    }
}
.transition-status {
    min-width: 110px;
    display: flex;
    align-items: center;
    margin-right: 20px;
    white-space: nowrap;

    .ui-tag-status {
        max-width: 110px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 12px;
        box-sizing: border-box;
        display: inline-block;
        height: 20px;
        line-height: 18px;
        transition: border-color .2s;
        border: 1px solid;
        border-radius: 20px;
        padding: 0px 8px;
    }
}
</style>
