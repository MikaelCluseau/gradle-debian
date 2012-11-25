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

package org.gradle.api.internal.artifacts;

import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.gradle.api.Transformer;
import org.gradle.api.internal.artifacts.configurations.ResolverProvider;
import org.gradle.api.internal.artifacts.repositories.ArtifactRepositoryInternal;

import java.util.Collections;
import java.util.List;

/**
 * This is a temporary measure while we are having to deal with parallel publication mechanisms.
 */
public class DefaultArtifactPublisherFactory implements ArtifactPublisherFactory {

    private final Transformer<ArtifactPublisher, ResolverProvider> providerToPublisherTransformer;

    public DefaultArtifactPublisherFactory(Transformer<ArtifactPublisher, ResolverProvider> providerToPublisherTransformer) {
        this.providerToPublisherTransformer = providerToPublisherTransformer;
    }

    public ArtifactPublisher createArtifactPublisher(final ArtifactRepositoryInternal repository) {
        return providerToPublisherTransformer.transform(new ResolverProvider() {
            public List<DependencyResolver> getResolvers() {
                return Collections.singletonList(repository.createResolver());
            }
        });
    }

}
