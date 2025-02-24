<template>
    <div style="width: 100%">
        <a-form-model ref="backgroundFormBar" :model="commonBackground" label-width="80px" :layout="'horizontal'"
            :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">
            <a-form-model-item :label="$t('panel.inner_padding')" class="form-item form-item-slider">

                <a-slider v-model="commonBackground.innerPadding" show-input :show-input-controls="false" input-size="mini"
                    :max="50" @change="themeChange('innerPadding')" />
            </a-form-model-item>
            <a-form-model-item :label="$t('panel.board_radio')" class="form-item form-item-slider">

                <a-slider v-model="commonBackground.borderRadius" show-input :show-input-controls="false" input-size="mini"
                    @change="themeChange('borderRadius')" />
            </a-form-model-item>

            <a-form-model-item label="颜色" class="form-item" :colon="false">
                <a-row>
                    <a-col :span="6">
                        <a-checkbox v-model="commonBackground.backgroundColorSelect"
                            @change="themeChange('backgroundColorSelect')">使用</a-checkbox>
                    </a-col>
                    <a-col :span="4">
                        <vue-color-picker v-model="commonBackground.color" class="color-picker-style"
                            v-show="commonBackground.backgroundColorSelect" :predefine="predefineColors"
                            :disabled="!commonBackground.backgroundColorSelect" @change="themeChange('color')" />
                    </a-col>
                    <a-col :span="14">
                        <a-slider v-model="commonBackground.alpha" show-input :show-input-controls="false" input-size="mini"
                            v-show="commonBackground.backgroundColorSelect"
                            :disabled="!commonBackground.backgroundColorSelect" @change="themeChange('alpha')" />
                    </a-col>
                </a-row>

            </a-form-model-item>

            <a-form-model-item label="背景填充" class="form-item" :colon="false">
                <a-checkbox v-model="commonBackground.backgroundImageSelect"
                    @change="themeChange('backgroundImageSelect')">使用</a-checkbox>
                <a-radio-group v-model="commonBackground.backgroundType" size="small"
                    v-show="commonBackground.backgroundImageSelect" :dsiable="!commonBackground.backgroundImageSelect"
                    @change="themeChange('backgroundType')">
                    <!-- <a-radio-button value="color">颜色</a-radio-button> -->
                    <a-radio-button value="outerImage">图片</a-radio-button>
                    <a-radio-button value="innerImage">边框</a-radio-button>
                </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="图片" class="form-item"
                v-show="commonBackground.backgroundImageSelect && commonBackground.backgroundType === 'outerImage'">
                <a-upload name="file" :multiple="false" :showUploadList="false" :directory="false"
                    :disabled="commonBackground.backgroundType !== 'outerImage'" :customRequest="onUpload">
                    <img :src="commonBackground.outerImage" width="16" height="16" />
                </a-upload>
            </a-form-model-item>

            <a-form-model-item label="边框" class="form-item"
                v-show="commonBackground.backgroundImageSelect && commonBackground.backgroundType === 'innerImage'">
                <vue-color-picker v-model="commonBackground.innerImageColor" class="color-picker-style"
                    :predefine="predefineColors" @change="themeChange('innerImageColor')" />
            </a-form-model-item>
            <a-row v-show="commonBackground.backgroundImageSelect && commonBackground.backgroundType === 'innerImage'">
                <a-col class="main-row" style="margin-left:100px;">
                    <a-row>
                        <a-col v-for="item in borderImages" :key="item.id" :span="12">
                            <background-item-overall :common-background="commonBackground" :template="item"
                                @borderChange="themeChange('innerImage')" />
                        </a-col>
                    </a-row>
                </a-col>
            </a-row>

        </a-form-model>
    </div>
</template>

<script>
import BackgroundItemOverall from './BackgroundItemOverall'
import { imgUrlTrans } from '@/pages/smart-page/util/canvasUtils'
import { COLOR_PANEL } from '@/pages/smart-page/util/chart'
import { uploadFile, downloadFile } from '@/services/global/FileService'


export default {
    name: 'BackgroundOverall',
    // eslint-disable-next-line
    components: { BackgroundItemOverall },
    props: {
        position: {
            type: String,
            required: false,
            default: 'component'
        },
        pageStyle: {
            required: true,
        }
    },
    data() {
        return {
            commonBackground: null,
            borderImages: [
                {
                    "id": "blue_1",
                    "name": "边框1",
                    "classification": "商务",
                    "remark": null,
                    "sort": null,
                    "uploadTime": null,
                    "baseUrl": "img/board",
                    "url": "blue_1.svg",
                    "content": ""
                },
                {
                    "id": "blue_2",
                    "name": "边框2",
                    "classification": "商务",
                    "remark": null,
                    "sort": null,
                    "uploadTime": null,
                    "baseUrl": "img/board",
                    "url": "blue_2.svg",
                    "content": null
                },
                {
                    "id": "blue_3",
                    "name": "边框3",
                    "classification": "商务",
                    "remark": null,
                    "sort": null,
                    "uploadTime": null,
                    "baseUrl": "img/board",
                    "url": "blue_3.svg",
                    "content": null
                },
                {
                    "id": "blue_4",
                    "name": "边框4",
                    "classification": "商务",
                    "remark": null,
                    "sort": null,
                    "uploadTime": null,
                    "baseUrl": "img/board",
                    "url": "blue_4.svg",
                    "content": null
                },
                {
                    "id": "blue_5",
                    "name": "边框5",
                    "classification": "商务",
                    "remark": null,
                    "sort": null,
                    "uploadTime": null,
                    "baseUrl": "img/board",
                    "url": "blue_5.svg",
                    "content": null
                },
                {
                    "id": "blue_6",
                    "name": "边框6",
                    "classification": "商务",
                    "remark": null,
                    "sort": null,
                    "uploadTime": null,
                    "baseUrl": "img/board",
                    "url": "blue_6.svg",
                    "content": null
                },
                {
                    "id": "blue_7",
                    "name": "边框7",
                    "classification": "商务",
                    "remark": null,
                    "sort": null,
                    "uploadTime": null,
                    "baseUrl": "img/board",
                    "url": "blue_7.svg",
                    "content": null
                },
                {
                    "id": "blue_8",
                    "name": "边框8",
                    "classification": "商务",
                    "remark": null,
                    "sort": null,
                    "uploadTime": null,
                    "baseUrl": "img/board",
                    "url": "blue_8.svg",
                    "content": null
                },
                {
                    "id": "blue_9",
                    "name": "边框9",
                    "classification": "商务",
                    "remark": null,
                    "sort": null,
                    "uploadTime": null,
                    "baseUrl": "img/board",
                    "url": "blue_9.svg",
                    "content": null
                },
                {
                    "id": "dark_1",
                    "name": "边框10",
                    "classification": "商务",
                    "remark": null,
                    "sort": null,
                    "uploadTime": null,
                    "baseUrl": "img/board",
                    "url": "dark_1.svg",
                    "content": null
                }
            ],
            checked: false,
            backgroundOrigin: {},
            fileList: [],
            dialogImageUrl: '',
            dialogVisible: false,
            uploadDisabled: false,
            panel: null,
            predefineColors: COLOR_PANEL
        }
    },
    computed: {
        // customStyle() {
        //     let style = {}
        //     if (this.pageStyle.openCommonStyle) {
        //         if (this.pageStyle.panel.backgroundType === 'image' && this.pageStyle.panel.imageUrl) {
        //             style = {
        //                 background: `url(${imgUrlTrans(this.pageStyle.panel.imageUrl)}) no-repeat`,
        //                 ...style
        //             }
        //         } else if (this.pageStyle.panel.backgroundType === 'color') {
        //             style = {
        //                 background: this.pageStyle.panel.color,
        //                 ...style
        //             }
        //         }
        //     }
        //     if (!style.background) {
        //         style.background = '#FFFFFF'
        //     }
        //     return style
        // },
    },
    created() {
        this.init()
        // bus.$on('onThemeColorChange', this.init)
    },
    beforeDestroy() {
        // bus.$off('onThemeColorChange', this.init)
    },
    methods: {
        init() {
            console.log(this.pageStyle)
            this.commonBackground = this.pageStyle.chartInfo.chartCommonStyle
            // if (this.commonBackground && this.commonBackground.outerImage && typeof (this.commonBackground.outerImage) === 'string') {
            //     this.fileList.push({ url: imgUrlTrans(this.commonBackground.outerImage) })
            // }
        },
        // onChangeType() {
        // },
        // handleRemove(file, fileList) {
        //     this.uploadDisabled = false
        //     this.commonBackground.outerImage = null
        //     this.themeChange('outerImage')
        //     this.fileList = []
        //     this.commitStyle()
        // },
        // handlePictureCardPreview(file) {
        //     this.dialogImageUrl = file.url
        //     this.dialogVisible = true
        // },
        onUpload(e) {
            uploadFile(e.file).then((resp) => {
                e.onSuccess(resp, e.file)

                let attachment = { name: resp.origin_name, filePath: resp.url, fileSize: resp.fileSize }
                console.log(attachment)
                this.commonBackground.outerImage = process.env.VUE_APP_API_BASE_URL + '/file?path=' + attachment.filePath
                this.themeChange()
            })
        },
        themeChange() {
            this.$emit('change', this.commonBackground)
            // this.componentData.forEach((item, index) => {
            //     if (item.type === 'view') {
            //         item.commonBackground[modifyName] = this.commonBackground[modifyName]
            //     }
            // })
        }
    }
}
</script>

<style scoped>
.color-picker-style {
    cursor: pointer;
    z-index: 1003;
    border: solid 1px gray;
    line-height: 40px;
    top: 5px;
    width: 18px;
}
</style>
