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

package com.harbortek.helm.tracker.util;

import com.harbortek.helm.common.entity.BaseEntity;
import com.harbortek.helm.common.vo.SpecialRole;
import com.harbortek.helm.system.vo.TrackerSpecialRole;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.vo.view.FilterCondition;
import com.harbortek.helm.tracker.vo.view.ConditionGroup;
import com.harbortek.helm.tracker.vo.view.ObjectFilter;
import com.harbortek.helm.util.DateUtils;
import com.harbortek.helm.util.ObjectUtils;
import com.harbortek.helm.util.SQLUtils;
import com.harbortek.helm.util.SecurityUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.springframework.data.relational.core.query.Criteria;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.noCondition;

public class FilterUtils {
    public static Criteria getCriteria(ObjectFilter filter) {
        List<ConditionGroup> groups = filter.getConditionGroups();
        Criteria allGroup = Criteria.empty();
        for (ConditionGroup group : groups) {
            Criteria oneGroup = Criteria.empty();
            for (FilterCondition filterCondition : group.getConditions()) {
                Criteria  criteria =  conditionToCriteria(filterCondition);
                if(criteria != null){
                    oneGroup =  oneGroup.and(criteria);
                }
            }
            if (!oneGroup.isEmpty()) {
                allGroup=allGroup.or(oneGroup);
            }
        }

        Criteria groupCriteria = Criteria.empty();
        if (filter.getGroupCondition().getField() != null) {//分组
            if (filter.getGroupCondition().getValue() != null) {
                groupCriteria = groupCriteria.and(Criteria.where(filter.getGroupCondition().getField())
                        .is(Long.parseLong(filter.getGroupCondition().getValue().toString())));
            } else {//未设置
                groupCriteria = groupCriteria.and(Criteria.where(filter.getGroupCondition().getField()).isNull());
            }
        } else if (filter.getCalendarCondition().getField() != null) {
            if (filter.getCalendarCondition().getValue() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                try {
                    Date currentData = sdf.parse(filter.getCalendarCondition().getValue().toString());
                    Date endDate = sdf.parse(sdf.format(DateUtils.addMonth(currentData, 1)));
                    groupCriteria = groupCriteria.and(Criteria.where(filter.getCalendarCondition().getField()).greaterThanOrEquals(currentData)
                            .and(Criteria.where(filter.getCalendarCondition().getField())
                                    .lessThanOrEquals(endDate)));

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return allGroup.and(groupCriteria);
    }

    private static Criteria conditionToCriteria(FilterCondition filterCondition) {
        Criteria criteria = null;
        if (filterCondition.getField() == null) {
            return criteria;
        }
        if (filterCondition.getValue() == null && !filterCondition.getOperator().equals("NN")
                && !filterCondition.getOperator().equals("NULL")) {
            return criteria;
        }
        if (filterCondition.getField().equals("status")) {
            filterCondition.setField(TrackerItemEntity.Fields.statusId);
        }else if (filterCondition.getField().equals("priority")) {
            filterCondition.setField(TrackerItemEntity.Fields.priorityId);
        }else if (filterCondition.getField().equals("meaning")) {
            filterCondition.setField(TrackerItemEntity.Fields.meaningId);
        }
        if (filterCondition.getType().equals("DATE")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                List<Date> dateList = new ArrayList<>();
                if (!filterCondition.getOperator().equals("BETWEEN")) {
                    Date currentData = sdf.parse(filterCondition.getValue().toString());
                    if (filterCondition.getOperator().equals("EQ")) {
                        dateList.add(currentData);
                        Date endDate = new Date(currentData.getTime() + 24 * 60 * 60 * 1000);
                        dateList.add(endDate);
                        filterCondition.setValue(dateList);
                        filterCondition.setOperator("BETWEEN");
                    } else if (filterCondition.getOperator().equals("NEQ")) {
                        Date endDate = new Date(currentData.getTime() + 24 * 60 * 60 * 1000);
                        dateList.add(endDate);
                        dateList.add(currentData);
                        filterCondition.setValue(dateList);
                    } else if (filterCondition.getOperator().equals("LEQ")) {
                        Date endDate = new Date(currentData.getTime() + 24 * 60 * 60 * 1000);
                        filterCondition.setValue(endDate);
                    } else {
                        filterCondition.setValue(sdf.parse(filterCondition.getValue().toString()));
                    }
                } else {
                    Object[] objects = ((ArrayList) filterCondition.getValue()).toArray();
                    dateList.add(sdf.parse(objects[0].toString()));
                    Date endDate =
                            new Date(sdf.parse(objects[1].toString()).getTime() + 24 * 60 * 60 * 1000);
                    dateList.add(endDate);
                    filterCondition.setValue(dateList);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        if (filterCondition.getOperator().equals("INCL")) {
            if ((filterCondition.getType().equals("USER")&&!"watchers".equals(filterCondition.getField())) ||
                    filterCondition.getType().equals("STATUS") ||
                    filterCondition.getType().equals("OPTIONS") || filterCondition.getType().equals("WORK_ITEM") ||
                    filterCondition.getType().equals("WORK_ITEM_TYPE") ||
                    filterCondition.getType().equals("STATUS_TYPE")) {
                if (((ArrayList) filterCondition.getValue()).size() == 0) {
                    return criteria;
                }
                List<Long> longs = new ArrayList<>();
                ((ArrayList) filterCondition.getValue()).forEach(item -> {
                    if(ObjectUtils.isNotEmpty(item)){
                        longs.add(Long.parseLong((String) item));
                    }
                });
                criteria = Criteria.where(filterCondition.getField()).in(longs);
            } else {
                criteria = Criteria.where(filterCondition.getField()).like("%"+ filterCondition.getValue().toString()+"%");
            }
        } else if (filterCondition.getOperator().equals("EXCL")) {
            if ((filterCondition.getType().equals("USER")&&!"watchers".equals(filterCondition.getField())) ||
                    filterCondition.getType().equals("STATUS") ||
                    filterCondition.getType().equals("OPTIONS") || filterCondition.getType().equals("WORK_ITEM") ||
                    filterCondition.getType().equals("WORK_ITEM_TYPE") ||
                    filterCondition.getType().equals("STATUS_TYPE")) {
                if (((ArrayList) filterCondition.getValue()).size() == 0) {
                    return criteria;
                }
                List<Long> longs = new ArrayList<>();
                ((ArrayList) filterCondition.getValue()).forEach(item -> {
                    longs.add(Long.parseLong(item.toString()));
                });
                criteria = Criteria.where(filterCondition.getField()).in(longs);
            } else {
                criteria = Criteria.where(filterCondition.getField())
                                   .is(Pattern.compile("^((?!" + filterCondition.getValue() + ").)*$",
                                                       Pattern.CASE_INSENSITIVE));
            }
        } else if (filterCondition.getOperator().equals("EQ")) {
            criteria = Criteria.where(filterCondition.getField()).is(filterCondition.getValue());
        } else if (filterCondition.getOperator().equals("NEQ")) {
            if (filterCondition.getType().equals("DATE")) {
                Object[] objects = ((ArrayList) filterCondition.getValue()).toArray();
                criteria = Criteria.where(filterCondition.getField()).greaterThanOrEquals(objects[0]).or(
                                    Criteria.where(filterCondition.getField()).lessThanOrEquals(objects[1]));
            } else {
                criteria = Criteria.where(filterCondition.getField()).not(filterCondition.getValue());
            }
        } else if (filterCondition.getOperator().equals("GEQ")) {
            criteria = Criteria.where(filterCondition.getField()).greaterThanOrEquals(filterCondition.getValue());
        } else if (filterCondition.getOperator().equals("LEQ")) {
            criteria = Criteria.where(filterCondition.getField()).lessThanOrEquals(filterCondition.getValue());
        } else if (filterCondition.getOperator().equals("BETWEEN")) {
            Object[] objects = ((ArrayList) filterCondition.getValue()).toArray();
            criteria = Criteria.where(filterCondition.getField()).between(objects[0], objects[1]);
        } else if (filterCondition.getOperator().equals("NULL")) {
            criteria = Criteria.where(filterCondition.getField()).is("").or(
                                Criteria.where(filterCondition.getField()).isNull());
        } else if (filterCondition.getOperator().equals("NN")) {
            criteria = Criteria.where(filterCondition.getField()).not("").and(Criteria.where(filterCondition.getField()).isNotNull());
        }
        return criteria;
    }

    public static Condition getCondition(ObjectFilter filter) {
        List<ConditionGroup> groups = filter.getConditionGroups();
        Condition allGroup = noCondition();
        for (ConditionGroup group : groups) {
            Condition oneGroup = noCondition();
            boolean flag=false;
            for (FilterCondition filterCondition : group.getConditions()) {
                Condition  condition =  filterConditionToCondition(filterCondition);
                if(condition != null){
                    oneGroup =  oneGroup.and(condition);
                    flag=true;
                }
            }
            if (flag) {
                allGroup=allGroup.or(oneGroup);
            }
        }

        Condition groupCondition = noCondition();
        if (filter.getGroupCondition().getField() != null) {//分组
            if (filter.getGroupCondition().getValue() != null) {
                groupCondition = groupCondition.and(getField(filter.getGroupCondition().getField())
                        .eq(Long.parseLong(filter.getGroupCondition().getValue().toString())));
            } else {//未设置
                groupCondition = groupCondition.and(getField(filter.getGroupCondition().getField()).isNull());
            }
        } else if (filter.getCalendarCondition().getField() != null) {
            if (filter.getCalendarCondition().getValue() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date currentData = sdf.parse(filter.getCalendarCondition().getValue().toString());
                    Date endDate = sdf.parse(sdf.format(DateUtils.addMonth(currentData, 1)));
                    groupCondition = groupCondition.and(getField(filter.getCalendarCondition().getField()).greaterOrEqual(format.format(currentData))
                            .and(getField(filter.getCalendarCondition().getField())
                                    .lessOrEqual(format.format(endDate))));

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return allGroup.and(groupCondition);
    }

    private static Condition filterConditionToCondition(FilterCondition filterCondition) {
        Condition condition = noCondition();
        if (filterCondition.getField() == null) {
            return condition;
        }
        if (filterCondition.getValue() == null && !filterCondition.getOperator().equals("NN")
                && !filterCondition.getOperator().equals("NULL")) {
            return condition;
        }
        switch (filterCondition.getField()) {
            case "status" -> filterCondition.setField(TrackerItemEntity.Fields.statusId);
            case "priority" -> filterCondition.setField(TrackerItemEntity.Fields.priorityId);
            case "meaning" -> filterCondition.setField(TrackerItemEntity.Fields.meaningId);
        }
        if (filterCondition.getType().equals("DATE")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                List<String> dateList = new ArrayList<>();
                if (!filterCondition.getOperator().equals("BETWEEN")) {
                    Date currentData = sdf.parse(filterCondition.getValue().toString());
                    if (filterCondition.getOperator().equals("EQ")) {
                        dateList.add(formatter.format(currentData));
                        Date endDate = new Date(currentData.getTime() + 24 * 60 * 60 * 1000);
                        dateList.add(formatter.format(endDate));
                        filterCondition.setValue(dateList);
                        filterCondition.setOperator("BETWEEN");
                    } else if (filterCondition.getOperator().equals("NEQ")) {
                        Date endDate = new Date(currentData.getTime() + 24 * 60 * 60 * 1000);
                        dateList.add(formatter.format(endDate));
                        dateList.add(formatter.format(currentData));
                        filterCondition.setValue(dateList);
                    } else if (filterCondition.getOperator().equals("LEQ")) {
                        Date endDate = new Date(currentData.getTime() + 24 * 60 * 60 * 1000);
                        filterCondition.setValue(formatter.format(endDate));
                    } else {
                        filterCondition.setValue(formatter.format(sdf.parse(filterCondition.getValue().toString())));
                    }
                } else {
                    Object[] objects = ((ArrayList) filterCondition.getValue()).toArray();
                    dateList.add(formatter.format(sdf.parse(objects[0].toString())));
                    Date endDate =
                            new Date(sdf.parse(objects[1].toString()).getTime() + 24 * 60 * 60 * 1000);
                    dateList.add(formatter.format(endDate));
                    filterCondition.setValue(dateList);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        if (filterCondition.getOperator().equals("INCL")) {
            if ((filterCondition.getType().equals("USER")&&!"watchers".equals(filterCondition.getField())) ||
                    filterCondition.getType().equals("STATUS") ||
                    filterCondition.getType().equals("OPTIONS") || filterCondition.getType().equals("WORK_ITEM") ||
                    filterCondition.getType().equals("WORK_ITEM_TYPE") ||
                    filterCondition.getType().equals("STATUS_TYPE")) {
                if (((ArrayList) filterCondition.getValue()).isEmpty()) {
                    return condition;
                }
                List<Long> longs = new ArrayList<>();
                ((ArrayList) filterCondition.getValue()).forEach(item -> {
                    longs.add(Long.parseLong(item.toString()));
                });
                condition = getField(filterCondition.getField()).in(longs);
            }else if(filterCondition.getType().equals("USER")){
                if (((ArrayList) filterCondition.getValue()).isEmpty()) {
                    return condition;
                }
                for (Object item : (ArrayList) filterCondition.getValue()) {
                    condition=condition.or(getField(filterCondition.getField()).like("%"+ item.toString()+"%"));
                }
            }else {
                condition = getField(filterCondition.getField()).like("%"+ filterCondition.getValue().toString()+"%");
            }
        } else if (filterCondition.getOperator().equals("EXCL")) {
            if ((filterCondition.getType().equals("USER")&&!"watchers".equals(filterCondition.getField())) || //屏蔽watchers
                    filterCondition.getType().equals("STATUS") ||
                    filterCondition.getType().equals("OPTIONS") || filterCondition.getType().equals("WORK_ITEM") ||
                    filterCondition.getType().equals("WORK_ITEM_TYPE") ||
                    filterCondition.getType().equals("STATUS_TYPE")) {
                if (((ArrayList) filterCondition.getValue()).isEmpty()) {
                    return condition;
                }
                List<Long> longs = new ArrayList<>();
                ((ArrayList) filterCondition.getValue()).forEach(item -> {
                    longs.add(Long.parseLong(item.toString()));
                });
                condition = getField(filterCondition.getField()).notIn(longs);
            } else if(filterCondition.getType().equals("USER")){
                if (((ArrayList) filterCondition.getValue()).isEmpty()) {
                    return condition;
                }
                for (Object item : (ArrayList) filterCondition.getValue()) {
                    condition=condition.and(getField(filterCondition.getField())
                            .notLike("%"+ item.toString()+"%"));
                }
            } else {
                condition = getField(filterCondition.getField())
                        .notLike("%"+ filterCondition.getValue().toString()+"%");
            }
        } else if (filterCondition.getOperator().equals("EQ")) {
            condition = getField(filterCondition.getField()).eq(filterCondition.getValue());
        } else if (filterCondition.getOperator().equals("NEQ")) {
            if (filterCondition.getType().equals("DATE")) {
                Object[] objects = ((ArrayList) filterCondition.getValue()).toArray();
                condition = getField(filterCondition.getField()).greaterOrEqual(objects[0]).or(
                        getField(filterCondition.getField()).lessOrEqual(objects[1]));
            } else {
                condition = getField(filterCondition.getField()).ne(filterCondition.getValue());
            }
        } else if (filterCondition.getOperator().equals("GEQ")) {
            condition = getField(filterCondition.getField()).greaterOrEqual(filterCondition.getValue());
        } else if (filterCondition.getOperator().equals("LEQ")) {
            condition = getField(filterCondition.getField()).lessOrEqual(filterCondition.getValue());
        } else if (filterCondition.getOperator().equals("BETWEEN")) {
            Object[] objects = ((ArrayList) filterCondition.getValue()).toArray();
            condition = getField(filterCondition.getField()).between(objects[0], objects[1]);
        } else if (filterCondition.getOperator().equals("NULL")) {
            condition = getField(filterCondition.getField()).eq("").or(
                    getField(filterCondition.getField()).isNull());
        } else if (filterCondition.getOperator().equals("NN")) {
            condition = getField(filterCondition.getField()).ne("").and(getField(filterCondition.getField()).isNotNull());
        }
        return condition;
    }

    public static Criteria getPermCriteria(List<Long> trackerIdsPerm, List<TrackerSpecialRole> specialRoles, Criteria criteria){
        Long userId = SecurityUtils.getCurrentUser().getId();

        criteria= criteria.and(Criteria.where(TrackerItemEntity.Fields.trackerId).in(trackerIdsPerm));
        Criteria specialRoleCriteria = Criteria.empty();
        for (TrackerSpecialRole special : specialRoles) {
            if (SpecialRole.TRACKER_OWNER.equals(special.getSpecialRoleType())) {
                specialRoleCriteria= specialRoleCriteria.and(
                        Criteria.where(TrackerItemEntity.Fields.trackerId).is(special.getTrackerId())
                                .and(Criteria.where(TrackerItemEntity.Fields.ownerId)
                                        .is(userId)));
            } else if (SpecialRole.TRACKER_CREATOR.equals(special.getSpecialRoleType())) {
                specialRoleCriteria= specialRoleCriteria.and(
                        Criteria.where(TrackerItemEntity.Fields.trackerId).is(special.getTrackerId())
                                .and(Criteria.where(BaseEntity.Fields.createBy)
                                        .is(userId)));
            } else if (SpecialRole.TRACKER_WATCHER.equals(special.getSpecialRoleType())) {
                specialRoleCriteria= specialRoleCriteria.and(
                        Criteria.where(TrackerItemEntity.Fields.trackerId).is(special.getTrackerId())
                                .and(Criteria.where(
                                                TrackerItemEntity.Fields.watchers + ".referTo._id")
                                        .is(userId)));
            }
        }
        return criteria.or(specialRoleCriteria);
    }

    public static Condition getPermCondition(List<Long> trackerIdsPerm, List<TrackerSpecialRole> specialRoles){
        Long userId = SecurityUtils.getCurrentUser().getId();

        Condition condition=getField(TrackerItemEntity.Fields.trackerId).in(trackerIdsPerm);
        Condition specialRoleCondition =noCondition();
        for (TrackerSpecialRole special : specialRoles) {
            if (SpecialRole.TRACKER_OWNER.equals(special.getSpecialRoleType())) {
                specialRoleCondition= specialRoleCondition.and(
                        getField(TrackerItemEntity.Fields.trackerId).eq(special.getTrackerId())
                                .and(getField(TrackerItemEntity.Fields.ownerId)
                                        .eq(userId)));
            } else if (SpecialRole.TRACKER_CREATOR.equals(special.getSpecialRoleType())) {
                specialRoleCondition= specialRoleCondition.and(
                        getField(TrackerItemEntity.Fields.trackerId).eq(special.getTrackerId())
                                .and(getField(BaseEntity.Fields.createBy)
                                        .eq(userId)));
            } else if (SpecialRole.TRACKER_WATCHER.equals(special.getSpecialRoleType())) {
                specialRoleCondition= specialRoleCondition.and(
                        getField(TrackerItemEntity.Fields.trackerId).eq(special.getTrackerId())
                                .and(getField(
                                                TrackerItemEntity.Fields.watchers + ".referTo._id")
                                        .eq(userId)));
            }
        }
        return condition.or(specialRoleCondition);
    }


    public static Field<Object> getField(String fieldName){
        return field(SQLUtils.camelCaseToUnderScore(fieldName));
    }
}
