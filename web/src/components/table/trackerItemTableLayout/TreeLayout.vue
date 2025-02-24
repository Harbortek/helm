<template>
    <div v-if="isShowTree"  class="calendardate" style="position:relative;width:auto;overflow:auto hidden;height:100%;background-color: #fff;">
        <div style="height: 32px;margin-bottom: 10px;">
            <a-space>
                <vxe-select v-model="linkDirection" @change="loadWorkItems()">
                    <vxe-option key="true" value="true" label="关联"></vxe-option>
                    <vxe-option key="false" value="false" label="反向关联"></vxe-option>
                </vxe-select>
                关联类型：
                <vxe-select v-model="linkTypeId" transfer @change="loadWorkItems()">
                    <vxe-option :key="-1" :value="-1" label="-- 全部 --"></vxe-option>
                    <vxe-option v-for="item in trackerRelations" :key="item.id"
                        :value="item.id" :label="item.name"></vxe-option>
                </vxe-select>
                层级：
                <vxe-select v-model="linkLevel" transfer @change="loadWorkItems()">
                    <vxe-option :key="0" :value="0" :label="0"></vxe-option>
                    <vxe-option v-for="i in 10" :key="i" :value="i" :label="i"></vxe-option>
                </vxe-select>
            </a-space>
        </div>
        <div style="height:calc(100% - 42px);">
            <vxe-table ref="vxeTable" height="auto" style="cursor: pointer;" :data="itemData" :loading="loading"
                resizable  :tree-config="{transform: true,rowField: 'id',parentField: 'parentId'}"
                :row-config="{ isHover: true, keyField:'rowId' }"  @scroll="onScroll"
                :checkbox-config="{ reserve:true,checkField: 'checked', trigger: 'row' }">

            <!-- <vxe-column type="checkbox" title="" width="30"></vxe-column> -->
                <vxe-column field="itemNo" title="编号" tree-node>
                    <template #default="{ row }">
                        {{ currentProjectKeyName + '-' + row.itemNo }}
                    </template>
                </vxe-column>
                <vxe-column field="name" title="标题" min-width="300">
                    <template #default="{ row }">
                        <a class="task-table-cell">
                            <div class="task-table-cell-content">
                                <div class="task-cell-summary">
                                    <div class="task-cell-summary-container">
                                        <div @click.stop class="task-description">
                                            <div class="prefix-container" @click="onEditTrackerItem(row)">
                                                <a-tooltip v-if="row?.icon" :title="'工作项类型：' + row.tracker?.name" :overlayStyle="{ fontSize: '10px' }">
                                                    <div class="task-icon"><a-icon :component="row?.icon" /></div>
                                                </a-tooltip>
                                                <span>{{ row.name }}</span>
                                            </div>
                                        </div>

                                    </div>
                                    <a-icon v-if="row.parentId" component="itemTree" />
                                </div>
                            </div>
                        </a>
                    </template>
                </vxe-column>
                <vxe-column field="status" title="优先级">
                    <template #default="{ row }">
                        <a-tag style="border:none"
                            :style="{ color: row.priority?.color, backgroundColor: row.priority?.backgroundColor }">{{
                                row.priority?.name }}</a-tag>
                    </template>
                </vxe-column>
                <vxe-column field="status.name" title="状态">
                    <template #default="{ row }">
                    <div v-if="row.status" class="transition-status">
                        <span class="ui-tag-status" :style="{ color: row.meaning?.color, 'border-color': row.meaning?.color }">{{
                        row.status?.name }}</span>
                    </div>
                    </template>
                </vxe-column>
                <vxe-column field="planEndDate" title="计划结束时间"> </vxe-column>

                <vxe-column field="createBy" title="创建者">
                    <template #default="{ row }">
                        <h-avatar v-if="!loading" :name="row.createBy.name" :icon="row.createBy.icon"></h-avatar>
                    </template>
                </vxe-column>
                <vxe-column field="owner" title="负责人">
                    <template #default="{ row }">
                        <div v-if="row.owner">
                            <h-avatar :name="row.owner?.name" :icon="row.owner?.icon"></h-avatar>
                        </div>
                    </template>
                </vxe-column>
                <vxe-column field="createDate" title="创建日期"> </vxe-column>
            </vxe-table>
        </div>
    </div>
</template>

<script>
import Vue from "vue"
import { mapGetters } from "vuex";
import moment from 'moment';
import VxeTable from 'vxe-table'
import HAvatar from '@/components/avatar/h-avatar.vue';
import {
    findTrackerItems,findTrackerLinksByItemIds, findTrackerItemsTree, updateMatrixLinks
} from "@/services/tracker/TrackerItemService";
import { findLinkTypes } from "@/services/tracker/TrackerLinkTypeService"
export default {
    name: 'MatrixLayout',
    props: {
        tracker: Object,
        projectId: String,
        isShowTree: Boolean,
        currentView:Object,
        conditionGroups:Array,
    },
    components: {
        HAvatar
    },
    computed:{
        ...mapGetters("project", ["currentProjectKeyName"]),
    },
    data() {
        return {
            loading:false,
            itemData:[],
            pagination: {
                current:1,
                pageSize: 20,
            },
            trackerRelations: [],
            linkDirection:'true',
            linkTypeId:-1,
            trackerLinks:[],
            newTrackerLinks:[],
            delTrackerLinks:[],
            linkLevel:1,

        }
    },
    mounted() {
        if(this.projectId){
            this.initData();
        }
    },
    watch: {
        isShowTree:{
            handler:function(newVal,oldVal){
                if(newVal){
                    this.loadWorkItems()
                }
            }
        }

    },
    methods: {
        loadWorkItems(pagination) { 
            this.loading = true;
            let keyword = this.keyword
            let sort = [];
            let pageable = {
                page: pagination?.current-1||0,
                size: pagination?.pageSize||20,
            }
            let filter = { "conditionGroups": this.conditionGroups }
            this.currentView.viewConfig?.orderBys?.forEach(item => {
                if(item.field.systemProperty){
                    sort.push(item.field.systemProperty + "," + item.orderBy)
                }else if(this.tracker.trackerFields.find(field=>field.id==item.field.id)){
                    item.field=this.tracker.trackerFields.find(field=>field.id==item.field.id)
                    sort.push(item.field.systemProperty + "," + item.orderBy)
                }
            })
            findTrackerItemsTree(this.projectId, this.tracker.id, null, filter, keyword, pageable, sort,this.linkDirection,this.linkTypeId,this.linkLevel).then(resp => {
                resp.content.forEach(element => {
                    this.$set(element,'rowId',element.id+"_"+element.parentId)
                });
                this.pagination.total=resp.totalElements
                if(pagination?.current){
                    this.itemData.push(...resp.content)
                }else{
                    this.pagination.current=1
                    this.itemData=resp.content
                }
            }).finally(() => {
                this.loading = false;
            })
            
        },
        onClickSaveLink(){
            let allTrackerLinks=this.delTrackerLinks.concat(this.newTrackerLinks)
            updateMatrixLinks(allTrackerLinks).then(resp=>{
                VxeTable.modal.message({content:"保存成功",status:"success"})
                this.newTrackerLinks=[]
                this.delTrackerLinks=[]
                this.hasChange=false
                this.loadWorkItems()
            })
        },
        onClickRevert(){
            VxeTable.modal.confirm({
                content: '是否还原更改？'
            }).then((type =>{
                if(type=="confirm"){
                    this.newTrackerLinks=[]
                    this.delTrackerLinks=[]
                    this.hasChange=false
                }
            }))
        },  
        onClickRemoveLink(row,sourceId,targetId){
            if(this.linkTypeId==-1){
                VxeTable.modal.message({content:"请选择关联类型",status:"info"})
                return;
            }
            this.hasChange=true
            trackerLink=this.delTrackerLinks.find(v=>v.sourceItemId==sourceId&&v.targetItemId==targetId)
            if(trackerLink){
                this.delTrackerLinks.splice(this.delTrackerLinks.indexOf(trackerLink),1)
                return;
            }
            trackerLink=this.newTrackerLinks.find(v=>v.sourceItemId==sourceId&&v.targetItemId==targetId)
            if(trackerLink){
                this.newTrackerLinks.splice(this.newTrackerLinks.indexOf(trackerLink),1)
                return;
            }
            let trackerLink=this.trackerLinks.find(v=>v.sourceItemId==sourceId&&v.targetItemId==targetId)
            if(trackerLink){
                this.delTrackerLinks.push({
                    id: trackerLink.id,
                    sourceItemId:sourceId,
                    targetItemId:targetId,
                    linkTypeId:trackerLink.linkTypeId
                })
                return;
            }
        },
        initData(){
            findLinkTypes(this.projectId).then(resp => {    
                this.trackerRelations = resp
            })
        },

        onEditTrackerItem(row){
            this.$emit("onEditTrackerItem",row)
        },
        refresh(){
            this.$emit('refresh');
        },
        onScroll(e){
            if(this.loading){
                return
            }
            const that =this
            let scrollTop = e.scrollTop
            let scrollHeight = e.scrollHeight
            let clientHeight = e.bodyHeight
            if(scrollTop + clientHeight >= scrollHeight-1){
                if (!this.timer) {
                    this.timer = setTimeout(() => { 
                        that.timer = null;
                        if(that.pagination.total){
                            let current=that.pagination.current
                            let pageSize=that.pagination.pageSize
                            if(that.pagination.total<=current*pageSize){
                                return
                            }
                        }
                        that.pagination.current+=1
                        that.loadWorkItems(that.pagination) 
                    }, 300)
                }
            }
        },  
        
    },

}
</script>

<style scoped lang="less">
.calendardate {
   :deep(.ant-radio-group) {
        display:none;
    }
}
.table-header-rotate{
    // height: 30px;   
    position: absolute;
    top:1px;
    white-space: nowrap;
    transform-origin: left bottom;
    transform: rotate(90deg);
}
.task-table-cell {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    // padding: 0 10px;
    cursor: pointer;

    .task-table-cell-content {
        height: 100%;
        display: flex;
        flex: 1 1 auto;
        align-items: center;
        overflow: hidden;

        .task-cell-summary {
            width: 100%;
            height: 100%;
            display: flex;
            flex: 1 1 auto;
            align-items: center;

            .tree-placeholder {
                height: 100%;
                display: flex;
                flex: 0 0 auto;
                justify-content: flex-end;
                align-items: center;
            }

            .task-cell-summary-container {
                height: 100%;
                display: flex;
                flex: 1 1 auto;
                align-items: center;
                overflow: hidden;

                .task-description {
                    display: block;
                    max-width: 100%;

                    .prefix-container {
                        max-width: 100%;
                        display: inline-flex;
                        flex-direction: row;
                        align-items: center;
                        margin: 0 5px 0 0;
                        vertical-align: middle;
                        font-size: 14px;

                        span{
                            white-space: nowrap;
                            text-overflow: ellipsis;
                            overflow: hidden;
                        }

                        .task-icon {
                            margin-right: 5px;
                        }
                    }
                }

                .task-summary-edit {
                    fill-opacity: 0;
                    margin: 5px;
                    min-width: 16px;
                }

                .ui-icon {
                    fill: currentColor;
                    line-height: 1;
                    vertical-align: middle;
                    border-radius: 3px;
                    box-sizing: border-box;
                    display: inline-block;
                    height: 16px;
                    line-height: 14px;
                    width: 16px;
                    transition: opacity .2s;
                    border-radius: 0;
                }
            }
        }

        &:hover {
            .task-summary-edit {
                fill-opacity: 1;
                fill: #606060;
            }
        }
    }
}
.transition-status {
    min-width: 110px;
    display: flex;
    align-items: center;
    margin-right: 20px;
    white-space: nowrap;

    .ui-tag-status {
        max-width: 110px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 12px;
        box-sizing: border-box;
        display: inline-block;
        height: 20px;
        line-height: 18px;
        transition: border-color .2s;
        border: 1px solid;
        border-radius: 20px;
        padding: 0px 8px;
    }
}
</style>
