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
import java.util.Locale;
import java.util.Objects;

/**
 * The {@code Comment} class defines an {@link Element element} used to store a
 * text comment in a {@link Document document}. A Comment does not store any
 * information about what it describes, and its primary purpose is to retain
 * human-made comments when reading a {@code Document} from a file. The
 * {@code String} representation of a comment is equal to:
 * <pre>
 *     ";<i>text</i>"
 * </pre>
 * where <i>text</i> is equal to the string returned by
 * {@link #getTextString()}.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class Comment extends Element implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0;

    /**
     * The text of this comment.
     */
    @NotNull
    public final String text;
    /**
     * Cached hash code. Set on first call to {@link #hashCode()}.
     */
    protected transient int hash = 0;
    /**
     * {@code true} if the computed hash code of this element is {@code 0}. Set
     * on first call to {@link #hashCode()}.
     */
    protected transient boolean hashIsZero = false;

    /**
     * Constructs a new comment with the specified {@code text}.
     *
     * @param text the comment text.
     * @throws NullPointerException if {@code text} is {@code null}.
     */
    @Contract(pure = true)
    public Comment(@NotNull final String text) throws NullPointerException {
        this.text = Objects.requireNonNull(text, "text is null");
    }

    /**
     * Returns a new comment with the same text as this comment.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Comment clone() {
        return new Comment(text);
    }

    /**
     * Returns {@code true} if this comment is equal to {@code obj}
     * ({@code this == obj}), or {@code obj} is also a comment and their text
     * is equal. Otherwise {@code false} is returned.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof Comment comment)
            return text.equals(comment.text);
        else
            return false;
    }

    /**
     * Returns a {@code String} representation of this comment, equal to:
     * <pre>
     *     ";<i>text</i>"
     * </pre>
     * where <i>text</i> is equal to the string returned by
     * {@link #getTextString()}.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getString() {
        return ';' + getTextString();
    }

    /**
     * Returns an escaped version of this comment's text. The following
     * characters are escaped:
     * <ul>
     *     <li>{@code \n} new line, U+000A</li>
     *     <li>{@code \r} carriage return, U+000D</li>
     *     <li>{@code \\} reverse solidus, U+005C</li>
     * </ul>
     *
     * @see Element#escape(String, char...)
     */
    @NotNull
    public String getTextString() {
        return escape(text, '\n', '\r', '\\');
    }

    /**
     * Returns the hash code of this comment's text.
     */
    @Contract(pure = true)
    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0 && !hashIsZero) {
            h = text.hashCode();
            if (h == 0)
                hashIsZero = true;
            else
                hash = h;
        }
        return h;
    }

    /**
     * Returns a {@code String} representation of this comment, equal to:
     * <pre>
     *     "<i>class-name</i>@<i>hashCode</i>{text=<i>text</i>}"
     * </pre>
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String toString() {
        return this.getClass().getName() + '@' + hashCode() + "{text=" + text + '}';
    }
}
