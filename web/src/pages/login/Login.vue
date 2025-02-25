<template>
  <div class="main">
    <a-row style="height: 100%">
      <a-col :span="14" class="h-left">

        <div class="h-bg"></div>
      </a-col>
      <a-col :span="10" class="h-right">
        <div class="h-right-panel">

          <a-form class="user-layout-login h-form" :form="form" @submit="handleSubmit">
            <div class="h-welcome">
              <img :src="loginLogo" @error="ImgError" class="logo" alt="logo" />
              <span class="title">{{ $t("user.login.wecone-to") }}{{ systemName }}</span>
            </div>
            <!-- <a-form-item class="h-login">
              {{ $t("user.login.login") }}
            </a-form-item> -->
            <a-alert v-if="isLoginError" type="error" showIcon style="margin-bottom: 24px"
              :message="$t('user.login.message-invalid-credentials')" />

            <a-form-item prop="username">
              <a-input class="h-item" type="text" :placeholder="$t('user.login.username.placeholder')" v-decorator="[
                'username',
                { initialValue: 'admin' },
                {
                  rules: [
                    {
                      required: true,
                      message: this.$t('user.login.username.placeholder'),
                      whitespace: true,
                    },
                  ],
                },
              ]">
                <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }" />
              </a-input>
            </a-form-item>

            <a-form-item prop="password">
              <a-input-password class="h-item" :placeholder="$t('user.login.password.placeholder')" v-decorator="[
                'password',
                { initialValue: 'admin' },
                {
                  rules: [
                    {
                      required: true,
                      message: this.$t('user.login.password.placeholder'),
                      whitespace: true,
                    },
                  ],
                },
              ]">
                <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }" />
              </a-input-password>
            </a-form-item>

            <a-form-item style="margin-top: 24px; padding-bottom: 24px; text-align: center">
              <a-button size="large" type="primary" htmlType="submit" class="login-button" :loading="state.loginBtn"
                :disabled="state.loginBtn">{{ $t("user.login.login") }}</a-button>
            </a-form-item>
          </a-form>
        </div>
        <!-- <div class="h-copyright">
          Copyright &copy; 2023 南京浩博科技有限公司
        </div> -->
      </a-col>
    </a-row>
  </div>
</template>

<script>
import md5 from "md5";
import { mapState, mapMutations } from "vuex";
import { setAuthorization } from "@/utils/request";
import { login, info } from "@/services/global/LoginService";
import { loadRoutes } from '@/router/router-helper'


export default {
  components: {},
  computed: {
    ...mapState("setting", ["systemName"]),
  },
  data() {
    return {
      loginBtn: false,
      isLoginError: false,
      form: this.$form.createForm(this),
      state: {
        time: 60,
        loginBtn: false,
      },
      loginLogo: require("@/assets/logo2.png"),
      validateStatus: 'success',
      hasFeedback: false,
      help: '',
    };
  },
  created() {
    if (localStorage.getItem("loginLoge") != null) {
      this.loginLogo = localStorage.getItem("loginLoge");
    }
  },
  methods: {
    ImgError() {
      this.loginLogo = require('@/assets/logo2.png')
    },
    ...mapMutations('setting', ['setSystemName']),
    ...mapMutations("account", ["setUser"]),
    handleSubmit(e) {
      e.preventDefault();
      this.state.loginBtn = true;
      this.form.validateFields((err) => {
        if (!err) {
          this.logging = true;
          const username = this.form.getFieldValue("username");
          const password = this.form.getFieldValue("password");
          login(username, md5(password))
            .then(this.loginSuccess)
            .catch(this.requestFailed)
            .finally(() => {
              this.state.loginBtn = false;
            });
        } else {
          setTimeout(() => {
            this.state.loginBtn = false;
          }, this.state.time);
        }
      });
    },
    loginSuccess(resp) {
      if (resp.code && resp.code.length > 0) {
        this.validateStatus = 'error'
        this.hasFeedback = true
        this.help = resp.message
        this.isLoginError = true;
        this.state.loginBtn = true;
      } else {
        this.validateStatus = 'success'
        this.hasFeedback = false
        this.help = ''
        console.log("登录成功", resp);
        setAuthorization({
          token: resp.access_token,
          expireAt: new Date(resp.expires_in),
        });

        this.$store.dispatch('account/getInfo').then(() => {
          this.$router.push({ path: "/tracker/projectList" })
            .then(() => {
              loadRoutes();
            })
        })

        this.state.loginBtn = true;
        this.isLoginError = false;
      }
    },
    requestFailed(err) {
      this.isLoginError = true;
      this.state.loginBtn = true;
      // this.$message.error(err.message);
    },
  },
};
</script>

<style lang="less">
.main {
  height: 100%;

  .h-left {
    background: #f1f7ff;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;


  }

  .h-bg {
    height: 575px;
    background: #f1f7ff url(~@/assets/h-bg.png) no-repeat;
    width: 631px;
    max-width: 100%;
    position: relative;
    background-position: right;
  }
}

.h-right {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  .h-welcome {
      width: 100%;
      height: 36px;
      line-height: 36px;
      text-align: center;
      position: relative;
      margin-bottom: 20px;
  
      .logo {
        height: 33px;
        vertical-align: top;
        margin-right: 10px;
        border-style: none;
      }
  
      .title {
        color: #333333;
        font-family: Avenir, "Helvetica Neue", PingFangSCSemibold, Helvetica,
          sans-serif;
        font-weight: 600;
        font-size: 30px;        
      }
    }

  .h-copyright {
    color: rgba(0, 0, 0, 0.45);
    font-size: 14px;
    width: 380px;
    position: relative;
  }

  .h-login {
    font-size: 30px;
    color: #333333;
    font-family: Avenir, "Helvetica Neue", PingFangSCSemibold, Helvetica,
      sans-serif;
    font-weight: 600;
    padding-top: 50px;
    padding-bottom: 24px;
    text-align: center;
  }

  .login-button {
    height: 48px;
    font-size: 20px;
    background: #2b86ff;
    border-radius: 32px;
    width: 120px;
  }

  .h-form {
    position: relative;
    background: #ffffff;
    box-shadow: 0px 4px 20px 0px rgba(241, 247, 255, 1);
    box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.2);
    border-radius: 16px;
    width: 80%;
    min-width: 500px;
    padding: 40px;

      .h-item {
        height: 64px;
        opacity: 0.7;
        border: 1px solid rgba(43, 134, 255, 1);
        border-radius: 12px;

        height: 64px;

        input {
          height: 60px;
          border: 0;
          padding: 10px 10px 10px 30px;
          outline-style: none;
          background-color: transparent;
          font-size: 16px;

          &:focus {
            outline-style: none;
            border: 0;
            background-color: transparent;
            box-shadow: none;
            -webkit-box-shadow: none;
            -moz-box-shadow: none;
          }
        }
      }
    }
}
</style>
