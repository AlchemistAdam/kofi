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

/**
 * <p>Abstract implementation of an element in a {@link Document}. An element can
 * be represented as a {@code String} returned by {@link #getString()}.
 *
 * @author Adam Martinu
 * @see Comment
 * @see Property
 * @see Section
 * @see Whitespace
 * @since 1.0
 */
public abstract class Element {

    /**
     * Returns an escaped version of the specified string {@code s}. The
     * characters to escape are specified in the {@code chars} array. If no
     * characters were escaped, then {@code s} is returned.
     */
    @Contract(pure = true)
    @NotNull
    protected static String escape(@NotNull final String s, final char... chars) {
        final StringBuilder sb = new StringBuilder(s.length());
        outer:
        for (int i = 0; i < s.length(); i++) {
            final char c0 = s.charAt(i);
            for (char c1 : chars)
                if (c0 == c1) {
                    sb.append('\\').append(c0);
                    continue outer;
                }
            sb.append(c0);
        }
        return s.length() == sb.length() ? s : sb.toString();
    }

    /**
     * Returns a copy of this element or throws a
     * {@code CloneNotSupportedException} if this element cannot be cloned.
     *
     * @throws CloneNotSupportedException if this element cannot be cloned.
     */
    @SuppressWarnings("RedundantThrows")
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public abstract Element clone() throws CloneNotSupportedException;

    /**
     * Returns a {@code String} representation of this element as it would
     * appear in a textual context, e.g. a file or user interface.
     */
    @Contract(pure = true)
    @NotNull
    public abstract String getString();

}
