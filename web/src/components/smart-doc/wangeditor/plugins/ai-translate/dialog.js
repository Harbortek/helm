/**
 * 对话框工具模块
 * 提供统一的对话框交互功能
 */
import Vue from 'vue'
import AIDialog from './AIDialog.vue'

let dialogInstance = null

function getDialogInstance() {
    if (!dialogInstance) {
        const DialogConstructor = Vue.extend(AIDialog)
        dialogInstance = new DialogConstructor()
        dialogInstance.$mount()
        document.body.appendChild(dialogInstance.$el)
    }
    return dialogInstance
}

/**
 * 显示输入对话框
 * @param {string} defaultValue - 默认值
 * @returns {Promise<string|null>} - 用户输入的内容，取消则返回null
 */
export function showPromptDialog(defaultValue = '') {
    return getDialogInstance().show(defaultValue)
}

/**
 * 显示确认对话框
 * @param {string} message - 确认消息
 * @returns {Promise<boolean>} - 用户确认结果
 */
export function showConfirmDialog(message) {
    return getDialogInstance().showConfirm(message)
}