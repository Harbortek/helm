<template>
    <div v-if="isShowCalendar"  class="calendardate" style="position:relative;width:auto;overflow:auto hidden;height:82vh;">
        <div style="display: flex;justify-content: end;margin-right: 16px;">
            <span style="line-height: 32px;">Display by&nbsp;</span>
            <a-select default-value="createDate" @change="onChangeDisplayBy" style="width: 120px">
                <a-select-option value="createDate">
                    创建时间
                </a-select-option>
                <a-select-option value="lastModifiedDate">
                    修改时间
                </a-select-option>
                <a-select-option value="planEndDate">
                    计划结束时间
                </a-select-option>
                <a-select-option value="planStartDate">
                    计划开始时间
                </a-select-option>
            </a-select>
        </div>
        <a-spin :spinning="loading">
        <a-calendar v-model="displayDate" @panelChange="panelChange" :header-render="headerRender">
            <div slot="dateCellRender" slot-scope="value" class="events">
                <a-popover v-model="calendarsVisible[value.format('YYYY-MM-DD')]" :title="value.format('YYYY-MM-DD')" :overlayStyle="{background:'#666;',maxHeight:'600px',overflow:'auto'}">
                    <template slot="content">
                        <div v-for="item in getListData(value)" :key="item.id">
                            <div class="calendar-item" @click="onEditTrackerItem(item,value)">
                                <div class="calendar-item-content">
                                    <div class="calendar-item-title">
                                        <a-icon v-if="tracker.icon" :component="tracker.icon" /><span
                                            style="margin:auto 10px;">{{ item.name }}</span>

                                        <a-tag v-if="item.owner">{{ item.owner?.name }} </a-tag>
                                        <a-tag v-if="item.owner"
                                            :style="{ color: item.priority?.color, backgroundColor: item.priority?.backgroundColor }">{{
                                                item.priority?.name }}</a-tag>
                                        <a-tag>{{ currentProjectKeyName + '-' + item.itemNo }}</a-tag><br>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </template>
                    <div v-for="item in getListData(value)" :key="item.id">
                        <div class="calendar-item" @click="onEditTrackerItem(item,value)">
                            <div class="calendar-item-content">
                                <div class="calendar-item-title">
                                    <a-icon v-if="tracker.icon" :component="tracker.icon" /><span
                                        style="margin:auto 10px;">{{ item.name }}</span>
                                    <a-tooltip v-if="item.owner" :title="'负责人：' + item.owner?.name"
                                        :overlayStyle="{ fontSize: '10px' }">
                                        <a-tag>{{ item.owner?.name }} </a-tag>
                                    </a-tooltip>
                                    <a-tooltip v-if="item.priority?.name" :title="'优先级：' + item.priority?.name"
                                        :overlayStyle="{ fontSize: '10px' }">
                                        <a-tag
                                            :style="{ color: item.priority?.color, backgroundColor: item.priority?.backgroundColor }">{{
                                                item.priority?.name }}</a-tag>
                                    </a-tooltip>
                                    <a-tooltip v-if="item.itemNo" :title="'编号：' + currentProjectKeyName + '-' + item.itemNo"
                                        :overlayStyle="{ fontSize: '10px' }">
                                        <a-tag>{{ currentProjectKeyName + '-' + item.itemNo }}</a-tag><br>
                                    </a-tooltip>
                                </div>
                            </div>
                        </div>
                    </div>
                </a-popover>
            </div>
        </a-calendar>
        </a-spin>
    </div>
</template>

<script>
import Vue from "vue"
import { mapGetters } from "vuex";
import moment from 'moment';
import {
    findTrackerItems
} from "@/services/tracker/TrackerItemService";
export default {
    name: 'CalendarLayout',
    props: {
        tracker: Object,
        projectId: String,
        isShowCalendar: Boolean,
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
            displayBy: 'createDate',
            displayDate:null,
            itemData:[],
            calendarsVisible:{},
        }
    },
    mounted() {

    },
    watch: {
        isShowCalendar:{
            handler:function(newVal,oldVal){
                this.loadWorkItems(this.displayBy,moment().format("yyyy-MM"))
            }
        }

    },
    methods: {
        loadData(){
            this.loadWorkItems(this.displayBy,this.displayDate.format("yyyy-MM"))
        },
        loadWorkItems(displayBy,date) { 
            this.loading = true;
            let keyword = this.keyword
            let sort = [];
            let pageable = {
                page: 0,
                size: 10000,
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
            filter.calendarCondition = {"field":displayBy,"value":date}
            
            findTrackerItems(this.projectId, this.tracker.id, null, filter, keyword, pageable, sort).then(resp => {
                // this.statusData[statusId] = resp.content
                console.log(resp)
                this.itemData=resp.content
                
            }).finally(() => {
                this.loading = false;
            })
            
        },
        onChangeDisplayBy(e) {
            this.displayBy = e;
            this.loadData();
        },
        getListData(value) {
            return this.itemData.filter(item => { return value.format("YYYY-MM-DD") == moment(item[this.displayBy]).format("YYYY-MM-DD") })
        },
        onEditTrackerItem(row,value){
            this.calendarsVisible[value.format('YYYY-MM-DD')]=false;
            this.$emit("onEditTrackerItem",row)
        },
        refresh(){
            this.$emit('refresh');
        },
        panelChange(e){
            this.displayDate=e
            this.loadData();
        },
        //日历自定义头部
        headerRender ({ value, type, onChange, onTypeChange }) {
            const start = 0
            const end = 12
            const monthOptions = []

            const current = value.clone()
            const localeData = value.localeData()
            const months = []
            for (let i = 0; i < 12; i++) {
                current.month(i)
                months.push(localeData.monthsShort(current))
            }

            for (let index = start; index < end; index++) {
                monthOptions.push(
                    <a-select-option class="month-item" key={`${index}`}>
                        {months[index]}
                    </a-select-option>
                )
            }
            const month = value.month()

            const year = value.year()
            const options = []
            for (let i = year - 10; i < year + 10; i += 1) {
                options.push(
                <a-select-option key={i} value={i} class="year-item">
                    {i + '年'}
                </a-select-option>
                )
            }
            /* 上个月 */
            const prevMonth = () => {
                let newMonth = moment(value).subtract(1, 'months');
                onChange(newMonth);
            };
            /* 下个月 */
            const nextMonth = () => {
                let newMonth = moment(value).add(1, 'months');
                onChange(newMonth);
            };
            // 返回今天
            const showTotay = () => {
                const today = moment(new Date())
                onChange(today)
            }
            return (
                <div style={{ padding: ' 15px 15px 40px 15px', textAlign: 'center', position: 'relative'}}>
                <div style={{display: 'inline-block', textAlign: 'center'}}>
                    <a-select
                        dropdownMatchSelectWidth={false}
                        class="header-select"
                        onChange={newYear => {
                            const now = value.clone().year(newYear)
                            onChange(now)
                        }}
                        value={Number(year)}
                        >
                        {options}
                    </a-select>
                    <a-select
                    dropdownMatchSelectWidth={false}
                    class="header-select"
                    value={String(month)}
                    onChange={selectedMonth => {
                        const newValue = value.clone()
                        newValue.month(parseInt(selectedMonth, 10))
                        onChange(newValue)
                    }}
                    >
                    {monthOptions}
                    </a-select>
                </div>
                <div style={{position: 'absolute',right: '15px',top: '20px'}}>
                    <a-button-group>
                        <a-button onClick={() => prevMonth()}><a-icon type="left" />上一月</a-button>
                        <a-button onClick={() => showTotay()}>返回今日</a-button>
                        <a-button onClick={() => nextMonth()}>下一月<a-icon type="right" /></a-button>
                    </a-button-group>
                </div>
                </div>
            )
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
.calendar-item{
    width: 100%;
    margin-bottom:5px;
    .calendar-item-content{
        background: #fff;
        color: #303030;
        box-shadow: 0 1px 1px 0 rgba(31,31,31,.1);
        .calendar-item-title{
            margin-bottom:5px;
            padding:2px;
            margin-left:5px;
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
        }
    }
}
.calendar-item :hover{
    cursor: pointer;
    background-color: #f5f7fa;
}
</style>
