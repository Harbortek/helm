<template>
    <vxe-select v-model="selectItem" :mode="mode" placeholder="请选择" @change="onChange" :disabled="disabled" v-bind="$attrs">
        <vxe-option v-for="item in data" :key="item.id" :label="item.name" :value="item.id">
            <template #default="{ option }">
                <h-avatar :name="option.name" :icon="option.icon"></h-avatar>
                <span class="domain-list-cell-subtext">({{ option.email }})</span>
            </template>
        </vxe-option>
    </vxe-select>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import {
    findProjectUsers
} from "@/services/tracker/ProjectRoleMemberService";

export default ({
    name: "ProjectUserSelect",
    components:{HAvatar },
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        projectId: {
            required: true
        },
        value: {
            required: true
        },
        disabled: false,
        mode: false,
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
                // console.log('this.selectItem', this.selectItem)
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
        onChange(e) {
            console.log(e)
            this.$emit("change", e.value);
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
