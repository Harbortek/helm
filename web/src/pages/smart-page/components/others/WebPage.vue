<template>
    <div ref="webpageContainer" style="width: 100%; height: 100%;">
        <h-iframe class="h-iframe" ref="webPage" :src="webUrl"></h-iframe>
    </div>
</template>

<script>
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import HIframe from '@/components/iframe'
export default {
    name: 'WebPage',
    components: { HIframe },
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
            required: true
        }
    },
    data() {
        return {
            chart: undefined,
            webUrl: '',
        }
    },
    computed: {

    },
    watch: {
        componentData: {
            handler(newVal, oldVal) {
                this.chart = deepCopy(newVal)
                this.loadData()
            },
            deep: true,
            immediate: true,
        },
        params: {
            handler(newVal, oldVal) {
            },
            deep: true,
            immediate: false,
        },
    },
    beforeDestroy() {
    },
    mounted() {
        this.chart = deepCopy(this.componentData)

    },
    methods: {
        onCompoenntResized(compId, newH, newW, newHPx, newWPx) {
        },
        loadData() {
            if (this.chart && this.chart.data) {
                const data = JSON.parse(this.chart.data) || {}
                this.webUrl = data.webUrl +'?embedded=true'
                console.log(this.webUrl)
            }
        },

    },
}
</script>

<style></style>