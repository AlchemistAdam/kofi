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
 * Immutable {@link KofiValue} implementation of an array.
 *
 * @author Adam Martinu
 * @see ArrayProperty
 * @since 1.0
 */
public class KofiArray extends KofiValue implements Iterable<Object>, Serializable {

    /**
     * Empty, zero-length object array constant.
     */
    protected static final Object[] EMPTY = new Object[0];
    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new {@code KofiArray} that wraps around the specified array
     * using reflection and returns it. Removing or inserting elements in
     * {@code array} will not change the returned array.
     *
     * @param array the array to reflect
     * @return a new {@code KofiArray}
     * @throws NullPointerException     if {@code array} is {@code null}
     * @throws IllegalArgumentException if {@code array} is not an array type,
     *                                  determined by {@link Class#isArray()}
     * @see #KofiArray(Object...)
     */
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static KofiArray reflect(@NotNull final Object array) throws IllegalArgumentException {
        Objects.requireNonNull(array, "array is null");
        if (!array.getClass().isArray())
            throw new IllegalArgumentException("array is not an array type");
        final int len = Array.getLength(array);
        // list of reflected array values
        final ArrayList<Object> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++)
            list.add(Array.get(array, i));
        return new KofiArray(list);
    }

    /**
     * The objects contained in this array. Each object is guaranteed to be
     * defined.
     *
     * @see KofiValue#getKofiValue(Object)
     */
    @NotNull
    protected final Object[] array;

    /**
     * Construct a new, empty {@code KofiArray}.
     */
    @Contract(pure = true)
    public KofiArray() {
        array = EMPTY;
    }

    /**
     * Constructs a new {@code KofiArray} containing the defined objects of
     * {@code values}.
     *
     * @param values the array objects, or {@code null}
     * @see KofiValue#getKofiValue(Object)
     */
    @Contract(pure = true)
    public KofiArray(@Nullable final Object... values) {
        if (values != null && values.length != 0) {
            final ArrayList<Object> list = new ArrayList<>();
            for (final Object value : values)
                list.add(getKofiValue(value));
            array = list.toArray(new Object[list.size()]);
        }
        else
            array = EMPTY;
    }

    /**
     * Constructs a new {@code KofiArray} containing the defined objects of the
     * values in the specified list.
     *
     * @param list the list of objects, or {@code null}
     * @see KofiValue#getKofiValue(Object)
     */
    @Contract(pure = true)
    public KofiArray(@Nullable final List<Object> list) {
        if (list != null && !list.isEmpty())
            array = list.parallelStream()
                    .map(this::getKofiValue)
                    .toArray(Object[]::new);
        else
            array = EMPTY;
    }

    /**
     * Returns {@code true} if this array is equal to {@code obj}
     * ({@code this == obj}), or {@code obj} is also a {@code KofiArray} and
     * its length and elements are equal to this array's length and elements.
     * Otherwise {@code false} is returned.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof KofiArray kofi && array.length == kofi.length()) {
            for (int i = 0; i < array.length; i++)
                if (!Objects.equals(array[i], kofi.get(i)))
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
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public String getString() {
        final StringBuilder sb = new StringBuilder(length() * 8);
        getString(sb);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @Override
    public void getString(@NotNull final StringBuilder sb) {
        sb.append('[');
        if (array.length > 0) {
            getString(array[0], sb);
            for (int index = 1; index < array.length; index++) {
                sb.append(", ");
                getString(array[index], sb);
            }
        }
        sb.append(" ]");
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
     * Reconstructs a new array of the specified type from the elements of this
     * array and returns it.
     *
     * @param type the class of the array to reconstruct
     * @param <V>  the runtime type of the array to reconstruct
     * @return a new, reconstructed array
     * @throws NullPointerException     if {@code type} is {@code null}
     * @throws IllegalArgumentException if {@code type} does not represent an
     *                                  array class, or if one of the elements
     *                                  cannot be converted to the component
     *                                  type
     */
    public <V> V reconstruct(@NotNull final Class<V> type) throws IllegalArgumentException {
        Objects.requireNonNull(type, "type is null");
        final KofiLog.Source src = new KofiLog.Source(KofiArray.class, "reconstruct(Class)");

        if (!type.isArray())
            throw KofiLog.exception(src, new IllegalArgumentException("type must represent an array class"));

        final Class<?> componentType = type.componentType();
        //noinspection unchecked
        final V array = (V) Array.newInstance(componentType, length());

        // Java objects (including nested arrays)
        if (!componentType.isPrimitive()) {
            for (int i = 0; i < length(); i++)
                try {
                    Array.set(array, i, getJavaValue(i, componentType));
                }
                catch (IllegalArgumentException | ReconstructionException e) {
                    throw KofiLog.exception(src, new IllegalArgumentException("cannot set value in "
                            + array.getClass().getSimpleName() + " array to value of {" + get(i) + "}", e));
                }
        }
        // primitives
        else if (componentType == int.class)
            for (int i = 0; i < length(); i++) {
                if (get(i) instanceof Integer integer)
                    Array.setInt(array, i, integer);
                else
                    throw KofiLog.exception(src, new IllegalArgumentException("cannot set int value in "
                            + array.getClass().getSimpleName() + " array to value of {" + get(i) + "}"));
            }
        else if (componentType == long.class)
            for (int i = 0; i < length(); i++) {
                if (get(i) instanceof Long l)
                    Array.setLong(array, i, l);
                else
                    throw KofiLog.exception(src, new IllegalArgumentException("cannot set long value in "
                            + array.getClass().getSimpleName() + " array to value of {" + get(i) + "}"));
            }
        else if (componentType == float.class)
            for (int i = 0; i < length(); i++) {
                if (get(i) instanceof Float f)
                    Array.setFloat(array, i, f);
                else
                    throw KofiLog.exception(src, new IllegalArgumentException("cannot set float value in "
                            + array.getClass().getSimpleName() + " array to value of {" + get(i) + "}"));
            }
        else if (componentType == double.class)
            for (int i = 0; i < length(); i++) {
                if (get(i) instanceof Double d)
                    Array.setDouble(array, i, d);
                else
                    throw KofiLog.exception(src, new IllegalArgumentException("cannot set double value in "
                            + array.getClass().getSimpleName() + " array to value of {" + get(i) + "}"));
            }
        else if (componentType == boolean.class)
            for (int i = 0; i < length(); i++) {
                if (get(i) instanceof Boolean b)
                    Array.setBoolean(array, i, b);
                else
                    throw KofiLog.exception(src, new IllegalArgumentException("cannot set boolean value in "
                            + array.getClass().getSimpleName() + " array to value of {" + get(i) + "}"));
            }
        else if (componentType == byte.class)
            for (int i = 0; i < length(); i++) {
                if (get(i) instanceof Byte b)
                    Array.setByte(array, i, b);
                else
                    throw KofiLog.exception(src, new IllegalArgumentException("cannot set byte value in "
                            + array.getClass().getSimpleName() + " array to value of {" + get(i) + "}"));
            }
        else if (componentType == short.class)
            for (int i = 0; i < length(); i++) {
                if (get(i) instanceof Short s)
                    Array.setShort(array, i, s);
                else
                    throw KofiLog.exception(src, new IllegalArgumentException("cannot set short value in "
                            + array.getClass().getSimpleName() + " array to value of {" + get(i) + "}"));
            }
        else // if (componentType == char.class)
            for (int i = 0; i < length(); i++) {
                if (get(i) instanceof Character c)
                    Array.setChar(array, i, c);
                else
                    throw KofiLog.exception(src, new IllegalArgumentException("cannot set char value in "
                            + array.getClass().getSimpleName() + " array to value of {" + get(i) + "}"));
            }
        return array;
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
     * Returns the Java value of the element in this array at the specified index.
     *
     * @param index the index of the element
     * @param type  the Java type of the element
     * @return the Java value of the element at the specified index
     * @throws ArrayIndexOutOfBoundsException if <code>index &lt; 0</code> or
     *                                        <code>index &ge; length()</code>
     *                                        is {@code true}
     * @throws NullPointerException           if {@code type} is {@code null}
     * @see KofiValue#getJavaValue(Object, Class)
     */
    @Contract(pure = true)
    @Nullable
    protected Object getJavaValue(@Range(from = 0, to = Integer.MAX_VALUE) final int index,
            @NotNull final Class<?> type) throws ArrayIndexOutOfBoundsException, NullPointerException,
            ReconstructionException {
        return getJavaValue(array[index], type);
    }

    /**
     * An immutable iterator over the indexed objects of a {@code KofiArray}.
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
