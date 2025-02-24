<template>
  <a-drawer :title="title" :width="480" :visible="visible" :body-style="{ paddingBottom: '80px' }" @close="handleOk">
    <a-spin :spinning="loading">
      <a-form layout="horizontal" :labelCol="{ span: 7 }" :wrapperCol="{ span: 16, offset: 1 }" :loading="loading">
        <a-form-item :label="$t('system.log.table.module')">
          <span class="ant-form-text">
            {{ log.module }}
          </span>
        </a-form-item>
        <a-form-item :label="$t('system.log.table.title')">
          <span class="ant-form-text">
            {{ log.name }}
          </span>
        </a-form-item>
        <a-form-item :label="$t('system.log.table.content')">
          <span class="ant-form-text">
            {{ log.description }}
          </span>
        </a-form-item>
        <a-form-item :label="$t('system.log.table.remoteAddr')">
          <span class="ant-form-text">
            {{ log.remoteAddress }}
          </span>
        </a-form-item>
        <a-form-item :label="$t('system.log.table.createBy')">
          <span class="ant-form-text">
            {{ log.createBy ? log.createBy.name : '' }}
          </span>
        </a-form-item>
        <a-form-item :label="$t('system.log.table.createDate')">
          <span class="ant-form-text">
            {{ log.createDate }}
          </span>
        </a-form-item>
      </a-form>
    </a-spin>
    <div :style="{
      position: 'absolute',
      right: 0,
      bottom: 0,
      width: '100%',
      borderTop: '1px solid #e9e9e9',
      padding: '10px 16px',
      background: '#fff',
      textAlign: 'right',
      zIndex: 1,
    }
      ">
      <a-button type="primary" @click="handleOk">{{ $t('ok') }}</a-button>
    </div>
  </a-drawer>
</template>

<script>
import { getLog } from '@/services/system/LogService'
import ATextarea from 'ant-design-vue/es/input/TextArea'
export default {
  name: "LogDrawer",
  components: {
    ATextarea
  },
  data() {
    return {
      log: {},
      labelCol: {
        xs: { span: 20 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
      title: this.$t('detail'), //明细
      visible: false,
      loading: false
    }

  },
  created() {
  },
  methods: {
    loadData(logId) {
      this.loading = true;
      return getLog(logId).then(res => {
        this.loading = false;
        console.log('getLog', res);
        this.log = res;
        return res
      })
    },
    view() {
      this.title = this.$t('detail'); //明细
      this.visible = true;
    },
    close() {
      this.log = {};
      this.visible = false;
    },
    handleOk(e) {
      this.close();
    }
  }
}
</script>

<style scoped></style>