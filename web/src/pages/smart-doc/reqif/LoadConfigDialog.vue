<template>
    <a-modal v-model="visiable" title="保存导入配置" @ok="onOK" @cancel="onCancel" centered :width="400">
        <a-form-model ref="xForm" :model="formData" :layout="'horizontal'" :labelCol="{ span: 7 }"
            :wrapperCol="{ span: 16, offset: 1 }">
            <a-form-model-item label="配置" prop="id">
                <a-select v-model="formData.id" style="width: 100%;">
                    <a-select-option :value="item.id" :key="item.id" v-for="item in configs">{{ item.name
                        }}</a-select-option>
                </a-select>
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>

<script>
import { findReqIFImportConfigs, createReqIFImportConfig, updateReqIFImportConfig } from '@/services/smart-doc/ReqIFImportService'
import {cloneDeep} from 'lodash'
import XEUtils from 'xe-utils'
export default {
    name: 'LoadConfigDialog',
    props: ['showDialog', 'projectId'],
    data() {
        return {
            visiable: false,
            formData: {
            },
            configs: [],
        }
    },
    watch: {
        showDialog: {
            handler(newVal) {
                this.visiable = newVal
                if (newVal) {
                    findReqIFImportConfigs(this.projectId).then(resp => {
                        this.configs = resp
                        if (this.configs.length > 0) {
                            this.formData.id = this.configs[0].id
                        }
                    })
                }
            },
        }
    },
    mounted() {

    },
    methods: {
        onOK() {
            const id = this.formData.id
            const config = XEUtils.find(this.configs, item => item.id === id)
            this.$emit('ok', config)
        },
        onCancel() {
            this.$emit('cancel')
        }
    },
}
</script>

<style></style>