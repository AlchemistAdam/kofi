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

import dk.martinu.kofi.Document;
import dk.martinu.kofi.JsonArray;
import dk.martinu.kofi.codecs.KofiCodec;

import static org.junit.jupiter.api.Assertions.*;

public class JsonArrayTest {

    final KofiCodec codec = KofiCodec.provider();
    final Value<Document> value = new Value<>();

    @Test
    void mixedArray() {
        final Object[] objects = {
                1L,
                "Hello, World!",
                true,
                new JsonArray(1L, 2L, 3L)
        };
        final JsonArray array = new JsonArray(objects);

        assertEquals(objects.length, array.length());
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof String s)
                assertEquals('"' + s + '"', array.get(i));
            else
                assertEquals(objects[i], array.get(i));
        }

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + array.toJson())));
        final JsonArray parsedArray = value.get().getArray("v");
        assertNotNull(parsedArray);
        assertEquals(array, parsedArray);
        assertEquals(objects.length, parsedArray.length());
        for (int i = 0; i < objects.length; i++)
            if (objects[i] instanceof String s)
                assertEquals('"' + s + '"', parsedArray.get(i));
            else
                assertEquals(objects[i], parsedArray.get(i));
    }

    @Test
    void nestedJsonArray() {
        final Object[] objects = {
                new JsonArray(1, 2, 3),
                new JsonArray(4, 5, 6),
                new JsonArray(7, 8, 9)
        };
        final JsonArray array = new JsonArray(objects);

        assertEquals(objects.length, array.length());
        for (int i = 0; i < objects.length; i++)
            assertEquals(objects[i], array.get(i));

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + array.toJson())));
        final JsonArray parsedArray = value.get().getArray("v");
        assertNotNull(parsedArray);
        assertEquals(array, parsedArray);
        assertEquals(objects.length, parsedArray.length());
        for (int i = 0; i < objects.length; i++)
            if (objects[i] instanceof String s)
                assertEquals('"' + s + '"', parsedArray.get(i));
            else
                assertEquals(objects[i], parsedArray.get(i));
    }

    @Test
    void nestedPrimitiveArray() {
        final Object[] objects = {
                new long[] {1, 2, 3},
                new long[] {4, 5, 6},
                new long[] {7, 8, 9}
        };
        final JsonArray array = new JsonArray(objects);

        assertEquals(objects.length, array.length());
        for (int i = 0; i < objects.length; i++)
            assertEquals(JsonArray.reflect(objects[i]), array.get(i));

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + array.toJson())));
        final JsonArray parsedArray = value.get().getArray("v");
        assertNotNull(parsedArray);
        assertEquals(array, parsedArray);
        assertEquals(objects.length, parsedArray.length());
        for (int i = 0; i < objects.length; i++)
            if (objects[i] instanceof String s)
                assertEquals('"' + s + '"', parsedArray.get(i));
            else
                assertEquals(JsonArray.reflect(objects[i]), parsedArray.get(i));
    }

    @Test
    void nestedWrapperArray() {
        final Object[] objects = {
                new Long[] {1L, 2L, 3L},
                new Long[] {4L, 5L, 6L},
                new Long[] {7L, 8L, 9L}
        };
        final JsonArray array = new JsonArray(objects);

        assertEquals(objects.length, array.length());
        for (int i = 0; i < objects.length; i++)
            assertEquals(JsonArray.reflect(objects[i]), array.get(i));

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + array.toJson())));
        final JsonArray parsedArray = value.get().getArray("v");
        assertNotNull(parsedArray);
        assertEquals(array, parsedArray);
        assertEquals(objects.length, parsedArray.length());
        for (int i = 0; i < objects.length; i++)
            if (objects[i] instanceof String s)
                assertEquals('"' + s + '"', parsedArray.get(i));
            else
                assertEquals(JsonArray.reflect(objects[i]), parsedArray.get(i));
    }

    @Test
    void primitiveArray() {
        final long[] fibonacci = {1, 1, 2, 3, 5, 8, 13};
        final JsonArray array = JsonArray.reflect(fibonacci);

        assertEquals(fibonacci.length, array.length());
        for (int i = 0; i < fibonacci.length; i++)
            assertEquals(fibonacci[i], array.get(i));

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + array.toJson())));
        final JsonArray parsedArray = value.get().getArray("v");
        assertNotNull(parsedArray);
        assertEquals(array, parsedArray);
        assertEquals(fibonacci.length, parsedArray.length());
        for (int i = 0; i < fibonacci.length; i++)
            assertEquals(fibonacci[i], parsedArray.get(i));
    }

    @Test
    void wrapperArray() {
        final Long[] fibonacci = {0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L};
        final JsonArray array = JsonArray.reflect(fibonacci);

        assertEquals(fibonacci.length, array.length());
        for (int i = 0; i < fibonacci.length; i++)
            assertEquals(fibonacci[i], array.get(i));

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + array.toJson())));
        final JsonArray parsedArray = value.get().getArray("v");
        assertNotNull(parsedArray);
        assertEquals(array, parsedArray);
        assertEquals(fibonacci.length, parsedArray.length());
        for (int i = 0; i < fibonacci.length; i++)
            assertEquals(fibonacci[i], parsedArray.get(i));
    }

    static class Value<T> {

        T value = null;

        T get() {
            return value;
        }

        void put(T value) {
            this.value = value;
        }
    }
}
