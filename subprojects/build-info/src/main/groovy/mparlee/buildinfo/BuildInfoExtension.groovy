/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020 Sergio del Amo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mparlee.buildinfo

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

@CompileStatic
class BuildInfoExtension {

    public static final String DEFAULT_KEY_BUILDID = "buildid"
    public static final String DEFAULT_KEY_VERSION = "version"
    public static final String DEFAULT_KEY_GROUP = "group"
    public static final String DEFAULT_KEY_NAME = "name"
    public static final String DEFAULT_KEY_COMMIT_SHA = "commitsha"

    final DirectoryProperty outputDirectory

    final Property<String> versionKey

    final Property<String> nameKey

    final Property<String> groupKey

    final Property<String> buildIdKey

    final Property<String> commitShaKey

    BuildInfoExtension(Project project) {
        outputDirectory = project.objects.directoryProperty()
                .convention(project.layout.buildDirectory.dir("build-info"))
        buildIdKey = project.objects.property(String)
                .convention(DEFAULT_KEY_BUILDID)
        versionKey = project.objects.property(String)
                .convention(DEFAULT_KEY_VERSION)
        groupKey = project.objects.property(String)
                .convention(DEFAULT_KEY_GROUP)
        nameKey = project.objects.property(String)
                .convention(DEFAULT_KEY_NAME)
        commitShaKey = project.objects.property(String)
                .convention(DEFAULT_KEY_COMMIT_SHA)
    }
}
