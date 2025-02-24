<template>
    <div class="main">
        <div class="toolbar">
            <vxe-toolbar size="medium">
                <template #buttons>
                    <vxe-button v-action="'PROJECT_CREATE'" status="primary" content="新建项目" @click="onCreateProject"></vxe-button>
                </template>
                <template #tools>
                    <a-space>
                        <div v-show="viewId && isShowSaveButton" v-action="'PROJECT_LIST'"
                            style="height:26px;align-items: center;background: #edeff2;border-radius: 13px;color: #575859;">
                            <vxe-button @click="onClickSaveView" style="margin:0;border-right: 1px solid #d4d6d9;"
                                type="text" :content="'保存'"></vxe-button>
                            <vxe-button @click="onClickSaveAsView" style="margin:0;border-right: 1px solid #d4d6d9"
                                type="text" :content="'另存为'"></vxe-button>
                            <vxe-button @click="onClickResetView" style="margin:0;" type="text"
                                :content="'还原'"></vxe-button>
                        </div>

                        分组
                        <a-select :value="groupBy" style="width: 120px" @change="onChangeGroupBy">
                            <a-select-option value="">不分组</a-select-option>
                            <a-select-option value="name">项目名称</a-select-option>
                            <a-select-option value="category">项目分类</a-select-option>
                            <a-select-option value="status">项目状态</a-select-option>
                            <a-select-option value="owner">项目负责人</a-select-option>
                            <a-select-option value="createDate">创建时间</a-select-option>
                        </a-select>
                        排序
                        <a-select ref="orderBy" optionLabelProp="label" :value="orderBy" style="width: 120px">

                            <a-select-option v-for="item in tracker.trackerFields" :key="item.id"
                                :value="item.systemProperty" :label="item.name" @click="onChangeOrderBy">{{ item.name }}
                                <template v-if="item.systemProperty == orderBy">
                                    <a-icon v-if="orderByType == 'ASC'" type="arrow-up"
                                        style="float:right;margin-top:5px;color:#338fe5" />
                                    <a-icon v-else type="arrow-down" style="float:right;margin-top:5px;color:#338fe5" />
                                </template>
                            </a-select-option>
                        </a-select>

                        布局
                        <a-select :value="layout" style="width: 120px" @change="onChangeLayout">
                            <a-select-option value="card">卡片</a-select-option>
                            <a-select-option value="table">表格</a-select-option>
                        </a-select>
                        <a-badge :number-style="{ backgroundColor: '#338fe5' }" :count="getFilterCount()">
                            <a-button type="text" @click="onClickFilter">筛选<a-icon :component="filterIcon" /></a-button>
                        </a-badge>

                        <vxe-input v-model="keyword" placeholder="搜索项目关键字" type="search" className="searchbox" clearable
                            @search-click="onSearch()"></vxe-input>
                    </a-space>

                </template>
            </vxe-toolbar>
        </div>
        <tracker-item-filter style="transition: all .25s;"
            :class="activeKey == 'true' ? 'filter-flex-active' : 'filter-flex'" :members="filterMembers" :tracker="tracker"
            :conditionGroups="conditionGroups" :activeKey="activeKey" @refresh="onRefreshFilter"></tracker-item-filter>
        <a-layout style="flex:5 1 65%;">
            <a-layout-sider v-if="groupBy == ''" theme="light" style="height:100%">
                <vxe-table ref="categoryRef" :data="categories"
                    :row-config="{ isHover: true, isCurrent: true, keyField: 'id' }" @current-change="onCategoryChange">
                    <vxe-column field="name" title="项目分类"></vxe-column>
                </vxe-table>
            </a-layout-sider>
            <a-layout-sider v-else theme="light" style="height:100%;border-right:2px solid #e8e8e8">
                <div style="height:100%;display:flex;flex-direction: column;">
                    <div style="border-bottom:1px solid #e8e8e8;padding:10px;"><a-input-search v-model="groupSearch"
                            placeholder="搜索" /></div>
                    <a-menu style="width: 100%;padding:10px;flex:1" v-model="groupSelect" mode="vertical">
                        <a-menu-item v-for="item in groupMenuFilter" :key="item.name">
                            {{ item.object ? item[groupBy].name : item[groupBy] }} ({{ item.count }})
                        </a-menu-item>
                    </a-menu>
                    <a-button @click="groupBy = ''" style="width: 95%;position: absolute;bottom: 0px;"
                        icon="file-excel">关闭分组</a-button>
                </div>
            </a-layout-sider>
            <a-layout-content style="margin-left: 5px;height:100%;">
                <div class="card-layout" v-show="layout === 'card'">
                    <project-block :loading="loading" :key="p.id" v-for="p in getProjectList" :project="p"
                        @click.native="onOpenProject(p)" />
                    <!-- <a-card :loading="loading" :key="p.id" v-for="p in getProjectList" hoverable bordered
                        style="width: 260px;margin-left: 15px;margin-bottom: 15px;" @click="onOpenProject(p)">
                        <a-row type="flex">
                            <a-col flex="80px" class="card-project-description">{{ p.keyName }}</a-col>
                            <a-col flex="auto" style="float: right;">数据更新于1天前</a-col>
                        </a-row>
                        <a-row type="flex">
                            <a-col flex="auto">
                                <div class="card-project-title"> {{ p.name }}</div>
                            </a-col>
                        </a-row>
                        <a-row>
                            <div ref="dailyTrend"></div>
                        </a-row>
                        <a-row type="flex">
                            <a-col flex="auto">
                                <p>所有者: {{ p.owner.name }}</p>
                            </a-col>
                            <a-col flex="18px"><a-icon type="dash" /></a-col>
                        </a-row>
                    </a-card> -->
                    <div v-if="projects.empty" class="empty-data">
                        <div class="ant-spin-container">
                            <div class="ant-list-empty-text">
                                <a-empty v-if="projects.empty" />
                            </div>
                        </div>
                    </div>

                </div>

                <div class="table-layout" style="height: calc((100% - 50px));" v-show="layout === 'table'">
                    <vxe-table :data="getProjectList" height="auto" :loading="loading" :row-config="{ isHover: true }">
                        <vxe-column type="seq" width="60"></vxe-column>
                        <vxe-column field="name" title="项目名称">
                            <template #default="{ row }">
                                <vxe-button type="text" @click="onOpenProject(row)">{{ row.name }}</vxe-button>
                            </template>
                        </vxe-column>
                        <vxe-column field="status" title="项目状态">
                            <template #default="{ row }">
                                <a-tag style="border-radius: 10px;font-size: 13px;"
                                    :style="{ color: row.status?.color, borderColor: row.status?.color }">
                                    {{ row.status?.name }}
                                </a-tag>
                            </template>
                        </vxe-column>
                        <vxe-column field="category.name" title="项目分类"></vxe-column>
                        <vxe-column field="owner.name" title="项目负责人">
                            <template #default="{ row }">
                                <h-avatar :name="row.owner.name" :icon="row.owner.icon"></h-avatar>
                            </template>
                        </vxe-column>
                        <vxe-column field="createBy.name" title="项目创建者">
                            <template #default="{ row }">
                                <h-avatar :name="row.createBy.name" :icon="row.createBy.icon"></h-avatar>
                            </template>
                        </vxe-column>
                        <vxe-column field="createDate" title="项目创建日期"></vxe-column>
                        <vxe-column title="操作" width="100" show-overflow>
                            <template #default="{ row }">
                                <vxe-button v-if="projectAdminIds?.includes(row.id)" type="text" icon="vxe-icon-edit" @click="onEditEvent(row)"></vxe-button>
                                <!-- <vxe-button type="text" icon="vxe-icon-delete" @click="onDeleteEvent(row)"></vxe-button> -->
                            </template>
                        </vxe-column>
                    </vxe-table>
                    <vxe-pager background :current-page.sync="pagination.current" :page-size.sync="pagination.pageSize"
                        :total="pagination.total" @page-change="handlePageChange"
                        :layouts="['PrevJump', 'PrevPage', 'JumpNumber', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']">
                    </vxe-pager>
                </div>
            </a-layout-content>
        </a-layout>
        <a-modal :visible="showDialog" :title="dialogTitle" @ok="onOK" @cancel="onCancel">
            <a-form-model ref="xForm" :model="formData" :rules="rules" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
                <a-form-model-item ref="name" label="项目名称" prop="name">
                    <a-input v-model="formData.name" placeholder="请输入状态名称" />
                </a-form-model-item>
                <a-form-model-item ref="status.id" label="项目状态" prop="status.id" required>
                    <ProjectStatusSelect v-model="formData.status.id" />
                </a-form-model-item>
                <a-form-model-item ref="owner" label="项目负责人" prop="owner" required>
                    <a-select ref="select" v-model="formData.owner.id" @change="onChangeSelect">
                        <a-select-option v-for="item in members" :key="item.id" :title="item.name" :value="item.id">
                            <h-avatar :name="item.name" :icon="item.icon"></h-avatar>
                            <span class="domain-list-cell-subtext"> {{ item.description }} </span>
                        </a-select-option>
                    </a-select>
                </a-form-model-item>

            </a-form-model>
        </a-modal>
        <create-view-dialog :isShowDialog="isShowCreateDialog" :editMode="'saveAs'" :view="currentView" :objectId="''"
            @ok="onCreateViewDialogOK" @cancel="isShowCreateDialog = false" />
    </div>
</template>
<script>
import VXETable from "vxe-table";
import HAvatar from '@/components/avatar/h-avatar.vue';
import {
    findEnumsByCode,
} from "@/services/system/EnumService";
import { getUsers } from '@/services/system/UserService'
import {
    findProjects, findProjectCategories, updateProject, findOneProject,recordRecentProject
} from "@/services/tracker/ProjectService";
import { mapState, mapMutations, mapGetters } from "vuex";
import {
    findProjectRolesAndMembers
} from "@/services/tracker/ProjectRoleMemberService";
import { createView, findOneView, updateView } from "@/services/tracker/ViewService"
import Vue from "vue";
import CreateViewDialog from "@/pages/tracker/items/CreateViewDialog.vue";
import TrackerItemFilter from '@/components/tool/TrackerItemFilter'
import ProjectBlock from "./ProjectBlock.vue";
import ProjectStatusSelect from '@/components/select/ProjectStatusSelect.vue';
import PAGE_COMPONENTS from '@/components/menu/menu-with-project'
import { findProjectIdsByPermission } from "@/services/tracker/ProjectPermissionService"

export default {
    name: "ProjectList",
    components: {
        TrackerItemFilter, ProjectBlock, ProjectStatusSelect, CreateViewDialog,HAvatar
    },
    props: ['views', "viewId"],
    data() {
        const that = this;
        const nameValidator = (rule, value, callback) => {
            let found = false;
            let i = 0;
            for (i = 0; i < that.projects.content.length; i++) {
                if (that.projects.content[i].name === value) {
                    if (that.editMode == "edit" && that.projects.content[i].id == this.formData.id) {
                        continue;
                    }
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
            activeTab: 'ALL',
            orderBy: 'name',
            orderByType: 'DESC',
            groupBy: '',
            groupMenu: [],
            layout: 'card',
            keyword: "",
            loading: false,
            categories: [],
            currentCategoryId: '',
            projects: [],
            pagination: {
                current: 1,
                pageSize: 10,
            },
            showDialog: false,
            dialogTitle: "编辑项目",
            formData: {
                status: {},
                owner: {},
            },
            members: [],
            rules: {
                name: [
                    { required: true, message: "请输入项目名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "项目名称长度在2到20之间",
                        trigger: "blur",
                    },
                    { validator: nameValidator, trigger: ["change", "blur"] },
                ],
            },
            tracker: {
                trackerFields: [
                    { id: 1, name: "项目名称", systemProperty: "name", inputType: "TEXT" },
                    { id: 2, name: "项目分类", systemProperty: "categoryId", inputType: "OPTIONS" },
                    { id: 3, name: "项目状态", systemProperty: "statusId", inputType: "OPTIONS" },
                    { id: 4, name: "创建者", systemProperty: "createBy", inputType: "USER" },
                    { id: 5, name: "创建日期", systemProperty: "createDate", inputType: "DATE" },
                    { id: 6, name: "修改者", systemProperty: "lastModifiedBy", inputType: "USER" },
                    { id: 7, name: "修改日期", systemProperty: "lastModifiedDate", inputType: "DATE" },
                ],
                trackerStatuses: [],
            },
            groupSelect: [''],
            groupSearch: '',
            activeKey: '',
            conditionGroups: [{
                conditions: []
            }],
            filterIcon: "expandDown",
            filterMembers: [],
            currentView: {
                viewConfig: {
                    orderBys: []
                }
            },
            status: '',
            isShowSaveButton: false,
            isShowCreateDialog: false,
            projectAdminIds:[],
        };
    },
    computed: {
        groupMenuFilter() {
            var regexp = new RegExp(this.groupSearch)
            return this.groupMenu.filter(v => {
                if (v.object) {
                    return regexp.test(v[this.groupBy].name)
                } else {
                    return regexp.test(v[this.groupBy])
                }
            });
        },
        getProjectList() {
            var projectList = [];
            if (this.projects.content) {
                projectList = this.projects.content.filter(v => { return (new RegExp(this.keyword)).test(v.name) });
                if (this.groupBy != ''&&this.groupMenu?.length>0) {
                    console.log("getproejctList",this.groupMenu)
                    projectList = projectList.filter(item => {
                        if ((typeof this.groupMenu[0][this.groupBy]) == "object") {
                            return this.groupSelect[0] == item[this.groupBy].id
                        } else {
                            return this.groupSelect[0] == item[this.groupBy]
                        }
                    })
                }
            }
            return projectList;
        },
    },
    watch: {
        viewId() {
            this.loadViewData();
        }
    },
    mounted() {
        // this.loadData();
        this.initFilterData();
        this.loadPermissions();
    },
    methods: {
        ...mapMutations('project', ["setCurrentProjectId","setCurrentProjectKeyName"]),

        loadViewData() {
            if (this.viewId) {
                this.loading = true;
                this.isShowSaveButton=false
                this.groupBy=''
                this.projects=[]
                findOneView(this.viewId).then(resp => {
                    this.currentView = resp
                    console.log("view", this.currentView)
                    if (this.currentView.viewConfig) {
                        if (this.currentView.viewConfig.filter?.conditionGroups) {
                            this.conditionGroups = Object.assign([], this.currentView.viewConfig.filter.conditionGroups)
                        }
                        if (this.currentView.viewConfig.tableConfig) {
                            this.layout = this.currentView.viewConfig.tableConfig.layout || 'card'
                            this.groupBy = this.currentView.viewConfig.tableConfig.groupBy || ''
                        } else {
                            this.currentView.viewConfig.tableConfig = {}
                            this.layout = 'table'
                            this.groupBy = ''
                        }
                        if(this.currentView.viewConfig?.orderBys?.length>0){
                            this.orderBy=this.currentView.viewConfig.orderBys[0].field.name
                            this.orderByType=this.currentView.viewConfig.orderBys[0].orderBy
                        } else{
                            this.orderBy='name'
                            this.orderByType ='DESC'
                            this.currentView.viewConfig.orderBys = Vue.observable([]);
                        }
                    } else {
                        this.currentView.viewConfig = Vue.observable({
                            type: "TRACKER_VIEW_CONFIG",
                            tableConfig: {},
                            orderBys: [],
                            filter: {},
                        })
                        this.layout = 'card'
                        this.groupBy = ''
                        this.conditionGroups = Vue.observable([{ conditions: [] }])
                        this.currentView.viewConfig.orderBys = Vue.observable([]);
                        this.orderBy='name'
                        this.orderByType ='DESC'
                    }
                }).finally(() => {
                    this.loadData(this.conditionGroups)
                })
            } else {
                this.loadData(this.conditionGroups)
            }
        },
        loadPermissions() {
            findProjectIdsByPermission("PROJECT_ADMIN").then(res=>{
                this.projectAdminIds = res;
            })
        },
        onRefreshFilter() {
            if (this.conditionGroups) {
                this.currentView.viewConfig.filter = { "conditionGroups": this.conditionGroups }
            }
            this.isShowSaveButton = true;
            this.refresh();
        },
        onClickResetView() {
            this.isShowSaveButton = false;
            this.loadViewData();
        },
        onClickSaveAsView() {
            this.currentView.viewConfig.tableConfig.layout = this.layout
            this.currentView.viewConfig.tableConfig.groupBy = this.groupBy
            this.isShowCreateDialog = true;
        },
        onClickSaveView() {
            console.log("this", this.currentView)
            this.currentView.viewConfig.tableConfig.layout = this.layout
            this.currentView.viewConfig.tableConfig.groupBy = this.groupBy

            if (this.currentView.viewType == 'PUBLIC') {
                VXETable.modal.confirm({
                    title: '保存公共视图',
                    content: '公共视图显示条件将会被更改，有该视图查看权限的成员可以查看。是否保存？'
                }).then(type => {
                    if (type === 'confirm') {
                        if (this.currentView?.id) {
                            updateView(this.currentView).then(res => {
                                VXETable.modal.message({ content: '更新成功', status: 'success' })
                            })
                        }
                    }
                    this.isShowSaveButton = false;
                })
            } else {
                if (this.currentView?.id) {
                    updateView(this.currentView).then(res => {
                        VXETable.modal.message({ content: '更新成功', status: 'success' })
                    })
                    this.isShowSaveButton = false;
                }
            }
        },
        //创建工作项
        onCreateViewDialogOK(view) {
            view.system = false
            createView(view).then(resp => {
                this.isShowCreateDialog = false
                this.$emit("refresh", resp)
                this.isShowSaveButton = false;
            })
        },
        handlePageChange(pagination) {
            this.pagination.current = pagination.currentPage
            this.pagination.pageSize = pagination.pageSize
            this.loadData(this.conditionGroups)
        },
        getFilterCount() {
            let count = 0;
            this.conditionGroups.forEach(item => {
                item.conditions.forEach(item2 => {
                    if (item2 && item2.value) {
                        count++;
                    }
                })
            })
            return count;

        },
        onClickFilter() {
            const that = this;
            if (this.activeKey == 'true') {
                this.activeKey = 'false'
                this.filterIcon = "expandDown"
            } else {
                this.activeKey = 'true'
                this.filterIcon = 'collapseUp'
            }
        },
        refresh() {
            this.pagination.current = 1
            this.loadData(this.conditionGroups)
        },
        onChangeSelect(value) {
            // for(let item of this.projects.content){
            //     if(item.meaning==value){
            //         this.formData.color=item.color
            //     }
            // }
            console.log("onChangeSelect", this.formData)
        },
        onOK() {
            this.$refs["xForm"].validate((valid) => {
                if (valid) {
                    this.showDialog = false;
                    updateProject(this.formData).then(res => {
                        this.$message.success("更新成功！");
                        this.refresh();
                    }).catch((error) => {
                        this.$message.success("更新失败！");
                    })
                }
            })
        },
        onCancel() {
            this.showDialog = false;
        },
        onEditEvent(row) {
            console.log("row", row);
            this.editMode = "edit";
            this.dialogTitle = "编辑项目";
            this.showDialog = true;
            this.$nextTick(() => {
                this.$refs["xForm"].resetFields();
                this.formData = JSON.parse(JSON.stringify(row));
                // this.formData = { id: row.id, name: row.name,status:row.status.id,owner:row.owner.id };
                findProjectRolesAndMembers(row.id, "").then(resp => {
                    this.members = resp.members;
                });
            });
        },
        onDeleteEvent(row) {
            const { id } = row;
            const that = this;
            this.$confirm({
                title: "您确定要删除该数据?",
                okText: this.$t("ok"),
                okType: "danger",
                cancelText: this.$t("cancel"),
                onOk() {
                    // deleteProjectStatus(id).then((resp) => {
                    //     that.loadData();
                    // });
                },
                onCancel() { },
            });
        },
        onChangeOrderBy(value) {
            if (this.orderBy == value.key && this.orderByType == "ASC") {
                this.orderByType = "DESC"
            } else {
                this.orderByType = "ASC"
            }
            this.isShowSaveButton = true;
            this.orderBy = value.key
            console.log("value",value)
            this.currentView.viewConfig.orderBys = Vue.observable([{field:{name:value.key},orderBy:this.orderByType}]);
            this.loadData(this.conditionGroups)

        },
        onChangeGroupBy(value,isFirst) {
            console.log("value",isFirst)
            if(isFirst!=true&&this.groupBy!=value){
                this.isShowSaveButton = true;
            }
            this.groupBy = value
            if (value == "") {
                return;
            }
            this.groupMenu = []
            this.projects?.content?.forEach(item => {
                let flag = true;
                for (let item2 of this.groupMenu) {
                    if ((typeof item[value]) == "object") {
                        if (item[value].id == item2[value].id) {
                            item2.count++;
                            flag = false;
                            break;
                        }
                    } else {
                        if (item[value] == item2[value]) {
                            item.count++;
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag) {
                    let menu = { "count": 1 };
                    if ((typeof item[value]) == "object") {
                        menu.object = true;
                        menu.name = item[value].id
                    } else {
                        menu.name = item[value]
                    }
                    menu[value] = item[value];
                    if (menu[value].status) {
                        menu[value].name = this.$t('project.status.' + menu[value].status);
                    }
                    this.groupMenu.push(menu)
                }
            })
            if(this.groupMenu?.length>0){
                if ((typeof this.groupMenu[0][value]) == "object") {
                    this.groupSelect[0] = this.groupMenu[0][value].id
                } else {
                    this.groupSelect[0] = this.groupMenu[0][value]
                }
            }
            

        },
        onChangeLayout(value) {
            this.isShowSaveButton = true;
            this.layout = value
            this.loadData(this.conditionGroups);
        },
        initFilterData(){
            findEnumsByCode('PROJECT_STATUS_MEANING').then((resp) => {
                this.tracker.statusId = Vue.observable([]);
                resp.forEach(item => {
                    this.tracker.statusId.push({ id: item.id, name: item.name })
                });
            });
            getUsers().then(res => {
                this.filterMembers = res.content;
                this.filterMembers.forEach(item => {
                    item.description = item.email
                })
            })
        },  
        loadData(conditionGroups) {
            this.loading = true;
            let pageable = {
                page: this.pagination.current - 1,
                size: this.pagination.pageSize,
            }
            if (this.layout == 'card') {
                pageable.size = 1000
            }
            findProjectCategories().then((resp) => {
                this.categories = resp;
                this.tracker.categoryId = resp;
                findProjects({
                    categoryId: this.currentCategoryId, keyword: this.keyword, status: this.status,
                    sort: this.orderBy + "," + this.orderByType, page: pageable.page, size: pageable.size
                }, { conditionGroups },).then(resp => {
                    this.projects = resp;
                    console.log("projects",resp,this.loading)
                    this.pagination.total = parseInt(resp.totalElements);
                }).finally(() => {
                    this.loading = false;
                    if(this.groupBy){
                        this.onChangeGroupBy(this.groupBy,true);
                    }
                })
            }).catch(() =>{
                this.loading = false;
            })
        },
        onSearch() {
            this.loadData();
        },
        onCategoryChange(table) {
            if (this.currentCategoryId == table.newValue.id) {
                this.currentCategoryId = '';
                this.$refs.categoryRef.clearCurrentRow();
            } else {
                this.currentCategoryId = table.newValue.id
            }
            this.loadData()
        },
        onCreateProject() {
            this.$router.push({ name: 'createProject' })
        },
        getComponentPagePath(page, projectId) {
            const comp_page = PAGE_COMPONENTS.filter(item => item.id === page.componentType)[0]
            if (comp_page) {
                return comp_page.path(page, projectId)
            }
        },
        getTrackerPagePath(page, projectId) {
            const trackerId = page.tracker?.id
            return `/tracker/project/${projectId}/trackerItems/${trackerId}`
        },
        onOpenProject(p) {
            this.setCurrentProjectId(p.id);
            this.loading = true;
            this.$store.dispatch('account/getInfo').then(() => {
                findOneProject(p.id).then(resp => {
                    const projectId = p.id
                    this.setCurrentProjectKeyName(p.keyName)
                    //构建项目主体菜单
                    let pages = resp.pages;
                    let index = 1;
                    let menuItem = {}
                    if (pages && pages.length > 0) {
                        for (let j = 0; j < 1; j++) {
                            const page = pages[j]
                            if (page.folder) {
                                for (let k = 0; k < page.children.length; k++) {
                                    const childPage = page.children[k]
                                    const pageId = childPage.id
                                    if (childPage.type === 'wiki') {
                                        menuItem = { id: index++, name: childPage.name, path: `/tracker/project/${projectId}/wiki/${pageId}` }
                                    } else if (childPage.type === 'report') {
                                        menuItem = { id: index++, name: childPage.name, path: `/tracker/project/${projectId}/smartPage/${pageId}` }
                                    } else if (childPage.type === 'component') {
                                        menuItem = { id: index++, name: childPage.name, path: this.getComponentPagePath(childPage, projectId) }
                                    } else if (childPage.type === 'tracker') {
                                        menuItem = { id: index++, name: childPage.name, path: this.getTrackerPagePath(childPage, projectId) }
                                    } else if (childPage.type === 'url') {
                                        menuItem = { id: index++, name: childPage.name, path: childPage.url }
                                    }   
                                }
                                break;
                            } else {
                                menuItem = {}
                                const pageId = page.id
                                if (page.type === 'wiki') {
                                    menuItem = { id: index++, name: page.name, path: `/tracker/project/${projectId}/wiki${pageId}`, icon: page.icon ? page.icon : 'form' }
                                } else if (page.type === 'report') {
                                    menuItem = { id: index++, name: page.name, path: `/tracker/project/${projectId}/smartPage/${pageId}`, icon: page.icon ? page.icon : 'bar-chart' }
                                } else if (page.type === 'component') {
                                    menuItem = { id: index++, name: page.name, path: this.getComponentPagePath(page, projectId), icon: page.icon || 'form' }
                                } else if (page.type === 'tracker') {
                                    menuItem = { id: index++, name: page.name, path: this.getTrackerPagePath(page, projectId) }
                                } else if (page.type === 'url') {
                                    menuItem = { id: index++, name: page.name, path: page.url }
                                }
                            }
                        }
                    }
                    if (menuItem.path) {
                        console.log(menuItem)
                        this.$router.push({
                            path: menuItem.path,
                        })
                    }else {
                        VXETable.modal.message({ content: '没有找到对应的页面', status: 'warning' })
                    }
                    this.loading = false
                })
                //记录最近访问项目
                recordRecentProject(p.id).then(() => { })
            })
        },
    },
};
</script>
<style lang="less" scoped>
:deep(.content-page .ant-tabs-bar) {
    margin-bottom: 1px;
}

:deep(.content-page .ant-tabs-content) {
    height: 100%;
}

.main {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.card-layout {
    padding: 10px;
    display: flex;
    align-item: flex-start;
    flex-wrap: wrap;

    .card-project-title {
        display: flex;
        align-items: center;
        /*垂直居中*/
        font-size: 18px;
        height: 60px;
        line-height: 18px;
        vertical-align: middle;
    }

    .card-project-desciption {
        color: #808080;
    }

    .empty-data {
        width: 100%;
        height: 300px;
        box-sizing: border-box;
        margin: 0;
        padding: 0;
        color: rgba(0, 0, 0, .65);
        font-size: 14px;
        font-variant: tabular-nums;
        line-height: 1.5;
        list-style: none;
        font-feature-settings: "tnum";
        position: relative;
    }
}

.domain-list-cell-subtext {
    margin-left: 5px;
    font-size: 12px;
    color: #606060;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 180px;
    flex: 0 0 auto;
    color: var(--gray-80);
}

.filter-flex {
    flex: 0 1 0%;
    height: 0px;
}

.filter-flex-active {
    flex: 2 1 26%;
}</style>
