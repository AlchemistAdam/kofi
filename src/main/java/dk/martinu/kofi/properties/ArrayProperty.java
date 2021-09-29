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

package dk.martinu.kofi.properties;

import org.jetbrains.annotations.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

import dk.martinu.kofi.*;

public class ArrayProperty extends Property<JsonArray> implements Cloneable, Serializable, Iterable<Object> {

    @Serial
    private static final long serialVersionUID = 0L;

    protected static void getValueStringOf(@NotNull final JsonArray array, @NotNull final StringBuilder sb) {
        sb.append('[');
        for (int index = 0; index < array.length(); index++) {
            if (index > 0)
                sb.append(',');
            sb.append(' ');

            Object value = array.get(index);
            if (value instanceof String s)
                sb.append('\"').append(StringProperty.escape(s)).append('\"');
            else if (value instanceof Integer i)
                sb.append((int) i);
            else if (value instanceof Long l)
                sb.append((long) l).append('L');
            else if (value instanceof Float f)
                sb.append((float) f).append('f');
            else if (value instanceof Double d)
                sb.append((double) d).append('d');
            else if (value instanceof Character c)
                sb.append('\'').append((char) c).append('\'');
            else if (value instanceof Boolean b)
                sb.append((boolean) b);
            else if (value instanceof JsonArray jsonArray)
                getValueStringOf(jsonArray, sb);
            else if (value instanceof JsonObject jsonObject)
                ObjectProperty.getValueStringOf(jsonObject, sb);
            else
                sb.append(value); // TODO should log a warning
        }
        sb.append(' ').append(']');
    }

    @Contract(pure = true)
    public ArrayProperty(@NotNull final String key, @Nullable final JsonArray array) throws NullPointerException {
        super(key, Objects.requireNonNullElse(array, new JsonArray()));
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public ArrayProperty clone() {
        return new ArrayProperty(key, value);
    }

    @Override
    public void forEach(@NotNull final Consumer<? super Object> action) throws NullPointerException {
        //noinspection ConstantConditions
        for (Object o : value)
            action.accept(o);
    }

    @Contract(pure = true)
    @NotNull
    @Override
    public Class<JsonArray> getValueClass() {
        return JsonArray.class;
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    @Override
    public String getValueString() {
        final StringBuilder sb = new StringBuilder(value.length() * 8);
        getValueStringOf(value, sb);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return hashCodeImpl();
    }

    @Contract(value = "-> new")
    @NotNull
    @Override
    public Iterator<Object> iterator() {
        //noinspection ConstantConditions
        return value.iterator();
    }

    @Contract(value = "-> new")
    @NotNull
    @Override
    public Spliterator<Object> spliterator() {
        //noinspection ConstantConditions
        return value.spliterator();
    }
}
