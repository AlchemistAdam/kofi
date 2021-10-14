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

package dk.martinu.kofi.properties;

import org.jetbrains.annotations.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import dk.martinu.kofi.Property;

/**
 * {@link Property} implementation that holds an {@code Character} value.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class CharProperty extends Property<Character> implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new property with the specified {@code key} and
     * {@code value}. The key is not case-sensitive when compared to other
     * properties. If {@code value} is {@code null}, then the property value
     * will default to the null character ({@code U+0000}).
     *
     * @param key   The property key.
     * @param value The property value, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     */
    @Contract(pure = true)
    public CharProperty(@NotNull final String key, @Nullable final Character value) throws NullPointerException {
        super(key, Objects.requireNonNullElse(value, (char) 0));
    }

    /**
     * Returns a copy of this property with the same property key and value.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public CharProperty clone() {
        return new CharProperty(key, value);
    }

    /**
     * Returns {@code Character.class}.
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public Class<Character> getValueClass() {
        return Character.class;
    }

    /**
     * <p>Returns a {@code String} representation of this property's value. The
     * returned string is equal to (with some exceptions):
     * <pre>
     *     "'" + value + "'"
     * </pre>
     * <p>If this property's value is equal to any of the escapable characters in
     * the Java language, then the escaped version is used instead. E.g. if
     * {@code value} is equal to {@code '\t'} then the returned string is equal
     * to:
     * <pre>
     *     "'\\t'"
     * </pre>
     * The following is a list of all such characters:
     * <ul>
     *     <li>{@code \t} horizontal tabulation, U+0009</li>
     *     <li>{@code \b} backspace, U+0008</li>
     *     <li>{@code \n} new line, U+000A</li>
     *     <li>{@code \r} carriage return, U+000D</li>
     *     <li>{@code \f} form feed, U+000C</li>
     *     <li>{@code \0} null, U+0000</li>
     * </ul>
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getValueString() {
        //noinspection ConstantConditions
        return switch (value) {
            case '\t' -> "'\\t'";
            case '\b' -> "'\\b'";
            case '\n' -> "'\\n'";
            case '\r' -> "'\\r'";
            case '\f' -> "'\\f'";
            case '\0' -> "'\\0'";
            default -> String.copyValueOf(new char[] {'\'', value, '\''});
        };
    }
}
