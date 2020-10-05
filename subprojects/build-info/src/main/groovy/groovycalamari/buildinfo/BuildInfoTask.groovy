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
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CompileStatic
class BuildInfoTask extends DefaultTask {
    public static final String TRAVIS_BUILD_ID = "TRAVIS_BUILD_ID"
    public static final String GITHUB_BUILD_ID = "GITHUB_RUN_ID"
    public static final String CODEBUILD_BUILD_ID = "CODEBUILD_BUILD_ID"

    private final Property<File> output
    private final Property<String> versionKey
    private final Property<String> nameKey
    private final Property<String> groupKey
    private final Property<String> buildIdKey

    BuildInfoTask() {
        output = getProject().getObjects().property(File)
        versionKey = getProject().getObjects().property(String)
        nameKey = getProject().getObjects().property(String)
        groupKey = getProject().getObjects().property(String)
        buildIdKey = getProject().getObjects().property(String)
    }

    @TaskAction
    void generateBuildInfo() {
        Properties properties = generateProperties(createLabels(), createValues())
        File outputFile = output.get()
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
        properties.store(outputFile.newOutputStream(), "")
    }

    private static String parseBuildId() {
        if (System.getenv(TRAVIS_BUILD_ID)) {
            return System.getenv(TRAVIS_BUILD_ID)
        } else if (System.getenv(GITHUB_BUILD_ID)) {
            return System.getenv(GITHUB_BUILD_ID)
        } else if (System.getenv(CODEBUILD_BUILD_ID)) {
            return System.getenv(CODEBUILD_BUILD_ID)
        }
        null
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
        }
    }

    private BuildInfoValues createValues() {
        new BuildInfoValues() {
            @Override
            String getBuildId() {
                parseBuildId()
            }

            @Override
            String getVersion() {
                project.version.toString()
            }

            @Override
            String getGroup() {
                project.group.toString()
            }

            @Override
            String getName() {
                project.name
            }
        }
    }
    private static Properties generateProperties(BuildInfoLabels labels, BuildInfoValues values) {
        Properties props = new Properties()
        if (values.buildId) {
            props.setProperty(labels.buildId, values.buildId)
        }
        props.setProperty(labels.name, values.name)
        props.setProperty(labels.group, values.group)
        props.setProperty(labels.version, values.version)
        props
    }

    @OutputFile
    Property<File> getOutput() {
        return output
    }

    @Input
    Property<String> getVersionKey() {
        return versionKey
    }

    @Input
    Property<String> getGroupKey() {
        return groupKey
    }

    @Input
    Property<String> getNameKey() {
        return nameKey
    }

    @Input
    Property<String> getBuilIdKey() {
        return buildIdKey
    }
}
