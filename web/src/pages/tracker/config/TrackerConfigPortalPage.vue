<template>
    <config-page :routes="routes()">
        <vxe-table :data="operations" :row-config="{ isHover: true }" :show-footer="true" :footer-method="footerMethod"
            @cell-click="onClickRow">
            <vxe-column title="以下为当前项目使用的工作项类型">
                <template #default="{ row }">
                    <div class="icon-title-desc-item">
                        <div class="icon-container">
                            <i :class="row.icon"></i>
                        </div>
                        <div class="text-container">
                            <div class="ui-font-h3"><a-space>{{ row.title }}</a-space></div>
                            <div class="desc-container">
                                <span class="field-count">{{ row.description }}</span>
                            </div>
                        </div>
                        <div class="right-arrow">
                            <i class="vxe-icon-arrow-right"></i>
                        </div>
                    </div>
                </template>
                <template #footer="{ items, _columnIndex }">
                    <a-space>
                        <vxe-button status="danger" @click="onDeleteTracker">删除工作项类型</vxe-button>
                        <vxe-button status="danger" @click="onDeleteTrackerItems">清除所有相关工作项</vxe-button>
                    </a-space>
                </template>
            </vxe-column>

        </vxe-table>

    </config-page>
</template>
<script>
import {
    findOneTracker, deleteTracker,
} from "@/services/tracker/TrackerService";
import { deleteTrackerItemsByTrackerId } from "@/services/tracker/TrackerItemService";
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
import VXETable from "vxe-table";
export default {
    name: "TrackerConfigPortalPage",
    components: { ConfigPage },
    data() {
        return {
            tracker: {},
            operations: [
                { icon: 'vxe-icon-menu', title: '工作项属性', description: '工作项属性定义属于工作项的属性字段、类型', to: 'trackerFieldsConfig' },
                { icon: 'vxe-icon-table', title: '工作项布局', description: '视图可以定制工作项类型显示哪些功能标签页、详情标签页里显示哪些属性', to: 'trackerLayoutConfig' },
                { icon: 'vxe-icon-user', title: '工作项权限', description: '工作项权限可以定制对工作项整体的操作权限', to: 'trackerPermissionConfig' },
                { icon: 'vxe-icon-flow-branch', title: '工作项工作流', description: '工作流可以用于定制对应工作项的在不同状态阶段的流转', to: 'trackerStateTransitionConfig' },
                { icon: 'vxe-icon-bell', title: '工作项通知', description: '用于配置工作项操作以及系统属性变更通知方式和通知对象', to: 'trackerNotificationConfig' },
            ]
        };
    },
    computed: {
        trackerId() {
            return this.$route.params.trackerId
        }
    },
    mounted() {
        findOneTracker(this.trackerId).then(resp => {
            this.tracker = resp;
        })
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
            }]
        },
        footerMethod({ columns, data }) {
            return [
                columns.map(column => {
                    return null
                })
            ]
        },
        onClickRow(table) {
            this.$router.push({ name: table.row.to, params: { trackerId: this.tracker.id } })
        },
        onDeleteTracker() {
            VXETable.modal.confirm({
                title: '删除工作项类型',
                content: '工作项类型「' + this.tracker.name + '」的数据将被删除'
            }).then(type => {
                if (type === 'confirm') {
                    deleteTracker(this.tracker.id).then(resp => {
                        VXETable.modal.message({ content: '工作项类型「' + this.tracker.name + '」已被删除', status: 'info' })
                        this.$router.push({ name: 'trackerListConfig' })
                    })
                }
            })
        },
        onDeleteTrackerItems() {
            VXETable.modal.confirm({
                title: '删除相关工作项',
                content: '所有工作项类型为「' + this.tracker.name + '」的数据将被删除'
            }).then(type => {
                if (type === 'confirm') {
                    deleteTrackerItemsByTrackerId(this.tracker.id).then(resp => {
                        VXETable.modal.message({ content: '所有「' + this.tracker.name + '」的工作项已被删除', status: 'info' })
                    })
                }
            })
        }

    },
};
</script>
<style lang="less" scoped>
.icon-title-desc-item {
    height: 69px;
    padding: 15px 20px;
    align-items: center;
    height: auto;
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    padding: 0px;
    line-height: 1.4;
    cursor: pointer;

    .icon-container {
        align-self: flex-start;
        line-height: 0;
        padding-top: 3px;
        margin-right: 10px;
    }

    .text-container {
        margin: 0;
        flex-grow: 1;

        .ui-font-h3 {
            font-size: 15px;
            color: #303030;
            font-weight: 500;
        }

        .desc-container {
            color: #909090;
            font-size: 12px;
            line-height: 20px;

            .desc-seperate {
                margin: 0 10px;
            }
        }
    }

    .right-arrow {
        align-self: center;
    }
}
</style>
