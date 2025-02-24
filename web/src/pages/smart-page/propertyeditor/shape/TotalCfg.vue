<template>
  <div style="width: 100%">
    <a-col style="flex: 1 1;">
      <a-form-model ref="totalForm" :model="totalForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <a-divider v-if="showProperty('row')" content-position="center" class="divider-style">{{ $t('chart.row_cfg')
        }}</a-divider>
        <a-form-model-item v-show="showProperty('row')" :label="$t('chart.total_show')" class="form-item">
          <a-checkbox v-model="totalForm.row.showGrandTotals" @change="changeTotalCfg('row')">{{ $t('chart.show')
          }}</a-checkbox>
        </a-form-model-item>
        <div v-show="showProperty('row') && totalForm.row.showGrandTotals">
          <!-- <a-form-model-item :label="$t('chart.total_position')" class="form-item">
            <a-radio-group v-model="totalForm.row.reverseLayout" @change="changeTotalCfg('row')">
              <a-radio :value="true">{{ $t('chart.total_pos_top') }}</a-radio>
              <a-radio :value="false">{{ $t('chart.total_pos_bottom') }}</a-radio>
            </a-radio-group>
          </a-form-model-item> -->
          <a-form-model-item :label="$t('chart.total_label')" class="form-item">
            <a-input v-model="totalForm.row.label" style="width: 160px;" :placeholder="$t('chart.total_label')"
              size="small" clearable @change="changeTotalCfg('row')" />
          </a-form-model-item>
          <a-form-model-item :label="$t('chart.aggregation')" class="form-item">
            <a-select v-model="totalForm.row.calcTotals.aggregation" class="form-item-select"
              :placeholder="$t('chart.aggregation')" size="small" dropdownClassName="props-dropdown"
              @change="changeTotalCfg('row')">
              <a-select-option v-for="option in aggregations" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <!-- <a-form-model-item v-if="chart.type === 'table-pivot'" :label="$t('chart.total_sort')" class="form-item">
            <a-radio-group v-model="totalForm.row.totalSort" @change="changeTotalCfg('row')">
              <a-radio value="none">{{ $t('chart.total_sort_none') }}</a-radio>
              <a-radio value="asc">{{ $t('chart.total_sort_asc') }}</a-radio>
              <a-radio value="desc">{{ $t('chart.total_sort_desc') }}</a-radio>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-if="chart.type === 'table-pivot' && totalForm.row.totalSort !== 'none'"
            :label="$t('chart.total_sort_field')" class="form-item">
            <a-select v-model="totalForm.row.totalSortField" class="form-item-select"
              :placeholder="$t('chart.total_sort_field')" size="small" dropdownClassName="props-dropdown"
              @change="changeTotalCfg('row')">
              <a-select-option v-for="option in totalSortFields" :key="option.dataeaseName" :title="option.name"
                :value="option.dataeaseName">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item> -->
        </div>

        <a-form-model-item v-show="showProperty('row')" :label="$t('chart.sub_total_show')" class="form-item">
          <a-checkbox v-model="totalForm.row.showSubTotals" :disabled="rowNum < 2" @change="changeTotalCfg('row')">{{
            $t('chart.show') }}</a-checkbox>
        </a-form-model-item>
        <div v-show="showProperty('row') && totalForm.row.showSubTotals">
          <!-- <a-form-model-item :label="$t('chart.total_position')" class="form-item">
            <a-radio-group v-model="totalForm.row.reverseSubLayout" :disabled="rowNum < 2"
              @change="changeTotalCfg('row')">
              <a-radio :value="true">{{ $t('chart.total_pos_top') }}</a-radio>
              <a-radio :value="false">{{ $t('chart.total_pos_bottom') }}</a-radio>
            </a-radio-group>
          </a-form-model-item> -->
          <a-form-model-item :label="$t('chart.total_label')" class="form-item">
            <a-input v-model="totalForm.row.subLabel" :disabled="rowNum < 2" style="width: 160px;"
              :placeholder="$t('chart.total_label')" size="small" clearable @change="changeTotalCfg" />
          </a-form-model-item>
          <a-form-model-item :label="$t('chart.aggregation')" class="form-item">
            <a-select v-model="totalForm.row.calcSubTotals.aggregation" :disabled="rowNum < 2" class="form-item-select"
              :placeholder="$t('chart.aggregation')" size="small" dropdownClassName="props-dropdown"
              @change="changeTotalCfg('row')">
              <a-select-option v-for="option in aggregations" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
        </div>

        <a-divider v-if="showProperty('col')" content-position="center" class="divider-style">{{ $t('chart.col_cfg')
        }}</a-divider>
        <a-form-model-item v-show="showProperty('col')" :label="$t('chart.total_show')" class="form-item">
          <a-checkbox v-model="totalForm.col.showGrandTotals" @change="changeTotalCfg('col')">{{ $t('chart.show')
          }}</a-checkbox>
        </a-form-model-item>
        <div v-show="showProperty('col') && totalForm.col.showGrandTotals">
          <!-- <a-form-model-item :label="$t('chart.total_position')" class="form-item">
            <a-radio-group v-model="totalForm.col.reverseLayout" @change="changeTotalCfg('col')">
              <a-radio :value="true">{{ $t('chart.total_pos_left') }}</a-radio>
              <a-radio :value="false">{{ $t('chart.total_pos_right') }}</a-radio>
            </a-radio-group>
          </a-form-model-item> -->
          <a-form-model-item :label="$t('chart.total_label')" class="form-item">
            <a-input v-model="totalForm.col.label" style="width: 160px;" :placeholder="$t('chart.total_label')"
              size="small" clearable @change="changeTotalCfg('col')" />
          </a-form-model-item>
          <a-form-model-item :label="$t('chart.aggregation')" class="form-item">
            <a-select v-model="totalForm.col.calcTotals.aggregation" class="form-item-select"
              :placeholder="$t('chart.aggregation')" size="small" dropdownClassName="props-dropdown"
              @change="changeTotalCfg('col')">
              <a-select-option v-for="option in aggregations" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <!-- <a-form-model-item v-if="chart.type === 'table-pivot'" :label="$t('chart.total_sort')" class="form-item">
            <a-radio-group v-model="totalForm.col.totalSort" @change="changeTotalCfg('col')">
              <a-radio value="none">{{ $t('chart.total_sort_none') }}</a-radio>
              <a-radio value="asc">{{ $t('chart.total_sort_asc') }}</a-radio>
              <a-radio value="desc">{{ $t('chart.total_sort_desc') }}</a-radio>
            </a-radio-group>
          </a-form-model-item> -->
          <!-- <a-form-model-item v-show="false && chart.type === 'table-pivot' && totalForm.col.totalSort !== 'none'"
            :label="$t('chart.total_sort_field')" class="form-item">
            <a-select v-model="totalForm.col.totalSortField" class="form-item-select"
              :placeholder="$t('chart.total_sort_field')" size="small" dropdownClassName="props-dropdown"
              @change="changeTotalCfg('col')">
              <a-select-option v-for="option in totalSortFields" :key="option.dataeaseName" :title="option.name"
                :value="option.dataeaseName">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item> -->
        </div>

        <a-form-model-item v-show="showProperty('col')" :label="$t('chart.sub_total_show')" class="form-item">
          <a-checkbox v-model="totalForm.col.showSubTotals" :disabled="colNum < 2" @change="changeTotalCfg('col')">{{
            $t('chart.show') }}</a-checkbox>
        </a-form-model-item>
        <div v-show="showProperty('col') && totalForm.col.showSubTotals">
          <!-- <a-form-model-item :label="$t('chart.total_position')" class="form-item">
            <a-radio-group v-model="totalForm.col.reverseSubLayout" :disabled="colNum < 2"
              @change="changeTotalCfg('col')">
              <a-radio :value="true">{{ $t('chart.total_pos_left') }}</a-radio>
              <a-radio :value="false">{{ $t('chart.total_pos_right') }}</a-radio>
            </a-radio-group>
          </a-form-model-item> -->
          <a-form-model-item :label="$t('chart.total_label')" class="form-item">
            <a-input v-model="totalForm.col.subLabel" :disabled="colNum < 2" style="width: 160px;"
              :placeholder="$t('chart.total_label')" size="small" clearable @change="changeTotalCfg('col')" />
          </a-form-model-item>
          <a-form-model-item :label="$t('chart.aggregation')" class="form-item">
            <a-select v-model="totalForm.col.calcSubTotals.aggregation" :disabled="colNum < 2" class="form-item-select"
              :placeholder="$t('chart.aggregation')" size="small" dropdownClassName="props-dropdown"
              @change="changeTotalCfg('col')">
              <a-select-option v-for="option in aggregations" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
        </div>
      </a-form-model>
    </a-col>
  </div>
</template>

<script>
import { DEFAULT_TOTAL } from '@/pages/smart-page/util/chart'

export default {
  name: 'TotalCfg',
  props: {
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
      totalForm: JSON.parse(JSON.stringify(DEFAULT_TOTAL)),
      aggregations: [
        { name: this.$t('chart.sum'), value: 'SUM' },
        { name: this.$t('chart.avg'), value: 'AVG' },
        { name: this.$t('chart.max'), value: 'MAX' },
        { name: this.$t('chart.min'), value: 'MIN' }
      ],
      totalSortFields: []
    }
  },
  computed: {
    rowNum() {
      const chart = JSON.parse(JSON.stringify(this.chart))
      if (chart.xaxisExt) {
        let arr = null
        if (Object.prototype.toString.call(chart.xaxisExt) === '[object Array]') {
          arr = JSON.parse(JSON.stringify(chart.xaxisExt))
        } else {
          arr = JSON.parse(chart.xaxisExt)
        }
        return arr.length
      }
      return 0
    },
    colNum() {
      const chart = JSON.parse(JSON.stringify(this.chart))
      if (chart.xaxis) {
        let arr = null
        if (Object.prototype.toString.call(chart.xaxis) === '[object Array]') {
          arr = JSON.parse(JSON.stringify(chart.xaxis))
        } else {
          arr = JSON.parse(chart.xaxis)
        }
        return arr.length
      }
      return 0
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
        if (customAttr.totalCfg) {
          this.totalForm = customAttr.totalCfg
        } else {
          this.totalForm = JSON.parse(JSON.stringify(DEFAULT_TOTAL))
        }

        this.totalForm.row.totalSort = this.totalForm.row.totalSort ? this.totalForm.row.totalSort : DEFAULT_TOTAL.row.totalSort
        this.totalForm.row.totalSortField = this.totalForm.row.totalSortField ? this.totalForm.row.totalSortField : DEFAULT_TOTAL.row.totalSortField
        this.totalForm.col.totalSort = this.totalForm.col.totalSort ? this.totalForm.col.totalSort : DEFAULT_TOTAL.col.totalSort
        this.totalForm.col.totalSortField = this.totalForm.col.totalSortField ? this.totalForm.col.totalSortField : DEFAULT_TOTAL.col.totalSortField
      }
      // 解析表格的指标
      if (chart.yaxis) {
        if (Object.prototype.toString.call(chart.yaxis) === '[object Array]') {
          this.totalSortFields = JSON.parse(JSON.stringify(chart.yaxis))
        } else {
          this.totalSortFields = JSON.parse(chart.yaxis)
        }
        if (this.totalSortFields.length > 0) {
          if (this.resetTotalSort(this.totalForm.row.totalSortField)) {
            this.totalForm.row.totalSortField = this.totalSortFields[0].dataeaseName
          }
          this.totalForm.col.totalSortField = this.totalSortFields[0].dataeaseName
        } else {
          this.totalForm.row.totalSortField = ''
          this.totalForm.col.totalSortField = ''
        }
      }
    },
    changeTotalCfg(modifyName) {
      this.totalForm['modifyName'] = modifyName
      this.$emit('onTotalCfgChange', this.totalForm)
    },
    showProperty(property) {
      return this.propertyInner.includes(property)
    },
    resetTotalSort(field) {
      if (field === '') {
        return true
      }
      const sortFieldList = []
      this.totalSortFields.forEach(ele => {
        sortFieldList.push(ele.dataeaseName)
      })
      return sortFieldList.indexOf(field) === -1
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

.form-item-select {
  width: 160px !important;
}
</style>
