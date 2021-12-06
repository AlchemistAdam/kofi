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

    protected static final Entry[] EMPTY = new Entry[0];
    @Serial
    private static final long serialVersionUID = 0L;

    // TODO ensure that java strings are (un)escaped correctly when reflecting/reconstructing
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

        // object to assign field values and return
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
                            if (fieldType.equals(String.class)) {
                                field.set(obj, json.getJavaString((String) entry.value));
                            }
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

    @NotNull
    protected final Entry[] entries;

    public JsonObject() {
        entries = EMPTY;
    }

    protected JsonObject(@NotNull final Map<String, Object> map) {
        if (map.size() > 0)
            entries = map.entrySet().parallelStream()
                    .map(entry -> new Entry(entry.getKey(), entry.getValue()))
                    .toArray(Entry[]::new);
        else
            entries = EMPTY;
    }

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

    @NotNull
    @Override
    public String toJson() {
        final StringBuilder sb = new StringBuilder(size() * 16);
        toJson(sb);
        return sb.toString();
    }

    // TODO javadoc
    @Contract(pure = true)
    @Override
    protected void toJson(@NotNull final StringBuilder sb) {
        sb.append('{');
        for (int index = 0; index < entries.length; index++) {
            if (index > 0)
                sb.append(',');
            sb.append(" \"").append(entries[index].getName()).append("\": ");
            toJson(entries[index].getValue(), sb);
        }
        sb.append(" }");
    }

    public class Entry implements Comparable<Entry> {

        @NotNull
        public final String name;
        @Nullable
        protected Object value;

        public Entry(@NotNull final String name, @Nullable final Object value) throws NullPointerException {
            this.name = Objects.requireNonNull(name, "name is null");
            this.value = getDefinedObject(value);
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
            else if (obj instanceof Entry entry) // TODO should not be case sensitive
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

    public static class Builder {

        protected final TreeMap<String, Object> map = new TreeMap<>();

        @Contract(pure = true)
        @NotNull
        public JsonObject build() {
            return new JsonObject(map);
        }

        public void clear() {
            map.clear();
        }

        @Nullable
        public Object get(@NotNull final String name) throws NullPointerException {
            Objects.requireNonNull(name, "name is null");
            return map.get(name);
        }

        @NotNull
        public Builder put(@NotNull final Entry entry) throws NullPointerException {
            Objects.requireNonNull(entry, "entry is null");
            return put(entry.getName(), entry.getValue());
        }

        @NotNull
        public Builder put(@NotNull final String name, @Nullable final Object value) throws NullPointerException {
            Objects.requireNonNull(name, "name is null");
            map.put(name, value);
            return this;
        }

        @NotNull
        public Builder remove(@NotNull final String name) throws NullPointerException {
            Objects.requireNonNull(name, "name is null");
            map.remove(name);
            return this;
        }

        public int size() {
            return map.size();
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
