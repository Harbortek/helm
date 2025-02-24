<template>
    <div class="review-progress-header">
        <div class="item-space">
            <div class="item item-name">{{ value.name }}</div>
            <div class="item item-desc">
                <a-tooltip placement="topLeft" v-if="value.description">
                    <template slot="title">
                        <span>{{ value.description }}</span>
                    </template>
                    <a-icon type="info-circle" theme="filled" style="color: gray;" />
                </a-tooltip>
            </div>
            <div class="item item-status">
                <div class="status">
                    <span class="status__IN_PROGRESS" v-if="value.status === 'IN_PROGRESS'">进行中</span>
                    <span class="status__DONE" v-if="value.status === 'DONE'">已结束</span>
                </div>
                <div class="remain">剩余 <span
                        :style="{ color: (diffDays <= 0 ? 'rgb(246, 77, 62)' : ''), 'font-weight': 'bold' }">{{ diffDays
                        }}</span> 天</div>
                <a-tooltip placement="topLeft">
                    <template slot="title">
                        <div>开始时间：{{ value.createDate | formatDate }}</div>
                        <div>结束时间：{{ value.dueDate }}</div>
                    </template>
                    <a-icon type="info-circle" style="color: gray;" />
                </a-tooltip>

            </div>
            <h-user-selector class="item item-reviewers" :projectId="projectId" :selectUserIds="value.reviewers"
                @SELECT_CHANGED="handleSelectChanged" />
            <div class="item item-status-op" style="gap: 8px;">
                <div class="item-status-op-me" style=""
                    v-if="value.status !== 'DONE' && value.reviewers?.find(t => t == currentUser.id)">
                    <button :disabled="value.dones?.find(t => t == currentUser.id)" type="button" v-action="'REVIEW_CREATE'"
                        class="ant-btn ant-btn-shadow" @click="handleReviewByMe(value.id)"><span>我已评审</span></button>
                </div>
                <div class="item-status-op-all" v-if="value.status !== 'DONE'">
                    <button type="button" class="ant-btn ant-btn-shadow" v-action="'REVIEW_CREATE'"
                        @click="handleFinishReview(value.id)"><span>结束评审</span></button>
                </div>
            </div>
        </div>
        <div class="item-space">
            <div class="item item-status-progress">
                <div class="overview-teiFTUTT1w">
                    <div class="ant-space ant-space-horizontal ant-space-align-center" style="gap: 24px;">
                        <div class="ant-space-item" style="font-weight: bold;">
                            <div>{{ calValue.total > 0 ? (calValue.passedItems / calValue.total * 100).toFixed(2) : 0 }}%评审通过</div>
                        </div>
                        <div class="ant-space-item" style="font-weight: bold;">
                            <div>已评用例 {{ calValue.passedItems + calValue.unPassedItems + calValue.suggestPassedItems }}
                                /{{
                                    calValue.total }}
                            </div>
                        </div>
                    </div>
                </div>
                <ul class="progress-status">
                    <li
                        :style="{ width: `${calValue.passedItems / calValue.total * 100}%`, backgroundColor: 'rgb(45, 211, 111)' }">
                    </li>
                    <li
                        :style="{ width: `${calValue.suggestPassedItems / calValue.total * 100}%`, backgroundColor: 'rgb(255, 191, 0)' }">
                    </li>
                    <li
                        :style="{ width: `${calValue.unPassedItems / calValue.total * 100}%`, backgroundColor: 'rgb(246, 77, 62)' }">
                    </li>
                    <li
                        :style="{ width: `${(1 - ((calValue.passedItems + calValue.suggestPassedItems + calValue.unPassedItems) / calValue.total)) * 100}%`, backgroundColor: 'rgb(222, 223, 229)' }">
                    </li>
                </ul>
            </div>
            <div class="item  item-status-info"><span>
                    <div class="" style="font-weight: bold;">{{ `${value.dones?.length || 0} / ${value.reviewers?.length ||
                        0}` }}
                    </div>
                    <div class="">已评人员</div>
                </span></div>
            <div class="item  item-status-info">
                <div>{{ value.createDate | formatDate }}</div>
                <div class="cell-bottom-2gZ_TqR6_N">创建日期</div>
            </div>
            <div class="item  item-status-info">
                <div :style="{ color: (diffDays <= 0 ? 'rgb(246, 77, 62)' : '') }">{{ value.dueDate }}</div>
                <div>截止日期</div>
            </div>
        </div>
    </div>
</template>
<script lang="js">
import HUserSelector from '@/components/select/HUserSelector.vue';
import store from '@/store'
import {
    findEnumsByCode
} from "@/services/system/EnumService";
import { calcDiffDays } from '@/utils/DateUtils.js'
import { formatDate } from '@/utils/DateUtils.js'
import { finishReview, finishReviewByMe, updateReviewersByReviewId } from '@/services/review/ReviewService';
export default {
    name: 'ReviewProgressHeader',
    components: { HUserSelector },
    model: {
        prop: 'value',
        event: 'UPDATE_VALUE'
    },
    props: {
        value: {},
        projectId: {}
    },
    data() {
        return {
            calValue: {},
            PROJECT_REVIEW_TYPES: []
        }
    },
    watch: {
        value: {
            handler(newVal) {
                if(!newVal){
                    return;
                }
                this.calValue = newVal;
                const userId = this.currentUser.id;
                let passedItems = 0;
                let unPassedItems = 0;
                let suggestPassedItems = 0;
                let unReviewedItems = 0;
                this.loadEnums().then(() => {
                    const reviewStatuses = newVal.reviewStatuses ||[];
                    for (let i = 0; i < reviewStatuses.length; i++) {
                        const rs = reviewStatuses[i];
                        if (rs.reviewerId == userId) {
                            for (let j = 0; j < this.PROJECT_REVIEW_TYPES.length; j++) {
                                const prt = this.PROJECT_REVIEW_TYPES[j];
                                if (prt.id == rs.statusId) {
                                    if ("通过" == prt.name) {
                                        passedItems++;
                                        break;
                                    }
                                    if ("不通过" == prt.name) {
                                        unPassedItems++;
                                        break;
                                    }
                                    if ("建议" == prt.name) {
                                        suggestPassedItems++;
                                        break;
                                    }
                                    if ("未评审" == prt.name) {
                                        unReviewedItems++
                                        break;
                                    }

                                }

                            }
                        }

                    }
                    this.calValue.passedItems = passedItems;
                    this.calValue.unPassedItems = unPassedItems;
                    this.calValue.suggestPassedItems = suggestPassedItems;
                    this.calValue.unReviewedItems = unReviewedItems;
                    console.log(newVal,this.calValue);
                });

            },
            immediate: false,
        },
    },
    filters: {
        formatDate(value) {
            return formatDate(Date.parse(value));
        },

    },
    computed: {
        diffDays() {
            let days=calcDiffDays(this.value.dueDate, new Date());
            return days<=0?0:days;
        },
        currentUser() {
            return store.getters["account/user"];
        }
    },
    created() {
        this.loadEnums();
    },
    methods: {

        loadEnums() {
            const that = this;
            let p = new Promise((reslove, reject) => {
                if (that.PROJECT_REVIEW_TYPES.length > 0) {
                    reslove();
                } else {
                    findEnumsByCode('PROJECT_REVIEW_TYPE').then((resp) => {
                        that.PROJECT_REVIEW_TYPES = resp || [];
                        reslove();
                    })
                }
            })
            return p;

        },
        handleSelectChanged(reviewers) {
            this.value.reviewers = reviewers;
            updateReviewersByReviewId(this.value.id, reviewers).then((res) => {
                this.$message.success('评审人员保存成功!');
            })
        },
        handleReviewByMe(reviewId) {
            finishReviewByMe(reviewId).then((res) => {
                this.$emit('REVIEW_LIST_RELOAD')
                this.$emit('UPDATE_VALUE', this.value)
            })
        },
        handleFinishReview(reviewId) {
            finishReview(reviewId).then((res) => {
                this.$emit('REVIEW_LIST_RELOAD')
                this.$emit('UPDATE_VALUE', this.value)
            })
        }
    }
}
</script>

<style lang="less" scoped>
.review-progress-header {
    width: 100%;
    background-color: #fff;
    margin-bottom: 20px;
    padding: 5px;

    .item-space {
        align-items: center;
        display: flex;

        .item-name {
            font-size: 16px;
            font-weight: bold;
            margin-right: 10px;
            max-width: 500px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;

        }


        .item-status {
            margin-right: 16px;

            .status {
                .status__IN_PROGRESS {
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    overflow: hidden;
                    display: inline-block;
                    padding: 0px 4px;
                    line-height: 16px;
                    border: 1px solid rgba(255, 170, 33, 0.5);
                    border-radius: 2px;
                    font-size: 12px;
                    color: rgb(255, 170, 33);
                }

                .status__DONE {
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    overflow: hidden;
                    display: inline-block;
                    padding: 0px 4px;
                    line-height: 16px;
                    border: 1px solid rgba(109, 118, 148, 0.5);
                    border-radius: 2px;
                    font-size: 12px;
                    color: rgb(109, 118, 148);
                }
            }

            .remain {
                font-size: 14px;
                line-height: 20px;
                padding: 0 8px;
            }
        }

        .item-reviewers {
            position: relative;
            height: 32px;
        }

        .item-desc {
            cursor: pointer;
            margin-right: 20px;
            box-sizing: content-box;
        }

        .item-status-op {
            gap: 8px;
            margin-left: auto;
            display: inline-flex;
        }

        .item-status-progress {
            border-right: 1px solid #ddd;
            display: flex;
            flex: 1 1;
            -ms-flex-direction: column;
            flex-direction: column;
            padding-right: 20px;

            .progress-status {
                width: 100%;
                height: 16px;
                list-style: none;
                margin-top: 10px;
                width: 100%;
                height: 100%;
                display: inline-flex;
                min-width: 160px;
                box-sizing: border-box;
                border-radius: 3px;
                margin-right: -2px;
                background: rgb(255, 255, 255);
                padding-inline-start: 0px;

                li {
                    height: 16px;
                    margin: 0px 2px 0px 0px;
                    border: 0px solid transparent;
                    transition: all 0.2s cubic-bezier(0.645, 0.045, 0.355, 1) 0s;
                    cursor: pointer;

                    &:first-of-type {
                        border-top-left-radius: 3px;
                        border-bottom-left-radius: 3px;
                    }
                }
            }
        }

        .item-status-info {
            align-items: center;
            display: -ms-flexbox;
            display: flex;
            -ms-flex-direction: column;
            flex-direction: column;
            padding: 10px 20px;
        }

        .item {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            display: flex;
        }
    }
}
</style>