<template>
    <content-page style=" width: 100%; height: 100%; padding: 0px 10px 10px 10px; background-color: white;">
        <vxe-toolbar size="medium">
            <template #buttons>
                <a-input-search v-model="keyword" placeholder="搜索集合" className="searchbox" allowClear style="width: 300px"
                    @search="onSearch()" />
            </template>
            <template #tools>
                <vxe-button v-action="'PAGE_WRITE|'+pageId" status="primary" content="新增集合" @click="onCreateCollection"></vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-table  :data="tableData" :loading="loading" :row-config="{ isHover: true }" >
            <vxe-column type="seq" width="60"></vxe-column>
            <vxe-column field="name" title="集合名称"></vxe-column>
            <vxe-column field="createBy.name" title="创建者" width="100px">
                <template #default="{ row }">
                    <h-avatar :name="row.createBy.name" :icon="row.createBy.icon"></h-avatar>
                </template>
            </vxe-column>
            <vxe-column field="createDate" title="创建日期" width="200px"></vxe-column>
            <vxe-column title="操作" width="300" header-align="center" align="center" show-overflow>
                <template #default="{ row }">
                    <vxe-button v-action="'PAGE_WRITE|'+pageId" type="text" status="primary" content="操作" @click="onEditCollection(row)"></vxe-button>
                    <vxe-button v-action="'PAGE_WRITE|'+pageId" type="text" status="primary" content="删除" @click="onDeleteCollection(row)"></vxe-button>
                </template>
            </vxe-column>
        </vxe-table>
            <vxe-pager :loading="loading" :current-page="tablePage.currentPage" :page-size="tablePage.pageSize"
            :total="tablePage.totalResult" :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="onPageChange"></vxe-pager>

        <collection-dialog :isShowDialog="showCreateDialog" :editMode="editMode" :projectId="projectId" :collection="currentCollection" :collections="tableData"
            @cancel="onCreateCancel" @ok="onCreateOK" />
    </content-page>
</template>

<script>
import {cloneDeep} from 'lodash'
import HAvatar from '@/components/avatar/h-avatar.vue';
import { findCollectionsByProjectId, createCollection, deleteCollection,updateCollection } from '@/services/collection/CollectionService'
import CollectionDialog from './CollectionDialog.vue'
import ContentPage from '@/components/page/content/ContentPage.vue';
import VXETable from 'vxe-table';
export default {
    name: 'CollectionList',
    components: { ContentPage,  HAvatar, CollectionDialog },
    data() {
        return {
            loading: false,
            keyword: '',
            tableData: [],
            showCreateDialog: false,
            currentCollection: undefined,
            editMode: 'create',
            tablePage: {
                currentPage: 1,
                pageSize: 10,
                totalResult: 0
            },
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
            let pageable = {
                page: this.tablePage.currentPage-1,
                size: this.tablePage.pageSize,
            }
            findCollectionsByProjectId(this.projectId, this.keyword, pageable).then(resp => {
                this.tableData = resp.content
                this.tablePage.totalResult = parseInt(resp.totalElements)
                this.loading = false
            })
        },
        onSearch() {
            this.loadData()
        },
        onCreateCollection() {
            this.editMode = 'create'
            this.currentCollection = { name: '', description: '' }
            this.showCreateDialog = true
        },
        onEditCollection(row){
            this.editMode = 'edit'
            this.currentCollection = cloneDeep(row)
            this.showCreateDialog = true
        },
        onDeleteCollection(row) {
            VXETable.modal.confirm({
                title: '删除集合',
                content: '集合「' + row.name + '」的数据将被删除'
            }).then(type => {
                if (type === 'confirm') {
                    deleteCollection(row.id).then(resp => {
                        VXETable.modal.message({ content: '集合「' + row.name + '」已被删除', status: 'info' })
                        this.loadData()
                    })
                }
            })
        },
        onPageChange({ currentPage, pageSize }) {
            this.tablePage.currentPage = currentPage
            this.tablePage.pageSize = pageSize
            this.loadData();
        },
        onCreateCancel() {
            this.showCreateDialog = false
            this.currentCollection = undefined
        },
        onCreateOK(collection) {
            collection.projectId = this.projectId
            if(this.editMode === 'create'){
                createCollection(collection).then(resp => {
                    this.showCreateDialog = false
                    this.currentCollection = undefined
                    this.loadData()
                    VXETable.modal.message({ content: '新增成功', status: 'success' })
                })
            }else{
                updateCollection(collection).then(resp => {
                    this.showCreateDialog = false
                    this.currentCollection = undefined
                    this.loadData()
                    VXETable.modal.message({ content: '修改成功', status: 'success' })
                })
            }

        },


    },
}
</script>

<style></style>