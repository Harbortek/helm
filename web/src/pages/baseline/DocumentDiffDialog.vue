<template>
    <a-modal v-if="visiable" v-model="visiable" title="查看文档差异" :width="950" @cancel="onCancel" centered>
        <template slot="footer">
            <a-button key="close" type="primary" @click="onCancel">
                关闭
            </a-button>
        </template>
        <a-spin style="width: 100%;margin: 0 auto" size="large" :spinning="loading" />
        <!-- <div class="editor-holder-wrapper">
            <div ref="diffContent" class="diffContent editor-holder" 
                style="margin: 0 auto;word-break: break-all;overflow-y:auto;"
                >
            </div>
        </div> -->
        <div class="diffContent">
            <vxe-table size="mini" :loading="loading" ref="compareTable" show-overflow border row-key :row-config="{ isHover: true }"
                :show-header="true" auto-resize :data="getTableData" :checkbox-config="{ labelField: 'name' }"
                :cell-style="cellStyle"
                :tree-config="{ transform: true, accordion: false, line: true, rowField: 'id', parentField: 'parentId', iconOpen: 'vxe-icon-square-minus', iconClose: 'vxe-icon-square-plus' }">
                <vxe-column field="name" tree-node>
                    <template #default="{ row }">
                        <div style="display: inline-flex;">
                            <div style="width:20px;"> <a-icon :component="row.icon" v-if="row.icon" /></div> {{ row.name }}
                        </div>
                    </template>
                </vxe-column>
                <vxe-colgroup title="左侧" header-align="center">
                    <vxe-column field="leftName" title="名称" header-align="center">
                        <template #default="{ row }">
                            {{ row.left?.name }}
                        </template>
                    </vxe-column>
                    <vxe-column field="leftRevision" title="版本" header-align="center">
                        <template #default="{ row }">
                            {{ row.left?.revision }}
                        </template>
                    </vxe-column>
                </vxe-colgroup>
                <vxe-colgroup title="右侧" header-align="center">
                    <vxe-column field="rightName" title="名称" header-align="center">
                        <template #default="{ row }">
                            {{ row.right?.name }}
                        </template>
                    </vxe-column>
                    <vxe-column field="rightRevision" title="版本" header-align="center">
                        <template #default="{ row }">
                            {{ row.right?.revision }}
                        </template>
                    </vxe-column>
                </vxe-colgroup>
                <vxe-column title="操作" header-align="center">
                    <template #default="{ row }">
                        <a-tooltip title="查看差异">
                            <vxe-button v-if="!row.folder&&row.type!='wiki'" type="text" icon="vxe-icon-eye-fill"
                                @click="onViewDiff(row)"></vxe-button>
                        </a-tooltip>
                    </template>
                </vxe-column>
            </vxe-table>
        </div>

        <tracker-item-diff-dialog :isShowDialog="showTrackerItemDiff" :currentCompare="currentCompareItem" @cancel="showTrackerItemDiff = false" />


        <!-- <smart-doc style="display: none;" ref="leftDoc" :pageId="leftPageId" :projectId="projectId" displayMode="preview" :previewDoc="leftDocContnet" />
        <smart-doc style="display: none;" ref="rightDoc" :pageId="rightPageId" :projectId="projectId" displayMode="preview" :previewDoc="rightDocContnet" />      -->

    </a-modal>
</template>

<script>
import { findDocumentHistory } from '@/services/baseline/BaselineService'
import { findByProjectId } from "@/services/tracker/ProjectPageService";
import {getHTMLDiff} from '@/utils/diff_html.js'
import TrackerItemDiffDialog from './TrackerItemDiffDialog.vue';
import SmartDoc from '@/components/smart-doc/SmartDoc.vue'


export default {
    name: 'DocumentDiffDialog',
    components: {SmartDoc,TrackerItemDiffDialog},
    props: {
        isShowDialog: {
            required: true
        },
        currentCompare: {
            required: true
        },
        tableData: {
            required: true
        }
    },
    data() {
        return {
            loading: false,
            tracker:{},

            resultStr: '',
            oldStr: '',
            newStr: '',
            leftDocContnet:{},
            rightDocContnet:{},
            leftPageId:'',
            rightPageId:'',
            showRight:false,
            pageList:[],
            currentCompareItem:{},
            showTrackerItemDiff:false,
            leftHistoryItemIds:[],
            rightHistoryItemIds:[],
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
                        if(res){
                            res.forEach(element => {
                                if(element.revision==this.currentCompare.left?.revision){
                                    this.leftPageId=element.pageId
                                    this.leftDocContnet=element
                                    this.leftHistoryItemIds=element.elements?.filter(v=>v.type=='tracker-item').map(v=>v.refHistoryId)
                                }else{
                                    this.rightPageId=element.pageId
                                    this.rightDocContnet=element 
                                    this.rightHistoryItemIds=element.elements?.filter(v=>v.type=='tracker-item').map(v=>v.refHistoryId)
                                }
                            });
                        }else{
                            console.log("res is null")
                        }
                    }).finally(()=>{
                        this.$nextTick(() => {
                            this.$refs.compareTable.setAllTreeExpand(true)
                        })
                        this.loading=false

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
        getTableData(){
            let table=this.tableData.filter(v=>{
                if(v.type=='tracker'&&!this.leftHistoryItemIds.includes(v.left?.historyId)&&!
                    this.rightHistoryItemIds.includes(v.right?.historyId)){
                    return false;
                }
                if(v.name=='文档'||(v.type=='wiki'&&(this.currentCompare.left?.id!=v.left?.id&&this.currentCompare.right?.id!=v.right?.id))){
                    return false;
                }
                return true;
            })
            let parentIds=table.map(v=>v.parentId)
            table=table.filter(v=>parentIds.includes(v.id)||!v.folder)
            return table
        }
    },
    mounted() {
        // findByProjectId(this.projectId).then(resp => {
        //   this.pageList = resp.filter(item=>item.type=='wiki');
        // });
    },
    methods: {
        onCancel() {
            this.$emit('cancel')
        },
        onViewDiff(row) {
            this.currentCompareItem = row
            this.showTrackerItemDiff = true

        },
        cellStyle({ row, column }) {
            if (column.field === 'leftName' || column.field === 'leftRevision') {
                if (row.mode == 'ADD') {
                    return {
                        color: '#189FFF',
                        backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                        backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                        backgroundRepeat: 'no-repeat',
                        backgroundSize: '1px 100%, 100% 1px',
                        backgroundPosition: '100% 0, 100% 100%',
                    }
                } else if (row.mode === 'UPDATE') {
                    if (parseInt(row.left.revision) > parseInt(row.right.revision)) {
                        return {
                            color: '#f5222f',
                            backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                            backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                            backgroundRepeat: 'no-repeat',
                            backgroundSize: '1px 100%, 100% 1px',
                            backgroundPosition: '100% 0, 100% 100%',
                        }
                    } else {
                        return {
                            color: '#908b8b',
                            backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                            backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                            backgroundRepeat: 'no-repeat',
                            backgroundSize: '1px 100%, 100% 1px',
                            backgroundPosition: '100% 0, 100% 100%',
                        }
                    }

                }
            } else if (column.field === 'rightName' || column.field === 'rightRevision') {
                if (row.mode === 'DELETE') {
                    return {
                        color: '#189FFF',
                        backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                        backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                        backgroundRepeat: 'no-repeat',
                        backgroundSize: '1px 100%, 100% 1px',
                        backgroundPosition: '100% 0, 100% 100%',
                    }
                } else if (row.mode === 'UPDATE') {
                    if (parseInt(row.left.revision) > parseInt(row.right.revision)) {
                        return {
                            color: '#908b8b',
                            backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                            backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                            backgroundRepeat: 'no-repeat',
                            backgroundSize: '1px 100%, 100% 1px',
                            backgroundPosition: '100% 0, 100% 100%',
                        }
                    } else {
                        return {
                            color: '#f5222f',
                            backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                            backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                            backgroundRepeat: 'no-repeat',
                            backgroundSize: '1px 100%, 100% 1px',
                            backgroundPosition: '100% 0, 100% 100%',
                        }
                    }
                }
            }
            return {
                backgroundImage: '-webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec)), -webkit-gradient(linear, left top, left bottom, from(#e8eaec), to(#e8eaec))',
                backgroundImage: 'linear-gradient(#e8eaec, #e8eaec), linear-gradient(#e8eaec, #e8eaec)',
                backgroundRepeat: 'no-repeat',
                backgroundSize: '1px 100%, 100% 1px',
                backgroundPosition: '100% 0, 100% 100%',
            }
        },
        
    },
}
</script>

<style lang="less" scoped>

.diffContent{
    margin: 10px;
    height: calc(100% - 350px);
    overflow: auto;
} 
</style>