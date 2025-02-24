<template>
  <div style="width: 100%">
    <a-form-model ref="sizeFormBar" :model="sizeForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
      :wrapperCol="{ span: 16, offset: 1 }">
      <!--bar-begin-->
      <a-form-model-item v-show="showProperty('barDefault')" :label="$t('chart.adapt')" class="form-item">
        <a-checkbox v-model="sizeForm.barDefault" @change="changeBarSizeCase('barDefault')">{{ $t('chart.adapt')
        }}</a-checkbox>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('barWidth')" :label="$t('chart.bar_width')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.barWidth" :disabled="sizeForm.barDefault" show-input :show-input-controls="false"
          input-size="mini" :min="1" :max="80" @change="changeBarSizeCase('barWidth')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('barGap')" :label="$t('chart.bar_gap')" class="form-item form-item-slider">
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
      <a-form-model-item v-show="showProperty('lineType')" :label="$t('chart.line_type')" class="form-item">
        <a-radio-group v-model="sizeForm.lineType" @change="changeBarSizeCase('lineType')">
          <a-radio-button value="solid">{{ $t('chart.line_type_solid') }}</a-radio-button>
          <a-radio-button value="dashed">{{ $t('chart.line_type_dashed') }}</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('lineSymbol')" :label="$t('chart.line_symbol')" class="form-item">
        <a-select v-model="sizeForm.lineSymbol" :placeholder="$t('chart.line_symbol')"
          @change="changeBarSizeCase('lineSymbol')">
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
        }}</a-checkbox>
      </a-form-model-item>

      <!--line-end-->
      <!--pie-begin-->
      <a-form-model-item v-show="showProperty('pieInnerRadius')" :label="$t('chart.pie_inner_radius')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.pieInnerRadius" show-input :show-input-controls="false" input-size="mini" :min="0"
          :max="100" @change="changeBarSizeCase('pieInnerRadius')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('pieOuterRadius')" :label="$t('chart.pie_outer_radius')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.pieOuterRadius" show-input :show-input-controls="false" input-size="mini" :min="0"
          :max="100" @change="changeBarSizeCase('pieOuterRadius')" />
      </a-form-model-item>

      <span v-show="showProperty('pieRoseType') || showProperty('pieRoseRadius')">
        <a-form-model-item v-show="showProperty('pieRoseType')" :label="$t('chart.rose_type')" class="form-item">
          <a-radio-group v-model="sizeForm.pieRoseType" size="mini" @change="changeBarSizeCase('pieRoseType')">
            <a-radio-button value="radius">{{ $t('chart.radius_mode') }}</a-radio-button>
            <a-radio-button value="area">{{ $t('chart.area_mode') }}</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('pieRoseRadius')" :label="$t('chart.rose_radius')"
          class="form-item form-item-slider">
          <a-slider v-model="sizeForm.pieRoseRadius" show-input :show-input-controls="false" input-size="mini" :min="0"
            :max="100" @change="changeBarSizeCase('pieRoseRadius')" />
        </a-form-model-item>
      </span>
      <!--pie-end-->
      <!--funnel-begin-->

      <a-form-model-item v-show="showProperty('funnelWidth')" :label="$t('chart.funnel_width')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.funnelWidth" show-input :show-input-controls="false" input-size="mini" :min="0"
          :max="100" @change="changeBarSizeCase('funnelWidth')" />
      </a-form-model-item>

      <!--funnel-end-->
      <!--radar-begin-->
      <a-form-model-item v-show="showProperty('radarShape')" :label="$t('chart.shape')" class="form-item">
        <a-radio-group v-model="sizeForm.radarShape" size="mini" @change="changeBarSizeCase('radarShape')">
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
        <a-select v-model="sizeForm.tablePageMode" :placeholder="$t('chart.table_page_mode')"
          @change="changeBarSizeCase('tablePageMode')">
          <a-select-option :title="$t('chart.page_mode_page')" value="page">{{ $t('chart.page_mode_page')
          }}</a-select-option>
          <a-select-option :title="$t('chart.page_mode_pull')" value="pull">{{ $t('chart.page_mode_pull')
          }}</a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tablePageSize') && sizeForm.tablePageMode === 'page'" label-width="100px"
        :label="$t('chart.table_page_size')" class="form-item">
        <a-select v-model="sizeForm.tablePageSize" :placeholder="$t('chart.table_page_size')"
          @change="changeBarSizeCase('tablePageSize')">
          <a-select-option v-for="item in pageSizeOptions" :key="item.value" :title="item.name" :value="item.value">
            {{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableAutoBreakLine')" label-width="100px"
        :label="$t('chart.table_auto_break_line')" class="form-item">
        <a-checkbox v-model="sizeForm.tableAutoBreakLine" @change="changeBarSizeCase('tableAutoBreakLine')">{{
          $t('chart.open') }}</a-checkbox>
        <a-tooltip class="item" effect="dark" placement="bottom">
          <div slot="content">
            {{ $t('chart.table_break_line_tip') }}
          </div>
          <i class="el-icon-info" style="cursor: pointer;color: gray;font-size: 12px;" />
        </a-tooltip>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableTitleFontSize')" label-width="100px"
        :label="$t('chart.table_title_fontsize')" class="form-item">
        <a-select v-model="sizeForm.tableTitleFontSize" :placeholder="$t('chart.table_title_fontsize')"
          @change="changeBarSizeCase('tableTitleFontSize')">
          <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name" :value="option.value">
            {{ option.name }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableItemFontSize')" label-width="100px"
        :label="$t('chart.table_item_fontsize')" class="form-item">
        <a-select v-model="sizeForm.tableItemFontSize" :placeholder="$t('chart.table_item_fontsize')"
          @change="changeBarSizeCase('tableItemFontSize')">
          <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name" :value="option.value">
            {{ option.name }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableTitleHeight')" label-width="100px"
        :label="$t('chart.table_title_height')" class="form-item form-item-slider">
        <a-slider v-model="sizeForm.tableTitleHeight" :min="36" :max="100" show-input :show-input-controls="false"
          input-size="mini" @change="changeBarSizeCase('tableTitleHeight')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableItemHeight')" label-width="100px"
        :label="$t('chart.table_item_height')" class="form-item form-item-slider">
        <a-slider v-model="sizeForm.tableItemHeight" :disabled="sizeForm.tableAutoBreakLine" :min="36" :max="100"
          show-input :show-input-controls="false" input-size="mini" @change="changeBarSizeCase('tableItemHeight')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('tableColumnWidth')" label-width="100px"
        :label="$t('chart.table_column_width_config')" class="form-item form-item-slider">
        <a-slider v-model="sizeForm.tableColumnWidth" :min="10" :max="1000" show-input :show-input-controls="false"
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

      <!--table-end-->
      <!--gauge-begin-->
      <a-form-model-item v-show="showProperty('gaugeMin')" :label="$t('chart.min')" class="form-item form-item-slider">
        <a-input-number v-model="sizeForm.gaugeMin" size="mini" @change="changeBarSizeCase('gaugeMin')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('gaugeMax')" :label="$t('chart.max')" class="form-item form-item-slider">
        <a-input-number v-model="sizeForm.gaugeMax" size="mini" @change="changeBarSizeCase('gaugeMax')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('gaugeStartAngle')" :label="$t('chart.start_angle')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.gaugeStartAngle" show-input :show-input-controls="false" input-size="mini" :min="-360"
          :max="360" @change="changeBarSizeCase('gaugeStartAngle')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('gaugeEndAngle')" :label="$t('chart.end_angle')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.gaugeEndAngle" show-input :show-input-controls="false" input-size="mini" :min="-360"
          :max="360" @change="changeBarSizeCase('gaugeEndAngle')" />
      </a-form-model-item>
      <!--gauge-end-->
      <!--text&label-start-->
      <a-form-model-item v-show="showProperty('quotaFontSize')" :label="$t('chart.quota_font_size')" class="form-item">
        <a-select v-model="sizeForm.quotaFontSize" :placeholder="$t('chart.quota_font_size')"
          @change="changeBarSizeCase('quotaFontSize')">
          <a-select-option v-for="option in fontSize" :key="option.value" :label="option.name" :value="option.value" />
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('quotaFontFamily')" :label="$t('chart.quota_font_family')"
        class="form-item">
        <a-select v-model="sizeForm.quotaFontFamily" :placeholder="$t('chart.quota_font_family')"
          @change="changeBarSizeCase('quotaFontFamily')">
          <a-select-option v-for="option in fontFamily" :key="option.value" :label="option.name" :value="option.value" />
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('quotaFontStyle')" :label="$t('chart.quota_text_style')" class="form-item">
        <a-checkbox v-model="sizeForm.quotaFontIsItalic" @change="changeBarSizeCase('quotaFontIsItalic')">{{
          $t('chart.italic') }}</a-checkbox>
        <a-checkbox v-model="sizeForm.quotaFontIsBolder" @change="changeBarSizeCase('quotaFontIsBolder')">{{
          $t('chart.bolder') }}</a-checkbox>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('quotaLetterSpace')" :label="$t('chart.quota_letter_space')"
        class="form-item">
        <a-select v-model="sizeForm.quotaLetterSpace" :placeholder="$t('chart.quota_letter_space')"
          @change="changeBarSizeCase('quotaLetterSpace')">
          <a-select-option v-for="option in fontLetterSpace" :key="option.value" :label="option.name"
            :value="option.value" />
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
          <a-select v-model="sizeForm.dimensionFontSize" :placeholder="$t('chart.dimension_font_size')"
            @change="changeBarSizeCase('dimensionFontSize')">
            <a-select-option v-for="option in fontSize" :key="option.value" :label="option.name" :value="option.value" />
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('dimensionFontFamily')" :label="$t('chart.dimension_font_family')"
          class="form-item">
          <a-select v-model="sizeForm.dimensionFontFamily" :placeholder="$t('chart.dimension_font_family')"
            @change="changeBarSizeCase('dimensionFontFamily')">
            <a-select-option v-for="option in fontFamily" :key="option.value" :label="option.name"
              :value="option.value" />
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
            @change="changeBarSizeCase('dimensionLetterSpace')">
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
          <a-input-number v-model="sizeForm.spaceSplit" :min="0" size="mini" @change="changeBarSizeCase('spaceSplit')" />
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('hPosition')" :label="$t('chart.h_position')" class="form-item">
          <a-select v-model="sizeForm.hPosition" :placeholder="$t('chart.h_position')"
            @change="changeBarSizeCase('hPosition')">
            <a-select-option value="start" :label="$t('chart.p_left')">{{ $t('chart.p_left') }}</a-select-option>
            <a-select-option value="center" :label="$t('chart.p_center')">{{ $t('chart.p_center') }}</a-select-option>
            <a-select-option value="end" :label="$t('chart.p_right')">{{ $t('chart.p_right') }}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item v-show="showProperty('vPosition')" :label="$t('chart.v_position')" class="form-item">
          <a-select v-model="sizeForm.vPosition" :placeholder="$t('chart.v_position')"
            @change="changeBarSizeCase('vPosition')">
            <a-select-option value="start" :label="$t('chart.p_top')">{{ $t('chart.p_top') }}</a-select-option>
            <a-select-option value="center" :label="$t('chart.p_center')">{{ $t('chart.p_center') }}</a-select-option>
            <a-select-option value="end" :label="$t('chart.p_bottom')">{{ $t('chart.p_bottom') }}</a-select-option>
          </a-select>
        </a-form-model-item>
      </div>
      <!--text&label-end-->
      <!--scatter-begin-->
      <a-form-model-item v-show="showProperty('scatterSymbol')" :label="$t('chart.bubble_symbol')" class="form-item">
        <a-select v-model="sizeForm.scatterSymbol" :placeholder="$t('chart.line_symbol')"
          @change="changeBarSizeCase('scatterSymbol')">
          <a-select-option v-for="item in lineSymbolOptions" :key="item.value" :title="item.name" :value="item.value">{{
            item.value }}</a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('scatterSymbolSize')" :label="$t('chart.bubble_size')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.scatterSymbolSize" show-input :show-input-controls="false" input-size="mini" :min="1"
          :max="40" @change="changeBarSizeCase('scatterSymbolSize')" />
      </a-form-model-item>

      <!--scatter-end-->
      <!--treemap-begin-->
      <a-form-model-item v-show="showProperty('treemapWidth')" :label="$t('chart.width')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.treemapWidth" show-input :show-input-controls="false" input-size="mini" :min="0"
          :max="100" @change="changeBarSizeCase('treemapWidth')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('treemapHeight')" :label="$t('chart.height')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.treemapHeight" show-input :show-input-controls="false" input-size="mini" :min="0"
          :max="100" @change="changeBarSizeCase('treemapHeight')" />
      </a-form-model-item>
      <!--treemap-end-->
      <!--chart-mix-start-->
      <span v-show="showProperty('mix')">
        <a-divider content-position="center" class="divider-style">{{ $t('chart.chart_bar') }}</a-divider>
        <a-form-model-item :label="$t('chart.adapt')" class="form-item">
          <a-checkbox v-model="sizeForm.barDefault" @change="changeBarSizeCase('barDefault')">{{ $t('chart.adapt')
          }}</a-checkbox>
        </a-form-model-item>
        <a-form-model-item :label="$t('chart.bar_width')" class="form-item form-item-slider">
          <a-slider v-model="sizeForm.barWidth" :disabled="sizeForm.barDefault" show-input :show-input-controls="false"
            input-size="mini" :min="1" :max="80" @change="changeBarSizeCase('barWidth')" />
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
        <a-form-model-item :label="$t('chart.line_type')" class="form-item">
          <a-radio-group v-model="sizeForm.lineType" @change="changeBarSizeCase('lineType')">
            <a-radio-button value="solid">{{ $t('chart.line_type_solid') }}</a-radio-button>
            <a-radio-button value="dashed">{{ $t('chart.line_type_dashed') }}</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item :label="$t('chart.line_symbol')" class="form-item">
          <a-select v-model="sizeForm.lineSymbol" :placeholder="$t('chart.line_symbol')"
            @change="changeBarSizeCase('lineSymbol')">
            <a-select-option v-for="item in lineSymbolOptions" :key="item.value" :title="item.name" :value="item.value">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item :label="$t('chart.line_symbol_size')" class="form-item form-item-slider">
          <a-slider v-model="sizeForm.lineSymbolSize" show-input :show-input-controls="false" input-size="mini" :min="0"
            :max="20" @change="changeBarSizeCase" />
        </a-form-model-item>
        <a-form-model-item :label="$t('chart.line_smooth')" class="form-item">
          <a-checkbox v-model="sizeForm.lineSmooth" @change="changeBarSizeCase('lineSmooth')">{{
            $t('chart.line_smooth') }}</a-checkbox>
        </a-form-model-item>
        <a-divider content-position="center" class="divider-style">{{ $t('chart.chart_scatter') }}</a-divider>
        <a-form-model-item :label="$t('chart.bubble_symbol')" class="form-item">
          <a-select v-model="sizeForm.scatterSymbol" :placeholder="$t('chart.line_symbol')"
            @change="changeBarSizeCase('scatterSymbol')">
            <a-select-option v-for="item in lineSymbolOptions" :key="item.value" :title="item.name" :value="item.value">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item :label="$t('chart.bubble_size')" class="form-item form-item-slider">
          <a-slider v-model="sizeForm.scatterSymbolSize" show-input :show-input-controls="false" input-size="mini"
            :min="1" :max="40" @change="changeBarSizeCase('scatterSymbolSize')" />
        </a-form-model-item>
      </span>
      <!--chart-mix-end-->
      <!--liquid-begin-->
      <a-form-model-item v-show="showProperty('liquidShape')" :label="$t('chart.liquid_shape')" class="form-item">
        <a-select v-model="sizeForm.liquidShape" :placeholder="$t('chart.liquid_shape')"
          @change="changeBarSizeCase('liquidShape')">
          <a-select-option v-for="item in liquidShapeOptions" :key="item.value" :title="item.name" :value="item.value">
            {{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('liquidMax')" :label="$t('chart.liquid_max')"
        class="form-item form-item-slider">
        <a-input-number v-model="sizeForm.liquidMax" :min="1" size="mini" @change="changeBarSizeCase('liquidMax')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('liquidSize')" :label="$t('chart.radar_size')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.liquidSize" show-input :show-input-controls="false" input-size="mini" :min="1"
          :max="100" @change="changeBarSizeCase('liquidSize')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('liquidOutlineBorder')" :label="$t('chart.liquid_outline_border')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.liquidOutlineBorder" show-input :show-input-controls="false" input-size="mini"
          :min="1" :max="20" @change="changeBarSizeCase('liquidOutlineBorder')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('liquidOutlineDistance')" :label="$t('chart.liquid_outline_distance')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.liquidOutlineDistance" show-input :show-input-controls="false" input-size="mini"
          :min="0" :max="20" @change="changeBarSizeCase('liquidOutlineDistance')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('liquidWaveLength')" :label="$t('chart.liquid_wave_length')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.liquidWaveLength" show-input :show-input-controls="false" input-size="mini" :min="10"
          :max="500" @change="changeBarSizeCase('liquidWaveLength')" />
      </a-form-model-item>
      <a-form-model-item v-show="showProperty('liquidWaveCount')" :label="$t('chart.liquid_wave_count')"
        class="form-item form-item-slider">
        <a-slider v-model="sizeForm.liquidWaveCount" show-input :show-input-controls="false" input-size="mini" :min="2"
          :max="10" @change="changeBarSizeCase('liquidWaveCount')" />
      </a-form-model-item>

      <!--liquid-end-->
    </a-form-model>
  </div>
</template>

<script>
import { CHART_FONT_FAMILY, CHART_FONT_LETTER_SPACE, DEFAULT_SIZE } from '@/pages/smart-page/util/chart'
export default {
  name: 'SizeSelector',
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
    }
  },
  data() {
    return {
      sizeForm: JSON.parse(JSON.stringify(DEFAULT_SIZE)),
      lineSymbolOptions: [
        { name: this.$t('chart.line_symbol_none'), value: 'none' },
        { name: this.$t('chart.line_symbol_emptyCircle'), value: 'emptyCircle' },
        { name: this.$t('chart.line_symbol_circle'), value: 'circle' },
        { name: this.$t('chart.line_symbol_rect'), value: 'rect' },
        { name: this.$t('chart.line_symbol_roundRect'), value: 'roundRect' },
        { name: this.$t('chart.line_symbol_triangle'), value: 'triangle' },
        { name: this.$t('chart.line_symbol_diamond'), value: 'diamond' },
        { name: this.$t('chart.line_symbol_pin'), value: 'pin' },
        { name: this.$t('chart.line_symbol_arrow'), value: 'arrow' }
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
      fontFamily: CHART_FONT_FAMILY,
      fontLetterSpace: CHART_FONT_LETTER_SPACE
    }
  },
  watch: {
    'chart': {
      handler: function () {
        this.initData()
      }
    }
  },
  mounted() {
    this.init()
    this.initData()
  },
  methods: {
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

          this.sizeForm.tablePageMode = this.sizeForm.tablePageMode ? this.sizeForm.tablePageMode : DEFAULT_SIZE.tablePageMode
          this.sizeForm.tablePageSize = this.sizeForm.tablePageSize ? this.sizeForm.tablePageSize : DEFAULT_SIZE.tablePageSize

          this.sizeForm.showIndex = this.sizeForm.showIndex ? this.sizeForm.showIndex : DEFAULT_SIZE.showIndex
          if (this.sizeForm.indexLabel === null || this.sizeForm.indexLabel === undefined) {
            this.sizeForm.indexLabel = DEFAULT_SIZE.indexLabel
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

          this.sizeForm.tableAutoBreakLine = this.sizeForm.tableAutoBreakLine ? this.sizeForm.tableAutoBreakLine : DEFAULT_SIZE.tableAutoBreakLine
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
</style>
