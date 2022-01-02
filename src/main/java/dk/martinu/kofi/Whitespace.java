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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * An {@link Element element} used to mark pure whitespace, such as a blank
 * line in a text file. The string representation of whitespace, returned by
 * {@link #getString()}, is equal to an empty string {@code ""}.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class Whitespace extends Element implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new whitespace element.
     */
    @Contract(pure = true)
    public Whitespace() { }

    /**
     * Clones this whitespace element.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Whitespace clone() {
        return new Whitespace();
    }

    /**
     * Returns {@code true} if {@code obj} is a {@code Whitespace} object.
     * Otherwise {@code false} is returned.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Whitespace;
    }

    /**
     * Returns a string representation of this Whitespace element, equal to an
     * empty string {@code ""}.
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public String getString() {
        return "";
    }

    /**
     * Returns {@code 0}.
     */
    @Contract(pure = true)
    @Override
    public int hashCode() {
        return 0;
    }
}
