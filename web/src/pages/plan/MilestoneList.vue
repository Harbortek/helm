<template>
    <config-page title="里程碑" description="里程碑是项目生命周期里的一些重要节点，是为了确保项目成功必须达到或完成的任务或指标，用来对项目的进程进行检查和控制。">
        <div>

        </div>
        <div class="milestone-timeline">
            <div class="vir-timeline-node-wrapper node-only" style="width:110px;">
                <div class="fix-height-area"><span class="" style="max-width: 122px; height: 20px;"><span
                            class=""></span></span>
                    <div class="node-point-wrapper start-node-point-wrapper">
                        <div class="point startPoint">
                            <div
                                style="border: solid 4px #bebfc2; border-radius: 50%; background-color: transparent; width: 14px; height: 14px;">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="dynamic-height-area">
                    <div class="milestone-fields">
                        <div class="timeline-node-field"></div>
                        <div class="timeline-node-field"></div>
                        <div class="timeline-node-field"></div>
                        <div class="timeline-node-field"></div>
                    </div>
                </div>
            </div>

            <div class="vir-timeline-node-wrapper" style="width:180px;" v-for="(item, index) in tableData" :key="index">

                <div class="fix-height-area">
                    <span style="max-width: 122px; height: 20px;" class="milestone-status">
                        <span class="ui-tag-status"
                            :style="{ color: getStatusColor(item.finished), 'border-color': getStatusColor(item.finished) }">
                            {{ item.name }}</span>
                    </span>
                    <div class="node-point-wrapper">
                        <div class="point"
                            :style="{ color: getStatusColor(item.finished), 'border-color': getStatusColor(item.finished) }">
                            <h-icon type="flag" />
                        </div>
                    </div>
                </div>
                <div class="dynamic-height-area">
                    <div class="milestone-fields">
                        <div class="timeline-node-field">状态：{{ item.finished ? '已完成' : '未完成' }}</div>
                        <div class="timeline-node-field">{{ formatDate(item.planEndDate) }}</div>
                        <div class="timeline-node-field"></div>
                        <div class="timeline-node-field"></div>
                    </div>
                </div>
            </div>

            <div class="vir-timeline-node-wrapper node-only" style="width:110px;">
                <div class="fix-height-area"><span class="" style="max-width: 122px; height: 20px;"><span
                            class=""></span></span>
                    <div class="node-point-wrapper end-node-point-wrapper">
                        <div class="point endPoint">
                            <div
                                style="border: solid 4px #bebfc2; border-radius: 50%; background-color: #bebfc2; width: 14px; height: 14px;">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="dynamic-height-area">
                    <div class="milestone-fields">
                        <div class="timeline-node-field"></div>
                        <div class="timeline-node-field"></div>
                        <div class="timeline-node-field"></div>
                        <div class="timeline-node-field"></div>
                    </div>
                </div>
            </div>
        </div>
        <div>
            <vxe-table size="small" ref="xTable" show-overflow border row-key :show-header="true" :auto-resize="true"
                :data="tableData" :checkbox-config="{ labelField: 'name' }"
                :row-config="{ keyField: 'id', isCurrent: true }">
                <vxe-column field="itemNo" title="ID" width="50px">
                    <template #default="{ row }">
                        {{ '#' + row.itemNo }}
                    </template>
                </vxe-column>
                <vxe-column field="name" title="名称">
                    <template #default="{ row }">
                        <div style="display: inline-flex;">
                            <div style="width:20px;">
                                <h-icon type="plan-group" v-if="row.type === 'GROUP'" />
                                <h-icon type="plan-tasks" v-else-if="row.type === 'TASK'" />
                                <h-icon type="plan-milestone" v-else-if="row.type === 'MILE_STONE'" />
                            </div> <a @click="showDetail(row)">{{ row.name }}</a>
                        </div>
                    </template>
                </vxe-column>

                <vxe-column field="status" title="状态" width="80px">
                    <template #default="{ row }">
                        {{ row.status?.name }}
                    </template>
                </vxe-column>
                <vxe-column field="planEndDate" title="结束日期" width="100px">
                    <template #default="{ row }">
                        {{ formatDate(row.planEndDate) }}
                    </template>
                </vxe-column>
                <vxe-column field="deliverableSummary" title="交付物" width="100px">
                    <template #default="{ row }">
                        <a @click="showDetail(row)"> {{ committedDeliverables(row.deliverables) }} / {{
                            row.deliverables.length }} </a>
                    </template>
                </vxe-column>

            </vxe-table>
        </div>
    </config-page>
</template>

<script>
import ConfigPage from '@/components/config-page/ConfigPage.vue';
import { findMilestones } from '@/services/plan/PlanService'
import { formatDate } from '@/utils/DateUtils'

export default {
    name: 'MilestoneList',
    components: { ConfigPage },
    data() {
        return {
            tableData: [],
        }
    },
    computed: {
        projectId() {
            return this.$route.params.projectId
        },
    },
    mounted() {
        this.loadData()
    },
    methods: {
        formatDate(v) {
            if (!v) return '-'
            else return formatDate(v)
        },
        getStatusColor(status) {
            if (status) {
                return '#24b47e'
            } else {
                return '#338fe5'
            }
        },
        loadData() {
            findMilestones(this.projectId).then(resp => {
                this.tableData = resp
            })
        },
        committedDeliverables(deliverables) {
            return deliverables.filter(f => { return f?.committed }).length
        },
        showDetail(row) {
            this.$router.push({ name: 'milestoneDetail', params: { itemId: row.id } })
        }
    },
}
</script>

<style lang="less" scoped>
.milestone-timeline {
    display: flex;
    flex-direction: row;
    height: 180px;

    .vir-timeline-node-wrapper {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;

        .fix-height-area {
            flex: 0 0 auto;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            padding-top: 30px;
            align-self: stretch;

            .milestone-status {
                display: flex;
                align-items: center;
                white-space: nowrap;

                .ui-tag-status {
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    font-size: 12px;
                    box-sizing: border-box;
                    display: inline-block;
                    height: 18px;
                    line-height: 18px;
                    transition: border-color .2s;
                    border: 1px solid;
                    border-radius: 20px;
                    padding: 0px 8px;
                }
            }

            .node-point-wrapper {
                position: relative;
                display: flex;
                align-self: stretch;
                align-items: center;
                justify-content: center;
                min-height: 32px;

                .point {
                    margin: 5px;
                    background-color: #fff;

                    &:before {
                        left: 0;
                        width: calc(50% - 6px);
                        top: calc(50% - 0.5px);
                        content: "";
                        height: 1px;
                        border-top: solid 1px #bebfc2;
                        position: absolute;
                    }

                    &:after {
                        right: 0;
                        width: calc(50% - 6px);
                        top: calc(50% - 0.5px);
                        content: "";
                        height: 1px;
                        border-top: solid 1px #bebfc2;
                        position: absolute;
                    }
                }
            }

            .node-point-wrapper.start-node-point-wrapper {
                padding-left: 20px;
                justify-content: flex-start;

                .point:after {
                    width: calc(100% - 38px);
                }

                .point:before {
                    width: 0;
                }
            }

            .node-point-wrapper.end-node-point-wrapper {
                padding-right: 20px;
                justify-content: flex-end;

                .point:before {
                    width: calc(100% - 38px);
                }

                .point:after {
                    width: 0;
                }
            }
        }

        .dynamic-height-area {
            flex: 1 1 auto;

            .timeline-node-field {
                color: #87888a;
                font-size: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
            }
        }
    }
}
</style>