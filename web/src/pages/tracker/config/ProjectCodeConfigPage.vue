<template>
    <config-page description="关联代码仓后，可以实现代码提交、合并请求与工作项等业务对象进行关联，并统计代码相关指标。">
        <div v-if="!currentBinding" style="width:25%;">
            <a-form-model ref="repoForm" layout="vertical" :model="formData" :rules="rules" style="margin-top: 10px;">
                <a-form-model-item ref="repositoryId" label="代码仓" prop="repositoryId">
                    <a-select v-model="formData.repositoryId" style="width:400px;" @change="onRepositoryChange">
                        <a-select-option v-for="item in repositories" :key="item.id" :value="item.id">
                            <span>{{ $t('repository.type.' + item.type) }}</span>
                            <span style="margin-left: 10px;">{{ item.host }}</span>
                        </a-select-option>
                    </a-select>
                </a-form-model-item>
                <a-form-model-item ref="projectIdOfRepository" label="代码仓内项目" prop="projectIdOfRepository">
                    <a-select v-model="formData.projectIdOfRepository" style="width:400px;">
                        <a-select-option v-for="item in projects" :key="item.id" :value="item.id">
                            {{ item.name }}
                        </a-select-option>
                    </a-select>
                </a-form-model-item>
                <a-button status="primary" @click="onBindProject">关联代码仓</a-button>
            </a-form-model>
        </div>


        <div v-else style="width:25%;">
            <a-form-model ref="repoForm" layout="vertical" style="margin-top: 10px;">
                <a-form-model-item ref="repositoryId" label="代码仓" prop="repositoryId" :required="true">
                    <a-input v-model="repositoryName" :disabled="true" />
                </a-form-model-item>
                <a-form-model-item ref="projectIdOfRepository" label="代码仓内项目" prop="projectIdOfRepository" :required="true">
                    <a-input v-model="currentBinding.projectNameOfRepository" :disabled="true" />
                </a-form-model-item>
                <a-button status="primary" @click="onunBindProject">解除关联代码仓</a-button>
            </a-form-model>
        </div>

    </config-page>
</template>

<script>
import VXETable from 'vxe-table';
import ConfigPage from '@/components/config-page/ConfigPage.vue'
import { findRopsitories, findProjects, bindProject, unbindProject, findCurrentBindProject } from '@/services/scm/RepositoryService'
export default {
    name: 'ProjectCodeConfigPage',
    components: { ConfigPage },
    data() {
        return {
            repositories: [],
            projects: [],
            formData: {
                repositoryId: '',
                projectIdOfRepository: '',
            },
            rules: {
                repositoryId: [{ required: true, message: "请选择代码仓", trigger: "change" },],
                projectIdOfRepository: [{ required: true, message: "请选择代码仓内的项目", trigger: "change" },]
            },
            currentBinding: undefined,
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        repositoryId() {
            if (this.currentBinding) {
                const repo = this.currentBinding.repository
                return repo.id
            }
        },
        repositoryName() {
            if (this.currentBinding) {
                const repo = this.currentBinding.repository
                return this.$t('repository.type.' + repo.type) + '  ' + repo.host
            }
        },
    },
    mounted() {
        this.loadData()
    },
    methods: {
        loadData() {
            this.currentBinding = undefined
            findCurrentBindProject(this.projectId).then(resp => {
                if (resp && resp.projectId) {
                    this.currentBinding = resp
                } else {
                    findRopsitories().then(resp => {
                        this.repositories = resp
                    })
                }
            })
        },

        onRepositoryChange(id) {
            findProjects(this.formData.repositoryId).then(resp => {
                this.projects = resp || []
            })
        },
        findProjectName(id) {
            return this.projects.filter(item => item.id === id)[0].name
        },
        onBindProject() {
            this.$refs.repoForm.validate((valid) => {
                if (valid) {
                    const projectRepo = this.formData
                    projectRepo.projectId = this.projectId
                    projectRepo.projectNameOfRepository = this.findProjectName(this.formData.projectIdOfRepository)
                    const repoId = this.formData.repositoryId
                    bindProject(repoId, projectRepo).then(resp => {
                        this.$message.success('绑定成功')
                        this.loadData()
                    })
                }
            })
        },
        onunBindProject() {
            VXETable.modal.confirm({
                title: '解除代码仓绑定',
                content: '要将本项目和' + this.currentBinding.projectNameOfRepository + '」解除绑定吗?'
            }).then(type => {
                if (type === 'confirm') {
                    unbindProject(this.repositoryId, this.projectId).then(resp => {
                        this.loadData()
                    })
                }
            })
        }

    },
}
</script>

<style></style>