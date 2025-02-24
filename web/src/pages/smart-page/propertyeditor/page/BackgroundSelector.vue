<template>
    <div style="width: 100%">
        <a-form-model ref="backgroundFormBar" :model="backgroundForm" label-width="80px" :layout="'horizontal'"
            :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">
            <!-- <a-form-model-item label="主题色" class="form-item form-item-slider">
                <a-radio-group v-model="backgroundForm.themeColor" size="small" @change="changeBackground">
                    <a-radio-button value="light">浅色</a-radio-button>
                    <a-radio-button value="dark">深色</a-radio-button>
                </a-radio-group>
            </a-form-model-item> -->
            <a-form-model-item label="背景" class="form-item form-item-slider">
                <a-radio-group v-model="backgroundForm.backgroundType" size="small" @change="changeBackground">
                    <a-radio-button value="color">颜色</a-radio-button>
                    <a-radio-button value="image">图片</a-radio-button>
                </a-radio-group>
            </a-form-model-item>
            <a-form-model-item :label="$t('chart.color')" class="form-item">
                <vue-color-picker v-model="backgroundForm.color" class="color-picker-style" :predefine="predefineColors"
                    :disabled="backgroundForm.backgroundType !== 'color'" @change="changeBackground" />
            </a-form-model-item>
            <a-form-model-item label="图片" class="form-item form-item-slider">
                <a-upload name="file" :multiple="false" :showUploadList="false" :directory="false"
                    :disabled="backgroundForm.backgroundType !== 'image'" :customRequest="onUpload">
                    <img :src="backgroundForm.imageUrl" width="16" height="16" />
                </a-upload>
            </a-form-model-item>
            <a-form-model-item :label="$t('chart.not_alpha')" class="form-item form-item-slider">
                <a-slider v-model="backgroundForm.alpha" show-input :show-input-controls="false" input-size="mini"
                    @change="changeBackground" />
            </a-form-model-item>

        </a-form-model>
    </div>
</template>

<script>
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import { uploadFile, downloadFile } from '@/services/global/FileService'
import { COLOR_PANEL, DEFAULT_BACKGROUND_COLOR } from '@/pages/smart-page/util/chart'
import { DEFAULT_COMMON_CANVAS_STYLE_STRING } from '@/pages/smart-page/util/panel'
export default {
    name: 'BackgroundSelector',
    props: {
        pageStyle: {
            required: true,
        }
    },
    data() {
        return {
            backgroundForm: deepCopy(DEFAULT_COMMON_CANVAS_STYLE_STRING).background,
            predefineColors: COLOR_PANEL
        }
    },
    watch: {
        pageStyle: {
            handler(val) {
                this.backgroundForm = deepCopy(val.background)
            },
            immediate: true,
            deep: true,
        }
    },
    methods: {
        changeBackground() {
            this.$emit('change', this.backgroundForm)
        },
        onUpload(e) {
            uploadFile(e.file).then((resp) => {
                e.onSuccess(resp, e.file)

                let attachment = { name: resp.origin_name, filePath: resp.url, fileSize: resp.fileSize }
                console.log(attachment)
                this.backgroundForm.imageUrl = process.env.VUE_APP_API_BASE_URL + '/file?path=' + attachment.filePath
                this.changeBackground()
            })
        },
    },
}
</script>

<style lang="less" scoped>
.color-picker-style {
    cursor: pointer;
    z-index: 1003;
    border: solid 1px gray;
    line-height: 40px;
    top: 5px;
}
</style>