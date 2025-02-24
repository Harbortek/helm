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

package com.harbortek.helm.tracker.entity.code;

import com.harbortek.helm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;
import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper=true)
@FieldNameConstants
@NoArgsConstructor
@Table(value = "tracker_commits")
public class TrackerCommitEntity extends BaseEntity {
    Long projectId;
    Long trackerId;
    Long itemId;

    Long repositoryId;
    String repositoryType;

    String commitId;
    String commitMessage;
    String committer;
    Date commitOn;
    String projectNameOfRepository;
    String branch;
    String status;

    private List<String> added;
    private List<String> modified;
    private List<String> removed;

    String projectUrl;
    String commitUrl;

}
