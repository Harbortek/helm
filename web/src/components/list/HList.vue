<template>
    <div style="width: 100%;height: 100%;background-color: transparent;">
        <div class="left-block_action">
            <slot name="title"></slot>
        </div>
        <ul class="list-container">
            <li v-for="item in data" :key="item.key" :class="
                (selectedItem || defaultSelectedItem) === item.key
                    ? 'link link-selected'
                    : 'link'" @click="handleClick(item)">
                {{ item.name }}
            </li>
        </ul>
    </div>
</template>
<script>
export default {
    name: "HList",
    props: {
        width: {
            type: String,
            required: false,
            default: "200px",
        },
        data: {
            type: Array,
            required: true,
            default: []
        },
        defaultSelectedItem: {
            type: String,
            required: false,
            default: null,
        },
    },
    data() {
        return {
            selectedItem: null
        };
    },
    methods: {
        handleClick(item) {
            if (this.selectedItem === item.key) {
                return;
            }
            this.selectedItem = item.key;
            this.$emit('selectedChange', item.key);
        }
    }
};
</script>
<style lang="less" scoped>
.left-block_action {
    align-items: center;
    margin: 10px 0px 10px 30px;
    color: #606060;
    font-size: 12px;

    .desc {}
}

.list-container {
    flex-grow: 1;
    overflow: auto;
    width: 100%;
    height: 100%;
    padding-left: 0px;

    .link {
        margin: 0;
        padding-left: 41px;
        height: 30px;
        line-height: 30px;
        display: flex;
        justify-content: space-between;
        padding-right: 20px;
        color: #303030;
        position: relative;
        display: flex;
        align-items: center;
        padding-left: 40px;
        margin-right: 0px;
        border-top-right-radius: 3px;
        border-bottom-right-radius: 3px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        height: 34px;
        line-height: 34px;
        outline: none;
        border-radius: 0px;

        &:hover {
            cursor: pointer;
            background-color: #e8e8e8;
        }
    }

    .link-selected {
        margin: 0;
        padding-left: 41px;
        height: 30px;
        line-height: 30px;
        display: flex;
        justify-content: space-between;
        padding-right: 20px;
        position: relative;
        display: flex;
        align-items: center;
        padding-left: 35px;
        margin-right: 0px;
        border-top-right-radius: 3px;
        border-bottom-right-radius: 3px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        height: 34px;
        line-height: 34px;
        outline: none;
        border-radius: 0px;
        background-color: #eaf3fc;
        color: #338fe5;
        border-left: solid 4px #338fe5;
    }

    .link-selected:hover {
        background: #e8e8e8;
    }
}
</style>
  