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

public class Whitespace extends Element<String> implements Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    @Contract(pure = true)
    public Whitespace() { }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Whitespace clone() {
        return new Whitespace();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        else
            return obj instanceof Whitespace;
    }

    @Contract(pure = true)
    @NotNull
    @Override
    public String getString() {
        return "";
    }

    @Contract(pure = true)
    @Override
    protected int getHash() {
        return 0;
    }
}
