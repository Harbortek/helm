<template>
  <config-page :autoFit="true" :title="$t('system.log.list.config-page.title')"
    :description="$t('system.log.list.config-page.description')" id="logList">
    <template #header>
      <vxe-toolbar>
        <template v-slot:buttons>
          <a-input-search @search="handleSearch" v-model="queryParam.keyword" :placeholder="$t('system.log.list.config-page.input')" style="width:300px;"
            enter-button></a-input-search>
        </template>
      </vxe-toolbar>
    </template>


    <vxe-table :data="data" :loading="loading" :row-config="{ isHover: true }" row-id="id" height="100%"
      @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent" :checkbox-config="{ reserve: true }">
      <vxe-column type="checkbox" width="60"></vxe-column>
      <vxe-column align="center" :title="$t('system.user.table.number')" type="seq" width="60"></vxe-column>
      <vxe-column align="center" field="module" :title="$t('system.log.table.module')"></vxe-column>
      <vxe-column align="center" field="name" :title="$t('system.log.table.title')"></vxe-column>
      <vxe-column align="center" field="description" :title="$t('system.log.table.content')"></vxe-column>
      <vxe-column align="center" field="remoteAddress" :title="$t('system.log.table.remoteAddr')"></vxe-column>
      <vxe-column align="center" field="createBy" :title="$t('system.log.table.createBy')"
        :formatter="formatterCreateBy"></vxe-column>
      <vxe-column align="center" field="createDate" :title="$t('system.log.table.createDate')"></vxe-column>

      <vxe-column align="center" :title="$t('system.enum.table.action')" width="">
        <template #default="{ row }">
          <a @click="handleView(row.id)" v-action="'SYSTEM_LOG'">{{ $t('detail') }}</a>
        </template>
      </vxe-column>
    </vxe-table>

    <template #footer>
      <vxe-pager :loading="loading" :current-page="pagination.current" :page-size="pagination.pageSize"
        :total="pagination.total" :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
        @page-change="handleChange">
      </vxe-pager>
    </template>
    <log-drawer ref="logDrawer"></log-drawer>
  </config-page>
</template>

<script>
import { getLogs } from '@/services/system/LogService'
import LogDrawer from './LogDrawer'
import ConfigPage from '@/components/config-page/ConfigPage'

export default {
  name: 'LogList',
  components: {
    LogDrawer, ConfigPage
  },
  data() {
    return {
      data: [],
      pagination: {
        current: 1,
        pageSize: 10,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '30', '50', '100'],
        showTotal: total => this.$t('system.log.showtotal') + ` ${total} ` + this.$t('system.log.nape')
      },
      statusList: [
        { label: this.$t('system.log.success'), value: true },
        { label: this.$t('system.log.fail'), value: false }
      ],
      // 查询条件参数
      queryParam: {
        keyword: ''
      },
      loading: false,
    }
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
      return getLogs(Object.assign({
        size: parameter.pageSize,
        page: parameter.current - 1
      }, this.queryParam)).then(res => {
        console.log('getLogs', res);
        res.content.map(v => (v.duration = v.endTime - v.startTime))//处理耗时
        const pagination = { ...this.pagination };
        pagination.total = parseInt(res.totalElements);
        this.loading = false;
        this.data = res.content;
        this.pagination = pagination;
        return res;
      })
    },
    // formatterStatus({ cellValue }) {
    //   let item = this.statusList.find(item => item.value === cellValue)
    //   if (item.value == true) {
    //     return item ? `<span class="ant-tag ant-tag-green" style="margin: 0px;">` + item.label + `</span>` : ''
    //   }
    //   return item ? `<span class="ant-tag ant-tag-red" style="margin: 0px;">` + item.label + `</span>` : ''
    // },
    // formatterType({ cellValue }) {
    //   if (cellValue.code == 'OP') {
    //     return `<span class="ant-tag ant-tag-orange" style="margin: 0px;">` + cellValue.type + `</span>`
    //   } else if (cellValue.code == 'LOGIN') {
    //     return `<span class="ant-tag ant-tag-purple" style="margin: 0px;">` + cellValue.type + `</span>`
    //   }
    //   return `<span class="ant-tag ant-tag-blue" style="margin: 0px;">` + cellValue.type + `</span>`
    // },
    // formatterMethod({ cellValue }) {
    //   if (cellValue == 'GET') {
    //     return `<span class="ant-tag ant-tag-blue" style="margin: 0px;">` + cellValue + `</span>`
    //   } else if (cellValue == 'POST') {
    //     return `<span class="ant-tag ant-tag-green" style="margin: 0px;">` + cellValue + `</span>`
    //   } else if (cellValue == 'PUT') {
    //     return `<span class="ant-tag ant-tag-cyan" style="margin: 0px;">` + cellValue + `</span>`
    //   }
    //   return `<span class="ant-tag ant-tag-red" style="margin: 0px;">` + cellValue + `</span>`
    // },
    formatterCreateBy({ cellValue }) {
      if (cellValue) {
        return cellValue.name
      }
    },
    selectChangeEvent({ checked, records, reserves }) {
      this.selectedRowKeys = [...reserves.map(v => v.id), ...records.map(v => v.id)];
      this.selectedRows = [...reserves, ...records];
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
    handleView(logId) {
      this.$refs.logDrawer.view();
      this.$refs.logDrawer.loadData(logId);
    },
    handleChange(pagination) {
      this.pagination.current = pagination.currentPage
      this.pagination.pageSize = pagination.pageSize
      this.loadData(this.pagination);
    }
  }
}
</script>