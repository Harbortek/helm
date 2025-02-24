<template>
  <a-modal :visible="visible" v-if="visible" width="1100px" centered
    :title="!isTest ? !isConditionSelect ? '工作项选择' : '筛选条件选择' : '测试用例选择'" ok-text="确认" cancel-text="取消" @ok="onOk"
    @cancel="onCancel">
    <a-row style="margin-bottom: 15px">
      <a-col :span="12">
        <div class="alert" v-if="!isConditionSelect">
          <div type="info" :show-icon="true" v-if="selectedRowKeys">
            <div class="message" style="font-size: 12px">
              已选择&nbsp;<a>{{ selectedRowKeys.length }}</a>&nbsp;项，共{{ pagination.totalElements }}个符合条件的结果：
            </div>
          </div>
        </div>
      </a-col>
      <a-col :span="12">
        <a-col :offset="8" :span="4">
          <a-badge :number-style="{ backgroundColor: '#338fe5' }" :count="getFilterCount()">
            <a-button type="text" @click="onClickFilter">筛选<a-icon :component="filterIcon" /></a-button>
          </a-badge>
        </a-col>
        <a-col :span="12">
          <a-input-search placeholder="搜索工作项标题" v-model="queryParam.keyword" @search="handleSearch" />
        </a-col>
      </a-col>
    </a-row>
    <tracker-item-filter style="transition: all .25s;"
      :class="activeKey == 'true' ? 'filter-flex-active' : 'filter-flex'" :conditionGroups="conditionGroups"
      :activeKey="activeKey" :members="members" :tracker="tracker" @refresh="refresh"></tracker-item-filter>

    <div class="table-box" :style="{ height: tableHeight }" style="display:flex;flex-direction: column;">
      <div style="height:calc(100% - 50px);overflow: hidden;">
        <vxe-table ref="vxeTable" row-id="id" height="auto" style="cursor: pointer;" :data="itemData" :loading="loading"
          :show-header="!isConditionSelect" @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent"
          :row-config="{ isHover: true }"
          :checkbox-config="{ checkRowKeys: selectedRowKeys, checkMethod: checCheckboxkMethod, reserve: true, checkField: 'checked', trigger: 'row' }">

          <vxe-column v-if="!isConditionSelect" type="checkbox" title="" width="30"></vxe-column>
          <vxe-colgroup title="全选" header-class-name="custom-span">
            <template>

              <vxe-column field="" header-class-name="hidden-cell" title="" :width="120">
                <template #default="{ row }">
                  <a-tooltip :title="'工作项类型：' + row.tracker?.name" :overlayStyle="{ fontSize: '10px' }">
                    <a-icon v-if="row?.icon" :component="row?.icon" />
                  </a-tooltip>
                  {{ (currentProjectKeyName + '-' + row.itemNo).toUpperCase() }}
                </template>
              </vxe-column>
              <vxe-column field="name" title="" width="" header-class-name="hidden-cell">
                <template #default="{ row }">

                  <span>&nbsp;{{ row.name }}</span>
                </template>
              </vxe-column>

              <vxe-column field="" header-class-name="hidden-cell" title="" :width="60">
                <template #default="{ row }">
                  <a-tooltip :title="'负责人：' + row.owner?.name" :overlayStyle="{ fontSize: '10px' }"
                    :mouseEnterDelay="0.01">
                    <a-tag class="a-tag-box">
                      {{ row.owner?.name }}</a-tag>
                  </a-tooltip>
                </template>
              </vxe-column>

              <vxe-column header-class-name="hidden-cell" title="" :width="60">
                <template #default="{ row }">
                  <a-tooltip :title="'优先级：' + row.priority?.name" :overlayStyle="{ fontSize: '10px' }">
                    <a-tag style="border:none;cursor: pointer;"
                      :style="{ color: row.priority?.color, backgroundColor: row.priority?.backgroundColor }">{{
    row.priority?.name }}</a-tag>
                  </a-tooltip>
                </template>
              </vxe-column>

              <vxe-column header-class-name="hidden-cell" title="" :width="60">
                <template #default="{ row }">
                  <a-tooltip :title="'类型：' + row.meaning?.name" :overlayStyle="{ fontSize: '10px' }">
                    <a-tag class="a-tag-box">
                      {{ row.meaning?.name }}</a-tag>
                  </a-tooltip>
                </template>
              </vxe-column>

            </template>
          </vxe-colgroup>

          <vxe-column field="status.name" title="" header-class-name="hidden-cell" width="120">
            <template #default="{ row }">
              <div v-if="row.status" class="transition-status">
                <span class="ui-tag-status"
                  :style="{ color: row.meaning?.color, 'border-color': row.meaning?.color }">{{
    row.status?.name }}</span>
              </div>
            </template>
          </vxe-column>
        </vxe-table>
      </div>

      <vxe-pager :loading="loading" :current-page="pagination.current" :page-size="pagination.pageSize"
        :total="pagination.total" :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
        @page-change="handlePageChange">
      </vxe-pager>
    </div>

  </a-modal>
</template>

<script>
import { mapGetters } from "vuex";
import {systemFields} from '@/utils/systemFields'
import {
  findTrackerItems
} from "@/services/tracker/TrackerItemService";
import {
  findTrackers, findOneTracker, findTrackerLayout
} from "@/services/tracker/TrackerService";
import {
  findProjectRolesAndMembers
} from "@/services/tracker/ProjectRoleMemberService";
import {
  findEnumsByCode
} from "@/services/system/EnumService";
import TrackerItemFilter from "../tool/TrackerItemFilter.vue";
import Vue from "vue";

export default {
  name: 'TrackerItemSelectModal',
  props: {
    initalData: Array,
    projectId: String,
    trackerId: String,
    trackerIds: Array,
    isConditionSelect: Boolean,
    isTest: Boolean,
  },
  components: {
    TrackerItemFilter
  },
  data() {
    return {
      // 查询条件参数
      queryParam: {
        keyword: '',
      },
      itemIds: [],
      itemData: [],
      members: [],
      selectedRows: [],
      selectedRowKeys: [],
      loading: false,
      visible: false,
      tracker: {},
      activeKey: '',
      tableHeight: '590px',
      conditionGroups: [
        {
          conditions: []
        }
      ],
      filterIcon: 'expandDown',
      pagination: {
        current: 1,
        pageSize: 10,
      },
    }
  },
  computed: {
    ...mapGetters("project", ["currentProjectKeyName"]),
  },
  mounted() {

  },
  methods: {
    handlePageChange(pagination) {
      this.pagination.current = pagination.currentPage
      this.pagination.pageSize = pagination.pageSize
      this.refresh();
    },
    checCheckboxkMethod({ row }) {
      let ids = this.initalData.map(_ => _.id);
      return ids.indexOf(row.id) == -1;
    },
    getsystemProperty(systemProperty) {
      if (systemProperty) {
        systemProperty = systemProperty.toString();
        if (systemProperty.endsWith('Id')) {
          systemProperty = systemProperty.substring(0, systemProperty.length - 2)
        }
      }
      return systemProperty
    },
    getFilterCount() {
      let count = 0;
      this.conditionGroups.forEach(item => {
        item.conditions.forEach(item2 => {
          if (item2 && item2.value) {
            count++;
          }
        })
      })
      return count;

    },
    onClickFilter() {
      const that = this;
      if (this.activeKey == 'true') {
        this.activeKey = 'false'
        this.tableHeight = "590px"
        this.filterIcon = "expandDown"
      } else {
        this.activeKey = 'true'
        this.tableHeight = "370px"
        this.filterIcon = "collapseUp"
      }
    },
    selectChangeEvent({ checked, records, reserves }) {
      this.selectedRowKeys = [...reserves.map(v => v.id), ...records.map(v => v.id)];
      this.selectedRows = [...reserves, ...records];
    },
    onOk() {
      this.visible = false;
      this.$emit('trackerItemSelector', this.selectedRows);
      this.$emit('trackerConditonSelector', this.conditionGroups)
      this.onClear()
    },
    onCancel() {
      this.onClear()
      this.visible = false;
    },
    view(itemIds) {
      this.itemIds = itemIds;
      this.visible = true;
      this.activeKey = "false";
      if (this.isConditionSelect && this.activeKey != 'true') {
        this.onClickFilter();
      }
      this.selectData();
      this.loadData();
    },
    handleSearch() {
      this.loadTrackerItemsData({});
    },
    selectData() {
      this.selectedRows = Object.assign([], this.initalData);
      for (let i = 0; i < this.selectedRows.length; i++) {
        this.selectedRowKeys.push(this.selectedRows[i].id)
      }
    },
    loadData() {
      this.loading = true;
      if (this.trackerId) {
        findOneTracker(this.trackerId).then(resp => {
          this.tracker = resp;
        }).finally(() => {
          this.loadTrackerData();
        })
      } else {
        this.initTrackerFields();
        this.loadTrackerData();
      }
      findProjectRolesAndMembers(this.projectId, "").then(resp => {
        this.members = resp.members;
      });
    },
    onSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },
    onClear() {
      this.pagination = {
        current: 1,
        pageSize: 10,
      },
        this.selectedRowKeys = []
      this.selectedRows = []
      this.queryParam.keyword = ""
      this.$refs.vxeTable.clearCheckboxRow()
    },
    refresh() {
      this.loadTrackerItemsData({ conditionGroups: this.conditionGroups });
    },
    loadTrackerItemsData(filter) {
      this.loading = true;
      const that = this;
      // let filter = null
      let keyword = this.queryParam.keyword
      let pageable = {
        page: this.pagination.current - 1,
        size: this.pagination.pageSize,
      }

      findTrackerItems(this.projectId, null, null, filter, keyword, pageable, [],this.isTest).then(resp => {
        if (resp) {
          if (this.itemIds) {
            that.itemData = resp.content.filter(item => this.itemIds.indexOf(item.id) == -1)
          } else {
            that.itemData = resp.content;
          }
          this.pagination.total = parseInt(resp.totalElements);

        }
      }).finally(() => {
        that.loading = false;
        //初始化选中
        this.$nextTick(() => {
          for (let item2 of this.itemData) {
            for (let item of this.selectedRows) {
              if (item.id == item2.id)
                this.$refs["vxeTable"].setCheckboxRow(item2, true)
            }
          }
        })
      })
    },
    loadTrackerData() {
      if (this.trackerId || this.trackerIds) {
        this.conditionGroups[0].conditions = [];
        let field = this.tracker.trackerFields.find(field => field.systemProperty == 'trackerId')
        this.conditionGroups[0].conditions.push({
          id: field?.id, field: 'trackerId', type: 'OPTIONS', value: [...((this.trackerIds) || [this.trackerId])], operator: 'INCL'
        })
      }
      this.loadTrackerItemsData({ conditionGroups: this.conditionGroups });

      findTrackers({ projectId: this.projectId }).then(resp => {
        console.log("tracertracv",resp)

        if(this.isTest){
          resp=resp.filter(tracker=>this.hasTestStep(tracker))
        }
        this.tracker.trackerId = Vue.observable([]);
        resp.forEach(item => {
          this.tracker.trackerId.push({ id: item.id, name: item.name, icon: item.icon })
        });
      })

      findEnumsByCode('TRACKER_PRIORITY').then((resp) => {
        this.tracker.priority = Vue.observable([]);
        resp.forEach(item => {
          this.tracker.priority.push({ id: item.id, name: item.name })
        });
      });

      findEnumsByCode('TRACKER_STATUS_MEANING').then((resp) => {
        this.tracker.meaning = Vue.observable([]);
        resp.forEach(item => {
          this.tracker.meaning.push({ id: item.id, name: item.name })
        });
      })
    },
    initTrackerFields() {
      this.tracker.trackerFields = systemFields.filter(v=>v.systemProperty!='status');
    },
    hasTestStep(tracker){
      for(let field of tracker.trackerFields){
        if(field.inputType=="TEST_STEP"){
          return true;
        }
      }
      return false;
    },
  },

}
</script>

<style scoped lang="less">
/deep/.ant-modal-body {
  height: 650px;
}

/deep/.ant-collapse-content>.ant-collapse-content-box {
  padding: 0;
}

/deep/.hidden-cell {
  //实现vxe表头合并
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

.table-box {
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

.add-condition-group {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.add-condition-group:hover {
  color: #338fe5;
  background-color: #f3f3f3;
}

.group-line {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  padding-bottom: 5px;

  .group-or {
    padding: 0 10px;
    background-color: rgb(250, 250, 250);
    position: relative;
    z-index: 1;
  }
}

.group-line::after {
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

.a-tag-box {
  cursor: pointer;
  border: none;
  background-color: rgba(144, 144, 144, .15);
}

.filter-flex {
  height: 0;
}

.filter-flex-active {
  height: 220px;
}
</style>
