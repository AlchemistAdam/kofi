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

import dk.martinu.kofi.properties.ObjectProperty;
import org.jetbrains.annotations.*;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * {@link Json} implementation of an immutable JSON object value.
 *
 * @author Adam Martinu
 * @since 1.0
 * @see ObjectProperty
 */
public class JsonObject extends Json implements Iterable<JsonObject.Entry>, Serializable {

    /**
     * Empty, zero-length entry array constant.
     */
    protected static final Entry[] EMPTY = new Entry[0];
    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Reconstructs a new object of the specified type from the entries of
     * {@code json} and returns it.
     *
     * @param json the {@code JsonObject} whose entries are used to reconstruct
     *             the object
     * @param type the class of the object to reconstruct
     * @param <V> the runtime type of the object to reconstruct
     * @return a new, reconstructed object
     * @throws NullPointerException if {@code json} or {@code type} is
     * {@code null}
     * @throws InstantiationException if {@code type} does not represent a
     * non-abstract class
     * @throws NoSuchMethodException if {@code type} does not declare a no-arg
     * constructor
     * @throws IllegalAccessException if the no-arg constructor declared by
     * {@code type} is inaccessible
     * @throws ExceptionInInitializerError if the initialization caused by
     * calling the constructor or setting the value of a field fails.
     * @throws InvocationTargetException if calling the constructor throws an
     * exception
     * @throws NoSuchFieldException if {@code json} contains an entry whose
     * name does not match the name of a field declared by {@code type}
     */
    // TODO ensure that java strings are (un)escaped correctly when reflecting/reconstructing
    // TODO check for a JSON array named "0" and if found use as constructor args
    //  (this is safe because field names cannot begin with numbers
    // TODO provide more details on how entry values are assigned to fields
    @Contract(pure = true)
    @NotNull
    public static <V> V reconstruct(@NotNull final JsonObject json, @NotNull final Class<V> type) throws
            InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            NoSuchFieldException {
        Objects.requireNonNull(json, "json is null");
        Objects.requireNonNull(type, "type is null");
        if (Modifier.isAbstract(type.getModifiers()) || Modifier.isInterface(type.getModifiers())) {
            final InstantiationException exc = new InstantiationException(
                    "type must represent a non-abstract class {" + type + "}");
            KofiLog.throwing(JsonObject.class.getName(), "reconstruct", exc);
            throw exc;
        }

        // no-arg constructor to create object
        final Constructor<V> constructor;
        try {
            constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null) && !constructor.trySetAccessible()) {
                final IllegalAccessException exc = new IllegalAccessException(
                        "no-arg constructor to create object is inaccessible {" + type + "}");
                KofiLog.throwing(JsonObject.class.getName(), "reconstruct", exc);
                throw exc;
            }
        }
        catch (NoSuchMethodException e) {
            NoSuchMethodException exc = new NoSuchMethodException(
                    "no-arg constructor not found {" + type + "}");
            exc.initCause(e);
            KofiLog.throwing(JsonObject.class.getName(), "reconstruct", exc);
            throw exc;
        }

        // object to assign field values and return
        final V obj;
        try {
            obj = constructor.newInstance();
        }
        catch (InvocationTargetException e) {
            KofiLog.throwing(JsonObject.class.getName(), "reconstruct", e);
            throw e;
        }

        // assign entry values to fields
        for (Entry entry : json) {
            try {
                final Field field = type.getField(entry.name);
                // field must be accessible and not final
                if (!Modifier.isFinal(field.getModifiers()) && (field.canAccess(obj) || field.trySetAccessible())) {
                    final Class<?> fieldType = field.getType();
                    // set to false if the entry value is cast and assigned to the field
                    boolean unassigned = true;
                    // if value is not null, then field type must be an
                    // assignable Object type OR a primitive type where value
                    // is of a matching wrapper type
                    if (entry.value != null) {
                        // assignable Object type
                        if (fieldType.isAssignableFrom(entry.value.getClass())) {
                            if (fieldType.equals(String.class)) {
                                field.set(obj, json.getJavaString((String) entry.value));
                            }
                            else
                                field.set(obj, entry.value);
                            unassigned = false;
                        }
                        // below are primitive types
                        else if (fieldType.equals(int.class)) {
                            if (entry.value instanceof Number n) {
                                field.setInt(obj, n.intValue());
                                unassigned = false;
                            }
                        }
                        else if (fieldType.equals(long.class)) {
                            if (entry.value instanceof Number n) {
                                field.setLong(obj, n.longValue());
                                unassigned = false;
                            }
                        }
                        else if (fieldType.equals(float.class)) {
                            if (entry.value instanceof Number n) {
                                field.setFloat(obj, n.floatValue());
                                unassigned = false;
                            }
                        }
                        else if (fieldType.equals(double.class)) {
                            if (entry.value instanceof Number n) {
                                field.setDouble(obj, n.doubleValue());
                                unassigned = false;
                            }
                        }
                        else if (fieldType.equals(byte.class)) {
                            if (entry.value instanceof Number n) {
                                field.setByte(obj, n.byteValue());
                                unassigned = false;
                            }
                        }
                        else if (fieldType.equals(short.class)) {
                            if (entry.value instanceof Number n) {
                                field.setShort(obj, n.shortValue());
                                unassigned = false;
                            }
                        }
                        // characters are not defined in the JSON specification,
                        // use java style assignment of integers to chars
                        else if (fieldType.equals(char.class)) {
                            if (entry.value instanceof Number n) {
                                field.setChar(obj, (char) n.intValue());
                                unassigned = false;
                            }
                        }
                        else if (fieldType.equals(boolean.class)) {
                            if (entry.value instanceof Boolean b) {
                                field.setBoolean(obj, b);
                                unassigned = false;
                            }
                        }

                    }
                    // if field is an Object type assign null (value is null)
                    else if (!fieldType.isPrimitive()) {
                        field.set(obj, null);
                        unassigned = false;
                    }
                    // throw exception if field was not assigned a value
                    if (unassigned) {
                        final IllegalArgumentException exc = new IllegalArgumentException(
                                "cannot assign entry {" + entry + "} to field {" + field + "}");
                        KofiLog.throwing(JsonObject.class.getName(), "reconstruct", exc);
                        throw exc;
                    }
                }
                else
                    KofiLog.finest("field is inaccessible and cannot be assigned {" + field + "}");
            }
            catch (NoSuchFieldException e) {
                KofiLog.throwing(JsonObject.class.getName(), "reconstruct", e);
                throw e;
            }
        }

        return obj;
    }

    /**
     * Constructs a new {@code JsonObject} that wraps around the specified object
     * using reflection and returns it. Setting non-static field values in
     * {@code object} will not change the returned object.
     *
     * @param object the object to reflect
     * @return a new {@code JsonObject}
     * @throws NullPointerException if {@code object} is {@code null}
     */
    // TODO ensure that java strings are (un)escaped correctly when reflecting/reconstructing
    // TODO char is not specified in JSON
    @NotNull
    public static JsonObject reflect(@NotNull final Object object) throws NullPointerException {
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
                        KofiLog.severe("field could not be accessed {" + object.getClass().getName()
                                + ", " + field.getName() + "}");
                        throw new RuntimeException(e);
                    }
                    map.put(field.getName(), value);
                }
                else
                    KofiLog.finest("field could not be accessed {" + object.getClass().getName()
                            + ", " + field.getName() + "}");
        return new JsonObject(map);
    }

    /**
     * The entries contained in this object. Each entry value is guaranteed to
     * be defined.
     *
     * @see Json#getDefinedObject(Object)
     */
    @NotNull
    protected final Entry[] entries;

    /**
     * Construct a new, empty {@code JsonObject}.
     */
    @Contract(pure = true)
    public JsonObject() {
        entries = EMPTY;
    }

    /**
     * Constructs a new {@code JsonObject} containing the defined name-value
     * pairs of the specified map.
     *
     * @param map the map of name-value pairs, or {@code null}
     * @see Json#getDefinedObject(Object)
     * @see Entry
     */
    @Contract(pure = true)
    public JsonObject(@Nullable final Map<String, Object> map) {
        if (map != null && map.size() > 0)
            entries = map.entrySet().parallelStream()
                    .map(entry -> new Entry(entry.getKey(), entry.getValue()))
                    .toArray(Entry[]::new);
        else
            entries = EMPTY;
    }

    /**
     * Returns {@code true} if this object is equal to {@code obj}
     * ({@code this == obj}), or {@code obj} is also a {@code JsonObject} and
     * its size and entries are equal to this object's size and entries.
     * Otherwise {@code false} is returned.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof JsonObject json && entries.length == json.entries.length) {
            for (int i = 0; i < entries.length; i++)
                if (!Objects.equals(entries[i].value, json.getEntry(i).value)
                        && !(entries[i].value instanceof Number n0
                        && json.getEntry(i).value instanceof Number n1
                        && areNumbersEqual(n0, n1)))
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
    public Object get(@NotNull final String name) throws NullPointerException {
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
     *                                   ({@code index < 0 || index >= size()})
     */
    @Contract(pure = true)
    @NotNull
    public Entry getEntry(@Range(from = 0, to = Integer.MAX_VALUE) final int index) throws
            ArrayIndexOutOfBoundsException {
        return entries[index];
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
    @NotNull
    @Override
    public String toJson() {
        final StringBuilder sb = new StringBuilder(size() * 16);
        toJson(sb);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @Override
    protected void toJson(@NotNull final StringBuilder sb) {
        sb.append('{');
        if (entries.length > 0) {
            sb.append(" \"").append(entries[0].getName()).append("\": ");
            toJson(entries[0], sb);
            for (int index = 1; index < entries.length; index++) {
                sb.append(", \"").append(entries[index].getName()).append("\": ");
                toJson(entries[index].getValue(), sb);
            }
        }
        sb.append(" }");
    }

    /**
     * A name-value pair. The value of an entry is guaranteed to be defined.
     *
     * @see Json#getDefinedObject(Object)
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
         * @see Json#getDefinedObject(Object)
         */
        @Nullable
        public final Object value;
        /**
         * Cached name hash code. Set on first call to {@link #hashCode()}.
         */
        protected transient int hash = 0;
        /**
         * {@code true} if the computed name hash code is {@code 0}. Set on
         * first call to {@link #hashCode()}.
         */
        protected transient boolean hashIsZero = false;

        /**
         * Creates a new entry with the specified name and defined object of
         * {@code value}.
         *
         * @param name the entry name
         * @param value the value to get the defined object from, which will be
         *              the entry value
         * @throws NullPointerException if {@code name} is {@code null}
         * @see Json#getDefinedObject(Object)
         */
        @Contract(pure = true)
        public Entry(@NotNull final String name, @Nullable final Object value) throws NullPointerException {
            this.name = Objects.requireNonNull(name, "name is null");
            this.value = getDefinedObject(value);
        }

        /**
         * Compares this entry to the specified entry.
         *
         * @see String#compareTo(String)
         */
        @Contract(pure = true)
        @Override
        public int compareTo(@NotNull final JsonObject.Entry entry) {
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
            else if (obj instanceof Entry entry) // TODO should not be case sensitive
                return name.equals(entry.name) && Objects.equals(value, entry.value);
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
         *     keyHash | valueHash << 16
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
     * A {@code JsonObject} builder. The values of name-value pairs in a
     * builder are not guaranteed to be defined.
     *
     * @see Json#getDefinedObject(Object)
     */
    public static class Builder {

        /**
         * Map of name-value pairs.
         */
        protected final TreeMap<String, Object> map = new TreeMap<>();

        /**
         * Constructs a new object from the name-value pairs in this builder.
         *
         * @see JsonObject#JsonObject(Map)
         */
        @Contract(pure = true)
        @NotNull
        public JsonObject build() {
            return new JsonObject(map);
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
        public Object get(@NotNull final String name) throws NullPointerException {
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
        public Builder put(@NotNull final Entry entry) throws NullPointerException {
            Objects.requireNonNull(entry, "entry is null");
            return put(entry.getName(), entry.getValue());
        }

        /**
         * Puts the name-value pair in this builder. If this builder already
         * contains a mapping for the specified name, then the old value is
         * replaced.
         *
         * @param name the name
         * @param value the value
         * @return this builder
         * @throws NullPointerException if {@code name} is {@code null}
         */
        @Contract(value = "_, _ -> this", pure = true)
        @NotNull
        public Builder put(@NotNull final String name, @Nullable final Object value) throws NullPointerException {
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
        public Builder remove(@NotNull final String name) throws NullPointerException {
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
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @Override
        public boolean hasNext() {
            return index < entries.length;
        }

        /**
         * Throws an {@code UnsupportedOperationException}.
         */
        @Contract(value = "-> fail", pure = true)
        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("remove");
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
    }
}
