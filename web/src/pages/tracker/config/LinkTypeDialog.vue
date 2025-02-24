<template>
  <a-modal v-model="visiable" :title="title" @ok="onOK" @cancel="onCancel" centered>
    <a-form-model ref="xFrom" layout="horizontal" :model="formData" :rules="rules">
      <a-form-model-item ref="name" label="正向名称" prop="name">
        <a-input v-model="formData.name" />
      </a-form-model-item>
      <a-form-model-item ref="name" label="反向名称" prop="name">
        <a-input v-model="formData.oppositeName" />
      </a-form-model-item>
      <a-form-model-item ref="description" label="描述" prop="description">
        <a-input v-model="formData.description" type="textarea" />
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>
<script>
import {cloneDeep} from 'lodash'
export default {
  name: "LinkTypeDialog",
  components: {},
  props: {
    isShowDialog: {
      required: true
    },
    editMode: {
      required: true
    },
    linkType: {
      required: true
    },
    linkTypes: {
      required: true
    },
    projectId: {
      required: true
    }
  },
  data() {
    const that = this;
    const nameValidator = (rule, value, callback) => {
      let found = false;
      let i = 0;
      for (i = 0; i < that.linkTypeNames.length; i++) {
        if (that.linkTypeNames[i] === value) {
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
    const nameValidator2 = (rule, value, callback) => {
      let found = false;
      let i = 0;
      for (i = 0; i < that.linkTypeOppositeNames.length; i++) {
        if (that.linkTypeOppositeNames[i] === value) {
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
      meanings: [],
      formData: {
        name: '',
        description: '',
      },
      rules: {
        name: [
          { required: true, message: "请输入名称", trigger: "blur" },
          {
            min: 2,
            max: 20,
            message: "长度在2到20之间",
            trigger: "blur",
          },
          { validator: nameValidator, trigger: "blur" },
        ],
        oppositeName: [
          { required: true, message: "请输入名称", trigger: "blur" },
          {
            min: 2,
            max: 20,
            message: "长度在2到20之间",
            trigger: "blur",
          },
          { validator: nameValidator2, trigger: "blur" },
        ],
        description: [
        ]
      },
    };
  },

  computed: {
    title() {
      if (this.editMode === 'create') {
        return '创建关联类型'
      } else {
        return '编辑关联类型'
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
    linkTypeNames() {
      let names = []
      this.linkTypes.forEach(f => {
        if (f.id !== this.linkType.id) {
          names.push(f.name)
        }
      })
      return names
    },
    linkTypeOppositeNames() {
      let names = []
      this.linkTypes.forEach(f => {
        if (f.id !== this.linkType.id) {
          names.push(f.oppositeName)
        }
      })
      return names
    }
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
        this.formData = cloneDeep(this.linkType)
      } else {
        this.formData = { name: '', oppositeName: '', description: '' }
      }
    },
    isExistsByName(projectId, name) {
      return isExistsByName(projectId, name)
    },
    onOK() {
      this.$refs["xFrom"].validate((valid) => {
        if (valid) {
          let result = cloneDeep(this.linkType)
          result.name = this.formData.name
          result.oppositeName = this.formData.oppositeName
          result.description = this.formData.description
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
