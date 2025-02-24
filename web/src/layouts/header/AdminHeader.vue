<template>
  <a-layout-header :class="[headerTheme, 'admin-header']" style="width:100px">
    <div :class="['admin-header-wide', layout, pageWidth]">
      <a-row type="flex">
        <a-col flex="auto" style="display: flex;">
          <a-icon v-if="collapsed" class="trigger"
            style="font-size:14px;margin: 0px 12px 0 12px;padding: 0;"
            :type="collapsed ? 'menu-unfold' : 'menu-fold'" @click="toggleCollapse" />
          <slot name="title"></slot>
          <slot name="topMenu"></slot>
        </a-col>
        <a-col flex="300px">
          <div v-if="layout !== 'side'" class="admin-header-menu" :style="`width: ${menuWidth};`">
            <i-menu class="head-menu" :theme="headerTheme" mode="horizontal" :options="menuData" @select="onSelect" />
          </div>
          <div :class="['admin-header-right', headerTheme]">
            <!-- <header-search class="header-item" @active="val => searchActive = val" /> -->
            <a-tooltip class="header-item" title="帮助文档" placement="bottom">
              <a href="/docs/index.html" target="_blank">
                <a-icon type="question-circle-o" />
              </a>
            </a-tooltip>
            <ai-chat class="header-item" />
            <a-tooltip class="header-item" title="锁屏" placement="bottom">
              <a-icon type="lock" style="align-self: center;" @click="showLockScreenDialog = true"></a-icon>
            </a-tooltip>

            <header-notice class="header-item" />
            <header-avatar class="header-item" />
            <!-- <a-dropdown class="lang header-item">
          <div>
            <a-icon type="global" /> {{ langAlias }}
          </div>
          <a-menu @click="val => setLang(val.key)" :selected-keys="[lang]" slot="overlay">
            <a-menu-item v-for=" lang in langList" :key="lang.key">{{ lang.key.toLowerCase() + ' ' +
              lang.name }}</a-menu-item>
          </a-menu>
        </a-dropdown> -->
          </div>
        </a-col>
      </a-row>
    </div>
    <lock-screen-dialog :showDialog="showLockScreenDialog" @ok="onCloseLockScreenDialog" />
  </a-layout-header>
</template>

<script>
import HeaderSearch from './HeaderSearch'
import HeaderNotice from './HeaderNotice'
import AiChat from './AiChat'
import HeaderAvatar from './HeaderAvatar'
import IMenu from '@/components/menu/menu'
import { mapState, mapMutations } from 'vuex'
import LockScreenDialog from '@/pages/login/LockScreenDialog'

export default {
  name: 'AdminHeader',
  components: { IMenu, HeaderAvatar, HeaderNotice, HeaderSearch, LockScreenDialog,AiChat },//
  props: ['collapsed', 'menuData', 'logoWidth'],
  data() {
    return {
      langList: [
        { key: 'CN', name: '简体中文', alias: '简体' },
        { key: 'US', name: 'English', alias: 'English' }
      ],
      searchActive: false,
      showLockScreenDialog: false,
    }
  },
  computed: {
    ...mapState('setting', ['theme', 'layout', 'systemName', 'lang', 'pageWidth']),
    headerTheme() {
      return this.theme.mode
    },
    langAlias() {
      let lang = this.langList.find(item => item.key == this.lang)
      return lang.alias
    },
    menuWidth() {
      const { layout, searchActive } = this
      const headWidth = layout === 'head' ? '100% - 188px' : '100%'
      const extraWidth = searchActive ? '600px' : '400px'
      return `calc(${headWidth} - ${extraWidth})`
    },

  },
  mounted() {
  },
  beforeDestroy() {
  },
  methods: {
    toggleCollapse() {
      this.$emit('toggleCollapse')
    },
    onSelect(obj) {
      this.$emit('menuSelect', obj)
    },
    onCloseLockScreenDialog() {
      this.showLockScreenDialog = false
    },
    ...mapMutations('setting', ['setLang'])
  }
}
</script>

<style lang="less" scoped>
@import "index";
</style>
