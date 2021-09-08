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

public class JsonObject implements Iterable<JsonObject.Entry>, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    @NotNull
    protected Entry[] entries;

    public JsonObject(@Nullable final Entry... entries) {
        //noinspection NullableProblems
        this.entries = Objects.requireNonNullElse(entries, new Entry[0]);
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

    @Contract(pure = true)
    public Entry get(final int index) throws ArrayIndexOutOfBoundsException {
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
    public int length() {
        return entries.length;
    }

    @Contract(pure = true)
    @Override
    public Spliterator<Entry> spliterator() {
        return Arrays.spliterator(entries);
    }

    public static class Entry {

        @NotNull
        private final String key;
        @NotNull
        private Object value;

        public Entry(@NotNull final String key, @NotNull final Object value) throws NullPointerException {
            this.key = Objects.requireNonNull(key, "key is null");
            this.value = Objects.requireNonNull(value, "value is null");
        }

        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(@Nullable final Object obj) {
            if (this == obj)
                return true;
            else if (obj instanceof Entry entry) // TODO is JSON case sensitive?
                return key.equals(entry.key) && value.equals(entry.value);
            else
                return false;
        }

        @Contract(pure = true)
        @Override
        public int hashCode() {
            return key.toLowerCase().hashCode() | value.hashCode() << 16;
        }

        @Contract(pure = true)
        @NotNull
        public String key() {
            return key;
        }

        public void setValue(@NotNull final Object value) throws NullPointerException {
            this.value = Objects.requireNonNull(value, "value is null");
        }

        @Contract(pure = true)
        @NotNull
        public Object value() {
            return value;
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
