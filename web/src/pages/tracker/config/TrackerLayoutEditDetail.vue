<template>
    <a-card class="main-card" :bodyStyle="{ padding: 0, width: '55vw' }">
        <span slot="extra" href="#"><a-icon type="close" /></span>
        <template slot="title">
            <div class="ui-task-detail-header__topbar ui-task-detail-header__topbar--without-bottom">
                <div class="ui-task-detail-header__nav">
                    <div class="task-basic-left">
                        <div class="task-icon no-hover"><a-icon v-if="trackerItem?.icon"
                                :component="trackerItem?.icon" /></div>
                        <div class="task-basic-task-number">#{{ currentProjectKeyName + '-' + trackerItem?.itemNo }}
                        </div>
                    </div>
                </div>
                <div class="ui-task-detail-header__actions">
                    <div class="ui-task-actions">
                        <div></div>
                    </div>
                </div>
            </div>
        </template>
        <div style="position:relative;margin: 0 10px;">
            <span style="inset: 0px;position: absolute;z-index: 20;"></span>
            <a-layout class="ui-task-detail">
                <a-layout-header class="ui-task-detail__header" theme="light">
                    <a-row class="ui-task-detail-header__summary">
                        <a-input class="task-basic-title" v-model="formData.name" />
                    </a-row>
                    <a-row class="ui-task-primary-fields" style="margin: 0 5px;padding-bottom: 5px;">
                        <span v-if="isCustomConfig" @click="onClickSelectSpan('keyFields')"
                            :class="{ 'clickable-zoom': true, actived: selectTab == 'keyFields' }"
                            style="inset: 0px -16px;position: absolute;z-index: 20;"></span>

                        <tracker-item-key-fields v-if="projectId" :projectId="projectId" :keyFields="keyFields"
                            :trackerItem="trackerItem" :tracker="tracker"></tracker-item-key-fields>

                    </a-row>
                </a-layout-header>
                <div v-if="isCustomConfig"
                    style="margin-left: -10px;width: 860px;background: rgb(238,240,243);height:10px"></div>
                <a-layout class="ui-task-detail__container">
                    <a-layout-content class="ui-task-detail__main">
                        <div class="ui-task-detail__tabs">
                            <div :class="isCustomConfig?'ui-task-tabs-wapper':''">
                                <span v-if="isCustomConfig" @click="onClickSelectSpan('sections')"
                                    :class="{ 'clickable-zoom': true, actived: selectTab == 'sections' }"
                                    style="inset: 0px -16px;position: absolute;z-index: 20;"></span>
                                <a-tabs v-model="currentSubTab" default-active-key="DETAIL">

                                    <a-tab-pane v-for="item in sections" :key="item.value"
                                        :tab="item.name"></a-tab-pane>
                                </a-tabs>
                            </div>
                            <div class="" style="flex: 1 1 0%; min-height: 0px;">
                                <div style="height: 100%; overflow: hidden auto;">
                                    <div class="task-detail-module task-desc" v-show="currentSubTab === 'DETAIL'">
                                        <div class="task-detail-module-title">
                                            <div class="task-detail-module-title-text">描述</div>
                                        </div>
                                        <div class="task-desc-content">
                                            <div
                                                style="position: absolute; width: 0px; height: 0px; visibility: hidden; display: none;">
                                            </div>
                                            <div class="richtext-input ">
                                                <div class="richtext-input-viewer-wrapper">
                                                    <div class="richtext-editor">
                                                        <div class="richtext-editor-content"
                                                            :style="{ 'border': editorInEditMode ? 'solid 1px #5caff2' : '' }">
                                                            <simple-editor ref="editor" v-model="formData.description"
                                                                :showToolbar="editorInEditMode"
                                                                @focus="editorInEditMode = true" />
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="task-desc-btns" v-show="editorInEditMode">
                                                <div class="task-desc-btns-right">
                                                    <a-button @click="cancelDescription">取消</a-button>
                                                    <a-button type="primary">保存</a-button>
                                                </div>
                                            </div>
                                        </div>
                                        <div style="position:relative">
                                            <span v-if="isCustomConfig" @click="onClickSelectSpan('field')"
                                                :class="{ 'clickable-zoom': true, actived: selectTab == 'field' }"
                                                style="inset:0;position: absolute;z-index: 210;"></span>
                                            <div class="task-detail-module task-desc">
                                                <div class="task-detail-module-title">
                                                    <div class="task-detail-module-title-text">自定义属性</div>
                                                </div>
                                                <a-form-model :layout="'horizontal'" :labelCol="{ span: 8 }"
                                                    :wrapperCol="{ span: 16 }" :model="formData">
                                                    <a-row :gutter="15">
                                                        <a-col :span="isTable(f)?24:12"
                                                            v-for="(f, index) in customerFields" :key="f.id">
                                                            <a-form-item :label="f.name" :prop="f.name"
                                                                :required="f.required"
                                                                :labelCol="{ span: isTable(f) ? 4 : 8 }"
                                                                :wrapperCol="{ span: isTable(f) ? 20 : 16 }">
                                                                <ItemCustomFieldsShow :fields="f" v-model="formData.values[f.id]" :projectId="projectId" :trackerId="tracker.id"
                                                                    :readOnly="false"></ItemCustomFieldsShow>
                                                                    
                                                            </a-form-item>
                                                        </a-col>
                                                    </a-row>
                                                </a-form-model>
                                            </div>
                                        </div>

                                        <div class="task-detail-module task-desc">
                                            <div class="task-detail-module-title">
                                                <div class="task-detail-module-title-text">基础属性</div>
                                            </div>
                                            <a-form-model :layout="'horizontal'" :labelCol="{ span: 8 }"
                                                :wrapperCol="{ span: 16 }">
                                                <a-row :gutter="15">
                                                    <a-col :span="12">
                                                        <a-form-model-item label="所属项目">
                                                            {{ trackerItem?.project?.name }}
                                                        </a-form-model-item>
                                                    </a-col>
                                                    <a-col :span="12">
                                                        <a-form-model-item label="工作项类型">
                                                            {{ tracker?.name }}
                                                        </a-form-model-item>
                                                    </a-col>
                                                    <a-col :span="12">
                                                        <a-form-model-item label="创建者">
                                                            {{ trackerItem?.createBy?.name }}
                                                        </a-form-model-item>
                                                    </a-col>
                                                    <a-col :span="12">
                                                        <a-form-model-item label="创建日期">
                                                            {{ trackerItem?.createDate }}
                                                        </a-form-model-item>
                                                    </a-col>
                                                    <a-col :span="12">
                                                        <a-form-model-item label="更新者">
                                                            {{ trackerItem?.lastModifiedBy?.name }}
                                                        </a-form-model-item>
                                                    </a-col>
                                                    <a-col :span="12">
                                                        <a-form-model-item label="更新日期">
                                                            {{ trackerItem?.lastModifiedDate }}
                                                        </a-form-model-item>
                                                    </a-col>
                                                    <a-col :span="12">
                                                        <a-form-model-item ref="sprintId" label="所属迭代" prop="sprintId">
                                                            <sprint-select v-model="formData.sprintId"
                                                                :projectId="projectId"></sprint-select>
                                                        </a-form-model-item>
                                                    </a-col>
                                                </a-row>
                                            </a-form-model>
                                        </div>
                                    </div>

                                    <div v-show="currentSubTab === 'RELATED_ITEMS'">
                                        <tracker-related-item :projectId="projectId" :trackerId="tracker.id"
                                            :relatedWorkItems="formData.relatedWorkItems"></tracker-related-item>
                                    </div>
                                    <div v-show="currentSubTab === 'ATTACHMENTS'">
                                        <tracker-attachment :tracker-item="trackerItem" />
                                    </div>

                                    <div v-show="currentSubTab === 'RELATED_WIKI'">
                                        <tracker-related-wiki :projectId="projectId" :tracker-item="trackerItem" />
                                    </div>

                                    <div v-show="currentSubTab === 'RELATED_CODE'">
                                        <tracker-related-code :tracker-item="trackerItem"
                                            :isShowDialog="currentSubTab === 'RELATED_CODE'" />
                                    </div>

                                    <div v-show="currentSubTab === 'CYCLE_PROGRESS'">
                                        <tracker-cycle-progress :formData="formData"></tracker-cycle-progress>
                                    </div>
                                    <div v-show="currentSubTab === 'WORK_HOURS'">
                                        <tracker-work-hours :currentSubTab="currentSubTab" :formData="formData"
                                            :trackerItem="trackerItem" :projectId="projectId"></tracker-work-hours>
                                    </div>
                                    <div v-show="currentSubTab === 'HYPERLINKS'">
                                        <tracker-hyperlinks :tracker-item="trackerItem"
                                            :isShowDialog="currentSubTab === 'HYPERLINKS'" />
                                    </div>
                                    <div v-if="currentSubTab === 'TEST_CASES'">
                                        <tracker-test-cases style="margin-left:10px;" :formData="trackerItem"
                                            :isShowDialog="currentSubTab=='TEST_CASES'"></tracker-test-cases>
                                    </div>
                                    <div v-show="currentSubTab === 'RELATED_TESTS'">
                                        <tracker-related-test :projectId="projectId" :trackerId="tracker.id"
                                            :itemId="trackerItem?.id" :isShowDialog="currentSubTab=='RELATED_TESTS'" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </a-layout-content>
                    <a-layout-sider class="ui-task-detail__side" theme="light" width="340">
                        <tracker-comment :projectId="projectId" :itemId="trackerItem?.id"
                            :watchers="trackerItem.watchers" title="动态" />
                    </a-layout-sider>
                </a-layout>
            </a-layout>
        </div>
    </a-card>

</template>
<script>
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import SimpleEditor from '../../../components/editor/SimpleEditor.vue';
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
import SprintSelect from '../../../components/select/SprintSelect.vue';
import { mapGetters, mapState, mapMutations } from "vuex";
import AdminLayout from '../../../layouts/AdminLayout.vue';
import QuickPicker from '@/components/select/QuickPicker.vue';
import TrackerComment from '../items/TrackerComment.vue';
import TrackerItemKeyFields from '@/components/select/TrackerItemKeyFields.vue';
import TrackerAttachment from '../items/TrackerAttachment.vue';
import TrackerRelatedWiki from '../items/TrackerRelatedWiki.vue';
import TrackerRelatedItem from '../items/TrackerRelatedItem.vue';
import TrackerRelatedCode from '../items/TrackerRelatedCode.vue';
import TrackerWorkHours from '../items/TrackerWorkHours.vue';
import TrackerCycleProgress from '../items/TrackerCycleProgress.vue';
import TrackerHyperlinks from '../items/TrackerHyperlinks.vue';
import TrackerTestCases from '../items/TrackerTestCases.vue';
import TrackerRelatedTest from '../items/TrackerRelatedTest.vue';
import ItemCustomFieldsShow from '@/components/tool/ItemCustomFieldsShow.vue';

export default {
    name: "TrackerLayoutEditDetailPage",
    components: {
        ConfigPage, RoleMembersTable, SimpleEditor, AdminLayout, TrackerItemKeyFields,
         QuickPicker, TrackerComment, TrackerAttachment,TrackerRelatedWiki,TrackerRelatedItem,
         TrackerRelatedCode, TrackerWorkHours, TrackerCycleProgress,TrackerTestCases,
         TrackerHyperlinks,TrackerRelatedTest,SprintSelect,ItemCustomFieldsShow
        
    },
    props: {
        tracker: Object,
        projectId: String,
        keyFields: Array,
        customerFields: Array,
        sections: Array,
        selectTab: String,
        isPreview: String,
    },
    data() {
        return {
            currentSubTab: 'DETAIL',
            trackerId: '',
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
            isCustomConfig: true,
            trackerItem: {},
            editorInEditMode: false,
        };
    },
    watch: {
        tracker: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {

                }
            }
        },
        isPreview:{
            immediate: true,
            handler: function (newVal, oldVal) {
                if(newVal){
                    this.isCustomConfig=false
                }
            }
        }
    },
    computed: {
        ...mapGetters("account", ["user"]),
        ...mapGetters("project", ["currentProjectKeyName"]),
    },
    mounted() {
        this.trackerId = this.$route.params.trackerId
        this.reload()
    },
    methods: {
        onClickSelectSpan(e){
            this.$emit('changeSelectTab', e);
        },
        cancelDescription() {
            this.editorInEditMode = false
            this.$refs.editor.blur()
            this.formData.description = this.trackerItem.description
        },
        reload() {
            this.$nextTick(() => {
                this.prepareFormData()
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
        },
        onSelectTrackerItem() {
            this.$refs.trackerItmeSlectModal.view();
        },
        isTable(f) {
            return f.inputType === 'TABLE' || f.inputType === 'TEST_STEP'
        },
    },
};
</script>
<style lang="less" scoped>
.ui-task-tabs-wapper{
    position:relative;
    margin:0 6px;
    /deep/ .ant-tabs-nav {
        z-index: 30;
    }
    /deep/ .ant-tabs-nav-container-scrolling {
        z-index: 30;
    }
}

.clickable-zoom {
    position: absolute;
    border-radius: 3px;
    border: dashed 2px #c7c7c7;
    cursor: pointer;
    z-index: 10;
}

.clickable-zoom:hover {
    border: dashed 2px #338fe5;
    background-color: rgba(51, 143, 229, .1);
}

.clickable-zoom.actived {
    border: solid 2px #338fe5;
}

//详情
:deep(.ant-form-item) {
    margin-bottom: 8px;
}

.ui-task-detail-header__topbar {
    .task-basic-left {
        flex: 1 1 auto;
        display: flex;
        align-items: center;
        min-width: 0;

        .task-icon {
            line-height: 1;
        }

        .task-basic-task-number {
            flex: 0 0 auto;
            line-height: 1;
            cursor: pointer;
            transition: color .2s;
            color: #303030;
            margin-left: 10px;
        }
    }
}

.ui-task-detail {
    height: 100%;
    display: flex;
    flex-flow: column;
    padding-bottom: 0px;
    border-left: 0;
    flex: 1 1 auto;
    position: relative;
    width: 100%;
    max-width: 100%;

    .ui-task-detail__header {
        // border-bottom: 1px solid #f0f0f0;
        border-radius: 4px 4px 0 0;
        background: #fff;
        height: auto;
        padding: 0 10px;

        .ui-task-detail-header__summary {
            margin-bottom: 10px;
            overflow: hidden;
            line-height: 32px;

            .task-basic-title {
                position: relative;
                border: none;
                resize: none;
                outline: none;
                box-shadow: none;
                font-size: 18px;
                color: #303030;
                font-weight: 500;
                line-height: 24px;
            }

            .ant-input {
                border: none;
            }

            .ant-input:focus {
                border-color: #5caff2;
                box-shadow: none;
                background-color: #f8f8f8;
            }
        }
    }

    .ui-task-detail__container {
        height: 100%;
        background-color: #fff;
    }
}



.ui-task-primary-fields {
    display: flex;
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
    padding-left: 10px;
    padding-right: 10px;
}



.ui-task-detail__main {
    margin-right: 0;
    border-right: 1px solid #e8e8e8;
    box-shadow: none;

    .ui-task-detail__tabs {
        padding: 0 10px;
        height: 100%;
    }

    .task-desc {
        z-index: 0;
        padding-top: 20px;
    }

    .task-detail-module {
        margin: 0 20px 0;
        padding-top: 20px;

        .task-detail-module-title {
            font-size: 15px;
            color: #303030;
            font-weight: 500;
            display: flex;
            align-items: center;

            .task-detail-module-title-text {
                flex: 1 1 auto;
            }
        }

        .task-desc-content {
            position: relative;

            .task-desc .task-desc-fullscreen-btn {
                display: flex;
                justify-content: flex-end;
                margin-top: -20px;
            }

            .richtext-input .richtext-input-editor-wrapper {
                position: relative;
                height: 100%;
            }

            .task-desc-btns {
                display: flex;
                justify-content: flex-end;
                margin-top: 10px;
            }
        }
    }
}

.ui-task-detail__side {
    min-width: 240px;
    max-width: 460px;
    // flex: 0 0 32%;
}

.main-card {
    background-color: #fff;
    height: 91vh;
    overflow: hidden auto;
    box-shadow: 0px 0px 1px rgba(48, 48, 48, .25), 0px 12px 24px rgba(48, 48, 48, .15);
}
</style>
