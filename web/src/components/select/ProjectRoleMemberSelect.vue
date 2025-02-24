<template>
    <a-select v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled">
        <a-select-opt-group v-if="projectRoles.length > 0">
            <span slot="label">角色</span>
            <a-select-option v-for="item in projectRoles" :key="item.id" :title="item.name" :value="item.id">
                {{ item.name }} <span class="domain-list-cell-subtext">{{ item.description }}</span>
            </a-select-option>
        </a-select-opt-group>
        <a-select-opt-group v-if="specialRoles.length > 0">
            <span slot="label">特殊角色</span>
            <a-select-option v-for="item in specialRoles" :key="item.id" :title="item.name" :value="item.id">
                {{ item.name }}
            </a-select-option>
        </a-select-opt-group>
        <a-select-opt-group v-if="members.length > 0">
            <span slot="label">项目成员</span>
            <a-select-option v-for="item in members" :key="item.id" :title="item.name" :value="item.id">
                <h-avatar :name="item.name" :icon="item.icon"></h-avatar>
                <span class="domain-list-cell-subtext"> {{ item.description }} </span>
            </a-select-option>
        </a-select-opt-group>
    </a-select>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import {
    findProjectRolesAndMembers
} from "@/services/tracker/ProjectRoleMemberService";

export default ({
    name: "ProjectRoleMemberSelect",
    components: { HAvatar },
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        projectId: {
            required: true
        },
        scope: {
            required: true,
            default: 'SCOPE_PROJECT'
        },
        value: {
            required: false
        },
        exclude: {
            required: false,
            type: Array,
            default: () => { return [] }
        },
        disabled: false,
    },
    watch: {
        projectId: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        value: function (curVal, oldVal) {
        }
    },
    mounted() {
        this.loadData()
    },
    data() {
        return {
            selectItem: "",
            data: {
                projectRoles: [],
                specialRoles: [],
                members: []
            }
        };
    },
    computed: {
        projectRoles: function () {
            return this.data.projectRoles.filter(f => !this.isExcluded(f.id))
        },
        specialRoles: function () {
            return this.data.specialRoles.filter(f => !this.isExcluded(f.id))
        },
        members: function () {
            return this.data.members.filter(f => !this.isExcluded(f.id))
        }
    },
    methods: {
        loadData: function () {
            findProjectRolesAndMembers(this.projectId, this.scope).then(resp => {
                this.data = resp;
            });
        },
        onChange: function (v) {
            const all = [].concat(this.data.projectRoles).concat(this.data.specialRoles).concat(this.data.members)
            for (let i = 0; i < all.length; i++) {
                if (all[i].id === v) {
                    this.$emit("change", v, all[i])
                    this.selectItem = undefined
                    break
                }
            }
        },
        isExcluded: function (id) {
            return this.exclude.some(e => e.id == id)
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

