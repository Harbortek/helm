<template>
    <config-page title="" description="项目成员用于配置项目下需要参与的角色，以及各个角色的成员组成，针对不同角色可以进行页面权限设置。">
        <a-row>
            <div class="toolbar">
                <vxe-toolbar size="medium">
                    <template #buttons>
                        <!-- <div>已选 <span class="user-head-count">(共有{{
                            selectedRole.members ? selectedRole.members.length : '' }}人)</span></div> -->
                        <vxe-input v-model="keyword" :placeholder="'搜索' + selectedRole.name" style="width:240px"
                            type="search" clearable></vxe-input>
                    </template>
                    <template #tools>
                        <a-space v-if="tabValue == 'member'">
                            <vxe-button status="text" content="添加角色" @click="onCreateProjectRole"></vxe-button>
                            <vxe-button status="primary" content="添加成员" @click="onSelectUsers"></vxe-button>
                            <a-icon type="delete" @click="onDeleteUsers" />
                        </a-space>
                        <a-space v-else-if="hasChange">
                            <vxe-button status="primary" content="保存" @click="onSavePagePerm"></vxe-button>
                            <vxe-button status="text" content="取消" @click="onCancelSave"></vxe-button>
                        </a-space>
                    </template>
                </vxe-toolbar>
            </div>
        </a-row>
        <a-row :gutter="16" style="border: 1px solid #dedede;margin-top:20px;height:90%">
            <a-col :span="5" style="padding-left: 0;padding-right: 0;height: 100%;border-right: 1px solid #dedede;">
                <div>
                    <selectable-list-border style="padding-left: 0px;padding-right: 0;" :items="projectRoles"
                        :selected-item="selectedRole" @change="onRoleChange">
                        <template #footer="{ row }">
                            <vxe-button type="text" @click="onEditProjectRole(row)"><a-icon type="edit" /></vxe-button>
                            <vxe-button type="text" style="margin-left:0;" @click="onDeleteProjectRole(row.id)"><a-icon
                                    type="delete" /></vxe-button>
                        </template>
                    </selectable-list-border>

                </div>
            </a-col>
            <a-tabs v-model="tabValue" @change="onChangeTabs" :animated="false" style="height:100%;">
                <a-tab-pane key="member" tab="项目成员">
                    <vxe-table :data="getSelectedRoleMembers()" height="auto" :loading="loading"
                        :row-config="{ isHover: true }">
                        <vxe-column type="seq" width="60"></vxe-column>
                        <vxe-column field="name" title="姓名">
                            <template #default="{ row }">
                                <h-avatar v-if="!loading" :name="row.name" :icon="row.icon"></h-avatar>
                            </template>

                        </vxe-column>
                        <vxe-column field="email" title="邮件"></vxe-column>
                        <vxe-column title="操作" width="100" show-overflow>

                            <template #default="{ row }">
                                <vxe-button style="font-size: 15px;" title="移除" type="text" icon="vxe-icon-close"
                                    @click="onDeleteEvent(row)"></vxe-button>
                            </template>
                        </vxe-column>
                    </vxe-table>
                </a-tab-pane>
                <a-tab-pane key="permission" tab="页面权限">
                    <vxe-table v-if="!loading" :data="selectedRole.permissions" height="auto" :loading="loading"
                        :row-config="{ isHover: true }" :tree-config="{
                            transform: true, expandAll: true,//accordion: true, line: true,iconOpen: 'vxe-icon-square-minus', iconClose: 'vxe-icon-square-plus',
                            rowField: 'id', parentField: 'parentId'
                        }">
                        <vxe-column type="seq" width="60"></vxe-column>
                        <vxe-column field="name" title="页面名称" tree-node>
                            <!-- <vxe-column field="name" title="页面名称" tree-node>
                        <template #header="{ column }">
                            <a><i class="vxe-icon-caret-right"></i></a>
                            <span>{{ column.title }}</span>
                        </template> -->
                        </vxe-column>
                        <vxe-column title="权限">
                            <template #default="{ row }">
                                <vxe-checkbox-group v-if="row.parentId || row.children==0" v-model="row.permission"
                                    @change="onChangePermission(row)">
                                    <vxe-checkbox v-for="item in perms" :key="item" :label="item"
                                        :content="$t('project.page.permission.' + (item))"></vxe-checkbox>
                                </vxe-checkbox-group>
                                <!-- <vxe-checkbox-group v-else v-model="row.permission" @change="onChangePermission(row)">
                                <vxe-checkbox v-for="item in perms" :key="item" :label="item" style="margin-right: 28px;" ></vxe-checkbox>
                            </vxe-checkbox-group> -->
                            </template>
                        </vxe-column>
                    </vxe-table>
                </a-tab-pane>
            </a-tabs>
        </a-row>
        <user-select-modal :initalData="selectedRole.members" ref="userSelectModal"
            @userSelector="userSelector"></user-select-modal>

        <a-modal :visible="showDialog" :title="dialogTitle" @ok="onOK" @cancel="showDialog = false">
            <a-form-model ref="projectRoleForm" :model="formData" :rules="rules" layout="horizontal">
                <a-form-model-item ref="name" label="角色名称" prop="name">
                    <a-input v-model="formData.name" @blur="() => {
                        $refs.name.onFieldBlur();
                    }
                        " />
                </a-form-model-item>
            </a-form-model>
        </a-modal>


    </config-page>
</template>
<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import SelectableListBorder from '@/components/list/SelectableListBorder.vue';
import UserSelectModal from '@/components/dialog/UserSelectModal';
import { findProjectRoleMembers, updateProjectRoleMember, deleteProjectRoleMember } from "@/services/tracker/ProjectRoleMemberService";
import { createProjectRole, updateProjectRole, deleteProjectRole } from "@/services/tracker/ProjectRoleService";
import { batchUpdatePagePermission, findPagePermissionPerm } from "@/services/tracker/PagePermissionService";

export default {
    name: "ProjectRoleConfigPage",
    components: { ConfigPage, SelectableListBorder, UserSelectModal, HAvatar },
    data() {
        const nameValidator = (rule, value, callback) => {
            let found = false;
            for (let i = 0; i < this.projectRoles.length; i++) {
                if (this.projectRoles[i].name === value) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return callback();
            } else {
                return callback(new Error("已存在同样名称的数据"));
            }
        };
        return {

            tableHeight: 400,
            loading: false,
            keyword: '',
            projectId: '',
            projectRoles: [],
            hasChange: false,
            changePermList: [],
            selectedRole: {},
            selectedPerm: [],
            formData: {},
            editMode: "create",
            showDialog: false,
            dialogTitle: "新增项目分类",
            tabValue: 'member',
            copySelectedPerm: [],
            perms: [],
            rules: {
                name: [
                    { required: true, message: "请输出角色名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "角色名称长度在2到20之间",
                        trigger: "blur",
                    },
                    { validator: nameValidator, trigger: "blur" },
                ],
            },
        };
    },
    computed: {
    },
    created() {
        this.projectId = this.$route.params.projectId
        this.loadData();
    },
    mounted() {

    },
    methods: {
        //页面权限
        onSavePagePerm() {
            console.log("save", this.changePermList)
            batchUpdatePagePermission(this.changePermList).then(res => {
                this.$message.success('保存成功！');
                this.hasChange = false;
            }).finally(() => {
                // window.location.reload();
            })
        },
        onCancelSave() {
            this.hasChange = false;
            this.loadData()
        },
        onChangePermission(row) {
            this.hasChange = true;
            let flag = true;
            for (let item of this.changePermList) {
                if (item.id == row.id) {
                    flag = false;
                    break;
                }
            }
            console.log("?",row)

            if (flag) {
                row.projectId = this.projectId;
                this.changePermList.push(row);
            }
        },
        onChangeTabs(e) {

        },
        //项目成员
        loadData() {
            this.loading = true;
            let projectId = this.projectId
            findProjectRoleMembers({ projectId }).then(resp => {
                this.projectRoles = resp;
                if (this.projectRoles.length > 0) {
                    this.selectedRole = this.projectRoles[0]
                }
            }).finally(() => {
                this.loading = false;
            })
            findPagePermissionPerm().then(res => {//权限
                this.perms = res;
            })
        },
        onDeleteProjectRole(id) {
            const that = this;
            this.$confirm({
                title: this.$t('system.role.remind.delete.title'),//提示
                content: this.$t('system.enum.remind.delete.content'),//确定删除吗？
                okText: this.$t('ok'),
                okType: 'danger',
                cancelText: this.$t('cancel'),
                onOk() {
                    deleteProjectRole(id).then(res => {
                        that.loading = false;
                        that.$message.success(that.$t('system.enum.remind.delete.success')); //删除成功！
                        that.loadData();
                    })
                },
                onCancel() {
                }
            })
        },
        onEditProjectRole(row) {
            console.log("row", row)
            this.formData = Object.assign({}, row)
            this.editMode = "edit";
            this.dialogTitle = "修改项目角色";
            this.showDialog = true;
            // this.$nextTick(() => {
            //     this.$refs["projectRoleForm"].resetFields();
            // });
        },
        onCreateProjectRole() {
            this.formData = { name: "" };
            this.editMode = "create";
            this.dialogTitle = "新增项目角色";
            this.showDialog = true;
            this.$nextTick(() => {
                this.$refs["projectRoleForm"].resetFields();
            });
        },
        onOK() {
            const { editMode, formData } = this;
            const that = this;
            this.$refs["projectRoleForm"].validate((valid) => {
                if (valid) {
                    if ("create" === editMode) {
                        formData.projectId = this.projectId
                        createProjectRole(formData).then((resp) => {
                            this.$message.success("添加成功")
                            this.loadData();
                        });
                    } else {
                        formData.projectId = this.projectId
                        updateProjectRole(formData).then((resp) => {
                            this.$message.success("添加成功")
                            this.loadData();
                        });
                    }
                    that.showDialog = false;
                } else {
                }
            });
        },
        getSelectedRoleMembers() {
            if (this.selectedRole.members && this.keyword) {
                var regexp = new RegExp(this.keyword)
                return this.selectedRole.members.filter(v => { return regexp.test(v.name) });
            } else {
                return this.selectedRole.members;
            }
        },
        onRoleChange(item) {
            this.selectedRole = item
            this.loading = true;//解决文字头像显示问题
            this.$nextTick(() => {
                this.loading = false;
            })
        },
        onSelectUsers() {
            this.$refs.userSelectModal.view();
        },
        onDeleteEvent(row) {
            this.selectedRole.members = this.selectedRole.members.filter(function (item) {
                return item.id != row.id
            })
            this.deleteProjectRoleMember([row.id]);
        },
        onDeleteUsers() {
            const that = this
            this.$confirm({
                title: '提示',
                content: '确认删除所有已选用户？',
                okType: 'danger',
                onOk() {
                    that.selectedRole.members = []
                    that.deleteProjectRoleMember(that.selectedRole.members.map(_ => _.id));
                },
            });
        },
        deleteProjectRoleMember(ids) {
            let memberRoles = []
            ids.forEach(element => {
                memberRoles.push({ id: element })
            });
            deleteProjectRoleMember({ projectId: this.projectId, roleId: this.selectedRole.id }, memberRoles).then(res => {
                this.$message.success("移除成功")
            })

        },
        userSelector(selectedRows) {
            this.selectedRole.members.push(...selectedRows)
            updateProjectRoleMember({ projectId: this.projectId, roleId: this.selectedRole.id }, selectedRows).then(resp => {
                this.$message.success("更新成功")
            }).finally(() => {

            })
        },
    },
};
</script>
<style lang="less" scoped>
::v-deep .ant-tabs .ant-tabs-top-content,
.ant-tabs .ant-tabs-bottom-content {
    height: calc(100% - 50px);
}

::v-deep .ant-tabs .ant-tabs-top-content>.ant-tabs-tabpane,
.ant-tabs .ant-tabs-bottom-content>.ant-tabs-tabpane {
    height: 100%;
}

::v-deep .no-flex>.ant-tabs-content>.ant-tabs-tabpane-inactive,
.ant-tabs-no-animation>.ant-tabs-content>.ant-tabs-tabpane-inactive {
    height: 0;
}
</style>