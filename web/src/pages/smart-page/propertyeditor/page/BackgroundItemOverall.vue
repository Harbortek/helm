<template>
  <div class="testcase-template">
    <div :class="[
      {
        ['template-img-active']: itemActive
      },
      'template-img'
    ]" :style="itemStyle" @click.stop="setBoard">
      <a-icon :style="{ 'color': commonBackground.innerImageColor }" width="100%" height="100%" class="svg-background"
        :component="mainIconClass" />
    </div>
    <span class="demonstration">{{ template.name }}</span>
  </div>
</template>

<script>
import { imgUrlTrans } from '@/pages/smart-page/util/canvasUtils'
import { hexColorToRGBA } from '@/pages/smart-page/util/util'

export default {
  name: 'BackgroundItemOverall',
  props: {
    template: {
      type: Object,
      default() {
        return {}
      }
    },
    commonBackground: {
      type: Object,
      required: true
    }
  },
  computed: {
    itemStyle() {
      if (this.commonBackground.backgroundType === 'color') {
        return {
          'background-color': hexColorToRGBA(this.commonBackground.color, this.commonBackground.alpha)
        }
      } else {
        return {}
      }
    },
    mainIconClass() {
      return this.template.url.replace('board/', '').replace('.svg', '')
    },
    itemActive() {
      return this.commonBackground && this.commonBackground.innerImage === this.template.url
    },
    classBackground() {
      if (this.template.url) {
        return {
          background: `url(${imgUrlTrans(this.template.url)}) no-repeat`,
          'background-size': `100% 100%`
        }
      } else {
        return {}
      }
    }
  },
  methods: {
    setBoard() {
      this.commonBackground.innerImage = this.template.url
      this.$emit('borderChange')
    }
  }
}
</script>

<style lang="less" scoped>
.testcase-template {
  display: inline-block;
  margin: 5px 0px;
  width: 90px;
}

.demonstration {
  display: block;
  font-size: 8px;
  color: gray;
  text-align: center;
  margin: 10px auto;
  width: 110px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.template-img {
  position: relative;
  height: 70px;
  width: 110px;
  margin: 0 auto;
  /*box-shadow: 0 0 2px 0 rgba(31, 31, 31, 0.15), 0 1px 2px 0 rgba(31, 31, 31, 0.15);*/
  /*border: solid px #fff;*/
  box-sizing: border-box;
  border-radius: 3px;
}

.template-img:hover {
  border: solid 1px #4b8fdf;
  border-radius: 3px;
  color: deepskyblue;
  cursor: pointer;
}

.template-img>i {
  display: block;
  float: right;
  color: gray;
  margin: 2px;
}

.template-img>i:hover {
  color: red;
}

.template-img:hover>.el-icon-error {
  display: inline;
}

.template-img:hover>.el-icon-edit {
  display: inline;
}

.template-img-active {
  border: solid 1px red;
  border-radius: 3px;
  color: deepskyblue;
}

.svg-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.svg-background ::v-deep svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100% !important;
  height: 100% !important
}
</style>
