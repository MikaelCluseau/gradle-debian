/*
 * Copyright 2012 the original author or authors.
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


package org.gradle.testing.testng

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.JUnitTestExecutionResult
import spock.lang.Unroll

import static org.hamcrest.Matchers.*
import static org.hamcrest.core.IsNot.not

public class TestNGProducesJUnitXmlResultsIntegrationTest extends
        AbstractIntegrationSpec {
    def setup() {
        executer.allowExtraLogging = false
    }

    @Unroll("#testConfiguration")
    def "produces JUnit xml results"() {
        expect:
        assertProducesXmlResults(testConfiguration)

        where:
        testConfiguration << [
                "useTestNG()",
                "useTestNG(); forkEvery 1",
                "useTestNG(); maxParallelForks 2"
        ]
    }

    def assertProducesXmlResults(String testConfiguration) {
        file("src/test/java/org/MixedMethodsTest.java") << """package org;
import org.testng.*;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class MixedMethodsTest {
    @Test public void passing() {
        System.out.println("out.pass");
        System.err.println("err.pass");
    }
    @Test public void failing() {
        System.out.println("out.fail");
        System.err.println("err.fail");
        fail("failing!");
    }
    @Test public void passing2() {
        System.out.println("out.pass2");
        System.err.println("err.pass2");
    }
    @Test public void failing2() {
        System.out.println("out.fail2");
        System.err.println("err.fail2");
        fail("failing2!");
    }
    @Test(enabled = false) public void skipped() {}
}
"""
        file("src/test/java/org/PassingTest.java") << """package org;
import org.testng.*;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class PassingTest {
    @Test public void passing() {
        System.out.println("out" );
    }
    @Test public void passing2() {}
}
"""
        file("src/test/java/org/FailingTest.java") << """package org;
import org.testng.*;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class FailingTest {
    @Test public void failing() {
        System.err.println("err");
        fail();
    }
    @Test public void failing2() {
        fail();
    }
}
"""
        file("src/test/java/org/NoOutputsTest.java") << """package org;
import org.testng.*;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class NoOutputsTest {
    @Test(enabled=false) public void skipped() {}
    @Test public void passing() {}
}
"""

        def buildFile = file('build.gradle')
        buildFile << """
apply plugin: 'java'
repositories { mavenCentral() }
dependencies { testCompile 'org.testng:testng:6.3.1' }

test {
    testReport = true
    $testConfiguration
}
"""
        //when
        executer.withTasks('test').runWithFailure()

        //then
        def junitResult = new JUnitTestExecutionResult(file("."));
        junitResult
            .assertTestClassesExecuted("org.FailingTest","org.PassingTest", "org.MixedMethodsTest", "org.NoOutputsTest")

        junitResult.testClass("org.MixedMethodsTest")
            .assertTestCount(4, 2, 0)
            .assertTestsExecuted("passing", "passing2", "failing", "failing2")
            .assertTestFailed("failing", equalTo('java.lang.AssertionError: failing!'))
            .assertTestFailed("failing2", equalTo('java.lang.AssertionError: failing2!'))
            .assertTestPassed("passing")
            .assertTestPassed("passing2")
            .assertTestsSkipped()
            .assertStderr(allOf(containsString("err.fail"), containsString("err.fail2"), containsString("err.pass"), containsString("err.pass2")))
            .assertStderr(not(containsString("out.")))
            .assertStdout(allOf(containsString("out.fail"), containsString("out.fail2"), containsString("out.pass"), containsString("out.pass2")))
            .assertStdout(not(containsString("err.")))

        junitResult.testClass("org.PassingTest")
            .assertTestCount(2, 0, 0)
            .assertTestsExecuted("passing", "passing2")
            .assertTestPassed("passing").assertTestPassed("passing2")
            .assertStdout(equalTo("out\n"))
            .assertStderr(equalTo(""))

        junitResult.testClass("org.FailingTest")
            .assertTestCount(2, 2, 0)
            .assertTestsExecuted("failing", "failing2")
            .assertTestFailed("failing", anything()).assertTestFailed("failing2", anything())
            .assertStdout(equalTo(""))
            .assertStderr(equalTo("err\n"))

        junitResult.testClass("org.NoOutputsTest")
            .assertTestCount(1, 0, 0)
            .assertTestsExecuted("passing").assertTestPassed("passing")
            .assertStdout(equalTo(""))
            .assertStderr(equalTo(""))
    }
}
