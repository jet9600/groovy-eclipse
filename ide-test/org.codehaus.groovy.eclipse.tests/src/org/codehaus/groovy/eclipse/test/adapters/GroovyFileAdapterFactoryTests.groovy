/*
 * Copyright 2009-2017 the original author or authors.
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
package org.codehaus.groovy.eclipse.test.adapters

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.eclipse.test.EclipseTestCase
import org.eclipse.core.resources.IFile
import org.junit.Assert
import org.junit.Test

/**
 * Tests the Groovy File Adapter Factory.
 */
final class GroovyFileAdapterFactoryTests extends EclipseTestCase {

    @Test
    void testFileAdapter() {
        testProject.createGroovyTypeAndPackage("pack1", "MainClass.groovy", "class MainClass { static void main(String[] args){}}")
        buildAll()
        final IFile script = (IFile) testProject.getProject().findMember("src/pack1/MainClass.groovy")
        Assert.assertNotNull(script)
        ClassNode node = script.getAdapter(ClassNode.class)
        Assert.assertEquals("pack1.MainClass", node.getName())
        Assert.assertFalse(node.isInterface())
        Assert.assertNotNull(node.getMethods("main"))
    }

    @Test
    void testFileAdapterCompileError() {
        testProject.createGroovyTypeAndPackage("pack1", "OtherClass.groovy", "class OtherClass { static void main(String[] args")
        buildAll()
        final IFile script = (IFile) testProject.getProject().findMember("src/pack1/OtherClass.groovy")
        ClassNode node = script.getAdapter(ClassNode.class)
        Assert.assertEquals("pack1.OtherClass", node.getName())
        Assert.assertFalse(node.isInterface())
        Assert.assertNotNull(node.getMethods("main"))
    }

    @Test
    void testFileAdapterHorendousCompileError() {
        testProject.createGroovyTypeAndPackage("pack1", "OtherClass.groovy", "class C {\n  abstract def foo() {}\n" + "}")
        buildAll()
        final IFile script = (IFile) testProject.getProject().findMember("src/pack1/OtherClass.groovy")
        ClassNode node = script.getAdapter(ClassNode.class)
        Assert.assertNull(node)
    }

    @Test
    void testFileAdapterNotGroovyFile() {
        testProject.createFile("NotGroovy.file", "this is not a groovy file")
        buildAll()
        final IFile notScript = (IFile) testProject.getProject().findMember("src/NotGroovy.file")
        Assert.assertNotNull(notScript)
        Assert.assertNull(notScript.getAdapter(ClassNode.class))
    }
}