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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import dk.martinu.kofi.*;
import dk.martinu.test.dummy.Dummy;
import dk.martinu.test.dummy.Dummy2;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KofiUtilTest {


    /**
     * Test for {@link KofiUtil#escape(String)} and
     * {@link KofiUtil#escape(String, char...)}.
     */
    @Test
    void escape() {
        // assert escaped string is equal
        assertEquals(
                "\\\\Hello, World!\\n",
                KofiUtil.escape("\\Hello, World!\n"));

        // assert escaped string and other is equal
        assertEquals(
                "\\\\He\\l\\lo, \\Wor\\ld!\\n",
                KofiUtil.escape("\\Hello, World!\n", 'l', 'W'));

        // empty string
        assertEquals(
                "",
                KofiUtil.escape(""));
    }

    /**
     * Test for {@link KofiUtil#getJavaString(String)}.
     */
    @Test
    void getJavaString() {
        // assert converted KoFi string is equal to Java string
        final String[][] content = {
                /* 0 */ {"", "\"\""}, // empty string
                /* 1 */ {"Hello, ", "\"Hello, \""}, // plain string
                /* 2 */ {"World!\n", "\"World!\\n\""}, // two-character escapes
                /* 3 */ {"Ko\u0066\u0069", "\"Ko\\u0066\\u0069\""} // six-character escapes
        };
        for (int i = 0; i < content.length; i++)
            assertEquals(
                    content[i][0],
                    KofiUtil.getJavaString(content[i][1]),
                    i + ":");
    }

    /**
     * Test for {@link KofiUtil#getJavaValue(Object, Class)}.
     */
    @Test
    void getJavaValue() {
        // assert converted defined values are equal
        assertNull(KofiUtil.getJavaValue(null, void.class));
        assertEquals("Hello", KofiUtil.getJavaValue("\"Hello\"", String.class));
        assertEquals(1, KofiUtil.getJavaValue(1, Integer.class));
        assertEquals(1L, KofiUtil.getJavaValue(1L, Long.class));
        assertEquals(1f, KofiUtil.getJavaValue(1f, Float.class));
        assertEquals(1d, KofiUtil.getJavaValue(1d, Double.class));
        assertEquals((byte) 1, KofiUtil.getJavaValue((byte) 1, Byte.class));
        assertEquals((short) 1, KofiUtil.getJavaValue((short) 1, Short.class));
        assertEquals(true, KofiUtil.getJavaValue(true, Boolean.class));
        assertEquals('A', KofiUtil.getJavaValue('A', Character.class));

        // assert converted primitive array is equal
        assertArrayEquals(
                new int[] {1},
                KofiUtil.getJavaValue(new KofiArray(1), int[].class));

        // dummy objects used for undefined values below (arrays and objects)
        final Dummy<Integer> dummy = new Dummy<>(1);
        final Dummy2<Integer> dummy2 = new Dummy2<>(1, 2);
        final Dummy<Integer[]> dummyX = new Dummy<>(new Integer[] {1});

        // assert converted array is equal
        assertArrayEquals(
                new Dummy<?>[] {dummy},
                KofiUtil.getJavaValue(new KofiArray(dummy), Dummy[].class)
        );
        // assert converted array is equal, with elements extending array component type
        assertArrayEquals(
                new Dummy<?>[] {dummy2},
                KofiUtil.getJavaValue(new KofiArray(dummy2), Dummy[].class)
        );
        // assert converted array is equal, with nested array
        assertArrayEquals(
                new Dummy<?>[][] {new Dummy[] {dummy}},
                KofiUtil.getJavaValue(new KofiArray((Object) new Dummy[] {dummy}), Dummy[][].class)
        );
        // assert converted array is equal, with nested array extending array component type
        assertArrayEquals(
                new Dummy<?>[][] {new Dummy2[] {dummy2}},
                KofiUtil.getJavaValue(new KofiArray((Object) new Dummy2[] {dummy2}), Dummy[][].class)
        );

        // assert converted object is equal
        assertEquals(
                dummy,
                KofiUtil.getJavaValue(KofiObject.reflect(dummy), Dummy.class)
        );
        // assert converted object is equal, with fields from supertype
        assertEquals(
                dummy2,
                KofiUtil.getJavaValue(KofiObject.reflect(dummy2), Dummy2.class)
        );
        // assert converted object is equal, with nested array
        assertEquals(
                dummyX,
                KofiUtil.getJavaValue(KofiObject.reflect(dummyX), Dummy.class)
        );
    }

    /**
     * Test for {@link KofiUtil#getKofiString(String)}.
     */
    @Test
    void getKofiString() {
        // assert converted Java string is equal to KoFi string
        final String[][] content = {
                /* 0 */ {"\"\"", ""}, // empty string
                /* 1 */ {"\"Hello\"", "Hello"}, // plain string
                /* 2 */ {"\"World!\\r\\n\"", "World!\r\n"}, // two-character escapes
        };
        for (int i = 0; i < content.length; i++)
            assertEquals(
                    content[i][0],
                    KofiUtil.getKofiString(content[i][1]),
                    i + ":");
    }

    /**
     * Test for {@link KofiUtil#getKofiValue(Object)}.
     */
    @Test
    void getKofiValue() {
        // defined values
        assertNull(KofiUtil.getKofiValue(null));
        assertEquals("\"Hello\"", KofiUtil.getKofiValue("Hello"));
        assertEquals(1, KofiUtil.getKofiValue(1));
        assertEquals(1L, KofiUtil.getKofiValue(1L));
        assertEquals(1f, KofiUtil.getKofiValue(1f));
        assertEquals(1d, KofiUtil.getKofiValue(1d));
        assertEquals((byte) 1, KofiUtil.getKofiValue((byte) 1));
        assertEquals((short) 1, KofiUtil.getKofiValue((short) 1));
        assertEquals(true, KofiUtil.getKofiValue(true));
        assertEquals('A', KofiUtil.getKofiValue('A'));

        // arrays
        assertEquals(new KofiArray(), KofiUtil.getKofiValue(new KofiArray()));
        assertEquals(new KofiArray(), KofiUtil.getKofiValue(new int[0]));
        assertEquals(new KofiArray(), KofiUtil.getKofiValue(new Object[0]));

        // objects
        assertEquals(new KofiObject(), KofiUtil.getKofiValue(new KofiObject()));
        assertEquals(new KofiObject(), KofiUtil.getKofiValue(new Object()));
    }

    /**
     * Test for {@link KofiUtil#isDefinedType(Object)}.
     */
    @Test
    void isDefinedType() {
        // null, string and primitives
        assertTrue(KofiUtil.isDefinedType(null));
        assertTrue(KofiUtil.isDefinedType("Hello"));
        assertTrue(KofiUtil.isDefinedType(1));
        assertTrue(KofiUtil.isDefinedType(true));
        assertTrue(KofiUtil.isDefinedType('A'));

        // KoFi values
        assertTrue(KofiUtil.isDefinedType(new KofiArray()));
        assertTrue(KofiUtil.isDefinedType(new KofiObject()));

        assertFalse(KofiUtil.isDefinedType(this));
    }

    /**
     * Test for {@link KofiUtil#isDigit(char)}.
     */
    @Test
    void isDigit() {
        // assert all characters are digits
        final char[] digits = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
        };
        for (char c : digits)
            assertTrue(KofiUtil.isDigit(c), "isDigit(" + c + ")");
    }

    /**
     * Test for {@link KofiUtil#isHexDigit(char)}.
     */
    @Test
    void isHexDigit() {
        // assert all characters are hex digits
        final char[] hexDigits = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F',
                'a', 'b', 'c', 'd', 'e', 'f'
        };
        for (char c : hexDigits)
            assertTrue(KofiUtil.isHexDigit(c), "isHexDigit(" + c + ")");
    }

    /**
     * Test for {@link KofiUtil#matches(char[], int, int, char[])}.
     */
    @Test
    void matches() {
        // assert entire content matches
        {
            final String[][] content = {
                    /* 0 */ {"", ""},
                    /* 1 */ {"true", ""},
                    /* 2 */ {"true", "TRUE"},
                    /* 3 */ {"TRUE", "TRUE"},
                    /* 4 */ {"True", "TRUE"}
            };
            for (int i = 0; i < content.length; i++) {
                // string to compare to
                final char[] string = content[i][0].toCharArray();
                // string to compare with
                final char[] comp = content[i][1].toCharArray();
                assertTrue(KofiUtil.matches(string, 0, string.length, comp), i + ":");
            }
        }

        // assert subcontent matches
        {
            // string to compare to
            final char[] content = "Hello, World!".toCharArray();
            // string to compare with
            final char[] comp = "WORLD".toCharArray();
            assertTrue(KofiUtil.matches(content, 7, content.length, comp));
        }
    }

    /**
     * Test for {@link KofiUtil#trim(char[], int, int)}.
     */
    @Test
    void trim() {
        // assert content is equal -- trim whole string
        {
            final String[][] content = {
                    /* 0 */ {"", ""},
                    /* 1 */ {"", "   "},
                    /* 3 */ {"trim", "trim"},
                    /* 4 */ {"trim", "trim   "},
                    /* 5 */ {"trim", "   trim"},
                    /* 6 */ {"trim", "   trim   "}
            };
            for (int i = 0; i < content.length; i++)
                assertArrayEquals(
                        content[i][0].toCharArray(),
                        KofiUtil.trim(content[i][1].toCharArray()),
                        i + ":");
        }

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
    @Test
    void unescape() {
        // assert content is equal
        {
            // string to unescape
            final String s = "\\\\Hello, \u0057orld!\\n";
            assertEquals(
                    "\\Hello, World!\n",
                    KofiUtil.unescape(s, 0, s.length()));
        }

        // assert returned string is same as passed if no characters were unescaped
        {
            final String expected = "No escapes";
            assertSame(
                    expected,
                    KofiUtil.unescape(expected, 0, expected.length()));
        }
    }
}
