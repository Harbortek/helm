<template>
    <div ref="editorConatiner" style="width: 100%; height: 100%;">
        <simple-editor v-model="richTextContent" :disabled="readOnly"
            :style="{ height: maxHeight + 'px', '--w-e-textarea-bg-color': 'transparent' }"
            :showToolbar="!readOnly" @change="onChange"/>
    </div>
</template>

<script>
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import SimpleEditor from '@/components/editor/SimpleEditor.vue';
export default {
    name: 'RichTextView',
    components: { SimpleEditor },
    props: {
        pageId: {
            required: true
        },
        componentData: {
            type: Object,
            required: true
        },
        params: {
            type: Array,
            required: true,
        },
        canvasStyleData: {
            type: Object,
            required: true
        },
        readOnly: {
            default: false,
            type: Boolean,
            required: true
        }
    },
    data() {
        return {
            chartHeight: '100%',
            chart: undefined,
            richTextContent: '',
            editor: null,
            html: '<p>hello</p>',
            maxHeight: 300,
        }
    },
    watch: {
        componentData: {
            handler(newVal, oldVal) {
                this.chart = deepCopy(newVal)
            },
            deep: true,
            immediate: true,
        },
    },
    mounted() {
        this.chart = deepCopy(this.componentData)
        setTimeout(() => {
            this.$nextTick(() => {
                if (this.$refs.editorConatiner) {
                    this.maxHeight = this.$refs.editorConatiner.offsetHeight - (this.readOnly ? 0 : 40)
                }
            })
        }, 200)
    },
    methods: {
        onComponentDataReload() {
            this.loadData()
        },
        onCompoenntResized(compId, newH, newW, newHPx, newWPx) {
            if (this.$refs.editorConatiner) {
                this.maxHeight = newHPx - (this.readOnly ? 0 : 40)
            }
        },
        loadData() {
            this.richTextContent = this.chart.richTextContent
        },
        onChange(v) {
            if (this.chart.richTextContent === this.richTextContent) {
                return
            }
            this.chart.richTextContent = this.richTextContent
            this.$emit("change", this.chart);
        },
    },
}
</script>

<style></style>