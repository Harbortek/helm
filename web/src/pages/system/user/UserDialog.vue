<template>
  <a-modal :title="title" :ok="false" :visible="visible" :confirmLoading="loading" :closable="false" :keyboard="true"
    centered>

    <a-spin :spinning="loading">
      <a-form :form="form" layout="horizontal" :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }"
        :loading="loading">

        <a-form-item :label="$t('system.user.modal.label.user-id')" :hidden=true>
          <a-input v-decorator="['id']" />
        </a-form-item>
        <a-form-item :label="$t('system.user.modal.label.login-name')">
          <a-input :placeholder="$t('system.user.modal.login-name.message')" :disabled="!isAdd" v-decorator="[
            'loginName',
            { rules: [{ required: true, validator: nameValidator }] },
          ]" />
        </a-form-item>
        <a-form-item :hidden="!isAdd">
          <span slot="label">
            {{ $t('system.user.modal.password') }}&nbsp;
            <a-tooltip :title="$t('system.user.modal.password.title')">
              <a-icon type="question-circle-o" />
            </a-tooltip>
          </span>
          <a-input-password :placeholder="$t('system.user.modal.password.message')"
            v-decorator="['password', { rules: [{ required: true, message: $t('system.user.modal.password.message') + '!', validator: pwdValidator }] }]" />
        </a-form-item>
        <a-form-item :label="$t('system.user.search.label.user-name')">
          <a-input :placeholder="$t('system.user.modal.name.message')" :disabled="isView" v-decorator="[
            'name',
            { rules: [{ required: true, message: $t('system.user.modal.name.message') + '!' }] },
          ]" />
        </a-form-item>
        <a-form-item :label="$t('system.user.modal.mobile.label')">
          <a-input :placeholder="$t('system.user.modal.mobile.message')" :disabled="isView" v-decorator="[
            'mobilePhone',
            { rules: [{ required: true, validator: mobileValidator }] },
          ]" />
        </a-form-item>
        <a-form-item :label="$t('system.user.modal.email.label')">
          <a-input :placeholder="$t('system.user.modal.email.message')" :disabled="isView" v-decorator="[
            'email',
            { rules: [{ required: true, validator: emailValidator }] },
          ]" />
        </a-form-item>
        <a-form-item label="部门">
          <tree-select :treeData='orgTree' :isView="isView" @getData='getOrgData'
            v-decorator="['orgId', { rules: [{ required: true ,message:'请选择所属部门'}] }]" />
        </a-form-item>
        <a-form-item label="角色">
          <a-select
            mode="multiple"
            style="width: 100%"
            placeholder="请选择角色"
            v-decorator="['roles', { rules: [{ required: true ,message:'请选择角色'}] }]"
          >
            <a-select-option v-for="i in roleData" :key="i.value">
              {{ i.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <span slot="label">
            {{ $t('system.user.modal.user-type.label') }}&nbsp;
            <a-tooltip :title="$t('system.user.modal.user-type.title')">
              <a-icon type="question-circle-o" />
            </a-tooltip>
          </span>
          <a-radio-group :disabled="isView" v-decorator="[
            'status',
            { initialValue: '1', rules: [{ required: true }] },
          ]">
            <a-radio value="1">
              {{ $t('system.user.search.label.on') }}
            </a-radio>
            <a-radio value="2">
              {{ $t('system.user.search.label.off') }}
            </a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-spin>
    <template slot="footer">
      <div style="text-align:center">
        <a-button v-if="!isView" @click="handleOk" type="primary">{{ $t('ok') }}</a-button>
        <a-button @click="handleCancel">{{ $t('close') }}</a-button>
      </div>
    </template>
  </a-modal>
</template>

<script>
import { saveUser, updateUser, getUser, isExistsByName } from '@/services/system/UserService'
import ATextarea from 'ant-design-vue/es/input/TextArea'
import TreeSelect from '@/components/input/TreeSelect'
import { getRolesNoPage } from '@/services/system/RoleService'


export default {
  name: "UserDialog",
  components: {
    ATextarea,
    TreeSelect,
  },
  props: {
    treeData: Array,
  },
  data() {
    return {
      form: this.$form.createForm(this, { name: 'user_modal' }),
      labelCol: {
        xs: { span: 20 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
      title: this.$t('system.user.table.action'),
      visible: false,
      loading: false,
      isView: false,
      isAdd: false,
      orgTree: [],
      roleData: [],
      checkedList:[],
    }
  },
  watch: {
    treeData: {
      handler(newV, oldV) {
        this.orgTree = JSON.parse(JSON.stringify(newV).replace(/key/g, 'value'))
      }
    }
  },
  mounted(){
    this.getRoleloadData()
  },
  methods: {
    pwdValidator: (rule, value, callback) => {
      // var passwordreg = /(?=.*\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{6,16}/
      // if (!passwordreg.test(value)) {
      //   callback(new Error('密码必须由数字、字母、特殊字符组合,请输入6-16位.'))
      // }
      // else {
      return callback()
      // }
    },
    emailValidator: function (rule, value, callback) {
      const regEmail = /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
      if (regEmail.test(value)) {
        //合法的邮箱
        return callback()
      }
      callback(this.$t('system.user.remind.error.email'))
    },
    //验证手机号码的规则
    mobileValidator: function (rule, value, callback) {

      const regMobile = /^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/
      if (regMobile.test(value)) {
        //合法的手机号码
        return callback()
      }
      callback(this.$t('system.user.remind.error.mobile'))
    },
    nameValidator: function (rule, value, callback) {
      if (!!!value) {
        callback(this.$t('system.user.remind.error.name'));
        return;
      }
      return isExistsByName(value).then((res) => {
        console.log('isExistsByName');
        if (res && this.form.getFieldsValue()["id"] == null) {
          const err = this.$t('system.user.remind.error.name.exists');
          callback(err);
        } else {
          callback();
        }
      })
    },
    loadData(id) {
      this.loading = false;
      return getUser(id).then(res => {
        this.loading = false;
        console.log('getUser', res);
        this.checkedList = res.roles?.map((item) => item.id);
        this.form.setFieldsValue({ id: res.id, loginName: res.loginName, name: res.name, status: res.status,
             mobilePhone: res.mobilePhone, email: res.email, password: res.password, orgId: res.orgId,roles:this.checkedList });
        return res
      })
    },
    add() {
      this.checkedList=[]
      this.isView = false;
      this.title = this.$t('add'); //新增
      this.visible = true;
      this.isAdd = true;
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
            values.roles=values.roles.map(v=>{return {id:v,name:v}})
            // console.log("updateUser", values);
            updateUser(values).then(function () {
              that.$message.success(that.$t('system.user.remind.success.update'));
              that.close();
              that.$emit('refresh');
            })
          } else {
            values.roles=values.roles.map(v=>{return {id:v,name:v}})
            // console.log("saveUser", values);
            saveUser(values,values.password).then(function () {
              that.$message.success(that.$t('system.user.remind.success.save'));
              that.$emit('refresh');
              that.close();
            })
          }

        } else {
          console.log("err", err)
          return err;
        }
      });
    },
    handleCancel() {
      this.close();
    },
    getOrgData(res) {
      this.form.setFieldsValue({ orgId: res });
    },
    getRoleloadData() {
      const that = this;
      this.loading = true;
        return getRolesNoPage().then(res => {
          console.log('getRoles', res);
          that.roleData = res.map(item=>{
            return {value: item.id, label: item.name}
          })
          that.loading = false;
          return res;
        })
      }
  }
}
</script>

<style scoped></style>