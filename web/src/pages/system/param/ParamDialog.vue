<template>
  <a-modal :title="title" :ok="false" :visible="visible" :confirmLoading="loading" :closable="false" :keyboard="true"
    centered>

    <a-spin :spinning="loading">
      <a-form :form="form" layout="horizontal" :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }"
        :loading="loading">

        <a-form-item :label="$t('system.param.modal.label.param-id')" :hidden=true>
          <a-input v-decorator="['id']" />
        </a-form-item>
        <a-form-item :label="$t('system.param.search.label.param-name')">
          <a-input :placeholder="$t('system.param.modal.param-name.message')" :disabled="isView" v-decorator="[
            'name',
            { rules: [{ required: true, trigger: 'change', asyncValidator: nameValidator }] },
          ]" />
        </a-form-item>
        <a-form-item>
          <span slot="label">
            {{ $t('system.param.modal.label.param-code') }}&nbsp;
            <a-tooltip :title="$t('system.param.modal.param-code.message-error')">
              <a-icon type="question-circle-o" />
            </a-tooltip>
          </span>
          <a-input :placeholder="$t('system.param.modal.param-code.message')" :disabled="isView" v-decorator="[
            'code',
            { rules: [{ required: true, trigger: 'change', asyncValidator: codeValidator }] },
          ]" />
        </a-form-item>
        <a-form-item :label="$t('system.param.modal.label.param-content')">
          <a-textarea :placeholder="$t('system.param.modal.param-content.message')" :disabled="isView" v-decorator="[
            'content',
            { rules: [{ required: true, message: this.$t('system.param.modal.param-content.message') + '!' }] },
          ]" />
        </a-form-item>
        <a-form-item :label="$t('system.param.modal.label.param-desc')">
          <a-textarea :placeholder="$t('system.param.modal.param-desc.message')" :disabled="isView" v-decorator="[
            'description'
          ]"></a-textarea>
        </a-form-item>
      </a-form>
    </a-spin>
    <template slot="footer">
      <div style="text-align:center">
        <a-button v-if="!isView" @click="handleOk" type="primary">{{ $t('ok') }}</a-button>
        <a-button @click="handleCancel">{{ $t('close') }}</a-button>
      </div>
    </template>
  </a-modal>
</template>

<script>
import { saveParam, updateParam, getParam, isExistsByCode, isExistsByName } from '@/services/system/ParamService'
import ATextarea from 'ant-design-vue/es/input/TextArea'
export default {
  name: "ParamDialog",
  components: {
    ATextarea
  },
  data() {
    return {
      form: this.$form.createForm(this, { name: 'param_modal' }),
      labelCol: {
        xs: { span: 20 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
      title: this.$t('system.param.table.action'), //操作
      visible: false,
      loading: false,
      isView: false,
      paramName: '',
      paramCode: ''
    }
  },
  created() {
  },
  methods: {
    nameValidator: function (rule, value, callback) {
      if (!!!value) {
        callback(this.$t('system.param.modal.param-name.message')); //请输入参数名称！
        return;
      }
      return isExistsByName(value).then((res) => {
        console.log('isExistsByName', res);
        if (res && this.paramName != this.form.getFieldsValue()["name"]) {
          const err = this.$t('system.param.modal.param-name.message-error'); //参数名称不能重复!
          callback(err);
        }
        callback();
      })
    },
    codeValidator: function (rule, value, callback) {
      if (!!!value) {
        callback(this.$t('system.param.modal.param-code.message')); //请输入参数编码
        return;
      }
      return isExistsByCode(value).then((res) => {
        console.log('isExistsByCode', res);
        if (res && this.paramCode != this.form.getFieldsValue()["code"]) {
          const err = this.$t('system.param.modal.param-code.message-error'); //参数编码必须唯一!
          callback(err);
        }
        callback();
      })
    },

    loadData(id) {
      this.loading = false;
      return getParam(id).then(res => {
        this.loading = false;
        console.log('getParam', res);
        this.form.setFieldsValue({ id: res.id, code: res.code, name: res.name, content: res.content, description: res.description });
        this.paramName = res.name;
        this.paramCode = res.code;
        return res
      })
    },
    add() {
      this.isView = false;
      this.title = this.$t('add'); //新增
      this.visible = true;
      this.paramName = '';
      this.paramCode = '';
    },
    edit() {
      this.isView = false;
      this.title = this.$t('edit'); //编辑
      this.visible = true;
    },
    view() {
      this.title = this.$t('detail'); //详情
      this.visible = true;
      this.isView = true;
    },
    close() {
      this.form.resetFields();
      this.visible = false;
    },
    handleOk(e) {
      e.preventDefault();
      const that = this;
      this.form.validateFields((err, values) => {
        if (!err) {
          if (values.id) {
            console.log("updateParam", values);
            updateParam(values).then(function () {
              that.$message.success(that.$t('system.param.update-success')); //更新成功!
              that.close();
              that.$emit('refresh');
            })
          } else {
            console.log("saveParam", values);
            saveParam(values).then(function () {
              that.$message.success(that.$t('system.param.save-success')); //保存成功!
              that.$emit('refresh');
              that.close();
            })
          }

        } else {
          return err;
        }
      });
    },
    handleCancel() {
      this.close();
    }
  }
}
</script>

<style scoped></style>