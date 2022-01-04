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

        final char[] notDigits = {'\\', ' ', 'A', 'a', '\0', '\t', 'B', 'b'};
        for (char c : notDigits)
            assertFalse(KofiUtil.isDigit(c), "isDigit(" + c + ")");
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
    void unescape() {
        final String escaped = "\\t\\\\Hello, World!\\\\\\r\\n";
        final String unescaped = "\t\\Hello, World!\\\r\n";
        assertEquals(unescaped, KofiUtil.unescape(escaped.toCharArray(), 0, escaped.length()));
    }
}
