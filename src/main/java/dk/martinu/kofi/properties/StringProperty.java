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
import java.util.Objects;

import dk.martinu.kofi.Property;

public class StringProperty extends Property<String> implements Cloneable, Serializable {

    // TODO find usages and determine best enclosing class for this method
    @NotNull
    public static String escape(@NotNull final String s) {
        final char[] chars = s.toCharArray();
        final StringBuilder sb = new StringBuilder(chars.length);
        for (char c : chars) {
            switch (c) {
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\f' -> sb.append("\\f");
                case '\0' -> sb.append("\\0");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    @Serial
    private static final long serialVersionUID = 0L;

    @Nullable
    protected String escapedString = null;

    @Contract(pure = true)
    public StringProperty(@NotNull final String key, @Nullable final String value) throws NullPointerException {
        super(key, Objects.requireNonNullElse(value, ""));
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public StringProperty clone() {
        return new StringProperty(key, value);
    }

    @Contract(pure = true)
    @NotNull
    @Override
    public Class<String> getValueClass() {
        return String.class;
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getValueString() {
        if (escapedString == null)
            escapedString = escape(value);
        return '\"' + escapedString + '\"';
    }
}
