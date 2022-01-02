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

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import dk.martinu.kofi.*;

import static org.junit.jupiter.api.Assertions.*;

public class JsonTest {

    final JsonTestImpl instance = new JsonTestImpl();
    final String[] javaStrings = {
            "\0Hello,\r\nWorld!\u001F",
            "",
            "\\\r\n\\\0\\\"",
            "Two muffins sat in an oven.\r\n\nOne muffin said \"Wow, it's getting pretty hot!\"\r\n\nThen the other muffin said \"Wow, a talking muffin!\""
    };
    final String[] jsonStrings = {
            "\"\\u0000Hello,\\r\\nWorld!\\u001F\"",
            "\"\"",
            "\"\\\\\\r\\n\\\\\\u0000\\\\\\\"\"",
            "\"Two muffins sat in an oven.\\r\\n\\nOne muffin said \\\"Wow, it's getting pretty hot!\\\"\\r\\n\\nThen the other muffin said \\\"Wow, a talking muffin!\\\"\""
    };

    @Test
    public void getDefinedObject() {
        assertNull(instance.getDefinedObject(null));
        assertEquals(String.class, instance.getDefinedObject("").getClass());
        assertEquals(Integer.class, instance.getDefinedObject(1).getClass());
        assertEquals(Long.class, instance.getDefinedObject(1L).getClass());
        assertEquals(Float.class, instance.getDefinedObject(1f).getClass());
        assertEquals(Double.class, instance.getDefinedObject(1d).getClass());
        assertEquals(Byte.class, instance.getDefinedObject((byte) 1).getClass());
        assertEquals(Short.class, instance.getDefinedObject((short) 1).getClass());
        assertEquals(Integer.class, instance.getDefinedObject('1').getClass());
        assertEquals(JsonArray.class, instance.getDefinedObject(new int[] {1, 2, 3}).getClass());
        assertEquals(JsonArray.class, instance.getDefinedObject(new Object[] {20, "20"}).getClass());
        assertEquals(JsonArray.class, instance.getDefinedObject(new JsonArray()).getClass());
        assertEquals(JsonObject.class, instance.getDefinedObject(this).getClass());
        assertEquals(JsonObject.class, instance.getDefinedObject(new JsonObject()).getClass());
    }

    @Test
    public void getJavaString() {
        for (int i = 0; i < javaStrings.length; i++)
            assertEquals(javaStrings[i], instance.getJavaString(jsonStrings[i]));
    }

    @Test
    public void getJsonString() {
        for (int i = 0; i < jsonStrings.length; i++)
            assertEquals(jsonStrings[i], instance.getJsonString(javaStrings[i]));
    }

    @Test
    public void isTypeDefined() {
        assertTrue(instance.isDefinedType(null));
        assertTrue(instance.isDefinedType(instance));
        assertTrue(instance.isDefinedType(new JsonObject()));
        assertTrue(instance.isDefinedType(new JsonArray()));
        assertTrue(instance.isDefinedType("Hello, World!"));
        assertTrue(instance.isDefinedType(123));
        assertTrue(instance.isDefinedType(true));

        assertFalse(instance.isDefinedType('A'));
        assertFalse(instance.isDefinedType(this));
    }

    static class JsonTestImpl extends Json {

        @Override
        public Object getDefinedObject(final Object o) {
            return super.getDefinedObject(o);
        }

        @Override
        public @NotNull String getJavaString(@NotNull final String string) {
            return super.getJavaString(string);
        }

        @Override
        @NotNull
        public String getJsonString(@NotNull final String string) {
            return super.getJsonString(string);
        }

        @Override
        public boolean isDefinedType(final Object o) {
            return super.isDefinedType(o);
        }

        @Override
        public void toJson(final Object value, @NotNull final StringBuilder sb) throws IllegalArgumentException {
            super.toJson(value, sb);
        }

        @Override
        @NotNull
        public String toJson() {
            return "null";
        }

        @Override
        protected void toJson(final @NotNull StringBuilder sb) { }
    }
}
