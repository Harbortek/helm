<template>
    <div class="review-progress-body">
        <vxe-toolbar ref="toolbarRef" >
            <template #buttons>
                <div style="width:100%;height:34px">
                    <vxe-button :style="{visibility:isShowBatchReview?'visible':'hidden'}" v-action="'REVIEW_CREATE'"
                         :disabled="selectedItemsTotal==0" content="批量评审" @click="handleBatchReview"></vxe-button>
                </div>
                <a-form ref="xForm" style="width: 260px;position: absolute;right: 10px;" 
                    :labelCol="{ span: 6 }" :wrapperCol="{ span: 18}" :model="formData">
                    <a-form-item style="margin-bottom:0px" required label="项目" prop="projectId" >
                        <project-select  :disabled="true" v-model="formData.projectId" @change="onChangeProjectSelect"></project-select>
                    </a-form-item>
                </a-form>
            </template>
        </vxe-toolbar>
        <TableItemSelectView ref="tableItemSelectView" :reviewId="value?.id" :reviewStatuses="value?.reviewStatuses" 
                :reviewers="value?.reviewers" :projectId="formData.projectId" :initalData="refItems" :is-read="isRead"
                :editReviewId="reviewId" @trackerItemSelector="handleTrackerItemSelected"  style="overflow-y: hidden;"></TableItemSelectView>
        <ReviewOperationModal ref="reviewOperationModalRef" :selectedRows="selectedRows" :selectedItemsTotal="selectedItemsTotal"
            v-model="reviewOperationModalOpened" @UPDATE_VALUE="onSubmitResult"></ReviewOperationModal>
    </div>
</template>
<script>
import ProjectSelect from "@/components/select/ProjectSelect.vue";
import TableItemSelectView from '@/components/dialog/TableItemSelectView';
import ReviewOperationModal from './ReviewOperationModal.vue'
import { batchReviewResult } from '@/services/review/ReviewService';
import VXETable from "vxe-table";
import store from '@/store'
export default {
    name: 'ReviewProgressBody',
    components: { ReviewOperationModal,TableItemSelectView,ProjectSelect},
    model: {
        prop: 'value',
        event: 'UPDATE_VALUE'
    },
    props: {
        value: {},
        projectId: {},
        refItems:{},
        isShow:{},
        isRead:{},
        reviewId:{},
    },
    data() {
        return {
            reviewOperationModalOpened: false,
            formData:{
                projectId:'',
            },
            loading: false,
            selectedRows:[],
            tableData: [{
                
            }],
            tablePage: {
                currentPage: 1,
                pageSize: 10,
                totalResult: 0
            }
        }
    },
    watch: {
        isShow: {
            handler(newVal) {
                this.$nextTick(() => {
                    if(newVal&&this.formData.projectId){
                        this.$refs.tableItemSelectView.view();
                    }
                })
            },
            immediate: true,
        },
        projectId: {
            handler(newVal){
                this.formData.projectId=newVal;
            },
            immediate:true
        },
        // refItems: {
        //     handler(newVal) {
        //         console.log("refItems",newVal);
        //         if(!this.isShow&&this.value?.id){
        //             this.$nextTick(() => {
        //                 this.$refs.tableItemSelectView.view();
        //             })
        //         }
        //     },
        //     deep:true,
        //     immediate: true,
        // }
    },
    computed: {
        currentUser() {
            return store.getters["account/user"];
        },
        selectedItemsTotal() {
            const i = this.selectedRows
            const keys = Object.keys(i);
            if (keys.length === 0) {
                return 0;
            } else {
                let t = 0;
                keys.forEach((c) =>{
                    if(c!='selected'){
                        t += i[c].length
                    }
                }) 
                return t;

            }
        },
        isShowBatchReview(){
            let flag=this.value?.status !== 'DONE' && this.value?.reviewers?.find(t => t == this.currentUser.id)
            if(flag&&this.isRead){
                return true;
            }
            return false;
        }
    },
    created() {
    },
    mounted() {
        
    },
    methods: {
        clear(){
            this.$refs.tableItemSelectView.onClear();
        },
        onChangeProjectSelect(v){
            if(v){
                this.$nextTick(() => {
                    this.$refs.tableItemSelectView.view();
                })
            }
        },
        handleBatchReview() {
            this.reviewOperationModalOpened = true;
        },
        handleTrackerItemSelected(keyItems){
            // console.log("handleTrackerItemSelected",keyItems);
            this.selectedRows=keyItems;
            this.$emit('SELECTED_ITEMS',keyItems);
        },
        onSubmitResult(reviewStatuses){
            this.reviewOperationModalOpened = false;
            if(reviewStatuses&&this.value?.id){
                console.log("onSubmitResult",reviewStatuses);
                batchReviewResult(this.value.id,reviewStatuses).then(res=>{
                    VXETable.modal.message({ status: 'success', content: '操作成功' });
                    this.$emit("refresh",this.projectId,this.value.id)
                })
            }

        }
    }
}
</script>

<style lang="less" scoped>
.review-progress-body {
    width: 100%;
    height: 100%;

}
</style>