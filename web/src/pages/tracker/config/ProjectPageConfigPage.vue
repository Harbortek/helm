<template>
    <config-page title="" description="项目页面可用于自定义本项目的文档和报表,可通过拖拽来调整目录和文档的次序">
        <vxe-toolbar ref="xToolbar" size="medium">
            <template #buttons>
                <a-space>
                    <a-button @click="onClapseAll"><h-icon type="collapse_all" />收缩</a-button>
                    <a-button @click="onExpandAll"><h-icon type="expand_all" />展开</a-button>
                </a-space>
            </template>
            <template #tools>
                <a-space>
                    <vxe-button type="primary" @click="onCreateTopFolder" icon="vxe-icon-add">新增目录</vxe-button>
                    <vxe-button type="primary" @click="onCreateTopPage" icon="vxe-icon-add">新增一级页面</vxe-button>
                </a-space>
            </template>
        </vxe-toolbar>
        <vxe-table size="medium" ref="pagesTable" show-overflow border row-key :row-config="{ isHover: true }"
            :show-header="true" auto-resize :data="tableData" :checkbox-config="{ labelField: 'name' }"
            :tree-config="{ transform: true, accordion: false, line: true, rowField: 'id', parentField: 'parentId', iconOpen: 'vxe-icon-square-minus', iconClose: 'vxe-icon-square-plus' }"
            :row-class-name="rowClassName">
            <vxe-column field="name" title="名称" tree-node>
                <template #default="{ row }">
                    <div style="display: inline-flex;">
                        <div style="width:20px;">
                            <h-icon :type="row.icon" v-if="row.icon" />
                            <h-icon type="document" v-else-if="!row.folder && row.type === 'wiki'" />
                            <h-icon type="bar" v-else-if="!row.folder && row.type === 'report'" />
                            <h-icon type="custom-component" v-else-if="!row.folder && row.type === 'component'" />
                            <h-icon type="tracker" v-else-if="!row.folder && row.type === 'tracker'" />
                            <h-icon type="url" v-else-if="!row.folder && row.type === 'url'" />
                        </div> {{ row.name }}
                    </div>
                </template>
            </vxe-column>
            <vxe-column field="type" title="类型" header-align="center" width="100">
                <template #default="{ row }">
                    <span v-if="row.type && !row.folder">{{ $t('page.type.' + row.type) }}</span>
                </template>
            </vxe-column>
            <!-- <vxe-column field="createBy.name" title="创建者" width="100px">
                <template #default="{ row }">
                    <a-avatar v-if="row.createBy.icon" class="avatar" size="small" :src="iconUrl(row.createBy.icon)" />
                    <a-avatar v-else class="avatar" size="small" style="backgroundColor:rgb(44,178,174)">{{
                        row.createBy.name }}</a-avatar>
                    {{ row.createBy.name }}
                </template>
            </vxe-column>
            <vxe-column field="createDate" title="创建日期" width="200px"></vxe-column> -->

            <vxe-column title="操作" header-align="center" width="200">
                <template #default="{ row }">
                    <a-space>
                        <a-tooltip title="新增下级文档">
                            <vxe-button :disabled="!row.folder" type="text" @click="onCreatePage(row)"
                                icon="vxe-icon-add"></vxe-button>
                        </a-tooltip>
                        <a-divider />
                        <a-tooltip title="编辑">
                            <vxe-button type="text" @click="onEditPage(row)" icon="vxe-icon-edit"></vxe-button>
                        </a-tooltip>
                        <a-divider />
                        <a-tooltip title="删除">
                            <vxe-button type="text" @click="onDeletePage(row)" icon="vxe-icon-delete"></vxe-button>
                        </a-tooltip>

                    </a-space>

                </template>
            </vxe-column>
        </vxe-table>
        <project-page-dialog :isShowDialog="showDialog" :projectId="projectId" :editMode="editMode"
            :current-page="currentPage" :pageList="tableData" @cancel="showDialog = false" @ok="onEditOK" />
    </config-page>
</template>
<script>
import { iconUrl } from "@/utils/util"
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import VXETable from "vxe-table";
import { findByProjectId, createProjectPage, updateProjectPageInfo, deleteProjectPage, changeProjectPageOrder } from '@/services/tracker/ProjectPageService'
import ProjectPageDialog from './ProjectPageDialog.vue';
import Sortable from "sortablejs"
import XEUtils from "xe-utils";
export default {
    name: "ProjectPageConfigPage",
    components: { ConfigPage, ProjectPageDialog },
    data() {
        return {
            loading: false,
            tableData: [],
            editMode: 'create',
            showDialog: false,
            currentPage: undefined,
        };
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        }
    },
    created() {
    },
    mounted() {
        this.loadData()
    },
    beforeDestroy() {
        if (this.sortable) {
            this.sortable.destroy();
        }
    },
    methods: {
        iconUrl(v) {
            return iconUrl(v)
        },
        loadData() {
            this.loading = true
            findByProjectId(this.projectId).then(resp => {
                this.tableData = resp
                console.log(this.tableData)
                this.loading = false
                if (!this.sortable) {
                    this.rowDrop()
                }
                this.$nextTick(() => {
                    this.$refs.pagesTable.setAllTreeExpand(true)
                })
            })
        },
        rowClassName({ row, rowIndex, $rowIndex }) {
            let xTable = this.$refs.pagesTable;
            if (xTable.isTreeExpandByRow(row)) {
                return 'no-drag'
            }
        },
        rowDrop() {
            let that = this
            this.$nextTick(() => {
                let xTable = that.$refs.pagesTable;
                if (xTable) {
                    that.sortable = Sortable.create(
                        xTable.$el.querySelector(".body--wrapper>.vxe-table--body tbody"),
                        {
                            handle: ".vxe-body--row",
                            filter: '.no-drag',
                            animation: 150,
                            onEnd: ({ item, oldIndex }) => {
                                const options = { children: 'children' }
                                const targetTrElem = item
                                const wrapperElem = targetTrElem.parentNode
                                const prevTrElem = targetTrElem.previousElementSibling
                                const tableData = that.tableData
                                const selfRow = xTable.getRowNode(targetTrElem).item
                                const selfNode = XEUtils.findTree(tableData, row => row === selfRow, options)
                                if (prevTrElem) {
                                    // 移动到节点
                                    const prevRow = xTable.getRowNode(prevTrElem).item
                                    const prevNode = XEUtils.findTree(tableData, row => row === prevRow, options)
                                    if (XEUtils.findTree(selfRow[options.children], row => prevRow === row, options)) {
                                        // 错误的移动
                                        const oldTrElem = wrapperElem.children[oldIndex]
                                        wrapperElem.insertBefore(targetTrElem, oldTrElem)
                                        return this.$XModal.message({ content: '不允许自己给自己拖动！', status: 'error' })
                                    }
                                    const currRow = selfNode.items[selfNode.index]

                                    if (currRow.level == 2) {
                                        const currnetRowIndex = XEUtils.findIndexOf(tableData, row => {
                                            return row.id === currRow.id
                                        })
                                        tableData.splice(currnetRowIndex, 1)
                                        const parentIndex = XEUtils.findIndexOf(tableData, row => {
                                            return row.id === prevRow.parentId
                                        })
                                        const prevIndex = XEUtils.findIndexOf(tableData, row => {
                                            return row.id === prevRow.id
                                        })
                                        if (prevRow.level == 2) {
                                            //移动到某个父节点下
                                            currRow.parentId = prevRow.parentId
                                            tableData.splice(prevIndex + 1, 0, currRow)
                                        } else if (prevRow.level == 1) {
                                            //移动到顶层节点后
                                            if (xTable.isTreeExpandByRow(prevRow)) {
                                                // 移动到prevRow下作为子节点
                                                currRow.parentId = prevRow.id
                                                tableData.splice(prevIndex + 1, 0, currRow)
                                            } else if (currRow.level == 2) {
                                                // prevRow是个空的父节点
                                                currRow.parentId = prevRow.id
                                                tableData.splice(prevIndex + 1, 0, currRow)
                                                xTable.setTreeExpand([currRow], true)
                                            }
                                            else {
                                                // 移动到prevRow后作为平行节点
                                                currRow.parentId = null
                                                tableData.splice(prevIndex + 1, 0, currRow)
                                            }
                                        }
                                    } else {
                                        const prevParentId = prevRow.level == 1 ? prevRow.id : prevRow.parentId
                                        const startIndex = XEUtils.findIndexOf(tableData, row => { return row.id === currRow.id || row.parentId == currRow.id })
                                        const endIndex = XEUtils.findLastIndexOf(tableData, row => { return row.id === currRow.id || row.parentId == currRow.id })
                                        const toInsert = tableData.splice(startIndex, endIndex - startIndex + 1)

                                        const endIndexToInsert = XEUtils.findLastIndexOf(tableData, row => { return row.id === prevParentId || row.parentId == prevParentId })

                                        for (let i = 0; i < toInsert.length; i++) {
                                            tableData.splice(endIndexToInsert + 1 + i, 0, toInsert[i])
                                        }

                                    }

                                } else {
                                    // 移动到第一行

                                    const currRow = selfNode.items[selfNode.index]
                                    if (currRow.level == 1) {
                                        const startIndex = XEUtils.findIndexOf(tableData, row => { return row.id === currRow.id || row.parentId == currRow.id })
                                        const endIndex = XEUtils.findLastIndexOf(tableData, row => { return row.id === currRow.id || row.parentId == currRow.id })
                                        const toInsert = tableData.splice(startIndex, endIndex - startIndex + 1)
                                        for (let i = 0; i < toInsert.length; i++) {
                                            tableData.splice(i, 0, toInsert[i])
                                        }
                                    } else {
                                        const currnetRowIndex = XEUtils.findIndexOf(this.tableData, row => {
                                            return row.id === currRow.id
                                        })
                                        tableData.splice(currnetRowIndex, 1)
                                        currRow.parentId = null
                                        tableData.splice(0, 0, currRow)
                                    }

                                }
                                // 如果变动了树层级，需要刷新数据
                                that.tableData = [...tableData]
                                // this.tableData = xTable.getTableData().tableData
                                console.log(that.tableData) 
                                changeProjectPageOrder(this.tableData).then(resp => {
                                    this.loadData()
                                })
                                location.reload()
                            }
                        }
                    );
                }
            });
        },
        onDeletePage(row) {
            VXETable.modal.confirm({
                title: '删除页面',
                content: '「' + row.name + '」的数据将被删除'
            }).then(type => {
                if (type === 'confirm') {
                    deleteProjectPage(row).then(resp => {
                        this.loadData()
                        location.reload()
                    })
                }
            })
        },
        onCreateTopFolder() {
            this.editMode = 'create'
            this.currentPage = { parentId: '', folder: true, type: 'folder', level: 1, projectId: this.projectId, componentType: '', tracker: {}, url: '' }
            this.showDialog = true
        },
        onCreateTopPage() {
            this.editMode = 'create'
            this.currentPage = { parentId: '', folder: false, type: 'wiki', level: 1, projectId: this.projectId, componentType: '', tracker: {}, url: '' }
            this.showDialog = true
        },
        onCreatePage(row) {
            this.editMode = 'create'
            this.currentPage = { parentId: row.id, folder: false, type: 'wiki', level: 2, projectId: this.projectId, componentType: '', tracker: {}, url: '' }
            this.showDialog = true
        },
        onEditPage(row) {
            this.editMode = 'edit'
            this.currentPage = row
            this.showDialog = true
        },
        onEditOK(row) {
            if (this.editMode === 'create') {
                row.projectId = this.projectId
                createProjectPage(row).then(resp => {
                    this.loadData()
                    this.showDialog = false
                    location.reload()
                })
            } else {
                updateProjectPageInfo(row).then(resp => {
                    this.loadData()
                    this.showDialog = false
                    location.reload()
                })
            }
        },
        onClapseAll() {
            this.$nextTick(() => {
                this.$refs.pagesTable.setAllTreeExpand(false)
            })
        },
        onExpandAll() {
            this.$nextTick(() => {
                this.$refs.pagesTable.setAllTreeExpand(true)
            })
        }

    }
};
</script>
<style scoped></style>