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

import static dk.martinu.kofi.KofiUtil.isHexDigit;

/**
 * <p>Abstract implementation of a JSON value, which can be represented as a
 * string with {@link #toJson()}. The string representation must conform to the
 * IETF RFC 8259 specification.
 *
 * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259"> RFC 8259</a>
 * for more details.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public abstract class Json {

    /**
     * <p>Array of precomputed strings for escaping characters in the range
     * [0x00;0x1F] as a six-character sequence, except for those characters
     * which are allowed to be escaped as a two-character sequence.
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-7">
     * RFC 8259, section 7</a> for more details.
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

    /**
     * <p>Returns a string representation of this object as JSON text that
     * conforms with the IETF RFC 8259 specification.
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-2">
     * RFC 8259, section 2</a> for more details.
     */
    @NotNull
    public abstract String toJson();

    /**
     * Returns {@code true} if the two numbers {@code n0} and {@code n1} have
     * equal values. Comparison of floating-point numbers with integer numbers
     * and vice-versa will always return false.
     *
     * @param n0 The first number.
     * @param n1 The second number.
     * @return {@code true} if {@code n0} and {@code n1} have equal values,
     * otherwise {@code false}.
     */
    protected boolean areNumbersEqual(@NotNull final Number n0, @NotNull final Number n1) {
        // decimals
        if (n0 instanceof Double) {
            if (n1 instanceof Double || n1 instanceof Float)
                return n0.doubleValue() == n1.doubleValue();
        }
        else if (n1 instanceof Double) {
            if (n0 instanceof Float)
                return n0.doubleValue() == n1.doubleValue();
        }
        else if (n0 instanceof Float) {
            if (n1 instanceof Float)
                return n0.floatValue() == n1.floatValue();
            else
                return false;
        }
        // integers
        else if (n0 instanceof Long) {
            if (n1 instanceof Long || n1 instanceof Integer || n1 instanceof Short || n1 instanceof Byte)
                return n0.longValue() == n1.longValue();
        }
        else if (n1 instanceof Long) {
            if (n0 instanceof Integer || n0 instanceof Short || n0 instanceof Byte)
                return n0.longValue() == n1.longValue();
        }
        else if (n0 instanceof Integer) {
            if (n1 instanceof Integer || n1 instanceof Short || n1 instanceof Byte)
                return n0.intValue() == n1.intValue();
        }
        else if (n1 instanceof Integer) {
            if (n0 instanceof Short || n0 instanceof Byte)
                return n0.intValue() == n1.intValue();
        }
        else if (n0 instanceof Short) {
            if (n1 instanceof Short || n1 instanceof Byte)
                return n0.shortValue() == n1.shortValue();
        }
        else if (n1 instanceof Short) {
            if (n0 instanceof Byte)
                return n0.shortValue() == n1.shortValue();
        }
        else if (n0 instanceof Byte && n1 instanceof Byte) {
            return n0.byteValue() == n1.byteValue();
        }
        return false;
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

    /**
     * <p>Parses the specified string {@code s}, as if it was a JSON string
     * according to the IETF RFC 8259 specification, to a plain Java string and
     * returns it. Surrounding quotation marks are removed, and all
     * two-character and six-character escape sequences are unescaped to their
     * single character equivalent.
     *
     * <p><b>NOTE:</b> this method assumes that {@code s} is a JSON string and
     * that it is valid. The behaviour of passing in a string that is not a
     * JSON string or an invalid JSON string is undefined.
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-7">
     * RFC 8259, section 7</a> for more details.
     */
    protected String getJavaString(@NotNull final String s) {
        final char[] chars = s.toCharArray();
        return KofiUtil.unescape(chars, 0, chars.length);
    }

    /**
     * <p>Surrounds the specified string {@code s} with quotation marks and
     * escapes any characters necessary, such that it conforms to the IETF RFC
     * 8259 specification, with the addition that control characters in the
     * range [0x7F;0x9F] are also escaped.
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-7">
     * RFC 8259, section 7</a> for more details.
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
     *     <li>{@code Number} wrapper of a primitive type, e.g. {@code Integer}</li>
     *     <li>{@code Boolean}</li>
     *     <li>{@link Json}. The KOFI API provides implementations for objects
     *     and arrays (the only structures defined in the specification), but
     *     any implementation of {@code Json} will be accepted as a defined
     *     type.</li>
     * </ul>
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-3">
     * RFC 8259, section 3</a> for more details.
     */
    @Contract(value = "null -> true", pure = true)
    protected boolean isTypeDefined(@Nullable final Object o) {
        if (o == null
                || o instanceof String
                || o instanceof Boolean
                || o instanceof Json)
            return true;
        else if (o instanceof Number)
            return o instanceof Integer
                    || o instanceof Long
                    || o instanceof Float
                    || o instanceof Double
                    || o instanceof Byte
                    || o instanceof Short;
        else
            return false;
    }

    /**
     * <p>Creates a JSON string representation of the specified object
     * {@code value} and appends it to the specified {@code StringBuilder}
     * {@code sb}. This method is intended for implementations that represent
     * an array or object which can have member values.
     *
     * <p><b>NOTE:</b> The IETF RFC 8259 specification does not permit
     * irrational numbers such as {@code Infinity} or {@code NaN}; these values
     * are represented as {@code 0.0}.
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-6">
     * RFC 8259, section 6</a> for more details.
     *
     * @throws IllegalArgumentException if {@code value} is not
     *                                  {@link #isTypeDefined(Object) defined}.
     */
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

    /**
     * <p>Creates a JSON string representation of this JSON object and appends
     * it to the specified {@code StringBuilder} {@code sb}.
     */
    protected abstract void toJson(@NotNull final StringBuilder sb);
}
