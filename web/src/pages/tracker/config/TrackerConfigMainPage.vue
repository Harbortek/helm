<template>
    <div>
        <router-view class="tracker-config-main" style="height: 100%;"></router-view>
    </div>
</template>
<script>
import ConfigPage from '../../../components/config-page/ConfigPage.vue';
export default {
    name: "TrackerConfigMainPage",
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
