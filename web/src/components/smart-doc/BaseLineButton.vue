<template>
  <vxe-button type="text" size="mini"  @click="doOpenCreateBaselineModal">
    <template #icon>
        <span><h-icon type="baseline" /></span>
      </template>
    基线
    <vxe-modal ref="baseLineModal" width="400" @close="onClose" :show-close="false" show-footer :mask="true">
      
      <template #title>
        <span>创建文档基线</span>
      </template>
      <template #default>
        <vxe-form :data="baseLine" :rules="formRules" ref="form">
          <vxe-form-item title="基线名称" span="24" title-width="60" title-align="right">
            <template #default="params">
              <vxe-input v-model="params.data.name" placeholder="基线名称" clearable></vxe-input>
            </template>
          </vxe-form-item>
        </vxe-form>
      </template>
      <template #footer>
        <vxe-button @click="doClose">关闭</vxe-button>
        <vxe-button status="primary" :loading="saveLoading" @click="doSave">保存</vxe-button>
      </template>
    </vxe-modal>
  </vxe-button>
</template>

<script>

import HIcon from "@/components/icon/hicon";
export default {
  name: 'BaseLineButton',
  components: {
  },
  props: {
    
    pageId: {
      type: String,
      require: true
    }
  },
  computed: {

  },
  watch: {
    
  },
  data() {
    return {
      baseLine: {
        name: '',
      },
      saveLoading:false,
      formRules: {
        name: [
          { required: true, message: '请输入基线名称' }
        ]
      }
    }
  },
  mounted() {

  },
  methods: {
    doOpenCreateBaselineModal(){
      this.$refs.baseLineModal.open();
    },
    
    doClose() {
      this.baseLine = {
        name: '',
      }
      this.saveLoading=false;
      this.$refs.baseLineModal.close();
    },
    onClose(){
      this.doClose();
    },
    doSave() {
      this.saveLoading = true;
      setTimeout(() => {
        this.doClose();
        this.$message.success(`保存成功！`)
      }, 500);
      
    }
  }
}
</script>
<style lang="less">

</style>