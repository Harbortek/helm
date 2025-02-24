<template>
  <div :class="advanced ? 'null' : null">
    <a-form layout="horizontal">
      <div :class="advanced ? null : 'null'">
        <a-row type="flex">
          <a-col v-if="labels[0]" :flex="1">
            <a-form-item :label="labels[0]" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <slot name='one'>
                <a-input :placeholder="$t('system.search.input.placeholder')" />
              </slot>
            </a-form-item>
          </a-col>
          <a-col v-if="labels[1]" :flex="1">
            <a-form-item :label="labels[1]" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <slot name='two'>
                <a-input :placeholder="$t('system.search.input.placeholder')" />
              </slot>
            </a-form-item>
          </a-col>
          <a-col :flex="labels[1] ? 1 : 15" v-if="!advanced">
            <span style="float: left; margin-top: 3px;margin-left:10px">
              <a-button :loading="loading" type="primary" @click="onClickSearch">{{ $t('query') }}</a-button>
              <a-button style="margin-left: 8px" @click="onClickReset">{{ $t('reset') }}</a-button>
              <a v-if="labels[2]" @click="toggleAdvanced" style="margin-left: 8px">
                {{ advanced ? '$t(`system.search.input.retract`)' : '$t(`system.search.input.expand`)' }}
                <a-icon :type="advanced ? 'up' : 'down'" />
              </a>
            </span>
          </a-col>

        </a-row>
        <a-row type="flex" v-if="advanced">
          <a-col :md="12" :sm="24" v-if="labels[2]">
            <a-form-item :label="labels[2]" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <slot name='three'>
                <a-input :placeholder="$t('system.search.input.placeholder')" />
              </slot>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24" v-if="labels[3]">
            <a-form-item :label="labels[3]" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <slot name='four'>
                <a-input :placeholder="$t('system.search.input.placeholder')" />
              </slot>
            </a-form-item>
          </a-col>
        </a-row>

      </div>
      <span v-if="advanced" style="float: right; margin-top: 3px;">
        <a-button type="primary">{{ $t('query') }}</a-button>
        <a-button style="margin-left: 8px">{{ $t('reset') }}</a-button>
        <a @click="toggleAdvanced" style="margin-left: 8px">
          {{ advanced ? '$t(`system.search.input.retract`)' : '$t(`system.search.input.expand`)' }}
          <a-icon :type="advanced ? 'up' : 'down'" />
        </a>
      </span>
    </a-form>
  </div>
</template>

<script>

export default {
  name: 'SearchInput',
  components: {},
  props: {
    labels: Array,
    loading: Boolean,
  },
  data() {
    return {
      advanced: false,
      selectedRows: [],
    }
  },

  mounted() {

  },
  methods: {
    onClickReset() {
      this.$emit("onClickReset")
    },
    onClickSearch(e) {
      this.$emit("onClickSearch")
    },
    toggleAdvanced() {
      this.advanced = !this.advanced
    },

  }
}
</script>

<style lang="less" scoped>
:global(.ant-form-item) {
  margin-bottom: 8px;

}

.search {
  margin-bottom: 54px;
}

.fold {
  width: calc(100% - 216px);
  display: inline-block
}

.operator {
  margin-bottom: 18px;
}

@media screen and (max-width: 900px) {
  .fold {
    width: 100%;
  }
}
</style>
