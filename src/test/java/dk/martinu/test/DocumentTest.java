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
import dk.martinu.kofi.codecs.KofiCodec;
import dk.martinu.kofi.properties.*;

import static org.junit.jupiter.api.Assertions.*;

// TODO add remove tests

/**
 * Testing {@link Document} methods on an input string that has different
 * amounts of whitespace, and with elements of all value types. Tests are
 * ordered randomly.
 * <p>
 * This test not only helps verify that documents work as intended, but also
 * that {@link KofiCodec} parses strings to elements correctly.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.Random.class)
public class DocumentTest {

    // TODO add some nested json
    public static final String input = """
             
             ; hi
              int2  = 442211  \s
             bool  = false  \s
            negative=-8
            empty=[      ]
            null=null
              char =  \\u0025 \s
                        
            ; this is an array with mixed value types
            mix=["Hello","World",true, 2 ,  null ]
                        
              [abc]  \s
             double=  123.567d
            char2  =  '\\'
               empty=[]
            string  ="Hello, World!"  \s
                        
             object =  { "name":"John",   "age"   : 50    ,"sex" :  "male"} \s
              int=4422
              null2 = null
             ; this is the last section in the document
            [def] \s
             empty={ }
              char3='A'  \s
            object2={"animal":"cat","color":"black","age":4,"name":"Gert","friendly":true,"owner":null}
                        
            float  =  0.999f
              long  =  111222333444L \s
             numbers = [ 123,  567,890]  \s
             
            """;

    Document document;

    @Test
    void containsArray() {
        assertTrue(document.contains("empty"));
        assertTrue(document.contains("empty", JsonArray.class));

        assertTrue(document.contains("mix"));
        assertTrue(document.contains("mix", JsonArray.class));

        assertTrue(document.contains("abc", "empty"));
        assertTrue(document.contains("abc", "empty", JsonArray.class));

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
    void containsNull() {
        assertTrue(document.contains("null"));
        assertTrue(document.contains("abc", "null2"));
    }

    @Test
    void containsObject() {
        assertTrue(document.contains("abc", "object"));
        assertTrue(document.contains("abc", "object", JsonObject.class));

        assertTrue(document.contains("def", "empty"));
        assertTrue(document.contains("def", "empty", JsonObject.class));

        assertTrue(document.contains("def", "object2"));
        assertTrue(document.contains("def", "object2", JsonObject.class));
    }

    @Test
    void containsString() {
        assertTrue(document.contains("abc", "string"));
        assertTrue(document.contains("abc", "string", String.class));
    }

    @SuppressWarnings("UnusedAssignment")
    @Test
    void documentElements() {
        int i = 0;
        assertEquals(Whitespace.class, document.getElement(i++).getClass());
        assertEquals(Comment.class, document.getElement(i++).getClass());
        assertEquals(IntProperty.class, document.getElement(i++).getClass());
        assertEquals(BooleanProperty.class, document.getElement(i++).getClass());
        assertEquals(IntProperty.class, document.getElement(i++).getClass());
        assertEquals(ArrayProperty.class, document.getElement(i++).getClass());
        assertEquals(NullProperty.class, document.getElement(i++).getClass());
        assertEquals(CharProperty.class, document.getElement(i++).getClass());
        assertEquals(Whitespace.class, document.getElement(i++).getClass());
        assertEquals(Comment.class, document.getElement(i++).getClass());
        assertEquals(ArrayProperty.class, document.getElement(i++).getClass());
        assertEquals(Whitespace.class, document.getElement(i++).getClass());
        assertEquals(Section.class, document.getElement(i++).getClass());
        assertEquals(DoubleProperty.class, document.getElement(i++).getClass());
        assertEquals(CharProperty.class, document.getElement(i++).getClass());
        assertEquals(ArrayProperty.class, document.getElement(i++).getClass());
        assertEquals(StringProperty.class, document.getElement(i++).getClass());
        assertEquals(Whitespace.class, document.getElement(i++).getClass());
        assertEquals(ObjectProperty.class, document.getElement(i++).getClass());
        assertEquals(IntProperty.class, document.getElement(i++).getClass());
        assertEquals(NullProperty.class, document.getElement(i++).getClass());
        assertEquals(Comment.class, document.getElement(i++).getClass());
        assertEquals(Section.class, document.getElement(i++).getClass());
        assertEquals(ObjectProperty.class, document.getElement(i++).getClass());
        assertEquals(CharProperty.class, document.getElement(i++).getClass());
        assertEquals(ObjectProperty.class, document.getElement(i++).getClass());
        assertEquals(Whitespace.class, document.getElement(i++).getClass());
        assertEquals(FloatProperty.class, document.getElement(i++).getClass());
        assertEquals(LongProperty.class, document.getElement(i++).getClass());
        assertEquals(ArrayProperty.class, document.getElement(i++).getClass());
        assertEquals(Whitespace.class, document.getElement(i++).getClass());
    }

    @Test
    void documentSize() {
        final long count = input.chars().filter(i -> i == '\n').count();
        assertEquals(count, document.elements().size());
    }

    @Test
    void getArray() {
        assertEquals(new JsonArray(), document.getArray("empty"));

        assertEquals(new JsonArray("Hello", "World", true, 2, null), document.getArray("mix"));

        assertEquals(new JsonArray(), document.getArray("abc", "empty"));

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
    void getNull() {
        final Object o = new Object();
        assertNull(document.getValue("null", o));
        assertNull(document.getValue("null", Object.class, o));
        assertNull(document.getValue("abc", "null2", o));
        assertNull(document.getValue("abc", "null2", Object.class, o));
    }

    @Test
    void getObject() {
        assertEquals(new JsonObject.Builder()
                        .put("name", "John")
                        .put("age", 50)
                        .put("sex", "male")
                        .build(),
                document.getObject("abc", "object"));

        assertEquals(new JsonObject(), document.getObject("def", "empty"));

        assertEquals(new JsonObject.Builder()
                        .put("animal", "cat")
                        .put("color", "black")
                        .put("age", 4)
                        .put("name", "Gert")
                        .put("friendly", true)
                        .put("owner", null)
                        .build(),
                document.getObject("def", "object2"));
    }

    @Test
    void getPropertyCount() {
        assertEquals(7, document.getPropertyCount(null));

        assertEquals(7, document.getPropertyCount("abc"));

        assertEquals(6, document.getPropertyCount("def"));
    }

    @Test
    void getSectionCount() {
        assertEquals(2, document.getSectionCount());
    }

    @Test
    void getString() {
        assertEquals("Hello, World!", document.getString("abc", "string", "12"));
    }

    @BeforeAll
    void initDocument() {
        assertDoesNotThrow(() -> document = new KofiCodec().readString(input));
        assertNotNull(document);
    }

    @Test
    void matchesWithSuper() {
        assertEquals(442211, document.getValue("int2", Number.class, 0));
        assertEquals(123.567d, document.getValue("abc", "double", Number.class, 0.0d));
        assertEquals("Hello, World!", document.getValue("abc", "string", CharSequence.class, ""));
        assertNotNull(document.getValue("empty", Json.class, null));
    }
}
