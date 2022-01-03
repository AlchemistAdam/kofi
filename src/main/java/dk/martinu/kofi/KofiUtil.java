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

package dk.martinu.kofi;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Contains static utility methods used the KOFI API.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class KofiUtil {

    /**
     * An array of precomputed strings for escaping characters in the range
     * [0x00;0x1F] as a six-character escape sequence, or a two-character
     * escape sequence if the character is one of the following:
     * <ul>
     *    <li>{@code \0 U+0000} NULL</li>
     *    <li>{@code \b U+0008} BACKSPACE</li>
     *    <li>{@code \t U+0009} HORIZONTAL TAB</li>
     *    <li>{@code \n U+000A} LINE FEED</li>
     *    <li>{@code \f U+000C} FORM FEED</li>
     *    <li>{@code \r U+000D} CARRIAGE RETURN</li>
     * </ul>
     */
    private static final String[] ESCAPED_CHARS_00_1F = {
            "\\0", "\\u0001", "\\u0002",
            "\\u0003", "\\u0004", "\\u0005",
            "\\u0006", "\\u0007", "\\b",
            "\\t", "\\n", "\\u000B",
            "\\f", "\\r", "\\u000E",
            "\\u000F", "\\u0010", "\\u0011",
            "\\u0012", "\\u0013", "\\u0014",
            "\\u0015", "\\u0016", "\\u0017",
            "\\u0018", "\\u0019", "\\u001A",
            "\\u001B", "\\u001C", "\\u001D",
            "\\u001E", "\\u001F"
    };

    /**
     * Returns an escaped version of {@code string}. Characters in the range
     * [0x00;0x1F] are escaped as a six-character escape sequence, or a
     * two-character escape sequence if the character is one of the following:
     * <ul>
     *    <li>{@code \0 U+0000} Null</li>
     *    <li>{@code \b U+0008} Backspace</li>
     *    <li>{@code \t U+0009} Horizontal Tabulation</li>
     *    <li>{@code \n U+000A} Line Feed</li>
     *    <li>{@code \f U+000C} Form Feed</li>
     *    <li>{@code \r U+000D} Carriage Return</li>
     * </ul>
     * {@code \ U+005C} Reverse Solidus characters are also escaped as a
     * two-character escape sequence. If no characters were escaped then
     * {@code string} is returned.
     *
     * @param string the string to escape
     * @return an escaped version of {@code string}
     * @throws NullPointerException if {@code string} is null
     * @see #escape(String, char...)
     */
    @Contract(pure = true)
    @NotNull
    public static String escape(@NotNull final String string) {
        final char[] chars = string.toCharArray();
        final StringBuilder sb = new StringBuilder(chars.length);
        for (char c : chars)
            if (c < 0x20)
                sb.append(ESCAPED_CHARS_00_1F[c]);
            else if (c == '\\')
                sb.append("\\\\");
            else
                sb.append(c);
        return chars.length == sb.length() ? string : sb.toString();
    }

    // TODO test surrogate characters

    /**
     * Returns an escaped version of {@code string}. Characters in the range
     * [0x00;0x1F] are escaped as a six-character escape sequence, or a
     * two-character escape sequence if the character is one of the following:
     * <ul>
     *    <li>{@code \0 U+0000} Null</li>
     *    <li>{@code \b U+0008} Backspace</li>
     *    <li>{@code \t U+0009} Horizontal Tabulation</li>
     *    <li>{@code \n U+000A} Line Feed</li>
     *    <li>{@code \f U+000C} Form Feed</li>
     *    <li>{@code \r U+000D} Carriage Return</li>
     * </ul>
     * {@code \ U+005C} Reverse Solidus and {@code other} characters are also
     * escaped as two-character escape sequences. If no characters were escaped
     * then {@code string} is returned.
     *
     * @param string the string to escape
     * @param other  other characters to escape
     * @return an escaped version of {@code string}
     * @throws NullPointerException if {@code string} is {@code null}, or if
     *                              {@code other} is {@code null} and {@code string} contains a character
     *                              that is not escaped by default
     * @see #escape(String)
     */
    @Contract(pure = true)
    @NotNull
    public static String escape(@NotNull final String string, final char... other) {
        final char[] chars = string.toCharArray();
        final StringBuilder sb = new StringBuilder(chars.length);
        outer:
        for (char c0 : chars) {
            if (c0 < 0x20)
                sb.append(ESCAPED_CHARS_00_1F[c0]);
            else if (c0 == '\\')
                sb.append("\\\\");
            else
                for (char c1 : other)
                    if (c0 == c1) {
                        sb.append('\\').append(c0);
                        continue outer;
                    }
            sb.append(c0);
        }
        return string.length() == sb.length() ? string : sb.toString();
    }

    /**
     * Returns an escape sequence for the specified control character as a
     * six-character escape sequence, or a two-character  escape sequence if
     * {@code c} is one of the following:
     * <ul>
     *    <li>{@code \0 U+0000} NULL</li>
     *    <li>{@code \b U+0008} BACKSPACE</li>
     *    <li>{@code \t U+0009} HORIZONTAL TAB</li>
     *    <li>{@code \n U+000A} LINE FEED</li>
     *    <li>{@code \f U+000C} FORM FEED</li>
     *    <li>{@code \r U+000D} CARRIAGE RETURN</li>
     * </ul>
     *
     * @param c the control character to escape
     * @return an escape sequence of {@code c}
     * @throws ArrayIndexOutOfBoundsException if {@code c} is not a character
     *                                        in the range 0-1F, inclusive.
     */
    @Contract(pure = true)
    @NotNull
    public static String escape_00_1F(final char c) {
        return ESCAPED_CHARS_00_1F[c];
    }

    /**
     * Returns {@code true} if the specified character {@code c} is a decimal
     * digit, otherwise {@code false} is returned.
     *
     * @param c the character to be tested
     * @return {@code true} if {@code c} is a decimal digit, otherwise
     * {@code false}
     */
    @Contract(pure = true)
    public static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Returns {@code true} if the specified character {@code c} is a
     * hexadecimal digit, otherwise {@code false} is returned.
     *
     * @param c the character to be tested
     * @return {@code true} if {@code c} is a hexadecimal digit, otherwise
     * {@code false}
     */
    @Contract(pure = true)
    public static boolean isHexDigit(final char c) {
        if (isDigit(c))
            return true;
        else if (c >= 'A' && c <= 'F')
            return true;
        else
            return c >= 'a' && c <= 'f';
    }

    /**
     * Returns {@code true} if the specified character is whitespace, otherwise
     * {@code false} is returned. The following characters are whitespace:
     * <ul>
     *    <li>{@code ' ' U+0020} Space</li>
     *    <li>{@code \n U+0008} Line Feed</li>
     *    <li>{@code \r U+000A} Carriage Return</li>
     *    <li>{@code \t U+0009} Horizontal Tabulation</li>
     * </ul>
     *
     * @param c the character to be tested
     * @return {@code true} if {@code c} is whitespace, otherwise {@code false}
     */
    public static boolean isWhitespace(final char c) {
        if (c > ' ')
            return false;
        else
            return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    /**
     * Returns an unescaped version of the specified characters, omitting the
     * first and last character in {@code chars}.
     *
     * @param chars the characters to create an unescaped string from
     * @param start the offset into {@code chars}
     * @param end   the l
     * @return an unescaped string
     */
    // TODO needs general testing - also surrogate characters
    // TODO javadoc - indices inclusive?
    @Contract(value = "null, _, _ -> fail", pure = true)
    @NotNull
    public static String unescape(final char[] chars, final int start, final int end) {
        final StringBuilder sb = new StringBuilder(end - start - 2);
        for (int i = start + 1; i < end - 1; ) {
            if (chars[i] == '\\') {
                final int len = end - 1 - i;
                if (len < 2) {
                    KofiLog.warning("empty escape sequence");
                    sb.append(chars[i++]);
                }
                // six-character escape sequence
                else if (chars[i + 1] == 'u') {
                    if (len >= 6) {
                        if (isHexDigit(chars[i + 2])
                                && isHexDigit(chars[i + 3])
                                && isHexDigit(chars[i + 4])
                                && isHexDigit(chars[i + 5])) {
                            // get int value of 4-digit hex and cast it to char
                            sb.append((char) Integer.valueOf(
                                    String.copyValueOf(chars, i + 2, 4), 16).intValue());
                        }
                        else {
                            final String esc = String.copyValueOf(chars, i, 6);
                            KofiLog.warning("unknown six-character escape sequence {" + esc + "}");
                            sb.append(esc);
                        }
                        i += 6;
                    }
                    else {
                        final String esc = String.copyValueOf(chars, i, len);
                        KofiLog.warning("incomplete six-character escape sequence {" + esc + "}");
                        sb.append(esc);
                        i += len;
                    }
                }
                // two-character escape sequence
                else {
                    final char c = chars[i + 1];
                    if (c == '\\' || c == '"' || c == '/')
                        sb.append(c);
                    else if (c == 'b')
                        sb.append('\b');
                    else if (c == 't')
                        sb.append('\t');
                    else if (c == 'n')
                        sb.append('\n');
                    else if (c == 'f')
                        sb.append('\f');
                    else if (c == 'r')
                        sb.append('\r');
                        // unknown escape sequence
                    else {
                        KofiLog.warning("unknown two-character escape sequence {" + chars[i] + chars[i + 1] + "}");
                        sb.append(chars[i]);
                        sb.append(chars[i + 1]);
                    }
                    i += 2;
                }
            }
            else
                sb.append(chars[i++]);
        }
        return sb.toString();
    }
}
