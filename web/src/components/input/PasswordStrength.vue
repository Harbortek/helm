<template>
    <div class="intensity">
        <span class="psdText">密码强度</span>
        <span
        class="line"
        :class="{ active: strength >= 1 }"
        ></span>
        <span
        class="line"
        :class="{ active: strength >= 2 }"
        ></span>
        <span
        class="line"
        :class="{ active: strength === 3 }"
        ></span>
    </div>
</template>

<script>
export default {
    name: 'PasswordStrength',
    props: {
        password: {
            type: String,
            default: ''
        }
    },
    data() {
        return {
            strength: 0
        }
    },
    watch: {
        password: {
            handler(newVal) {
                if (!newVal) {
                    this.strength = 0
                    this.$emit('on-change', this.strength)
                    return
                } else if (newVal.length < 6) {
                    this.strength = 1
                    this.$emit('on-change', this.strength)
                    return
                }
                let hasNum = 0
                let hasWord = 0
                let hasSpecChar = 0
                for (let i = 0; i < newVal.length; i++) {
                    if (hasNum === 0 && /[0-9]/.test(newVal[i])) {
                        hasNum++
                    } else if (hasWord === 0 && /[a-zA-Z]/.test(newVal[i])) {
                        hasWord++
                    } else if (hasSpecChar === 0 && /[@#￥!^&*()=+-]/.test(newVal[i])) {
                        hasSpecChar++
                    }
                }
                if (hasNum + hasWord + hasSpecChar < 2) {
                    this.strength = 1
                } else if (hasNum + hasWord + hasSpecChar === 2) {
                    this.strength = 2
                } else if (hasNum + hasWord + hasSpecChar === 3) {
                    this.strength = 3
                }
                this.$emit('on-change', this.strength)
                
            }
        }
    }
}
</script>

<style scoped lang="less">
    .intensity {
        width: 100%;
        margin-bottom: 16px;
        .psdText {
            display: inline-block;
            align-items: left;
            margin-bottom: 8px;
            font-size: 12px;
            margin-right: 10px;
        }
        .line {
            display: inline-block;
            width: 20%;
            height: 4px;
            background: #d8d8d8;
            border-radius: 3px;
            margin-right: 8px;
            border-radius: 4px;
            opacity: .2;
            transition: all .2s linear;
            &:nth-child(2) {
                background: #FF0000;
            }
            &:nth-child(3) {
                background: #FF7200;
            }
            &:nth-child(4) {
                background: #3FD662;
            }
            &.active {
                opacity: 1;
            }
        }
    }
</style>

