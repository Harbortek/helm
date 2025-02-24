<template>
    <div :class="[selected ? 'comp-wapper-selected' : '', 'comp-wapper']" @click="onComponentSelected"
        :style="commonStyle">
        <a-icon v-if="borderEnable" :component="borderSvg" class="svg-background" style="width:100%;height:100%;"
            :style="{ color: borderColor }" />
        <div ref="componentWrapper" class="component">
            <!-- <div class="component-tool-bar" :style="toolbarStyle" v-show="selected">
                <a-dropdown>
                    <a-button icon="setting" shape="circle" size="small" />
                    <a-menu slot="overlay" @click="onMenuClick">
                        <a-menu-item key="delete"> <a-icon type="delete" />删除</a-menu-item>
                    </a-menu>
                </a-dropdown>
            </div> -->

            <div class="component-body-warapper">

                <table-normal ref="innerComponent"
                    v-if="component && component.type && component.type === 'table-normal'" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params"
                    :readOnly="readOnly" class="chart-class" :theme-style="component.commonBackground"
                    @onChartClick="chartClick" @change="onChange" />
                <table-info ref="innerComponent"
                    v-else-if="component && component.type && component.type === 'table-info'" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params"
                    :readOnly="readOnly" class="chart-class" :theme-style="component.commonBackground"
                    @onChartClick="chartClick" @change="onChange" />
                <table-pivot ref="innerComponent"
                    v-else-if="component && component.type && component.type === 'table-pivot'" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params"
                    :readOnly="readOnly" class="chart-class" :theme-style="component.commonBackground"
                    @onChartClick="chartClick" @change="onChange" />

                <traceability-table ref="innerComponent"
                    v-else-if="component && component.type && component.type === 'table-traceability'" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params"
                    :readOnly="readOnly" class="chart-class" :theme-style="component.commonBackground"
                    @onChartClick="chartClick" @change="onChange" />

                <text-card ref="innerComponent"
                    v-else-if="component && component.type && component.type === 'text-card'" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params"
                    :readOnly="readOnly" class="chart-class" :theme-style="component.commonBackground"
                    @onChartClick="chartClick" @change="onChange" />
                <indicator-card ref="innerComponent"
                    v-else-if="component && component.type && component.type === 'indicator-card'" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params"
                    :readOnly="readOnly" class="chart-class" :theme-style="component.commonBackground"
                    @onChartClick="chartClick" @change="onChange" />

                <single-line-text ref="innerComponent"
                    v-else-if="component && component.type && component.type === 'single-line-text'" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params"
                    :readOnly="readOnly" class="chart-class" :theme-style="component.commonBackground"
                    @change="onChange" />

                <rich-text-view ref="innerComponent"
                    v-else-if="component && component.type && component.type === 'richTextView'" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params" class="chart-class"
                    :theme-style="component.commonBackground" :readOnly="readOnly" @change="onChange" />

                <web-page ref="innerComponent" v-else-if="component && component.type && component.type === 'web-page'"
                    :pageId="pageId" :componentData="component" :canvas-style-data="canvasStyleData" :params="params"
                    class="chart-class" :theme-style="component.commonBackground" :readOnly="readOnly" />

                <tabs-panel ref="innerComponent"
                    v-else-if="component && component.type && component.type === 'tabs-panel'" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params" class="chart-class"
                    :theme-style="component.commonBackground" :readOnly="readOnly" @change="onChange"
                    @select="onSubComponentSelected" />

                <page-query ref="innerComponent"
                    v-else-if="component && component.type && component.type === 'page-query'" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params" class="chart-class"
                    :theme-style="component.commonBackground" @pageQuery="onPageQuery" />

                <chart-component ref="innerComponent" v-else-if="component && component.type" :pageId="pageId"
                    :componentData="component" :canvas-style-data="canvasStyleData" :params="params"
                    :readOnly="readOnly" class="chart-class" :theme-style="component.commonBackground"
                    @onChartClick="chartClick" @change="onChange" />

            </div>
        </div>
    </div>
</template>

<script>
import ChartComponent from '@/pages/smart-page/components/chart/ChartComponent.vue'
import TableNormal from '@/pages/smart-page/components/table/TableNormal.vue'
import TableInfo from "@/pages/smart-page/components/table/TableInfo.vue"
import TablePivot from '@/pages/smart-page/components/table/TablePivot.vue'
import TextCard from '@/pages/smart-page/components/normal/TextCard.vue'
import IndicatorCard from '@/pages/smart-page/components/normal/IndicatorCard.vue'
import SingleLineText from '@/pages/smart-page/components/others/SingleLineText.vue'
import PageQuery from '@/pages/smart-page/components/page-query/PageQuery.vue'
import VXETable from 'vxe-table'
import TraceabilityTable from '@/pages/smart-page/components/table/TraceabilityTable.vue'
import RichTextView from '@/pages/smart-page/components/others/RichTextView.vue'
import WebPage from '@/pages/smart-page/components/others/WebPage.vue'
import TabsPanel from '@/pages/smart-page/components/others/TabsPanel.vue'
import { hexColorToRGBA } from '@/pages/smart-page/util/util'
export default {
    name: 'ComponentWrapper',
    components: {
        ChartComponent, TableNormal, TableInfo, TablePivot, TextCard, IndicatorCard, SingleLineText, PageQuery, WebPage, TabsPanel,
        TraceabilityTable, RichTextView
    },
    props: {
        pageId: {
            required: true
        },
        component: {
            type: Object,
            required: true,
        },
        params: {
            type: Array,
            required: true,
        },
        canvasStyleData: {
            type: Object,
            required: true
        },
        selected: {
            required: true
        },
        readOnly: {
            required: true
        }
    },
    data() {
        return {
            toolbarStyle: {
                position: 'absolute',
                right: '0px',
                zIndex: 10,
            },
        }
    },
    
    computed: {
        commonStyle() {
            const style = {
                width: '100%',
                height: '100%'
            }
            const commonBackground = this.canvasStyleData.chartInfo.chartCommonStyle
            if (commonBackground) {
                style['padding'] = (commonBackground.innerPadding || 0) + 'px'
                style['border-radius'] = (commonBackground.borderRadius || 0) + 'px'
                let colorRGBA = ''
                let background = ''
                if (commonBackground.backgroundColorSelect) {
                    let colorRGBA = hexColorToRGBA(commonBackground.color, commonBackground.alpha)
                    background = colorRGBA
                }
                if (commonBackground.backgroundImageSelect && commonBackground.backgroundType === 'outerImage') {
                    const url = commonBackground.outerImage
                    background = `url(${url}) no-repeat ${colorRGBA}`
                }
                style['background'] = background

                style['overflow'] = 'hidden'
            }
            return style
        },
        borderEnable() {
            const commonBackground = this.canvasStyleData.chartInfo.chartCommonStyle
            return commonBackground.backgroundImageSelect && commonBackground.backgroundType === 'innerImage'
        },
        borderSvg() {
            const commonBackground = this.canvasStyleData.chartInfo.chartCommonStyle
            return commonBackground.backgroundImageSelect ? commonBackground.innerImage.replace('board/', '').replace('.svg', '') : ''
        },
        borderColor() {
            const commonBackground = this.canvasStyleData.chartInfo.chartCommonStyle
            return commonBackground.backgroundImageSelect ? commonBackground.innerImageColor : ''
        }
    },
    mounted() {
        // const width = this.$refs.componentWrapper.offsetWidth
        // this.toolbarStyle.left = width + 'px'
    },
    methods: {
        onComponentDataReload() {
            if (this.$refs.innerComponent && this.$refs.innerComponent.onComponentDataReload) {
                this.$refs.innerComponent.onComponentDataReload()
            }
        },
        onCompoenntMoved(compId, newX, newY) {
            if (this.$refs.innerComponent && this.$refs.innerComponent.onCompoenntMoved) {
                this.$refs.innerComponent.onCompoenntMoved(compId, newX, newY)
            }
        },
        onCompoenntResized(compId, newH, newW, newHPx, newWPx) {
            if (this.$refs.innerComponent && this.$refs.innerComponent.onCompoenntResized) {
                this.$refs.innerComponent.onCompoenntResized(compId, newH, newW, newHPx, newWPx)
            }
        },
        onComponentSelected(event) {
            event.stopPropagation();
            console.log('onComponentSelected', this.component)
            this.$emit('select', this.component)
        },
        onSubComponentSelected(comp) {
            console.log('onSubComponentSelected', comp)
            this.$emit('select', comp)
        },
        onPageQuery(pageParameters) {
            this.$emit('pageQuery', pageParameters)
        },
        onChange(e) {
            this.$emit('change', e)
        },
        onMenuClick(e) {
            const command = e.key
            if (command === 'delete') {
                VXETable.modal.confirm({
                    title: '删除组件',
                    content: '「' + this.component.title + '」将被从页面中删除'
                }).then(type => {
                    if (type === 'confirm') {
                        this.$emit('delete', this.component)
                    }
                })
            } else if (command === 'linkage') {

            } else if (command === 'jump') {

            } else if (command === 'style') {

            }
        },
        chartClick(param) {
            if (this.drillClickDimensionList.length < this.view.drillFields.length - 1) {
                // const isSwitch = (this.chart.type === 'map' && this.sendToChildren(param))
                if (this.chart.type === 'map' || this.chart.type === 'buddle-map') {
                    if (this.sendToChildren(param)) {
                        this.drillClickDimensionList.push({ dimensionList: param.data.dimensionList })
                        // this.getData(this.param.id)
                        this.calcData(true, 'chart', false, false)
                    }
                } else {
                    this.drillClickDimensionList.push({ dimensionList: param.data.dimensionList })
                    // this.getData(this.param.id)
                    this.calcData(true, 'chart', false, false)
                }
            } else if (this.view.drillFields.length > 0) {
                this.$message({
                    type: 'error',
                    message: this.$t('chart.last_layer'),
                    showClose: true
                })
            }
        },
        
    },
}
</script>

<style lang="less" scoped>
.comp-wapper {
    height: 100%;
    width: 100%;
    // border: 1px solid rgb(224, 219, 219);
    z-index: 5;
    display: block;
    // border-radius: 6px;
    // background-color: transparent;
    color: black;
    // box-shadow: 0 0 40px rgba(226, 226, 226, 0.5);
    // overflow: hidden;
    // padding: 0 15 15 15;
    margin: 0px;
    box-sizing: border-box;
    background-size: 100% 100% !important;

    .component {
        position: relative;
        display: flex;
        flex-direction: column;
    }

    .component-tool-bar {
        position: absolute;
        display: flex;
        flex-direction: column;
        width: 30px;
        // line-height: 30px;
        font-size: 12px;
        margin: 0 5px;
        top:8px;
        // flex: 0 0 auto;
    }

    .component-body-warapper {
        flex: 1 1 auto;
        height: 100%;
        padding: 5px;
    }

    &.cssTransforms {
        transition: all 200ms ease;
        transition-property: box-shadow, transform;
    }

    // &:hover {
    //     box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
    // }

    &.popup {
        position: absolute !important;
        z-index: 100;
    }

    .component {
        width: 100%;
        height: 100%;
        display: block;
    }
}

.comp-wapper-selected {
    border: solid 1px #70c0ff;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
}

.svg-background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100% !important;
    height: 100% !important;
    z-index: 1;
}

.svg-background ::v-deep svg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100% !important;
    height: 100% !important;
    z-index: 1;
}
</style>