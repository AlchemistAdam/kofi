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
 * An abstract element, which can be represented as a string returned by
 * {@link #getString()}. This class does not define exactly what an element is
 * nor its string representation.
 * <p>
 * Examples of elements could be the lines of a text file, the values of an
 * array or the elements of a list.
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
     * Returns a copy of this element or throws a
     * {@code CloneNotSupportedException} if this element cannot be cloned.
     *
     * @throws CloneNotSupportedException if this element cannot be cloned
     */
    @SuppressWarnings("RedundantThrows")
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public abstract Element clone() throws CloneNotSupportedException;

    /**
     * Returns a string representation of this element as it would appear in a
     * textual context, such as a file or graphical user interface.
     */
    @Contract(pure = true)
    @NotNull
    public abstract String getString();

}
