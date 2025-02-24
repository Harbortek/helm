<template>
  <a-modal :title="title" :ok="false" :visible="visible" :confirmLoading="loading" :closable="false" :keyboard="true"
    centered>
    <a-checkbox-group v-model="checkedList" :options="roleData">
    </a-checkbox-group>
    <a-spin :spinning="loading" />
    <template slot="footer">
      <div style="text-align:center">
        <a-button @click="handleOk" type="primary">{{ $t('ok') }}</a-button>
        <a-button @click="handleCancel">{{ $t('close') }}</a-button>
      </div>
    </template>
  </a-modal>
</template>

<script>
import { getRolesNoPage } from '@/services/system/RoleService'
import { getUserRoles, updateRoles } from '@/services/system/UserService'
export default {
  name: 'UserRoleDialog',
  data() {
    return {
      title: this.$t('system.user.remind.user-role.title'),
      visible: false,
      roleData: [],
      loading: false,
      checkedList: []
    };
  },
  methods: {
    edit(user) {
      this.user = user;
      this.visible = true;
      this.loadData(user);
    },
    handleCancel() { this.close(); },
    handleOk() {
      const that = this;
      const rs = this.checkedList;
      updateRoles(this.user.id, rs).then(res => {
        console.log('updateRoles', res);
        that.$message.success(that.$t('system.user.remind.user-role.success'));
        that.user.roleIds = rs;
        that.close();
        return res;
      })
    },
    close() {
      this.user = null;
      this.checkedList = [];
      this.loading = false;
      this.visible = false;
    },
    loadData(user) {
      const that = this;
      this.loading = true;
      return getRolesNoPage().then(res => {
        console.log('getRoles', res);
        that.roleData = res.map(item=>{
          return {value: item.id, label: item.name}
        })
        that.loading = false;
        getUserRoles(user.id).then(res => {
          console.log('getUserRoles', res);
          that.checkedList = res.map((item) => item.id);
        })
        // this.checkedList = user.roles.map((item) => item.id);
        // return res;
      })
    },
  },
};
</script>