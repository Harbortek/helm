<template>
  <div class="main">
    <a-row style="height: 100%">
      <a-col :span="12" class="h-left">
        <div class="h-welcome">
          <img :src="loginLogo" @error="ImgError" class="logo" alt="logo" />
          <span class="title">{{ $t("user.login.wecone-to") }}{{ systemName }}</span>
        </div>
        <div class="h-bg"></div>
      </a-col>
      <a-col :span="10" class="h-right">
        <a-form class="user-layout-login h-form" :form="form" @submit="handleSubmit">
          <a-form-item class="h-login">
            {{ $t("user.login.login") }}
          </a-form-item>
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
import { loadRoutes} from '@/router/router-helper'


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

        this.$store.dispatch('account/getInfo').then(()=>{
          this.$router.push({ path: "/tracker/projectList" })
          .then(()=>{
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
@h-left-top: 130px;
@h-left-right: 40px;
@h-right-right: 40px;
@h-right-left: 50px;
@h-right-top: 160px;
@h-bg-width: 505px;
@h-bg-height: 460px;
@h-bg-height: 500px;
@h-left-title-font-size: 24px;
@h-form-width: 380px;
@h-form-item-width: 300px;
@h-copyright-left: 115px;
@h-copyright-top: 150px;

@h-left-top-lg: 140px;
@h-left-right-lg: 110px;
@h-bg-right-lg: 120px;
@h-right-left-lg: 120px;
@h-right-top-lg: 250px;
@h-bg-width-lg: 631px;
@h-bg-height-lg: 575px;
@h-left-welcom-margin-bottom-lg: 60px;
@h-left-title-font-size-lg: 32px;
@h-form-width-lg: 480px;
@h-form-item-width-lg: 400px;
@h-copyright-left-lg: 250px;
@h-copyright-top-lg: 240px;

.main {
  height: 100%;

  .h-left {
    background: #f1f7ff;
    height: 100%;

    .h-welcome {
      width: 100%;
      height: 36px;
      line-height: 36px;
      text-align: right;
      position: relative;

      .logo {
        height: 33px;
        vertical-align: top;
        margin-right: 2px;
        border-style: none;
      }

      .title {
        color: #333333;
        font-family: Avenir, "Helvetica Neue", PingFangSCSemibold, Helvetica,
          sans-serif;
        font-weight: 600;
      }
    }

    .h-bg {
      background: #f1f7ff url(~@/assets/h-bg.png) no-repeat;
      width: 100%;
      position: relative;
      background-position: right;
    }
  }

  .h-right {
    height: 100%;

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

      .h-item {
        height: 64px;
        opacity: 0.7;
        border: 1px solid rgba(43, 134, 255, 1);
        border-radius: 12px;

        height: 64px;
        margin-left: 40px;

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
}

@media (max-width: 767px) {

  // col-xs
  .main {
    .h-left {
      padding-top: @h-left-top;

      .h-welcome {
        right: @h-left-right;

        .title {
          font-size: @h-left-title-font-size;
        }
      }

      .h-bg {
        background-size: @h-bg-width @h-bg-height;
        height: @h-bg-height;
        right: @h-right-right;
      }
    }

    .h-right {
      .h-copyright {
        left: @h-copyright-left;
        top: @h-copyright-top;
      }

      .h-form {
        left: @h-right-left;
        top: @h-right-top;

        width: @h-form-width;

        .h-item {
          width: @h-form-item-width;
        }
      }
    }
  }
}

@media (min-width: 768px) and (max-width: 991px) {

  // col-sm
  // col-xs
  .main {
    .h-left {
      padding-top: @h-left-top;

      .h-welcome {
        right: @h-left-right;
      }

      .h-bg {
        background-size: @h-bg-width @h-bg-height;
        height: @h-bg-height;
        right: @h-right-right;
      }
    }

    .h-right {
      .h-form {
        left: @h-right-left;
        top: @h-right-top;

        .h-item {
          width: @h-form-item-width;
        }
      }
    }
  }
}

@media (min-width: 992px) and (max-width: 1199px) {

  // col-md
  // col-xs
  .main {
    .h-left {
      padding-top: @h-left-top;

      .h-welcome {
        right: @h-left-right;
      }

      .h-bg {
        background-size: @h-bg-width @h-bg-height;
        height: @h-bg-height;
        right: @h-right-right;
      }
    }

    .h-right {
      .h-form {
        left: @h-right-left;
        top: @h-right-top;

        .h-item {
          width: @h-form-item-width;
        }
      }
    }
  }
}

@media (min-width: 1200px) {

  // col-lg
  .main {
    .h-left {
      padding-top: @h-left-top-lg;

      .h-welcome {
        right: @h-left-right-lg;

        margin-bottom: @h-left-welcom-margin-bottom-lg;

        .title {
          font-size: @h-left-title-font-size-lg;
        }
      }

      .h-bg {
        background-size: @h-bg-width-lg @h-bg-height-lg;
        height: @h-bg-height-lg;
        right: @h-bg-right-lg;
      }
    }

    .h-right {
      .h-copyright {
        left: @h-copyright-left-lg;
        top: @h-copyright-top-lg;
      }

      .h-form {
        left: @h-right-left-lg;
        top: @h-right-top-lg;
        width: @h-form-width-lg;

        .h-item {
          width: @h-form-item-width-lg;
        }
      }
    }
  }
}
</style>
