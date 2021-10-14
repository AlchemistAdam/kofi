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
 * {@link Property} implementation that holds an {@code Integer} value.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class IntProperty extends Property<Integer> implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new property with the specified {@code key} and
     * {@code value}. The key is not case-sensitive when compared to other
     * properties. If {@code value} is {@code null}, then the property value
     * will default to {@code 0}.
     *
     * @param key   The property key.
     * @param value The property value, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     */
    @Contract(pure = true)
    public IntProperty(@NotNull final String key, @Nullable final Integer value) throws NullPointerException {
        super(key, Objects.requireNonNullElse(value, 0));
    }

    /**
     * Returns a copy of this property with the same property key and value.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public IntProperty clone() {
        return new IntProperty(key, value);
    }

    /**
     * Returns {@code Integer.class}.
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public Class<Integer> getValueClass() {
        return Integer.class;
    }

    /**
     * Returns a {@code String} representation of this property's value. The
     * returned string is equal to:
     * <pre>
     *     Integer.toString(value)
     * </pre>
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getValueString() {
        //noinspection ConstantConditions
        return Integer.toString(value);
    }
}
