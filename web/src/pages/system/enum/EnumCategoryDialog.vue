<template>
  <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" centered>
    <a-form-model ref="xFrom" layout="horizontal" :model="formData" :rules="rules">
      <a-form-model-item ref="name" label="名称" prop="name">
        <a-input v-model="formData.name" />
      </a-form-model-item>
      <a-form-model-item label="类型" prop="name">
        <a-switch checked-children="动态" un-checked-children="静态" v-model="formData.dynamic" />
      </a-form-model-item>
      <a-form-model-item ref="description" label="描述" prop="description">
        <a-input v-model="formData.description" type="textarea" />
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
import { isExistsByName } from '@/services/system/EnumService'
export default {
  name: "EnumCategoryDialog",
  components: {},
  data() {
    const that = this;
    const nameValidator = (rule, value, callback) => {

      that.isExistsByName(that.projectId, value).then(resp => {
        let found = resp;
        if (value == this.enumCategory.name) {
          found = false
        }
        if (!found) {
          return callback();
        } else {
          return callback(new Error("已存在同样名称的数据"));
        }
      })

    };
    return {
      meanings: [],
      formData: {
        name: '',
        description: '',
        dynamic: false,
      },
      rules: {
        name: [
          { required: true, message: "请输入枚举值名称", trigger: "blur" },
          {
            min: 2,
            max: 20,
            message: "长度在2到20之间",
            trigger: "blur",
          },
          { validator: nameValidator, trigger: "blur" },
        ],
        description: [
        ]
      },
    };
  },
  props: {
    isShowDialog: {
      required: true
    },
    editMode: {
      required: true
    },
    enumCategory: {
      required: true
    },
    projectId: {
      required: true
    }
  },
  computed: {
    title() {
      if (this.editMode === 'create') {
        return '创建枚举'
      } else {
        return '编辑枚举'
      }
    },
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
    visiable() {
      if (this.visiable) {
        this.loadData();
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
      if (this.editMode === 'edit') {
        this.formData = cloneDeep(this.enumCategory)
      } else {
        this.formData = { name: '', description: '', dynamic: false }
      }
    },
    isExistsByName(projectId, name) {
      return isExistsByName(projectId, name)
    },
    onOK() {
      this.$refs["xFrom"].validate((valid) => {
        if (valid) {
          let result = cloneDeep(this.enumCategory)
          result.name = this.formData.name
          result.description = this.formData.description
          result.dynamic = this.formData.dynamic
          this.$emit("ok", result);
        }
      })

    },
    onCancel() {
      this.$emit("cancel");
    }
  },
  created() { }
};
</script>
<style lang="less" scoped></style>
