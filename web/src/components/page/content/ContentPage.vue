<template>
    <div class="content-page">
        <div class="page-header" v-if="title">
        </div>
        <div class="page-content">
            <slot></slot>
        </div>
    </div>
</template>
<script>
export default ({
    name: 'ContentPage',
    props: ['title', 'description'],
    computed: {},
    data() {
        return {
        };
    },
    mounted() {

    },
})
</script>
<style lang="less" scoped>
.content-page {
    flex: 1 1 auto;
    position: relative;
    width: 100%;
    padding: 10px;
    background-color: #fff;
    height: 100%;

    .page-header {
        flex: 0 0 auto;
        line-height: 1;
        padding: 20px;
        font-size: 18px;
        font-weight: 500;
        border-bottom: 1px solid #e8e8e8;
    }

    .page-content {
        padding-top: 10px;
        height: 100%;
    }
}
</style>

