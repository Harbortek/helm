<template>
    <div class="page-header">
        <div class="page-description page-description-info manager-desc" v-if="description">
            <div class="page-description-border"></div>
            <div class="page-description-container">
                <p class="page-description-p style--font-14">
                    {{ description }}
                </p>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'HAlert',
    props: {
        description: {
            required: true
        }
    },
    data() {
        return {

        }
    },
    methods: {

    },
}
</script>

<style lang="less" scoped>
.page-description {
    display: flex;
    // margin-top: 20px;
    line-height: 1.5;
    box-sizing: border-box;
    border-bottom: 1px solid #e8e8e8;
    border-radius: 0;
    border-right: 1px solid #e8e8e8;
    border-top: 1px solid #e8e8e8;
}

.page-description-border {
    border-radius: 0;
    flex: none;
    width: 4px;
}

.page-description-info .page-description-border {
    background: #338fe5;
}

.page-description-container {
    background: #fff;
    display: flex;
    flex: auto;
    padding: 10px;
}

.page-description-p {
    margin: 0 0 0 0;
    padding: 0;
    white-space: pre-wrap;
}

.style--font-14 {
    font-size: 14px;
    line-height: 22px;
}
</style>