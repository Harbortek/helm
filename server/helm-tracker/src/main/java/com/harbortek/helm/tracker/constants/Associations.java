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

public enum Associations {


    PARENT("parent", "has parent", "is parent of"),

    IMPLEMENTS("implements", "implements", "is implemented by"),


    REFINES("refines", "refines", "is refined by"),

    DEPENDS_ON("depends_on", "depends on", "blocks"),

    DUPLICATES("duplicates", "duplicates", "is duplicated by"),
    FOLLOW_UP("follow_up", "has follow-up", "follows"),
    VERIFIES("verifies", "verifies", "is verified by"),
    ASSESSES("assesses", "assesses", "is assessed by"),
    TRIGGERED_BY("triggered_by", "is triggered by", "triggers"),
    MITIGATES("mitigates", "mitigates", "is mitigated by"),
    USES("uses", "uses", "is used by"),
    AFFECTS("affects", "affects", "is affected by"),
    TRACKS("tracks", "tracks", "is tracked by"),
    DERIVED_FROM("derived_from", "derived from", "is derived by"),

    BRANCHED_FROM("branched_from", "is branched from", "has branch"),
    RELATES_TO("relates_to", "relates to", "is related to");


    String id;
    String name;
    String oppositeName;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOppositeName() {
        return oppositeName;
    }

    Associations(String id, String name, String oppositeName) {
        this.id = id;
        this.name = name;
        this.oppositeName = oppositeName;
    }
}
