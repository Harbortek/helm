<template>
    <config-page title="" description="产品线权限可以用于当前产品线的操作权限控制 ">
        <a-list style="margin-top:30px;" size="large" :loading="loading" item-layout="vertical"
            :data-source="grantData">
            <a-list-item slot="renderItem" slot-scope="record">
                <a-list-item-meta :description="$t('productLine.permission.desc.' + record.permissionName)">
                    <template #title>
                        <span class="list-title">
                            {{ $t('productLine.permission.' + record.permissionName) }}</span>
                    </template>
                </a-list-item-meta>

                <role-members-table :productLineId="productLineId" :value="record.granted" scope="SCOPE_PRODUCT_LINE"
                    title="以下成员域拥有此操作权限" tableHeight="auto" @delete="e => onDelete(e, record.permissionName)"
                    @add="e => onAdd(e, record.permissionName)" />
            </a-list-item>
        </a-list>
        <div style="height:50px"></div>
    </config-page>
</template>

<script>
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import { findPermissionGrants, grantPermission, unGrantPermission } from "@/services/product-line/ProductLinePermissionService"
import RoleMembersTable from './RoleMembersTable.vue';
import VXETable from "vxe-table";
export default {
    name: "ProductPermissionConfigMainPage",
    components: { ConfigPage, RoleMembersTable },
    data() {
        return {
            tableHeight: 400,
            loading: false,
            grantData: []
        };
    },
    computed: {
        productLineId() {
            return this.$route.params.productLineId
        }
    },
    created() {
    },
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            this.loading = true;
            findPermissionGrants(this.productLineId).then(resp => {
                console.log("resp1", resp)
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
                    unGrantPermission(this.productLineId, permission, row).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」授权已被取消', status: 'info' })
                        this.loadData()
                    })
                }
            })
        },
        onAdd(row, permission) {
            grantPermission(this.productLineId, permission, row).then(resp => {
                this.loadData()
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