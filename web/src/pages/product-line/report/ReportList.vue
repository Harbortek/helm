<template>
    <content-page>
        <vxe-toolbar size="medium" ref="xToolbar">
            <template #buttons>
                <vxe-button content="新增分组" @click="onCreateReportGroup"></vxe-button>
                <vxe-button content="新增报表" @click="onCreateReport"></vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-table size="medium" ref="xTable" show-overflow border row-key :row-config="{ isHover: true }"
            :height="tableHeight" :data="tableData"
            :tree-config="{ transform: false, childrenField: 'reports', accordion: false, line: true, rowField: 'id' }">
            <vxe-column field="name" title="报表名称" tree-node header-align="center">

                <template #default="{ row }">
                    <span v-if="row.groupId"><vxe-button type="text" status="primary" @click="gotoReport(row)">{{
                    row.name
                }}</vxe-button></span>
                    <span v-else>{{ row.name }}</span>
                </template>
            </vxe-column>
            <vxe-column field="createBy.name" title="创建者" width="100px">

                <template #default="{ row }">
                    <a-avatar v-if="row.createBy.icon" class="avatar" size="small" :src="iconUrl(row.createBy.icon)" />
                    <a-avatar v-else class="avatar" size="small" style="backgroundColor:rgb(44,178,174)">{{
                    row.createBy.name }}</a-avatar>
                    {{ row.createBy.name }}
                </template>
            </vxe-column>
            <vxe-column field="createDate" title="创建日期" width="200px"></vxe-column>
            <vxe-column title="" align="right" width="120">

                <template #default="{ row }">
                    <a-dropdown>
                        <vxe-button type="text" icon="vxe-icon-ellipsis-h"></vxe-button>
                        <a-menu slot="overlay">
                            <template>
                                <a-menu-item v-if="row.groupId">
                                    <a @click="onEditReport(row)"><a-space><a-icon type="folder-add" />编辑</a-space></a>
                                </a-menu-item>
                                <a-menu-item v-if="row.groupId">
                                    <a @click="onSaveReportAs(row)"><a-space> <a-icon
                                                type="file-add" />另存为</a-space></a>
                                </a-menu-item>
                                <a-menu-item>
                                    <a @click="onRenameReport(row)"><a-space><a-icon type="file-add" />重命名</a-space></a>
                                </a-menu-item>
                                <a-menu-divider />
                                <a-menu-item>
                                    <a @click="onDeleteReport(row)"> <a-space><a-icon type="delete" />删除</a-space></a>
                                </a-menu-item>
                            </template>
                        </a-menu>
                    </a-dropdown>
                </template>
            </vxe-column>
        </vxe-table>
        <ReportGroupDialog :editMode="editMode" :isShowDialog="isShowGroupDialog" :reportGroup="currentReportGroup"
            :productLineId="productLineId" @close="isShowGroupDialog = false" @ok="onReportGroupDialogOK" />
        <ReportDialog :editMode="editMode" :isShowDialog="isShowDialoog" :report="currentReport"
            :productLineId="productLineId" @close="isShowDialoog = false" @ok="onReportDialogOK" />
        <RenameDialog ref="renameDialog" />
    </content-page>
</template>

<script>
import ContentPage from '@/components/page/content/ContentPage.vue';
import ReportGroupDialog from '@/pages/product-line/report/ReportGroupDialog.vue'
import ReportDialog from '@/pages/product-line/report/ReportDialog.vue'
import RenameDialog from '@/pages/product-line/report/RenameDialog.vue';
import { findReports, createReport, updateReport, deleteReport } from '@/services/product-line/ReportService';
import { findReportGroups, createReportGroup, updateReportGroup, deleteReportGroup } from '@/services/product-line/ReportGroupService';
import VXETable from 'vxe-table'
import { iconUrl } from "@/utils/util"
export default {
    name: 'ReportList',
    components: {
        ContentPage, ReportDialog, ReportGroupDialog, RenameDialog
    },
    // props: {
    //     productLineId: {
    //         required: true,
    //     }
    // },
    data() {
        return {
            tableData: [],
            tableHeight: 400,
            editMode: 'create',
            isShowDialoog: false,
            currentReport: {},
            currentReportGroup: {},
            isShowGroupDialog: false,
        }
    },
    computed: {
        productLineId() {
            return this.$route.params.productLineId
        }
    },
    watch: {
        productLineId: {
            immediate: true,
            handler(newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        }
    },
    mounted() {
        this.$nextTick(function () {
            this.tableHeight =
                window.innerHeight - (this.$refs.xTable.$el.offsetTop) - 80;

            // 监听窗口大小变化
            let self = this;
            window.onresize = function () {
                if (self.$refs.xTable.$el) {
                    self.tableHeight =
                        window.innerHeight - (self.$refs.xTable.$el.offsetTop) - 80;
                }
            };

        })
    },
    methods: {
        iconUrl(v) {
            return iconUrl(v)
        },
        loadData() {
            findReports(this.productLineId).then(resp => {
                this.tableData = resp
                this.$nextTick(function () {
                    this.$refs.xTable.setAllTreeExpand(true)
                })
            })
        },
        onCreateReportGroup() {
            this.editMode = 'create'
            this.currentReportGroup = { name: '' }
            this.isShowGroupDialog = true
        },
        onCreateReport(parent) {
            this.editMode = 'create'
            this.currentReport = { name: '', description: '' }
            this.isShowDialoog = true
        },
        onEditReport(row) {
            this.editMode = 'edit'
            this.currentReport = row
            this.isShowDialoog = true
        },
        onDeleteReport(row) {
            VXETable.modal.confirm({
                title: '删除报表',
                content: '「' + row.name + '」的数据将被删除'
            }).then(type => {
                if (type === 'confirm') {
                    deleteReport(this.productLineId, row.id).then(resp => {
                        this.loadData()
                    })
                }
            })
        },
        onSaveReportAs() {

        },
        async onRenameReport(row) {
            if (row.groupId) {
                const result = await this.$refs.renameDialog.show({ title: '报表', label: '报表名称', name: row.name })
                console.log(result)
                row.name = result
                updateReport(this.productLineId, row).then(resp => {
                    this.$message.success('重命名成功')
                    this.loadData()
                })
            } else {
                const result = await this.$refs.renameDialog.show({ title: '分组', label: '分组名称', name: row.name })
                console.log(result)
                row.name = result
                updateReportGroup(this.productLineId, row).then(resp => {
                    this.$message.success('重命名成功')
                    this.loadData()
                })
            }
        },
        onReportGroupDialogOK(group) {
            if (group.id) {
                updateReportGroup(this.productLineId, group).then(resp => {
                    this.isShowGroupDialog = false
                    this.$message.success('更新分组成功')
                    this.loadData()
                })
            } else {
                group.productLineId = this.productLineId

                createReportGroup(this.productLineId, group).then(resp => {
                    this.isShowGroupDialog = false
                    this.$message.success('创建分组成功')
                    this.loadData()
                })
            }
        },
        onReportDialogOK(report) {
            if (report.id) {
                updateReport(this.productLineId, report).then(resp => {
                    this.isShowDialoog = false
                    this.$message.success('更新报表成功')
                    this.loadData()
                })
            } else {
                report.productLineId = this.productLineId

                createReport(this.productLineId, report).then(resp => {
                    this.isShowDialoog = false
                    this.$message.success('创建报表成功')
                    this.loadData()
                })
            }
        },
        gotoReport(row) {
            this.$router.push({
                name: 'productLineReportViewer',
                params: {
                    productLineId: this.productLineId,
                    reportId: row.id
                }
            })
        }
    },
}
</script>

<style></style>