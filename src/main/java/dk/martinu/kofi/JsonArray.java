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
import java.util.function.Consumer;

import dk.martinu.kofi.properties.ArrayProperty;

/**
 * {@link Json} implementation of an immutable JSON array value.
 *
 * @author Adam Martinu
 * @see ArrayProperty
 * @since 1.0
 */
public class JsonArray extends Json implements Iterable<Object>, Serializable {

    /**
     * Empty, zero-length object array constant.
     */
    protected static final Object[] EMPTY = new Object[0];
    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new {@code JsonArray} that wraps around the specified array
     * using reflection and returns it. Removing or inserting elements in
     * {@code array} will not change the returned array.
     *
     * @param array the array to reflect
     * @return a new {@code JsonArray}
     * @throws NullPointerException     if {@code array} is {@code null}
     * @throws IllegalArgumentException if {@code array} is not an array type,
     *                                  determined by {@link Class#isArray()}
     * @see #JsonArray(Object...)
     */
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static JsonArray reflect(@NotNull final Object array) throws IllegalArgumentException {
        Objects.requireNonNull(array, "array is null");
        if (!array.getClass().isArray())
            throw new IllegalArgumentException("array is not an array type");
        final int len = Array.getLength(array);
        // list of reflected array values
        final ArrayList<Object> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++)
            list.add(Array.get(array, i));
        return new JsonArray(list);
    }
    /**
     * The objects contained in this array. Each object is guaranteed to be
     * defined.
     *
     * @see Json#getDefinedObject(Object)
     */
    @NotNull
    protected final Object[] array;

    /**
     * Construct a new, empty {@code JsonArray}.
     */
    @Contract(pure = true)
    public JsonArray() {
        array = EMPTY;
    }

    /**
     * Constructs a new {@code JsonArray} containing the defined objects of
     * {@code values}.
     *
     * @param values the array objects, or {@code null}
     * @see Json#getDefinedObject(Object)
     */
    @Contract(pure = true)
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
     * Constructs a new {@code JsonArray} containing the defined objects of the
     * values in the specified list.
     *
     * @param list the list of objects, or {@code null}
     * @see Json#getDefinedObject(Object)
     */
    @Contract(pure = true)
    public JsonArray(@Nullable final List<Object> list) {
        if (list != null && !list.isEmpty())
            array = list.parallelStream()
                    .map(this::getDefinedObject)
                    .toArray(Object[]::new);
        else
            array = EMPTY;
    }

    /**
     * Returns {@code true} if this array is equal to {@code obj}
     * ({@code this == obj}), or {@code obj} is also a {@code JsonArray} and
     * its length and elements are equal to this array's length and elements.
     * Otherwise {@code false} is returned.
     */
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
        for (Object o : array)
            action.accept(o);
    }

    /**
     * Returns the object at the specified index in this array.
     *
     * @param index the index of the object
     * @return the element at the specified index, can be {@code null}
     * @throws ArrayIndexOutOfBoundsException if {@code index} is out of bounds
     *                                        {@code (index < 0 || index >= length())}
     */
    @Contract(pure = true)
    @Nullable
    public Object get(@Range(from = 0, to = Integer.MAX_VALUE) final int index) throws ArrayIndexOutOfBoundsException {
        return array[index];
    }

    /**
     * Returns the hash code of this array's objects.
     *
     * @see Arrays#hashCode(Object[])
     */
    @Contract(pure = true)
    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    /**
     * Returns an iterator over the objects in this array. The returned
     * iterator is immutable and calling {@link Iterator#remove()} will throw
     * an {@code UnsupportedOperationException}.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return new ObjectIterator();
    }

    /**
     * Returns the length of this array.
     */
    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int length() {
        return array.length;
    }

    /**
     * Returns a spliterator covering the objects this array.
     *
     * @see Arrays#spliterator(Object[])
     */
    @Contract(value = "-> new", pure = true)
    @Override
    public Spliterator<Object> spliterator() {
        return Arrays.spliterator(array);
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public String toJson() {
        final StringBuilder sb = new StringBuilder(length() * 8);
        toJson(sb);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @Override
    public void toJson(@NotNull final StringBuilder sb) {
        sb.append('[');
        if (array.length > 0) {
            toJson(array[0], sb);
            for (int index = 1; index < array.length; index++) {
                sb.append(", ");
                toJson(array[index], sb);
            }
        }
        sb.append(" ]");
    }

    /**
     * An immutable iterator over the objects of a {@code JsonArray}.
     */
    protected class ObjectIterator implements Iterator<Object> {

        /**
         * The current index (position) of the iteration.
         */
        @Range(from = 0, to = Integer.MAX_VALUE)
        private int index = 0;

        /**
         * Performs the given action for each remaining element until all
         * elements have been processed or the action throws an exception. If
         * the action throws an exception, use of the iterator can continue as
         * long as it has more elements.
         *
         * @param action the action to be performed for each element
         * @throws NullPointerException if the specified action is {@code null}
         */
        @Override
        public void forEachRemaining(@NotNull final Consumer<? super Object> action) {
            Objects.requireNonNull(action, "action is null");
            while (index < array.length)
                action.accept(array[index++]);
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @Override
        public boolean hasNext() {
            return index < array.length;
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Object next() throws NoSuchElementException {
            if (index >= array.length)
                throw new NoSuchElementException();
            return array[index++];
        }

        /**
         * Throws an {@code UnsupportedOperationException}.
         */
        @Contract(value = "-> fail", pure = true)
        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("remove");
        }
    }
}
