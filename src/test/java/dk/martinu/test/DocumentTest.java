/*
 * Copyright (c) 2021, Adam Martinu. All rights reserved. Altering or
 * removing copyright notices or this file header is not allowed.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package dk.martinu.test;

import org.junit.jupiter.api.*;

import dk.martinu.kofi.*;
import dk.martinu.kofi.codecs.IniCodec;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing {@link Document} methods on an input string with elements that have
 * different amounts of whitespace. Tests are ordered randomly.
 * <p>
 * This test not only helps verify that documents work as intended, but also
 * that {@link IniCodec} parses strings to corresponding elements correctly.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.Random.class)
public class DocumentTest {

    // TODO add empty array and empty object
    // TODO add comments
    // TODO add Whitespace elements
    public static final String input = """
              int2  = 442211  \s
             bool  = false  \s
              char =  \\u0025 \s
            mix=["Hello","World",true,'a']
              [abc]  \s
             double=  123.567d
            char2  =  '\\'
            string  ="Hello, World!"  \s
             object =  { "name":"John",   "age"   : 50    ,"sex" :  "male"} \s
              int=4422
            [def] \s
              char3='A'  \s
            object2={"animal":"cat","color":"black","age":4,"name":"Gert","friendly":true}
            float  =  0.999f
              long  =  111222333444L \s
             numbers = [ 123,  567,890]  \s
            """;

    Document document;

    @Test
    void containsArray() {
        assertTrue(document.contains("mix"));
        assertTrue(document.contains("mix", JsonArray.class));

        assertTrue(document.contains("def", "numbers"));
        assertTrue(document.contains("def", "numbers", JsonArray.class));
    }

    @Test
    void containsBoolean() {
        assertTrue(document.contains("bool"));
        assertTrue(document.contains("bool", Boolean.class));
    }

    @Test
    void containsChar() {
        assertTrue(document.contains("char"));
        assertTrue(document.contains("char", Character.class));

        assertTrue(document.contains("abc", "char2"));
        assertTrue(document.contains("abc", "char2", Character.class));

        assertTrue(document.contains("def", "char3"));
        assertTrue(document.contains("def", "char3", Character.class));
    }

    @Test
    void containsDouble() {
        assertTrue(document.contains("abc", "double"));
        assertTrue(document.contains("abc", "double", Double.class));
    }

    @Test
    void containsFloat() {
        assertTrue(document.contains("def", "float"));
        assertTrue(document.contains("def", "float", Float.class));
    }

    @Test
    void containsInt() {
        assertTrue(document.contains("abc", "int"));
        assertTrue(document.contains("abc", "int", Integer.class));
        assertEquals(4422, document.getInt("abc", "int", 12));

        assertTrue(document.contains("int2"));
        assertTrue(document.contains("int2", Integer.class));
        assertEquals(442211, document.getInt("int2", 12));
    }

    @Test
    void containsLong() {
        assertTrue(document.contains("def", "long"));
        assertTrue(document.contains("def", "long", Long.class));
    }

    @Test
    void containsObject() {
        assertTrue(document.contains("abc", "object"));
        assertTrue(document.contains("abc", "object", JsonObject.class));

        assertTrue(document.contains("def", "object2"));
        assertTrue(document.contains("def", "object2", JsonObject.class));
    }

    @Test
    void containsString() {
        assertTrue(document.contains("abc", "string"));
        assertTrue(document.contains("abc", "string", String.class));
    }

    @AfterAll
    void documentSize() {
        assertEquals(16, document.elements().size());
    }

    @Test
    void getArray() {
        assertEquals(new JsonArray("Hello", "World", true, 'a'), document.getArray("mix"));

        assertEquals(new JsonArray(123, 567, 890), document.getArray("def", "numbers"));
    }

    @Test
    void getBoolean() {
        assertEquals(false, document.getBoolean("bool", true));
    }

    @Test
    void getChar() {
        assertEquals('%', document.getChar("char", '1'));

        assertEquals('\\', document.getChar("abc", "char2", '1'));

        assertEquals('A', document.getChar("def", "char3", '1'));
    }

    @Test
    void getDouble() {
        assertEquals(123.567d, document.getDouble("abc", "double", 12d));
    }

    @Test
    void getFloat() {
        assertEquals(.999F, document.getFloat("def", "float", 12F));
    }

    @Test
    void getLong() {
        assertEquals(111222333444L, document.getLong("def", "long", 12L));
    }

    @Test
    void getObject() {
        final JsonObject.Entry[] objectEntries = {
                new JsonObject.Entry("name", "John"),
                new JsonObject.Entry("age", 50),
                new JsonObject.Entry("sex", "male")
        };
        assertEquals(new JsonObject(objectEntries), document.getObject("abc", "object"));

        final JsonObject.Entry[] object2Entries = {
                new JsonObject.Entry("animal", "cat"),
                new JsonObject.Entry("color", "black"),
                new JsonObject.Entry("age", 4),
                new JsonObject.Entry("name", "Gert"),
                new JsonObject.Entry("friendly", true)
        };
        assertEquals(new JsonObject(object2Entries), document.getObject("def", "object2"));
    }

    @Test
    void getString() {
        assertEquals("Hello, World!", document.getString("abc", "string", "12"));
    }

    @BeforeAll
    void initDocument() {
        assertDoesNotThrow(() -> document = new IniCodec().readString(input));
        assertNotNull(document);
    }
}
