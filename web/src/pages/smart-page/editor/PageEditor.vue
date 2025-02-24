<template>
    <div class="page-editor">
        <div class="top-tool-bar">
            <div class="" style="padding-right:10px;">
                <vxe-toolbar size="medium">
                    <template #tools>
                        <a-space>
                            <a-button icon="save" @click="onSave" :disabled="!dirty">保存</a-button>
                            <a-button icon="close" @click="onClose">退出编辑</a-button>
                        </a-space>
                    </template>
                </vxe-toolbar>
            </div>
        </div>
        <div class="work-area">
            <div style="background: #fff;width: 60px;overflow-x: hidden;flex: 0 0 60px;z-index: 20;">
                <div class="leftPanel">
                    <div class="toolbar">
                        <div class="large-button">
                            <div class="button-icon">
                                <a-button shape="circle" size="large" @click="showComponetPalette">
                                    <a-icon type="picture" />
                                </a-button>
                            </div>
                            <div class="button-text">组件</div>
                        </div>
                        <div class="large-button">
                            <div class="button-icon">
                                <a-button shape="circle" size="large" @click="showDatasetList">
                                    <a-icon type="filter" />
                                </a-button>
                            </div>
                            <div class="button-text">数据集</div>
                        </div>
                    </div>

                </div>
            </div>

            <div style="flex:1 1 auto" class="edit-canvas-container" id="mainEditorWarpper" ref="mainEditorWarpper">
                <a-drawer placement="left" :closable="false" :visible="componentPaletteVisible" :zIndex="10"
                    get-container="#mainEditorWarpper" @close="onClose" :mask="false"
                    :wrap-style="{ position: 'absolute', height: 'calc(100%)' }" :bodyStyle="{ padding: 0 }"
                    width="300">
                    <div class="component-palette">
                        <div class="palette-title">组件库</div>
                        <a-input-search placeholder="搜索组件" class="component-search" />
                        <a-collapse expandIconPosition="right" style="margin:10px;">
                            <a-collapse-panel :key="index" :header="$t(category)"
                                v-for="(category, index) in Object.keys(componentTypeMap)">
                                <div class="component-box" :key="idx" v-for="(item, idx) in componentTypeMap[category]"
                                    @dragstart="e => dragStart(e, item)" @drag="drag(item)" @dragend="dragend(item)"
                                    draggable="true" unselectable="on" @click="appendNewComponent(item)">
                                    <div class="component-icon"> <a-icon :component="item.icon"
                                            style="width:56px;height:56px;fontSize:42px;" />
                                    </div>
                                    <div class="component-title">{{ $t(item.title) }}</div>
                                </div>
                            </a-collapse-panel>
                        </a-collapse>
                    </div>
                </a-drawer>
                <a-drawer placement="left" :closable="false" :visible="datasetVisible" width="300" :mask="false"
                    :zIndex="10" get-container="#mainEditorWarpper" @close="onClose"
                    :wrap-style="{ position: 'absolute', height: 'calc(100%)' }" :bodyStyle="{ padding: 0 }">
                    <div class="dataset-list">
                        <dataset :pageId="pageId" />
                    </div>
                </a-drawer>
                    <div class="bg-image" :style="backgroundStyle()"></div>
                    <grid-layout id="main-canvas" ref="gridlayout" :layout.sync="layout"
                        :col-num="pageStyle.layout.columnNums" :row-height="pageStyle.layout.rowHeight"
                        :is-draggable="true" :is-resizable="true" :is-mirrored="false" :vertical-compact="true"
                        :margin="[pageStyle.layout.gap, pageStyle.layout.gap]" 
                        :prevent-collision="false" :use-css-transforms="true" :style="gridLayoutStyle()"
                        class="grid-layout">

                        <grid-item v-for="item in layout" :x="item.x" :y="item.y" :w="item.w" :h="item.h" :i="item.i"
                            :key="item.i" @moved="onCompoenntMoved" @resized="onCompoenntResized">
                            <component-wrapper ref="componentWrapper" v-if="item.component" :pageId="pageId"
                                :component="item.component" :canvasStyleData="pageStyle"
                                :params="queryParameterMap[item.component.id] || []"
                                :selected="item.component.id === selectedComponent?.id" :readOnly="false"
                                @select="onComponentSelect" @delete="onComponentDelete" @pageQuery="onPageQuery"
                                @change="componentDataChange" />
                        </grid-item>
                    </grid-layout>
            </div>
            <div style="flex: 0 0 350px; width: 350px; background: #fff">
                <div ref="rightPanel" class="rightPanel">
                    <template v-if="selectedComponent">
                        <div class="title-setting"> 
                            <div style="flex:1;">
                            <span>{{ $t(typeTitle) + '[' + selectedComponent?.title + "]" }}</span>
                            </div>
                            <div style="flex:0;width: 50px;">
                                <a-button type="link" size="small" icon="delete"  @click="onDeleteComponent"><span style="font-size: 12px;">删除</span></a-button>
                            </div>
                        </div>
                        <a-tabs :default-active-key="propetyEditor[0].name" style="flex: 1 1 auto;">
                            <a-tab-pane :key="tab.name" :tab="$t('chart.tab_title_' + tab.name)"
                                v-for="(tab, index) in propetyEditor" :forceRender="true">
                                <component :is="propetyEditor[index].component" :pageId="pageId" :charts="charts"
                                    :component="selectedComponent" @change="componentDataChange" />
                            </a-tab-pane>
                        </a-tabs>
                    </template>
                    <page-props-panel v-else :pageStyle="pageStyle" @change="onPageStyleChange" />
                </div>
            </div>

        </div>

    </div>
</template>

<script>
import { getCompoenentId } from '@/services/smart-page/PageComponentService';
import { loadPageDefinition, savePageDefinition } from '@/services/smart-page/PageDefinitionService'
import { panelDataPrepare } from '@/pages/smart-page/util/canvasUtils'
import { TYPE_CONFIGS } from '@/pages/smart-page/components/componentRegistry'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import VueGridLayout from 'vue-grid-layout';
import ComponentWrapper from '@/pages/smart-page/components/ComponentWrapper';
import {
    COMMON_BACKGROUND,
} from '@/pages/smart-page/util/component-list' // 左侧列表数据
import Dataset from './Dataset.vue';
import PagePropsPanel from '@/pages/smart-page/propertyeditor/page/PagePropsPanel.vue';

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
import VXETable from 'vxe-table';


let mouseXY = { "x": null, "y": null };
let DragPos = { "x": null, "y": null, "w": 1, "h": 1, "i": null };


export default {
    name: 'PageEditor',
    components: {
        ComponentWrapper, Dataset, PagePropsPanel,
        GridLayout: VueGridLayout.GridLayout,
        GridItem: VueGridLayout.GridItem
    },
    data() {
        return {
            componentPaletteVisible: false,
            datasetVisible: false,
            leftSideWidth: 60,
            pageStyle: deepCopy(DEFAULT_COMMON_CANVAS_STYLE_STRING),
            componentTypes: [...TYPE_CONFIGS],
            componentTypeMap: {},
            layout: [],
            selectedComponent: undefined,
            pageDefinition: { dataScope: {} },
            dirty: false,
            queryParameterMap: {},
        }
    },
    props: {
        pageId: {
            required: true
        },
    },
    watch: {
        pageId: {
            handler(newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }

            },
            immediate: true
        },
    },
    computed: {
        typeTitle() {
            const selected = this.selectedComponent
            const viewConfig = this.componentTypes.filter(item => item.render === selected.render && item.value === selected.type)
            if (viewConfig && viewConfig.length > 0) {
                return viewConfig[0].title
            }
        },
        propetyEditor() {
            const selected = this.selectedComponent
            const viewConfig = this.componentTypes.filter(item => item.render === selected.render && item.value === selected.type)
            if (viewConfig && viewConfig.length > 0) {
                return viewConfig[0].propertyEditor
            }
        },
        charts() {
            let componentList = []
            this.layout.forEach(item => {
                item.component.layout.left = item.x
                item.component.layout.top = item.y
                item.component.layout.width = item.w
                item.component.layout.height = item.h
                componentList.push(item.component)
            })
            return componentList
        }
    },
    mounted() {
        this.initEvents()
        this.formatTypes()

        this.$refs.gridlayout.$el.addEventListener("dragover", function (e) {
            mouseXY.x = e.clientX;
            mouseXY.y = e.clientY;
        }, false);

        this.$refs.gridlayout.$el.addEventListener("click", this.onCanvasClick)

    },
    beforeDestroy() {
        this.$refs.gridlayout.$el.removeEventListener('click', this.onCanvasClick)
        const elx = this.$refs.rightPanel
        elx && elx.remove()
    },
    methods: {
        initEvents() {
        },
        onCanvasClick(e) {
            if (e.target && e.target.id === 'main-canvas') {
                this.selectedComponent = null
            }
        },
        formatTypes() {
            this.componentTypes.forEach(item => {
                const { render, category } = item
                this.componentTypeMap = this.componentTypeMap || {}
                this.componentTypeMap[category] = this.componentTypeMap[category] || []
                if (item.render === 'antv') {
                    this.componentTypeMap[category].push(item)
                }
            })
        },
        loadData() {
            loadPageDefinition(this.pageId).then(resp => {
                this.pageDefinition = resp
                let componentList = JSON.parse(resp.pageComponents || '[]')

                this.queryParameterMap = {}
                let hasPageQuery = false
                for (let j = 0; j < componentList.length; j++) {
                    const component = componentList[j]
                    this.queryParameterMap[component.id] = []
                    if (component.type === 'page-query') {
                        hasPageQuery = true
                    }
                }

                this.pageStyle = JSON.parse(resp.pageStyle || JSON.stringify(DEFAULT_COMMON_CANVAS_STYLE_STRING))

                panelDataPrepare(componentList, this.pageStyle, function () { })

                this.layout = []
                componentList.forEach(comp => {
                    this.layout.push({
                        x: comp.layout.left,
                        y: comp.layout.top,
                        w: comp.layout.width,
                        h: comp.layout.height,
                        i: comp.id,
                        component: comp
                    })
                })
                console.log('this page has ' + componentList.length + ' components:', this.layout)

                if (hasPageQuery) {
                    //通过page-query组件加载数据

                } else {
                    //直接加载组件的数据
                    this.$nextTick(() => {
                        this.onPageQuery([])
                    })
                }
            })
        },
        onSave() {
            this.dirty = false
            let pageDefinition = deepCopy(this.pageDefinition)
            pageDefinition.pageStyle = JSON.stringify(this.pageStyle)

            let componentList = []
            this.layout.forEach(item => {
                let comp = deepCopy(item.component)
                comp.layout.left = item.x
                comp.layout.top = item.y
                comp.layout.width = item.w
                comp.layout.height = item.h
                comp.queryParameters = []
                componentList.push(comp)
            })
            pageDefinition.pageComponents = JSON.stringify(componentList)

            savePageDefinition(this.pageId, pageDefinition).then(resp => {
                this.$message.success('保存成功')
                this.loadData()
            })
        },
        getComponentById(compId) {
            for (let i = 0; i < this.layout.length; i++) {
                const component = this.layout[i].component
                if (component.id === compId) {
                    return this.layout[i].component
                }
            }
            return null
        },
        updateComponentById(newVal) {
            const compId = newVal.id
            for (let i = 0; i < this.layout.length; i++) {
                const component = this.layout[i].component
                if (component.id === compId) {
                    this.layout[i].component = newVal
                    this.$set(this.layout, i, this.layout[i])
                    console.log('component data changed', newVal)
                    break
                } else if (component.type === 'tabs-panel') {
                    const tabPanel = component
                    if (!tabPanel.children || !Array.isArray(tabPanel.children)) {
                        continue
                    }
                    for (let j = 0; j < tabPanel.children.length; j++) {
                        const tabPanelComponent = tabPanel.children[j]
                        if (tabPanelComponent.id === compId) {
                            tabPanel.children[j] = newVal
                            this.$set(tabPanel.children, j, tabPanel.children[j])
                            console.log('component data changed', newVal)
                            break
                        }
                    }
                }
            }


            if (this.selectedComponent && this.selectedComponent.id === compId) {
                this.selectedComponent = newVal
            }
            this.dirty = true
        },
        deleteComponentById(compId) {
            this.layout = this.layout.filter(item => { return item.i !== compId })
        },
        componentDataChange(comp) {
            this.updateComponentById(comp)
            const componnents = this.$refs.componentWrapper || []
            componnents.forEach(cp => {
                if (cp.component.id === comp.id) {
                    cp.onComponentDataReload()
                }
            })
        },
        componentStyleChange(comp) {
            this.updateComponentById(comp)
        },
        showComponetPalette() {
            this.datasetVisible = false
            this.componentPaletteVisible = !this.componentPaletteVisible
            // this.leftSideWidth = this.componentPaletteVisible ? 360 : 60
        },
        showDatasetList() {
            this.componentPaletteVisible = false
            this.datasetVisible = !this.datasetVisible
            // this.leftSideWidth = this.datasetVisible ? 360 : 60
        },
        dragStart(e, item) {
            e.dataTransfer.setData('text/plain', JSON.stringify(item))
        },
        drag: function (item) {
            let parentRect = document.getElementById('main-canvas').getBoundingClientRect();
            let mouseInGrid = false;
            if (((mouseXY.x > parentRect.left) && (mouseXY.x < parentRect.right)) && ((mouseXY.y > parentRect.top) && (mouseXY.y < parentRect.bottom))) {
                mouseInGrid = true;
            }
            let isOverItem = this.isOverLayoutItem()
            if (isOverItem) {
                let dropIndex = this.layout.findIndex(obj => { return obj.i === 'drop' });
                if (dropIndex !== -1) {
                    try {
                        const size = this.$refs.gridlayout.$children.length
                        for (let i = 0; i < size; i++) {
                            const element = this.$refs.gridlayout.$children[i]
                            if (element.i === 'drop') {
                                element.$el.style.display = "none";
                            }
                        }
                    } catch {
                    }
                }
                this.layout = this.layout.filter(obj => { return obj.i !== 'drop' });
                return
            }


            if (mouseInGrid === true && (this.layout.findIndex(obj => { return obj.i === 'drop' })) === -1) {
                this.layout.push({
                    x: (this.layout.length * 2) % (this.pageStyle.layout.columnNums),
                    y: this.layout.length + (this.pageStyle.layout.columnNums), // puts it at the bottom
                    w: 1,
                    h: 1,
                    i: 'drop',
                    component: null
                });
            }
            let index = this.layout.findIndex(obj => { return obj.i === 'drop' });
            if (index !== -1) {
                try {
                    const size = this.$refs.gridlayout.$children.length
                    for (let i = 0; i < size; i++) {
                        const element = this.$refs.gridlayout.$children[i]
                        if (element.i === 'drop') {
                            element.$el.style.display = "block";
                        }
                    }
                } catch {
                }
                let el = this.$refs.gridlayout.$children[index];
                el.dragging = { "top": mouseXY.y - parentRect.top, "left": mouseXY.x - parentRect.left };
                let new_pos = el.calcXY(mouseXY.y - parentRect.top, mouseXY.x - parentRect.left);

                if (mouseInGrid === true) {
                    this.$refs.gridlayout.dragEvent('dragstart', 'drop', new_pos.x, new_pos.y, 1, 1);
                    DragPos.i = String(index);
                    DragPos.x = this.layout[index].x;
                    DragPos.y = this.layout[index].y;
                }
                if (mouseInGrid === false) {
                    this.$refs.gridlayout.dragEvent('dragend', 'drop', new_pos.x, new_pos.y, 1, 1);
                    this.layout = this.layout.filter(obj => { return obj.i !== 'drop' });
                }
            }
        },
        dragend: function (item) {
            let parentRect = document.getElementById('main-canvas').getBoundingClientRect();
            let mouseInGrid = false;
            if (((mouseXY.x > parentRect.left) && (mouseXY.x < parentRect.right)) && ((mouseXY.y > parentRect.top) && (mouseXY.y < parentRect.bottom))) {
                mouseInGrid = true;
            }
            let isOverItem = this.isOverLayoutItem()
            if (isOverItem === true) {
                this.$refs.gridlayout.dragEvent('dragend', 'drop', DragPos.x, DragPos.y, 1, 1);
                this.layout = this.layout.filter(obj => obj.i !== 'drop');
                this.componentPaletteVisible = false
            }
            else if (mouseInGrid === true) {
                //alert(`Dropped element props:\n${JSON.stringify(DragPos, ['x', 'y', 'w', 'h'], 2)}`);
                this.$refs.gridlayout.dragEvent('dragend', 'drop', DragPos.x, DragPos.y, 1, 1);
                this.layout = this.layout.filter(obj => obj.i !== 'drop');

                // UNCOMMENT below if you want to add a grid-item
                this.createComponent('新建视图', item.value, item.render, DragPos.x,
                    DragPos.y,
                    1,
                    1)
                this.componentPaletteVisible = false
            }
        },
        appendNewComponent(item) {
            this.componentPaletteVisible = false 
            //自动计算目前的位置
            let x = 0
            let y = 0
            let maxY = 0
            this.layout.forEach(item => {
                if (item.y > maxY) {
                    maxY = item.y
                }
            }) 
            y = maxY + 1

            this.createComponent('新建视图', item.value, item.render,x ,y, 1, 1)
        },
        createComponent(title, type, render, x, y, w, h) {
            let component = { type: type, render: render, layout: {} }
            // const viewConfig = this.componentTypes.filter(item => item.render === render && item.value === type)
            // if (viewConfig && viewConfig.length > 0) {
            //     component.useDataset = viewConfig[0].dataset || false
            // }

            component.layout.left = x
            component.layout.top = y
            component.layout.width = w
            component.layout.height = h

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
            // 新建的视图应用当前主题
            adaptCurTheme(customStyle, customAttr, type, this.pageStyle)
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
            component.queryParameters = []
            this.setChartDefaultOptions(component)

            getCompoenentId(this.pageId).then(resp => {
                component.id = resp
                this.layout.push({
                    x: x,
                    y: y,
                    w: 1,
                    h: 1,
                    i: component.id,
                    component: component
                });
                this.$refs.gridlayout.dragEvent('dragend', DragPos.i, x, y, 1, 1);
                try {
                    this.$refs.gridlayout.$children[this.layout.length].$refs.item.style.display = "block";
                } catch {
                }

                this.onComponentSelect(component)
                this.dirty = true
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
        onComponentSelect(comp) {
            if (!this.selectedComponent || this.selectedComponent.id !== comp.id) {
                this.selectedComponent = comp
            }
        },
        onCompoenntMoved(compId, newX, newY) {            
            let component = this.getComponentById(compId)
            if (component) {
                component.layout.left = newX
                component.layout.top = newY
                console.log('component'+ component.title +' moved')
                // this.updateComponentById(component)
                this.dirty = true
                const componnents = this.$refs.componentWrapper || []
                componnents.forEach(comp => { comp.onCompoenntMoved(compId, newX, newY) })
            }
        },
        onCompoenntResized(compId, newH, newW, newHPx, newWPx) {
            let component = this.getComponentById(compId)
            if (component) {
                component.layout.width = newW
                component.layout.height = newH
                // this.updateComponentById(component)
                this.dirty = true
                const componnents = this.$refs.componentWrapper || []
                componnents.forEach(comp => { comp.onCompoenntResized(compId, newH, newW, newHPx, newWPx) })

            }
        },
        onComponentDelete(comp) {
            this.selectedComponent = undefined
            this.deleteComponentById(comp.id)
            this.dirty = true
        },
        onPageStyleChange(newVal) {
            this.pageStyle = deepCopy(newVal)
            console.log(this.pageStyle)


            this.dirty = true
        },
        backgroundStyle() {
            if (this.pageStyle.background.backgroundType === 'image') {
                return {
                    backgroundImage: 'url(' + this.pageStyle.background.imageUrl + ')',
                    opacity: this.pageStyle.background.alpha / 100,
                    backgroundPosition: 'center',
                    backgroundRepeat: 'no-repeat',
                    backgroundSize: 'cover',
                }
            } else {
                return { backgroundColor: this.pageStyle.background.color, opacity: this.pageStyle.background.alpha / 100 }
            }
        },
        gridLayoutStyle() {
            if (this.$refs.gridlayout && this.$refs.gridlayout.$el) {
                const size = this.$refs.gridlayout.$el.offsetWidth / this.pageStyle.layout.columnNums
                const size2 = size - 1
                return {
                    height: '100%',
                    '--colNum': this.pageStyle.layout.columnNums,
                    '--rowHeight': this.pageStyle.layout.rowHeight + 'px',
                    '--margin': this.pageStyle.layout.gap / 2 + 'px',
                    '--rowHeightMargin': this.pageStyle.layout.rowHeight + this.pageStyle.layout.gap + 'px'
                    // background:
                    //     `-webkit-linear-gradient(top, transparent ${size2}px, #E6E6E7 ${size}px),-webkit-linear-gradient(left, transparent ${size2}px, #E6E6E7 ${size}px)`,
                    // backgroundSize: `${size}px ${size}10px`
                }
            }

        },
        onPageQuery(pageParameters) {
            console.log('onPageQuery', pageParameters)

            this.queryParameterMap = {}
            for (let j = 0; j < this.layout.length; j++) {
                let component = this.layout[j].component
                this.queryParameterMap[component.id] = []
            }

            for (let i = 0; i < pageParameters.length; i++) {
                let parameter = pageParameters[i] || {}
                if (parameter.conditions && parameter.conditions.length > 0) {
                    if (parameter.autoConfig === 'AUTO') {
                        for (let j = 0; j < this.layout.length; j++) {
                            const component = this.layout[j].component
                            if (component.datasetId === parameter.datasetId && component.type !== 'page-query') {
                                //组件的数据集和参数的数据集相同,则更新组件的参数
                                if (this.queryParameterMap[component.id]) {
                                    let p = deepCopy(parameter)
                                    this.queryParameterMap[component.id].push(p)
                                }
                            }
                        }
                    } else {
                        for (let k = 0; k < parameter.linkConfig.length; k++) {
                            let link = parameter.linkConfig[k]
                            if (link.linkageField && link.linkageField !== '') {
                                for (let j = 0; j < this.layout.length; j++) {
                                    const component = this.layout[j].component
                                    if (component.id === link.chartId) {
                                        //组件的数据集和参数的数据集相同,则更新组件的参数
                                        if (this.queryParameterMap[component.id]) {
                                            let p = deepCopy(parameter)
                                            p.field = link.linkageField || p.field
                                            this.queryParameterMap[component.id].push(p)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (let j = 0; j < this.layout.length; j++) {
                const component = this.layout[j].component
                const dataFilter = this.queryParameterMap[component.id] || []
                if (dataFilter.length >= 0) {
                    const componnents = this.$refs.componentWrapper || []
                    componnents.forEach(cp => {
                        if (cp.component.id === component.id) {
                            this.$nextTick(() => {
                                cp.onComponentDataReload()
                            })
                        }
                    })
                }
            }
        },
        onClose() {
            if (this.dirty) {
                VXETable.modal.confirm({
                    title: '放弃修改页面',
                    content: '「' + this.pageDefinition.name + '」数据已修改，是否放弃修改'
                }).then(type => {
                    if (type === 'confirm') {
                        this.$emit('close')
                    }
                })
            } else {
                this.$emit('close')
            }
        },
        isOverLayoutItem() {
            const size = this.$refs.gridlayout.$children.length
            for (let i = 0; i < size; i++) {
                const element = this.$refs.gridlayout.$children[i]

                let target = element.$el.getBoundingClientRect()
                if (((mouseXY.x > target.left) && (mouseXY.x < target.right)) && ((mouseXY.y > target.top) && (mouseXY.y < target.bottom))) {
                    if (element.i === 'drop') {
                        return false
                    }
                    let comp = this.getComponentById(element.i)
                    return comp && comp.type === 'tabs-panel'
                }
            }
            return false
        },
        onDeleteComponent() {
            VXETable.modal.confirm({
                content: '是否删除组件【'+this.selectedComponent.title+'】',
            }).then((type => {
                if (type == "confirm") {
                    this.deleteComponentById(this.selectedComponent.id)
                    this.selectedComponent = undefined
                    this.dirty = true
                }
            }))
        },
    },
}
</script>

<style lang="less">
.page-editor{
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 100%;
    background-color: #f0f2f5;
}

.top-tool-bar {
    flex: 0;
    width: 100%;
    height: 50px;
    left: 0;
    z-index: 20;
    top: 0;
    background-color: #fff;
    border: solid 1px #f0f2f5;
}

.large-button {
    .button-icon {
        width: 60px;
        height: 34px;
        text-align: center;
        line-height: 1;
        position: relative;
        margin: 16px auto 0px;
    }

    .button-text {
        width: 60px;
        height: 30px;
        text-align: center;
        line-height: 1;
        position: relative;
        margin: 16px auto 0px;
    }
}

.work-area{
    flex: 1 1 auto;
    display: flex;
    flex-direction: row;
    height: calc(100% - 50px);
}

.leftPanel {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    width: 60px;
    height: 100%;
    border: solid 1px #f0f2f5;

    .toolbar {
        float: left;
        width: 60px;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        height: 100%;
        border-right: solid 1px #f0f2f5;
    }


}

.component-palette {
    width: 300px;
    height: 100%;
    margin-right: 0px;

    .palette-title {
        height: 48px;
        font-size: 16px;
        padding: 0 15px;
        color: #0f1726;
        font-weight: 700;
        display: flex;
        align-items: center;
    }

    .component-search {
        width: 280px;
        margin-left: 10px;
        margin-right: 10px;
    }

    .component-box {
        width: 92px;
        height: 114px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        border: 1px solid #eaeaea;

        .component-icon {
            width: 56px;
            height: 56px;
        }

        .component-title {
            font-size: 12px;
            width: 92px;
            height: 15px;
            line-height: 15px;
            text-align: center;
        }
    }

    .ant-collapse-content>.ant-collapse-content-box {
        padding: 0px;
        flex-direction: row;
        flex-wrap: wrap;
        display: flex;
    }

    .ant-collapse>.ant-collapse-item>.ant-collapse-header {
        font-size: 12px;
        font-weight: 500;
        color: #303030;
        padding-top: 4px !important;
        padding-bottom: 4px !important;
        margin-top: 0px;
        padding-left: 16px;
        padding-right: 20px;
        margin-right: 0px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        background-color: #fff;
    }
}

.dataset-list {
    width: 300px;
    height: 100%;
    margin-right: 0px;
    display: flex;
    flex-wrap: nowrap;
    flex-flow: row;
}

.rightPanel {
    display: flex;
    flex-direction: column;
    height: 100%;
    border: solid 1px #f0f2f5;
    padding-left: 5px;
    padding-right: 5px;

    .title-setting {
        height: 50px;
        font-weight: bold;
        display: -webkit-box;
        display: -webkit-flex;
        display: -ms-flexbox;
        display: flex;
        -webkit-box-align: center;
        -webkit-align-items: center;
        -ms-flex-align: center;
        align-items: center;
        padding: 0 16px;
        font-weight: 700;
        border-bottom: 1px solid #e0dbdb;
    }

    .ant-tabs-nav {
        font-size: 12px;
        line-height: 12px;
    }

    .ant-tabs-nav-wrap {
        margin-bottom: -1px;
        overflow: hidden;
        align-items: center;
        margin-left: 50px;
    }

    .ant-tabs .ant-tabs-top-content.ant-tabs-content-animated {
        height: calc(100% - 45px);
    }

}

.edit-canvas-container {
    position: relative;

    .bg-image {
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
    }
    .grid-layout {
        background: transparent;
        overflow-y: auto;
        overflow-x: hidden;
        // height: 100%;
        // background:
        //     -webkit-linear-gradient(top, transparent 9px, #E6E6E7 10px),
        //     -webkit-linear-gradient(left, transparent 9px, #E6E6E7 10px);
        // background-size: 10px 10px;
    }

    // .grid-layout::before {
        // content: '';
        // background-size: calc(calc(100% - var(--margin)) / var(--colNum)) var(--rowHeightMargin);
        // background-image: linear-gradient(to right,
        //         lightgrey 1px,
        //         transparent 1px),
        //     linear-gradient(to bottom, lightgrey 1px, transparent 1px);
        // height: calc(100% - var(--margin));
        // width: calc(100% - var(--margin));
        // position: absolute;
        // background-repeat: repeat;
        // margin: var(--margin);
    // }
}
</style>