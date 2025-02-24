<template>
    <span :style="{ color: item.color, 'background-color': item.backgroundColor }">{{ item.name }}</span>
</template>

<script>
import { findEnumItemById } from "@/services/system/EnumService";
export default {
    name: "EnumItemValue",
    props: {
        value: {
            required: true
        }
    },
    data() {
        return {
            enumId: undefined,
            item: { name: '-' }
        }
    },
    watch: {
        value: {
            immediate: true,
            handler: function (curVal, oldVal) {
                this.enumId = curVal;
                if (this.enumId) {
                    this.loadData();
                } else {
                    this.item = { name: '-' }
                }
            }
        }
    },
    methods: {
        loadData() {
            findEnumItemById(this.enumId).then(resp => {
                this.item = resp;
            })
        }
    },
}
</script>

<style></style>