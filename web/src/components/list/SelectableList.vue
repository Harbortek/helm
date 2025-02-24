<template>
    <div class="select-item-list">
        <div class="select-item-list-row" :key="item.id" v-for="item in items" @click="onSelect(item)">
            <div role="presentation"
                :class="getSelectedItem().name === item.name ? 'select-item-list-cell select-item-list-cell-active' : 'select-item-list-cell'">
                <div class="select-item-list-cell-content">
                    <div class="select-item-list-cell-title">{{ item.name }}</div>
                    <a-tooltip :overlayStyle="{'max-width': '400px','font-size': '12px'}">
                        <template slot="title">
                            {{ item.description }}
                        </template>
                        <div class="select-item-list-cell-desc">{{ item.description }}</div>
                    </a-tooltip>
                </div>
            </div>
            <div class="select-item-list-row-cell-split"></div>
        </div>
    </div>
</template>
<script>
export default {
    name: "SelectableList",
    props: ['items', 'selectedItem'],
    components: {},
    data() {
        return {
        };
    },
    computed: {
    },
    mounted() {
    },
    methods: {
        getSelectedItem() {
            return this.selectedItem
        },
        onSelect(item) {
            this.$emit('change', item)
        }
    },
};
</script>
<style lang="less" scoped>
.select-item-list {
    flex-direction: column;
    display: flex;
    flex: 1 1 auto;
    margin-top: 5px;

    .select-item-list-row {
        display: flex;
        flex-direction: column;

        ::selection {
            background: #338fe5;
            color: #fff;
        }

        .select-item-list-cell {
            cursor: pointer;
            padding: 0 20px;
            display: flex;
            flex-direction: row;
            border: 1px solid transparent;
            border-left: 3px solid transparent;
            transition: .3s;
            position: relative;

            .select-item-list-cell-content {
                max-width: 100%;
                flex: 1 1 auto;
                padding: 10px 0;

                .select-item-list-cell-title {
                    font-size: 15px;
                    color: #303030;
                    font-weight: 500;
                }

                .select-item-list-cell-desc {
                    font-size: 13px;
                    color:#87888a;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
            }
        }

        .select-item-list-cell.select-item-list-cell-active {
            border-left-color: #338fe5;

            .select-item-list-cell-title {
                color: #338fe5;
            }

            .select-item-list-cell-desc {
                color: #338fe5;
            }
        }



        .select-item-list-row-cell-split {
            height: 1px;
            background: #e8e8e8;
            display: flex;
            margin: 0 20px;
        }
    }
}
</style>
