/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.initialization;

import org.gradle.GradleLauncher;
import org.gradle.StartParameter;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.gradle.util.WrapUtil.toList;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Hans Dockter
 */
public class DefaultGradleLauncherFactoryTest {
    private JUnit4Mockery context = new JUnit4Mockery();
    private final CommandLineConverter<StartParameter> parameterConverter = context.mock(CommandLineConverter.class);
    private final DefaultGradleLauncherFactory factory = new DefaultGradleLauncherFactory();

    @Before
    public void setUp() {
        factory.setCommandLineConverter(parameterConverter);
    }

    @After
    public void tearDown() {
        GradleLauncher.injectCustomFactory(null);
    }

    @Test
    public void registersSelfWithGradleLauncher() {
        final StartParameter startParameter = new StartParameter();
        context.checking(new Expectations() {{
            allowing(parameterConverter).convert(toList("a"));
            will(returnValue(startParameter));
        }});

        assertThat(GradleLauncher.createStartParameter("a"), sameInstance(startParameter));
    }
    
    @Test
    public void newInstanceWithStartParameter() {
        final StartParameter startParameter = new StartParameter();
        assertNotNull(factory.newInstance(startParameter));
    }

    @Test
    public void newInstanceWithCommandLineArgs() {
        final StartParameter startParameter = new StartParameter();
        context.checking(new Expectations() {{
            allowing(parameterConverter).convert(toList("A", "B"));
            will(returnValue(startParameter));
        }});
        assertNotNull(factory.newInstance("A", "B"));
    }

    @Test
    public void createStartParameter() {
        final StartParameter startParameter = new StartParameter();
        context.checking(new Expectations() {{
            allowing(parameterConverter).convert(toList("A", "B"));
            will(returnValue(startParameter));
        }});

        assertThat(factory.createStartParameter("A", "B"), sameInstance(startParameter));
    }
}
