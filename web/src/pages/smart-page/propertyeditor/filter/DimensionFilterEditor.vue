<template>
  <a-col>
    <a-button
      icon="a-icon-plus"
      circle
      size="mini"
      style="margin-bottom: 10px;"
      @click="addFilter"
    />
    <div style="max-height: 50vh;overflow-y: auto;">
      <a-row
        v-for="(f,index) in item.filter"
        :key="index"
        class="filter-item"
      >
        <a-col :span="4">
          <span>{{ item.name }}</span>
        </a-col>
        <a-col :span="8">
          <a-select
            v-model="f.term"
            size="mini"
          >
            <a-option-group
              v-for="(group,idx) in options"
              :key="idx"
              :label="group.label"
            >
              <a-option
                v-for="opt in group.options"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </a-option-group>
          </a-select>
        </a-col>
        <a-col :span="6">
          <a-input
            v-show="!f.term.includes('null') && !f.term.includes('empty')"
            v-model="f.value"
            class="value-item"
            :placeholder="$t('chart.condition')"
            size="mini"
            clearable
          />
        </a-col>
        <a-col :span="6">
          <a-button
            type="text"
            icon="a-icon-delete"
            circle
            style="float: right"
            @click="removeFilter(index)"
          />
        </a-col>
      </a-row>
    </div>
  </a-col>
</template>

<script>
export default {
  name: 'DimensionFilterEditor',
  props: {
    item: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      options: [
        {
          label: '',
          options: [{
            value: 'eq',
            label: this.$t('chart.filter_eq')
          }, {
            value: 'not_eq',
            label: this.$t('chart.filter_not_eq')
          }]
        },
        {
          label: '',
          options: [{
            value: 'like',
            label: this.$t('chart.filter_like')
          }, {
            value: 'not like',
            label: this.$t('chart.filter_not_like')
          }]
        },
        {
          label: '',
          options: [{
            value: 'null',
            label: this.$t('chart.filter_null')
          }, {
            value: 'not_null',
            label: this.$t('chart.filter_not_null')
          }]
        },
        {
          label: '',
          options: [{
            value: 'empty',
            label: this.$t('chart.filter_empty')
          }, {
            value: 'not_empty',
            label: this.$t('chart.filter_not_empty')
          }]
        }
      ]
    }
  },
  mounted() {
  },
  methods: {
    addFilter() {
      this.item.filter.push({
        term: 'eq',
        value: ''
      })
    },
    removeFilter(index) {
      this.item.filter.splice(index, 1)
    }
  }
}
</script>

<style scoped>
.filter-item {
  width: 100%;
  border-radius: 4px;
  border: 1px solid #DCDFE6;
  padding: 4px 14px;
  margin-bottom: 10px;
  display: flex;
  justify-content: left;
  align-items: center;
}

.form-item ::v-deep .a-form-item__label {
  font-size: 12px;
}

span {
  font-size: 12px;
}

.value-item ::v-deep .a-input {
  position: relative;
  display: inline-block;
  width: 80px !important;
}

.a-select-dropdown__item {
  padding: 0 20px;
  font-size: 12px;
}
</style>
