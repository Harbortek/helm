<template>
    <a-modal v-model="visiable" title="保存导入配置" @ok="onOK" @cancel="onCancel" centered :width="400">
        <a-form-model ref="xForm" :model="formData" :layout="'horizontal'" :labelCol="{ span: 7 }"
            :wrapperCol="{ span: 16, offset: 1 }">
            <a-form-model-item label="配置" prop="id">
                <a-select v-model="formData.id" style="width: 100%;">
                    <a-select-option :value="item.id" :key="item.id" v-for="item in templates">{{ item.name
                        }}</a-select-option>
                </a-select>
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>

<script>
import { findReqIFExportTemplates, createReqIFExportTemplate, updateReqIFExportTemplate } from '@/services/smart-doc/ReqIFExportService'
import {cloneDeep} from 'lodash'
import XEUtils from 'xe-utils'
export default {
    name: 'LoadTemplateDialog',
    props: ['showDialog', 'projectId'],
    data() {
        return {
            visiable: false,
            formData: {
            },
            templates: [],
        }
    },
    watch: {
        showDialog: {
            handler(newVal) {
                this.visiable = newVal
                if (newVal) {
                    findReqIFExportTemplates(this.projectId).then(resp => {
                        this.templates = resp
                        if (this.templates.length > 0) {
                            this.formData.id = this.templates[0].id
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
            const tempalte = XEUtils.find(this.templates, item => item.id === id)
            this.$emit('ok', tempalte)
        },
        onCancel() {
            this.$emit('cancel')
        }
    },
}
</script>

<style></style>