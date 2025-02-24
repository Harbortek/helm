import {
    updateOneProjectPage,findOneAndChildrenByPageId,createProjectPage
} from "@/services/tracker/ProjectPageService";
import { createTrackerItem, deleteTrackerItem, findOneTrackerItem } from "@/services/tracker/TrackerItemService";
import {
    findTrackerByIds,findTrackers
} from "@/services/tracker/TrackerService";
import {
    findByPageId,findOneProjectPage4Block,saveDoc,exportDoc2Word
} from "@/services/smart-doc/DocService";

export {
    updateOneProjectPage,
    exportDoc2Word,
    createTrackerItem,
    findOneTrackerItem,
    findTrackers,
    findTrackerByIds,
    findByPageId,
    saveDoc,
    findOneProjectPage4Block,
    findOneAndChildrenByPageId,
    createProjectPage
}