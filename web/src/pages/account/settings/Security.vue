<template>
  <a-list itemLayout="horizontal" :dataSource="data">
    <a-list-item slot="renderItem" slot-scope="item, index" :key="index">
      <a-list-item-meta>
        <a slot="title">{{ item.title }}</a>
        <span slot="description">
          <span class="security-list-description">{{ item.description }}</span>
          <span v-if="item.value"> : </span>
          <span class="security-list-value">{{ item.value }}</span>
        </span>
      </a-list-item-meta>
      <password-modal></password-modal>
    </a-list-item>
  </a-list>
</template>

<script>
import PasswordModal from './PasswordModal.vue'
import { AppEvents } from '@/utils/event'
export default {
  components: {
    PasswordModal
  },
  computed: {
    data() {
      return [
        { title: this.$t('account.settings.security.password'), 
          description: this.$t('account.settings.security.password-description'), 
          value: this.$t('account.settings.security.password-description-strong'), 
        },
      ]
    }
  },
  mounted() {
    //更新挂在完成
    this.$bus.$emit(AppEvents.CONTENT_LOADING, {
      type: AppEvents.CONTENT_LOADING,
      data: false
    });
  },
  methods: {
    handlePwd: function () {
      const record = this.$store.getters['account/user'].id;
      console.log(record);
    }
  }
}
</script>

<style scoped></style>
