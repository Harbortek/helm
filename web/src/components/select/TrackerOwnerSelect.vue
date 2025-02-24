<template>
    <a-select v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled">
        <a-select-option v-for="item in data" :key="item.id" :title="item.name" :value="item.id">
            <h-avatar :name="item.name" :icon="item.icon"></h-avatar>
            <span class="domain-list-cell-subtext">({{ item.email }})</span>
        </a-select-option>
    </a-select>
</template>

<script>
import {
    findProjectUsers
} from "@/services/tracker/ProjectRoleMemberService";
import HAvatar from '@/components/avatar/h-avatar.vue';
export default ({
    name: "TrackerOwnerSelect",
    components:{HAvatar },
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        projectId: {
            required: true
        },
        trackerId: {
            required: true
        },
        value: {
            required: true
        },
        disabled: false,
    },
    watch: {
        projectId: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        },
        value: {
            immediate: true,
            handler: function (curVal, oldVal) {
                this.selectItem = curVal;
            }
        }
    },
    mounted() {
    },
    data() {
        return {
            selectItem: "",
            data: []
        };
    },
    methods: {
        loadData() {
            findProjectUsers(this.projectId).then(resp => {
                this.data = resp;
            });
        },
        onChange(v) {
            this.$emit("change", v);
        },
    }
});
</script>
<style lang="less" scoped>
.domain-list-cell-subtext {
    margin-left: 5px;
    font-size: 12px;
    color: #606060;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 180px;
    flex: 0 0 auto;
    color: var(--gray-80);
}
</style>
