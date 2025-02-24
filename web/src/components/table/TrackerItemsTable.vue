<template>
    <div class="toolbar">
        <vxe-toolbar size="medium">
            <template #buttons>
                <template v-if="!loading">
                    <vxe-button v-if="(sprintType == 'Unplanned' || sprintType == 'All' || isMatters)"
                        style="margin-left:10px;" v-action="'PAGE_WRITE|'+ proejctPageId" status="primary"
                        content="新增工作项" @click="onCreateTrackerItem"></vxe-button>
                    <vxe-button v-else status="primary" :content="'新建 ' + (tracker.name || '工作项')"
                        v-action="'ITEM_CREATE|' + trackerId" @click="onCreateTrackerItem"></vxe-button>
                    <!-- v-action="'PAGE_WRITE|'+proejctPageId" -->
                    <vxe-button v-if="sprintType == 'Unplanned' || sprintType == 'All'"
                        :disabled="selectedRowKeys.length == 0" v-action="'PAGE_WRITE|' + proejctPageId" status="text"
                        content="规划至迭代" @click="showSprintOperateDialog = true"></vxe-button>
                    <vxe-button v-if="sprintType == 'All'" :disabled="selectedRowKeys.length == 0" status="text"
                        content="移出迭代" v-action="'PAGE_WRITE|' + proejctPageId" @click="onRemoveSprint"></vxe-button>
                    <div v-if="selectedRowKeys != 0 && (sprintType || isBatchModfigle)"
                        style="margin-left: 10px;font-size: 14px; color: #87888a; display: flex;align-items: center;">
                        已选择{{ selectedRowKeys.length }}个</div>
                    <!-- <span style="margin-left: 10px;font-size: 14px; color: #87888a; display: flex;align-items: center;">共{{
                        getItemList.length }}个</span> -->
                </template>
            </template>
            <template #tools>

                <a-space>
                    <div v-show="viewId && isShowSaveButton"
                        style="height:26px;align-items: center;background: #edeff2;border-radius: 13px;color: #575859;">
                        <vxe-button @click="onClickSaveView" style="margin:0;border-right: 1px solid #d4d6d9;"
                            type="text" :content="'保存'"></vxe-button>
                        <vxe-button @click="onClickSaveAsView" style="margin:0;border-right: 1px solid #d4d6d9"
                            type="text" :content="'另存为'"></vxe-button>
                        <vxe-button @click="onClickResetView" style="margin:0;" type="text"
                            :content="'还原'"></vxe-button>
                    </div>
                    <template v-if="!sprintType">
                        布局
                        <a-select v-model="layout" style="width: 120px" @change="onChangeLayout">
                            <!-- <a-select-option value="document">文档</a-select-option> -->
                            <a-select-option value="table">表格</a-select-option>
                            <a-select-option v-if="trackerId" value="kanban">看板</a-select-option>
                            <a-select-option value="calendar">日历</a-select-option>
                            <a-select-option value="tree">树状</a-select-option>
                            <a-select-option value="matrix">矩阵</a-select-option>
                        </a-select>
                        <!-- 结构
                        <a-select v-model="displayStyle" style="width: 120px">
                            <a-select-opt-group>
                                <span slot="label">平铺</span>
                                <a-select-option :disabled="layout!='table'" value="DISPLAY_SUB">显示子工作项</a-select-option>
                                <a-select-option value="HIDE_SUB">隐藏子工作项</a-select-option>
                            </a-select-opt-group>
                            <a-select-opt-group>
                                <span slot="label">树状</span>
                                <a-select-option :disabled="layout!='table'" value="TREE_EXPAND">树状展开</a-select-option>
                                <a-select-option :disabled="layout!='table'" value="TREE_COLLAPSE">树状折叠</a-select-option>
                            </a-select-opt-group>
                        </a-select> -->
                    </template>
                    排序
                    <a-popover trigger="['click']]" placement="bottom" @visibleChange="rowDrop">
                        <template slot="content">
                            <div ref="orderByTable">
                                <div class="order-row" :key="orderByItem.id"
                                    v-for=" (orderByItem, index) in currentView.viewConfig?.orderBys">
                                    <a-space>
                                        <a-icon type="minus-circle" class="icon-circle disabled"
                                            v-if="currentView.viewConfig?.orderBys?.length == 1" theme="filled" />
                                        <a-icon type="minus-circle" class="icon-circle" v-else
                                            @click="removeOrderItem(orderByItem, index)" />
                                        <a-select v-model="orderByItem.field.id" style="width:180px;margin-left: 10px;">
                                            <a-select-option :value="f.id" :key="f.id"
                                                @click="orderConditionChange(orderByItem, f)"
                                                v-for="f in orderByFields(orderByItem)">{{
                                                f.name
                                                }}</a-select-option>
                                        </a-select>
                                        <a-select v-model="orderByItem.orderBy" style="width:180px;margin-right: 10px;"
                                            @change="orderConditionChange(orderByItem)">
                                            <a-select-option value="DESC">倒序排序</a-select-option>
                                            <a-select-option value="ASC">正序排序</a-select-option>
                                        </a-select>
                                        <i class="vxe-icon--menu"></i>
                                    </a-space>
                                </div>
                                <div v-if="orderByFields().length != 0" class="order-row order-row-bottom">
                                    <a-icon type="plus-circle" style="font-size:18px" @click="addOrderItem" />
                                    <vxe-button type="text" content="添加条件" style="margin-left: 10px;"
                                        @click="addOrderItem" />
                                </div>
                            </div>
                        </template>
                        <a-button type="text">
                            <span v-if="currentView.viewConfig?.orderBys?.length == 1">{{
                                currentView.viewConfig?.orderBys[0].field.name }}</span>
                            <span v-else>{{ currentView.viewConfig?.orderBys?.length || 0 }}个条件</span>

                            <a-icon type="down" />
                        </a-button>
                    </a-popover>

                    <template v-if="!groupByShow && layout != 'kanban'">
                        分组
                        <a-popover :overlayStyle="{ width: '220px', height: '500px', padding: '12px 10px' }"
                            trigger="['click']" placement="bottom" @visibleChange="rowDrop">
                            <template slot="content">
                                <div ref="groupByTable" style="margin:0 -5px;">
                                    <div style="font-weight: bolder;font-size: 14px;color: #303030;">分组维度</div>
                                    <a-select v-model="groupBy" style="width:100%;margin:5px 0;">
                                        <a-select-option v-for="item in groupList" :key="item.id" :value="item.value"
                                            @click="onChangeGroupBy(item.value)">{{ item.name
                                            }}</a-select-option>
                                    </a-select>
                                    <div
                                        style="font-weight: bolder;padding: 5px 5px 1px;font-size: 14px;color: #303030;">
                                        分组选项</div>
                                    <div style="border:1px solid #d9d9d9;height:310px;margin:5px 0;">
                                        <div style="padding:10px;"><a-input-search v-model="groupSearch"
                                                placeholder="搜索" />
                                        </div>
                                        <a-menu style="width: 100%;padding:5px;" v-model="groupSelect" mode="vertical">
                                            <a-menu-item style="margin:0;padding:0 5px;height:30px;line-height: 30px;"
                                                v-for="item in groupMenuFilter" :key="item?.name">
                                                {{ item.name }} ({{ item.count }})
                                            </a-menu-item>
                                        </a-menu>
                                    </div>
                                    <a-button @click="groupByShow = true" style="height:20px;border:none;padding: 0;"
                                        icon="import">固定为左栏</a-button>
                                </div>
                            </template>
                            <a-button type="text">{{ groupByName }}<a-icon type="down" /></a-button>
                        </a-popover>
                    </template>

                    <a-badge :number-style="{ backgroundColor: '#338fe5' }" :count="getFilterCount()">
                        <a-button type="text" :style="activeKey == 'true'? 'color:#5caff2;borderColor:#5caff2': ''"
                            @click="onClickFilter">筛选<a-icon :component="filterIcon" /></a-button>
                    </a-badge>

                    <vxe-input v-if="waitInputSearch" v-model="keyword" placeholder="搜索工作项关键字" type="search" clearable
                        @search-click="onSearch()" @blur="onBlurSearch()"></vxe-input>
                    <a-button v-else type="text" @click="waitInputSearch = true">
                        <a-icon type="search" /> </a-button>

                    <a-popover placement="bottomRight" trigger="['click']]" v-model="moreMenuVisible"
                        :getPopupContainer="triggerNode => {
                    return triggerNode.parentNode;
                }">
                        <template slot="content">
                            <div class="ui-table" style="overflow:auto">
                                <div v-if="!pageId && !sprintId && trackerId" style="height:30px;margin-right:10px;">
                                    <vxe-button @click="onClickImport" type="text"
                                        :content="'导入' + (tracker.name || '工作项')"></vxe-button>
                                </div>
                                <div v-if="!pageId && !sprintId && trackerId" style="height:30px;margin-right:10px;">
                                    <vxe-button @click="onClickExport" type="text"
                                        :content="'导出' + (tracker.name || '工作项')"></vxe-button>
                                </div>
                                <div style="height:30px;margin-right:10px;">
                                    <vxe-button @click="onClickBatchModifigleStart()" type="text"
                                        :content="'批量修改工作项属性'"></vxe-button>
                                </div>
                                <div style="height:30px;margin-right:10px;">
                                    <vxe-button @click="onClickBatchDeleteStart()" type="text"
                                        :content="'批量删除工作项'"></vxe-button>
                                </div>
                            </div>
                        </template>
                        <a-button type="text"><a-icon type="unordered-list" /></a-button>
                    </a-popover>
                </a-space>

            </template>
        </vxe-toolbar>

        <tracker-item-filter style="transition: all .25s;"
            :class="activeKey == 'true' ? 'filter-flex-active' : 'filter-flex'" :members="members"
            :tracker="trackerFilter" :conditionGroups="conditionGroups" :activeKey="activeKey"
            @refresh="onRefreshFilter"></tracker-item-filter>

        <a-layout style="flex:5 1 65%;">
            <a-layout-sider v-if="groupByShow" theme="light" style="height:100%;">
                <div style="height:100%;display:flex;flex-direction: column;">
                    <div style="font-weight: bolder;padding: 10px 10px 5px;font-size: 14px;color: #303030;">分组维度</div>
                    <a-select v-model="groupBy" style="width:100%;margin:5px 0;padding-right: 8px;">
                        <a-select-option v-for="item in groupList" :key="item.id" :value="item.value"
                            @click="onChangeGroupBy(item.value)">{{ item.name }}</a-select-option>
                    </a-select>
                    <div style="font-weight: bolder;padding: 10px 10px 5px;font-size: 14px;color: #303030;">分组选项</div>
                    <div style="border:1px solid #d9d9d9;margin-right: 10px;flex:1;">
                        <div style="padding:10px;"><a-input-search v-model="groupSearch" placeholder="搜索" /></div>
                        <a-menu style="width: 100%;padding:5px;" v-model="groupSelect" mode="vertical">
                            <a-menu-item style="margin:0;padding:0 5px;height:30px;" v-for="item in groupMenuFilter"
                                :key="item?.name">
                                {{ item.name }} ({{ item.count }})
                            </a-menu-item>
                        </a-menu>
                    </div>
                    <a-button @click="groupByShow = false" style="width: 95%;position: absolute;bottom: 0px;padding: 0;"
                        icon="export">收起</a-button>
                </div>
            </a-layout-sider>
            <a-layout-content>
                <div class="table-layout" style="height:100%;" v-show="layout === 'table'">
                    <a-popover placement="bottomRight" trigger="['click']]" v-model="customRowVisible"
                        :getPopupContainer="triggerNode => {
                    return triggerNode.parentNode;
                }">
                        <template slot="content">
                            <a-card style="border:none;" :bodyStyle="{ padding: '0' }" trigger="['click']]"
                                placement="bottom" @mouseenter="rowDropHeader">
                                <div class="ui-table">
                                    <div class="table-head layout-fields-table-row order-row-header-bottom">
                                        <div style="">
                                            <vxe-checkbox v-model="customRowCheckAll" :indeterminate="indeterminate"
                                                @change="onChangeCustomRowAll" content="全选"></vxe-checkbox>
                                        </div>
                                    </div>
                                    <div style="overflow:auto">
                                        <vxe-checkbox-group v-model="customRowCheck" @change="onChangeCustomRow">
                                            <div ref="orderByRow" style="overflow: auto;">
                                                <div class="order-row-header" v-for="item in customRowList"
                                                    :key="item.id">
                                                    <div class="layout-fields-table-row table-row">
                                                        <div style="padding-left: 0;width: 160px;">
                                                            <vxe-checkbox :label="item.id"></vxe-checkbox>
                                                            <i class="vxe-icon--menu"></i>
                                                            {{ item.name }}
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </vxe-checkbox-group>
                                    </div>
                                </div>
                            </a-card>
                            <div style="border-top: 1px solid #dadce0;text-align: right;">
                                <vxe-button style="width: 45%;height:34px;" type="text"
                                    @click="onClickCustomRowReset">重置</vxe-button>
                                <vxe-button style="width: 45%;height:34px;" type="text"
                                    @click="onClickCustomRowOk">确认</vxe-button>
                            </div>
                        </template>
                        <vxe-button style="position: absolute;right: 10px;background-color: #f8f8f9;
                        z-index:1;font-size: 20px;margin-top: 5px;" type="text"
                            icon="vxe-icon-setting-fill"></vxe-button>
                    </a-popover>

                    <vxe-grid :loading="loading" height="auto" ref="trackerItemTable" :row-config="{ isHover: true }"
                        :columns="tableColumn" :data="getItemList" row-id="id"
                        :edit-config="{trigger: 'click', mode: 'cell',activeMethod: activeCellMethod, beforeEditMethod: beforeEditMethod}"
                        :checkbox-config="{ checkRowKeys: selectedRowKeys,reserve:true, checkField: 'checked', trigger: 'row' }"
                        @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent">

                        <template #itemNo_default="{ row }">
                            <div style="display: inline-flex;">
                            <a-tooltip v-if="row?.icon" :title="'工作项类型：' + row.tracker?.name"
                                :overlayStyle="{ fontSize: '10px' }">
                                <div class="task-icon"><h-icon :component="row.tracker?.icon" /></div>
                            </a-tooltip>
                            <span style="margin-left: 5px;">{{ currentProjectKeyName.toUpperCase() + '-' + row.itemNo }}</span>
                            </div>
                        </template>

                        <template #name_default="{ row }">
                            <a class="task-table-cell">
                                <div class="task-table-cell-content">
                                    <div class="task-cell-summary">
                                        <div class="task-cell-summary-container">
                                            <div @click.stop class="task-description">
                                                <div class="prefix-container" @click="onEditTrackerItem(row)">

                                                    <a-tooltip v-if="row.name?.length > 20" :title="row.name"
                                                        :overlayStyle="{ fontSize: '10px' }">
                                                        <span>{{ row.name }}</span>
                                                    </a-tooltip>
                                                    <span v-else>{{ row.name }}</span>

                                                </div>
                                            </div>

                                        </div>
                                        <a-icon v-if="row.parentId" component="itemTree" />
                                    </div>
                                </div>
                            </a>
                        </template>
                        <template #name_edit="{ row }">
                            <vxe-input @focus="focusValue = row.name" @blur="onBlurEdit(row, 'name', row.name)"
                                v-model="row.name"></vxe-input>
                        </template>

                        <template #priority_default="{ row }">
                            <a-tag style="border:none"
                                :style="{ color: row.priority?.color, backgroundColor: row.priority?.backgroundColor }">{{
                                row.priority?.name }}</a-tag>
                        </template>
                        <template #priority_edit="{ row }">
                            <vxe-select v-model="row.priority.id" @change="e => onChangePriorityEdit(e, row)"
                                type="text" transfer>
                                <vxe-option v-for="item in prioritys" :key="item.value" :value="item.id"
                                    :label="item.name">
                                </vxe-option>
                            </vxe-select>
                        </template>

                        <template #status_default="{ row }">
                            <a-popover v-if="row.status" v-model="statusPopoverVisible[row.id]" trigger="click"
                                :style="{ pointerEvents: !row.notPagePerm ? '' : 'none' }" placement="bottomLeft"
                                overlayClassName="tracker-select-dropdown">
                                <template slot="content">
                                    <tracker-item-status-popover :disabled="true" :projectId="projectId"
                                        :trackerItem="row" :tracker="tracker"
                                        @change="statusPopoverVisible[row.id]=false"></tracker-item-status-popover>
                                </template>
                                <div style="cursor: pointer" class="transition-status">
                                    <span class="ui-tag-status"
                                        :style="{ color: row.status?.meaning?.color, 'border-color': row.status?.meaning?.color }">{{
                                        row.status?.name }}</span>
                                </div>
                            </a-popover>
                        </template>
                        <template #status_header>
                            <i style="margin-right: .2em;" class="vxe-icon-edit"></i>状态
                        </template>

                        <template #createBy_default="{ row }">
                            <h-avatar v-if="!loading" :name="row.createBy.name" :icon="row.createBy.icon"></h-avatar>
                        </template>

                        <template #lastModified_default="{ row }">
                            <h-avatar :name="row.lastModifiedBy.name" :icon="row.lastModifiedBy.icon"></h-avatar>
                        </template>

                        <template #owner_default="{ row }">
                            <tracker-item-user-select v-if="!loading" :disabled="row.notPagePerm" :projectId="projectId"
                                size="small" @refresh="onRefreshSuccess"
                                :field="tracker.trackerFields?.find(item => item.systemProperty == 'ownerId')"
                                :trackerItem="row" :projectUserList="members"></tracker-item-user-select>
                        </template>
                        <template #owner_header>
                            <i style="margin-right: .2em;" class="vxe-icon-edit"></i>负责人
                        </template>

                        <template #plan_end_time_edit="{ row }">
                            <vxe-input v-model="row.planEndDate" @change="e => onChangeDateEdit(e, 'planEndDate', row)"
                                placeholder="日期和时间选择" type="datetime" transfer></vxe-input>
                        </template>

                        <template #estimate_working_hours_edit="{ row }">
                            <vxe-input @focus="focusValue = row.estimateWorkingHours"
                                @blur="onBlurEdit(row, 'estimateWorkingHours', row.estimateWorkingHours)"
                                v-model="row.estimateWorkingHours"></vxe-input>
                        </template>
                        <template #registered_working_hours_default="{ row }">
                            <div style="cursor:pointer" @click.stop="onClickWorkHours(row)">
                                <span>{{ row.registeredWorkingHours || 0 }}</span>
                            </div>
                        </template>
                        <template #registered_working_hours_header>
                            <i style="margin-right: .2em;" class="vxe-icon-edit"></i>已登记工时
                        </template>

                        <template #remaining_working_hours_edit="{ row }">
                            <vxe-input @focus="focusValue = row.remainingWorkingHours"
                                @blur="onBlurEdit(row, 'remainingWorkingHours', row.remainingWorkingHours)"
                                v-model="row.remainingWorkingHours"></vxe-input>
                        </template>

                        <template #assigned_to_default="{ row }">
                            <tracker-item-user-select :projectId="projectId" :disabled="row.notPagePerm" size="small"
                                @refresh="onRefreshSuccess"
                                :field="tracker.trackerFields?.find(item => item.systemProperty == 'assignedToId')"
                                :trackerItem="row"></tracker-item-user-select>
                        </template>
                        <template #assigned_to_header>
                            <i style="margin-right: .2em;" class="vxe-icon-edit"></i>分配给
                        </template>
                        <template #assigned_date_edit="{ row }">
                            <vxe-input v-model="row.assignedDate"
                                @change="e => onChangeDateEdit(e, 'assignedDate', row)" placeholder="日期和时间选择"
                                type="datetime" transfer></vxe-input>
                        </template>
                        <template #plan_start_date_edit="{ row }">
                            <vxe-input v-model="row.planStartDate"
                                @change="e => onChangeDateEdit(e, 'planStartDate', row)" placeholder="日期和时间选择"
                                type="datetime" transfer></vxe-input>
                        </template>
                        <template #real_start_date_edit="{ row }">
                            <vxe-input v-model="row.realStartDate"
                                @change="e => onChangeDateEdit(e, 'realStartDate', row)" placeholder="日期和时间选择"
                                type="datetime" transfer></vxe-input>
                        </template>
                        <template #real_end_date_edit="{ row }">
                            <vxe-input v-model="row.realEndDate" @change="e => onChangeDateEdit(e, 'realEndDate', row)"
                                placeholder="日期和时间选择" type="datetime" transfer></vxe-input>
                        </template>
                        <template #progress_edit="{ row }">
                            <vxe-input @focus="focusValue = row.progress"
                                @blur="onBlurEdit(row, 'progress', row.progress)" v-model="row.progress"></vxe-input>
                        </template>
                        <template #close_date_edit="{ row }">
                            <vxe-input v-model="row.closeDate" @change="e => onChangeDateEdit(e, 'closeDate', row)"
                                placeholder="日期和时间选择" type="datetime" transfer></vxe-input>
                        </template>
                        <template #pager>
                            <div style="height: 48px;line-height:48px;padding-right:10px;text-align: right;background-color: #fff;"
                                v-if="isBatchModfigle">
                                <vxe-button @click="onClickBatchModifigleCancel">取消</vxe-button>
                                <vxe-button @click="onClickBatchModifigleNext" status="primary">下一步</vxe-button>
                            </div>
                            <vxe-pager :loading="loading" :current-page="pagination.current"
                                :page-size="pagination.pageSize" :total="pagination.total"
                                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
                                @page-change="handlePageChange">
                            </vxe-pager>

                        </template>
                    </vxe-grid>
                </div>

                <KanbanLayout v-show="layout === 'kanban'" ref="kanbanRef" :isShowKanban="layout === 'kanban'"
                    :tracker="tracker" :projectId="projectId" :itemData="getItemList" :currentView="currentView"
                    :conditionGroups="conditionGroups" @onClickWorkHours="onClickWorkHours"
                    @onEditTrackerItem="onEditTrackerItem" @refresh="refresh"></KanbanLayout>

                <CalendarLayout ref="calendarRef" v-show="layout === 'calendar'" :isShowCalendar="layout === 'calendar'"
                    :tracker="tracker" :projectId="projectId" :itemData="getItemList" :currentView="currentView"
                    :conditionGroups="conditionGroups" @onEditTrackerItem="onEditTrackerItem"></CalendarLayout>

                <TreeLayout ref="calendarRef" v-show="layout === 'tree'" :isShowTree="layout === 'tree'"
                    :tracker="tracker" :projectId="projectId" :itemData="getItemList" :currentView="currentView"
                    :conditionGroups="conditionGroups" @onEditTrackerItem="onEditTrackerItem"></TreeLayout>

                <MatrixLayout ref="calendarRef" v-show="layout === 'matrix'" :isShowMatrix="layout === 'matrix'"
                    :tracker="tracker" :projectId="projectId" :itemData="getItemList" :currentView="currentView"
                    :conditionGroups="conditionGroups" @onEditTrackerItem="onEditTrackerItem"></MatrixLayout>


            </a-layout-content>
        </a-layout>

        <sprint-operate-dialog :isShowDialog="showSprintOperateDialog" :projectId="projectId"
            :currentSelectedIds="selectedRowKeys" @cancel="showSprintOperateDialog = false" @ok="onSprintDialogOK" />

        <create-tracker-item-dialog :is-show-dialog="isShowCreateTrackerItemDialog" :projectId="projectId"
            :tracker="tracker" @ok="onCreateTrackerItemOK" @cancel="onCreateTrackerItemCancel" />

        <edit-tracker-item-dialog :is-show-dialog="isShowEditTrackerItemDialog" :projectId="projectId"
            :tracker="trackerEdit" :itemId="itemId" @ok="onEditTrackerItemOK" @cancel="onEditTrackerItemCancel" />

        <register-hour-dialog :isShowDialog="registerHourVisible" :trackerItem="trackerItem" :projectId="projectId"
            :dialogTitleWorkHour="'添加成员登记工时'" @ok="onOKRegisterHour"
            @close="registerHourVisible = false"></register-hour-dialog>

        <create-view-dialog :isShowDialog="isShowCreateDialog" :editMode="'saveAs'" :view="currentView"
            :objectId="trackerId || pageId" @ok="onCreateViewDialogOK" @cancel="isShowCreateDialog = false" />

        <tracker-item-import-modal :isShowDialog="isShowImportDialog" :editMode="'saveAs'" :view="currentView"
            :trackerId="trackerId" @ok="onImportDialogOK" @cancel="isShowImportDialog = false" />
        <tracker-item-export-modal :isShowDialog="isShowExportDialog" :customRowList="customRowList"
            :customRowCheck="customRowCheck" :trackerId="trackerId" @ok="onExportDialogOK"
            @cancel="isShowExportDialog = false" />

        <tracker-transform-modal :projectId="projectId" :projectKeyName="currentProjectKeyName"
            :isShowDialog="isShowTransformDialog" :initalData="selectedRows" @onPrevStep="isShowTransformDialog = false"
            @cancel="onCancelTransform" @refresh="loadWorkItems()"></tracker-transform-modal>

        <tracker-item-batch-delete-modal :projectId="projectId" :projectKeyName="currentProjectKeyName"
            :isShowDialog="isShowBatchDelDialog" :initalData="selectedRows" @onPrevStep="isShowBatchDelDialog = false"
            @cancel="onCancelTransform" @refresh="loadWorkItems()"></tracker-item-batch-delete-modal>



    </div>
</template>
<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import { mapGetters } from "vuex";
import Vue from "vue"
import moment from 'moment'
import VXETable from "vxe-table";
import _ from 'lodash'
import {cloneDeep} from 'lodash'
import ContentPage from '@/components/page/content/ContentPage.vue';
import {systemFields} from '@/utils/systemFields'
import { hasPermission } from '@/utils/permission'
import {
    findTrackers, findOneTracker
} from "@/services/tracker/TrackerService";
import {
    findEnumsByCode,
} from "@/services/system/EnumService";
import { findProjects } from "@/services/tracker/ProjectService";
import {
    findTrackerItems, findGroupItems, findOneTrackerItem, createTrackerItem, updateTrackerItem, changeSystemField,
    createWorkHours, stateChange, updateTrackerItemSprint, exportTrackerItems
} from "@/services/tracker/TrackerItemService";
import { findTrackerItemsByPageIdWithoutInternalTrackers } from '@/services/tracker/ProjectPageService'

import { findProjectUsers } from "@/services/tracker/ProjectRoleMemberService";
import { createView, findOneView, updateView } from "@/services/tracker/ViewService"
import Sortable from "sortablejs"
import CreateTrackerItemDialog from '@/pages/tracker/items/CreateTrackerItemDialog.vue';
import EditTrackerItemDialog from '@/pages/tracker/items/EditTrackerItemDialog.vue'
import RegisterHourDialog from '@/pages/tracker/items/RegisterHourDialog.vue';
import TrackerItemFilter from '@/components/tool/TrackerItemFilter'
import SprintOperateDialog from '@/pages/plan/SprintOperateDialog.vue';
import TrackerItemStatusPopover from '@/components/tool/TrackerItemStatusPopover.vue';
import ProjectSelect from '@/components/select/ProjectSelect.vue';
import ProjectUserSelect from '@/components/select/ProjectUserSelect.vue';
import TrackerItemUserSelect from '@/components/select/TrackerItemUserSelect.vue';
import CreateViewDialog from "@/pages/tracker/items/CreateViewDialog.vue";
import TrackerItemImportModal from "@/pages/tracker/items/TrackerItemImportModal.vue";
import TrackerItemExportModal from "@/pages/tracker/items/TrackerItemExportModal.vue";
import TrackerTransformModal from '@/components/dialog/TrackerTransformModal.vue'
import TrackerItemBatchDeleteModal from '@/components/dialog/TrackerItemBatchDeleteModal.vue'
import KanbanLayout from './trackerItemTableLayout/KanbanLayout.vue';
import CalendarLayout from './trackerItemTableLayout/CalendarLayout.vue';
import TreeLayout from './trackerItemTableLayout/TreeLayout.vue';
import MatrixLayout from './trackerItemTableLayout/MatrixLayout.vue';



export default {
    name: 'TrackerItemsTable',
    components: {
        ContentPage, Sortable, CreateTrackerItemDialog, EditTrackerItemDialog, TrackerItemFilter,
        RegisterHourDialog, SprintOperateDialog, TrackerItemStatusPopover, ProjectSelect, ProjectUserSelect,
        TrackerItemUserSelect, CreateViewDialog, TrackerItemImportModal, TrackerItemExportModal, TrackerTransformModal,
        KanbanLayout, CalendarLayout, HAvatar, TreeLayout, MatrixLayout, TrackerItemBatchDeleteModal
    },
    props: {
        projectId: {
            required: true,
        },
        trackerId: {
            required: false,
        },
        pageId: {
            required: false,
        },
        sprintId: {
            required: false,
        },
        views: {
            required: false,
        },
        viewId: {
            required: false,
        },
        sprintType: {
            required: false,
        },
        isMatters: {
            required: false,
        }
    },
    data() {
        return {
            loading: false,
            keyword: '',
            isShowViewConfig: false,
            userId: '',
            tracker: { id: '' },
            trackerFilter: {},
            trackerEdit: {},
            currentView: {
                viewConfig: {
                    orderBys: []
                }
            },
            layout: 'table',
            displayStyle: 'HIDE_SUB',
            waitInputSearch: false,
            items: [],
            pagination: {
                current: 1,
                pageSize: 10,
            },
            isShowCreateTrackerItemDialog: false,
            itemId: null,
            isShowEditTrackerItemDialog: false,
            activeKey: '',
            conditionGroups: [{
                conditions: []
            }],
            members: [],
            filterIcon: "expandDown",
            groupList: [
                { id: 1, name: '不分组', value: '' },
                { id: 2, name: '状态类型', value: 'meaningId' },
                { id: 3, name: '创建者', value: 'createBy' },
                { id: 4, name: '负责人', value: 'ownerId' },
                { id: 5, name: '优先级', value: 'priorityId' },
            ],
            groupSelect: ['全部'],
            groupBy: '',
            groupByName: '不分组',
            groupMenu: [],
            groupByShow: false,
            groupSearch: '',
            registerHourVisible: false,
            trackerItem: {},
            selectedRows: [],
            selectedRowKeys: [],
            showSprintOperateDialog: false,
            customRowCheck: [],
            customRowCheckAll: false,
            customRowList: [],
            customRowVisible: false,
            customRowChange: false,
            indeterminate: true,
            tableColumn: [],
            prioritys: [],
            isShowSaveButton: false,
            isInitLoad: true,
            isShowCreateDialog: false,
            moreMenuVisible: false,
            isShowImportDialog: false,
            isShowExportDialog: false,
            isBatchModfigle: false,
            isShowTransformDialog: false,
            isShowBatchDelDialog: false,
            batchType: '',
            focusValue:'',
            statusPopoverVisible:{},
        };
    },
    computed: {
        ...mapGetters("project", ["currentProjectKeyName"]),
        proejctPageId() {
            return this.$route.params.pageId
        },
        groupMenuFilter() {
            var regexp = new RegExp(this.groupSearch)
            return this.groupMenu.filter(v => {
                return regexp.test(v.name)
            });
        },
        getItemList() {
            var itemList = [];
            if (this.items) {
                itemList = Object.assign([], this.items)//this.items.filter(v => { return v.tracker })//(new RegExp(this.keyword)).test(v.name)&&
            }
            if (this.displayStyle == 'HIDE_SUB' && this.trackerId) {
                itemList = itemList.filter(item => item.tracker?.id == this.trackerId)
            }
            var notPagePerm = this.proejctPageId && !hasPermission("PAGE_WRITE",this.proejctPageId);
            itemList.forEach(item => {
                if (item.parent) {
                    item.parentId = item.parent.id;
                }
                if (!item.priority) {
                    this.$set(item, 'priority', {})
                }
                this.$set(item, 'notPagePerm', notPagePerm)

            })
            return itemList;

        },
    },
    watch: {
        trackerId: {
            handler: function (newVal, oldVal) {
                if (newVal === "" && this.sprintId) {
                    this.loadData();
                }else if (newVal != "code") {
                    this.loading=true;
                    this.initTracker();
                }
            }
        },
        viewId: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        sprintId: {
            handler: function (newVal, oldVal) {
                if (newVal) {
                    this.loadData();
                }
            }
        },
        sprintType: {
            handler: function (newVal, oldVal) {
                if (newVal == 'All') {
                    this.loadData();
                }
                this.selectedRowKeys = [];
                this.loadtableColumn();
            }
        },
        customRowVisible: {
            handler: function (newVal, oldVal) {
                if (!newVal && this.customRowChange) {
                    this.loadtableColumn();
                    this.initLoadTableColumn();
                    this.isShowSaveButton = true;
                    this.customRowChange = false;
                }
            }
        },
        groupSelect: {//分组选项改变
            handler: function (newVal, oldVal) {
                this.refresh();
            }
        }
    },
    created() {
        this.userId = this.$store.getters['account/user'].id;
    },
    mounted() {
        this.tableColumn = this.initTableColumn();
        this.initTracker();
        this.loadTrackerStatus();
        findProjectUsers(this.projectId).then(resp => {
            this.members = resp;
        });
    },
    methods: {
        //批量修改
        onCancelTransform() {
            this.isShowTransformDialog = false;
            this.isShowBatchDelDialog = false;
            this.onClickBatchModifigleCancel();
        },
        onClickBatchDeleteStart(){
            this.batchType='delete'
            this.batchModfigle();
        },
        onClickBatchModifigleStart() {
            this.batchType='update'
            this.batchModfigle();
        },
        batchModfigle(){
            this.selectedRowKeys = [];
            this.selectedRows = [];
            this.$refs["trackerItemTable"].clearCheckboxRow()
            this.$refs["trackerItemTable"].clearCheckboxReserve()
            this.isBatchModfigle = true;
            this.loadtableColumn();
        },
        onClickBatchModifigleCancel() {
            this.selectedRowKeys = [];
            this.selectedRows = [];
            this.isBatchModfigle = false;
            this.$refs["trackerItemTable"].clearCheckboxRow()
            this.$refs["trackerItemTable"].clearCheckboxReserve()
            this.loadtableColumn();
        },
        onClickBatchModifigleNext() {
            if (this.selectedRowKeys.length == 0) {
                VXETable.modal.message({ content: '请至少选择一个工作项', status: 'warning' })
            } else {
                if(this.batchType=='delete'){
                    this.isShowBatchDelDialog= true;
                }else{                    
                    this.isShowTransformDialog = true;
                }

            }
        },
        //导入
        onClickImport() {
            this.moreMenuVisible = false;
            this.isShowImportDialog = true;
        },
        onClickExport() {
            this.moreMenuVisible = false;
            this.isShowExportDialog = true;
        },
        onImportDialogOK() {
            this.isShowImportDialog = false;
            this.loadData();
        },
        onExportDialogOK(selectedRowIds) {
            // this.loading = true;
            let sort = [];
            let filter = { "conditionGroups": this.conditionGroups }
            this.currentView.viewConfig?.orderBys?.forEach(item => {
                if (item.field.systemProperty) {
                    sort.push(item.field.systemProperty + "," + item.orderBy)
                } else if (this.tracker.trackerFields.find(field => field.id == item.field.id)) {
                    item.field = this.tracker.trackerFields?.find(field => field.id == item.field.id)
                    sort.push(item.field.systemProperty + "," + item.orderBy)
                }

            })
            exportTrackerItems(this.projectId, this.trackerId, this.sprintId, filter, selectedRowIds, null, sort).then(res => {
                console.log("rere",res)
                const link = document.createElement('a')
                const blob = new Blob([res], { type: 'application/vnd.ms-excel'} )
                link.style.display = 'none'
                link.href = URL.createObjectURL(blob)
                link.download = this.tracker.name || '导出工作项' + '.xls'
                document.body.appendChild(link)
                link.click()
                document.body.removeChild(link)
            })
        },
        //
        onCreateViewDialogOK(view) {
            view.system = false
            createView(view).then(resp => {
                this.isShowCreateDialog = false
                this.$emit("refresh", resp)
                this.isShowSaveButton = false;
            })
        },
        onClickResetView() {
            this.isShowSaveButton = false;
            this.tableColumn = this.initTableColumn();
            this.initCustomRow();
            this.loadData();
        },
        onClickSaveAsView() {
            this.currentView.viewConfig.tableConfig.layout = this.layout
            this.currentView.viewConfig.tableConfig.groupBy = this.groupBy
            this.currentView.viewConfig.tableConfig.displayStyle = this.displayStyle
            this.currentView.viewConfig.tableConfig.fields = this.customRowCheck
            this.isShowCreateDialog = true;
        },
        onClickSaveView() {
            this.currentView.viewConfig.tableConfig.layout = this.layout
            this.currentView.viewConfig.tableConfig.groupBy = this.groupBy
            this.currentView.viewConfig.tableConfig.displayStyle = this.displayStyle
            this.currentView.viewConfig.tableConfig.fields = this.customRowCheck

            if (this.currentView.viewType == 'PUBLIC') {
                VXETable.modal.confirm({
                    title: '保存公共视图',
                    content: '公共视图显示条件将会被更改，有该视图查看权限的成员可以查看。是否保存？'
                }).then(type => {
                    if (type === 'confirm') {
                        if (this.currentView?.id) {
                            updateView(this.currentView).then(res => {
                                VXETable.modal.message({ content: '更新成功', status: 'success' })
                            })
                        }
                    }
                    this.isShowSaveButton = false;
                })
            } else {
                if (this.currentView?.id) {
                    updateView(this.currentView).then(res => {
                        VXETable.modal.message({ content: '更新成功', status: 'success' })
                    })
                    this.isShowSaveButton = false;
                }
            }


        },
        //单元格编辑
        onChangeDateEdit(e, systemProperty, row) {
            changeSystemField(row.id, systemProperty, e.value).then(resp => {
                VXETable.modal.message({ content: '更新成功', status: 'success' })
            })
        },
        onRefreshSuccess() {
            VXETable.modal.message({ content: '更新成功', status: 'success' })
        },
        onChangeOwnerEdit() {
            console.log("onChangeOwnerEdit")
        },
        onChangePriorityEdit(e, row) {
            for (let priority of this.prioritys) {
                if (e.value == priority.id) {
                    row.priority = Object.assign({}, priority);
                    break;
                }
            }
            changeSystemField(row.id, "priority", e.value).then(resp => {
                VXETable.modal.message({ content: '更新成功', status: 'success' })
            })
        },
        onBlurEdit(row, systemProperty, value) {
            if (value && value != this.focusValue) {
                changeSystemField(row.id, systemProperty, value).then(resp => {
                    VXETable.modal.message({ content: '更新成功', status: 'success' })
                })
            }
            this.focusValue = ''
        },
        activeCellMethod(row) {
            return true
        },
        beforeEditMethod({ row, rowIndex, column, columnIndex }) {
            if (this.proejctPageId && !hasPermission("PAGE_WRITE",this.proejctPageId)) return false;
            return true;
        },
        //自定义表头
        initTableColumn() {
            return [
                {
                    field: '', type: 'checkbox', width: 60, visible: (this.sprintType || this.isBatchModfigle)
                        && (!this.proejctPageId || hasPermission("PAGE_WRITE",this.proejctPageId)) ? true : false
                },
                { field: 'itemNo', title: '编号', minWidth: 100, slots: { default: 'itemNo_default', } },
                { field: 'name', title: '标题', editRender: {}, minWidth: 300, slots: { default: 'name_default', edit: 'name_edit' } },
                { field: 'priority', title: '优先级', editRender: {}, minWidth: 100, slots: { default: 'priority_default', edit: 'priority_edit' } },
                { field: 'status', title: '状态', minWidth: 100, slots: { default: 'status_default', header: 'status_header' } },
                { field: 'planEndDate', title: '计划结束时间', editRender: {}, minWidth: 150, showHeaderOverflow: "tooltip", slots: { edit: 'plan_end_time_edit' } },
                { field: 'createBy', title: '创建者', minWidth: 100, slots: { default: 'createBy_default' } },
                { field: 'ownerId', title: '负责人', minWidth: 100, slots: { default: 'owner_default', header: 'owner_header' } },
                { field: 'createDate', title: '创建日期', minWidth: 100 },
            ]
        },
        initTableColumnTail() {
            return [
                { field: 'lastModifiedBy', title: '修改者', minWidth: 100, slots: { default: 'lastModified_default' } },
                { field: 'estimateWorkingHours', title: '预计花费工时', align: "right", editRender: {}, minWidth: 150, showHeaderOverflow: "tooltip", slots: { edit: 'estimate_working_hours_edit' } },
                { field: 'registeredWorkingHours', title: '已登记工时', align: "right", minWidth: 150, showHeaderOverflow: "tooltip", slots: { default: 'registered_working_hours_default', header: 'registered_working_hours_header' } },
                { field: 'remainingWorkingHours', title: '剩余工时', align: "right", editRender: {}, minWidth: 100, slots: { edit: 'remaining_working_hours_edit' } },
                { field: 'assignedToId', title: '分配给', minWidth: 100, slots: { default: 'assigned_to_default', header: 'assigned_to_header' } },
                { field: 'assignedDate', title: '分配日期', editRender: {}, minWidth: 100, slots: { edit: 'assigned_date_edit' } },
                { field: 'planStartDate', title: '计划开始时间', editRender: {}, minWidth: 150, showHeaderOverflow: "tooltip", slots: { edit: 'plan_start_date_edit' } },
                { field: 'realStartDate', title: '实际开始时间', editRender: {}, minWidth: 150, showHeaderOverflow: "tooltip", slots: { edit: 'real_start_date_edit' } },
                { field: 'realEndDate', title: '实际结束时间', editRender: {}, minWidth: 150, showHeaderOverflow: "tooltip", slots: { edit: 'real_end_date_edit' } },
                { field: 'progress', title: '进度', editRender: {}, minWidth: 100, slots: { edit: 'progress_edit' } },
                { field: 'closeDate', title: '关闭时间', editRender: {}, minWidth: 100, slots: { edit: 'close_date_edit' } },
            ]
        },
        
        initLoadTableColumn() {
            let selectedCheck = [];
            let unselectedCheck = [];
            this.customRowCheck.forEach(item => {
                const field = this.customRowList.find(f => f.id === item);
                if (field) {
                    selectedCheck.push(field);
                }
            });
            unselectedCheck=this.customRowList.filter(item=>!this.customRowCheck.includes(item.id));
            this.customRowList = [...selectedCheck, ...unselectedCheck];

        },
        loadtableColumn() {
            let newTableColumn = [];
            if (this.sprintType || this.isBatchModfigle) {
                newTableColumn.push({
                    type: 'checkbox', width: 60, visible: (this.sprintType || this.isBatchModfigle)
                        && (!this.proejctPageId || hasPermission("PAGE_WRITE",this.proejctPageId))
                })
            }
            let oldColumn = [...this.initTableColumn(), ...this.initTableColumnTail()];
            let newCustomRowCheck=[];
            for (let field of this.customRowList) {
                if (this.customRowCheck.indexOf(field.id) != -1) {
                    newCustomRowCheck.push(field.id);
                    if (field.systemProperty) {
                        let index;
                        for (index = 0; index < oldColumn.length; index++) {
                            if (field.systemProperty == oldColumn[index].field) {
                                newTableColumn.push(oldColumn[index]);
                                break;
                            }
                        }
                        if (index == oldColumn.length) {
                            let columnField = field.systemProperty;
                            if (field.inputType == "USER" || field.inputType == 'STATUS_TYPE' || field.inputType == 'WORK_ITEM') {
                                columnField += ".name"
                            } else if (field.systemProperty.endsWith("Id")) {
                                columnField = columnField.slice(0, columnField.length - 2)
                                columnField += ".name"
                            }
                            newTableColumn.push({ field: columnField, title: field.name, minWidth: field.name.length * 25, showOverflow: "tooltip", showHeaderOverflow: "tooltip" });
                        }
                    } else {
                        newTableColumn.push({
                            title: field.name, showOverflow: "tooltip", minWidth: 100,
                            slots: {
                                default: ({ row }) => {
                                    let value = row.values[field.id]
                                    return [<span>{(value)}</span>]
                                },
                            }
                        });
                    }
                }
            }
            if (newTableColumn[newTableColumn.length - 1]) {
                newTableColumn[newTableColumn.length - 1].minWidth = 180
            }
            this.tableColumn = newTableColumn;
            this.customRowCheck=newCustomRowCheck
        },
        onClickCustomRowReset() {
            this.initCustomRow();
        },
        onClickCustomRowOk() {
            this.customRowVisible = false;
        },

        initCustomRow() {
            let defaultCustomRow = ['itemNo', 'name', 'priority', 'status', 'planEndDate', 'createBy', 'ownerId', 'createDate'];
            // this.customRowList = Object.assign([], this.tracker.trackerFields);
            this.customRowList = this.tracker.trackerFields.filter(item=>item.systemProperty)
            this.customRowCheck = []
            for (let index in defaultCustomRow) {
                for (let fieldIndex in this.customRowList) {
                    if (this.customRowList[fieldIndex]?.systemProperty) {
                        if (defaultCustomRow[index] == this.customRowList[fieldIndex].systemProperty) {
                            this.customRowCheck.push(this.customRowList[fieldIndex].id);
                            let t = this.customRowList[index];
                            this.customRowList[index] = this.customRowList[fieldIndex];
                            this.customRowList[fieldIndex] = t;
                            break;
                        }
                    }

                }
            }
        },
        onChangeCustomRow() {
            if(this.tracker.trackerFields.length==this.customRowCheck.length){
                this.customRowCheckAll = true;
                this.indeterminate = false;
            }
            else if (this.customRowCheck.length == 0) {
                this.indeterminate = false;
            } else {
                this.indeterminate = true;
            }
            this.customRowChange = true;
        },
        onChangeCustomRowAll() {
            if (this.customRowCheckAll) {
                this.customRowCheck = this.tracker.trackerFields.filter(item=>item.systemProperty).map(item => item.id)
            } else {
                this.customRowCheck = [];
            }
            this.indeterminate = false;
            this.customRowChange = true;
        },
        //迭代
        onRemoveSprint() {
            VXETable.modal.confirm({
                title: '移除迭代',
                content: '正在将' + this.selectedRowKeys.length + '个工作项移出迭代，工作项移出迭代后为未规划工作项，是否移出?'
            }).then(type => {
                if (type === 'confirm') {
                    updateTrackerItemSprint('', this.selectedRowKeys).then(() => {
                        VXETable.modal.message({ content: this.selectedRowKeys.length + '个工作项移出迭代成功', status: 'success' })
                        this.loadData();
                    })
                }
            })
        },
        onSprintDialogOK() {
            this.loadData();
        },
        selectChangeEvent({ checked, records, reserves }) {
            this.selectedRowKeys = [...reserves.map(v => v.id), ...records.map(v => v.id)];
            this.selectedRows = [...reserves, ...records];
        },

        onClickWorkHours(item) {
            console.log("onClick",item)
            this.trackerItem = item
            this.registerHourVisible = true
        },
        onOKRegisterHour(workHours) {
            this.registerHourVisible = false
            if (this.trackerItem.registeredWorkingHours) {        
                this.trackerItem.registeredWorkingHours = parseInt(this.trackerItem.registeredWorkingHours) 
                this.trackerItem.registeredWorkingHours += parseInt(workHours.actualHour)
            } else {
                this.trackerItem.registeredWorkingHours = parseInt(workHours.actualHour)
            }
            this.trackerItem.remainingWorkingHours = workHours.remainHour
            console.log("123",this.trackerItem)
            createWorkHours(this.trackerItem.id, workHours).then(resp => {
                VXETable.modal.message({ content: '登记工时添加成功', status: 'success' })
            })
            changeSystemField(this.trackerItem.id, "remainingWorkingHours", workHours.remainHour).then(resp => {
            })
            changeSystemField(this.trackerItem.id, "registeredWorkingHours", this.trackerItem.registeredWorkingHours).then(resp => {
            })

        },
        onRefreshFilter() {
            let filter = { "conditionGroups": this.conditionGroups }
            if (this.conditionGroups) {
                this.currentView.viewConfig.filter = filter
            }
            this.isShowSaveButton = true;
            this.refresh();
        },
        loadData: function () {
            this.isShowSaveButton=false;
            this.isInitLoad=true;
            this.conditionGroups=[];
            this.groupMenu = [];
            this.selectedRowKeys = [];
            this.$refs["trackerItemTable"].clearCheckboxReserve()
            this.pagination = { current: 1, pageSize: 10, }

            if (this.viewId) {
                this.loading = true;
                let trackerId=this.trackerId;
                findOneView(this.viewId).then(resp => {
                    if(this.trackerId!=trackerId){
                        return;
                    }
                    this.currentView = resp
                    if (this.currentView.viewConfig) {
                        if (this.currentView.viewConfig.filter?.conditionGroups) {
                            this.conditionGroups = Object.assign([], this.currentView.viewConfig.filter.conditionGroups)
                        }
                        if (this.currentView.viewConfig.tableConfig) {
                            this.layout = this.currentView.viewConfig.tableConfig.layout || 'table'
                            this.groupBy = this.currentView.viewConfig.tableConfig.groupBy || ''
                            this.displayStyle = this.currentView.viewConfig.tableConfig.displayStyle || 'HIDE_SUB'
                            if (this.currentView.viewConfig?.tableConfig.fields) {
                                this.customRowCheck = this.currentView.viewConfig.tableConfig.fields
                            } else {
                                this.initCustomRow();
                            }
                        } else {
                            this.currentView.viewConfig.tableConfig = {}
                            this.displayStyle = 'HIDE_SUB'
                            this.layout = 'table'
                            this.groupBy = ''
                            this.initCustomRow();
                        }
                        if (!this.currentView.viewConfig.orderBys) {
                            this.currentView.viewConfig.orderBys = Vue.observable([]);
                        }
                    } else {
                        this.currentView.viewConfig = Vue.observable({
                            type: "TRACKER_VIEW_CONFIG",
                            tableConfig: {},
                            orderBys: [],
                            filter: {},
                        })
                        this.displayStyle = 'HIDE_SUB'
                        this.layout = 'table'
                        this.groupBy = ''
                        this.conditionGroups = Vue.observable([{ conditions: [] }])
                        this.initCustomRow();
                    }
                    if (!this.trackerId) {
                        this.initMatersData(resp);
                    } else {
                        this.initViewFilter(resp);
                    }
                }).finally(() => {
                    if(this.trackerId==trackerId){
                        this.onChangeGroupBy(this.groupBy,true)
                        this.loadWorkItems()
                    }
                    
                })
            } else if (this.sprintType == 'Unplanned') {
                this.loadWorkItems()
            } else if (this.sprintType == 'All') {
                this.loadWorkItems()
            } else if (this.sprintId) {
                this.loadWorkItems()
            } else {
                this.loadWorkItems()
            }

        },
        initTracker() {
            if (this.trackerId && !this.sprintType) {
                findOneTracker(this.trackerId).then(resp => {
                    this.tracker = resp;
                    this.trackerFilter.trackerFields = this.tracker.trackerFields;
                    this.trackerFilter.trackerStatuses = this.tracker.trackerStatuses;
                }).finally(() => {
                    if(!this.groupList.find(item=>item.value=='statusId')){
                        this.groupList.push({ id: 6, name: '状态', value: 'statusId' })
                    }
                    this.initCustomRow();
                    if(this.sprintId){
                        this.loading=false;
                    }
                })
            } else {
                this.initTrackerFields();
                this.initCustomRow();
            }
        },
        mattersMethod(field) {
            this.conditionGroups.forEach(item => {
                let flag = true;
                item.conditions.forEach(condition => {
                    if (condition.field == field.field) {
                        flag = false;
                        if (condition.operator === "INCL") {
                            if (condition.value) {
                                if (condition.value.indexOf(this.userId) == -1) {
                                    condition.value.push(this.userId)
                                }
                            } else {
                                condition.value = [this.userId]
                            }
                        }
                    }
                })
                if (flag) {
                    item.conditions.push({
                        "id": field.id,
                        "type": "USER",
                        "field": field.field,
                        "value": [
                            this.userId
                        ],
                        "operator": "INCL"
                    })
                }
            })
        },
        initMatersData(view) {
            if (!this.conditionGroups || this.conditionGroups.length == 0 || this.conditionGroups[0].conditions.length == 0) {
                this.conditionGroups = Vue.observable([{ conditions: [] }]);
                if (view.name == '我负责的') {
                    this.mattersMethod({ id: '6', field: 'ownerId' })
                } else if (view.name == '我关注的') {
                    this.mattersMethod({ id: '26', field: 'watchers' })
                } else if (view.name === '分配给我的') {
                    this.mattersMethod({ id: '13', field: 'assignedToId' })
                }
            }
        },
        initViewFilter(view) {
            if (!this.conditionGroups || this.conditionGroups.length == 0) {
                this.conditionGroups = [{ conditions: [] }];
                let statusNames = this.tracker?.trackerStatuses?.map(v => v.name);
                if (statusNames?.indexOf(view.name) > -1) {
                    let field = this.tracker.trackerFields?.find(field => field.systemProperty === "status")
                    let status = this.tracker.trackerStatuses?.find(status => status.name === view.name)
                    let condition = {
                        "id": field.id,
                        "type": field.inputType,
                        "field": field.systemProperty,
                        "value": [
                            status.id
                        ],
                        "operator": "INCL"
                    }
                    this.conditionGroups[0].conditions[0] = condition
                }

            }
        },
        onChangeGroupBy(value,isinit) {
            this.groupBy = value
            this.groupByName = this.groupList.find(item => item.value == value)?.name;
            if (!isinit) {
                this.isShowSaveButton = true;
            }
            if (value == "") {
                this.groupMenu = [];
                if (this.groupSelect[0] != '全部') {
                    this.loadWorkItems();
                }
                return;
            }
            this.groupSelect[0] = '全部'
            let filter = { "groupCondition": { field: this.groupBy }, "conditionGroups": this.conditionGroups }
            this.groupMenu = [];
            let arr = [], obj, total = 0, isnull = false, nullCount = 0
            if (this.groupBy == "createBy" || this.groupBy == "ownerId") {
                arr = this.members
            } else if (this.groupBy == "statusId") {
                arr = this.trackerFilter.trackerStatuses
            } else {
                arr = this.trackerFilter[this.groupBy.replace("Id", "")]
            }
            if (!arr || arr.length == 0) {
                this.groupMenu.unshift({ "name": '全部', "count": this.pagination.total, "field": this.groupBy })
                return;
            }
            findGroupItems(this.projectId, this.trackerId, this.sprintId, filter, this.keyword).then(resp => {
                resp.forEach(v => {
                    if (v.field) {
                        obj = arr?.find(item => item.id == v.field)
                        this.groupMenu.push({ "name": obj.name, "count": v.value, "field": this.groupBy, "value": v.field })
                    } else {
                        isnull = true;
                        nullCount = v.value
                    }
                    total += parseInt(v.value);
                })
                if (isnull) {
                    this.groupMenu.push({ "name": '未设置', "count": nullCount, "field": this.groupBy, "value": null })
                }
                this.groupMenu.unshift({ "name": '全部', "count": total, "field": this.groupBy })
            })
        },

        getFilterCount() {
            let count = 0;
            this.conditionGroups.forEach(item => {
                item.conditions.forEach(item2 => {
                    if (item2 && item2.value) {
                        count++;
                    }
                })
            })
            return count;
        },
        onClickFilter() {
            if (this.activeKey == 'true') {
                this.activeKey = 'false'
                this.filterIcon = "expandDown"
            } else {
                this.activeKey = 'true'
                this.filterIcon = 'collapseUp'
            }
        },

        handlePageChange(pagination) {
            this.pagination.current = pagination.currentPage
            this.pagination.pageSize = pagination.pageSize
            this.isInitLoad = true;
            this.loadWorkItems();
        },
        refresh() {
            // this.$emit("refresh")
            if (this.layout == 'table') {
                this.pagination = { current: 1, pageSize: 10, }
                this.loadWorkItems()
            } else if (this.layout == 'kanban') {
                this.$refs["kanbanRef"].loadData()
            } else if (this.layout == 'calendar') {
                this.$refs["calendarRef"].loadData()
            }

        },
        loadWorkItems(isRoute) {
            this.loading = true;
            let keyword = this.keyword
            let sort = [];
            let pageable = {
                page: this.pagination.current - 1,
                size: this.pagination.pageSize,
            }
            let filter = { "conditionGroups": this.conditionGroups }
            if (this.groupBy != '' && this.groupSelect[0] != "全部") {
                let menu = this.groupMenu?.find(v => v.name == this.groupSelect[0])
                filter.groupCondition = { "field": menu.field, "value": menu.value }
            }

            this.currentView.viewConfig?.orderBys?.forEach(item => {
                if(item.field.systemProperty)   {
                    sort.push(item.field.systemProperty + "," + item.orderBy)
                } else if (this.tracker.trackerFields && this.tracker.trackerFields?.find(field => field.id == item.field.id)) {
                    item.field = this.tracker.trackerFields?.find(field => field.id == item.field.id)
                    sort.push(item.field.systemProperty + "," + item.orderBy)
                }

            })
            if (this.pageId && !this.isMatters) {
                findTrackerItemsByPageIdWithoutInternalTrackers(this.projectId, this.pageId, pageable, sort).then(res => {
                    this.items = res.content
                    console.log("res", res)
                    this.pagination.total = parseInt(res.totalElements);
                }).finally(() => {
                    this.findItemsFinally(isRoute);
                })
            }else{
                findTrackerItems(this.projectId, this.trackerId, this.sprintId, filter, keyword, pageable, sort).then(resp => {
                    this.items = resp.content
                    console.log("resp",resp)
                    if(resp.totalElements){
                        this.pagination.total = parseInt(resp.totalElements);
                    }else{
                        this.pagination.total = resp.content.length;
                    }
                }).finally(() => {
                    this.findItemsFinally(isRoute);
                })
            }

        },
        findItemsFinally(isRoute) {
            if (this.isInitLoad) {
                this.initLoadTableColumn();
                this.loadtableColumn()
                this.isInitLoad = false
            }
            if (this.$route.params.itemId && !isRoute) {
                findOneTrackerItem(this.$route.params.itemId).then(res => {
                    if (res) {
                        this.onEditTrackerItem(res);
                    }
                })

            }
            this.loading = false;
        },
        onViewConfig: function () {
            this.isShowViewConfig = true
        },
        onChangeLayout: function () {
            if (this.layout) {
                if (this.layout != 'table') {
                    this.displayStyle = 'HIDE_SUB'
                }
            }
            this.isShowSaveButton = true;
        },

        onSearch: function () {
            this.isInitLoad = true;
            this.refresh();
        },
        onBlurSearch() {
            this.isInitLoad = true;
            if (!this.keyword) {
                this.waitInputSearch = false
            }
            this.refresh();
        },
        onCreateTrackerItem: function () {
            this.isShowCreateTrackerItemDialog = true
        },
        onCreateTrackerItemOK: function (trackerItem) {
            createTrackerItem(trackerItem).then(resp => {
                this.$emit("refreshSprint")
                this.loadData()
                this.isShowCreateTrackerItemDialog = false
            })
        },
        onCreateTrackerItemCancel: function () {
            this.isShowCreateTrackerItemDialog = false
        },
        onEditTrackerItemCancel: function () {
            console.log("onEditTrackerItemCancel,2323423")
            this.$emit("refreshSprint")
            this.isInitLoad = true;
            if(this.layout == 'table'){
                this.loadWorkItems(true);
            }else if(this.layout == 'kanban'){
                this.$refs["kanbanRef"].loadData();
            }else if(this.layout == 'calendar'){
                this.$refs["calendarRef"].loadData();
            }
            // this.selectedRows = [];
            // this.selectedRowKeys = [];
            this.isShowEditTrackerItemDialog = false;
        },
        onViewCancel: function (isChange) {
            this.isShowViewConfig = false
            if (isChange) {
                this.loadData();
            }
        },
        onEditTrackerItem: function (row) {
            if (row.notPagePerm) {
                VXETable.modal.message({ content: '权限不足', status: 'warning' })
                return;
            }
            this.itemId = row.id
            if (row.tracker?.id && (!this.tracker.id || row.tracker.id != this.tracker.id)) {
                findOneTracker(row.tracker.id).then(resp => {
                    this.trackerEdit = resp;
                }).finally(() => {
                    this.isShowEditTrackerItemDialog = true
                })
            } else {
                this.trackerEdit = cloneDeep(this.tracker)
                this.isShowEditTrackerItemDialog = true
            }
        },
        onEditTrackerItemOK: function (item) {
            this.isShowEditTrackerItemDialog = false
        },

        orderByFields: function (orderByItem) {
            let calcFields = this.tracker.trackerCalcFields || []
            calcFields = calcFields.filter(f => { return f.calcMethod === 'STATUS_MEANING' })

            let fields = []
            if (this.trackerId) {
                fields = this.tracker.trackerFields || []
            } else {
                fields = this.trackerFilter.trackerFields || []
            }

            fields = fields.filter(f => {
                return f.inputType === 'INTEGER' || f.inputType === 'TEXT' || f.inputType === 'STATUS'
                    || f.inputType === 'DATE' || f.inputType === 'USER' || f.inputType === 'WORK_ITEM_NO'
            })

            let all = calcFields.concat(fields)

            let ids = this.currentView.viewConfig?.orderBys?.map(f => f.field?.id);
            if (orderByItem) {
                ids = ids.filter(f => {
                    return f !== orderByItem.field?.id
                })
            }
            if (!ids) ids = [];

            let result = all.filter(f => { return ids.indexOf(f.id) < 0 })

            return result
        },
        rowDrop(visiable) {
            if (visiable) {
                let that = this
                this.$nextTick(() => {
                    let xTable = that.$refs.orderByTable;
                    if (xTable) {
                        that.sortable = Sortable.create(
                            xTable,
                            {
                                handle: ".order-row",
                                animation: 150,
                                filter: '.order-row-bottom',
                                onEnd: ({ newIndex, oldIndex }) => {
                                    that.currentView.viewConfig.orderBys.splice(newIndex, 0, this.currentView.viewConfig.orderBys.splice(oldIndex, 1)[0]);
                                    var newArray = this.currentView.viewConfig.orderBys.slice(0);
                                    that.currentView.viewConfig.orderBys = [];
                                    that.$nextTick(function () {
                                        that.currentView.viewConfig.orderBys = newArray;
                                        this.refresh()
                                    });
                                },
                            }
                        );
                    }
                });
            }
        },

        rowDropHeader(visiable) {
            if (visiable) {
                let that = this
                this.$nextTick(() => {
                    let xTable = that.$refs.orderByRow;
                    if (xTable) {
                        that.sortable = Sortable.create(
                            xTable,
                            {
                                handle: ".order-row-header",
                                animation: 150,
                                filter: '.order-row-header-bottom',
                                onEnd: ({ newIndex, oldIndex }) => {
                                    that.customRowList.splice(newIndex, 0, this.customRowList.splice(oldIndex, 1)[0]);
                                    var newArray = this.customRowList.slice(0);
                                    that.customRowList = [];
                                    that.$nextTick(function () {
                                        this.customRowChange = true;
                                        that.customRowList = newArray;
                                    });
                                },
                            }
                        );
                    }
                });
            }
        },

        addOrderItem: function () {
            this.isShowSaveButton = true;
            let orderByFields = this.orderByFields();
            this.currentView.viewConfig.orderBys.push({
                field: {
                    id: orderByFields[0].id, name: orderByFields[0].name,
                    systemProperty: orderByFields[0].systemProperty
                }, orderBy: "ASC"
            })
            this.refresh()
        },
        removeOrderItem: function (orderByItem, index) {
            this.currentView.viewConfig.orderBys.splice(index, 1)
            this.isShowSaveButton = true;
            this.refresh()
        },
        orderConditionChange: function (row, f) {
            if (f) {
                row.field = f;
            }
            this.isShowSaveButton = true;
            this.refresh()
        },
        loadTrackerStatus() {
            findTrackers({ projectId: this.projectId }).then(resp => {
                this.trackerFilter.trackerId = Vue.observable([]);
                resp.forEach(item => {
                    this.trackerFilter.trackerId.push({ id: item.id, name: item.name, icon: item.icon })
                });
            })

            findEnumsByCode('TRACKER_PRIORITY').then((resp) => {
                this.trackerFilter.priority = Vue.observable([]);
                this.prioritys = resp;
                resp.forEach(item => {
                    this.trackerFilter.priority.push({ id: item.id, name: item.name })
                });
            });
            findEnumsByCode('TRACKER_STATUS_MEANING').then((resp) => {
                this.trackerFilter.meaning = Vue.observable([]);
                resp.forEach(item => {
                    this.trackerFilter.meaning.push({ id: item.id, name: item.name })
                });
            });
            findProjects({}, {}).then(resp => {
                this.trackerFilter.projectId = Vue.observable([]);
                resp.content.forEach(item => {
                    this.trackerFilter.projectId.push({ id: item.id, name: item.name })
                });
            })
        },
        initTrackerFields() {
            this.trackerFilter.trackerFields = systemFields;
            this.tracker.trackerFields = this.trackerFilter.trackerFields;
        },
    },
    // beforeDestroy() {
    //     if (this.sortable) {
    //         this.sortable.destroy();
    //     }
    // },
};
</script>
<style lang="less" scoped>
:deep(.content-page .ant-tabs-bar) {
    margin-bottom: 1px;
}

/deep/.ant-popover-inner-content {
    padding: 12px 10px;
}

.table-layout {
    /deep/.ant-popover-inner-content {
        padding: 0;
    }
}

/deep/.ant-fullcalendar-fullscreen .ant-fullcalendar-content {
    position: static;
    width: auto;
    height: 88px;
    overflow-y: hidden;
}



.toolbar {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.order-row {
    width: 440px;
    height: 40px;
    line-height: 40px;
    cursor: move;

    .icon-circle {
        font-size: 18px;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 22px;
        height: 22px;
        // border: solid 1px currentColor;
        border-radius: 50%;
        cursor: pointer;
    }

    .icon-circle .disabled {
        font-size: 18px;
        opacity: .5;
        color: #909090;
        cursor: not-allowed;
        background-color: #e8e8e8;
        border-color: #979797;
    }
}

.transition-status {
    min-width: 110px;
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


a:not(.ui-base-link) {
    font-size: inherit !important;
    text-decoration: none !important;
}

a:hover {
    color: #2872b7;
}

a:active,
a:hover {
    outline: 0;
    text-decoration: none;
}

.task-table-cell {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    // padding: 0 10px;
    cursor: pointer;

    .task-table-cell-content {
        height: 100%;
        display: flex;
        flex: 1 1 auto;
        align-items: center;
        overflow: hidden;

        .task-cell-summary {
            width: 100%;
            height: 100%;
            display: flex;
            flex: 1 1 auto;
            align-items: center;

            .tree-placeholder {
                height: 100%;
                display: flex;
                flex: 0 0 auto;
                justify-content: flex-end;
                align-items: center;
            }

            .task-cell-summary-container {
                height: 100%;
                display: flex;
                flex: 1 1 auto;
                align-items: center;
                overflow: hidden;

                .task-description {
                    display: block;
                    max-width: 100%;

                    .prefix-container {
                        max-width: 100%;
                        display: inline-flex;
                        flex-direction: row;
                        align-items: center;
                        margin: 0 5px 0 0;
                        vertical-align: middle;
                        font-size: 14px;

                        span {
                            white-space: nowrap;
                            text-overflow: ellipsis;
                            overflow: hidden;
                        }

                        .task-icon {
                            margin-right: 5px;
                        }
                    }
                }

                .task-summary-edit {
                    fill-opacity: 0;
                    margin: 5px;
                    min-width: 16px;
                }

                .ui-icon {
                    fill: currentColor;
                    line-height: 1;
                    vertical-align: middle;
                    border-radius: 3px;
                    box-sizing: border-box;
                    display: inline-block;
                    height: 16px;
                    line-height: 14px;
                    width: 16px;
                    transition: opacity .2s;
                    border-radius: 0;
                }
            }
        }

        &:hover {
            .task-summary-edit {
                fill-opacity: 1;
                fill: #606060;
            }
        }
    }
}



.ui-table {
    // height: 400px;
    max-height: 400px;
    // border: solid 1px #dedede;
    border-radius: 3px;
    background-color: #fff;
    display: flex;
    flex-direction: column;
}

.layout-fields-table-row {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    min-height: 40px;
    padding-right: 20px;
    padding-left: 20px;
    padding-top: 10px;
    padding-bottom: 10px;
}

.table-head {
    width: 100%;
    padding: 0 20px;
    font-size: 14px;
    color: #303030;
    font-weight: 500;
    height: 40px;
    line-height: 40px;
    background-color: #f8f8f8;
    // border-bottom: solid 1px #dedede;
    box-sizing: border-box;
}

.table-row {
    width: 100%;
    padding: 0 20px;
    height: 40px;
    line-height: 40px;
    // border-bottom: solid 1px #dedede;
    font-size: 14px;
    color: #303030;
    box-sizing: border-box;
    cursor: move;
}

.table-row:hover {
    background-color: #f6f6f6;
}

.filter-flex {
    flex: 0 1 0%;
    height: 0px;
}

.filter-flex-active {
    flex: 2 1 26%;
}
</style>