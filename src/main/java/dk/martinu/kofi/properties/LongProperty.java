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

public class LongProperty extends Property<Long> implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    @Contract(pure = true)
    public LongProperty(@NotNull final String key, @Nullable final Long value) throws NullPointerException {
        super(key, Objects.requireNonNullElse(value, 0L));
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public LongProperty clone() {
        return new LongProperty(key, value);
    }

    @Contract(pure = true)
    @NotNull
    @Override
    public Class<Long> getValueClass() {
        return Long.class;
    }

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public String getValueString() {
        return Long.toString(value) + 'L';
    }
}
