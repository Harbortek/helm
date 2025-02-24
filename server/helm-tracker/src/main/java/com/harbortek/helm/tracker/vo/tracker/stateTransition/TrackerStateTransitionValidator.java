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

package com.harbortek.helm.tracker.vo.tracker.stateTransition;

import com.harbortek.helm.tracker.entity.tracker.TrackerEntity;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import lombok.Data;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Data
public class TrackerStateTransitionValidator {

    String name;

    String expressionEL;

    String errorMessage;

    public Boolean validate(TrackerItemEntity item, TrackerEntity tracker) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(getExpressionEL());
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("item",item );
        context.setVariable("tracker",tracker );
        return expression.getValue(context, Boolean.class);
    }
}
