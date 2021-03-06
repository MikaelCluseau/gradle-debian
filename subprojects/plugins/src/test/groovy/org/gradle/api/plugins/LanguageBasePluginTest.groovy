/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.plugins

import org.gradle.api.Project
import org.gradle.api.tasks.BinariesContainer
import org.gradle.api.tasks.ProjectSourceSet
import org.gradle.api.tasks.ResourceSet
import org.gradle.util.HelperUtil

import spock.lang.Specification

class LanguageBasePluginTest extends Specification {
    Project project = HelperUtil.createRootProject()

    def setup() {
        project.plugins.apply(LanguageBasePlugin)
    }

    def "adds a 'binaries' container to the project"() {
        expect:
        project.extensions.findByName("binaries") instanceof BinariesContainer
    }

    def "adds a 'sources' container to the project"() {
        expect:
        project.extensions.findByName("sources") instanceof ProjectSourceSet
    }

    def "registers the 'ResourceSet' type for each functional source set added to the 'sources' container"() {
        when:
        project.sources.create("custom")
        project.sources.custom.create("resources", ResourceSet)

        then:
        project.sources.custom.resources instanceof ResourceSet
    }
}
