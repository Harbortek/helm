<template>
    <div :class="!isToolBar ? 'task-detail-module' : ''">
        <a-form-model-item label="关联测试结果" prop="relatedWorkItems" :span="24">
            <vxe-table ref="optionsTable" :loading="loading" :show-header="false" :data="testResult"
                :row-config="{ isHover: true }" stripe>
                <vxe-column field="" title="" :width="90">
                    <template #default="{ row }">
                        <a-tooltip :title="'测试运行：' + row.testRun?.name" :overlayStyle="{ fontSize: '10px' }"
                            :mouseEnterDelay="0.01">
                            <a-tag class="a-tag-box">
                                {{ row.testRun?.name }}</a-tag>
                        </a-tooltip>
                    </template>
                </vxe-column>

                <vxe-column field="" title="" :width="90">
                    <template #default="{ row }">
                        <a-tooltip v-if="currentProjectKeyName"
                            :title="'用例编号：' + currentProjectKeyName + '-' + row.testCase?.itemNo"
                            :overlayStyle="{ fontSize: '10px' }" :mouseEnterDelay="0.01">
                            <a-tag class="a-tag-box">
                                {{ currentProjectKeyName + '-' + row.testCase?.itemNo }}</a-tag>
                        </a-tooltip>
                    </template>
                </vxe-column>

                <vxe-column field="name" title="" width="">
                    <template #default="{ row }">
                        <a-icon v-if="row.testCase?.icon" :component="row.testCase?.icon" />&nbsp;
                        <span>{{ row.testCase?.name }}</span>
                    </template>
                </vxe-column>

                <vxe-column v-if="!isToolBar" field="" title="" width="80">
                    <template #default="{ row }">
                        <div v-if="row.testCase?.resultId" class="transition-status-right">
                            {{ void (result = resultObject[row.testCase?.resultId]) }}
                            <span class="ui-tag-status"
                                :style="{ color: result?.color, 'border-color': result?.color }">{{
        result?.name }}</span>
                        </div>
                    </template>
                </vxe-column>

                <vxe-column v-if="!isToolBar" field="" title="操作" header-align="center" width="100">
                    <template #default="{ row }">
                        <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                            <template slot="title">
                                <span>新窗口打开</span>
                            </template>
                            <vxe-button type="text" icon="vxe-icon-edit" @click="onEditItem(row)"></vxe-button>
                        </a-tooltip>
                        <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                            <template slot="title">
                                <span>删除</span>
                            </template>
                            <vxe-button type="text" icon="vxe-icon-delete" @click="onDeleteItem(row)"
                                :disabled="trackerItem?.notPagePerm"></vxe-button>
                        </a-tooltip>
                    </template>
                </vxe-column>

                <vxe-column v-if="isToolBar" field="" title="" width="100">
                    <template #default="{ row }">
                        <div style="height:20px;" @mouseover="operateMouseOver($event)"
                            @mouseleave="operateMouseLeave($event)">
                            <div id="status-id">
                                <div v-if="row.testCase?.resultId" class="transition-status-right">
                                    {{ void (result = resultObject[row.testCase?.resultId]) }}
                                    <span class="ui-tag-status"
                                        :style="{ color: result?.color, 'border-color': result?.color }">{{
        result?.name }}</span>
                                </div>
                            </div>
                            <div id="operate-id" style="cursor:pointer" class="status-operate-class">
                                <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                                    <template slot="title">
                                        <span>新窗口打开</span>
                                    </template>
                                    <vxe-button type="text" icon="vxe-icon-edit" @click="onEditItem(row)"></vxe-button>
                                </a-tooltip>
                                <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                                    <template slot="title">
                                        <span>删除</span>
                                    </template>
                                    <vxe-button type="text" icon="vxe-icon-delete" :disabled="trackerItem?.notPagePerm"
                                        @click="onDeleteItem(row)"></vxe-button>
                                </a-tooltip>
                            </div>
                        </div>
                    </template>
                </vxe-column>
            </vxe-table>
        </a-form-model-item>
    </div>
</template>

<script>
import { mapGetters } from "vuex";
import { findTestResultsByTrackerItemId, unlinkTestResultWithDownstreamTrackerItems,findUpstreamTrackerTestResultsByTrackerItemId } from '@/services/test/TestRunService'
import TrackerItemSelectModal from '@/components/dialog/TrackerItemSelectModal'
import VXETable from "vxe-table";
import { findEnumsByCode, } from "@/services/system/EnumService";

export default {
    name: 'TrackerRelatedTest',
    components: { TrackerItemSelectModal, },
    props: {
        projectId: String,
        trackerId: String,
        itemId: String,
        isShowDialog: Boolean,
        isToolBar: Boolean,
        trackerItem: Object,
    },
    data() {
        return {
            tableData: [],
            testResult: [],
            resultObject: {},
            loading: false,
            trackerRelations: [],
            itemIdChildren: {},
            tracker: { id: '' },
        }
    },
    computed: {
        ...mapGetters("project", ["currentProjectKeyName"]),
    },
    watch: {
        isShowDialog: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadTestResult();
                }
            }
        }
    },
    mounted() {
        findEnumsByCode('TEST_CASE_RESULT').then((resp) => {
            resp.forEach(v => {
                this.$set(this.resultObject, v.id, v)
            })
        });
    },
    methods: {
        loadTestResult() {
            if (this.itemId) {
                findUpstreamTrackerTestResultsByTrackerItemId(this.itemId).then(res => {
                console.log("linkTestResultWithDownstreamTrackerItems",res)
                    this.testResult = res
                    res.forEach(result => {
                        result.testRun.items?.forEach(item => {
                            if (item.itemId == result.testCase.id) {
                                this.$set(result.testCase, 'resultId', item.resultId)
                            }
                        })
                    })
                })
            }
        },
        operateMouseOver(e) {
            e.currentTarget.querySelector("#status-id").className = 'status-operate-class'
            e.currentTarget.querySelector("#operate-id").className = ''
        },
        operateMouseLeave(e) {
            e.currentTarget.querySelector("#status-id").className = ''
            e.currentTarget.querySelector("#operate-id").className = 'status-operate-class'
        },

        onEditItem(row) {
            this.onClickTestResult(row)
            // const routeData = this.$router.resolve({
            //     path: `/tracker/project/${this.projectId}/testRun/testRunCase/${row.testRun.id}/testRunExecute/${row.testCase.id}`
            // });
            // window.open(routeData.href, '_blank');
        },
        onDeleteItem(row) {
            if (this.itemId) {
                unlinkTestResultWithDownstreamTrackerItems(row.id,this.itemId).then(resp => {
                    VXETable.modal.message({ content: '删除成功', status: 'success' })
                    this.loadTestResult();
                })
            }
        },
        refresh() {
            this.$emit("refresh")
        },
        onClickTestResult(row) {
            let testResultMenu=this.findTestResultPath();
            if(testResultMenu&&testResultMenu.path){
                const routeData = this.$router.resolve({
                    path: testResultMenu.path+`/testRunCase/${row.testRun.id}/testRunExecute/${row.id}`
                });
                window.open(routeData.href, '_blank');
            }else{
                // 提示用户配置测试运行页面
                this.$message.warning("请先在系统中配置测试运行页面。")
            }
        },
        findTestResultPath() {
            // 递归查找包含testRun的菜单项
            let sideMenu=this.$store.getters['account/sideMenu']
            const findTestResultMenu = (menu) => {
                if (menu.path && menu.path.includes('testRun')) {
                    return menu;
                }
                if (menu.children) {
                    for (const child of menu.children) {
                        const found = findTestResultMenu(child);
                        if (found) return found;
                    }
                }
                return null;
            }

            // 遍历菜单数组查找testRunExecute
            for (const menu of sideMenu) {
                const testResultMenu = findTestResultMenu(menu);
                if (testResultMenu) {
                    return testResultMenu;
                }
            }
            return null;
        },
    },
}
</script>

<style lang="less" scoped>
.task-detail-module {
    margin: 0 20px 0;
}

.transition-status-right {
    display: flex;
    align-items: center;
    white-space: nowrap;

    .ui-tag-status {
        max-width: 110px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 12px;
        box-sizing: border-box;
        display: inline-block;
        height: 20px;
        line-height: 18px;
        transition: border-color .2s;
        border: 1px solid;
        border-radius: 20px;
        padding: 0px 8px;
    }
}

.a-tag-box {
    cursor: pointer;
    border: none;
    background-color: rgba(144, 144, 144, .15);
}

.relation-table-border {
    border: none
}

.relation-table-border input {
    border: none
}

.status-operate-class {
    display: none;
}
</style>