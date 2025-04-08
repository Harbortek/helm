<template>
    <div class="h-itemNo">
        <item-icon v-if="showIcon" :trackerType="trackerItem?.trackerType||trackerItem?.tracker"></item-icon>
        <span :style="{color: getColor(trackerItem?.severity?.color), textDecoration:isEnded?'line-through':'',
            }">
            {{ currentProjectKeyName?.toUpperCase() + '-' + trackerItem?.itemNo }}</span>
    </div>
</template>
  
<script>
import { mapGetters } from "vuex";
import ItemIcon from '@/components/icon/ItemIcon.vue';

export default {
    name: 'HItemNo',
    components: {
        ItemIcon,
    },
    data() {
        return {
        }
    },
    props: {
        trackerItem: {
            type: Object,
            default: () => {
                return {};
            }
        },
        showIcon: {
            type: Boolean,
            default: true,
        },
    },
    watch: {
    },
    computed: {
        ...mapGetters("project", ["currentProjectKeyName"]),
        isEnded() {
            if (this.trackerItem?.meaning?.code == 'CLOSED') {//this.trackerItem?.meaning?.code == 'ENDED'||
                return true;
            }
            return false;
        }
    },
    created() {
    },
    methods: {
        getColor(color) {
            if (color=='rgb(255, 255, 255)') {
                return 'red';
            }
            return color;
        }
    }
}
</script>
  
<style lang="less" scoped>
.h-itemNo {
    display: inline-block;
}
</style>
  