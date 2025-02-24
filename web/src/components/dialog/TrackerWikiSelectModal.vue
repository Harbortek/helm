<template>
  <a-modal :visible="visible" :v-if="visible" width="800px" title="关联Wiki页面" ok-text="确认" cancel-text="取消" @ok="onOk"
    @cancel="onCancel">
    <a-row style="margin-bottom: 15px">
      <a-col :span="12">
        <div class="alert">
          <div type="info" :show-icon="true" v-if="selectedRowKeys">
            <div class="message" style="font-size: 12px">
              已选择&nbsp;<a>{{ selectedRowKeys.length }}</a>&nbsp;项，共{{ itemData.length }}个符合条件的结果：
            </div>
          </div>
        </div>  
      </a-col>
      <a-col :span="12">
        <a-col :offset="10" :span="12">
          <a-input-search placeholder="搜索页面标题" v-model="queryParam.keyword" @search="handleSearch" />
        </a-col>
      </a-col>
    </a-row>
    <div class="table-box" :style="{height: tableHeight}">
      <vxe-table ref="vxeTable" row-id="id"  style="cursor: pointer;" :data="itemData" :loading="loading" @checkbox-all="selectChangeEvent"
        @checkbox-change="selectChangeEvent" :row-config="{ isHover: true }" :checkbox-config="{checkRowKeys: selectedRowKeys,checkField: 'checked', trigger: 'row'}">

          <vxe-column type="checkbox" title="" width="30"></vxe-column>
          <vxe-colgroup title="全选" header-class-name="custom-span">
            <vxe-column field="name" title="" width="" header-class-name="hidden-cell">
                <template #default="{ row }">
                  {{ row.name }}
                </template>
            </vxe-column>
          </vxe-colgroup>
      </vxe-table>
    </div>
    
  </a-modal>
</template>

<script>

import { findByProjectId } from "@/services/tracker/ProjectPageService";
import Vue from "vue";

export default {
  name: 'TrackerWikiSelectModal',
  props: {
    initalData: Array,
    projectId: String,
    trackerId: String,
  },
  components: {
    
  },
  data() {
    return {
      // 查询条件参数
      queryParam: {
        keyword: '',
      },
      itemData: [],
      members: [],
      selectedRows: [],
      selectedRowKeys: [],
      loading: false,
      visible: false,
      tracker:{},
      activeKey: '',
      tableHeight: '650px',
      conditionGroups: [
        {
          conditions:[]
        }
      ],
      filterIcon: 'expandDown',
    }
  },
  mounted() {
  },
  methods: {
    selectChangeEvent ({ checked, records, reserves }) {
      this.selectedRowKeys = [...reserves.map(v => v.id),...records.map(v => v.id)];
      this.selectedRows = [...reserves,...records];
    },
    onOk() {
      this.visible = false;
      this.$emit('trackerWikiSelector', this.selectedRows);
      this.onClear()
    },
    onCancel() {
      this.onClear()
      this.visible = false;
    },
    view() {
      this.visible = true;
      this.activeKey="false";
      this.selectData();
      this.loadData();
    },
    handleSearch() {
      // this.loadTrackerItemsData({});
    },
    selectData() {
      this.loading=true;
        this.selectedRows = Object.assign([],this.initalData);
        for (let i = 0; i < this.selectedRows.length; i++) {
          this.selectedRowKeys.push(this.selectedRows[i].id)
        }
    },
    loadData() {
      this.loading = true;
      findByProjectId(this.projectId).then(res=>{
        this.itemData=res.filter(item=>item.type=='wiki');
       
        this.loading = false;
      }).finally(()=>{
        this.loading=false;
        //初始化选中
        this.$nextTick(() => {
          for(let item2 of this.itemData){
            for(let item of this.selectedRows){
              if(item.id==item2.id)
                this.$refs["vxeTable"].setCheckboxRow(item2,true)
            }
          }
        })
      })
     
    },
    onSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },
    onClear() {
      this.selectedRowKeys = []
      this.selectedRows = []
      this.queryParam.keyword = ""
    },
    refresh(){
      // this.loadTrackerItemsData({conditionGroups:this.conditionGroups});
    },
    
  },

}
</script>

<style scoped lang="less">
/deep/.ant-modal-body {
  height: 550px;
}
/deep/.ant-collapse-content > .ant-collapse-content-box {
    padding: 0;
}
/deep/.hidden-cell { //实现vxe表头合并
  display: none;
}
.standard-table {
  .alert {
    margin-bottom: 16px;
    .message {
      font-size: 12px;
      a {
        font-weight: 600;
      }
    }
    .clear {
      float: right;
    }
  }
}
.transition-status {
    float: right;
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
.table-box{
  box-sizing: border-box;
    direction: ltr;
    position: relative;
    will-change: transform;
    overflow-y: auto;
    transition: height .25s;
}
.order-row {
    width: 440px;
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
.a-tag-box{
    cursor: pointer;
    border:none;
    background-color: rgba(144,144,144,.15);
}
</style>
