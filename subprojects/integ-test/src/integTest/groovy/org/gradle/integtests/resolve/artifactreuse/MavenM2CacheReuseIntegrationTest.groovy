/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.integtests.resolve.artifactreuse

import org.gradle.integtests.fixture.M2Installation
import org.gradle.integtests.resolve.AbstractDependencyResolutionTest

class MavenM2CacheReuseIntegrationTest extends AbstractDependencyResolutionTest {
    def "uses cached artifacts from maven local cache"() {
        given:
        def module1 = mavenHttpRepo.module('gradletest.maven.local.cache.test', "foo", "1.0").publish()
        def m2 = new M2Installation(testDir).generateGlobalSettingsFile()
        def module2 = m2.mavenRepo().module('gradletest.maven.local.cache.test', "foo", "1.0").publish()
        server.start()

        buildFile.text = """
repositories {
    maven { url "${mavenHttpRepo.uri}" }
}
configurations { compile }
dependencies {
    compile 'gradletest.maven.local.cache.test:foo:1.0'
}
task retrieve(type: Sync) {
    from configurations.compile
    into 'build'
}
"""
        and:
        module1.expectPomHead()
        module1.expectPomSha1Get()
        module1.expectArtifactHead()
        module1.expectArtifactSha1Get()

        when:
        executer.withEnvironmentVars(M2_HOME: m2.globalMavenDirectory)
        run 'retrieve'

        then:
        file('build/foo-1.0.jar').assertIsCopyOf(module2.artifactFile)
    }
}
