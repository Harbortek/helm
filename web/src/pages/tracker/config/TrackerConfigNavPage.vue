<template>
    <div>
        <router-view class="tracker-config-nav" style="height: 100%;"></router-view>
    </div>
</template>
<script>
import ConfigPage from '../../../components/page/content/ContentPage.vue';
export default {
    name: "TrackerConfigNavPage",
    components: { ConfigPage },
    data() {
        return {
        };
    },
    mounted() {
    },
    methods: {
        
    },
};
</script>
<style lang="less" scoped>

</style>
