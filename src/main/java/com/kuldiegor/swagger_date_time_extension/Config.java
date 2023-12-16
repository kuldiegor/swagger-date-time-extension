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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    private void configureModules(DateTimeModelResolver dateTimeModelResolver) {
        if (dateTimeModelResolver==null){
            ObjectMapper objectMapper = Json.mapper();
            dateTimeModelResolver = new DateTimeModelResolver(objectMapper,new QualifiedTypeNameResolver());
        }
        ObjectMapper objectMapper = Json.mapper();
        objectMapper.registerModule(new DeserializationModule());
        ModelConverters modelConverters = ModelConverters.getInstance();
        modelConverters.addConverter(dateTimeModelResolver);
    }

    @Bean
    @ConditionalOnBean(value = {OpenAPI.class, DateTimeModelResolver.class})
    public EmptyBean configureModulesWithOpenAPIAndResolver(DateTimeModelResolver dateTimeModelResolver){
        configureModules(dateTimeModelResolver);
        return EmptyBean.OpenAPIAndResolver;
    }

    @Bean
    @ConditionalOnBean(value = {OpenAPI.class})
    @ConditionalOnMissingBean(value = {DateTimeModelResolver.class})
    public EmptyBean configureModulesWithOpenAPIAndWithoutResolver(){
        configureModules(null);
        return EmptyBean.OpenAPIAndWithoutResolver;
    }

    @Bean
    @ConditionalOnBean(value = {DateTimeModelResolver.class})
    @ConditionalOnMissingBean(value = {OpenAPI.class})
    public EmptyBean configureModulesWithResolverAndWithoutOpenAPI(DateTimeModelResolver dateTimeModelResolver){
        configureModules(dateTimeModelResolver);
        return EmptyBean.ResolverAndWithoutOpenAPI;
    }

    @Bean
    @ConditionalOnMissingBean(value = {OpenAPI.class, DateTimeModelResolver.class})
    public EmptyBean configureModulesWithoutOpenAPIAndResolver(){
        configureModules(null);
        return EmptyBean.WithoutOpenAPIAndResolver;
    }
}
