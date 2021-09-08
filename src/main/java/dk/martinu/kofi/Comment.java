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
import java.util.Objects;

public class Comment extends Element<String> implements Serializable {

    @Serial
    private static final long serialVersionUID = 0;

    @NotNull
    protected final String comment;

    @Contract(pure = true)
    public Comment(@NotNull final String comment) throws NullPointerException {
        this.comment = Objects.requireNonNull(comment, "comment is null");
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Comment clone() {
        return new Comment(comment);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof Comment)
            return comment.equals(((Comment) obj).comment);
        else
            return false;
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getString() {
        return ';' + comment;
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String toString() {
        return this.getClass().getName() + '@' + hashCode() + "{comment=" + comment + '}';
    }

    @Contract(pure = true)
    @Override
    protected int getHash() {
        return comment.hashCode();
    }
}
