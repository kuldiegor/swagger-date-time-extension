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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class ModelDeserializer extends JsonDeserializer<Schema> {
    @Override
    public Schema deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode allOf = node.get("allOf");
        JsonNode anyOf = node.get("anyOf");
        JsonNode oneOf = node.get("oneOf");

        Schema schema = null;

        if (allOf != null || anyOf != null || oneOf != null) {

            return Json.mapper().convertValue(node, ComposedSchema.class);

        } else {

            JsonNode type = node.get("type");
            String format = node.get("format") == null ? "" : node.get("format").textValue();

            if (type != null && "array".equals(((TextNode) type).textValue())) {
                schema = Json.mapper().convertValue(node, ArraySchema.class);
            } else if (type != null) {
                if (type.textValue().equals("integer")) {
                    schema = Json.mapper().convertValue(node, IntegerSchema.class);
                    if (StringUtils.isBlank(format)) {
                        schema.setFormat(null);
                    }
                } else if (type.textValue().equals("number")) {
                    schema = Json.mapper().convertValue(node, NumberSchema.class);
                } else if (type.textValue().equals("boolean")) {
                    schema = Json.mapper().convertValue(node, BooleanSchema.class);
                } else if (type.textValue().equals("string")) {
                    if ("date".equals(format)) {
                        schema = Json.mapper().convertValue(node, StringSchema.class);
                    } else if ("date-time".equals(format)) {
                        schema = Json.mapper().convertValue(node, StringSchema.class);
                    } else if ("email".equals(format)) {
                        schema = Json.mapper().convertValue(node, EmailSchema.class);
                    } else if ("password".equals(format)) {
                        schema = Json.mapper().convertValue(node, PasswordSchema.class);
                    } else if ("uuid".equals(format)) {
                        schema = Json.mapper().convertValue(node, UUIDSchema.class);
                    } else {
                        schema = Json.mapper().convertValue(node, StringSchema.class);
                    }
                } else if (type.textValue().equals("object")) {
                    schema = deserializeObjectSchema(node);
                }
            } else if (node.get("$ref") != null) {
                schema = new Schema().$ref(node.get("$ref").asText());
            } else { // assume object
                schema = deserializeObjectSchema(node);
            }
        }

        return schema;
    }

    private Schema deserializeObjectSchema(JsonNode node) {
        JsonNode additionalProperties = node.get("additionalProperties");
        Schema schema = null;
        if (additionalProperties != null) {
            // try first to convert to Schema, if it fails it must be a boolean
            try {
                Schema innerSchema = Json.mapper().convertValue(additionalProperties, Schema.class);
                ((ObjectNode)node).remove("additionalProperties");
                MapSchema ms = Json.mapper().convertValue(node, MapSchema.class);
                ms.setAdditionalProperties(innerSchema);
                schema = ms;
            } catch (Exception e) {
                Boolean additionalPropsBoolean = Json.mapper().convertValue(additionalProperties, Boolean.class);
                if (additionalPropsBoolean) {
                    schema = Json.mapper().convertValue(node, MapSchema.class);
                } else {
                    schema = Json.mapper().convertValue(node, ObjectSchema.class);
                }
                schema.setAdditionalProperties(additionalPropsBoolean);
            }

        } else {
            schema = Json.mapper().convertValue(node, ObjectSchema.class);
        }
        return schema;
    }
}
