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

public abstract class Element<V> implements Cloneable {

    private transient int hash = 0;
    private transient boolean hashIsZero = false;

    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public abstract Element<V> clone() throws CloneNotSupportedException;

    @Contract(pure = true)
    @NotNull
    public abstract String getString();

    @Contract(pure = true)
    @Override
    public int hashCode() {
        // implementation derived from String.hashCode()
        int h = hash;
        if (h == 0 && !hashIsZero) {
            h = getHash();
            if (h == 0)
                hashIsZero = true;
            else
                hash = h;
        }
        return h;
    }

    @Contract(pure = true)
    protected abstract int getHash();
}
