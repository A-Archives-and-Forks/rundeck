/*
 * Copyright 2018 Rundeck, Inc. (http://rundeck.com)
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
package com.dtolabs.rundeck.core.plugins

import com.dtolabs.rundeck.core.VersionConstants
import spock.lang.Specification
import spock.lang.Unroll
import sun.misc.Unsafe

import java.lang.invoke.MethodHandles
import java.lang.invoke.VarHandle
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier


class PluginMetadataValidatorTest extends Specification {


    def "ValidateTargetHostCompatibility"() {
        when:
        def errors = []
        String originalOsName = System.getProperty("os.name")
        System.setProperty("os.name", rundeckHost)
        PluginMetadataValidator.validateTargetHostCompatibility(errors, targetHost)
        String validation = errors.isEmpty() ? "compatible" : "incompatible"
        System.setProperty("os.name", originalOsName)

        then:
        validation == expected

        where:
        rundeckHost   |   targetHost      | expected
        "windows"     |    "android"      | "incompatible"
        "windows"     |    null           | "incompatible"
        "windows"     |    "unix"         | "incompatible"
        "unix"        |    "windows"      | "incompatible"
        "windows"     |    "windows"      | "compatible"
        "unix"        |    "unix"         | "compatible"
        "mac os x"    |    "unix"         | "compatible"
        "freebsd"     |    "unix"         | "compatible"

    }

    @Unroll
    def "ValidateRundeckCompatibility"() {
        when:
        def errors = []
        PluginMetadataValidator.validateRundeckCompatibility(errors, rundeckVersion, compatVersion)
        String validation = errors.isEmpty() ? "compatible" : "incompatible"

        then:
        validation == expected

        where:
        rundeckVersion  |   compatVersion   | expected
        "3.0.0"         |    null           | "incompatible"
        "3.0.0"         |    "2.0"          | "compatible"
        "3.0.0"         |    "2.11.x"       | "compatible"
        "2.11.0"        |    "3.0.x"        | "incompatible"
        "3.1.0"         |    "3.0.0+"       | "compatible"
        "3.0.5"         |    "3.1.0+"       | "incompatible"
        "3.0.0"         |    "3.0.5+"       | "incompatible"
        "4.0.0"         |    "3.0+"         | "compatible"
        "4.0.0"         |    "3.0"          | "compatible"
        "4.0.0"         |    "3.4.12+"      | "compatible"
        "5.0.0"         |    "3.0+"         | "compatible"
        "5.0.0"         |    "3.2+"         | "compatible"
        "5.0.0"         |    "4.0+"         | "compatible"
        "5.0.0"         |    "4.1+"         | "compatible"
        "5.0.0"         |    "4.0"          | "compatible"
        "5.0.0"         |    "4.4"          | "compatible"
        "3.0.0"         |    "3.0.0"        | "compatible"
        "3.0.0"         |    "3.0.x"        | "compatible"
        "3.0.5"         |    "3.0.x"        | "compatible"
        "3.0.5"         |    "3.0.5+"       | "compatible"
        "3.0.5"         |    "3.0+"         | "compatible"
        "3.1.2"         |    "3.0+"         | "compatible"
        "3.9.6"         |    "3.0+"         | "compatible"
        "3.1.0"         |    "3.0.14+"      | "compatible"
        "3.1.0"         |    "3.x"          | "compatible"
        "3.0.13"        |    "3.0+.14+"     | "incompatible"
    }
}
