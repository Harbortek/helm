<template>
    <vxe-modal transfer class="review-item-modal" :value="value" :title="title" width="1200" height="95%" resize @hide="onCancel"
        :mask="true" :esc-closable="true" :show-footer="true">
        <template #default>
            <ReviewProgressBody ref="itemsRef" :isShow="value" :project-id="projectId" :reviewId="reviewId" @SELECTED_ITEMS="handleSelectItems" :refItems="refItems"></ReviewProgressBody>
        </template>
        <template #footer>
            <vxe-button status="primary" @click="onOk">确认</vxe-button>
            <vxe-button status="info" @click="onCancel">取消</vxe-button>
        </template>
    </vxe-modal>
</template>
<script lang="js">
import ReviewProgressBody from './ReviewProgressBody.vue';
export default {
    name: 'ReviewItemModal',
    components: { ReviewProgressBody },
    model: {
        prop: 'value',
        event: 'UPDATE_VALUE'
    },
    props: {
        value: {},
        projectId: {},
        refItems: {},
        reviewId:{},
    },
    data() {
        return {
            title: '项目评审',
            selectedItems:{},
        }
    },
    computed: {

    },
    created() {
    },
    methods: {
        handleSelectItems(keyItems) {
            this.selectedItems = keyItems;
        },
        onOk() {
            this.$refs.itemsRef.clear();
            this.$emit('UPDATE_VALUE', false)
            this.$emit('REVIEW_ITEM_SAVE',this.selectedItems)
        },
        onCancel() {
            this.$refs.itemsRef.clear();
            this.$emit('UPDATE_VALUE', false)
        }
    }
}
</script>

<style lang="less" scoped>
.review-item-modal {
    width: 100%;
    height: 100%;

}
</style>