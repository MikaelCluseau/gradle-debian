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

package org.gradle.plugins.ide.eclipse.model

import org.gradle.api.JavaVersion

/**
 * Models the java compatibility information
 * <p>
 * For example see docs for {@link EclipseProject}
 *
 * @author: Szczepan Faber, created at: 4/20/11
 */
class EclipseJdt {

    /**
     * The source Java language level.
     * <p>
     * For example see docs for {@link EclipseProject}
     */
    JavaVersion sourceCompatibility

    void setSourceCompatibility(Object sourceCompatibility) {
        this.sourceCompatibility = JavaVersion.toVersion(sourceCompatibility)
    }

    /**
     * The target JVM to generate {@code .class} files for.
     * <p>
     * For example see docs for {@link EclipseProject}
     */
    JavaVersion targetCompatibility

    void setTargetCompatibility(Object targetCompatibility) {
        this.targetCompatibility = JavaVersion.toVersion(targetCompatibility)
    }
}
