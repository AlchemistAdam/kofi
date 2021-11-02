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

import org.jetbrains.annotations.*;

import java.util.concurrent.atomic.*;

// TODO javadoc
public abstract class Json {

    /**
     * <p>Array of precomputed strings for escaping characters in the range
     * [0x00;0x1F] as a six-character sequence, except for those characters
     * which are allowed to be escaped as a two-character sequence.
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-7">
     * RFC 8259</a> for more details.
     */
    protected static final String[] ESCAPED_CHARS_00_1F = {
            "\\u0000", "\\u0001", "\\u0002",
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
     * Array of precomputed strings for escaping characters in the range
     * [0x7F;0x9F] as a six-character sequence.
     */
    protected static final String[] ESCAPED_CHARS_7F_9F = {
            "\\u007F", "\\u0080", "\\u0081",
            "\\u0082", "\\u0083", "\\u0084",
            "\\u0085", "\\u0086", "\\u0087",
            "\\u0088", "\\u0089", "\\u008A",
            "\\u008B", "\\u008C", "\\u008D",
            "\\u008E", "\\u008F", "\\u0090",
            "\\u0091", "\\u0092", "\\u0093",
            "\\u0094", "\\u0095", "\\u0096",
            "\\u0097", "\\u0098", "\\u0099",
            "\\u009A", "\\u009B", "\\u009C",
            "\\u009D", "\\u009E", "\\u009F"
    };

    protected static boolean isHexDigit(final char c) {
        if (c >= '0' && c <= '9')
            return true;
        else if (c >= 'A' && c <= 'F')
            return true;
        else
            return c >= 'a' && c <= 'f';
    }

    /**
     * <p>Returns a string representation of this object as JSON text that
     * conforms with the IETF RFC 8259 specification.
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-2">
     * RFC 8259</a> for more details.
     */
    @NotNull
    public abstract String toJson();

    protected boolean areNumbersEqual(@NotNull final Number n0, @NotNull final Number n1) {
        if (n0 instanceof Double || n1 instanceof Double
                || n0 instanceof DoubleAccumulator || n0 instanceof DoubleAdder
                || n1 instanceof DoubleAccumulator || n1 instanceof DoubleAdder)
            return n0.doubleValue() == n1.doubleValue();
        else if (n0 instanceof Float || n1 instanceof Float)
            return n0.floatValue() == n1.floatValue();
        else if (n0 instanceof Long || n1 instanceof Long
                || n0 instanceof LongAccumulator || n0 instanceof LongAdder
                || n1 instanceof LongAccumulator || n1 instanceof LongAdder)
            return n0.longValue() == n1.longValue();
        else if (n0 instanceof Integer || n1 instanceof Integer)
            return n0.intValue() == n1.intValue();
        else if (n0 instanceof Short || n1 instanceof Short)
            return n0.shortValue() == n1.shortValue();
        else
            return n0.byteValue() == n1.byteValue();
    }

    /**
     * Returns an {@code Object} whose type is guaranteed to be
     * {@link #isTypeDefined(Object) defined}. The returned object is
     * determined in the following way:
     * <ul>
     *     <li>If {@code o} is already of a defined type, then {@code o} is
     *     returned.</li>
     *     <li>If {@code o} is a {@code Character}, then its numerical value is
     *     returned as an {@code Integer}.</li>
     *     <li>If {@code o} is an instance of {@code Object[]} then a
     *     {@link JsonArray} is returned, passing the array as the parameter to
     *     the constructor.</li>
     *     <li>If the class of the object represents an array as determined by
     *     {@link Class#isArray()}, then a {@code JsonArray} created via
     *     reflection is returned.</li>
     *     <li>Otherwise, a {@link JsonObject} created via reflection is
     *     returned.</li>
     * </ul>
     *
     * @see JsonArray#JsonArray(Object...)
     * @see JsonArray#reflect(Object)
     * @see JsonObject#reflect(Object)
     */
    @Contract(value = "null -> null; !null -> !null", pure = true)
    @Nullable
    protected Object getDefinedObject(@Nullable final Object o) {
        if (isTypeDefined(o))
            if (o instanceof String s)
                return getJsonString(s);
            else
                return o;
            // characters are not in the JSON specification, use numerical value instead
        else if (o instanceof Character c)
            return Integer.valueOf(c);
        else if (o instanceof Object[] array)
            return new JsonArray(array);
        else if (o.getClass().isArray())
            return JsonArray.reflect(o);
        else
            return JsonObject.reflect(o);
    }

    // TODO needs testing
    protected String getJavaString(@NotNull final String s) {
        final char[] chars = s.toCharArray();
        final StringBuilder sb = new StringBuilder(chars.length - 2);
        for (int i = 1; i < chars.length - 1; ) {
            if (chars[i] == '\\') {
                final int len = chars.length - 1 - i;
                if (len >= 5 && chars[i + 1] == 'u') {
                    if (isHexDigit(chars[i + 2])
                            && isHexDigit(chars[i + 3])
                            && isHexDigit(chars[i + 4])
                            && isHexDigit(chars[i + 5])) {
                        sb.append((char) Integer.valueOf(
                                String.copyValueOf(chars, i + 2, 4), 16).intValue());
                        i += 6;
                    }
                    else {
                        KofiLog.warning("unknown six-character escape sequence {"
                                + String.copyValueOf(chars, i, 6) + "}");
                        sb.append(chars[i++]);
                    }
                }
                else if (len >= 1) {
                    final char c = chars[i + 1];
                    if (c == '\\' || c == '"' || c == '/') {
                        sb.append(c);
                        i += 2;
                    }
                    else if (c == 'b') {
                        sb.append('\b');
                        i += 2;
                    }
                    else if (c == 't') {
                        sb.append('\t');
                        i += 2;
                    }
                    else if (c == 'n') {
                        sb.append('\n');
                        i += 2;
                    }
                    else if (c == 'f') {
                        sb.append('\f');
                        i += 2;
                    }
                    else if (c == 'r') {
                        sb.append('\r');
                        i += 2;
                    }
                    // unknown escape sequence
                    else {
                        KofiLog.warning("unknown escape sequence {"
                                + chars[i] + chars[i + 1] + "}");
                        sb.append(chars[i++]);
                    }
                }
                // unknown escape sequence
                else {
                    KofiLog.warning("unknown escape sequence");
                    sb.append(chars[i++]);
                }
            }
            else
                sb.append(chars[i++]);
        }
        return sb.toString();
    }

    /**
     * <p>Surrounds the specified string {@code s} with quotation marks and
     * escapes any characters necessary, such that it conforms to the IETF RFC
     * 8259 specification, with the addition that control characters in the
     * range [0x7F;0x9F] are also escaped.
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-7">
     * RFC 8259</a> for more details.
     */
    @NotNull
    protected String getJsonString(@NotNull final String s) {
        final char[] chars = s.toCharArray();
        final StringBuilder sb = new StringBuilder(chars.length + 2);
        sb.append('"');
        for (char c : chars) {
            if (c <= 0x1F)
                sb.append(ESCAPED_CHARS_00_1F[c]);
            else if (c >= 0x7F && c <= 0x9F)
                sb.append(ESCAPED_CHARS_7F_9F[c - 0x7F]);
            else if (c == '"' || c == '\\')
                sb.append('\\').append(c);
            else
                sb.append(c);
        }
        return sb.append('"').toString();
    }

    /**
     * <p>Returns {@code true} if the type of the specified {@code Object}
     * {@code o} is defined in the IETF RFC 8259 specification. Otherwise
     * {@code false} is returned. The following is a
     * list of all types for which this method returns {@code true}:
     * <ul>
     *     <li>{@code null}, not specifically a type but {@code null} is a
     *     defined value.</li>
     *     <li>{@code String}</li>
     *     <li>{@code Number}</li>
     *     <li>{@code Boolean}</li>
     *     <li>{@link Json}</li>
     * </ul>
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-3">
     * RFC 8259</a> for more details.
     */
    @Contract(value = "null -> true", pure = true)
    protected boolean isTypeDefined(@Nullable final Object o) {
        return o == null
                || o instanceof String
                || o instanceof Number
                || o instanceof Boolean
                || o instanceof Json;
    }

    protected void toJson(@Nullable Object value, @NotNull final StringBuilder sb) throws IllegalArgumentException {
        if (isTypeDefined(value)) {
            if (value == null)
                sb.append("null");
            else if (value instanceof String s)
                sb.append(s);
            else if (value instanceof Number n) {
                if (value instanceof Integer i)
                    sb.append(i.intValue());
                else if (value instanceof Long l)
                    sb.append(l.longValue());
                else if (value instanceof Float f)
                    // NaN and Infinity are not valid JSON
                    if (f.isNaN() || f.isInfinite()) {
                        KofiLog.warning("invalid float value {" + f + "}");
                        sb.append("0.0");
                    }
                    else
                        sb.append(f.floatValue());
                else if (value instanceof Double d)
                    // NaN and Infinity are not valid JSON
                    if (d.isNaN() || d.isInfinite()) {
                        KofiLog.warning("invalid double value {" + d + "}");
                        sb.append("0.0");
                    }
                    else
                        sb.append(d.doubleValue());
                else if (value instanceof Byte b)
                    sb.append(b.byteValue());
                else if (value instanceof Short s)
                    sb.append(s.shortValue());
                    // AtomicInteger, AtomicLong, BigDecimal, BigInteger, DoubleAccumulator, DoubleAdder, LongAccumulator, LongAdder
                    // All these implementations override toString() to print their value
                else
                    sb.append(n);
            }
            else if (value instanceof Boolean b)
                sb.append(b.booleanValue());
            else if (value instanceof Json json)
                json.toJson(sb);
        }
        else {
            throw new IllegalArgumentException("value is not defined {" + value + "}");
        }
    }

    // TODO javadoc
    protected abstract void toJson(@NotNull final StringBuilder sb);
}
