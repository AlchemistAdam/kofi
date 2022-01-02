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

import dk.martinu.kofi.Element;
import dk.martinu.kofi.Property;

/**
 * A {@link Property} whose value is always {@code null}. Because a
 * {@code null} value does not inherently belong to a specific value type, the
 * value type of {@code NullProperty} is {@code Object}. This means that all
 * (except primitive) types will {@link #matches(Class) match} against an
 * instance of this class.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class NullProperty extends Property<Object> implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new property with the specified key.
     *
     * @param key The property key
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Contract(pure = true)
    public NullProperty(@NotNull final String key) {
        super(key);
    }

    /**
     * Returns a copy of this property with the same property key.
     */
    @Contract(value = "-> new", pure = true)
    @Override
    @NotNull
    public Element clone() {
        return new NullProperty(key);
    }

    /**
     * Returns {@code Object.class}.
     */
    @Contract(pure = true)
    @Override
    @NotNull
    public Class<Object> getValueClass() {
        return Object.class;
    }

    /**
     * Returns the string {@code "null"}.
     */
    @Contract(pure = true)
    @Override
    @NotNull
    public String getValueString() {
        return "null";
    }
}
