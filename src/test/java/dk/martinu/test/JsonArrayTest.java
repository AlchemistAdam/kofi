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

import dk.martinu.kofi.JsonArray;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonArrayTest {

    @Test
    void mixedArray() {
        final Object[] objects = {
                1,
                "Hello, World!",
                true,
                new JsonArray(1, 2, 3)
        };
        final JsonArray json = new JsonArray(objects);

        assertEquals(objects.length, json.length());
        for (int i = 0; i < objects.length; i++)
            if (objects[i] instanceof String s)
                assertEquals('"' + s + '"', json.get(i));
            else
                assertEquals(objects[i], json.get(i));
    }

    @Test
    void nestedJsonArray() {
        final JsonArray[] nested = {
                new JsonArray(1, 2, 3),
                new JsonArray(4, 5, 6),
                new JsonArray(7, 8, 9)
        };
        final JsonArray json = new JsonArray((Object[]) nested);

        assertEquals(nested.length, json.length());
        for (int i = 0; i < nested.length; i++)
            assertEquals(nested[i], json.get(i));
    }

    @Test
    void nestedPrimitiveArray() {
        final int[][] nested = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        final JsonArray json = new JsonArray((Object[]) nested);

        assertEquals(nested.length, json.length());
        for (int i = 0; i < nested.length; i++)
            assertEquals(JsonArray.reflect(nested[i]), json.get(i));
    }

    @Test
    void nestedWrapperArray() {
        final Integer[][] nested = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        final JsonArray json = new JsonArray((Object[]) nested);

        assertEquals(nested.length, json.length());
        for (int i = 0; i < nested.length; i++)
            assertEquals(JsonArray.reflect(nested[i]), json.get(i));
    }

    @Test
    void primitiveArray() {
        final int[] fibonacci = {1, 1, 2, 3, 5, 8, 13};
        final JsonArray json = JsonArray.reflect(fibonacci);

        assertEquals(fibonacci.length, json.length());
        for (int i = 0; i < fibonacci.length; i++)
            assertEquals(fibonacci[i], json.get(i));
    }

    @Test
    void wrapperArray() {
        final Integer[] fibonacci = {1, 1, 2, 3, 5, 8, 13};
        final JsonArray json = JsonArray.reflect(fibonacci);

        assertEquals(fibonacci.length, json.length());
        for (int i = 0; i < fibonacci.length; i++)
            assertEquals(fibonacci[i], json.get(i));
    }
}
