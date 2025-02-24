import { SlateTransforms, Editor, Range } from "@wangeditor/editor";
import bus from '@/utils/bus';

const BTN_CLASS = 'slash-command-button';
const ACTIVE_CLASS = 'active';

class SlashCommandModal {
    constructor(editor) {
        this.editor = editor;
        this.docId = editor.getConfig()?.docId || '';
        this.modal = document.createElement('div');
        this.modal.className = 'slash-command-modal';
        this.modal.style.display = 'none';

        this.initButtons();
        this.activeButtonIdx = 0;
        this.handleKeyDownWith = (event) => {
            this.handleKeyDown(event);
        };
        document.body.appendChild(this.modal);
        this.initEvents();
    }

    initButtons() {
        const buttons = [
            {
                text: 'H1',
                className: BTN_CLASS,
                onClick: () => this.handleButtonClick('add', { type: 'header1', children: [{ text: '' }] })
            },
            {
                text: 'H2',
                className: BTN_CLASS,
                onClick: () => this.handleButtonClick('add', { type: 'header2', children: [{ text: '' }] })
            },
            {
                text: 'H3',
                className: BTN_CLASS,
                onClick: () => this.handleButtonClick('add', { type: 'header3', children: [{ text: '' }] })
            },
            {
                text: 'H4',
                className: BTN_CLASS,
                onClick: () => this.handleButtonClick('add', { type: 'header4', children: [{ text: '' }] })
            },
            {
                text: 'H5',
                className: BTN_CLASS,
                onClick: () => this.handleButtonClick('add', { type: 'header5', children: [{ text: '' }] })
            },
            {
                text: '段落',
                className: BTN_CLASS,
                onClick: () => this.handleButtonClick('add', { type: 'paragraph', children: [{ text: '' }] })
            }
        ];
        const delButon = [
            {
                text: 'Delete',
                className: 'slash-command-button',
                onClick: () => this.handleButtonClick('delete')
            }];
        const { trackers = [] } = this.editor.getConfig().EXTEND_CONF;
        const LEN = buttons.length;

        buttons.forEach((button, index) => {
            const buttonElement = document.createElement('div');
            buttonElement.textContent = button.text;
            buttonElement.className = button.className;
            buttonElement.onclick = button.onClick;
            buttonElement.tabIndex = index; // 设置初始焦点
            this.modal.appendChild(buttonElement);
        });
        trackers.forEach((item, index) => {
            const buttonElement = document.createElement('div');
            buttonElement.textContent = item.name;
            buttonElement.className = `${BTN_CLASS} ${ACTIVE_CLASS}`;
            buttonElement.onclick = () => this.handleAddTrackerItem(item);
            buttonElement.tabIndex = LEN + index; // 设置初始焦点
            this.modal.appendChild(buttonElement);
        });
        delButon.forEach((button, index) => {
            const buttonElement = document.createElement('div');
            buttonElement.textContent = button.text;
            buttonElement.className = button.className;
            buttonElement.onclick = button.onClick;
            buttonElement.tabIndex = LEN + trackers.length + index; // 设置初始焦点
            this.modal.appendChild(buttonElement);
        });
    }
    initEvents() {
        const EVE_NAME = `post-create-tracker-item-by-${this.docId || ''}`;
        bus.$on(EVE_NAME, this.handlePostAddTrackerItem.bind(this));
    }

    handleButtonClick(action, param) {
        this.hide();
        if (action === 'add') {
            this.handleAdd(param);
        } else if (action === 'delete') {
            this.handleDelete();
        }
    }

    handleAdd(param) {
        const { selection } = this.editor;
        if (param) {
            setTimeout(() => {
                SlateTransforms.insertNodes(this.editor, param, {
                    at: [selection.anchor.path[0] + 1],
                    mode: 'highest',
                    select: true,
                })
            }, 0);
        }
    }
    handleAddTrackerItem(param) {

        this.hide();
        bus.$emit(`create-tracker-item-by-${this.docId}`, param);
    }
    handlePostAddTrackerItem(item) {
        this.editor.restoreSelection();
        setTimeout(() => {
            const { selection } = this.editor;
            console.log('handlePostAddTrackerItem', item)
            if (item) {
                SlateTransforms.insertNodes(this.editor, {
                    type: 'tracker-item',
                    children: [{
                        type: 'tracker-item-title',
                        children: [{ text: item.name }],
                        ref: item.id
                    }, {
                        type: 'tracker-item-description',
                        children: item.description || [{ type: 'paragraph', children: [{ text: '' }] }],
                        ref: item.id
                    }, {
                        type: 'tracker-item-extra',
                        children: [{ text: '' }],
                        ref: item.id
                    }],
                    ref: item.id,
                    trackerItem: item
                }, {
                    at: [selection.anchor.path[0] + 1],
                    mode: 'highest',
                    select: false,
                })
            }
        }, 0);
    }
    handleDelete() {
        const { selection } = this.editor;
        if (selection) {
            setTimeout(() => {
                const path = [selection.anchor.path[0]]
                SlateTransforms.select(this.editor, {
                    anchor: {
                        path,
                        offset: 0,
                    },
                    focus: {
                        path,
                        offset: 0,
                    },
                });
                SlateTransforms.removeNodes(this.editor, { at: path });
            }, 0);
        }
    }

    show() {
        const bodyRect = this.editor.getEditableContainer().getBoundingClientRect();
        const editRect = this.editor.getSelectionPosition();
        this.modal.style.position = 'absolute';
        this.modal.style.left = `calc(${bodyRect.left}px + ${editRect.left})`;
        this.modal.style.top = `calc(${bodyRect.top}px + ${editRect.top})`;
        this.activeButtonIdx = 0;
        document.addEventListener('keydown', this.handleKeyDownWith);

        const buttons = this.modal.querySelectorAll(`.${BTN_CLASS}`);
        this.updateActiveButton(buttons[this.activeButtonIdx], buttons);
        this.modal.style.display = 'block';
    }

    hide() {
        document.removeEventListener('keydown', this.handleKeyDownWith);
        this.modal.style.display = 'none';
        const buttons = this.modal.querySelectorAll(`.${BTN_CLASS}`);
        this.updateActiveButton(null, buttons);
        this.editor.restoreSelection();
    }

    handleKeyDown(event) {
        const buttons = this.modal.querySelectorAll(`.${BTN_CLASS}`);
        if (event.key === 'Escape') {
            this.hide();
        } else if (event.key === 'ArrowDown') {
            this.activeButtonIdx++;
            if (this.activeButtonIdx >= buttons.length) {
                this.activeButtonIdx = 0;
            }
            const nextButton = buttons[this.activeButtonIdx];
            this.updateActiveButton(nextButton, buttons);
        } else if (event.key === 'ArrowUp') {
            this.activeButtonIdx--;
            if (this.activeButtonIdx < 0) {
                this.activeButtonIdx = buttons.length - 1;
            }
            const prevButton = buttons[this.activeButtonIdx];
            this.updateActiveButton(prevButton, buttons);
        } else if (event.key === 'Enter') {
            const activeButton = this.modal.querySelector(`.${BTN_CLASS}.${ACTIVE_CLASS}`);
            if (activeButton) {
                activeButton.click();
            }
        }
        event.preventDefault();
        event.stopPropagation();
    }

    updateActiveButton(activeButton, buttons) {
        buttons.forEach(button => {
            button.classList.remove(ACTIVE_CLASS);
        });
        activeButton && activeButton.classList.add(ACTIVE_CLASS);
    }
}

export default SlashCommandModal;