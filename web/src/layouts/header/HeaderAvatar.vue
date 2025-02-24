<template>
  <a-dropdown>
    <div class="header-avatar" style="cursor: pointer">
      <h-avatar :name="user?.name" :icon="avatar"></h-avatar>
    </div>
    <a-menu :class="['avatar-menu']" slot="overlay">
      <!-- <a-menu-item>
        <a-icon type="user" />
        <span>个人中心</span>
      </a-menu-item> -->
      <a-menu-item @click="toSettings">
        <a-icon type="setting" />
        <span>{{ $t("personal.settings") }}</span>
      </a-menu-item>
      <a-menu-divider />
      <a-menu-item @click="logout">
        <a-icon style="margin-right: 8px" type="poweroff" />
        <span>{{ $t("user.logout") }}</span>
      </a-menu-item>
    </a-menu>
  </a-dropdown>
</template>

<script>
import { } from 'vuex'
import HAvatar from '@/components/avatar/h-avatar.vue';
import { iconUrl } from "@/utils/util"
import { mapGetters, mapState, mapMutations } from "vuex";
import { logout } from "@/services/global/LoginService";
import { getSystemsNoPage } from '@/services/system/SystemService'


export default {
  name: "HeaderAvatar",
  components: { HAvatar },
  computed: {
    avatar() {
      this.loadData();//在初始化头像时，初始化系统信息
      if (this.user && this.user.icon) {
        return this.user.icon;//头像
      }
      return null;
    },

    ...mapGetters("account", ["user"]),
  },
  methods: {
    ...mapMutations('setting', ['setSystemName', 'setSystemLogo']),
    ImgError() {
      console.log("error")
    },
    logout() {
      logout();
      this.$router.push("/login");
    },
    toSettings() {
      this.$router.push("/account");
    },
    loadData() {
      getSystemsNoPage(null).then(res => {
        if (this.$store.getters['account/user'] && res.length > 0) {
          this.$store.getters['account/user'].systemId = res[0].id;
          this.setSystemName(res[0]?.name);
          if (res[0].logo) {
            this.setSystemLogo(iconUrl(res[0].logo));
          } else {
            this.setSystemLogo(require('@/assets/logo.png'));
          }
          if (res[0].loginLogo) {
            localStorage.setItem("loginLogo",iconUrl(res[0].loginLogo))
          } else {
            localStorage.setItem("loginLogo",require('@/assets/logo2.png'))
          }
        }
        return res;
      })
    },
  },
};
</script>

<style lang="less">
.header-avatar {
  display: inline-flex;

  .avatar,
  .name {
    align-self: center;
  }

  .avatar {
    margin-right: 3px;
  }

  .name {
    font-weight: 500;
  }
}

.avatar-menu {
  width: 150px;
}
</style>
