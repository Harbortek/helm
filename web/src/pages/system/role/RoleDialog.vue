<template>
  <a-modal :title="title" :width="480" :visible="visible" :body-style="{ paddingBottom: '80px' }" @close="handleCancel"
    centered>
    <a-spin :spinning="loading">
      <a-form :form="form" :label-col="labelCol" :wrapper-col="wrapperCol" :loading="loading">
        <a-form-item :label="$t('system.role.modal.label.role-id')" :hidden=true>
          <a-input v-decorator="['id']" />
        </a-form-item>
        <a-form-item :label="$t('system.role.search.label.role-name')">
          <a-input :placeholder="$t('system.role.modal.role-name.message')" :disabled="isView" v-decorator="[
            'name',
            { rules: [{ required: true, validator: nameValidator }] },
          ]" />
        </a-form-item>
        <!-- <a-form-item :label="$t('system.role.modal.label.role-code')">
          <a-input :placeholder="$t('system.role.modal.role-code.message')" :disabled="isView" v-decorator="[
            'code',
            { rules: [{ required: true, validator: codeValidator }] },
          ]" />
        </a-form-item>
        <a-form-item :label="$t('system.role.table.desc')">
          <a-textarea :placeholder="$t('system.role.modal.desc.message')" :disabled="isView" v-decorator="[
            'description',
            { rules: [{ required: true, message: this.$t('system.role.modal.desc.message') + '!' }] },
          ]" />
        </a-form-item> -->
      </a-form>
    </a-spin>
    <div :style="{
      position: 'absolute',
      right: 0,
      bottom: 0,
      width: '100%',
      borderTop: '1px solid #e9e9e9',
      padding: '10px 16px',
      background: '#fff',
      textAlign: 'right',
      zIndex: 1,
    }">
      <a-button style="margin-right: 8px" @click="handleCancel">{{ $t('cancel') }}</a-button>
      <a-button type="primary" @click="handleOk">{{ $t('ok') }}</a-button>
    </div>
  </a-modal>
</template>

<script>
import { saveRole, updateRole, getRole, isExistsByName, isExistsByCode } from '@/services/system/RoleService'
import ATextarea from 'ant-design-vue/es/input/TextArea'
export default {
  name: "RoleDialog",
  components: {
    ATextarea
  },
  data() {
    return {
      labelCol: {
        xs: { span: 20 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
      title: this.$t('system.role.table.action'),//操作
      visible: false,
      loading: false,
      isView: false,
      isAdd: false,
      form: this.$form.createForm(this, { name: 'role_actions' }),
      roleCode: '',
      roleName: ''
    }

  },
  created() {
  },
  methods: {
    nameValidator: function (rule, value, callback) {
      if (!!!value) {
        callback(this.$t('system.role.modal.role-name.message') + '');//请输入角色名称！
        return;
      }
      return isExistsByName(value).then((res) => {
        if (res && this.roleName != this.form.getFieldsValue()["name"]) {
          const err = this.$t('system.role.modal.role-name.message-error');//角色名称不能重复!
          callback(err);
        }
        callback();
      })
    },
    codeValidator: function (rule, value, callback) {
      if (!!!value) {
        callback(this.$t('system.role.modal.role-code.message') + '！');//请输入角色编码！
        return;
      }
      return isExistsByCode(value).then((res) => {
        console.log('isExistsByCode', res);
        if (res && this.roleCode != this.form.getFieldsValue()["code"]) {
          const err = this.$t('system.role.modal.role-code.message-error'); //角色编码不能重复!
          callback(err);
        }
        callback();
      })
    },
    loadData(id) {
      this.loading = true;
      return getRole(id).then(res => {
        this.loading = false;
        console.log('getRole', res);
        this.form.setFieldsValue({ id: res.id, name: res.name, code: res.code, description: res.description });
        this.roleName = res.name;
        this.roleCode = res.code;
        return res
      })
    },
    add() {
      this.isView = false;
      this.title = this.$t('add'); //新增
      this.visible = true;
      this.isAdd = true;
      this.roleName = '';
      this.roleCode = '';
    },
    edit() {
      this.isAdd = false;
      this.isView = false;
      this.title = this.$t('edit');//编辑
      this.visible = true;
    },
    view() {
      this.isAdd = false;
      this.title = this.$t('detail'); //详情
      this.visible = true;
      this.isView = true;
    },
    close() {
      this.form.resetFields();
      this.visible = false;
    },
    handleOk(e) {
      e.preventDefault();
      const that = this;
      this.form.validateFields((err, values) => {
        if (!err) {
          if (values.id) {
            console.log("updateRole", values);
            updateRole(values).then(function () {
              that.$message.success(that.$t('system.role.update-success'));  //更新成功!
              that.close();
              that.$emit('refresh');
            })
          } else {
            console.log("saveRole", values);
            saveRole(values).then(function () {
              that.$message.success(that.$t('system.role.save-success'));  //保存成功!
              that.$emit('refresh');
              that.close();
            })
          }

        } else {
          return err;
        }
      });
    },
    handleCancel() {
      this.close();
    }
  }
}
</script>

<style scoped></style>