<template>
    <config-page :autoFit="true" :title="title" :description="description" id="userList">
        <template #header>
            <vxe-toolbar>
                <template v-slot:tools>
                    <a-space>
                        <a-button type="primary" @click="onCreateLinkType()">{{ $t('add')
                        }}</a-button>
                    </a-space>
                </template>
            </vxe-toolbar>
        </template>


        <vxe-table :data="tableData" :loading="loading" :row-config="{ isHover: true }" row-id="id" height="100%">
            <vxe-column title="序号" type="seq" width="60"></vxe-column>
            <vxe-column field="name" title="正向名称" width="300">
                <template #default="{ row }">
                    {{ row.name }}
                </template>
            </vxe-column>
            <vxe-column field="name" title="反向名称" width="300">
                <template #default="{ row }">
                    {{ row.oppositeName }}
                </template>
            </vxe-column>
            <vxe-column field="description" :title="$t('system.enum.table.desc')"></vxe-column>
            <vxe-column field="name" title="系统创建" width="100">
                <template #default="{ row }">
                    <a-tag v-if="row.system" style="margin-left:10px;">系统</a-tag>
                </template>
            </vxe-column>

            <vxe-column align="center" :title="$t('system.enum.table.action')" width="280">
                <template #default="{ row }">
                    <a-button type="link" :disabled="projectId && !row.projectId" @click="onEditLinkType(row)">{{
                        $t('edit') }}</a-button>
                    <a-button type="link" :disabled="row.system" @click="onDeleteLinkType(row.id)">
                        {{
                            $t('delete') }}</a-button>
                </template>
            </vxe-column>
        </vxe-table>



        <link-type-dialog :isShowDialog="showLinkTypeDialog" :linkType="linkType" :linkTypes="tableData"
            :editMode="editMode" :projectId="projectId" @cancel="showLinkTypeDialog = false"
            @ok="onCreateLinkTypeOK"></link-type-dialog>
    </config-page>
</template>

<script>
import LinkTypeDialog from './LinkTypeDialog'
import { findLinkTypes, createLinkType, findOneLinkType, updateOneLinkType, deleteLinkType, changeLinkTypeOrder } from '@/services/tracker/TrackerLinkTypeService'
import ConfigPage from '@/components/config-page/ConfigPage'
import SearchInput from '@/components/input/SearchInput'
import VXETable from 'vxe-table'
export default {
    name: 'LinkTypeList',
    components: {
        LinkTypeDialog, ConfigPage, SearchInput
    },
    data() {
        return {
            keyword: '',
            tableData: [],
            showLinkTypeDialog: false,
            linkType: {},
            editMode: 'create',
            loading: false,
            showItemsDialog: false,
        }
    },
    mounted() {
        this.loadData();
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        title() {
            return '工作项关联类型'

        },
        description() {
            return '设置项目范围内使用的工作项关联类型'

        }

    },
    created() {

    },
    methods: {
        loadData() {
            this.loading = true;
            const that = this;

            findLinkTypes(this.projectId).then(resp => {
                that.tableData = resp || [];
            }).finally(() => {
                this.loading = false
            })
        },

        onEditItems(category) {
            this.linkType = category
            this.showItemsDialog = true
        },
        onCreateLinkType() {
            this.editMode = 'create'
            this.linkType = { name: '', oppositeName: '', description: '', projectId: this.projectId }
            this.showLinkTypeDialog = true
        },
        onEditLinkType(row) {
            this.linkType = row
            this.editMode = 'edit'
            this.showLinkTypeDialog = true
        },
        onCreateLinkTypeOK(category) {
            if (this.editMode === 'create') {
                category.projectId = this.projectId
                createLinkType(this.projectId, category).then(resp => {

                    this.showLinkTypeDialog = false
                    this.loadData()
                })
            } else {
                updateOneLinkType(this.projectId, category).then(resp => {

                    this.showLinkTypeDialog = false
                    this.loadData()
                })
            }
        },
        async onDeleteLinkType(id) {
            const type = await VXETable.modal.confirm('您确定要删除该数据?')
            if (type === 'confirm') {
                const that = this
                deleteLinkType(this.projectId, id).then(res => {
                    this.loading = false;
                    this.$message.success(that.$t('system.enum.remind.delete.success')); //删除成功！
                    this.loadData();
                })
            }
        },
    }
}
</script>

<style lang="less" scoped>
.box {
    width: 20px;
    height: 20px;
    border: solid 1px black;
}

.tag-box {
    height: 18px;
    line-height: 15px;
    border-radius: 10px;
    color: #909090;
    background: #0000;
}
</style>
