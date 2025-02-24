<template>
    <config-page :description="$t('config.codeRepository.config-page.description')">
        <vxe-toolbar>
            <template #tools>
                <a-space><a-button v-action="'PROJECT_SCM'" @click="showCreateDialog = true">{{$t('config.codeRepository.tool.button')}}</a-button></a-space>
            </template>
        </vxe-toolbar>
        <vxe-table size="small" ref="xTable" show-overflow border row-key :show-header="true" :auto-resize="true"
            :data="tableData" :checkbox-config="{ labelField: 'name' }" :row-config="{ keyField: 'id', isCurrent: true }">
            <vxe-column type="seq" title="ID" width="50px" header-align="center" align="center">
            </vxe-column>
            <vxe-column field="type" :title="$t('config.codeRepository.table.type')" width="100" header-align="center" align="center">
                <template #default="{ row }">
                    <div style="display: inline-flex;">
                        <span v-if="row.type === 'gitlab'">{{$t('config.codeRepository.table.type.gitlab')}}</span>
                        <span v-if="row.type === 'svn'">{{$t('config.codeRepository.table.type.svn')}}</span>
                        <span v-if="row.type === 'bitbucket'">{{$t('config.codeRepository.table.type.bitbucket')}}</span>
                    </div>
                </template>
            </vxe-column>

            <vxe-column field="status" :title="$t('config.codeRepository.table.addr')">
                <template #default="{ row }">
                    {{ row.host }}
                </template>
            </vxe-column>
            <vxe-column field="tokenType" width="80" :title="$t('config.codeRepository.table.auth')" header-align="center" align="center">
                <template #default="{ row }">
                    OAuth
                </template>
            </vxe-column>
            <vxe-column field="grantUserName" width="100" :title="$t('config.codeRepository.table.associate-user')" header-align="center" align="center">
                <template #default="{ row }">
                    {{ row.grantUserName }}
                </template>
            </vxe-column>
            <vxe-column field="createDate" width="180" :title="$t('config.codeRepository.table.time')" header-align="center" align="center">
                <template #default="{ row }">
                    {{ row.createDate }}
                </template>
            </vxe-column>
            <vxe-column :title="$t('config.codeRepository.table.operate')" width="400" header-align="center" align="center">
                <template #default="{ row }">
                    <a-button type="link" v-action="'PROJECT_SCM'" @click="onResetWebHook(row)">{{$t('config.codeRepository.table.operate.reset')}}</a-button>
                    <a-button type="link" v-action="'PROJECT_SCM'" @click="onWebHook(row)">{{$t('config.codeRepository.table.operate.manual')}}</a-button>
                    <a-button type="link" v-action="'PROJECT_SCM'" @click="onDelete(row)">{{$t('delete')}}</a-button>
                    <!-- <a-button type="link" @click="onListProjects(row)">项目</a-button> -->
                </template>
            </vxe-column>

        </vxe-table>
        <div style="margin-top:20px;background-color: transparent;">
            <a-alert :message="$t('config.codeRepository.config-page.instructions')"
                :description="$t('config.codeRepository.config-page.instructions-desc')"
                type="info" show-icon />
        </div>
        <create-repository-dialog :isShowDialog="showCreateDialog" :editMode="editMode" @cancel="showCreateDialog = false"
            @ok="onCreateRepositoryOK" />
        <create-gitlab-repository-dialog :isShowDialog="showGitlabDialog" :editMode="editMode" :repository="repository"
            @cancel="showGitlabDialog = false;" @ok="onCreateGitlabOK" @previous="onPrevious" />
        <web-hook-dialog :isShowDialog="showWebHookDialog" :repository="repository" @ok="showWebHookDialog = false"
            @cancel="showWebHookDialog = false" />
    </config-page>
</template>

<script>
import VXETable from 'vxe-table';
import ConfigPage from '@/components/config-page/ConfigPage.vue'
import CreateRepositoryDialog from './CreateRepositoryDialog.vue';
import CreateGitlabRepositoryDialog from './CreateGitlabRepositoryDialog.vue';
import WebHookDialog from './WebHookDialog.vue'
import { createRopsitory, enableRopsitory, updateRopsitory, findRopsitories, deleteRopsitory, findProjects, resetWebHook, checkRepositoryUsedInProject } from '@/services/scm/RepositoryService'
export default {
    name: 'RepositoryList',
    components: { ConfigPage, CreateRepositoryDialog, CreateGitlabRepositoryDialog, WebHookDialog },
    data() {
        return {
            tableData: [],
            showCreateDialog: false,
            editMode: 'create',
            showGitlabDialog: false,
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
            findRopsitories().then(resp => {
                this.tableData = resp
            })
        },
        onCreateRepositoryOK(type) {
            this.showCreateDialog = false
            if (type === 'gitlab') {
                const repo = { type: type, deleted: true }
                createRopsitory(repo).then(resp => {
                    this.repository = resp
                    this.showGitlabDialog = true
                })
            }
        },
        onCreateGitlabOK(repo) {
            console.log(repo)
            this.showGitlabDialog = false
            updateRopsitory(repo)

            VXETable.modal.confirm({
                title: this.$t('config.codeRepository.message.gitLab'),
                content: this.$t('config.codeRepository.message.gitLab.content'),
                confirmButtonText: this.$t('ok'),
                cancelButtonText: this.$t('cancel'),
            }).then(type => {
                if (type === 'confirm') {
                    enableRopsitory(repo.id).then(resp => {
                        this.loadData()
                    })
                }
            })
        },
        onPrevious() {
            this.showGitlabDialog = false
            this.showCreateDialog = true
        },
        onDelete(row) {
            VXETable.modal.confirm({
                title: this.$t('config.codeRepository.message.delete'),
                content: this.$t('config.codeRepository.message.delete.content',{host:row.host}),
                confirmButtonText: this.$t('ok'),
                cancelButtonText: this.$t('cancel'),
            }).then(type => {
                if (type === 'confirm') {
                    deleteRopsitory(row.id).then(resp => {
                        this.loadData()
                    })
                }
            })
        },
        onListProjects(row) {
            findProjects(row.id).then(resp => {
                console.log(resp)
            })
        },
        onResetWebHook(row) {
            VXETable.modal.confirm({
                title: this.$t('config.codeRepository.message.reset'),
                content: this.$t('config.codeRepository.message.reset.content'),
                confirmButtonText: this.$t('ok'),
                cancelButtonText: this.$t('cancel'),
            }).then(type => {
                if (type === 'confirm') {
                    resetWebHook(row.id).then(resp => {
                        this.$message.success(this.$t('config.codeRepository.message.reset.success'))
                    })
                }
            })
        },
        onWebHook(row) {
            this.repository = row
            this.showWebHookDialog = true
        }

    },
}
</script>

<style></style>