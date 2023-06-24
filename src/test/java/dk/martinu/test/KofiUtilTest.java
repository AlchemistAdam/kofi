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

import dk.martinu.kofi.*;
import dk.martinu.test.dummy.Dummy;
import dk.martinu.test.dummy.Dummy2;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KofiUtil")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KofiUtilTest {

    /**
     * Provides arguments for {@link #escape(String, String, char[])}.
     */
    static Stream<Arguments> escapeProvider() {
        return Stream.of(
                Arguments.of("", "", null),
                Arguments.of("", "", new char[] {'l', 'W'}),
                Arguments.of("\\\\Hello, World!\\n", "\\Hello, World!\n", null),
                Arguments.of("\\\\He\\l\\lo, \\Wor\\ld!\\n", "\\Hello, World!\n", new char[] {'l', 'W'})
        );
    }

    /**
     * Provides arguments for {@link #getKofiValue(Object, Object)}.
     */
    static Stream<Arguments> getKofiValueProvider() {
        return Stream.of(
                // simple values
                Arguments.of(null, null),
                Arguments.of("\"Hello\"", "Hello"),
                Arguments.of(1, 1),
                Arguments.of(1L, 1L),
                Arguments.of(1f, 1f),
                Arguments.of(1d, 1d),
                Arguments.of((byte) 1, (byte) 1),
                Arguments.of((short) 1, (short) 1),
                Arguments.of(true, true),
                Arguments.of('A', 'A'),
                // arrays
                Arguments.of(new KofiArray(), new KofiArray()),
                Arguments.of(new KofiArray(), new int[0]),
                Arguments.of(new KofiArray(), new Object[0]),
                // objects
                Arguments.of(new KofiObject(), new KofiObject()),
                Arguments.of(new KofiObject(), new Object())
        );
    }

    /**
     * Provides arguments for {@link #getJavaValue(Object, Object, Class)}.
     */
    static Stream<Arguments> getJavaValueProvider() {
        return Stream.of(
                Arguments.of(null, null, void.class),
                Arguments.of("Hello", "\"Hello\"", String.class),
                Arguments.of(1, 1, Integer.class),
                Arguments.of(1L, 1L, Long.class),
                Arguments.of(1f, 1f, Float.class),
                Arguments.of(1d, 1d, Double.class),
                Arguments.of((byte) 1, (byte) 1, Byte.class),
                Arguments.of((short) 1, (short) 1, Short.class),
                Arguments.of(true, true, Boolean.class),
                Arguments.of('A', 'A', Character.class)
        );
    }

    /**
     * Test for {@link KofiUtil#escape(String)} and
     * {@link KofiUtil#escape(String, char...)}.
     */
    @DisplayName("can escape strings")
    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void escape(final String expected, final String escape, final char[] other) {
        assertEquals(expected, KofiUtil.escape(escape, other));
    }

    /**
     * Test for {@link KofiUtil#getJavaString(String)}.
     */
    @DisplayName("can get KoFi strings from Java strings")
    @ParameterizedTest
    @CsvSource({
            "'', \"\"",
            "'Hello, ', '\"Hello, \"'",
            "'World!\n', '\"World!\\n\"'",
            "'Ko\u0066\u0069', '\"Ko\\u0066\\u0069\"'"
    })
    public void getJavaString(final String javaString, final String kofiString) {
        assertEquals(javaString, KofiUtil.getJavaString(kofiString));
    }

    /**
     * Test for {@link KofiUtil#getJavaValue(Object, Class)}.
     */
    @DisplayName("can get Java values from KoFi values")
    @ParameterizedTest
    @MethodSource("getJavaValueProvider")
    public void getJavaValue(final Object expected, final Object kofiValue, final Class<?> cls) {
        assertEquals(expected, KofiUtil.getJavaValue(kofiValue, cls));
    }

    /**
     * Test for {@link KofiUtil#getJavaValue(Object, Class)} that specifically
     * tests converting KoFi arrays.
     */
    @DisplayName("can get Java array values from KoFi values")
    @Test
    public void getJavaValueWithArray() {
        // assert converted primitive array is equal
        assertArrayEquals(
                new int[] {1},
                KofiUtil.getJavaValue(new KofiArray(1), int[].class));

        // assert converted array is equal
        assertArrayEquals(
                new Dummy<?>[] {Dummy.of(1)},
                KofiUtil.getJavaValue(new KofiArray(Dummy.of(1)), Dummy[].class));

        // assert converted array is equal, with elements extending array component type
        assertArrayEquals(
                new Dummy<?>[] {Dummy2.of(1, 2)},
                KofiUtil.getJavaValue(new KofiArray(Dummy2.of(1, 2)), Dummy[].class));

        // assert converted array is equal, with nested array
        assertArrayEquals(
                new Dummy<?>[][] {new Dummy[] {Dummy.of(1)}},
                KofiUtil.getJavaValue(new KofiArray((Object) new Dummy[] {Dummy.of(1)}), Dummy[][].class));

        // assert converted array is equal, with nested array extending array component type
        assertArrayEquals(
                new Dummy<?>[][] {new Dummy2[] {Dummy2.of(1, 2)}},
                KofiUtil.getJavaValue(new KofiArray((Object) new Dummy2[] {Dummy2.of(1, 2)}), Dummy[][].class));
    }

    /**
     * Test for {@link KofiUtil#getJavaValue(Object, Class)} that specifically
     * tests converting KoFi objects.
     */
    @DisplayName("can get Java object values from KoFi values")
    @Test
    public void getJavaValueWithObject() {
        // assert converted object is equal
        assertEquals(
                Dummy.of(1),
                KofiUtil.getJavaValue(KofiObject.reflect(Dummy.of(1)), Dummy.class));

        // assert converted object is equal, with fields from supertype
        assertEquals(
                Dummy2.of(1, 2),
                KofiUtil.getJavaValue(KofiObject.reflect(Dummy2.of(1, 2)), Dummy2.class));

        // assert converted object is equal, with nested array
        assertEquals(
                Dummy.of(new Integer[] {1}),
                KofiUtil.getJavaValue(KofiObject.reflect(Dummy.of(new Integer[] {1})), Dummy.class));
    }

    /**
     * Test for {@link KofiUtil#getKofiString(String)}.
     */
    @DisplayName("can get Java strings from KoFi strings")
    @ParameterizedTest
    @CsvSource({
            "'\"\"', ''",
            "'\"Hello, \"', 'Hello, '",
            "'\"World!\\r\\n\"', 'World!\r\n'",
            "'\"Hello, \\u000B\"', 'Hello, \u000B'"
    })
    public void getKofiString(final String kofiString, final String javaString) {
        assertEquals(kofiString, KofiUtil.getKofiString(javaString));
    }

    /**
     * Test for {@link KofiUtil#getKofiValue(Object)}.
     */
    @DisplayName("can get KoFi values from Java values")
    @ParameterizedTest
    @MethodSource("getKofiValueProvider")
    public void getKofiValue(final Object expected, final Object javaObject) {
        assertEquals(expected, KofiUtil.getKofiValue(javaObject));
    }

    /**
     * Test for {@link KofiUtil#indexOf(char, char[], int, int)}
     */
    @DisplayName("can get index of unescaped character in string")
    @ParameterizedTest
    @CsvSource({
            "0,  'a',  'a',       0, 1",
            "0,  'a',  'a   ',    0, 4",
            "1,  'a',  ' a  ',    0, 4",
            "3,  'a',  '   a',    0, 4",
            "-1, 'a',  '',        0, 0",
            "-1, 'a',  ' ',       0, 1",
            "-1, 'a',  '    ',    0, 4",
            "-1, 'a',  'aaaa',    0, 0",
            "0,  'a',  'a  a',    0, 4",
            "0,  'a',  'a  a',    0, 3",
            "3,  'a',  'a  a',    1, 4",
            "1,  'a',  'aa a',    1, 4",
            "3,  'a',  '\\a a',   0, 4",
            "2,  'a',  '\\\\aa',  0, 4",
            "-1, 'a',  '\\a  ',   0, 4",
            "1,  '\\', '\\\\  ',  0, 4",
            "3,  '\\', '\\ \\\\', 0, 4",
            "-1, '\\', '\\   ',   0, 4",
    })
    public void indexOf(final int expected, final char c, final String s, final int start, final int end) {
        assertEquals(expected, KofiUtil.indexOf(c, s.toCharArray(), start, end));
    }

    /**
     * Test for {@link KofiUtil#isDefinedType(Object)}.
     */
    @DisplayName("can determine if an object's type is defined")
    @Test
    public void isDefinedType() {
        final Object[] values = {null, "Hello", 1, true, 'A', new KofiArray(), new KofiObject()};
        for (Object value : values)
            assertTrue(KofiUtil.isDefinedType(value), "isDefinedType(" + value + ")");
    }

    /**
     * Test for {@link KofiUtil#isDigit(char)}.
     */
    @DisplayName("can determine if a character represents a digit")
    @ParameterizedTest
    @ValueSource(chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'})
    public void isDigit(final char c) {
        assertTrue(KofiUtil.isDigit(c));
    }

    /**
     * Test for {@link KofiUtil#isHexDigit(char)}.
     */
    @DisplayName("can determine if a character represents a hexadecimal digit")
    @ParameterizedTest
    @ValueSource(chars = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F',
            'a', 'b', 'c', 'd', 'e', 'f'})
    public void isHexDigit(final char c) {
        assertTrue(KofiUtil.isHexDigit(c));
    }

    /**
     * Test for {@link KofiUtil#isWhitespace(char)}.
     */
    @DisplayName("can determine if a character represents whitespace")
    @Test
    public void isWhitespace() {
        for (int i = 0; i < 256; i++) {
            if (i == ' ' || i == '\r' || i == '\t')
                assertTrue(KofiUtil.isWhitespace((char) i),
                        "(U+" + Integer.toHexString(i) + ") '" + ((char) i) + "'");
            else
                assertFalse(KofiUtil.isWhitespace((char) i),
                        "(U+" + Integer.toHexString(i) + ") '" + ((char) i) + "'");
        }
    }

    /**
     * Test for {@link KofiUtil#matches(char[], int, int, char[])}.
     */
    @DisplayName("can match strings")
    @ParameterizedTest
    @CsvSource({
            "'',              '',      0",
            "'true',          '',      0",
            "'true',          'TRUE',  0",
            "'TRUE',          'TRUE',  0",
            "'True',          'TRUE',  0",
            "'Hello, World!', 'WORLD', 7",
    })
    public void matches(final String s, final String c, final int start) {
        final char[] sChars = s.toCharArray();
        final char[] cChars = c.toCharArray();
        assertTrue(KofiUtil.matches(sChars, start, sChars.length, cChars));
    }

    /**
     * Test for {@link KofiUtil#trim(char[], int, int)}.
     */
    @DisplayName("can trim strings")
    @Test
    public void trim() {
//        // assert content is equal -- trim whole string
//        {
//            final String[][] content = {
//                    /* 0 */ {"", ""},
//                    /* 1 */ {"", "   "},
//                    /* 3 */ {"trim", "trim"},
//                    /* 4 */ {"trim", "trim   "},
//                    /* 5 */ {"trim", "   trim"},
//                    /* 6 */ {"trim", "   trim   "}
//            };
//            for (int i = 0; i < content.length; i++)
//                assertArrayEquals(
//                        content[i][0].toCharArray(),
//                        KofiUtil.trim(content[i][1].toCharArray()),
//                        i + ":");
//        }

        // assert content is equal -- trim between x and y
        {
            final String[][] content = {
                    /* 0 */ {"trim", "xtrimy"},
                    /* 1 */ {"trim", "   x   trimy"},
                    /* 2 */ {"trim", "xtrim   y   "},
                    /* 3 */ {"trim", " x  trim  y "},
                    /* 4 */ {"trim", "x   trim   y"}
            };
            for (int i = 0; i < content.length; i++)
                assertArrayEquals(
                        content[i][0].toCharArray(),
                        KofiUtil.trim(
                                content[i][1].toCharArray(),
                                content[i][1].indexOf('x') + 1,
                                content[i][1].indexOf('y')),
                        i + ":");
        }

        // assert empty range (offset=length) returns empty array
        {
            final char[] c = "trim".toCharArray();
            assertArrayEquals(new char[0], KofiUtil.trim(c, 0, 0));
            assertArrayEquals(new char[0], KofiUtil.trim(c, 1, 1));
            assertArrayEquals(new char[0], KofiUtil.trim(c, c.length, c.length));
        }

        // assert returned array is same as passed array when equal
        {
            final char[] empty = "".toCharArray();
            assertSame(empty, KofiUtil.trim(empty, 0, empty.length));

            final char[] noTrim = "no trim".toCharArray();
            assertSame(noTrim, KofiUtil.trim(noTrim, 0, noTrim.length));
        }
    }


    /**
     * Test for {@link KofiUtil#unescape(String, int, int)}.
     */
    @DisplayName("can unescape strings")
    @Test
    public void unescape() {
        // assert content is equal
        final String kofiString = "\\\\Hello, \u0057orld!\\n";
        assertEquals("\\Hello, World!\n", KofiUtil.unescape(kofiString, 0, kofiString.length()));

        // assert returned string is same as passed if no characters were unescaped
        final String expected = "No escapes";
        assertSame(expected, KofiUtil.unescape(expected, 0, expected.length()));
    }
}
