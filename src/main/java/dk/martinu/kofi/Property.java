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

public abstract class Property<V> extends Element<V> {

    @NotNull
    protected final String key;
    @NotNull
    protected final V value;

    @Contract(pure = true)
    public Property(@NotNull final String key, @NotNull final V value) throws NullPointerException {
        this.key = Objects.requireNonNull(key, "key is null");
        this.value = Objects.requireNonNull(value, "value is null");
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof Property<?> p)
            return key.equalsIgnoreCase(p.key) && value.equals(p.value);
        else
            return false;
    }

    @Contract(pure = true)
    @NotNull
    public String getKey() {
        return key;
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    public String getKeyString() {
        final StringBuilder keyBuilder = new StringBuilder(key.length());
        for (int i = 0; i < key.length(); i++)
            switch (key.charAt(i)) {
                // TODO add missing escape sequences
                case '[', ']', ';', '=', '\r', '\n', '\\' -> keyBuilder.append('\\').append(key.charAt(i));
                default -> keyBuilder.append(key.charAt(i));
            }
        return keyBuilder.toString();
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getString() {
        return getKeyString() + " = " + getValueString();
    }

    @Contract(pure = true)
    @NotNull
    public V getValue() {
        return value;
    }

    @Contract(pure = true)
    @NotNull
    public abstract Class<? super V> getValueClass();

    @Contract(pure = true)
    @NotNull
    public abstract String getValueString();

    @Contract(pure = true)
    public boolean matches(@NotNull final String key) throws NullPointerException {
        Objects.requireNonNull(key, "key is null");
        return this.key.equalsIgnoreCase(key);
    }

    // TODO use is assignable from if possible
    @Contract(pure = true)
    public boolean matches(@NotNull final String key, @Nullable final Class<?> valueType) throws NullPointerException {
        Objects.requireNonNull(key, "key is null");
        if (this.key.equalsIgnoreCase(key)) {
            if (valueType == null)
                return true;
            else
                return getValueClass().equals(valueType);
        }
        else
            return false;
    }

    // TODO use is assignable from if possible
    @Contract(value = "null -> true", pure = true)
    public boolean matches(@Nullable final Class<?> valueType) {
        if (valueType == null)
            return true;
        else
            return getValueClass().equals(valueType);
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String toString() {
        return this.getClass().getName() + '@' + super.hashCode() + "{key=" + key + ", value=" + value + '}';
    }

    @Contract(pure = true)
    @Override
    protected int getHash() {
        return key.toLowerCase().hashCode() | value.hashCode() << 16;
    }
}
