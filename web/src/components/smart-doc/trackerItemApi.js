/**
 * @module trackerItemApi
 */

let docs = {};
let config = {};
export default {
    get(docId) {
        return docs[docId];
    },
    set(docId, data) {
        docs[docId] = data;
    },
    remove(docId) {
        delete docs[docId];
        delete config[docId];
    },
    getDocConfig(docId) {
        return config[docId];
    },
    setDocConfig(docId, data) {
        config[docId] = data;
    }


}