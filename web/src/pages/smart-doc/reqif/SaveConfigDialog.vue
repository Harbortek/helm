<template>
    <a-modal v-model="visiable" title="保存导入配置" @ok="onOK" @cancel="onCancel" centered :width="600">
        <a-form-model ref="xForm" :model="formData" :rules="rules" :layout="'horizontal'" :labelCol="{ span: 7 }"
            :wrapperCol="{ span: 16, offset: 1 }">
            <a-form-model-item label="模式" prop="mode">
                <a-radio-group v-model="formData.mode">
                    <a-radio-button :value="'NEW'">
                        创建新配置
                    </a-radio-button>
                    <a-radio-button :value="'EXIST'">
                        覆盖原有配置
                    </a-radio-button>
                </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="范围" prop="scope" v-if="formData.mode === 'NEW'">
                <a-select v-model="formData.scope" style="width: 300px;">
                    <a-select-option value="GLOBAL">全局</a-select-option>
                    <a-select-option value="PROJECT">项目</a-select-option>
                </a-select>
            </a-form-model-item>
            <a-form-model-item label="名称" prop="name" v-if="formData.mode === 'NEW'">
                <a-input v-model="formData.name" style="width:300px;" />
            </a-form-model-item>

            <a-form-model-item label="配置名称" prop="id" v-if="formData.mode === 'EXIST'">
                <a-select v-model="formData.id" style="width: 300px;">
                    <a-select-option :value="item.id" :key="item.id" v-for="item in configs">{{ item.name
                        }}</a-select-option>
                </a-select>
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>

<script>
import {cloneDeep} from 'lodash'
import { findReqIFImportConfigs, createReqIFImportConfig, updateReqIFImportConfig } from '@/services/smart-doc/ReqIFImportService'
import XEUtils from 'xe-utils'
export default {
    name: 'SaveConfigDialog',
    props: ['config', 'showDialog', 'projectId'],
    data() {
        return {
            visiable: false,
            formData: {
                mode: 'NEW',
                scope: 'GLOBAL'
            },
            rules: {
                name:
                    [{ required: true, message: '请输入名称', trigger: 'blur' }],
            },
            configs: [],
        }
    },
    watch: {
        config: {
            handler(newVal) {
                if (newVal) {
                    this.formData = cloneDeep(newVal)
                    if (this.formData.id) {
                        this.formData.mode = 'EXIST'

                    } else {
                        this.formData.mode = 'NEW'
                        this.formData.scope = 'GLOBAL'
                    }
                    this.formData = { ...this.formData }
                    console.log(this.formData)
                }
            },
        },
        showDialog: {
            handler(newVal) {
                this.visiable = newVal
                if (newVal) {
                    findReqIFImportConfigs(this.projectId).then(resp => {
                        this.configs = resp
                    })
                }
            },
        },
    },
    mounted() {

    },
    methods: {
        onOK() {
            this.$refs.xForm.validate(valid => {
                if (valid) {
                    if (this.formData.mode === 'NEW') {
                        if (this.formData.scope === 'GLOBAL') {
                            delete this.formData.projectId
                        } else {
                            this.formData.projectId = this.projectId
                        }
                        createReqIFImportConfig(this.formData).then(resp => {
                            this.$emit('ok', resp)
                            this.$message.success('保存成功')
                        })
                    } else {
                        let oldConfig = this.configs.filter(item => item.id === this.formData.id)[0]
                        if (oldConfig) {
                            oldConfig.rules = this.formData.rules
                        }
                        updateReqIFImportConfig(oldConfig).then(resp => {
                            this.$emit('ok', resp)
                            this.$message.success('保存成功')
                        })
                    }
                }
            })
        },
        onCancel() {
            this.$emit('cancel')
        }
    },
}
</script>

<style></style>