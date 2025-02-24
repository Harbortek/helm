<template>
  <div class="account-settings-info-view">
    <a-row :gutter="16" type="flex" justify="center">
      <a-col :order="1" :md="24" :lg="16">

        <a-form :form="form" layout="vertical">
          <a-form-item :label="$t('system.user.modal.label.user-id')" :hidden=true>
            <a-input v-decorator="['id']" />
          </a-form-item>
          <a-form-item :label="$t('account.settings.basic.login-name')">
            <a-input :placeholder="$t('account.settings.basic.login-name-message')" disabled
              v-decorator="['loginName', { rules: [{ required: true }] },]" />
          </a-form-item>
          <a-form-item :label="$t('account.settings.basic.user-name')">
            <a-input :placeholder="$t('account.settings.basic.user-name-message')"
              v-decorator="['name', { rules: [{ required: true, message: $t('account.settings.basic.user-name-message') }] },]" />
          </a-form-item>
          <a-form-item :label="$t('account.settings.basic.phone')">
            <a-input :placeholder="$t('account.settings.basic.phone-message')"
              v-decorator="['mobilePhone', { rules: [{ required: true, validator: mobileValidator }] },]" />
          </a-form-item>

          <a-form-item :label="$t('account.settings.basic.email')">
            <a-input :placeholder="$t('account.settings.basic.email-message')"
              v-decorator="['email', { rules: [{ required: true, validator: emailValidator }] },]" />
          </a-form-item>

          <a-form-item>
            <a-button type="primary" @click="handleUpdate()">{{ $t('account.settings.basic.update') }}</a-button>
          </a-form-item>
        </a-form>

      </a-col>
      <a-col :order="1" :md="24" :lg="8" :style="{ minHeight: '180px' }">
        <div class="ant-upload-preview" @click="$refs.modal.edit(1)">
          <a-icon type="cloud-upload-o" class="upload-icon" />
          <div class="mask">
            <a-icon type="plus" />
          </div>
          <img :src="option.img" />
        </div>
      </a-col>

    </a-row>

    <avatar-modal ref="modal" @ok="setAvatar">
    </avatar-modal>

  </div>
</template>

<script>
import AvatarModal from './AvatarModal'
import { updateAccount } from '@/services/global/LoginService'
import { AppEvents } from '@/utils/event'
import { iconUrl } from "@/utils/util"
export default {
  components: {
    AvatarModal
  },
  mounted() {
    this.loadData();
  },
  data() {
    return {
      // cropper
      form: this.$form.createForm(this),
      preview: {},
      currentUser: {},
      option: {
        // img: '/avatar2.jpg',
        // img: require('@/assets/h-bg.png'),
        img: '',
        info: true,
        size: 1,
        outputType: 'jpeg',
        canScale: false,
        autoCrop: true,
        // 只有自动截图开启 宽度高度才生效
        autoCropWidth: 180,
        autoCropHeight: 180,
        fixedBox: true,
        // 开启宽度和高度比例
        fixed: true,
        fixedNumber: [1, 1]
      }
    }
  },
  methods: {
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
    loadData() {
      let info = this.$store.getters['account/user'];
      if (info != null) {
        this.option.img = iconUrl(info["icon"]);//头像
        this.form.setFieldsValue({ id: info.id, loginName: info.loginName, name: info.name, mobilePhone: info.mobilePhone, email: info.email });
        //更新挂在完成
        this.$bus.$emit(AppEvents.CONTENT_LOADING, {
          type: AppEvents.CONTENT_LOADING,
          data: false
        });
      }
    },
    handleUpdate: function () {
      const that = this;
      this.form.validateFields((err, values) => {
        if (!err) {
          updateAccount(values).then(function (res) {
            console.log('updateAccount', res);
            that.$message.success(that.$t('account.settings.basic.update-success'));
            return res;
          })
        }
      });
    },
    setAvatar(url) {
      this.option.img = iconUrl(url);
      this.$store.getters['account/user']["icon"] = url;
    }
  }
}
</script>

<style lang="less" scoped>
.avatar-upload-wrapper {
  height: 200px;
  width: 100%;
}

.ant-upload-preview {
  position: relative;
  margin: 0 auto;
  width: 100%;
  max-width: 180px;
  border-radius: 50%;
  box-shadow: 0 0 4px #ccc;

  .upload-icon {
    position: absolute;
    top: 0;
    right: 10px;
    font-size: 1.4rem;
    padding: 0.5rem;
    background: rgba(222, 221, 221, 0.7);
    border-radius: 50%;
    border: 1px solid rgba(0, 0, 0, 0.2);
  }

  .mask {
    opacity: 0;
    position: absolute;
    background: rgba(0, 0, 0, 0.4);
    cursor: pointer;
    transition: opacity 0.4s;

    &:hover {
      opacity: 1;
    }

    i {
      font-size: 2rem;
      position: absolute;
      top: 50%;
      left: 50%;
      margin-left: -1rem;
      margin-top: -1rem;
      color: #d6d6d6;
    }
  }

  img,
  .mask {
    width: 100%;
    max-width: 180px;
    height: 100%;
    border-radius: 50%;
    overflow: hidden;
  }
}
</style>
