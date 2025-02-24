<template>
  <vxe-button type="text" size="mini" @click="doOpenVersionModal">
    <template #icon>
      <h-icon type="version" />
    </template>
    版本
    <DocHistory ref="docHistoryRef"></DocHistory>
  </vxe-button>
</template>

<script>
import  DocHistory  from './DocHistory.vue'
import HIcon from "@/components/icon/hicon";
export default {
  name: 'CreateBaseLineButton',
  components: {
    DocHistory
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
          { required: true, message: '请输入基线名称' }
        ]
      }
    }
  },
  mounted() {

  },
  methods: {
    doOpenVersionModal() {
      this.$refs.docHistoryRef.open();
    },

    doClose() {
      this.baseLine = {
        name: '',
      }
      this.saveLoading = false;
      this.$refs.versionModal.close();
    },
    onClose() {
      this.doClose();
    }
  }
}
</script>
<style lang="less"></style>