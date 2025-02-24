<template>
  <config-page :title="$t('system.org.list.config-page.title')"
    :description="$t('system.org.list.config-page.description')" id="orgList">
    <vxe-toolbar size="medium">
      <template #tools>
        <div v-action="'SYSTEM_DEPT'">
          <vxe-button type="primary" @click="onCreateTopOrg">{{ $t("system.org.button.top-org.add") }}</vxe-button>
          <vxe-button type="primary" :disabled="!isNodeSelected" @click="onCreateChildOrg">{{
            $t("system.org.button.child-org.add") }}</vxe-button>
          <vxe-button type="primary" :disabled="!isNodeSelected" @click="onModifyOrg">{{ $t("edit")}}</vxe-button>
          <a-divider type="vertical" />
          <vxe-button type="default" icon="vxe-icon-arrow-up" :disabled="!isNodeSelected" @click="onMoveUp">{{
            $t("system.org.button.move-up") }}</vxe-button>
          <vxe-button type="default" icon="vxe-icon-arrow-down" :disabled="!isNodeSelected" @click="onMoveDown">{{
            $t("system.org.button.move-down") }}</vxe-button>
          <vxe-button type="default" icon="vxe-icon-delete" :disabled="!isNodeSelected" @click="onDelete">{{ $t("delete")
          }}</vxe-button>
          <a-divider type="vertical" />
          <vxe-button icon="vxe-icon-upload" @click="onImport">{{
            $t("system.org.button.import")
          }}</vxe-button>
          <vxe-button icon="vxe-icon-download" @click="onExport">{{
            $t("system.org.button.export")
          }}</vxe-button>
        </div>
      </template>
    </vxe-toolbar>

    <a-row :gutter="8">
      <a-col :span="12">
        <a-tree :tree-data="orgTree" @select="onTreeNodeSelect"> </a-tree>
      </a-col>
    </a-row>

    <a-modal v-if="isShowOrganizationAdd" v-model="isShowOrganizationAdd" :title="$t('system.org.organiztion.add')"
      @ok="onSaveOk" @cancel="onDialogClose">
      <a-form-model :model="orgToAdd" :rules="rules" ref="addForm" layout="horizontal" :labelCol="{ span: 6 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <a-form-model-item prop="name" :label="$t('system.org.organiztion.name')">
          <a-input v-model="orgToAdd.name" :placeholder="$t('system.org.organiztion.name')" />
        </a-form-model-item>
        <a-form-model-item v-if="isNodeSelected" :label="$t('system.org.organiztion.top')">
          <a-input v-model="selectedOrg.name" disabled />
        </a-form-model-item>
      </a-form-model>
    </a-modal>

    <a-modal v-model="isShowOrganizationModify" :title="$t('system.org.organiztion.edit')" @ok="onSaveModify"
      @close="onDialogClose">
      <a-form-model :model="orgToModify" :rules="rules" ref="modifyForm" layout="horizontal" :labelCol="{ span: 6 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <a-form-model-item prop="name" :label="$t('system.org.organiztion.name')" required>
          <a-input v-model="orgToModify.name" :placeholder="$t('system.org.organiztion.name')" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>

    <a-drawer :title="$t('system.org.organiztion.import')" placement="top" :destroyOnClose="true" :get-container="false"
      :wrap-style="{ position: 'absolute' }" height="420" :closable="false" :visible="isShowImportDialog">
      <OrganizationImport @closeImport="onCloseImport()"></OrganizationImport>
    </a-drawer>
  </config-page>
</template>

<script>
import ConfigPage from '../../../components/config-page/ConfigPage.vue'
import OrganizationImport from '@/pages/system/org/OrganizationImport'
import {
  findOrganizationTree, createOrganization, deleteOrganization,
  moveUpOrganization, moveDownOrganization, exportOrganization,
  updateOrganization, isExistsByName
} from '@/services/system/OrgService'

export default {
  name: 'OrganizationList',
  components: {
    OrganizationImport,
    ConfigPage
  },
  data() {
    return {
      // loading: true,
      // 分页相关
      totalItems: 0,
      currentPage: 1,
      pageSize: 20,
      queryForm: {
        keyword: ''
      },
      isShowOrganizationAdd: false, // 控制添加目录组件是否显示
      isShowOrganizationModify: false, //控制修改部门组件是否显示
      isShowImportDialog: false,
      isNodeSelected: false,
      parentNode: {},
      orgToAdd: {
        orgId: '',
        name: ''
      },
      orgToModify: {
        orgId: '',
        name: ''
      },
      // 查询参数
      queryParam: {},
      // 表头
      columns: [
        {
          title: '#',
          dataIndex: 'no'
        },
        {
          title: this.$t("system.org.table.name"), //成员名称
          dataIndex: 'description'
        },
        {
          title: this.$t("system.org.table.call-no"), //登录次数
          dataIndex: 'callNo',
          sorter: true,
          needTotal: true,
          customRender: (text) => text + ' 次'
        },
        {
          title: this.$t("system.org.table.status"), //状态
          dataIndex: 'status',
          needTotal: true
        },
        {
          title: this.$t("system.org.table.update-time"), //更新时间
          dataIndex: 'updatedAt',
          sorter: true
        },
        {
          title: this.$t("system.org.table.action"), //操作
          dataIndex: 'action',
          width: '150px',
          scopedSlots: { customRender: 'action' }
        }
      ],
      orgTree: [],
      selectedRowKeys: [],
      selectedRows: [],
      rules: {
        name: [{ validator: this.nameValidator, trigger: ['blur', 'change'] }],
      },
      name: '',
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      // const keyword = this.queryForm.keyword
      findOrganizationTree().then((res) => {
        this.orgTree = res
        this.loading = false
      })
    },
    nameValidator(rule, value, callback) {
      if (!!!value) {
        return callback(new Error(this.$t('system.org.remind.error.name')));
      }
      return isExistsByName(value).then((res) => {

        console.log('isExistsByName', res, this.name);
        if (res && this.name != value) {
          const err = this.$t('system.org.remind.error.name.exists');
          return callback(new Error(err));
        } else {
          return callback();
        }
      })
    },
    onTreeNodeSelect(selectedKeys, e) {
      if (selectedKeys.length > 0) {
        this.selectedOrg = { orgId: selectedKeys[0], name: e.selectedNodes[0].data.props.title }
        this.isNodeSelected = true
        this.orgToModify = this.selectedOrg
      }
    },
    onCreateTopOrg() {
      this.selectedOrg = { orgId: 0, name: '' }
      this.orgToAdd = { name: '' }
      this.isShowOrganizationAdd = true
    },
    onCreateChildOrg() {
      this.orgToAdd = { name: '', parentId: this.selectedOrg.orgId }
      this.isShowOrganizationAdd = true
    },
    onModifyOrg() {
      this.isShowOrganizationModify = true
      this.name = this.orgToModify.name
    },
    onSaveModify() {
      this.$refs["modifyForm"].validate((valid) => {
        if (valid) {
          updateOrganization({
            id: this.orgToModify.orgId,
            name: this.orgToModify.name,
          }).then((res) => {
            this.$message.success(this.$t("system.org.remind.update-success"))
            this.isShowOrganizationModify = false
            this.loadData()
          })
          this.name = ''
        }
      });
    },
    onDelete() {
      const orgId = this.selectedOrg.orgId
      this.$confirm({
        title: this.$t('ok'),
        content: this.$t('system.org.remind.delete.content'),
        okText: this.$t('ok'),
        okType: 'danger',
        cancelText: this.$t('cancel'),
        onOk: () => {
          deleteOrganization(orgId).then((res) => {
            if (res) {
              this.$message.success(this.$t('system.org.remind.delete.success'),)
              this.loadData()
            } else {
              this.$message.error(this.$t('system.org.remind.delete.fail'))
            }

          })
        }
      })
    },
    onDialogClose() {
      this.isShowOrganizationAdd = false
      this.loadData()
      this.selectedOrg = this.orgToModify
      this.name = ''
    },
    onSaveOk() {
      this.$refs["addForm"].validate((valid) => {
        if (valid) {
          createOrganization(this.orgToAdd).then((res) => {
            this.isShowOrganizationAdd = false
            this.$message.success(this.$t('system.org.button.org.add.success'),)
            this.loadData()
          })
        }
      });
    },
    onMoveUp() {
      moveUpOrganization(this.selectedOrg.orgId).then((res) => {
        this.$message.success(this.$t('system.org.button.move-up.success'),)
        this.loadData()
      })
    },
    onMoveDown() {
      moveDownOrganization(this.selectedOrg.orgId).then((res) => {
        this.$message.success(this.$t('system.org.button.move-down.success'),)
        this.loadData()
      })
    },
    onImport() {
      this.isShowImportDialog = true
    },
    onExport() {
      exportOrganization()
    },
    onCloseImport() {
      this.isShowImportDialog = false
      this.loadData()
    }
  }
}
</script>
