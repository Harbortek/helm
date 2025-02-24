<template>
  <a-modal v-model="visiable" title="配置枚举值" @ok="onOK" @cancel="onCancel" centered>
    <vxe-toolbar size="medium" style="align-items: flex-end;">
      <template #buttons>
        <a-form-model ref="fieldForm" class="field-form" layout="vertical" :model="formData" :rules="rules">
          <a-form-model-item ref="newOptionValue" label="新增枚举值" prop="newOptionValue"
            style="margin-bottom: 0px;padding-bottom: 0px;">
            <a-row type="flex" justify="space-between" align="top">
              <a-col width="auto">
                <a-input v-model="formData.newOptionValue" style="width:360px;" placeholder="请输入枚举值" @blur="() => {
                  $refs.newOptionValue.onFieldBlur();
                }
                  " />
              </a-col>
            </a-row>
          </a-form-model-item>
        </a-form-model>
      </template>
      <template #tools>
        <a-space>
          <a-button type="primary" @click="onCreateOption">添加</a-button>
        </a-space>
      </template>
    </vxe-toolbar>
    <vxe-table ref="xTable" :data="tableData" :row-config="{ isHover: true }" stripe height="400"
      :edit-config="{ trigger: 'manual', mode: 'row' }" @edit-closed="onEditClosed">
      <vxe-column field="name" title="枚举值" header-align="center" :edit-render="{}">
        <template #edit="{ row }">
          <vxe-input v-model="row.name" type="text" placeholder="请输入值"></vxe-input>
        </template>
        <template #default="{ row }">
          <h-icon type="drag" /> <span :style="{ color: row.color, backgroundColor: row.backgroudColor }">{{ row.name
          }}</span>
        </template>
      </vxe-column>
      <vxe-column field="color" title="颜色" header-align="center" align="center" width="100">
        <template #default="{ row }">
          <a-space>
            <div class="box" :style="{ 'background-color': row.color }" @click="onEditColor(row, false)">
            </div>

            <div class="box" :style="{ 'background-color': row.backgroundColor }" @click="onEditColor(row, true)">
            </div>
          </a-space>

        </template>
      </vxe-column>
      <vxe-column field="icon" title="图标" header-align="center" align="center" width="80">
        <template #default="{ row }">
          <icon-picker placeholder="" v-model="row.icon" :dropdownWidth="520" />
        </template>
      </vxe-column>
      <vxe-column field="" title="操作" header-align="center" align="right" width="100">
        <template #default="{ row }">
          <vxe-button type="text" icon="vxe-icon-edit" @click="onEditItem(row)"
            :disabled="projectId && !row.projectId"></vxe-button>
          <vxe-button type="text" icon="vxe-icon-delete" @click="onDeleteItem(row)"
            :disabled="projectId && !row.projectId"></vxe-button>
        </template>
      </vxe-column>
    </vxe-table>

    <a-modal :title="editColorTitle" v-model="showColorPicker" style="width:800px;" @cancel="showColorPicker = false"
      @ok="onColorPickerOK">
      <Verte display="widget" v-model="color" picker="square" model="rgb" />
    </a-modal>
  </a-modal>
</template>
<script>
import VxeTable from 'vxe-table'
import Verte from 'verte';
import 'verte/dist/verte.css';
import Sortable from "sortablejs"
import IconPicker from '@/components/icon/IconPicker.vue';
import {cloneDeep} from 'lodash'
import { findEnums } from '@/services/system/EnumService'
export default {
  name: "EnumItemDialog",
  components: { Verte, Sortable, IconPicker },
  props: {
    isShowDialog: {
      required: true
    },
    enumCategory: {
      required: true
    },
    projectId: {
      required: true
    }
  },
  data() {
    const that = this;
    const valueValidator = (rule, value, callback) => {
      let found = false;
      let i = 0;
      for (i = 0; i < that.tableData.length; i++) {
        if (that.tableData[i].name === value) {
          found = true;
          break;
        }
      }
      if (!found) {
        return callback();
      } else {
        return callback(new Error("已存在同样名称的数据"));
      }
    };
    return {
      formData: {
        newOptionValue: '',
      },
      color: '',
      foregroudColor: '#00000',
      backgroudColor: '#FFFFFF',
      editColorTitle: '编辑字体颜色',
      showColorPicker: false,
      tableData: [],
      rules: {
        newOptionValue: [
          { required: true, message: "请输入枚举值", trigger: "blur" },
          {
            min: 2,
            max: 20,
            message: "长度在2到20之间",
            trigger: "blur",
          },
          { validator: valueValidator, trigger: "change" },
        ]
      },
    };
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
  },
  watch: {
    visiable: {
      handler: function (newVal, oldVal) {
        if (newVal) {
          this.loadData();
        }

      }
    }
  },
  mounted() {
  },
  methods: {
    closeDialog() {
      this.$emit("close");
    },
    loadData() {
      const _this = this;
      if (this.enumCategory) {
        const param = {
          categoryId: this.enumCategory.id,
          projectId: this.projectId,
        }
        findEnums(param).then(resp => {
          this.tableData = resp
          if (!this.sortable) {
            this.rowDrop()
          }
        })
      }
    },
    onCreateOption() {
      const optionIds = this.tableData.map(o => o.id);
      // let MAXID = 0
      // if (optionIds.length > 0) {
      //   MAXID = optionIds.reduce((a, b) => {
      //     return Math.max(a, b);
      //   });
      // }

      this.$refs["fieldForm"].validate((valid) => {
        if (valid) {
          this.tableData.push({ 
            id: this.tableData.length + 1,
            name: this.formData.newOptionValue,
            categoryId: this.enumCategory.id,
            projectId: this.projectId,
            color: '#000000',
            backgroudColor: '#ffffff',
          })
          this.formData.newOptionValue = ''
        }
      })
    },
    onBatchCreateOption() {

    },
    onEditItem(row) {
      const $table = this.$refs.xTable
      $table.setActiveRow(row)
    },
    onEditClosed({ row, column }) {
      const $table = this.$refs.xTable
      const field = column.property
      const cellValue = row[field]
      // 判断单元格值是否被修改
      if ($table.isUpdateByRow(row, field)) {
        this.tableData[row - 1][field] = cellValue
        $table.reloadRow(row, null, field)
      }
      console.log(this.tableData)
    },
    onEditColor(row, isBackground) {
      if(isBackground){
        this.editColorTitle="编辑背景颜色"
      }else{
        this.editColorTitle="编辑字体颜色"
      }
      console.log(row)
      this.selectedRow = row
      this.color = row.color
      this.isBackground = isBackground
      this.showColorPicker = true
    },
    onColorPickerOK() {
      console.log(this.color)
      this.showColorPicker = false

      this.tableData.forEach(item => {
        if (item.id === this.selectedRow.id) {
          if (this.isBackground) {
            item.backgroundColor = this.color
          } else {
            item.color = this.color
          }
        }
      })
      this.selectedRow = null
      console.log(this.tableData)
    },

    async onDeleteItem(row) {
      const type = await VxeTable.modal.confirm('您确定要删除该数据?')
      const $table = this.$refs.xTable
      if (type === 'confirm') {
        this.tableData = this.tableData.filter(item => { return item.name != row.name })
      }
    },
    onOK() {
      let index = 1;
      this.tableData.forEach(item => { item.ordinary = index++ })
      console.log("onOk",this.tableData)
      this.$emit("ok", this.tableData);
    },
    onCancel() {
      this.$emit("cancel");
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
              filter: '.start-state',
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
  },
  created() { },
  beforeDestroy() {
    if (this.sortable) {
      this.sortable.destroy();
    }
  },
};
</script>
<style lang="less" scoped>
.box {
  width: 20px;
  height: 20px;
  border: solid 1px black;
}
</style>
