<template>
  <div>
    <a-layout style="background-color: transparent">
      <a-layout-sider width="260" style="background-color: transparent">
        <a-layout style="background-color: transparent">
          <a-layout-content style="background-color: transparent">
            <a-steps direction="vertical" :current="currentStep" finish-status="success">
              <a-step :title="$t('system.org.button.import.title.first')"></a-step>
              <a-step :title="$t('system.org.button.import.title.second')"></a-step>
              <a-step :title="$t('system.org.button.import.title.third')"></a-step>
            </a-steps>
          </a-layout-content>
          <a-layout-footer style="background-color: transparent">
            <a-button @click="onClose" v-show="currentStep != 3 && currentStep != 2">{{ $t('cancel') }}</a-button>
            <a-button type="primary" @click="getNext" v-if="currentStep == 0" :disabled="!uploadFinished">{{
              $t('system.org.button.import.next-step') }}</a-button>
            <a-button type="primary" @click="getPrev" v-else-if="importResults.hasError">{{
              $t('system.org.button.import.previous-step') }}</a-button>
            <a-button type="primary" @click="getNext" v-else-if="currentStep === 1 && !importResults.hasError"
              :disabled="!checkFinished">{{ $t('system.org.button.import.ok') }}</a-button>
            <a-button @click="onClose" v-else-if="currentStep === 2" :disabled="!importFinished">{{
              $t('return') }}</a-button>
          </a-layout-footer>
        </a-layout>
      </a-layout-sider>
      <a-layout style="background-color: transparent">
        <a-layout-content>
          <div v-show="currentStep === 0">
            <p>
              <span>{{ $t('system.org.button.import.download-template') }}</span><a @click="onClickTemplate"><i
                  class="el-icon-download"></i>{{ $t('system.org.button.import.download-template.i') }}</a>
            </p>
            <p>
              <span>{{ $t('system.org.button.import.upload-document') }}:</span>
              <a-upload style="margin-left: 10px" :multiple="false" :directory="false" :fileList="fileList" action=""
                name="uploadFile"
                accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel"
                :beforeUpload="beforeUpload" :remove="onRemove" :customRequest="onUpload" ref="upload">
                <a-button @click="clearFile"> <a-icon type="upload" />{{ $t('system.org.button.import.select-file') }}
                </a-button>
              </a-upload>
            </p>
            <p>
              <span>{{ $t('system.org.button.import.operating-steps') }}<br />
                {{ $t('system.org.button.import.operating-steps-1') }}<br />
                {{ $t('system.org.button.import.operating-steps-2') }}<br />
                {{ $t('system.org.button.import.operating-steps-3') }}<br />
                {{ $t('system.org.button.import.operating-steps-4') }}
              </span>
            </p>
          </div>
          <div v-show="currentStep == 1 && importResults.errorRowCount > 0">
            <p>
              {{ $t('system.org.button.import.data-validation.error-1') }}<span style="color: #4a90e2; margin-right: 0">{{
                importResults.totalRowCount }}</span> {{ $t('system.org.button.import.data-validation.error-2') }}<span
                style="color: red; margin-right: 0">{{ importResults.errorRowCount }}</span> {{
                  $t('system.org.button.import.data-validation.error-3') }}
              <a @click="onDownloadErrorReport"> <a-icon type="download" /> {{
                $t('system.org.button.import.data-validation.error-download') }}</a>
            </p>
            <a-table :columns="tableCoumns" :data-source="importResults.logList" style="width: 100%; position: relative">
            </a-table>
          </div>
          <div v-show="currentStep == 1 && !checkFinished">
            <p>{{ $t('system.org.button.import.data-validationing') }}<a-icon type="loading" /></p>
          </div>
          <div
            v-show="currentStep === 1 && importResults.errorRowCount === 0 && checkFinished && !importResults.hasError">
            <p>{{ $t('system.org.button.import.data-validation.success') }}</p>
          </div>
          <div v-show="currentStep == 1 && importResults.hasError && importResults.errorRowCount == 0">
            <p>{{ $t('system.org.button.import.file.error') }}</p>
          </div>
          <div v-show="currentStep === 2 && !importFinished">
            <p>{{ $t('system.org.button.import.data.loading') }}<a-icon type="loading" /></p>
          </div>
          <div v-show="currentStep === 2 && importFinished">
            <p>{{ $t('system.org.button.import.data-success') }}</p>
          </div>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </div>
</template>

<script>
// 引用组件
import { importOrganizationCheck, importOrganization, importOrganizationTemplate } from '@/services/system/OrgService'
import { uploadFile, downloadFile } from '@/services/global/FileService'

export default {
  data() {
    return {
      visible: false,
      uploadFinished: false,
      checkFinished: false,
      importFinished: false,
      currentStep: 0,
      fileList: [],
      uploadedFile: null,
      tableCoumns: [
        {
          title: this.$t('system.org.table.title'),
          dataIndex: 'rowNum',
          key: 'rowNum',
          width: 60,
          align: 'center'
        },
        {
          title: this.$t('system.org.table.error'),
          dataIndex: 'log',
          key: 'log'
        }
      ],
      importResults: {
        totalRowCount: 0,
        errorRowCount: 0,
        hasError: false,
        logList: []
      },
      isLimitFile: true
    }
  },
  methods: {
    clearFile() {
      // 清除文件
      this.fileList = []
    },
    onRemove(f) {
      this.clearFile()
      return true
    },
    onUpload(e) {
      uploadFile(e.file).then((res) => {
        this.uploadedFile = res.url
        if (this.isLimitFile) {
          this.uploadFinished = true
          this.fileList.push(e.file)
          e.onSuccess(res, e.file)
        } else {
          this.currentStep = 0
          this.uploadFinished = false
          this.clearFile()
        }
      })
    },
    beforeUpload(file) {
      const isXLSX = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' // xlsx
      const isXLS = file.type === 'application/vnd.ms-excel' // xls
      const isLt5M = file.size / 1024 / 1024 < 5
      this.isLimitFile = true
      if (!isXLSX && !isXLS && file.type !== '') {
        this.$message.error(this.$t('system.org.button.import.message.error-1'))
        this.isLimitFile = false
      }
      if (!isLt5M) {
        this.$message.error(this.$t('system.org.button.import.message.error-2'))
        this.isLimitFile = false
      }
      return isLt5M
    },
    onClickTemplate(){
      importOrganizationTemplate().then(res=>{
        if(res){
          const link = document.createElement('a')
          const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
          link.style.display = 'none'
          link.href = URL.createObjectURL(blob)
          link.download = '机构导入模板.xls'
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
        }
      })
    },
    onClose() {
      this.currentStep = 0
      this.uploadedFile = null
      this.uploadFinished = false
      this.checkFinished = false
      this.importFinished = false
      this.importResults = {
        totalRowCount: 0,
        errorRowCount: 0,
        hasError: false,
        logList: []
      }
      this.$emit('closeImport')
    },
    getPrev() {
      this.currentStep--
      if (this.importResults.errorRowCount > 0) {
        // 在上传错误文件时，应该让用户重新上传新文件
        this.clearFile()
      }
    },
    getNext(e) {
      if (this.currentStep === 0) {
        if (this.uploadedFile != null) {
          this.currentStep++

          importOrganizationCheck(this.uploadedFile).then(
            (response) => {
              console.log(response)
              this.importResults = response
              this.checkFinished = true
            },
            (error) => {
              console.log(error)
              this.currentStep = this.currentStep - 1
            }
          )
        } else {
          this.$message({
            message: this.$t('system.org.button.import.message.remind'),
            type: 'warning'
          })
        }
      } else if (this.currentStep === 1) {
        if (!this.importResults.hasError) {
          this.currentStep++
          importOrganization(this.uploadedFile).then(
            (response) => {
              this.importFinished = true
            },
            (error) => {
              console.log(error)
              this.currentStep = this.currentStep - 1
              this.importFinished = true
            }
          )
        }
      }
    },
    onDownloadErrorReport(e) {
      downloadFile({
        name: '错误.xls',
        url: this.importResults.errorLogFilePath
      }
      )
    }
  }
}
</script>

<style lang="less" scoped></style>
