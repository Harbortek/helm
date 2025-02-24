import { request, METHOD } from "@/utils/request";
class RequestQueue {

    
    constructor(concurrency = 1) {
        this.controller = new AbortController();
        this.concurrency = concurrency;
        this.queue = [];
        this.running = 0;
    }

    async enqueue(req) {
        this.controller.abort();
        return new Promise((resolve, reject) => {
            this.queue.push({ req, resolve, reject });
            this.run();
        });
    }

    run() {
        while (this.running < this.concurrency && this.queue.length > 0) {
            const { req, resolve, reject } = this.queue.shift();
            this.running++;
           
            try {
                // console.log('running',request);
                request({
                    url: `/smart-doc/${req.projectId}/${req.pageId}/doc`,
                    method: METHOD.POST,
                    signal: this.controller.signal,
                    data: encodeURIComponent(req.doc)
                }).then(res => {
                    resolve(res);
                });
            } catch (error) {
                reject(error);
            } finally {
                this.running--;
                this.run();
            }
        }
    }
}

// queue.enqueue({
//     url: 'https://example.com',
//     method: 'GET',
// }).then(response => console.log(response));

// queue.enqueue({
//     url: 'https://example.com/api',
//     method: 'POST',
//     body: { key: 'value' },
//     headers: { 'Content-Type': 'application/json' },
// }).then(response => console.log(response));

/**
 *
 */
export default class RemoteApi {

    //请求队列
    static QUEUE = new RequestQueue(1);

    save(vo) {
        return RemoteApi.QUEUE.enqueue(vo);
    }



}
