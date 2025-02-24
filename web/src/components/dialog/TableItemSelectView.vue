<template>
  <div style="width:100%;height:calc(100% - 50px);display:flex;">
    <div style="width:15%;border-right: 1px solid #cecece;">
      <a-menu mode="vertical" v-model="currentTab" :defaultSelectedKeys="['trackerItems']" @select="onChangeTab">
        <a-menu-item style="background-color: #f5f9f5;" key="selected" tab="">已选择项
          <span v-if="getSelectedCount!=0"> ({{getSelectedCount}})</span>
        </a-menu-item>
        <a-menu-item key="trackerItems" tab="">工作项
          <span v-if="getSelectedItemCount('trackerItems')!=0"> ({{getSelectedItemCount('trackerItems')}})</span>
        </a-menu-item>
        <a-menu-item key="projectPlans" tab="">项目计划
          <span v-if="getSelectedItemCount('projectPlans')!=0"> ({{getSelectedItemCount('projectPlans')}})</span>
        </a-menu-item>
        <a-menu-item key="targetVersions" tab="">版本
          <span v-if="getSelectedItemCount('targetVersions')!=0"> ({{getSelectedItemCount('targetVersions')}})</span>
        </a-menu-item>
        <a-menu-item key="sprints" tab="">迭代
          <span v-if="getSelectedItemCount('sprints')!=0"> ({{getSelectedItemCount('sprints')}})</span>
        </a-menu-item>
        <a-menu-item key="milestones" tab="">里程碑
          <span v-if="getSelectedItemCount('milestones')!=0"> ({{getSelectedItemCount('milestones')}})</span>
        </a-menu-item>
        <a-menu-item key="tasks" tab="">计划执行
          <span v-if="getSelectedItemCount('tasks')!=0"> ({{getSelectedItemCount('tasks')}})</span>
        </a-menu-item>
        <a-menu-item key="deliverables" tab="">交付物
          <span v-if="getSelectedItemCount('deliverables')!=0"> ({{getSelectedItemCount('deliverables')}})</span>
        </a-menu-item>
        <a-menu-item key="documents" tab="">文档
          <span v-if="getSelectedItemCount('documents')!=0"> ({{getSelectedItemCount('documents')}})</span>
        </a-menu-item>
      </a-menu>
    </div>
    <div style="padding:10px;width:85%;overflow:auto">

      <div v-show="currentTab[0] == 'selected'" style="height:100%;">
        <a-row style="margin-bottom: 15px">
          <a-col :span="12">
            <div class="alert">
              <div type="info" :show-icon="true">
                <div class="message" style="font-size: 12px">
                  已选择&nbsp;<a>{{ getSelectedCount }}</a>&nbsp;项，
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <a-col :offset="8" :span="4">

            </a-col>
            <a-col :span="12">
              <a-input-search placeholder="搜索标题" v-model="keyword['selected']" @search="handleSearch" />
            </a-col>
          </a-col>
        </a-row>
        <div class="table-box" :style="{ height: tableHeight }">
          <vxe-table ref="vxeTableSelected" height="100%" row-id="id" style="overflow:auto"
            :data="getSelectedData?.filter(v => (new RegExp(keyword['selected'])).test(v.name))"
            :loading="loading" :row-config="{ isHover: true }" 
              @checkbox-all="selectedChangeAll" @checkbox-change="selectedChange"
            :checkbox-config="{ 
              checkRowKeys: selectedRowKey['selected'],checkField: 'checked', 
            }" >

            <vxe-column type="checkbox" title="" width="30"></vxe-column>

            <vxe-column field="name" title="名称">
              <template #default="{ row }">
                {{ row.name }}
              </template>
            </vxe-column>

            <vxe-column v-if="reviewId" field="" title="评审状态">
              <template #default="{ row }">
                  {{ void ({status,item} = getReviewStatus(row.id)) }} 
                  {{ void (user= members.find(v=>v.id == status?.reviewerId)) }}
                  <a-tag v-if="!item?.name||item?.name=='未评审'" style="border-radius: 10px;font-size: 13px;"
                      :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                      {{  item?.name||'未评审' }}
                  </a-tag>
                  <a-popover v-else :overlayStyle="{zIndex:9999}">
                    <template slot="content">
                      <div style="font-size: 14px;color: rgb(32, 45, 64);font-weight: bold;margin-bottom: 5px;">评审建议</div>
                      <div style="display: flex;">
                        <div>
                          <h-avatar :name="user?.name" :icon="user?.icon" :isShowName="false" size="default"></h-avatar>
                        </div>
                        <div style="margin-left: 8px;">
                          <div style="font-size: 13px;">
                            <a-icon theme="filled" :style="{ 'margin-right': '5px', color: item?.color }" type="check-circle" />{{item?.name}}
                            <span>发表于 {{ status?.createDate }}</span>
                          </div>
                          <a-tooltip>
                            <template slot="title">
                              {{status?.description}}
                            </template>
                            <div style="max-width: 260px; overflow: hidden;white-space: normal;text-overflow: ellipsis;">
                              {{ status?.description }}
                            </div>
                          </a-tooltip>
                        </div>
                      </div>
                    </template>
                    <a-tag style="border-radius: 10px;font-size: 13px;"
                        :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                        {{  item?.name||'未评审' }}
                    </a-tag>
                  </a-popover>
              </template>
            </vxe-column>
          </vxe-table>
        </div>
      </div>

      <div v-show="currentTab[0] == 'trackerItems'" style="height:100%;display:flex;flex-direction: column;">
        <a-row style="margin-bottom: 15px">
          <a-col :span="12">
            <div class="alert">
              <div type="info" :show-icon="true">
                <div class="message" style="font-size: 12px">
                  已选择&nbsp;<a>{{ selectedRowKey['trackerItems']?.length }}</a>&nbsp;项，共{{ pagination.total }}个符合条件的结果：
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
              <a-col :offset="8" :span="4">
                <a-badge :number-style="{ backgroundColor: '#338fe5' }" :count="getFilterCount()">
                  <a-button type="text" @click="onClickFilter">筛选<a-icon :component="filterIcon" /></a-button>
                </a-badge>
              </a-col>
              <a-col style="padding-left: 10px;" :span="12">
                <a-input-search placeholder="搜索工作项标题" v-model="queryParam.keyword" @search="handleSearch" />
              </a-col>
          </a-col>
        </a-row>
        <tracker-item-filter  style="transition: all .25s;" :class="activeKey == 'true' ? 'filter-flex-active' : 'filter-flex'"
          :conditionGroups="conditionGroups" :activeKey="activeKey" :members="members" :tracker="tracker"
          @refresh="refresh"></tracker-item-filter>

        <div class="table-box" :style="{ height: tableHeight }">
          <vxe-table ref="vxeTableTrackerItems" row-id="id" style="height:clac(100% - 30px)" :data="itemData['trackerItems']"
            :loading="loading" @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent"
            :row-config="{ isHover: true }" :checkbox-config="{
              checkRowKeys: selectedRowKey['trackerItems'],
              reserve: true, checkField: 'checked'
            }">

            <vxe-column type="checkbox" title="" width="30"></vxe-column>

            <vxe-column field="" title="编号" :width="100">
              <template #default="{ row }">
                {{ currentProjectKeyName + '-' + row.itemNo }}
              </template>
            </vxe-column>

            <vxe-column field="name" title="名称" width="" >
              <template #default="{ row }">
                <div class="task-description">
                    <a @click.stop class="prefix-container" @click="onEditTrackerItem(row)">
                        <div v-if="row?.icon" class="task-icon"><a-icon :component="row?.icon" /></div>
                        <span>{{ row.name }}</span>
                    </a>
                </div>
              </template>
            </vxe-column>

            <vxe-column v-if="reviewId" field="" title="评审状态">
              <template #default="{ row }">
                <div>
                  {{ void ({status,item} = getReviewStatus(row.id)) }} 
                  {{ void (user= members.find(v=>v.id == status?.reviewerId)) }}
                  <a-tag v-if="!item?.name||item?.name=='未评审'" style="border-radius: 10px;font-size: 13px;"
                      :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                      {{  item?.name||'未评审' }}
                  </a-tag>
                  <a-popover v-else :overlayStyle="{zIndex:9999}">
                    <template slot="content">
                      <div style="font-size: 14px;color: rgb(32, 45, 64);font-weight: bold;margin-bottom: 5px;">评审建议</div>
                      <div style="display: flex;">
                        <div>
                          <h-avatar :name="user?.name" :icon="user?.icon" :isShowName="false" size="default"></h-avatar>
                        </div>
                        <div style="margin-left: 8px;">
                          <div style="font-size: 13px;">
                            <a-icon theme="filled" :style="{ 'margin-right': '5px', color: item?.color }" type="check-circle" />{{item?.name}}
                            <span>发表于 {{ status?.createDate }}</span>
                          </div>
                          <a-tooltip>
                            <template slot="title">
                              {{status?.description}}
                            </template>
                            <div style="max-width: 260px; overflow: hidden;white-space: normal;text-overflow: ellipsis;">
                              {{ status?.description }}
                            </div>
                          </a-tooltip>
                        </div>
                      </div>
                    </template>
                    <a-tag style="border-radius: 10px;font-size: 13px;"
                        :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                        {{  item?.name||'未评审' }}
                    </a-tag>
                  </a-popover>
                </div>
              </template>
            </vxe-column>
          </vxe-table>

          <vxe-pager v-if="!reviewId" :loading="loading" :current-page="pagination.current" :page-size="pagination.pageSize"
            :total="pagination.total" :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total']"
            @page-change="handlePageChange">
          </vxe-pager>
        </div>
      </div>

      <div v-show="currentTab[0] == 'projectPlans'" style="height:100%;">
        <a-row style="margin-bottom: 15px">
          <a-col :span="12">
            <div class="alert">
              <div type="info" :show-icon="true">
                <div class="message" style="font-size: 12px">
                  已选择&nbsp;<a>{{ selectedRowKey['projectPlans']?.length }}</a>&nbsp;项，共{{ itemData['projectPlans'].length
                  }}个符合条件的结果：
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <a-col :offset="8" :span="4">

            </a-col>
            <a-col :span="12">
              <a-input-search placeholder="搜索工作项标题" v-model="keyword['projectPlans']" @search="handleSearch" />
            </a-col>
          </a-col>
        </a-row>
        <div class="table-box" :style="{ height: tableHeight }">
          <vxe-table ref="vxeTableProjectPlans" height="100%" row-id="id" style="overflow:auto"
            :data="itemData['projectPlans'].filter(v => (new RegExp(keyword['projectPlans'])).test(v.name))"
            :loading="loading" @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent"
            :row-config="{ isHover: true }" :checkbox-config="{
              checkRowKeys: selectedRowKey['projectPlans'],
              reserve: true, checkField: 'checked'
            }">

            <vxe-column type="checkbox" title="" width="35"></vxe-column>
            <vxe-column field="name" title="名称" min-width="100">
              <template #default="{ row }">
                <div style="display: inline-flex;" @click.stop @click="onOpenDetail('projectPlans',row)">
                  <a style="width:20px;" >
                    <h-icon type="plan-group" v-if="row.type === 'GROUP'" />
                    <h-icon type="plan-tasks" v-else-if="row.type === 'TASK'" />
                    <h-icon type="flag" v-else-if="row.type === 'MILE_STONE'" />
                  </a>
                  <a> {{ row.name }}</a>
                </div>
              </template>
            </vxe-column>

            <vxe-column v-if="reviewId" field="" title="评审状态">
              <template #default="{ row }">
                  {{ void ({status,item} = getReviewStatus(row.id)) }} 
                  {{ void (user= members.find(v=>v.id == status?.reviewerId)) }}
                  <a-tag v-if="!item?.name||item?.name=='未评审'" style="border-radius: 10px;font-size: 13px;"
                      :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                      {{  item?.name||'未评审' }}
                  </a-tag>
                  <a-popover v-else :overlayStyle="{zIndex:9999}">
                    <template slot="content">
                      <div style="font-size: 14px;color: rgb(32, 45, 64);font-weight: bold;margin-bottom: 5px;">评审建议</div>
                      <div style="display: flex;">
                        <div>
                          <h-avatar :name="user?.name" :icon="user?.icon" :isShowName="false" size="default"></h-avatar>
                        </div>
                        <div style="margin-left: 8px;">
                          <div style="font-size: 13px;">
                            <a-icon theme="filled" :style="{ 'margin-right': '5px', color: item?.color }" type="check-circle" />{{item?.name}}
                            <span>发表于 {{ status?.createDate }}</span>
                          </div>
                          <a-tooltip>
                            <template slot="title">
                              {{status?.description}}
                            </template>
                            <div style="max-width: 260px; overflow: hidden;white-space: normal;text-overflow: ellipsis;">
                              {{ status?.description }}
                            </div>
                          </a-tooltip>
                        </div>
                      </div>
                    </template>
                    <a-tag style="border-radius: 10px;font-size: 13px;"
                        :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                        {{  item?.name||'未评审' }}
                    </a-tag>
                  </a-popover>
              </template>
            </vxe-column>
          </vxe-table>
        </div>
      </div>

      <div v-show="currentTab[0] == 'targetVersions'" style="height:100%;">
        <a-row style="margin-bottom: 15px">
          <a-col :span="12">
            <div class="alert">
              <div type="info" :show-icon="true">
                <div class="message" style="font-size: 12px">
                  已选择&nbsp;<a>{{ selectedRowKey['targetVersions']?.length }}</a>&nbsp;项，共{{
                    itemData['targetVersions'].length }}个符合条件的结果：
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <a-col :offset="8" :span="4">

            </a-col>
            <a-col :span="12">
              <a-input-search placeholder="搜索工作项标题" v-model="keyword['targetVersions']" @search="handleSearch" />
            </a-col>
          </a-col>
        </a-row>
        <div class="table-box" :style="{ height: tableHeight }">
          <vxe-table ref="vxeTableTargetVersions" height="99%" row-id="id" style="overflow:auto"
            :data="itemData['targetVersions'].filter(v => (new RegExp(keyword['targetVersions'])).test(v.name))"
            :loading="loading" @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent"
            :row-config="{ isHover: true }" :checkbox-config="{
              checkRowKeys: selectedRowKey['targetVersions'],
              reserve: true, checkField: 'checked'
            }">

            <vxe-column type="checkbox" title="" width="35"></vxe-column>
            <vxe-column field="name" title="名称">
              <template #default="{ row }">
                <a @click.stop @click="onOpenDetail('targetVersions',row)">
                  {{ row.name }}
                </a>
              </template>
            </vxe-column>

            <vxe-column v-if="reviewId" field="" title="评审状态">
              <template #default="{ row }">
                  {{ void ({status,item} = getReviewStatus(row.id)) }} 
                  {{ void (user= members.find(v=>v.id == status?.reviewerId)) }}
                  <a-tag v-if="!item?.name||item?.name=='未评审'" style="border-radius: 10px;font-size: 13px;"
                      :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                      {{  item?.name||'未评审' }}
                  </a-tag>
                  <a-popover v-else :overlayStyle="{zIndex:9999}">
                    <template slot="content">
                      <div style="font-size: 14px;color: rgb(32, 45, 64);font-weight: bold;margin-bottom: 5px;">评审建议</div>
                      <div style="display: flex;">
                        <div>
                          <h-avatar :name="user?.name" :icon="user?.icon" :isShowName="false" size="default"></h-avatar>
                        </div>
                        <div style="margin-left: 8px;">
                          <div style="font-size: 13px;">
                            <a-icon theme="filled" :style="{ 'margin-right': '5px', color: item?.color }" type="check-circle" />{{item?.name}}
                            <span>发表于 {{ status?.createDate }}</span>
                          </div>
                          <a-tooltip>
                            <template slot="title">
                              {{status?.description}}
                            </template>
                            <div style="max-width: 260px; overflow: hidden;white-space: normal;text-overflow: ellipsis;">
                              {{ status?.description }}
                            </div>
                          </a-tooltip>
                        </div>
                      </div>
                    </template>
                    <a-tag style="border-radius: 10px;font-size: 13px;"
                        :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                        {{  item?.name||'未评审' }}
                    </a-tag>
                  </a-popover>
              </template>
            </vxe-column>
          </vxe-table>
        </div>
      </div>

      <div v-show="currentTab[0] == 'sprints'" style="height:100%;">
        <a-row style="margin-bottom: 15px">
          <a-col :span="12">
            <div class="alert">
              <div type="info" :show-icon="true">
                <div class="message" style="font-size: 12px">
                  已选择&nbsp;<a>{{ selectedRowKey['sprints']?.length }}</a>&nbsp;项，共{{ itemData['sprints'].length }}个符合条件的结果：
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <a-col :offset="8" :span="4">

            </a-col>
            <a-col :span="12">
              <a-input-search placeholder="搜索工作项标题" v-model="keyword['sprints']" @search="handleSearch" />
            </a-col>
          </a-col>
        </a-row>
        <div class="table-box" :style="{ height: tableHeight }">
          <vxe-table ref="vxeTableSprints" height="100%" row-id="id" style="overflow:auto"
            :data="itemData['sprints'].filter(v => (new RegExp(keyword['sprints'])).test(v.name))" :loading="loading"
            @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent" :row-config="{ isHover: true }"
            :checkbox-config="{
              checkRowKeys: selectedRowKey['sprints'],
              reserve: true, checkField: 'checked'
            }">

            <vxe-column type="checkbox" title="" width="35"></vxe-column>
            <vxe-column field="name" title="名称">
              <template #default="{ row }">
                <a @click.stop @click="onOpenDetail('sprints',row)">
                  {{ row.name }}
                </a>
              </template>
            </vxe-column>

           <vxe-column v-if="reviewId" field="" title="评审状态">
              <template #default="{ row }">
                  {{ void ({status,item} = getReviewStatus(row.id)) }} 
                  {{ void (user= members.find(v=>v.id == status?.reviewerId)) }}
                  <a-tag v-if="!item?.name||item?.name=='未评审'" style="border-radius: 10px;font-size: 13px;"
                      :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                      {{  item?.name||'未评审' }}
                  </a-tag>
                  <a-popover v-else :overlayStyle="{zIndex:9999}">
                    <template slot="content">
                      <div style="font-size: 14px;color: rgb(32, 45, 64);font-weight: bold;margin-bottom: 5px;">评审建议</div>
                      <div style="display: flex;">
                        <div>
                          <h-avatar :name="user?.name" :icon="user?.icon" :isShowName="false" size="default"></h-avatar>
                        </div>
                        <div style="margin-left: 8px;">
                          <div style="font-size: 13px;">
                            <a-icon theme="filled" :style="{ 'margin-right': '5px', color: item?.color }" type="check-circle" />{{item?.name}}
                            <span>发表于 {{ status?.createDate }}</span>
                          </div>
                          <a-tooltip>
                            <template slot="title">
                              {{status?.description}}
                            </template>
                            <div style="max-width: 260px; overflow: hidden;white-space: normal;text-overflow: ellipsis;">
                              {{ status?.description }}
                            </div>
                          </a-tooltip>
                        </div>
                      </div>
                    </template>
                    <a-tag style="border-radius: 10px;font-size: 13px;"
                        :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                        {{  item?.name||'未评审' }}
                    </a-tag>
                  </a-popover>
              </template>
            </vxe-column>
            
          </vxe-table>
        </div>
      </div>

      <div v-show="currentTab[0] == 'milestones'" style="height:100%;">
        <a-row style="margin-bottom: 15px">
          <a-col :span="12">
            <div class="alert">
              <div type="info" :show-icon="true">
                <div class="message" style="font-size: 12px">
                  已选择&nbsp;<a>{{ selectedRowKey['milestones']?.length }}</a>&nbsp;项，共{{ itemData['milestones'].length
                  }}个符合条件的结果：
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <a-col :offset="8" :span="4">

            </a-col>
            <a-col :span="12">
              <a-input-search placeholder="搜索工作项标题" v-model="keyword['milestones']" @search="handleSearch" />
            </a-col>
          </a-col>
        </a-row>
        <div class="table-box" :style="{ height: tableHeight }">
          <vxe-table ref="vxeTableMilestones" height="100%" row-id="id" style="overflow:auto"
            :data="itemData['milestones'].filter(v => (new RegExp(keyword['milestones'])).test(v.name))" :loading="loading"
            @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent" :row-config="{ isHover: true }"
            :checkbox-config="{
              checkRowKeys: selectedRowKey['milestones'],
              reserve: true, checkField: 'checked',
            }">

            <vxe-column type="checkbox" title="" width="35"></vxe-column>
            <vxe-column field="itemNo" title="ID" width="50px">
              <template #default="{ row }">
                {{ '#' + row.itemNo }}
              </template>
            </vxe-column>
            <vxe-column field="name" title="名称">
              <template #default="{ row }">
                <div style="display: inline-flex;" @click.stop @click="onOpenDetail('milestones',row)">
                  <a style="width:20px;">
                    <h-icon type="plan-group" v-if="row.type === 'GROUP'" />
                    <h-icon type="plan-tasks" v-else-if="row.type === 'TASK'" />
                    <h-icon type="plan-milestone" v-else-if="row.type === 'MILE_STONE'" />
                  </a>
                  <a>{{ row.name }}</a>
                </div>
              </template>
            </vxe-column>

            <vxe-column v-if="reviewId" field="" title="评审状态">
              <template #default="{ row }">
                  {{ void ({status,item} = getReviewStatus(row.id)) }} 
                  {{ void (user= members.find(v=>v.id == status?.reviewerId)) }}
                  <a-tag v-if="!item?.name||item?.name=='未评审'" style="border-radius: 10px;font-size: 13px;"
                      :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                      {{  item?.name||'未评审' }}
                  </a-tag>
                  <a-popover v-else :overlayStyle="{zIndex:9999}">
                    <template slot="content">
                      <div style="font-size: 14px;color: rgb(32, 45, 64);font-weight: bold;margin-bottom: 5px;">评审建议</div>
                      <div style="display: flex;">
                        <div>
                          <h-avatar :name="user?.name" :icon="user?.icon" :isShowName="false" size="default"></h-avatar>
                        </div>
                        <div style="margin-left: 8px;">
                          <div style="font-size: 13px;">
                            <a-icon theme="filled" :style="{ 'margin-right': '5px', color: item?.color }" type="check-circle" />{{item?.name}}
                            <span>发表于 {{ status?.createDate }}</span>
                          </div>
                          <a-tooltip>
                            <template slot="title">
                              {{status?.description}}
                            </template>
                            <div style="max-width: 260px; overflow: hidden;white-space: normal;text-overflow: ellipsis;">
                              {{ status?.description }}
                            </div>
                          </a-tooltip>
                        </div>
                      </div>
                    </template>
                    <a-tag style="border-radius: 10px;font-size: 13px;"
                        :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                        {{  item?.name||'未评审' }}
                    </a-tag>
                  </a-popover>
              </template>
            </vxe-column>
          </vxe-table>
        </div>
      </div>

      <div v-show="currentTab[0] == 'tasks'" style="height:100%;">
        <a-row style="margin-bottom: 15px">
          <a-col :span="12">
            <div class="alert">
              <div type="info" :show-icon="true">
                <div class="message" style="font-size: 12px">
                  已选择&nbsp;<a>{{ selectedRowKey['tasks']?.length }}</a>&nbsp;项，共{{ itemData['tasks'].length }}个符合条件的结果：
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <a-col :offset="8" :span="4">

            </a-col>
            <a-col :span="12">
              <a-input-search placeholder="搜索工作项标题" v-model="keyword['tasks']" @search="handleSearch" />
            </a-col>
          </a-col>
        </a-row>
        <div class="table-box" :style="{ height: tableHeight }">
          <vxe-table ref="vxeTableTasks" height="100%" row-id="id" style="overflow:auto"
            :data="itemData['tasks'].filter(v => (new RegExp(keyword['tasks'])).test(v.name))" :loading="loading"
            @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent" :row-config="{ isHover: true }"
            :checkbox-config="{
              checkRowKeys: selectedRowKey['tasks'],
              reserve: true, checkField: 'checked',
            }">

            <vxe-column type="checkbox" title="" width="35"></vxe-column>
            <vxe-column field="itemNo" title="ID" header-align="center" align="center" width="50px">
              <template #default="{ row }">
                #{{ row.itemNo }}
              </template>
            </vxe-column>

            <vxe-column field="name" title="名称">
              <template #default="{ row }">
                <a @click.stop @click="onOpenDetail('tasks',row)">
                  {{ row.name }}
                </a>
              </template>
            </vxe-column>
            <vxe-column v-if="reviewId" field="" title="评审状态">
              <template #default="{ row }">
                  {{ void ({status,item} = getReviewStatus(row.id)) }} 
                  {{ void (user= members.find(v=>v.id == status?.reviewerId)) }}
                  <a-tag v-if="!item?.name||item?.name=='未评审'" style="border-radius: 10px;font-size: 13px;"
                      :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                      {{  item?.name||'未评审' }}
                  </a-tag>
                  <a-popover v-else :overlayStyle="{zIndex:9999}">
                    <template slot="content">
                      <div style="font-size: 14px;color: rgb(32, 45, 64);font-weight: bold;margin-bottom: 5px;">评审建议</div>
                      <div style="display: flex;">
                        <div>
                          <h-avatar :name="user?.name" :icon="user?.icon" :isShowName="false" size="default"></h-avatar>
                        </div>
                        <div style="margin-left: 8px;">
                          <div style="font-size: 13px;">
                            <a-icon theme="filled" :style="{ 'margin-right': '5px', color: item?.color }" type="check-circle" />{{item?.name}}
                            <span>发表于 {{ status?.createDate }}</span>
                          </div>
                          <a-tooltip>
                            <template slot="title">
                              {{status?.description}}
                            </template>
                            <div style="max-width: 260px; overflow: hidden;white-space: normal;text-overflow: ellipsis;">
                              {{ status?.description }}
                            </div>
                          </a-tooltip>
                        </div>
                      </div>
                    </template>
                    <a-tag style="border-radius: 10px;font-size: 13px;"
                        :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                        {{  item?.name||'未评审' }}
                    </a-tag>
                  </a-popover>
              </template>
            </vxe-column>
          </vxe-table>
        </div>
      </div>

      <div v-show="currentTab[0] == 'deliverables'" style="height:100%;">
        <a-row style="margin-bottom: 15px">
          <a-col :span="12">
            <div class="alert">
              <div type="info" :show-icon="true">
                <div class="message" style="font-size: 12px">
                  已选择&nbsp;<a>{{ selectedRowKey['deliverables']?.length }}</a>&nbsp;项，共{{ itemData['deliverables'].length
                  }}个符合条件的结果：
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <a-col :offset="8" :span="4">

            </a-col>
            <a-col :span="12">
              <a-input-search placeholder="搜索工作项标题" v-model="keyword['deliverables']" @search="handleSearch" />
            </a-col>
          </a-col>
        </a-row>
        <div class="table-box" :style="{ height: tableHeight }">
          <vxe-table ref="vxeTableDeliverables" height="100%" row-id="id" style="overflow:auto"
            :data="itemData['deliverables'].filter(v => (new RegExp(keyword['deliverables'])).test(v.name))"
            :loading="loading" @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent"
            :row-config="{ isHover: true }" :checkbox-config="{
              checkRowKeys: selectedRowKey['deliverables'],
              reserve: true, checkField: 'checked',
            }">

            <vxe-column type="checkbox" title="" width="35"></vxe-column>
            <vxe-column type="seq" width="50px" />

            <vxe-column field="sourceName" title="名称" >
              <template #default="{ row }">
                <a @click.stop @click="onOpenDetail('deliverables',row)">
                  {{ row.sourceName }}
                </a>
              </template>
            </vxe-column>
            
            <vxe-column v-if="reviewId" field="" title="评审状态">
              <template #default="{ row }">
                  {{ void ({status,item} = getReviewStatus(row.id)) }} 
                  {{ void (user= members.find(v=>v.id == status?.reviewerId)) }}
                  <a-tag v-if="!item?.name||item?.name=='未评审'" style="border-radius: 10px;font-size: 13px;"
                      :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                      {{  item?.name||'未评审' }}
                  </a-tag>
                  <a-popover v-else :overlayStyle="{zIndex:9999}">
                    <template slot="content">
                      <div style="font-size: 14px;color: rgb(32, 45, 64);font-weight: bold;margin-bottom: 5px;">评审建议</div>
                      <div style="display: flex;">
                        <div>
                          <h-avatar :name="user?.name" :icon="user?.icon" :isShowName="false" size="default"></h-avatar>
                        </div>
                        <div style="margin-left: 8px;">
                          <div style="font-size: 13px;">
                            <a-icon theme="filled" :style="{ 'margin-right': '5px', color: item?.color }" type="check-circle" />{{item?.name}}
                            <span>发表于 {{ status?.createDate }}</span>
                          </div>
                          <a-tooltip>
                            <template slot="title">
                              {{status?.description}}
                            </template>
                            <div style="max-width: 260px; overflow: hidden;white-space: normal;text-overflow: ellipsis;">
                              {{ status?.description }}
                            </div>
                          </a-tooltip>
                        </div>
                      </div>
                    </template>
                    <a-tag style="border-radius: 10px;font-size: 13px;"
                        :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                        {{  item?.name||'未评审' }}
                    </a-tag>
                  </a-popover>
              </template>
            </vxe-column>
          </vxe-table>
        </div>
      </div>

      <div v-show="currentTab[0] == 'documents'" style="height:100%;">
        <a-row style="margin-bottom: 15px">
          <a-col :span="12">
            <div class="alert">
              <div type="info" :show-icon="true">
                <div class="message" style="font-size: 12px">
                  已选择&nbsp;<a>{{ selectedRowKey['documents']?.length }}</a>&nbsp;项，共{{ itemData['documents'].length
                  }}个符合条件的结果：
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <a-col :offset="8" :span="4">

            </a-col>
            <a-col :span="12">
              <a-input-search placeholder="搜索工作项标题" v-model="keyword['documents']" @search="handleSearch" />
            </a-col>
          </a-col>
        </a-row>
        <div class="table-box" :style="{ height: tableHeight }">
          <vxe-table ref="vxeTableDocuments" height="100%" row-id="id" style="overflow:auto"
            :data="itemData['documents'].filter(v => (new RegExp(keyword['documents'])).test(v.name))" :loading="loading"
            @checkbox-all="selectChangeEvent" @checkbox-change="selectChangeEvent" :row-config="{ isHover: true }"
            :checkbox-config="{
              checkRowKeys: selectedRowKey['documents'],
              reserve: true, checkField: 'checked',
            }">

            <vxe-column type="checkbox" title="" width="35"></vxe-column>
            <vxe-column field="name" title="名称">
              <template #default="{ row }">
                <a @click.stop @click="onOpenDetail('documents',row)">
                  {{ row.name }}
                </a>
              </template>
            </vxe-column>

            <vxe-column v-if="reviewId" field="" title="评审状态">
              <template #default="{ row }">
                  {{ void ({status,item} = getReviewStatus(row.id)) }} 
                  {{ void (user= members.find(v=>v.id == status?.reviewerId)) }}
                  <a-tag v-if="!item?.name||item?.name=='未评审'" style="border-radius: 10px;font-size: 13px;"
                      :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                      {{  item?.name||'未评审' }}
                  </a-tag>
                  <a-popover v-else :overlayStyle="{zIndex:9999}">
                    <template slot="content">
                      <div style="font-size: 14px;color: rgb(32, 45, 64);font-weight: bold;margin-bottom: 5px;">评审建议</div>
                      <div style="display: flex;">
                        <div>
                          <h-avatar :name="user?.name" :icon="user?.icon" :isShowName="false" size="default"></h-avatar>
                        </div>
                        <div style="margin-left: 8px;">
                          <div style="font-size: 13px;">
                            <a-icon theme="filled" :style="{ 'margin-right': '5px', color: item?.color }" type="check-circle" />{{item?.name}}
                            <span>发表于 {{ status?.createDate }}</span>
                          </div>
                          <a-tooltip>
                            <template slot="title">
                              {{status?.description}}
                            </template>
                            <div style="max-width: 260px; overflow: hidden;white-space: normal;text-overflow: ellipsis;">
                              {{ status?.description }}
                            </div>
                          </a-tooltip>
                        </div>
                      </div>
                    </template>
                    <a-tag style="border-radius: 10px;font-size: 13px;"
                        :style="{ color: item?.color||'#87888a', borderColor: item?.color||'#87888a' }">
                        {{  item?.name||'未评审' }}
                    </a-tag>
                  </a-popover>
              </template>
            </vxe-column>
          </vxe-table>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import HAvatar from '@/components/avatar/h-avatar.vue';
import { iconUrl } from "@/utils/util"
import {cloneDeep} from 'lodash'
import { mapGetters } from "vuex";
import {systemFields} from '@/utils/systemFields'
import {
  findTrackerItems,findTrackerItemByIds
} from "@/services/tracker/TrackerItemService";
import {
  findTrackers, findOneTracker,
} from "@/services/tracker/TrackerService";
import {
  findProjectRolesAndMembers
} from "@/services/tracker/ProjectRoleMemberService";
import {
  findEnumsByCode
} from "@/services/system/EnumService";
import TrackerItemFilter from "../tool/TrackerItemFilter.vue";
import Vue from "vue";
import { buildGantt } from '@/services/plan/PlanService'
import { findVersions } from '@/services/plan/TargetVersionService'
import { findSprints } from '@/services/plan/SprintService'
import { findWaitExecutePlans } from '@/services/plan/PlanService'
import { findMilestones } from '@/services/plan/PlanService'
import { findByProjectId } from '@/services/tracker/ProjectPageService'


export default {
  name: 'TableItemSelectView',
  props: {
    initalData: Object,
    projectId: String,
    reviewId:String,
    reviewStatuses: Array,
    isRead: Boolean,
    reviewers: Array,
    editReviewId: String,
  },
  components: {
    TrackerItemFilter, HAvatar
  },
  data() {
    return {
      // 查询条件参数
      queryParam: {
        keyword: '',
      },
      keyword: {
        trackerItems: '',
        projectPlans: '',
        targetVersions: '',
        sprints: '',
        milestones: '',
        tasks: '',
        deliverables: '',
        documents: '',
        selected:'',
      },
      itemData: {
        trackerItems: [],
        projectPlans: [],
        targetVersions: [],
        sprints: [],
        milestones: [],
        tasks: [],
        deliverables: [],
        documents: [],
      },
      members: [],
      selectedRowKey: {
        trackerItems: [],
        projectPlans: [],
        targetVersions: [],
        sprints: [],
        milestones: [],
        tasks: [],
        deliverables: [],
        documents: [],
        selected:[],
      },
      selectedRow: {
        trackerItems: [],
        projectPlans: [],
        targetVersions: [],
        sprints: [],
        milestones: [],
        tasks: [],
        deliverables: [],
        documents: [],
      },
      tableRefMap:{
        trackerItems: 'vxeTableTrackerItems',
        projectPlans: 'vxeTableProjectPlans',
        targetVersions: 'vxeTableTargetVersions',
        sprints: 'vxeTableSprints',
        milestones: 'vxeTableTasks',
        tasks: 'vxeTableDeliverables',
        deliverables: 'vxeTableMilestones',
        documents: 'vxeTableDocuments'
      },
      loading: false,
      tracker: {},
      activeKey: '',
      tableHeight: 'calc(100% - 50px)',
      conditionGroups: [
        {
          conditions: []
        }
      ],
      filterIcon: 'expandDown',
      pagination: {
        current: 1,
        pageSize: 10,
      },
      currentTab: ['trackerItems'],
      statusList:[],
      editItemTable: [],
    }
  },
  watch:{
    reviewId: {
      handler(newVal) {
          if(newVal){
            this.$nextTick(() => {
              console.log('this.reviewStatuses', this.reviewStatuses);
              this.$emit('trackerItemSelector', this.selectedRowKey);
            })
          }
        },
        immediate: true,
    },
  },
  computed:{
    ...mapGetters("project", ["currentProjectKeyName"]),
    getSelectedCount(){
      let count=0;
      for (let key in this.selectedRowKey) {
        if(key !='selected'){
          count+=this.selectedRowKey[key].length
        }
      }
      return count;
    },
    getSelectedData(){
      let data=[];
      for (let key in this.selectedRow) {
        if(key !='selected'){
          if(key=='trackerItems'&&this.editReviewId&&this.editItemTable.length>0){
            data.push(...this.editItemTable)
            this.editItemTable=[]
          }else{
            data.push(...this.selectedRow[key])
          }
        }
      }
      return cloneDeep(data);
    },
    getReviewStatus(){
      return function(rowId) {
        let status=this.reviewStatuses.find(v=>v.objectId==rowId)
        if(status?.statusId){
          return {status:status,item:this.statusList.find(v=>v.id==status?.statusId)}
        }else{
          return {status: null,item: null}
        }
      }
    },
  },
  mounted() {
    
    // this.loadData();


  },
  methods: {
    getSelectedItemCount(item){
      if(this.reviewId){
        return this.initalData[item].length;
      }else{
        return this.selectedRowKey[item].length;
      }
    },  
    onClickReview(row){

    },
    onEditTrackerItem(row){
      if(row.tracker?.id){
        const routeData= this.$router.resolve({
            path:`/tracker/project/${this.projectId}/trackerItems/${row.tracker.id}/${row.id}`
        });
        window.open(routeData.href, '_blank');
      }
    },
    onOpenDetail(key,row){
      let routeData;
      if(key=='projectPlans'){
        routeData= this.$router.resolve({
            path: `/tracker/project/${this.projectId}/plan`
        });
      }else if(key =='targetVersions'){
        routeData= this.$router.resolve({
            path: `/tracker/project/${this.projectId}/targetVersion`
        });
      }else if(key =='sprints'){
        routeData= this.$router.resolve({
            path: `/tracker/project/${this.projectId}/sprints`
        });
      }else if(key =='milestones'){
        routeData= this.$router.resolve({
            path: `/tracker/project/${this.projectId}/milestones`
        });
      }else if(key =='tasks'){
        routeData= this.$router.resolve({
            path: `/tracker/project/${this.projectId}/tasks`
        });
      }else if(key =='deliverables'){
        routeData= this.$router.resolve({
            path: `/tracker/project/${this.projectId}/deliverables`
        });
      }else if(key =='documents'){
        routeData= this.$router.resolve({
            path: `/tracker/project/${this.projectId}/wiki/${row.id}`
        });
      }
      window.open(routeData.href, '_blank');
    },
    selectedChange({checked,reserves, records,rowid}) {
      this.selectedRowKey['selected'] = [...reserves.map(v => v.id), ...records.map(v => v.id)];
      for (let key in this.selectedRowKey) {
        if(key !='selected'){
          let row=this.itemData[key].find(v=>v.id==rowid)
          if(row){
            this.$refs[this.tableRefMap[key]].toggleCheckboxRow(row)
            if(checked){
              this.selectedRowKey[key].push(rowid)
            }else{
              this.selectedRowKey[key]=this.selectedRowKey[key].filter(v=>v!=rowid)  
            }                    
          }
        }
      }
      this.$emit('trackerItemSelector', this.selectedRowKey);
    },
    selectedChangeAll({checked, reserves, records}){
      this.selectedRowKey['selected'] = [...reserves.map(v => v.id), ...records.map(v => v.id)];
      if(checked){
        records.forEach(item=>{
          for (let key in this.selectedRowKey) {
            if(key !='selected'){
              let row=this.itemData[key].find(v=>v.id==item.id)
              if(row){
                this.$refs[this.tableRefMap[key]].setCheckboxRow(row,true)
                if(!this.selectedRowKey[key].includes(item.id)){
                  this.selectedRowKey[key].push(item.id)
                }
              }
            }
          }
        })
      }else{
          for (let key in this.selectedRowKey) {
            if(key !='selected'){
                this.$refs[this.tableRefMap[key]].clearCheckboxRow()
                this.selectedRowKey[key]=[]
            }
          }
      }
      this.$emit('trackerItemSelector', this.selectedRowKey);
      
    },
    //documents
    loadDocumentsData() {
      findByProjectId(this.projectId).then(resp => {
        this.itemData.documents = resp.filter(item => item.type === 'wiki')
      }).finally(() => {
        //初始化选中
        this.$nextTick(() => {
          this.selectedRow['documents']=[]
          for (let item2 of this.itemData.documents) {
            for (let item of this.selectedRowKey?.documents) {
              if (item == item2.id){
                this.$refs["vxeTableDocuments"].setCheckboxRow(item2, true)
                this.selectedRow['documents'].push(item2)
              }
            }
          }
          if(this.isRead){
            this.itemData.documents=this.itemData.documents.filter(
                v=>this.selectedRowKey['documents'].includes(v.id))
          }
        })
      })
    },
    //deliverables
    loadDeliverablesData() {
      findMilestones(this.projectId).then(resp => {
        this.itemData.milestones = resp
        this.itemData.deliverables=Vue.observable([])
        resp.forEach(element => {
          const objs = element.deliverables || []
          objs.forEach(obj => {
            const item = obj
            item.sourceName = element.name
            item.sourceType = 'MILE_STONE'
            this.itemData.deliverables.push(item)
          })
        });
    
      }).finally(() => {
        //初始化选中
        this.$nextTick(() => {
          this.selectedRow['milestones']=[]
          this.selectedRow['deliverables']=[]
          for (let item2 of this.itemData.milestones) {
            for (let item of this.selectedRowKey?.milestones) {
              if (item == item2.id){
                this.$refs["vxeTableMilestones"].setCheckboxRow(item2, true)
                this.selectedRow['milestones'].push(item2)
              }
            }
          }
          for (let item2 of this.itemData.deliverables) {
            for (let item of this.selectedRowKey?.deliverables) {
              if (item == item2.id){
                this.$refs["vxeTableDeliverables"].setCheckboxRow(item2, true)
                this.selectedRow['deliverables'].push(item2)
              }
            }
          }
          if(this.isRead){
            this.itemData.milestones=this.itemData.milestones.filter(
                v=>this.selectedRowKey['milestones'].includes(v.id))
            this.itemData.deliverables=this.itemData.deliverables.filter(
                v=>this.selectedRowKey['deliverables'].includes(v.id))
          }
        })
      })
    },
    //tasks
    loadTasksData() {
      findWaitExecutePlans(this.projectId).then(resp => {
        this.itemData.tasks = resp || []
      }).finally(() => {
        //初始化选中
        this.$nextTick(() => {
          this.selectedRow['tasks']=[]
          for (let item2 of this.itemData.tasks) {
            for (let item of this.selectedRowKey?.tasks) {
              if (item == item2.id){
                this.$refs["vxeTableTasks"].setCheckboxRow(item2, true)
                this.selectedRow['tasks'].push(item2)
              }
            }
          }
          if(this.isRead){
            this.itemData.tasks=this.itemData.tasks.filter(
                v=>this.selectedRowKey['tasks'].includes(v.id))
          }
        })
      })
    },
    //milestones
    loadMilestonesData() {
      // this.loadDeliverablesData();
    },
    //sprints
    loadSprintsData() {
      findSprints(this.projectId).then(resp => {
        this.itemData.sprints = resp || []
      }).finally(() => {
        //初始化选中
        this.$nextTick(() => {
          this.selectedRow['sprints']=[]
          for (let item2 of this.itemData.sprints) {
            for (let item of this.selectedRowKey.sprints) {
              if (item == item2.id){
                this.$refs["vxeTableSprints"].setCheckboxRow(item2, true)
                this.selectedRow['sprints'].push(item2)
              }
            }
          }
          if(this.isRead){
            this.itemData.sprints=this.itemData.sprints.filter(
                v=>this.selectedRowKey['sprints'].includes(v.id))
          }
        })
      })
    },
    //targetVersions
    loadTargetVersionsData() {
      findVersions(this.projectId).then(resp => {
        this.itemData.targetVersions = resp || []
      }).finally(() => {
        //初始化选中
        this.$nextTick(() => {
          this.selectedRow['targetVersions']=[]
          for (let item2 of this.itemData.targetVersions) {
            for (let item of this.selectedRowKey?.targetVersions) {
              if (item == item2.id){
                this.$refs["vxeTableTargetVersions"].setCheckboxRow(item2, true)
                this.selectedRow['targetVersions'].push(item2)
              }
            }
          }
          if(this.isRead){
            this.itemData.targetVersions=this.itemData.targetVersions.filter(
                v=>this.selectedRowKey['targetVersions'].includes(v.id))
          }
        })
      })
    },
    //projectPlans
    loadProjectPlanData() {
      buildGantt(this.projectId).then(resp => {
        this.itemData.projectPlans = resp.tasks || []
        this.itemData.projectPlans.forEach(f => { f.owner = f.owner || { id: '' } })
      }).finally(() => {
        //初始化选中
        this.$nextTick(() => {
          this.selectedRow['projectPlans']=[]
          for (let item2 of this.itemData.projectPlans) {
            for (let item of this.selectedRowKey?.projectPlans) {
              if (item == item2.id){
                this.$refs["vxeTableProjectPlans"].setCheckboxRow(item2, true)
                this.selectedRow['projectPlans'].push(item2)
              }
            }
          }
          if(this.isRead){
            this.itemData.projectPlans=this.itemData.projectPlans.filter(
                v=>this.selectedRowKey['projectPlans'].includes(v.id))
          }
        })
      })
    },

    //trackerItems
    onChangeTab(e) {
      // this.currentTab[0]=e.key
    },
    handlePageChange(pagination) {
      this.pagination.current = pagination.currentPage
      this.pagination.pageSize = pagination.pageSize
      this.refresh();
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
      const that = this;
      if (this.activeKey == 'true') {
        this.activeKey = 'false'
        this.tableHeight = "calc(100% - 50px)"
        this.filterIcon = "expandDown"
      } else {
        this.activeKey = 'true'
        this.tableHeight = "350px"
        this.filterIcon = "collapseUp"
      }
    },
    selectChangeEvent({ checked, records, reserves }) {
      this.selectedRowKey[this.currentTab[0]] = [...reserves.map(v => v.id), ...records.map(v => v.id)];
      this.selectedRow[this.currentTab[0]] = [...reserves, ...records];

      this.$emit('trackerItemSelector', this.selectedRowKey);
    },

    view() {
      this.activeKey = "false";
      this.selectData();
      this.loadData();
    },
    selectData() {
      this.selectedRow = {
          trackerItems: [],
          projectPlans: [],
          targetVersions: [],
          sprints: [],
          milestones: [],
          tasks: [],
          deliverables: [],
          documents: [],
          selected: [],
      };
      for (let key in this.initalData) {
        this.selectedRowKey[key] = this.initalData[key].filter(v=>v)
      }
    },
    handleSearch() {
      if (this.currentTab[0] == "trackerItems") {
        this.loadTrackerItemsData({ conditionGroups: this.conditionGroups });
      }
    },

    loadData() {
      this.initTrackerFields();
      this.loadTrackerData();

      this.loadAllTableData()
      
      findProjectRolesAndMembers(this.projectId, "").then(resp => {
        this.members = resp.members;
      });
    },
    loadAllTableData(){
      if (this.itemData['trackerItems'].length == 0) {
        // if(this.editReviewId){
        //     findTrackerItemByIds(this.selectedRowKey['trackerItems']).then(res=>{
        //       this.editItemTable = res;
        //     }).finally(() => {
        //       this.loading = false;
        //       //初始化选中
        //       this.$nextTick(() => {
        //         // this.selectedRow['selected']=[]
        //         for (let item2 of this.editItemTable) {
        //           for (let item of this.selectedRowKey.trackerItems) {
        //             if (item == item2.id){
        //               this.$refs["vxeTableSelected"].setCheckboxRow(item2, true)
        //               this.selectedRow['selected'].push(item2)
        //             }
        //           }
        //         }
        //       })
        //     })
        //   }
        this.loadTrackerItemsData({ conditionGroups: this.conditionGroups });
      }
      if (this.itemData['projectPlans'].length == 0) {
        this.loadProjectPlanData();
      }
      if (this.itemData['targetVersions'].length == 0) {
        this.loadTargetVersionsData();
      }
      if (this.itemData['sprints'].length == 0) {
        this.loadSprintsData();
      }
      // if (this.itemData['milestones'].length == 0) {
      //   this.loadMilestonesData();
      // }
      if (this.itemData['tasks'].length == 0) {
        this.loadTasksData();
      }
      if (this.itemData['deliverables'].length == 0) {
        this.loadDeliverablesData();
      }
      if ( this.itemData['documents'].length == 0) {
        this.loadDocumentsData();
      }
    },
    onClear() {
      this.selectedRowKey = {
        trackerItems: [],
        projectPlans: [],
        targetVersions: [],
        sprints: [],
        milestones: [],
        tasks: [],
        deliverables: [],
        documents: [],
        selected: [],
      },
      this.itemData = {
          trackerItems: [],
          projectPlans: [],
          targetVersions: [],
          sprints: [],
          milestones: [],
          tasks: [],
          deliverables: [],
          documents: [],
        },
        this.selectedRow = {
          trackerItems: [],
          projectPlans: [],
          targetVersions: [],
          sprints: [],
          milestones: [],
          tasks: [],
          deliverables: [],
          documents: [],
          selected: [],
        },
      this.queryParam.keyword = ""
      this.$refs.vxeTableTrackerItems.clearCheckboxRow()
      this.$refs.vxeTableProjectPlans.clearCheckboxRow()
      this.$refs.vxeTableTargetVersions.clearCheckboxRow()
      this.$refs.vxeTableSprints.clearCheckboxRow()
      this.$refs.vxeTableTasks.clearCheckboxRow()
      this.$refs.vxeTableDeliverables.clearCheckboxRow()
      this.$refs.vxeTableMilestones.clearCheckboxRow()
      this.$refs.vxeTableDocuments.clearCheckboxRow()
      this.$refs.vxeTableSelected.clearCheckboxRow()
      this.currentTab = ['trackerItems']
      this.pagination= {
        current: 1, 
        pageSize: 10,
      };
    },
    refresh() {
      this.loadTrackerItemsData({ conditionGroups: this.conditionGroups });
    },

    loadTrackerItemsData(filter) {
      this.loading = true;
      // let filter = null
      let keyword = this.queryParam.keyword
      let pageable = {
        page: this.pagination.current - 1,
        size: this.pagination.pageSize,
      }
      // console.log("loadTrackerItemsData")
      if(this.reviewId){
        if(this.selectedRowKey['trackerItems'].length==0){
          this.loading = false;
          return;
        }
        findTrackerItemByIds(this.selectedRowKey['trackerItems']).then(res=>{
          console.log("res",res)
          this.itemData.trackerItems = res;
        }).finally(() => {
          this.loading = false;
          //初始化选中
          this.$nextTick(() => {
            // this.selectedRow['trackerItems']=[]
            for (let item2 of this.itemData.trackerItems) {
              this.$refs["vxeTableTrackerItems"].setCheckboxRow(item2, false)
              for (let item of this.selectedRowKey.trackerItems) {
                if (item == item2.id){
                  this.$refs["vxeTableTrackerItems"].setCheckboxRow(item2, true)
                  this.selectedRow['trackerItems'].push(item2)
                }
              }
            }
            if(this.isRead){
              this.itemData.trackerItems=this.itemData.trackerItems.filter(
                  v=>this.initalData['trackerItems'].includes(v.id))
            }
          })
        })
      }
      else 
      findTrackerItems(this.projectId, null, null, filter, keyword, pageable, []).then(resp => {
        if (resp) {
          this.itemData.trackerItems = resp.content;
          this.pagination.total = parseInt(resp.totalElements);
        }
      }).finally(() => {
        this.loading = false;
        //初始化选中
        this.$nextTick(() => {
          // this.selectedRow['trackerItems']=[]
          for (let item2 of this.itemData.trackerItems) {
            this.$refs["vxeTableTrackerItems"].setCheckboxRow(item2, false)
            for (let item of this.selectedRowKey.trackerItems) {
              if (item == item2.id){
                this.$refs["vxeTableTrackerItems"].setCheckboxRow(item2, true)
                this.selectedRow['trackerItems'].push(item2)
              }
            }
          }
          if(this.isRead){
            this.itemData.trackerItems=this.itemData.trackerItems.filter(
                v=>this.initalData['trackerItems'].includes(v.id))
          }
        })
      })
    },
    loadTrackerData() {
      findEnumsByCode('PROJECT_REVIEW_TYPE').then((resp) => {
        this.statusList=resp
      });
      findTrackers({ projectId: this.projectId }).then(resp => {
        this.tracker.trackerId = Vue.observable([]);
        resp.forEach(item => {
          this.tracker.trackerId.push({ id: item.id, name: item.name, icon: item.icon })
        });
      })

      findEnumsByCode('TRACKER_PRIORITY').then((resp) => {
        this.tracker.priority = Vue.observable([]);
        resp.forEach(item => {
          this.tracker.priority.push({ id: item.id, name: item.name })
        });
      });

      findEnumsByCode('TRACKER_STATUS_MEANING').then((resp) => {
        this.tracker.meaning = Vue.observable([]);
        resp.forEach(item => {
          this.tracker.meaning.push({ id: item.id, name: item.name })
        });
      })
    },
    initTrackerFields() {
      this.tracker.trackerFields = systemFields?.filter(v=>v.systemProperty!='status')
    },
    iconUrl(icon){
        return iconUrl(icon);
    },
  },

}
</script>

<style scoped lang="less">
/deep/.ant-modal-body {
  height: 700px;
  padding-top: 5px;
}

/deep/.ant-collapse-content>.ant-collapse-content-box {
  padding: 0;
}

.standard-table {
  .alert {
    margin-bottom: 16px;

    .message {
      font-size: 12px;

      a {
        font-weight: 600;
      }
    }

    .clear {
      float: right;
    }
  }
}

.transition-status {
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

.table-box {
  box-sizing: border-box;
  direction: ltr;
  position: relative;
  will-change: transform;
  overflow-y: auto;
  transition: height .25s;
}

.order-row {
  width: 440px;
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

.add-condition-group {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.add-condition-group:hover {
  color: #338fe5;
  background-color: #f3f3f3;
}

.group-line {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  padding-bottom: 5px;

  .group-or {
    padding: 0 10px;
    background-color: rgb(250, 250, 250);
    position: relative;
    z-index: 1;
  }
}

.group-line::after {
  content: "";
  display: block;
  height: 1px;
  width: 100%;
  background-color: #e8e8e8;
  position: absolute;
}

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

.input-separate {
  color: #303030;
  position: relative;
  display: flex;
  align-items: center;
}

.input-separate::before {
  content: "";
  display: block;
  width: 13px;
  height: 39px;
  border: 1px solid #c8c8c8;
  border-left: transparent;
  position: absolute;
  left: -5px;
}

.input-separate .input-separate-label {
  width: 100%;
  background-color: #fff;
  position: relative;
  z-index: 1;
}

.a-tag-box {
  cursor: pointer;
  border: none;
  background-color: rgba(144, 144, 144, .15);
}

.filter-flex {
  height: 0;
}

.filter-flex-active {
  height: 220px;
}

.task-description {
    display: block;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    .prefix-container {
        display: inline-flex;
        flex-direction: row;
        align-items: center;
        font-size: 14px;

        .task-icon {
            margin-right: 5px;
        }
    }
}
</style>
