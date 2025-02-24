<template>
    <div class="layout-center">
        <div class="layout-center-full-height addProject">
            <div class="addProject-inner">
                <div class="addProject-header top_head">新建项目</div>

                <div class="addProject-content">
                    <div class="addProject-main">
                        <div class="select-project-template">
                            <div class="select-project-template-left">
                                <a-form-model ref="projectForm" class="project-form" layout="vertical" :model="formData"
                                    :rules="rules">
                                    <a-form-model-item ref="name" label="项目名称" prop="name">
                                        <a-input v-model="formData.name" @blur="() => {
                                            $refs.name.onFieldBlur();
                                        }" />
                                    </a-form-model-item>
                                    <a-form-model-item ref="keyName" label="缩写" prop="keyName">
                                        <a-input v-model="formData.keyName" @input="handleInput" @blur="() => {
                                            $refs.keyName.onFieldBlur();
                                        }
                                            " />
                                    </a-form-model-item>
                                    <a-form-model-item v-if="activeTab=='TEMPLATE'" ref="categoryId" label="项目分类" prop="categoryId">
                                        <project-category-select v-model="formData.categoryId"
                                            select-first="true"></project-category-select>
                                    </a-form-model-item>
                                    <a-form-model-item ref="description" label="描述" prop="description">
                                        <a-textarea v-model="formData.description" placeholder="项目描述" :rows="4" @blur="() => {
                                            $refs.description.onFieldBlur();
                                        }
                                            " />
                                    </a-form-model-item>
                                </a-form-model>
                                <a-tabs :activeKey="activeTab" @change="onTabChange" class="project-tabs" style="">
                                    <a-tab-pane key="TEMPLATE" tab="选择项目模版" force-render>
                                        <selectable-list :items="templates" :selected-item="selectedTemplate"
                                            @change="onTemplateChange">

                                        </selectable-list>
                                    </a-tab-pane>
                                    <a-tab-pane key="EXISTED" tab="从已有项目复制">
                                        <a-form-model-item ref="id" label="选择要复制的项目" prop="id">
                                            <project-select style="width:100%" v-model="formData.id"
                                                @change="onChangeProjectSelect" :selectFirst="true"></project-select>
                                        </a-form-model-item>
                                        <ProjectCopyTool :formData="formData" :trackers="trackers" :trackerIds="trackerIds"></ProjectCopyTool>
                                    </a-tab-pane>
                                </a-tabs>
                            </div>
                            <div class="select-project-template-right">
                                <div class="select-project-template-right-img select-project-template-right-img-project-t5">
                                </div>
                                <div>
                                    <div class="select-project-template-right-info">
                                        <div class="select-project-template-right-subtitle">模板介绍</div>
                                        <div class="select-project-template-right-desc">
                                            通过内置的敏捷研发管理组件，可以轻松实现迭代管控、需求分配、缺陷管理等核心研发工作，通过各类报表实时掌控项目进度状况。</div>
                                    </div>
                                    <div class="select-project-template-right-detail-desc">
                                        <div class="select-project-template-right-subtitle">包含组件（8个）</div>
                                        <div class="select-project-template-right-desc">需求、迭代、缺陷、任务、概览、报表、文档、成员</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="addProject-footer ">
                        <a-space>
                            <vxe-button status="blank" content="取消" @click="onCancel"></vxe-button>
                            <vxe-button status="primary" content="完成" @click="onCreateProject"
                                :loading="spinning"></vxe-button>
                        </a-space>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>

import ProjectSelect from "@/components/select/ProjectSelect.vue";
import { findProjectTemplates } from "../../../services/tracker/ProjectTemplateService";
import {
    createProject,
    copyProject,
    checkDuplicateProjectName,
    checkDuplicateProjectShortName
} from "@/services/tracker/ProjectService";
import {findTrackers,} from "@/services/tracker/TrackerService";
import ContentPage from '../../../components/page/content/ContentPage.vue';
import PageHeader from '../../../components/page/header/PageHeader.vue';
import SelectableList from '../../../components/list/SelectableList.vue';
import ProjectCategorySelect from '../../../components/select/ProjectCategorySelect.vue';
import ProjectCopyTool from "@/components/tool/ProjectCopyTool.vue";
export default {
    name: "ProjectList",
    components: { ContentPage, PageHeader, SelectableList, ProjectCategorySelect, ProjectSelect,
        ProjectCopyTool},
    data() {
        const that = this;
        const nameValidator = (rule, value, callback) => {
            checkDuplicateProjectName(this.formData.projectId || 0, value)
                .then(resp => {
                    if (!resp) {
                        callback();
                    } else {
                        callback(new Error("项目名称已存在"));
                    }
                })
                .catch(err => {
                    callback(new Error("项目名称已存在"));
                });
        };
        const keyNameValidator = (rule, value, callback) => {

            checkDuplicateProjectShortName(this.formData.projectId || 0, value)
                .then(resp => {
                    if (!resp) {
                        callback();
                    } else {
                        callback(new Error("项目缩略名称已存在"));
                    }
                })
                .catch(err => {
                    callback(new Error("项目缩略名称已存在"));
                });
        };
        return {
            currentStep: 1,
            activeTab: 'TEMPLATE',
            orderBy: 'name',
            layout: 'card',
            keyword: "",
            loading: false,
            templates: [],
            selectedTemplate: {},
            projects: [],
            formData: { 
                name: '', 
                keyName: '', 
                description: '', 
                categoryId: '',
                id:'',
                trackerList: [],
                sprint: true,
                member: true,
                attachment: true,
                report: true,
            },
            rules: {
                name: [
                    { required: true, message: "请输入项目名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "项目名称长度在2到20之间",
                        trigger: "blur",
                    },
                    { validator: nameValidator, trigger: ["change","blur"] },
                ],
                keyName: [
                    { required: true, message: "请输入项目缩写", trigger: "blur" },
                    {
                        min: 2,
                        max: 5,
                        message: "分类名称长度在2到5之间",
                        trigger: "blur",
                    },
                    { validator: keyNameValidator, trigger: ["change","blur"] },
                ],
            },
            projectRoles: [],
            selectedRole: { members: [] },
            spinning: false,
            projectCopy: {},
            trackers: [],
            trackerIds: [],
        };
    },
    mounted() {
        this.loadData();
    },
    methods: {
        onTabChange(activeKey) {
            this.activeTab = activeKey
        },
        onChangeOrderBy(value) {
            this.orderBy = value
        },
        onChangeLayout(value) {
            this.layout = value
        },
        loadData() {
            findProjectTemplates().then((resp) => {
                this.templates = resp;
                if (this.templates.length > 0) {
                    this.selectedTemplate = this.templates[0]
                }
            });

        },
        onSelectUsers() {

        },
        onTemplateChange(item) {
            this.selectedTemplate = item
        },
        onRoleChange(item) {
            this.selectedRole = item
        },
        handleInput(event) {
            this.formData.keyName = event.target.value.toUpperCase();
        },

        onCancel() {
            this.$router.push({ name: 'projectList' })
        },
        onNext() {

        },
        onCreateProject() {
            if(this.activeTab=='TEMPLATE'){
                this.$refs["projectForm"].validate((valid) => {
                    if (valid) {
                        let project = {
                            name: this.formData.name,
                            keyName: this.formData.keyName,
                            description: this.formData.description,
                            category: { id: this.formData.categoryId },
                            templateName: this.selectedTemplate.name,
                            roleMembers: this.projectRoles
                        }
                        this.spinning = true
                        this.$message.loading({ content: 'Loading...', key: "key", duration: 0 });
                        createProject(project).then(resp => {
                            this.$message.success({ content: '项目创建成功', key: "key" })
                            this.$store.dispatch('account/getInfo').then(()=>{
                                this.$router.push({ name: 'projectList'})
                            })
                        }).catch((error) => {
                            this.$message.error({ content: '项目创建失败', key: "key" })
                        }).finally(res=>{
                            this.spinning = false
                        });
                    }
                });
            }else{
                //复制项目
                this.$refs["projectForm"].validate((valid) => {
                    if (valid) {
                        this.spinning = true
                        this.$message.loading({ content: 'Loading...', key: "key", duration: 0  });
                         copyProject(this.formData).then((resp) => {
                            this.$message.success({ content: '项目复制成功', key: "key" })
                            this.$store.dispatch('account/getInfo').then(()=>{
                                this.$router.push({ name: 'projectList'})
                            })
                        }).catch((error) => {
                            this.$message.error({ content: '项目复制失败', key: "key" })
                        }).finally(res=>{
                            this.spinning = false
                        });
                    }
                });
            }
            

        },
        onChangeProjectSelect(v,project){
            this.projectCopy=project;
            console.log("projectCopy",project)
            findTrackers({ projectId: project.id}).then(res=>{
                this.trackers=res;
                this.trackerIds=res.map(v=>v.id)
                this.formData.trackerList=this.trackerIds
                this.formData.sprint=true;
                this.formData.member=true;
                this.formData.attachment=true;
                this.formData.report=true;
            })
        },
    },
};
</script>
<style lang="less">
.layout-center>.layout-center-full-height {
    position: absolute;
    height: 100%;
    width: 100%;
}

.addProject {
    .addProject-inner {
        background: #fff;
        margin: 20px auto;
        border-radius: 3px;
        box-shadow: 0 4px 6px 0 rgba(31, 31, 31, .05), 0 0 2px 0 rgba(31, 31, 31, .2);
        height: calc(100% - 2 * 20px);
        width: 900px;
        display: flex;
        flex-direction: column;
    }

    .addProject-header {
        flex: 0 0 auto;
        line-height: 1;
        padding: 20px;
        font-size: 18px;
        font-weight: 500;
        border-bottom: 1px solid #e8e8e8;
    }

    .addProject-content {
        flex: 1 1 auto;
        min-height: 0;
        display: flex;
        flex-direction: column;
    }

    .addProject-main {
        display: flex;
        flex: 1 1 auto;
        min-height: 0;
        padding: 0;
        overflow: auto;
    }

    .select-project-template {
        display: flex;

        .toolbar {
            padding-bottom: 16px;
        }

        .select-project-template-left {
            width: 540px;
            display: flex;
            flex-direction: column;
            padding: 0 20px 0 20px;

            .project-form {
                width: 500px;

            }
            .project-tabs{
                height: 100%;
                overflow-y: auto;
                &::-webkit-scrollbar { //改变滚动条
                    width: 5px;
                    height: 5px;
                }
                &::-webkit-scrollbar-thumb {
                    border-radius: 5px;
                    background: #bebfc2;
                }
            }
        }

        .select-project-template-right {
            flex: 0 0 auto;
            width: 360px;
            padding: 20px;
            padding-top: 45px;

            .select-project-template-right-img {
                margin-bottom: 20px;
                height: 194px;
                background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAUQAAADCCAMAAAAGqkuFAAABfVBMVEVHcEwAAAAAAAAAAAAkJCQfHx8cHBwAAADW1tb5+fn9/f7///8XFxcAAAAVFRUzMzMnJycqKioAAAD+/v/19vfu8PLr7vDx8/Xn6e3k5+oAAADy8/X3+Pn19vjz9Pbs7fDk5urp7O7X3ODO1NnR19vf4ubs7vDc4eTz+P7J3vinyfOEtO9moere6/s4heRJj+Zcm+l+sO6hxvLE2/d5rO01g+Mwf+IxgOO51PZUleiWvvH4+PlFjOWbwvK/1/dYmOj6/P34+frW5vpvpuvi7fvo8fyRu/BPkuf4+/9qo+uy0PXl8PxCiuXO4fk9h+Tu9f3S4/mszPRhnurw9v2KuO/q8/3a6Pp0qezm+fbh+PTo8vzf9/M0itA9s4g4tIY0tIUwtIMstIIqtIEmtIAitH5y0K8yhNk9rJWeyvNOxJnz/Pq9696S3MOw5tQzj+Xt+/nY9e/I4PicyfM6mbhBs4mE1rih4cw6k+ZBqJ7I7+VFs4sRERG5ubkMDAwWFhaI5zUIAAAAf3RSTlMAAQIDBwgJCSzI//8LBAwFDQYP/////////xD///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////8PMxQXLMdDrgAACIZJREFUeAHs1YFugjAUheENQAWsCLYC1qlYdO//hksCSVYG6yXSsC7nf4P75TR9+6Mh9G4IAXCmQDhLJkJvNPQr4wQ/NKKoE/poJJ1xwNDzg3C1RsZWYeC3jAOGmyhOCKE42vxUbJ9yEIGHWhS0T7pv6K2xQ3Lx2usUdUR/Cxt6W79D1IfIdqCht2PdFDVENg0RiExD7IaY7kFDb5+2U9QQGcumIAIx66aov+YgBw29PNDec4eYTkMEYjqAmPUQD1wcvyX4IdEryspQWbhKZL4+D7IeojeAyI+9eM+wIuSoovn6DtHTEH2WhTqiOPYSiVZZESrdJDRfn+Rhxnz7iEDEc+7FiYgnfCz0608kRKQHRCACEYgIiEAEIhBfCYiSHlpwiec4sV58duo5AxGIQAQiEIEIRCACEYhABCIQgQhEIAIRiEAEooWACEQg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);
                background-repeat: no-repeat;
                background-position: center center;
                background-size: 324px 194px;
            }

            .select-project-template-right-subtitle {
                color: #303030;
                font-weight: 500;
                font-size: 15px;
                display: flex;
                margin-top: 20px;
            }

            .user-head-count {
                color: #909090;
                ;
                flex-grow: 1;
                margin: 0 var(--space);
            }
        }

    }

    .ant-form-item {
        margin-bottom: 0px !important;
    }

    .addProject-footer {
        flex: 0 0 auto;
        text-align: right;
        height: 50px;
        display: flex;
        align-items: center;
        justify-content: flex-end;
        padding: 0 20px;
        background: #fff;
        border-top: 1px solid #e8e8e8;
    }
}
</style>
