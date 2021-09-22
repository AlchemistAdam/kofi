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
import java.util.Objects;

/**
 * The {@code Section} class defines an {@link Element element} used to
 * categorize {@link Property properties} in a {@link Document document}.
 * Sections only have a {@code String} name and do not store any information
 * about properties they enclose in a document. Section names are not
 * case-sensitive when compared. The {@code String} representation of a section
 * is equal to:
 * <pre>
 *     "[<i>name</i>]"
 * </pre>
 * where <i>name</i> is equal to the string returned by
 * {@link #getNameString()}.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class Section extends Element implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * The name of this section.
     */
    @NotNull
    public final String name;

    /**
     * Constructs a new section with the specified {@code name}. The name of a
     * section is not case-sensitive when compared to other sections.
     *
     * @param name the section name.
     * @throws NullPointerException if {@code name} is {@code null}.
     */
    @Contract(pure = true)
    public Section(@NotNull final String name) throws NullPointerException {
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
     * Returns an escaped version of this section's name. The following
     * characters are escaped:
     * <ul>
     *     <li>{@code \n} new line, U+000A</li>
     *     <li>{@code \r} carriage return, U+000D</li>
     *     <li>{@code \\} reverse solidus, U+005C</li>
     * </ul>
     *
     * @see Element#escape(String, char...)
     */
    @Contract(pure = true)
    public String getNameString() {
        return escape(name, '\n', '\r', '\\');
    }

    /**
     * Returns a {@code String} representation of this section, equal to:
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
     * Returns {@code true} if the name of this section is equal to
     * {@code name}, ignoring case. Otherwise {@code false} is returned.
     */
    @Contract(value = "null -> false", pure = true)
    public boolean matches(@Nullable final String name) {
        return this.name.equalsIgnoreCase(name);
    }

    /**
     * Returns a {@code String} representation of this section, equal to:
     * <pre>
     *     "<i>class-name</i>@<i>hashCode</i>{name=<i>name</i>}"
     * </pre>
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String toString() {
        return this.getClass().getName() + '@' + hashCode() + "{name=" + name + '}';
    }

    /**
     * Returns the hash code of this section's name in lower-case.
     */
    @Contract(pure = true)
    @Override
    protected int hashCodeImpl() {
        return name.toLowerCase().hashCode();
    }
}
