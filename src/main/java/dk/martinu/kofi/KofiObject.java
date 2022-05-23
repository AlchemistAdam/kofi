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
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;

import dk.martinu.kofi.properties.ObjectProperty;

/**
 * Immutable {@link KofiValue} implementation of an object.
 *
 * @author Adam Martinu
 * @see ObjectProperty
 * @since 1.0
 */
public class KofiObject extends KofiValue implements Iterable<KofiObject.Entry>, Serializable {

    /**
     * Empty, zero-length entry array constant.
     */
    protected static final Entry[] EMPTY = new Entry[0];
    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new {@code KofiObject} that wraps around the specified object
     * using reflection and returns it. Setting non-static field values in
     * {@code object} will not change the returned object.
     *
     * @param object the object to reflect
     * @return a new {@code KofiObject}
     * @throws NullPointerException if {@code object} is {@code null}
     */
    @NotNull
    public static KofiObject reflect(@NotNull final Object object) {
        Objects.requireNonNull(object, "object is null");
        // class for reflection
        final Class<?> cl = object.getClass();
        // map of field names and values
        final HashMap<String, Object> map = new HashMap<>();
        for (Field field : cl.getFields())
            if (!Modifier.isStatic(field.getModifiers()))
                if (field.canAccess(object) || field.trySetAccessible()) {
                    // get field value
                    final Object value;
                    try {
                        value = field.get(object);
                    }
                    catch (IllegalAccessException e) {
                        // reflection access is already checked, this should never happen
                        KofiLog.severe("field could not be accessed {"
                                + object.getClass().getName() + ", " + field.getName() + "}");
                        throw new RuntimeException(e);
                    }
                    map.put(field.getName(), value);
                }
                else
                    KofiLog.finest("field could not be accessed {"
                            + object.getClass().getName() + ", " + field.getName() + "}");
        return new KofiObject(map);
    }

    /**
     * The entries contained in this object. Each entry value is guaranteed to
     * be defined. The entries are sorted in lexicographical order.
     *
     * @see KofiValue#getKofiValue(Object)
     */
    @NotNull
    protected final Entry[] entries;

    /**
     * Construct a new, empty {@code KofiObject}.
     */
    @Contract(pure = true)
    public KofiObject() {
        entries = EMPTY;
    }

    /**
     * Constructs a new {@code KofiObject} containing the defined name-value
     * pairs of the specified map.
     *
     * @param map the map of name-value pairs, or {@code null}
     * @see KofiValue#getKofiValue(Object)
     * @see Entry
     */
    @Contract(pure = true)
    public KofiObject(@Nullable final Map<String, Object> map) {
        if (map != null && map.size() > 0)
            entries = map.entrySet().parallelStream()
                    .map(entry -> new Entry(entry.getKey(), entry.getValue()))
                    .sorted()
                    .toArray(Entry[]::new);
        else
            entries = EMPTY;
    }

    /**
     * Constructs a new object of the specified type from the entries of this
     * object and returns it.
     *
     * @param type the class of the object to construct
     * @param <T>  the runtime type of the object to construct
     * @return a new object
     * @throws NullPointerException        if {@code type} is {@code null}
     * @throws InstantiationException      if {@code type} does not represent a
     *                                     non-abstract class
     * @throws NoSuchMethodException       if {@code type} does not declare a
     *                                     no-arg constructor
     * @throws IllegalAccessException      if the no-arg constructor declared
     *                                     by {@code type} is inaccessible
     * @throws ExceptionInInitializerError if the initialization caused by
     *                                     calling the constructor or setting
     *                                     the value of a field fails.
     * @throws InvocationTargetException   if calling the constructor throws an
     *                                     exception
     * @throws NoSuchFieldException        if this object contains an entry
     *                                     whose name does not match the name
     *                                     of a field declared by {@code type}
     * @throws IllegalArgumentException    if an entry value could not be
     *                                     assigned to a field
     * @throws ConstructionException       if an exception ocurred when
     *                                     constructing an array or object
     *                                     from an entry value
     */
    // DOC provide more details on how entry values are assigned to fields
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public <T> T construct(@NotNull final Class<T> type) throws ReflectiveOperationException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Objects.requireNonNull(type, "type is null");
        final KofiLog.Source src = new KofiLog.Source(KofiObject.class, "construct(Class)");

        if (Modifier.isAbstract(type.getModifiers()) || Modifier.isInterface(type.getModifiers()))
            throw KofiLog.exception(src, new InstantiationException(
                    "cannot create new instance of type {" + type + "}"));

        // no-arg constructor to create object
        final Constructor<T> constructor;
        try {
            constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null) && !constructor.trySetAccessible())
                throw KofiLog.exception(src, new IllegalAccessException(
                        "no-arg constructor to create object is inaccessible {" + type + "}"));
        }
        catch (NoSuchMethodException e) {
            throw KofiLog.exception(src, e);
        }

        // object to assign field values and return
        final T obj;
        try {
            obj = constructor.newInstance();
        }
        catch (InvocationTargetException e) {
            throw KofiLog.exception(src, e);
        }

        // assign entry values to fields
        for (Entry entry : entries) {
            final Field field;
            try {
                field = type.getField(entry.name);
            }
            catch (NoSuchFieldException e) {
                throw KofiLog.exception(src, e);
            }
            // skip final fields
            if (Modifier.isFinal(field.getModifiers()))
                continue;
                // field must be accessible
            else if (field.canAccess(obj) || field.trySetAccessible()) {
                final Class<?> fieldType = field.getType();
                // examine field type and set value if entry matches
                if (entry.value != null) {
                    // arrays
                    if (fieldType.isArray()) {
                        if (entry.value instanceof KofiArray array)
                            try {
                                field.set(obj, array.construct(fieldType));
                                continue;
                            }
                            catch (IllegalArgumentException e) {
                                throw KofiLog.exception(src, new ConstructionException(
                                        "could not construct array from entry {" + entry + "}", e));
                            }
                    }
                    // strings and wrappers
                    else if (fieldType.isAssignableFrom(entry.value.getClass())) {
                        if (fieldType.equals(String.class))
                            field.set(obj, getJavaString((String) entry.value));
                        else
                            field.set(obj, entry.value);
                        continue;
                    }
                    // undefined objects
                    else if (!fieldType.isPrimitive()) {
                        if (entry.value instanceof KofiObject object)
                            try {
                                field.set(obj, object.construct(fieldType));
                                continue;
                            }
                            catch (IllegalArgumentException e) {
                                throw KofiLog.exception(src, new ConstructionException(
                                        "could not construct object from entry {" + entry + "}", e));
                            }
                    }
                    // below are primitive types
                    else if (fieldType == int.class) {
                        if (entry.value instanceof Number n) {
                            field.setInt(obj, n.intValue());
                            continue;
                        }
                    }
                    else if (fieldType == long.class) {
                        if (entry.value instanceof Number n) {
                            field.setLong(obj, n.longValue());
                            continue;
                        }
                    }
                    else if (fieldType == float.class) {
                        if (entry.value instanceof Number n) {
                            field.setFloat(obj, n.floatValue());
                            continue;
                        }
                    }
                    else if (fieldType == double.class) {
                        if (entry.value instanceof Number n) {
                            field.setDouble(obj, n.doubleValue());
                            continue;
                        }
                    }
                    else if (fieldType == byte.class) {
                        if (entry.value instanceof Number n) {
                            field.setByte(obj, n.byteValue());
                            continue;
                        }
                    }
                    else if (fieldType == short.class) {
                        if (entry.value instanceof Number n) {
                            field.setShort(obj, n.shortValue());
                            continue;
                        }
                    }
                    else if (fieldType == char.class) {
                        if (entry.value instanceof Character c) {
                            field.setChar(obj, c);
                            continue;
                        }
                    }
                    else // if (fieldType == boolean.class)
                        if (entry.value instanceof Boolean b) {
                            field.setBoolean(obj, b);
                            continue;
                        }
                }
                // if field is an Object type set to null (value is null)
                else if (!fieldType.isPrimitive()) {
                    field.set(obj, null);
                    continue;
                }
                // throw exception if field value was not set
                throw KofiLog.exception(src, new IllegalArgumentException(
                        "cannot set field {" + field + "} to value of {" + entry.value + "}"));
            }
            else
                KofiLog.finest("field is inaccessible and cannot be assigned {"
                        + type.getName() + ", " + field + "}");
        }
        return obj;
    }

    /**
     * Returns {@code true} if this object is equal to {@code obj}
     * ({@code this == obj}), or {@code obj} is also a {@code KofiObject} and
     * its size and entries are equal to this object's size and entries.
     * Otherwise {@code false} is returned.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof KofiObject kofi && entries.length == kofi.entries.length) {
            for (int i = 0; i < entries.length; i++)
                if (!Objects.equals(entries[i].value, kofi.getEntry(i).value))
                    return false;
            return true;
        }
        else
            return false;
    }

    /**
     * Returns the value of the entry in this object with the specified name,
     * or {@code null}.
     *
     * @param name the name of the entry
     * @return the entry value, or {@code null}
     * @throws NullPointerException if {@code name} is {@code null}
     */
    @Contract(pure = true)
    @Nullable
    public Object get(@NotNull final String name) {
        Objects.requireNonNull(name, "name is null");
        int min = 0, max = size() - 1;
        for (int i, k; min <= max; ) {
            i = (min + max) >> 1;
            k = entries[i].name.compareTo(name);
            if (k < 0)
                min = i + 1;
            else if (k > 0)
                max = i - 1;
            else
                return entries[i].value;
        }
        return null;
    }

    /**
     * Returns the entry at the specified index in this object.
     *
     * @param index the index of the entry
     * @return the entry at the specified index
     * @throws ArrayIndexOutOfBoundsException if {@code index} is out of bounds
     *                                        ({@code index < 0 || index >= size()})
     */
    @Contract(pure = true)
    @NotNull
    public Entry getEntry(@Range(from = 0, to = Integer.MAX_VALUE) final int index) throws
            ArrayIndexOutOfBoundsException {
        return entries[index];
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public String getString() {
        final StringBuilder sb = new StringBuilder(size() * 16);
        getString(sb);
        return sb.toString();
    }

    /**
     * Returns the hash code of this object's entries.
     *
     * @see Arrays#hashCode(Object[])
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(entries);
    }

    /**
     * Returns an iterator over the entries in this object. The returned
     * iterator is immutable and calling {@link Iterator#remove()} will throw
     * an {@code UnsupportedOperationException}.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Iterator<Entry> iterator() {
        return new EntryIterator();
    }

    /**
     * Returns the size of this object.
     */
    @Contract(pure = true)
    public int size() {
        return entries.length;
    }

    /**
     * Returns a spliterator covering the entries of this object.
     *
     * @see Arrays#spliterator(Object[])
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Spliterator<Entry> spliterator() {
        return Arrays.spliterator(entries);
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @Override
    protected void getString(@NotNull final StringBuilder sb) {
        sb.append('{');
        if (entries.length > 0) {
            Entry entry = entries[0];
            sb.append(" \"").append(entry.name).append("\": ");
            getString(entry.value, sb);
            for (int index = 1; index < entries.length; index++) {
                entry = entries[index];
                sb.append(", \"").append(entry.name).append("\": ");
                getString(entry.value, sb);
            }
        }
        sb.append(" }");
    }

    /**
     * A name-value pair. The value of an entry is guaranteed to be defined.
     *
     * @see KofiValue#getKofiValue(Object)
     */
    public class Entry implements Comparable<Entry> {

        /**
         * The entry name.
         */
        @NotNull
        public final String name;
        /**
         * The defined object entry value.
         *
         * @see KofiValue#getKofiValue(Object)
         */
        @Nullable
        public final Object value;
        /**
         * Cached name hash code.
         */
        protected transient int hash = 0;
        /**
         * {@code true} if the computed name hash code is {@code 0}.
         */
        protected transient boolean hashIsZero = false;

        /**
         * Creates a new entry with the specified name and defined object of
         * {@code value}.
         *
         * @param name  the entry name
         * @param value the value to get the defined object from, which will be
         *              the entry value
         * @throws NullPointerException if {@code name} is {@code null}
         * @see KofiValue#getKofiValue(Object)
         */
        @Contract(pure = true)
        public Entry(@NotNull final String name, @Nullable final Object value) {
            this.name = Objects.requireNonNull(name, "name is null");
            this.value = getKofiValue(value);
        }

        /**
         * Compares this entry to the specified entry.
         *
         * @see String#compareTo(String)
         */
        @Contract(pure = true)
        @Override
        public int compareTo(@NotNull final KofiObject.Entry entry) {
            return name.compareTo(entry.name);
        }

        /**
         * Returns {@code true} if this entry is equal to {@code obj}
         * ({@code this == obj}), or {@code obj} is also an {@code Entry} and
         * its name and value is equal to this object's name and value.
         * Otherwise {@code false} is returned.
         */
        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(@Nullable final Object obj) {
            if (this == obj)
                return true;
            else if (obj instanceof Entry entry)
                return name.equalsIgnoreCase(entry.name) && Objects.equals(value, entry.value);
            else
                return false;
        }

        /**
         * Returns the name of this entry.
         */
        @Contract(pure = true)
        @NotNull
        public String getName() {
            return name;
        }

        /**
         * Returns the value of this entry.
         */
        @Contract(pure = true)
        @Nullable
        public Object getValue() {
            return value;
        }

        /**
         * Returns a combined hash code of this entry's key, in upper-case, and
         * value. The returned value is equal to:
         * <pre>
         *     keyHash | valueHash &lt;&lt; 16
         * </pre>
         */
        @Contract(pure = true)
        @Override
        public int hashCode() {
            // name hash is immutable and is cached
            int h = hash;
            if (h == 0 && !hashIsZero) {
                h = name.toUpperCase(Locale.ROOT).hashCode();
                if (h == 0)
                    hashIsZero = true;
                else
                    hash = h;
            }
            // value hash is mutable and must be computed each time
            return value != null ? h | value.hashCode() << 16 : h;
        }

        /**
         * Returns a string representation of this entry, equal to:
         * <pre>
         *     "\"<i>name</i>\": <i>value</i>"
         * </pre>
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public String toString() {
            return '\"' + name + "\": " + value;
        }
    }

    /**
     * A {@code KofiObject} builder. The values of name-value pairs in a
     * builder are not guaranteed to be defined.
     *
     * @see KofiValue#getKofiValue(Object)
     */
    @SuppressWarnings("unused")
    public static class Builder {

        /**
         * Map of name-value pairs.
         */
        protected final TreeMap<String, Object> map = new TreeMap<>();

        /**
         * Constructs a new object from the name-value pairs in this builder.
         *
         * @see KofiObject#KofiObject(Map)
         */
        @Contract(pure = true)
        @NotNull
        public KofiObject build() {
            return new KofiObject(map);
        }

        /**
         * Clears this builder of all name-value pairs.
         */
        public void clear() {
            map.clear();
        }

        /**
         * Returns the value of the name-value pair for the specified name.
         *
         * @throws NullPointerException if {@code name} is {@code null}
         */
        @Nullable
        public Object get(@NotNull final String name) {
            Objects.requireNonNull(name, "name is null");
            return map.get(name);
        }

        /**
         * Puts the name-value pair of the specified entry in this builder. If
         * this builder already contains a mapping for the entry name, then the
         * old value is replaced.
         *
         * @param entry the name-value pair to put into this builder
         * @return this builder
         * @throws NullPointerException if {@code entry} is {@code null}
         */
        @Contract(value = "_ -> this", pure = true)
        @NotNull
        public Builder put(@NotNull final Entry entry) {
            Objects.requireNonNull(entry, "entry is null");
            return put(entry.getName(), entry.getValue());
        }

        /**
         * Puts the name-value pair in this builder. If this builder already
         * contains a mapping for the specified name, then the old value is
         * replaced.
         *
         * @param name  the name
         * @param value the value
         * @return this builder
         * @throws NullPointerException if {@code name} is {@code null}
         */
        @Contract(value = "_, _ -> this", pure = true)
        @NotNull
        public Builder put(@NotNull final String name, @Nullable final Object value) {
            Objects.requireNonNull(name, "name is null");
            map.put(name, value);
            return this;
        }

        /**
         * Removes the name-value pair in this builder matching the specified
         * name.
         *
         * @param name the name of the name-value pair to remove
         * @return this builder
         * @throws NullPointerException if {@code name} is {@code null}
         */
        @Contract(value = "_ -> this", pure = true)
        @NotNull
        public Builder remove(@NotNull final String name) {
            Objects.requireNonNull(name, "name is null");
            map.remove(name);
            return this;
        }

        /**
         * Returns the size of this builder.
         */
        @Contract(pure = true)
        @Range(from = 0, to = Integer.MAX_VALUE)
        public int size() {
            return map.size();
        }
    }

    protected class EntryIterator implements Iterator<Entry> {

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
        public void forEachRemaining(@NotNull final Consumer<? super Entry> action) {
            Objects.requireNonNull(action, "action is null");
            while (index < entries.length)
                action.accept(entries[index++]);
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @Override
        public boolean hasNext() {
            return index < entries.length;
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Entry next() {
            return entries[index++];
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
