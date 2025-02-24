<template>
  <div style="width: 100%">
    <a-col style="flex: 1 1;">
      <a-form-model ref="titleForm" :model="titleForm" label-width="80px" :layout="'horizontal'" :labelCol="{ span: 7 }"
        :wrapperCol="{ span: 16, offset: 1 }">
        <a-form-model-item v-show="showProperty('show')" :label="$t('chart.show')" class="form-item">
          <a-checkbox v-model="titleForm.show" @change="changeTitleStyle('show')">{{ $t('chart.show') }}
          </a-checkbox>
        </a-form-model-item>
        <div v-show="titleForm.show || chart.type === 'richTextView'">
          <a-form-model-item v-show="showProperty('title')" v-if="!batchOptStatus" :label="$t('chart.title')"
            class="form-item">
            <a-input v-model="titleForm.title" size="small" :placeholder="$t('chart.title')" clearable
              @blur="changeTitleStyle('title')" @input="inputOnInput($event)" />
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('fontFamily')" :label="$t('chart.font_family')" class="form-item">
            <a-select v-model="titleForm.fontFamily" :placeholder="$t('chart.font_family')"
              dropdownClassName="props-dropdown" @change="changeTitleStyle('fontFamily')">
              <a-select-option v-for="option in fontFamily" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('fontSize')" :label="$t('chart.text_fontsize')" class="form-item">
            <a-select v-model="titleForm.fontSize" :placeholder="$t('chart.text_fontsize')" size="small"
              dropdownClassName="props-dropdown" @change="changeTitleStyle('fontSize')">
              <a-select-option v-for="option in fontSize" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('color')" :label="$t('chart.text_color')" class="form-item">
            <vue-color-picker v-model="titleForm.color" class="color-picker-style" :predefine="predefineColors"
              @change="changeTitleStyle('color')" />
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('hPosition')" :label="$t('chart.text_h_position')" class="form-item">
            <a-radio-group v-model="titleForm.hPosition" size="small" @change="changeTitleStyle('hPosition')">
              <a-radio-button value="left">{{ $t('chart.text_pos_left') }}</a-radio-button>
              <a-radio-button value="center">{{ $t('chart.text_pos_center') }}</a-radio-button>
              <a-radio-button value="right">{{ $t('chart.text_pos_right') }}</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('isItalic') || showProperty('isBolder')"
            :label="$t('chart.text_style')" class="form-item">
            <a-checkbox v-model="titleForm.isItalic" @change="changeTitleStyle('isItalic')">{{ $t('chart.italic') }}
            </a-checkbox>
            <a-checkbox v-model="titleForm.isBolder" @change="changeTitleStyle('isBolder')">{{ $t('chart.bolder') }}
            </a-checkbox>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('letterSpace')" :label="$t('chart.letter_space')" class="form-item">
            <a-select v-model="titleForm.letterSpace" :placeholder="$t('chart.quota_letter_space')"
              dropdownClassName="props-dropdown" @change="changeTitleStyle('letterSpace')">
              <a-select-option v-for="option in fontLetterSpace" :key="option.value" :title="option.name"
                :value="option.value">{{ option.name }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item v-show="showProperty('fontShadow')" :label="$t('chart.font_shadow')" class="form-item">
            <a-checkbox v-model="titleForm.fontShadow" @change="changeTitleStyle('fontShadow')">{{
              $t('chart.font_shadow') }}
            </a-checkbox>
          </a-form-model-item>

          <a-form-model-item v-show="showProperty('remarkShow')" :label="$t('chart.remark')" class="form-item">
            <a-checkbox v-model="titleForm.remarkShow" @change="changeTitleStyle('remarkShow')">{{ $t('chart.show') }}
            </a-checkbox>
          </a-form-model-item>
          <span v-show="titleForm.remarkShow">
            <a-form-model-item v-show="showProperty('remarkShow')" :label="$t('chart.remark_edit')" class="form-item">
              <!-- <a-button :title="$t('chart.edit')" icon="el-icon-edit" type="text" size="small" @click="editRemark" /> -->
              <a-input v-model="titleForm.remark" size="small" :placeholder="$t('chart.remark')" clearable
                @blur="changeTitleStyle('remark')" @input="inputOnInput($event)" />
            </a-form-model-item>
            <!-- <a-form-model-item v-show="showProperty('remarkShow')" :label="$t('chart.remark_bg_color')"
              class="form-item">
              <vue-color-picker v-model="titleForm.remarkBackgroundColor" class="color-picker-style"
                :predefine="predefineColors" @change="changeTitleStyle('remarkBackgroundColor')" />
            </a-form-model-item> -->
          </span>
        </div>
      </a-form-model>
    </a-col>

    <!--富文本编辑框-->
    <a-modal v-if="showEditRemark" :title="$t('chart.remark')" :visible="showEditRemark" :show-close="false" width="70%"
      class="dialog-css" append-to-body>
      <a-input v-model="tmpRemark" :maxLength="200" :background="titleForm.remarkBackgroundColor" />
      <div slot="footer" class="dialog-footer">
        <a-button size="small" @click="closeRemark">{{ $t('chart.cancel') }}
        </a-button>
        <a-button type="primary" size="small" @click="changeRemark">{{ $t('chart.confirm') }}
        </a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { CHART_FONT_FAMILY, CHART_FONT_LETTER_SPACE, COLOR_PANEL, DEFAULT_TITLE_STYLE } from '@/pages/smart-page/util/chart'
import { checkViewTitle } from '@/pages/smart-page/util/canvasUtils'
import { mapState } from 'vuex'

export default {
  name: 'TitleSelectorAntV',
  props: {
    param: {
      type: Object,
      required: true
    },
    chart: {
      type: Object,
      required: true
    },
    propertyInner: {
      type: Array,
      required: false,
      default: function () {
        return []
      }
    }
  },
  data() {
    return {
      titleForm: JSON.parse(JSON.stringify(DEFAULT_TITLE_STYLE)),
      fontSize: [],
      isSetting: false,
      predefineColors: COLOR_PANEL,
      showEditRemark: false,
      tmpRemark: '',
      fontFamily: CHART_FONT_FAMILY,
      fontLetterSpace: CHART_FONT_LETTER_SPACE
    }
  },
  computed: {
    ...mapState([
      'batchOptStatus'
    ]),
    title() {
      return this.chart.name
    }
  },
  watch: {
    'chart': {
      handler: function () {
        this.initData()
      }
    },
    title(val) {
      this.titleForm = { ...this.titleForm, title: val }
    }
  },
  mounted() {
    this.init()
    this.initData()
  },
  methods: {
    initData() {
      const chart = JSON.parse(JSON.stringify(this.chart))
      if (chart.customStyle) {
        let customStyle = null
        if (Object.prototype.toString.call(chart.customStyle) === '[object Object]') {
          customStyle = JSON.parse(JSON.stringify(chart.customStyle))
        } else {
          customStyle = JSON.parse(chart.customStyle)
        }
        if (customStyle.text) {
          this.titleForm = customStyle.text
          this.titleForm.remarkShow = this.titleForm.remarkShow ? this.titleForm.remarkShow : DEFAULT_TITLE_STYLE.remarkShow
          this.titleForm.remark = this.titleForm.remark ? this.titleForm.remark : DEFAULT_TITLE_STYLE.remark
          this.titleForm.remarkBackgroundColor = this.titleForm.remarkBackgroundColor ? this.titleForm.remarkBackgroundColor : DEFAULT_TITLE_STYLE.remarkBackgroundColor

          this.titleForm.fontFamily = this.titleForm.fontFamily ? this.titleForm.fontFamily : DEFAULT_TITLE_STYLE.fontFamily
          this.titleForm.letterSpace = this.titleForm.letterSpace ? this.titleForm.letterSpace : DEFAULT_TITLE_STYLE.letterSpace
          this.titleForm.fontShadow = this.titleForm.fontShadow ? this.titleForm.fontShadow : DEFAULT_TITLE_STYLE.fontShadow
        }
        if (!this.batchOptStatus) {
          this.titleForm.title = this.chart.title
        }
        console.log('titleForm', this.titleForm)
      }
    },
    init() {
      const arr = []
      for (let i = 10; i <= 60; i = i + 2) {
        arr.push({
          name: i + '',
          value: i + ''
        })
      }
      this.fontSize = arr
    },
    changeTitleStyle(modifyName) {
      if (!this.batchOptStatus) {
        if (this.titleForm.title.length < 1) {
          this.$error(this.$t('chart.title_cannot_empty'))
          this.titleForm.title = this.chart.title
          return
        }
        if (checkViewTitle('update', this.chart.id, this.titleForm.title)) {
          this.$error(this.$t('chart.title_repeat'))
          this.titleForm.title = this.chart.title
          return
        }
      }
      this.titleForm['modifyName'] = modifyName
      this.$emit('onTextChange', this.titleForm)
    },
    inputOnInput: function (e) {
      this.$forceUpdate()
    },
    showProperty(property) {
      return this.propertyInner.includes(property)
    },

    editRemark() {
      this.tmpRemark = this.titleForm.remark
      this.showEditRemark = true
    },
    closeRemark() {
      this.showEditRemark = false
    },
    changeRemark() {
      this.titleForm.remark = this.tmpRemark
      this.changeTitleStyle('remark')
      this.closeRemark()
    },
  }
}
</script>

<style scoped>
.shape-item {
  padding: 6px;
  border: none;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-item-slider ::v-deep .a-form-model-item__label {
  font-size: 12px;
  line-height: 38px;
}

.form-item ::v-deep .a-form-model-item__label {
  font-size: 12px;
}

.a-select-dropdown__item {
  padding: 0 20px;
}

span {
  font-size: 12px
}

.a-form-model-item {
  margin-bottom: 6px;
}

.switch-style {
  position: absolute;
  right: 10px;
  margin-top: -4px;
}

.color-picker-style {
  cursor: pointer;
  z-index: 1003;
}

.dialog-css ::v-deep .a-modal__title {
  font-size: 14px;
}

.dialog-css ::v-deep .a-modal__header {
  padding: 20px 20px 0;
}

.dialog-css ::v-deep .a-modal__body {
  padding: 10px 20px 20px;
}
</style>
