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
     * Returns an escaped version of the specified string {@code s}. The
     * characters to escape are specified in the {@code chars} array. If no
     * characters were escaped, then {@code s} is returned.
     */
    @Contract(pure = true)
    @NotNull
    public static String escape(@NotNull final String s, final char... chars) {
        final StringBuilder sb = new StringBuilder(s.length());
        outer:
        for (int i = 0; i < s.length(); i++) {
            final char c0 = s.charAt(i);
            for (char c1 : chars)
                if (c0 == c1) {
                    sb.append('\\').append(c0);
                    continue outer;
                }
            sb.append(c0);
        }
        return s.length() == sb.length() ? s : sb.toString();
    }


    /**
     * Returns {@code true} if the specified character {@code c} is a decimal
     * digit, otherwise {@code false} is returned.
     *
     * @param c the character to be tested.
     * @return {@code true} if {@code c} is a decimal digit, otherwise
     * {@code false}.
     */
    @Contract(pure = true)
    public static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Returns {@code true} if the specified character {@code c} is a
     * hexadecimal digit, otherwise {@code false} is returned.
     *
     * @param c the character to be tested.
     * @return {@code true} if {@code c} is a hexadecimal digit, otherwise
     * {@code false}.
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

    public static boolean isWhitespace(final char c) {
        if (c > ' ')
            return false;
        else
            return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    // TODO needs testing
    // TODO javadoc
    @Contract(value = "null, _, _ -> fail; _, _, _ -> new", pure = true)
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
