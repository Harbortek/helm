<template>
  <a-modal :title="$t('account.settings.avatar.modal.title')" :visible="visible" :maskClosable="false"
    :confirmLoading="confirmLoading" :width="800" :footer="null" @cancel="cancelHandel">
    <a-row>
      <a-col :xs="24" :md="12" :style="{ height: '350px' }">
        <vue-cropper ref="cropper" :img="options.img" :info="true" :autoCrop="options.autoCrop"
          :autoCropWidth="options.autoCropWidth" :autoCropHeight="options.autoCropHeight" :fixedBox="options.fixedBox"
          @realTime="realTime">
        </vue-cropper>
      </a-col>
      <a-col :xs="24" :md="12" :style="{ height: '350px' }">
        <div class="avatar-upload-preview">
          <img :src="previews.url" :style="previews.img" />
        </div>
      </a-col>
    </a-row>
    <br>
    <a-row>
      <a-col :lg="2" :md="2">
        <a-upload name="file" :beforeUpload="beforeUpload" :showUploadList="false">
          <a-button icon="upload">{{ $t('account.settings.avatar.select-image') }}</a-button>
        </a-upload>
      </a-col>
      <a-col :lg="{ span: 1, offset: 2 }" :md="2">
        <a-button icon="plus" @click="changeScale(1)" />
      </a-col>
      <a-col :lg="{ span: 1, offset: 1 }" :md="2">
        <a-button icon="minus" @click="changeScale(-1)" />
      </a-col>
      <a-col :lg="{ span: 1, offset: 1 }" :md="2">
        <a-button icon="undo" @click="rotateLeft" />
      </a-col>
      <a-col :lg="{ span: 1, offset: 1 }" :md="2">
        <a-button icon="redo" @click="rotateRight" />
      </a-col>
      <a-col :lg="{ span: 2, offset: 6 }" :md="2">
        <a-button type="primary" @click="finish('blob')">{{ $t('account.settings.avatar.save') }}</a-button>
      </a-col>
    </a-row>
  </a-modal>
</template>
<script>
import { VueCropper } from 'vue-cropper'
import { updateAvatar } from '@/services/global/LoginService'
import { iconUrl } from "@/utils/util"


export default {
  components: {
    VueCropper
  },
  data() {
    return {
      visible: false,
      id: null,
      confirmLoading: false,
      fileList: [],
      uploading: false,
      options: {
        img: '',
        autoCrop: true,
        autoCropWidth: 180,
        autoCropHeight: 180,
        fixedBox: true
      },
      previews: {}
    }
  },
  mounted() {
    let info = this.$store.getters['account/user'];
    this.options.img = iconUrl(info["icon"]);
  },
  methods: {
    edit(id) {
      this.visible = true
      this.id = id
      /* 获取原始头像 */
    },
    close() {
      this.id = null
      this.visible = false
    },
    cancelHandel() {
      this.close()
    },
    changeScale(num) {
      num = num || 1
      this.$refs.cropper.changeScale(num)
    },
    rotateLeft() {
      this.$refs.cropper.rotateLeft()
    },
    rotateRight() {
      this.$refs.cropper.rotateRight()
    },
    beforeUpload(file) {
      const reader = new FileReader()
      // 把Array Buffer转化为blob 如果是base64不需要
      // 转化为base64
      reader.readAsDataURL(file)
      reader.onload = () => {
        this.options.img = reader.result
      }
      // 转化为blob
      // reader.readAsArrayBuffer(file)
      return false
    },

    // 上传图片（点击上传按钮）
    finish(type) {
      console.log('finish')
      const _this = this
      const formData = new FormData()
      // 输出
      if (type === 'blob') {
        this.$refs.cropper.getCropBlob((data) => {
          const img = window.URL.createObjectURL(data)
          this.model = true
          this.modelSrc = img
          formData.append('file', data, this.fileName)
          formData.append("id", this.$store.getters['account/user'].id)
          updateAvatar(formData).then(function (res) {
            _this.$message.success(_this.$t('account.settings.avatar.update-success')); //头像更新成功！
            // _this.options.img = iconUrl(res)
            _this.$emit('ok', res)
            _this.visible = false
            return new Promise(resolve => {
              resolve(true)
            })
          }).catch((error) => {
            console.log(error)
            _this.$message.error(_this.$t("account.settings.avatar.update-fail")); //头像更新失败！
            _this.visible = false
          });

        })
      } else {
        this.$refs.cropper.getCropData((data) => {
          this.model = true
          this.modelSrc = data
        })
      }
    },
    okHandel() {
      const vm = this

      vm.confirmLoading = true
      setTimeout(() => {
        vm.confirmLoading = false
        vm.close()
        vm.$message.success(this.$t('account.settings.avatar.upload-head-success')) //上传头像成功
      }, 2000)
    },

    realTime(data) {
      this.previews = data
    }
  }
}
</script>

<style lang="less" scoped>
.avatar-upload-preview {
  position: absolute;
  top: 50%;
  transform: translate(50%, -50%);
  width: 180px;
  height: 180px;
  border-radius: 50%;
  box-shadow: 0 0 4px #ccc;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
  }
}
</style>
