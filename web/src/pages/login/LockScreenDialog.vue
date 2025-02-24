<template>
    <a-modal :visible="showDialog" title="锁屏" :closable="false" :centered="true" :keyboard="false">
        <template slot="footer">
            <a-button key="submit" type="primary" @click="handleSubmit">
                确定
            </a-button>
        </template>
        <a-form-model ref="reloginForm" layout="horizontal" :model="form" :rules="rules" labelAlign="left"
            :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">
            <a-form-item label="用户名称" prop="username">
                {{ user?.name }}
                <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }" />
            </a-form-item>
            <a-form-item label="密码" prop="password" :validateStatus="validateStatus" :hasFeedback="hasFeedback"
                :help="help">
                <a-input-password v-model="form.password" class="h-item"
                    :placeholder="$t('user.login.password.placeholder')">
                    <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }" />
                </a-input-password>
            </a-form-item>
        </a-form-model>
    </a-modal>
</template>

<script>
import md5 from "md5";
import { mapState, mapGetters, mapMutations } from "vuex";
import { setAuthorization } from "@/utils/request";
import { login, info } from "@/services/global/LoginService";
export default {
    name: 'LockScreenDialog',
    props: {
        showDialog: {
            required: true,
        }
    },
    computed: {
        ...mapState("setting", ["systemName"]),
        ...mapGetters("account", ["user"]),
    },
    watch: {
        showDialog: {
            handler(newVal, oldVal) {
                this.form.password = ''
            }
        }
    },
    data() {
        return {
            loginBtn: false,
            isLoginError: false,
            form: {
                username: '',
                password: '',
            },
            rules: {
                password: [{
                    required: true,
                    message: this.$t('user.login.password.placeholder'),
                    whitespace: false,
                    trigger: "blur",
                },]
            },
            state: {
                time: 60,
                loginBtn: false,
            },
            loginLogo: require("@/assets/logo2.png"),
            validateStatus: 'success',
            hasFeedback: false,
            help: '',

        }
    },
    methods: {
        ...mapMutations('setting', ['setSystemName']),
        ...mapMutations("account", ["setUser"]),
        handleSubmit(e) {
            e.preventDefault();
            this.state.loginBtn = true;
            this.$refs.reloginForm.validate((valid) => {
                if (valid) {
                    this.logging = true;
                    const username = this.user.loginName
                    const password = this.form.password
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

                this.$store.dispatch('account/getInfo');
                // this.$router.push({ path: "/" });
                this.state.loginBtn = true;
                this.isLoginError = false;
                this.$emit('ok')
            }

        },
        requestFailed(err) {
            this.isLoginError = true;
            this.state.loginBtn = true;
            // this.$message.error(err.message);
        },
    },
}
</script>

<style></style>