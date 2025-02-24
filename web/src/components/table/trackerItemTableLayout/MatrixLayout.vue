<template>
    <div v-if="isShowMatrix"  class="calendardate" style="position:relative;width:auto;overflow:auto hidden;height:100%;background-color: #fff;">
        <div style="height: 32px;margin-bottom: 10px;">
            <a-space>
                <vxe-button :disabled="!hasChange" @click="onClickSaveLink()" :content="'保存'"></vxe-button>
                <vxe-button :disabled="!hasChange" @click="onClickRevert()" :content="'恢复'"></vxe-button>
                <vxe-select v-model="linkTypeId" transfer>
                    <vxe-option :key="-1" :value="-1" label="-- 全部 -- "></vxe-option>
                    <vxe-option v-for="item in trackerRelations" :key="item.id"
                        :value="item.id" :label="item.name"></vxe-option>
                </vxe-select>
                <vxe-radio-group v-model="linkDirection">
                    <vxe-radio-button type="text" label="1">
                        <a-icon style="font-size: 18px;transform: rotate(-90deg) scaleX(-1);" type="enter"/>
                    </vxe-radio-button>
                    <vxe-radio-button type="text" label="2"> 
                        <a-icon style="font-size: 18px;" type="enter"/>
                    </vxe-radio-button>
                </vxe-radio-group>
            </a-space>
        </div>
        <div style="height:calc(100% - 42px);">
            <vxe-grid border :loading="loading" height="auto" :columns="tableColumn" :data="itemDataRow" row-id="id"
                :column-config="{resizable: true}" :row-config="{isHover: true,resizable: true,height: 38}" @scroll="onScroll"
                :header-cell-style="{'height': '150px'}" show-overflow onselectstart="return false">
                <template #first_default="{ row }">
                    <div @click="onEditTrackerItem(row)" style="white-space: nowrap;overflow: hidden;cursor:pointer;"> 
                            <a-icon v-if="tracker.icon" :component="tracker.icon" /> 
                            {{ currentProjectKeyName + '-' + row.itemNo }} - {{ row.name }}</div>
                </template>
                
                <template #name_header="{ column }">
                    <div style="height: 150px;cursor:pointer;" @click="onEditTrackerItem(column)">
                        <div class="table-header-rotate">
                            <a-icon v-if="tracker.icon" :component="tracker.icon" /> 
                            {{ currentProjectKeyName + '-' + column?.itemNo }} - {{column?.name}}</div>
                    </div>
                </template>
                <template #default="{ row,columnIndex }">
                    {{ void(trackerLink=getTrackerLinkDirection(row.id,itemDataCol[columnIndex-1].id)) }}
                    <div style="cursor:pointer;font-size: 18px;" :title="currentProjectKeyName + '-' + row.itemNo +' <-> '+ currentProjectKeyName+ '-' + itemDataCol[columnIndex-1].itemNo">
                        <a-icon v-if="trackerLink=='one'" style="transform: rotate(-90deg) scaleX(-1);" type="enter" @click="onClickRemoveLink(row,row.id,itemDataCol[columnIndex-1].id)"/>
                        <a-icon v-else-if="trackerLink=='two'" style="" type="enter"  @click="onClickRemoveLink(row,itemDataCol[columnIndex-1].id,row.id)" />
                        <a-icon v-else-if="trackerLink=='newOne'" style="color: #36c81d;transform: rotate(-90deg) scaleX(-1);" type="enter" @click="onClickRemoveLink(row,row.id,itemDataCol[columnIndex-1].id,)"/>
                        <a-icon v-else-if="trackerLink=='newTwo'" style="color: #36c81d;" type="enter"  @click="onClickRemoveLink(row,itemDataCol[columnIndex-1].id,row.id)" />
                        <a-icon v-else-if="trackerLink=='delOne'" style="color: #d75454;transform: rotate(-90deg) scaleX(-1);" type="enter" @click="onClickRemoveLink(row,row.id,itemDataCol[columnIndex-1].id,)"/>
                        <a-icon v-else-if="trackerLink=='delTwo'" style="color: #d75454;" type="enter"  @click="onClickRemoveLink(row,itemDataCol[columnIndex-1].id,row.id)" />
                        <div v-else style="width: 38px;height: 38px;margin-left: -10px;" @click="onClickSelect(row,row.id,itemDataCol[columnIndex-1].id)"></div>   
                    </div>
                </template>
            </vxe-grid>
        </div>
    </div>
</template>

<script>
import Vue from "vue"
import { mapGetters } from "vuex";
import moment from 'moment';
import VxeTable from 'vxe-table'
import {
    findTrackerItems,findTrackerLinksByItemIds, updateMatrixLinks
} from "@/services/tracker/TrackerItemService";
import { findLinkTypes } from "@/services/tracker/TrackerLinkTypeService"
export default {
    name: 'MatrixLayout',
    props: {
        tracker: Object,
        projectId: String,
        isShowMatrix: Boolean,
        currentView:Object,
        conditionGroups:Array,
    },
    components: {
        
    },
    computed:{
        ...mapGetters("project", ["currentProjectKeyName"]),
    },
    data() {
        return {
            loading:false,
            itemIds:new Set(),
            itemDataRow:[],
            itemDataCol:[],
            paginationRow: {
                current:1,
                pageSize: 50,
            },
            paginationCol: {
                current:1,
                pageSize: 50,
            },
            targetTd: null,
            coltargetTd: null,
            resizeable: false,
            mousedown: false,
            trackerRelations: [],
            linkTypeId:-1,
            linkDirection:'1',
            trackerLinks:[],
            newTrackerLinks:[],
            delTrackerLinks:[],
            hasChange:false,
            bodyWidth:0,
            scrollWidth:0,
            tableColumn: [],

        }
    },
    mounted() {
        if(this.projectId){
            this.initData();
        }
    },
    watch: {
        isShowMatrix:{
            handler:function(newVal,oldVal){
                if(newVal){
                    this.$nextTick(() => {
                    // 表格添加列宽变化
                        this.tableInit();
                    });
                    this.loadWorkItems()
                }
            }
        }

    },
    methods: {
        tableInit() {
            let self = this;
            /* 获取头部td集合,这边是测试表格，只有一个所以直接el-table__body 的0，后续可以在<el-table> 加class，
            再用querySelector
            */
            let tblObj = document.getElementsByClassName("vxe-table--body")[0];
            let screenYStart = 0;
            let tdHeight = 0;
            let headerHeight = 0;

            let headerRows = new Array();
            for (let i = 0; i < tblObj.rows.length; i++) {
                //只有rows这个能选，col要先选rows，然后用cells
                headerRows[i] = tblObj.rows[i].cells[0];
            }
            for (let i = 0; i < headerRows.length; i++) {
                //添加头部单元格事件
                this.addListener(headerRows[i], "mousedown", onmousedown);
                this.addListener(headerRows[i], "mousemove", onmousemove);
            }
            document.onmousedown = function(event) {
                if (this.resizeable) {
                    let evt = event || window.event;
                    this.mousedown = true;
                    screenYStart = evt.screenY;
                    tdHeight = this.targetTd.offsetHeight;
                    headerHeight = tblObj.offsetHeight;
                }
            };
            document.onmousemove = function(event) {
                let evt = event || window.event;
                let srcObj = event.target || eeventvt.srcElement;
                

                //rowIndex是未定义！！！cellIndex是好用的。我应该获取的是tr的rowindex
                //获取偏移 这里是鼠标的偏移
                let offsetY = evt.offsetY;
                if (this.mousedown) {
                    let height = tdHeight + (evt.screenY - screenYStart) + "px"; //计算后的新的宽度，原始td+偏移
                    this.targetTd.style.height = height;
                    tblObj.style.height =
                        headerHeight + (evt.screenY - screenYStart) + "px";
                } else {
                    if(!this.resizeable){
                        if(!srcObj._prevClass || (srcObj._prevClass && srcObj._prevClass.indexOf('vxe-header--column') === -1)){
                            return
                        }  
                    }
                    //修改光标样式
                    if (srcObj.offsetHeight - evt.offsetY <= 8 && srcObj.offsetWidth - evt.offsetX > 8) {
                        this.targetTd = srcObj;
                        this.resizeable = true;
                        srcObj.style.cursor = "row-resize";
                    }else if (evt.offsetY <= 8 && evt.offsetX > 8) {
                        if (srcObj.parentNode.rowIndex) {
                            this.targetTd = tblObj.rows[srcObj.parentNode.rowIndex - 1]?.cells[0];
                            this.resizeable = true;
                            srcObj.style.cursor = "row-resize";
                        }
                    }else if (srcObj.offsetHeight - evt.offsetY > 8 && srcObj.offsetWidth - evt.offsetX <= 8) {
                        srcObj.style.cursor = "column-resize";
                    }else if (evt.offsetY > 8 && evt.offsetX <= 8) {
                        if (srcObj.parentNode.rowIndex) {
                            srcObj.style.cursor = "column-resize";
                        }
                    }else {
                        this.resizeable = false;
                        srcObj.style.cursor = "default";
                    }
                }
            };
            //放开鼠标恢复原位
            document.onmouseup = function(event) {
                this.mousedown = false;
                this.targetTd = null;
                this.coltargetTd = null;
                this.resizeable = false;
                document.body.style.cursor = "default";
            };
        },
        // 添加监听
        addListener(element, type, listener, useCapture) {
            //这是两种写法，对应不同浏览器
            element.addEventListener
                ? element.addEventListener(type, listener, useCapture)
                : element.attachEvent("on" + type, listener);
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
        onClickSelect(row,sourceId,targetId){
            if(this.linkTypeId==-1){
                VxeTable.modal.message({content:"请选择关联类型",status:"info"})
            }else if(sourceId==targetId){
                VxeTable.modal.message({content:"关联失败，同一工作项不能进行关联",status:"info"})
            }else if(this.newTrackerLinks .find(v=>(v.sourceItemId==sourceId&&v.targetItemId==targetId)
                            ||(v.sourceItemId==targetId&&v.targetItemId==sourceId))||
                    this.trackerLinks .find(v=>(v.sourceItemId==sourceId&&v.targetItemId==targetId)
                        ||(v.sourceItemId==targetId&&v.targetItemId==sourceId))){
                VxeTable.modal.message({content:"关联失败,已存在关联",status:"info"})
            }else {
                this.hasChange=true
                if(this.linkDirection=='1'){
                    this.newTrackerLinks.push({
                        sourceItemId:sourceId,
                        targetItemId:targetId,
                        linkTypeId:this.linkTypeId
                    })
                }else{
                    this.newTrackerLinks.push({
                        sourceItemId:targetId,
                        targetItemId:sourceId,
                        linkTypeId:this.linkTypeId
                    })
                }
                
            }
        },
        initData(){
            findLinkTypes(this.projectId).then(resp => {
                this.trackerRelations = resp
            })
        },
        loadWorkItems(pagination,direction) { 
            this.loading = true;
            let keyword = this.keyword
            let sort = [];
            let pageable = {
                page: pagination?.current-1||0,
                size: pagination?.pageSize||50,
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
            findTrackerItems(this.projectId, this.tracker.id, null, filter, keyword, pageable, sort).then(resp => {
                if(direction=='row'){
                    this.itemDataRow.push(...resp.content)
                }else if(direction=='col'){
                    this.itemDataCol.push(...resp.content)
                    resp.content.forEach(item=>{this.addTableColumn(item)})
                }else{
                    this.itemDataRow=Object.assign([],resp.content)
                    this.itemDataCol=Object.assign([],resp.content)
                    this.paginationRow.current=1
                    this.paginationRow.total=parseInt(resp.totalElements)
                    this.paginationCol.current=1
                    this.paginationCol.total=parseInt(resp.totalElements)
                    this.trackerLinks=[];
                    this.itemIds= new Set();
                    this.tableColumn=[]
                    this.itemDataCol.forEach((item,index)=>{
                        if(index==0){
                            this.tableColumn.push({
                                field: item.id,
                                title: '',
                                editRender: {},
                                width: 150,
                                showHeaderOverflow:"tooltip",
                                slots: {default: 'first_default'}
                            })
                        }else{
                            this.addTableColumn(item)
                        }
                    })
                }
                
                let len=this.itemIds.size
                resp.content.map(item=>item.id).forEach(item=>this.itemIds.add(item))
                this.loading = false;
                if(len!=this.itemIds.size){
                    findTrackerLinksByItemIds(Array.from(this.itemIds)).then(res=>{
                        if(!direction){
                            this.trackerLinks=[];
                        }
                        res.forEach(item=>{
                            this.trackerLinks.push({id:item.id,sourceItemId:item.sourceItem?.id,targetItemId:item.targetItem?.id,linkTypeId:item.linkType?.id})
                        })
                    })
                }
                
            }).finally(() => {
                this.loading = false;
            })
            
        },
        addTableColumn(item){
            this.tableColumn.push({
                field: item.id,
                title: item.name,
                editRender: {},
                width: 38,
                showHeaderOverflow:"tooltip",
                slots: { default: 'default',
                    header: ()=>{
                        return [
                        <div style="height: 150px;cursor:pointer;" onClick={ ()=> this.onEditTrackerItem(item)}>
                                <div class="table-header-rotate">
                                    <a-icon {...{props:{component:this.tracker.icon}}}/>  
                                    {' '+this.currentProjectKeyName + '-' + item.itemNo + ' - '+ (item.name||'')}</div>
                            </div>
                        ]
                    }
                }
            })
        },
        getTrackerLinkDirection(sourceId,targetId){
            let directionDelOne=this.delTrackerLinks.find(v=>v.sourceItemId==sourceId&&v.targetItemId==targetId)
            if(directionDelOne&&(this.linkTypeId==-1||this.linkTypeId==directionDelOne.linkTypeId)){
                return "delOne"
            }
            let directionDelTwo=this.delTrackerLinks.find(v=>v.sourceItemId==targetId&&v.targetItemId==sourceId)
            if(directionDelTwo&&(this.linkTypeId==-1||this.linkTypeId==directionDelTwo.linkTypeId)){
                return "delTwo"
            }
            let directionNewOne=this.newTrackerLinks.find(v=>v.sourceItemId==sourceId&&v.targetItemId==targetId)
            if(directionNewOne&&(this.linkTypeId==-1||this.linkTypeId==directionNewOne.linkTypeId)){
                return "newOne"
            }
            let directionNewTwo=this.newTrackerLinks.find(v=>v.sourceItemId==targetId&&v.targetItemId==sourceId)
            if(directionNewTwo&&(this.linkTypeId==-1||this.linkTypeId==directionNewTwo.linkTypeId)){
                return "newTwo"
            }
            let directionOne=this.trackerLinks.find(v=>v.sourceItemId==sourceId&&v.targetItemId==targetId)
            if(directionOne&&(this.linkTypeId==-1||this.linkTypeId==directionOne.linkTypeId)){
                return "one"
            }
            let directionTwo=this.trackerLinks.find(v=>v.sourceItemId==targetId&&v.targetItemId==sourceId)
            if(directionTwo&&(this.linkTypeId==-1||this.linkTypeId==directionTwo.linkTypeId)){
                return "two"
            }
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
            that.bodyWidth=e.bodyWidth
            that.scrollWidth=e.scrollWidth
            let scrollTop = e.scrollTop
            let scrollHeight = e.scrollHeight
            let clientHeight = e.bodyHeight
            if(e.isX){
                scrollTop=e.scrollLeft
                scrollHeight=that.scrollWidth
                clientHeight=that.bodyWidth
            }
            if(scrollTop + clientHeight >= scrollHeight-1){
                if (!this.timer) {
                    this.timer = setTimeout(() => { 
                        that.timer = null;
                        if(e.isX){
                            if(that.paginationCol.total){
                                let current=that.paginationCol.current
                                let pageSize=that.paginationCol.pageSize
                                if(that.paginationCol.total<=current*pageSize)
                                {
                                    return
                                }
                            }
                            that.paginationCol.current+=1
                            that.loadWorkItems(that.paginationCol,"col")
                        }else{//isY
                            if(that.paginationRow.total){
                                let current=that.paginationRow.current
                                let pageSize=that.paginationRow.pageSize
                                if(that.paginationRow.total<=current*pageSize)
                                {
                                    return
                                }
                            }
                            that.paginationRow.current+=1
                            that.loadWorkItems(that.paginationRow,"row")
                        }
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
</style>
