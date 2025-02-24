<template>
    <div :class="!isToolBar ? 'task-detail-module' : ''">
        <vxe-button
            v-if="!trackerItem?.notPagePerm"
            type="text" style="float:right;z-index: 1;" @click="onSelectTrackerItem"
            icon="vxe-icon-paste">关联工作项</vxe-button>
        <a-form-model-item label="关联工作项" prop="relatedWorkItems" :span="24">
            <vxe-table ref="optionsTable" :loading="loading" :show-header="false" :data="relatedWorkItems"
                :row-config="{ isHover: true }" stripe>
                <vxe-column type="seq" :width="60" />
                <vxe-column field="relation" title="" min-width="110" show-overflow>
                    <template #default="{ row }">
                        <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                            <template slot="title">
                                <template v-if="row.sourceItem.id === itemId">
                                    {{ currentProjectKeyName + '-' + row.sourceItem?.itemNo + ' ' + row.sourceItem?.name
                                    }}
                                    {{ (row.linkType.id[0] == 'S' ? row.linkType.name : row.linkType.oppositeName) }}
                                    {{ currentProjectKeyName + '-' + row.targetItem?.itemNo + ' ' + row.targetItem?.name
                                    }}
                                </template>
                                <template v-else>
                                    {{ currentProjectKeyName + '-' + row.targetItem?.itemNo + ' ' + row.targetItem?.name
                                    }}
                                    {{ (row.linkType.id[0] == 'S' ? row.linkType.name : row.linkType.oppositeName) }}
                                    {{ currentProjectKeyName + '-' + row.sourceItem?.itemNo + ' ' + row.sourceItem?.name
                                    }}
                                </template>
                            </template>
                            <template
                                v-if="!trackerItem?.notPagePerm">
                                <vxe-select class-name="relation-table-border" v-model="row.linkType.id"
                                    @change="onChangeRelation(row)" transfer>
                                    <vxe-option v-for="item in trackerRelations" :key="item.id" :value="item.id"
                                        :label="item.name"></vxe-option>
                                </vxe-select>
                            </template>
                            <template v-else>
                                <span>{{ (row.linkType.id[0] == 'S' ? row.linkType.name : row.linkType.oppositeName)
                                    }}</span>
                            </template>
                        </a-tooltip>
                    </template>
                </vxe-column>
                <vxe-column title="" width="90" show-overflow>
                    <template #default="{ row }">
                        <template v-if="row.sourceItem.id === itemId">
                            <a-tooltip v-if="row.targetItem?.icon" :title="'工作项类型：' + row.targetItem?.tracker?.name"
                                :overlayStyle="{ fontSize: '10px' }">
                                <div style="margin-right:5px"><a-icon :component="row.targetItem?.icon" />{{
        currentProjectKeyName + '-' + row.targetItem?.itemNo }}</div>
                            </a-tooltip>

                        </template>
                        <template v-else>
                            <a-tooltip v-if="row.sourceItem?.icon" :title="'工作项类型：' + row.sourceItem?.tracker?.name"
                                :overlayStyle="{ fontSize: '10px' }">
                                <div style="margin-right:5px"><a-icon :component="row.sourceItem?.icon" />{{
        currentProjectKeyName + '-' + row.sourceItem?.itemNo }}</div>
                            </a-tooltip>

                        </template>
                    </template>
                </vxe-column>
                <vxe-column v-if="!isToolBar" field="" title="" :width="60">
                    <template #default="{ row }">
                        <template v-if="row.sourceItem.id === itemId">
                            <a-tooltip :title="'负责人：' + row.targetItem?.owner?.name"
                                :overlayStyle="{ fontSize: '10px' }" :mouseEnterDelay="0.01">
                                <a-tag class="a-tag-box">
                                    {{ row.targetItem?.owner?.name }}</a-tag>
                            </a-tooltip>
                        </template>
                        <template v-else>
                            <a-tooltip :title="'负责人：' + row.sourceItem?.owner?.name"
                                :overlayStyle="{ fontSize: '10px' }" :mouseEnterDelay="0.01">
                                <a-tag class="a-tag-box">
                                    {{ row.sourceItem?.owner?.name }}</a-tag>
                            </a-tooltip>
                        </template>
                    </template>
                </vxe-column>

                <vxe-column v-if="!isToolBar" title="" :width="60">
                    <template #default="{ row }">
                        <template v-if="row.sourceItem.id === itemId">
                            <a-tooltip :title="'优先级：' + row.targetItem?.priority?.name"
                                :overlayStyle="{ fontSize: '10px' }">
                                <a-tag style="border:none;cursor: pointer;"
                                    :style="{ color: row.targetItem?.priority?.color, backgroundColor: row.targetItem?.priority?.backgroundColor }">{{
        row.targetItem?.priority?.name }}</a-tag>
                            </a-tooltip>
                        </template>
                        <template v-else>
                            <a-tooltip :title="'优先级：' + row.sourceItem?.priority?.name"
                                :overlayStyle="{ fontSize: '10px' }">
                                <a-tag style="border:none;cursor: pointer;"
                                    :style="{ color: row.sourceItem?.priority?.color, backgroundColor: row.sourceItem?.priority?.backgroundColor }">{{
        row.sourceItem?.priority?.name }}</a-tag>
                            </a-tooltip>
                        </template>
                    </template>
                </vxe-column>


                <vxe-column field="name" title="" min-width="80" show-overflow>
                    <template #default="{ row }">
                        <template v-if="row.sourceItem.id === itemId">
                            <div style="display: flex;">

                                <span>{{ row.targetItem?.name }}</span>
                            </div>
                        </template>
                        <template v-else>
                            <div style="display: flex;">
                                <a-tooltip v-if="row.sourceItem?.icon" :title="'工作项类型：' + row.sourceItem?.tracker?.name"
                                    :overlayStyle="{ fontSize: '10px' }">
                                    <div style="margin-right:5px"><a-icon :component="row.sourceItem?.icon" /></div>
                                </a-tooltip>
                                <span>{{ row.sourceItem?.name }}</span>
                            </div>
                        </template>
                    </template>
                </vxe-column>
                <vxe-column v-if="isToolBar" field="status.name" title="" min-width="100" show-overflow>
                    <template #default="{ row }">
                        <div style="height:20px;" @mouseover="operateMouseOver($event)"
                            @mouseleave="operateMouseLeave($event)">
                            <div id="status-id">
                                <template v-if="row.sourceItem.id === itemId">
                                    <div class="transition-status-right">
                                        <span v-if="row.targetItem?.status" class="ui-tag-status"
                                            :style="{ color: row.targetItem?.status?.meaning?.color, 'border-color': row.targetItem?.status?.meaning?.color }">{{
        row.targetItem?.status?.name }}</span>
                                    </div>
                                </template>
                                <template v-else>
                                    <div class="transition-status-right">
                                        <span v-if="row.sourceItem?.status" class="ui-tag-status"
                                            :style="{ color: row.sourceItem?.status?.meaning?.color, 'border-color': row.sourceItem?.status?.meaning?.color }">{{
        row.sourceItem?.status?.name }}</span>
                                    </div>
                                </template>
                            </div>
                            <div id="operate-id" style="cursor:pointer" class="status-operate-class">
                                <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                                    <template slot="title">
                                        新窗口打开
                                    </template>
                                    <vxe-button type="text" icon="vxe-icon-zoom-in"
                                        @click="onEditItem(row)"></vxe-button>
                                </a-tooltip>

                                <a-tooltip v-if="!trackerItem?.notPagePerm" :overlayStyle="{ fontSize: '10px' }">
                                    <template slot="title">
                                        <span>删除</span>
                                    </template>
                                    <vxe-button type="text" icon="vxe-icon-delete"
                                        @click="onDeleteItem(row)"></vxe-button>
                                </a-tooltip>
                            </div>
                        </div>
                    </template>
                </vxe-column>
                <vxe-column v-if="!isToolBar" field="status.name" title="" min-width="100" show-overflow>
                    <template #default="{ row }">
                        <template v-if="row.sourceItem.id === itemId">
                            <div v-if="row.targetItem?.status" class="transition-status-right">
                                <span class="ui-tag-status"
                                    :style="{ color: row.targetItem?.status?.meaning?.color, 'border-color': row.targetItem?.status?.meaning?.color }">{{
        row.targetItem?.status?.name }}</span>
                            </div>
                        </template>
                        <template v-else>
                            <div v-if="row.sourceItem?.status" class="transition-status-right">
                                <span class="ui-tag-status"
                                    :style="{ color: row.sourceItem?.status?.meaning?.color, 'border-color': row.sourceItem?.status?.meaning?.color }">{{
        row.sourceItem?.status?.name }}</span>
                            </div>
                        </template>
                    </template>
                </vxe-column>
                <vxe-column v-if="!isToolBar" field="" title="操作" header-align="center" width="100">
                    <template #default="{ row }">
                        <a-tooltip :overlayStyle="{ fontSize: '10px' }">
                            <template slot="title">
                                <span>新窗口打开</span>
                            </template>
                            <vxe-button type="text" icon="vxe-icon-zoom-in" @click="onEditItem(row)"></vxe-button>
                        </a-tooltip>
                        <a-tooltip
                            v-if="!trackerItem?.notPagePerm"
                            :overlayStyle="{ fontSize: '10px' }">
                            <template slot="title">
                                <span>删除</span>
                            </template>
                            <vxe-button type="text" icon="vxe-icon-delete" @click="onDeleteItem(row)"></vxe-button>
                        </a-tooltip>
                    </template>
                </vxe-column>
            </vxe-table>
        </a-form-model-item>

        <tracker-item-select-modal :isConditionSelect="false" :projectId="projectId" :trackerId="trackerId"
            :initalData="getRelatedItems" ref="trackerItmeSlectModal" @trackerItemSelector="trackerItemSelector"
            @trackerConditonSelector="trackerConditonSelector"></tracker-item-select-modal>
    </div>
</template>

<script>
import {
    createTrackerLink, deleteTrackerLink, updateTrackerLink, findTrackerItemByIds
} from "@/services/tracker/TrackerItemService"
import { findLinkTypes } from "@/services/tracker/TrackerLinkTypeService"
import TrackerItemSelectModal from '@/components/dialog/TrackerItemSelectModal'
import { hasPermission } from '@/utils/permission'
import VXETable from "vxe-table";
import { mapGetters } from "vuex";
export default {
    name: 'TrackerRelatedItem',
    components: { TrackerItemSelectModal, },
    props: {
        projectId: String,
        trackerId: String,
        itemId: String,
        trackerItem: Object,
        relatedWorkItems: Array,
        isToolBar: Boolean,
    },
    data() {
        return {
            tableData: [],
            loading: false,
            trackerRelations: [],
            itemIdChildren: {},
            tracker: { id: '' },
        }
    },
    computed: {
        ...mapGetters("project", ["currentProjectKeyName"]),
        getRelatedItems() {
            let relatedItems = [];
            this.relatedWorkItems?.forEach(item => {
                if (item.sourceItem.id != this.itemId) {
                    relatedItems.push(item.sourceItem);
                }
                if (item.targetItem.id != this.itemId) {
                    relatedItems.push(item.targetItem);
                }
            })
            return relatedItems;
        },
    },
    watch: {
        relatedWorkItems: {
            immediate: true,
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadItemData();
                }
            }
        }
    },
    mounted() {
        if (this.projectId) {
            this.initData();
        }
    },
    methods: {
        operateMouseOver(e) {
            e.currentTarget.querySelector("#status-id").className = 'status-operate-class'
            e.currentTarget.querySelector("#operate-id").className = ''

        },
        operateMouseLeave(e) {
            e.currentTarget.querySelector("#status-id").className = ''
            e.currentTarget.querySelector("#operate-id").className = 'status-operate-class'
        },

        trackerItemSelector(selectedRows) {
            let items = [];
            let ids = this.getRelatedItems.map(item => item.id)
            selectedRows = selectedRows.filter(item => !ids.includes(item.id))
            if (selectedRows.length == 0) return;
            selectedRows.forEach(item => {
                items.push({
                    sourceItem: { id: this.itemId, itemNo: item.itemNo },
                    targetItem: { id: item.id, itemNo: item.itemNo, ...item },
                    linkType: { id: this.trackerRelations[this.trackerRelations.length - 1].value }
                })
            })

            if (this.itemId) {
                createTrackerLink(this.itemId, items).then(resp => {
                    this.refresh()
                })
            } else {
                items.forEach(v => v.linkType.id = 'T' + v.linkType.id)
                this.relatedWorkItems.push(...items)
            }
        },
        trackerConditonSelector(conditions) {
            console.log("conditions", conditions)
        },
        onChangeRelation(row) {
            let link = {
                id: row.id,
                sourceItem: { id: row.sourceItem.id, itemNo: row.sourceItem.itemNo, parent: row.sourceItem.parent },
                targetItem: { id: row.targetItem.id, itemNo: row.targetItem.itemNo, parent: row.targetItem.parent },
                linkType: { id: row.linkType.id.substring(1) }
            }
            if (row.linkType.id[0] == 'T' && row.sourceItem.id == this.itemId ||
                row.linkType.id[0] == 'S' && row.targetItem.id == this.itemId) {
                link = {
                    id: row.id,
                    sourceItem: { id: row.targetItem.id, itemNo: row.targetItem.itemNo, parent: row.targetItem.parent },
                    targetItem: { id: row.sourceItem.id, itemNo: row.sourceItem.itemNo, parent: row.sourceItem.parent },
                    linkType: { id: row.linkType.id.substring(1) }
                }
            }
            if (this.itemId) {
                updateTrackerLink(this.itemId, link).then(resp => {
                    if (resp) {
                        VXETable.modal.message({ content: '更新成功', status: 'success' })
                    } else {
                        VXETable.modal.message({ content: '关联失败，请确认父子关系是否正确', status: 'error' })
                    }
                }).finally(() => {
                    this.refresh();
                })
            }
        },
        onSelectTrackerItem() {
            let relatedItemIds = [this.itemId];
            this.$refs.trackerItmeSlectModal.view(relatedItemIds);
        },
        onEditItem(row) {
            if (row.sourceItem.id == this.itemId) {
                this.itemIdChildren = row.targetItem
            } else {
                this.itemIdChildren = row.sourceItem
            }

            const routeData = this.$router.resolve({
                path: `/tracker/project/${this.projectId}/trackerItems/${this.itemIdChildren.tracker.id}/${this.itemIdChildren.id}`
            });
            window.open(routeData.href, '_blank');

        },
        onDeleteItem(row) {
            let link = {
                id: row.id,
                targetItem: { id: row.targetItem.id, itemNo: row.targetItem.itemNo },
                linkType: { id: row.linkType.id.substring(1), parent: row.linkType.row }
            }
            if (this.itemId) {
                deleteTrackerLink(this.itemId, link).then(resp => {
                    this.$delete(this.relatedWorkItems, this.relatedWorkItems.indexOf(row))
                    VXETable.modal.message({ content: '删除成功', status: 'success' })
                }).cat.finally(()=>{
                    this.refresh();
                })
            }
        },
        refresh() {
            this.$emit("refresh")
        },
        loadItemData() {
            // this.loading=true;
            let ids = this.getRelatedItems.map(item => item.id)
            if (ids.length != 0 && this.itemId) {
                ids.push(this.itemId)
                findTrackerItemByIds(ids).then(resp => {
                    resp.forEach(itemVo => {
                        this.relatedWorkItems.forEach(item => {
                            if (item.sourceItem.id == itemVo.id) {
                                item.sourceItem = itemVo;
                            }
                            if (item.targetItem.id == itemVo.id) {
                                item.targetItem = itemVo;
                            }
                        })
                    })
                }).finally(() => {
                    this.loading = false;
                })
            } else {
                this.loading = false;
            }

        },
        initData() {
            findLinkTypes(this.projectId).then(resp => {
                const types = resp
                this.trackerRelations = []
                types.forEach(item => {
                    this.trackerRelations.push({
                        id: 'S' + item.id,
                        name: item.name,
                        value: item.id
                    })
                    if (item.oppositeName) {
                        this.trackerRelations.push({
                            id: 'T' + item.id,
                            name: item.oppositeName,
                            value: item.id
                        })
                    }
                })
            })
        },
    },
}
</script>

<style lang="less" scoped>
.task-detail-module {
    margin: 0 20px 0;
}

.transition-status-right {
    float: right;
    display: flex;
    align-items: center;
    margin-right: 20px;
    white-space: nowrap;

    .ui-tag-status {
        max-width: 110px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 12px;
        box-sizing: border-box;
        display: inline-block;
        height: 20px;
        line-height: 18px;
        transition: border-color .2s;
        border: 1px solid;
        border-radius: 20px;
        padding: 0px 8px;
    }
}

.a-tag-box {
    cursor: pointer;
    border: none;
    background-color: rgba(144, 144, 144, .15);
}

.relation-table-border {
    border: none
}

.relation-table-border input {
    border: none
}

.status-operate-class {
    display: none;
}
</style>