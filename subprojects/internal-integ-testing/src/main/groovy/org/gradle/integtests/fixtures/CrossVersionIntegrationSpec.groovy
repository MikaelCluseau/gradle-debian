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
package org.gradle.integtests.fixtures

import org.gradle.util.TestFile
import org.junit.Rule
import org.junit.runner.RunWith
import spock.lang.Specification

@RunWith(CrossVersionTestRunner)
abstract class CrossVersionIntegrationSpec extends Specification {
    @Rule public final GradleDistribution current = new GradleDistribution()
    static BasicGradleDistribution previous
    private MavenFileRepository mavenRepo

    BasicGradleDistribution getPrevious() {
        return previous
    }

    protected TestFile getBuildFile() {
        testDir.file('build.gradle')
    }

    protected TestFile getTestDir() {
        current.getTestDir();
    }

    protected TestFile file(Object... path) {
        testDir.file(path);
    }

    protected MavenRepository getMavenRepo() {
        if (mavenRepo == null) {
            mavenRepo = new MavenFileRepository(file("maven-repo"))
        }
        return mavenRepo
    }

    def version(BasicGradleDistribution dist) {
        def executer = dist.executer();
        if (executer instanceof GradleDistributionExecuter) {
            executer.withDeprecationChecksDisabled()
        }
        if (dist.multiProcessSafeCache) {
            executer.withGradleUserHomeDir(current.userHomeDir)
        } else {
            executer.withGradleUserHomeDir(current.file("user-home/$dist.version"))
        }
        executer.inDirectory(testDir)
        return executer;
    }
}
