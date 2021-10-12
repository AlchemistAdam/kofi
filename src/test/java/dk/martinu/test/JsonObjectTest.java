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

import dk.martinu.kofi.JsonObject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonObjectTest {

    @Test
    void reconstructNumberObject() {
        final JsonObject json = new JsonObject(
                new JsonObject.Entry("n0", 4),
                new JsonObject.Entry("n1", 8)
        );
        assertDoesNotThrow(() -> {
            final NumberObject object = JsonObject.reconstruct(json, NumberObject.class);
            assertEquals(4, object.n0);
            assertEquals(8, object.n1);
        });
    }

    @Test
    void reflectNumberObject() {
        final NumberObject object = new NumberObject();
        final JsonObject json = JsonObject.reflect(object);

        assertEquals(2, json.size());
        assertEquals(1, json.getEntry(0).getValue());
        assertEquals(2, json.getEntry(1).getValue());
        assertEquals(1, json.get("n0"));
        assertEquals(2, json.get("n1"));
    }

    public static class NumberObject {

        public int n0 = 1;
        public int n1 = 2;
    }
}
