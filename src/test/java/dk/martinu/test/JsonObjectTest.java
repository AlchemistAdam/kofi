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

import dk.martinu.kofi.*;
import dk.martinu.kofi.codecs.KofiCodec;

import static org.junit.jupiter.api.Assertions.*;

public class JsonObjectTest {

    final KofiCodec codec = KofiCodec.provider();
    final Value<Document> value = new Value<>();

    @Test
    void mixedObject() {
        final JsonObject.Builder builder = new JsonObject.Builder()
                .put("number", 1L)
                .put("string", "Hello, World!")
                .put("bool", true)
                .put("array", new JsonArray(1, 2, 3));
        final JsonObject object = builder.build();

        assertEquals(builder.size(), object.size());
        for (int i = 0; i < builder.size(); i++) {
            final JsonObject.Entry entry = object.getEntry(i);
            if (entry.getValue() instanceof String s)
                assertEquals('"' + (String) builder.get(entry.getName()) + '"', s);
            else
                assertEquals(builder.get(entry.getName()), entry.getValue());
        }

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + object.toJson())));
        final JsonObject parsedObject = value.get().getObject("v");
        assertNotNull(parsedObject);
        assertEquals(object, parsedObject);
        assertEquals(builder.size(), parsedObject.size());
        for (int i = 0; i < builder.size(); i++) {
            final JsonObject.Entry entry = parsedObject.getEntry(i);
            if (entry.getValue() instanceof String s)
                assertEquals('"' + (String) builder.get(entry.getName()) + '"', s);
            else
                assertEquals(builder.get(entry.getName()), entry.getValue());
        }
    }

    @Test
    void nestedJsonObject() {
        final JsonObject.Builder builder = new JsonObject.Builder()
                .put("n0", new JsonObject.Builder().put("number", 1L).build())
                .put("n1", new JsonObject.Builder().put("string", "Hello, World").build())
                .put("n2", new JsonObject.Builder().put("bool", false).build());
        final JsonObject object = builder.build();

        assertEquals(builder.size(), object.size());
        for (int i = 0; i < builder.size(); i++) {
            final JsonObject.Entry entry = object.getEntry(i);
            if (entry.getValue() instanceof String s)
                assertEquals('"' + (String) builder.get(entry.getName()) + '"', s);
            else
                assertEquals(builder.get(entry.getName()), entry.getValue());
        }

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + object.toJson())));
        final JsonObject parsedObject = value.get().getObject("v");
        assertNotNull(parsedObject);
        assertEquals(object, parsedObject);
        assertEquals(builder.size(), parsedObject.size());
        for (int i = 0; i < builder.size(); i++) {
            final JsonObject.Entry entry = parsedObject.getEntry(i);
            if (entry.getValue() instanceof String s)
                assertEquals('"' + (String) builder.get(entry.getName()) + '"', s);
            else
                assertEquals(builder.get(entry.getName()), entry.getValue());
        }
    }

    @Test
    void reconstructNumberObject() {
        final JsonObject.Builder builder = new JsonObject.Builder()
                .put("n0", 4L)
                .put("n1", 8L);
        final JsonObject object = builder.build();

        assertEquals(builder.size(), object.size());
        for (int i = 0; i < builder.size(); i++) {
            final JsonObject.Entry entry = object.getEntry(i);
            assertEquals(builder.get(entry.getName()), entry.getValue());
        }

        // TODO use reflection when asserting equals and add more fields to NumberObject
        assertDoesNotThrow(() -> {
            final NumberObject numberObject = JsonObject.reconstruct(object, NumberObject.class);
            assertEquals(4L, numberObject.n0);
            assertEquals(8L, numberObject.n1);
        });
    }

    @Test
    void reflectNumberObject() {
        final NumberObject object = new NumberObject();
        final JsonObject json = JsonObject.reflect(object);

        assertEquals(2, json.size());
        assertEquals(1L, json.getEntry(0).getValue());
        assertEquals(2L, json.getEntry(1).getValue());
        assertEquals(1L, json.get("n0"));
        assertEquals(2L, json.get("n1"));
    }

    static class NumberObject {

        public long n0 = 1L;
        public long n1 = 2L;
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
