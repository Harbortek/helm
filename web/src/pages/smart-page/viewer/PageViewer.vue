<template>
    <div style="height: 100%;">
        <div ref="pageViewContainer" class="viewer-content" style="height: 100%;width: 100%;">
            <div style="position: absolute; right:20px;top:25px;z-index: 200;height: 24px;width: 100px;">
                <vxe-button type="text" icon="vxe-icon-setting"
                    @click="onEditPage">编辑</vxe-button>
            </div>
            <div class="bg-image" :style="backgroundStyle()"></div>
            <grid-layout id="main-canvas" ref="gridlayout" :layout.sync="layout" :col-num="pageStyle.layout.columnNums"
                :row-height="pageStyle.layout.rowHeight" :is-draggable="false" :is-resizable="false"
                :is-mirrored="false" :vertical-compact="true" :margin="[pageStyle.layout.gap, pageStyle.layout.gap]"
                :use-css-transforms="true" class="grid-layout" style="position: relative;left: 0;top: 0; height:100%;">

                <grid-item v-for="item in layout" :x="item.x" :y="item.y" :w="item.w" :h="item.h" :i="item.i"
                    :key="item.i">
                    <component-wrapper ref="componentWrapper" v-if="item.component" :pageId="pageId"
                        :component="item.component" :canvasStyleData="pageStyle"
                        :params="queryParameterMap[item.component.id]" :selected="false" :readOnly="true"
                        @pageQuery="onPageQuery" />
                </grid-item>
            </grid-layout>
        </div>
    </div>
</template>

<script>
import { DEFAULT_COMMON_CANVAS_STYLE_STRING } from '@/pages/smart-page/util/panel'
import { loadPageDefinition, savePageDefinition } from '@/services/smart-page/PageDefinitionService'
import { panelDataPrepare } from '@/pages/smart-page/util/canvasUtils'
import { TYPE_CONFIGS } from '@/pages/smart-page/components/componentRegistry'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import VueGridLayout from 'vue-grid-layout';
import ComponentWrapper from '@/pages/smart-page/components/ComponentWrapper';
import { mapState } from "vuex";
export default {
    name: 'PageViewer',
    components: {
        ComponentWrapper,
        GridLayout: VueGridLayout.GridLayout,
        GridItem: VueGridLayout.GridItem
    },
    props: {
        pageId: {
            required: true
        },
    },
    data() {
        return {
            pageStyle: deepCopy(DEFAULT_COMMON_CANVAS_STYLE_STRING),
            componentTypes: [...TYPE_CONFIGS],
            componentTypeMap: {},
            layout: [],
            params: {
                pageParameters: [],
            },
            pageDefinition: { dataScope: {} },
            queryParameterMap: {}
        }
    },
    computed: {
        ...mapState("setting", [, "topTitle"]),
    },
    watch: {
        pageId: {
            handler(v) {
                if (v) {
                    this.loadData()
                }
            }
        },
    },
    mounted() {
    },
    methods: {
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
        onPageQuery(pageParameters) {
            console.log('query parameter changed', pageParameters)

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
        onFullScreen: function () {

        },
        onEditPage: function () {
            this.$emit('edit')
        }
    },
}
</script>

<style lang="less">
.viewer-content {
    background-color: #fff;

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
    }
}
</style>