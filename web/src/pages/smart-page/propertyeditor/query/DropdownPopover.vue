
<template>
  <a-popover v-model="dropdownVisible" trigger="click" placement="top" :overlayClassName="'dropdown-popover'">
      <template slot="content">
          <div style="width: 480px;height: 470px;">
            <div class="container">
    <div class="header">
      <div class="header-left">字段</div>
      <div class="header-right">已添加({{ selectedItems.length }})</div>
    </div>
    <div class="main">
      <div class="left">
        <a-input v-model="searchText" class="search-box" placeholder="请输入查询内容">
          <a-icon slot="prefix" type="search" /></a-input>
        
        <div
          v-for="(option, index) in filteredOptions"
          :key="index"
          class="option-item"
          :class="{ 'option-selected': selectedItems.some(item => item.id === option.id) }"
          @click="() => toggleSelection(option)"
        >
          <a-tooltip :title="option.id">
            <span class="check-space">
              <a-icon v-if="selectedItems.some(item => item.id === option.id)" type="check" />
            </span>
            <span style="width:160px;display: inline-block;">{{ option.text }}</span>
          </a-tooltip>
        </div>
        <a-button size="small" class="add-all-button" @click="addAllFields">添加左侧全部字段值</a-button>
      </div>
      <!-- <a-divider type="vertical" /> -->
      <div class="right">
        <div
          v-for="(item, index) in sortedSelectedItems"
          :key="index"
          class="option-item"
          @click="() => removeSelected(item)"
        >
          <span>{{ item.text }}</span>
        </div>
        <div class="footer">
          <a-checkbox size="small" v-model="excludeModeDefault">排除模式</a-checkbox>
          <a-button size="small" type="link" icon="delete" @click="clearSelection">清空</a-button>
        </div>
      </div>
    </div>
    <div class="footer" style="justify-content: flex-end;">
      <!-- <a-checkbox size="small" v-model="sortOrder">按添加顺序排序</a-checkbox> -->
      <div>
        <a-button size="small" @click="cancel" style="margin-right: 10px;">取消</a-button>
        <a-button size="small" type="primary" @click="confirm">确认</a-button>
      </div>
    </div>
  </div>
          </div>
      </template> 
      <a-select v-model="selectedValues" placeholder="请选择（多选）" mode="multiple" size="small" :open="false" :showArrow="true" style="width:160px;margin-left:107px" >
        <!-- <a-icon slot="suffixIcon" type="smile" /> -->
        <a-select-option v-for="option in filteredOptions" :key="option.id" :value="option.id">{{ option.text }}</a-select-option>
      </a-select>
  </a-popover>
</template>


<script>
import moment from 'moment';

export default {
  name: 'DropdownPopover',
  props: {
    options:{
      type:Array,
    },
    defaultValue:{
      type:Array,
    },
    excludeMode:{
      type:Boolean,
      default:false
    }
  },
  data() {
    return {
      selectedItems: [],
      selectedValues: [],
      sortOrder: false,
      excludeModeDefault: false,
      dropdownVisible:false,
      searchText: '',
    }
  },
  watch:{
    dropdownVisible(newVal){
      if(newVal&&this.defaultValue){
        this.selectedItems=this.options.filter(option=>this.defaultValue?.includes(option.id))||[]
      }
    }
  },
  mounted() {
    this.excludeModeDefault = this.excludeMode
    this.selectedItems=this.options.filter(option=>this.defaultValue?.includes(option.id))||[]

  },
  computed: {
    filteredOptions() {
      return this.searchText
        ? this.options.filter(option =>
            option.text.toLowerCase().includes(this.searchText.toLowerCase())
          )
        : this.options;
    },
    sortedSelectedItems() {
      return this.sortOrder ? this.selectedItems.slice() : this.selectedItems;
    },
  },
  methods: {
    toggleSelection(option) {
      const index = this.selectedItems.findIndex(item => item.id === option.id);
      if (index === -1) {
        this.selectedItems.push(option);
      } else {
        this.selectedItems.splice(index, 1);
      }
    },
    removeSelected(item) {
      const index = this.selectedItems.findIndex(selectedItem => selectedItem.id === item.id);
      if (index !== -1) {
        this.selectedItems.splice(index, 1);
      }
    },
    addAllFields() {
      this.selectedItems = [...new Set([...this.selectedItems, ...this.options])];
    },
    clearSelection() {
      this.selectedItems = [];
    },
    cancel() {
      this.dropdownVisible=false
    },
    confirm() {
      this.selectedValues = this.selectedItems.map(item => item.id);
      this.dropdownVisible=false
      this.$emit('ok', this.selectedValues,this.excludeModeDefault)
    },
    
  }
}
</script>

<style scoped lang="less">
::v-deep .ant-form-item-label > label {
  font-size: 12px;
}
::v-deep span{
  font-size: 12px;
}
::v-deep input,div{
  font-size: 12px;
}
::v-deep .ant-popover-inner-content{
  padding-bottom: 0px !important;
  margin-bottom: -10px !important;
}

.container {
  display: flex;
  flex-direction: column;
  height: 100%;
  font-size: 12px;
}
.header {
  display: flex;
  padding: 8px;
  .header-left {
    flex: 1;
  }
  .header-right {
    flex: 1;
  }
}
.main {
  display: flex;
  flex: 1;
  overflow: hidden;
}
.left,
.right {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  border-top: 1px solid #eaeaea;
}
.left {
  /* margin-right: 8px; */
}
.right {
  /* margin-left: 8px; */
  border-top: 1px solid #eaeaea;
  border-left: 1px solid #eaeaea;
}
.a-divider {
  height: 100%;
}
.option-item {
  display: flex;
  align-items: center;
  padding: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.option-item:hover {
  background-color: #f5f5f5;
}
.option-selected {
  color: #1890ff;
}
.option-selected:hover {
  background-color: #e6f7ff;
}
.check-space {
  width: 16px;
  display: inline-block;
}
.add-all-button {
  margin-top: auto; /* 固定到最下方 */
  width: 97%;
  margin-bottom: 5px;
  height: 26px;
}
.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-top: 1px solid #eaeaea;
  margin-bottom: -8px;
}
.right .footer {
  margin-top: auto; /* 固定到右侧最下方 */
  margin-bottom: 5px;
  height: 30px;
}
.search-box {
  border: none; /* 移除边框 */
  margin-bottom: 8px;
}
.dropdown-popover ::v-deep .ant-popover-arrow{
  display: none;
}
</style>