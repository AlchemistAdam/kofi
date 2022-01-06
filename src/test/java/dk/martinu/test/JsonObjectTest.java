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

import java.awt.Rectangle;

import dk.martinu.kofi.*;
import dk.martinu.kofi.codecs.KofiCodec;

import static org.junit.jupiter.api.Assertions.*;

public class JsonObjectTest {

    final KofiCodec codec = KofiCodec.provider();
    Document document = null;

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

        assertDoesNotThrow(() -> document = codec.readString("v = " + object.toJson()));
        final JsonObject parsedObject = document.getObject("v");
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

        assertDoesNotThrow(() -> document = codec.readString("v = " + object.toJson()));
        final JsonObject parsedObject = document.getObject("v");
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
    void reconstructArea() {
        final JsonObject.Builder builder = new JsonObject.Builder()
                .put("union", new Rectangle[] {new Rectangle(0, 0, 400, 100),
                        new Rectangle(0, 300, 400, 100)})
                .put("subtract", new Rectangle[] {new Rectangle(50, 50, 300, 300)});
        final JsonObject object = builder.build();

        assertEquals(builder.size(), object.size());

        assertDoesNotThrow(() -> {
            final Area area = JsonObject.reconstruct(object, Area.class);
            assertEquals(new Rectangle[] {new Rectangle(0, 0, 400, 100),
                    new Rectangle(0, 300, 400, 100)}, area.union);
            assertEquals(new Rectangle[] {new Rectangle(50, 50, 300, 300)}, area.subtract);
        });
    }

    @Test
    void reconstructNumbers() {
        final JsonObject.Builder builder = new JsonObject.Builder()
                .put("n0", 4L)
                .put("n1", 8L);
        final JsonObject object = builder.build();

        assertEquals(builder.size(), object.size());
        for (int i = 0; i < builder.size(); i++) {
            final JsonObject.Entry entry = object.getEntry(i);
            assertEquals(builder.get(entry.getName()), entry.getValue());
        }

        assertDoesNotThrow(() -> {
            final Numbers numbers = JsonObject.reconstruct(object, Numbers.class);
            assertEquals(4L, numbers.n0);
            assertEquals(8L, numbers.n1);
        });
    }

    @Test
    void reconstructText() {
        final JsonObject.Builder builder = new JsonObject.Builder()
                .put("text", "banana");
        final JsonObject object = builder.build();

        assertEquals(builder.size(), object.size());

        assertDoesNotThrow(() -> {
            final Text text = JsonObject.reconstruct(object, Text.class);
            assertEquals("banana", text.text);
        });
    }

    @Test
    void reflectArea() {
        final Area area = new Area();
        final JsonObject object = JsonObject.reflect(area);

        assertEquals(2, object.size());
        assertEquals(JsonArray.reflect(new Rectangle[] {
                new Rectangle(0, 0, 100, 100),
                new Rectangle(200, 0, 100, 100)
        }), object.get("union"));
        assertEquals(JsonArray.reflect(new Rectangle[] {
                new Rectangle(50, 25, 200, 50)
        }), object.get("subtract"));
    }

    @Test
    void reflectNumbers() {
        final Numbers numbers = new Numbers();
        final JsonObject object = JsonObject.reflect(numbers);

        assertEquals(2, object.size());
        assertEquals(1L, object.get("n0"));
        assertEquals(2L, object.get("n1"));
    }

    @Test
    void reflectText() {
        final Text text = new Text();
        final JsonObject object = JsonObject.reflect(text);

        assertEquals(1, object.size());
        assertEquals("\"Hello, World!\"", object.get("text"));
    }

    static class Area {

        public Rectangle[] union = {
                new Rectangle(0, 0, 100, 100),
                new Rectangle(200, 0, 100, 100)
        };
        public Rectangle[] subtract = {
                new Rectangle(50, 25, 200, 50)
        };
    }

    static class Numbers {

        public long n0 = 1L;
        public long n1 = 2L;
    }

    static class Text {

        public String text = "Hello, World!";
    }
}
