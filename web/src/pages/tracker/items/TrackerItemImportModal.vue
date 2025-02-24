<template>
    <div>
        <a-modal v-model="visiable" :maskClosable="false" title="从Excel导入工作项" @ok="onOK" @cancel="onCancel">
            <template slot="footer">
                <vxe-button content="关闭" @click="onCancel"></vxe-button>
                <vxe-button status="primary" :disabled="!uploadedFile" @click="onOK" content="导入"></vxe-button>
            </template>
            <a-upload
                name="avatar"
                list-type="picture-card"
                class="avatar-uploader"
                :show-upload-list="false"
                :customRequest="onUpload"
                :before-upload="beforeUpload"
                @change="handleChange"
            >
                <div v-if="uploadedFile">
                    <a-icon  style="font-size:20px;" type="file-excel" />
                    <div class="ant-upload-text">
                        {{uploadedFileData?.origin_name}}
                    </div>
                    <span>{{uploadedFileData?.fileSize/1024}}KB</span>
                </div>
                <div v-else>
                    <a-icon style="font-size:20px;" :type="loading ? 'loading' : 'cloud-upload'" />
                    <div class="ant-upload-text">
                        拖拽文件到此处，或点击上传
                    </div>
                </div>
            </a-upload>
            <div style="margin-left:20px;">
                <p style="margin: 5px 0 0;">· 请上传一个 Excel 文件，文件格式符合模板格式
                    <vxe-button type="text" @click="onClickDemoDown" status="primary" content="下载模板"></vxe-button></p>
                <p style="margin: 5px 0 0;">· 确保表格内只有一个工作簿，如果有多个工作簿只有第一张会被处理.</p>
                <p style="margin: 5px 0 0;">· 单次最大支持导入 5000 条记录.</p>
                <!-- <p>· 层级关系通过父事项标题匹配，须确保关联父事项在表格行之前已创建.</p> -->
                <p style="margin: 5px 0 0;">· 多选项值之间用竖线分隔,例如A|B|C.</p>
            </div> 
        </a-modal>

        <vxe-modal v-model="isShowLoading" top="50" id='loadMessage' :duration="-1" type="message" :showHeader="false" :showFooter="false" :maskClosable="false">
            <div v-if="!isHasError">
                <a-icon style="font-size:20px;" type="loading" />
                <span style="margin-left:10px;font-size:18px;color: #202d40;line-height: 24px;font-weight: 600;">正在导入，请稍后...</span>
            </div>
            <div v-else>
                <div style="margin-left:10px;font-size:18px;color: #202d40;line-height: 24px;font-weight: 600;">
                    <a-icon style="color: #ffae0d;font-size: 24px;" type="warning" theme="filled" />&nbsp; 
                    <span>导入时检测到问题</span></div>
                <br>
                <div style="font-size: 16px;color: #8592a6;width: 400px;margin-left:10px;" v-for="log in logList" :key="log.log">{{log.log}}</div><br>
                <vxe-button content="关闭" @click="onCancelError"></vxe-button>
            </div>
        </vxe-modal>
    </div>
</template>
<script>
import {cloneDeep} from 'lodash'
import VXETable from 'vxe-table';
import {
    downloadImportDome,importTrackerItems
} from "@/services/tracker/TrackerItemService";
import { uploadFile, downloadFile } from '@/services/global/FileService'
import { tr } from 'date-fns/locale';


export default {
    name: "TrackerItemImportModal",
    components: {},
    data() {      
        return {
            loading: false,
            uploadedFile: '',
            uploadedFileData:'',
            isShowLoading:false,
            isHasError:false,
            logList:[],
        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        editMode: {
            required: true
        },
        view: {
            required: false
        },
        views: {
            required: false,
            type: Array,
            default: () => { return [] }
        },
        trackerId:{
            required: false
        }
    },
    computed: {
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if(newVal){

                }
            }
        },
    },
    mounted() {
    },
    methods: {
        onUpload(e){
            uploadFile(e.file).then((res) => {
                this.uploadedFile = res.url
                this.uploadedFileData=res;
                e.onSuccess(res, e.file)
                this.loading=false
            })
        },
        handleChange(info) {
            if (info.file.status === 'uploading') {
                this.loading = true;
                return;
            }else{
                console.log("aaaeelse",info)
            }
            if (info.file.status === 'done') {
                this.loading = false;
            }
        },
        beforeUpload(file) {
            const isXLSX = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' // xlsx
            const isXLS = file.type === 'application/vnd.ms-excel'
            const isLt2M = file.size / 1024 / 1024 < 50;
            if (!isXLSX && !isXLS && file.type !== '') {
                this.$message.error('上传文件只能是 xlsx 或 xls 格式!')
                return false;
            }
            if (!isLt2M) {
                this.$message.error('上传文件大小不能超过 50MB!!');
            }
            return isLt2M
        },
        onClickDemoDown(){
            downloadImportDome(this.trackerId).then(res=>{
                const link = document.createElement('a')
                const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
                link.style.display = 'none'
                link.href = URL.createObjectURL(blob)
                link.download = '模板.xls'
                document.body.appendChild(link)
                link.click()
                document.body.removeChild(link)
            })
        },
        loadData() {
           
        },
        onOK() {
           if(this.uploadedFile){
                this.isShowLoading=true;
                importTrackerItems(this.trackerId,this.uploadedFile).then((res)=>{
                    console.log("response",res)
                    if(!res.hasError){
                        this.isShowLoading=false;
                        VXETable.modal.close("loadMessage")
                        this.$emit("ok");
                        VXETable.modal.message({ content: '导入成功', status: 'success' })
                    }else{
                        this.isHasError=true; 
                        this.logList=res.logList;  
                        if(this.logList.length==0){
                            this.logList.push({log:'导入出错，请修改文件后重新导入。'})
                        }
                    }
                    this.visiable=false;
                })
           }
        },
        onCancelError(){
            VXETable.modal.close("loadMessage")
            this.logList=[];
            this.isHasError=false;
        },
        onCancel() {
            this.$emit("cancel");
        }
    },
    created() { }
};
</script>
<style lang="less" scoped>
.avatar-uploader{
    padding:0 30px;
    /deep/ .ant-upload {
        width: 100%;
        height: 160px;
    }
} 
</style>
