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
import java.util.function.IntConsumer;

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
    public static KofiArray reflect(@NotNull final Object array) {
        Objects.requireNonNull(array, "array is null");
        if (!array.getClass().isArray())
            throw new IllegalArgumentException("array is not an array type");

        final int len = Array.getLength(array);

        // list of reflected array values
        final ArrayList<Object> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++)
            list.add(Array.get(array, i));

        final KofiArray rv = new KofiArray(list);
        rv.arrayType = array.getClass();

        return rv;
    }

    /**
     * The objects contained in this array. Each object is guaranteed to be a
     * {@link KofiUtil#isDefinedType(Object) defined} KoFi value.
     *
     * @see KofiUtil#getKofiValue(Object)
     */
    protected final Object[] array;

    /**
     * The runtime type of this array when it was reflected, or {@code null} if
     * unknown.
     */
    // TODO set arrayType when constructing -- requires arrayType to be serialized
    protected transient Class<?> arrayType = null;

    /**
     * Construct a new, empty {@code KofiArray}.
     */
    @Contract(pure = true)
    public KofiArray() {
        array = EMPTY;
    }

    /**
     * Constructs a new {@code KofiArray} containing the values of
     * {@code values} converted to
     * {@link KofiUtil#isDefinedType(Object) defined} objects.
     *
     * @param values the array objects, or {@code null}
     * @see KofiUtil#getKofiValue(Object)
     */
    @Contract(pure = true)
    public KofiArray(@Nullable final Object... values) {
        if (values != null && values.length != 0) {
            array = new Object[values.length];
            for (int i = 0; i < values.length; i++)
                array[i] = KofiUtil.getKofiValue(values[i]);
        }
        else
            array = EMPTY;
    }

    /**
     * Constructs a new {@code KofiArray} containing the values of
     * {@code string} converted to KoFi strings.
     *
     * @param strings the array of strings, or {@code null}
     * @see KofiUtil#getKofiString(String)
     */
    @Contract(pure = true)
    public KofiArray(@Nullable final String[] strings) {
        if (strings != null && strings.length != 0) {
            array = new Object[strings.length];
            for (int i = 0; i < strings.length; i++) {
                final String s = strings[i];
                if (s != null)
                    array[i] = KofiUtil.getKofiString(s);
                else
                    array[i] = null;
            }
        }
        else
            array = EMPTY;
        arrayType = String[].class;
    }

    /**
     * DOC
     *
     * @param ints
     */
    @Contract(pure = true)
    public KofiArray(final int[] ints) {
        if (ints != null && ints.length != 0) {
            array = new Integer[ints.length];
            for (int i = 0; i < ints.length; i++)
                array[i] = ints[i];
        }
        else
            array = EMPTY;
        arrayType = int[].class;
    }

    /**
     * DOC
     *
     * @param longs
     */
    @Contract(pure = true)
    public KofiArray(final long[] longs) {
        if (longs != null && longs.length != 0) {
            array = new Long[longs.length];
            for (int i = 0; i < longs.length; i++)
                array[i] = longs[i];
        }
        else
            array = EMPTY;
        arrayType = long[].class;
    }

    /**
     * DOC
     *
     * @param floats
     */
    @Contract(pure = true)
    public KofiArray(final float[] floats) {
        if (floats != null && floats.length != 0) {
            array = new Float[floats.length];
            for (int i = 0; i < floats.length; i++)
                array[i] = floats[i];
        }
        else
            array = EMPTY;
        arrayType = float[].class;
    }

    /**
     * DOC
     *
     * @param doubles
     */
    @Contract(pure = true)
    public KofiArray(final double[] doubles) {
        if (doubles != null && doubles.length != 0) {
            array = new Double[doubles.length];
            for (int i = 0; i < doubles.length; i++)
                array[i] = doubles[i];
        }
        else
            array = EMPTY;
        arrayType = double[].class;
    }

    /**
     * DOC
     *
     * @param bytes
     */
    @Contract(pure = true)
    public KofiArray(final byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            array = new Byte[bytes.length];
            for (int i = 0; i < bytes.length; i++)
                array[i] = bytes[i];
        }
        else
            array = EMPTY;
        arrayType = byte[].class;
    }

    /**
     * DOC
     *
     * @param shorts
     */
    @Contract(pure = true)
    public KofiArray(final short[] shorts) {
        if (shorts != null && shorts.length != 0) {
            array = new Short[shorts.length];
            for (int i = 0; i < shorts.length; i++)
                array[i] = shorts[i];
        }
        else
            array = EMPTY;
        arrayType = short[].class;
    }

    /**
     * DOC
     *
     * @param booleans
     */
    @Contract(pure = true)
    public KofiArray(final boolean[] booleans) {
        if (booleans != null && booleans.length != 0) {
            array = new Boolean[booleans.length];
            for (int i = 0; i < booleans.length; i++)
                array[i] = booleans[i];
        }
        else
            array = EMPTY;
        arrayType = boolean[].class;
    }

    /**
     * DOC
     *
     * @param chars
     */
    @Contract(pure = true)
    public KofiArray(final char[] chars) {
        if (chars != null && chars.length != 0) {
            array = new Character[chars.length];
            for (int i = 0; i < chars.length; i++)
                array[i] = chars[i];
        }
        else
            array = EMPTY;
        arrayType = char[].class;
    }

    /**
     * Constructs a new {@code KofiArray} containing the
     * {@link KofiUtil#isDefinedType(Object) defined} object values of the
     * values in the specified list.
     *
     * @param list the list of objects, or {@code null}
     * @see KofiUtil#getKofiValue(Object)
     */
    @Contract(pure = true)
    public KofiArray(@Nullable final List<Object> list) {
        if (list != null && !list.isEmpty())
            array = list.parallelStream()
                    .map(KofiUtil::getKofiValue)
                    .toArray(Object[]::new);
        else
            array = EMPTY;
    }

    /**
     * Constructs a new array of the specified type from the elements of this
     * array and returns it.
     *
     * @param type the class of the array to construct
     * @param <V>  the runtime type of the array to construct
     * @return a new array
     * @throws NullPointerException     if {@code type} is {@code null}
     * @throws IllegalArgumentException if {@code type} does not represent an
     *                                  array class, or if one of the elements
     *                                  cannot be converted to the component
     *                                  type
     */
    @SuppressWarnings("DataFlowIssue")
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public <V> V construct(@NotNull final Class<V> type) {
        Objects.requireNonNull(type, "type is null");
        final KofiLog.Source src = new KofiLog.Source(KofiArray.class, "construct(Class)");

        if (!type.isArray())
            throw KofiLog.exception(src, new IllegalArgumentException("type must represent an array class"));

        final Class<?> componentType = type.componentType();

        final int len = length();
        //noinspection unchecked
        final V array = (V) Array.newInstance(componentType, len);
        // lambda expression to assign values to array
        final IntConsumer assign;

        // Java objects
        if (!componentType.isPrimitive()) {
            assign = i -> Array.set(array, i, KofiUtil.getJavaValue(this.array[i], componentType));
        }
        // primitives
        else if (componentType == int.class)
            assign = i -> Array.setInt(array, i, ((Number) get(i)).intValue());
        else if (componentType == long.class)
            assign = i -> Array.setLong(array, i, ((Number) get(i)).longValue());
        else if (componentType == float.class)
            assign = i -> Array.setFloat(array, i, ((Number) get(i)).floatValue());
        else if (componentType == double.class)
            assign = i -> Array.setDouble(array, i, ((Number) get(i)).doubleValue());
        else if (componentType == byte.class)
            assign = i -> Array.setByte(array, i, ((Number) get(i)).byteValue());
        else if (componentType == short.class)
            assign = i -> Array.setShort(array, i, ((Number) get(i)).shortValue());
        else if (componentType == boolean.class)
            assign = i -> Array.setBoolean(array, i, (Boolean) get(i));
        else // if (componentType == char.class)
            assign = i -> Array.setChar(array, i, (Character) get(i));

        // assign values to array
        for (int i = 0; i < len; i++) {
            try {
                assign.accept(i);
            }
            catch (IllegalArgumentException | ClassCastException | ConstructionException | NullPointerException e) {
                throw KofiLog.exception(src, new IllegalArgumentException("cannot set value in "
                        + array.getClass().componentType().getSimpleName()
                        + " array to value of " + get(i), e));
            }
        }

        return array;
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
    public Object get(@Range(from = 0, to = Integer.MAX_VALUE) final int index) {
        return array[index];
    }

    /**
     * Returns the runtime type of this array when it was reflected or
     * constructed, or {@code null} if unknown.
     */
    @Contract(pure = true)
    @Nullable
    public Class<?> getArrayType() {
        return arrayType;
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
        public Object next() {
            if (index >= array.length)
                throw new NoSuchElementException();
            return array[index++];
        }

        /**
         * Throws an {@code UnsupportedOperationException}.
         */
        @Contract(value = "-> fail", pure = true)
        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
