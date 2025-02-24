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

package com.harbortek.helm.common.config;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.harbortek.helm.common.constants.CacheConstants;
import com.harbortek.helm.common.entity.BaseEntity;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
@ConditionalOnExpression("'${spring.cache.type}'.equals('redis')")
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //设置不同cacheName的过期时间
        Map<String, RedisCacheConfiguration> configurations = new HashMap<>(16);
        // 序列化方式
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = getJsonRedisSerializer();
        RedisSerializationContext.SerializationPair<Object> serializationPair =
                RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer);

        // 默认的缓存配置
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                                                                                 .entryTtl(Duration.ofSeconds(3600L))
                                                                                 .serializeValuesWith(
                                                                                         serializationPair);
        // 自定义用户模块的缓存配置 自定义的配置可以覆盖默认配置（当前的模块）
        configurations.put(CacheConstants.OBJECT_CACHE_NAME, RedisCacheConfiguration.defaultCacheConfig()
                                                                                    .entryTtl(Duration.ofSeconds(3660L))
                                                                                    .serializeValuesWith(
                                                                                            serializationPair));

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                                .cacheDefaults(redisCacheConfiguration)
                                .withInitialCacheConfigurations(configurations)
                                // 事物支持
                                .transactionAware()
                                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = getJsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jsonRedisSerializer);
        // 支持事物
        //template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }

    @Bean("entityKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new KeyGenerator(){

            @NotNull
            @Override
            public Object generate(@NotNull Object target, @NotNull Method method, Object... params) {
                if (target instanceof BaseEntity){
                    return target.getClass().getSimpleName() + "_"
                            + ((BaseEntity) target).getId();
                }
                return "";
            }
        };
    }

    /**
     * 设置jackson的序列化方式
     */
    private GenericJackson2JsonRedisSerializer getJsonRedisSerializer() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        Reflections reflections = new Reflections("com.harbortek.helm");
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(JsonTypeName.class);
        classSet.parallelStream().forEach(clazz -> objectMapper.registerSubtypes(clazz));

//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);
        return jackson2JsonRedisSerializer;
    }
}
