<template>
  <span>
    <a-popover trigger="click">
      <template slot="title">
        <div class="title_bar">
          <div class="title"><span>未读通知</span></div>
          <div class="link"><a-button type="link" @click="markAllRead">标记所有为已读</a-button></div>
        </div>
      </template>
      <template slot="content">
        <div class="top_bar_notice_container">
          <a-spin :spinning="loading">
            <a-list v-if="totalCount > 0" class="notice-list" item-layout="horizontal" :data-source="notifications">
              <a-list-item slot="renderItem" slot-scope="item">
                <a-list-item-meta :description="item.description">
                  <a slot="title" @click="readIt(item)">{{ item.title }}</a>
                </a-list-item-meta>
                <div slot="actions">{{ formatDate(item.createDate) }}</div>
              </a-list-item>
            </a-list>
            <a-empty v-if="totalCount === 0" />
          </a-spin>
        </div>
      </template>
      <span @click="fetchNotice" class="header-notice">
        <a-badge class="notice-badge" :count="totalCount">
          <a-icon :class="['header-notice-icon']" type="bell" />
        </a-badge>
      </span>
    </a-popover>
    <lock-screen-dialog :showDialog="showLockScreenDialog" @ok="onCloseLockScreenDialog" />
  </span>
</template>

<script>
import { findUnreadNotifications, readIt, readAll } from '@/services/system/NotificationService'
import { shortDate } from '@/utils/DateUtils'
import { mapState, mapMutations } from 'vuex'
import { checkAuthorization } from '@/utils/request'
import LockScreenDialog from '@/pages/login/LockScreenDialog'
export default {
  name: 'HeaderNotice',
  components: { LockScreenDialog },//
  data() {
    return {
      loading: false,
      show: false,
      notifications: [],
      page: 0,
      pageSize: 7,
      totalCount: 0,
      showLockScreenDialog: false,
    }
  },
  computed: {
  },
  mounted() {
    this.timer = setInterval(this.fetchNotice, 1000 * 30);
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
  methods: {
    fetchNotice() {
      this.checkAcessToken()

      this.loading = true
      findUnreadNotifications(this.page, this.pageSize).then(resp => {
        this.notifications = resp.content
        this.totalCount = resp.totalElements
        this.loading = false
        return
      })
    },
    formatDate(d) {
      return shortDate(d)
    },
    markAllRead() {
      readAll().then(resp => {
        this.fetchNotice()
      })
    },
    readIt(item) {
      readIt(item.id).then(resp => {
        this.fetchNotice()
        if (item.href) {
          this.$router.push({ path: item.href })
        }
      })
    },
    checkAcessToken() {
      if (!checkAuthorization()) {
        //已过期
        this.showLockScreenDialog = true
      }
    },
    onCloseLockScreenDialog() {
      this.showLockScreenDialog = false
    },
  }
}
</script>

<style lang="less">
.header-notice {
  display: inline-block;
  transition: all 0.3s;

  span {
    vertical-align: initial;
  }

  .notice-badge {
    color: inherit;

    .header-notice-icon {
      font-size: 16px;
      padding: 4px;
    }
  }
}

.ant-popover-title {
  padding: 0;
  border-bottom: solid 1px #dedede;

  .title_bar {
    height: 48px;
    padding: 5px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .title {
    font-size: 18px;
    color: #303030;
    font-weight: 500;
  }

  .link {
    cursor: pointer;
    line-height: 1;
    font-size: 14px;
    color: #606060;

    .ant-btn-link {
      color: #606060;
    }
  }
}

.top_bar_notice_container {
  padding: 10px;
  box-sizing: border-box;
  cursor: auto;
  font-size: 14px;
  background-color: #fff;
  width: 420px;
  height: 480px;
  margin: -8px -10px;
  display: flex;
  flex-direction: column;
  flex: 1 1 auto;
  overflow: auto;
  &::-webkit-scrollbar {
    width: 0px;
  }

  .notice-list {
    .ant-list-item-meta-title {
      margin-bottom: 4px;
      font-size: 12px;
      color: #909090;
      line-height: 22px;
    }

    .ant-list-item-meta-description {
      font-size: 14px;
      color: #303030;
      // height: 16px;
      line-height: 1;
      
      overflow:hidden;
      text-overflow: ellipsis;
      display:-webkit-box ;
      -webkit-line-clamp: 4;//控制在3行显示
      -webkit-box-orient: vertical;

    }
  }
}
</style>
