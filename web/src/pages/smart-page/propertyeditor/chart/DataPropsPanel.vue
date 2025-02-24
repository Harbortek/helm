<template>
    <div class="prop-container">
        <div class="data-props-panel">
            <div>
                <div class="prop-name"><span>数据集</span></div>
                <div class="prop-value">
                    <a-select v-model="formData.datasetId" @change="onDatasetChange" size="small" style="width: 100%">
                        <a-select-option v-for="item in datasets" :key="item.id" :value="item.id">{{ item.name
                            }}</a-select-option>
                    </a-select>
                </div>
            </div>

            <!--xAxisExt-->
            <div v-if="component.type === 'table-pivot'">
                <div class="prop-name">
                    <div class="data-area-label">
                        <span>{{ $t('chart.table_pivot_row') }}</span>
                        /
                        <span>{{ $t('chart.dimension') }}</span>
                    </div>
                    <a-icon type="delete" class="data-area-clear" @click="clearData('xaxisExt')" />
                </div>
                <div class="prop-value">
                    <draggable v-model="formData.xaxisExt"
                        v-bind="{ group: { name: 'drag', pull: 'clone' }, sort: true }" animation="300" :move="onMove"
                        class="drag-block-style" @add="addXaxis" @update="calcData(true)">
                        <transition-group class="draggable-group">
                            <dimension-ext-item v-for="(item, index) in formData.xaxisExt" :key="item.name"
                                :index="index" :item="item" :dimension-data="formData.xaxis"
                                :quota-data="formData.yaxis" :chart="component"
                                @onDimensionItemChange="dimensionItemExtChange"
                                @onDimensionItemRemove="dimensionItemRemove" @editItemFilter="showDimensionEditFilter"
                                @onNameEdit="showRename" @valueFormatter="valueFormatter"
                                @onCustomSort="onCustomSort" />
                        </transition-group>
                    </draggable>
                    <div v-show="!formData.xaxisExt || formData.xaxisExt.length === 0" class="drag-placeholder-style">
                        <span class="drag-placeholder-style-span">{{ $t('chart.placeholder_field') }}</span>
                    </div>
                </div>
            </div>

            <!--xAxis-->
            <div
                v-if="component.type !== 'indicator-card' && component.type !== 'gauge' && component.type !== 'liquid'">
                <div class="prop-name">
                    <div class="data-area-label">
                        <span v-if="component.type && component.type.includes('table')">{{
                            $t('chart.drag_block_table_data_column')
                        }}</span>
                        <span
                            v-else-if="component.type && (component.type.includes('bar') || component.type.includes('line') || component.type.includes('scatter') || component.type === 'chart-mix' || component.type === 'waterfall' || component.type === 'area')">{{
                                $t('chart.drag_block_type_axis') }}</span>
                        <span v-else-if="component.type && component.type.includes('pie')">{{
                            $t('chart.drag_block_pie_label')
                        }}</span>
                        <span v-else-if="component.type && component.type.includes('funnel')">{{
                            $t('chart.drag_block_funnel_split')
                        }}</span>
                        <span v-else-if="component.type && component.type.includes('radar')">{{
                            $t('chart.drag_block_radar_label')
                        }}</span>
                        <span v-else-if="component.type && component.type === 'map'">{{ $t('chart.area') }}</span>
                        <span v-else-if="component.type && component.type.includes('treemap')">{{
                            $t('chart.drag_block_treemap_label')
                        }}</span>
                        <span v-else-if="component.type && component.type === 'word-cloud'">{{
                            $t('chart.drag_block_word_cloud_label')
                        }}</span>
                        <span v-else-if="component.type && component.type === 'text-card'">{{
                            $t('chart.drag_block_label')
                        }}</span>
                        <span v-else-if="component.type === 'flow-map'">{{ $t('chart.start_point') }}</span>
                        <span v-show="component.type !== 'richTextView'"> / </span>
                        <span
                            v-if="component.type && component.type !== 'table-info' && component.type !== 'indicator-card'">
                            {{ $t('chart.dimension') }}
                        </span>
                        <span v-else-if="component.type && component.type === 'table-info'">{{
                            $t('chart.dimension_or_quota')
                        }}</span>

                    </div>
                    <a-icon type="delete" class="data-area-clear" @click="clearData('xaxis')" />
                </div>
                <div class="prop-value">
                    <draggable v-model="formData.xaxis" v-bind="{ group: { name: 'drag', pull: 'clone' }, sort: true }"
                        animation="300" :move="onMove" class="drag-block-style" @add="addXaxis"
                        @update="calcData(true)">
                        <transition-group class="draggable-group">
                            <dimension-item v-for="(item, index) in formData.xaxis" :key="item.name" :index="index"
                                :item="item" :dimension-data="formData.xaxis" :quota-data="formData.yaxis"
                                :chart="component" @onDimensionItemChange="dimensionItemChange"
                                @onDimensionItemRemove="dimensionItemRemove" @editItemFilter="showDimensionEditFilter"
                                @onNameEdit="showRename" @valueFormatter="valueFormatter"
                                @onCustomSort="onCustomSort" />
                        </transition-group>
                    </draggable>
                    <div v-show="!formData.xaxis || formData.xaxis.length === 0" class="drag-placeholder-style">
                        <span class="drag-placeholder-style-span">{{ $t('chart.placeholder_field') }}</span>
                    </div>
                </div>
            </div>

            <!--group field, use xAxisExt-->
            <div v-if="component.type === 'bar-group'
                || component.type === 'bar-group-stack'
                || (component.render === 'antv' && component.type === 'line')
                || component.type === 'flow-map'">
                <div class="prop-name">
                    <div class="data-area-label">
                        <span>
                            <span v-if="component.type !== 'flow-map'">{{ $t('chart.chart_group') }}</span>
                            <span v-else-if="component.type === 'flow-map'">{{ $t('chart.end_point') }}</span>
                            <span v-show="component.type !== 'line'" style="color:#F54A45;">*</span>
                        </span>
                        /
                        <span>{{ $t('chart.dimension') }}</span>
                        <a-tooltip v-show="component.type !== 'line'" class="item" effect="dark" placement="bottom">
                            <div slot="content">
                                {{ $t('chart.sub_dimension_tip') }}
                            </div>
                            <i class="el-icon-info" style="cursor: pointer;color: #606266;" />
                        </a-tooltip>
                    </div>
                    <a-icon type="delete" class="data-area-clear" @click="clearData('xaxisExt')" />
                </div>
                <div class="prop-value">
                    <draggable v-model="formData.xaxisExt"
                        v-bind="{ group: { name: 'drag', pull: 'clone' }, sort: true }" animation="300" :move="onMove"
                        class="drag-block-style" @add="addXaxis" @update="calcData(true)">
                        <transition-group class="draggable-group">
                            <dimension-item v-for="(item, index) in formData.xaxisExt" :key="item.name" :index="index"
                                :item="item" :dimension-data="formData.xaxis" :quota-data="formData.yaxis"
                                :chart="component" @onDimensionItemChange="dimensionItemExtChange"
                                @onDimensionItemRemove="dimensionItemExtRemove"
                                @editItemFilter="showDimensionEditFilter" @onNameEdit="showRename"
                                @valueFormatter="valueFormatter" @onCustomSort="onCustomSort" />
                        </transition-group>
                    </draggable>
                    <div v-show="!formData.xaxisExt || formData.xaxisExt.length === 0" class="drag-placeholder-style">
                        <span class="drag-placeholder-style-span">{{ $t('chart.placeholder_field') }}</span>
                    </div>
                </div>
            </div>

            <!--yaxis-->
            <div v-if="!equalsAny(component.type, 'table-info', 'text-card', 'flow-map')">
                <div class="prop-name">
                    <div class="data-area-label">
                        <span v-if="component.type && component.type.includes('table')">{{
                            $t('chart.drag_block_table_data_column')
                        }}</span>
                        <span v-else-if="component.type
                            && (includesAny(component.type, 'bar', 'line', 'scatter')
                                || equalsAny(component.type, 'waterfall', 'area', 'flow-map'))">{{
                                    $t('chart.drag_block_value_axis') }}</span>
                        <span v-else-if="component.type && component.type.includes('pie')">{{
                            $t('chart.drag_block_pie_angel')
                        }}</span>
                        <span v-else-if="component.type && component.type.includes('funnel')">{{
                            $t('chart.drag_block_funnel_width')
                        }}</span>
                        <span v-else-if="component.type && component.type.includes('radar')">{{
                            $t('chart.drag_block_radar_length')
                        }}</span>
                        <span v-else-if="component.type && component.type.includes('gauge')">{{
                            $t('chart.drag_block_gauge_angel')
                        }}</span>
                        <span v-else-if="component.type && component.type.includes('indicator-card')">{{
                            $t('chart.drag_block_label_value')
                        }}</span>
                        <span v-else-if="component.type && component.type === 'map'">{{ $t('chart.chart_data') }}</span>
                        <span v-else-if="component.type && component.type.includes('tree')">{{
                            $t('chart.drag_block_treemap_size')
                        }}</span>
                        <span v-else-if="component.type && component.type === 'chart-mix'">{{
                            $t('chart.drag_block_value_axis_main')
                        }}</span>
                        <span v-else-if="component.type && component.type === 'liquid'">{{
                            $t('chart.drag_block_progress')
                        }}</span>
                        <span v-else-if="component.type && component.type === 'word-cloud'">{{
                            $t('chart.drag_block_word_cloud_size')
                        }}</span>
                        <span v-show="component.type !== 'richTextView'"> / </span>
                        <span>{{ $t('chart.quota') }}</span>
                    </div>
                    <a-icon type="delete" class="data-area-clear" @click="clearData('yaxis')" />
                </div>
                <div class="prop-value">
                    <draggable v-model="formData.yaxis" group="drag" animation="300" :move="onMove"
                        class="drag-block-style" @add="addYaxis" @update="calcData(true)">
                        <transition-group class="draggable-group">
                            <quota-item v-for="(item, index) in formData.yaxis" :key="item.name" :index="index"
                                :item="item" :chart="component" :dimension-data="formData.xaxis"
                                :quota-data="formData.yaxis" @onQuotaItemChange="quotaItemChange"
                                @onQuotaItemRemove="quotaItemRemove" @editItemFilter="showQuotaEditFilter"
                                @onNameEdit="showRename" @editItemCompare="showQuotaEditCompare"
                                @valueFormatter="valueFormatter" />
                        </transition-group>
                    </draggable>
                    <div v-if="!formData.yaxis || formData.yaxis.length === 0" class="drag-placeholder-style">
                        <span class="drag-placeholder-style-span">{{ $t('chart.placeholder_field') }}</span>
                    </div>
                </div>
            </div>

            <!--yaxisExt-->
            <div v-if="equalsAny(component.type, 'chart-mix', 'bidirectional-bar')">
                <div class="prop-name">
                    <div class="data-area-label">
                        <span>{{ $t('chart.drag_block_value_axis_ext') }}</span>
                        /
                        <span>{{ $t('chart.quota') }}</span>
                    </div>
                    <a-icon type="delete" class="data-area-clear" @click="clearData('yaxisExt')" />
                </div>
                <div class="prop-value">
                    <draggable v-model="formData.yaxisExt" group="drag" animation="300" :move="onMove"
                        class="drag-block-style" @add="addYaxisExt" @update="calcData(true)">
                        <transition-group class="draggable-group">
                            <quota-ext-item v-for="(item, index) in formData.yaxisExt" :key="item.name" :index="index"
                                :item="item" :chart="component" :dimension-data="formData.xaxis"
                                :quota-data="formData.yaxis" @onQuotaItemChange="quotaItemChange"
                                @onQuotaItemRemove="quotaItemRemove" @editItemFilter="showQuotaEditFilter"
                                @onNameEdit="showRename" @editItemCompare="showQuotaEditCompare"
                                @valueFormatter="valueFormatter" />
                        </transition-group>
                    </draggable>
                    <div v-if="!formData.yaxisExt || formData.yaxisExt.length === 0" class="drag-placeholder-style">
                        <span class="drag-placeholder-style-span">{{ $t('chart.placeholder_field') }}</span>
                    </div>
                </div>
            </div>

            <!--extStack-->
            <div v-if="component.type && component.type.includes('stack')">
                <div class="prop-name">
                    <div class="data-area-label">
                        <span>{{ $t('chart.stack_item') }}</span>
                        /
                        <span>{{ $t('chart.dimension') }}</span>
                    </div>
                    <a-icon type="delete" class="data-area-clear" @click="clearData('extStack')" />
                </div>
                <div class="prop-value">
                    <draggable v-model="formData.extStack" group="drag" animation="300" :move="onMove"
                        class="drag-block-style" @add="addStack" @update="calcData(true)">
                        <transition-group class="draggable-group">
                            <stack-item v-for="(item, index) in formData.extStack" :key="item.name" :index="index"
                                :item="item" :chart="component" :dimension-data="formData.xaxis"
                                :quota-data="formData.yaxis" @onItemChange="stackItemChange"
                                @onItemRemove="stackItemRemove" />
                        </transition-group>
                    </draggable>
                    <div v-if="!formData.extStack || formData.extStack.length === 0" class="drag-placeholder-style">
                        <span class="drag-placeholder-style-span">{{ $t('chart.placeholder_field') }}</span>
                    </div>
                </div>
            </div>

            <!--extBubble-->
            <div v-if="component.type && component.type.includes('scatter')">
                <div class="prop-name">
                    <div class="data-area-label">
                        <span>{{ $t('chart.bubble_size') }}</span>
                        /
                        <span>{{ $t('chart.quota') }}</span>
                        <a-tooltip class="item" effect="dark" placement="bottom">
                            <div slot="content">
                                {{ $t('chart.scatter_tip') }}
                            </div>
                            <i class="el-icon-info" style="cursor: pointer;color: #606266;" />
                        </a-tooltip>
                    </div>
                    <a-icon type="delete" class="data-area-clear" @click="clearData('extBubble')" />
                </div>
                <div class="prop-value">
                    <draggable v-model="formData.extBubble" group="drag" animation="300" :move="onMove"
                        class="drag-block-style" @add="addBubble" @update="calcData(true)">
                        <transition-group class="draggable-group">
                            <bubble-item v-for="(item, index) in formData.extBubble" :key="item.name" :index="index"
                                :item="item" :chart="component" :dimension-data="formData.xaxis"
                                :quota-data="formData.yaxis" @onItemChange="bubbleItemChange"
                                @onItemRemove="bubbleItemRemove" />
                        </transition-group>
                    </draggable>
                    <div v-if="!formData.extBubble || formData.extBubble.length === 0" class="drag-placeholder-style">
                        <span class="drag-placeholder-style-span">{{ $t('chart.placeholder_field') }}</span>
                    </div>
                </div>
            </div>

            <div>
                <div class="prop-name">
                    <div class="data-area-label">
                        <span>{{ $t('chart.result_filter') }}</span>
                    </div>
                    <a-icon type="delete" class="data-area-clear" @click="clearData('customFilter')" />
                </div>
                <div class="prop-value">
                    <draggable v-model="formData.customFilter" group="drag" animation="300" :move="onMove"
                        class="theme-item-class"
                        style="padding:2px 0 0 0;width:100%;min-height: 32px;border-radius: 4px;border: 1px solid #DCDFE6;overflow-x: auto;display: flex;align-items: center;background-color: white;"
                        @add="addCustomFilter" @update="calcData(true)">
                        <transition-group class="draggable-group">
                            <filter-item v-for="(item, index) in formData.customFilter" :key="item.name"
                                :chart="component" :index="index" :item="item" :dimension-data="formData.xaxis"
                                :quota-data="formData.yaxis" @onFilterItemRemove="filterItemRemove"
                                @editItemFilter="showEditFilter" />
                        </transition-group>
                    </draggable>
                    <div v-if="!formData.customFilter || formData.customFilter.length === 0"
                        class="drag-placeholder-style">
                        <span class="drag-placeholder-style-span">{{ $t('chart.placeholder_field') }}</span>
                    </div>
                </div>

            </div>
        </div>



        <div class="field-list-container">
            <div class="prop-name">
                <span class="data-area-label">数据集字段
                </span>
            </div>

            <div class="field-list" style="">
                <draggable v-model="fields" v-bind="{ group: { name: 'drag', pull: 'clone' }, sort: false }"
                    animation="300" :move="onMove" class="drag-list">
                    <transition-group>
                        <div class="field-item" :key="f.name" v-for="f in fields">
                            <a-icon component="field_type_text" style="color: #222222;" v-show="f.type === 'TEXT'" />
                            <a-icon component="field_type_number" style="color: cadetblue;" v-show="f.type === 'NUM'" />
                            <a-icon component="date" style="color:cadetblue;" v-show="f.type === 'DATE'" />
                            {{ f.title }}
                        </div>
                    </transition-group>
                </draggable>
            </div>
        </div>
        <div class="refresh-chart">
            <a-button type="primary" @click="calcData(true)" style="width: 100%;font-size: 12px;">刷新</a-button>
        </div>

        <input-name-dialog :isShowDialog="renameItem" :name="itemForm.title" @ok="saveRename" @cancel="closeRename" />
        <value-format-dialog :isShowDialog="showValueFormatter" :formatterItem="valueFormatterItem"
            @cancel="closeValueFormatter" @ok="saveValueFormatter" />
        <result-filter-editor :isShowDialog="resultFilterEdit" :pageId="pageId" :item="filterItem"
            :dataset="getDataset()" @ok="saveResultFilter" @cancel="closeResultFilter" />
    </div>
</template>

<script>
import draggable from 'vuedraggable'
import { equalsAny, includesAny } from '@/utils/StringUtils'
import { findDatasetsByPageId, getSQLPreview } from '@/services/smart-page/DatasetService.js'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import DimensionItem from '@/pages/smart-page/propertyeditor/dragItem/DimensionItem.vue'
import DimensionExtItem from '@/pages/smart-page/propertyeditor/dragItem/DimensionExtItem.vue'
import QuotaItem from '@/pages/smart-page/propertyeditor/dragItem/QuotaItem.vue'
import QuotaExtItem from '@/pages/smart-page/propertyeditor/dragItem/QuotaExtItem.vue'
import StackItem from '@/pages/smart-page/propertyeditor/dragItem/StackItem.vue'
import BubbleItem from '@/pages/smart-page/propertyeditor/dragItem/BubbleItem.vue'
import FilterItem from '@/pages/smart-page/propertyeditor/dragItem/FilterItem.vue'
import ResultFilterEditor from '../filter/ResultFilterEditor'
import QuotaFilterEditor from '../filter/QuotaFilterEditor'
import DimensionFilterEditor from '../filter/DimensionFilterEditor'

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
import InputNameDialog from '@/components/dialog/InputNameDialog.vue'
import ValueFormatDialog from '@/pages/smart-page/editor/ValueFormatDialog.vue'
export default {
    name: 'DataPropsPanel',
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
        DimensionItem, DimensionExtItem, QuotaItem, QuotaExtItem, StackItem, BubbleItem, FilterItem, draggable, InputNameDialog, ValueFormatDialog, ResultFilterEditor, QuotaFilterEditor, DimensionFilterEditor
    },
    data() {
        return {
            datasets: [],
            fields: [],
            formData: {
                datasetId: '',
                xaxis: [],
                xaxisExt: [],
                yaxis: [],
                yaxisExt: [],
                extStack: [],
                drillFields: [],
                viewFields: [],
                extBubble: [],
                show: true,
                type: 'bar',
                title: '',
                refreshViewEnable: false,
                refreshUnit: 'minute',
                refreshTime: 5,
                customAttr: {
                    color: DEFAULT_COLOR_CASE,
                    size: DEFAULT_SIZE,
                    label: DEFAULT_LABEL,
                    tooltip: DEFAULT_TOOLTIP,
                    totalCfg: DEFAULT_TOTAL
                },
                customStyle: {
                    text: DEFAULT_TITLE_STYLE,
                    legend: DEFAULT_LEGEND_STYLE,
                    xAxis: DEFAULT_XAXIS_STYLE,
                    yAxis: DEFAULT_YAXIS_STYLE,
                    yAxisExt: DEFAULT_YAXIS_EXT_STYLE,
                    split: DEFAULT_SPLIT
                },
                senior: {
                    functionCfg: DEFAULT_FUNCTION_CFG,
                    assistLine: [],
                    threshold: DEFAULT_THRESHOLD
                },
                customFilter: [],
                render: 'antv',
                isPlugin: false
            },
            moveId: -1,
            renameItem: false,
            itemForm: {
                title: ''
            },
            showValueFormatter: false,
            valueFormatterItem: {},
            resultFilterEdit: false,
            filterItem: {},
        }
    },
    computed: {
    },
    watch: {
        component: function (newVal, oldVale) {
            this.prepareFormData(newVal)
        }
    },
    mounted() {
        findDatasetsByPageId(this.pageId).then(resp => {
            this.datasets = resp || []

            this.prepareFormData(this.component)
        })
    },
    methods: {
        includesAny,
        equalsAny,
        emitDataChange() {
            let comp = deepCopy(this.formData)
            comp.xaxis = JSON.stringify(this.formData.xaxis)
            comp.xaxisExt = JSON.stringify(this.formData.xaxisExt)
            comp.yaxis = JSON.stringify(this.formData.yaxis)
            comp.yaxisExt = JSON.stringify(this.formData.yaxisExt)
            comp.extStack = JSON.stringify(this.formData.extStack)
            comp.customFilter = JSON.stringify(this.formData.customFilter)
            comp.drillFields = JSON.stringify(this.formData.drillFields)
            comp.extBubble = JSON.stringify(this.formData.extBubble)
            comp.viewFields = JSON.stringify(this.formData.viewFields)
            this.$emit('change', comp)
        },
        prepareFormData(newVal) {
            this.formData = deepCopy(newVal)
            this.formData.datasetId = newVal.datasetId || ''
            this.formData.xaxis = JSON.parse(newVal.xaxis)
            this.formData.xaxisExt = JSON.parse(newVal.xaxisExt)
            this.formData.yaxis = JSON.parse(newVal.yaxis)
            this.formData.yaxisExt = JSON.parse(newVal.yaxisExt)
            this.formData.extStack = JSON.parse(newVal.extStack)
            this.formData.customFilter = JSON.parse(newVal.customFilter)
            this.formData.drillFields = JSON.parse(newVal.drillFields)
            this.formData.extBubble = JSON.parse(newVal.extBubble)
            this.formData.viewFields = JSON.parse(newVal.viewFields)
            console.log('formdata', this.formData)
            this.fields = this.getFieldsFromDataset()
        },
        onDatasetChange() {
            this.fields = this.getFieldsFromDataset()
            this.formData.xaxis = []
            this.formData.xaxisExt = []
            this.formData.yaxis = []
            this.formData.yaxisExt = []
            this.formData.extStack = []
            this.formData.customFilter = []
            this.formData.drillFields = []
            this.formData.extBubble = []
            this.formData.viewFields = []
            this.emitDataChange()
        },
        getDataset() {
            if (this.formData.datasetId && this.datasets && this.datasets.length > 0) {
                const datasetId = this.formData.datasetId
                let filterd = this.datasets.find(item => { return item.id === datasetId })
                return filterd ? filterd : {}
            }
            return {};
        },
        getFieldsFromDataset() {
            let filterd = this.getDataset()
            return filterd ? filterd.fields : []
        },
        changeEditStatus(status) {
            this.hasEdit = status
            // this.$store.commit('recordViewEdit', { viewId: this.param.id, hasEdit: status })
        },
        calcData(getData, trigger, needRefreshGroup = false, switchType = false, switchRender = false) {
            this.changeEditStatus(true)
            this.emitDataChange()
        },
        // calcStyle(modifyName) {
        //     this.changeEditStatus(true)
        //     // 将视图传入echart...组件
        //     const view = JSON.parse(JSON.stringify(this.formData))
        //     view.xaxis = JSON.stringify(this.view.xaxis)
        //     view.viewFields = JSON.stringify(this.view.viewFields)
        //     view.xaxisExt = JSON.stringify(this.view.xaxisExt)
        //     view.yaxis = JSON.stringify(this.view.yaxis)
        //     view.yaxisExt = JSON.stringify(this.view.yaxisExt)
        //     view.extStack = JSON.stringify(this.view.extStack)
        //     view.drillFields = JSON.stringify(this.view.drillFields)
        //     view.extBubble = JSON.stringify(this.view.extBubble)
        //     view.customAttr = JSON.stringify(this.view.customAttr)
        //     view.customStyle = JSON.stringify(this.view.customStyle)
        //     view.customFilter = JSON.stringify(this.view.customFilter)
        //     view.senior = JSON.stringify(this.view.senior)
        //     view.title = this.view.title
        //     view.stylePriority = this.view.stylePriority
        //     // view.data = this.data
        //     this.chart = view

        //     // 保存到缓存表
        //     const viewSave = this.buildParam(true, 'chart', false, false)
        //     if (!viewSave) return
        //     viewEditSave(this.panelInfo.id, viewSave)

        //     if (modifyName === 'color') {
        //         bus.$emit('view-in-cache', {
        //             type: 'styleChange',
        //             viewId: this.view.id,
        //             viewInfo: view,
        //             refreshProp: 'customAttr'
        //         })
        //     } else {
        //         bus.$emit('view-in-cache', { type: 'styleChange', viewId: this.view.id, viewInfo: view })
        //     }
        // },
        // move回调方法,返回false禁止拖动
        onMove(e, originalEvent) {
            // console.log(e)
            // if (e.to.parentNode.className === 'drag-list') {
            //     return false
            // }
            this.moveId = e.draggedContext.element.name
            return true
        },

        onCustomSort(item) {
            this.customSortField = this.component.xaxis[item.index]
            // this.customSort()
        },

        dimensionItemChange(item) {
            this.formData.xaxis.splice(item.index, 1, item)
            // this.$set(this.formData.xaxis, item.index, item)
            console.log(this.formData.xaxis)
            this.calcData(true)
        },

        dimensionItemExtChange(item) {
            this.formData.xaxisExt.splice(item.index, 1, item)
            this.calcData(true)
        },

        dimensionItemRemove(item) {
            if (item.removeType === 'dimension') {
                this.formData.xaxis.splice(item.index, 1)
            } else if (item.removeType === 'dimensionExt') {
                this.formData.xaxisExt.splice(item.index, 1)
            }
            this.calcData(true)
        },
        dimensionItemExtRemove(item) {
            this.formData.xaxisExt.splice(item.index, 1)
            this.calcData(true)
        },

        quotaItemChange(item) {
            this.formData.yaxis.splice(item.index, 1, item)
            console.log(this.formData.yaxis)
            this.calcData(true)
        },

        quotaItemRemove(item) {
            if (item.removeType === 'quota') {
                this.formData.yaxis.splice(item.index, 1)
            } else if (item.removeType === 'quotaExt') {
                this.formData.yaxisExt.splice(item.index, 1)
            }
            this.calcData(true)
        },
        dragCheckType(list, type) {
            // if (list && list.length > 0) {
            //     for (let i = 0; i < list.length; i++) {
            //         if (list[i].groupType !== type) {
            //             list.splice(i, 1)
            //         }
            //     }
            // }
        },
        dragMoveDuplicate(list, e, mode) {
            if (mode === 'ds') {
                list.splice(e.newDraggableIndex, 1)
            } else {
                const that = this
                const dup = list.filter(function (m) {
                    return m.name === that.moveId
                })
                if (dup && dup.length > 1) {
                    list.splice(e.newDraggableIndex, 1)
                }
            }
        },
        dragRemoveChartField(list, e) {
            const that = this
            const dup = list.filter(function (m) {
                return m.id === that.moveId
            })
            if (dup && dup.length > 0) {
                if (dup[0].chartId) {
                    list.splice(e.newDraggableIndex, 1)
                }
            }
        },
        addXaxis(e) {
            if (this.formData.type !== 'table-info') {
                this.dragCheckType(this.formData.xaxis, 'd')
            }
            this.dragMoveDuplicate(this.formData.xaxis, e)
            if ((this.formData.type === 'map' || this.formData.type === 'word-cloud' || this.formData.type === 'text-card') && this.formData.xaxis.length > 1) {
                this.formData.xaxis = [this.formData.xaxis[0]]
            }
            this.calcData(true)
        },
        addXaxisExt(e) {
            if (this.formData.type !== 'table-info') {
                this.dragCheckType(this.formData.xaxisExt, 'd')
            }
            this.dragMoveDuplicate(this.formData.xaxisExt, e)
            if ((this.formData.type === 'map' || this.formData.type === 'word-cloud') && this.formData.xaxisExt.length > 1) {
                this.formData.xaxisExt = [this.formData.xaxisExt[0]]
            }
            this.calcData(true)
        },
        addYaxis(e) {
            this.dragCheckType(this.formData.yaxis, 'q')
            this.dragMoveDuplicate(this.formData.yaxis, e)
            if ((equalsAny(this.formData.type, 'waterfall', 'word-cloud', 'bidirectional-bar') || this.formData.type.includes('group')) && this.formData.yaxis.length > 1) {
                this.formData.yaxis = [this.formData.yaxis[0]]
            }
            this.calcData(true)
        },
        addYaxisExt(e) {
            this.dragCheckType(this.formData.yaxisExt, 'q')
            this.dragMoveDuplicate(this.formData.yaxisExt, e)
            if (equalsAny(this.formData.type, 'map', 'bidirectional-bar') && this.formData.yaxisExt.length > 1) {
                this.formData.yaxisExt = [this.formData.yaxisExt[0]]
            }
            this.calcData(true)
        },
        addStack(e) {
            this.dragCheckType(this.formData.extStack, 'd')
            if (this.formData.extStack && this.formData.extStack.length > 1) {
                this.formData.extStack = [this.formData.extStack[0]]
            }
            this.calcData(true)
        },
        stackItemChange(item) {
            this.calcData(true)
        },
        stackItemRemove(item) {
            this.formData.extStack.splice(item.index, 1)
            this.calcData(true)
        },
        addBubble(e) {
            this.dragCheckType(this.formData.extBubble, 'q')
            if (this.formData.extBubble && this.formData.extBubble.length > 1) {
                this.formData.extBubble = [this.formData.extBubble[0]]
            }
            this.calcData(true)
        },
        bubbleItemChange(item) {
            this.calcData(true)
        },
        bubbleItemRemove(item) {
            this.formData.extBubble.splice(item.index, 1)
            this.calcData(true)
        },

        showDimensionEditFilter(item) {
            this.dimensionItem = JSON.parse(JSON.stringify(item))
            this.dimensionFilterEdit = true
        },
        closeDimensionFilter() {
            this.dimensionFilterEdit = false
        },
        saveDimensionFilter() {
            for (let i = 0; i < this.dimensionItem.filter.length; i++) {
                const f = this.dimensionItem.filter[i]
                if (!f.term.includes('null') && !f.term.includes('empty') && (!f.value || f.value === '')) {
                    this.$message({
                        message: this.$t('chart.filter_value_can_null'),
                        type: 'error',
                        showClose: true
                    })
                    return
                }
            }
            this.formData.xaxis[this.dimensionItem.index].filter = this.dimensionItem.filter
            this.calcData(true)
            this.closeDimensionFilter()
        },
        showQuotaEditCompare(item) {
            this.quotaItemCompare = JSON.parse(JSON.stringify(item))
            this.showEditQuotaCompare = true
        },
        closeQuotaEditCompare() {
            this.showEditQuotaCompare = false
        },
        saveQuotaEditCompare() {
            // 更新指标
            if (this.quotaItemCompare.calcType === 'quota') {
                this.formData.yaxis[this.quotaItemCompare.index].compareCalc = this.quotaItemCompare.compareCalc
            } else if (this.quotaItemCompare.calcType === 'quotaExt') {
                this.formData.yaxisExt[this.quotaItemCompare.index].compareCalc = this.quotaItemCompare.compareCalc
            }
            this.calcData(true)
            this.closeQuotaEditCompare()
        },

        showQuotaEditFilter(item) {
            this.quotaItem = JSON.parse(JSON.stringify(item))
            if (!this.quotaItem.logic) {
                this.quotaItem.logic = 'and'
            }
            this.quotaFilterEdit = true
        },
        closeQuotaFilter() {
            this.quotaFilterEdit = false
        },
        saveQuotaFilter() {
            for (let i = 0; i < this.quotaItem.filter.length; i++) {
                const f = this.quotaItem.filter[i]
                if (!f.term.includes('null') && !f.term.includes('empty') && (!f.value || f.value === '')) {
                    this.$message({
                        message: this.$t('chart.filter_value_can_null'),
                        type: 'error',
                        showClose: true
                    })
                    return
                }
                if (isNaN(f.value)) {
                    this.$message({
                        message: this.$t('chart.filter_value_can_not_str'),
                        type: 'error',
                        showClose: true
                    })
                    return
                }
            }
            if (this.quotaItem.filterType === 'quota') {
                this.formData.yaxis[this.quotaItem.index].filter = this.quotaItem.filter
                this.formData.yaxis[this.quotaItem.index].logic = this.quotaItem.logic
            } else if (this.quotaItem.filterType === 'quotaExt') {
                this.formData.yaxisExt[this.quotaItem.index].filter = this.quotaItem.filter
                this.formData.yaxisExt[this.quotaItem.index].logic = this.quotaItem.logic
            }
            this.calcData(true)
            this.closeQuotaFilter()
        },
        showRename(val) {
            this.itemForm = JSON.parse(JSON.stringify(val))
            if (!this.itemForm.title || this.itemForm.title === '') {
                this.itemForm.title = this.itemForm.name
            }
            this.renameItem = true
        },
        saveRename(newValue) {
            this.itemForm.title = newValue
            if (this.itemForm.renameType === 'quota') {
                this.formData.yaxis[this.itemForm.index].title = this.itemForm.title
                this.$set(this.formData.yaxis, this.itemForm.index, deepCopy(this.formData.yaxis[this.itemForm.index]))
            } else if (this.itemForm.renameType === 'dimension') {
                this.formData.xaxis[this.itemForm.index].title = this.itemForm.title
                this.$set(this.formData.xaxis, this.itemForm.index, deepCopy(this.formData.xaxis[this.itemForm.index]))
            } else if (this.itemForm.renameType === 'quotaExt') {
                this.formData.yaxisExt[this.itemForm.index].title = this.itemForm.title
                this.$set(this.formData.yaxisExt, this.itemForm.index, deepCopy(this.formData.yaxisExt[this.itemForm.index]))
            } else if (this.itemForm.renameType === 'dimensionExt') {
                this.formData.xaxisExt[this.itemForm.index].title = this.itemForm.title
                this.$set(this.formData.xaxisExt, this.itemForm.index, deepCopy(this.formData.xaxisExt[this.itemForm.index]))
            }
            this.calcData(true)
            this.closeRename()

        },
        closeRename() {
            this.renameItem = false
            this.resetRename()
        },
        resetRename() {
            // this.itemForm = {}
        },
        valueFormatter(item) {
            this.valueFormatterItem = JSON.parse(JSON.stringify(item))
            this.showValueFormatter = true
        },
        closeValueFormatter() {
            this.showValueFormatter = false
        },
        saveValueFormatter(formatterCfg) {
            const ele = formatterCfg.decimalCount
            if (ele === undefined || ele.toString().indexOf('.') > -1 || parseInt(ele).toString() === 'NaN' || parseInt(ele) < 0 || parseInt(ele) > 10) {
                this.$message({
                    message: this.$t('chart.formatter_decimal_count_error'),
                    type: 'error',
                    showClose: true
                })
                return
            }
            // 更新指标
            if (this.valueFormatterItem.formatterType === 'quota') {
                this.formData.yaxis[this.valueFormatterItem.index].formatterCfg = formatterCfg
            } else if (this.valueFormatterItem.formatterType === 'quotaExt') {
                this.formData.yaxisExt[this.valueFormatterItem.index].formatterCfg = formatterCfg
            } else if (this.valueFormatterItem.formatterType === 'dimension') {
                this.formData.xaxis[this.valueFormatterItem.index].formatterCfg = formatterCfg
            }
            this.calcData(true)
            this.closeValueFormatter()
        },
        addCustomFilter(e) {
            // 记录数等自动生成字段不做为过滤条件
            if (this.formData.customFilter && this.formData.customFilter.length > 0) {
                for (let i = 0; i < this.formData.customFilter.length; i++) {
                    if (this.formData.customFilter[i].id === 'count') {
                        this.formData.customFilter.splice(i, 1)
                    }
                }
            }
            this.formData.customFilter[e.newDraggableIndex].filter = []
            this.dragMoveDuplicate(this.formData.customFilter, e)
            this.dragRemoveChartField(this.formData.customFilter, e)
            this.calcData(true)
        },
        filterItemRemove(item) {
            this.formData.customFilter.splice(item.index, 1)
            this.calcData(true)
        },
        showEditFilter(item) {
            this.filterItem = JSON.parse(JSON.stringify(item))
            this.chartForFilter = JSON.parse(JSON.stringify(this.formData))
            if (!this.filterItem.logic) {
                this.filterItem.logic = 'and'
            }
            if (!this.filterItem.filterType) {
                this.filterItem.filterType = 'logic'
            }
            if (!this.filterItem.enumCheckField) {
                this.filterItem.enumCheckField = []
            }
            this.resultFilterEdit = true
        },
        closeResultFilter() {
            this.resultFilterEdit = false
        },
        saveResultFilter(filterItem) {
            console.log("filterItem", filterItem)
            if (((this.filterItem.type === 'TEXT') && this.filterItem.filterType !== 'enum') ||
                this.filterItem.type === 'NUM' ||
                this.filterItem.type === 'DATE') {
                for (let i = 0; i < this.filterItem.filter.length; i++) {
                    const f = this.filterItem.filter[i]
                    if (!f.term.includes('null') && !f.term.includes('empty') && (!f.value || f.value === '')) {
                        this.$message({
                            message: this.$t('chart.filter_value_can_null'),
                            type: 'error',
                            showClose: true
                        })
                        return
                    }
                    if (this.filterItem.deType === 2 || this.filterItem.deType === 3) {
                        if (isNaN(f.value)) {
                            this.$message({
                                message: this.$t('chart.filter_value_can_not_str'),
                                type: 'error',
                                showClose: true
                            })
                            return
                        }
                    }
                }
            }

            // this.formData.customFilter[this.filterItem.index].filter = this.filterItem.filter
            // this.formData.customFilter[this.filterItem.index].logic = this.filterItem.logic
            // this.formData.customFilter[this.filterItem.index].filterType = this.filterItem.filterType
            // this.formData.customFilter[this.filterItem.index].enumCheckField = this.filterItem.enumCheckField
            this.formData.customFilter[this.filterItem.index].filterMethod = this.filterItem.filterMethod
            this.formData.customFilter[this.filterItem.index].queryMethod = this.filterItem.queryMethod
            this.formData.customFilter[this.filterItem.index].conditionMethod = this.filterItem.conditionMethod
            this.formData.customFilter[this.filterItem.index].conditions = this.filterItem.conditions
            this.formData.customFilter[this.filterItem.index].rangeType = this.filterItem.rangeType
            this.calcData(true)
            this.closeResultFilter()
        },
    },
}
</script>

<style lang="less" scoped>
.prop-container {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.data-props-panel {
    flex: 0;
    width: 100%;
    padding-left: 10px;
    padding-right: 10px;

    .prop-name {
        width: 100%;
        height: 22px;
        margin-top: 15px;
        font-size: 12px;
        line-height: 12px;
        text-align: left;
        position: relative;
        width: 100%;
        display: inline-flex;

        .data-area-label {
            flex: 1 1 auto;
            font-weight: bold;

            .data-area-clear {
                color: #646a73;
                cursor: pointer;
                margin-top: 2px;
                margin-right: 2px;
            }
        }
    }

    .prop-value {
        width: 100%;
        min-height: 26px;
    }



    .drag-block-style {
        padding: 2px 0 0 0;
        width: 100%;
        min-height: 32px;
        border-radius: 4px;
        border: 1px solid #DCDFE6;
        overflow-x: hidden;
        display: flex;
        align-items: center;
        background-color: white;
    }

    .draggable-group {
        display: block;
        width: 100%;
        height: calc(100% - 6px);
    }

    .drag-placeholder-style {
        position: relative;
        top: -26px;
        left: 0;
        width: 100%;
        color: #CCCCCC;
    }

    .drag-placeholder-style-span {
        padding-left: 16px;
        font-size: 12px;
    }
}

.field-list-container {
    border-top: 1px solid #e0dbdb;
    padding-left: 10px;
    padding-right: 10px;
    padding-top: 10px;
    flex: 1 1 auto;
    overflow: hidden;
    display: flex;
    flex-direction: column;

    .prop-name {
        flex: 0;
        width: 100%;
        height: 22px;
        font-size: 12px;
        line-height: 22px;
        text-align: left;
        position: relative;
        width: 100%;
        display: inline-block;

        .data-area-label {
            flex: 1 1 auto;
            font-weight: bold;

            .data-area-clear {
                color: #646a73;
                cursor: pointer;
                margin-top: 2px;
                margin-right: 2px;
            }
        }
    }

    .field-list {
        flex: 1 1 auto;
        overflow-y: auto;
        display: flex;
        flex-direction: column;
        padding: 2px 0;
        background-color: #f0f2f5;



        .drag-list {
            flex: 1 1 auto;

            .field-item {
                height: 26px;
                background-color: #fff;
                margin: 5px 5px;
                padding: 2px 5px;
                font-size: 12px;

                &:hover {
                    color: #1890ff;
                    border-color: #a3d3ff;
                    cursor: pointer;
                }
            }
        }
    }
}

.refresh-chart {
    height: 50px;
    width: 100%;
    padding-left: 10px;
    padding-right: 10px;
    margin-top: 10px;
    margin-bottom: 10px;
}
</style>