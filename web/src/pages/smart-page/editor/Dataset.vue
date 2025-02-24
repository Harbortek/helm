<template>
    <div class="dataset-main">

        <div class="title-bar"><span class="title-bar-title">数据集</span>
            <vxe-button type="text" icon="vxe-icon-add" @click="onCreateDataset"></vxe-button>
        </div>
        <vxe-toolbar ref="xToolbar" style="margin: 0 10px;">
            <template #buttons>
                <a-input-search v-model="keyword" placeholder="搜索数据集" type="search" className="searchbox" clearable
                    @search="onSearch()"></a-input-search>
            </template>
        </vxe-toolbar>
        <a-menu mode="vertical" style="border: 0;" :defaultSelectedKeys="selectedKeys">
            <a-menu-item @click="onDatasetChange(item)" v-for="item in filterDatasets" :key="item.id">
                <div style="display:flex;flex-direction: row;">
                    <div style="flex: 1 1 auto;">
                        <a-icon type="table" />
                        {{ item.name }}
                    </div>
                    <div style="">
                        <a-dropdown>
                            <a-icon style="position: absolute;top: 30%;right: 0px;width: 10px;" type="more" />
                            <a-menu slot="overlay">
                                <a-menu-item @click="onEditDataset(item)">
                                    <a-icon type="edit" />{{ $t('edit') }}
                                </a-menu-item>
                                <a-menu-item @click="onDeleteDataset(item)">
                                    <a-icon type="delete" />{{ $t('delete') }}
                                </a-menu-item>
                            </a-menu>
                        </a-dropdown>
                    </div>
                </div>


            </a-menu-item>
        </a-menu>

        <dataset-dialog :isShowDialog="isShowCreateDiaog" :dataset="currentDataset" :datasets="datasets" :pageId="pageId"
            :editMode="editMode" @cancel="isShowCreateDiaog = false"
            @ok="onCreateDatasetOK" />
    </div>
</template>

<script>
import DatasetDialog from './DatasetDialog.vue';
import { findDatasetsByPageId, createDataset, updateDataset, deleteDataset, getSQLPreview } from '@/services/smart-page/DatasetService.js'
import VxeTable from 'vxe-table'
export default {
    name: 'Dataset',
    components: { DatasetDialog },
    props: {
        pageId: {
            required: true
        },
    },
    data() {
        return {

            isShowCreateDiaog: false,
            editMode: 'create',
            currentDataset: { data: [], fields: [] },
            keyword: '',
            datasets: [],
            filterDatasets: [],
        }
    },
    computed: {
        selectedKeys() {
            if (this.currentDataset && this.currentDataset.id) {
                return [this.currentDataset.id]
            }
            return []
        }
    },
    mounted() {
        this.loadData()
    },
    methods: {
        loadData: function () {
            findDatasetsByPageId(this.pageId).then(resp => {
                this.datasets = resp || []
                this.filterDatasets = this.datasets
            })
        },
        formatType(type) {
            if (type === 'TEXT') {
                return '文本'
            } else if (type === 'NUM') {
                return '数字'
            } else if (type === 'DATE') {
                return '日期'
            }
        },
        onSearch: function () {
            const keyword = this.keyword
            if (keyword === '') {
                this.filterDatasets = this.datasets
            } else {
                this.filterDatasets = this.datasets.filter(item => {
                    return item.name.indexOf(keyword) >= 0
                })
            }

        },
        onTabChange: function (activeKey) {
            this.currentTab = activeKey
            console.log(this.currentTab)
        },
        onCreateDataset: function () {
            this.editMode = 'create'
            this.currentDataset = { name: '', sql: '', data: [], fields: [] }
            this.isShowCreateDiaog = true
        },
        onCreateDatasetOK(dataset) {
            if (this.editMode === 'create') {
                let request = {
                    dataset: dataset,
                    dataRequest: { }
                }
                createDataset(this.pageId, request).then(resp => {
                    this.isShowCreateDiaog = false
                    this.currentDataset = dataset
                    this.loadData()
                })
            } else if (this.editMode === 'edit') {
                let request = {
                    dataset: dataset,
                    dataRequest: {  }
                }
                updateDataset(this.pageId, request).then(resp => {
                    this.isShowCreateDiaog = false
                    this.currentDataset = dataset
                    this.loadData()
                })
            }

        },
        onEditDataset(item) {
            this.currentDataset = item
            this.editMode = 'edit'
            this.isShowCreateDiaog = true
        },
        onSaveDataset() {
            updateDataset(this.pageId, this.currentDataset).then(resp => {
                this.$message.success('数据集保存成功')
            })
        },
        async onDeleteDataset(dataset) {
            const type = await VxeTable.modal.confirm('您确定要删除该数据?')
            if (type === 'confirm') {
                deleteDataset(this.pageId, dataset.id).then(resp => {
                    this.currentDataset = { data: [], fields: [] }
                    this.loadData()
                })

            }
        },
        onDatasetChange(dataset) {
            this.currentDataset = dataset
        }
    },
}
</script>

<style lang="less" scoped>
.dataset-main {
    flex: 1 1 auto;
    margin-top: 0;

    .title-bar {
        display: flex;
        align-items: center;
        flex: 1;
        height: 48px;
        padding: 0 15px;

        .title-bar-title {
            flex: 1;
            font-size: 16px;
            font-weight: 500;
            margin-right: auto;
            color: #303030;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
    }
}
</style>