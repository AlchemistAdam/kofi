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

/**
 * Abstract implementation of a JSON value, which can be represented as a
 * string with {@link #toJson()}. The string representation must conform to the
 * IETF RFC 8259 specification.
 * <p>
 * See <a href="https://datatracker.ietf.org/doc/html/rfc8259"> RFC 8259</a>
 * for more details.
 *
 * @author Adam Martinu
 * @see JsonArray
 * @see JsonObject
 * @since 1.0
 */
public abstract class Json {

    /**
     * Returns a string representation of this object as JSON text that
     * conforms with the IETF RFC 8259 specification.
     * <p>
     * See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-2">
     * RFC 8259, section 2</a> for more details.
     */
    @NotNull
    public abstract String toJson();

    /**
     * Returns {@code true} if the two numbers {@code n0} and {@code n1} have
     * equal values. Comparing floating-point numbers to integer numbers
     * and vice-versa, or {@code null} numbers, will always return false.
     *
     * @param n0 The first number.
     * @param n1 The second number.
     * @return {@code true} if {@code n0} and {@code n1} have equal values,
     * otherwise {@code false}.
     */
    @Contract(value = "null, _ -> false; _, null -> false", pure = true)
    protected boolean areNumbersEqual(@Nullable final Number n0, @Nullable final Number n1) {
        // decimal numbers
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
        }
        // integer numbers - n0 and n1 are guaranteed not to be floating point
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
     * Returns an object whose type is guaranteed to be
     * {@link #isDefinedType(Object) defined}. The returned object is
     * determined in the following way:
     * <ul>
     *     <li>
     *         If {@code o} is already of a defined type, except
     *         {@code String}, then {@code o} is returned.
     *     </li>
     *     <li>
     *         If {@code o} is a {@code String}, then a
     *         {@link #getJsonString(String) JSON string} is returned.
     *     </li>
     *     <li>
     *         If {@code o} is a {@code Character}, then its numerical value is
     *         returned as an {@code Integer}.
     *     </li>
     *     <li>
     *         If {@code o} is an instance of {@code Object[]} then a
     *         {@code JsonArray} is
     *         {@link JsonArray#JsonArray(Object...) constructed} and returned.
     *     </li>
     *     <li>
     *         If {@code o} is an array as determined by
     *         {@link Class#isArray()}, then a {@code JsonArray} created by
     *         {@link JsonArray#reflect(Object) reflection} is returned.
     *     </li>
     *     <li>
     *         Otherwise a {@code JsonObject} created by
     *         {@link JsonObject#reflect(Object) reflection} is returned.
     *     </li>
     * </ul>
     *
     * @see JsonArray
     * @see JsonObject
     */
    @Contract(value = "null -> null; !null -> !null", pure = true)
    @Nullable
    protected Object getDefinedObject(@Nullable final Object o) {
        if (isDefinedType(o)) {
            if (o instanceof String s)
                return getJsonString(s);
            else
                return o;
        }
        // TODO needs testing when using reflection; int <--> char
        // characters are not in the JSON specification, using numerical value instead
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
     * Parses the specified string, as if it was a JSON string according to the
     * IETF RFC 8259 specification, to a plain Java string and returns it;
     * surrounding quotation marks are removed, and all two-character and
     * six-character escape sequences are unescaped to their single character
     * equivalent.
     * <p>
     * <b>NOTE:</b> this method assumes that {@code string} is a valid JSON
     * string. The result of parsing a string that is not a JSON string or an
     * invalid JSON string is undefined.
     * <p>
     * See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-7">
     * RFC 8259, section 7</a> for more details.
     *
     * @throws NullPointerException if {@code string} is {@code null}
     */
    @Contract(pure = true)
    @NotNull
    protected String getJavaString(@NotNull final String string) {
        final char[] chars = string.toCharArray();
        return KofiUtil.unescape(chars, 1, chars.length - 1);
    }

    // TODO this escapes u+0000 as \0, which is not in the specification

    /**
     * Surrounds the specified string with quotation marks and escapes any
     * characters necessary, such that it conforms to the IETF RFC 8259
     * specification.
     * <p>
     * See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-7">
     * RFC 8259, section 7</a> for more details.
     *
     * @throws NullPointerException if {@code string} is {@code null}
     */
    @Contract(pure = true)
    @NotNull
    protected String getJsonString(@NotNull final String string) {
        return '"' + KofiUtil.escape(string, '\"') + '"';
    }

    /**
     * Returns {@code true} if the type of the specified object is defined in
     * the IETF RFC 8259 specification. Otherwise {@code false} is returned.
     * The following is a list of all types for which this method returns
     * {@code true}:
     * <ul>
     *     <li>
     *         {@code null} (not specifically a type but {@code null} is a
     *         defined value)
     *     </li>
     *     <li>
     *         {@code String}
     *     </li>
     *     <li>
     *         {@code Number} wrapper of a primitive type, such as {@code Integer}
     *     </li>
     *     <li>
     *         {@code Boolean}
     *     </li>
     *     <li>
     *         {@code Json}
     *     </li>
     * </ul>
     * <p>
     * See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-3">
     * RFC 8259, section 3</a> for more details.
     *
     * @see #getDefinedObject(Object)
     */
    @Contract(value = "null -> true", pure = true)
    protected boolean isDefinedType(@Nullable final Object o) {
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
     * Creates a JSON string representation of {@code value} and appends it to
     * the specified {@code StringBuilder}. This method is intended for
     * implementations that represent an array or object which can have member
     * values. If {@code value} is an instance of {@code Json}, then its own
     * {@link #toJson(StringBuilder)} will be called.
     * <p>
     * <b>NOTE:</b> irrational numbers such as {@code Infinity} or {@code NaN}
     * are represented as {@code 0.0}.
     * <p>
     * See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-6">
     * RFC 8259, section 6</a> for more details.
     *
     * @throws IllegalArgumentException if {@code value} is not
     *                                  {@link #isDefinedType(Object) defined}
     * @throws NullPointerException     if {@code sb} is {@code null}
     */
    protected void toJson(@Nullable Object value, @NotNull final StringBuilder sb) throws IllegalArgumentException {
        if (isDefinedType(value)) {
            if (value == null)
                sb.append("null");
            else if (value instanceof String s)
                sb.append(s); // TODO convert to JSON string?
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
                else if (value instanceof Short s) {
                    sb.append(s.shortValue());
                }
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
     * Creates a JSON string representation of this JSON object that conforms
     * with the IETF RFC 8259 specification and appends it to the specified
     * {@code StringBuilder}.
     * <p>
     * See <a href="https://datatracker.ietf.org/doc/html/rfc8259#section-2">
     * RFC 8259, section 2</a> for more details.
     */
    protected abstract void toJson(@NotNull final StringBuilder sb);
}
