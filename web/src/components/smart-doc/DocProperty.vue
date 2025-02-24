<template>
  <div class="DOCProperty-wrapper">
    文档视图
    <vxe-form title-colon ref="formRef" title-align="right" title-width="120" :data="formData" :rules="formRules"
      :loading="loading">
      <vxe-form-item title="名称" field="name" span="24"></vxe-form-item>
      <vxe-form-item title="状态" field="status" span="24">
        <template #default="{ data }">
          <vxe-select v-model="data.status" placeholder="请选择状态" @change="changeEvent({ data })">
            <vxe-option value="1" label="open"></vxe-option>
            <vxe-option value="2" label="close"></vxe-option>
          </vxe-select>
        </template>
      </vxe-form-item>
      <vxe-form-item title="版本" field="version" span="24" :item-render="{}">
        <template #default="{ data }">
          {{ doc.version }}
          <!-- <vxe-input v-model="data.version" placeholder="请输入版本" @change="changeEvent({ data })" clearable></vxe-input> -->
        </template>
      </vxe-form-item>
      <vxe-form-item title="最后修改时间" field="lastModifiedDate" span="24" :item-render="{}">
        <template #default="{ data }">
          {{ doc.lastModifiedDate + '      '}}<a @click="doViewHistory">查看历史</a> 
        </template>
      </vxe-form-item>
    </vxe-form>
    <DocHistory ref="docHistoryRef"></DocHistory>
  </div>
</template>

<script>
import { VXETable } from 'vxe-table'
import  DocHistory  from './DocHistory.vue'
export default {
  name: 'DOCProperty',
  components: {
    DocHistory
  },
  props: {
    doc: Object,
  },
  data() {
    return {
      loading: false,
      formData: {
        name: 'test1',
        version: '1.0',
        status: '1'
      },
      formRules: {
        name: [
          { required: true, message: '请输入名称' },
          { min: 3, max: 100, message: '长度在 3 到 100 个字符' }
        ],
        version: [
          { required: true, message: '请输入版本' }
        ],
        status: [
          { required: true, message: '请选择状态' }
        ],
      }
    }
  },
  methods: {
    changeEvent(params) {
      const $form = this.$refs.formRef
      if ($form) {
        $form.updateStatus(params)
      }
    },
    doViewHistory(){
      this.$refs.docHistoryRef.open();
    }
  }
}
</script>

<style scoped>
.DOCProperty-wrapper {
  width: 100%;
  height: 100%;
  padding-top:10px;
  padding-left:10px
}
</style>