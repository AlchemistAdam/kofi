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

import dk.martinu.kofi.JsonObject;
import dk.martinu.kofi.Property;

/**
 * {@link Property} implementation that holds a {@link JsonObject} value.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class ObjectProperty extends Property<JsonObject> implements Cloneable, Serializable,
        Iterable<JsonObject.Entry> {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new property with the specified {@code key} and
     * {@code value}. The key is not case-sensitive when compared to other
     * properties. If {@code value} is {@code null}, then the property value
     * will default to an empty {@code JsonObject}.
     *
     * @param key   The property key.
     * @param value The property value, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     */
    @Contract(pure = true)
    public ObjectProperty(@NotNull final String key, @Nullable final JsonObject value) throws NullPointerException {
        super(key, Objects.requireNonNullElse(value, new JsonObject()));
    }

    /**
     * Returns a copy of this property with the same property key and value.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public ObjectProperty clone() {
        return new ObjectProperty(key, value);
    }

    /**
     * Performs the given action for each entry in the {@code JsonObject} until
     * all entries have been processed or the action throws an exception.
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each entry.
     * @throws NullPointerException if {@code action} is {@code null}.
     */
    @Override
    public void forEach(final Consumer<? super JsonObject.Entry> action) {
        Objects.requireNonNull(action, "action is null");
        //noinspection ConstantConditions
        for (JsonObject.Entry o : value)
            action.accept(o);
    }

    /**
     * Returns {@code JsonObject.class}.
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public Class<JsonObject> getValueClass() {
        return JsonObject.class;
    }

    /**
     * Returns a {@code String} representation of this property's value.
     *
     * @see JsonObject#toJson()
     */
    @SuppressWarnings("ConstantConditions")
    @NotNull
    @Override
    public String getValueString() {
        return value.toJson();
    }

    /**
     * Returns the hash code of this property.
     */
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
     * Returns an iterator over the entries in this property's
     * {@code JsonObject}.
     *
     * @see JsonObject#iterator()
     */
    @NotNull
    @Override
    public Iterator<JsonObject.Entry> iterator() {
        //noinspection ConstantConditions
        return value.iterator();
    }

    /**
     * Returns a spliterator over the entries in this property's
     * {@code JsonObject}.
     *
     * @see JsonObject#spliterator()
     */
    @Override
    public Spliterator<JsonObject.Entry> spliterator() {
        //noinspection ConstantConditions
        return value.spliterator();
    }
}
