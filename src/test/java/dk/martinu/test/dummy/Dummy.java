/*
 * Copyright (c) 2022, Adam Martinu. All rights reserved. Altering or
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

package dk.martinu.test.dummy;

import java.util.Arrays;

public class Dummy<V> {

    public static <V> Dummy<V> of (final V v) {
        return new Dummy<>(v);
    }

    public V v0;

    public Dummy() {
        this(null);
    }

    public Dummy(final V v0) {
        this.v0 = v0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        else if (obj instanceof Dummy<?> d) {
            if (v0 == null)
                return d.v0 == null;
            else if (!v0.getClass().isArray())
                return v0.equals(d.v0);
            else if (d.v0.getClass().isArray())
                return Arrays.deepEquals((Object[]) v0, (Object[]) d.v0);
            else
                return false;
        }
        else
            return false;
    }
}
