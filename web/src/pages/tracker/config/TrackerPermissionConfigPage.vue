<template>
    <config-page :routes="routes()" title="" description="">
        <a-menu style="margin-top:10px;" v-model="current" :default-selected-keys="['Fields']" mode="horizontal">
            <a-menu-item key="Items">
                工作项权限
            </a-menu-item>
            <a-menu-item key="Fields">
                属性修改权限
            </a-menu-item>
        </a-menu>
        <config-page v-show="current[0] == 'Items'" style="padding:0;"
            description="工作项权限可以定制对工作项整体的操作权限。包含新建、查看、编辑、删除工作项权限，成为负责人权限，更新状态，管理关注者，导出工作项以及管理工时权限。">
            <a-list size="large" :loading="loading" item-layout="vertical" :data-source="defaultTrackerPermissoins" style="height: 78vh;">
                <a-list-item slot="renderItem" slot-scope="record">
                    <a-list-item-meta
                        :description="$t('tracker.permission.desc.' + record).format(tracker.name)"> 
                        <template #title>
                            <span class="list-title">
                                {{ $t('tracker.permission.' + record).format(tracker.name) }}</span>
                        </template>
                    </a-list-item-meta>
                    <role-members-table :projectId="projectId" :value="getTrackerPermission(record)" :scope="getRoleMembersScope(record)"
                        title="以下成员域拥有此操作权限" tableHeight="auto" @delete="e => onDelete(e, record)"
                        @add="e => onAdd(e, record)" />
                </a-list-item>
            </a-list>
        </config-page>
        <tracker-fields-permission v-show="current[0] == 'Fields'"></tracker-fields-permission>

    </config-page>
</template>
<script>
import {  mapMutations } from 'vuex'
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import TrackerFieldsPermission from './TrackerFieldsPermission.vue';
import {
    findOneTracker, findTrackerPermissionsDefault, grantTrackerPermission, unGrantTrackerPermission
} from "@/services/tracker/TrackerService";
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
import VXETable from "vxe-table";

export default {
    name: "TrackerPermissionConfigPage",
    components: { ConfigPage, RoleMembersTable, TrackerFieldsPermission },
    data() {
        return {
            tableHeight: 400,
            loading: false,
            keyword: '',
            tracker: {},
            current: ['Items'],
            proectId: '',
            trackerId: '',
            grantData: [],
            defaultTrackerPermissoins:[],
        };
    },
    computed: {
    },
    created() {
    },
    mounted() {
        this.trackerId = this.$route.params.trackerId
        this.projectId = this.$route.params.projectId
        this.loadData()

    },
    methods: {
        loadData() {
            console.log("tracker load")
            this.loading = true;
            findOneTracker(this.trackerId).then(resp => {
                this.tracker = resp;
                this.grantData = this.tracker.trackerPermissions;
            }).finally(()=>{
                findTrackerPermissionsDefault().then(res=>{
                    this.defaultTrackerPermissoins=res;
                    this.loading = false;
                })
            })
        },
        getRoleMembersScope(permissionName){
            if(permissionName=='ITEM_CREATE'||permissionName=='ITEM_EXPORT'){
                return 'SCOPE_PROJECT'
            }else{
                return 'SCOPE_TRACKER'
            }
        },
        getTrackerPermission(permissionName){
            return this.grantData.find(item=>item.permissionName==permissionName)?.granted;
        },
        onDelete(row, permission) {
            VXETable.modal.confirm({
                title: '取消授权',
                content: '「' + row.name + '」的授权将被取消'
            }).then(type => {
                if (type === 'confirm') {
                    unGrantTrackerPermission(this.trackerId, permission, row).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」授权已被取消', status: 'info' })
                    }).finally(()=>{
                        this.$store.dispatch('account/getInfo')
                        this.loadData()
                    })
                }
            })
        },
        onAdd(row, permission) {
            grantTrackerPermission(this.trackerId, permission, row).then(resp => {
            }).finally(()=>{
                this.$store.dispatch('account/getInfo')
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
                name: 'trackerStateTransitionConfig', meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerPermissionConfig",
                    show: false,
                },
            }]
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

.table-view {
    .first-col {
        position: relative;
        height: 20px;
    }

    .first-col:before {
        content: "";
        position: absolute;
        left: -14px;
        top: 10px;
        width: 204px;
        height: 1px;
        transform: rotate(13deg);
        background-color: #e8eaec;
    }

    .first-col .first-col-top {
        position: absolute;
        right: 4px;
        top: -10px;
    }

    .first-col .first-col-bottom {
        position: absolute;
        left: 4px;
        bottom: -10px;
    }
}
</style>
