<template>
  <a-modal :visible="visible" :v-if="visible" style="padding:10px 24px" centered width="1100px" title="用户选择" ok-text="确认" cancel-text="取消" @ok="onOk"
    @cancel="onCancel">
    <a-row type="flex" :gutter="8">
      <a-col :span="6">
        <a-card title="部门选择" style="height:100%">
          <a-menu mode="vertical" style="border: 0;">
            <a-col :span="4" style="margin-top: 15px">
              <a-row :gutter="8">
                <a-col :span="12">
                  <a-tree :tree-data="orgTree" @select="onTreeNodeSelect" :expandedKeys="expandedKeys" @expand="onExpand">
                  </a-tree>
                </a-col>
              </a-row>
            </a-col>
          </a-menu>
        </a-card>
      </a-col>
      <a-col :span="18">
        <a-card title="用户列表">
          <a-form style="float:right;height: 40px;" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-col :span="14" :md="14" :sm="14">
              <a-form-item :label="$t('system.user.search.label.user-name')" :labelCol="{ span: 6 }"
                :wrapperCol="{ span: 17, offset: 1 }">
                <a-input-search placeholder="" v-model="queryParam.keyword" @search="handleSearch" />
              </a-form-item>
            </a-col>
            <a-col :span="10" :md="10" :sm="10">
              <a-space style="float: right; margin-top: 3px;">
                <a-button :loading="loading" v-action="'SYSTEM_USER'" type="primary" @click="handleSearch()">{{
                  $t('query') }}</a-button>
                <a-button v-action="'SYSTEM_USER'" @click="() => this.queryParam.keyword = ''">{{ $t('reset')
                }}</a-button>
              </a-space>
            </a-col>
          </a-form>
          <a-descriptions>
            <a-col>
              <div class="alert">
                <a-alert type="info" :show-icon="true" v-if="selectedRowKeys">
                  <div class="message" slot="message">
                    已选择&nbsp;<a>{{ selectedRowKeys.length }}</a>&nbsp;项 <a class="clear" @click="onResetting">清空</a>
                  </div>
                </a-alert>
              </div>
              <div class="table-box">
                <vxe-table ref="vxeTable" style="cursor: pointer;min-height:550px" :data="data" :loading="loading"
                  :row-config="{ isHover: true }" row-id="id" @checkbox-all="selectChangeEvent"
                  @checkbox-change="selectChangeEvent"
                  :checkbox-config="{ checkRowKeys: selectedRowKeys, checkMethod: checCheckboxkMethod,reserve:true,checkField: 'checked', trigger: 'row' }">
                  <vxe-column type="checkbox" width="60"></vxe-column>
                  <vxe-column field="name" :title="$t('system.user.table.userName')"></vxe-column>
                  <vxe-column field="mobilePhone" :title="$t('system.user.table.mobilePhone')"></vxe-column>
                  <vxe-column field="email" :title="$t('system.user.table.email')"></vxe-column>
                </vxe-table>
                <vxe-pager :loading="loading" :current-page="pagination.current" :page-size="pagination.pageSize"
                  :total="pagination.total"
                  :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                  @page-change="handleChangePage">
                </vxe-pager>
              </div>
            </a-col>
          </a-descriptions>
        </a-card>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
import { findOrganizationTree } from '@/services/system/OrgService'
import { getUsers } from '@/services/system/UserService'

export default {
  name: 'UserSelectModal',
  props: {
    initalData: Array,
  },
  components: {
  },
  data() {
    return {
      pagination: {
        current: 1,
        pageSize: 10,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '30', '50', '100'],
        showTotal: total => this.$t('system.user.showtotal') + ` ${total} ` + this.$t('system.user.nape')
      },
      // 查询条件参数
      queryParam: {
        keyword: '',
        orgId: '',
      },
      data: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      selectedRows: [],
      selectedRowKeys: [],
      loading: false,
      orgTree: [{ "title": "所有部门", key: "0", group: true, children: [] }],
      expandedKeys: ["0"],
      visible: false,
    }
  },
  methods: {
    handleChangePage(pagination) {
      this.pagination.current = pagination.currentPage
      this.pagination.pageSize = pagination.pageSize
      this.loadData(this.pagination);
    },
    checCheckboxkMethod({ row }) {
      let ids = this.initalData.map(_ => _.id);
      return ids.indexOf(row.id) == -1;

    },
    selectChangeEvent({ checked, records, reserves }) {
      this.selectedRowKeys = [...reserves.map(v => v.id), ...records.map(v => v.id)];
      this.selectedRows = [...reserves, ...records];
    },
    onOk() {
      this.visible = false;
      let ids = this.initalData.map(_ => _.id);
      this.selectedRows = this.selectedRows.filter(item => ids.indexOf(item.id) == -1)
      this.$emit('userSelector', this.selectedRows);
      this.onClear()
    },
    onCancel() {
      this.onClear()
      this.visible = false;
    },
    view() {
      this.visible = true;
      this.selectData();
      this.loadTreeData();
      this.loadData({
        current: 1,
        pageSize: this.pagination.pageSize
      });
    },
    refresh: function () {
      this.loadData(this.pagination);
    },
    handleSearch() {
      this.refresh();
    },
    selectData() {
      this.selectedRows = Object.assign([], this.initalData);
      for (let i = 0; i < this.selectedRows.length; i++) {
        this.selectedRowKeys.push(this.selectedRows[i].id)
      }
    },
    loadData(parameter) {
      parameter = parameter || {};
      this.loading = true;
      const that = this;
      return getUsers(Object.assign({
        size: parameter.pageSize,
        page: parameter.current - 1
      }, this.queryParam)).then(res => {
        console.log('getUsers', res);
        const pagination = { ...that.pagination };
        pagination.total = parseInt(res.totalElements);
        this.loading = false;
        that.data = res.content;
        that.pagination = pagination;
        return res;
      }).finally(() => {
        this.$nextTick(() => {
          for (let item2 of this.data) {
            for (let item of this.initalData) {
              if (item.id == item2.id)
                this.$refs["vxeTable"].setCheckboxRow(item2, true)
            }
          }
        })
      })
    },
    loadTreeData() {
      findOrganizationTree().then((res) => {
        this.orgTree[0]["children"] = res
      })
    },
    onTreeNodeSelect(selectedKeys, e) {
      if (selectedKeys.length > 0) {
        this.selectedOrg = { orgId: selectedKeys[0], orgName: e.selectedNodes[0].data.props.title }
        this.isNodeSelected = true
      }
      this.handleTree()
    },
    onExpand(expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    handleTree() {
      if (this.selectedOrg.orgId != 0) {
        this.queryParam.orgId = this.selectedOrg.orgId;
      } else {
        this.queryParam.orgId = ""
      }

      this.loadData(this.pagination);
    },
    onResetting() {
      this.onClear();
      this.selectData();
      this.refresh();
    },
    onClear() {
      this.selectedRowKeys = []
      this.selectedRows = []
      this.queryParam.keyword = ""
      this.$refs["vxeTable"].clearCheckboxReserve()
    },
    updateSelect(selectedRowKeys, selectedRows) {
      this.selectedRows = selectedRows
      this.selectedRowKeys = selectedRowKeys
    },
  },

}
</script> 

<style scoped lang="less">
/deep/ .ant-descriptions-item>span {
  display: inline-block;
  width: 100%;
}

.standard-table {
  .alert {
    margin-bottom: 16px;

    .message {
      a {
        font-weight: 600;
      }
    }

    .clear {
      float: right;
    }
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
.a-desc-box{
  min-height: 650px;
}
</style>
