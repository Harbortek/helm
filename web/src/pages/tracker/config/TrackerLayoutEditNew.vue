<template>
    <a-card title="创建" class="main-card">
        <div class="" style="position: relative;">
            <span style="inset: 0px;position: absolute;z-index: 5;"></span>
            <a-form ref="itemForm" layout="horizontal" :model="formData">
                <a-row :gutter="15">
                    <a-form-item ref="name" label="标题" prop="name" required>
                        <a-input v-model="formData.name" @blur="() => {
                $refs.name.onFieldBlur();
            }" />
                    </a-form-item>
                </a-row>
                <a-row :gutter="15">
                    <a-col :span="12">
                        <a-form-model-item ref="trackerId" label="工作项类型" placeholder="工作项类型" prop="trackerId">
                            <tracker-select v-model="formData.trackerId" :projectId="projectId"></tracker-select>
                        </a-form-model-item>
                    </a-col>
                    <a-col :span="12">
                        <a-form-model-item ref="sprintId" label="所属迭代" placeholder="工作项迭代" prop="sprintId">
                            <sprint-select v-model="formData.sprintId" :projectId="projectId"></sprint-select>
                        </a-form-model-item>
                    </a-col>
                </a-row>

                <a-row :gutter="15">
                    <span v-if="isCustomConfig" @click="onClickSelectSpan"
                        :class="{ 'clickable-zoom': true, actived: selectTab == 'field' }"
                        style="inset: 0px -16px;position: absolute;z-index: 20;"></span>
                    <a-col :span="12" v-for="f in customerFields" :key="f.id">
                        <a-form-item :label="f.name" :prop="f.name" :required="f.required">

                            <ItemCustomFieldsShow :fields="f" v-model="formData.values[f.id]" :projectId="projectId" 
                                :trackerId="tracker?.id"></ItemCustomFieldsShow>

                            <!-- <a-input v-if="f.inputType == 'TEXT'" :placeholder="f.name" />
                            <a-textarea v-else-if="f.inputType == 'TEXT_AREA'" :placeholder="f.name" auto-size />
                            <simple-editor ref="editor" v-else-if="f.inputType == 'WIKI'"
                                v-model="formData[f.systemProperty]" :showToolbar="editorInEditMode"
                                @focus="editorInEditMode = true" />

                            <a-select v-else-if="f.inputType == 'OPTIONS'" :placeholder="f.name">

                            </a-select>
                            <a-select v-else-if="f.inputType == 'MULTI_OPTIONS' || f.inputType == 'WORK_ITEM'"
                                :mode="'multiple'" :placeholder="f.name" v-model="formData.values[f.id]">

                            </a-select>
                            <a-select v-else-if="f.inputType == 'BOOL' || f.inputType == 'USER' || f.inputType == 'MEMBERS'"
                                :placeholder="f.name">

                            </a-select>

                            <a-date-picker v-else-if="f.inputType == 'DATE'" style="width:100%" :placeholder="f.name" />
                            <a-time-picker v-else-if="f.inputType == 'TIME'" style="width:100%" use24-hours />

                            <a-input v-else :placeholder="f.name" /> -->
                        </a-form-item>
                    </a-col>
                </a-row>
                <div v-for="item in sections" :key="item.value">
                    <a-row :gutter="15" v-if="item.value == 'DETAIL'">
                        <a-col :span="24">
                            <a-form-item ref="owner" label="描述" prop="description" required>
                                <simple-editor v-model="formData.description" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="12">
                            <a-form-item ref="owner" label="负责人" prop="owner" required>
                                <project-user-select v-model="formData.owner.id" :projectId="projectId" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="12">
                            <a-form-item label="优先级" prop="projectId" :span="12" required>
                                <priority-select v-model="formData.priority" />
                            </a-form-item>
                        </a-col>
                    </a-row>

                    <fieldset v-else-if="item.value == 'RELATED_ITEMS'">
                        <TrackerRelatedItem style="margin:0" :projectId="projectId" :isToolBar="true"
                            :trackerId="tracker.id" :relatedWorkItems="formData.relatedWorkItems"></TrackerRelatedItem>
                    </fieldset>

                    <fieldset v-else-if="item.value == 'CYCLE_PROGRESS'">
                        <TrackerCycleProgress style="margin:0" :formData="formData"></TrackerCycleProgress>
                    </fieldset>
                    <fieldset v-else-if="item.value == 'WORK_HOURS'">
                        <a-form-model-item label="预计工时" prop="watchers" style="width:50%">
                            <a-input-search style="margin-left:10px;" v-model="formData.estimateWorkingHours"
                                placeholder="请输入数字">
                                <a-button slot="enterButton">
                                    小时
                                </a-button>
                            </a-input-search>
                        </a-form-model-item>
                    </fieldset>
                    <fieldset v-else-if="item.value == 'TEST_CASES'">
                        <div style="font-size:14px;color:#000000D9">测试</div>
                        <tracker-test-cases style="margin-left:10px;" :formData="formData"
                            :isShowDialog="item == 'TEST_CASES'"></tracker-test-cases>
                    </fieldset>
                    <fieldset v-else-if="item.value == 'RELATED_WIKI'">
                        <TrackerRelatedWiki style="margin:0" :projectId="projectId" :trackerItem="formData">
                        </TrackerRelatedWiki>
                    </fieldset>

                    <fieldset v-else-if="item.value == 'ATTACHMENTS'">
                        <TrackerAttachment style="margin:0" :trackerItem="formData"></TrackerAttachment>
                    </fieldset>

                    <fieldset v-else-if="item === 'HYPERLINKS'">
                        <TrackerHyperlinks style="margin:0" :tracker-item="formData"
                            :isShowDialog="item === 'HYPERLINKS'"></TrackerHyperlinks>
                        <!-- <tracker-hyperlinks :tracker-item="trackerItem" :isShowDialog="currentTab === 'HYPERLINKS'" />  -->
                    </fieldset>

                </div>

                <fieldset>
                    <a-form-item label="关注者" prop="watchers" :span="24">
                        <role-members-table v-model="formData.watchers" :projectId="projectId" scope="SCOPE_TRACKER"
                            :title="'添加需要关注的成员'" />
                    </a-form-item>
                </fieldset>
            </a-form>
        </div>
    </a-card>

</template>
<script>
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import SprintSelect from '@/components/select/SprintSelect.vue';
import ProjectSelect from '../../../components/select/ProjectSelect.vue';
import TrackerSelect from '../../../components/select/TrackerSelect.vue';
import ProjectUserSelect from '../../../components/select/ProjectUserSelect.vue';
import PrioritySelect from '../../../components/select/PrioritySelect.vue';
import SimpleEditor from '../../../components/editor/SimpleEditor.vue';
import RoleMembersTable from '../../../components/table/RoleMembersTable.vue';
import { mapGetters, mapState, mapMutations } from "vuex";
import AdminLayout from '../../../layouts/AdminLayout.vue';
import TrackerLayoutKeyFieldsDialog from './TrackerLayoutKeyFieldsDialog';
import TrackerCycleProgress from '../items/TrackerCycleProgress.vue';
import TrackerRelatedItem from '../items/TrackerRelatedItem.vue';
import TrackerRelatedWiki from '../items/TrackerRelatedWiki.vue';
import TrackerHyperlinks from '../items/TrackerHyperlinks.vue';
import TrackerAttachment from '../items/TrackerAttachment.vue';
import TrackerTestCases from '../items/TrackerTestCases.vue';
import ItemCustomFieldsShow from '@/components/tool/ItemCustomFieldsShow.vue';

export default {
    name: "TrackerLayoutEditNew",
    components: {
        ConfigPage, ProjectSelect, TrackerSelect, ProjectUserSelect, PrioritySelect, RoleMembersTable, SimpleEditor, AdminLayout,
        TrackerLayoutKeyFieldsDialog, TrackerCycleProgress, TrackerRelatedItem, TrackerRelatedWiki, TrackerHyperlinks,
        TrackerAttachment, TrackerTestCases, SprintSelect,ItemCustomFieldsShow
    },
    props: {
        tracker: Object,
        customerFields: Array,
        sections: Array,
        selectTab: String,
        isPreview: String,
    },
    data() {
        return {
            projectId: '',
            trackerId: '',
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
            isCustomConfig: true,
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
        isPreview: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.isCustomConfig = false
                }
            }
        }
    },
    computed: {
        ...mapGetters("account", ["user"]),
    },
    mounted() {
        this.projectId = this.$route.params.projectId
        this.trackerId = this.$route.params.trackerId
        this.$nextTick(() => {
            this.prepareFormData()
        })

    },
    methods: {
        onClickSelectSpan() {
            this.$emit('changeSelectTab', 'field');
        },
        prepareFormData() {
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

    },
};
</script>
<style lang="less" scoped>
.main-card {
    background-color: #fff;
    overflow: hidden auto;
    box-shadow: 0px 0px 1px rgba(48, 48, 48, .25), 0px 12px 24px rgba(48, 48, 48, .15);
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
</style>
