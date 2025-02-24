<template>
    <a-popover v-model="visible" trigger="click" placement="bottomLeft"
    :style="{pointerEvents: !disabled?'':'none'}" overlayClassName="tracker-select-dropdown">
        <template slot="content">
            <div @click.stop>
                <a-input-search :placeholder="'搜索'+field?.title" style="width: 100%;margin-bottom: 5px" />
                <div class="tracker-select-option"
                    :style="{ color: isCurrentField(p.id)? '#338fe5' : '' }"
                    v-for="p in projectUsers" :key="p.id" @click="onClickUserSelect(p)">
                    <div class="option-item">
                        <div class="option-item-content">
                            <h-avatar :name="p.name" :icon="p.icon"></h-avatar>
                            <span class="option-item-subtext">({{ p.email }})</span>
                        </div>
                        <div class="option-item-icon "><a-icon type="check"
                            v-if="isCurrentField(p.id)" />
                        </div>
                    </div>
                </div>
            </div>
        </template>
        <slot>
            <div v-if="size=='small'" style="cursor:pointer">
                <div v-if="trackerItem[getsystemProperty(field?.systemProperty)]">
                    <h-avatar :name="trackerItem[getsystemProperty(field?.systemProperty)]?.name" 
                        :icon="trackerItem[getsystemProperty(field?.systemProperty)]?.icon"></h-avatar>
                </div>
                <div v-else>未分配</div>
            </div>
            <quick-picker v-else :title="trackerItem[getsystemProperty(field?.systemProperty)]?.name" :sub-title="field?.title"
                :icon-url="iconUrl(trackerItem[getsystemProperty(field?.systemProperty)]?.icon)" 
                :icon-text="trackerItem[getsystemProperty(field?.systemProperty)]?.name">
                <template slot="icon">
                    <h-avatar v-if="trackerItem[getsystemProperty(field?.systemProperty)]" 
                        :name="trackerItem[getsystemProperty(field?.systemProperty)]?.name" :size="32"
                        :icon="trackerItem[getsystemProperty(field?.systemProperty)]?.icon" :isShowName="false"></h-avatar>
                </template>
            </quick-picker>
        </slot> 
        
    </a-popover>
</template>

<script>
import { iconUrl } from "@/utils/util"
import HAvatar from '@/components/avatar/h-avatar.vue';
import VXETable from "vxe-table";
import {
    findProjectUsers
} from "@/services/tracker/ProjectRoleMemberService";
import QuickPicker from '@/components/select/QuickPicker.vue';
import {changeSystemField,addWatch,cancelWatch} from "@/services/tracker/TrackerItemService"

export default ({
    name: "TrackerItemUserSelect",
    components: {
        QuickPicker, HAvatar, 
    },
    props: {
        projectId:{
            required: true
        },
        trackerItem: {
            required: true
        },
        field: {
            required: false
        },
        size: {
            required: false
        },
        projectUserList:{
            required:false
        },
        disabled:{
            required:false,
            default: false,
        },
        mode: {
            required: false,
        }
    },
    computed:{
    },
    mounted() {
        this.loadData();
    },
    data() {
        return {
            visible: false,
            projectUsers: []
        };
    },
    methods: {
        isCurrentField(userId){
            if(this.mode=='multiple'&&this.trackerItem){
                if(this.trackerItem[this.getsystemProperty(this.field?.systemProperty)]?.find(v=>v.id===userId)){
                    return true;
                }
            }else if(this.trackerItem){
                if(userId=== this.trackerItem[this.getsystemProperty(this.field?.systemProperty)]?.id){
                    return true;
                }
            }
            return false;

        },
        getsystemProperty(systemProperty){
            if(systemProperty){
                systemProperty=systemProperty.toString();
                if(systemProperty.endsWith('Id')){
                    systemProperty=systemProperty.substring(0,systemProperty.length-2)
                }
            }
            return systemProperty
        },
        loadData() {
            if(this.projectUserList?.length>0){
                this.projectUsers=this.projectUserList;
            }else{
                findProjectUsers(this.projectId).then(resp => {
                    this.projectUsers = resp;
                });
            }
            
        },
        onClickUserSelect(projectUser){
            if(!this.trackerItem){
                return;
            }
            if(this.mode=='multiple'){
                if(!this.trackerItem[this.getsystemProperty(this.field?.systemProperty)]){
                    this.trackerItem[this.getsystemProperty(this.field?.systemProperty)]=[];
                }
                let index=this.trackerItem[this.getsystemProperty(this.field?.systemProperty)].findIndex(v=>v.id===projectUser.id)
                if(index!=-1){
                    this.trackerItem[this.getsystemProperty(this.field?.systemProperty)].splice(index, 1);  
                    if(this.field?.systemProperty==='watchers'){
                        cancelWatch(this.trackerItem?.id,projectUser).then(res=>{
                            VXETable.modal.message({ content: '关注者移除成功', status: 'success' })
                        }).catch(()=>{
                            this.trackerItem[this.getsystemProperty(this.field?.systemProperty)].push(projectUser)
                        }).finally(()=>{
                            this.$emit("refresh")
                        })
                    }
                }else{
                    this.trackerItem[this.getsystemProperty(this.field?.systemProperty)].push(projectUser)
                    if(this.field?.systemProperty==='watchers'){
                        addWatch(this.trackerItem?.id,projectUser).then(res=>{
                            VXETable.modal.message({ content: '关注者添加成功', status: 'success' })
                        }).catch(()=>{
                            this.trackerItem[this.getsystemProperty(this.field?.systemProperty)].splice(-1, 1);
                        }).finally(()=>{
                            this.$emit("refresh")
                        })
                    }        
                }
            }else{
                if(this.trackerItem[this.getsystemProperty(this.field?.systemProperty)]?.id!=projectUser.id){
                    this.trackerItem[this.getsystemProperty(this.field?.systemProperty)]=projectUser
                    changeSystemField(this.trackerItem.id, this.field?.systemProperty, projectUser?.id).then(resp => {
                        this.$emit("refresh");
                    })
                }
            }
            this.visible=false;
        },
        iconUrl(icon){
            return iconUrl(icon);
        },
    }
});
</script>
<style lang="less" scoped>
.domain-list-cell-subtext {
    margin-left: 5px;
    font-size: 12px;
    color: #606060;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 180px;
    flex: 0 0 auto;
    color: var(--gray-80);
}
.tracker-select-option {
    font-size: 12px;
    cursor: pointer;
    display: block;
    font-size: 14px;
    font-weight: 400;
    line-height: 22px;
    min-height: 34px;
    min-width: 100px;
    padding: 6px 10px;
    position: relative;
    color: #000;

    &:hover {
        background-color: #f8f8f8;
        color: #338fe5;
    }

    .option-item {
        display: flex;

        .option-item-content {
            flex: auto;
            display: inline-flex;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .option-item-icon {
            flex: none;
            display: inline-flex;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            padding-top: 5px;
            margin-left: 10px;
        }


        &>a {
            color: inherit;
        }

        &>a:hover {
            color: inherit;
        }

        .option-item-text {}

        .option-item-subtext {
            margin-left: 5px;
            font-size: 12px;
            color: #606060;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            max-width: 180px;
            flex: 0 0 auto;
            color: var(--gray-80);
        }
    }
}
</style>
