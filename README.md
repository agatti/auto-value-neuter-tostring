# AutoValue: toString() Neutering Extension

An extension for Google's [AutoValue](https://github.com/google/auto/tree/master/value) that generates `toString()` methods that do not print anything more than the generic `toString()` method provided by `java.lang.Object`.  Even if having extensive `toString()` implementations can be invaluable during development and debugging, there are situations in which disclosing extra information might not be beneficial or space taken by the code is at a premium.

## Usage

Include the extension in your project, define a `@NeuteredToString` annotation, and apply it to any class that you wish to neuter.

```java
@Retention(SOURCE)
@Target(TYPE)
public @interface NeuteredToString {
}
```

```java
@AutoValue
@NeuteredToString
public abstract class User {
  public abstract String name();
  public abstract String phoneNumber();
}
```

When you call `toString()` the output does not reveal anything that wouldn't be available otherwise

```
User@0x12345678
```

## Download

Using Gradle:

Add JitPack as a source repository:

```groovy
repositories {
  maven {
    url "https://jitpack.io"
  }
}
```

And then add the dependency:

```groovy
apt 'com.github.agatti:auto-value-neuter-tostring:0.1.0'
```
(Using the [android-apt][apt] plugin)

or using Maven:

Add JitPack as a source repository:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

And then add the dependency:

```xml
<dependency>
  <groupId>com.github.agatti</groupId>
  <artifactId>auto-value-neuter-tostring</artifactId>
  <version>0.1.0</version>
  <scope>provided</scope>
</dependency>
```

## License

```
Copyright 2016 Alessandro Gatti

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

 [apt]: https://bitbucket.org/hvisser/android-apt
