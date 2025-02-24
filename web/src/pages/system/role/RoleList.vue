<template>
  <config-page :title="$t('system.role.list.config-page.title')"
    :description="$t('system.role.list.config-page.description')" id="roleList">
    <a-row type="flex" :gutter="8" style="height:100%">
      <a-col :span="6"> 
        <a-card :title="$t('system.role.search.label.role-name')" :loading="roleLoading" style="min-height:600px">
          <template #extra>
            <a-button type="link" :title="$t('system.role.a-card-button-refresh')" icon="reload"
              @click="handleRoleReload()" />
            <a-button type="link" :title="$t('system.role.a-card-button-save')" icon="plus" @click="handleAdd()"
              v-action="'SYSTEM_ROLE'" />
          </template>
          <a-menu :selectable="!permLoading" mode="vertical" style="border: 0;"
            :defaultSelectedKeys="defaultSelectedKeys">
            <a-menu-item @click="loadPerms(item.id, item.name)" v-for="(item, i) in roles" :key="item.id">
              {{ item.name }}
              <a-dropdown>
                <a-icon style="position: absolute;top: 30%;right: 0px;width: 10px;" type="more" />
                <a-menu v-action="'SYSTEM_ROLE'" slot="overlay">
                  <a-menu-item @click="handleEdit(item.id)">
                    {{ $t('edit') }}
                  </a-menu-item>
                  <a-menu-item @click="handleDelete(item.id)">
                    {{ $t('delete') }}
                  </a-menu-item>
                  <a-menu-item @click="handleView(item.id)">
                    {{ $t('detail') }}
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </a-menu-item>
          </a-menu>
        </a-card>
      </a-col>
      <a-col :span="18">
        <a-card :title="roleName" :loading="permLoading" style="min-height:600px">
          <template #extra>
            <a-button type="link" icon="reload" @click="loadPerms()">{{$t('system.role.a-card-button-refresh')}}</a-button>
            <a-button v-action="'SYSTEM_ROLE'" type="link" icon="save" @click="savePerms()">{{$t('system.role.a-card-button-save')}}</a-button>
          </template>
          <a-descriptions bordered :column="1">
            <a-descriptions-item v-for="(val, key, i) in perms" :key="i" bordered>
              <span slot="label">{{ $t('permission.group.' + key) }} </span>
              <a-checkbox v-for="(item3, k) in val" :key="k" @change="handlePermChange($event, item3)"
                :defaultChecked="item3.checked">{{ $t('permission.name.' + item3.name) }}</a-checkbox>
            </a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
      <role-dialog ref="roleDrawer" @refresh="handleRoleReload()"></role-dialog>
    </a-row>
  </config-page>
</template>

<script>
import RoleDialog from './RoleDialog.vue'
import { getRole, getRolesNoPage, batchDeleteRole, deleteRole, findRolePerms, saveRolePerms } from '@/services/system/RoleService'
import { getPerms } from '@/services/system/PermissionService'
import ConfigPage from '@/components/config-page/ConfigPage'


export default {
  name: 'RoleList',
  components: {
    RoleDialog, ConfigPage
  },
  data() {
    return {
      roles: [],
      defaultSelectedKeys: [],
      perms: [],
      roleLoading: false,
      permLoading: false,
      loading: false,

      roleName: this.$t('system.role.permission-list'),
      checklist: [],
      loadingMore: false,
      showLoadingMore: true
    }
  },
  mounted() {
    this.loadData()
  },
  created() {
  },
  methods: {
    handleView(id) {
      this.$refs.roleDrawer.view()
      this.$refs.roleDrawer.loadData(id)
    },
    handleDelete(id) {
      const that = this
      this.$confirm({
        title: this.$t('system.role.remind.delete.title'),//提示
        content: this.$t('system.role.remind.delete.content'),//确定删除吗？
        okText: this.$t('ok'),
        okType: 'danger',
        cancelText: this.$t('cancel'),
        onOk() {
          deleteRole(id).then(res => {
            that.loading = false
            that.$message.success(that.$t('system.role.remind.delete.success'))//删除成功！
            that.handleRoleReload()
          })
        },
        onCancel() {
        }
      })
    },
    handleEdit(id) {
      this.$refs.roleDrawer.edit()
      this.$nextTick(() => {
        this.$refs.roleDrawer.loadData(id)
      })
    },
    savePerms() {
      const that = this
      saveRolePerms(this.id, this.checklist).then(res => {
        that.$message.success(that.$t('system.role.remind.save.success'))
        location.reload()
        return res
      })
    },
    handlePermChange(e, item) {
      if (e.target.checked) {
        this.checklist.push(item.name)
      } else {
        this.checklist = this.checklist.filter(obj => obj !== item.name)
      }
      this.checklist = this.checklist.filter(obj => obj != null)
    },
    loadData: function (parameter) {
      parameter = parameter || {}
      this.roleLoading = true
      const that = this
      return getRolesNoPage().then(res => {
        that.roles = res.map(function (item) {
          return {
            name: item.name,
            id: item.id,
          }
        })
        that.clear()
        if (that.roles && that.roles.length > 0) {
          const currentRole = that.roles[0]
          that.defaultSelectedKeys = [currentRole.id]
          that.loadPerms(currentRole.id, currentRole.name)
        }
        that.roleLoading = false
        return res
      })
    },
    clear() {
      this.roleName = this.$t('system.role.permission-list')
      this.id = null
      this.perms = []
      this.checklist = []
      this.roleLoading = false
      this.permLoading = false
    },
    handleRoleReload() {
      this.clear()
      this.loadData()
    },
    handleAdd() {
      this.$refs.roleDrawer.add()
    },
    loadPerms(id, roleName) {
      if (!id && !this.id) {
        this.$message.success(this.$t('system.role.remind.message.info'))
        return
      }
      // if(this.id==id) return
      this.id = id || this.id
      this.roleName = roleName ? this.$t('system.role.permission-list') + '（' + roleName + '）' : this.roleName
      this.permLoading = true
      const that = this
      const p1 = getPerms()
      const p2 = getRole(that.id)
      const p3 = findRolePerms(that.id)

      return Promise.all([p1, p2, p3]).then(function (a) {
        that.permLoading = true
        const role = a[1]
        const res = a[0]
        const permissions = a[2] || []
        for (const group in res) {
          res[group].forEach(perm => {
            that.checklist = permissions.map(function (obj) {
              if (obj === perm.name) {
                perm.checked = true
              }
              return obj
            })
          })
        }
        that.perms = res
        that.permLoading = false
      })
    }
  }
}
</script>

<style lang="less" scoped></style>
