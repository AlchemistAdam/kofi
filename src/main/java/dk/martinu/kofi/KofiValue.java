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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An abstract KoFi value implementation. This class does not restrict what
 * value types can be represented, but is intended for complex value types,
 * such as arrays and objects.
 *
 * @author Adam Martinu
 * @see KofiArray
 * @see KofiObject
 * @since 1.0
 */
public abstract class KofiValue {

    /**
     * Returns a string representation of this value as it would appear in a
     * textual context, such as a file or graphical user interface.
     */
    @NotNull
    public abstract String getString();

    /**
     * Creates a string representation of this object that conforms with the
     * KoFi Text Syntax and appends it to the specified {@code StringBuilder}.
     */
    protected abstract void getString(@NotNull final StringBuilder sb);

    /**
     * Appends a string representation of {@code value} to the specified
     * {@code StringBuilder}. This method assumes {@code value} is a
     * {@link KofiUtil#isDefinedType(Object) defined} KoFi value. If
     * {@code value} is an instance of {@code KofiValue}, then its own
     * {@link #getString(StringBuilder)} method will be called.
     *
     * @throws NullPointerException     if {@code sb} is {@code null}
     * @throws IllegalArgumentException if {@code value} is not defined
     * @see KofiUtil#getKofiValue(Object)
     */
    protected void getString(@Nullable Object value, @NotNull final StringBuilder sb) {
        Objects.requireNonNull(sb, "sb is null");
        if (KofiUtil.isDefinedType(value)) {
            if (value == null)
                sb.append("null");
            else if (value instanceof String s)
                sb.append(s);
            else if (value instanceof Number) {
                if (value instanceof Integer i)
                    sb.append(i.intValue());
                else if (value instanceof Long l)
                    sb.append(l.longValue()).append('L');
                else if (value instanceof Float f)
                    if (f.isNaN() || f.isInfinite())
                        sb.append(f.floatValue());
                    else
                        sb.append(f.floatValue()).append('F');
                else if (value instanceof Double d)
                    if (d.isNaN() || d.isInfinite())
                        sb.append(d.doubleValue());
                    else
                        sb.append(d.doubleValue()).append('d');
                else if (value instanceof Byte b)
                    sb.append(b.byteValue());
                else if (value instanceof Short s)
                    sb.append(s.shortValue());
            }
            else if (value instanceof Boolean b)
                sb.append(b.booleanValue());
            else if (value instanceof KofiValue kofiValue)
                kofiValue.getString(sb);
        }
        else
            throw KofiLog.exception(KofiValue.class, "getString(Object, StringBuilder)",
                    new IllegalArgumentException("value is not defined {" + value + "}"));
    }

}
