<template>
  <vxe-modal style="padding: 0px;overflow: hidden;" v-model="loading" width="90%" height="90%" title="文档历史"
    :esc-closable="true" :mask="true" :mask-close="false" :show-footer="false">
    <div class="wrapper">
      <div class="content">
        <div class="header">
          <a-form layout="inline">
            <a-form-item style="margin-right: 0px;">
              当前版本：<b>{{ '2024-05-27 11:36' }}</b>
              <a-tag color="blue" style="margin-left: 10px;">
                {{ '最新版本' }}
              </a-tag>
            </a-form-item>
            <a-form-item>
              与
              <vxe-select size="mini" style="width: 160px" v-model="compareTo" placeholder="未选择版本" clearable>
                <vxe-option v-for="num in 15" :key="num" :value="num" :label="`选项${num}`"></vxe-option>
              </vxe-select>
              比较
            </a-form-item>
          </a-form>
        </div>
        <div class="editor">
          2
        </div>
      </div>
      <div class="version">
        <div class="header">
          <div class="header-title">历史版本 ({{ history.length }})</div>
          <div class="header-filter">
            <a-dropdown v-model="filter">
              <a class="ant-dropdown-link" @click="e => e.preventDefault()">
                全部 <a-icon type="down" />
              </a>
              <a-menu slot="overlay" @click="doFilter">
                <a-menu-item key="1">
                  全部
                </a-menu-item>
                <a-menu-item key="2">
                  仅基线
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </div>
        </div>
        <div class="version-list">
          <a-list :data-source="history">
            <a-list-item slot="renderItem" :class="checked == index ? 'checked' : ''" slot-scope="item, index"
              @click="doCheck(index)">
              <div class="version-item">
                <div class="version-item-title">
                  <div>
                    <span class="version-item-date">{{ item.name }}</span>
                    <a-tag color="blue" style="margin-left: 5px;">
                      {{ '最新版本' }}
                    </a-tag>
                  </div>
                  <div class="version-item-actions">
                    <vxe-button status="info" size="mini" mode="text" content="备注"></vxe-button>
                    <vxe-button status="info" size="mini" mode="text" content="回滚"></vxe-button>
                  </div>
                </div>
                <ul class="version-item-authors">
                  <li class="version-item-author">
                    <h-avatar :name="item.author?.name" :icon="item.author?.icon" :isShowName="true"
                      :size="16"></h-avatar>
                  </li>
                </ul>
              </div>
            </a-list-item>
          </a-list>
        </div>
      </div>
    </div>
  </vxe-modal>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
export default {
  name: 'DocHistory',
  components: {
    HAvatar
  },
  props: {
    doc: Object,
  },
  data() {
    return {
      loading: false,
      checked: 0,
      compareTo: '',
      filter: false,
      history: [{
        name: '11',
        author: {
          name:'胡军',
          icon:''
        }
      }, {
        name: '21',
        author: {
          name:'胡军',
          icon:''
        }
      }]
    }
  },
  methods: {
    open() {
      this.loading = true
    },
    doCheck(index) {
      this.checked = index;
    },
    doFilter() {
      this.filter = false;
    }
  }
}
</script>

<style scoped lang="less">
@version-width : 300px;

:deep(.vxe-modal--content) {
  padding: 0px;
}

.header {
  height: 50px;
  border-bottom: #e8e8e8 1px solid;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
  padding-right: 20px;
  padding-left: 20px;

  .header-title {
    display: flex;
    align-items: center;
    margin-top: 8px;
    margin-bottom: 8px;
  }

  .header-filter {}
}

.wrapper {
  width: 100%;
  height: 100%;
  clear: both;
}

.content {
  width: calc(100% - @version-width);
  height: 100%;
  float: left;

  .editor {
    padding: 10px 20px;
  }
}

.version {
  width: @version-width;
  height: 100%;
  float: left;
  border-left: #e8e8e8 1px solid;
  display: flex;
  flex-direction: column;

  .version-list {
    height: 100%;

    :deep(li.ant-list-item:not([class~="checked"])):hover {
      background-color: #f8f8f8;
    }

    .version-item {
      width: 100%;

      .version-item-title {
        font-weight: 500;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0 20px;

        .version-item-date {}

        .version-item-actions {}
      }

      .version-item-authors {
        list-style: none;
        padding: 0;

        .version-item-author {
          display: flex;
          padding: 0 20px;
          margin-top: 10px;
          justify-content: flex-start;
          line-height: 12px;
          font-size: 12px;
        }

      }
    }
  }

  .checked {
    background-color: #f0f6ff;
  }
}
</style>