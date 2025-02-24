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

package com.harbortek.helm.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Component  //声明组件
@Aspect //  声明切面
@ComponentScan  //组件自动扫描
@EnableAspectJAutoProxy //spring自动切换JDK动态代理和CGLIB
@Slf4j
public class MethodExecuteLogAop {

    /**
     *自定义日志
     */

    /**
     * 打印类method的名称以及参数
     * @param point 切面
     */
    public void printMethodParams(JoinPoint point){
        if(point == null){
            return;
        }
        /**
         * Signature 包含了方法名、申明类型以及地址等信息
         */
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        //重新定义日志
//        Logger logger = LoggerFactory.getLogger(point.getTarget().getClass());
//        logger.info("Executing {}.{} ",className,methodName);
    }

    /**
     * <li>Before       : 在方法执行前进行切面</li>
     * <li>execution    : 定义切面表达式</li>
     * <p>public * com.eparty.ccp.*.impl..*.*(..)
     *      <li>public :匹配所有目标类的public方法，不写则匹配所有访问权限</li>
     *      <li>第一个* :方法返回值类型，*代表所有类型 </li>
     *      <li>第二个* :包路径的通配符</li>
     *      <li>第三个..* :表示impl这个目录下所有的类，包括子目录的类</li>
     *      <li>第四个*(..) : *表示所有任意方法名,..表示任意参数</li>
     * </p>
     * @param point 切面
     */
    @Before("execution(public * com.harbortek.helm.*.dao..*.*(..))")
    public void before(JoinPoint point) {
        this.printMethodParams(point);
    }
}
