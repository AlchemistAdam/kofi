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

public class JsonObject extends Json implements Iterable<JsonObject.Entry>, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    // TODO check for a JSON array named "0" and if found use as constructer args
    //  (this is safe because field names cannot begin with numbers
    public static <V> V reconstruct(@NotNull final JsonObject json, @NotNull final Class<V> objectClass) throws
            NullPointerException, InstantiationException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, NoSuchFieldException, ClassCastException {
        Objects.requireNonNull(json, "json is null");
        Objects.requireNonNull(objectClass, "objectClass is null");
        if (Modifier.isAbstract(objectClass.getModifiers()) || Modifier.isInterface(objectClass.getModifiers())) {
            final InstantiationException exc = new InstantiationException(
                    "objectClass must represent a non-abstract class {" + objectClass + "}");
            KofiLog.throwing(JsonObject.class.getName(), "reconstruct", exc);
            throw exc;
        }

        // no-arg constructor to create object
        final Constructor<V> constructor;
        try {
            constructor = objectClass.getDeclaredConstructor();
            if (!constructor.canAccess(null) && !constructor.trySetAccessible()) {
                final IllegalAccessException exc = new IllegalAccessException(
                        "no-arg constructor to create object is inaccessible {" + objectClass + "}");
                KofiLog.throwing(JsonObject.class.getName(), "reconstruct", exc);
                throw exc;
            }
        }
        catch (NoSuchMethodException e) {
            NoSuchMethodException exc = new NoSuchMethodException(
                    "no-arg constructor not found {" + objectClass + "}");
            exc.initCause(e);
            KofiLog.throwing(JsonObject.class.getName(), "reconstruct", exc);
            throw exc;
        }

        // object assign field values and return
        final V obj;
        try {
            obj = constructor.newInstance();
        }
        catch (InvocationTargetException e) {
            KofiLog.throwing(JsonObject.class.getName(), "reconstruct", e);
            throw e;
        }
        // assign values to object fields
        for (Entry entry : json) {
            try {
                final Field field = objectClass.getField(entry.name);
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
                        else if (fieldType.equals(char.class)) {
                            if (entry.value instanceof Character c) {
                                field.setChar(obj, c);
                                unassigned = false;
                            }
                            // special case, allow java style assignment of numbers to chars
                            else if (entry.value instanceof Number n) {
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

    // TODO test
    @NotNull
    public static JsonObject reflect(@NotNull final Object object) throws NullPointerException {
        Objects.requireNonNull(object, "object is null");
        // class for reflection
        final Class<?> cl = object.getClass();
        // list of entries in the JSON object
        final ArrayList<Entry> entries = new ArrayList<>();
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
                    // store field as an entry
                    entries.add(new Entry(field.getName(), value));
                }
                else
                    KofiLog.finest("field could not be accessed {" + object.getClass().getName()
                            + ", " + field.getName() + "}");
        return new JsonObject(entries);
    }

    @NotNull
    protected final Entry[] entries;

    public JsonObject(@Nullable final Entry... entries) {
        final TreeSet<Entry> set = new TreeSet<>();
        if (entries != null) {
            for (Entry entry : entries)
                if (entry != null)
                    set.add(entry);
        }
        this.entries = set.toArray(new Entry[set.size()]);
    }

    private JsonObject(@NotNull final List<Entry> list) {
        this.entries = list.toArray(new Entry[list.size()]);
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof JsonObject jsonObject)
            return Arrays.equals(entries, jsonObject.entries);
        else
            return false;
    }

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

    @NotNull
    public Entry getEntry(final int index) throws ArrayIndexOutOfBoundsException {
        return entries[index];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(entries);
    }

    @Contract(pure = true)
    @NotNull
    @Override
    public Iterator<Entry> iterator() {
        return new EntryIterator();
    }

    @Contract(pure = true)
    public int size() {
        return entries.length;
    }

    @Contract(pure = true)
    @Override
    public Spliterator<Entry> spliterator() {
        return Arrays.spliterator(entries);
    }

    public static class Entry implements Comparable<Entry> {

        @NotNull
        private final String name;
        @Nullable
        private Object value;

        public Entry(@NotNull final String name, @Nullable final Object value) throws NullPointerException {
            this.name = Objects.requireNonNull(name, "name is null");
            this.value = getKnownType(value);
        }

        @Override
        public int compareTo(@NotNull final JsonObject.Entry entry) {
            return name.compareTo(entry.name);
        }

        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(@Nullable final Object obj) {
            if (this == obj)
                return true;
            else if (obj instanceof Entry entry) // TODO is JSON case sensitive?
                return name.equals(entry.name) && Objects.equals(value, entry.value);
            else
                return false;
        }

        @Contract(pure = true)
        @NotNull
        public String getName() {
            return name;
        }

        @Contract(pure = true)
        @Nullable
        public Object getValue() {
            return value;
        }

        @Contract(pure = true)
        @Override
        public int hashCode() {
            if (value != null)
                return name.toLowerCase().hashCode() | value.hashCode() << 16;
            else
                return name.toLowerCase().hashCode();
        }

        public void setValue(@NotNull final Object value) throws NullPointerException {
            this.value = Objects.requireNonNull(value, "value is null");
        }

        @Contract(value = "-> new", pure = true)
        @NotNull
        @Override
        public String toString() {
            return '\"' + name + "\": " + value;
        }
    }

    protected class EntryIterator implements Iterator<Entry> {

        private int index = 0;

        @Contract(pure = true)
        @Override
        public boolean hasNext() {
            return index < entries.length;
        }

        @Contract(pure = true)
        @Override
        public Entry next() {
            return entries[index++];
        }
    }
}
