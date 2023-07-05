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
import java.util.Locale;
import java.util.Objects;

/**
 * An {@link Element element} used to categorize other elements. Sections only
 * store a string name. Section names are not case-sensitive when compared. The
 * string representation of a section, returned by {@link #getString()}, is
 * equal to:
 * <pre>
 *     "[<i>name</i>]"
 * </pre>
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class Section extends Element implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 202307052158L;

    /**
     * The name of this section.
     */
    @NotNull
    public final String name;
    /**
     * Cached hash code.
     */
    protected transient int hash = 0;
    /**
     * {@code true} if the computed hash code of this element is {@code 0}.
     */
    protected transient boolean hashIsZero = false;

    /**
     * Constructs a new section with the specified name.
     *
     * @param name the section name
     * @throws NullPointerException if {@code name} is {@code null}
     */
    @Contract(pure = true)
    public Section(@NotNull final String name) {
        this.name = Objects.requireNonNull(name, "name is null");
    }

    /**
     * Returns a new section with the same name as this section.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Section clone() {
        return new Section(name);
    }

    /**
     * Returns {@code true} if this section is equal to {@code obj}
     * ({@code this == obj}), or {@code obj} is also a section and their names
     * are equal, ignoring case. Otherwise {@code false} is returned.
     */
    @Contract(pure = true)
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof Section s)
            return name.equalsIgnoreCase(s.name);
        else
            return false;
    }

    /**
     * Returns an escaped version of this section's name.
     *
     * @see KofiUtil#escape(String)
     */
    @Contract(pure = true)
    public String getNameString() {
        return KofiUtil.escape(name);
    }

    /**
     * Returns a string representation of this section, equal to:
     * <pre>
     *     "[<i>name</i>]"
     * </pre>
     * where <i>name</i> is equal to the string returned by
     * {@link #getNameString()}.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getString() {
        return '[' + getNameString() + ']';
    }

    /**
     * Returns the hash code of this section's name in upper-case.
     */
    @Contract(pure = true)
    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0 && !hashIsZero) {
            h = name.toUpperCase(Locale.ROOT).hashCode();
            if (h == 0)
                hashIsZero = true;
            else
                hash = h;
        }
        return h;
    }

    /**
     * Returns {@code true} if the name of this section is equal to
     * {@code name}, ignoring case. Otherwise {@code false} is returned.
     */
    @Contract(value = "null -> false", pure = true)
    public boolean matches(@Nullable final String name) {
        return this.name.equalsIgnoreCase(name);
    }

    /**
     * Returns a string representation of this section, equal to:
     * <pre>
     *     "<i>className</i>@<i>hashCode</i>{name=<i>name</i>}"
     * </pre>
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String toString() {
        return this.getClass().getName() + '@' + hashCode() + "{name=" + name + '}';
    }
}
