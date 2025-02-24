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

package com.harbortek.helm.tracker.entity.plan;

import java.time.LocalDateTime;
import java.util.Date;

public interface Planable {

     Long getProjectId();

     void setProjectId(Long projectId);

     Long getOwnerId();

     void setOwnerId(Long ownerId);

     LocalDateTime getPlanStartDate();

     void setPlanStartDate(LocalDateTime planStartDate);

     LocalDateTime getPlanEndDate();

     void setPlanEndDate(LocalDateTime planEndDate);

     LocalDateTime getRealStartDate();

     void setRealStartDate(LocalDateTime realStartDate);

     LocalDateTime getRealEndDate() ;

     void setRealEndDate(LocalDateTime realEndDate) ;

     Integer getProgress() ;

     void setProgress(Integer progress);
}
