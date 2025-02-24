<template>
    <div style="width: 100%;height: 100%;" ref="editorConatiner">
        <a-tabs v-model="activeTab">
            <a-tab-pane v-for="(item, index) in tabs" :key="index" :tab="item.title">
                <div :style="{ width: '100%', height: height + 'px' }" @dragstart="dragStart(index, $event)"
                    @dragenter.prevent="dragenterDebounce(index, $event)"
                    @dragleave.prevent="dragleaveDebounce(index, $event)" @dragover.prevent
                    @dragend="dragendDebounce(index, $event)" @drop.prevent="drop(index, $event)">
                    <component-wrapper v-if="item.type" :pageId="pageId" :component="item"
                        :canvasStyleData="canvasStyleData" :params="params" :selected="item.id === selectedComponent?.id"
                        :readOnly="readOnly" @select="onComponentSelect" @delete="onComponentDelete"
                        @pageQuery="onPageQuery" @change="componentDataChange" />
                    <div v-if="!item.type && dragPlaceHolder" class="drag-placeholder"></div>
                </div>
            </a-tab-pane>
            <a-button slot="tabBarExtraContent" @click="onAddTab">
                新增标签页
            </a-button>
        </a-tabs>
    </div>
</template>

<script>
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import { COMMON_BACKGROUND } from '@/pages/smart-page/util/component-list' // 左侧列表数据
import {
    DEFAULT_COLOR_CASE,
    DEFAULT_FUNCTION_CFG,
    DEFAULT_LABEL,
    DEFAULT_LEGEND_STYLE,
    DEFAULT_SIZE,
    DEFAULT_SPLIT,
    DEFAULT_THRESHOLD,
    DEFAULT_TITLE_STYLE,
    DEFAULT_TOOLTIP,
    DEFAULT_TOTAL,
    DEFAULT_XAXIS_STYLE,
    DEFAULT_YAXIS_EXT_STYLE,
    DEFAULT_YAXIS_STYLE
} from '@/pages/smart-page/util/chart'
import { adaptCurTheme } from '@/pages/smart-page/util/style'
import { DEFAULT_COMMON_CANVAS_STYLE_STRING } from '@/pages/smart-page/util/panel'
import { getCompoenentId } from '@/services/smart-page/PageComponentService';
import XEUtils from "xe-utils";
export default {
    name: 'TabsPanel',
    components: { ComponentWrapper: () => import('../ComponentWrapper.vue') },
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
            tabs: '',
            activeTab: 0,
            selectedComponent: null,
            height: 240,
            dragPlaceHolder: false,
            dragging: 0,
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
    created() {
        this.dragenterDebounce = XEUtils.debounce(this.dragenter, 300)
        this.dragleaveDebounce = XEUtils.debounce(this.dragleave, 300)
        this.dragendDebounce = XEUtils.debounce(this.dragend, 300)
    },
    beforeDestroy() {
    },
    mounted() {
        setTimeout(() => {
            this.$nextTick(() => {
                if (this.$refs.editorConatiner) {
                    this.height = this.$refs.editorConatiner.offsetHeight - 60
                    console.log(this.height)
                }
            })
        }, 200)
    },
    methods: {
        onCompoenntResized(compId, newH, newW, newHPx, newWPx) {
            if (this.$refs.editorConatiner) {
                this.height = newHPx - 60
                console.log(this.height)
            }
        },
        loadData() {
            if (this.chart && this.chart.children) {
                const data = this.chart.children || [{ title: '新标签页' }]
                this.tabs = data
                console.log('loadData', data)
            } else {
                this.tabs = [{ title: '新标签页' }]

            }
        },
        onComponentSelect(comp) {
            this.selectedComponent = comp
            console.log('onComponentSelect', comp)
            this.$emit('select', comp)
        },
        onComponentDelete() {
            this.tabs[this.activeTab] = { title: this.tabs[this.activeTab].title }
            this.$set(this.tabs, this.activeTab, this.tabs[this.activeTab])
            this.chart.children = deepCopy(this.tabs)
            this.$emit('change', this.chart)
        },
        onPageQuery(pageParameters) {
            this.$emit('pageQuery', pageParameters)
        },
        componentDataChange(comp) {
            this.tabs[this.activeTab] = comp
            this.$set(this.tabs, this.activeTab, this.tabs[this.activeTab])
            this.chart.children = deepCopy(this.tabs)
            this.$emit('change', this.chart)
        },
        onAddTab() {
            this.tabs.push({
                title: '新标签页',
            })
        },
        dragStart(index, event) {
            // console.log('dragStart', index, event)
        },
        dragenter(index, event) {
            this.dragging++
            // console.log('dragenter', index, event)
            event.stopPropagation();
            if (this.dragging > 0) {
                this.dragPlaceHolder = true
            }

        },
        dragleave(index, event) {
            this.dragging--
            // console.log('dragleave', index, event)
            event.stopPropagation();
            if (this.dragging === 0) {
                this.dragPlaceHolder = false
            }
        },
        dragend(index, event) {
            // console.log('dragend', index, event)
            event.stopPropagation();
            this.dragPlaceHolder = false
        },
        drop(index, event) {
            event.stopPropagation();
            this.dragPlaceHolder = false
            const comp = JSON.parse(event.dataTransfer.getData('text/plain'))
            this.createComponent('新建视图', comp.value, comp.render)

        },
        createComponent(title, type, render) {
            let component = { type: type, render: render, layout: {} }
            // const viewConfig = this.componentTypes.filter(item => item.render === render && item.value === type)
            // if (viewConfig && viewConfig.length > 0) {
            //     component.useDataset = viewConfig[0].dataset || false
            // }

            // component.layout.left = x
            // component.layout.top = y
            // component.layout.width = w
            // component.layout.height = h

            // component.id = newComponentId
            // 统一设置背景信息
            component.commonBackground = deepCopy(COMMON_BACKGROUND)
            // 适配当前主题

            component.name = title
            component.title = title
            component.type = type
            component.isPlugin = false
            component.render = render
            component.resultMode = 'custom'
            component.resultCount = 1000
            component.refreshViewEnable = false
            component.refreshUnit = 'minute'
            component.refreshTime = 5
            const customAttr = {
                color: DEFAULT_COLOR_CASE,
                tableColor: DEFAULT_COLOR_CASE,
                size: DEFAULT_SIZE,
                label: DEFAULT_LABEL,
                tooltip: DEFAULT_TOOLTIP,
                totalCfg: DEFAULT_TOTAL
            }
            const customStyle = {
                text: DEFAULT_TITLE_STYLE,
                legend: DEFAULT_LEGEND_STYLE,
                xAxis: DEFAULT_XAXIS_STYLE,
                yAxis: DEFAULT_YAXIS_STYLE,
                yAxisExt: DEFAULT_YAXIS_EXT_STYLE,
                split: DEFAULT_SPLIT
            }
            const canvasStyle = deepCopy(DEFAULT_COMMON_CANVAS_STYLE_STRING)
            // 新建的视图应用当前主题
            adaptCurTheme(customStyle, customAttr, type, canvasStyle)
            component.customAttr = JSON.stringify(customAttr)
            component.customStyle = JSON.stringify(customStyle)
            component.senior = JSON.stringify({
                functionCfg: DEFAULT_FUNCTION_CFG,
                assistLine: [],
                threshold: DEFAULT_THRESHOLD
            })
            component.stylePriority = 'view' // 默认样式优先级视图
            component.xaxis = JSON.stringify([])
            component.xaxisExt = JSON.stringify([])
            component.yaxis = JSON.stringify([])
            component.yaxisExt = JSON.stringify([])
            component.extStack = JSON.stringify([])
            component.customFilter = JSON.stringify([])
            component.drillFields = JSON.stringify([])
            component.extBubble = JSON.stringify([])
            component.viewFields = JSON.stringify([])
            this.setChartDefaultOptions(component)
            getCompoenentId(this.pageId).then(resp => {
                component.id = resp
                this.tabs[this.activeTab] = component
                this.$set(this.tabs, this.activeTab, this.tabs[this.activeTab])
                console.log(this.tabs)
                this.chart.children = this.tabs
                this.$emit('change', this.chart)
            })
        },
        setChartDefaultOptions(view) {
            const type = view.type
            const attr = JSON.parse(view.customAttr)
            if (view.render === 'echarts') {
                attr.label.position = 'inside'
            } else {
                attr.label.position = 'middle'
            }
            if (type.indexOf('pie') >= 0) {
                if (view.render === 'echarts') {
                    attr.label.position = 'inside'
                } else {
                    const customStyle = JSON.parse(view.customStyle)
                    customStyle.legend.show = false
                    view.customStyle = JSON.stringify(customStyle)
                    attr.label.show = true
                    attr.label.position = 'outer'
                }
                if (type === 'pie-donut') {
                    attr.size.pieInnerRadius = Math.round(attr.size.pieOuterRadius * 0.75)
                }
                if (type === 'pie-donut-rose') {
                    attr.size.pieInnerRadius = Math.round(attr.size.pieOuterRadius * 0.5)
                }
            } else if (type.indexOf('bar') >= 0) {
                attr.label.labelContent = ['quota']
                const senior = JSON.parse(view.senior)
                senior.functionCfg.emptyDataStrategy = 'ignoreData'
                view.senior = JSON.stringify(senior)
            } else if (type.indexOf('line') >= 0) {
                attr.label.position = 'top'
            }
            view.customAttr = JSON.stringify(attr)
        },
    },
}
</script>

<style lang="less" scoped>
.drag-placeholder {
    width: 100%;
    height: 100%;
    border: dashed 1px #409eff;
}
</style>