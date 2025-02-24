<template>
    <config-page description="展示在项目计划下的所有版本,在迭代中中可以关联相关版本。">
        <div>
            <vxe-toolbar style="flex: 0 0;">

                <template #tools>
                    <vxe-button v-action="'PAGE_WRITE|'+pageId" icon="vxe-icon-add" @click="onCreateVerson">新增版本</vxe-button>
                </template>
            </vxe-toolbar>
            <vxe-table ref="xTable" show-overflow border row-key :show-header="true" :auto-resize="true" :data="tableData"
                :row-config="{ keyField: 'id', isCurrent: true }" style="flex: 1 1 auto;">


                <vxe-column field="name" title="名称">
                    <template #default="{ row }">
                        {{ row.name }}
                    </template>
                </vxe-column>

                <!-- <vxe-column field="progress" title="进度" header-align="center" align="center" width="100px">
                    <template #default="{ row }">
                        {{ formatProgress(row.progress) }}
                    </template>
                </vxe-column>
                <vxe-column field="owner.name" title="负责人" header-align="center" align="center" width="150px">
                    <template #default="{ row }">
                        <template v-if="row.owner.id">
                            <a-avatar v-if="row.owner?.icon" class="avatar" size="small" :src="iconUrl(row.owner?.icon)" />
                            <a-avatar v-else class="avatar" size="small" style="backgroundColor:rgb(44,178,174)">{{
                                row.owner?.name }}</a-avatar>
                            {{ row.owner?.name }}
                        </template>
                        <template v-else>
                            -
                        </template>
                    </template>
                </vxe-column>
                <vxe-column field="planStartDate" title="开始日期" header-align="center" align="center" width="150">
                    <template #default="{ row }">
                        {{ formatDate(row.planStartDate) }}
                    </template>
                </vxe-column>
                <vxe-column field="planEndDate" title="结束日期" header-align="center" align="center" width="150px">
                    <template #default="{ row }">
                        {{ formatDate(row.planEndDate) }}
                    </template>
                </vxe-column> -->
                <vxe-column align="center" title="操作" width="280">
                    <template #default="{ row }">
                        <a-button v-action="'PAGE_WRITE|'+pageId" type="link" @click="onEditVersion(row)">{{
                            $t('edit') }}</a-button>
                        <a-button v-action="'PAGE_WRITE|'+pageId" type="link" @click="onDeleteVersion(row)">
                            {{
                                $t('delete') }}</a-button>
                    </template>
                </vxe-column>
            </vxe-table>
        </div>
        <input-name-dialog :isShowDialog="showDialog" :name="formData.name" @ok="onEditOK" @cancel="showDialog = false" />
    </config-page>
</template>

<script>
import { findVersions, createVersion, updateVersion, deleteVersion } from '@/services/plan/TargetVersionService'
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import { iconUrl } from "@/utils/util"
import { formatDate, isWeekend } from '@/utils/DateUtils'
import InputNameDialog from '@/components/dialog/InputNameDialog.vue'
import VXETable from 'vxe-table';
export default {
    name: 'TargetVersionList',
    components: { ConfigPage, InputNameDialog },
    data() {
        return {
            tableData: [],
            formData: { name: '' },
            showDialog: false,
            editMode: 'create',
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        pageId() {
            return this.$route.params.pageId
        },
        finished() {
            return this.tableData.filter(row => row.finished).length;
        },
        total() {
            return this.tableData.length
        },
    }, 
    mounted() {
        this.loadData()
    },
    methods: {
        iconUrl(v) {
            return iconUrl(v)
        },
        formatDate(v) {
            if (!v) return '-'
            else return formatDate(v)
        },
        formatProgress(v) {
            if (!v) return '-'
            else return v + '%'
        },
        loadData() {
            findVersions(this.projectId).then(resp => {
                this.tableData = resp
            })
        },
        onCreateVerson() {
            this.formData = { name: '', projectId: this.projectId }
            this.editMode = 'create'
            this.showDialog = true
        },
        onEditVersion(row) {
            this.formData = row
            this.editMode = 'edit'
            this.showDialog = true
        },
        onEditOK(result) {
            this.formData.name = result
            if (this.editMode === 'create') {
                createVersion(this.formData).then(resp => {
                    this.loadData()
                    this.showDialog = false
                })
            } else {
                updateVersion(this.formData).then(resp => {
                    this.loadData()
                    this.showDialog = false
                })
            }
        },
        onDeleteVersion(row) {
            VXETable.modal.confirm({
                title: '删除版本',
                content: '「' + row.name + '」将被删除，该动作不可恢复'
            }).then(type => {
                if (type === 'confirm') {
                    deleteVersion(row.id).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」已被删除', status: 'info' })
                        this.loadData()
                    })
                }
            })
        }


    },
}
</script>

<style lang="less" scoped></style>