export default {
  "tracker.field.type.TEXT": "Single Line Text",
  "tracker.field.type.TEXT_AREA": "Multi-line Text",
  "tracker.field.type.WIKI": "Formatted Text",
  "tracker.field.type.OPTIONS": "Single Select Menu",
  "tracker.field.type.MULTI_OPTIONS": "Multi-select Menu",
  "tracker.field.type.BOOL": "Yes/No",
  "tracker.field.type.DATE": "Date",
  "tracker.field.type.TIME": "Time",
  "tracker.field.type.DURATION": "Duration",
  "tracker.field.type.INTEGER": "Integer",
  "tracker.field.type.DECIMAL": "Decimal",
  "tracker.field.type.USER": "Single Select User",
  "tracker.field.type.MEMBERS": "Multi-select Users",
  "tracker.field.type.WORK_ITEM": "Work Item",
  "tracker.field.type.WORK_ITEM_NO": "Work Item Number",
  "tracker.field.type.WORK_ITEM_TYPE": "Work Item Type",
  "tracker.field.type.TABLE": "Table",
  "tracker.field.type.STATUS": "Status",
  "tracker.field.type.STATUS_TYPE": "Status Type",
  "tracker.field.type.COUNTRY": "Country",
  "tracker.field.type.LANGUAGE": "Language",
  "tracker.field.type.COLOR": "Color",
  "tracker.field.type.REFERENCE": "Reference",
  "tracker.field.type.PROJECT": "Work Item Project",
  "tracker.field.type.SPRINT": "Sprint",
  "tracker.field.type.TEST_STEP": "Test Step",

  "project.permission.PROJECT_ADMIN": "Manage Project",
  "project.permission.desc.PROJECT_ADMIN":
    "Add members as project administrators to manage the current project and update project configuration",

  "project.permission.PROJECT_VIEW": "View Project",
  "project.permission.desc.PROJECT_VIEW":
    "Allow members to browse the current project, including work items, filters, and other information",

  "tracker.permission.ITEM_VIEW": "View {0}",
  "tracker.permission.desc.ITEM_VIEW": "Allow members to view {0}",

  "tracker.permission.ITEM_CREATE": "Create {0}",
  "tracker.permission.desc.ITEM_CREATE": "Allow members to create {0}",

  "tracker.permission.ITEM_EDIT": "Edit {0}",
  "tracker.permission.desc.ITEM_EDIT":
    "Allow members to modify {0} and perform other changes to {0}",

  "tracker.permission.ITEM_CHANGE_STATUS": "Update {0} Status",
  "tracker.permission.desc.ITEM_CHANGE_STATUS":
    "Allow members to update the status of {0}",

  "tracker.permission.ITEM_DELETE": "Delete {0}",
  "tracker.permission.desc.ITEM_DELETE": "Allow members to delete {0}",
  "tracker.permission.ITEM_MANAGE_WATCHER": "Manage Watchers",
  "tracker.permission.desc.ITEM_MANAGE_WATCHER":
    "Allow members to modify watchers of {0}",
  "tracker.permission.ITEM_EDIT_ASSOCIATIONS": "Edit Associated Work Items",
  "tracker.permission.desc.ITEM_EDIT_ASSOCIATIONS":
    "Allow members to modify associated work items of {0}",
  "tracker.permission.ITEM_EXPORT": "Export {0} List",
  "tracker.permission.desc.ITEM_EXPORT":
    "Allow members to export the {0} list as a file",
  "tracker.permission.ITEM_MANAGE_ESTIMATE_WORK_HOURS":
    "Manage Estimated Work Hours",
  "tracker.permission.desc.ITEM_MANAGE_ESTIMATE_WORK_HOURS":
    "Allow members to add, modify, and delete estimated work hours for {0}",
  "tracker.permission.ITEM_MANAGE_ALL_REGISTERED_WORK_HOURS":
    "Manage All Registered Work Hours",
  "tracker.permission.desc.ITEM_MANAGE_ALL_REGISTERED_WORK_HOURS":
    "Allow members to add registered work hours for all members under {0}, and modify or delete registered work hours and remaining work hours for all members in the member work records",
  "tracker.permission.ITEM_MANAGE_OWN_REGISTERED_WORK_HOURS":
    "Manage Own Registered Work Hours",
  "tracker.permission.desc.ITEM_MANAGE_OWN_REGISTERED_WORK_HOURS":
    "Allow members to add their own registered work hours under {0}, and modify or delete their own registered work hours and remaining work hours in the member work records",

  "tracker.event.CREATE_ITEM": "Create {0}",
  "tracker.event.desc.CREATE_ITEM": "Send notification when {0} is created",

  "tracker.event.CHANGE_ITEM_OWNER": "Change Owner",
  "tracker.event.desc.CHANGE_ITEM_OWNER":
    "Send notification when the owner is changed",

  "tracker.event.CHANGE_ITEM_STATUS": "Change Status",
  "tracker.event.desc.CHANGE_ITEM_STATUS":
    "Send notification when the status of {0} is changed",

  "tracker.event.CHANGE_ITEM_PRIORITY": "Change Priority",
  "tracker.event.desc.CHANGE_ITEM_PRIORITY":
    "Send notification when the priority of {0} is changed",

  "tracker.event.CHANGE_ITEM_TITLE": "Change Title",
  "tracker.event.desc.CHANGE_ITEM_TITLE":
    "Send notification when the title of {0} is changed",

  "tracker.event.CHANGE_ITEM_DESCRIPTION": "Change Description",
  "tracker.event.desc.CHANGE_ITEM_DESCRIPTION":
    "Send notification when the description of {0} is changed",

  "tracker.event.CREATE_COMMENTS": "Create Comments",
  "tracker.event.desc.CREATE_COMMENTS":
    "Send notification when comments are added",

  "tracker.event.CHANGE_ITEM_WATCHER": "Change Watchers",
  "tracker.event.desc.CHANGE_ITEM_WATCHER":
    "Send notification when members add or remove {0} from their watchlist",

  "tracker.event.CHANGE_ITEM_ASSOCIATIONS": "Change Associated Items",
  "tracker.event.desc.CHANGE_ITEM_ASSOCIATIONS":
    "Send notification when associated items are added or modified",

  "tracker.event.CHANGE_ITEM_ATTACHMENT": "Upload Attachment",
  "tracker.event.desc.CHANGE_ITEM_ATTACHMENT":
    "Send notification when an attachment is uploaded for {0}",

  "tracker.event.CHANGE_ITEM_PROGRESS": "Change Progress",
  "tracker.event.desc.CHANGE_ITEM_PROGRESS":
    "Send notification when the progress is changed",

  "tracker.event.CHANGE_ITEM_ESTIMATE_WORKING_HOURS":
    "Change Estimated Working Hours",
  "tracker.event.desc.CHANGE_ITEM_ESTIMATE_WORKING_HOURS":
    "Send notification when estimated working hours are added, modified, or deleted",

  "tracker.event.CHANGE_ITEM_REGISTERED_WORKING_HOURS":
    "Change Registered Working Hours",
  "tracker.event.desc.CHANGE_ITEM_REGISTERED_WORKING_HOURS":
    "Send notification when registered working hours are added, modified, or deleted",

  "tracker.event.CHANGE_ITEM_REMAINING_WORKING_HOURS":
    "Change Remaining Working Hours",
  "tracker.event.desc.CHANGE_ITEM_REMAINING_WORKING_HOURS":
    "Send notification when remaining working hours are added, modified, or deleted",

  "tracker.event.CHANGE_ITEM_PLAN_START_DATE": "Change Planned Start Date",
  "tracker.event.desc.CHANGE_ITEM_PLAN_START_DATE":
    "Send notification when the planned start date is modified",

  "tracker.event.CHANGE_ITEM_PLAN_END_DATE": "Change Planned End Date",
  "tracker.event.desc.CHANGE_ITEM_PLAN_END_DATE":
    "Send notification when the planned end date is modified",

  "tracker.event.CHANGE_ITEM_WIKI": "Change Associated Wiki Page",
  "tracker.event.desc.CHANGE_ITEM_WIKI":
    "Send notification when a Wiki page is associated or removed",

  "tracker.event.CHANGE_ITEM_SPRINT": "Change Assigned Sprint",
  "tracker.event.desc.CHANGE_ITEM_SPRINT":
    "Send notification when the assigned sprint is added or modified",

  "project.status.NOT_STARTED": "Not Started",
  "project.status.ONGOING": "Ongoing",
  "project.status.ENDED": "Completed",
  "project.status.CLOSED": "Closed",
  "project.status.DELAYED": "Delayed",

  "tracker.field.permission.type.Unrestricted": "Unrestricted",
  "tracker.field.permission.type.Single": "Single Permission",
  "tracker.field.permission.type.PerStatus": "Per Status",

  "project.page.permission.PAGE_READ": "Read",
  "project.page.permission.PAGE_WRITE": "Write",

  ACTION_TYPE_UPDATE_ITEM_PROPERTIES: "Update Item Properties",
  ACTION_TYPE_TRIGGER_STATE_TRANSITION: "Trigger State Transition",
  ACTION_TYPE_UPDATE_REFERRING_ITEMS: "Update Referring Items",
  ACTION_TYPE_CREATE_NEW_UP_DOWNSTREAM_REFERRING_ITEMS:
    "Create New Up/Downstream Referring Items",
  ACTION_TYPE_CREATE_A_NEW_SEQUENTIAL_ID: "Create a New Sequential ID",
  ACTION_TYPE_EXECUTE_A_CUSTOM_SCRIPT: "Execute a Custom Script",
  ACTION_TYPE_SEND_A_CUSTOMER_EMAIL_TO_SPECIFIC_RECIPIENTS:
    "Send a Custom Email to Specific Recipients",
  ACTION_TYPE_START_A_NEW_REVIEW: "Start a New Review",
  ACTION_TYPE_WEBHOOK: "Webhook",

  "setMethod.SET_VALUE": "Set Value",
  "setMethod.VALUE_OF": "Value of",
  "setMethod.RESULT_OF": "Result of",

  "page.type.wiki": "Wiki",
  "page.type.report": "Report",
  "page.type.component": "Component",
  "page.type.tracker": "Tracker",
  "page.type.url": "External Link",
};
