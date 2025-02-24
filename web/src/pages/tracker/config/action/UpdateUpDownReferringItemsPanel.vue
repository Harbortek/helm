<template>
    <div>
    </div>
</template>

<script>
export default {
    name: 'UpdateUpDownReferringItemsPanel',
    props: {
        tracker: {
            type: Object
        },
        action: {
            type: Object
        }
    },
    components: {

    },
    data() {
        return {

        }
    },
    computed: {

    },
    mounted() {

    },
    methods: {
        validate(callback) {
            this.$refs.form.validate((valid) => {
                callback(valid)
            })
        },
    },
}
</script>

<style></style>