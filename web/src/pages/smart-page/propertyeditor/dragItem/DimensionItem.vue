<template>
    <div style="position: relative;display: inline-block;width:100%;">

        <a-icon type="delete" style="position: absolute;top: 6px;right: 24px;color: #878d9f;cursor: pointer;z-index: 1;"
            @click="removeItem" />
        <a-dropdown :trigger="['click']" size="mini" :overlayStyle="{ 'min-width': '200px', left: '20px' }">

            <span class="ant-dropdown-link">
                <a-tag size="small" class="item-axis" color="blue">
                    <span style="float: left">
                        <a-icon component="field_type_text" style="color: #222222;" v-show="item.type === 'TEXT'" />
                        <a-icon component="field_type_number" style="color: cadetblue;" v-show="item.type === 'NUM'" />
                        <a-icon component="date" style="color:cadetblue;" v-show="item.type === 'DATE'" />

                        <a-icon type="sort-ascending" v-if="item.sort === 'asc'" />
                        <a-icon type="sort-descending" v-if="item.sort === 'desc'" />

                    </span>
                    <span class="item-span-style" :title="item.title">{{ item.title }}</span>
                    <span v-if="false && item.type === 'DATE'" class="summary-span">
                        {{ $t('chart.' + item.dateStyle) }}
                    </span>
                </a-tag>
                <a class="ant-dropdown-link" @click="e => e.preventDefault()"
                    style="position: absolute;top: 4px;right: 6px;">
                    <a-icon type="down" />
                </a>
            </span>
            <a-menu slot="overlay" @click="menuClick">
                <a-sub-menu>
                    <span slot="title" class="mini-text">
                        <a-icon component="sorting" />
                        {{ $t('chart.sort') }}
                        <span class="summary-span-item">({{ $t('chart.' + item.sort) }})</span>
                    </span>

                    <a-menu-item key="sort_none" class="mini-text">{{ $t('chart.none')
                        }}</a-menu-item>
                    <a-menu-item key="sort_asc" class="mini-text">{{ $t('chart.asc')
                        }}</a-menu-item>
                    <a-menu-item key="sort_desc" class="mini-text">{{ $t('chart.desc')
                        }}</a-menu-item>
                    <!-- <a-menu-item v-show="!item.chartId && (item.deType === 0 || item.deType === 5)"
                        :command="beforeSort('custom_sort')" class="mini-text">{{ $t('chart.custom_sort')
                        }}...</a-menu-item> -->
                </a-sub-menu>
                <a-sub-menu v-show="item.type === 'DATE'" class="mini-text" divided>
                    <span slot="title" class="mini-text">
                        <a-icon component="date" />
                        {{ $t('chart.dateStyle') }}
                        <span class="summary-span-item">({{ $t('chart.'+item.dateStyle) }})</span>
                    </span>

                    <a-menu-item key="date_y" class="mini-text">{{ $t('chart.y') }}</a-menu-item>
                    <a-menu-item key="date_y_Q" class="mini-text" v-if="showDateExt">{{ $t('chart.y_Q')
                        }}</a-menu-item>
                    <a-menu-item key="date_y_M" class="mini-text">{{ $t('chart.y_M') }}</a-menu-item>
                    <a-menu-item key="date_y_W" class="mini-text" v-if="showDateExt">{{ $t('chart.y_W')
                        }}</a-menu-item>
                    <a-menu-item key="date_y_M_d" class="mini-text">{{ $t('chart.y_M_d')
                        }}</a-menu-item>
                    <a-menu-item key="date_H_m_s" class="mini-text" divided>{{ $t('chart.H_m_s')
                        }}</a-menu-item>
                    <a-menu-item key="date_y_M_d_H_m" class="mini-text">{{ $t('chart.y_M_d_H_m')
                        }}</a-menu-item>
                    <a-menu-item key="date_y_M_d_H_m_s" class="mini-text">{{ $t('chart.y_M_d_H_m_s')
                        }}</a-menu-item>
                </a-sub-menu>
                <a-sub-menu v-show="item.type === 'DATE'" class="mini-text">
                    <span slot="title" class="mini-text">
                        <a-icon component="date" />
                        {{ $t('chart.datePattern') }}
                        <span class="summary-span-item">({{ $t('chart.date_'+item.datePattern) }})</span>
                    </span>


                    <a-menu-item key="date_pattern_sub" class="mini-text">{{ $t('chart.date_sub')
                        }}(1990-01-01)</a-menu-item>
                    <a-menu-item key="date_pattern_split" class="mini-text">{{
                        $t('chart.date_split')
                        }}(1990/01/01)</a-menu-item>
                </a-sub-menu>
                <a-sub-menu v-show="item.type === 'DATE'" class="mini-text">
                    <span slot="title" class="mini-text">
                        <a-icon component="date" />
                        {{ $t('chart.dateFill') }}
                        <span class="summary-span-item">({{ $t('chart.date_fill_' + item.dateFill) }})</span>
                    </span>

                    <a-menu-item key="date_fill_auto" class="mini-text">{{ $t('chart.date_fill_auto')
                        }}</a-menu-item>
                    <a-menu-item key="date_fill_none" class="mini-text">{{
                        $t('chart.date_fill_none')
                        }}</a-menu-item>
                </a-sub-menu>


                <a-menu-item v-if="chart.render === 'antv' && chart.type.includes('table') && item.groupType === 'q'"
                    divided key="formatter">
                    <a-icon type="ordered-list" />
                    <span>{{ $t('chart.value_formatter') }}...</span>
                </a-menu-item>
                <a-menu-item icon="a-icon-edit-outline" class="mini-text" divided key="rename">
                    <a-icon type="edit" />
                    <span>{{ $t('chart.show_name_set') }}</span>
                </a-menu-item>
                <a-menu-item icon="a-icon-delete" class="mini-text" divided key="remove">
                    <a-icon type="delete" />
                    <span>{{ $t('chart.delete') }}</span>
                </a-menu-item>
            </a-menu>
        </a-dropdown>
    </div>
</template>

<script>
import { formatterItem } from '@/pages/smart-page/util/formatter'
import { deepCopy } from '@/pages/smart-page/util/canvasUtils'
export default {
    name: 'DimensionItem',
    props: {
        item: {
            type: Object,
            required: true
        },
        index: {
            type: Number,
            required: true
        },
        chart: {
            type: Object,
            required: true
        },
        dimensionData: {
            type: Array,
            required: true
        },
        quotaData: {
            type: Array,
            required: true
        }
    },
    data() {
        return {
            tagType: 'success',
            formatterItem: formatterItem,
            showDateExt: false
        }
    },
    watch: {
        dimensionData: function () {
            this.getItemTagType()
        },
        item: {
            handler: function (newVal, oldVal) {
                this.getItemTagType()
            },
            immediate: true,
        },
        chart: function () {
            this.getDateExtStatus()
        }
    },
    mounted() {
    },
    methods: {
        init() {
            if (!this.item.formatterCfg) {
                this.item.formatterCfg = JSON.parse(JSON.stringify(this.formatterItem))
            }
        },
        menuClick(e) {
            const command = e.key
            if (command === 'sort_none') {
                this.sort('none')
            } else if (command === 'sort_asc') {
                this.sort('asc')
            } else if (command === 'sort_desc') {
                this.sort('desc')
            } else if (command === 'date_y') {
                this.dateStyle('y')
            } else if (command === 'date_y_Q') {
                this.dateStyle('y_Q')
            } else if (command === 'date_y_M') {
                this.dateStyle('y_M')
            } else if (command === 'date_y_W') {
                this.dateStyle('y_W')
            } else if (command === 'date_y_M_d') {
                this.dateStyle('y_M_d')
            } else if (command === 'date_H_m_s') {
                this.dateStyle('M_m_s')
            } else if (command === 'date_y_M_d_H_m') {
                this.dateStyle('y_M_d_H_m')
            } else if (command === 'date_y_M_d_H_m_s') {
                this.dateStyle('y_M_d_H_m_s')
            } else if (command === 'date_pattern_sub') {
                this.datePattern('sub')
            } else if (command === 'date_pattern_split') {
                this.datePattern('split')
            } else if (command === 'date_fill_auto') {
                this.dateFill('auto')
            } else if (command === 'date_fill_none') {
                this.dateFill('none')
            } else if (command === 'rename') {
                this.showRename()
            } else if (command === 'remove') {
                this.removeItem()
            } else if (command === 'filter') {
                this.editFilter()
            } else if (command === 'formatter') {
                this.valueFormatter()
            }
        },
        sort(type) {
            if (type === 'custom_sort') {
                const item = {
                    index: this.index,
                    sort: type
                }
                this.$emit('onCustomSort', item)
            } else {
                this.item.index = this.index
                this.item.sort = type
                this.item.customSort = []
                this.$emit('onDimensionItemChange', deepCopy(this.item))
            }
        },

        dateStyle(type) {
            this.item.dateStyle = type
            this.$emit('onDimensionItemChange', deepCopy(this.item))
        },

        datePattern(type) {
            this.item.datePattern = type
            this.$emit('onDimensionItemChange', deepCopy(this.item))
        },
        dateFill(type) {
            this.item.dateFill = type
            this.$emit('onDimensionItemChange', deepCopy(this.item))
        },

        editFilter() {
            this.item.index = this.index
            this.$emit('editItemFilter', deepCopy(this.item))
        },
        showRename() {
            this.item.index = this.index
            this.item.renameType = 'dimension'
            this.item.originName = this.item.name
            this.$emit('onNameEdit', deepCopy(this.item))
        },
        removeItem() {
            this.item.index = this.index
            this.item.removeType = 'dimension'
            this.$emit('onDimensionItemRemove', deepCopy(this.item))
        },
        getItemTagType() {
            // this.tagType = getItemType(this.dimensionData, this.quotaData, this.item)
        },

        valueFormatter() {
            this.item.index = this.index
            this.item.formatterType = 'dimension'
            this.$emit('valueFormatter', deepCopy(this.item))
        },

        getDateExtStatus() {
            this.showDateExt = false
        }
    }

}
</script>

<style lang="less" scoped>
.item-axis {
    padding: 1px 6px;
    margin: 0 3px 2px 3px;
    text-align: left;
    height: 24px;
    line-height: 22px;
    display: flex;
    border-radius: 4px;
    box-sizing: border-box;
    white-space: nowrap;
    // width: 200px;
}

.item-axis:hover {
    background-color: #fdfdfd;
    cursor: pointer;
}

span {
    font-size: 12px;
}

.summary-span {
    margin-left: 4px;
    color: #878d9f;
    position: absolute;
    right: 25px;
}

.inner-dropdown-menu {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%
}

.item-span-style {
    display: inline-block;
    width: 100px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
}

.summary-span-item {
    margin-left: 4px;
    color: #878d9f;
}
.mini-text{
    font-size: 12px;
}
</style>