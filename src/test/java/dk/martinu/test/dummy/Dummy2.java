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

public class Dummy2<V> extends Dummy<V> {

    public static <V> Dummy2<V> of (final V v0, final V v1) {
        return new Dummy2<>(v0, v1);
    }

    public V v1;

    public Dummy2() {
        this(null, null);
    }

    public Dummy2(final V value_a, final V v1) {
        super(value_a);
        this.v1 = v1;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        else if (obj instanceof Dummy2<?> d && super.equals(d)) {
            if (v1 == null)
                return d.v1 == null;
            else if (!v1.getClass().isArray())
                return v1.equals(d.v1);
            else if (d.v1.getClass().isArray())
                return Arrays.deepEquals((Object[]) v1, (Object[]) d.v1);
            else
                return false;
        }
        else
            return false;
    }
}
