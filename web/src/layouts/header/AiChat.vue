<template>
  <div class="ai-chat" style="cursor: pointer">
    <a-icon type="user" @click="toggleChatDialog" />
    <a-modal v-model:visible="showChatDialog" title="AI Chat" :footer="null" :width="450"
      :bodyStyle="{ height: 'calc(100vh - 56px)', margin: 0, padding: 0 }"
      :dialogStyle="{ top: 0, right: 0, bottom: 0, left: 'auto', position: 'fixed' }" :maskClosable="false">


      <iframe
        src="http://192.168.1.108:9999/chat/share?shared_id=79a2cbeae75911ef865a0242ac130006&from=agent&auth=NlYThkMWU0ZTVkYzExZWY5ODhkMDI0Mm"
        style="width: 100%; height: 100%; min-height: 600px" frameborder="0">
      </iframe>

    </a-modal>
  </div>
</template>

<script>
import { right } from '@antv/g2plot/lib/plots/sankey/sankey';

export default {
  name: "AiChat",
  data() {
    return {
      showChatDialog: false,
    }
  },
  computed: {
  },
  methods: {
    toggleChatDialog() {
      this.showChatDialog = !this.showChatDialog;
    }
  }
}
</script>

<style lang="less">
.ai-chat {
  display: inline-flex;

}
</style>
