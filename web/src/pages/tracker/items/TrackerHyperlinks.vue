<template>
    <div :class="!isToolBar?'task-detail-module':''">
        <div style="margin-top:20px" class="task-detail-module-title">
            <div style="color: rgba(0, 0, 0, 0.85);">链接</div>
        </div>

        <a-config-provider>
            <template #renderEmpty>
                <div style="text-align: left;height: 42px;line-height: 42px;color:#87888a">
                    <span>暂无链接</span>
                </div>
            </template>
            <vxe-table
                border="none"
                :loading="loading"
                show-footer
                :row-config="{isHover: true}"
                :footer-method="footerMethod"
                :data="hyperlinkList">
                <vxe-column title="角色" width="150"  show-overflow>
                    <template #default="{ row }">
                        <a-select v-model="row.linkRole" @change="onChangeLinkType(row)" style="width:100%;" 
                            :disabled="trackerItem?.notPagePerm" placeholder="Role">
                            <a-select-option value="internal">内部链接</a-select-option>
                            <a-select-option value="external">外部链接</a-select-option>
                        </a-select>
                    </template>
                </vxe-column>
                <vxe-column title="链接" min-width="150"  show-overflow>
                    <template #default="{ row }">
                        <div style="display: flex;">
                            <a-select v-model="row.linkType" @change="onChangeType(row)" style="width:90px"
                                :disabled="trackerItem?.notPagePerm" placeholder="Type">
                                <a-select-option value="URL">URL</a-select-option>
                            </a-select>
                            <vxe-input @focus="focusValue=row.linkPath" @blur="onBlurEdit(row)" :disabled="trackerItem?.notPagePerm"
                                style="width:calc(100% - 100px);margin-left:10px;" v-model="row.linkPath" placeholder="URL"></vxe-input>
                        </div>
                    </template>
                </vxe-column>
                <vxe-column title="操作" width="150" align="center"  show-overflow>  
                    <template #default="{ rowIndex }">
                        <div style="display: flex;justify-content: center">
                            <vxe-button title="删除" type="text" icon="vxe-icon-delete" :disabled="trackerItem?.notPagePerm"
                                @click="onClickDelete(rowIndex)"></vxe-button>
                            <vxe-button v-if="rowIndex==hyperlinkList.length-1" title="增加" type="text"
                                :disabled="trackerItem?.notPagePerm" icon="vxe-icon-add" @click="onClickAdd"></vxe-button>
                            </div>
                    </template>
                </vxe-column>
                
            </vxe-table>
        </a-config-provider>
    </div>
</template>

<script>
import VXETable from "vxe-table";
import {
    findHyperlinkByObjectId, updateHyperlink,deleteHyperlink
} from "@/services/tracker/TrackerItemService"

export default {
    name: 'TrackerHyperlinks',
    components: {},
    props: {
        isShowDialog: {
            required: true
        },
        trackerItem: {
            required: true
        },
        isToolBar: {
            require: false
        }
    },
    data() {
        return {
            hyperlinkList: [],
            loading: false,
            focusValue:'',
        }
    },
    watch: {
        isShowDialog: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        }
    },
    mounted() {

    },
    methods: {
        loadData() {
            if (this.trackerItem?.id) {
                this.loading = true;
                findHyperlinkByObjectId(this.trackerItem.id).then(res => {
                    this.hyperlinkList = res;
                }).finally(()=>{
                    if(this.hyperlinkList.length === 0){
                        this.hyperlinkList.push([])
                    }
                    this.loading = false;
                })
            }else{
                if(this.hyperlinkList.length === 0){
                    this.hyperlinkList.push([])
                }
            }
        },
        footerMethod: function ({ columns, data }) {
            return [
                columns.map(column => {
                    return null
                })
            ]
        },
        onClickAdd(){
            this.hyperlinkList.push([])
        },
        onClickDelete(rowIndex){
            if(!this.hyperlinkList[rowIndex].id){
                this.$delete(this.hyperlinkList,rowIndex)
                this.$delete(this.trackerItem.hyperlinks,rowIndex)
                if(this.hyperlinkList.length === 0){
                    this.hyperlinkList.push([])
                }
                return;
            }
            VXETable.modal.confirm({
                title: '移除链接',
                content: '该链接数据将被删除?'
            }).then(type => {
                if (type === 'confirm') {
                    if(this.trackerItem?.id){
                        deleteHyperlink(this.hyperlinkList[rowIndex].id).then(res=>{
                            VXETable.modal.message({ content: '删除成功', status: 'success' })
                            this.$delete(this.hyperlinkList,rowIndex)
                            if(this.hyperlinkList.length === 0){
                                this.hyperlinkList.push([])
                            }
                        }).finally(()=>{
                            // this.$emit("loadComment")   
                            this.loadData();
                        })
                    }
                }
            })
        },
        onBlurEdit(row){
            if(this.focusValue != row.linkPath){
                this.updateHyperlink(row);
            }
            this.focusValue = ""
        },
        onChangeType(row){
            this.updateHyperlink(row);
        },
        onChangeLinkType(row){
            this.updateHyperlink(row);
        },
        updateHyperlink(row){
            if(row.linkRole&&row.linkType&&row.linkPath){
                let hyperlink= Object.assign({},row)
                if(this.trackerItem?.id){
                    updateHyperlink(this.trackerItem.id,hyperlink).then(res=>{
                        if(!row.id){
                            row.id=res.id
                        }
                        VXETable.modal.message({ content: '更新成功', status: 'success' })
                    }).finally(()=>{
                        // this.$emit("loadComment")   
                        this.loadData();
                    })
                }else{
                    this.trackerItem.hyperlinks.push(hyperlink)
                }
            }
        }
    },
}
</script>

<style lang="less" scoped>
.task-detail-module {
    margin: 0 20px 0;
}
.register-hour-item {
    border-bottom: 1px solid #e8e8e8;
    cursor: pointer;
    padding: 12px;
}

.register-hour-item:hover {
    background-color: #f8f8f8;
}

.register-hour-item-edit {
    display: none;
}
</style>