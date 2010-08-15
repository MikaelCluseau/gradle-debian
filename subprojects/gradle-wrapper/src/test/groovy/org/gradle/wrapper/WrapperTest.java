/*
 * Copyright 2007-2008 the original author or authors.
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
package org.gradle.wrapper;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Hans Dockter
 */
@RunWith(org.jmock.integration.junit4.JMock.class)
public class WrapperTest {
    Wrapper wrapper;
    BootstrapMainStarter bootstrapMainStarterMock;
    Install installMock;

    JUnit4Mockery context = new JUnit4Mockery();

    private File propertiesDir = new File("propertiesDir");
    private File propertiesFile;

    @Before
    public void setUp() throws IOException {
        context.setImposteriser(ClassImposteriser.INSTANCE);
        wrapper = new Wrapper();
        bootstrapMainStarterMock = context.mock(BootstrapMainStarter.class);
        installMock = context.mock(Install.class);
        propertiesDir.mkdirs();
        propertiesFile = new File(propertiesDir, "wrapper.properties");
        Properties testProperties = new Properties();
        testProperties.load(WrapperTest.class.getResourceAsStream("/org/gradle/wrapper/wrapper.properties"));
        testProperties.store(new FileOutputStream(propertiesFile), null);
        System.setProperty(Wrapper.WRAPPER_PROPERTIES_PROPERTY, propertiesFile.getCanonicalPath());
    }

    @After
    public void tearDown() {
        propertiesFile.delete();
        propertiesDir.delete();
        System.getProperties().remove(Wrapper.WRAPPER_PROPERTIES_PROPERTY);
    }

    @Test
    public void execute() throws Exception {
        final String[] expectedArgs = { "arg1", "arg2" };
        final int expectedExitValue = 5;
        final String expectedGradleHome = "somepath";
        context.checking(new Expectations() {{
          one(installMock).createDist(
                  "test" + Wrapper.URL_ROOT_PROPERTY,
                  "test" + Wrapper.DISTRIBUTION_BASE_PROPERTY,
                  "test" + Wrapper.DISTRIBUTION_PATH_PROPERTY,
                  "test" + Wrapper.DISTRIBUTION_NAME_PROPERTY,
                  "test" + Wrapper.DISTRIBUTION_VERSION_PROPERTY,
                  "test" + Wrapper.DISTRIBUTION_CLASSIFIER_PROPERTY,
                  "test" + Wrapper.ZIP_STORE_BASE_PROPERTY,
                  "test" + Wrapper.ZIP_STORE_PATH_PROPERTY
          ); will(returnValue(expectedGradleHome));
          one(bootstrapMainStarterMock).start(expectedArgs, expectedGradleHome, "test" + Wrapper.DISTRIBUTION_VERSION_PROPERTY);
        }});
        wrapper.execute(expectedArgs, installMock, bootstrapMainStarterMock);
    }
}
