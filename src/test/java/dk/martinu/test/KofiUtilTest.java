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

import static dk.martinu.kofi.KofiUtil.trim;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KofiUtilTest {

    @Test
    void equalsIgnoreCase() {
        // assert true
        final String[] tstrings = {
                "", "",
                "NULL", "",
                "NULL", "NULL",
                "null", "NULL",
                "NuLl", "NULL",
                "Hello", "HELLO",
                "hI", "HI",
                "nulll", "NULL",
                "Helloo", "HELLO",
                "hIi", "HI"
        };
        final char[][] tchars = new char[tstrings.length][2];
        for (int i = 0; i < tstrings.length; i++)
            tchars[i] = tstrings[i].toCharArray();
        for (int i = 0; i < tchars.length - 1; i += 2) {
            final int fi = i;
            assertTrue(KofiUtil.equalsIgnoreCase(tchars[i], 0, tchars[i].length, tchars[i + 1]),
                    () -> "{" + new String(tchars[fi]) + "} {" + new String(tchars[fi + 1]) + "}");
        }

        // assert false
        final String[] fstrings = {
                "", "NULL",
                "nul", "NULL",
                "ull", "NULL",
                "nnull", "NULL",
                "NuLl", "null"
        };
        final char[][] fchars = new char[fstrings.length][2];
        for (int i = 0; i < fstrings.length; i++)
            fchars[i] = fstrings[i].toCharArray();
        for (int i = 0; i < fchars.length - 1; i += 2) {
            final int fi = i;
            assertFalse(KofiUtil.equalsIgnoreCase(fchars[i], 0, fchars[i].length, fchars[i + 1]),
                    () -> "{" + new String(fchars[fi]) + "} {" + new String(fchars[fi + 1]) + "}");
        }

        // assert true - specified start index
        final String[] tsstrings = {
                "", "",
                "NULL", "",
                "abcdNULL", "NULL",
                "abcdnull", "NULL",
                "abcdNuLl", "NULL",
                "abcdeHello", "HELLO",
                "abhI", "HI",
                "abcdnulll", "NULL",
                "abcdeHelloo", "HELLO",
                "abhIi", "HI"
        };
        final char[][] tschars = new char[tsstrings.length][2];
        for (int i = 0; i < tsstrings.length; i++)
            tschars[i] = tsstrings[i].toCharArray();
        for (int i = 0; i < tschars.length - 1; i += 2) {
            final int fi = i;
            assertTrue(KofiUtil.equalsIgnoreCase(tschars[i], tschars[i + 1].length, tschars[i].length, tschars[i + 1]),
                    () -> "{" + new String(tschars[fi]) + "} {" + new String(tschars[fi + 1]) + "}");
        }

        // assert false - specified start index
        final String[] fsstrings = {
                "", "NULL",
                "nul", "NULL",
                "ull", "NULL",
                "nnull", "NULL",
        };
        final char[][] fschars = new char[fsstrings.length][2];
        for (int i = 0; i < fsstrings.length; i++)
            fschars[i] = fsstrings[i].toCharArray();
        for (int i = 0; i < fschars.length - 1; i += 2) {
            final int fi = i;
            assertFalse(KofiUtil.equalsIgnoreCase(fschars[i], 0, fschars[i].length, fschars[i + 1]),
                    () -> "{" + new String(fschars[fi]) + "} {" + new String(fschars[fi + 1]) + "}");
        }
    }

    @Test
    void escape() {
        final String unescaped = "\t\\Hello, World!\\\r\n";
        final String escaped = "\\t\\\\Hello, World!\\\\\\r\\n";
        assertEquals(escaped, KofiUtil.escape(unescaped));
    }

    @Test
    void escapeOther() {
        final String unescaped = "\t\\Hello, World!\\\r\n";
        final String escaped = "\\t\\\\He\\l\\lo, \\Wor\\ld!\\\\\\r\\n";
        assertEquals(escaped, KofiUtil.escape(unescaped, 'l', 'W'));
    }

    @Test
    void isDigit() {
        final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        for (char c : digits)
            assertTrue(KofiUtil.isDigit(c), "isDigit(" + c + ")");
    }

    @Test
    void isHexDigit() {
        final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (char c : digits)
            assertTrue(KofiUtil.isHexDigit(c), "isHexDigit(" + c + ")");

        final char[] notDigits = {'\\', ' ', 'G', 'g', '\0', '\t', 'H', 'h'};
        for (char c : notDigits)
            assertFalse(KofiUtil.isHexDigit(c), "isHexDigit(" + c + ")");
    }

    @Test
    void trimEquals() {
        assertEquals("", trim(""));
        assertEquals("", trim("     "));
        assertEquals("", trim("\t  \r"));
        assertEquals("abc", trim("  abc     "));
        assertEquals("abc", trim("abc"));
        assertEquals("abc", trim("\t  abc     "));
        assertEquals("abc def", trim("abc def"));
        assertEquals("abc def", trim("  abc def     "));
    }

    @Test
    void unescape() {
        final String escaped = "\\t\\\\Hello, \\u0041!\\\\\\r\\n";
        final String unescaped = "\t\\Hello, A!\\\r\n";
        assertEquals(unescaped, KofiUtil.unescape(escaped, 0, escaped.length()));
    }
}
