/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.smartdoc.config;

import com.harbortek.helm.smartdoc.editor.operation.Operation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OperationQueue {
    private BlockingQueue<Operation> queue = new LinkedBlockingQueue<>();

    public void addOperation(Operation operation) {
        queue.add(operation);
    }

    public Operation takeOperation() throws InterruptedException {
        return queue.take();
    }
}