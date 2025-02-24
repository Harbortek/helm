<template>
  <div style="width: 100%">
    <a-row style="flex: 1 1;">
      <a-form-model ref="sizeFormBar" :model="sizeForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <!--bar-begin-->
        <a-form-model-item v-show="showProperty('barDefault')" :label="$t('chart.adapt')" class="form-item">
          <a-checkbox v-model="sizeForm.barDefault" @change="changeBarSizeCase('barDefault')">{{ $t('chart.adapt')
          }}</a-checkbox>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('barGap')" :label="$t('chart.bar_gap')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.barGap" :disabled="sizeForm.barDefault" show-input :show-input-controls="false"
            input-size="mini" :min="0" :max="5" :step="0.1" @change="changeBarSizeCase('barGap')" />
        </a-form-model-item>
        <!--bar-end-->
        <!--line-begin-->
        <a-form-model-item v-show="showProperty('lineWidth')" :label="$t('chart.line_width')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.lineWidth" show-input :show-input-controls="false" input-size="mini" :min="0"
            :max="10" @change="changeBarSizeCase('lineWidth')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('lineSymbol')" :label="$t('chart.line_symbol')" class="form-item">
          <a-select v-model="sizeForm.lineSymbol" :placeholder="$t('chart.line_symbol')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('lineSymbol')">
            <a-select-option v-for="item in lineSymbolOptions" :key="item.value" :title="item.name" :value="item.value">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('lineSymbolSize')" :label="$t('chart.line_symbol_size')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.lineSymbolSize" show-input :show-input-controls="false" input-size="mini" :min="0"
            :max="20" @change="changeBarSizeCase('lineSymbolSize')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('lineSmooth')" :label="$t('chart.line_smooth')" class="form-item">
          <a-checkbox v-model="sizeForm.lineSmooth" @change="changeBarSizeCase('lineSmooth')">{{ $t('chart.line_smooth')
          }}
          </a-checkbox>
        </a-form-model-item>
        <!--line-end-->

        <!--pie-begin-->
        <a-form-model-item v-show="showProperty('pieInnerRadius')" :label="$t('chart.pie_inner_radius_percent')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.pieInnerRadius" show-input :show-input-controls="false" input-size="mini" :min="0"
            :max="100" @change="changeBarSizeCase('pieInnerRadius')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('pieOuterRadius')" :label="$t('chart.pie_outer_radius_size')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.pieOuterRadius" show-input :show-input-controls="false" input-size="mini" :min="0"
            :max="100" @change="changeBarSizeCase('pieOuterRadius')" />
        </a-form-model-item>
        <!--pie-end-->
        <!--radar-begin-->
        <a-form-model-item v-show="showProperty('radarShape')" :label="$t('chart.shape')" class="form-item">
          <a-radio-group v-model="sizeForm.radarShape" size="small" @change="changeBarSizeCase('radarShape')">
            <a-radio-button value="polygon">{{ $t('chart.polygon') }}</a-radio-button>
            <a-radio-button value="circle">{{ $t('chart.circle') }}</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('radarSize')" :label="$t('chart.radar_size')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.radarSize" show-input :show-input-controls="false" input-size="mini" :min="0"
            :max="100" @change="changeBarSizeCase('radarSize')" />
        </a-form-model-item>
        <!--radar-end-->


        <!--table-begin-->
        <a-form-model-item v-show="showProperty('tablePageMode')" label-width="100px" :label="$t('chart.table_page_mode')"
          class="form-item">
          <a-select v-model="sizeForm.tablePageMode" :placeholder="$t('chart.table_page_mode')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('tablePageMode')">
            <a-select-option :title="$t('chart.page_mode_page')" value="page">{{ $t('chart.page_mode_page')
            }}</a-select-option>
            <a-select-option :title="$t('chart.page_mode_pull')" value="pull">{{ $t('chart.page_mode_pull')
            }}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('tablePageSize') && sizeForm.tablePageMode === 'page'" label-width="100px"
          :label="$t('chart.table_page_size')" class="form-item">
          <a-select v-model="sizeForm.tablePageSize" :placeholder="$t('chart.table_page_size')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('tablePageSize')">
            <a-select-option v-for="item in pageSizeOptions" :key="item.value" :title="item.name" :value="item.value">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('tableTitleFontSize')" label-width="100px"
          :label="$t('chart.table_title_fontsize')" class="form-item">
          <a-select v-model="sizeForm.tableTitleFontSize" :placeholder="$t('chart.table_title_fontsize')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('tableTitleFontSize')">
            <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name" :value="option.value">
              {{ option.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('tableItemFontSize')" label-width="100px"
          :label="$t('chart.table_item_fontsize')" class="form-item">
          <a-select v-model="sizeForm.tableItemFontSize" :placeholder="$t('chart.table_item_fontsize')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('tableItemFontSize')">
            <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name" :value="option.value">
              {{ option.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('tableHeaderAlign')" label-width="100px"
          :label="$t('chart.table_header_align')" class="form-item">
          <a-select v-model="sizeForm.tableHeaderAlign" :placeholder="$t('chart.table_header_align')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('tableHeaderAlign')">
            <a-select-option v-for="option in alignOptions" :key="option.value" :title="option.name"
              :value="option.value">
              {{ option.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('tableItemAlign')" label-width="100px"
          :label="$t('chart.table_item_align')" class="form-item">
          <a-select v-model="sizeForm.tableItemAlign" :placeholder="$t('chart.table_item_align')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('tableItemAlign')">
            <a-select-option v-for="option in alignOptions" :key="option.value" :title="option.name"
              :value="option.value">{{ option.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('tableTitleHeight')" label-width="100px"
          :label="$t('chart.table_title_height')" class="form-item form-item-slider">
          <a-slider v-model="sizeForm.tableTitleHeight" :min="20" :max="100" show-input :show-input-controls="false"
            input-size="mini" @change="changeBarSizeCase('tableTitleHeight')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('tableItemHeight')" label-width="100px"
          :label="$t('chart.table_item_height')" class="form-item form-item-slider">
          <a-slider v-model="sizeForm.tableItemHeight" :min="20" :max="100" show-input :show-input-controls="false"
            input-size="mini" @change="changeBarSizeCase('tableItemHeight')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('tableColumnMode')" label-width="100px"
          :label="$t('chart.table_column_width_config')" class="form-item">
          <a-radio-group v-model="sizeForm.tableColumnMode" @change="changeBarSizeCase('tableColumnMode')">
            <a-radio value="adapt"><span>{{ $t('chart.table_column_adapt') }}</span></a-radio>
            <a-radio value="custom">
              <span>{{ $t('chart.table_column_custom') }}</span>
            </a-radio>
          </a-radio-group>
          <a-tooltip class="item" effect="dark" placement="bottom">
            <div slot="content" v-html="$t('chart.table_column_width_tip')" />
            <i class="el-icon-info" style="cursor: pointer;color: #606266;margin-left: 4px;" />
          </a-tooltip>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('tableColumnMode') && sizeForm.tableColumnMode === 'custom'" label=""
          label-width="100px" class="form-item form-item-slider">
          <a-slider v-model="sizeForm.tableColumnWidth" :min="10" :max="500" show-input :show-input-controls="false"
            input-size="mini" @change="changeBarSizeCase('tableColumnWidth')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('showIndex')" label-width="100px" :label="$t('chart.table_show_index')"
          class="form-item">
          <a-radio-group v-model="sizeForm.showIndex" input-size="mini" @change="changeBarSizeCase('showIndex')">
            <a-radio :value="true">{{ $t('panel.yes') }}</a-radio>
            <a-radio :value="false">{{ $t('panel.no') }}</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('indexLabel') && sizeForm.showIndex" label-width="100px"
          :label="$t('chart.table_index_desc')" class="form-item">
          <a-input v-model="sizeForm.indexLabel" type="text" @blur="changeBarSizeCase('indexLabel')" />
        </a-form-model-item>

        <!--chart-mix-start-->
        <span v-show="showProperty('mix')">
          <a-divider content-position="center" class="divider-style">{{ $t('chart.chart_bar') }}</a-divider>
          <a-form-model-item :label="$t('chart.adapt')" class="form-item">
            <a-checkbox v-model="sizeForm.barDefault" @change="changeBarSizeCase('barDefault')">{{ $t('chart.adapt')
            }}</a-checkbox>
          </a-form-model-item>
          <a-form-model-item :label="$t('chart.bar_gap')" class="form-item form-item-slider">
            <a-slider v-model="sizeForm.barGap" :disabled="sizeForm.barDefault" show-input :show-input-controls="false"
              input-size="mini" :min="0" :max="5" :step="0.1" @change="changeBarSizeCase('barGap')" />
          </a-form-model-item>
          <a-divider content-position="center" class="divider-style">{{ $t('chart.chart_line') }}</a-divider>
          <a-form-model-item :label="$t('chart.line_width')" class="form-item form-item-slider">
            <a-slider v-model="sizeForm.lineWidth" show-input :show-input-controls="false" input-size="mini" :min="0"
              :max="10" @change="changeBarSizeCase('lineWidth')" />
          </a-form-model-item>
          <a-form-model-item :label="$t('chart.line_symbol')" class="form-item">
            <a-select v-model="sizeForm.lineSymbol" :placeholder="$t('chart.line_symbol')" size="small"
              dropdownClassName="props-dropdown" @change="changeBarSizeCase('lineSymbol')">
              <a-select-option v-for="item in lineSymbolOptions" :key="item.value" :title="item.name"
                :value="item.value">{{ item.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item :label="$t('chart.line_symbol_size')" class="form-item form-item-slider">
            <a-slider v-model="sizeForm.lineSymbolSize" show-input :show-input-controls="false" input-size="mini" :min="0"
              :max="20" @change="changeBarSizeCase('lineSymbolSize')" />
          </a-form-model-item>
          <a-form-model-item :label="$t('chart.line_smooth')" class="form-item">
            <a-checkbox v-model="sizeForm.lineSmooth" @change="changeBarSizeCase('lineSmooth')">{{
              $t('chart.line_smooth') }}
            </a-checkbox>
          </a-form-model-item>
          <a-divider content-position="center" class="divider-style">{{ $t('chart.chart_scatter') }}</a-divider>
          <a-form-model-item :label="$t('chart.bubble_symbol')" class="form-item">
            <a-select v-model="sizeForm.scatterSymbol" :placeholder="$t('chart.line_symbol')" size="small"
              dropdownClassName="props-dropdown" @change="changeBarSizeCase('scatterSymbol')">
              <a-select-option v-for="item in lineSymbolOptions" :key="item.value" :title="item.name"
                :value="item.value">{{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item :label="$t('chart.bubble_size')" class="form-item form-item-slider">
            <a-slider v-model="sizeForm.scatterSymbolSize" show-input :show-input-controls="false" input-size="mini"
              :min="1" :max="40" @change="changeBarSizeCase('scatterSymbolSize')" />
          </a-form-model-item>
        </span>
        <!--chart-mix-end-->
      </a-form-model>
      <!--table-end-->

      <!--gauge-begin-->
      <a-form-model ref="sizeFormGauge" :model="sizeForm" label-width="100px" :layout="'horizontal'"
        :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }">
        <a-form-model-item v-show="showProperty('gaugeMin')" :label="$t('chart.min')" class="form-item">
          <a-radio-group v-model="sizeForm.gaugeMinType" size="small" @change="changeQuotaField('min')">
            <a-radio-button value="fix">{{ $t('chart.fix') }}</a-radio-button>
            <a-radio-button value="dynamic">{{ $t('chart.dynamic') }}</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item v-if="showProperty('gaugeMin') && sizeForm.gaugeMinType === 'fix'"
          class="form-item form-item-slider" label=" " :colon="false">
          <a-input-number v-model="sizeForm.gaugeMin" size="small" @change="changeBarSizeCase('gaugeMin')" />
        </a-form-model-item>
        <a-form-model-item v-if="showProperty('gaugeMin') && sizeForm.gaugeMinType === 'dynamic'"
          class="form-item form-flex" label=" " :colon="false">
          <a-col :span="12">
            <a-select v-model="sizeForm.gaugeMinField.id" :placeholder="$t('chart.field')" size="small"
              dropdownClassName="props-dropdown" @change="changeQuotaField('min', true)">
              <a-select-option v-for="item in quotaData" :key="item.name" :title="item.title" :value="item.name">
                <span style="float: left">
                  <a-icon component="field_type_text" style="color: #222222;" v-show="item.type === 'TEXT'" />
                  <a-icon component="field_type_number" style="color: cadetblue;" v-show="item.type === 'NUM'" />
                  <a-icon component="date" style="color:cadetblue;" v-show="item.type === 'DATE'" />
                </span>
                <span style="float: left; color: #8492a6; font-size: 12px">{{ item.name }}</span>
              </a-select-option>
            </a-select>
          </a-col>
          <a-col :span="12">
            <a-select v-model="sizeForm.gaugeMinField.summary" :placeholder="$t('chart.summary')" size="small"
              dropdownClassName="props-dropdown" @change="changeQuotaField('min')">
              <a-select-option v-if="validMinField" key="sum" value="sum" :title="$t('chart.sum')">{{ $t('chart.sum')
              }}</a-select-option>
              <a-select-option v-if="validMinField" key="avg" value="avg" :title="$t('chart.avg')">{{ $t('chart.avg')
              }}</a-select-option>
              <a-select-option v-if="validMinField" key="max" value="max" :title="$t('chart.max')">{{ $t('chart.max')
              }}</a-select-option>
              <a-select-option v-if="validMinField" key="min" value="min" :title="$t('chart.min')">{{ $t('chart.min')
              }}</a-select-option>
              <a-select-option v-if="validMinField" key="stddev_pop" value="stddev_pop" :title="$t('chart.stddev_pop')">{{
                $t('chart.stddev_pop') }}</a-select-option>
              <a-select-option v-if="validMinField" key="var_pop" value="var_pop" :title="$t('chart.var_pop')">{{
                $t('chart.var_pop') }}</a-select-option>
              <a-select-option key="count" value="count" :title="$t('chart.count')">{{ $t('chart.count')
              }}</a-select-option>
              <a-select-option v-if="minField.id !== 'count'" key="count_distinct" value="count_distinct"
                :title="$t('chart.count_distinct')">{{ $t('chart.count_distinct') }}</a-select-option>
            </a-select>
          </a-col>
        </a-form-model-item>

        <a-form-model-item v-show="showProperty('gaugeMax')" :label="$t('chart.max')" class="form-item">
          <a-radio-group v-model="sizeForm.gaugeMaxType" size="small" @change="changeQuotaField('max')">
            <a-radio-button value="fix">{{ $t('chart.fix') }}</a-radio-button>
            <a-radio-button value="dynamic">{{ $t('chart.dynamic') }}</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item v-if="showProperty('gaugeMax') && sizeForm.gaugeMaxType === 'fix'"
          class="form-item form-item-slider" label=" " :colon="false">
          <a-input-number v-model="sizeForm.gaugeMax" size="small" @change="changeBarSizeCase('gaugeMax')" />
        </a-form-model-item>
        <a-form-model-item v-if="showProperty('gaugeMax') && sizeForm.gaugeMaxType === 'dynamic'"
          class="form-item form-flex" label=" " :colon="false">
          <a-col :span="12">
            <a-select v-model="sizeForm.gaugeMaxField.id" :placeholder="$t('chart.field')" size="small"
              dropdownClassName="props-dropdown" @change="changeQuotaField('max', true)">
              <a-select-option v-for="item in quotaData" :key="item.name" :title="item.title" :value="item.name">
                <span style="float: left">
                  <a-icon component="field_type_text" style="color: #222222;" v-show="item.type === 'TEXT'" />
                  <a-icon component="field_type_number" style="color: cadetblue;" v-show="item.type === 'NUM'" />
                  <a-icon component="date" style="color:cadetblue;" v-show="item.type === 'DATE'" />
                </span>
                <span style="float: left; color: #8492a6; font-size: 12px">{{ item.name }}</span>
              </a-select-option>
            </a-select>
          </a-col>
          <a-col :span="12">
            <a-select v-model="sizeForm.gaugeMaxField.summary" :placeholder="$t('chart.summary')" size="small"
              dropdownClassName="props-dropdown" @change="changeQuotaField('max')">
              <a-select-option v-if="validMaxField" key="sum" value="sum" :title="$t('chart.sum')">{{ $t('chart.sum')
              }}</a-select-option>
              <a-select-option v-if="validMaxField" key="avg" value="avg" :title="$t('chart.avg')">{{ $t('chart.avg')
              }}</a-select-option>
              <a-select-option v-if="validMaxField" key="max" value="max" :title="$t('chart.max')">{{ $t('chart.max')
              }}</a-select-option>
              <a-select-option v-if="validMaxField" key="min" value="min" :title="$t('chart.min')">{{ $t('chart.min')
              }}</a-select-option>
              <a-select-option v-if="validMaxField" key="stddev_pop" value="stddev_pop" :title="$t('chart.stddev_pop')">{{
                $t('chart.stddev_pop') }}</a-select-option>
              <a-select-option v-if="validMaxField" key="var_pop" value="var_pop" :title="$t('chart.var_pop')">{{
                $t('chart.var_pop') }}</a-select-option>
              <a-select-option key="count" value="count" :title="$t('chart.count')">{{ $t('chart.count')
              }}</a-select-option>
              <a-select-option v-if="maxField.id !== 'count'" key="count_distinct" value="count_distinct"
                :title="$t('chart.count_distinct')">{{ $t('chart.count_distinct') }}</a-select-option>
            </a-select>
          </a-col>
        </a-form-model-item>

        <a-form-model-item v-show="showProperty('gaugeStartAngle')" :label="$t('chart.start_angle')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.gaugeStartAngle" show-input :show-input-controls="false" input-size="mini"
            :min="-360" :max="360" @change="changeBarSizeCase('gaugeStartAngle')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('gaugeEndAngle')" :label="$t('chart.end_angle')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.gaugeEndAngle" show-input :show-input-controls="false" input-size="mini" :min="-360"
            :max="360" @change="changeBarSizeCase('gaugeEndAngle')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('gaugeTickCount')" :label="$t('chart.tick_count')"
          class="form-item form-item-slider">
          <a-input-number v-model="sizeForm.gaugeTickCount" :min="1" :step="1" :precision="0"
            @change="changeBarSizeCase('gaugeTickCount')" />
        </a-form-model-item>
      </a-form-model>
      <!--gauge-end-->

      <a-form-model ref="sizeFormPie" :model="sizeForm" label-width="100px" :layout="'horizontal'" :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <!--text&label-start-->
        <a-form-model-item v-show="showProperty('quotaFontSize')" :label="$t('chart.quota_font_size')" class="form-item">
          <a-select v-model="sizeForm.quotaFontSize" :placeholder="$t('chart.quota_font_size')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('quotaFontSize')">
            <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name" :value="option.value">{{
              option.name }}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('quotaFontFamily')" :label="$t('chart.quota_font_family')"
          class="form-item">
          <a-select v-model="sizeForm.quotaFontFamily" :placeholder="$t('chart.quota_font_family')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('quotaFontFamily')">
            <a-select-option v-for="option in fontFamily" :key="option.value" :title="option.name"
              :value="option.value">{{ option.name }}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('quotaFontStyle')" :label="$t('chart.quota_text_style')"
          class="form-item">
          <a-checkbox v-model="sizeForm.quotaFontIsItalic" @change="changeBarSizeCase('quotaFontIsItalic')">{{
            $t('chart.italic') }}</a-checkbox>
          <a-checkbox v-model="sizeForm.quotaFontIsBolder" @change="changeBarSizeCase('quotaFontIsBolder')">{{
            $t('chart.bolder') }}</a-checkbox>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('quotaLetterSpace')" :label="$t('chart.quota_letter_space')"
          class="form-item">
          <a-select v-model="sizeForm.quotaLetterSpace" :placeholder="$t('chart.quota_letter_space')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('quotaLetterSpace')">
            <a-select-option v-for="option in fontLetterSpace" :key="option.value" :title="option.name"
              :value="option.value">{{ option.name }}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('quotaFontShadow')" :label="$t('chart.font_shadow')" class="form-item">
          <a-checkbox v-model="sizeForm.quotaFontShadow" @change="changeBarSizeCase('quotaFontShadow')">{{
            $t('chart.font_shadow') }}</a-checkbox>
        </a-form-model-item>
        <a-divider v-if="showProperty('dimensionShow')" />
        <a-form-model-item v-show="showProperty('dimensionShow')" :label="$t('chart.dimension_show')" class="form-item">
          <a-checkbox v-model="sizeForm.dimensionShow" @change="changeBarSizeCase('dimensionShow')">{{ $t('chart.show')
          }}</a-checkbox>
        </a-form-model-item>
        <div v-show="sizeForm.dimensionShow">
          <a-form-model-item v-show="showProperty('dimensionFontSize')" :label="$t('chart.dimension_font_size')"
            class="form-item">
            <a-select v-model="sizeForm.dimensionFontSize" :placeholder="$t('chart.dimension_font_size')" size="small"
              dropdownClassName="props-dropdown" @change="changeBarSizeCase('dimensionFontSize')">
              <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('dimensionFontFamily')" :label="$t('chart.dimension_font_family')"
            class="form-item">
            <a-select v-model="sizeForm.dimensionFontFamily" :placeholder="$t('chart.dimension_font_family')" size="small"
              dropdownClassName="props-dropdown" @change="changeBarSizeCase('dimensionFontFamily')">
              <a-select-option v-for="option in fontFamily" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('dimensionFontStyle')" :label="$t('chart.dimension_text_style')"
            class="form-item">
            <a-checkbox v-model="sizeForm.dimensionFontIsItalic" @change="changeBarSizeCase('dimensionFontIsItalic')">{{
              $t('chart.italic') }}</a-checkbox>
            <a-checkbox v-model="sizeForm.dimensionFontIsBolder" @change="changeBarSizeCase('dimensionFontIsBolder')">{{
              $t('chart.bolder') }}</a-checkbox>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('dimensionLetterSpace')" :label="$t('chart.dimension_letter_space')"
            class="form-item">
            <a-select v-model="sizeForm.dimensionLetterSpace" :placeholder="$t('chart.dimension_letter_space')"
              size="small" dropdownClassName="props-dropdown" @change="changeBarSizeCase('dimensionLetterSpace')">
              <a-select-option v-for="option in fontLetterSpace" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('dimensionFontShadow')" :label="$t('chart.font_shadow')"
            class="form-item">
            <a-checkbox v-model="sizeForm.dimensionFontShadow" @change="changeBarSizeCase('dimensionFontShadow')">{{
              $t('chart.font_shadow') }}</a-checkbox>
          </a-form-model-item>
          <a-divider v-if="showProperty('spaceSplit')" />
          <a-form-model-item v-show="showProperty('spaceSplit')" :label="$t('chart.space_split')" class="form-item">
            <a-input-number v-model="sizeForm.spaceSplit" :min="0" @change="changeBarSizeCase('spaceSplit')" />
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('hPosition')" :label="$t('chart.h_position')" class="form-item">
            <a-select v-model="sizeForm.hPosition" :placeholder="$t('chart.h_position')" size="small"
              dropdownClassName="props-dropdown" @change="changeBarSizeCase('hPosition')">
              <a-select-option value="start" :title="$t('chart.p_left')">{{ $t('chart.p_left') }}</a-select-option>
              <a-select-option value="center" :title="$t('chart.p_center')">{{ $t('chart.p_center') }}</a-select-option>
              <a-select-option value="end" :title="$t('chart.p_right')">{{ $t('chart.p_right') }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('vPosition')" :label="$t('chart.v_position')" class="form-item">
            <a-select v-model="sizeForm.vPosition" :placeholder="$t('chart.v_position')" size="small"
              dropdownClassName="props-dropdown" @change="changeBarSizeCase('vPosition')">
              <a-select-option value="start" :title="$t('chart.p_top')">{{ $t('chart.p_top') }}</a-select-option>
              <a-select-option value="center" :title="$t('chart.p_center')">{{ $t('chart.p_center') }}</a-select-option>
              <a-select-option value="end" :title="$t('chart.p_bottom')">{{ $t('chart.p_bottom') }}</a-select-option>
            </a-select>
          </a-form-model-item>
        </div>
        <!--text&label-end-->
        <!--scatter-begin-->
        <a-form-model-item v-show="showProperty('scatterSymbol')" :label="$t('chart.bubble_symbol')" class="form-item">
          <a-select v-model="sizeForm.scatterSymbol" :placeholder="$t('chart.line_symbol')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('scatterSymbol')">
            <a-select-option v-for="item in lineSymbolOptions" :key="item.value" :title="item.name" :value="item.value">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('scatterSymbolSize')" :label="$t('chart.bubble_size')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.scatterSymbolSize" show-input :show-input-controls="false" input-size="mini"
            :min="1" :max="40" @change="changeBarSizeCase('scatterSymbolSize')" />
        </a-form-model-item>
        <!--scatter-end-->
        <!--liquid-begin-->
        <a-form-model-item v-show="showProperty('liquidShape')" :label="$t('chart.liquid_shape')" class="form-item">
          <a-select v-model="sizeForm.liquidShape" :placeholder="$t('chart.liquid_shape')" size="small"
            dropdownClassName="props-dropdown" @change="changeBarSizeCase('liquidShape')">
            <a-select-option v-for="item in liquidShapeOptions" :key="item.value" :title="item.name"
              :value="item.value">{{ item.name }}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('liquidMax')" :label="$t('chart.liquid_max')" class="form-item">
          <a-radio-group v-model="sizeForm.liquidMaxType" size="small" @change="changeQuotaField('max')">
            <a-radio-button value="fix">{{ $t('chart.fix') }}</a-radio-button>
            <a-radio-button value="dynamic">{{ $t('chart.dynamic') }}</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item v-if="showProperty('liquidMax') && sizeForm.liquidMaxType === 'fix'"
          class="form-item form-item-slider" label=" " :colon="false">
          <a-input-number v-model="sizeForm.liquidMax" :min="1" @change="changeBarSizeCase('liquidMax')" />
        </a-form-model-item>
        <a-form-model-item v-if="showProperty('liquidMax') && sizeForm.liquidMaxType === 'dynamic'"
          class="form-item form-flex" label=" " :colon="false">
          <a-col :span="12">
            <a-select v-model="sizeForm.liquidMaxField.id" :placeholder="$t('chart.field')" size="small"
              dropdownClassName="props-dropdown" @change="changeQuotaField('max', true)">
              <a-select-option v-for="item in quotaData" :key="item.name" :title="item.name" :value="item.name">
                <span style="float: left">
                  <a-icon component="field_type_text" style="color: #222222;" v-show="item.type === 'TEXT'" />
                  <a-icon component="field_type_number" style="color: cadetblue;" v-show="item.type === 'NUM'" />
                  <a-icon component="date" style="color:cadetblue;" v-show="item.type === 'DATE'" />
                </span>
                <span style="float: left; color: #8492a6; font-size: 12px">{{ item.name }}</span>
              </a-select-option>
            </a-select>
          </a-col>
          <a-col :span="12">
            <a-select v-model="sizeForm.liquidMaxField.summary" :placeholder="$t('chart.summary')" size="small"
              dropdownClassName="props-dropdown" @change="changeQuotaField('max')">
              <a-select-option v-if="validLiquidMaxField" key="sum" value="sum" :title="$t('chart.sum')">{{
                $t('chart.sum')
              }}</a-select-option>
              <a-select-option v-if="validLiquidMaxField" key="avg" value="avg" :title="$t('chart.avg')">{{
                $t('chart.avg')
              }}</a-select-option>
              <a-select-option v-if="validLiquidMaxField" key="max" value="max" :title="$t('chart.max')">{{
                $t('chart.max')
              }}</a-select-option>
              <a-select-option v-if="validLiquidMaxField" key="min" value="min" :title="$t('chart.min')">{{
                $t('chart.min')
              }}</a-select-option>
              <a-select-option v-if="validLiquidMaxField" key="stddev_pop" value="stddev_pop"
                :title="$t('chart.stddev_pop')">{{ $t('chart.stddev_pop') }}</a-select-option>
              <a-select-option v-if="validLiquidMaxField" key="var_pop" value="var_pop" :title="$t('chart.var_pop')">{{
                $t('chart.var_pop') }}</a-select-option>
              <a-select-option key="count" value="count" :title="$t('chart.count')">{{ $t('chart.count')
              }}</a-select-option>
              <a-select-option v-if="liquidMaxField.id !== 'count'" key="count_distinct" value="count_distinct"
                :title="$t('chart.count_distinct')">{{ $t('chart.count_distinct') }}</a-select-option>
            </a-select>
          </a-col>
        </a-form-model-item>

        <a-form-model-item v-show="showProperty('liquidSize')" :label="$t('chart.radar_size')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.liquidSize" show-input :show-input-controls="false" input-size="mini" :min="1"
            :max="100" @change="changeBarSizeCase('liquidSize')" />
        </a-form-model-item>
        <!--liquid-end-->
        <a-form-model-item v-if="showProperty('symbolOpacity')" :label="$t('chart.not_alpha')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.symbolOpacity" show-input :show-input-controls="false" input-size="mini" :min="0"
            :max="10" @change="changeBarSizeCase('symbolOpacity')" />
        </a-form-model-item>

        <a-form-model-item
          v-if="showProperty('symbolStrokeWidth') && sizeForm.scatterSymbol && sizeForm.scatterSymbol !== 'marker'"
          :label="$t('plugin_style.border')" class="form-item form-item-slider">
          <a-slider v-model="sizeForm.symbolStrokeWidth" show-input :show-input-controls="false" input-size="mini"
            :min="0" :max="5" @change="changeBarSizeCase('symbolStrokeWidth')" />
        </a-form-model-item>
      </a-form-model>
      <!--flow-map-start-->
      <!-- <a-form-model ref="flowMapForm" :model="sizeForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <a-form-model-item v-show="showProperty('mapPitch')" :label="$t('chart.map_pitch')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.mapPitch" :min="0" :max="90" @change="changeBarSizeCase('mapPitch')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('mapLineType')" :label="$t('chart.map_line_type')" class="form-item">
          <a-select v-model="sizeForm.mapLineType" @change="changeBarSizeCase('mapLineType')" size="small">
            <a-select-option v-for="item in lineTypeOptions" :key="item.name" :label="item.name" :value="item.value"
              :disabled="checkMapLineType(item)" />
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('mapLineWidth')" :label="$t('chart.map_line_width')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.mapLineWidth" :min="1" :max="10" @change="changeBarSizeCase('mapLineWidth')" />
        </a-form-model-item>
        <a-form-model-item v-show="false" :label="$t('chart.map_line_animate')" class="form-item">
          <a-checkbox v-model="sizeForm.mapLineAnimate" :disabled="checkMapLineAnimate"
            @change="changeBarSizeCase('mapLineAnimate')" />
        </a-form-model-item>
        <div v-if="sizeForm.mapLineAnimate">
          <a-form-model-item v-show="showProperty('mapLineAnimateDuration')"
            :label="$t('chart.map_line_animate_duration')" class="form-item form-item-slider">
            <a-slider v-model="sizeForm.mapLineAnimateDuration" :min="0" :max="20"
              @change="changeBarSizeCase('mapLineAnimateDuration')" />
          </a-form-model-item>
          <a-form-model-item v-show="false" :label="$t('chart.map_line_animate_interval')"
            class="form-item form-item-slider">
            <a-slider v-model="sizeForm.mapLineAnimateInterval" :min="0" :max="1" :step="0.1"
              @change="changeBarSizeCase('mapLineAnimateInterval')" />
          </a-form-model-item>
          <a-form-model-item v-show="false" :label="$t('chart.map_line_animate_trail_length')"
            class="form-item form-item-slider">
            <a-slider v-model="sizeForm.mapLineAnimateTrailLength" :min="0" :max="1" :step="0.1"
              @change="changeBarSizeCase('mapLineAnimateTrailLength')" />
          </a-form-model-item>
        </div>
      </a-form-model> -->
      <!--flow-map-end-->
    </a-row>

  </div>
</template>

<script>
import { CHART_FONT_FAMILY, CHART_FONT_LETTER_SPACE, DEFAULT_SIZE } from '@/pages/smart-page/util/chart'
import { equalsAny } from '@/utils/StringUtils'

export default {
  name: 'SizeSelectorAntV',
  props: {
    param: {
      type: Object,
      required: true
    },
    chart: {
      type: Object,
      required: true
    },
    propertyInner: {
      type: Array,
      required: false,
      default: function () {
        return []
      }
    },
    quotaFields: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      sizeForm: JSON.parse(JSON.stringify(DEFAULT_SIZE)),
      lineSymbolOptions: [
        // { name: this.$t('chart.line_symbol_none'), value: 'none' },
        { name: this.$t('chart.line_symbol_circle'), value: 'circle' },
        { name: this.$t('chart.line_symbol_rect'), value: 'square' },
        { name: this.$t('chart.line_symbol_triangle'), value: 'triangle' },
        { name: this.$t('chart.line_symbol_diamond'), value: 'diamond' }
      ],
      liquidShapeOptions: [
        { name: this.$t('chart.liquid_shape_circle'), value: 'circle' },
        { name: this.$t('chart.liquid_shape_diamond'), value: 'diamond' },
        { name: this.$t('chart.liquid_shape_triangle'), value: 'triangle' },
        { name: this.$t('chart.liquid_shape_pin'), value: 'pin' },
        { name: this.$t('chart.liquid_shape_rect'), value: 'rect' }
      ],
      pageSizeOptions: [
        { name: '10' + this.$t('chart.table_page_size_unit'), value: '10' },
        { name: '20' + this.$t('chart.table_page_size_unit'), value: '20' },
        { name: '50' + this.$t('chart.table_page_size_unit'), value: '50' },
        { name: '100' + this.$t('chart.table_page_size_unit'), value: '100' }
      ],
      fontSize: [],
      alignOptions: [
        { name: this.$t('chart.table_align_left'), value: 'left' },
        { name: this.$t('chart.table_align_center'), value: 'center' },
        { name: this.$t('chart.table_align_right'), value: 'right' }
      ],
      fontFamily: CHART_FONT_FAMILY,
      fontLetterSpace: CHART_FONT_LETTER_SPACE,
      minField: {},
      maxField: {},
      liquidMaxField: {},
      quotaData: [],
      lineTypeOptions: [
        { name: this.$t('chart.map_line_type_line'), value: 'line' },
        { name: this.$t('chart.map_line_type_arc'), value: 'arc' },
        { name: this.$t('chart.map_line_type_arc_3d'), value: 'arc3d' }
      ]
    }
  },
  computed: {
    validLiquidMaxField() {
      return this.isValidField(this.liquidMaxField)
    },
    validMinField() {
      return this.isValidField(this.minField)
    },
    validMaxField() {
      return this.isValidField(this.maxField)
    },
    checkMapLineAnimate() {
      const chart = this.chart
      if (chart.type === 'flow-map') {
        let customAttr = null
        if (Object.prototype.toString.call(chart.customAttr) === '[object Object]') {
          customAttr = JSON.parse(JSON.stringify(chart.customAttr))
        } else {
          customAttr = JSON.parse(chart.customAttr)
        }
        return customAttr.color.mapLineGradient && equalsAny(this.sizeForm.mapLineType, 'line', 'arc')
      }
      return false
    }
  },
  watch: {
    'chart': {
      handler: function () {
        this.initField()
        this.initData()
      }
    },
    'quotaFields': function () {
      this.initField()
    }
  },
  mounted() {
    this.initField()
    this.init()
    this.initData()
  },
  methods: {
    initField() {
      this.quotaData = this.quotaFields.filter(ele => !ele.chartId && ele.id !== 'count')
      if (this.sizeForm.gaugeMinField.id) {
        this.minField = this.getQuotaField(this.sizeForm.gaugeMinField.id)
      }
      if (this.sizeForm.gaugeMaxField.id) {
        this.maxField = this.getQuotaField(this.sizeForm.gaugeMaxField.id)
      }
      if (this.sizeForm.liquidMaxField.id) {
        this.liquidMaxField = this.getQuotaField(this.sizeForm.liquidMaxField.id)
      }
    },
    initData() {
      const chart = JSON.parse(JSON.stringify(this.chart))
      if (chart.customAttr) {
        let customAttr = null
        if (Object.prototype.toString.call(chart.customAttr) === '[object Object]') {
          customAttr = JSON.parse(JSON.stringify(chart.customAttr))
        } else {
          customAttr = JSON.parse(chart.customAttr)
        }
        if (customAttr.size) {
          this.sizeForm = customAttr.size
          this.sizeForm.treemapWidth = this.sizeForm.treemapWidth ? this.sizeForm.treemapWidth : 80
          this.sizeForm.treemapHeight = this.sizeForm.treemapHeight ? this.sizeForm.treemapHeight : 80
          this.sizeForm.radarSize = this.sizeForm.radarSize ? this.sizeForm.radarSize : 80

          this.sizeForm.liquidShape = this.sizeForm.liquidShape ? this.sizeForm.liquidShape : DEFAULT_SIZE.liquidShape
          this.sizeForm.liquidMax = this.sizeForm.liquidMax ? this.sizeForm.liquidMax : DEFAULT_SIZE.liquidMax
          this.sizeForm.liquidSize = this.sizeForm.liquidSize ? this.sizeForm.liquidSize : DEFAULT_SIZE.liquidSize
          this.sizeForm.liquidOutlineBorder = this.sizeForm.liquidOutlineBorder ? this.sizeForm.liquidOutlineBorder : DEFAULT_SIZE.liquidOutlineBorder
          this.sizeForm.liquidOutlineDistance = (this.sizeForm.liquidOutlineDistance || this.sizeForm.liquidOutlineDistance === 0) ? this.sizeForm.liquidOutlineDistance : DEFAULT_SIZE.liquidOutlineDistance
          this.sizeForm.liquidWaveLength = this.sizeForm.liquidWaveLength ? this.sizeForm.liquidWaveLength : DEFAULT_SIZE.liquidWaveLength
          this.sizeForm.liquidWaveCount = this.sizeForm.liquidWaveCount ? this.sizeForm.liquidWaveCount : DEFAULT_SIZE.liquidWaveCount
          this.sizeForm.liquidMaxType = this.sizeForm.liquidMaxType ? this.sizeForm.liquidMaxType : DEFAULT_SIZE.liquidMaxType
          if (!this.sizeForm.liquidMaxField) {
            this.sizeForm.liquidMaxField = DEFAULT_SIZE.liquidMaxField
            this.sizeForm.liquidMaxField.id = this.quotaData[0]?.id
            this.sizeForm.liquidMaxField.summary = 'count'
          }

          this.sizeForm.tablePageMode = this.sizeForm.tablePageMode ? this.sizeForm.tablePageMode : DEFAULT_SIZE.tablePageMode
          this.sizeForm.tablePageSize = this.sizeForm.tablePageSize ? this.sizeForm.tablePageSize : DEFAULT_SIZE.tablePageSize

          this.sizeForm.tableColumnMode = this.sizeForm.tableColumnMode ? this.sizeForm.tableColumnMode : DEFAULT_SIZE.tableColumnMode
          this.sizeForm.tableColumnWidth = this.sizeForm.tableColumnWidth ? this.sizeForm.tableColumnWidth : DEFAULT_SIZE.tableColumnWidth

          this.sizeForm.tableHeaderAlign = this.sizeForm.tableHeaderAlign ? this.sizeForm.tableHeaderAlign : DEFAULT_SIZE.tableHeaderAlign
          this.sizeForm.tableItemAlign = this.sizeForm.tableItemAlign ? this.sizeForm.tableItemAlign : DEFAULT_SIZE.tableItemAlign

          this.sizeForm.showIndex = this.sizeForm.showIndex ? this.sizeForm.showIndex : DEFAULT_SIZE.showIndex
          if (this.sizeForm.indexLabel === null || this.sizeForm.indexLabel === undefined) {
            this.sizeForm.indexLabel = DEFAULT_SIZE.indexLabel
          }

          this.sizeForm.gaugeTickCount = this.sizeForm.gaugeTickCount ? this.sizeForm.gaugeTickCount : DEFAULT_SIZE.gaugeTickCount

          this.sizeForm.gaugeMinType = this.sizeForm.gaugeMinType ? this.sizeForm.gaugeMinType : DEFAULT_SIZE.gaugeMinType
          if (!this.sizeForm.gaugeMinField) {
            this.sizeForm.gaugeMinField = DEFAULT_SIZE.gaugeMinField
            this.sizeForm.gaugeMinField.id = this.quotaData[0]?.id
            this.sizeForm.gaugeMinField.summary = 'count'
          }
          this.sizeForm.gaugeMaxType = this.sizeForm.gaugeMaxType ? this.sizeForm.gaugeMaxType : DEFAULT_SIZE.gaugeMaxType
          if (!this.sizeForm.gaugeMaxField) {
            this.sizeForm.gaugeMaxField = DEFAULT_SIZE.gaugeMaxField
            this.sizeForm.gaugeMaxField.id = this.quotaData[0]?.id
            this.sizeForm.gaugeMaxField.summary = 'count'
          }

          this.sizeForm.quotaFontFamily = this.sizeForm.quotaFontFamily ? this.sizeForm.quotaFontFamily : DEFAULT_SIZE.quotaFontFamily
          this.sizeForm.quotaFontIsBolder = this.sizeForm.quotaFontIsBolder ? this.sizeForm.quotaFontIsBolder : DEFAULT_SIZE.quotaFontIsBolder
          this.sizeForm.quotaFontIsItalic = this.sizeForm.quotaFontIsItalic ? this.sizeForm.quotaFontIsItalic : DEFAULT_SIZE.quotaFontIsItalic
          this.sizeForm.quotaLetterSpace = this.sizeForm.quotaLetterSpace ? this.sizeForm.quotaLetterSpace : DEFAULT_SIZE.quotaLetterSpace
          this.sizeForm.quotaFontShadow = this.sizeForm.quotaFontShadow ? this.sizeForm.quotaFontShadow : DEFAULT_SIZE.quotaFontShadow
          this.sizeForm.dimensionFontFamily = this.sizeForm.dimensionFontFamily ? this.sizeForm.dimensionFontFamily : DEFAULT_SIZE.dimensionFontFamily
          this.sizeForm.dimensionFontIsBolder = this.sizeForm.dimensionFontIsBolder ? this.sizeForm.dimensionFontIsBolder : DEFAULT_SIZE.dimensionFontIsBolder
          this.sizeForm.dimensionFontIsItalic = this.sizeForm.dimensionFontIsItalic ? this.sizeForm.dimensionFontIsItalic : DEFAULT_SIZE.dimensionFontIsItalic
          this.sizeForm.dimensionLetterSpace = this.sizeForm.dimensionLetterSpace ? this.sizeForm.dimensionLetterSpace : DEFAULT_SIZE.dimensionLetterSpace
          this.sizeForm.dimensionFontShadow = this.sizeForm.dimensionFontShadow ? this.sizeForm.dimensionFontShadow : DEFAULT_SIZE.dimensionFontShadow

          this.sizeForm.hPosition = this.sizeForm.hPosition ? this.sizeForm.hPosition : DEFAULT_SIZE.hPosition
          this.sizeForm.vPosition = this.sizeForm.vPosition ? this.sizeForm.vPosition : DEFAULT_SIZE.vPosition

          this.sizeForm.mapPitch = this.sizeForm.mapPitch ? this.sizeForm.mapPitch : DEFAULT_SIZE.mapPitch
          this.sizeForm.mapLineType = this.sizeForm.mapLineType ? this.sizeForm.mapLineType : DEFAULT_SIZE.mapLineType
          this.sizeForm.mapLineWidth = this.sizeForm.mapLineWidth ? this.sizeForm.mapLineWidth : DEFAULT_SIZE.mapLineWidth
          this.sizeForm.mapLineAnimate = this.sizeForm.mapLineAnimate !== undefined ? this.sizeForm.mapLineAnimate : DEFAULT_SIZE.mapLineAnimate
          this.sizeForm.mapLineAnimateDuration = this.sizeForm.mapLineAnimateDuration !== undefined ? this.sizeForm.mapLineAnimateDuration : DEFAULT_SIZE.mapLineAnimateDuration
          this.sizeForm.mapLineAnimateInterval = this.sizeForm.mapLineAnimateInterval !== undefined ? this.sizeForm.mapLineAnimateInterval : DEFAULT_SIZE.mapLineAnimateInterval
          this.sizeForm.mapLineAnimateTrailLength = this.sizeForm.mapLineAnimateTrailLength !== undefined ? this.sizeForm.mapLineAnimateTrailLength : DEFAULT_SIZE.mapLineAnimateTrailLength
        }
      }
    },
    init() {
      const arr = []
      for (let i = 10; i <= 60; i = i + 2) {
        arr.push({
          name: i + '',
          value: i + ''
        })
      }
      this.fontSize = arr
    },
    changeBarSizeCase(modifyName) {
      this.sizeForm['modifyName'] = modifyName
      if (this.sizeForm.gaugeMax <= this.sizeForm.gaugeMin) {
        this.$message.error(this.$t('chart.max_more_than_mix'))
        return
      }
      this.$emit('onSizeChange', this.sizeForm)
    },
    showProperty(property) {
      return this.propertyInner.includes(property)
    },

    changeQuotaField(type, resetSummary) {
      if (type === 'min') {
        if (this.sizeForm.gaugeMinType === 'dynamic') {
          if (!this.sizeForm.gaugeMinField.id) {
            this.sizeForm.gaugeMinField.id = this.quotaData[0]?.name
          }
          if (!this.sizeForm.gaugeMinField.summary) {
            this.sizeForm.gaugeMinField.summary = 'count'
          }
          if (resetSummary) {
            this.sizeForm.gaugeMinField.summary = 'count'
          }
          if (this.sizeForm.gaugeMinField.id && this.sizeForm.gaugeMinField.summary) {
            this.minField = this.getQuotaField(this.sizeForm.gaugeMinField.name)
            this.changeBarSizeCase('gaugeMinField')
          }
        } else {
          if (this.sizeForm.gaugeMaxType === 'dynamic') {
            if (this.sizeForm.gaugeMaxField.id && this.sizeForm.gaugeMaxField.summary) {
              this.changeBarSizeCase('gaugeMinField')
            }
          } else {
            this.changeBarSizeCase('gaugeMinField')
          }
        }
      } else if (type === 'max') {
        if (this.chart.type === 'liquid') {
          if (!this.sizeForm.liquidMaxField.id) {
            this.sizeForm.liquidMaxField.id = this.quotaData[0]?.name
          }
          if (!this.sizeForm.liquidMaxField.summary) {
            this.sizeForm.liquidMaxField.summary = 'count'
          }
          if (resetSummary) {
            this.sizeForm.liquidMaxField.summary = 'count'
          }
          if (this.sizeForm.liquidMaxField.id && this.sizeForm.liquidMaxField.summary) {
            this.maxField = this.getQuotaField(this.sizeForm.liquidMaxField.id)
            this.changeBarSizeCase('liquidMaxField')
          }
        } else {
          if (this.sizeForm.gaugeMaxType === 'dynamic') {
            if (!this.sizeForm.gaugeMaxField.id) {
              this.sizeForm.gaugeMaxField.id = this.quotaData[0]?.name
            }
            if (!this.sizeForm.gaugeMaxField.summary) {
              this.sizeForm.gaugeMaxField.summary = 'count'
            }
            if (resetSummary) {
              this.sizeForm.gaugeMaxField.summary = 'count'
            }
            if (this.sizeForm.gaugeMaxField.id && this.sizeForm.gaugeMaxField.summary) {
              this.maxField = this.getQuotaField(this.sizeForm.gaugeMaxField.id)
              this.changeBarSizeCase('gaugeMaxField')
            }
          } else {
            if (this.sizeForm.gaugeMinType === 'dynamic') {
              if (this.sizeForm.gaugeMinField.id && this.sizeForm.gaugeMinField.summary) {
                this.changeBarSizeCase('gaugeMaxField')
              }
            } else {
              this.changeBarSizeCase('gaugeMaxField')
            }
          }
        }
      }
    },
    getQuotaField(id) {
      if (!id) {
        return {}
      }
      const fields = this.quotaData.filter(ele => {
        return ele.name === id
      })
      if (fields.length === 0) {
        return {}
      } else {
        return fields[0]
      }
    },
    isValidField(field) {
      return (field.type == 'NUM')
    },
    checkMapLineType(item) {
      const chart = this.chart
      if (chart.type === 'flow-map') {
        let customAttr = null
        if (Object.prototype.toString.call(chart.customAttr) === '[object Object]') {
          customAttr = JSON.parse(JSON.stringify(chart.customAttr))
        } else {
          customAttr = JSON.parse(chart.customAttr)
        }
        if (customAttr.color.mapLineGradient && customAttr.size.mapLineAnimate) {
          return equalsAny(item.value, 'line', 'arc')
        }
      }
      return false
    }
  }
}
</script>

<style scoped>
.shape-item {
  padding: 6px;
  border: none;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-item-slider ::v-deep .a-form-model-item__label {
  font-size: 12px;
  line-height: 38px;
}

.form-item ::v-deep .a-form-model-item__label {
  font-size: 12px;
}

.a-select-dropdown__item {
  padding: 0 20px;
}

span {
  font-size: 12px
}

.a-form-model-item {
  margin-bottom: 6px;
}

.a-divider--horizontal {
  margin: 10px 0
}

.divider-style ::v-deep .el-divider__text {
  color: #606266;
  font-size: 12px;
  font-weight: 400;
  padding: 0 10px;
}

.form-flex>>>.a-form-model-item__content {
  display: flex;
}</style>
