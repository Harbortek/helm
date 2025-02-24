<template>
    <a-select v-model="selectItem" placeholder="请选择" @change="onChange" :disabled="disabled">
        <a-select-option v-for="item in versions" :key="item.id" :title="item.name" :value="item.id">
            {{ item.name }}
        </a-select-option>
    </a-select>
</template>

<script>
import { findVersions } from '@/services/plan/TargetVersionService'

export default ({
    name: "TargetVersionSelect",
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
        selectFirst: true
    },
    watch: {
        projectId: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        value: function (curVal, oldVal) {
            if (curVal) {
                this.selectItem = curVal;
            } else {
                if (this.selectFirst && this.versions.length > 0) {
                    this.selectItem = this.versions[0].id;
                    this.onChange(this.selectItem);
                }
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
            versions: []
        };
    },
    methods: {
        loadData() {
            let that = this;
            findVersions(this.projectId).then(resp => {
                that.versions = resp;
                if (this.selectFirst && this.versions.length > 0) {
                    this.selectItem = this.versions[0].id;
                    this.onChange(this.selectItem);
                }
            });
        },
        onChange(v) {
            this.$emit("input", v);
            this.$emit("change", v);
        }
    }
});
</script>
