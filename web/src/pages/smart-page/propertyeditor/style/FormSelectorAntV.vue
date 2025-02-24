<template>
    <div style="width: 100%">
        <a-col style="flex: 1 1;">
            <a-form-model ref="titleForm" :model="titleForm" label-width="80px" :layout="'horizontal'"
                :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">

                <a-form-model-item v-show="showProperty('layout')" :label="$t('chart.form_layout')" class="form-item">
                    <a-radio-group v-model="titleForm.layout" size="small" @change="changeFormStyle('formLayout')">
                        <a-radio-button value="horizontal">{{ $t('chart.form_layout_horizontal') }}</a-radio-button>
                        <a-radio-button value="vertical">{{ $t('chart.form_layout_vertical') }}</a-radio-button>
                        <a-radio-button value="inline">{{ $t('chart.form_layout_inline') }}</a-radio-button>
                    </a-radio-group>
                </a-form-model-item>
                <a-form-model-item v-show="showProperty('columnCount')" :label="$t('chart.form_columnCount')"
                    class="form-item">
                    <a-input v-model="titleForm.columnCount" type="number" class="hide-icon-number" :min="1" :max="24"
                        @change="changeFormStyle('columnCount')">
                    </a-input>
                </a-form-model-item>
                <a-form-model-item v-show="showProperty('labelCol')" :label="$t('chart.form_labelCol')" class="form-item">
                    <a-input v-model="titleForm.labelCol" type="number" class="hide-icon-number" :min="1" :max="24"
                        @change="changeFormStyle('labelCol')">
                    </a-input>
                </a-form-model-item>
                <a-form-model-item v-show="showProperty('wrapperCol')" :label="$t('chart.form_wrapperCol')"
                    class="form-item">
                    <a-input v-model="titleForm.wrapperCol" type="number" :min="1" :max="24" class="hide-icon-number"
                        @change="changeFormStyle('wrapperCol')">
                    </a-input>
                </a-form-model-item>


                <!-- <a-form-model-item v-show="showProperty('fontFamily')" :label="$t('chart.font_family')" class="form-item">
                    <a-select v-model="titleForm.fontFamily" :placeholder="$t('chart.font_family')"
                        dropdownClassName="props-dropdown" @change="changeFormStyle('fontFamily')">
                        <a-select-option v-for="option in fontFamily" :key="option.value" :title="option.name"
                            :value="option.value">{{ option.name }}</a-select-option>
                    </a-select>
                </a-form-model-item>
                <a-form-model-item v-show="showProperty('fontSize')" :label="$t('chart.text_fontsize')" class="form-item">
                    <a-select v-model="titleForm.fontSize" :placeholder="$t('chart.text_fontsize')" size="small"
                        dropdownClassName="props-dropdown" @change="changeFormStyle('fontSize')">
                        <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name"
                            :value="option.value">{{ option.name }}</a-select-option>
                    </a-select>
                </a-form-model-item>
                <a-form-model-item v-show="showProperty('color')" :label="$t('chart.text_color')" class="form-item">
                    <vue-color-picker v-model="titleForm.color" class="color-picker-style" :predefine="predefineColors"
                        @change="changeFormStyle('color')" />
                </a-form-model-item>
                <a-form-model-item v-show="showProperty('hPosition')" :label="$t('chart.text_h_position')"
                    class="form-item">
                    <a-radio-group v-model="titleForm.hPosition" size="small" @change="changeFormStyle('hPosition')">
                        <a-radio-button value="left">{{ $t('chart.text_pos_left') }}</a-radio-button>
                        <a-radio-button value="center">{{ $t('chart.text_pos_center') }}</a-radio-button>
                        <a-radio-button value="right">{{ $t('chart.text_pos_right') }}</a-radio-button>
                    </a-radio-group>
                </a-form-model-item>

                <a-form-model-item v-show="showProperty('letterSpace')" :label="$t('chart.letter_space')" class="form-item">
                    <a-select v-model="titleForm.letterSpace" :placeholder="$t('chart.quota_letter_space')"
                        dropdownClassName="props-dropdown" @change="changeFormStyle('letterSpace')">
                        <a-select-option v-for="option in fontLetterSpace" :key="option.value" :title="option.name"
                            :value="option.value">{{ option.name }}</a-select-option>
                    </a-select>
                </a-form-model-item>
                <a-form-model-item v-show="showProperty('fontShadow')" :label="$t('chart.font_shadow')" class="form-item">
                    <a-checkbox v-model="titleForm.fontShadow" @change="changeFormStyle('fontShadow')">{{
                        $t('chart.font_shadow') }}
                    </a-checkbox>
                </a-form-model-item> -->


            </a-form-model>
        </a-col>


    </div>
</template>

<script>
import { CHART_FONT_FAMILY, CHART_FONT_LETTER_SPACE, COLOR_PANEL, DEFAULT_FORM_STYLE } from '@/pages/smart-page/util/chart'
import { checkViewTitle } from '@/pages/smart-page/util/canvasUtils'
import { mapState } from 'vuex'

export default {
    name: 'FormSelectorAntV',
    props: {
        param: {
            type: Object,
            required: true
        },
        chart: {
            type: Object,
            required: true
        },
        propertyInner: {
            type: Array,
            required: false,
            default: function () {
                return []
            }
        }
    },
    data() {
        return {
            titleForm: JSON.parse(JSON.stringify(DEFAULT_FORM_STYLE)),
            fontSize: [],
            isSetting: false,
            predefineColors: COLOR_PANEL,
            showEditRemark: false,
            tmpRemark: '',
            fontFamily: CHART_FONT_FAMILY,
            fontLetterSpace: CHART_FONT_LETTER_SPACE
        }
    },
    computed: {
        ...mapState([
            'batchOptStatus'
        ]),
        title() {
            return this.chart.name
        }
    },
    watch: {
        'chart': {
            handler: function () {
                this.initData()
            }
        },
        title(val) {
            this.titleForm = { ...this.titleForm, title: val }
        }
    },
    mounted() {
        this.init()
        this.initData()
    },
    methods: {
        initData() {
            const chart = JSON.parse(JSON.stringify(this.chart))
            if (chart.customStyle) {
                let customStyle = null
                if (Object.prototype.toString.call(chart.customStyle) === '[object Object]') {
                    customStyle = JSON.parse(JSON.stringify(chart.customStyle))
                } else {
                    customStyle = JSON.parse(chart.customStyle)
                }
                if (customStyle.form) {
                    this.titleForm = customStyle.form
                    this.titleForm.layout = this.titleForm.layout ? this.titleForm.layout : DEFAULT_FORM_STYLE.layout
                    this.titleForm.labelCol = this.titleForm.labelCol ? this.titleForm.labelCol : DEFAULT_FORM_STYLE.labelCol
                    this.titleForm.wrapperCol = this.titleForm.wrapperCol ? this.titleForm.wrapperCol : DEFAULT_FORM_STYLE.wrapperCol
                    this.titleForm.fontFamily = this.titleForm.fontFamily ? this.titleForm.fontFamily : DEFAULT_FORM_STYLE.fontFamily
                    this.titleForm.letterSpace = this.titleForm.letterSpace ? this.titleForm.letterSpace : DEFAULT_FORM_STYLE.letterSpace
                    this.titleForm.fontShadow = this.titleForm.fontShadow ? this.titleForm.fontShadow : DEFAULT_FORM_STYLE.fontShadow
                }
            }
        },
        init() {
            const arr = []
            for (let i = 10; i <= 60; i = i + 2) {
                arr.push({
                    name: i + '',
                    value: i + ''
                })
            }
            this.fontSize = arr
        },
        changeFormStyle(modifyName) {
            //this.titleForm['modifyName'] = modifyName
            this.$emit('onFormChange', this.titleForm)
        },
        inputOnInput: function (e) {
            this.$forceUpdate()
        },
        showProperty(property) {
            return this.propertyInner.includes(property)
        },
    }
}
</script>

<style scoped>
.shape-item {
    padding: 6px;
    border: none;
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.form-item-slider ::v-deep .a-form-model-item__label {
    font-size: 12px;
    line-height: 38px;
}

.form-item ::v-deep .a-form-model-item__label {
    font-size: 12px;
}

.a-select-dropdown__item {
    padding: 0 20px;
}

span {
    font-size: 12px
}

.a-form-model-item {
    margin-bottom: 6px;
}

.switch-style {
    position: absolute;
    right: 10px;
    margin-top: -4px;
}

.color-picker-style {
    cursor: pointer;
    z-index: 1003;
}

.dialog-css ::v-deep .a-modal__title {
    font-size: 14px;
}

.dialog-css ::v-deep .a-modal__header {
    padding: 20px 20px 0;
}

.dialog-css ::v-deep .a-modal__body {
    padding: 10px 20px 20px;
}
</style>
