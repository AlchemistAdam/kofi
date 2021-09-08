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
import java.util.*;

public class JsonArray implements Iterable<Object>, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    @NotNull
    protected Object[] array;

    public JsonArray(@Nullable final Object... values) {
        //noinspection NullableProblems
        array = Objects.requireNonNullElse(values, new Object[0]);
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
    public ArrayIterator iterator() {
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
