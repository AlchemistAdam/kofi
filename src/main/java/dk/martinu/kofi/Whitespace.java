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
 * The {@code Whitespace} class defines an {@link Element element} used to mark
 * an index as pure whitespace in a {@link Document document} (a blank line).
 * The {@code String} representation of a {@code Whitespace} element is equal
 * to an empty string {@code ""}.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class Whitespace extends Element implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs a new {@code Whitespace} element.
     */
    @Contract(pure = true)
    public Whitespace() { }

    /**
     * Clones this {@code Whitespace} element.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Whitespace clone() {
        return new Whitespace();
    }

    /**
     * Returns {@code true} if {@code obj} is a {@code Whitespace} element.
     * Otherwise {@code false} is returned.
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Whitespace;
    }

    /**
     * Returns a {@code String} representation of this {@code Whitespace}
     * element, equal to an empty string {@code ""}.
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
