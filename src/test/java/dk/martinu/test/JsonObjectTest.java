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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonObjectTest {

    @Test
    void numberObject() {
        final NumberObject object = new NumberObject();
        final JsonObject json = JsonObject.reflect(object);

        assertEquals(2, json.size());
        assertEquals(1, json.getEntry(0).getValue());
        assertEquals(2, json.getEntry(1).getValue());
        assertEquals(1, json.get("one"));
        assertEquals(2, json.get("two"));
    }

    public static class NumberObject {

        public final int one = 1;
        public final int two = 2;
    }
}
