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

import com.google.auto.value.processor.AutoValueProcessor;
import com.google.testing.compile.JavaFileObjects;

import java.util.Arrays;
import javax.tools.JavaFileObject;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public final class AutoValueNeuterToStringExtensionTest {
    private JavaFileObject neutered;

    @Before
    public void setUp() {
        neutered = JavaFileObjects.forSourceString("test.NeuteredToString", ""
                + "package test;\n"
                + "import java.lang.annotation.Retention;\n"
                + "import java.lang.annotation.Target;\n"
                + "import static java.lang.annotation.ElementType.TYPE;\n"
                + "import static java.lang.annotation.RetentionPolicy.SOURCE;\n"
                + "@Retention(SOURCE)\n"
                + "@Target(TYPE)\n"
                + "public @interface NeuteredToString {\n"
                + "}");
    }

    @Test
    public void simple() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "@AutoValue @NeuteredToString public abstract class Test {\n"
                // Reference type
                + "public abstract String a();\n"
                + "public abstract String b();\n"
                + "public abstract String c();\n"
                + "public abstract String d();\n"
                // Array type
                + "public abstract int[] e();\n"
                + "public abstract int[] f();\n"
                + "public abstract int[] g();\n"
                + "public abstract int[] h();\n"
                // Primitive type
                + "public abstract int i();\n"
                + "public abstract int j();\n"
                + "}\n");

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
                + "package test;\n"
                + "import java.lang.Override;\n"
                + "import java.lang.String;\n"
                + "final class AutoValue_Test extends $AutoValue_Test {\n"
                + "  AutoValue_Test(\n"
                + "      String a, String b, String c, String d,\n"
                + "      int[] e, int[] f, int[] g, int[] h,\n"
                + "      int i, int j) {\n"
                + "    super(a, b, c, d, e, f, g, h, i, j);\n"
                + "  }\n"
                + "  @Override public final String toString() {\n"
                + "    return getClass().getName() + \"@\" + Integer.toHexString(hashCode());\n"
                + "  }\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Arrays.asList(neutered, source))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void beanPrefix() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "@AutoValue @NeuteredToString public abstract class Test {\n"
                + "public abstract String getOne();\n"
                + "public abstract String getTwo();\n"
                + "public abstract String getThree();\n"
                + "public abstract String getFour();\n"
                + "}\n");

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
                + "package test;\n"
                + "import java.lang.Override;\n"
                + "import java.lang.String;\n"
                + "final class AutoValue_Test extends $AutoValue_Test {\n"
                + "  AutoValue_Test(String one, String two, String three, String four) {\n"
                + "    super(one, two, three, four);\n"
                + "  }\n"
                + "  @Override public final String toString() {\n"
                + "    return getClass().getName() + \"@\" + Integer.toHexString(hashCode());\n"
                + "  }\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Arrays.asList(neutered, source))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void preConcatStrings() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "@AutoValue @NeuteredToString public abstract class Test {\n"
                + "public abstract String one();\n"
                + "public abstract String two();\n"
                + "}\n");

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
                + "package test;\n"
                + "import java.lang.Override;\n"
                + "import java.lang.String;\n"
                + "final class AutoValue_Test extends $AutoValue_Test {\n"
                + "  AutoValue_Test(String one, String two) {\n"
                + "    super(one, two);\n"
                + "  }\n"
                + "  @Override public final String toString() {\n"
                + "    return getClass().getName() + \"@\" + Integer.toHexString(hashCode());\n"
                + "  }\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Arrays.asList(neutered, source))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }
}
