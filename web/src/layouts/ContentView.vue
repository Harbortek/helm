<template>
  <a-spin :spinning="loading" class="spin content-view">
    <router-view/>
  </a-spin>
</template>
<script>
import { mapState } from "vuex";
export default {
  name: "ContentView",
  data() {
    return {
      loading: true,
    };
  },
  mounted() {
    this.loading = false;
  },
};
</script>

<style lang="less" scoped>

.content-view {
  
}
.spin {
  width: 100%;
  height: 100%;
  background-color: #f8f8f8;
  /deep/.ant-spin-container {
    width: 100%;
    height: 100%;
  }
}
</style>
