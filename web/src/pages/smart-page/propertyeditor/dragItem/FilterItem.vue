<template>
  <span style="position: relative;display: inline-block;width:100%;">

    <a-icon type="delete" style="position: absolute;top: 6px;right: 24px;color: #878d9f;cursor: pointer;z-index: 1;"
      @click="removeItem" />

    <a-dropdown :trigger="['click']" size="mini" :overlayStyle="{ 'min-width': '200px', left: '20px' }">

      <span class="ant-dropdown-link">
        <a-tag size="small" class="item-axis" color="blue">
          <span style="float: left">
            <a-icon component="field_type_text" style="color: #222222;" v-show="item.type === 'TEXT'" />
            <a-icon component="date" style="color:cadetblue;" v-show="item.type === 'DATE'" />
            <a-icon component="field_type_number" style="color: cadetblue;" v-show="item.type === 'NUM'" />
          </span>
          <span class="item-span-style" :title="item.name">{{ item.name }}</span>

        </a-tag>
        <a class="ant-dropdown-link" @click="e => e.preventDefault()" style="position: absolute;top: 4px;right: 6px;">
          <a-icon type="down" />
        </a>
      </span>

      <a-menu slot="overlay" @click="clickItem">
        <a-menu-item icon="el-icon-files" key="filter" style="font-size:12px;">
          <span>{{ $t('chart.filter') }}...</span>
        </a-menu-item>
        <a-menu-item icon="el-icon-delete" divided key="remove" style="font-size:12px;">
          <span>{{ $t('chart.delete') }}</span>
        </a-menu-item>
      </a-menu>
    </a-dropdown>
  </span>
</template>

<script>
import { getItemType } from '@/pages/smart-page/propertyeditor/dragItem/utils'

export default {
  name: 'FilterItem',
  components: { },
  props: {
    chart: {
      type: Object,
      required: true
    },
    item: {
      type: Object,
      required: true
    },
    index: {
      type: Number,
      required: true
    },
    dimensionData: {
      type: Array,
      required: true
    },
    quotaData: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      tagType: 'success'
    }
  },
  watch: {
    dimensionData: function () {
      this.getItemTagType()
    },
    quotaData: function () {
      this.getItemTagType()
    },
    item: function () {
      this.getItemTagType()
    }
  },
  mounted() {
  },
  beforeDestroy() {
  },
  methods: {
    clickItem(e) {
      if (!e) {
        return
      }
      const command = e.key
      switch (command) {
        case 'remove':
          this.removeItem()
          break
        case 'filter':
          this.editFilter()
          break
        default:
          break
      }
    },
    editFilter() {
      this.item.index = this.index
      this.$emit('editItemFilter', this.item)
    },
    removeItem() {
      this.item.index = this.index
      this.$emit('onFilterItemRemove', this.item)
    },
    getItemTagType() {
      this.tagType = getItemType(this.dimensionData, this.quotaData, this.item)
    }
  }
}
</script>

<style lang="less" scoped>
.item-axis {
  padding: 1px 6px;
  margin: 0 3px 2px 3px;
  text-align: left;
  height: 24px;
  line-height: 22px;
  display: flex;
  border-radius: 4px;
  box-sizing: border-box;
  white-space: nowrap;
}

.item-axis:hover {
  background-color: #fdfdfd;
  cursor: pointer;
}

span {
  font-size: 12px;
}

.summary-span {
  margin-left: 4px;
  color: #878d9f;
  position: absolute;
  right: 25px;
}

.inner-dropdown-menu {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%
}

.item-span-style {
  display: inline-block;
  width: 100px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.summary-span-item {
  margin-left: 4px;
  color: #878d9f;
}
</style>
