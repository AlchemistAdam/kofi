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

import java.util.Objects;

/**
 * An abstract KoFi value implementation. This class does not restrict what
 * value types can be represented, but is intended for complex value types,
 * such as arrays and objects.
 *
 * @author Adam Martinu
 * @see KofiArray
 * @see KofiObject
 * @since 1.0
 */
public abstract class KofiValue {

    /**
     * Returns a string representation of this value as it would appear in a
     * textual context, such as a file or graphical user interface.
     */
    @NotNull
    public abstract String getString();

    /**
     * Converts the specified string, assuming it is a KoFi string, to a plain
     * Java string and returns it; surrounding quotation marks are removed, and
     * all two-character and six-character escape sequences are unescaped to
     * their single character equivalent. The result of converting a string
     * that is not a KoFi string or an invalid KoFi string is undefined.
     *
     * @param string a KoFi string to convert
     * @return the converted Java string
     * @throws NullPointerException if {@code string} is {@code null}
     */
    @Contract(pure = true)
    @NotNull
    protected String getJavaString(@NotNull final String string) {
        Objects.requireNonNull(string, "string is null");
        return KofiUtil.unescape(string, 1, string.length() - 1);
    }

    /**
     * Converts the specified value, assuming it is a KoFi value, to a Java
     * value of the specified type and returns it. The result of converting a
     * value that is not a KoFi value or an invalid KoFi value is undefined.
     *
     * @param value a KoFi value to convert
     * @param type  the class of the converted value
     * @param <V>   the runtime type of the converted value
     * @return the converted Java value
     * @throws NullPointerException     if {@code value} or {@code type} is
     *                                  {@code null}
     * @throws IllegalArgumentException if a value cannot be converted to the
     *                                  specified type
     * @throws ReconstructionException  if an exception ocurred when
     *                                  reconstructing an array or object from
     *                                  a value
     * @see #getKofiValue(Object)
     */
    // DOC provide more details on how/what values are converted to
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    @NotNull
    protected <V> V getJavaValue(@NotNull final Object value, @NotNull final Class<V> type) {
        Objects.requireNonNull(value, "value is null");
        Objects.requireNonNull(type, "type is null");
        final KofiLog.Source src = new KofiLog.Source(KofiValue.class, "getJavaValue(Object, Class)");

        // arrays
        if (type.isArray()) {
            if (value instanceof KofiArray array)
                try {
                    return array.reconstruct(type);
                }
                catch (IllegalArgumentException e) {
                    throw KofiLog.exception(src, new ReconstructionException("could not reconstruct "
                            + type.getSimpleName() + " array from value {" + value + "}", e));
                }
            else
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot get " + type.getSimpleName() + " array from value {" + value + "}"));
        }

        // assignable Object types (defined)
        else if (type.isAssignableFrom(value.getClass())) {
            if (type.equals(String.class))
                return (V) getJavaString((String) value);
            else
                return (V) value; // wrapper type
        }

        // undefined and non-primitive objects
        else if (!type.isPrimitive()) {
            if (value instanceof KofiObject object) {
                try {
                    return object.reconstruct(type);
                }
                catch (ReflectiveOperationException e) {
                    throw KofiLog.exception(src, new ReconstructionException("could not reconstruct "
                            + type.getSimpleName() + " object from value {" + value + "}", e));
                }
            }
            else
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot get " + type.getSimpleName() + " object from value {" + value + "}"));
        }

        // int
        else if (type == int.class) {
            if (value instanceof Number n)
                return (V) Integer.valueOf(n.intValue());
            else
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot get int from value {" + value + "}"));
        }
        // long
        else if (type == long.class) {
            if (value instanceof Number n)
                return (V) Long.valueOf(n.longValue());
            else
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot get long from value {" + value + "}"));
        }
        // float
        else if (type == float.class) {
            if (value instanceof Number n)
                return (V) Float.valueOf(n.floatValue());
            else
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot get float from value {" + value + "}"));
        }
        // double
        else if (type == double.class) {
            if (value instanceof Number n)
                return (V) Double.valueOf(n.doubleValue());
            else
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot get double from value {" + value + "}"));
        }
        // byte
        else if (type == byte.class) {
            if (value instanceof Number n)
                return (V) Byte.valueOf(n.byteValue());
            else
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot get byte from value {" + value + "}"));
        }
        // short
        else if (type == short.class) {
            if (value instanceof Number n)
                return (V) Short.valueOf(n.shortValue());
            else
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot get short from value {" + value + "}"));
        }
        // char
        else if (type == char.class) {
            if (value instanceof Character c)
                return (V) c;
            else
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot get char from value {" + value + "}"));
        }
        // boolean
        else // if (type == boolean.class)
            if (value instanceof Boolean b)
                return (V) b;
            else
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot get boolean from value {" + value + "}"));
    }

    /**
     * Surrounds the specified string with quotation marks and escapes any
     * characters necessary, such that it conforms to the KoFi specification.
     *
     * @param string the string to convert
     * @return a KoFi string
     * @throws NullPointerException if {@code string} is {@code null}
     */
    @Contract(pure = true)
    @NotNull
    protected String getKofiString(@NotNull final String string) {
        return '"' + KofiUtil.escape(string, '\"') + '"';
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
     *         {@link #getKofiString(String) KoFi string} is returned.
     *     </li>
     *     <li>
     *         If {@code o} is an instance of {@code Object[]} then a
     *         {@code KofiArray} is
     *         {@link KofiArray#KofiArray(Object...) constructed} and returned.
     *     </li>
     *     <li>
     *         If {@code o} is an array as determined by
     *         {@link Class#isArray()}, then a {@code KofiArray} created by
     *         {@link KofiArray#reflect(Object) reflection} is returned.
     *     </li>
     *     <li>
     *         Otherwise a {@code KofiObject} created by
     *         {@link KofiObject#reflect(Object) reflection} is returned.
     *     </li>
     * </ul>
     *
     * @see KofiArray
     * @see KofiObject
     */
    @Contract(value = "null -> null; !null -> !null", pure = true)
    @Nullable
    protected Object getKofiValue(@Nullable final Object o) {
        if (isDefinedType(o)) {
            if (o instanceof String s)
                return getKofiString(s);
            else
                return o;
        }
        else if (o instanceof Object[] array)
            return new KofiArray(array);
        else if (o.getClass().isArray())
            return KofiArray.reflect(o);
        else
            return KofiObject.reflect(o);
    }

    /**
     * Creates a string representation of this object that conforms with the
     * KoFi Text Syntax and appends it to the specified {@code StringBuilder}.
     */
    protected abstract void getString(@NotNull final StringBuilder sb);

    /**
     * Appends a string representation of {@code value} to the specified
     * {@code StringBuilder}. This method assumes {@code value} is a defined
     * KoFi value. If {@code value} is an instance of {@code KofiValue}, then
     * its own {@link #getString(StringBuilder)} will be called.
     *
     * @throws IllegalArgumentException if {@code value} is not
     *                                  {@link #isDefinedType(Object) defined}
     * @throws NullPointerException     if {@code sb} is {@code null}
     * @see #getKofiValue(Object)
     */
    protected void getString(@Nullable Object value, @NotNull final StringBuilder sb) throws IllegalArgumentException {
        if (isDefinedType(value)) {
            if (value == null)
                sb.append("null");
            else if (value instanceof String s)
                sb.append(s);
            else if (value instanceof Number) {
                if (value instanceof Integer i)
                    sb.append(i.intValue());
                else if (value instanceof Long l)
                    sb.append(l.longValue()).append('L');
                else if (value instanceof Float f)
                    if (f.isNaN() || f.isInfinite())
                        sb.append(f.floatValue());
                    else
                        sb.append(f.floatValue()).append('F');
                else if (value instanceof Double d)
                    if (d.isNaN() || d.isInfinite())
                        sb.append(d.doubleValue());
                    else
                        sb.append(d.doubleValue()).append('d');
                else if (value instanceof Byte b)
                    sb.append(b.byteValue());
                else if (value instanceof Short s)
                    sb.append(s.shortValue());
            }
            else if (value instanceof Boolean b)
                sb.append(b.booleanValue());
            else if (value instanceof KofiValue kofiValue)
                kofiValue.getString(sb);
        }
        else
            throw KofiLog.exception(KofiValue.class, "getString(Object, StringBuilder)",
                    new IllegalArgumentException("value is not defined {" + value + "}"));
    }

    /**
     * Returns {@code true} if the type of the specified value is defined in
     * the KoFi specification, otherwise {@code false} is returned. The
     * following is a list of all types for which this method returns
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
     *         {@code Character}
     *     </li>
     *     <li>
     *         {@code Boolean}
     *     </li>
     *     <li>
     *         {@code Number} wrapper of a primitive type, such as {@code Integer}
     *     </li>
     *     <li>
     *         {@code KofiValue}
     *     </li>
     * </ul>
     *
     * @param value the value to test
     * @return {@code true} if the type value is defined, otherwise
     * {@code false}
     * @see #getKofiValue(Object)
     */
    @Contract(value = "null -> true", pure = true)
    protected boolean isDefinedType(@Nullable final Object value) {
        if (value == null
                || value instanceof String
                || value instanceof Character
                || value instanceof Boolean
                || value instanceof KofiValue)
            return true;
        else if (value instanceof Number)
            return value instanceof Integer
                    || value instanceof Long
                    || value instanceof Float
                    || value instanceof Double
                    || value instanceof Byte
                    || value instanceof Short;
        else
            return false;
    }
}
