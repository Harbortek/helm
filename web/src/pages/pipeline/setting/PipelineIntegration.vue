<template>
    <config-page :description="$t('config.pipline.config-page.description')">
        <vxe-toolbar>
            <template #tools>
                <a-space><a-button v-action="'PROJECT_PIPELINE'" @click="showCreateDialog = true">{{$t('config.pipline.tool.button')}}</a-button></a-space>
            </template>
        </vxe-toolbar>
        <vxe-table size="small" ref="xTable" show-overflow border row-key :show-header="true" :auto-resize="true"
            :data="tableData" :row-config="{ keyField: 'id', isCurrent: true }">
            <vxe-column type="seq" :title="$t('config.pipline.table.id')" width="50px" header-align="center" align="center">
            </vxe-column>
            <vxe-column field="type" :title="$t('config.pipline.table.type')" width="100" header-align="center" align="center">
                <template #default="{ row }">
                    <div style="display: inline-flex;">
                        Jenkins
                    </div>
                </template>
            </vxe-column>

            <vxe-column field="serverUrl" :title="$t('config.pipline.table.addr')">
                <template #default="{ row }">
                    {{ row.serverUrl }}
                </template>
            </vxe-column>
            <vxe-column field="userName" :title="$t('config.pipline.table.user')">
                <template #default="{ row }">
                    {{ row.userName }}
                </template>
            </vxe-column>
            <vxe-column field="createDate" width="180" :title="$t('config.pipline.table.time')" header-align="center" align="center">
                <template #default="{ row }">
                    {{ row.createDate }}
                </template>
            </vxe-column>
            <vxe-column :title="$t('config.pipline.table.operate')" width="400" header-align="center" align="center">
                <template #default="{ row }">
                    <a-button v-action="'PROJECT_PIPELINE'" type="link" @click="onEdit(row)">{{$t('edit')}}</a-button>
                    <a-button v-action="'PROJECT_PIPELINE'" type="link" @click="onDelete(row)">{{$t('delete')}}</a-button>
                </template>
            </vxe-column>

        </vxe-table>

        <create-pipeline-dialog :isShowDialog="showCreateDialog" @cancel="showCreateDialog = false"
            @ok="onCreatePipelineOK" />
        <create-jenkins-dialog :isShowDialog="showJenkinsDialog" :editMode="editMode" :repository="repository"
            @cancel="showJenkinsDialog = false;" @ok="onCreateJenkinsOK" @previous="onPrevious" />

    </config-page>
</template>

<script>
import VXETable from 'vxe-table';
import ConfigPage from '@/components/config-page/ConfigPage.vue'
import CreatePipelineDialog from './CreatePipelineDialog.vue';
import CreateJenkinsDialog from './CreateJenkinsDialog.vue';
import { createPipelineRepository, updatePipelineRepository, findPipelineRepositories, deletePipelineRepository } from '@/services/pipeline/PipelineService'
export default {
    name: 'PipelineIntegration',
    components: { ConfigPage, CreatePipelineDialog, CreateJenkinsDialog },
    data() {
        return {
            tableData: [],
            showCreateDialog: false,
            editMode: 'create',
            showJenkinsDialog: false,
            repository: {},
            showWebHookDialog: false,
        }
    },
    computed: {

    },
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            this.repository = {}
            findPipelineRepositories().then(resp => {
                this.tableData = resp
            })
        },
        onCreatePipelineOK(type) {
            this.showCreateDialog = false
            if (type === 'jenkins') {
                this.editMode = 'create'
                this.repository = { type: type }
                this.showJenkinsDialog = true
            }
        },
        onCreateJenkinsOK(repo) {
            console.log(repo)
            this.showJenkinsDialog = false
            if (this.editMode == 'create') {
                createPipelineRepository(repo).then(resp => {
                    this.loadData()
                })
            } else {
                updatePipelineRepository(repo).then(resp => {
                    this.loadData()
                })
            }
        },
        onPrevious() {
            this.showJenkinsDialog = false
            this.showCreateDialog = true
        },
        onEdit(row) {
            this.editMode = 'edit'
            this.repository = row
            this.showJenkinsDialog = true
        },
        onDelete(row) {
            VXETable.modal.confirm({
                title: this.$t('config.pipline.modal.delete.title'),
                content: this.$t('config.pipline.modal.delete.content',{serverUrl:row.serverUrl}),
                confirmButtonText: this.$t('ok'),
                cancelButtonText: this.$t('cancel'),
            }).then(type => {
                if (type === 'confirm') {
                    deletePipelineRepository(row.id).then(resp => {
                        this.loadData()
                    })
                }
            })
        },
    },
}
</script>

<style></style>