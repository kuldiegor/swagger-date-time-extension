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

import io.swagger.v3.core.jackson.TypeNameResolver;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class QualifiedTypeNameResolver extends TypeNameResolver {
    @Override
    protected String nameForClass(Class<?> cls, Set<Options> options) {
        String className = cls.getName().startsWith("java.") ? cls.getSimpleName() : cls.getName();
        if (options.contains(Options.SKIP_API_MODEL)) {
            return className;
        }
        final io.swagger.v3.oas.annotations.media.Schema model = cls.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
        final String modelName = model == null ? null : StringUtils.trimToNull(model.name());
        return modelName == null ? className : modelName;
    }
}
