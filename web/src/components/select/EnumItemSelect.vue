<template>
    <vxe-select v-model="selectItem" placeholer="请选择" @change="onChange" :disabled="disabled" type="text" transfer>
        <vxe-option v-for="item in data" :key="item.id" :value="item.id" :label="item.name"
            />
    </vxe-select>
    <!-- <a-select v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled" v-bind="$attrs">
        <a-select-option v-for="item in data" :key="item.id" :title="item.name" :value="item.id" @click="clickItem(item)">
            <span :style="{ color: item.color, 'background-color': item.backgroundColor }">{{ item.name }}</span>
        </a-select-option>
    </a-select> -->
</template>

<script>
import {
    findEnums, findEnumsByCode
} from "@/services/system/EnumService";

export default ({
    name: "EnumItemSelect",
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
        categoryId: {
            required: false
        },
        categoryCode: {
            required: false
        }
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
            if (this.categoryId) {
                const param = {
                    categoryId: this.categoryId,
                    projectId: this.projectId,
                }
                findEnums(param).then(resp => {
                    this.data = resp;
                });
            } else if (this.categoryCode) {
                findEnumsByCode(this.categoryCode, this.projectId).then(resp => {
                    this.data = resp;
                })
            }
        },
        onChange(v) {
            console.log("onChange",v.value);
            this.$emit("change", v.value);
        },
    }
});
</script>
