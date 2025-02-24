<template>
    <a-select v-model="selectItem" :placeholder="placeholder" @change="onChange" :disabled="disabled" v-bind="$attrs">
        <a-select-option v-for="item in sprints" :key="item.id" :title="item.name" :value="item.id">
            {{ item.name }}
        </a-select-option>
    </a-select>
</template>

<script>
import {
    findSprints
} from "@/services/plan/SprintService";

export default ({
    name: "SprintSelect",
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        projectId: {
            required: true
        },
        value: {
            required: true
        },
        disabled: false,
        selectFirst: true,
        placeholder:false,
    },
    watch: {
        projectId: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        value: function (curVal, oldVal) {
            this.selectItem = curVal;
            if (this.selectFirst && this.sprints.length > 0) {
                this.selectItem = this.sprints[0].id;
                this.onChange(this.selectItem);
            }
        }
    },
    mounted() {
        this.selectItem = this.value
        this.loadData()
    },
    data() {
        return {
            selectItem: "",
            sprints: []
        };
    },
    methods: {
        loadData() {
            let that = this;
            findSprints(this.projectId).then(resp => {
                that.sprints = resp;
                if (this.selectFirst && this.sprints.length > 0) {
                    this.selectItem = this.sprints[0].id;
                    this.onChange(this.selectItem);
                }
            });
        },
        onChange(v) {
            this.$emit("input", v);
            const e = this.sprints.find(_ => _.id === v)
            this.$emit("change", v, e);
        }
    }
});
</script>
