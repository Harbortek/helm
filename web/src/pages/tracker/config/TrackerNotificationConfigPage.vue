<template>
    <config-page :routes="routes()" title="工作项通知设置" description="">
        <a-menu style="margin-top:10px;" v-model="current" :default-selected-keys="['Fields']" mode="horizontal"
            @click="onClickMenu">
            <a-menu-item key="Items">
                工作项操作与系统属性
            </a-menu-item>
            <a-menu-item key="Fields">
                自定义属性
            </a-menu-item>
        </a-menu>
        <config-page v-show="current[0] == 'Items'" style="padding:0;overflow: visible;"
            description="用于配置工作项操作以及系统属性变更通知方式和通知对象，使通知更精准。">
            <div style="border-bottom: 1px solid #dedede;">
                <vxe-input v-model="keyword" placeholder="搜索通知事件" style="width:240px" type="search"
                    clearable></vxe-input><br>
                <span
                    style="font-size:14px;line-height: 4;font-weight: 700;color: rgb(48,48,48);margin-right:70px;">全选</span>
                <a-checkbox v-model="selectAllData.useMessage" :indeterminate="selectAllData.MessageIndeterminate"
                    @change="onChangeSelectAll('useMessage')">通知中心</a-checkbox>
                <a-checkbox v-model="selectAllData.useEmail" :indeterminate="selectAllData.EmailIndeterminate"
                    @change="onChangeSelectAll('useEmail')">邮件</a-checkbox>
            </div>
            <a-list size="large" :loading="loading" item-layout="vertical" :data-source="getSystemNotifications">
                <a-list-item slot="renderItem" slot-scope="record">
                    <a-list-item-meta :description="$t('tracker.event.desc.' + record.eventName).format(tracker.name)">
                        <template #title>
                            <span class="list-title">
                                {{ $t('tracker.event.' + record.eventName).format(tracker.name) }}</span>
                        </template>
                    </a-list-item-meta>

                    <div style="">
                        <span
                            style="font-size:14px;line-height: 4;font-weight: 700;color: rgb(48,48,48);margin-right:40px;">通知方式</span>
                        <a-checkbox v-model="record.useMessage" @change="onChangeSelectNotice(record)">通知中心</a-checkbox>
                        <a-checkbox v-model="record.useEmail" @change="onChangeSelectNotice(record)">邮件</a-checkbox>
                    </div>
                    <role-members-table :projectId="projectId" :value="record.subscribers" scope="SCOPE_TRACKER"
                        title="以下成员域拥有此操作权限" tableHeight="auto" @delete="e => onDelete(e, record.eventName)"
                        @add="e => onAdd(e, record.eventName)" />
                </a-list-item>
            </a-list>
        </config-page>

        <config-page v-show="current[0] == 'Fields'" style="padding:0;overflow: visible;"
            description="用于配置自定义属性变更通知方式和通知对象，使通知更精准。">

            <div v-if="tracker.trackerNotification">
                <a-form-item required style="margin:0">
                    <span slot="label" style="font-size:16px">属性范围</span>
                    <a-radio-group name="radioGroup" v-model="tracker.trackerNotification.usedForAllCustomerFields"
                        @change="onChangeRadio" :default-value="true">
                        <a-radio :value="true">
                            全部自定义属性
                        </a-radio>
                        <a-radio :value="false">
                            指定自定义属性
                        </a-radio>
                    </a-radio-group>
                </a-form-item>


                <div v-if="tracker.trackerNotification.usedForAllCustomerFields">
                    <span
                        style="font-size:14px;line-height: 4;font-weight: 700;color: rgb(48,48,48);margin-right:40px;">通知方式</span>
                    <a-checkbox v-model="tracker.trackerNotification.defaultNotification.useMessage"
                        @change="onChangeSelectNotice()">通知中心</a-checkbox>
                    <a-checkbox v-model="tracker.trackerNotification.defaultNotification.useEmail"
                        @change="onChangeSelectNotice()">邮件</a-checkbox>
                    <role-members-table :projectId="projectId"
                        :value="tracker.trackerNotification.defaultNotification.subscribers" scope="SCOPE_TRACKER"
                        title="以下成员将接收到通知" tableHeight="auto" @delete="e => onDeleteAllCustomer(e)"
                        @add="e => onAddAllCustomer(e)" />
                </div>
                <div v-else>
                    <div style="border-bottom: 1px solid #dedede;">
                        <vxe-input v-model="keyword" placeholder="搜索通知事件" style="width:240px" type="search"
                            clearable></vxe-input>
                        <a-button type="primary" @click="e => visiable = true" style="float:right" size="small">
                            新建通知规则
                        </a-button>
                        <br>
                        <div v-if="getCustomerNotifications?.length>0">
                            <span
                                style="font-size:14px;line-height: 4;font-weight: 700;color: rgb(48,48,48);margin-right:70px;">全选</span>
                            <a-checkbox v-model="selectAllData.useMessage" :indeterminate="selectAllData.MessageIndeterminate"
                                @change="onChangeSelectAll('useMessage')">通知中心</a-checkbox>
                            <a-checkbox v-model="selectAllData.useEmail" :indeterminate="selectAllData.EmailIndeterminate"
                                @change="onChangeSelectAll('useEmail')">邮件</a-checkbox>
                        </div>
                        <div v-else style="height:20px;"></div>
                    </div>

                    <a-list size="large" :loading="loading" item-layout="vertical" :data-source="getCustomerNotifications">
                        <a-list-item slot="renderItem" slot-scope="record">
                            <a-list-item-meta>
                                <template #title>
                                    <span class="list-title">
                                        {{ record.trackerField.name }}</span>
                                </template>
                            </a-list-item-meta>

                            <div style="">
                                <span
                                    style="font-size:14px;line-height: 4;font-weight: 700;color: rgb(48,48,48);margin-right:40px;">通知方式</span>
                                <a-checkbox v-model="record.useMessage"
                                    @change="onChangeSelectNotice(record)">通知中心</a-checkbox>
                                <a-checkbox v-model="record.useEmail" @change="onChangeSelectNotice(record)">邮件</a-checkbox>
                                <vxe-button style="float:right" type="text" icon="vxe-icon-delete"
                                    @click="onDeleteItem(record)">删除</vxe-button>
                            </div>
                            <role-members-table :projectId="projectId" :value="record.subscribers" scope="SCOPE_TRACKER"
                                title="以下成员将接收到通知" tableHeight="auto"
                                @delete="e => onDeleteCustomer(e, record.trackerField.id)"
                                @add="e => onAddCustomer(e, record.trackerField.id)" />
                        </a-list-item>
                    </a-list>
                </div>

            </div>
            <a-modal v-if="visiable" v-model="visiable" title="新增通知规则" @ok="onOK" @cancel="onCancel">
                <a-form-model ref="fieldForm" class="field-form" layout="horizontal" :model="formData" :rules="rules">

                    <a-form-model-item ref="trackerField" label="选择属性" prop="trackerField">
                        <a-select placeholder="请选择属性" show-search option-filter-prop="children" style="width: 100%"
                            :filter-option="filterOption" @change="handleChangeSelect">
                            <a-select-option v-for="item in customerFileds" :key="item.id" :value="item.id">
                                {{ item.name }}
                            </a-select-option>
                        </a-select>
                    </a-form-model-item>
                </a-form-model>
                <a-form-model-item ref="name2" label="选择通知方式" prop="name2">
                    <a-checkbox v-model="formData.useMessage">通知中心</a-checkbox>
                    <a-checkbox v-model="formData.useEmail">邮件</a-checkbox>
                </a-form-model-item>
                <a-form-model-item ref="name3" label="选择成员域" prop="name3">
                    <a-select mode="multiple" style="width: 100%" v-model="selectItems" placeholder="请选择">
                        <a-select-opt-group v-if="projectRoles.length > 0">
                            <span slot="label">角色</span>
                            <a-select-option v-for="item in projectRoles" :key="item.id" :title="item.name"
                                :value="item.id">
                                {{ item.name }} <span class="domain-list-cell-subtext">{{ item.description }}</span>
                            </a-select-option>
                        </a-select-opt-group>
                        <a-select-opt-group v-if="specialRoles.length > 0">
                            <span slot="label">特殊角色</span>
                            <a-select-option v-for="item in specialRoles" :key="item.id" :title="item.name"
                                :value="item.id">
                                {{ item.name }}
                            </a-select-option>
                        </a-select-opt-group>
                        <a-select-opt-group v-if="members.length > 0">
                            <span slot="label">项目成员</span>
                            <a-select-option v-for="item in members" :key="item.id" :title="item.name" :value="item.id">
                                <h-avatar :name="item.name" :icon="item.icon"></h-avatar>
                                <span class="domain-list-cell-subtext"> {{ item.description }} </span>
                            </a-select-option>
                        </a-select-opt-group>
                    </a-select>
                </a-form-model-item>
            </a-modal>
        </config-page>

    </config-page>
</template>
<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
import VXETable from "vxe-table";
import {
    findOneTracker, subscribeSystemEvent, unSubscribeSystemEvent,findSystemEventDefault,
    subscribeCustomerEvent, unSubscribeCustomerEvent, createCustomerNotification,
    deleteCustomerNotification, subscribeAllCustomerEvent, unSubscribeAllCustomerEvent,
    updateSystemEventNoticeType, updateCustomerEventNoticeType, updateAllCustomerEventNoticeType
} from "@/services/tracker/TrackerService";
import {
    findProjectRolesAndMembers
} from "@/services/tracker/ProjectRoleMemberService";
import { sub } from 'date-fns';
export default {
    name: "TrackerNotificationConfigPage",
    components: { ConfigPage, RoleMembersTable, HAvatar},
    data() {
        return {
            tableHeight: 400,
            loading: false,
            keyword: '',
            tracker: {},
            rules: {
                "trackerField": { required: true, message: "请选择属性", trigger: "blur" },
            },
            current: ['Items'],
            trackerId: '',
            systemNotifications: [],
            visiable: false,
            formData: {
                trackerField: '',
                useMessage: true,
                useEmail: false,
                subscribers: []
            },
            selectAllData: {
                MessageIndeterminate: true,
                EmailIndeterminate: true,
                useMessage: false,
                useEmail: false,
            },
            data: {
                projectRoles: [],
                specialRoles: [],
                members: []
            },
            selectItems: undefined,
        };
    },
    computed: {
        customerFileds() {
            let customerFileds = []
            if (this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                this.tracker.trackerFields.forEach(f => {
                    if (f.system === false) {
                        let flag = true;
                        this.tracker.trackerNotification.customerTrackerNotifications.forEach((item) => {
                            if (item.trackerField.id == f.id) {
                                flag = false;
                            }
                        })
                        if (flag) {
                            customerFileds.push(f)
                        }

                    }
                })
            }
            return customerFileds
        },
        projectRoles: function () {
            return this.data.projectRoles;
        },
        specialRoles: function () {
            return this.data.specialRoles;
        },
        members: function () {
            return this.data.members;
        },
        getSystemNotifications() {
            if (this.systemNotifications && this.keyword) {
                var regexp = new RegExp(this.keyword)
                return this.systemNotifications.filter(v => { return regexp.test(this.$t('tracker.event.' + v.eventName)) });
            } else {
                return this.systemNotifications;
            }
        },
        getCustomerNotifications() {
            if (this.tracker.trackerNotification.customerTrackerNotifications && this.keyword) {
                var regexp = new RegExp(this.keyword)
                return this.tracker.trackerNotification.customerTrackerNotifications.filter(v => {
                    return regexp.test(this.$t('tracker.event.' + v.trackerField.name))
                });
            } else {
                return this.tracker.trackerNotification.customerTrackerNotifications;
            }
        }
    },
    created() {
    },
    mounted() {
        this.trackerId = this.$route.params.trackerId
        this.projectId = this.$route.params.projectId
        this.loadData()
        this.loadData2();

    },
    methods: {
        loadData2: function () {
            findProjectRolesAndMembers(this.projectId, "SCOPE_TRACKER").then(resp => {
                this.data = resp;
            });
        },
        onClickMenu(row) {
            this.loadSelectAll(row.key)
        },
        onChangeRadio() {
            if (this.tracker.trackerNotification.usedForAllCustomerFields) {//全部自定义属性
                updateAllCustomerEventNoticeType(this.trackerId, { useEmail: null, useMessage: null }).then((resp) => {
                    console.log("resp", resp)
                })
            } else {
                updateCustomerEventNoticeType(this.trackerId, null, { useEmail: null, useMessage: null }).then((resp) => {
                    console.log("resp", resp)
                })
            }
        },
        onChangeSelectNotice(record) {
            if (this.current[0] == "Items") {
                updateSystemEventNoticeType(this.trackerId, record.eventName, record).then((resp) => {
                    this.loadData();
                })
            } else {
                if (record == null) {
                    updateAllCustomerEventNoticeType(this.trackerId, this.tracker.trackerNotification.defaultNotification).then((resp) => {
                        console.log("resp", resp)
                        this.loadData();
                    })
                } else {
                    updateCustomerEventNoticeType(this.trackerId, record.trackerField.id, record).then((resp) => {
                        console.log("resp", resp)
                        this.loadData();
                    })
                }

            }
        },
        onChangeSelectAll(key) {
            if (this.current[0] == "Items") {
                if (key == "useMessage") {
                    updateSystemEventNoticeType(this.trackerId, "", { useEmail: null, useMessage: this.selectAllData.useMessage })
                        .then((resp) => {
                            console.log("resp", resp)
                            this.loadData();
                        })
                } else {
                    updateSystemEventNoticeType(this.trackerId, "", { useEmail: this.selectAllData.useEmail, useMessage: null })
                        .then((resp) => {
                            console.log("resp", resp)
                            this.loadData();
                        })
                }
            } else {
                if (key == "useMessage") {
                    updateCustomerEventNoticeType(this.trackerId, null, { useEmail: null, useMessage: this.selectAllData.useMessage })
                        .then((resp) => {
                            console.log("resp", resp)
                            this.loadData();
                        })
                } else {
                    updateCustomerEventNoticeType(this.trackerId, null, { useEmail: this.selectAllData.useEmail, useMessage: null })
                        .then((resp) => {
                            console.log("resp", resp)
                            this.loadData();
                        })
                }
            }
        },
        onDeleteItem(row) {
            VXETable.modal.confirm({
                title: '删除通知规则',
                content: '删除自定义属性「' + row.trackerField.name + '」的通知规则，该属性变更时将不再发送通知。此操作无法恢复，确认删除吗？'
            }).then(type => {
                if (type === 'confirm') {
                    deleteCustomerNotification(this.trackerId, row).then(resp => {
                        VXETable.modal.message({ content: '「' + row.trackerField.name + '」通知规则删除成功', status: 'info' })
                        this.loadData()
                    })
                }
            })
        },
        onOK() {
            this.$refs["fieldForm"].validate((valid) => {
                if (valid) {
                    const all = [].concat(this.data.projectRoles).concat(this.data.specialRoles).concat(this.data.members)
                    if (this.selectItems) {
                        for (let i = 0; i < all.length; i++) {
                            for (let selectRole of this.selectItems) {
                                if (all[i].id === selectRole) {
                                    this.formData.subscribers.push(all[i])
                                }
                            }
                        }
                    }
                    this.selectItems = undefined
                    createCustomerNotification(this.trackerId, this.formData).then(resp => {
                        this.loadData()
                        VXETable.modal.message({ content: '新建成功', status: 'success' })
                    })
                    this.visiable = false;
                }
            })

        },
        onCancel() {

        },
        handleChangeSelect(v) {
            if (this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                for (let f of this.tracker.trackerFields) {
                    if (f.system === false && f.id == v) {
                        this.formData.trackerField = { id: f.id, name: f.name }
                        break;
                    }
                }
            }
        },
        loadData() {
            this.loading = true;
            findSystemEventDefault().then(systemEvents=>{
                findOneTracker(this.trackerId).then(resp => {
                    this.tracker = resp;
                    this.systemNotifications=[];
                    for(let systemEvent of systemEvents){
                        let flag=true;
                        for(let notification of this.tracker.trackerNotification.systemTrackerNotifications){
                            if(systemEvent==notification.eventName){
                                this.systemNotifications.push(notification)
                                flag=false;
                                break;
                            }
                        }
                        if(flag){
                            this.systemNotifications.push(
                                {eventName:systemEvent,subscribers:[],useEmail:false,useMessage:false}
                            )  
                        }
                    }



                    // this.systemNotifications = this.tracker.trackerNotification.systemTrackerNotifications;
                    // console.log("systemNotifications",this.systemNotifications)
                    this.loading = false;
                    this.loadSelectAll(this.current[0])
                })
            })
            

        },
        onDeleteAllCustomer(row) {
            VXETable.modal.confirm({
                title: '取消通知订阅',
                content: '「' + row.name + '」的订阅将被取消'
            }).then(type => {
                if (type === 'confirm') {
                    unSubscribeAllCustomerEvent(this.trackerId, row).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」订阅已被取消', status: 'info' })
                        this.loadData()
                    })
                }
            })
        },
        onAddAllCustomer(row) {
            subscribeAllCustomerEvent(this.trackerId, row).then(resp => {
                this.loadData()
            })
        },
        onDeleteCustomer(row, fieldId) {
            VXETable.modal.confirm({
                title: '取消通知订阅',
                content: '「' + row.name + '」的订阅将被取消'
            }).then(type => {
                if (type === 'confirm') {
                    unSubscribeCustomerEvent(this.trackerId, fieldId, row).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」订阅已被取消', status: 'info' })
                        this.loadData()
                    })
                }
            })
        },
        onAddCustomer(row, fieldId) {
            subscribeCustomerEvent(this.trackerId, fieldId, row).then(resp => {
                this.loadData()
            })
        },
        onDelete(row, eventName) {
            VXETable.modal.confirm({
                title: '取消通知订阅',
                content: '「' + row.name + '」的订阅将被取消'
            }).then(type => {
                if (type === 'confirm') {
                    unSubscribeSystemEvent(this.trackerId, eventName, row).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」订阅已被取消', status: 'info' })
                        this.loadData()
                    })
                }
            })
        },
        onAdd(row, eventName) {
            subscribeSystemEvent(this.trackerId, eventName, row).then(resp => {
                this.loadData()
            })
        },
        routes: function () {
            return [{
                name: "trackerListConfig", meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerListConfig",
                    show: false,
                },
            }, {
                name: "trackerConfigPortal",
                meta: {
                    icon: "blank",
                    title: this.tracker.name,
                    show: false,
                },
            }, {
                name: 'trackerNotificationConfig', meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerNotificationConfig",
                    show: false,
                },
            }]
        },
        loadSelectAll(key) {
            this.selectAllData = {
                MessageIndeterminate: true,
                EmailIndeterminate: true,
                useMessage: false,
                useEmail: false,
            }
            let emailCount = 0;
            let messageCount = 0;
            if (key == 'Items') {
                this.systemNotifications.forEach(item => {
                    if (item.useEmail) {
                        emailCount++;
                    }
                    if (item.useMessage) {
                        messageCount++;
                    }
                })
                if (messageCount == 0) {
                    this.selectAllData.MessageIndeterminate = false;
                } else if (messageCount == this.systemNotifications.length) {
                    this.selectAllData.MessageIndeterminate = false;
                    this.selectAllData.useMessage = true;
                }
                if (emailCount == 0) {
                    this.selectAllData.EmailIndeterminate = false;
                } else if (emailCount == this.systemNotifications.length) {
                    this.selectAllData.EmailIndeterminate = false;
                    this.selectAllData.useEmail = true;
                }
            } else {
                this.tracker.trackerNotification.customerTrackerNotifications.forEach(item => {
                    if (item.useEmail) {
                        emailCount++;
                    }
                    if (item.useMessage) {
                        messageCount++;
                    }
                })
                if (messageCount == 0) {
                    this.selectAllData.MessageIndeterminate = false;
                } else if (messageCount == this.tracker.trackerNotification.customerTrackerNotifications.length) {
                    this.selectAllData.MessageIndeterminate = false;
                    this.selectAllData.useMessage = true;
                }
                if (emailCount == 0) {
                    this.selectAllData.EmailIndeterminate = false;
                } else if (emailCount == this.tracker.trackerNotification.customerTrackerNotifications.length) {
                    this.selectAllData.EmailIndeterminate = false;
                    this.selectAllData.useEmail = true;
                }
            }
        },
        filterOption(input, option) {
            return (
                option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
            );
        },
    },
};
</script>
<style lang="less" scoped>
.mytable-style {
    font-size: 16px;
}

.list-title {
    color: rgba(93, 99, 105);
    font-size: 18px;
    font-weight: 500;
    line-height: 1.5;
}

.ant-menu-horizontal>.ant-menu-item-selected {
    color: #338fe5;
    border-bottom: 3px solid #338fe5 !important;
}

.ant-menu-horizontal>.ant-menu-item:hover {
    color: #338fe5;
    border-bottom: 3px solid #338fe5 !important;
}

.ant-menu-horizontal>.ant-menu-item {
    border-top: 0px !important;
    font-size: 16px;
}
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
</style>
