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

package com.harbortek.helm.system.dao;

import com.harbortek.helm.common.dao.BaseJdbcDao;
import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.system.entity.NotificationEntity;
import com.harbortek.helm.util.CacheUtils;
import com.harbortek.helm.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;

import static org.jooq.impl.DSL.noCondition;

@Repository
@Slf4j
public class NotificationDao extends BaseJdbcDao {

    public NotificationEntity send(NotificationEntity notification) {
        notification.setId(IDUtils.getId());
        return save(notification);
    }

    public void readIt(Long notificationId) {
        getDslContext().update(getTable(NotificationEntity.class))
                       .set(getField(NotificationEntity.Fields.read), true)
                       .set(getField(NotificationEntity.Fields.readDate), new Date())
                       .where(getField(BaseEntity.Fields.id).eq(notificationId))
                       .execute();

        CacheUtils.evict(notificationId, NotificationEntity.class);

    }

    public void delete(Long notificationId) {
        markAsDeleted(notificationId, NotificationEntity.class);
    }

    public NotificationEntity findById(Long logId) {
        return findById(logId, NotificationEntity.class);
    }

    public Page<NotificationEntity> findNotifications(Long userId, Boolean showUnread, Boolean showRead,
                                                      Pageable pageable) {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and(Criteria.where(BaseEntity.Fields.deleted).is(false));
        criteria = criteria.and(Criteria.where(NotificationEntity.Fields.receiverId).is(userId));

        if (showUnread && !showRead) {
            criteria = criteria.and(Criteria.where(NotificationEntity.Fields.read).is(Boolean.FALSE));
        } else if (!showUnread && showRead) {
            criteria = criteria.and(Criteria.where(NotificationEntity.Fields.read).is(Boolean.TRUE));
        }

        Query query = Query.query(criteria).with(pageable);
        query.sort(Sort.by(Sort.Direction.DESC, BaseEntity.Fields.createDate));

        return find(query,pageable, NotificationEntity.class);
    }

    public void batchCreate(Collection<NotificationEntity> notificationEntities) {
        if (notificationEntities.isEmpty()) {
            return;
        }

        notificationEntities.forEach(r -> {
            r.setId(IDUtils.getId());
        });
        saveAll(notificationEntities);
    }

    public void readAllNotification(Long userId) {
        Condition condition = noCondition();
        condition = condition.and(getField(NotificationEntity.Fields.receiverId).eq(userId));
        condition = condition.and(getField(NotificationEntity.Fields.read).eq(false));
        getDslContext().update(getTable(NotificationEntity.class))
                       .set(getField(NotificationEntity.Fields.read), true)
                       .set(getField(NotificationEntity.Fields.readDate), new Date())
                       .where(condition)
                       .execute();
        CacheUtils.evictAll(NotificationEntity.class);

    }
}
