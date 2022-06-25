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

    public V value_b;

    public Dummy2() {
        this(null, null);
    }

    public Dummy2(final V value_a, final V value_b) {
        super(value_a);
        this.value_b = value_b;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        else if (obj instanceof Dummy2 d && super.equals(d)) {
            if (value_b == null)
                return d.value_b == null;
            else if (!value_b.getClass().isArray())
                return value_b.equals(d.value_b);
            else if (d.value_b.getClass().isArray())
                return Arrays.deepEquals((Object[]) value_b, (Object[]) d.value_b);
            else
                return false;
        }
        else
            return false;
    }
}
