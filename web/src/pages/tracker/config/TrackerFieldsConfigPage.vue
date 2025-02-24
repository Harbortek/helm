<template>
    <config-page :routes="routes()" title="工作项属性设置" description="属性与视图可以定制工作项类型显示哪些功能标签页、详情标签页里显示哪些属性，并配置属性的默认值和是否必填。">
        <vxe-toolbar size="medium">
            <template #buttons>
                <vxe-input v-model="keyword" placeholder="搜索工作项属性" type="search" className="searchbox" width="240"
                    clearable></vxe-input>
            </template>
            <template #tools>
                <vxe-button content="从已有工作项复制" @click="onCopyFromTracker"></vxe-button>
                <vxe-button status="primary" content="添加工作项属性" @click="onCreateFiled"></vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-table ref="dataTable" :data="trackerFields" :loading="loading" :row-config="{ isHover: true }"
            :height="tableHeight" :edit-config="{ trigger: 'click', mode: 'cell' }">
            <!-- <vxe-column type="seq" width="60"></vxe-column> -->
            <vxe-column field="name" title="属性名称">
                <template #default="{ row }">
                    <a-space>
                        <span
                            :style="{ 'padding-left': row.group ? '0' : '20px', 'font-weight': row.group ? '500' : '400' }">{{
        row.name
    }}</span>
                        <a-tag v-if="row.system"> 系统</a-tag>
                    </a-space>
                </template>
            </vxe-column>
            <vxe-column field="inputType" title="属性类型" width="200">
                <template #default="{ row }">
                    <div v-if="!row.group">{{ $t('tracker.field.type.' + row.inputType) }}</div>
                </template>
            </vxe-column>
            <vxe-column field="mandatory" title="必填项">
                <template #default="{ row }">
                    <div v-if="row.system">
                        <div class="can-not-edit"></div>
                    </div>
                    <div v-else-if="!row.group">{{ row.exceptStatus?.length > 0 ? '按状态' : '' }} {{ row.mandatory ? '必填' :
        ''
                        }}
                    </div>
                </template>
            </vxe-column>
            <vxe-column field="permission" title="权限">
                <template #default="{ row }">
                    <div v-if="row.permission">
                        <vxe-button type="text" @click="onEditFieldPerm(row)">{{ $t('tracker.field.permission.type.' +
        row.permission.type) }}</vxe-button>
                    </div>
                </template>
            </vxe-column>
            <vxe-column field="defaultValue" title="默认值" :edit-render="{}">
                <template #default="{ row }">
                    <div v-if="row.id">
                        <div v-if="row.defaultValue && row.system">
                            <span v-if="row.inputType == 'OPTIONS'">
                                {{ row.items.find(i => i.id == row.defaultValue)?.name }}
                            </span>
                            <span v-else-if="row.inputType == 'MULTI_OPTIONS'">
                                {{ row.items.filter(i => row.defaultValue.includes(i.id)).map(i => i.name).join(',') }}
                            </span>
                            <span v-else-if="row.inputType == 'BOOL'">
                                {{ row.defaultValue == 'true' ? '是' : '否' }}
                            </span>
                            <span v-else-if="row.inputType == 'DATE'">
                                {{ row.defaultValue }}
                            </span>
                            <span v-else-if="row.inputType == 'TIME'">
                                {{ row.defaultValue }}
                            </span>
                            <span v-else-if="row.inputType == 'USER'">
                                {{ projectUserList.find(v => v.id == row.defaultValue)?.name }}
                            </span>
                            <span v-else-if="row.inputType == 'MEMBERS'">
                                {{ projectUserList.filter(i => row.defaultValue.includes(i.id)).map(i => i.name).join(',')
                                }}
                            </span>
                            <span v-else-if="row.inputType == 'STATUS'">
                                {{ tracker.trackerStatuses.find(v => v.id == row.defaultValue)?.name }}
                            </span>
                            <span v-else-if="row.inputType == 'STATUS_TYPE'">
                                {{ trackerStatusTypeList.find(v => v.id == row.defaultValue)?.name }}
                            </span>
                            <span v-else-if="row.inputType == 'SPRINT'">
                                {{ sprintList.find(v => v.id == row.defaultValue)?.name }}
                            </span>
                            <span v-else-if="row.inputType == 'WORK_ITEM'">
                                {{ trackerList.find(v => v.id == row.defaultValue)?.name }}
                            </span>
                            <span v-else>
                                {{ row.defaultValue }}
                            </span>
                        </div>
                        <div v-else-if="row.defaultValue && !row.system">
                            <span v-if="row.inputType == 'MULTI_OPTIONS' || row.inputType == 'MEMBERS'">
                                {{ row.defaultValue.join(',') }}
                            </span>
                            <span v-else-if="row.inputType == 'WORK_ITEM'">
                                {{ trackerList.find(v => v.id == row.defaultValue)?.name }}
                            </span>
                            <span v-else>
                                {{ row.defaultValue }}
                            </span>
                        </div>
                        <div v-else class="can-not-edit"></div>
                    </div>
                </template>
                <template #edit="{ row }">
                    <div v-if="row.id">
                        <div v-if="exceptFields(row)" class="can-not-edit"></div>
                        <vxe-select v-else-if="row.inputType == 'OPTIONS'" v-model="row.defaultValue"
                            @change="onChangeDefaultValue(row)" clearable>
                            <vxe-option :value="row.system ? item.id : item.name" v-for="item in row.items" :key="item.id"
                                :label="item.name"></vxe-option>
                        </vxe-select>
                        <vxe-select v-else-if="row.inputType == 'MULTI_OPTIONS'" v-model="row.defaultValue"
                            @change="onChangeDefaultValue(row)" multiple clearable>
                            <vxe-option :value="row.system ? item.id : item.name" v-for="item in row.items" :key="item.id"
                                :label="item.name"></vxe-option>
                        </vxe-select>
                        <vxe-select v-else-if="row.inputType == 'STATUS'" v-model="row.defaultValue"
                            @change="onChangeDefaultValue(row)" clearable>
                            <vxe-option :value="row.system ? item.id : item.name" v-for="item in tracker.trackerStatuses"
                                :key="item.id" :label="item.name"></vxe-option>
                        </vxe-select>
                        <vxe-select v-else-if="row.inputType == 'STATUS_TYPE'" v-model="row.defaultValue"
                            @change="onChangeDefaultValue(row)" clearable>
                            <vxe-option :value="row.system ? item.id : item.name" v-for="item in trackerStatusTypeList"
                                :key="item.id" :label="item.name"></vxe-option>
                        </vxe-select>
                        <vxe-select v-else-if="row.inputType == 'SPRINT'" v-model="row.defaultValue"
                            @change="onChangeDefaultValue(row)" clearable>
                            <vxe-option :value="row.system ? item.id : item.name" v-for="item in sprintList" :key="item.id"
                                :label="item.name"></vxe-option>
                        </vxe-select>
                        <vxe-select v-else-if="row.inputType == 'BOOL'" v-model="row.defaultValue"
                            @change="onChangeDefaultValue(row)" clearable>
                            <vxe-option value="true" label="是"></vxe-option>
                            <vxe-option value="false" label="否"></vxe-option>
                        </vxe-select>
                        <vxe-input v-else-if="row.inputType == 'DATE'" v-model="row.defaultValue"
                            @change="onChangeDefaultValue(row)" placeholder="日期选择" type="date"></vxe-input>
                        <vxe-input v-else-if="row.inputType == 'TIME'" v-model="row.defaultValue"
                            @change="onChangeDefaultValue(row)" placeholder="时间选择" type="time"></vxe-input>

                        <project-user-select :getPopupContainer="triggerNode => { return triggerNode.parentNode; }"
                            v-model="row.defaultValue" v-else-if="row.inputType == 'USER'" style="width:100%"
                            :projectId="projectId" @change="onChangeDefaultValue(row)" />
                        <project-user-select :getPopupContainer="triggerNode => { return triggerNode.parentNode; }"
                            v-model="row.defaultValue" v-else-if="row.inputType == 'MEMBERS'" style="width:100%"
                            :mode="'multiple'" :projectId="projectId" @change="onChangeDefaultValue(row)" />
                        <tracker-select v-model="row.defaultValue" v-else-if="row.inputType == 'WORK_ITEM'"
                            style="width:100%" :getPopupContainer="triggerNode => { return triggerNode.parentNode; }"
                            :projectId="projectId" @change="onChangeDefaultValue(row)"></tracker-select>

                        <a-input v-model="row.defaultValue" v-else style="width:100%" />
                    </div>
                </template>
            </vxe-column>
            <!-- <vxe-column field="showInList" title="在列表中显示">
                <template #default="{ row }">
                    <div v-if="!row.group">
                        <vxe-switch v-model="row.showInList" open-label="是" open-value="true" close-label="否"
                            close-value="false"></vxe-switch>
                    </div>
                </template>
            </vxe-column> -->
            <vxe-column field="" title="操作" width="200" header-align="center">
                <template #default="{ row }">
                    <div v-if="row.system">
                        <div class="can-not-edit"></div>
                    </div>
                    <div>
                        <vxe-button v-if="!row.group && !row.system" type="text" icon="vxe-icon-edit"
                            @click="onEditField(row)">编辑</vxe-button>
                        <vxe-button v-if="!row.group && !row.system" type="text" icon="vxe-icon-delete"
                            @click="onDeleteField(row)">删除</vxe-button>
                    </div>
                </template>
            </vxe-column>
        </vxe-table>
        <tracker-field-dialog :isShowDialog="isShowEditDialog" :editMode="editMode" :trackerField="currentTrackerField"
            :tracker="tracker" @cancel="isShowEditDialog = false" @ok="onEditFieldOK"></tracker-field-dialog>
        <tracker-field-permission-dialog :isShowDialog="isShowEditPermDialog" :editMode="editMode"
            :trackerField="currentTrackerField" :tracker="tracker" @cancel="isShowEditPermDialog = false"
            @ok="onEditFieldOK"></tracker-field-permission-dialog>

        <tracker-fields-copy-dialog :isShowDialog="showCopyDialog" :trackerId="tracker?.id"
            :projectId="tracker?.projectId" @cancel="showCopyDialog = false" @ok="onCopyOK" />
    </config-page>
</template>
<script>
import {
    findOneTracker, findTrackers, copyTrackerFields,
    findTrackerFields, createTrackerField, updateTrackerField, deleteTrackerField
} from "@/services/tracker/TrackerService";
import { findEnumsByCode } from '@/services/system/EnumService'
import { findProjectUsers } from "@/services/tracker/ProjectRoleMemberService";
import { findSprints } from "@/services/plan/SprintService";
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import TrackerFieldDialog from './TrackerFieldDialog.vue';
import TrackerFieldPermissionDialog from './TrackerFieldPermissionDialog.vue';
import VXETable from "vxe-table";
import TrackerFieldsCopyDialog from './TrackerFieldsCopyDialog.vue';
import EnumCategorySelect from '@/components/select/EnumCategorySelect.vue';
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import TrackerSelect from '@/components/select/TrackerSelect.vue';
import TrackerStatusSelect from '../../../components/select/TrackerStatusSelect.vue';



export default {
    name: "TrackerFieldsConfigPage",
    components: {
        ConfigPage, TrackerFieldDialog, TrackerFieldPermissionDialog, TrackerFieldsCopyDialog,
        EnumCategorySelect, ProjectUserSelect, TrackerSelect, TrackerStatusSelect
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
        customerFileds() {
            let customerFileds = []
            customerFileds.push({ name: '自定义属性', group: true })
            if (this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                this.tracker.trackerFields.forEach(f => {
                    if (f.system === false && (new RegExp(this.keyword)).test(f.name)) {
                        customerFileds.push(f)
                    }
                })
            }
            return customerFileds
        },
        systemFields() {
            let systemFields = []
            systemFields.push({ name: '系统属性', group: true })
            if (this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                this.tracker.trackerFields.forEach(f => {
                    if (f.system === true && (new RegExp(this.keyword)).test(f.name)) {
                        systemFields.push(f)
                    }
                })
            }
            return systemFields
        },
        trackerFields() {
            let fields = []
            return [].concat(this.customerFileds).concat(this.systemFields)
        }
    },
    data() {
        return {
            tableHeight: 400,
            loading: false,
            keyword: '',
            tracker: {},
            isShowEditDialog: false,
            isShowEditPermDialog: false,
            currentTrackerField: {},
            editMode: 'create',
            showCopyDialog: false,
            projectUserList: [],
            trackerList: [],
            trackerStatusTypeList: [],
            sprintList: [],
        };
    },
    mounted() {
        this.$nextTick(function () {
            if (this.$refs.dataTable) {
                this.tableHeight =
                    window.innerHeight - this.$refs.dataTable?.$el.offsetTop - 100;
            }

            // 监听窗口大小变化
            let self = this;
            window.onresize = function () {
                self.tableHeight =
                    window.innerHeight - self.$refs.dataTable?.$el.offsetTop - 100;
            };
        });

        this.initData();
        this.loadData()
    },
    methods: {
        routes: function () {
            return [{
                name: "trackerListConfig", meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerListConfig",
                    show: false,
                },
            }, {
                name: "trackerConfigPortal",
                meta: {
                    icon: "blank",
                    title: this.tracker.name,
                    show: false,
                },
            }, {
                name: 'trackerFieldsConfig', meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerFieldsConfig",
                    show: false,
                },
            }]
        },
        initData() {
            findProjectUsers(this.projectId).then(res => {
                this.projectUserList = res;
            });
            findTrackers({ projectId: this.projectId }).then(res => {
                this.trackerList = res;
            })
            findEnumsByCode('TRACKER_STATUS_MEANING').then((res) => {
                this.trackerStatusTypeList = res
            });
            findSprints(this.projectId).then(res => {
                this.sprintList = res;
            });

        },
        loadData() {
            let trackerId = this.$route.params.trackerId
            findOneTracker(trackerId).then(resp => {
                this.tracker = resp;
                console.log(this.tracker.trackerFields)
                if (this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                    // let index = 1
                    // this.tracker.trackerFields.forEach(f => {
                    //     f.id = index++
                    // })
                }

            })
        },
        exceptFields(row) {
            let exceptSystemProperty = ['parent', 'itemNo', 'createBy', 'createDate', 'lastModifiedBy', 'lastModifiedDate',
                'assignedDate', 'projectId', 'trackerId', 'realStartDate']
            if (exceptSystemProperty.includes(row.systemProperty)) {
                return true
            }
            return false
        },
        onChangeDefaultValue(row) {
            this.onEditDefaultValue(row)
        },
        onCopyFromTracker() {
            this.showCopyDialog = true;
        },
        onCopyOK(fields) {
            console.log("okCOpyOK", fields)
            copyTrackerFields(this.tracker.id, fields.fields).then(res => {
                this.showCopyDialog = false
                this.loadData()
            })
        },
        onCreateFiled() {
            this.editMode = 'create'
            const fieldIds = this.tracker.trackerFields.map(f => f.id);
            // let MAXID = fieldIds.reduce((a, b) => {
            //     return Math.max(a, b);
            // });
            this.currentTrackerField = { id: null, name: '', inputType: 'TEXT', system: false, items: [] }
            this.isShowEditDialog = true

        },
        onEditField(row) {
            this.editMode = 'edit'
            this.currentTrackerField = row
            this.isShowEditDialog = true
        },
        onEditFieldPerm(row) {
            this.editMode = 'edit'
            this.currentTrackerField = JSON.parse(JSON.stringify(row))
            this.isShowEditPermDialog = true
        },
        onDeleteField(row) {
            VXETable.modal.confirm({
                title: '删除工作项属性',
                content: '「' + row.name + '」将被删除，该动作不可恢复'
            }).then(type => {
                if (type === 'confirm') {
                    deleteTrackerField(this.tracker.id, row).then(resp => {
                        VXETable.modal.message({ content: '「' + row.name + '」已被删除', status: 'info' })
                        this.loadData()
                    })
                }
            })
        },
        onEditFieldOK(tf) {
            console.log("tf.t", tf.items)
            this.isShowEditDialog = false
            this.isShowEditPermDialog = false
            if (tf.id === null) {
                tf.items = [];
                //插入新数据
                console.log("createTrackerFields", tf)
                createTrackerField(this.tracker.id, tf).then(resp => {
                    this.loadData()
                })
            } else {
                this.currentTrackerField.name = tf.name
                this.currentTrackerField.enumId = tf.enumId
                this.currentTrackerField.mandatory = tf.mandatory
                this.currentTrackerField.exceptStatus = tf.exceptStatus
                this.currentTrackerField.permission = tf.permission
                this.currentTrackerField.defaultValue = tf.defaultValue
                this.currentTrackerField.columns = tf.columns
                updateTrackerField(this.tracker.id, this.currentTrackerField).then(resp => {
                    this.loadData();
                    VXETable.modal.message({ content: '更新成功', status: 'success' })
                })
            }
        },
        onEditDefaultValue(trackerField) {
            updateTrackerField(this.tracker.id, trackerField).catch(err => {
                VXETable.modal.message({ content: '更新失败', status: 'error' })
            })
        },
    },
};
</script>
<style lang="less" scoped>
.can-not-edit {
    height: 50%;
    border-bottom: solid 2px #dedede;
    width: 45px;
    box-sizing: border-box;
}
</style>
