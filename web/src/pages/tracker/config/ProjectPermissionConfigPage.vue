<template>
    <config-page title="" description="项目权限可以用于定制项目的操作权限控制 ">
        <a-list style="margin-top:30px;" size="large" :loading="loading" item-layout="vertical" :data-source="grantData">
            <a-list-item slot="renderItem" slot-scope="record">
                <a-list-item-meta :description="$t('project.permission.desc.' + record.permissionName)">
                    <template #title>
                        <span class="list-title">
                            {{ $t('project.permission.' + record.permissionName) }}</span>
                    </template>
                </a-list-item-meta>

                <role-members-table :projectId="projectId" :value="record.granted" scope="SCOPE_PROJECT"
                    title="以下成员域拥有此操作权限" tableHeight="auto" @delete="e => onDelete(e, record.permissionName)"
                    @add="e => onAdd(e, record.permissionName)" />
            </a-list-item>
        </a-list>
        <div style="height:50px"></div>
    </config-page>
</template>
<script>
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import { findPermissionGrants, grantPermission, unGrantPermission } from "@/services/tracker/ProjectPermissionService"
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
import VXETable from "vxe-table";
import { hasPermission } from "@/utils/permission";
export default {
    name: "ProjectPermissionConfigMainPage",
    components: { ConfigPage, RoleMembersTable },
    data() {
        return {
            tableHeight: 400,
            loading: false,
            projectId: '',
            grantData: []
        };
    },
    computed: {

    },
    created() {
    },
    mounted() {
        this.projectId = this.$route.params.projectId
        if (!hasPermission("PROJECT_VIEW",this.projectId)||!hasPermission("PROJECT_ADMIN",this.projectId)) {
            this.$router.push({ name: "projectList" })
        }
        this.loadData()
    },
    methods: {
        loadData() {
            this.loading = true;
            findPermissionGrants(this.projectId).then(resp => {
                console.log("resp1",resp)
                this.grantData = resp;
                this.loading = false;
            })
        },
        onDelete(row, permission) {
            VXETable.modal.confirm({
                title: '取消授权',
                content: '「' + row.name + '」的授权将被取消'
            }).then(type => {
                if (type === 'confirm') {
                    unGrantPermission(this.projectId, permission, row).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」授权已被取消', status: 'info' })
                        this.loadData()
                    }).finally(()=>{
                        this.$store.dispatch('account/getInfo');
                        // location.reload();
                    })
                }
            })
        },
        onAdd(row, permission) {
            grantPermission(this.projectId, permission, row).then(resp => {
                this.loadData()
            }).finally(()=>{
                this.$store.dispatch('account/getInfo');
                // location.reload();
            })
        },

    }
};
</script>
<style scoped>
.mytable-style {
    font-size: 16px;
}

.list-title {
    color: rgb(48, 48, 48);
    font-size: 20px;
    font-weight: 500;
    line-height: 1.5;
}
</style>