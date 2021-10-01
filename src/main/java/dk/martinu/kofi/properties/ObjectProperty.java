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

import dk.martinu.kofi.*;

public class ObjectProperty extends Property<JsonObject> implements Cloneable, Serializable,
        Iterable<JsonObject.Entry> {

    @Serial
    private static final long serialVersionUID = 0L;

    public static void getValueStringOf(@NotNull final JsonObject object, @NotNull final StringBuilder sb) {
        sb.append('{');
        JsonObject.Entry entry = null;
        for (int index = 0; index < object.size(); index++) {
            if (entry != null)
                sb.append(',');

            entry = object.getEntry(index);
            final String name = entry.getName();
            final Object value = entry.getValue();
            sb.append(" \"").append(name).append("\": ");
            if (value == null)
                sb.append("null");
            else if (value instanceof String s)
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
                ArrayProperty.getValueStringOf(jsonArray, sb);
            else if (value instanceof JsonObject jsonObject)
                getValueStringOf(jsonObject, sb);
            else {
                sb.append(value);
                KofiLog.warning("Unknown value type in JSON object {name=" + name + ", value=" + value + "}");
            }
        }
        sb.append(' ').append('}');
    }

    @Contract(pure = true)
    public ObjectProperty(@NotNull final String key, @Nullable final JsonObject object) throws NullPointerException {
        super(key, Objects.requireNonNullElse(object, new JsonObject()));
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public ObjectProperty clone() {
        return new ObjectProperty(key, value);
    }

    @Contract(pure = true)
    @NotNull
    @Override
    public Class<JsonObject> getValueClass() {
        return JsonObject.class;
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    @Override
    public String getValueString() {
        final StringBuilder sb = new StringBuilder(value.size() * 16);
        getValueStringOf(value, sb);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return hashCodeImpl();
    }

    @NotNull
    @Override
    public Iterator<JsonObject.Entry> iterator() {
        //noinspection ConstantConditions
        return value.iterator();
    }

    @Override
    public Spliterator<JsonObject.Entry> spliterator() {
        //noinspection ConstantConditions
        return value.spliterator();
    }
}
