<template>
  <config-page :autoFit="true" :title="title" :description="description" id="userList">
    <template #header>
      <vxe-toolbar>
        <template v-slot:buttons>
          <a-input-search @search="handleSearch" v-model="keyword" :placeholder="$t('system.enum.modal.enumName.message')" style="width:300px;"
            enter-button></a-input-search>
        </template>
        <template v-slot:tools>
          <a-space>
            <a-button type="primary" @click="onCreateCategory()" v-action="'PROJECT_ENUM'">{{ $t('add') }}</a-button>
          </a-space>
        </template>
      </vxe-toolbar>
    </template>


    <vxe-table :data="tableData" :loading="loading" :row-config="{ isHover: true }" row-id="id" height="100%">
      <vxe-column title="序号" type="seq" width="60"></vxe-column>
      <vxe-column title="类型" width="60" field="dynamic">
        <template #default="{ row }">
          <a-tag v-if="row.dynamic" color="blue"> 动态 </a-tag>
          <a-tag v-else color="green">静态</a-tag>
        </template>
      </vxe-column>
      <vxe-column field="name" title="枚举名称" width="300">
        <template #default="{ row }">
          {{ row.name }} <a-tag v-if="row.system" style="margin-left:10px;">{{$t('system.enum.table.name.tag1')}}</a-tag> <a-tag v-if="!row.projectId"
            style="margin-left:10px;">{{$t('system.enum.table.name.tag2')}}</a-tag>
        </template>
      </vxe-column>
      <vxe-column field="description" :title="$t('system.enum.table.desc')"></vxe-column>

      <vxe-column align="center" :title="$t('system.enum.table.action')" min-width="280">
        <template #default="{ row }">
          <vxe-button type="text" icon="vxe-icon-edit" :disabled="projectId && !row.projectId"
            @click="onEditCategory(row)" v-action="'PROJECT_ENUM'">{{
    $t('edit') }}</vxe-button>
          <vxe-button type="text" icon="vxe-icon-num-list" v-if="!row.dynamic" @click="onEditItems(row)"
            v-action="'PROJECT_ENUM'">{{
    $t('system.enum.action.enum-config') }}</vxe-button>
          <vxe-button type="text" icon="vxe-icon-num-list" v-if="row.dynamic" @click="onEditScript(row)"
            v-action="'PROJECT_ENUM'">{{
    $t('system.enum.action.enum-config') }}</vxe-button>
          <vxe-button type="text" icon="vxe-icon-delete" :disabled="row.system" v-action="'PROJECT_ENUM'"
            @click="onDeleteCategory(row.id)">
            {{
    $t('delete') }}</vxe-button>
        </template>
      </vxe-column>
    </vxe-table>

    <template #footer>
      <vxe-pager background :current-page.sync="pagination.current" :page-size.sync="pagination.pageSize"
        :total="pagination.total" @page-change="onPageChange"
        :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']">
      </vxe-pager>
    </template>


    <enum-category-dialog :isShowDialog="showCategoryDialog" :enumCategory="enumCategory" :editMode="editMode"
      :projectId="projectId" @cancel="showCategoryDialog = false" @ok="onCreateCategoryOK"></enum-category-dialog>
    <enum-item-dialog :isShowDialog="showItemsDialog" :enumCategory="enumCategory" :projectId="projectId"
      @cancel="showItemsDialog = false" @ok="onEnumItemDialogOK"></enum-item-dialog>
    <enum-script-dialog :isShowDialog="showScriptDialog" :enumCategory="enumCategory" :projectId="projectId"
      @ok="onEnumScriptDialogOK" @cancel="showScriptDialog = false" />
  </config-page>
</template>

<script>
import EnumCategoryDialog from './EnumCategoryDialog.vue'
import EnumItemDialog from './EnumItemDialog.vue'
import EnumScriptDialog from './EnumScriptDialog.vue'
import { findEnumCategories, createEnumCategory, updateEnumCategory, deleteEnumCategory, updateEnums } from '@/services/system/EnumService'

import ConfigPage from '@/components/config-page/ConfigPage'
import VXETable from 'vxe-table'
export default {
  name: 'EnumList',
  components: {
    EnumCategoryDialog, EnumItemDialog, EnumScriptDialog, ConfigPage
  },
  data() {
    return {
      keyword: '',
      tableData: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '30', '50', '100'],
        showTotal: total => this.$t('system.param.showtotal') + ` ${total} ` + this.$t('system.param.nape')
      },
      showCategoryDialog: false,
      enumCategory: {},
      editMode: 'create',
      loading: false,
      showItemsDialog: false,
      showScriptDialog: false,
    }
  },
  mounted() {
    this.loadData();
  },
  computed: {
    projectId() {
      return this.$route.params.projectId
    },
    title() {
      if (this.projectId) {
        return this.$t('system.enum.list.config-page.project.title')
      } else {
        return this.$t('system.enum.list.config-page.title')
      }
    },
    description() {
      if (this.projectId) {
        return this.$t('system.enum.list.config-page.project.description')
      } else {
        return this.$t('system.enum.list.config-page.description')
      }
    }

  },
  created() {

  },
  methods: {
    loadData() {
      this.loading = true;
      const that = this;

      findEnumCategories(this.keyword, this.projectId, this.pagination.current - 1, this.pagination.pageSize).then(resp => {
        that.tableData = resp.content || [];
        this.pagination.total = parseInt(resp.totalElements);
        this.pagination.current = resp.number + 1;
        this.pagination.pageSize = resp.size;
        this.loading = false
      })
    },
    handleSearch() {
      this.pagination.current = 1
      this.loadData();
    },

    onEditItems(category) {
      this.enumCategory = category
      this.showItemsDialog = true
    },
    onCreateCategory() {
      this.editMode = 'create'
      this.enumCategory = { name: '', description: '', projectId: this.projectId }
      this.showCategoryDialog = true
    },
    onEditCategory(row) {
      this.enumCategory = row
      this.editMode = 'edit'
      this.showCategoryDialog = true
    },
    onCreateCategoryOK(category) {
      if (this.editMode === 'create') {
        category.projectId = this.projectId
        createEnumCategory(category).then(resp => {

          this.showCategoryDialog = false
          this.loadData()
        })
      } else {
        updateEnumCategory(category).then(resp => {

          this.showCategoryDialog = false
          this.loadData()
        })
      }
    },
    async onDeleteCategory(id) {
      const type = await VXETable.modal.confirm({
        title:this.$t('system.enum.remind.delete.title'),
        content: this.$t('system.enum.remind.delete.content'),
        confirmButtonText: this.$t('ok'),
        cancelButtonText: this.$t('cancel'),
      })
      if (type === 'confirm') {
        const that = this
        deleteEnumCategory(id).then(res => {
          this.loading = false;
          this.$message.success(that.$t('system.enum.remind.delete.success')); //删除成功！
          this.loadData();
        })
      }
    },
    onEnumItemDialogOK(enums) {
      updateEnums(this.enumCategory.id, this.projectId, enums).then(resp => {
        this.showItemsDialog = false
        this.loadData()
      })
    },
    onPageChange({ currentPage, pageSize }) {
      this.pagination.current = currentPage
      this.pagination.pageSize = pageSize
      this.loadData()
    },
    onEditScript(row) {
      this.enumCategory = row
      this.showScriptDialog = true
    },
    onEnumScriptDialogOK(script) {
      this.enumCategory.script = script
      updateEnumCategory(this.enumCategory).then(resp => {

        this.showScriptDialog = false
        this.loadData()
      })

    }

  }
}
</script>

<style lang="less" scoped>
.box {
  width: 20px;
  height: 20px;
  border: solid 1px black;
}

.tag-box {
  height: 18px;
  line-height: 15px;
  border-radius: 10px;
  color: #909090;
  background: #0000;
}
</style>
