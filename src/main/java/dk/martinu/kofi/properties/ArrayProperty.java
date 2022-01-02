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
import java.util.*;
import java.util.function.Consumer;

import dk.martinu.kofi.JsonArray;
import dk.martinu.kofi.Property;

/**
 * A {@link Property} that holds a {@link JsonArray} value.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class ArrayProperty extends Property<JsonArray> implements Cloneable, Serializable, Iterable<Object> {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new property with the specified key and value. If
     * {@code value} is {@code null}, then the property value will default to
     * an empty array.
     *
     * @param key   The property key
     * @param value The property value, can be {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Contract(pure = true)
    public ArrayProperty(@NotNull final String key, @Nullable final JsonArray value) {
        super(key, value != null ? value : new JsonArray());
    }

    /**
     * Returns a copy of this property with the same property key and value.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public ArrayProperty clone() {
        return new ArrayProperty(key, value);
    }

    /**
     * Performs the specified action for each element in the array until all
     * elements have been processed or the action throws an exception.
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to perform on each element
     * @throws NullPointerException if {@code action} is {@code null}
     */
    @Override
    public void forEach(@NotNull final Consumer<? super Object> action) {
        Objects.requireNonNull(action, "action is null");
        //noinspection ConstantConditions
        for (Object o : value)
            action.accept(o);
    }

    /**
     * Returns {@code JsonArray.class}.
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public Class<JsonArray> getValueClass() {
        return JsonArray.class;
    }

    /**
     * Returns a {@code String} representation of this property's value.
     *
     * @see JsonArray#toJson()
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public String getValueString() {
        //noinspection ConstantConditions
        return value.toJson();
    }

    /**
     * Returns a combined hash code of this property's key, in upper-case, and
     * value. The returned value is equal to:
     * <pre>
     *     keyHash | valueHash << 16
     * </pre>
     */
    @Contract(pure = true)
    @Override
    public int hashCode() {
        // key hash is immutable and is cached
        int h = hash;
        if (h == 0 && !hashIsZero) {
            h = key.toUpperCase(Locale.ROOT).hashCode();
            if (h == 0)
                hashIsZero = true;
            else
                hash = h;
        }
        // value hash is mutable and must be computed each time
        //noinspection ConstantConditions
        return h | value.hashCode() << 16;
    }

    /**
     * Returns an iterator over the elements in this property's
     * {@code JsonArray}.
     *
     * @see JsonArray#iterator()
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Iterator<Object> iterator() {
        //noinspection ConstantConditions
        return value.iterator();
    }

    /**
     * Returns a spliterator over the elements in this property's
     * {@code JsonArray}.
     *
     * @see JsonArray#spliterator()
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Spliterator<Object> spliterator() {
        //noinspection ConstantConditions
        return value.spliterator();
    }
}
