<template>
    <content-page>
        <vxe-toolbar size="medium">
            <template #buttons>
                <a-input-search v-model="keyword" placeholder="搜索基线" className="searchbox" allowClear style="width: 300px"
                    @search="onSearch()" />
            </template>
            <template #tools>
                <vxe-button v-action="'PAGE_WRITE|'+pageId" status="primary" content="新增基线" @click="onCreateBaseline"></vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-table :data="tableData" :loading="loading" :row-config="{ isHover: true }" height="300">
            <vxe-column type="seq" width="60"></vxe-column>
            <vxe-column field="name" title="基线名称">
            </vxe-column>
            <vxe-column field="type" title="基线类型">
                <template #default="{ row }">
                    <div v-if="row.type==='PROJECT'">项目基线</div>
                    <div v-else-if="row.type==='COLLECTION'">集合基线</div>
                    <div v-else-if="row.type==='DOCUMENT'">文档基线</div>
                </template>
            </vxe-column>
            <vxe-column field="createBy.name" title="创建者" width="100px">
                <template #default="{ row }">
                    <h-avatar :name="row.createBy.name" :icon="row.createBy.icon"></h-avatar>
                </template>
            </vxe-column>
            <vxe-column field="createDate" title="创建日期" width="200px"></vxe-column>
            <vxe-column title="操作" width="300" header-align="center" align="center" show-overflow>
                <template #default="{ row }">
                    <vxe-button v-action="'PAGE_WRITE|'+pageId" type="text" status="primary" content="删除" @click="onDeleteBaseline(row)"></vxe-button>
                </template>
            </vxe-column>
        </vxe-table>
        <baseline-compare ref="compare" :projectId="projectId" />

        <baseline-dialog :isShowDialog="showCreateDialog" :projectId="projectId" :baseline="currentBaseline" :baselines="tableData"
            @cancel="onCreateCancel" @ok="onCreateOK" />
    </content-page>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import { findBaselinesByProjectId, createBaseline, deleteBaseline } from '@/services/baseline/BaselineService'
import BaselineDialog from './BaselineDialog.vue'
import BaselineCompare from "./BaselineCompare.vue";
import ContentPage from '@/components/page/content/ContentPage.vue';
import VXETable from 'vxe-table';
export default {
    name: 'BaseLineList',
    components: { ContentPage, BaselineDialog, BaselineCompare, HAvatar },
    data() {
        return {
            loading: false,
            keyword: '',
            tableData: [],
            showCreateDialog: false,
            currentBaseline: undefined,
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },
    },
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            this.loading = true
            findBaselinesByProjectId(this.projectId, this.keyword).then(resp => {
                this.tableData = resp
                this.loading = false
            })
        },
        onSearch() {
            this.loadData()
        },
        onCreateBaseline() {
            this.currentBaseline = { name: '', description: '' }
            this.showCreateDialog = true
        },
        onDeleteBaseline(row) {
            VXETable.modal.confirm({
                title: '删除基线',
                content: '基线「' + row.name + '」的数据将被删除'
            }).then(type => {
                if (type === 'confirm') {
                    deleteBaseline(row.id).then(resp => {
                        VXETable.modal.message({ content: '基线「' + row.name + '」已被删除', status: 'info' })
                        this.loadData()
                    })
                }
            })
        },
        onCreateCancel() {
            this.showCreateDialog = false
            this.currentBaseline = undefined
        },
        onCreateOK(baseline) {
            baseline.projectId = this.projectId

            createBaseline(baseline).then(resp => {
                this.showCreateDialog = false
                this.currentBaseline = undefined
                this.loadData()
                this.$refs.compare.loadData();
            })
        },


    },
}
</script>

<style></style>