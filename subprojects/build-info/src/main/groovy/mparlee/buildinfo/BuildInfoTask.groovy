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
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
@CompileStatic
class BuildInfoTask extends DefaultTask {
    public static final String TRAVIS_BUILD_ID = "TRAVIS_BUILD_ID"
    public static final String GITHUB_BUILD_ID = "GITHUB_RUN_ID"
    public static final String CODEBUILD_BUILD_ID = "CODEBUILD_BUILD_ID"
    public static final String GENERIC_BUILD_ID = "BUILD_ID"
    public static final String COMMIT_SHA = "COMMIT_SHA"

    @OutputDirectory
    final DirectoryProperty outputDirectory

    @Input
    final Property<String> versionKey

    @Input
    final Property<String> nameKey

    @Input
    final Property<String> groupKey

    @Input
    final Property<String> buildIdKey

    @Input
    final Property<String> commitShaKey

    @Input
    final Provider<String> buildIdProvider

    @Input
    final Provider<String> versionProvider

    @Input
    final Provider<String> groupProvider

    @Input
    final Provider<String> nameProvider

    @Input
    final Provider<String> commitShaProvider

    BuildInfoTask() {
        outputDirectory = project.objects.directoryProperty()
        versionKey = project.objects.property(String)
        nameKey = project.objects.property(String)
        groupKey = project.objects.property(String)
        buildIdKey = project.objects.property(String)
        commitShaKey = project.objects.property(String)
        buildIdProvider = envVar(TRAVIS_BUILD_ID)
            .orElse(envVar(GITHUB_BUILD_ID))
            .orElse(envVar(CODEBUILD_BUILD_ID))
            .orElse(envVar(GENERIC_BUILD_ID))
        commitShaProvider = envVar(COMMIT_SHA)
        versionProvider = project.providers.provider { String.valueOf(project.version) }.forUseAtConfigurationTime()
        groupProvider = project.providers.provider { String.valueOf(project.group) }.forUseAtConfigurationTime()
        nameProvider = project.providers.provider { project.name }.forUseAtConfigurationTime()
    }

    private Provider<String> envVar(String name) {
        project.providers.environmentVariable(name).forUseAtConfigurationTime()
    }

    @TaskAction
    void generateBuildInfo() {
        Properties properties = generateProperties(createLabels(), createValues())
        File outputFile = outputDirectory.map { Directory it -> it.file("META-INF/build-info.properties") }.get().asFile
        if (!outputFile.exists()) {
            outputFile.parentFile.mkdirs()
            outputFile.createNewFile()
        }
        properties.store(outputFile.newOutputStream(), "")
    }

    private BuildInfoLabels createLabels() {
        new BuildInfoLabels() {
            @Override
            String getBuildId() {
                buildIdKey.get()
            }

            @Override
            String getVersion() {
                versionKey.get()
            }

            @Override
            String getGroup() {
                groupKey.get()
            }

            @Override
            String getName() {
                nameKey.get()
            }

            @Override
            String getCommitSha() {
                commitShaKey.get()
            }
        }
    }

    private BuildInfoValues createValues() {
        new BuildInfoValues() {
            @Override
            String getBuildId() {
                buildIdProvider.orNull
            }

            @Override
            String getVersion() {
                versionProvider.get()
            }

            @Override
            String getGroup() {
                groupProvider.get()
            }

            @Override
            String getName() {
                nameProvider.get()
            }

            @Override
            String getCommitSha() {
                commitShaProvider.orNull
            }
        }
    }

    private static Properties generateProperties(BuildInfoLabels labels, BuildInfoValues values) {
        Properties props = new Properties()
        if (values.buildId) {
            props.setProperty(labels.buildId, values.buildId)
        }
        if (values.commitSha) {
            props.setProperty(labels.commitSha, values.commitSha)
        }
        props.setProperty(labels.name, values.name)
        props.setProperty(labels.group, values.group)
        props.setProperty(labels.version, values.version)
        props
    }
}
