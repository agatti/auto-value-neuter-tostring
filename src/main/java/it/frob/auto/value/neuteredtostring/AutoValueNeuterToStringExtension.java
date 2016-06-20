/*
 * Copyright (C) 2016 Alessandro Gatti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.frob.auto.value.neuteredtostring;

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.*;
import com.squareup.javapoet.*;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Auto value extension that neuters {@link #toString()} methods for the classes it creates.
 */
@AutoService(AutoValueExtension.class)
public final class AutoValueNeuterToStringExtension extends AutoValueExtension {

    /**
     * The annotation name that triggers the extension.
     */
    private final static String ANNOTATION_NAME = "NeuteredToString";

    @Override
    public boolean applicable(Context context) {
        for (AnnotationMirror annotation : context.autoValueClass().getAnnotationMirrors()) {
            if (ANNOTATION_NAME.equals(annotation.getAnnotationType().asElement().getSimpleName().toString())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String generateClass(Context context, String className, String classToExtend, boolean isFinal) {
        String packageName = context.packageName();
        Map<String, ExecutableElement> properties = context.properties();

        TypeSpec subclass = TypeSpec.classBuilder(className)
                .addModifiers(isFinal ? Modifier.FINAL : Modifier.ABSTRACT)
                .superclass(ClassName.get(packageName, classToExtend))
                .addMethod(generateConstructor(properties))
                .addMethod(generateToString())
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, subclass).build();
        return javaFile.toString();
    }

    /**
     * Class constructor method generator.
     *
     * @param properties the properties of the class constructor to generate.
     * @return a {@link MethodSpec} instance containing the class constructor.
     */
    private static MethodSpec generateConstructor(Map<String, ExecutableElement> properties) {
        List<ParameterSpec> params = new ArrayList<ParameterSpec>();
        for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
            TypeName typeName = TypeName.get(entry.getValue().getReturnType());
            params.add(ParameterSpec.builder(typeName, entry.getKey()).build());
        }

        String body = String.format("super(%s)", Joiner.on(", ")
                .join(FluentIterable
                        .from(ImmutableRangeSet.of(Range.closedOpen(0, properties.size()))
                                .asSet(DiscreteDomain.integers())
                                .descendingSet())
                        .transform(new Function<Integer, String>() {
                            @Override
                            public String apply(Integer input) {
                                return "$N";
                            }
                        })));

        return MethodSpec.constructorBuilder()
                .addParameters(params)
                .addStatement(body, properties.keySet().toArray())
                .build();
    }

    /**
     * Neutered toString method generator.
     *
     * @return a {@link MethodSpec} instance containing a neutered toString method.
     */
    private static MethodSpec generateToString() {
        return MethodSpec.methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(String.class)
                .addCode("return getClass().getName() + \"@\" + Integer.toHexString(hashCode());\n")
                .build();
    }
}
