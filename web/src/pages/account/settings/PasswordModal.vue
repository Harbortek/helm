<template>
  <div>
    <template>
      <a @click="handlePwd">{{ $t('account.settings.security.modify') }}</a>
    </template>
    <a-modal :visible="visible" v-if="visible/*使用v-if 清除弹窗的缓存 */" :title="$t('account.settings.security.modify')"
      :ok-text="$t('account.settings.password.ok')" :cancel-text="$t('account.settings.password.cancel')" @ok="onOk"
      @cancel="onCancel">
      <a-form :form="form" ref="formRef" :labelCol="labelCol" :wrapperCol="wrapperCol">
        <a-form-item :label="$t('account.settings.password.oldPwd')">
          <a-input type="password" :placeholder="$t('account.settings.password.oldPwd.placeholder')"
            v-decorator="['oldPwd', { rules: [{ required: true, message: this.$t('account.settings.password.oldPwd'), validator: oldPwdValidator }] }]" />
        </a-form-item>
        <a-form-item :label="$t('account.settings.password.newPwd1')">
          <a-input type="password" :placeholder="$t('account.settings.password.newPwd1.placeholder')"
            v-decorator="['newPwd1', { rules: [{ required: true, validator: pwdValidator }] }]" />
        </a-form-item>
        <a-row :md="1" :lg="1">
          <a-col :offset="4" :md="15">
            <password-strength :password="password" @on-change="onChange"></password-strength>
          </a-col>

        </a-row>

        <a-form-item :label="$t('account.settings.password.newPwd2')">
          <a-input type="password" :placeholder="$t('account.settings.password.newPwd2.placeholder')"
            v-decorator="['newPwd2', { rules: [{ required: true, validator: pwd2Validator }] }]" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script>
import { updatePassword } from '@/services/global/LoginService'
import PasswordStrength from '@/components/input/PasswordStrength.vue'
import { defineComponent, reactive, ref, UnwrapRef } from 'vue';

export default {
  name: 'PasswordModal',
  components: {
    PasswordStrength,
  },
  data() {
    return {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 }
      },
      form: this.$form.createForm(this, { name: 'password_modal' },),
      visible: false, //modal 显示隐藏
      password: '',
      strength: 0,
      formRef: ref()
    }
  },

  methods: {
    handlePwd: function () {
      this.visible = true;
    },
    oldPwdValidator: function (rule, value, cb) {
      if (!!!value) {
        return cb(new Error(this.$t('account.settings.password.oldPwd.placeholder'))); //请输入旧密码!
      }
      return cb();
    },
    pwdValidator: function (rule, value, cb) {
      this.password = value;
      if (!!!value || this.strength < 2) {
        return cb(new Error('密码应由6-18位数字、字母、符号组成。')); //请输入新密码!
      }
      if (value.length > 18 || value.length < 6) {
        return cb(new Error('请输入6-18位的密码！')); //请输入新密码!
      }
      if (this.form.getFieldValue('newPwd2')) {
        this.form.validateFields(['newPwd2'], (errors, values) => {
        });
      }
      return cb();
    },
    pwd2Validator: function (rule, value, cb) {
      if (!!!value) {
        return cb(new Error(this.$t('account.settings.password.newPwd2.placeholder'))); //请确认新密码!
      }
      let newPwd1 = this.form.getFieldValue("newPwd1");
      if (newPwd1 != value) {
        return cb(new Error(this.$t('account.settings.password.error-message'))); //两次密码输入不一致!
      }
      return cb();
    },
    onOk() {
      const { form: { validateFields } } = this
      const that = this;
      validateFields((errors, values) => {
        if (!errors) {
          updatePassword({
            id: this.$store.getters['account/user'].id,
            password: values.newPwd1,
            oldPwd: values.oldPwd
          }).then(function (res) {
            that.$message.success(that.$t('account.settings.password.update-success')); //密码更新成功！
            that.visible = false;
            return new Promise(resolve => {
              resolve(true)
            })
          }).catch((error) => {
            console.log(error)
            that.$message.error(that.$t('account.settings.password.update-fail')); //密码更新失败！
          });
        }
      })

    },
    onChange(strength) {
      this.strength = strength;
      this.form.setFieldsValue({ newPwd1: this.form.getFieldValue('newPwd1') })
      this.form.validateFields(["newPwd1"], (errors, values) => {

      })
    },
    onCancel() {
      this.visible = false;
      console.log('监听了 modal cancel 事件')
      return new Promise(resolve => {
        resolve(true)
      })
    }
  }
}
</script>

<style scoped></style>
