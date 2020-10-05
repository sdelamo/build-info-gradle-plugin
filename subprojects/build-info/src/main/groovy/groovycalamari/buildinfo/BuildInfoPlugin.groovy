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
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class BuildInfoPlugin implements Plugin<Project> {
    public static final String EXTENSION_NAME_BUILDINFO = "buildInfo"
    public static final String TASK_BUILD_INFO = "generateBuildInfo"
    public static final String TASK_COMPILE_JAVA = 'compileJava'
    public static final String TASK_PROCESS_RESOURCES = 'processResources'
    public static final String TASK_COMPILE_GROOVY = 'compileGroovy'
    public static final String TASK_COMPILE_KOTLIN = 'compileKotlin'
    public static final String TASK_CLASSES = 'classes'
    public static final String GROUP_BUILD = "build"
    public static final String DESCRIPTION = "Generates a build info file"

    @Override
    void apply(Project project) {
        BuildInfoExtension extension = project.extensions.create(EXTENSION_NAME_BUILDINFO, BuildInfoExtension.class, project)
        project.tasks.register(TASK_BUILD_INFO, BuildInfoTask, new Action<BuildInfoTask>() {
            @Override
            void execute(BuildInfoTask buildInfo) {
                buildInfo.setGroup(GROUP_BUILD)
                buildInfo.setDescription(DESCRIPTION)
                buildInfo.getOutput().set(extension.outputFile)
                buildInfo.getVersionKey().set(extension.versionKey)
                buildInfo.getNameKey().set(extension.nameKey)
                buildInfo.getGroupKey().set(extension.groupKey)
                buildInfo.getBuilIdKey().set(extension.buildIdKey)

                if (project.tasks.findByName(TASK_COMPILE_JAVA)) {
                    buildInfo.mustRunAfter(TASK_COMPILE_JAVA)
                }
                if (project.tasks.findByName(TASK_PROCESS_RESOURCES)) {
                    buildInfo.mustRunAfter(TASK_PROCESS_RESOURCES)
                }
                if (project.tasks.findByName(TASK_COMPILE_GROOVY)) {
                    buildInfo.mustRunAfter(TASK_COMPILE_GROOVY)
                }
                if (project.tasks.findByName(TASK_COMPILE_KOTLIN)) {
                    buildInfo.mustRunAfter(TASK_COMPILE_KOTLIN)
                }
            }
        })
        project.tasks.findByName(TASK_CLASSES)?.dependsOn(TASK_BUILD_INFO)
    }
}
