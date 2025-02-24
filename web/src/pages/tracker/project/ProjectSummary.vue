<template>
    <div>
        项目概览页面
    </div>
</template>
<script>
import ContentPage from '../../../components/page/content/ContentPage.vue';
export default {
    name: "ProjectSummary",
    components: { ContentPage },
    data() {
        return {
            activeTab: 'ALL',
        };
    },
    mounted() {
    },
    methods: {
        onTabChange(activeKey) {
            this.activeTab = activeKey
            console.log(this.activeTab)
        },
    },
};
</script>
<style lang="less" scoped>

</style>