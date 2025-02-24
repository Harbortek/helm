<template>
  <config-page title="项目分类" description="项目分类用来按照用户习惯组织项目。">
    <vxe-toolbar size="medium">
      <template #buttons>
        <vxe-input v-model="keyword" placeholder="" type="search" className="searchbox" clearable
          @search-click="onSearch()"></vxe-input>
      </template>
      <template #tools>
        <vxe-button status="primary" content="新增项目分类" @click="onCreateCategory"></vxe-button>
      </template>
    </vxe-toolbar>
    <vxe-table :data="tableData" :loading="loading" :row-config="{ isHover: true }">
      <vxe-column type="seq" width="60"></vxe-column>
      <vxe-column field="name" title="分类名称"></vxe-column>
      <vxe-column title="操作" width="100" show-overflow>
        <template #default="{ row }">
          <vxe-button type="text" icon="vxe-icon-edit" @click="onEditEvent(row)"></vxe-button>
          <vxe-button type="text" icon="vxe-icon-delete" @click="onDeleteEvent(row)"></vxe-button>
        </template>
      </vxe-column>
    </vxe-table>
    <div>
      <a-modal :visible="showDialog" :title="dialogTitle" @ok="onOK" @cancel="onCancel">
        <a-form-model ref="categoryForm" :model="formData" :rules="rules" layout="horizontal" :labelCol="{ span: 5 }"
          :wrapperCol="{ span: 16, offset: 1 }">
          <a-form-model-item ref="name" label="分类名称" prop="name">
            <a-input v-model="formData.name" @blur="() => {
              $refs.name.onFieldBlur();
            }
              " />
          </a-form-model-item>
        </a-form-model>
      </a-modal>
    </div>
  </config-page>
</template>
<script>
import ConfigPage from "../../../components/config-page/ConfigPage.vue";
import {
  findProjectCategories,
  findOneProjectCategory,
  createProjectCategory,
  updateProjectCategory,
  deleteProjectCategory,
} from "@/services/tracker/ProjectCategoryService";

export default {
  name: "CategoryList",
  components: { ConfigPage },
  data() {
    const that = this;
    const nameValidator = (rule, value, callback) => {
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
      keyword: "",
      loading: false,
      tableData: [],
      editMode: "create",
      showDialog: false,
      dialogTitle: "新增项目分类",
      formData: {},
      rules: {
        name: [
          { required: true, message: "请输出分类名称", trigger: "blur" },
          {
            min: 2,
            max: 20,
            message: "分类名称长度在2到20之间",
            trigger: "blur",
          },
          { validator: nameValidator, trigger: ["blur","change"] },
        ],
      },
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    loadData() {
      this.loading = true;
      findProjectCategories({ keyword: this.keyword }).then((resp) => {
        this.tableData = resp;
        this.loading = false;
      });
    },
    onSearch() {
      this.loadData();
    },
    onCreateCategory() {
      this.formData = { name: "" };
      this.editMode = "create";
      this.dialogTitle = "新增项目分类";
      this.showDialog = true;
      this.$nextTick(() => {
        this.$refs["categoryForm"].resetFields();
      });
    },
    onEditEvent(row) {
      this.editMode = "edit";
      this.dialogTitle = "编辑项目分类";
      this.showDialog = true;
      this.$nextTick(() => {
        this.$refs["categoryForm"].resetFields();
        this.formData = { id: row.id, name: row.name };
      });
    },
    async onDeleteEvent(row) {
      const { id } = row;
      const that = this;
      this.$confirm({
        title: "您确定要删除该数据?",
        okText: this.$t("ok"),
        okType: "danger",
        cancelText: this.$t("cancel"),
        onOk() {
          deleteProjectCategory(id).then((resp) => {
            that.loadData();
          });
        },
        onCancel() { },
      });
    },

    onOK() {
      const { editMode, formData } = this;
      const that = this;
      this.$refs["categoryForm"].validate((valid) => {
        if (valid) {
          if ("create" === editMode) {
            createProjectCategory(formData).then((resp) => {
              that.loadData();
            });
          } else {
            updateProjectCategory(formData).then((resp) => {
              that.loadData();
            });
          }
          that.showDialog = false;
        } else {
        }
      });
    },
    onCancel() {
      this.showDialog = false;
    },
  },
};
</script>

<style lang="less">
.searchbox {
  width: 320px;
}
</style>
