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
package org.gradle.api.internal.artifacts.ivyservice.modulecache;

import org.apache.ivy.core.IvyPatternHelper;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.DefaultArtifact;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.gradle.api.internal.artifacts.ivyservice.ArtifactCacheMetaData;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ModuleVersionRepository;

import java.io.File;
import java.util.Collections;

public class ModuleDescriptorFileStore {
    private static final String DESCRIPTOR_ARTIFACT_PATTERN =
            "module-metadata/[organisation]/[module](/[branch])/[revision]/[resolverId].ivy.xml";

    private final ArtifactCacheMetaData cacheMetaData;

    public ModuleDescriptorFileStore(ArtifactCacheMetaData cacheMetaData) {
        this.cacheMetaData = cacheMetaData;
    }
    
    public File getModuleDescriptorFile(ModuleVersionRepository repository, ModuleRevisionId moduleRevisionId) {
        String filePath = getFilePath(repository, moduleRevisionId);
        return new File(cacheMetaData.getCacheDir(), filePath);
    }

    private String getFilePath(ModuleVersionRepository repository, ModuleRevisionId moduleRevisionId) {
        String resolverId = repository.getId();
        Artifact artifact = new DefaultArtifact(moduleRevisionId, null, "ivy", "ivy", "xml", Collections.singletonMap("resolverId", resolverId));
        return IvyPatternHelper.substitute(DESCRIPTOR_ARTIFACT_PATTERN, artifact);
    }
}