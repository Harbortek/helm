<template>
    <div style="height: 100%;">
        <router-view class="tracker-group-main" style="height: 100%;" />
    </div>
</template>
<script>
import {
    findOneProject,
} from "@/services/tracker/ProjectService";

export default {
    name: "TrackerGroupPage",
    components: {},
    data() {
        return {
            trackerGroups: []
        };
    },
    watch: {
        $route: function (newRoute) {
            let matched = newRoute.matched
            this.$nextTick(() => {

                for (let i = 0; i < matched.length; i++) {
                    if (matched[i].name === 'trackerGroup') {
                        this.gotoFirst()
                        break;
                    }
                }
            })
        }
    },
    mounted() {
        this.gotoFirst()
    },
    methods: {
        gotoFirst() {
            let projectId = this.$route.params.projectId
            let groupId = this.$route.params.groupId
            let trackerId = this.$route.params.trackerId
            if (groupId > 0 && !trackerId) {
                findOneProject(projectId).then(resp => {
                    this.trackerGroups = resp.trackerGroups;
                    for (let i = 0; i < this.trackerGroups.length; i++) {
                        const tg = this.trackerGroups[i]
                        if (tg.id === groupId) {
                            trackerId = tg.trackers[0].id

                            break;
                        }
                    }
                    if (trackerId) {
                        this.$router.push({
                            path: '/tracker/project/' + projectId + '/trackerGroup/' + groupId + '/trackerItems/' + trackerId,
                        })
                    }

                })
            }
        }
    },
};
</script>
<style lang="less" scoped>
.tracker-group-main {
    background-color: white;
    height: 100%;
}
</style>
