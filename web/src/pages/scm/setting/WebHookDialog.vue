<template>
    <a-modal v-model="visiable" :title="$t('config.codeRepository.message.manual')" :footer="null" @ok="onOK" @cancel="onCancel" centered :width="700"
        dialogClass="gitlab-dialog">

        <h-alert :description="$t('config.codeRepository.message.manual.description')" style="margin-bottom:10px;" />


        <div class="description-container">
            <span>{{ $t('config.codeRepository.message.manual.title') }}</span>
        </div>
        <div class="subtitle">
            {{ $t('config.codeRepository.message.manual.url.label') }}
        </div>
        <div class="copy-action-container">
            <a-input :value="repository.webHookUrl" class="copy-text" :disabled="true" /> <a-button
                v-clipboard:copy="repository.webHookUrl" v-clipboard:success="onCopySuccess"
                v-clipboard:error="onCopyError">{{$t('copy')}}</a-button>
        </div>
        <div class="subtitle">
            {{$t('config.codeRepository.message.manual.key.label')}}
        </div>
        <div class="copy-action-container">
            <a-input :value="repository.webHookToken" class="copy-text" :disabled="true" /> <a-button
                v-clipboard:copy="repository.webHookToken" v-clipboard:success="onCopySuccess"
                v-clipboard:error="onCopyError">{{$t('copy')}}</a-button>
        </div>

    </a-modal>
</template>

<script>
import HAlert from '@/components/alert/HAlert.vue'
import XEUtils from 'xe-utils'
export default {
    name: 'WebHookDialog',
    components: { HAlert },
    props: {
        isShowDialog: {
            required: true
        },
        repository: {
            required: true
        }
    },
    data() {
        return {

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
        },
        onCancel() {
            this.visiable = false
            this.$emit('cancel')
        },
        onOK() {
            this.visiable = false
            this.$emit('ok')
        },
        onCopySuccess() {
            this.$message.success(this.$t('config.codeRepository.message.manual.copy.success'));
        },
        onCopyError() {
            this.$message.error(this.$t('config.codeRepository.message.manual.copy.fail'));
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
    width: 650px;
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