<template>
    <div :class="!isToolBar?'task-detail-module':''">
        <!-- <div style="margin:10px 0" class="task-detail-module-title">
            <div class="task-detail-module-title-text">代码合并请求</div>
            <div style="float:right;z-index: 1;">
                <vxe-button @click="onClickRegisterHour" type="text"
                    icon="vxe-icon-add">新建合并请求</vxe-button>
            </div>
        </div>
        <a-config-provider>
            <template #renderEmpty>
                <div style="text-align: left;height: 42px;line-height: 42px;color:#87888a">
                    <span>暂无代码合并请求记录</span>
                </div>
            </template>
            <a-list item-layout="horizontal" :loading="loading" :data-source="[]">
                <a-list-item class="register-hour-item" slot="renderItem"
                    slot-scope="item, index">
                </a-list-item>
            </a-list>
        </a-config-provider> -->

        <div style="margin-top:20px" class="task-detail-module-title">
            <div class="task-detail-module-title">代码提交记录<span
                    style="color:#909090;margin-left:10px;">({{ trackerCommitList.length
                    }})</span>
            </div>
        </div>

        <a-config-provider>
            <template #renderEmpty>
                <div style="text-align: left;height: 42px;line-height: 42px;color:#87888a">
                    <span>暂无代码提交记录</span>
                </div>
            </template>
            <a-list item-layout="vertical" :loading="loading" :data-source="trackerCommitList">
                <a-list-item class="register-hour-item"
                    style="border-bottom: 1px solid rgb(232, 232, 232);" slot="renderItem"
                    slot-scope="item">
                    <div
                        style="display: flex;justify-content: space-between;font-size: 16px;color: #303030;">
                        <span>{{ item.committer }}</span>
                        <div
                            style="overflow: hidden;width: 100px;white-space: nowrap;text-overflow: ellipsis;">
                            <a-icon type="check-circle"
                                :style="{ color: '#01ab72', marginRight: '5px' }"
                                theme="filled" />{{ item.commitId }}
                        </div>
                    </div>
                    <div style="color: #505050;">{{ item.commitMessage }}</div>
                    <div style="display: flex;justify-content: space-between;color:#909090">
                        <span><h-icon :type="item.repositoryType"
                                :style="{ marginRight: '5px' }" />{{
                                    item.projectNameOfRepository
                                }}</span>
                        <span>{{ item.commitOn }}</span>
                    </div>
                </a-list-item>
            </a-list>
        </a-config-provider>
    </div>
</template>

<script>

import { findCommitsByItemId } from '@/services/tracker/TrackerCommitService'

export default {
    name: 'TrackerRelatedCode',
    components: {},
    props: {
        isShowDialog: {
            required: true
        },
        trackerItem: {
            required: true
        },
        isToolBar: {
            required: false
        }
        
    },
    data() {
        return {
            codemergeList: [],
            codeBranchList: [],
            trackerCommitList: [],
            loading: false,
        }
    },
    watch: {
        isShowDialog: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        }
    },
    mounted() {

    },
    methods: {
        loadData() {
            if (this.trackerItem?.id) {
                this.loading = true;
                findCommitsByItemId(this.trackerItem.id).then(res => {
                    this.trackerCommitList = res;
                }).finally(()=>{
                    this.loading = false;
                })
            }
        },
    },
}
</script>

<style lang="less" scoped>
.task-detail-module {
    margin: 0 20px 0;
}
.register-hour-item {
    border-bottom: 1px solid #e8e8e8;
    cursor: pointer;
    padding: 12px;
}

.register-hour-item:hover {
    background-color: #f8f8f8;
}

.register-hour-item-edit {
    display: none;
}
.task-detail-module-title {
    font-size: 15px;
    color: #303030;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

</style>