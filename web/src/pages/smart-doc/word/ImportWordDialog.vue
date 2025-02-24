<template>
    <a-modal v-model="visiable" title="导入WORD" width="500px" @ok="onOK" @cancel="onCancel" centered>
        <a-form-model ref="importForm" :layout="'horizontal'" :labelCol="{ span: 7 }"
            :wrapperCol="{ span: 16, offset: 1 }" :model="formData">
            <a-form-model-item label="导入Word文件" prop="importFile">
                <a-upload name="file" :multiple="false" :showUploadList="true" :directory="false"
                    :customRequest="onUpload">
                    <vxe-button type="text" icon="vxe-icon-upload">选择文件</vxe-button>
                </a-upload>
            </a-form-model-item>
        </a-form-model>
    </a-modal>
</template>

<script>
import { uploadFile, downloadFile } from '@/services/global/FileService'

export default {
    name: 'ImportWordDialog',
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
                    this.formData = {}
                    this.attachment = undefined
                }
            }
        }
    },
    data() {
        return {
            formData: {},
            attachment: undefined,
        }
    },
    methods: {
        onUpload(e) {
            uploadFile(e.file).then((resp) => {
                e.onSuccess(resp, e.file)
                this.attachment = { name: resp.origin_name, filePath: resp.url, fileSize: resp.fileSize }
            })
        },
        onOK() {
            if (!this.attachment) {
                this.$message.warning('请选择要导入的文件')
            } else {
                this.$emit('ok', this.attachment)
            }
        },
        onCancel() {
            this.$emit('cancel')
        }
    },
}
</script>

<style lang="less" scoped></style>