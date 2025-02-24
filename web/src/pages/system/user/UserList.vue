<template>
  <config-page :title="$t('system.user.list.config-page.title')"
    :description="$t('system.user.list.config-page.description')" id="userList">
    <a-row type="flex" :gutter="8" style="height:100%">
      <a-col :span="5">
        <a-card :title="$t('system.user.list.a-card.org-title')" style="min-height:600px">
          <a-col :span="4" style="margin-top: 15px">
            <a-row :gutter="8">
              <a-col :span="12">
                <a-tree :tree-data="orgTree" @select="onTreeNodeSelect" :expandedKeys="expandedKeys" @expand="onExpand">
                </a-tree>
              </a-col>
            </a-row>
          </a-col>
        </a-card>
      </a-col>
      <a-col :span="19">
        <a-card :title="$t('system.user.list.a-card.user-title')">
          <div style="height: calc(100vh - 290px); display: flex; flex-direction: column;">
            <div>
              <vxe-toolbar size="medium">
                <template #buttons>
                  <a-input-search @search="handleSearch" v-model="queryParam.keyword" :placeholder="$t('system.user.list.a-card.input')"
                    style="width:300px;" enter-button></a-input-search>
                </template>
                <template #tools>
                  <a-button v-action="'SYSTEM_USER'" type="primary" @click="handleAdd()">{{ $t('add') }}</a-button>
                  <a-button v-action="'SYSTEM_USER'" style="margin-left: 8px" type="danger" icon="delete"
                    v-if="selectedRowKeys.length > 0" @click="handleBatchDel">
                    {{ $t('batch-delete') }}
                    <template>
                      {{ `(${selectedRowKeys.length})` }}
                    </template>
                  </a-button>
                </template>
              </vxe-toolbar>
            </div>
            <div style="height: calc(100vh - 320px);flex: 1 1 auto;">
              <vxe-table ref="userTable" :data="data" :loading="loading" :row-config="{ isHover: true, keyField: 'id' }"
                :column-config="{ useKey: true }" height="100%">

                <vxe-column :title="$t('system.user.table.number')" type="seq" width="60"></vxe-column>
                <vxe-column field="loginName" :title="$t('system.user.table.loginName')"></vxe-column>
                <vxe-column field="name" :title="$t('system.user.table.userName')"></vxe-column>
                <!-- <vxe-column field="mobilePhone" :title="$t('system.user.table.mobilePhone')"></vxe-column> -->
                <vxe-column field="email" :title="$t('system.user.table.email')"></vxe-column>
                <vxe-column field="status" :title="$t('system.user.table.status')" :filters="statusList"
                  :filter-multiple="false">
                  <template #default="{ row }">
                    <a-tag v-if="row.status === '1'" style="color:green;">启用</a-tag>
                    <a-tag v-if="row.status === '2'" style="color:red;">禁用</a-tag>
                  </template>
                </vxe-column>
                <!-- <vxe-column field="lastLogin" :title="$t('system.user.table.lastLogin')"></vxe-column> -->
                <!-- <vxe-column field="createDate" :title="$t('system.user.table.createDate')"></vxe-column> -->
                <!-- <vxe-column :title="$t('system.user.table.action')"></vxe-column> -->

                <vxe-column align="center" field="opearte" :title="$t('system.user.table.action')" width="220">
                  <template #default="{ row }">
                    <div v-action="'SYSTEM_USER'">
                      <a @click="handleEdit(row.id)">{{ $t('edit') }}</a>
                      <a-divider type="vertical" />
                      <a-popconfirm :title="$t('system.user.action.edit.message')" :ok-text="$t('ok')"
                        :cancel-text="$t('cancel')" @confirm="handlePwdreset(row.id)">
                        <a title="密码重置" href="#">{{ $t('reset') }}</a>
                      </a-popconfirm>
                      <a-divider type="vertical" />
                      <a @click="handleRole(row)">{{ $t('system.user.action.role') }}</a>
                      <a-divider type="vertical" />

                      <a @click="handleDelete(row.id)">{{ $t('system.user.action.more.delete') }}</a>
                    </div>
                  </template>
                </vxe-column>
              </vxe-table>
            </div>
            <div>
              <vxe-pager :loading="loading" :current-page="pagination.current" :page-size="pagination.pageSize"
                :total="pagination.total" :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                @page-change="handleChange">
              </vxe-pager>
            </div>
          </div>

          <user-dialog ref="userModal" :treeData="orgTree[0]['children']" @refresh="refresh()"></user-dialog>
          <user-role-dialog ref="userRoleModal" @refresh="refresh()"></user-role-dialog>
        </a-card>
      </a-col>
    </a-row>
  </config-page>
</template>

<script>
import UserDialog from './UserDialog.vue'
import UserRoleDialog from './UserRoleDialog.vue'
import { findOrganizationTree } from '@/services/system/OrgService'
import ConfigPage from '@/components/config-page/ConfigPage'
import SearchInput from '@/components/input/SearchInput'

import VXETable from 'vxe-table'
import { getUser, getUsers, batchDeleteUser, deleteUser, resetPwd, updateRoles } from '@/services/system/UserService'

export default {
  name: 'UserList',
  components: {
    UserDialog, UserRoleDialog, ConfigPage, SearchInput,
    SearchInput
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
      statusList: [
        { label: this.$t('system.user.search.label.on'), value: '1' },
        { label: this.$t('system.user.search.label.off'), value: '2' }
      ],
      // 查询条件参数
      queryParam: {
        keyword: '',
        orgId: '',
      },
      selectedRowKeys: [],
      data: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      selectedRowKeys: [],
      selectedRows: [],
      loading: false,
      orgTree: [{ "title": "所有部门", key: "0", group: true, children: [] }],
      expandedKeys: ["0"],
    }
  },
  mounted() {
    this.loadData({
      current: 1,
      pageSize: this.pagination.pageSize
    });
  },
  computed: {

  },
  created() {
    this.loadTreeData();
  },
  methods: {
    selectChangeEvent({ checked, records, reserves }) {
      this.selectedRowKeys = [...reserves.map(v => v.id), ...records.map(v => v.id)];
      this.selectedRows = [...reserves, ...records];
    },
    handleDelete(id) {
      const that = this;
      VXETable.modal.confirm({
        title: this.$t('system.user.remind.title'),
        content: this.$t('system.user.remind.delete.content'),
      }).then(type => {
        if (type === 'confirm') {
          deleteUser(id).then(res => {
            that.loading = false;
            that.$message.success(that.$t('system.user.remind.delete.success'));
            that.refresh();
          })
        }
      })
    },
    refresh: function () {
      this.loadData(this.pagination);
    },
    handleAdd() {
      this.$refs.userModal.add();
    },
    handleView(id) {
      this.$refs.userModal.view();
      this.$refs.userModal.loadData(id);
    },
    handleSearch() {
      this.pagination.current = 1 //重置页面下标
      this.refresh();
    },
    handleReset() {
      this.queryParam.keyword = ''
    },
    loadData: function (parameter) {
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
      this.pagination.current = 1
      this.handleTree()
    },
    onExpand(expandedKeys) {
      this.expandedKeys = expandedKeys
    },
    handleTree() {
      console.log("this.selectedOrg.orgId", this.selectedOrg.orgId)
      if (this.selectedOrg.orgId != 0) {
        this.queryParam.orgId = this.selectedOrg.orgId;
      } else {
        this.queryParam.orgId = ""
      }

      this.loadData(this.pagination);
    },
    handleEdit(id) {
      this.$refs.userModal.edit();
      this.$nextTick(() => {
        this.$refs.userModal.loadData(id);
      });
    },
    handlePwdreset(id) {
      console.log('handlePwdreset', id);
      const that = this;
      resetPwd(id).then(res => {
        that.$message.success(that.$t('system.user.remind.resetPassword'));
      })
    },
    handleRole(user) {
      this.$refs.userRoleModal.edit(user);
    },
    handleChange(pagination) {
      this.pagination.current = pagination.currentPage
      this.pagination.pageSize = pagination.pageSize
      this.loadData(this.pagination);
    },
    handleBatchDel() {
      const that = this;
      this.$confirm({
        title: this.$t('system.user.remind.title'),
        content: this.$t('system.user.remind.delete.content'),
        okText: this.$t('ok'),
        okType: 'danger',
        cancelText: this.$t('cancel'),
        onOk() {
          console.log('batchDeleteUser', that.selectedRowKeys);
          batchDeleteUser(that.selectedRowKeys).then(res => {
            that.loading = false;
            that.selectedRows = [];
            that.$message.success(that.$t('system.user.remind.delete.success'));
            that.refresh();
          })
        },
        onCancel() {
        }
      })
    }
  }
}
</script>

<style lang="less" scoped></style>
