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

public class NestedDummy<V> {

    public Dummy<V> dummy;

    public NestedDummy() {
        this(null);
    }

    public NestedDummy(final Dummy<V> dummy) {
        this.dummy = dummy;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        else if (obj instanceof NestedDummy<?> nested) {
            if (dummy == null)
                return nested.dummy == null;
            // TODO will always be true?
            else if (!dummy.getClass().isArray())
                return dummy.equals(nested.dummy);
            else
                return false;
        }
        else
            return false;
    }
}
