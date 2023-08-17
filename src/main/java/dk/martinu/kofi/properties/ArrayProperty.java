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

import dk.martinu.kofi.KofiArray;
import dk.martinu.kofi.Property;

/**
 * A {@link Property} that holds a {@link KofiArray} value. The value of an
 * {@code ArrayProperty} can never be {@code null}.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class ArrayProperty extends Property<KofiArray> implements Cloneable, Serializable, Iterable<Object> {

    @Serial
    private static final long serialVersionUID = 202307052159L;

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
    public ArrayProperty(@NotNull final String key, @Nullable final KofiArray value) {
        super(key, value != null ? value : new KofiArray());
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
        //noinspection DataFlowIssue
        for (Object o : value)
            action.accept(o);
    }

    /**
     * Returns {@code KofiArray.class}.
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public Class<KofiArray> getValueClass() {
        return KofiArray.class;
    }

    /**
     * Returns a {@code String} representation of this property's value.
     *
     * @see KofiArray#getString()
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public String getValueString() {
        //noinspection DataFlowIssue
        return value.getString();
    }

    /**
     * Returns an iterator over the elements in this property's
     * {@code KofiArray} value.
     *
     * @see KofiArray#iterator()
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Iterator<Object> iterator() {
        //noinspection DataFlowIssue
        return value.iterator();
    }

    /**
     * Returns a spliterator over the elements in this property's
     * {@code KofiArray} value.
     *
     * @see KofiArray#spliterator()
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Spliterator<Object> spliterator() {
        //noinspection DataFlowIssue
        return value.spliterator();
    }
}
