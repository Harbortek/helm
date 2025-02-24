<template>
    <a-select :mode="selectMultiple ? 'multiple' : 'default'" v-model="selectItem" placeholder="请选择" @change="onChange"
        :disabled="disabled">
        <a-select-option v-for="item in data" :key="item.id" :title="item.name" :value="item.id">
            {{ item.name }}
        </a-select-option>
    </a-select>
</template>

<script>
import {
    findEnumsByCode
} from "@/services/system/EnumService";

export default ({
    name: "ProjectStatusSelect",
    model: {
        prop: "value", //绑定的值，通过父组件传递
        event: "change" //自定义时间名
    },
    props: {
        value: {
            required: true
        },
        disabled: false,
        selectFirst: true,
        selectMultiple: true,
    },
    watch: {
        trackerId: {
            handler: function (newVal, oldVal) {
                this.loadData();
            }
        },
        value: function (curVal, oldVal) {
            if (curVal) {
                this.selectItem = curVal;
            } else {
                if (this.selectFirst && this.trackers.length > 0) {
                    this.selectItem = this.trackers[0].id;
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
            data: []
        };
    },
    methods: {
        loadData() {
            let that = this;
            findEnumsByCode('PROJECT_STATUS_MEANING').then((resp) => {
                that.data = resp;
                if (this.selectFirst && this.trackers.length > 0) {
                    this.selectItem = this.data[0].id;
                    this.onChange(this.selectItem);
                }
            });
        },
        onChange(v) {
            this.$emit("change", v);
        }
    }
});
</script>
