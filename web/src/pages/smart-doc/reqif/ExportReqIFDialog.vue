<template>
    <a-modal v-model="visiable" title="导入ReqIF文件" width="500px" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="exportForm" :layout="'horizontal'" :labelCol="{ span: 6 }"
            :wrapperCol="{ span: 16, offset: 1 }" :model="formData">
            <a-radio-group v-model="formData.type" style="width:100%;" @change="onTypeChange">
                <a-form-model-item label="" :style="radioStyle">
                    <a-radio :value="1">
                        更新ReqIF文件至已有文件
                    </a-radio>
                </a-form-model-item>
                <a-form-model-item label="已有ReqIF文件" v-if="formData.type === 1" required>
                    <a-upload name="file" :multiple="false" :showUploadList="true" :directory="false"
                        :customRequest="onUpload">
                        <vxe-button type="text" icon="vxe-icon-upload">选择reqifz文件</vxe-button>
                    </a-upload>
                </a-form-model-item>
                <a-form-model-item label="文件名" v-if="formData.type === 1" required>
                    <a-select v-model="formData.name" placeholder="请选择文件名" style="width:100%;">
                        <a-select-option :value="item" v-for="(item, index) in fileList" :key="index">
                            {{ item }}
                        </a-select-option>
                    </a-select>
                </a-form-model-item>
                <a-form-model-item label="">
                    <a-radio :value="2" :style="radioStyle">
                        添加ReqIF文件至已有文件
                    </a-radio>
                </a-form-model-item>
                <a-form-model-item label="已有ReqIF文件" v-if="formData.type === 2" required>
                    <a-upload name="file" :multiple="false" :showUploadList="true" :directory="false"
                        :customRequest="onUpload">
                        <vxe-button type="text" icon="vxe-icon-upload">选择reqifz文件</vxe-button>
                    </a-upload>
                </a-form-model-item>
                <a-form-model-item label="文件名" v-if="formData.type === 2" required>
                    <a-input v-model="formData.name" placeholder="请输入文件名" />
                </a-form-model-item>
                <a-form-model-item label="">
                    <a-radio :value="3" :style="radioStyle">
                        创建新的ReqIF文件
                    </a-radio>
                </a-form-model-item>
                <a-form-model-item label="文件名" v-if="formData.type === 3" required>
                    <a-input v-model="formData.name" placeholder="请输入文件名" addon-after=".reqifz" />
                </a-form-model-item>
            </a-radio-group>

        </a-form-model>
    </a-modal>
</template>

<script>
import { uploadFile, downloadFile } from '@/services/global/FileService'
import { findReqIFFiles, saveExportJob } from '@/services/smart-doc/ReqIFExportService'
export default {
    name: 'ExportReqIFDialog',
    props: {
        isShowDialog: {
            required: true
        },
        pageId: {
            type: String,
            require: true
        },
        projectId: {
            type: String,
            require: false
        },
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
            handler(newValue, oldValue) {
                if (newValue) {
                    this.formData = {
                        type: 1,
                    }
                }
            }
        }
    },
    data() {
        return {
            formData: {
                type: 1,
            },
            radioStyle: {
                display: 'block',
                height: '30px',
                lineHeight: '30px',
            },
            attachment: undefined,
            fileList: [],
        }
    },
    methods: {
        onUpload(e) {
            uploadFile(e.file).then((resp) => {
                e.onSuccess(resp, e.file)
                this.attachment = { name: resp.origin_name, filePath: resp.url, fileSize: resp.fileSize }
                findReqIFFiles(this.pageId, resp.url).then((resp) => {
                    this.fileList = resp || []
                })
            })
        },
        onTypeChange() {
            this.formData.name = ''
            this.attachment = undefined
            this.fileList = []
        },
        onOK() {
            this.$refs.exportForm.validate((valid) => {
                if (valid) {
                    if ((this.formData.type === 1 || this.formData.type === 2) && !this.attachment) {
                        this.$message.error('请选择文件')
                        return
                    }
                    if ((this.formData.type === 2 || this.formData.type === 3) && !this.formData.name) {
                        this.$message.error('请输入文件名')
                        return
                    }
                    let job = {
                        exportType: this.formData.type,
                    }
                    if (this.formData.type === 1 || this.formData.type === 2) {
                        job.reqIFZipFilePath = this.attachment.filePath
                        job.reqIFZipFileName = this.attachment.name
                    }
                    if (this.formData.type === 1) {
                        job.reqIFFileName = this.formData.name + '.reqif'
                    } else if (this.formData.type === 2) {
                        job.reqIFFileName = this.formData.name + '.reqif'
                    } else if (this.formData.type === 3) {
                        job.reqIFFileName = this.formData.name + '.reqif'
                        job.reqIFZipFileName = this.formData.name + '.reqifz'
                    }
                    saveExportJob(this.pageId, job).then(resp => {
                        this.$emit('ok', this.formData)
                    })
                }
            })
        },
        onCancel() {
            this.$emit('cancel')
        }
    }
}
</script>

<style></style>