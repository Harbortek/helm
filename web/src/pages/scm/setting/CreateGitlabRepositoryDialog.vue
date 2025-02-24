<template>
    <a-modal v-model="visiable" title="关联代码仓" @ok="onOK" @cancel="onCancel" centered :width="700"
        dialogClass="gitlab-dialog">
        <template slot="footer">
            <a-button key="cancel" @click="onCancel">
                取消
            </a-button>
            <a-button key="back" @click="onPrevStep">
                上一步
            </a-button>
            <a-button key="submit" type="primary" @click="onOK">
                确定
            </a-button>
        </template>
        <h-alert description="认证私有 GitLab 需要先在 GitLab 中新建 Application，然后在当前弹窗填入信息并确认认证。" style="margin-bottom:10px;" />

        <a-steps direction="vertical" :current="0" size="small">
            <a-step title="在 GitLab 中新建 Application" status="process">
                <div slot="description">
                    <div class="description-container">
                        <span>1、打开私有 GitLab URL 后，点击页面右上角帐号中的「Settings」->「Applications」；</span>
                        <span>2、根据下方的说明在 GitLab 填入指定字段，然后点击「Save application」即可。</span>
                    </div>
                    <div class="subtitle">
                        Redirect URI 需填写：
                    </div>
                    <div class="copy-action-container">
                        <a-input :value="redirectUri" class="copy-text" :disabled="true" /> <a-button
                            v-clipboard:copy="redirectUri" v-clipboard:success="onCopySuccess"
                            v-clipboard:error="onCopyError">复制</a-button>
                    </div>
                    <div class="subtitle">
                        Scopes 需勾选：
                    </div>
                    <div>
                        <a-tag>api</a-tag> <a-tag>read_user</a-tag> <a-tag>read_repository</a-tag>
                        <a-tag>write_repository</a-tag>
                    </div>
                </div>
            </a-step>
            <a-step title="在当前弹窗填入认证信息" status="process">
                <div slot="description">
                    <div class="description-container">
                        <span>新建 Application 完成后，在 Application 详情能看到 Application ID 和 Secret。请结合上方信息填写以下字段。
                        </span>
                    </div>
                    <a-form-model ref="gitlabForm" layout="vertical" :model="formData" :rules="rules">
                        <a-form-model-item ref="applicationId" label="Application ID" prop="applicationId">
                            <a-input v-model="formData.applicationId" placeholder="请在 GitLab 复制 Application ID 并粘贴到此处"
                                @blur="() => {
                                    $refs.applicationId.onFieldBlur();
                                }" :allowClear="true" />
                        </a-form-model-item>
                        <a-form-model-item ref="secret" label="Secret" prop="secret">
                            <a-input v-model="formData.secret" placeholder="请在 GitLab 复制 Secret 并粘贴到此处" @blur="() => {
                                $refs.secret.onFieldBlur();
                            }" :allowClear="true" />
                        </a-form-model-item>
                        <a-form-model-item ref="host" label="私有 GitLab URL" prop="host">
                            <a-input v-model="formData.host" placeholder="例如 http://192.168.x.x:x/" @blur="() => {
                                $refs.host.onFieldBlur();
                            }" :allowClear="true" />
                        </a-form-model-item>
                    </a-form-model>
                </div>
            </a-step>
        </a-steps>
    </a-modal>
</template>

<script>
import HAlert from '@/components/alert/HAlert.vue'
import XEUtils from 'xe-utils'
export default {
    name: 'CreateGitlabRepositoryDialog',
    components: { HAlert },
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },
        repository: {
            required: true
        }
    },
    data() {
        return {
            formData: {
                applicationId: '',
                secret: '',
                host: 'https://gitlab.harbortek.com/',
            },
            rules: {
                applicationId: [
                    { required: true, message: "请输入Application ID", trigger: "blur" },
                ],
                secret: [
                    { required: true, message: "请输入secret", trigger: "blur" },
                ],
                host: [
                    { required: true, type: 'url', message: "请输入url", trigger: "blur" },
                ],
            },
        }
    },
    computed: {
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
        redirectUri() {
            const repositoryId = this.repository.id
            return process.env.VUE_APP_API_BASE_URL.replace(/\/+$/, '') + `/oauth/${repositoryId}/gitlab/callback`
        }
    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }

            }
        }
    },
    methods: {
        loadData() {
            if (this.$refs.gitlabForm) {
                this.$refs.gitlabForm.resetFields()
            }
        },
        onOK() {
            this.$refs["gitlabForm"].validate((valid) => {
                if (valid) {
                    let url = this.formData.host
                    if (!url.endsWith('/')) {
                        url += '/'
                    }
                    url += 'oauth/authorize?client_id=' + this.formData.applicationId
                    url += '&redirect_uri=' + this.redirectUri
                    url += '&response_type=code&state=HELM&scope=api read_user read_repository write_repository'
                    window.open(url, '_blank')

                    let repo = XEUtils.clone(this.repository)
                    repo.redirectUri = this.redirectUri
                    repo.applicationId = this.formData.applicationId
                    repo.secret = this.formData.secret
                    repo.host = this.formData.host
                    this.$emit("ok", repo);
                }
            })
        },
        onCancel() {
            this.$emit('cancel')
        },
        onPrevStep() {
            this.$emit('previous')
        },
        onCopySuccess() {
            this.$message.success('复制成功');
        },
        onCopyError() {
            this.$message.error('复制失败');
        },
    },
}
</script>

<style lang="less">
.ant-modal.gitlab-dialog {
    .ant-modal-content {
        .ant-modal-body {
            padding: 15px;
        }
    }

}

.description-container {
    width: 710px;
    font-size: 14px;
    font-weight: 400;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.43;
    letter-spacing: normal;
    color: #87888a;

    span {
        display: block;
    }
}

.copy-action-container {
    display: flex;
    padding-top: 5px;

    .copy-text {
        margin-right: 10px;
        border-radius: 2px;
        border: solid 1px #bebfc2;
        color: black;
    }
}

.subtitle {
    height: 20px;
    font-size: 14px;
    font-weight: 400;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.43;
    letter-spacing: normal;
    color: #575859;
    margin-top: 10px;
}
</style>