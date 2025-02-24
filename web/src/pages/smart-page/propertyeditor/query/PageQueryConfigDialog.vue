<template>
    <a-modal v-model="visiable" title="查询条件设置" width="1160px" @ok="onOK" @cancel="onCancel">
        <div class="query-config-content">
            <div class="query-fields-list">
                <vxe-table ref="xTable" :data="tableData" :row-config="{ isCurrent: true, isHover: true }" height="440"
                    border="none" :show-header="true" size="mini" :edit-config="{ trigger: 'manual', mode: 'row' }"
                    @current-change="onCurrentChange">
                    <vxe-column field="name" title="查询条件" header-align="left">
                        <template #default="{ row }">
                            <div class="status-sorting-item">
                                <div class="item-drag"><h-icon type="drag" /></div>
                                <div class="item-text">{{ row.name }}</div>
                            </div>
                        </template>
                    </vxe-column>
                </vxe-table>
            </div>
            <div class="query-field-config">
                <div class="query-config-effect">
                    <span style="margin-right: 10px;">生效模式</span>
                    <a-radio-group v-model="selectItem.autoConfig" name="effect" @change="onChangeAutoConfig" :default-value="'AUTO'" size="small">
                        <a-radio :value="'AUTO'">
                            <span style="font-size: 12px;">自动</span>
                        </a-radio>
                        <a-radio :value="'CUSTOMIZE'">
                            <span style="font-size: 12px;">自定义</span>
                        </a-radio>
                    </a-radio-group>
                </div>
                <div style="position: relative; width: 100%;">
                    <div v-if="selectItem.autoConfig==='AUTO'" class="query-config-effect-mask"></div>
                    <div class="query-config-settings">
                        <div class="query-config-charts">
                            <div class="query-config-charts-header">
                                <span style="height: 24px;line-height: 24px;">关联图表及字段</span>
                                <a-input size="small" style="width: 150px;font-size: 12px;margin-left:15px;"
                                     :placeholder="'共'+linkItems.length+'个图表'">
                                    <a-icon slot="prefix" style="top: 42%;left:10px;" type="search" />
                                </a-input>
                            </div>
                            <a-divider style="margin: 10px 0px 10px -10px;width: 103%;" />
                            <div class="query-config-charts-operator-header">
                                <div style="flex:0 0 150px;margin-bottom: 10px;">
                                    <a-checkbox @change="onSelectAll" v-model="allSelected"><span
                                            style="font-size: 12px;">全选</span></a-checkbox>
                                </div>
                                <div style="flex: 1 1 auto;">
                                    <a-button type="link" @click="onClearSelectFields" size="small" style="float:right;">
                                        <span style="font-size: 12px;">清空选入字段</span></a-button>
                                </div>
                            </div>
                            <div v-for="item in linkItems" :key="item.id" class="query-config-link">
                                <div style="flex:0 0 150px;">
                                    <a-checkbox v-model="item.selected" @change="onChangeSelected(item)" ></a-checkbox>
                                    <span><a-icon :component="item.chartIcon"
                                            style="width:16px;height:16px;font-size:12px;margin-left: 5px;" /></span>
                                    <span style="margin-left: 5px;">{{ item.chartName }}</span>
                                </div>
                                <div style="flex:0 0 90px;margin: auto;padding-bottom: 1px;">
                                    <span>{{ item.datasetName }}</span>
                                </div>
                                <div style="flex: 1 1 auto;">
                                    <a-select :disabled="!item.selected" v-model="item.linkageField" @change="e=>onSelectField(e,item)" style="width: 150px;" size="small">
                                        <a-select-option v-for="option in datasetFields(item.datasetId)"
                                            :value="option.name" :key="option.id">{{ option.name }}</a-select-option>
                                    </a-select>
                                </div>
                            </div>
                        </div>
                        <div class="query-config-search">
                            <div class="query-config-charts-header">
                                <span>查询条件设置</span>
                            </div>
                            <a-form-model v-if="!linkType" layout="horizontal" :model="selectItem.searchConfig" label-width="80px"
                                size="small" class="search-form" :labelCol="{ span: 4 }"
                                :wrapperCol="{ span: 19, offset: 1 }">
                                <a-form-model-item label="展示类型" prop="renderType">
                                    <a-select v-model="selectItem.searchConfig.renderType" @change="onRenderTypeChange" size="small">
                                        <a-select-option :value="'DROPDOWN'"
                                            :disabled="selectItem.type !== 'TEXT'">下拉列表</a-select-option>
                                        <a-select-option :value="'TEXT'"
                                            :disabled="selectItem.type !== 'TEXT'">文本输入框</a-select-option>
                                        <a-select-option :value="'DATE_PICKER'"
                                            :disabled="selectItem.type !== 'DATE'">日期选择</a-select-option>
                                        <a-select-option :value="'INPUT_NUMBER'"
                                            :disabled="selectItem.type !== 'NUM'">数值输入</a-select-option>
                                    </a-select>
                                </a-form-model-item>
                                <!-- 下拉列表 -->
                                <div v-if="selectItem.searchConfig.renderType === 'DROPDOWN'">
                                    <a-form-model-item label="选项值来源" prop="dataSource">
                                        <a-radio-group v-model="selectItem.searchConfig.dropdownConfig.dataSource"
                                            size="small" @change="onDropDataSourceChange()">
                                            <a-radio :value="'AUTO'">自动解析</a-radio>
                                            <a-radio :value="'DATASET'">单个数据集</a-radio>
                                            <a-radio :value="'MANUAL'">手工输入</a-radio>
                                        </a-radio-group>
                                        <div v-if="selectItem.searchConfig.dropdownConfig.dataSource === 'DATASET'"
                                            style="width: 360px;">
                                            <a-row style="line-height: 26px;">
                                                <a-col :span="6"><span class="smallText">数据集</span></a-col>
                                                <a-col :span="12">
                                                    <a-select v-model="selectItem.searchConfig.dropdownConfig.datasetId"
                                                        @change="onDatasetChange" style="width: 320px;" size="small"
                                                        placeholder="请选择数据集" show-search optionFilterProp="children">
                                                        <a-select-option v-for="item in datasets" :key="item.id"
                                                            :value="item.id">{{
                                                            item.name
                                                            }}</a-select-option>
                                                    </a-select>
                                                </a-col>
                                            </a-row>
                                            <a-row style="line-height: 26px;">
                                                <a-col :span="6"><span class="smallText">查询字段</span></a-col>
                                                <a-col :span="12">
                                                    <a-select @change="onSearchFieldChange" placeholder="请选择查询值"
                                                        v-model="selectItem.searchConfig.dropdownConfig.searchField" show-search
                                                        style="width: 150px;line-height: 24px;font-size: 12px;" size="small" >
                                                        <a-select-option
                                                            v-for="option in datasetFields(selectItem.searchConfig.dropdownConfig.datasetId)"
                                                            :value="option.name" :key="option.id">{{ option.name
                                                            }}</a-select-option>
                                                    </a-select>
                                                </a-col>
                                            </a-row>
                                            <a-row style="line-height: 26px;">
                                                <a-col :span="6"><span class="smallText">排序字段</span></a-col>
                                                <a-col :span="12">
                                                    <a-select :placeholder="'请选择排序字段'" allowClear v-model="selectItem.searchConfig.dropdownConfig.sortField"
                                                        style="width: 150px;font-size: 12px;" size="small" show-search>
                                                        <a-select-option
                                                            v-for="option in datasetFields(selectItem.searchConfig.dropdownConfig.datasetId)"
                                                            :value="option.name" :key="option.id">{{ option.name
                                                            }}</a-select-option>
                                                    </a-select>
                                                </a-col>
                                                <a-col style="margin-left: -20px;" :span="6">
                                                    <a-select :placeholder="'请选择'"
                                                        v-model="selectItem.searchConfig.dropdownConfig.sortDirection"
                                                        style="width: 140px;font-size: 12px;" size="small" >
                                                        <a-select-option :value="'ASC'">升序</a-select-option>
                                                        <a-select-option :value="'DESC'">降序</a-select-option>
                                                    </a-select>
                                                </a-col>
                                            </a-row>
                                        </div>
                                        <div v-else-if="selectItem.searchConfig.dropdownConfig.dataSource === 'MANUAL'"
                                            style="width: 360px;">
                                            <ManualInputPopover :options="selectItem.searchConfig.dropdownConfig?.options"
                                                @ok="onOkManual"></ManualInputPopover>
                                            
                                            <div v-if="selectItem.searchConfig.dropdownConfig.options?.length===0"
                                                style="font-size: 12px;display: inline;margin-left: 6px;">未配置</div>
                                            <div v-else style="font-size: 12px;display: inline;margin-left: 4px;color:#338fe5">
                                                已配置<a-button type="link" icon="delete" @click="onClearManual"
                                                    style="color: #5d6396; width: 20px;height: 20px; margin-left: 4px; font-size: 12px;"/>
                                            </div>
                                        </div>
                                    </a-form-model-item>
                                    <a-form-model-item label="查询方式" prop="conditionMethod"
                                        v-if="selectItem.searchConfig.renderType === 'DROPDOWN'">
                                        <a-radio-group v-model="selectItem.searchConfig.dropdownConfig.conditionMethod"
                                            size="small" @change="onConditionMethodChange">
                                            <a-radio :value="'SINGLE'">单选</a-radio>
                                            <a-radio :value="'MULTI'">多选</a-radio>
                                        </a-radio-group>
                                    </a-form-model-item>
                                    <a-form-model-item label="查询时间" prop="searchTrigger"
                                        v-if="selectItem.searchConfig.renderType === 'DROPDOWN' && selectItem.searchConfig.dataSource !== 'MANUAL'">
                                        <a-radio-group v-model="selectItem.searchConfig.dropdownConfig.searchTrigger"
                                            size="small">
                                            <a-radio :value="'ON_CLICK'">点击查询</a-radio>
                                            <a-radio :value="'PRE_LOAD'">预先查询</a-radio>
                                        </a-radio-group>
                                    </a-form-model-item>
                                    <a-form-model-item label="默认值" prop="enableDefault">
                                        <a-checkbox v-model="selectItem.searchConfig.dropdownConfig.enableDefault"
                                            @change="onDefaultChange" size="small">
                                            <span class="smallText">设定筛选默认值</span></a-checkbox>
                                        <!-- <div v-if="selectItem.searchConfig.enableDefault"
                                            style="width: 360px;line-height: 26px;">
                                            <a-select v-model="selectItem.searchConfig.dropdownConfig.defaultConfig.defaultValue"
                                                size="small" style="width:150px;">
                                            </a-select>
                                        </div> -->
                                    </a-form-model-item>
                                    <a-select v-if="selectItem.searchConfig.dropdownConfig?.enableDefault&&
                                            selectItem.searchConfig.dropdownConfig.conditionMethod=='SINGLE'"
                                            placeholder=" 请选择（单选）" class="dropdown-config" showSearch
                                        v-model="selectItem.searchConfig.dropdownConfig.defaultConfig.defaultValue" size="small" style="width:160px;margin-left: 107px;font-size: 12px;">
                                            <a-select-option v-for="opt in fieldOptions" :key="opt.id" :value="opt.text">{{ opt.text }}</a-select-option>
                                    </a-select>
                                    <DropdownPopover v-else-if="selectItem.searchConfig.dropdownConfig?.enableDefault"
                                        :defaultValue="selectItem.searchConfig.dropdownConfig.defaultConfig.defaultValue"
                                        :options="fieldOptions" :excludeMode="selectItem.searchConfig.dropdownConfig.excludeMode"
                                        @ok="onOkDropdownPopover"
                                    ></DropdownPopover>
                                </div>
                                <!-- 文本输入框 -->
                                <div v-if="selectItem.searchConfig.renderType === 'TEXT'">
                                    <a-form-model-item label="条件形式" prop="conditionMethod">
                                        <a-radio-group v-if="selectItem.searchConfig.inputTextConfig?.conditionMethod" 
                                            v-model="selectItem.searchConfig.inputTextConfig.conditionMethod" size="small">
                                            <a-radio :value="'SINGLE'">单条件</a-radio>
                                            <a-radio :value="'OR'">或条件</a-radio>
                                            <a-radio :value="'AND'">且条件</a-radio>
                                        </a-radio-group>
                                    </a-form-model-item>
                                    <a-form-model-item label="默认值" prop="enableDefault">
                                        <a-checkbox
                                            v-model="selectItem.searchConfig.inputTextConfig.enableDefault" 
                                            @change="onDefaultChange" size="small"><span
                                                class="smallText">设定筛选默认值</span></a-checkbox>
                                    </a-form-model-item>
                                    <TextConditionFilter
                                        v-if="selectItem.searchConfig.inputTextConfig?.enableDefault"
                                        :options="textOptions" :showLabel="false"
                                        :conditionMethod="selectItem.searchConfig.inputTextConfig.conditionMethod" 
                                        :conditions="selectItem.searchConfig.inputTextConfig.defaultConfig.defaultValue"
                                        ></TextConditionFilter>
                                </div>
                                <!-- 数字输入框 -->
                                <div v-if="selectItem.searchConfig.renderType === 'INPUT_NUMBER'">
                                    <a-form-model-item label="聚合方式" prop="dataSource">
                                        <a-select v-if="selectItem.searchConfig.inputNumberConfig?.aggregationType" v-model="selectItem.searchConfig.inputNumberConfig.aggregationType">
                                            <a-select-option :value="'SUM'">求和</a-select-option>
                                            <a-select-option :value="'COUNT'">计数</a-select-option>
                                            <a-select-option :value="'AVG'">平均值</a-select-option>
                                            <a-select-option :value="'MAX'">最大值</a-select-option>
                                            <a-select-option :value="'MIN'">最小值</a-select-option>
                                        </a-select>
                                    </a-form-model-item>
                                    <a-form-model-item label="条件形式" prop="conditionMethod">
                                        <a-radio-group v-if="selectItem.searchConfig.inputNumberConfig?.conditionMethod" 
                                            v-model="selectItem.searchConfig.inputNumberConfig.conditionMethod" size="small">
                                            <a-radio :value="'SINGLE'">单条件</a-radio>
                                            <a-radio :value="'OR'">或条件</a-radio>
                                            <a-radio :value="'AND'">且条件</a-radio>
                                        </a-radio-group>
                                    </a-form-model-item>
                                    <a-form-model-item label="默认值" prop="enableDefault">
                                        <a-checkbox
                                            v-model="selectItem.searchConfig.inputNumberConfig.enableDefault" 
                                            @change="onDefaultChange" size="small"><span
                                                class="smallText">设定筛选默认值</span></a-checkbox>
                                    </a-form-model-item>
                                    <NumConditionFilter v-if="selectItem.searchConfig.inputNumberConfig?.enableDefault&&selectItem.searchConfig.inputNumberConfig.defaultConfig?.defaultValue"
                                        :options="valueOptions" :showLabel="false"
                                        :conditionMethod="selectItem.searchConfig.inputNumberConfig.conditionMethod" 
                                        :conditions="selectItem.searchConfig.inputNumberConfig.defaultConfig.defaultValue"></NumConditionFilter>
                                </div>
                                
                                <!-- 日期选择 -->
                                <div v-if="selectItem.searchConfig.renderType === 'DATE_PICKER'">
                                    <a-form-model-item label="时间粒度" prop="dataSource">
                                        <a-select v-model="selectItem.searchConfig.datePickerConfig.dateFormat">
                                            <a-select-option :value="'YYYY-MM-DD'">YYYY-MM-DD</a-select-option>
                                            <a-select-option :value="'YYYY-MM'">YYYY-MM</a-select-option>
                                            <a-select-option :value="'YYYY'">YYYY</a-select-option>
                                        </a-select>
                                    </a-form-model-item>
                                    <!-- <a-form-model-item label="数据来源" prop="dataSource">
                                        <a-radio-group v-model="selectItem.searchConfig.datePickerConfig.dataSource"
                                            size="small">
                                            <a-radio :value="'DEFAULT'">默认日期选框</a-radio>
                                            <a-radio :value="'DATASET'">数据集字段</a-radio>
                                        </a-radio-group>
                                        <div v-if="selectItem.searchConfig.datePickerConfig.dataSource === 'DATASET'">
                                            <a-select style="width: 60%;margin-right: 6px;" v-model="selectItem.searchConfig.datePickerConfig.datasetId" @change="onDatasetChange">
                                                <a-select-option v-for="field in datasets" :key="field.id" :value="field.id">{{ field.name }}</a-select-option>
                                            </a-select>
                                            <a-select style="width: 35%" v-model="selectItem.searchConfig.datePickerConfig.searchField">
                                                <a-select-option v-for="field in datasetFields(selectItem.searchConfig.datePickerConfig.datasetId)" :key="field.field" :value="field.field">{{ field.name }}</a-select-option>
                                            </a-select>
                                        </div>
                                    </a-form-model-item> -->
                                    <a-form-model-item label="筛选方式" prop="conditionMethod">
                                        <a-radio-group v-model="selectItem.searchConfig.datePickerConfig.conditionMethod"
                                            size="small">
                                            <a-radio :value="'SINGLE_DATE'">{{dateConditionMethod('SINGLE_DATE')}}</a-radio>
                                            <a-radio :value="'DATE_RANGE'">{{dateConditionMethod('DATE_RANGE')}}</a-radio>
                                        </a-radio-group>
                                    </a-form-model-item>
                                    <a-form-model-item label="区间类型" prop="rangeType">
                                        <a-radio-group v-model="selectItem.searchConfig.datePickerConfig.rangeType"
                                            size="small" :defaultValue="'START_WITH'" @change="onRangeTypeChange">
                                            <a-radio :value="'START_WITH'">开始于</a-radio>
                                            <a-radio :value="'END_WITH'">结束于</a-radio>
                                            <a-radio :value="'BETWEEN'">时间区间</a-radio>
                                            <a-radio :value="'QUICK'">快捷区间</a-radio>
                                        </a-radio-group>
                                    </a-form-model-item>

                                    <!-- 快捷区间 -->
                                    <div v-if="selectItem.searchConfig.datePickerConfig.rangeType==='QUICK'" class="quick-range">
                                        <a-popover v-model="quickVisible" trigger="click" placement="bottom">
                                            <template slot="content">
                                                <div style="width: 100px;height: 200px;">
                                                    <a-checkbox-group v-model="selectItem.searchConfig.datePickerConfig.quickConfig" size="small">
                                                        <a-checkbox v-for="quick in quickTextOptions" :key="quick.value" :value="quick.value" style="margin-left:8px;margin-top: 4px;">{{ quick.label }}</a-checkbox>
                                                    </a-checkbox-group>
                                                </div>
                                            </template>
                                            <a-button style="font-size: 12px;margin-left: 110px;" :size="'small'" icon="tool"> 定制快捷区间 </a-button>
                                        </a-popover>
                                        <div v-if="selectItem.searchConfig.datePickerConfig.quickConfig.length===0"
                                            style="font-size: 12px;display: inline;margin-left: 4px;">未配置</div>
                                        <div v-else style="font-size: 12px;display: inline;margin-left: 4px;color:#338fe5">
                                            已配置<a-button type="link" icon="delete" @click="onClearQuick"
                                                style="color: #5d6396; width: 20px;height: 20px; margin-left: 4px; font-size: 12px;" block/>
                                        </div>
                                    </div>

                                    <a-form-model-item label="默认值" prop="enableDefault">
                                        <a-checkbox v-model="selectItem.searchConfig.datePickerConfig.enableDefault" @change="onDefaultChange" size="small"><span
                                                class="smallText">设定筛选默认值</span></a-checkbox>
                                    </a-form-model-item>
                                    <date-range-filter style="height: 150px;"
                                        v-if="selectItem.searchConfig.datePickerConfig.conditionMethod==='DATE_RANGE'&&selectItem.searchConfig.datePickerConfig.enableDefault
                                            &&selectItem.searchConfig.datePickerConfig.rangeType!=='QUICK'" 
                                        :rangeType="selectItem.searchConfig.datePickerConfig.rangeType" 
                                        :conditions="selectItem.searchConfig.datePickerConfig.defaultConfig.defaultValue" 
                                        :dateMode="selectItem.searchConfig.datePickerConfig.dateFormat" :showLabel="false"
                                        ></date-range-filter>
                                    <single-date-filter v-else-if="selectItem.searchConfig.datePickerConfig.conditionMethod==='SINGLE_DATE'&&selectItem.searchConfig.datePickerConfig.enableDefault
                                            &&selectItem.searchConfig.datePickerConfig.rangeType!=='QUICK'" :conditions="selectItem.searchConfig.datePickerConfig.defaultConfig.defaultValue" 
                                            :options="dateOptions" :label="'时间类型'" :dateMode="selectItem.searchConfig.datePickerConfig.dateFormat"></single-date-filter>
                                    <div v-else-if="selectItem.searchConfig.datePickerConfig.rangeType=='QUICK'&&selectItem.searchConfig.datePickerConfig.enableDefault">
                                        <a-select v-model="selectItem.searchConfig.datePickerConfig.defaultConfig.defaultValue" placeholder="请选择" size="small" style="width: 180px;margin-left: 110px;">
                                            <a-select-option v-for="quick in getQuickTextOptions" :key="quick.value" :value="quick.value">{{ quick.label }}</a-select-option>
                                        </a-select>
                                    </div>
                                    
                                </div>
                            </a-form-model>
                            <div v-else class="query-config-error">
                                <div>当前所选字段类型不一致，无法进行查询条件配置！</div>
                                <div>(请将字段切换成同类型再继续配置)</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </a-modal>
</template>

<script>
import { TYPE_CONFIGS } from '@/pages/smart-page/components/componentRegistry'
import Sortable from "sortablejs"
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
import { findEnumValues } from '@/services/smart-page/DatasetService'
import DateRangeFilter from '../filter/DateRangeFilter.vue';
import DropdownPopover from './DropdownPopover.vue';
import ManualInputPopover from './ManualInputPopover.vue';
import SingleDateFilter from '../filter/SingleDateFilter.vue';
import NumConditionFilter from '../filter/NumConditionFilter.vue';
import TextConditionFilter from '../filter/TextConditionFilter.vue';
import { defaultValue } from '@antv/l7';
export default {
    name: 'PageQueryConfigDialog',
    components: { Sortable,DateRangeFilter,NumConditionFilter,TextConditionFilter,SingleDateFilter,
        DropdownPopover,ManualInputPopover
     },
    props: {
        isShowDialog: {
            required: true
        },
        queryItems: {
            type: Array,
            required: true
        },
        charts: {
            type: Array,
            required: true
        },
        datasets: {
            type: Array,
            required: true
        },
        pageId: {
            type: String,
            required: true
        },
        selectedQuery: {
            type: Object,
        }
    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData()
                }
            }
        },
        selectItem: {
            handler: function (newVal, oldVal) {
                console.log(newVal)
            }
        }
    },
    data() {
        return {
            tableData: [],
            selectItem: {
                autoConfig: 'AUTO',
                searchConfig: {
                    renderType: 'AUTO',
                    conditionMethod: 'AUTO',
                }
            },
            allSelected:false,
            linkItems: [],
            componentTypes: [...TYPE_CONFIGS],
            linkType:false,
            quickVisible:false,
            fieldOptions:[],
            dateOptions: [
                {
                value: 'EQUAL',
                label: this.$t('chart.filter_eq')
                }, {
                value: 'NOT_EQUAL',
                label: this.$t('chart.filter_not_eq')
                }, {
                value: 'LESS_THAN',
                label: this.$t('chart.filter_lt')
                }, {
                value: 'GREATER_THAN',
                label: this.$t('chart.filter_gt')
                }, {
                value: 'LESS_THAN_OR_EQUAL',
                label: this.$t('chart.filter_le')
                }, {
                value: 'GREATER_THAN_OR_EQUAL',
                label: this.$t('chart.filter_ge')
            }],
            valueOptions: [{
                value: 'EQUAL',
                label: this.$t('chart.filter_eq')
                }, {
                value: 'NOT_EQUAL',
                label: this.$t('chart.filter_not_eq')
                }, {
                value: 'LESS_THAN',
                label: this.$t('chart.filter_lt')
                }, {
                value: 'GREATER_THAN',
                label: this.$t('chart.filter_gt')
                }, {
                value: 'LESS_THAN_OR_EQUAL',
                label: this.$t('chart.filter_le')
                }, {
                value: 'GREATER_THAN_OR_EQUAL',
                label: this.$t('chart.filter_ge')
            }],
            textOptions: [{
                value: 'STRICT_MATCH',
                label: this.$t('精确匹配')
                }, {
                value: 'NOT_MATCH',
                label: this.$t('不匹配')
                }, {
                value: 'CONTAINS',
                label: this.$t('chart.filter_like')
                }, {
                value: 'NOT_CONTAINS',
                label: this.$t('chart.filter_not_like')
                }, {
                value: 'START_WITH',
                label: this.$t('开头是')
                }, {
                value: 'END_WITH',
                label: this.$t('结尾是')
                },{
                value: 'IS_NULL',
                label: this.$t('chart.filter_null')
                }, {
                value: 'IS_NOT_NULL',
                label: this.$t('chart.filter_not_null')
                }, {
                value: 'IS_EMPTY',
                label: this.$t('chart.filter_empty')
                }, {
                value: 'IS_NOT_EMPTY',
                label: this.$t('chart.filter_not_empty')
            }],
            quickTextOptions: [{
                    value: 'TODAY',
                    label: '今天'
                }, {
                    value: 'YESTERDAY',
                    label: '昨天'
                }, {
                    value: 'LAST_7_DAYS',
                    label: '最近7天'
                }, {
                    value: 'LAST_30_DAYS',
                    label: '最近30天'
                }, {
                    value: 'LAST_90_DAYS',
                    label: '最近90天'
                }, {
                    value: 'THIS_MONTH',
                    label: '本月'
                },{
                    value: 'LAST_MONTH',    
                    label: '上个月'
                }, {
                    value: 'THIS_YEAR',
                    label: '今年'
            }],
        }
    },
    computed: {
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
        getQuickTextOptions(){
            return this.quickTextOptions.filter(item=>{
                return this.selectItem.searchConfig.datePickerConfig.quickConfig.includes(item.value)
            })
        }
    },
    created() {
    },
    mounted() {
    },
    methods: {
        loadData() {
            console.log("loadData",this.queryItems)
            this.tableData = deepCopy(this.queryItems)
            if (this.tableData.length > 0) {
                // this.selectItem = this.tableData[0]
                this.selectItem = this.tableData.find(item=>{return item.name===this.selectedQuery.name})||this.tableData[0]
                this.resetLinkItems()
            }


            if (!this.sortable) {
                this.rowDrop()
            }
            this.$nextTick(() => {
                let xTable = this.$refs.xTable;
                if (xTable && this.tableData.length > 0) {
                    // xTable.setCurrentRow(this.tableData[0])
                    xTable.setCurrentRow(this.selectedQuery)
                }
            })
        },
        rowDrop() {
            let that = this
            this.$nextTick(() => {
                let xTable = that.$refs.xTable;
                if (xTable) {
                    that.sortable = Sortable.create(
                        xTable.$el.querySelector(".body--wrapper>.vxe-table--body tbody"),
                        {
                            handle: ".vxe-body--row",
                            animation: 150,
                            onEnd: ({ newIndex, oldIndex }) => {
                                that.tableData.splice(newIndex, 0, this.tableData.splice(oldIndex, 1)[0]);
                                var newArray = this.tableData.slice(0);
                                that.tableData = [];
                                that.$nextTick(function () {
                                    that.tableData = newArray;
                                });
                            },
                        }
                    );
                }
            });
        },
        resetLinkItems() {
            console.log("resetLinkItems",this.charts,this.selectItem)
            this.linkItems = []
            for (let i = 0; i < this.charts.length ; i++) {
                let chart = this.charts[i]
                let chartType = this.getChartTypeConfig(chart.type)

                if (chartType && chartType.category !== 'chart.chart_type_others') {
                    let link = this.selectItem.linkConfig.find(item => { return item.chartId === chart.id })
                    let item = {
                        chartId: chart.id,
                        chartName: chart.title,
                        chartIcon: this.getChartIcon(chart.type),
                        datasetId: chart.datasetId,
                        datasetName: this.getDatasetName(chart.datasetId),
                        selected: link?.selected||false,
                        linkageField: link?.linkageField||''
                    }
                    this.linkItems.push(item)
                }
            }
            this.allSelected=this.linkItems.every(item=>{return item.selected})
            this.onDropDataSourceChange(true);
        },
        getDatasetName(datasetId) {
            if (datasetId && this.datasets && this.datasets.length > 0) {
                let filterd = this.datasets.filter(item => { return item.id === datasetId })
                let datasetName = filterd.length > 0 ? filterd[0].name : ''
                return datasetName
            }
            return ''
        },
        getChartIcon(type) {
            let chartType = this.getChartTypeConfig(type)
            return chartType ? chartType.icon : ''
        },
        getChartTypeConfig(chartType) {
            for (let i = 0; i < this.componentTypes.length - 1; i++) {
                if (this.componentTypes[i].value === chartType) {
                    return this.componentTypes[i]
                }
            }
        },
        onCurrentChange({ row }) {
            this.selectItem.linkConfig=this.linkItems.filter(item=>{return item.selected&&item.linkageField})
            this.selectItem = row
            this.resetLinkItems()
        },
        datasetFields(datasetId) {
            if (datasetId && this.datasets && this.datasets.length > 0) {
                let filterd = this.datasets.filter(item => { return item.id === datasetId })
                let fields = filterd.length > 0 ? filterd[0].fields : []
                return fields
            }
            return []
        },
        onOK() {
            // this.tableData.forEach(item=>{
            //     item.linkConfig=item.linkConfig.filter(item=>{return item.selected&&item.linkageField})
            // })
            this.selectItem.linkConfig=this.linkItems.filter(item=>{return item.selected&&item.linkageField})
            this.tableData = this.tableData.map(item => {
                if(item.searchConfig.renderType!="DROPDOWN"){
                    this.$delete(item.searchConfig,'dropdownConfig')
                }
                if(item.searchConfig.renderType!="INPUT_NUMBER"){
                    this.$delete(item.searchConfig,'inputNumberConfig')
                }
                if(item.searchConfig.renderType!="DATE_PICKER"){
                    this.$delete(item.searchConfig,'datePickerConfig')
                }
                if(item.searchConfig.renderType!="TEXT"){
                    this.$delete(item.searchConfig,'inputTextConfig')
                }
                return item;
            })
            // this.tableData.forEach(item=>{
            //     if(item.searchConfig.renderType=="DROPDOWN"){
            //         if(item.searchConfig.dropdownConfig?.dataSource=='MANUAL'){
            //             item.searchConfig.dropdownConfig.options=
            //                 item.searchConfig.dropdownConfig.options.map(item=>{return {key:item.id,value:item.text}})
            //         }
            //     }
            // })

            console.log("onOk",this.tableData)
            this.$emit("ok",this.tableData);
        },
        onCancel() {
            this.$emit("cancel");
        },
        onLinkageFieldChange(linkField){
            if(!linkField||!linkField.selected){
                linkField=this.linkItems.find(item=>{return item.selected})
            }
            let field = this.datasetFields(linkField?.datasetId).find(item=>{return item.name===linkField.linkageField})

            this.linkType=false;
            this.linkItems.forEach(link=>{
                if(link.selected&&!link.linkageField){
                    let fields=this.datasetFields(link.datasetId)
                    let field=fields.find(f=>{return f.name===linkField.linkageField})
                    if(!field){
                        field=fields.find(f=>{return f.type===this.selectItem.type})
                    }
                    this.$set(link,'linkageField',field?.name)
                }
                let field2 = this.datasetFields(link.datasetId).find(item=>{return item.name===link.linkageField})
                if(link.selected&&field2?.type!=field?.type){
                    this.linkType=true
                }
            })
        },
        onSelectField(e,item){
            let field = this.datasetFields(item.datasetId).find(item=>{return item.name===e})
            this.selectItem.type=field.type

            this.onLinkageFieldChange(item);
            
            if(field.type==='TEXT'){
                this.selectItem.searchConfig.renderType='DROPDOWN'
            }else if(field.type==='NUM'){
                this.selectItem.searchConfig.renderType='INPUT_NUMBER'
                
            }else{
                this.selectItem.searchConfig.renderType='DATE_PICKER'
            }
            this.onRenderTypeChange()
        },
        onChangeAutoConfig(){
            if(this.selectItem.autoConfig==='AUTO'){
                this.selectItem.searchConfig.datePickerConfig=null;
                this.selectItem.searchConfig.dropdownConfig=null;
                this.selectItem.searchConfig.inputTextConfig=null;
                this.selectItem.searchConfig.inputNumberConfig=null;
                this.onRenderTypeChange();
            }else{
                this.linkItems.forEach(item=>{
                    item.selected=true;
                    let fields=this.datasetFields(item.datasetId)
                    let field=fields.find(item=>{return item.name===this.selectItem.name&&item.type===this.selectItem.type})
                    if(!field){
                        field=fields.find(item=>{return item.type===this.selectItem.type})
                    }
                    this.$set(item,'linkageField',field?.name)
                })
            }
        },
        onSelectAll() {
            this.selectItem.linkConfig=[]
            this.linkItems.forEach(item => {
                 item.selected=this.allSelected
                 if(item.selected){
                    this.selectItem.linkConfig.push(item)
                    if(!item.linkageField){
                        let fields=this.datasetFields(item.datasetId)
                        let field=fields.find(item=>{return item.name===this.selectItem.name&&item.type===this.selectItem.type})
                        if(!field){
                            field=fields.find(item=>{return item.type===this.selectItem.type})
                        }
                        this.$set(item,'linkageField',field?.name)
                    }
                }
            })
            this.onLinkageFieldChange(this.linkItems[0]);
        },
        onChangeSelected(item){
            this.allSelected=this.linkItems.every(item=>{return item.selected})
            this.selectItem.linkConfig=[]
            this.linkItems.forEach(element => {
                if(element.selected){
                    this.selectItem.linkConfig.push(element)
                }
            });
            if(item.selected&&!item.linkageField){
                let fields=this.datasetFields(item.datasetId)
                let field=fields.find(item=>{return item.name===this.selectItem.name&&item.type===this.selectItem.type})
                if(!field){
                    field=fields.find(item=>{return item.type===this.selectItem.type})
                }
                this.$set(item,'linkageField',field?.name)
            }
            this.onLinkageFieldChange(item);
        },
        onClearSelectFields(){
            this.linkItems.forEach(item=>{item.linkageField=null})
        },
        //下拉列表
        onDatasetChange() {
            this.selectItem.searchConfig.dropdownConfig.searchField=undefined
            this.selectItem.searchConfig.dropdownConfig.sortField=undefined
        },
        onDropDataSourceChange(initFlag){
            if(this.selectItem.searchConfig.renderType=='DROPDOWN'){
                if(this.selectItem.searchConfig.dropdownConfig?.dataSource==='AUTO'){
                    this.initEnumOptions()
                }else if(this.selectItem.searchConfig.dropdownConfig?.dataSource==='MANUAL'){
                    this.fieldOptions=[]
                    if(!this.selectItem.searchConfig.dropdownConfig?.options?.length){
                        this.$set(this.selectItem.searchConfig.dropdownConfig, 'options', [])
                    }else{
                        this.fieldOptions=this.selectItem.searchConfig.dropdownConfig.options
                            .map(item=>{return {id:item.key,text:item.value}})
                    }
                }else if(this.selectItem.searchConfig.dropdownConfig?.dataSource==='DATASET'){
                    this.onSearchFieldChange();
                }
                if(!initFlag){
                    this.$set(this.selectItem.searchConfig.dropdownConfig, 'defaultConfig', {
                        defaultValue:undefined
                    })
                    this.selectItem.searchConfig.dropdownConfig.enableDefault=false;
                }
            }
        },
        async initEnumOptions() {
            // 查找枚举值
            if (this.selectItem.searchConfig.renderType === 'DROPDOWN') {
                if(this.selectItem.searchConfig.dropdownConfig?.dataSource==='AUTO'){
                    this.fieldOptions=[]
                    let options=[]
                    let fields=this.linkItems.filter(item=>{return item.selected&&item.linkageField}).map(item=>{return item.linkageField})
                    fields=[...new Set(fields)]
                    for(let item of fields){
                        await findEnumValues(this.pageId,this.selectItem.datasetId,item).then(res => {
                            options.push(...this.optionData(res))
                        })
                    }
                    this.fieldOptions=options
                }
            }
        },
        onClearManual(){
            this.$set(this.selectItem.searchConfig.dropdownConfig, 'options', [])
            this.fieldOptions=[]
            this.$set(this.selectItem.searchConfig.dropdownConfig.defaultConfig, 'defaultValue',undefined)
            this.selectItem.searchConfig.dropdownConfig.enableDefault=false
        },
        optionData(data) {
            if (!data) return null
            return data.filter(item => !!item).map(item => {
                return {
                id: item,
                text: item
                }
            })
        },
        onSearchFieldChange(){
            if(this.selectItem.searchConfig.dropdownConfig?.datasetId){
                this.fieldOptions=[]
                if(this.selectItem.searchConfig.dropdownConfig.searchField){
                    findEnumValues(this.pageId,this.selectItem.searchConfig.dropdownConfig?.datasetId,
                        this.selectItem.searchConfig.dropdownConfig.searchField).then(res => {
                            this.fieldOptions=[]
                            this.fieldOptions.push(...this.optionData(res))
                        })
                }
            }
        },
        onOkDropdownPopover(value,excludeMode){
            this.$set(this.selectItem.searchConfig.dropdownConfig, 'defaultConfig', {
                defaultValue:value,
            })
            this.selectItem.searchConfig.dropdownConfig.excludeMode=excludeMode
        },
        onOkManual(value){
            this.$set(this.selectItem.searchConfig.dropdownConfig, 'options', value)
            this.fieldOptions=value.map(item=>{return {id:item.key,text:item.value}})
        },
        onConditionMethodChange(e){
            if(e.target.value==='SINGLE'){
                this.$set(this.selectItem.searchConfig.dropdownConfig.defaultConfig, 'defaultValue',undefined)
            }else if(e.target.value==='MULTI'){
                this.$set(this.selectItem.searchConfig.dropdownConfig.defaultConfig, 'defaultValue',[])
            }
        },
        //日期
        onClearQuick(){
            this.selectItem.searchConfig.datePickerConfig.quickConfig=[]
        },
        dateConditionMethod(type){
            if(type=='SINGLE_DATE'){
                if(this.selectItem.searchConfig.datePickerConfig.dateFormat==='YYYY'){
                    return '单年'
                }else if(this.selectItem.searchConfig.datePickerConfig.dateFormat==='YYYY-MM'){
                    return '单月'
                }else{
                    return '单日'
                }
            }else{
                if(this.selectItem.searchConfig.datePickerConfig.dateFormat==='YYYY'){
                    return '年区间'
                }else if(this.selectItem.searchConfig.datePickerConfig.dateFormat==='YYYY-MM'){
                    return '月区间'
                }else{
                    return '日区间'
                }
            }
            
        },
        onRangeTypeChange(){
            if(this.selectItem.searchConfig.datePickerConfig.rangeType=='QUICK'){
                this.selectItem.searchConfig.datePickerConfig.defaultConfig.defaultValue=[]
            }else{
                this.selectItem.searchConfig.datePickerConfig.defaultConfig.defaultValue=null
                this.onDefaultChange();
            }
        },

        onRenderTypeChange(){
            this.initSelectItem();
        },
        initSelectItem(){
            if(this.selectItem.searchConfig.renderType==='DATE_PICKER'){
                if(!this.selectItem.searchConfig.datePickerConfig||!this.selectItem.searchConfig.datePickerConfig.defaultConfig){
                    this.$set(this.selectItem.searchConfig, 'datePickerConfig', {
                        dateFormat: "YYYY-MM-DD", // 时间粒度
                        dataSource: "DEFAULT", // 数据源类型 DEFAULT,DATASET
                        conditionMethod: "DATE_RANGE", // 搜索类型 SINGLE, RANGE
                        rangeType: "BETWEEN", // 范围类型 START_WITH, END_WITH, BETWEEN,QUICK
                        quickConfig: [],
                        enableDefault: false, // 是否启用默认值
                        defaultConfig: {
                            defaultValue: [
                            {
                                matchMethod: "",
                                matchValue: "",
                                relative: 'true',
                                relativeUnit: "DAY",
                                relativeValue: 1,
                                relativeDirection: "-",
                            },{
                                matchMethod: "", 
                                matchValue: "",
                                relative: 'true',
                                relativeUnit: "DAY",
                                relativeValue: 1,
                                relativeDirection: "-",
                            }], // 默认值,多个值为数组
                        },
                    })
                }
            }else if(this.selectItem.searchConfig.renderType==='DROPDOWN'){
                if(!this.selectItem.searchConfig.dropdownConfig||!this.selectItem.searchConfig.dropdownConfig.defaultConfig){
                    this.$set(this.selectItem.searchConfig, 'dropdownConfig', {
                        dataSource: "DATASET", // 数据源类型 DATASET,MANUAL
                        // 数据集配置
                        datasetId: "", // 数据集id
                        searchField: "", // 搜索字段
                        displayField: "", // 显示字段
                        sortField: undefined, // 排序字段
                        sortDirection: undefined, // 排序方向 ASC, DESC
                        conditionMethod: "SINGLE", // 搜索类型 SINGLE, MULTI
                        searchTrigger: "ON_CLICK", // 搜索触发类型 ON_CLICK, PRE_LOAD
                        //手工输入配置
                        options: [], // 手工输入值
                        enableDefault: false, // 是否启用默认值
                        defaultConfig: {
                            defaultValue: undefined, // 默认值,多个值为数组
                        },
                    })
                }
            }else if(this.selectItem.searchConfig.renderType==='TEXT'){
                if(!this.selectItem.searchConfig.inputTextConfig||!this.selectItem.searchConfig.inputTextConfig.defaultConfig){
                    this.$set(this.selectItem.searchConfig, 'inputTextConfig', {
                        conditionMethod: 'SINGLE',
                        enableDefault: false,
                        defaultConfig:{
                            defaultValue: [{
                                matchMethod: "STRICT_MATCH", 
                                matchValue: "",
                            },{
                                matchMethod: "STRICT_MATCH", 
                                matchValue: "",
                            }],
                        }
                    })
                }
            }else{
                if(!this.selectItem.searchConfig.inputNumberConfig||!this.selectItem.searchConfig.inputNumberConfig.defaultConfig){
                    this.$set(this.selectItem.searchConfig, 'inputNumberConfig', {
                        aggregationType: 'SUM',
                        conditionMethod: 'SINGLE',
                        enableDefault: false,
                        defaultConfig:{
                            defaultValue: [{
                                matchMethod: "EQUAL", 
                                matchValue: "",
                            },{
                                matchMethod: "EQUAL", 
                                matchValue: "",
                            }],
                        }
                    })
                }
            }
        },
        onDefaultChange(){
            if(this.selectItem.searchConfig.renderType==='DATE_PICKER'){
                if(!this.selectItem.searchConfig.datePickerConfig.defaultConfig.defaultValue){
                    this.$set(this.selectItem.searchConfig.datePickerConfig.defaultConfig, 'defaultValue', [
                            {
                                matchMethod: "",
                                matchValue: "",
                                relative: 'true',
                                relativeUnit: "DAY",
                                relativeValue: 1,
                                relativeDirection: "-",
                            },{
                                matchMethod: "", 
                                matchValue: "",
                                relative: 'true',
                                relativeUnit: "DAY",
                                relativeValue: 1,
                                relativeDirection: "-",
                            }])
                }
            }else if(this.selectItem.searchConfig.renderType==='DROPDOWN'){
                if(!this.selectItem.searchConfig.dropdownConfig.defaultConfig?.defaultValue){
                    this.$set(this.selectItem.searchConfig.dropdownConfig.defaultConfig, 'defaultValue',undefined)
                }
            }else if(this.selectItem.searchConfig.renderType==='TEXT'){
                if(!this.selectItem.searchConfig.inputTextConfig.defaultConfig.defaultValue){
                    this.$set(this.selectItem.searchConfig.inputTextConfig.defaultConfig, 'defaultValue', [{
                                matchMethod: "STRICT_MATCH", 
                                matchValue: "",
                            },{
                                matchMethod: "STRICT_MATCH", 
                                matchValue: "",
                            }])
                }
            }else{
                if(!this.selectItem.searchConfig.inputNumberConfig.defaultConfig.defaultValue){
                    this.$set(this.selectItem.searchConfig.inputNumberConfig.defaultConfig, 'defaultValue', [{
                            matchMethod: "EQUAL", 
                            matchValue: "",
                        },{
                            matchMethod: "EQUAL", 
                            matchValue: "",
                        }])
                }
            }
        }
    },

}
</script>

<style lang="less" scoped>
/deep/.ant-modal-body {
    padding-bottom: 0px;
}

/deep/.ant-form-item {
    margin-bottom: 0px;
}

/deep/.ant-form label {
    font-size: 12px;
}

/deep/.ant-form-item-label {
    font-size: 12px;
}

/deep/.ant-select-selection-selected-value {
    font-size: 12px;
}

.smallText {
    font-size: 12px;
}

.status-sorting-item {
    max-width: 478px;
    display: flex;

    &:hover {}

    .item-drag {
        flex: 0 0 20px;
        cursor: move;
    }

    .item-text {
        flex: 1 1 auto;
    }
}

.start-state {
    cursor: default;
}

.query-config-content {
    height: 100%;
    width: 1120px;
    font-size: 12px;
    display: flex;
    justify-content: center;
    align-items: stretch;
    overflow: hidden;

    .query-fields-list {
        border-right: 1px solid #e5e5e5;
        flex: 0 0 180px;
        height: 440px;
        overflow: hidden;
        width: 180px;

        .query-fields-title {
            color: #89898a;
            height: 36px;
            padding: 0 15px 0 18px;
        }
    }

    .query-field-config {
        flex: 1 0 0px;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        font-size: 12px;

        .query-config-effect {
            background-color: #fff;
            border-bottom: 1px solid #e5e5e5;
            height: 40px;
            padding: 0 14px;
            width: 100%;
        }
        .query-config-effect-mask {
            background-color: hsla(0, 0%, 100%, .75);
                bottom: 0;
                left: 0;
                position: absolute;
                right: 0;
                top: -1px;
                z-index: 4;
        }

        .query-config-settings {
            width: 940px;
            display: flex;
            justify-content: space-between;
            align-items: stretch;
            height: 100%;

            .query-config-charts {
                flex: 0 0 400px;
                border-right: 1px solid #e5e5e5;
                padding-top: 10px;
                padding-left: 10px;
                padding-left: 10px;
                height: 400px;

                .query-config-charts-header {
                    display: inline-flex;
                    flex-direction: row;
                    width: 400px;
                }

                .query-config-charts-operator-header {
                    display: inline-flex;
                    flex-direction: row;
                    width: 400px;
                }

                .query-config-link {
                    display: inline-flex;
                    flex-direction: row;
                    width: 400px;
                    margin-top: 5px;
                }
            }

            .query-config-search {
                flex: 1 1 auto;
                padding-top: 10px;
                padding-left: 10px;
            }
        }
    }
}
.query-config-charts-header ::v-deep .ant-input{
    font-size: 12px;
}
.query-config-error{
    align-items: center;
    background-color: #fff;
    display: flex;
    flex-direction: column;
    height: 100%;
    justify-content: center;
    width: 100%;
    padding-bottom: 80px;
    div{
        color: rgba(0,0,0,.4);
        line-height: 20px;
        text-align: center;
        width: 220px;
    }
}
// ::v-deep .ant-popover-arrow{
//   display: none;
// }
.dropdown-config /deep/ .ant-select-selection__placeholder{
    margin-left: 4px;
}
 </style>