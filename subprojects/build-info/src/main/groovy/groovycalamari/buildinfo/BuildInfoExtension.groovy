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
package groovycalamari.buildinfo

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile

@CompileStatic
class BuildInfoExtension {

    public static final String DEFAULT_KEY_BUILDID = "buildid"
    public static final String DEFAULT_KEY_VERSION = "version"
    public static final String DEFAULT_KEY_GROUP = "group"
    public static final String DEFAULT_KEY_NAME = "name"

    @OutputFile
    private final Property<File> outputFile

    @Input
    private final Property<String> versionKey

    @Input
    private final Property<String> nameKey

    @Input
    private final Property<String> groupKey

    @Input
    private final Property<String> buildIdKey

    BuildInfoExtension(Project project) {
        outputFile = project.objects.property(File)
                .convention(project.file("${project.buildDir}/classes/java/main/META-INF/build-info.properties"))
        buildIdKey = project.objects.property(String)
                .convention(DEFAULT_KEY_BUILDID)
        versionKey = project.objects.property(String)
                .convention(DEFAULT_KEY_VERSION)
        groupKey = project.objects.property(String)
                .convention(DEFAULT_KEY_GROUP)
        nameKey = project.objects.property(String)
                .convention(DEFAULT_KEY_NAME)
    }

    Property<File> getOutputFile() {
        this.outputFile
    }

    Property<String> getBuildIdKey() {
        this.buildIdKey
    }

    Property<String> getVersionKey() {
        this.versionKey
    }

    Property<String> getGroupKey() {
        this.groupKey
    }

    Property<String> getNameKey() {
        this.nameKey
    }
}
