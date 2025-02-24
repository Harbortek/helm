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

package com.harbortek.helm.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class JsonUtils {

    static ObjectMapper objectMapper;


    public static String toJSONString(Object object) {
        ObjectMapper objectMapper = getObjectMapper();

        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static <T> T toObject(String jsonString, Class<T> claz) {
        ObjectMapper objectMapper = getObjectMapper();


        try {
            return objectMapper.readValue(jsonString, claz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> toList(String jsonString, Class<T> claz) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, claz);
            return objectMapper.readValue(jsonString, listType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object toMap(String jsonString, Class<?> k, Class<?> v) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            MapType mapType = objectMapper.getTypeFactory().constructMapType(HashMap.class, k, v);
            return objectMapper.readValue(jsonString, mapType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();

            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            //设置日期格式
            SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            objectMapper.setDateFormat(smt);

            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
                    DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATETIME_FORMAT)));
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT)));
            javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_TIME_FORMAT)));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATETIME_FORMAT)));
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT)));
            javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DateUtils.DEFAULT_TIME_FORMAT)));
            objectMapper.registerModule(javaTimeModule).registerModule(new ParameterNamesModule());

            Reflections reflections = new Reflections("com.harbortek.helm");
            Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(JsonTypeName.class);
            classSet.forEach(objectMapper::registerSubtypes);

            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);


            simpleModule.addSerializer(JSONNull.class, new JsonSerializer<JSONNull>() {
                @Override
                public void serialize(JSONNull jsonNull, JsonGenerator jsonGenerator
                        , SerializerProvider serializerProvider) throws IOException {
                    jsonGenerator.writeNull();
                }
            });
            simpleModule.addDeserializer(JSONNull.class, new JsonDeserializer<JSONNull>() {
                @Override
                public JSONNull deserialize(JsonParser jsonParser
                        , DeserializationContext deserializationContext) {
                    return null;
                }
            });
            objectMapper.registerModule(simpleModule);
        }
        return objectMapper;
    }

    public static JSONObject parseObject(String content) {
        return JSONUtil.parseObj(content);
    }

    public static JSONArray parseArray(String content) {
        return JSONUtil.parseArray(content);
    }


}
