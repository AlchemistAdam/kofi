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

import java.util.Locale;
import java.util.Objects;

import dk.martinu.kofi.properties.NullProperty;

/**
 * An abstract {@link Element element} used to store data as a key-value pair.
 * Property implementations provided by the KOFI API are located in the
 * {@link dk.martinu.kofi.properties} package. Properties have a string key and
 * a value of type {@code V}. Keys are not case-sensitive when compared. The
 * string representation of a property, returned by {@link #getString()}, is
 * equal to:
 * <pre>
 *     "<i>key</i> = <i>value</i>"
 * </pre>
 *
 * @param <V> the property value type
 * @author Adam Martinu
 * @since 1.0
 */
public abstract class Property<V> extends Element {

    /**
     * The property key.
     */
    @NotNull
    public final String key;
    /**
     * The property value
     */
    @Nullable
    public final V value;
    /**
     * Cached hash code.
     */
    protected transient int hash = 0;
    /**
     * {@code true} if the computed hash code is {@code 0}.
     */
    protected transient boolean hashIsZero = false;

    /**
     * Constructs a new property with the specified key and value.
     *
     * @param key   The property key
     * @param value The property value
     * @throws NullPointerException if {@code key} or {@code value} is
     *                              {@code null}
     */
    @Contract(pure = true)
    public Property(@NotNull final String key, @NotNull final V value) {
        this.key = Objects.requireNonNull(key, "key is null");
        this.value = Objects.requireNonNull(value, "value is null");
    }

    /**
     * Protected constructor for property implementations with null values,
     * such as {@link NullProperty}.
     *
     * @param key The property key
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Contract(pure = true)
    protected Property(@NotNull final String key) {
        this.key = Objects.requireNonNull(key, "key is null");
        value = null;
    }

    /**
     * Returns {@code true} if this property is equal to {@code obj}
     * ({@code this == obj}), or {@code obj} is also a property and their keys
     * are equal, ignoring case, and their values are equal. Otherwise
     * {@code false} is returned.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof Property<?> p)
            if (key.equalsIgnoreCase(p.key))
                return Objects.equals(value, p.value);
            else
                return false;
        else
            return false;
    }

    /**
     * Returns an escaped version of this property's key. Note that all
     * {@code ; U+003B} Semicolon and {@code [ U+005B} Left Square Bracket
     * characters are also escaped.
     *
     * @see KofiUtil#escape(String, char...)
     */
    @Contract(pure = true)
    @NotNull
    public String getKeyString() {
        return KofiUtil.escape(key, ';', '[');
    }

    /**
     * Returns a string representation of this property, equal to:
     * <pre>
     *     "<i>key</i> = <i>value</i>"
     * </pre>
     * where <i>key</i> and <i>value</i> are equal to the strings returned by
     * {@link #getKeyString()} and {@link #getValueString()} respectively.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getString() {
        return getKeyString() + " = " + getValueString();
    }

    /**
     * Returns the class of this property's value.
     */
    @Contract(pure = true)
    @NotNull
    public abstract Class<? super V> getValueClass();

    /**
     * Returns a string representation of this property's value.
     */
    @Contract(pure = true)
    @NotNull
    public abstract String getValueString();

    /**
     * Returns a combined hash code of this property's key, in upper-case,
     * and value. The returned value is equal to:
     * <pre>
     *     keyHash | valueHash << 16
     * </pre>
     * <p>
     * <b>NOTE:</b> if the state of this property's value is mutable, then this
     * method should be overridden. The default implementation caches the hash
     * code and will not reflect changes to the value's state after the first
     * call.
     */
    @Contract(pure = true)
    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0 && !hashIsZero) {
            h = value != null ?
                    key.toUpperCase(Locale.ROOT).hashCode() | value.hashCode() << 16 :
                    key.toUpperCase(Locale.ROOT).hashCode();
            if (h == 0)
                hashIsZero = true;
            else
                hash = h;
        }
        return h;
    }

    /**
     * Returns {@code true} if the key of this property is equal to
     * {@code key}, ignoring case. Otherwise {@code false} is returned.
     *
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Contract(pure = true)
    public boolean matches(@NotNull final String key) {
        Objects.requireNonNull(key, "key is null");
        return this.key.equalsIgnoreCase(key);
    }

    /**
     * Returns {@code true} if this property matches the specified {@code key}
     * and {@code valueType}. Otherwise {@code false} is returned.
     *
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #matches(String)
     * @see #matches(Class)
     */
    @Contract(pure = true)
    public boolean matches(@NotNull final String key, @Nullable final Class<?> valueType) {
        return matches(key) && matches(valueType);
    }

    /**
     * Returns {@code true} if {@code valueType} is assignable from the value
     * type of this property or equal to {@code null}. Otherwise {@code false}
     * is returned.
     *
     * @see Class#isAssignableFrom(Class)
     */
    @Contract(value = "null -> true", pure = true)
    public boolean matches(@Nullable final Class<?> valueType) {
        if (valueType != null)
            return valueType.isAssignableFrom(getValueClass());
        else
            return true;
    }

    /**
     * Returns a string representation of this property, equal to:
     * <pre>
     *     "<i>className</i>@<i>hashCode</i>{key=<i>key</i>, value=<i>value</i>}"
     * </pre>
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public String toString() {
        return this.getClass().getName() + '@' + super.hashCode() + "{key=" + key + ", value=" + value + '}';
    }
}
