<template>
    <div>
        <div v-show="title_show" ref="title" :style="title_class">
            <div v-if="!titleEdit">
                <span @click="onEditTitle">{{ component.title }}
                </span>
                <a-tooltip>
                    <template slot="title">
                        {{ remarkCfg.content }}
                    </template>
                    <a-icon type="question-circle" v-if="remarkCfg.show" />
                </a-tooltip>
            </div>
            <div v-else><a-input ref="titleInput" v-model="title" :maxLength="50" style="width: 80%;"
                    @blur="onTitleChange" @pressEnter="onTitleChange" /></div>
        </div>
    </div>
</template>

<script>
import { getRemark, hexColorToRGBA } from '@/pages/smart-page/util/util'
import { DEFAULT_TITLE_STYLE } from '@/pages/smart-page/util/chart'

export default {
    name: 'TitleComponent',
    props: {
        component: {
            type: Object,
            required: true
        },
        readOnly: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            title_class: {
                margin: '5px 15px',
                width: '100%',
                fontSize: '18px',
                color: '#303133',
                textAlign: 'left',
                fontStyle: 'normal',
                fontWeight: 'normal',
                background: '',
                cursor: 'default',
            },
            title_show: false,
            titleEdit: false,
            title: '',
            remarkCfg: {},
        }
    },
    watch: {
        component: {
            handler(v) {
                this.initTitle()
            },
            immediate: true
        },
    },
    methods: {
        initTitle() {
            if (this.component.customStyle) {
                const customStyle = JSON.parse(this.component.customStyle)
                if (customStyle.text) {
                    this.title_show = customStyle.text.show
                    this.title_class.fontSize = customStyle.text.fontSize + 'px'
                    this.title_class.color = customStyle.text.color
                    this.title_class.textAlign = customStyle.text.hPosition
                    this.title_class.fontStyle = customStyle.text.isItalic ? 'italic' : 'normal'
                    this.title_class.fontWeight = customStyle.text.isBolder ? 'bold' : 'normal'

                    this.title_class.fontFamily = customStyle.text.fontFamily ? customStyle.text.fontFamily : DEFAULT_TITLE_STYLE.fontFamily
                    this.title_class.letterSpacing = (customStyle.text.letterSpace ? customStyle.text.letterSpace : DEFAULT_TITLE_STYLE.letterSpace) + 'px'
                    this.title_class.textShadow = customStyle.text.fontShadow ? '2px 2px 4px' : 'none'
                }
                if (customStyle.background) {
                    this.title_class.background = hexColorToRGBA(customStyle.background.color, customStyle.background.alpha)
                    this.borderRadius = (customStyle.background.borderRadius || 0) + 'px'
                }
                if (this.component.type === 'flow-map') {
                    this.title_class.zIndex = 4
                    this.title_class.position = 'absolute'
                }
                this.title = this.component.title
                this.remarkCfg = getRemark(this.component)
            }
        },
        onEditTitle() {
            if (this.readOnly) {
                return
            }
            this.titleEdit = true
            this.$nextTick(() => {
                this.$refs.titleInput.focus()
            })
        },
        onTitleChange() {
            this.titleEdit = false
            this.$emit('change', this.title)
        }
    }
}
</script>

<style></style>