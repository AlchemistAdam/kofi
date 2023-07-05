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

import dk.martinu.kofi.KofiObject;
import dk.martinu.kofi.Property;

/**
 * A {@link Property} that holds a {@link KofiObject} value. The value of an
 * {@code ObjectProperty} can never be {@code null}.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class ObjectProperty extends Property<KofiObject> implements Cloneable, Serializable,
        Iterable<KofiObject.Entry> {

    @Serial
    private static final long serialVersionUID = 202307052159L;

    /**
     * Constructs a new property with the specified key and value. If
     * {@code value} is {@code null}, then the property value will default to
     * an empty {@code KofiObject}.
     *
     * @param key   The property key
     * @param value The property value, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Contract(pure = true)
    public ObjectProperty(@NotNull final String key, @Nullable final KofiObject value) {
        super(key, Objects.requireNonNullElse(value, new KofiObject()));
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
     * Performs the given action for each entry in the object until all entries
     * have been processed or the action throws an exception. Exceptions thrown
     * by the action are relayed to the caller.
     *
     * @param action The action to be performed for each entry
     * @throws NullPointerException if {@code action} is {@code null}
     */
    @Override
    public void forEach(@NotNull final Consumer<? super KofiObject.Entry> action) {
        Objects.requireNonNull(action, "action is null");
        for (KofiObject.Entry o : value)
            action.accept(o);
    }

    /**
     * Returns {@code KofiObject.class}.
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public Class<KofiObject> getValueClass() {
        return KofiObject.class;
    }

    /**
     * Returns a string representation of this property's value.
     *
     * @see KofiObject#getString()
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public String getValueString() {
        return value.getString();
    }

    /**
     * Returns a combined hash code of this property's key, in upper-case, and
     * value. The returned value is equal to:
     * <pre>
     *     keyHash | valueHash &lt;&lt; 16
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
        return h | value.hashCode() << 16;
    }

    /**
     * Returns an iterator over the entries in this property's
     * {@code KofiObject} value.
     *
     * @see KofiObject#iterator()
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Iterator<KofiObject.Entry> iterator() {
        return value.iterator();
    }

    /**
     * Returns a spliterator over the entries in this property's
     * {@code KofiObject} value.
     *
     * @see KofiObject#spliterator()
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Spliterator<KofiObject.Entry> spliterator() {
        return value.spliterator();
    }
}
