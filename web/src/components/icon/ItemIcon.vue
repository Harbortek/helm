<template>
    <div class="t-icon">
        <a-tooltip v-if="trackerType?.icon" :title="'工作项类型：' + trackerType?.name" 
            :overlayStyle="{ fontSize: '10px' }" :mouseEnterDelay="showToolTip?0:100">
            <a-icon v-if="showColor" :style="{marginRight:'3px',color:trackerType?.color,backgroundColor:trackerType?.backgroundColor}"
                :component="trackerType?.icon" />
            <a-icon v-else :style="{marginRight:'3px'}" :component="trackerType?.icon" />
        </a-tooltip>
    </div>
</template>
  
<script>
export default {
    name: 'TIcon',
    data() {
        return {
        }
    },
    props: {
        trackerType:{
            type: Object,
        },
        showToolTip: {
            type: Boolean,
            default: true
        },
        showColor: {
            type: Boolean,
            default: false
        }
    },
    watch: {
        
    },
    created() {
    },
    methods: {

    }
}
</script>
  
<style lang="less" scoped>
.t-icon {
    display: inline-block;
}
</style>
  