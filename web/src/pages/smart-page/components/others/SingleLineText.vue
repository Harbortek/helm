<template>
    <div ref="chartContainer" style="padding: 0;width: 100%;height: 100%;overflow: hidden;" :style="bg_class">
        <TitleComponent :component="componentData" :readOnly="readOnly" @change="onTitleChange" />
    </div>
</template>

<script>

import { hexColorToRGBA } from '@/pages/smart-page/util/util'
import { equalsAny } from '@/utils/StringUtils'
import { DEFAULT_TITLE_STYLE } from '@/pages/smart-page/util/chart'
import { getData } from '@/services/smart-page/PageComponentService'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import TitleComponent from '../title/TitleComponent.vue'
export default {
    name: 'SingleLineText',
    components: {
        TitleComponent
    },
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
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            borderRadius: '0px',
            chartHeight: '100%',
            chart: undefined,
        }
    },
    computed: {
        bg_class() {
            return {
                backgroundColor: 'transparent',
                borderRadius: this.borderRadius
            }
        }
    },
    watch: {
        componentData: {
            handler(newVal, oldVla) {
                this.chart = deepCopy(newVal)

            },
            deep: true,
            immediate: true,
        },
        params: {
            handler(newVal, oldVla) {
                this.loadData()
            },
            deep: true,
            immediate: false,
        }
    },
    beforeDestroy() {
    },
    mounted() {
        this.chart = deepCopy(this.componentData)
        this.loadData()
    },
    methods: {
        loadData() {
            this.setBackGroundBorder()
            const data_config = {
                type: this.chart.type,
                datasetId: this.chart.datasetId,
                xaxis: JSON.parse(this.chart.xaxis),
                xaxisExt: JSON.parse(this.chart.xaxisExt),
                yaxis: JSON.parse(this.chart.yaxis),
                yaxisExt: JSON.parse(this.chart.yaxisExt),
                extStack: JSON.parse(this.chart.extStack),
                extBubble: JSON.parse(this.chart.extBubble),
                customFilter: JSON.parse(this.chart.customFilter),
                drillFields: JSON.parse(this.chart.drillFields),
            }
            const data = {
                config: JSON.stringify(data_config),
                customAttr: this.chart.customAttr,
                params: this.params
            }
            getData(this.pageId, this.chart.id, data).then(resp => {
                this.chart.data = JSON.parse(resp)
            })
        },
        setBackGroundBorder() {
            if (this.chart.customStyle) {
                const customStyle = JSON.parse(this.chart.customStyle)
                if (customStyle.background) {
                    this.borderRadius = (customStyle.background.borderRadius || 0) + 'px'
                }
            }
        },
        onTitleChange(title) {
            let chart = deepCopy(this.chart)
            chart.title = title
            this.$emit('change', chart)
        },
    },
}
</script>

<style></style>