<template>
    <a-spin :spinning="loading">
        <div style="position:relative;width:auto;overflow:auto hidden;height:82vh;">
            <div style="background-color:#fff;height:100%;display: flex;width: min-content;">
                
                <div v-for="type in tracker.trackerStatuses" :key="type.id"
                    :class="drag && dragFilterStatus.indexOf(type.name) != -1 ? 'kanban-dragable-able' : ''"
                    class="kanban-row-cell" @scroll="e=>onScroll(e,type.name,type.id)">
                    <div style="" class="kanban-row-cell-header">
                        <span style="font-size: 15px;color: #303030;">{{ type.name }}</span>
                        <span>({{ paginations[type.id]?.total }})</span>
                    </div>
                    <draggable :loading="loading" v-model="kanbanData[(type.name)]" :group="getDragGroup(type.name)" filter=".forbid"
                        :sort="false" animation="300" dragClass="dragClass" ghostClass="ghostClass"
                        @start="onStart(type.name)" @end="onEnd(type.name)">
                        <transition-group v-if="!dragselected || dragselected == type.name"
                            style="display: block;height:70vh;">
                            <div v-for="item in kanbanData[type.name]" :key="item.id" @mousedown="onMouseDown(item)"
                                style="margin-top:5px;">
                                <div style="margin-bottom:10px;width: 100%;cursor: pointer;"
                                    @click="onEditTrackerItem(item)">
                                    <div
                                        style="background: #fff;padding: 10px;margin:0 10px;color: #303030;box-shadow: 0 1px 1px 0 rgba(31,31,31,.1);">

                                        
                                        <div style="white-space: nowrap;text-overflow: ellipsis;overflow: hidden;"> 
                                            <a-icon v-if="tracker.icon" :component="tracker.icon" />
                                            <span style="margin-left:5px">{{ item.name }}</span>
                                        </div>
                                        
                                        <div style="display: flex;justify-content: space-between;">
                                            <div style="width: 85%;min-height: 30px;display:flex;justify-content: flex-start;flex-wrap:wrap;">
                                                <template v-for="field in listFields">

                                                    <a-tooltip v-if="field.systemProperty=='itemNo'" :key="field.id" :title="'编号：' + currentProjectKeyName + '-' + item.itemNo"
                                                        :overlayStyle="{ fontSize: '10px' }">
                                                        <div class="ui-tag-key-field">
                                                            {{ currentProjectKeyName + '-' + item.itemNo }}</div><br>
                                                    </a-tooltip>
                                                    <a-tooltip  v-else-if="field.systemProperty=='priority'&&item.priority" :key="field.id" :title="'优先级：' + item.priority?.name"
                                                        :overlayStyle="{ fontSize: '10px' }">
                                                        <div class="ui-tag-key-field"
                                                            :style="{ color: item.priority?.color, backgroundColor: item.priority?.backgroundColor }">
                                                            {{ item.priority?.name }}</div>
                                                    </a-tooltip>

                                                    <a-tooltip v-else-if="field.systemProperty&&getPropertyValue(item,field.systemProperty)" :key="field.id" 
                                                        :title="field.name+'：'+getPropertyValue(item,field.systemProperty)"
                                                        :overlayStyle="{ fontSize: '10px' }">
                                                        <div class="ui-tag-key-field">{{ getPropertyValue(item,field.systemProperty) }}</div>
                                                    </a-tooltip>   

                                                    <a-tooltip v-else-if="!field.systemProperty&&item.values[field.id]" :key="field.id" :title="field.name+'：'+(item.values[field.id])"
                                                        :overlayStyle="{ fontSize: '10px' }">
                                                        <div class="ui-tag-key-field">{{ item.values[field.id]}}</div>
                                                    </a-tooltip> 
                                                </template>

                                                <a-tooltip
                                                    :title="'预计工时：' + (item.estimateWorkingHours || 0) + '小时\n' + '剩余工时：' + (item.remainingWorkingHours || 0) + '小时'"
                                                    :overlayStyle="{ fontSize: '10px', width: '120px' }">
                                                    <div @click.stop="onClickWorkHours(item)"
                                                        @mouseleave="registeredMouseLeave($event)"
                                                        @mouseover="registeredMouseOver($event)"
                                                        class="ui-tag-key-field" style="min-width: 58px;position: relative;">
                                                        <div id="registerIcon" style="position: absolute;top: 0;left: 0;
                                                            width: 100%; height: 100%;text-align: center;"
                                                            class="register-icon-show"><a-icon type="edit"/>&nbsp;登记</div>
                                                        <div id="registerSpan" v-if="!loading" style="text-align: center;">{{
                                                            item.estimateWorkingHours || 0 }}h/{{ item.remainingWorkingHours || 0 }}h</div>
                                                    </div>
                                                </a-tooltip>
                                            </div>
                                            <span class="card-avatar">
                                                <a-tooltip :title="item.createBy.name"
                                                    :overlayStyle="{ fontSize: '10px' }">
                                                    <h-avatar :name="item.createBy.name" :icon="item.createBy.icon" :isShowName="false"></h-avatar>
                                                </a-tooltip>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div v-if="!kanbanData[type.name]?.length" :key="'暂无工作项'" class="forbid"
                                style="position:absolute;top:45%;width:100%">
                                <div style="width: 100%;text-align: center;">暂无工作项</div>
                            </div>
                        </transition-group>
                        <transition-group v-else-if="dragFilterStatus.indexOf(type.name) != -1"
                            style="display: block;height:70vh;">

                        </transition-group>
                        <transition-group v-else style="display: block;height:70vh;">
                            <div :key="'不能到达此状态'" class="forbid" style="position:absolute;top:45%;width:100%">
                                <div style="width: 100%;text-align: center;"><a-icon style="margin-right:3px;"
                                    type="close-circle" theme="filled" />不能到达此状态</div>
                            </div>
                        </transition-group>
                    </draggable>
                </div>
            </div>
        </div>
    </a-spin>
</template>

<script>
import Vue from "vue"
import { mapGetters } from "vuex";
import HAvatar from '@/components/avatar/h-avatar.vue';
import draggable from 'vuedraggable'
import {cloneDeep} from 'lodash'
import XEUtils from "xe-utils";
import {
    stateChange
} from "@/services/tracker/TrackerItemService";
import {
    findTrackerItems
} from "@/services/tracker/TrackerItemService";
import { roundToNearestMinutesWithOptions } from "date-fns/fp";

export default {
    name: 'KanbanLayout',
    props: {
        tracker: Object,
        projectId: String,
        itemData: Array,
        isShowKanban: Boolean,
        currentView:Object,
        conditionGroups:Array,
    },
    components: {
        draggable, HAvatar
    },
    computed:{
        ...mapGetters("project", ["currentProjectKeyName"]),
    },
    data() {
        return {
            loading: false,
            drag: false,
            dragFilterStatus: [],
            dragselected: '',
            dragselectedItem: '',
            kanbanData: {},
            statusData:{},
            paginations:{},
            scrollDebounce:{},
            timer:'',
            listFields:[],
        }
    },
    mounted() {

    },
    watch: {
        isShowKanban:{
            handler:function(newVal,oldVal){
                if(newVal){
                    this.loadData()
                }
            }
        },
    },
    methods: {
        //滚动加载
        mattersMethod(conditionGroups,field,statusId){
            if(!conditionGroups||conditionGroups.length==0){
                conditionGroups=[{conditions:[]}];
            }
            conditionGroups.forEach(item=>{
                item.conditions.push({   
                    "id" : field.id,
                    "type": "STATUS",
                    "field": field.field,
                    "value": [
                        statusId
                    ],
                    "operator": "INCL"
                }) 
            })
            return conditionGroups
        },
        loadData(){
            let field={id:11,field:"status"}
            this.kanbanData={}
            this.tracker.trackerStatuses.forEach(status=>{
                this.$set(this.scrollDebounce,status.id,undefined)
                this.$set(this.paginations,status.id,{current:1,pageSize:20,total:0})
                let conditionGroups=cloneDeep(this.conditionGroups);
                conditionGroups=this.mattersMethod(conditionGroups,field,status.id)
                this.loadWorkItems(conditionGroups,this.paginations[status.id],status.id,status.name)
            })
            this.loadFields();  
        },
        loadFields(){
            let layout = this.findLayout("LIST")
            if (layout) {
                const fields = layout.fields
                this.listFields=[];
                fields.forEach(f => {
                    this.listFields.push(this.findFields(f.id))
                })
            }

        },
        loadWorkItems(conditionGroups,pagination,statusId,statusName) { 
            this.loading = true;
            let keyword = this.keyword
            let sort = [];
            let pageable = {
                page: pagination.current-1,
                size: pagination.pageSize,
            }
            let filter = { "conditionGroups": conditionGroups }
            this.currentView.viewConfig?.orderBys?.forEach(item => {
                if(item.field.systemProperty){
                    sort.push(item.field.systemProperty + "," + item.orderBy)
                }else if(this.tracker.trackerFields.find(field=>field.id==item.field.id)){
                    item.field=this.tracker.trackerFields.find(field=>field.id==item.field.id)
                    sort.push(item.field.systemProperty + "," + item.orderBy)
                }
            })
            findTrackerItems(this.projectId, this.tracker.id, null, filter, keyword, pageable, sort).then(resp => {
                // this.statusData[statusId] = resp.content
                if(this.kanbanData[statusName]){
                    this.kanbanData[statusName].push(...resp.content)
                }else{
                    this.kanbanData[statusName]=resp.content
                }
                this.paginations[statusId].total = parseInt(resp.totalElements);
            }).finally(() => {
                this.loading = false;
            })
            
        },
        findLayout(layoutName) {
            let layout = null;
            if (this.tracker && this.tracker.trackerLayouts && this.tracker.trackerLayouts.length > 0) {
                layout = this.tracker.trackerLayouts.filter(lay => { return lay.name === layoutName })[0]
            }
            if(!layout){
                layout = {name:layoutName,keyFields:[],fields:[],sections:[]}
                if(!this.tracker.trackerLayouts){
                    this.tracker.trackerLayouts=[];
                }
                this.tracker.trackerLayouts.push(layout)
            }
            return layout;
        },
        findFields(fieldId) {
            if (this.tracker && this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                return this.tracker.trackerFields.filter(f => { return f.id === fieldId })[0]
            }
            return null;
        },
        getPropertyValue(obj, property){
            if(property.endsWith('Id')){
                property=property.substring(0, property.length - 2)
            }
            if(typeof obj[property] == 'object'){
                return obj[property].name;
            }else{
                return obj[property]
            }
        },
        onScroll(e,typeName,typeId){
            if(this.loading){
                return
            }
            const that =this
            let scrollTop = e.target.scrollTop
            let scrollHeight = e.target.scrollHeight
            let clientHeight = e.target.clientHeight
            if(scrollTop + clientHeight >= scrollHeight-1){
                if (!this.timer) {
                    this.timer = setTimeout(() => { 
                        that.timer = null;
                        if(that.paginations[typeId].total){
                            let current=that.paginations[typeId].current
                            let pageSize=that.paginations[typeId].pageSize
                            if(that.paginations[typeId].total<=current*pageSize)
                            {
                                return
                            }
                        }
                        let field={id:11,field:"status"}
                        let conditionGroups=cloneDeep(that.conditionGroups);
                        conditionGroups=that.mattersMethod(conditionGroups,field,typeId)
                        that.paginations[typeId].current+=1
                        that.loadWorkItems(conditionGroups,that.paginations[typeId],typeId,typeName)
                    }, 300)
                }
            }
  
            
        },  
        //
        registeredMouseOver(e) {
            e.currentTarget.querySelector("#registerIcon").className = ''
            e.currentTarget.querySelector("#registerSpan").className = 'register-icon-show'
        },
        registeredMouseLeave(e) {
            e.currentTarget.querySelector("#registerIcon").className = 'register-icon-show'
            e.currentTarget.querySelector("#registerSpan").className = ''
        },
        onClickWorkHours(row){
            this.loading=true
            this.$emit("onClickWorkHours",row)
            this.loading=false
        },
        onEditTrackerItem(row){
            this.$emit("onEditTrackerItem",row)
        },
        getDragGroup(typeName) {
            if (!this.dragselected || this.dragselected == typeName) {
                return "site"
            } else if (this.dragFilterStatus.indexOf(typeName) != -1) {
                return "site"
            } else {
                return "filter"
            }
        },
        onMouseDown(item) {
            const currentStatusId = item.status?.id
            let transition = this.tracker.trackerStateTransitions?.filter(s => s.transitionFrom.id === currentStatusId)
            this.dragFilterStatus = transition.map(_ => _.name);
        },
        onStart(typeName) {
            this.drag = true;
            this.dragselected = typeName;
        },
        onEnd(typeName) {
            this.drag = false;
            this.dragFilterStatus = [];
            this.dragselected = '';
            let changeItem = '';
            for (let status of this.tracker.trackerStatuses) {
                for (let item of this.kanbanData[status.name]) {
                    if (item.status?.name != status.name) {
                        changeItem = item;
                        changeItem.status = status;
                    }
                }
            }
            if (changeItem) {
                let stateTransition = this.tracker.trackerStateTransitions?.find(s => s.name == changeItem.status.name)
                stateChange(changeItem.id, stateTransition.id).then(resp => {
                    this.refresh()
                })
            }
        },
        refresh(){
            this.$emit('refresh');
        },
    },

}
</script>

<style scoped lang="less">

.register-icon-show {
    // display: none;
    visibility: hidden;
}
.kanban-row-cell {
    // height:100%;
    position: relative;
    width: 300px;
    background-color: #eee;
    margin: 10px 5px;
    overflow: auto;
    user-select: none;

    .kanban-row-cell-header {
        height: 40px;
        margin-left: 10px;
        margin-right: 10px;
        padding: 0 5px;
        display: flex;
        flex-direction: row;
        align-items: center;
    }
}
.kanban-dragable-able {
    background: #eaf3fc;
    border-width: 2px;
    border-color: #338fe5;
    border-style: dashed;
}

.kanban-dragable-able-selected {
    background: #e9f7f2;
    border-width: 2px;
    border-color: #24b47e;
    border-style: dashed;
    z-index: 3;
}
.ui-tag-key-field {
    max-width: 90px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-right: 10px;
    margin-top: 5px;
    background-color: rgba(144, 144, 144, .15);
    height: 20px;
    font-size: 12px;
    color: #303030;
    font-weight: 500;
    line-height: 20px;
    padding-left: 5px;
    padding-right: 5px;
    border-radius: 3px;
    display: inline-block;
}
</style>
