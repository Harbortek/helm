<template>
    <a-select v-model="selectItem" :loading="loading" :mode="mode" placeholder="请选择" @change="onChange"
        :disabled="disabled" allowClear :showSearch="true" :filterOption="handleFilter" @search="loadData"
        :getPopupContainer="getPopupContainer">
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
    getUsers
} from "@/services/system/UserService";
import HAvatar from '@/components/avatar/h-avatar.vue';
export default ({
    name: "GlobalUserSelect",
    components: { HAvatar },
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        value: {
            required: true
        },
        disabled: false,
        mode: false,
        isShowEmail: false,
        isShowAll: false,
        getPopupContainer: false
    },
    watch: {
        value: {
            immediate: true,
            handler: function (curVal, oldVal) {
                this.selectItem = curVal;
            }
        },
    },
    mounted() {
        this.loadData()
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
        loadData(keyword) {
            this.loading = true;
            getUsers({ keyword: keyword }).then(resp => {
                this.data = resp.content;
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
