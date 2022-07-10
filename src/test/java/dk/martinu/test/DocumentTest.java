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

import java.util.List;

import dk.martinu.kofi.*;
import dk.martinu.kofi.codecs.KofiCodec;
import dk.martinu.kofi.properties.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing {@link Document} methods on an input string that has different
 * amounts of whitespace, and with elements of all value types. Tests are
 * ordered randomly.
 * <p>
 * This test not only helps assert that documents work as intended, but also
 * that {@link KofiCodec} parses strings to elements correctly.</p>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.Random.class)
// TODO implement tests for 3 whitespace policies when implemented; minimum, default and random
public class DocumentTest {

    public static final String input = """
            remove = "remove"
            nestedArray = [[1, 2, 3], [2, 2, 3], [3, 3, 3]]
            nestedObject = {d0: { v: 6 }, d1: { v: 7 }, d2: { v: 9 }}
             \s
             ;hi
              int2  = 442211  \s
             bool  = false  \s
            negative=-8
            empty=[      ]
            null=null
              char =  '\\u0025' \s
                      \s
            ;mixed
            ;array
            mix=["Hello","World",true, 2 ,  null ]
                   \s
            ;this comment is not attached to an element
                       \s
              [abc]  \s
             double=  123.567d
            remove = 12345
            char2  =  '\\'
               empty=[]
            string  ="Hello, World!"  \s
                \s
             object =  { name:"John",   age   : 50    ,sex :  "male"} \s
              int=4422
              null2 = null
             ;last section
            [def] \s
             empty={ }
              char3='A'  \s
            object2={animal:"cat",color:"black",age:4,name:"Gert",friendly:true,owner:null}
                        \s
            float  =  0.999f
              long  =  111222333444L \s
             numbers = [ 123,  567,890]  \s
             \s
            remove = true
            """;

    Document document;

    @Test
    void acceptArray() {
        assertTrue(document.acceptArray(null, "nestedArray", Assertions::assertNotNull));
        assertTrue(document.acceptValue(null, "nestedArray", KofiArray.class, Assertions::assertNotNull));

        assertTrue(document.acceptArray(null, "empty", Assertions::assertNotNull));
        assertTrue(document.acceptValue(null, "empty", KofiArray.class, Assertions::assertNotNull));

        assertTrue(document.acceptArray(null, "mix", Assertions::assertNotNull));
        assertTrue(document.acceptValue(null, "mix", KofiArray.class, Assertions::assertNotNull));

        assertTrue(document.acceptArray("abc", "empty", Assertions::assertNotNull));
        assertTrue(document.acceptValue("abc", "empty", KofiArray.class, Assertions::assertNotNull));

        assertTrue(document.acceptArray("def", "numbers", Assertions::assertNotNull));
        assertTrue(document.acceptValue("def", "numbers", KofiArray.class, Assertions::assertNotNull));
    }

    @Test
    void acceptBoolean() {
        assertTrue(document.acceptBoolean(null, "bool", Assertions::assertNotNull));
        assertTrue(document.acceptValue(null, "bool", Boolean.class, Assertions::assertNotNull));

        assertTrue(document.acceptBoolean("def", "remove", Assertions::assertNotNull));
        assertTrue(document.acceptValue("def", "remove", Boolean.class, Assertions::assertNotNull));
    }

    @Test
    void acceptChar() {
        assertTrue(document.acceptChar(null, "char", Assertions::assertNotNull));
        assertTrue(document.acceptValue(null, "char", Character.class, Assertions::assertNotNull));

        assertTrue(document.acceptChar("abc", "char2", Assertions::assertNotNull));
        assertTrue(document.acceptValue("abc", "char2", Character.class, Assertions::assertNotNull));

        assertTrue(document.acceptChar("def", "char3", Assertions::assertNotNull));
        assertTrue(document.acceptValue("def", "char3", Character.class, Assertions::assertNotNull));
    }

    @Test
    void acceptDouble() {
        assertTrue(document.acceptDouble("abc", "double", Assertions::assertNotNull));
        assertTrue(document.acceptValue("abc", "double", Double.class, Assertions::assertNotNull));
    }

    @Test
    void acceptFloat() {
        assertTrue(document.acceptFloat("def", "float", Assertions::assertNotNull));
        assertTrue(document.acceptValue("def", "float", Float.class, Assertions::assertNotNull));
    }

    @Test
    void acceptInt() {
        assertTrue(document.acceptInt(null, "int2", Assertions::assertNotNull));
        assertTrue(document.acceptValue(null, "int2", Integer.class, Assertions::assertNotNull));

        assertTrue(document.acceptInt("abc", "int", Assertions::assertNotNull));
        assertTrue(document.acceptValue("abc", "int", Integer.class, Assertions::assertNotNull));

        assertTrue(document.acceptInt("abc", "remove", Assertions::assertNotNull));
        assertTrue(document.acceptValue("abc", "remove", Integer.class, Assertions::assertNotNull));
    }

    @Test
    void acceptLong() {
        assertTrue(document.acceptLong("def", "long", Assertions::assertNotNull));
        assertTrue(document.acceptValue("def", "long", Long.class, Assertions::assertNotNull));
    }

    @Test
    void acceptObject() {
        assertTrue(document.acceptObject(null, "nestedObject", Assertions::assertNotNull));
        assertTrue(document.acceptValue(null, "nestedObject", KofiObject.class, Assertions::assertNotNull));

        assertTrue(document.acceptObject("abc", "object", Assertions::assertNotNull));
        assertTrue(document.acceptValue("abc", "object", KofiObject.class, Assertions::assertNotNull));

        assertTrue(document.acceptObject("def", "empty", Assertions::assertNotNull));
        assertTrue(document.acceptValue("def", "empty", KofiObject.class, Assertions::assertNotNull));

        assertTrue(document.acceptObject("def", "object2", Assertions::assertNotNull));
        assertTrue(document.acceptValue("def", "object2", KofiObject.class, Assertions::assertNotNull));
    }

    @Test
    void acceptString() {
        assertTrue(document.acceptString("abc", "string", Assertions::assertNotNull));
        assertTrue(document.acceptValue("abc", "string", String.class, Assertions::assertNotNull));
    }

    @Test
    void containsArray() {
        assertTrue(document.contains("nestedArray"));
        assertTrue(document.contains("nestedArray", KofiArray.class));

        assertTrue(document.contains("empty"));
        assertTrue(document.contains("empty", KofiArray.class));

        assertTrue(document.contains("mix"));
        assertTrue(document.contains("mix", KofiArray.class));

        assertTrue(document.contains("abc", "empty"));
        assertTrue(document.contains("abc", "empty", KofiArray.class));

        assertTrue(document.contains("def", "numbers"));
        assertTrue(document.contains("def", "numbers", KofiArray.class));
    }

    @Test
    void containsBoolean() {
        assertTrue(document.contains("bool"));
        assertTrue(document.contains("bool", Boolean.class));

        assertTrue(document.contains("def", "remove"));
        assertTrue(document.contains("def", "remove", Boolean.class));
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
        assertTrue(document.contains("int2"));
        assertTrue(document.contains("int2", Integer.class));

        assertTrue(document.contains("abc", "int"));
        assertTrue(document.contains("abc", "int", Integer.class));

        assertTrue(document.contains("abc", "remove"));
        assertTrue(document.contains("abc", "remove", Integer.class));
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
        assertTrue(document.contains("nestedObject"));
        assertTrue(document.contains("nestedObject", KofiObject.class));

        assertTrue(document.contains("abc", "object"));
        assertTrue(document.contains("abc", "object", KofiObject.class));

        assertTrue(document.contains("def", "empty"));
        assertTrue(document.contains("def", "empty", KofiObject.class));

        assertTrue(document.contains("def", "object2"));
        assertTrue(document.contains("def", "object2", KofiObject.class));
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
        assertEquals(StringProperty.class, document.getElement(i++).getClass());
        assertEquals(ArrayProperty.class, document.getElement(i++).getClass());
        assertEquals(ObjectProperty.class, document.getElement(i++).getClass());
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
        assertEquals(Comment.class, document.getElement(i++).getClass());
        assertEquals(ArrayProperty.class, document.getElement(i++).getClass());
        assertEquals(Whitespace.class, document.getElement(i++).getClass());
        assertEquals(Comment.class, document.getElement(i++).getClass());
        assertEquals(Whitespace.class, document.getElement(i++).getClass());

        // section [abc]
        assertEquals(Section.class, document.getElement(i++).getClass());
        assertEquals(DoubleProperty.class, document.getElement(i++).getClass());
        assertEquals(IntProperty.class, document.getElement(i++).getClass());
        assertEquals(CharProperty.class, document.getElement(i++).getClass());
        assertEquals(ArrayProperty.class, document.getElement(i++).getClass());
        assertEquals(StringProperty.class, document.getElement(i++).getClass());
        assertEquals(Whitespace.class, document.getElement(i++).getClass());
        assertEquals(ObjectProperty.class, document.getElement(i++).getClass());
        assertEquals(IntProperty.class, document.getElement(i++).getClass());
        assertEquals(NullProperty.class, document.getElement(i++).getClass());
        assertEquals(Comment.class, document.getElement(i++).getClass());

        // section [def]
        assertEquals(Section.class, document.getElement(i++).getClass());
        assertEquals(ObjectProperty.class, document.getElement(i++).getClass());
        assertEquals(CharProperty.class, document.getElement(i++).getClass());
        assertEquals(ObjectProperty.class, document.getElement(i++).getClass());
        assertEquals(Whitespace.class, document.getElement(i++).getClass());
        assertEquals(FloatProperty.class, document.getElement(i++).getClass());
        assertEquals(LongProperty.class, document.getElement(i++).getClass());
        assertEquals(ArrayProperty.class, document.getElement(i++).getClass());
        assertEquals(Whitespace.class, document.getElement(i++).getClass());
        assertEquals(BooleanProperty.class, document.getElement(i++).getClass());
    }

    @Test
    void documentSize() {
        final long count = input.chars().filter(i -> i == '\n').count();
        assertEquals(count, document.elements().size());
    }

    @Test
    void getArray() {
        assertEquals(KofiArray.reflect(new int[][] {{1, 2, 3}, {2, 2, 3}, {3, 3, 3}}),
                document.getArray("nestedArray"));

        assertEquals(new KofiArray(), document.getArray("empty"));

        assertEquals(new KofiArray("Hello", "World", true, 2, null), document.getArray("mix"));

        assertEquals(new KofiArray(), document.getArray("abc", "empty"));

        assertEquals(new KofiArray(123, 567, 890), document.getArray("def", "numbers"));
    }

    @Test
    void getBoolean() {
        assertEquals(false, document.getBoolean("bool", true));

        assertEquals(true, document.getBoolean("def", "remove", false));
    }

    @Test
    void getChar() {
        assertEquals('%', document.getChar("char", '1'));

        assertEquals('\\', document.getChar("abc", "char2", '1'));

        assertEquals('A', document.getChar("def", "char3", '1'));
    }

    @Test
    void getComments() {
        List<Comment> comments;

        comments = document.getPropertyComments(null, "int2");
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("hi", comments.get(0).text);

        comments = document.getPropertyComments(null, "bool");
        assertNotNull(comments);
        assertEquals(0, comments.size());

        comments = document.getPropertyComments(null, "mix");
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("mixed", comments.get(0).text);
        assertEquals("array", comments.get(1).text);

        comments = document.getSectionComments("abc");
        assertNotNull(comments);
        assertEquals(0, comments.size());

        comments = document.getSectionComments("def");
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("last section", comments.get(0).text);
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
    void getInt() {
        assertEquals(442211, document.getInt("int2", 12));

        assertEquals(4422, document.getInt("abc", "int", 12));
        assertEquals(12345, document.getInt("abc", "remove", 12));
    }

    @Test
    void getLong() {
        assertEquals(111222333444L, document.getLong("def", "long", 12L));
    }

    @Test
    void getNull() {
        assertNull(document.getValue("null", new Object()));
        assertNull(document.getValue("null", Object.class, new Object()));
        assertTrue(document.isNull("null"));

        assertNull(document.getValue("abc", "null2", new Object()));
        assertNull(document.getValue("abc", "null2", Object.class, new Object()));
        assertTrue(document.isNull("abc", "null"));
    }

    @Test
    void getObject() {
        assertEquals(new KofiObject.Builder()
                        .put("d0", new KofiObject.Builder().put("v", 6).build())
                        .put("d1", new KofiObject.Builder().put("v", 7).build())
                        .put("d2", new KofiObject.Builder().put("v", 9).build())
                        .build(),
                document.getObject("nestedObject"));

        assertEquals(new KofiObject.Builder()
                        .put("name", "John")
                        .put("age", 50)
                        .put("sex", "male")
                        .build(),
                document.getObject("abc", "object"));

        assertEquals(new KofiObject(), document.getObject("def", "empty"));

        assertEquals(new KofiObject.Builder()
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
        assertEquals(10, document.getPropertyCount(null));
        assertEquals(8, document.getPropertyCount("abc"));
        assertEquals(7, document.getPropertyCount("def"));
    }

    @Test
    void getSectionCount() {
        assertEquals(2, document.getSectionCount());
    }

    @Test
    void getString() {
        assertEquals("remove", document.getString(null, "remove", "12"));
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
        assertEquals(new KofiArray(), document.getValue("empty", KofiValue.class, null));
    }

    @AfterAll
    void remove() {
        assertTrue(document.removeProperty("remove"));
        assertFalse(document.contains("remove"));
        assertTrue(document.isNull("remove"));

        assertTrue(document.removeProperty("abc", "remove"));
        assertFalse(document.contains("abc", "remove"));
        assertTrue(document.isNull("abc", "remove"));

        assertTrue(document.removeProperty("def", "remove"));
        assertFalse(document.contains("def", "remove"));
        assertTrue(document.isNull("def", "remove"));
    }
}
