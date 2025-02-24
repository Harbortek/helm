<template>
  <config-page :title="$t('system.system.list.config-page.title')"
    :description="$t('system.system.list.config-page.description')" id="systemList">
    <a-card :bordered="false" style="height:100%">
      <div class="account-settings-info-view">
        <a-row :gutter="16" type="flex">
          <a-col :order="1" :md="24" :lg="12">
            <a-form :form="form" layout="vertical">
              <a-row :md="16" :lg="8">
                <a-col :md="24" :lg="12">
                  <a-form-item :label="$t('system.system.name.label')">
                    <a-input :placeholder="$t('system.system.name.placeholder')" show-count :max-length="10" v-decorator="[
                      'name',
                      { rules: [{ required: true, message: this.$t('system.system.name.placeholder') }] },
                    ]" /><br>
                    <span style="color:#B0B0B0">{{ $t("system.system.name.remind") }}</span>
                  </a-form-item>
                </a-col>
              </a-row>
              <a-form-item :label="$t('system.system.logo.label')">
                <div class="avatar-upload-preview">
                  <img style="background: black" width="100" :src="logoImg" />
                </div><br>
                <a-upload name="file" :max-count="1" :beforeUpload="beforeUpload" :showUploadList="false">
                  <a-button>{{ $t('system.system.file.button') }}</a-button>
                </a-upload><br>
                <span style="color:#B0B0B0">{{ $t('system.system.file.remind') }}</span>
              </a-form-item>

              <a-form-item :label="$t('system.system.loginlogo.label')">
                <div class="avatar-upload-preview">
                  <img width="100" :src="loginLogoImg" />
                </div><br>
                <a-upload name="loginFile" :max-count="1" :beforeUpload="beforeUploadLogin" :showUploadList="false">
                  <a-button>{{ $t('system.system.file.button') }}</a-button>
                </a-upload><br>
                <span style="color:#B0B0B0">{{ $t('system.system.file.remind') }}</span>
              </a-form-item>

              <a-form-item>
                <a-button v-action="'system'" type="primary" @click="finish()">{{ $t('system.system.form.update') }}</a-button>
              </a-form-item>
            </a-form>
          </a-col>
        </a-row>
      </div>
    </a-card>
  </config-page>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import { updateSystem } from '@/services/system/SystemService'
import { iconUrl } from "@/utils/util"
import ConfigPage from '@/components/config-page/ConfigPage'
import { isPast } from 'date-fns'


export default {
  name: 'SettingPage',
  components: {
    ConfigPage
  },
  data() {
    return {
      form: this.$form.createForm(this),
      logoImg: '',
      loginLogoImg: '',
      imgData: '',
      imgLoginData: '',
    }
  },
  mounted() {
    this.loadData();
  },
  computed: {
    ...mapState('setting', ['theme', 'layout', 'systemName', "systemLogo", 'lang', 'pageWidth']),
  },
  watch: {
    systemLogo(newLogo, oldLogo) {
      this.form.setFieldsValue({ name: this.systemName })
      this.logoImg = newLogo;
      if (localStorage.getItem("loginLogo") != null) {
        this.loginLogoImg = localStorage.getItem("loginLogo");
      }
    }
  },
  methods: {
    ...mapMutations('setting', ['setSystemName', 'setSystemLogo']),
    beforeUpload(file) {
      const reader = new FileReader()
      this.imgData = file;
      reader.readAsDataURL(file)
      reader.onload = () => {
        this.logoImg = reader.result
      }
      return false
    },
    beforeUploadLogin(file) {
      const reader = new FileReader()
      this.imgLoginData = file;
      reader.readAsDataURL(file)
      reader.onload = () => {
        this.loginLogoImg = reader.result
      }
      return false
    },
    finish() {
      this.form.validateFields((err, values) => {
        if (!err) {
          const _this = this
          const formData = new FormData()
          formData.append("id", this.$store.getters['account/user'].systemId)
          formData.append("name", this.form.getFieldsValue()["name"])
          if (this.imgData) {
            formData.append("file", this.imgData, this.fileName)
          }
          if (this.imgLoginData) {
            formData.append("loginFile", this.imgLoginData)
          }
          updateSystem(formData).then(function (res) {
            _this.setSystemName(res.name);
            _this.setSystemLogo(iconUrl(res.logo));
            console.log("a")
            localStorage.setItem("loginLogo", _this.loginLogoImg)
            _this.$message.success(_this.$t('system.system.form.update-success')); //头像更新成功！
          }).catch((error) => {
            console.log(error)
            _this.$message.error(_this.$t('system.system.form.update-fail')); //头像更新失败！
          });

        }
      })

    },
    loadData() {
      this.form.setFieldsValue({ name: this.systemName })
      this.logoImg = this.systemLogo;
      if (localStorage.getItem("loginLogo") != null) {
        this.loginLogoImg = localStorage.getItem("loginLogo");
      }
    },
  }
}
</script>