<template>
    <vxe-modal transfer class="review-progress-modal" :showFooter="false" :value="value" :title="title" width="1200" height="95%" resize
        @hide="handleClose" :mask="true" :esc-closable="true" :show-footer="true">
        <template #default>
            <ReviewProgressHeader v-model="reviewModel" :project-id="projectId" @REVIEW_LIST_RELOAD="handleReload">
            </ReviewProgressHeader>
            <ReviewProgressBody ref="itemsRef" v-model="reviewModel" :project-id="projectId" :is-read="true" :is-show="isShowProgressBody"
                style="overflow-y: hidden;height:calc(100% - 145px);" @SELECTED_ITEMS="handleSelectItems"
                @refresh="loadReview" :refItems="refItems">
            </ReviewProgressBody>
        </template>
        <!-- <template #footer>
            <span style="margin-right: 10px;">已选 <span style="font-weight: bold;">{{ selectedItemsTotal || 0 }}</span>
                个项目</span>
            <vxe-button status="primary" @click="handleSubmit" :loading="saving">确定</vxe-button>
            <vxe-button status="info" @click="handleClose">取消</vxe-button>
        </template> -->
    </vxe-modal>
</template>
<script lang="js">
import ReviewProgressHeader from './ReviewProgressHeader.vue';
import ReviewProgressBody from './ReviewProgressBody.vue';

import { updateOneReview, findOneReview } from '@/services/review/ReviewService';
export default {
    name: 'ReviewProgressModal',
    components: { ReviewProgressBody, ReviewProgressHeader },
    model: {
        prop: 'value',
        event: 'UPDATE_VALUE'
    },
    props: {
        value: {},
    },
    data() {
        return {
            projectId: null,
            saving: false,
            title: '项目评审',
            reviewModel: {},
            selectedItems: {},
            isShowProgressBody: false,
        }
    },
    computed: {
        selectedItemsTotal() {
            const i = this.selectedItems
            const keys = Object.keys(i);
            if (keys.length === 0) {
                return 0;
            } else {
                let t = 0;
                keys.forEach((c) => {
                    if (c != 'selected') {
                        t += i[c].length
                    }
                })
                return t;

            }
        },
        refItems() {
            const a = {
                deliverables: this.reviewModel.deliverables || [],
                documents: this.reviewModel.documents || [],
                milestones: this.reviewModel.milestones || [],
                projectPlans: this.reviewModel.projectPlans || [],
                sprints: this.reviewModel.sprints || [],
                targetVersions: this.reviewModel.targetVersions || [],
                tasks: this.reviewModel.tasks || [],
                trackerItems: this.reviewModel.trackerItems || [],
            };
            return a
        }
    },
    created() {
    },
    methods: {
        handleReload() {
            this.$emit('REVIEW_LIST_RELOAD')
            this.$emit('UPDATE_VALUE', false)
            this.isShowProgressBody=false;
        },
        handleSelectItems(keyItems) {
            this.selectedItems = keyItems;
        },
        handleClose() {
            this.selectedItems = {};
            this.$refs.itemsRef.clear();
            this.$emit('UPDATE_VALUE', false)
            this.isShowProgressBody=false;
        },
        loadReview(projectId, reviewId) {
            this.projectId = projectId;
            if (reviewId) {
                findOneReview(reviewId).then(res => {
                    this.reviewModel = res;
                    this.isShowProgressBody=true;
                })
            } else {
                this.reviewModel = {};
                this.isShowProgressBody=true;
            }

        },
        handleSubmit() {
            let ojbectIds = [];
            for (let key in this.selectedItems) {
                this.reviewModel[key] = this.selectedItems[key]
                ojbectIds.push(...this.selectedItems[key])
            }
            this.reviewModel.reviewStatuses = this.reviewModel.reviewStatuses.filter(v => ojbectIds.indexOf(v.objectId) > -1);
            updateOneReview(Object.assign({}, this.reviewModel, {
                projectId: this.projectId,
                id: this.mode == 'ADD' ? null : this.reviewModel.id,
                creator: {
                    id: this.reviewModel.creator?.id || this.currentUser.id
                }
            })).then((resp) => {
                this.$emit('REVIEW_SAVE', this.reviewModel)
                this.handleClose();
            })

        },
    }
}
</script>

<style lang="less" scoped>
.review-progress-modal {
    width: 100%;
    height: 100%;


    /deep/ .vxe-modal--content {
        overflow: hidden;
    }


}
</style>