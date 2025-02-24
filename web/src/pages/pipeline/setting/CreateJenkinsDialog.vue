<template>
    <a-modal v-model="visiable" :title="$t('config.pipline.associate.jenkins')" @ok="onOK" @cancel="onCancel" centered :width="700"
        dialogClass="gitlab-dialog">
        <template slot="footer">
            <div style="display:flex">
                <div style="flex:0 0">
                    <a-button key="test" type="primary" @click="onTest">
                        {{$t('config.pipline.edit.test-connect')}}
                    </a-button>
                </div>

                <div style="flex: 1 1 auto;">
                    <a-button key="cancel" @click="onCancel">
                        {{$t('cancel')}}
                    </a-button>
                    <a-button key="back" @click="onPrevStep" v-if="editMode === 'create'">
                        {{$t('config.pipline.associate.pre-step')}}
                    </a-button>

                    <a-button key="submit" type="primary" @click="onOK">
                        {{$t('ok')}}
                    </a-button>
                </div>
            </div>
        </template>
        <h-alert
            :description="$t('config.pipline.config-page.title')"
            style="margin-bottom:10px;" />


        <a-form-model ref="jenkinsForm" layout="vertical" :model="formData" :rules="rules">
            <a-form-model-item ref="serverUrl" label="Jenkins URL" prop="serverUrl">
                <a-input v-model="formData.serverUrl" :placeholder="$t('config.pipline.create.serverUrl')" @blur="() => {
                    $refs.serverUrl.onFieldBlur();
                }" />
            </a-form-model-item>
            <a-form-model-item ref="userName" :label="$t('config.pipline.create.userName.label')" prop="userName">
                <a-input v-model="formData.userName" :placeholder="$t('config.pipline.create.userName')" @blur="() => {
                    $refs.userName.onFieldBlur();
                }" />
            </a-form-model-item>
            <a-form-model-item ref="password" :label="$t('config.pipline.create.password.label')" prop="password">
                <a-input type="password" v-model="formData.password" :placeholder="$t('config.pipline.create.password')" @blur="() => {
                    $refs.password.onFieldBlur();
                }" />
            </a-form-model-item>
        </a-form-model>

    </a-modal>
</template>

<script>
import HAlert from '@/components/alert/HAlert.vue'
import XEUtils from 'xe-utils'
import { testPipelineRepository } from '@/services/pipeline/PipelineService'
export default {
    name: 'CreateJenkinsDialog',
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
                serverUrl: '',
                userName: '',
                password: '',
            },
            rules: {
                serverUrl: [
                    { required: true, type: 'url', message: "Jenkins URL", trigger: "blur" },
                ],
                userName: [
                    { required: true, message: this.$t('config.pipline.create.userName'), trigger: "blur" },
                ],
                password: [
                    { required: true, message: this.$t('config.pipline.create.password'), trigger: "blur" },
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
            if (this.$refs.jenkinsForm) {
                this.$refs.jenkinsForm.resetFields()
            }
            this.formData = XEUtils.clone(this.repository)
        },
        onOK() {
            this.$refs["jenkinsForm"].validate((valid) => {
                if (valid) {
                    let repo = XEUtils.clone(this.repository)
                    repo.serverUrl = this.formData.serverUrl
                    repo.userName = this.formData.userName
                    repo.password = this.formData.password
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
        onTest() {
            let repo = XEUtils.clone(this.repository)
            repo.serverUrl = this.formData.serverUrl
            repo.userName = this.formData.userName
            repo.password = this.formData.password
            testPipelineRepository(repo).then(resp => {
                const success = resp
                if (success) {
                    this.$message.success(this.$t('connection_successful'))
                } else {
                    this.$message.error(this.$t('connection_failed'))
                }

            })
        }
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