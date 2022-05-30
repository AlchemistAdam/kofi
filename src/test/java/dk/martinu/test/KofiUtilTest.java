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

import dk.martinu.kofi.KofiUtil;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KofiUtilTest {

    /**
     * Test for {@link KofiUtil#matches(char[], int, int, char[])}
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
     * Test for {@link KofiUtil#escape(String)} and
     * {@link KofiUtil#escape(String, char...)}.
     */
    @Test
    void escape() {
        // assert escaped string is equal
        {
            final String expected = "\\t\\\\Hello, World!\\r\\n";
            // string to escape
            final String temp = "\t\\Hello, World!\r\n";
            final String actual = KofiUtil.escape(temp);
            assertEquals(expected, actual);
        }

        // assert escaped string (and other) is equal
        {
            // 'l' and 'W' are escaped
            final String expected = "\\t\\\\He\\l\\lo, \\Wor\\ld!\\r\\n";
            // string to escape
            final String temp = "\t\\Hello, World!\r\n";
            final String actual = KofiUtil.escape(temp, 'l', 'W');
            assertEquals(expected, actual);
        }
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
            for (int i = 0; i < content.length; i++) {
                final char[] expected = content[i][0].toCharArray();
                // string to trim
                final char[] temp = content[i][1].toCharArray();
                final char[] actual = KofiUtil.trim(temp);
                assertArrayEquals(expected, actual, i + ":");
            }
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
            for (int i = 0; i < content.length; i++) {
                final char[] expected = content[i][0].toCharArray();
                // string to trim
                final String temp = content[i][1];
                final char[] actual = KofiUtil.trim(temp.toCharArray(), temp.indexOf('x') + 1, temp.indexOf('y'));
                assertArrayEquals(expected, actual, i + ":");
            }
        }

        // assert empty range (offset=length) returns empty array
        {
            final char[] c = "trim".toCharArray();
            assertArrayEquals(new char[0], KofiUtil.trim(c, 0, 0));
            assertArrayEquals(new char[0], KofiUtil.trim(c, 1, 1));
            assertArrayEquals(new char[0], KofiUtil.trim(c, c.length, c.length));
        }

        // assert whitespace returns empty array
        {
            final char[] whitespace = "    ".toCharArray();
            assertArrayEquals(new char[0], KofiUtil.trim(whitespace));
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
     * Test for {@link KofiUtil#unescape(String, int, int)}
     */
    @Test
    void unescape() {
        // assert content is equal
        {
            final String expected = "\t\\Hello, A!\r\n";
            // string to unescape
            final String temp = "\\t\\\\Hello, \\u0041!\\r\\n";
            final String actual = KofiUtil.unescape(temp, 0, temp.length());
            assertEquals(expected, actual);
        }

        // assert returned string is same as passed if no characters were unescaped
        {
            final String expected = "No escapes";
            final String actual = KofiUtil.unescape(expected, 0, expected.length());
            assertSame(expected, actual);
        }
    }
}
