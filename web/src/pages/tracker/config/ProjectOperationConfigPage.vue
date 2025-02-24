<template>
    <a-spin :spinning="isShowLoading" tip="Loading...">
    <config-page title="" description="">
        <a-divider style="height: 1px; background-color: #dedede;" />
        <div style="font-size: 14px;;color: #303030;margin-bottom:20px;">基本信息</div>
        <a-form :form="form" layout="vertical">
            <a-form-item label="项目名称">
                <a-input placeholder="请输入项目名称" style="width:30%;margin-right:15px;" v-decorator="[
                    'name',
                    { rules: [{ required: true, validator: nameValidator }] },
                ]" />
                <vxe-button status="primary" content="更新" :disabled="nameDisabled" @click="handleUpdate()"></vxe-button>
                <br>
            </a-form-item>

            <a-form-item label="创建者" v-if="projectData.owner">
                <h-avatar :name="projectData.owner.name" :icon="projectData.owner.icon"></h-avatar>
                <span style="color:#909090">({{ projectData.owner.email}})</span>
            </a-form-item>
            <a-form-item label="创建时间">
                <span>{{ projectData.createDate }}</span>
            </a-form-item>
            <a-divider style="height: 1px; background-color: #dedede;display: inline-block;" />
            <a-form-item label="复制项目" class="aaa">
                <span style="color:#909090">复制当前项目的项目组件、工作项类型、权限配置、迭代配置以及项目配置，可自定义需要复制的项目数据。</span>
                <br><a-button style="margin-top:20px;" type="" @click="onClickCopy()">复制项目</a-button>
            </a-form-item>
            <a-divider style="height: 1px; background-color: #dedede;display: inline-block;" />
            <a-form-item label="保存为模板" class="aaa">
                <span style="color:#909090">保存当前项目的项目组件、工作项类型、权限配置、迭代配置以及项目配置，为模板。</span>
                <br><a-button style="margin-top:20px;" type="" @click="showTemplateDialog=true">保存为模板</a-button>
            </a-form-item>
            <a-divider style="height: 1px; background-color: #dedede;" />
            <a-form-item label="清除所有工作项数据">
                <span style="color:#909090">删除该项目下所有工作项数据，将无法恢复。</span><br>
                <a-button style="margin-top:20px;" type="danger" ghost @click="handleItemDelete()">清除</a-button>
            </a-form-item>
            <a-divider style="height: 1px; background-color: #dedede;" />
            <a-form-item label="删除项目">
                <span style="color:#909090">删除该项目下所有的资源，将无法恢复。</span><br>
                <a-button style="margin-top:20px;" type="danger" ghost @click="handleDelete()">删除</a-button>
            </a-form-item>
        </a-form>

        <a-modal :visible="showTemplateDialog" title="保存为模板" @cancel="showTemplateDialog=false">
            <a-form-model ref="templateForm" :model="formData" :rules="rulesTemplate">
                <a-form-model-item ref="name" label="模板名称" prop="name">
                    <a-input v-model="formData.name" required placeholder="请输入模板名称" @blur="() => {
                        $refs.name.onFieldBlur();
                    }
                        " />
                </a-form-model-item>
                <a-form-model-item ref="name" label="模板描述" prop="description">
                    <a-input v-model="formData.description" required placeholder="请输入模板描述" />
                </a-form-model-item>
            </a-form-model>
            <template slot="footer">
                <a-button key="back" @click="showTemplateDialog=false">
                    取消
                </a-button>
                <a-button key="submit" type="primary" :loading="loading" @click="onClickSaveTemplate()">
                    确定
                </a-button>
            </template>
        </a-modal>

        <a-modal :visible="showDialog" :title="dialogTitle" width="650px" @ok="onOK" @cancel="onCancel">
            <a-form-model ref="statusForm" :model="formData" :rules="rules">
                <a-form-model-item ref="name" label="新项目名称" prop="name">
                    <a-row>
                        <a-col :span="24">
                            <a-input v-model="formData.name" placeholder="请输入新项目名称" @blur="() => {
                                $refs.name.onFieldBlur();
                            }
                                " />
                        </a-col>
                    </a-row>
                    
                </a-form-model-item>
                <a-form-model-item ref="keyName" label="新项目缩写" prop="keyName">
                    <a-row>
                        <a-col :span="24">
                            <a-input v-model="formData.keyName" placeholder="请输入新项目缩写" @blur="() => {
                                $refs.keyName.onFieldBlur();
                            }
                                " />
                        </a-col>
                    </a-row>
                </a-form-model-item>
                <ProjectCopyTool :formData="formData" :trackers="trackers" :trackerIds="trackerIds"></ProjectCopyTool>
            </a-form-model>
        </a-modal>
        <!-- <vxe-modal v-model="isShowLoading" top="50" id='loadMessage' :duration="-1" type="message" :showHeader="false" :showFooter="false" :maskClosable="false">
            <a-icon style="font-size:20px;" type="loading" />
            <span style="margin-left:10px;font-size:18px;color: #202d40;line-height: 24px;font-weight: 600;">Loading...</span>
        </vxe-modal> -->
    </config-page>
    </a-spin>
</template>
<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import { mapState, mapMutations, mapGetters } from 'vuex'
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import ProjectCopyTool from '@/components/tool/ProjectCopyTool.vue';
import { saveAsTemplate } from "@/services/tracker/ProjectTemplateService";
import {
    findOneProject,
    updateProject,
    checkDuplicateProjectName,
    checkDuplicateProjectShortName,
    copyProject,
    deleteProject,
    clearTrackerItemData
} from "@/services/tracker/ProjectService";
import {
    findTrackers,
} from "@/services/tracker/TrackerService";
export default {
    name: "ProjectOperationConfigPage",
    components: { ConfigPage,ProjectCopyTool, HAvatar },
    data() {
        return {
            form: this.$form.createForm(this),
            tableHeight: 400,
            loading: false,
            nameDisabled: true,
            projectName: '',
            projectData: {},
            showDialog: false,
            showTemplateDialog: false,
            editMode: "copy",
            dialogTitle: '复制项目',
            formData: {
                "trackerList": []
            },
            trackers: [],
            trackerIds: [],
            rules: {
                name: [
                    { required: true, message: "请输入项目名称", trigger: "blur" },
                    { required: true, validator: this.nameValidator },
                ],
                keyName:[
                    { required: true, message: "请输入项目缩写", trigger: "blur" },
                    { required: true, validator: this.keyNameValidator },
                ]
            },
            rulesTemplate: {
                name: [
                    { required: true, message: "请输入模板名称", trigger: "blur" },
                ],
            },
            projectId: "",
            isShowLoading:false,
        };
    },
    computed: {
    },
    created() {

    },
    mounted() {
        this.projectId = this.$route.params.projectId
        this.loadData();
    },
    methods: {
        ...mapMutations('project', ["setCurrentProjectId"]),
        loadData() {
            findOneProject(this.projectId).then(resp => {
                console.log("resp", resp)
                this.projectData = resp;
                this.form.setFieldsValue({ name: resp.name });
            })
            findTrackers({ projectId: this.projectId }).then(resp => {
                this.trackers = resp;
                console.log("trackers", this.trackers)

                // this.trackers=this.trackers.map(v=>{return v.name})
                this.trackerIds = this.trackers.map(v => { return v.id })
            })
        },
        nameValidator: function (rule, value, callback) {
            let projectId = this.projectId
            if (!!!value) {
                callback("请输入项目名称");
                return;
            } else if (value == this.projectData.name) {
                this.nameDisabled = true;
            } else {
                this.nameDisabled = false;
            }
            if (this.showDialog) {
                projectId = 0;
            }
            checkDuplicateProjectName(projectId || 0, value)
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
        },
        keyNameValidator:function(rule, value, callback){
            let projectId = this.projectId
            if (!!!value) {
                callback("请输入项目缩写");
                return;
            }         
            checkDuplicateProjectShortName(null, value)
                .then(resp => {
                    if (!resp) {
                        callback();
                    } else {
                        callback(new Error("项目缩写已存在"));
                    }
                })
                .catch(err => {
                    callback(new Error("项目缩写已存在"));
                });
        },
        onClickCopy() {
            this.formData = {
                name: this.projectData.name + "(复制)",
                trackerList: [],
                sprint: true,
                member: true,
                attachment: true,
                report: true,
            };
            this.formData.trackerList = this.trackerIds;
            this.editMode = "copy";
            this.dialogTitle = "复制项目";
            this.showDialog = true;
            // this.$nextTick(() => {
            //     this.$refs["statusForm"].resetFields();
            // });
        },
        onClickSaveTemplate(){
            this.$refs["templateForm"].validate((valid) => {
                if (valid) {
                    this.loading=true;
                    this.formData.id = this.projectId;
                    this.$message.loading({ content: 'Loading...', key: "key", duration: 0 });
                    saveAsTemplate(this.formData).then(res=>{
                        this.$message.success({ content: '保存为模板成功', key: "key" })
                        this.showTemplateDialog=false
                        }).catch((error) => {
                            this.$message.error({ content: '保存为模板失败', key: "key" })
                        }).finally(()=>{
                            this.loading=false;
                        })
                    }
            });
            
           
        },
        onOK() {
            const { editMode } = this;
            const that = this;
            this.$refs["statusForm"].validate((valid) => {
                if (valid) {
                    if ("copy" === editMode) {
                        this.formData.id = this.projectId;
                        // this.$message.loading({ content: 'Loading...', key: "key", duration: 0});
                        this.isShowLoading=true;
                        copyProject(this.formData).then((resp) => {
                            // that.$message.success({ content: '项目复制成功', key: "key" })
                            that.$message.success("项目复制成功")
                            this.$store.dispatch('account/getInfo').then(()=>{
                                this.$router.push({ name: 'projectList'})
                            })
                        }).catch((error) => {
                            // that.$message.error({ content: '项目复制失败', key: "key" })
                        }).finally(()=>{
                            this.isShowLoading=false;
                        })
                    }
                    that.showDialog = false;
                } else {
                }
            });
        },
        handleDelete() {
            const that = this;
            this.$confirm({
                title: '提示',
                content: '确定要删除此项目吗？',
                okText: that.$t('ok'), //确认
                okType: 'danger',
                cancelText: that.$t('cancel'), //取消
                onOk() {
                    deleteProject(that.projectId).then(res => {
                        that.$message.success("删除成功");
                        that.setCurrentProjectId(null);
                        // that.$router.push("/welcome");
                        that.$router.push({
                            name: 'projectList'
                        })

                    })
                },
                onCancel() {
                }
            })
        },
        handleItemDelete(){
            const that = this;
            this.$confirm({
                title: '提示',
                content: '确定清除所有工作项数据吗？',
                okText: that.$t('ok'), //确认
                okType: 'danger',
                cancelText: that.$t('cancel'), //取消
                onOk() {
                    clearTrackerItemData(that.projectId).then(res => {
                        that.$message.success("清除成功");
                    })
                },
                onCancel() {
                }
            })
        },
        handleUpdate() {
            this.form.validateFields((err, values) => {
                this.projectData.name = values.name
                updateProject(this.projectData).then(res => {
                    this.$message.success("更新成功！");
                }).catch((error) => {
                    this.$message.success("更新失败！");
                })
            });

        },
        onCancel() {
            this.showDialog = false;
        },
    },
};
</script>
<style lang="less" scoped>
:deep(.ant-row) {
    margin-bottom: 8px;
}

:deep(.ant-divider-horizontal) {
    margin-top: 12px;
    margin-bottom: 12px;
}
</style>
