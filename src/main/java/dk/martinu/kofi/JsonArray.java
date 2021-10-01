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

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

public class JsonArray extends Json implements Iterable<Object>, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    @NotNull
    public static JsonArray reflect(@NotNull final Object object) throws NullPointerException,
            IllegalArgumentException {
        Objects.requireNonNull(object, "object is null");
        if (!object.getClass().isArray())
            throw new IllegalArgumentException("object is not an array");
        // list of values
        final ArrayList<Object> list = new ArrayList<>();
        // serialize and store values from array object in list
        final int len = Array.getLength(object);
        for (int i = 0; i < len; i++)
            list.add(getKnownType(Array.get(object, i)));
        return new JsonArray(list);
    }

    @NotNull
    protected final Object[] array;

    public JsonArray(@Nullable final Object... values) {
        if (values != null) {
            // list of values
            final ArrayList<Object> list = new ArrayList<>();
            // serialize and store values in list
            for (final Object value : values)
                list.add(getKnownType(value));
            array = list.toArray(new Object[list.size()]);
        }
        else
            array = new Object[0];
    }

    /**
     * Private constructor used by {@link #reflect(Object)}. The list is
     * guaranteed to only contain known types.
     *
     * @param list the list of values in this JSON array.
     * @see Json#isKnownType(Object)
     */
    private JsonArray(@NotNull final List<Object> list) {
        array = list.toArray(new Object[list.size()]);
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof JsonArray JsonArray)
            return Arrays.equals(array, JsonArray.array);
        else
            return false;
    }

    public Object get(final int index) throws ArrayIndexOutOfBoundsException {
        return array[index];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return new ArrayIterator();
    }

    public int length() {
        return array.length;
    }

    @Override
    public Spliterator<Object> spliterator() {
        return Arrays.spliterator(array);
    }

    public class ArrayIterator implements Iterator<Object> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < array.length;
        }

        @Override
        public Object next() {
            return array[index++];
        }
    }
}
