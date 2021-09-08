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

public class Section extends Element<String> implements Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    @NotNull
    protected final String section;

    @Contract(pure = true)
    public Section(@NotNull final String section) throws NullPointerException {
        this.section = Objects.requireNonNull(section, "section is null");
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Section clone() {
        return new Section(section);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof Section)
            return section.equalsIgnoreCase(((Section) obj).section);
        else
            return false;
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getString() {
        return '[' + section + ']';
    }

    @Contract(value = "null -> false", pure = true)
    public boolean matches(@Nullable final String section) {
        return this.section.equalsIgnoreCase(section);
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String toString() {
        return this.getClass().getName() + '@' + hashCode() + "{section=" + section + '}';
    }

    @Contract(pure = true)
    @Override
    protected int getHash() {
        return section.toLowerCase().hashCode();
    }
}
