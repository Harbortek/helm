<template>
  <config-page :title="$t('system.param.list.config-page.title')"
    :description="$t('system.param.list.config-page.description')" id="paramList">
    <template #header>
      <vxe-toolbar>
        <template #buttons>
          <a-input-search @search="handleSearch" v-model="queryParam.keyword" :placeholder="$t('system.param.list.config-page.input')" style="width:300px;"
            enter-button></a-input-search>
        </template>
        <template #tools>
          <a-button type="primary" @click="handleAdd" v-action="'SYSTEM_PARAM'">{{ $t('add') }}</a-button>
          <a-button style="margin-left: 8px" type="danger" icon="delete" v-if="selectedRows.length > 0"
            @click="handleBatchDel" v-action="'SYSTEM_PARAM'">
            {{ $t('batch-delete') }}
            <template>
              {{ `(${selectedRowKeys.length})` }}
            </template>
          </a-button>
        </template>
      </vxe-toolbar>
    </template>

    <vxe-table ref="tableRef" :data="data" :loading="loading" :row-config="{ isHover: true }" row-id="id" height="100%"
      @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent" :checkbox-config="{ reserve: true }">
      <vxe-column type="checkbox" width="60"></vxe-column>
      <vxe-column :title="$t('system.user.table.number')" type="seq" width="60"></vxe-column>
      <vxe-column field="name" :title="$t('system.param.search.label.param-name')"></vxe-column>
      <vxe-column field="code" :title="$t('system.param.table.code')"></vxe-column>
      <vxe-column field="content" :title="$t('system.param.table.content')"></vxe-column>
      <vxe-column field="description" :title="$t('system.param.table.desc')"></vxe-column>

      <vxe-column align="center" :title="$t('system.param.table.action')" width="220">
        <template #default="{ row }">
          <template>
            <a @click="handleEdit(row.id)" v-action="'SYSTEM_PARAM'">{{ $t('edit') }}</a>
            <a-divider type="vertical" />
          </template>
          <a-dropdown v-action="'SYSTEM_PARAM'">
            <a class="ant-dropdown-link">
              {{ $t('system.param.more') }} <a-icon type="down" />
            </a>
            <a-menu slot="overlay">
              <a-menu-item v-action="'SYSTEM_PARAM'">
                <a @click="handleView(row.id)">{{ $t('detail') }}</a>
              </a-menu-item>
              <a-menu-item v-action="'SYSTEM_PARAM'">
                <a @click="handleDelete(row.id)">{{ $t('delete') }}</a>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </template>
      </vxe-column>
    </vxe-table>
    <template #footer>
      <vxe-pager :loading="loading" :current-page="pagination.current" :page-size="pagination.pageSize"
        :total="pagination.total" :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
        @page-change="handleChange">
      </vxe-pager>
    </template>



    <param-dialog ref="paramModal" @refresh="refresh()"></param-dialog>
  </config-page>
</template>

<script>
import { getParams, deleteParam, batchDeleteParam } from '@/services/system/ParamService'
import ParamDialog from './ParamDialog'
import ConfigPage from '@/components/config-page/ConfigPage'

export default {
  name: 'ParamList',
  components: {
    ParamDialog, ConfigPage
  },
  data() {
    return {
      data: [],
      pagination: {
        current: 1,
        pageSize: 10,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '30', '50', '100'],
        showTotal: total => this.$t('system.param.showtotal') + ` ${total} ` + this.$t('system.param.nape')
      },
      // 查询条件参数
      queryParam: {
        keyword: ''
      },
      selectedRowKeys: [],
      selectedRows: [],
      loading: false,

    }
  },
  computed: {

  },
  mounted() {
    this.loadData({
      current: 1,
      pageSize: this.pagination.pageSize
    });
  },
  methods: {
    loadData: function (parameter) {
      parameter = parameter || {};
      this.loading = true;
      return getParams(Object.assign({
        size: parameter.pageSize,
        page: (parameter.current - 1)
      }, this.queryParam)).then(res => {
        console.log('getParams', res);
        const pagination = { ...this.pagination };
        pagination.total = parseInt(res.totalElements);
        this.loading = false;
        this.data = res.content;
        this.pagination = pagination;
        return res;
      })
    },
    selectChangeEvent({ checked, records, reserves }) {
      this.selectedRowKeys = [...reserves.map(v => v.id), ...records.map(v => v.id)];
      this.selectedRows = [...reserves, ...records];
    },
    handleChange(pagination) {
      this.pagination.current = pagination.currentPage
      this.pagination.pageSize = pagination.pageSize
      this.loadData(this.pagination);
    },
    refresh() {
      this.loadData(this.pagination);
    },
    handleSearch() {
      this.pagination.current = 1 //重置页面下标
      this.refresh();
    },
    handleReset() {
      this.queryParam.keyword = ''
    },
    handleAdd() {
      this.$refs.paramModal.add();
    },
    handleEdit(id) {
      this.$refs.paramModal.edit();
      this.$nextTick(() => {
        this.$refs.paramModal.loadData(id);
      });
    },
    handleView(id) {
      this.$refs.paramModal.view();
      this.$refs.paramModal.loadData(id);
    },
    handleDelete(id) {
      const that = this;
      this.$confirm({
        title: this.$t('system.param.remind.delete.title'), //提示
        content: this.$t('system.param.remind.delete.content'), //确定删除吗？
        okText: this.$t('ok'), //确认
        okType: 'danger',
        cancelText: this.$t('cancel'), //取消
        onOk() {
          deleteParam(id).then(res => {
            that.loading = false;
            that.$message.success(that.$t('system.param.remind.delete.success')); //删除成功！
            that.refresh();
          })
        },
        onCancel() {
        }
      })
    },
    handleBatchDel() {
      const that = this;
      this.$confirm({
        title: this.$t('system.param.remind.delete.title'), //提示
        content: this.$t('system.param.remind.delete.content'), //确定删除吗？
        okText: this.$t('ok'), //确认
        okType: 'danger',
        cancelText: this.$t('cancel'), //取消
        onOk() {
          console.log('batchDeleteParam', that.selectedRowKeys);
          batchDeleteParam(that.selectedRowKeys).then(res => {
            that.loading = false;
            that.selectedRows = [];
            that.$message.success(that.$t('system.param.remind.delete.success')); //删除成功！
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