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

package dk.martinu.test;

import org.junit.jupiter.api.Test;

import dk.martinu.kofi.KofiObject;
import dk.martinu.test.dummy.Dummy2;
import dk.martinu.test.dummy.NestedDummy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KofiObjectTest {
    /**
     * Test for {@link KofiObject#construct(Class)}
     */
    @Test
    void construct() {
        // simple object
        {
            final KofiObject.Builder builder = new KofiObject.Builder()
                    .put("value_a", 1)
                    .put("value_b", 2);
            final KofiObject object = builder.build();

            assertEquals(builder.size(), object.size());
            for (int i = 0; i < builder.size(); i++) {
                final KofiObject.Entry entry = object.getEntry(i);
                assertEquals(builder.get(entry.getName()), entry.getValue());
            }

            assertDoesNotThrow(() -> {
                //noinspection unchecked
                final Dummy2<Integer> dummy = object.construct(Dummy2.class);
                assertEquals(1, dummy.v0);
                assertEquals(2, dummy.v1);
            });
        }

        // nested object
        {
            final KofiObject.Builder builder = new KofiObject.Builder()
                    .put("value_a",
                            new KofiObject.Builder().put("value_a", 1).build());
            final KofiObject object = builder.build();

            assertEquals(builder.size(), object.size());
            for (int i = 0; i < builder.size(); i++) {
                final KofiObject.Entry entry = object.getEntry(i);
                assertEquals(builder.get(entry.getName()), entry.getValue());
            }

            assertDoesNotThrow(() -> {
                //noinspection unchecked
                final NestedDummy<Integer> dummy = object.construct(NestedDummy.class);
                assertEquals(1, dummy.dummy.v0);
            });
        }
    }

    /**
     * Test for {@link KofiObject#reflect(Object)}
     */
    @Test
    void reflect() {
        final Dummy2<Integer> numbers = new Dummy2<>(1, 2);
        final KofiObject object = KofiObject.reflect(numbers);

        assertEquals(2, object.size());
        assertEquals(1, object.get("value_a"));
        assertEquals(2, object.get("value_b"));
    }
}
