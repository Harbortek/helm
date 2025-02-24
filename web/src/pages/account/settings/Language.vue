<template>
  <!-- <config-page
      :title="$t('account.settings.language.title')"
      :description="$t('account.settings.language.config-page.description')"
    > -->
  <div class="account-settings-info-view">
    <a-row :gutter="16" type="flex">
      <a-col :order="1" :md="12" :lg="8">
        <a-form layout="vertical">
            <a-form-item :label="$t('account.settings.language.description')">
              <a-radio-group v-model="langAlias">
                <div v-for=" lang in langList" :key="lang.key"  >
                  <a-radio :value="lang.key" @click="setLang(lang.key)" style="margin-top:10px">{{lang.name }}</a-radio>
                  <a-badge v-if="langAlias==lang.key"
                    :count="$t('account.settings.language.radio.remind')"
                    :number-style="{
                      'font-size': '13px',
                      backgroundColor: '#fff',
                      color: '#999',
                      boxShadow: '0 0 0 1px #d9d9d9 inset',
                    }"
                  />
                  
                  <br/>
                </div>
              </a-radio-group>
          </a-form-item>
        </a-form>
      </a-col>
    </a-row>
  </div>
  <!-- </config-page> -->
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import ConfigPage from "../../../components/config-page/ConfigPage.vue";

export default {
  name: 'Language',
  components: { ConfigPage },
  data() {
    return {
      langList: [
        { key: 'CN', name: '简体中文', alias: '简体' },
        { key: 'US', name: 'English', alias: 'English' }
      ],
    }
  },
  computed: {
    ...mapState('setting', ['theme', 'layout', 'systemName', 'lang', 'pageWidth']),
    langAlias() {
      let lang = this.langList.find(item => item.key == this.lang)
      return lang.key
    }

  },
  methods: {
    ...mapMutations('setting', ['setLang']),
  }
}
</script>