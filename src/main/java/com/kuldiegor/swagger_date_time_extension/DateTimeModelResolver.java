/*
    Copyright 2023 Dmitrij Kulabuhov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.kuldiegor.swagger_date_time_extension;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;

import java.lang.annotation.Annotation;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;

public class DateTimeModelResolver extends ModelResolver {
    public DateTimeModelResolver(ObjectMapper mapper) {
        super(mapper, new QualifiedTypeNameResolver());
    }

    @Override
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (type.isSchemaProperty()) {
            JavaType _type = Json.mapper().constructType(type.getType());
            if (_type != null) {
                Class<?> cls = _type.getRawClass();
                if (cls == ZonedDateTime.class) {
                    return instanceDateTimeSchema(type, cls);
                } else if (cls == OffsetDateTime.class) {
                    return instanceDateTimeSchema(type, cls);
                } else if (cls == LocalDateTime.class) {
                    return instanceDateTimeSchema(type, cls);
                } else if (cls == LocalDate.class) {
                    return instanceDateSchema(type, cls);
                } else if (cls == LocalTime.class) {
                    return instanceDateTimeSchema(type, cls);
                }
            }
        }
        if (chain.hasNext()) {
            return chain.next().resolve(type, context, chain);
        } else {
            return null;
        }
    }

    private Schema<String> instanceDateTimeSchema(AnnotatedType type, Class<?> cls) {
        Schema<String> schema = new Schema<>();
        schema.type("string")
                .format("date-time");
        super.resolveSchemaMembers(schema, type);
        if (schema.getPattern() == null) {
            schema.setPattern(resolveJavaTimePatternByAnnotationPattern(type.getCtxAnnotations()));
        }
        if (schema.getExample() == null) {
            schema.setExample(resolveJavaTimeExampleByAnnotationPattern(cls, type.getCtxAnnotations()));
        }
        return schema;
    }

    private Schema<String> instanceDateSchema(AnnotatedType type, Class<?> cls) {
        Schema<String> schema = instanceDateTimeSchema(type,cls);
        schema.type("string")
                .format("date");
        return schema;
    }

    private String resolveJavaTimePatternByAnnotationPattern(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .filter(it -> it instanceof JsonFormat)
                .findFirst()
                .map(it -> (JsonFormat) it)
                .map(JsonFormat::pattern)
                .orElse(null);
    }

    protected String resolveJavaTimeExampleByAnnotationPattern(Class<?> cls, Annotation[] annotations) {
        ZonedDateTime exampleZonedDateTime = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("Europe/London")).withHour(1).withMinute(1).withSecond(1).withNano(1001001);
        String pattern = resolveJavaTimePatternByAnnotationPattern(annotations);
        if (pattern == null) {
            if (cls == OffsetDateTime.class) {
                return exampleZonedDateTime.toOffsetDateTime().toString();
            } else if (cls == LocalDateTime.class) {
                return exampleZonedDateTime.toLocalDateTime().toString();
            } else if (cls == LocalDate.class) {
                return exampleZonedDateTime.toLocalDateTime().toLocalDate().toString();
            } else if (cls == LocalTime.class) {
                return exampleZonedDateTime.toLocalDateTime().toLocalTime().toString();
            } else {
                return exampleZonedDateTime.toString();
            }
        }
        try {
            return DateTimeFormatter.ofPattern(pattern).format(exampleZonedDateTime);
        } catch (DateTimeException e) {
            return exampleZonedDateTime.toString();
        }
    }
}
