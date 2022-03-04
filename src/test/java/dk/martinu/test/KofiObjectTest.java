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

public class KofiObjectTest {

    final KofiCodec codec = KofiCodec.provider();
    Document document = null;

    @Test
    void mixedObject() {
        final KofiObject.Builder builder = new KofiObject.Builder()
                .put("number", 1L)
                .put("string", "Hello, World!")
                .put("bool", true)
                .put("array", new KofiArray(1, 2, 3));
        final KofiObject object = builder.build();

        assertEquals(builder.size(), object.size());
        for (int i = 0; i < builder.size(); i++) {
            final KofiObject.Entry entry = object.getEntry(i);
            if (entry.getValue() instanceof String s)
                assertEquals('"' + (String) builder.get(entry.getName()) + '"', s);
            else
                assertEquals(builder.get(entry.getName()), entry.getValue());
        }

        assertDoesNotThrow(() -> document = codec.readString("v = " + object.getString()));
        final KofiObject parsedObject = document.getObject("v");
        assertNotNull(parsedObject);
        assertEquals(object, parsedObject);
        assertEquals(builder.size(), parsedObject.size());
        for (int i = 0; i < builder.size(); i++) {
            final KofiObject.Entry entry = parsedObject.getEntry(i);
            if (entry.getValue() instanceof String s)
                assertEquals('"' + (String) builder.get(entry.getName()) + '"', s);
            else
                assertEquals(builder.get(entry.getName()), entry.getValue());
        }
    }

    @Test
    void nestedKofiObject() {
        final KofiObject.Builder builder = new KofiObject.Builder()
                .put("n0", new KofiObject.Builder().put("number", 1L).build())
                .put("n1", new KofiObject.Builder().put("string", "Hello, World").build())
                .put("n2", new KofiObject.Builder().put("bool", false).build());
        final KofiObject object = builder.build();

        assertEquals(builder.size(), object.size());
        for (int i = 0; i < builder.size(); i++) {
            final KofiObject.Entry entry = object.getEntry(i);
            if (entry.getValue() instanceof String s)
                assertEquals('"' + (String) builder.get(entry.getName()) + '"', s);
            else
                assertEquals(builder.get(entry.getName()), entry.getValue());
        }

        assertDoesNotThrow(() -> document = codec.readString("v = " + object.getString()));
        final KofiObject parsedObject = document.getObject("v");
        assertNotNull(parsedObject);
        assertEquals(object, parsedObject);
        assertEquals(builder.size(), parsedObject.size());
        for (int i = 0; i < builder.size(); i++) {
            final KofiObject.Entry entry = parsedObject.getEntry(i);
            if (entry.getValue() instanceof String s)
                assertEquals('"' + (String) builder.get(entry.getName()) + '"', s);
            else
                assertEquals(builder.get(entry.getName()), entry.getValue());
        }
    }

    @Test
    void reconstructArea() {
        final KofiObject.Builder builder = new KofiObject.Builder()
                .put("union", new Rectangle[] {new Rectangle(0, 0, 400, 100),
                        new Rectangle(0, 300, 400, 100)})
                .put("subtract", new Rectangle[] {new Rectangle(50, 50, 300, 300)})
                .put("bounds", new Rectangle(13, 16, 100, 80));
        final KofiObject object = builder.build();

        assertEquals(builder.size(), object.size());

        assertDoesNotThrow(() -> {
            final Area area = object.reconstruct(Area.class);
            assertArrayEquals(new Rectangle[] {new Rectangle(0, 0, 400, 100),
                    new Rectangle(0, 300, 400, 100)}, area.union);
            assertArrayEquals(new Rectangle[] {new Rectangle(50, 50, 300, 300)}, area.subtract);
            assertEquals(new Rectangle(13, 16, 100, 80), area.bounds);
        });
    }

    @Test
    void reconstructNumbers() {
        final KofiObject.Builder builder = new KofiObject.Builder()
                .put("n0", 4L)
                .put("n1", 8L);
        final KofiObject object = builder.build();

        assertEquals(builder.size(), object.size());
        for (int i = 0; i < builder.size(); i++) {
            final KofiObject.Entry entry = object.getEntry(i);
            assertEquals(builder.get(entry.getName()), entry.getValue());
        }

        assertDoesNotThrow(() -> {
            final Numbers numbers = object.reconstruct(Numbers.class);
            assertEquals(4L, numbers.n0);
            assertEquals(8L, numbers.n1);
        });
    }

    @Test
    void reconstructText() {
        final KofiObject.Builder builder = new KofiObject.Builder()
                .put("text", "banana");
        final KofiObject object = builder.build();

        assertEquals(builder.size(), object.size());

        assertDoesNotThrow(() -> {
            final Text text = object.reconstruct(Text.class);
            assertEquals("banana", text.text);
        });
    }

    @Test
    void reflectArea() {
        final Area area = new Area();
        final KofiObject object = KofiObject.reflect(area);

        assertEquals(Area.class.getFields().length, object.size());
        assertEquals(KofiArray.reflect(new Rectangle[] {
                new Rectangle(0, 0, 100, 100),
                new Rectangle(200, 0, 100, 100)
        }), object.get("union"));
        assertEquals(KofiArray.reflect(new Rectangle[] {
                new Rectangle(50, 25, 200, 50)
        }), object.get("subtract"));
        assertEquals(KofiObject.reflect(new Rectangle(13, 16, 100, 80)), object.get("bounds"));
    }

    @Test
    void reflectNumbers() {
        final Numbers numbers = new Numbers();
        final KofiObject object = KofiObject.reflect(numbers);

        assertEquals(2, object.size());
        assertEquals(1L, object.get("n0"));
        assertEquals(2L, object.get("n1"));
    }

    @Test
    void reflectText() {
        final Text text = new Text();
        final KofiObject object = KofiObject.reflect(text);

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
        public Rectangle bounds = new Rectangle(13, 16, 100, 80);
    }

    static class Numbers {

        public long n0 = 1L;
        public long n1 = 2L;
    }

    static class Text {

        public String text = "Hello, World!";
    }
}
