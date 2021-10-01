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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class JsonObject extends Json implements Iterable<JsonObject.Entry>, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    // TODO test
    @NotNull
    public static JsonObject reflect(@NotNull final Object object) throws NullPointerException {
        Objects.requireNonNull(object, "object is null");
        // class for reflection
        final Class<?> cl = object.getClass();
        // list of entries in the JSON object
        final ArrayList<Entry> entries = new ArrayList<>();
        for (Field field : cl.getFields())
            if (!Modifier.isStatic(field.getModifiers()) && field.canAccess(object)) {
                final Object value;
                try {
                    value = field.get(object);
                }
                catch (IllegalAccessException e) {
                    // reflection access is already checked, this should never happen
                    KofiLog.severe("Field could not be accessed {object=" + object + ", field=" + field.getName() + "}");
                    throw new RuntimeException(e);
                }
                entries.add(new Entry(field.getName(), value));
            }
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
