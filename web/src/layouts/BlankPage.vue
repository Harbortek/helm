<template>
  <a-result :title="$t($route.meta.title)">
    <template #icon>
      <a-icon v-if="$route.meta?.icon" :type="$route.meta.icon" />
    </template>
  </a-result>
</template>

<script>
export default {
  name: "BlankPage"
};
</script>

<style lang="less" scoped></style>