<template>
    <a-dropdown :trigger="['click']" placement="bottomLeft" ref="dropdown" v-model="visible">
        <a-card slot="overlay">
            <div class="branch-selector" :style="{ width: `${pickerWidth}px` }">
                <a-input-search @search="onSearch" v-model="keyword" placeholder="请输入分支或标签名称"
                    style="width:100%;"></a-input-search>
                <a-tabs v-model="currentTab" @change="handleTabChange" default-active-key="branch" :animated="false">
                    <a-tab-pane key="branch" tab="分支">
                        <vxe-table :data="branches" size="mini" :loading="loading"
                            :row-config="{ isHover: true, isCurrent: true }" row-id="name" :show-header="false" height="400"
                            @current-change="onBranchSelect">
                            <vxe-column field="name" title="名称">
                                <template #default="{ row }">
                                    <a-icon type="branches" /> {{ row.name }} <span v-if="row.default">
                                        <a-tag>默认</a-tag></span>
                                </template>
                            </vxe-column>
                        </vxe-table>
                    </a-tab-pane>
                    <a-tab-pane key="tag" tab="标签">
                        <vxe-table :data="tags" size="mini" :loading="loading"
                            :row-config="{ isHover: true, isCurrent: true }" row-id="name" :show-header="false" height="400"
                            @current-change="onTagSelect">
                            <vxe-column field="name" title="名称">

                            </vxe-column>
                        </vxe-table>
                    </a-tab-pane>
                </a-tabs>
            </div>
        </a-card>
        <span :class="{ 'branch-select-render': true }" @mouseenter="handleMouseEnter" @mouseleave="handleMouseLeave">
            <span v-if="selectedItem">
                <a-icon type="branches" /> {{ selectedItem.name }}
            </span>
            <h-icon class="picker-select-arrow" :style="{ fontSize: '14px', color: '#222222' }" @click="handleClear"
                :type="allowIcon" :theme="allowIcon === 'down' ? 'outlined' : 'filled'" />
        </span>
    </a-dropdown>
</template>

<script>
import { findBranches, findTags } from '@/services/scm/RepositoryService'
export default {
    name: 'BranchSelect',
    components: {
    },
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        value: {
            type: Object,
        },
        projectId: {
            required: true,
        }
    },
    data() {
        return {
            loading: false,
            visible: false,
            pickerWidth: 300,
            selectedItem: this.value || '',
            currentTab: 'branch',
            allowIcon: 'down',
            branches: [],
            tags: [],
            keyword: '',
        }
    },
    watch: {
        value: {
            handler(val) {
                this.selectedItem = val
            },
            immediate: true
        },
        visible: {
            handler(val) {
                if (val) {
                    //this.pickerWidth = this.$refs.dropdown.$el.offsetWidth
                }
            },
            immediate: true
        }
    },
    computed: {

    },
    created() {

    },
    mounted() {
        findBranches(this.projectId, this.keyword).then(resp => {
            this.branches = resp
            this.selectedItem = this.branches.filter(item => { return item.isDefault == true })[0]
            this.selectedItem.type = 'branch'
            this.$emit('input', this.selectedItem)
            this.$emit('change', this.selectedItem)
        })
        findTags(this.projectId, this.keyword).then(resp => {
            this.tags = resp
        })
    },
    methods: {
        loadData() {
            findBranches(this.projectId, this.keyword).then(resp => {
                this.branches = resp
            })
            findTags(this.projectId, this.keyword).then(resp => {
                this.tags = resp
            })
        },
        onBranchSelect({ newValue, oldValue, row }) {
            this.visible = false
            this.selectedItem = newValue
            this.selectedItem.type = 'branch'
            this.$emit('input', this.selectedItem)
            this.$emit('change', this.selectedItem)
        },
        onTagSelect({ newValue, oldValue, row }) {
            this.visible = false
            this.selectedItem = newValue
            this.selectedItem.type = 'tag'
            this.$emit('input', this.selectedItem)
            this.$emit('change', this.selectedItem)
        },
        handleTabChange(activeKey) {
            this.currentTab = activeKey
        },
        onSearch() {
            this.loadData()
        },
        handleClear() {
            if (this.allowClear && this.selectedItem) {
                this.visible = false
                this.selectedItem = null
                this.$emit('input', null)
                this.$emit('change', null)
                this.allowIcon = 'down'
            }
        },
        handleMouseEnter() {
            if (this.allowClear) {
                this.allowIcon = this.selectedItem ? 'close-circle' : 'down'
            }
        },
        handleMouseLeave() {
            this.allowIcon = 'down'
        }
    }
}
</script>

<style lang="less" scoped>
.branch-selector {
    margin: 5px 5px;
}

.branch-select-render {
    position: relative;
    cursor: pointer;
    margin: 0;
    display: inline-block;
    width: 100%;
    height: 32px;
    padding: 4px 11px;
    color: rgba(0, 0, 0, 0.65);
    font-size: 14px;
    line-height: 1.5;
    background-color: #fff;
    background-image: none;
    border: 1px solid #d9d9d9;
    border-radius: 2px;

    .placeholder {
        color: #ccc;
    }

    .picker-select-arrow {
        color: #ccc;
        right: 10px;
        top: 6px;
        position: absolute;
        line-height: 1.5;
    }

    .select-icon {
        margin-right: 4px;
        color: @primary-color;
    }

    &::after {
        display: inline-block;
        width: 0;
        visibility: hidden;
        content: ".";
        pointer-events: none;
    }
}

.icon-picker-disabled {
    background: #f5f5f5;
    cursor: not-allowed;
}

ul {
    list-style: none;
    padding: 0;
    height: 250px;
    margin: 0px;

    li {
        display: inline-block;
        padding: @padding-sm;
        margin: 3px 0;
        border-radius: @border-radius-base;

        &:hover,
        &.active {
            cursor: pointer;
            color: @white;
            background-color: @primary-color;
        }
    }
}
</style>