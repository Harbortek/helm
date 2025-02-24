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

package com.harbortek.helm.tracker.constants;

public interface PlanDependencyTypes {
    String START_START = "SS";
    String START_FINISH = "SF";

    String FINISH_FINISH = "FF";
    String FINISH_START = "FS";

    static int map(String type){
        if (FINISH_START.equals(type)){
            return 0;
        }else if (START_START.equals(type)){
            return 1;
        }else if (FINISH_FINISH.equals(type)){
            return 2;
        }else if (START_FINISH.equals(type)){
            return 3;
        }
        return -1;
    }
}
