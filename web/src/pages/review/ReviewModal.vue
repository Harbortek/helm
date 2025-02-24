<template>
    <vxe-modal transfer class="review-modal" :value="value" :title="mode == 'ADD' ? '发起评审' : '修改评审'" width="800" resize
        @hide="handleClose" :mask="true" :esc-closable="true" :show-footer="true">
        <template #default>
            <vxe-form ref="xForm" :data="formData" :rules="formRules" title-align="right" title-width="80"
                style="height: 600px;">
                <vxe-form-item title="标题" field="name" span="24">
                    <template #default="{ data }">
                        <vxe-input v-model="data.name" placeholder="请输入标题"></vxe-input>
                    </template>
                </vxe-form-item>
                <vxe-form-item title="发起人" field="creator" span="24">
                    <template #default="{ data }">
                        <h-avatar :userId="data.creator?.id" :name="data.creator?.name"
                            :icon="data.creator?.icon"></h-avatar>
                    </template>
                </vxe-form-item>
                <vxe-form-item title="所在项目" field="projectId" span="24">
                    <template #default="{ data }">
                        <project-select v-model="data.projectId" :selectFirst="true"
                            @change="onProjectChange" :getPopupContainer="(trigger) => trigger.parentNode" />
                    </template>
                </vxe-form-item>
                <vxe-form-item title="评审者" field="reviewers" span="24">
                    <template #default="{ data }">
                        <project-user-select v-model="data.reviewers" placeholder="请选择评审人" mode="multiple"
                            :projectId="data.projectId" :getPopupContainer="(trigger) => trigger.parentNode"/>
                    </template>
                </vxe-form-item>
                <vxe-form-item title="截止时间" field="dueDate" span="24">

                    <template #default="{ data }">
                        <vxe-input v-model="data.dueDate" type="date" placeholder="请选择截止日期"
                            :disabled-method="disabledDateMethod" :editable="false" clearable></vxe-input>
                    </template>

                </vxe-form-item>
                <vxe-form-item title="描述" field="description" span="24">
                    <template #default="{ data }">
                        <vxe-textarea v-model="data.description" placeholder="请填写描述信息" rows="6"></vxe-textarea>
                    </template>
                </vxe-form-item>
                <vxe-form-item title="评审项" field="total" span="24">
                    <template #default="{ data }">
                        <div class="review-items">
                            <span class="review-items-total">{{ data.total }}</span>项已选
                            <span class="review-items-op" @click="handleAddRef">添加评审项<a-icon type="caret-right" /></span>
                        </div>
                    </template>
                </vxe-form-item>
            </vxe-form>
            <ReviewItemModal ref="reviewItem" v-model="reviewItemOpened" :reviewId="reviewId"
                :project-id="formData.projectId" @REVIEW_ITEM_SAVE="handleReviewItemSave" :refItems="refItems">
            </ReviewItemModal>
        </template>
        <template #footer>
            <vxe-button status="primary" @click="handleSubmit" :loading="saving">{{ mode == 'ADD' ? '发起评审' : '保存'
            }}</vxe-button>
            <vxe-button status="info" @click="handleClose">取消</vxe-button>
        </template>
    </vxe-modal>
</template>
<script lang="js">
import ProjectSelect from "@/components/select/ProjectSelect.vue";
import HAvatar from '@/components/avatar/h-avatar.vue';
import store from '@/store'
import { updateOneReview, findOneReview } from '@/services/review/ReviewService';
import { calcDiffDays, formatDate } from '@/utils/DateUtils.js'
import ReviewItemModal from './review-progress/ReviewItemModal.vue'
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
export default {
    name: 'ReviewModal',
    components: { ProjectUserSelect, ReviewItemModal, HAvatar, ProjectSelect },
    model: {
        prop: 'value',
        event: 'UPDATE_VALUE'
    },
    props: {
        value: {},
        mode: {
            type: String,
            default: 'ADD'
        }
    },
    computed: {
        currentUser() {
            return store.getters["account/user"];
        },
    },
    data() {
        return {
            reviewItemOpened: false,
            saving: false,
            reviewId: {},
            projectId: '',
            formData: {
                id: null,
                name: null,
                creator: {
                    id: store.getters["account/user"].id
                },
                projectId: null,
                dueDate: null,
                description: null,
                reviewers: [],
                total: '',
            },
            formRules: {
                name: [
                    { required: true, message: '请输入标题' },
                    { min: 3, message: '至少3个字符', trigger: 'blur' }
                ],
                reviewers: [
                    { required: true, message: '请选择评审人' }
                ],
                projectId: [
                    { required: true, message: '请选择项目' }
                ],
                sex: [
                    { required: true, message: '请选择性别' }
                ],
                dueDate: [
                    { required: true, message: '请输入截止日期' }
                ],
                keyItems: [
                    { required: false, message: '请添加评审范围' }
                ]
            },
            refItems: {
                deliverables: this.formData?.deliverables || [],
                documents: this.formData?.documents || [],
                milestones: this.formData?.milestones || [],
                projectPlans: this.formData?.projectPlans || [],
                sprints: this.formData?.sprints || [],
                targetVersions: this.formData?.targetVersions || [],
                tasks: this.formData?.tasks || [],
                trackerItems: this.formData?.trackerItems || [],
            },
        }
    },

    created() {
    },
    methods: {

        loadReview(projectId, reviewId) {
            this.reviewId = reviewId
            this.projectId = projectId
            if (this.reviewId) {
                findOneReview(reviewId).then(res => {
                    this.formData = res;
                    this.formData.projectId=projectId
                    this.refItems={
                        deliverables: this.formData?.deliverables || [],
                        documents: this.formData?.documents || [],
                        milestones: this.formData?.milestones || [],
                        projectPlans: this.formData?.projectPlans || [],
                        sprints: this.formData?.sprints || [],
                        targetVersions: this.formData?.targetVersions || [],
                        tasks: this.formData?.tasks || [],
                        trackerItems: this.formData?.trackerItems || [],
                    }
                })
            } else {
                this.formData = {
                    id: null,
                    name: null,
                    creator: {
                        id: store.getters["account/user"].id
                    },
                    projectId: null,
                    dueDate: null,
                    description: null,
                    reviewers: [],
                    total: 0
                }
            }
        },
        handleClose() {
            if (this.reviewId) {
                this.reviewId = '';
                this.formData = {
                    id: null,
                    name: null,
                    creator: {
                        id: store.getters["account/user"].id
                    },
                    dueDate: null,
                    description: null,
                    reviewers: [],
                    total: 0,
                }
            }
            this.refItems={
                deliverables: this.formData?.deliverables || [],
                documents: this.formData?.documents || [],
                milestones: this.formData?.milestones || [],
                projectPlans: this.formData?.projectPlans || [],
                sprints: this.formData?.sprints || [],
                targetVersions: this.formData?.targetVersions || [],
                tasks: this.formData?.tasks || [],
                trackerItems: this.formData?.trackerItems || [],
            },
            this.$emit('UPDATE_VALUE', false)
        },
        handleSubmit() {

            this.$refs.xForm.validate().then((resp) => {
                if (!!!resp) {
                    updateOneReview(Object.assign({}, this.formData, {
                        id: this.mode == 'ADD' ? null : this.formData.id,
                        creator: {
                            id: this.formData.creator?.id || this.currentUser.id
                        },
                        project: {
                            id: this.formData.projectId
                        },
                        ...this.keyItems
                    })).then((resp) => {
                        this.$emit('REVIEW_SAVE', this.formData)
                        this.$emit('UPDATE_VALUE', false)
                    })
                }
            })
        },
        handleAddRef() {
            this.reviewItemOpened = true;

        },
        onProjectChange(value) {
            if(value!=this.projectId){
                this.refItems= {
                    deliverables:  [],
                    documents:  [],
                    milestones:  [],
                    projectPlans: [],
                    sprints:  [],
                    targetVersions:  [],
                    tasks: [],
                    trackerItems: [],
                };
                this.formData.total=0;
            }else{
                this.projectId=''
            }
            
        },
        handleReviewItemSave(keyItems) {
            this.keyItems = keyItems;
            let t = 0;
            Object.keys(keyItems).forEach(i => t += keyItems[i].length);
            this.formData.total = t;
            this.refItems = keyItems
        },
        disabledDateMethod(params) {
            const { date } = params
            return calcDiffDays(formatDate(date), formatDate(new Date())) < 0
        }
    }
}
</script>

<style lang="less" scoped>
.review-modal {
    width: 100%;
    height: 100%;

    .review-items {
        box-shadow: rgba(0, 0, 0, 0.05) 0px 0px 0px 1px, rgba(0, 0, 0, 0.05) 0px 1px 6px 0px;
        border-radius: 3px;
        height: 48px;
        display: flex;
        -webkit-box-align: center;
        align-items: center;
        padding: 0px 8px 0px 24px;
        -webkit-box-pack: justify;
        justify-content: left;

    }

    .review-items-total {
        font-weight: bold;
        padding-right: 4px;
    }

    .review-items-op {
        font-weight: bold;
        color: rgb(0, 102, 255);
        cursor: pointer;
        padding-left: 16px;
        opacity: 0.9;
    }
}
</style>