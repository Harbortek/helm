<template>
    <router-view class="project-config-main" style="height: 100%;" />
</template>
<script>
import ContentPage from '../../../components/page/content/ContentPage.vue';
export default {
    name: "ProjectConfigMainPage",
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