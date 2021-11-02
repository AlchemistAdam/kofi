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

import java.util.concurrent.atomic.AtomicLong;

import dk.martinu.kofi.*;

import static org.junit.jupiter.api.Assertions.*;

public class JsonTest {

    final JsonTestImpl instance = new JsonTestImpl();
    final String javaString = "\0Hello,\r\nWorld!\u001F";
    final String jsonString = "\"\\u0000Hello,\\r\\nWorld!\\u001F\"";

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
        final String java = instance.getJavaString(jsonString);
        assertEquals(javaString, java);
    }

    @Test
    public void getJsonString() {
        final String json = instance.getJsonString(javaString);
        assertEquals(jsonString, json);
    }

    @Test
    public void isHexDigit() {
        final String hexDigits = "0123456789ABCDEFabcdef";
        for (char c : hexDigits.toCharArray())
            assertTrue(instance.isHexDigitImpl(c));

        // this string is not meant to be exhaustive
        final String other = "QWRTYUIOPSGHJKLZXVNMqwrtyuiopsghjklzxvnm!\"#¤%&/()=?`´,.-;:_¨'^*<>\\";
        for (char c : other.toCharArray())
            assertFalse(instance.isHexDigitImpl(c));
    }

    @Test
    public void isTypeDefined() {
        assertTrue(instance.isTypeDefined(null));
        assertTrue(instance.isTypeDefined(instance));
        assertTrue(instance.isTypeDefined(new JsonObject()));
        assertTrue(instance.isTypeDefined(new JsonArray()));
        assertTrue(instance.isTypeDefined("Hello, World!"));
        assertTrue(instance.isTypeDefined(123));
        assertTrue(instance.isTypeDefined(new AtomicLong()));
        assertTrue(instance.isTypeDefined(true));

        assertFalse(instance.isTypeDefined('A'));
        assertFalse(instance.isTypeDefined(this));
    }

    static class JsonTestImpl extends Json {

        @Override
        public Object getDefinedObject(final Object o) {
            return super.getDefinedObject(o);
        }

        @Override
        public String getJavaString(@NotNull final String s) {
            return super.getJavaString(s);
        }

        @Override
        @NotNull
        public String getJsonString(@NotNull final String s) {
            return super.getJsonString(s);
        }

        public boolean isHexDigitImpl(final char c) {
            return isHexDigit(c);
        }

        @Override
        public boolean isTypeDefined(final Object o) {
            return super.isTypeDefined(o);
        }

        @Override
        public void toJson(final Object value, @NotNull final StringBuilder sb) throws IllegalArgumentException {
            super.toJson(value, sb);
        }

        @Override
        @NotNull
        public String toJson() {
            return "";
        }

        @Override
        protected void toJson(final @NotNull StringBuilder sb) { }
    }
}
