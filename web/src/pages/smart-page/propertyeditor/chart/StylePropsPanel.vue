<template>
    <a-row class="view-panel">
        <div v-if="properties.length === 0" class="no-properties">
            {{ $t('chart.chart_no_properties') }}
        </div>
        <div v-if="properties.length > 0"
            style="overflow:auto;border-right: 1px solid #e6e6e6;height: 100%;width: 100%;padding-right: 0px"
            class="attr-style theme-border-class">
            <a-row v-show="showPropertiesCollapse([
                'color-selector', 'size-selector', 'size-selector-ant-v',
                'label-selector', 'label-selector-ant-v',
                'tooltip-selector', 'tooltip-selector-ant-v',
                'total-cfg', 'suspension-selector'])" class="de-collapse-style">
                <span class="padding-lr">{{ $t('chart.shape_attr') }}</span>
                <a-collapse v-model="attrActiveNames" class="style-collapse" expandIconPosition="right">
                    <a-collapse-panel v-show="showPropertiesCollapse(['color-selector'])" name="color"
                        :header="$t('chart.color')">
                        <color-selector :param="param" class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['color-selector']"
                            @onColorChange="onColorChange($event, 'color-selector')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['size-selector'])" name="size"
                        :header="(component.type && component.type.includes('table')) ? $t('chart.table_config') : $t('chart.size')">
                        <size-selector :param="param" class="attr-selector"
                            :property-inner="propertyInnerAll['size-selector']" :chart="component"
                            @onSizeChange="onSizeChange($event, 'size-selector')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['size-selector-ant-v'])" name="size"
                        :header="(component.type && component.type.includes('table')) ? $t('chart.table_config') : $t('chart.size')">
                        <size-selector-ant-v :param="param" class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['size-selector-ant-v']" :quota-fields="quotaFields"
                            @onSizeChange="onSizeChange($event, 'size-selector-ant-v')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['label-selector', 'label-selector-ant-v'])"
                        name="label" :header="$t('chart.label')">
                        <label-selector v-if="showProperties('label-selector')" :param="param" class="attr-selector"
                            :chart="component" :property-inner="propertyInnerAll['label-selector']"
                            @onLabelChange="onLabelChange($event, 'label-selector')" />
                        <label-selector-ant-v v-else-if="showProperties('label-selector-ant-v')" :param="param"
                            class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['label-selector-ant-v']"
                            @onLabelChange="onLabelChange($event, 'label-selector-ant-v')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['tooltip-selector', 'tooltip-selector-ant-v'])"
                        name="tooltip" :header="$t('chart.tooltip')">
                        <tooltip-selector v-if="showProperties('tooltip-selector')" :param="param" class="attr-selector"
                            :chart="component" :property-inner="propertyInnerAll['tooltip-selector']"
                            @onTooltipChange="onTooltipChange($event, 'tooltip-selector')" />
                        <tooltip-selector-ant-v v-else-if="showProperties('tooltip-selector-ant-v')" :param="param"
                            class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['tooltip-selector-ant-v']"
                            @onTooltipChange="onTooltipChange($event, 'tooltip-selector-ant-v')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['total-cfg'])" name="totalCfg"
                        :header="$t('chart.total_cfg')">
                        <total-cfg :param="param" class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['total-cfg']"
                            @onTotalCfgChange="onTotalCfgChange($event, 'total-cfg')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['suspension-selector'])" name="suspension"
                        :header="$t('chart.suspension')">
                        <suspension-selector :param="param" class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['suspension-selector']"
                            @onSuspensionChange="onSuspensionChange($event, 'suspension-selector')" />
                    </a-collapse-panel>
                </a-collapse>
            </a-row>
            <a-row class="de-collapse-style">
                <span class="padding-lr">{{ $t('chart.module_style') }}</span>
                <a-collapse v-model="styleActiveNames" class="style-collapse" expandIconPosition="right">
                    <a-collapse-panel v-show="showPropertiesCollapse(['x-axis-selector', 'x-axis-selector-ant-v'])"
                        name="xAxis" :header="xAisTitle">
                        <x-axis-selector v-if="showProperties('x-axis-selector')" :param="param" class="attr-selector"
                            :chart="component" :property-inner="propertyInnerAll['x-axis-selector']"
                            @onChangeXAxisForm="onChangeXAxisForm($event, 'x-axis-selector')" />
                        <x-axis-selector-ant-v v-else-if="showProperties('x-axis-selector-ant-v')" :param="param"
                            class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['x-axis-selector-ant-v']"
                            @onChangeXAxisForm="onChangeXAxisForm($event, 'x-axis-selector-ant-v')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['y-axis-selector', 'y-axis-selector-ant-v'])"
                        name="yAxis" :header="yAxisTitle">
                        <y-axis-selector v-if="showProperties('y-axis-selector')" :param="param" class="attr-selector"
                            :chart="component" :property-inner="propertyInnerAll['y-axis-selector']"
                            @onChangeYAxisForm="onChangeYAxisForm($event, 'y-axis-selector')" />
                        <y-axis-selector-ant-v v-else-if="showProperties('y-axis-selector-ant-v')" :param="param"
                            class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['y-axis-selector-ant-v']"
                            @onChangeYAxisForm="onChangeYAxisForm($event, 'y-axis-selector-ant-v')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['y-axis-ext-selector', 'y-axis-ext-selector-ant-v'])"
                        name="yAxisExt" :header="$t('chart.yAxis_ext')">
                        <y-axis-ext-selector v-if="showProperties('y-axis-ext-selector')" :param="param"
                            class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['y-axis-ext-selector']"
                            @onChangeYAxisForm="onChangeYAxisExtForm($event, 'y-axis-ext-selector')" />
                        <y-axis-ext-selector-ant-v v-else-if="showProperties('y-axis-ext-selector-ant-v')" :param="param"
                            class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['y-axis-ext-selector-ant-v']"
                            @onChangeYAxisForm="onChangeYAxisExtForm($event, 'y-axis-ext-selector-ant-v')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['split-selector', 'split-selector-ant-v'])"
                        name="split" :header="$t('chart.split')">
                        <split-selector v-if="showProperties('split-selector')" :param="param" class="attr-selector"
                            :chart="component" :property-inner="propertyInnerAll['split-selector']"
                            @onChangeSplitForm="onChangeSplitForm($event, 'split-selector')" />
                        <split-selector-ant-v v-else-if="showProperties('split-selector-ant-v')" :param="param"
                            class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['split-selector-ant-v']"
                            @onChangeSplitForm="onChangeSplitForm($event, 'split-selector-ant-v')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['title-selector', 'title-selector-ant-v'])"
                        name="title" :header="$t('chart.title')">
                        <title-selector v-if="showProperties('title-selector')" :param="param" class="attr-selector"
                            :chart="component" :property-inner="propertyInnerAll['title-selector']"
                            @onTextChange="onTextChange($event, 'title-selector')" />
                        <title-selector-ant-v v-else-if="showProperties('title-selector-ant-v')" :param="param"
                            class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['title-selector-ant-v']"
                            @onTextChange="onTextChange($event, 'title-selector-ant-v')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['legend-selector', 'legend-selector-ant-v'])"
                        name="legend" :header="$t('chart.legend')">
                        <legend-selector v-if="showProperties('legend-selector')" :param="param" class="attr-selector"
                            :chart="component" :property-inner="propertyInnerAll['legend-selector']"
                            @onLegendChange="onLegendChange($event, 'legend-selector')" />
                        <legend-selector-ant-v v-else-if="showProperties('legend-selector-ant-v')" :param="param"
                            class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['legend-selector-ant-v']"
                            @onLegendChange="onLegendChange($event, 'legend-selector-ant-v')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-if="component.customStyle && component.customStyle.background" name="background"
                        :header="$t('chart.background')">
                        <background-color-selector :param="param" class="attr-selector" :chart="component"
                            @onChangeBackgroundForm="onChangeBackgroundForm($event, 'background-color-selector')" />
                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['margin-selector'])" name="margin"
                        :header="$t('panel.margin')">
                        <margin-selector v-if="showProperties('margin-selector')" :param="param" class="attr-selector"
                            :chart="component" :property-inner="propertyInnerAll['margin-selector']"
                            @onMarginChange="onMarginChange($event, 'margin-selector')" />

                    </a-collapse-panel>
                    <a-collapse-panel v-show="showPropertiesCollapse(['form-selector-ant-v'])" name="form"
                        :header="$t('chart.form')">
                        <form-selector-ant-v v-if="showProperties('form-selector-ant-v')" :param="param"
                            class="attr-selector" :chart="component"
                            :property-inner="propertyInnerAll['form-selector-ant-v']"
                            @onFormChange="onFormChange($event, 'form-selector-ant-v')" />

                    </a-collapse-panel>
                </a-collapse>
            </a-row>

            <!-- <a-row v-show="showPropertiesCollapse(['condition-style-selector']) && markSelectorShow"
                class="de-collapse-style">
                <span class="padding-lr">{{ $t('chart.function_style') }}</span>
                <a-collapse v-model="styleActiveNames" class="style-collapse">
                    <a-collapse-panel v-show="showPropertiesCollapse(['condition-style-selector']) && markSelectorShow"
                        name="conditionStyle" :header="$t('chart.condition_style')">

                        <map-mark-selector :param="param" class="attr-selector" :chart="component" :view="view"
                            :dimension-data="dimensionData" :quota-data="quotaData" @onMarkChange="onMarkChange" />
                    </a-collapse-panel>
                </a-collapse>
            </a-row> -->
        </div>
    </a-row>
</template>

<script>
import ColorSelector from '@/pages/smart-page/propertyeditor/shape/ColorSelector'
import SizeSelector from '@/pages/smart-page/propertyeditor/shape/SizeSelector'
import SizeSelectorAntV from '@/pages/smart-page/propertyeditor/shape/SizeSelectorAntV'
import LabelSelector from '@/pages/smart-page/propertyeditor/shape/LabelSelector'
import LabelSelectorAntV from '@/pages/smart-page/propertyeditor/shape/LabelSelectorAntV'
import TooltipSelector from '@/pages/smart-page/propertyeditor/shape/TooltipSelector'
import TooltipSelectorAntV from '@/pages/smart-page/propertyeditor/shape/TooltipSelectorAntV'
import TotalCfg from '@/pages/smart-page/propertyeditor/shape/TotalCfg'
import XAxisSelector from '@/pages/smart-page/propertyeditor/style/XAxisSelector'
import XAxisSelectorAntV from '@/pages/smart-page/propertyeditor/style/XAxisSelectorAntV'
import YAxisSelector from '@/pages/smart-page/propertyeditor/style/YAxisSelector'
import YAxisSelectorAntV from '@/pages/smart-page/propertyeditor/style/YAxisSelectorAntV'
import YAxisExtSelector from '@/pages/smart-page/propertyeditor/style/YAxisExtSelector'
import YAxisExtSelectorAntV from '@/pages/smart-page/propertyeditor/style/YAxisExtSelectorAntV'
import TitleSelector from '@/pages/smart-page/propertyeditor/style/TitleSelector'
import TitleSelectorAntV from '@/pages/smart-page/propertyeditor/style/TitleSelectorAntV'
import LegendSelector from '@/pages/smart-page/propertyeditor/style/LegendSelector'
import MarginSelector from '@/pages/smart-page/propertyeditor/style/MarginSelector'
import LegendSelectorAntV from '@/pages/smart-page/propertyeditor/style/LegendSelectorAntV'
import BackgroundColorSelector from '@/pages/smart-page/propertyeditor/style/BackgroundColorSelector'
import SplitSelector from '@/pages/smart-page/propertyeditor/style/SplitSelector'
import SplitSelectorAntV from '@/pages/smart-page/propertyeditor/style/SplitSelectorAntV'
import SuspensionSelector from '@/components/suspensionSelector'
import FormSelectorAntV from '../style/FormSelectorAntV.vue'
import { TYPE_CONFIGS } from '@/pages/smart-page/components/componentRegistry'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
export default {
    name: 'StylePropsPanel',
    props: {
        pageId: {
            type: String,
        },
        component: {
            type: Object,
            required: false
        },
        charts: {
            type: Array,
            required: false
        }
    },
    components: {
        SplitSelectorAntV,
        SplitSelector,
        BackgroundColorSelector,
        LegendSelectorAntV,
        LegendSelector,
        TitleSelectorAntV,
        TitleSelector,
        YAxisExtSelectorAntV,
        YAxisExtSelector,
        YAxisSelectorAntV,
        YAxisSelector,
        XAxisSelectorAntV,
        XAxisSelector,
        TotalCfg,
        TooltipSelectorAntV,
        TooltipSelector,
        LabelSelectorAntV,
        LabelSelector,
        SizeSelectorAntV,
        SizeSelector,
        ColorSelector,
        MarginSelector,
        SuspensionSelector,
        FormSelectorAntV,
    },
    data() {
        return {
            allTypes: TYPE_CONFIGS,
            attrActiveNames: [],
            styleActiveNames: [],
            param: {},
            view: {},
        }
    },
    computed: {
        config() {
            const _this = this
            if (_this.component && _this.component.render) {
                const viewConfig = this.allTypes.filter(item => item.render === _this.component.render && item.value === _this.component.type)
                if (viewConfig && viewConfig.length) {
                    return viewConfig[0]
                } else {
                    return null
                }
            } else {
                return null
            }
        },
        properties() {
            return this.config ? this.config.properties : null
        },
        propertyInnerAll() {
            return this.config ? this.config.propertyInner : null
        },
        quotaFields() {
            return JSON.parse(this.component.yaxis) || [];
        },
        markSelectorShow() {
            const hasViewFields = this.component?.viewFields?.length
            if (hasViewFields) {
                let hasx = false
                let hasy = false
                for (let index = 0; index < this.component.viewFields.length; index++) {
                    const element = this.component.viewFields[index]
                    if (element.busiType === 'locationXaxis') {
                        hasx = true
                    }
                    if (element.busiType === 'locationYaxis') {
                        hasy = true
                    }
                    if (hasx && hasy) {
                        break
                    }
                }
                if (hasx && hasy) {
                    return true
                }
            }
            return false
        },
        xAisTitle() {
            if (this.component.type === 'bidirectional-bar') {
                return this.$t('chart.yAxis')
            }
            return this.$t('chart.xAxis')
        },
        yAxisTitle() {
            if (this.component.type === 'bidirectional-bar') {
                return this.$t('chart.xAxis')
            }
            if (this.component.type === 'chart-mix') {
                return this.$t('chart.yAxis_main')
            }
            return this.$t('chart.yAxis')
        },

    },
    watch: {
        component: {
            handler: function (newVal, oldVal) {
                this.loadData()
            },
            immediate: true,
            deep: true,
        }

    },
    mounted() {
    },
    beforeDestroy() {
    },
    methods: {
        loadData() {
            this.view = deepCopy(this.component)
            this.view.viewFields = this.view.viewFields ? JSON.parse(this.view.viewFields) : []
            this.view.xaxis = this.view.xaxis ? JSON.parse(this.view.xaxis) : []
            this.view.xaxisExt = this.view.xaxisExt ? JSON.parse(this.view.xaxisExt) : []
            this.view.yaxis = this.view.yaxis ? JSON.parse(this.view.yaxis) : []
            this.view.yaxisExt = this.view.yaxisExt ? JSON.parse(this.view.yaxisExt) : []
            this.view.extStack = this.view.extStack ? JSON.parse(this.view.extStack) : []
            this.view.drillFields = this.view.drillFields ? JSON.parse(this.view.drillFields) : []
            this.view.extBubble = this.view.extBubble ? JSON.parse(this.view.extBubble) : []
            this.view.customAttr = this.view.customAttr ? JSON.parse(this.view.customAttr) : {}
            this.view.customStyle = this.view.customStyle ? JSON.parse(this.view.customStyle) : {}
            this.view.customFilter = this.view.customFilter ? JSON.parse(this.view.customFilter) : {}
            this.view.senior = this.view.senior ? JSON.parse(this.view.senior) : {}

            console.log(this.config.properties)
        },
        emitStyleChange() {
            const comp = JSON.parse(JSON.stringify(this.view))
            comp.xaxis = JSON.stringify(this.view.xaxis)
            comp.viewFields = JSON.stringify(this.view.viewFields)
            comp.xaxisExt = JSON.stringify(this.view.xaxisExt)
            comp.yaxis = JSON.stringify(this.view.yaxis)
            comp.yaxisExt = JSON.stringify(this.view.yaxisExt)
            comp.extStack = JSON.stringify(this.view.extStack)
            comp.drillFields = JSON.stringify(this.view.drillFields)
            comp.extBubble = JSON.stringify(this.view.extBubble)
            comp.customAttr = JSON.stringify(this.view.customAttr)
            comp.customStyle = JSON.stringify(this.view.customStyle)
            comp.customFilter = JSON.stringify(this.view.customFilter)
            comp.senior = JSON.stringify(this.view.senior)
            comp.title = this.view.title
            comp.stylePriority = this.view.stylePriority
            this.$emit('change', comp)
        },
        showProperties(property) {
            return this.properties && this.properties.length && this.properties.includes(property)
        },
        showPropertiesCollapse(propertiesInfo) {
            let includeCount = 0
            // Property does not support mixed mode
            if (propertiesInfo.includes('no-mix') && this.component.type.includes('mix')) {
                return false
            } else {
                propertiesInfo.forEach(property => {
                    this.properties.includes(property) && includeCount++
                })
                return includeCount > 0
            }
        },
        onColorChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customAttr.color = val
            this.emitStyleChange()
        },
        onSuspensionChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.component.customAttr.suspension = val
            this.emitStyleChange()
        },
        onSizeChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customAttr.size = val
            this.emitStyleChange()
        },
        onLabelChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customAttr.label = val
            this.emitStyleChange()
        },
        onTooltipChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customAttr.tooltip = val
            this.emitStyleChange()
        },
        onTotalCfgChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customAttr.totalCfg = val
            this.emitStyleChange()
        },
        onChangeXAxisForm(val, propertyName) {
            val['propertyName'] = propertyName
            let comp = deepCopy(this.view)
            this.view.customStyle.xAxis = val
            this.emitStyleChange()
        },
        onChangeYAxisForm(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customStyle.yAxis = val
            this.emitStyleChange()
        },
        onChangeYAxisExtForm(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customStyle.yAxisExt = val
            this.emitStyleChange()
        },
        onChangeSplitForm(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customStyle.split = val
            this.emitStyleChange()
        },
        onTextChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customStyle.text = val
            this.view.title = val.title
            this.emitStyleChange()
        },
        onLegendChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customStyle.legend = val
            this.emitStyleChange()
        },
        onMarginChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customStyle.margin = val
            this.emitStyleChange()
        },
        onChangeBackgroundForm(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customStyle.background = val
            this.emitStyleChange()
        },
        onMarkChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customAttr.mark = val
            this.emitStyleChange()
        },
        onFormChange(val, propertyName) {
            val['propertyName'] = propertyName
            this.view.customStyle.form = val
            this.emitStyleChange()
        }
    },
}
</script>

<style lang="less">
.de-collapse-style {
    ::v-deep.a-collapse-item__header {
        height: 34px !important;
        line-height: 34px !important;
        padding: 0 0 0 6px !important;
        font-size: 12px !important;
        font-weight: 400 !important;
    }
}

.padding-lr {
    padding: 0 6px;
}

.col {
    width: 40%;
    flex: 1;
    padding: 10px;
    border: solid 1px #eee;
    border-radius: 5px;
    float: left;
}

.col+.col {
    margin-left: 10px;
}

.view-panel {
    display: flex;
    // height: 100%;
    background-color: #f7f8fa;
    font-size: 12px;

    .ant-collapse-content>.ant-collapse-content-box {
        padding: 0px;
        flex-direction: row;
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

    .ant-collapse-icon-position-right>.ant-collapse-item>.ant-collapse-header {
        font-size: 12px;
    }

    span {
        font-size: 12px;
    }

    .ant-form {
        width: 100%;
    }

    .ant-form-item {
        margin-bottom: 0;
    }

    .ant-form label {
        font-size: 12px;
    }

    // .ant-form-item-label {
    //     display: inline-block;
    //     overflow: hidden;
    //     line-height: 28px;
    //     white-space: nowrap;
    //     text-align: right;
    //     vertical-align: middle;
    // }

    // .ant-form-item-control {
    //     position: relative;
    //     line-height: 28px;
    //     zoom: 1;
    // }

    .ant-select {
        font-size: 12px;
    }

    .ant-select-selection--single {
        position: relative;
        height: 28px;
        font-size: 12px;
        cursor: pointer;
    }

    .ant-select-selection__rendered {
        line-height: 28px;
        font-size: 12px;
    }

    .ant-select-arrow {
        color: #06B0FE;
        font-size: 12px;
        font-weight: 600;
    }

    .ant-select-dropdown {
        font-size: 12px;
    }

    .ant-input {
        font-size: 12px;
    }


    .cpicker {
        margin-top: 5px;
    }

    .ant-input-number {
        font-size: 12px;
        height: 22px;
        line-height: 22px;

        .ant-input-number-input {
            height: 100%;
        }
    }

}

.props-dropdown {
    .ant-select-dropdown-menu {
        font-size: 12px;
    }

    .ant-select-dropdown-menu-item {
        font-size: 12px !important;
    }
}

.blackTheme .view-panel {
    background-color: var(--MainBG);
}





.tab-header ::v-deep .a-tabs__header {
    border-top: solid 1px #eee;
    border-right: solid 1px #eee;
}

.tab-header ::v-deep .a-tabs__item {
    font-size: 12px;
    padding: 0 20px !important;
}

.blackTheme .tab-header ::v-deep .a-tabs__item {
    background-color: var(--MainBG);
}

.tab-header ::v-deep .a-tabs__nav-scroll {
    padding-left: 0 !important;
}

.tab-header ::v-deep .a-tabs__header {
    margin: 0 !important;
}

.tab-header ::v-deep .a-tabs__content {
    height: calc(100% - 40px);
}

.chart-icon {
    width: 20px;
    height: 20px;
}

.a-radio {
    margin: 5px;
}

.a-radio ::v-deep .a-radio__label {
    padding-left: 0;
}

.attr-style {
    height: calc(100vh - 56px - 60px - 40px - 40px);
}

.blackTheme .attr-style {
    color: var(--TextPrimary);
}

.attr-selector {
    width: 100%;
    height: 100%;
    margin: 6px 0;
    padding: 0 4px;
    display: flex;
    align-items: center;
    background-color: white
}

.blackTheme .attr-selector {

    background-color: var(--MainBG)
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

.blackTheme .theme-border-class {
    color: var(--TextPrimary) !important;
    background-color: var(--ContentBG);
}

.blackTheme .padding-lr {
    border-color: var(--TableBorderColor) !important;
}

.blackTheme .theme-item-class {
    background-color: var(--MainBG) !important;
    border-color: var(--TableBorderColor) !important;
}

.icon-class {
    color: #6c6c6c;
}

.blackTheme .icon-class {
    color: #cccccc;
}

.radio-span ::v-deep .a-radio__label {
    margin-left: 4px;
}

.view-title-name {
    display: -moz-inline-box;
    display: inline-block;
    width: 130px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-left: 45px;
}

::v-deep .item-axis {
    width: 128px !important;
}

::v-deep .a-slider__input {
    width: 80px !important;
}

::v-deep .a-input-number--mini {
    width: 100px !important;
}

::v-deep .a-slider__runway.show-input {
    width: 80px !important;
}

.no-senior {
    width: 100%;
    text-align: center;
    font-size: 12px;
    padding-top: 40px;
    overflow: auto;
    border-right: 1px solid #e6e6e6;
    height: 100%;
}

.form-item-slider ::v-deep .a-form-model-item__label {
    font-size: 12px;
    line-height: 38px;
}

.form-item ::v-deep .a-form-model-item__label {
    font-size: 12px;
}

.no-properties {
    width: 100%;
    text-align: center;
    font-size: 12px;
    padding-top: 40px;
    overflow: auto;
    height: 100%;
}
</style>