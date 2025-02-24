<template>
  <vxe-button type="text" size="mini" @click="doOpenPublishModal">
    <template #icon>
      <span><h-icon type="publish" /></span>
    </template>
    发布
    <vxe-modal ref="publishModal" width="400" @close="onClose" :show-close="false" show-footer :mask="true">

      <template #title>
        <span>发布</span>
      </template>
      <template #default>
        <vxe-form :data="baseLine" :rules="formRules" ref="form">
          <vxe-form-item title="新版本备注" span="24" title-width="80" title-align="right">
            <template #default="params">
              <vxe-input v-model="params.data.name" placeholder="备注" clearable></vxe-input>
            </template>
          </vxe-form-item>
        </vxe-form>
      </template>
      <template #footer>
        <vxe-button @click="doClose">关闭</vxe-button>
        <vxe-button status="primary" :loading="saveLoading" @click="doSave">确定</vxe-button>
      </template>
    </vxe-modal>
  </vxe-button>
</template>

<script>

import HIcon from "@/components/icon/hicon";
export default {
  name: 'PublishButton',
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
      saveLoading: false,
      formRules: {
        name: [
          { required: true, message: '请输入备注' }
        ]
      }
    }
  },
  mounted() {

  },
  methods: {
    doOpenPublishModal() {
      this.$refs.publishModal.open();
    },

    doClose() {
      this.baseLine = {
        name: '',
      }
      this.saveLoading = false;
      this.$refs.publishModal.close();
    },
    onClose() {
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
<style lang="less"></style>