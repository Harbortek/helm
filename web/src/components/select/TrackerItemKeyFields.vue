<template>
    <div class="ui-task-primary-fields">
    <div v-for="item in keyFields" :key="item.id" :style="{width:tracker.keyFieldSmall?'45%':'18%'}">

            <tracker-item-user-select v-if="item.inputType == 'USER'" @refresh="refresh" :projectId="projectId" :field="item"
                :trackerItem="trackerItem" :disabled=trackerItem?.notPagePerm></tracker-item-user-select>

            <a-popover v-model="popoverVisible[item.id]" v-else-if="item.systemProperty == 'status'"  trigger="click"
                placement="bottomLeft"  :style="{pointerEvents: !trackerItem?.notPagePerm?'':'none'}"
             overlayClassName="tracker-select-dropdown">
                <template slot="content">
                    <tracker-item-status-popover :disabled="true" :projectId="projectId" :trackerItem="trackerItem"
                        :tracker="tracker" @change="onChangeStatus(item)"></tracker-item-status-popover>
                </template>
                <quick-picker :title="trackerItem?.status?.name" :sub-title="'当前状态'">
                    <template slot="icon">
                        <a-avatar icon="arrow-right" v-if="trackerItem?.meaning"
                            :style="{ color: '#fff', backgroundColor: trackerItem?.meaning?.color }" />
                        <a-avatar icon="arrow-right" v-else
                            :style="{ color: '#fff', backgroundColor: '#338fe5' }" />
                    </template>
                </quick-picker>
            </a-popover>
        

            <a-popover v-else-if="item.systemProperty == 'priority'" v-model="priorityPopoverVisible" trigger="click"
             placement="bottomLeft" :style="{pointerEvents: !trackerItem?.notPagePerm?'':'none'}"
            overlayClassName="tracker-select-dropdown">
                <template slot="content">
                    <div @click.stop v-if="trackerItem?.values">
                        <div class="tracker-select-option"
                            :style="{ color: p.id === trackerItem?.priority?.id ? '#338fe5' : '' }" v-for="p in priorities"
                            :key="p.id">
                            <div class="option-item">
                                <div class="option-item-content" @click="changePriority(p)">
                                    <span class="option-item-text"> {{ p.name }}</span>
                                </div>
                                <div class="option-item-icon "><a-icon type="check"
                                        v-if="p.id === trackerItem?.priority?.id" />
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
                <quick-picker :title="trackerItem?.priority?.name" :sub-title="item.name">
                    <template slot="icon">
                        <a-avatar
                            :style="{ color: '#fff', backgroundColor: trackerItem?.priority?.name == '最高' ? trackerItem?.priority?.backgroundColor : trackerItem?.priority?.color }">
                            <a-icon slot="icon" component="priority" />
                        </a-avatar>
                    </template>
                </quick-picker>
            </a-popover>

            <a-popover v-else-if="item.inputType == 'OPTIONS'&&item.system" v-model="popoverVisible[item.id]" trigger="click"
             placement="bottomLeft" :style="{pointerEvents: !trackerItem?.notPagePerm?'':'none'}"
                overlayClassName="tracker-select-dropdown">
                <template slot="content">
                    <div @click.stop>
                        <div class="tracker-select-option"
                            :style="{ color: p.id === getFieldsData(item) ? '#338fe5' : '' }"
                            v-for="p in item.items" :key="p.id">
                            <div class="option-item">
                                <div class="option-item-content" @click="changeSystemType(item, p.id)">
                                    <span class="option-item-text"> {{ p.name }}</span>
                                </div>
                                <div class="option-item-icon"><a-icon type="check"
                                        v-if="p.id === getFieldsData(item)" />
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
                <quick-picker :title="getFieldsDataValue(item)?.name || '未设置'" :sub-title="item.name">                    
                    <template slot="icon">
                        <a-avatar :style="{ color: getFieldsDataValue(item)?.color||'#606060', backgroundColor: getFieldsDataValue(item)?.backgroundColor||'#e8e8e8' }">
                            <a-icon type="book" />
                        </a-avatar>
                    </template>
                </quick-picker>
            </a-popover>

            <a-popover v-else-if="item.inputType == 'OPTIONS'" v-model="popoverVisible[item.id]" trigger="click"
             placement="bottomLeft"  :style="{pointerEvents: !trackerItem?.notPagePerm?'':'none'}"
                overlayClassName="tracker-select-dropdown">
                <template slot="content">
                    <div @click.stop v-if="trackerItem.values">
                        <div class="tracker-select-option"
                            :style="{ color: p.name === trackerItem?.values[item.id] ? '#338fe5' : '' }"
                            v-for="p in item.items" :key="p.id">
                            <div class="option-item">
                                <div class="option-item-content" @click="changeType(item.id, p.id, p.name)">
                                    <span class="option-item-text"> {{ p.name }}</span>
                                </div>
                                <div class="option-item-icon"><a-icon type="check"
                                        v-if="p.name === trackerItem?.values[item.id]" />
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
                <quick-picker v-if="trackerItem?.values" :title="getFieldsDataValue(item)?.name || '未设置'" :sub-title="item.name">                    <template slot="icon">
                        <a-avatar :style="{ color: '#606060', backgroundColor: '#e8e8e8' }">
                            <a-icon type="book" />
                        </a-avatar>
                    </template>
                </quick-picker>
            </a-popover>
            <a-date-picker v-else-if="item.inputType == 'DATE'" v-model="newDateValue[item.id]" show-time
                 @ok="onChangeDatePicker(item)" :placeholder="item.name">
                <quick-picker   :title="getDateValue(item) || '未设置'" :sub-title="item.name"> 
                    <template slot="icon">
                        <a-avatar :style="{ color: '#606060', backgroundColor: '#e8e8e8' }">
                            <a-icon type="book" />
                        </a-avatar>
                    </template>
                </quick-picker>
            </a-date-picker>
            <div v-else  class="quick-default">
                <quick-picker :title="trackerItem[item.systemProperty] || '未设置'" :sub-title="item.name">
                    <template slot="icon">
                        <a-avatar :style="{ color: '#606060', backgroundColor: '#e8e8e8' }">
                            <a-icon type="book" />
                        </a-avatar>
                    </template>
                </quick-picker>
            </div>
            <!-- </a-popover> -->
        </div>
    </div>
</template>

<script>
import {
    findProjectUsers
} from "@/services/tracker/ProjectRoleMemberService";
import QuickPicker from '@/components/select/QuickPicker.vue';
import { changeSystemField, stateChange, changeCustomerField } from "@/services/tracker/TrackerItemService"
import TrackerItemUserSelect from '@/components/select/TrackerItemUserSelect.vue';
import TrackerItemStatusPopover from '@/components/tool/TrackerItemStatusPopover.vue';
import {
    findEnumsByCode
} from "@/services/system/EnumService";
import _ from 'lodash'
import moment from 'moment'


export default ({
    name: "TrackerItemKeyFields",
    components: {
        QuickPicker, TrackerItemUserSelect,TrackerItemStatusPopover
    },
    props: {
        projectId: {
            required: true
        },
        trackerItem: {
            required: true
        },
        keyFields: {
            required: true
        },
        tracker: {
            required: true
        },
    },
    watch: {
        // 监听keyFields属性变化
        keyFields: {
            handler(newVal) {
                if(newVal) {
                    for (let item of newVal) {
                        this.$set(this.popoverVisible, item.id, false)
                        if(item.inputType == 'DATE'){
                            if(item.system){
                                this.$set(this.newDateValue, item.id, moment(this.trackerItem[item.systemProperty]))
                            }else{
                                this.$set(this.newDateValue, item.id, moment(this.trackerItem.values[item.id]))
                            }
                        }
                    }
                }
            },
            immediate: true
        }
    },
    computed:{
    },
    mounted() {
        if(this.projectId){
            this.loadData();
        }
    },
    data() {
        return {
            visible: false,
            projectUsers: [],
            priorityPopoverVisible: false,  
            popoverVisible: {},
            priorities: [],
            newDateValue:{},
        };
    },
    methods: {
        changeType(ItemId,pId,pName) {
            this.onChangeCustomerField(ItemId, pId);
            this.popoverVisible[ItemId] = false
        },
        changeSystemType(item,value) {
            changeSystemField(this.trackerItem.id, item.systemProperty,value).then(resp => {
                this.refresh()
            })
            this.popoverVisible[item.id] = false
        },
        onChangeCustomerField(id, value) {
            changeCustomerField(this.trackerItem.id, id, value).then(resp => {
                this.refresh()
            })
        },
        changePriority(p) {
            this.trackerItem.priority=p;
            changeSystemField(this.trackerItem.id, 'priority', p.id).then(resp => {
                this.refresh()
            })
            this.priorityPopoverVisible=false
        },
        onChangeStatus(item){
            this.popoverVisible[item.id]=false
            this.refresh();
        },
        refresh(){
            this.$emit("refresh");
        },
        getSystemProperty(field) {
            let property = _.get(this.trackerItem, field.systemProperty)
            if (property) {
                if (property instanceof Object) {
                    return field.systemProperty + ".id"
                } else {
                    return field.systemProperty
                }
            }
            if (field.systemProperty.endsWith("Id")) {
                return field.systemProperty.substring(0, field.systemProperty.length - 2) + ".id"
            }
            
            return "";
        },
        getFieldsData(field) {
            if (field.system) {
                var systemProperty = this.getSystemProperty(field)
                if (systemProperty) {
                    return _.get(this.trackerItem, systemProperty)
                }
                return "";
            } else {
                if (this.trackerItem?.values) {
                    return this.trackerItem?.values[field.id]
                } else {
                    return "";
                }
            }
        },
        getFieldsDataValue(field){
            let value= this.getFieldsData(field);
            let item = field.items.find(i => i.id === value);
            return item;
        },
        getDateValue(field){
            if(field.system){
                return this.trackerItem[field.systemProperty]
            }else{
                return this.trackerItem.values[field.id]
            }
        },
        onChangeDatePicker(field) {
            if (this.newDateValue[field.id]) {
                let datePicker = moment(this.newDateValue[field.id]).format("YYYY-MM-DD HH:mm:ss")
                console.log("datePicker",datePicker)
                if(field.system){
                    this.changeSystemType(field, datePicker);
                }else{
                    this.onChangeCustomerField(field.id, datePicker);
                }
            } else {
                this.newDateValue[field.id] = null;
            }
            this.popoverVisible[field.id] = false
        },
        loadData() {
            findProjectUsers(this.projectId).then(resp => {
                this.projectUsers = resp;
            });
            findEnumsByCode('TRACKER_PRIORITY').then(resp => {
                this.priorities = resp
            })
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

.ui-task-primary-fields {
    display: flex;
    justify-content: flex-start;
    flex-wrap: wrap;
    padding-left: 10px;
    padding-right: 10px;
}
.tracker-select-dropdown {
    .ant-popover-inner-content {
        padding: 0px;
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

    .status-menu-items {
        .ant-menu-item {
            &:hover {
                background-color: #f8f8f8;
                color: #338fe5;
            }
        }
    }
}
quick-default /deep/ .ui-task-primary-fields .task-basic-quick-picker .basic-info-model{
    cursor: default;
}
</style>
