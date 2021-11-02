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
import java.util.concurrent.atomic.*;

public class JsonArray extends Json implements Iterable<Object>, Serializable {

    protected static final Object[] EMPTY = new Object[0];
    @Serial
    private static final long serialVersionUID = 0L;

    @NotNull
    public static JsonArray reflect(@NotNull final Object object) throws NullPointerException,
            IllegalArgumentException {
        Objects.requireNonNull(object, "object is null");
        if (!object.getClass().isArray())
            throw new IllegalArgumentException("object is not an array");
        final int len = Array.getLength(object);
        // list of reflected array values
        final ArrayList<Object> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++)
            list.add(Array.get(object, i));
        return new JsonArray(list);
    }

    protected final Object[] array;

    public JsonArray() {
        array = EMPTY;
    }

    public JsonArray(@Nullable final Object... values) {
        if (values != null && values.length != 0) {
            final ArrayList<Object> list = new ArrayList<>();
            for (final Object value : values)
                list.add(getDefinedObject(value));
            array = list.toArray(new Object[list.size()]);
        }
        else
            array = EMPTY;
    }

    /**
     * Constructs a {@code JsonArray} wrapping the values in the specified
     * {@code list}.
     */
    public JsonArray(@NotNull final List<Object> list) throws NullPointerException {
        Objects.requireNonNull(list, "list is null");
        if (!list.isEmpty())
            array = list.parallelStream()
                    .map(this::getDefinedObject)
                    .toArray(Object[]::new);
        else
            array = EMPTY;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof JsonArray json && array.length == json.length()) {
            for (int i = 0; i < array.length; i++)
                if (!Objects.equals(array[i], json.get(i))
                        && !(array[i] instanceof Number n0
                        && json.get(i) instanceof Number n1
                        && areNumbersEqual(n0, n1)))
                    return false;
            return true;
        }
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

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String toJson() {
        final StringBuilder sb = new StringBuilder(length() * 8);
        toJson(sb);
        return sb.toString();
    }

    // TODO javadoc
    @Contract(pure = true)
    @Override
    public void toJson(@NotNull final StringBuilder sb) {
        sb.append('[');
        for (int index = 0; index < array.length; index++) {
            if (index > 0)
                sb.append(',');
            sb.append(' ');
            toJson(array[index], sb);
        }
        sb.append(" ]");
    }

    public class ArrayIterator implements Iterator<Object> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < array.length;
        }

        @Override
        public Object next() throws NoSuchElementException {
            if (!hasNext())
                throw new NoSuchElementException();
            return array[index++];
        }
    }
}
