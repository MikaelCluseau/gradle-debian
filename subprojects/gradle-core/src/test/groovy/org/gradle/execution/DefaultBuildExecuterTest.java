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
package org.gradle.execution;

import org.gradle.api.Task;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.specs.Spec;
import static org.gradle.util.Matchers.*;
import static org.gradle.util.WrapUtil.*;
import static org.hamcrest.Matchers.*;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@RunWith(JMock.class)
public class DefaultBuildExecuterTest {
    private final JUnit4Mockery context = new JUnit4Mockery();
    private final GradleInternal gradle = context.mock(GradleInternal.class);
    private final TaskGraphExecuter taskExecuter = context.mock(TaskGraphExecuter.class);
    private final ProjectInternal project = context.mock(ProjectInternal.class);

    @Before
    public void setup() {
        context.checking(new Expectations(){{
            allowing(gradle).getTaskGraph();
            will(returnValue(taskExecuter));
            allowing(gradle).getDefaultProject();
            will(returnValue(project));
        }});
    }
    
    @Test
    public void usesProjectDefaultExecuterWhenNoTaskNamesProvided() {
        DefaultBuildExecuter executer = new DefaultBuildExecuter(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        assertThat(executer.getDelegate(), instanceOf(ProjectDefaultsBuildExecuter.class));
    }

    @Test
    public void usesNameResolvingExecuterWhenTaskNamesProvided() {
        List<String> taskNames = toList("a", "b");
        DefaultBuildExecuter executer = new DefaultBuildExecuter(taskNames, Collections.EMPTY_LIST);
        assertThat(executer.getDelegate(), reflectionEquals((Object) new TaskNameResolvingBuildExecuter(taskNames)));
    }

    @Test
    public void usesProjectDefaultExecuterAndNameFilterWhenExcludePatternsProvided() {
        DefaultBuildExecuter executer = new DefaultBuildExecuter(Collections.EMPTY_LIST, toList("b"));
        assertThat(executer.getDelegate(), instanceOf(ProjectDefaultsBuildExecuter.class));

        checkNameFilterApplied(executer);
    }

    @Test
    public void usesNameResolvingExecuterAndNameFilterWhenTaskNamesAndExcludePatternsProvided() {
        DefaultBuildExecuter executer = new DefaultBuildExecuter(toList("a"), toList("b"));
        assertThat(executer.getDelegate(), reflectionEquals((Object) new TaskNameResolvingBuildExecuter(toList("a"))));

        checkNameFilterApplied(executer);
    }

    private void checkNameFilterApplied(DefaultBuildExecuter executer) {
        final BuildExecuter delegate = context.mock(BuildExecuter.class);
        executer.setDelegate(delegate);

        context.checking(new Expectations(){{
            one(project).getTasksByName("b", true);
            will(returnValue(toSet(context.mock(Task.class))));
            one(taskExecuter).useFilter(with(notNullValue(Spec.class)));
            one(delegate).select(gradle);
        }});

        executer.select(gradle);
    }
}
