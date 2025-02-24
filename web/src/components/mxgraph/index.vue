<template>
  <vxe-modal v-model="action.show" :title="action.title" width="95%" height="95%" show-zoom resize class="graph-wrapper"
    :destroy-on-close="true">
    <template #corner>
      <vxe-button status="primary" icon="vxe-icon-save" :disabled="loading" @click="onSave">保存</vxe-button>
    </template>
    <template #default>
      <h-iframe class="h-iframe" ref="mxgraphEditor" v-if="action.show" :src="action.src" @load="onLoad"></h-iframe>
    </template>
  </vxe-modal>
</template>
<script>
import HIframe from '@/components/iframe'
export default {
  name: 'MxGraphEditor',
  components: {
    HIframe
  },
  data() {
    return {
      loading: true
    }
  },
  props: ['action'],
  methods: {

  },
  created() {

  },
  mounted() {

  },
  methods: {
    onLoad() {
      this.loading = true;
      let xml = this.action?.xml;
      if (xml) {
        const that = this;
        setTimeout(() => {
          that.$refs.mxgraphEditor.iframeEl.contentWindow._doLoadXml(xml);
          that.loading = false;
        }, 100);
      }else{
        this.loading = false;
      }
    },
    onSave() {
      if (this.$refs.mxgraphEditor.iframeEl) {
        const p = this.$refs.mxgraphEditor.iframeEl.contentWindow._doSave();
        p.then((data) => {
          this.$emit('change', {
            show: false,
            ...data
          });
        });

      }
    }
  }
}
</script>

<style lang="less" scoped>
.graph-wrapper {
  width: 100%;
  height: 100%;
  overflow-y: scroll;

  .h-iframe {
    width: 100%;
    height: 100%;
  }
}
</style>