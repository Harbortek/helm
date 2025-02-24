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

package com.harbortek.helm.pipeline.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SuperBuilder
@Data
@EqualsAndHashCode
@NoArgsConstructor
@FieldNameConstants
public class BuildInfo {
    List<Action> actions;

    boolean building;

    String description;

    String displayName;

    long duration;

    long estimatedDuration;

    String fullDisplayName;

    String id;

    boolean keepLog;

    int number;

    int queueId;

    String result;

    Date timestamp;

    String url;

    String builtOn;

    public String getBuildUser(){
        if (actions==null){
            return null;
        }
        Optional<Action> causeAction = getActions().stream().filter(action -> { return ("hudson" +
                ".model" +
                ".CauseAction").equals(action.get_class());}).findFirst();
        if (causeAction.isPresent()){
            Optional<Cause> userCause =
                    causeAction.get().getCauses().stream().filter(cause -> { return cause.getShortDescription().startsWith(
                            "Started by");}).findFirst();
            return userCause.map(Cause::getUserName).orElse(null);
        }
        return null;
    }
}
