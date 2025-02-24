<template>
    <a-modal v-model="visiable" title="规划至迭代" width="900" @cancel="onCancel" centered>
        <template slot="footer">
            <vxe-button content="取消" @click="onCancel"></vxe-button>
            <vxe-button status="primary" :disabled="!radioValue" @click="onOK" content="确定"></vxe-button>
        </template>
        <div class="sprint-model">
                <div style="margin-bottom:15px;">
                    <vxe-input v-model="keyword" placeholder="搜索迭代" style="width:240px" type="search" clearable></vxe-input>
                    <span style="margin-left: 18px;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;"
                        v-if="radioValue">已选择目标迭代：{{ selectedSprint.name }}</span>
                </div>
                <div class="sprint-list-header">
                    <div class="sprint-name" style="margin-left:30px;">迭代名称</div>
                    <div class="sprint-assign">迭代负责人</div>
                    <div class="sprint-section">迭代阶段</div>
                    <div class="sprint-start-time">开始日期</div>
                    <div class="sprint-end-time">完成日期</div>
                </div>

                <vxe-radio-group name="radioGroup" v-model="radioValue">
                    <div class="spring-list-content">
                        <div class="sprint-list" v-for="status in statusList" :key="status.id">
                            <div v-if="sprinttData(status).length > 0">
                                <div class="sprint-list-status sprint-n-cols">
                                    {{ status.name }}({{ sprinttData(status).length}})
                                </div>
                                <div v-for="sprint in sprinttData(status)" :key="sprint.id" @click="onSelectSprint(sprint)"
                                    class="sprint-list-item">
                                    <vxe-radio :label="sprint.id">
                                        <div class="sprint-list-item">
                                            <div class="sprint-name">
                                                <span class="sprint-name-text">{{ sprint.name }}</span>
                                            </div>
                                            <div class="sprint-assign">
                                                <h-avatar :name="sprint.owner?.name" :icon="sprint.owner?.icon"></h-avatar>
                                            </div>
                                            <div class="sprint-section">
                                                <a-tag style="border-radius: 10px;"
                                                    :style="{ color: sprint.status.color, borderColor: sprint.status.color }">
                                                    {{ sprint.status.name }}
                                                </a-tag>
                                            </div>
                                            <div class="sprint-start-time">{{ sprint.planStartDate }}</div>
                                            <div class="sprint-end-time">{{ sprint.planEndDate }}</div>
                                        </div>                            
                                    </vxe-radio>
                                </div>
                            </div>
                        </div>

                    </div>
                </vxe-radio-group>

        </div>
    </a-modal>
</template>
<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import moment from 'moment';
import { findSprints } from '@/services/plan/SprintService'
import { findEnumsByCode, } from "@/services/system/EnumService";
import { updateTrackerItemSprint } from "@/services/tracker/TrackerItemService"
import VXETable from "vxe-table";
export default {
    name: "SprintOperateDialog",
    components: {HAvatar},
    data() {
        return {
            keyword: '',
            formData: {
                name: '',

            },
            statusList: '',
            sprintList: [],
            radioValue: '',
            selectedSprint: '',

        };
    },
    props: {
        isShowDialog: {
            required: true
        },
        currentSelectedIds: {
            required: false
        },
        projectId: {
            required: true
        }
    },
    computed: {
        visiable: {
            get() {
                return this.isShowDialog
            },
            set(newValue) {
                return newValue
            }
        },
    },
    watch: {
        isShowDialog: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }

            }
        }
    },
    mounted() {
        // this.loadData();
    },
    methods: {
        loadData() {
            findSprints(this.projectId).then(resp => {
                if (!this.statusList) {
                    this.loadStatusData();
                }
                this.sprintList = resp;
            })
        },
        loadStatusData() {
            findEnumsByCode('PROJECT_STATUS_MEANING').then((resp) => {
                this.statusList = resp;
            });
        },
        onSelectSprint(sprint) {
            this.radioValue = sprint.id
            this.selectedSprint = sprint
        },
        sprinttData(status) {
            let sprints = this.sprintList;
            if (this.keyword) {
                var regexp = new RegExp(this.keyword)
                sprints = sprints.filter(item => regexp.test(item.name))
            }
            return sprints.filter(item => item.status.id == status.id)
        },
        onOK: function () {
            this.onCancel();
            console.log("currentSelectedIds", this.currentSelectedIds, this.radioValue)
            updateTrackerItemSprint(this.radioValue, this.currentSelectedIds).then(() => {
                VXETable.modal.message({ content: '规划成功 ' + this.currentSelectedIds.length + ' 个工作项到「' + this.selectedSprint.name + '」', status: 'success' })
                this.$emit("ok");
            })
        },
        onCancel: function () {
            this.$emit("cancel");
        },
    },
    created() { }
};
</script>
<style lang="less" scoped>
/deep/ .vxe-radio .vxe-radio--label {
    max-width: 100em;
}
.sprint-model {
    position: relative;
    .sprint-list-header {
        height: 48px;
        font-size: 15px;
        font-weight: 500;
        position: relative;
        display: flex;
        align-items: center;
        border-bottom: 1px solid #dfe1e5;
        border-top: 1px solid #d4d6d9;
    }

    .sprint-name {
        display: flex;
        align-items: center;
        width: 320px;
        padding-right: 10px;
    }

    .sprint-assign {
        width: 180px;
        display: flex;
        height: 100%;
        align-items: center;
    }

    .sprint-section {
        width: 140px;
        display: flex;
        align-items: center;
    }

    .sprint-start-time {
        width: 130px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .sprint-end-time {
        width: 130px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .spring-list-content {
        box-sizing: border-box;
        direction: ltr;
        height: 380px;
        position: relative;
        width: 950px;
        will-change: transform;
        overflow: auto;

        .sprint-list {
            width: auto;
            height: auto;
            max-width: 950px;
            overflow: hidden;
            position: relative;

            .sprint-list-status {
                font-size: 15px;
                font-weight: 600;
                height: 48px;
                align-items: flex-end;
                padding: 5px 0;
                display: flex;
                align-items: center;
                border-bottom: 1px solid #dfe1e5;
            }

            .sprint-list-item {
                height: 48px;
                background-color: transparent;
                transition: all .3s;
                cursor: pointer;
                display: flex;
                align-items: center;
                border-bottom: 1px solid #dfe1e5;
            }

            .sprint-list-item:hover {
                background-color: rgba(223, 225, 229, .5);
            }
        }
    }

}
</style>
