import { createParser } from 'eventsource-parser';

export async function* streamAsyncIterable(stream) {
    const reader = stream.getReader();
    try {
        while (true) {
            const { done, value } = await reader.read();
            if (done) {
                return;
            }
            yield value;
        }
    } finally {
        reader.releaseLock();
    }
}

export class ChatGPTError extends Error {
    constructor(message, cause) {
        super(message);
        this.cause = cause;
        this.statusCode = cause?.statusCode;
        this.statusText = cause?.statusText;
        this.isFinal = cause?.isFinal;
        this.accountId = cause?.accountId;
        // Set the prototype explicitly.
        Object.setPrototypeOf(this, ChatGPTError.prototype);
    }
}

export async function fetchSSE(
    url,
    options
) {
    const { onMessage, onError, ...fetchOptions } = options;
    const res = await fetch(url, fetchOptions);
    if (!res.ok) {
        let reason;

        try {
            reason = await res.text();
        } catch (err) {
            reason = res.statusText;
        }

        const msg = `ChatGPT error ${res.status}: ${reason}`;
        const error = new ChatGPTError(msg, { cause: res });
        error.statusCode = res.status;
        error.statusText = res.statusText;
        throw error;
    }

    const parser = createParser((event) => {
        if (event.type === 'event') {
            onMessage(event.data);
        }
    });

    // handle special response errors
    const feed = (chunk) => {
        let response = null;

        try {
            response = JSON.parse(chunk);
        } catch {
            // ignore
        }

        if (response?.detail?.type === 'invalid_request_error') {
            const msg = `ChatGPT error ${response.detail.message}: ${response.detail.code} (${response.detail.type})`;
            const error = new ChatGPTError(msg, { cause: response });
            error.statusCode = response.detail.code;
            error.statusText = response.detail.message;

            if (onError) {
                onError(error);
            } else {
                console.error(error);
            }

            // don't feed to the event parser
            return;
        }

        parser.feed(chunk);
    };

    if (!res?.body?.getReader) {
        // Vercel polyfills `fetch` with `node-fetch`, which doesn't conform to
        // web standards, so this is a workaround...
        const body = res.body;

        if (!body.on || !body.read) {
            throw new ChatGPTError('unsupported "fetch" implementation');
        }

        body.on('readable', () => {
            let chunk;
            while (null !== (chunk = body.read())) {
                feed(chunk.toString());
            }
        });
    } else {
        for await (const chunk of streamAsyncIterable(res.body)) {
            const str = new TextDecoder().decode(chunk);
            feed(str);
        }
    }
}
