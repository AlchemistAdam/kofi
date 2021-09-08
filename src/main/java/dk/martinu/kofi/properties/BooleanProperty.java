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

package dk.martinu.kofi.properties;

import org.jetbrains.annotations.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import dk.martinu.kofi.Property;

public class BooleanProperty extends Property<Boolean> implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    @Contract(pure = true)
    public BooleanProperty(@NotNull final String key, @Nullable final Boolean value) throws NullPointerException {
        super(key, Objects.requireNonNullElse(value, false));
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public BooleanProperty clone() {
        return new BooleanProperty(key, value);
    }

    @Contract(pure = true)
    @NotNull
    @Override
    public Class<Boolean> getValueClass() {
        return Boolean.class;
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getValueString() {
        return Boolean.toString(value);
    }
}
