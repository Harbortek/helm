<template>
    <config-page :routes="routes()" style="padding:0;" title="" description="">
        <a-layout style="">
            <a-layout-sider class="left-sider-content" style="max-width: 300px;min-width: 240px;">
                <h3>详情表单配置</h3>
                <div class="button-box">
                    <!-- <a-button style="margin:10px;">默认值和是否必填</a-button> -->
                    <a-button style="margin:10px;" @click="keyFieldsVisible = true">关键属性</a-button>
                </div>
                <h4>快速配置</h4>
                <a-radio-group :defaultValue="2" @change="onChangeRadio">
                    <a-radio style="" :value="1">
                        与新建表单保持一致
                    </a-radio>
                    <a-radio style="margin-top:10px;" :value="2">
                        自定义配置
                    </a-radio>
                </a-radio-group>
            </a-layout-sider>
            <a-layout-content class="main-content">
                <div class="main-wrapper" style="width: 100%;">
                    <tracker-layout-edit-detail :projectId="projectId" :selectTab="selectTab" :tracker="tracker"
                        :trackerItem="trackerItem" :keyFields="keyFields" :customerFields="customerFields"
                        :sections="sections" @changeSelectTab="onChangeSelectTab"></tracker-layout-edit-detail>
                </div>
            </a-layout-content>

            <a-layout-sider class="right-sider-content" style="max-width: 300px;min-width: 240px;">
                <template v-if="selectTab == 'field'">
                    <h3>属性列表卡片配置
                        <vxe-button style="font-size: 12px;float:right" title="移除" type="text" icon="vxe-icon-close"
                            @click="selectTab = ''"></vxe-button>
                    </h3>
                    <div class="subtitle" style="margin-bottom:10px;">
                        支持添加属性、调整排序。此卡片不支持展示富文本属性。同一个属性只能出现一次，不能重复添加。
                    </div>
                    <h4>属性列表
                        <vxe-button style="float:right" type="text" status="primary" @click="addFieldsVisible = true"
                            content="添加属性" icon="vxe-icon-square-plus-fill"></vxe-button>
                    </h4>
                    <div class="content-wrap" trigger="['click']]" placement="bottom" @mouseenter="rowDrop">
                        <div class="ui-table">
                            <div ref="fieldByTable">
                                <div class="order-row" style="height:30px;" v-for="item in customerFields"
                                    :key="item.id">
                                    <div class="layout-fields-table-row table-row">
                                        <div style="padding-left: 0;">
                                            <i class="vxe-icon--menu"></i>
                                            {{ item.name }}
                                            <vxe-button style="float:right" title="移除" type="text" icon="vxe-icon-close"
                                                @click="onClickDeleteFields(item)"></vxe-button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
                <template v-else-if="selectTab == 'sections'">
                    <h3 style="margin-bottom:20px;">模块标签页配置
                        <vxe-button style="font-size: 12px;float:right" title="移除" type="text" icon="vxe-icon-close"
                            @click="selectTab = ''"></vxe-button>
                    </h3>
                    <h4>已启动的标签页
                        <vxe-button style="float:right" type="text" status="primary" @click="addSectionsVisible = true"
                            content="添加标签页" icon="vxe-icon-square-plus-fill"></vxe-button>
                    </h4>
                    <div class="content-wrap" trigger="['click']]" placement="bottom" @mouseenter="rowDropSections">
                        <div class="ui-table">
                            <div ref="sectionByTable">
                                <div class="order-row" style="height:30px;" v-for="item in sections" :key="item.value">
                                    <div class="layout-fields-table-row table-row">
                                        <div style="padding-left: 0;">
                                            <i class="vxe-icon--menu"></i>
                                            {{ item.name }}
                                            <vxe-button v-if="item.name != '详情'" style="float:right" title="移除"
                                                type="text" icon="vxe-icon-close"
                                                @click="onClickDeleteSections(item)"></vxe-button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
                <template v-else-if="selectTab == 'keyFields'">
                    <h3 style="margin-bottom:20px;">关键属性卡片配置
                        <vxe-button style="font-size: 12px;float:right" title="移除" type="text" icon="vxe-icon-close"
                            @click="selectTab = ''"></vxe-button>
                    </h3>
                    <div class="subtitle" style="margin-bottom:10px;">
                        显示已配置的关键属性。
                    </div>
                    <a-button style="width:100%;" @click="keyFieldsVisible = true">关键属性</a-button>
                </template>
                <template v-else>
                    <h3>操作引导
                    </h3>
                    <div class="guide-warp">
                        <h4>1.标签页配置</h4>
                        <span style="margin-left:10px;">选中侧栏，以查看或编辑模块标签页。</span>
                        <div class="guide-img">
                            <img style="object-fit: cover;width: 100%;height: 100%;"
                                :src="require('@/assets/layout/layout-detail01.png')" />
                        </div>

                        <h4>2.编辑属性</h4>
                        <span style="margin-left:10px;">选中属性卡片，以添加或删除属性、编辑属性的顺序。</span>
                        <div class="guide-img">
                            <img style="object-fit: cover;width: 100%;height: 100%;"
                                :src="require('@/assets/layout/layout-detail02.png')" />
                        </div>
                    </div>
                </template>
                <div class="right-sider-footer">
                    <div class="guide-warp">
                        <a-button @click="onClickRelease" type="primary" style="margin-bottom:10px">发布</a-button>
                        <a-button @click="onCLickCancel">取消</a-button>
                    </div>
                </div>
            </a-layout-sider>
        </a-layout>


        <tracker-layout-Key-fields-dialog :isShowDialog="keyFieldsVisible" :tracker="tracker"
            :customerFields="customerFields" @refresh="reloadKeyFields"
            @cancel="keyFieldsVisible = false"></tracker-layout-Key-fields-dialog>

        <a-modal :visible="addFieldsVisible" centered width="520px" title="添加属性" @ok="onOKAddFields"
            @cancel="addFieldsVisible = false">
            <a-form-model :model="formData">
                <a-form-model-item prop="field">
                    <a-select placeholder="选择属性" style="width: 460px;" show-search optionFilterProp='children'
                        v-model="formData.field">
                        <a-select-option v-for="item in addFields" :key="item.id" :value="item.id">{{ item.name
                            }}</a-select-option>
                    </a-select>
                </a-form-model-item>
            </a-form-model>
        </a-modal>
        <a-modal v-if="addSectionsVisible" :visible="addSectionsVisible" centered width="520px"
            :bodyStyle="{ padding: '0 20px' }" title="添加标签页" @ok="onOKAddSections" @cancel="addSectionsVisible = false">
            <a-form-model :model="formData">
                <a-checkbox-group v-model="formData.sections" :defaultValue="sections.map(item => item.value)"
                    style="width: 100%;">
                    <a-row>
                        <a-col v-for="item in sectionList" :key="item.value">
                            <a-checkbox :disabled="hasSections(item.value)" :value="item.value"
                                style="color: #303030;margin-top:10px;">
                                {{ item.name }}
                            </a-checkbox>
                            <div class="sections-desc">
                                {{ item.desc }}</div>
                            <div class="sections-border"></div>
                        </a-col>
                    </a-row>
                </a-checkbox-group>
            </a-form-model>
        </a-modal>
    </config-page>
</template>
<script>
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import {
    findOneTracker, updateTrackerLayout, findTrackerLayout
} from "@/services/tracker/TrackerService";
import Router from 'vue-router'
import SimpleEditor from '../../../components/editor/SimpleEditor.vue';
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
import { mapGetters, mapState, mapMutations } from "vuex";
import {
    findProjectRolesAndMembers
} from "@/services/tracker/ProjectRoleMemberService";
import { findTrackerItems, findOneTrackerItem } from "@/services/tracker/TrackerItemService";
import AdminLayout from '../../../layouts/AdminLayout.vue';
import Sortable from "sortablejs"
import TrackerLayoutKeyFieldsDialog from './TrackerLayoutKeyFieldsDialog';
import QuickPicker from '@/components/select/QuickPicker.vue';
import TrackerComment from '../items/TrackerComment.vue';
import TrackerAttachment from '../items/TrackerAttachment.vue';
import TrackerItemKeyFields from '@/components/select/TrackerItemKeyFields.vue';
import TrackerLayoutEditDetail from './TrackerLayoutEditDetail.vue';



export default {
    name: "TrackerLayoutEditDetailPage",
    components: {
        ConfigPage, RoleMembersTable, SimpleEditor, AdminLayout, TrackerItemKeyFields,
        TrackerLayoutKeyFieldsDialog, QuickPicker, TrackerComment, TrackerAttachment,
        TrackerLayoutEditDetail
    },
    data() {
        return {
            currentSubTab: 'DETAIL',
            tableHeight: 400,
            loading: false,
            keyword: '',
            projectId: '',
            trackerId: '',
            tracker: {},
            itemData: [],
            formData: {
                name: '',
                projectId: '',
                trackerId: '',
                values: {},
                owner: { id: '' },
                priority: '',
                relatedWorkItems: [],
                watchers: [],
                field: undefined,
                sections: [],
            },
            keyFieldsVisible: false,
            addFieldsVisible: false,
            addSectionsVisible: false,
            selectTab: '',
            customerFields: [],
            sections: [],
            keyFields: [],
            isCustomConfig: true,
            rules: {
                name: [
                    { required: true, message: "请输入工作项名称", trigger: "blur" },
                    {
                        min: 2,
                        max: 20,
                        message: "长度在2到20之间",
                        trigger: "blur",
                    },
                ],
                projectId: [
                    { required: true, message: "请选择所属项", trigger: "blur" },
                ],
                trackerId: [
                    { required: true, message: "请选择工作项类型", trigger: "blur" },
                ]
            },
            trackerItem: {},
            priorities: [],
            projectUsers: [],
            editorInEditMode: false,
            sectionList: [
                { value: "DETAIL", name: "详情", desc: "显示当前工作项下的描述、属性和基础信息。" },
                // { value: "WORK_ITEMS", name: "子工作项", desc: "显示当前工作项下的子工作项列表。" },
                { value: "CYCLE_PROGRESS", name: "周期与进度", desc: "显示当前工作项下的进度、计划开始日期、计划完成日期。" },
                { value: "WORK_HOURS", name: "工时", desc: "显示当前工作项的工时信息和关联的工作记录。" },
                // { value: "TEST_CASES", name: "测试情况", desc: "显示当前工作项作为测试用例的测试信息。" },
                { value: "RELATED_ITEMS", name: "关联工作项", desc: "显示当前工作项关联的工作项列表。" },
                { value: "RELATED_CODE", name: "代码关联", desc: "显示当前工作项关联的代码提交、合并请求、代码分支信息。" },
                { value: "RELATED_WIKI", name: "关联Wiki页面", desc: "显示当前工作项关联的Wiki页面。" },
                { value: "ATTACHMENTS", name: "文件", desc: "显示当前工作项关联的文件；支持通过上传添加关联文件。" },
                { value: "HYPERLINKS", name: "链接", desc: "显示当前工作项关联的链接；包括内部链接和外部链接" },
                { value: "RELATED_TESTS", name: "关联测试结果", desc: "显示当前工作项关联的用例测试结果。" }
            ]
        };
    },
    computed: {
        ...mapGetters("account", ["user"]),
        addFields() {
            let fields = this.tracker.trackerFields || []
            fields = fields.filter(f => !f.system)

            let ids = this.customerFields.map(f => f.id);
            ids = [...ids, ...this.keyFields.map(f => f.id)]
            let result = fields.filter(f => { return ids.indexOf(f.id) < 0 })
            return result
        },
        stateTransitions() {
            const currentStatusId = this.trackerItem?.status?.id
            return this.tracker?.trackerStateTransitions?.filter(s => s.transitionFrom.id === currentStatusId)
        },
    },
    mounted() {
        this.projectId = this.$route.params.projectId
        this.trackerId = this.$route.params.trackerId

        findOneTracker(this.trackerId).then(resp => {
            this.tracker = resp;
            this.tracker.trackerFields.forEach(x => {
                x.trackerId = this.trackerId
            })
            this.reload()
        }).finally(() => {
            this.initData('DETAIL');
            console.log("select", this.sections)
        })
    },
    methods: {
        onChangeSelectTab(e) {
            this.selectTab = e
        },
        handleSearch(val) {
            findTrackerItems(this.projectId, this.trackerId, null, {}, val, {}, []).then(resp => {
                this.itemData = [];
                console.log("items", resp)
                resp.content.forEach(res => {
                    this.itemData.push({ value: res.id, label: res.name })
                })
            }).finally(() => {

            })
        },
        handleChange(val) {
            findTrackerItems(this.projectId, this.trackerId, null, {}, val, {}, []).then(resp => {
                this.itemData = [];
                console.log("items", resp)
                resp.content.forEach(res => {
                    this.itemData.push({ value: res.id, label: res.name })
                })
            }).finally(() => {

            })
        },
        onChangeRadio(e) {
            console.log("e", e.target.value)
            if (e.target.value == '1') {//与新建表单保持一致
                this.selectTab = ''
                this.isCustomConfig = false;
                this.initData("NEW");
            } else {
                this.isCustomConfig = true;
            }
        },
        onClickRelease() {
            let layout = this.findLayout('DETAIL')
            layout.fields = Object.assign([], this.customerFields);
            layout.sections = Object.assign([], this.sections.map(item => item.value))
            updateTrackerLayout(this.tracker.id, layout).then(resp => {
                this.$message.success("发布成功")
                this.$router.push({
                    name: "trackerLayoutConfig",
                    params: {
                        selectTab: 'DETAIL'
                    }
                })
            })
        },
        onCLickCancel() {
            let that = this
            this.$confirm({
                title: "你有未发布的变更",
                content: '取消修改会导致未发布的变更丢失,是否取消修改？',
                okText: '取消修改',
                cancelText: '继续编辑',
                onOk() {
                    that.$router.push({
                        name: "trackerLayoutConfig",
                        params: {
                            selectTab: 'DETAIL'
                        }
                    })
                },
                onCancel() { },
            });
        },
        cancelDescription() {
            this.editorInEditMode = false
            this.$refs.editor.blur()
            this.formData.description = this.trackerItem.description
        },
        hasSections(value) {
            for (let item of this.sections) {
                if (value == item.value) {
                    return true;
                }
            }
            return false;
        },
        onOKAddFields() {
            console.log("oooonk", this.formData.field)
            if (this.formData.field) {
                let field = this.findFields(this.formData.field)
                if (field) {
                    this.customerFields.push(field)
                }
                this.addFieldsVisible = false;
                this.formData.field = undefined;
            }
        },
        onOKAddSections() {
            console.log("oooonk", this.formData.sections)
            if (this.formData.sections) {
                this.sections = []
                for (let item of this.formData.sections) {
                    for (let item2 of this.sectionList) {
                        if (item == item2.value) {
                            this.sections.push(item2)
                        }
                    }
                }

                this.addSectionsVisible = false;
                this.formData.sections = undefined;
            }
        },
        onClickDeleteFields(row) {
            console.log("onClickDeleteFields", row)
            for (let i in this.customerFields) {
                if (this.customerFields[i].id == row.id) {
                    this.$delete(this.customerFields, i);
                    break;
                }
            }
        },
        onClickDeleteSections(row) {
            console.log("onClickDeleteSections", row)
            for (let i in this.sections) {
                if (this.sections[i].value == row.value) {
                    this.$delete(this.sections, i);
                    break;
                }
            }
        },
        initData(layoutName) {
            let layout = this.findLayout(layoutName)
            //fields
            if (layout) {
                const fields = layout.fields || []
                this.sections = []
                for (let item of layout.sections) {//模块标签页
                    for (let item2 of this.sectionList) {
                        if (item == item2.value) {
                            this.sections.push(item2)
                        }
                    }
                }
                this.customerFields = []
                fields.forEach(f => {
                    let field = this.findFields(f.id)
                    if (field) {
                        this.customerFields.push(field)
                    }
                })
                //keyFields
                if (layoutName == 'DETAIL') {
                    this.keyFields = [];
                    const keyfields = layout.keyFields
                    keyfields.forEach(f => {
                        let field = this.findFields(f.id)
                        if (field) {
                            this.keyFields.push(field)
                        }
                    })
                }
                this.markRequired(this.customerFields, this.getRequiredFields())
            }
        },
        reloadKeyFields(keyFields) {
            this.keyFields = keyFields;
        },
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
                name: 'trackerLayoutEditDetail', meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerLayoutEditDetail",
                    show: false,
                },
            }]
        },
        reload() {
            const projectId = this.$route.params.projectId
            const trackerId = this.$route.params.trackerId

            this.$nextTick(() => {
                this.prepareFormData()
            })
        },
        save() {
            console.log("save")
        },
        findLayout(layoutName) {
            let layout = null;
            if (this.tracker && this.tracker.trackerLayouts && this.tracker.trackerLayouts.length > 0) {
                layout = this.tracker.trackerLayouts.filter(lay => { return lay.name === layoutName })[0]
            }
            if (!layout) {
                layout = { name: layoutName, keyFields: [], fields: [], sections: [] }
                this.tracker.trackerLayouts.push(layout)
            }
            return layout;
        },
        findFields(fieldId) {
            if (this.tracker && this.tracker.trackerFields && this.tracker.trackerFields.length > 0) {
                return this.tracker.trackerFields.filter(f => { return f.id === fieldId })[0]
            }
            return null;
        },
        getFirstInitStatus() {
            const statuses = this.tracker.trackerStatuses
            for (let i = 0; i < statuses.length; i++) {
                if (statuses[i].initial) {
                    return statuses[i]
                }
            }
            return
        },
        getRequiredFields() {
            const fields = this.tracker.trackerFields
            const firstStatus = this.getFirstInitStatus()
            let requiredFields = []
            fields.forEach(f => {
                if (!f.system && f.mandatory) {
                    let found = false
                    for (let i = 0; i < f.exceptStatus.length; i++) {
                        if (f.exceptStatus[i].id === firstStatus.id) {
                            found = true
                            break
                        }
                    }
                    if (!found) {
                        requiredFields.push(f.id)
                    }
                }
            })
            return requiredFields
        },
        markRequired(componentList, requiredFields) {
            componentList.forEach(cur => {

                if (requiredFields.indexOf(cur.id) >= 0) {
                    cur.required = true
                }

            })
        },
        prepareFormData() {
            this.trackerItem = {
                itemNo: "1",
                name: "这是一条实例数据",
                priority: {
                    backgroundColor: "rgb(224, 236, 251)",
                    color: "rgb(48, 127, 226)",
                    name: "优先级"
                },
                status: {
                    name: "状态"
                },
                values: {},
                owner: this.user,
            }
            this.formData = {
                projectId: this.projectId,
                trackerId: this.tracker.id,
                name: '这是一条示例数据',
                description: '描述',
                values: {},
                owner: this.user,
                priority: '3',
                relatedWorkItems: [],
                watchers: []
            }

            findProjectRolesAndMembers(this.projectId, 'SCOPE_TRACKER').then(resp => {
                let data = resp;
                const all = [].concat(data.projectRoles).concat(data.specialRoles).concat(data.members)
                const valueArray = ['工作项创建者', '工作项负责人']
                let tableData = all.filter(item => { return valueArray.indexOf(item.name) >= 0 })
                this.formData.watchers = tableData
            });
        },
        onSelectTrackerItem() {
            this.$refs.trackerItmeSlectModal.view();
        },
        rowDrop(visiable) {
            // console.log("aaa",this.keyFields)
            // if (visiable) {
            let that = this
            this.$nextTick(() => {
                let xTable = that.$refs.fieldByTable;
                if (this.sortable && this.sortable.option("animation") == '151') {//销毁rowDropSections
                    this.sortable.destroy();
                    this.sortable = undefined
                }
                if (xTable && !this.sortable) {
                    that.sortable = Sortable.create(
                        xTable,
                        {
                            handle: ".order-row",
                            animation: 150,
                            filter: '.order-row-bottom',
                            onEnd: ({ newIndex, oldIndex }) => {
                                that.customerFields.splice(newIndex, 0, this.customerFields.splice(oldIndex, 1)[0]);
                                var newArray = this.customerFields.slice(0);
                                that.customerFields = [];
                                that.$nextTick(function () {
                                    that.customerFields = newArray;
                                });
                            },
                        }
                    );
                }
            });
            // }
        },
        rowDropSections() {
            let that = this
            this.$nextTick(() => {
                let xTable = that.$refs.sectionByTable;
                if (this.sortable && this.sortable.option("animation") == '150') {
                    this.sortable.destroy();
                    this.sortable = undefined
                }
                if (xTable && !this.sortable) {
                    that.sortable = Sortable.create(
                        xTable,
                        {
                            handle: ".order-row",
                            animation: 151,
                            filter: '.order-row-bottom',
                            onEnd: ({ newIndex, oldIndex }) => {
                                that.sections.splice(newIndex, 0, this.sections.splice(oldIndex, 1)[0]);
                                var newArray = this.sections.slice(0);
                                that.sections = [];
                                that.$nextTick(function () {
                                    that.sections = newArray;
                                });
                            },
                        }
                    );
                }
            });
            // }
        },
    },
};
</script>
<style lang="less" scoped>
.subtitle {
    font-size: 14px;
    line-height: 1.43;
    margin-top: 5px;
    color: #909090;
    font-weight: normal;
}

.order-row {
    width: 100%;
    cursor: move;

    .icon-circle {
        font-size: 18px;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 22px;
        height: 22px;
        // border: solid 1px currentColor;
        border-radius: 50%;
        cursor: pointer;
    }

    .icon-circle .disabled {
        font-size: 18px;
        opacity: .5;
        color: #909090;
        cursor: not-allowed;
        background-color: #e8e8e8;
        border-color: #979797;
    }
}

.order-row:hover {
    background: #f8f8f8;
}

//详情
:deep(.ant-form-item) {
    margin-bottom: 8px;
}

.left-sider-content {
    background-color: #fff;
    box-shadow: 0px 0px 1px rgba(48, 48, 48, .25);
    padding: 10px;
    max-width: 300px;
    min-width: 240px;

    .button-box {
        display: flex;
        flex-direction: column;
        justify-content: center;
    }
}

.main-content {
    overflow: visible;
    display: flex;
    flex-direction: row;
    padding: 10px;
    justify-content: center;
    height: 100%;
    width: 100%;

    .main-wrapper {
        display: flex;
        flex-direction: column;
        align-items: center;

        .main-card {
            background-color: #fff;
            height: 91vh;
            overflow: hidden auto;
            box-shadow: 0px 0px 1px rgba(48, 48, 48, .25), 0px 12px 24px rgba(48, 48, 48, .15);
        }
    }
}

.right-sider-content {
    background-color: #fff;
    box-shadow: 0px 1px 0px rgba(48, 48, 48, .25);
    padding: 15px;
    max-width: 300px;
    min-width: 240px;

    .content-wrap {
        border: none;
        padding: 0;
        margin-top: 10px;
    }

    .guide-warp {
        display: flex;
        flex-direction: column;
        justify-content: center;

        .guide-img {
            margin: 10px 10px 20px;
            box-shadow: 0 0 1px 0 silver, 0 4px 8px 0 silver, 0 3px 12px 0 silver;
        }
    }
}

.right-sider-footer {
    position: absolute;
    bottom: 10px;
    right: 20px;
    width: 80%
}

.sections-desc {
    font-size: 12px;
    line-height: 1.5;
    color: #909090;
    margin-left: 29px;
}

.sections-border {
    border-bottom: 1px solid #e8e8e8;
    margin: 5px 0px 0px -20px;
    width: 520px;
}
</style>
