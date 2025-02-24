<template>
    <a-select v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled">
        <a-select-option v-for="item in data" :key="item.id" :title="item.name" :value="item.id">
            {{ item.name }}
        </a-select-option>
    </a-select>
</template>

<script>
import {
    findEnumCategories
} from "@/services/system/EnumService";

export default ({
    name: "EnumCategorySelect",
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        value: {
            required: true
        },
        disabled: false,
        projectId: false,
    },
    watch: {
        value: function (curVal, oldVal) {
            this.selectItem = curVal;
        }
    },
    mounted() {
        this.selectItem = this.value
        this.loadData()
    },
    data() {
        return {
            selectItem: "",
            data: []
        };
    },
    methods: {
        loadData() {
            findEnumCategories(null, this.projectId).then(resp => {
                this.data = resp.content;
            });
        },
        onChange(v) {
            this.$emit("change", v);
        }
    }
});
</script>
