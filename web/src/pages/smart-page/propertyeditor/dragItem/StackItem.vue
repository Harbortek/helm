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
                    <span slot="title" style="font-size:12px;">
                        <a-icon type="calculator" />
                        {{ $t('chart.summary') }}
                        <span class="summary-span-item">({{ $t('chart.' + item.summary) }})</span>
                    </span>

                    <a-menu-item key="sum_sum" style="font-size:12px;">{{ $t('chart.sum')
                    }}</a-menu-item>
                    <a-menu-item key="sum_avg" style="font-size:12px;">{{ $t('chart.avg')
                    }}</a-menu-item>
                    <a-menu-item key="sum_max" style="font-size:12px;">{{ $t('chart.max')
                    }}</a-menu-item>
                    <a-menu-item key="sum_min" style="font-size:12px;">{{ $t('chart.min')
                    }}</a-menu-item>
                    <a-menu-item key="sum_stddev_pop" style="font-size:12px;">{{ $t('chart.stddev_pop')
                    }}</a-menu-item>
                    <a-menu-item key="sum_var_pop" style="font-size:12px;">{{ $t('chart.var_pop')
                    }}</a-menu-item>
                    <a-menu-item key="sum_count" style="font-size:12px;">{{ $t('chart.count')
                    }}</a-menu-item>
                    <a-menu-item key="sum_count_distinct" style="font-size:12px;">{{ $t('chart.count_distinct')
                    }}</a-menu-item>
                </a-sub-menu>

                <a-sub-menu>
                    <span slot="title" style="font-size:12px;">
                        <a-icon component="sorting" />
                        {{ $t('chart.sort') }}
                        <span class="summary-span-item">({{ $t('chart.' + item.sort) }})</span>
                    </span>

                    <a-menu-item key="sort_none" style="font-size:12px;">{{ $t('chart.none')
                    }}</a-menu-item>
                    <a-menu-item key="sort_asc" style="font-size:12px;">{{ $t('chart.asc')
                    }}</a-menu-item>
                    <a-menu-item key="sort_desc" style="font-size:12px;">{{ $t('chart.desc')
                    }}</a-menu-item>
                    <!-- <a-menu-item v-show="!item.chartId && (item.deType === 0 || item.deType === 5)"
                        :command="beforeSort('custom_sort')" style="font-size:12px;">{{ $t('chart.custom_sort')
                        }}...</a-menu-item> -->
                </a-sub-menu>
                <a-sub-menu v-show="item.type === 'DATE'" divided :title="$t('chart.dateStyle')">

                    <a-menu-item key="date_y">{{ $t('chart.y') }}</a-menu-item>
                    <a-menu-item key="date_y_Q" v-if="showDateExt">{{ $t('chart.y_Q')
                    }}</a-menu-item>
                    <a-menu-item key="date_y_M">{{ $t('chart.y_M') }}</a-menu-item>
                    <a-menu-item key="date_y_W" v-if="showDateExt">{{ $t('chart.y_W')
                    }}</a-menu-item>
                    <a-menu-item key="date_y_M_d">{{ $t('chart.y_M_d')
                    }}</a-menu-item>
                    <a-menu-item key="date_H_m_s" divided>{{ $t('chart.H_m_s')
                    }}</a-menu-item>
                    <a-menu-item key="date_y_M_d_H_m">{{ $t('chart.y_M_d_H_m')
                    }}</a-menu-item>
                    <a-menu-item key="date_y_M_d_H_m_s">{{ $t('chart.y_M_d_H_m_s')
                    }}</a-menu-item>
                </a-sub-menu>
                <a-sub-menu v-show="item.type === 'DATE'" :title="$t('chart.datePattern')">

                    <a-menu-item key="date_pattern_sub">{{ $t('chart.date_sub')
                    }}(1990-01-01)</a-menu-item>
                    <a-menu-item key="date_pattern_split">{{
                        $t('chart.date_split')
                    }}(1990/01/01)</a-menu-item>
                </a-sub-menu>


                <a-menu-item icon="a-icon-edit-outline" divided key="rename">
                    <a-icon type="edit" />
                    <span>{{ $t('chart.show_name_set') }}</span>
                </a-menu-item>
                <a-menu-item icon="a-icon-delete" divided key="remove">
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
    name: 'StackItem',
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
            } else if (command === 'sum_sum') {
                this.summary('sum')
            } else if (command === 'sum_avg') {
                this.summary('avg')
            } else if (command === 'sum_max') {
                this.summary('max')
            } else if (command === 'sum_min') {
                this.summary('min')
            } else if (command === 'sum_stddev_pop') {
                this.summary('stddev_pop')
            } else if (command === 'sum_var_pop') {
                this.summary('var_pop')
            } else if (command === 'sum_count') {
                this.summary('count')
            } else if (command === 'sum_count_distinct') {
                this.summary('count_distinct')
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
                this.$emit('onItemChange', deepCopy(this.item))
            }
        },
        summary(type) {
            this.item.index = this.index
            this.item.summary = type
            this.$emit('onItemChange', deepCopy(this.item))
        },
        dateStyle(type) {
            this.item.dateStyle = type
            this.$emit('onItemChange', deepCopy(this.item))
        },

        datePattern(type) {
            this.item.datePattern = type
            this.$emit('onItemChange', deepCopy(this.item))
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
            this.$emit('onItemRemove', deepCopy(this.item))
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
</style>