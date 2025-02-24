<template>
    <a-select v-model="selectItem" :loading="loading" :mode="mode" :placeholder="placeholder" @change="onChange"
        :disabled="disabled" allowClear :showSearch="true" :filterOption="handleFilter"
        :getPopupContainer="getPopupContainer" v-bind="$attrs">
        <a-select-option v-if="isShowAll" :title="'全部'" :value="''">
            <a-avatar :size="20" style="backgroundColor:rgb(44,178,174)"></a-avatar> &nbsp;所有成员
        </a-select-option>
        <a-select-option v-for="item in data" :key="item.id" :title="item.name" :value="item.id">
            <h-avatar :name="item.name" :icon="item.icon"></h-avatar>
            <span v-if="!isShowEmail" class="domain-list-cell-subtext">({{ item.email }})</span>
        </a-select-option>
    </a-select>
</template>

<script>
import {
    findProjectUsers
} from "@/services/tracker/ProjectRoleMemberService";
import HAvatar from '@/components/avatar/h-avatar.vue';
export default ({
    name: "ProjectUserSelect",
    components: { HAvatar },
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
        isShowEmail: false,
        isShowAll: false,
        getPopupContainer: false,
        placeholder:false,
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
            data: [],
            loading: false,
        };
    },
    methods: {
        handleFilter(value, option) {
            return option.componentOptions.propsData.title.indexOf(value) >= 0
        },
        loadData() {
            this.loading = true;
            findProjectUsers(this.projectId).then(resp => {
                this.data = resp;
                this.loading = false;
            });
        },
        onChange(v) {
            const e = this.data.find(_ => _.id === v)
            this.$emit("change", v, e);
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
