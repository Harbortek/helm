<template>
    <div ref="chartContainer" style="padding: 0;width: 100%;height: 100%;overflow: hidden;" :style="backgroundClass">
        <title-component ref="title" :component="componentData" @change="onTitleChange" />
        <div :id="chartId" style="width: 100%;overflow: hidden;" class="g2-container" :style="{ height: chartHeight }">
        </div>
    </div>
</template>

<script>
import { baseLiquid } from '@/pages/smart-page/components/chart/liquid/liquid'

import { baseBarOptionAntV, hBaseBarOptionAntV, baseBidirectionalBarOptionAntV } from '@/pages/smart-page/components/chart/bar/bar_antv'
import { baseAreaOptionAntV, baseLineOptionAntV } from '@/pages/smart-page/components/chart/line/line_antv'
import { basePieOptionAntV, basePieRoseOptionAntV } from '@/pages/smart-page/components/chart/pie/pie_antv'
import { baseScatterOptionAntV } from '@/pages/smart-page/components/chart/scatter/scatter_antv'
import { baseGaugeOptionAntV } from '@/pages/smart-page/components/chart/gauge/gauge_antv'
import { baseFunnelOptionAntV } from '@/pages/smart-page/components/chart/funnel/funnel_antv'
import { baseTreemapOptionAntV } from '@/pages/smart-page/components/chart/treemap/treemap_antv'
import { baseRadarOptionAntV } from '@/pages/smart-page/components/chart/radar/radar_antv'
import { baseWaterfallOptionAntV } from '@/pages/smart-page/components/chart/waterfall/waterfall'
import { baseWordCloudOptionAntV } from '@/pages/smart-page/components/chart/wordCloud/word_cloud'
import { baseMixOptionAntV } from '@/pages/smart-page/components/chart/mix/mix_antv'
import { baseFlowMapOption } from '@/pages/smart-page/components/chart/map/map_antv'
import { equalsAny } from '@/utils/StringUtils'

import { getData } from '@/services/smart-page/PageComponentService'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'

import TitleComponent from '../title/TitleComponent.vue'

export default {
    name: 'ChartComponent',
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
        },
    },
    data() {
        return {
            borderRadius: '0px',
            chartHeight: '100%',
            title_class: {
                margin: '5px 15px',
                width: '100%',
                fontSize: '18px',
                color: '#303133',
                textAlign: 'left',
                fontStyle: 'normal',
                fontWeight: 'normal',
                background: ''
            },
            title_show: true,
            chart: undefined,
            titleEdit: false,
            title: '',
        }
    },
    computed: {
        chartId() {
            return this.componentData.id
        },
        backgroundClass() {
            return {
                borderRadius: this.borderRadius
            }
        }

    },
    watch: {
        componentData: {
            handler(newVal, oldVla) {
                const data = this.chart?.data || {}
                this.chart = deepCopy(newVal)
                this.chart.data = data
                this.drawView()
            },
            deep: true,
            immediate: false,
        },
    },
    beforeDestroy() {
        if (this.myChart && typeof (this.myChart.destroy) === 'function') {
            this.myChart.destroy()
        }
        this.myChart = null
    },
    mounted() {
        this.chart = deepCopy(this.componentData)
        this.drawView()
    },
    methods: {        
        onComponentDataReload() {
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
                this.linkageActiveHistory = false
                this.drawView()
            })
        },
        onCompoenntMoved(compId, newX, newY) {

        }, 
        onCompoenntResized(compId, newH, newW, newHPx, newWPx) {

        },       
        calcHeightRightNow() {
            this.$nextTick(() => {
                if (this.$refs.chartContainer) {
                    const currentHeight = this.$refs.chartContainer.offsetHeight
                    if (this.$refs.title) {
                        const titleHeight = this.$refs.title.$el.offsetHeight
                        this.chartHeight = (currentHeight - titleHeight) + 'px'
                    }
                }
            })
        },
        calcHeightDelay() {
            setTimeout(() => {
                this.calcHeightRightNow()
            }, 100)
        },
        drawView() {
            this.calcHeightDelay()
            const chart = this.chart
            this.antVRenderStatus = true
            if (!chart.data || (!chart.data.data && !chart.data.series)) {
                chart.data = {
                    data: [{}],
                    series: [
                        {
                            data: [0]
                        }
                    ]
                }
            }
            if (chart.type === 'bar') {
                this.myChart = baseBarOptionAntV(this.myChart, this.chartId, chart, this.antVAction, true, false)
            } else if (chart.type === 'bar-group') {
                this.myChart = baseBarOptionAntV(this.myChart, this.chartId, chart, this.antVAction, true, false)
            } else if (equalsAny(chart.type, 'bar-stack', 'percentage-bar-stack')) {
                this.myChart = baseBarOptionAntV(this.myChart, this.chartId, chart, this.antVAction, false, true)
            } else if (chart.type === 'bar-group-stack') {
                this.myChart = baseBarOptionAntV(this.myChart, this.chartId, chart, this.antVAction, true, true)
            } else if (chart.type === 'bar-horizontal') {
                this.myChart = hBaseBarOptionAntV(this.myChart, this.chartId, chart, this.antVAction, true, false)
            } else if (equalsAny(chart.type, 'bar-stack-horizontal', 'percentage-bar-stack-horizontal')) {
                this.myChart = hBaseBarOptionAntV(this.myChart, this.chartId, chart, this.antVAction, false, true)
            } else if (chart.type === 'line') {
                this.myChart = baseLineOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'area') {
                this.myChart = baseAreaOptionAntV(this.myChart, this.chartId, chart, this.antVAction, false)
            } else if (chart.type === 'line-stack') {
                this.myChart = baseAreaOptionAntV(this.myChart, this.chartId, chart, this.antVAction, true)
            } else if (chart.type === 'scatter') {
                this.myChart = baseScatterOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'radar') {
                this.myChart = baseRadarOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'gauge') {
                this.myChart = baseGaugeOptionAntV(this.myChart, this.chartId, chart, this.antVAction, this.scale)
            } else if (chart.type === 'pie' || chart.type === 'pie-donut') {
                this.myChart = basePieOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'pie-rose' || chart.type === 'pie-donut-rose') {
                this.myChart = basePieRoseOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'funnel') {
                this.myChart = baseFunnelOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'treemap') {
                this.myChart = baseTreemapOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'liquid') {
                this.myChart = baseLiquid(this.myChart, this.chartId, chart)
            } else if (chart.type === 'waterfall') {
                this.myChart = baseWaterfallOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'word-cloud') {
                this.myChart = baseWordCloudOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'chart-mix') {
                this.myChart = baseMixOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'flow-map') {
                this.myChart = baseFlowMapOption(this.myChart, this.chartId, chart, this.antVAction)
            } else if (chart.type === 'bidirectional-bar') {
                this.myChart = baseBidirectionalBarOptionAntV(this.myChart, this.chartId, chart, this.antVAction)
            } else {
                if (this.myChart) {
                    this.antVRenderStatus = false
                    this.myChart.destroy()
                }
            }

            if (this.myChart && !equalsAny(chart.type, 'liquid', 'flow-map') && this.searchCount > 0) {
                this.myChart.options.animation = false
            }
            if (this.myChart?.options?.legend) {
                let pageNavigatorInactiveFill, pageNavigatorFill
                if (this.canvasStyleData.background.themeColor === 'dark') {
                    pageNavigatorFill = '#ffffff'
                    pageNavigatorInactiveFill = '#8c8c8c'
                } else {
                    pageNavigatorFill = '#000000'
                    pageNavigatorInactiveFill = '#8c8c8c'
                }
                this.myChart.options.legend['pageNavigator'] = {
                    marker: {
                        style: {
                            inactiveFill: pageNavigatorInactiveFill, // 不能点击的颜色
                            fill: pageNavigatorFill // 正常的颜色
                        }
                    }
                }
            }

            if (this.antVRenderStatus) {
                if (this.myChart && typeof (this.myChart.render) === 'function') {
                    this.myChart.render()
                }
                if (this.linkageActiveHistory) {
                    this.linkageActive()
                }
            }
            this.setBackGroundBorder()
        },

        antVAction(param) {
            if (this.chart.type === 'treemap') {
                this.pointParam = param.data.data
            } else {
                this.pointParam = param.data
            }
            this.linkageActiveParam = {
                category: this.pointParam.data.category ? this.pointParam.data.category : 'NO_DATA',
                name: this.pointParam.data.name ? this.pointParam.data.name : 'NO_DATA'
            }
            if (this.trackMenu.length < 2) { // 只有一个事件直接调用
                this.trackClick(this.trackMenu[0])
            } else { // 视图关联多个事件
                this.trackBarStyle.left = param.x + 'px'
                this.trackBarStyle.top = (param.y + 10) + 'px'
                this.$refs.viewTrack.trackButtonClick()
            }
        },
        setBackGroundBorder() {
            if (this.chart.customStyle) {
                const customStyle = JSON.parse(this.chart.customStyle)
                if (customStyle.background) {
                    this.borderRadius = (customStyle.background.borderRadius || 0) + 'px'
                }
            }
        },
        trackClick(trackAction) {
            const param = this.pointParam
            if (!param || !param.data || !param.data.dimensionList) {
                // 地图提示没有关联字段 其他没有维度信息的 直接返回
                if (this.chart.type === 'map') {
                    this.$warning(this.$t('panel.no_drill_field'))
                }
                return
            }
            const quotaList = this.pointParam.data.quotaList
            quotaList[0]['value'] = this.pointParam.data.value
            const linkageParam = {
                option: 'linkage',
                name: this.pointParam.data.name,
                viewId: this.chart.id,
                dimensionList: this.pointParam.data.dimensionList,
                quotaList: quotaList
            }
            const jumpParam = {
                option: 'jump',
                name: this.pointParam.data.name,
                viewId: this.chart.id,
                dimensionList: this.pointParam.data.dimensionList,
                quotaList: quotaList
            }

            switch (trackAction) {
                case 'drill':
                    this.$emit('onChartClick', this.pointParam)
                    break
                case 'linkage':
                    this.linkageActivePre()
                    this.$store.commit('addViewTrackFilter', linkageParam)
                    break
                case 'jump':
                    this.$emit('onJumpClick', jumpParam)
                    break
                default:
                    break
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