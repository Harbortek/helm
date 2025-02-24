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

package com.harbortek.helm.tracker.service;

import com.harbortek.helm.tracker.vo.plan.SprintVo;

import java.util.Collection;

public interface SprintService {
    Collection<SprintVo> findSprints(Long projectId);

    SprintVo findOneSprint(Long id);

    SprintVo createSprint(SprintVo sprintVo);

    SprintVo updateSprint(SprintVo sprintVo);

    void deleteSprint(Long id);

    SprintVo convertSprint(SprintVo sprintVo);

    Collection<SprintVo> findUnPlanedSprints(Long projectId);
}
