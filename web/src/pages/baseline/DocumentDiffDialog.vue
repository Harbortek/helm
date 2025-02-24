<template>
    <a-modal v-if="visiable" v-model="visiable" title="查看文档差异" :width="950" @cancel="onCancel" centered>
        <template slot="footer">
            <a-button key="close" type="primary" @click="onCancel">
                关闭
            </a-button>
        </template>
        <a-spin style="width: 100%;margin: 0 auto" size="large" :spinning="loading" />
        <div class="editor-holder-wrapper">
            <div ref="diffContent" class="diffContent editor-holder"
                style="margin: 0 auto;word-break: break-all;overflow-y:auto;padding-left: 60px;">
            </div>
        </div>

        <smart-doc style="display: none;"  ref="leftDoc" :pageId="leftPageId" :projectId="projectId" displayMode="preview" :previewDoc="leftDocContnet" />
        <smart-doc style="display: none;" ref="rightDoc"  :pageId="rightPageId" :projectId="projectId" displayMode="preview" :previewDoc="rightDocContnet" />     
    </a-modal>
</template>

<script>
import { findDocumentHistory } from '@/services/baseline/BaselineService'
import {getHTMLDiff} from '@/utils/diff_html.js'
import SmartDoc from '@/components/smart-doc/SmartDoc.vue'


export default {
    name: 'DocumentDiffDialog',
    components: {SmartDoc},
    props: {
        isShowDialog: {
            required: true
        },
        currentCompare: {
            required: true
        }
    },
    data() {
        return {
            loading: false,
            tableData:[],
            tracker:{},

            resultStr: '',
            oldStr: '',
            newStr: '',
            gridOptions1: {
                border: true,
                showHeader: false,
                columns: [
                    { field: 'item', width: 100 },
                    { field: 'left', slots: { default: 'left_default' } },
                    { field: 'right', slots: { default: 'right_default' } },
                ],
                data: [
                    { col1: '', col2: 'Test1', col3: 'Test2' },
                    { col1: 'Name', col2: 'Test1', col3: 'Test2' },
                    { col1: 'Role', col2: 'Develop', col3: 'PM', },
                    { col1: 'Sex', col2: 'Man', col3: 'Women', },
                    { col1: 'Age', col2: 28, col3: 18, },
                    { col1: 'Address', col2: 'Shenzhen', col3: 'Guangzhou', }
                ]
            },
            leftDocContnet:{},
            rightDocContnet:{},
            leftPageId:'',
            rightPageId:'',
        }
    },
    watch: {
        isShowDialog: {
            handler: async function (newVal, oldVal) {
                if(newVal){
                    this.loading=true
                    let historyIds=[]
                    if(this.currentCompare.left?.id){
                        historyIds.push(this.currentCompare.left.historyId)
                    }
                    if(this.currentCompare.right?.id){
                        historyIds.push(this.currentCompare.right.historyId)
                    }
                    findDocumentHistory(historyIds).then(res=>{
                        console.log("dcoument",res)
                        if(res){
                            res.forEach(element => {
                                if(element.revision==this.currentCompare.left?.revision){
                                    this.leftPageId=element.pageId
                                    this.leftDocContnet=element 
                                }else{
                                    this.rightPageId=element.pageId
                                    this.rightDocContnet=element 
                                    console.log("left",element,this.leftPageId)

                                }
                            });
                        }else{
                            console.log("res is null")
                        }
                        
                        // this.showResult(this.oldStr,this.newStr)

                    }).finally(()=>{
                        let leftStr="",rightStr=""
                        setTimeout(() => {
                            this.$nextTick(()=>{
                                leftStr=this.$refs.leftDoc?.$el.querySelector('.editor-holder')?.innerHTML||" "
                                rightStr=this.$refs.rightDoc?.$el.querySelector('.editor-holder')?.innerHTML||" "
                                this.showResult(leftStr,rightStr)
                                this.loading=false
                            })
                        }, 1500);
                    })
                }
            }
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
    },
    mounted() {

    },
    methods: {
        onCancel() {
            this.$emit('cancel')
        },
        showResult(oldVal, newVal, type) {
            this.$nextTick(()=>{
                let diffContent = this.$refs.diffContent
                diffContent.innerHTML = getHTMLDiff(oldVal, newVal,"delete")
                let codexEditor = document.querySelector(".codex-editor__redactor")
                codexEditor.style.paddingBottom = "30px"
            })
        },
    },
}
</script>

<style lang="less" scoped>

.diffContent{
    /deep/ del{
        background-color: #ffc6c6;  
    }
    /deep/ ins{
        background-color: #c6ffc6;
        text-decoration: none;
    }
} 
@import '@/components/smart-doc/index';
</style>