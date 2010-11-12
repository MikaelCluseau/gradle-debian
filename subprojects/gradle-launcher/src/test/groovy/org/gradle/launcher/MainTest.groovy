/*
 * Copyright 2010 the original author or authors.
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
package org.gradle.launcher

import spock.lang.Specification
import org.junit.Rule
import org.gradle.util.RedirectStdOutAndErr

class MainTest extends Specification {
    @Rule final RedirectStdOutAndErr outputs = new RedirectStdOutAndErr()
    final BuildCompleter completer = Mock()
    final CommandLineActionFactory factory = Mock()
    final String[] args = ['arg']
    final Main main = new Main(args) {
        @Override
        BuildCompleter createBuildCompleter() {
            completer
        }

        @Override
        CommandLineActionFactory createActionFactory(BuildCompleter buildCompleter) {
            assert buildCompleter == completer
            factory
        }
    }

    def createsAndExecutesCommandLineAction() {
        Runnable action = Mock()

        when:
        main.execute()

        then:
        1 * factory.convert(args) >> action
        1 * action.run()
        1 * completer.exit(null)
        0 * factory._
        0 * action._
        0 * completer._
    }

    def reportsActionExecutionFailure() {
        Runnable action = Mock()
        RuntimeException failure = new RuntimeException('broken')

        when:
        main.execute()

        then:
        1 * factory.convert(args) >> action
        1 * action.run() >> { throw failure }
        outputs.stdErr.contains('internal error')
        outputs.stdErr.contains('java.lang.RuntimeException: broken')
        1 * completer.exit(failure)
        0 * factory._
        0 * action._
        0 * completer._
    }

    def reportsActionCreationFailure() {
        RuntimeException failure = new RuntimeException('broken')

        when:
        main.execute()

        then:
        1 * factory.convert(args) >> { throw failure }
        outputs.stdErr.contains('internal error')
        outputs.stdErr.contains('java.lang.RuntimeException: broken')
        1 * completer.exit(failure)
        0 * factory._
        0 * completer._
    }
}
