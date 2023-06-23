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

import dk.martinu.kofi.KofiUtil;
import dk.martinu.kofi.Property;

/**
 * A {@link Property} that holds a character value.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class CharProperty extends Property<Character> implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new property with the specified key and value. If
     * {@code value} is {@code null}, then the property value will default to
     * the {@code \0 U+0000} Null character.
     *
     * @param key   The property key
     * @param value The property value, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Contract(pure = true)
    public CharProperty(@NotNull final String key, @Nullable final Character value) {
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
     * Returns a string representation of this property's value. The returned
     * string is equal to (with some exceptions):
     * <pre>
     *     "\'<i>value</i>\'"
     * </pre>
     * <p>
     * If this property's value is in the range 0-1F, inclusive, then it will
     * be represented as a two-character or six-character escape sequence
     * instead.
     *
     * @see KofiUtil#escape_00_1F(char)
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getValueString() {
        if (value < 0x20)
            return '\'' + KofiUtil.escape_00_1F(value) + '\'';
        else
            return new String(new char[] {'\'', value, '\''});
    }
}
