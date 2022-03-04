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

import java.awt.Dimension;

import dk.martinu.kofi.*;

import static org.junit.jupiter.api.Assertions.*;

public class KofiValueTest {

    final KofiValueTestImpl instance = new KofiValueTestImpl();
    final String[] javaStrings = {
            "\0Hello,\r\nWorld!\u001F",
            "",
            "\\\r\n\\\0\\\"",
            "Two muffins sat in an oven.\r\n\nOne muffin said \"Wow, it's getting pretty hot!\"\r\n\nThen the other muffin said \"Wow, a talking muffin!\""
    };
    final String[] kofiStrings = {
            "\"\\0Hello,\\r\\nWorld!\\u001F\"",
            "\"\"",
            "\"\\\\\\r\\n\\\\\\0\\\\\\\"\"",
            "\"Two muffins sat in an oven.\\r\\n\\nOne muffin said \\\"Wow, it's getting pretty hot!\\\"\\r\\n\\nThen the other muffin said \\\"Wow, a talking muffin!\\\"\""
    };

    @Test
    void getJavaString() {
        for (int i = 0; i < javaStrings.length; i++)
            assertEquals(javaStrings[i], instance.getJavaString(kofiStrings[i]));
    }

    @Test
    void getJavaValue() {
        // primitive
        assertEquals(1, instance.getJavaValue(1, int.class));
        assertEquals(1L, instance.getJavaValue(1L, long.class));
        assertEquals(1F, instance.getJavaValue(1F, float.class));
        assertEquals(1d, instance.getJavaValue(1d, double.class));
        assertTrue(instance.getJavaValue(true, boolean.class));
        assertEquals('A', instance.getJavaValue('A', char.class));
        assertEquals((short) 1, instance.getJavaValue((short) 1, short.class));
        assertEquals((byte) 1, instance.getJavaValue((byte) 1, byte.class));

        // wrapper
        assertEquals(1, instance.getJavaValue(1, Integer.class));
        assertEquals(1L, instance.getJavaValue(1L, Long.class));
        assertEquals(1F, instance.getJavaValue(1F, Float.class));
        assertEquals(1d, instance.getJavaValue(1d, Double.class));
        assertTrue(instance.getJavaValue(true, Boolean.class));
        assertEquals('A', instance.getJavaValue('A', Character.class));
        assertEquals((short) 1, instance.getJavaValue((short) 1, Short.class));
        assertEquals((byte) 1, instance.getJavaValue((byte) 1, Byte.class));

        // objects
        final int[] ints = {1, 2, 3, 4};
        assertArrayEquals(ints, instance.getJavaValue(KofiArray.reflect(ints), int[].class));
        final Integer[] Ints = {1, 2, 3, 4};
        assertArrayEquals(Ints, instance.getJavaValue(KofiArray.reflect(ints), Integer[].class));
        final Dimension d = new Dimension(20, 40);
        final KofiObject kofi = KofiObject.reflect(d);
        assertEquals(d, instance.getJavaValue(kofi, Dimension.class));
    }

    @Test
    void getKofiString() {
        for (int i = 0; i < kofiStrings.length; i++)
            assertEquals(kofiStrings[i], instance.getKofiString(javaStrings[i]));
    }

    @Test
    void getKofiValue() {
        assertNull(instance.getKofiValue(null));
        assertEquals(String.class, instance.getKofiValue("").getClass());
        assertEquals(Integer.class, instance.getKofiValue(1).getClass());
        assertEquals(Long.class, instance.getKofiValue(1L).getClass());
        assertEquals(Float.class, instance.getKofiValue(1f).getClass());
        assertEquals(Double.class, instance.getKofiValue(1d).getClass());
        assertEquals(Byte.class, instance.getKofiValue((byte) 1).getClass());
        assertEquals(Short.class, instance.getKofiValue((short) 1).getClass());
        assertEquals(Character.class, instance.getKofiValue('1').getClass());
        assertEquals(KofiArray.class, instance.getKofiValue(new int[] {1, 2, 3}).getClass());
        assertEquals(KofiArray.class, instance.getKofiValue(new Object[] {20, "20"}).getClass());
        assertEquals(KofiArray.class, instance.getKofiValue(new KofiArray()).getClass());
        assertEquals(KofiObject.class, instance.getKofiValue(this).getClass());
        assertEquals(KofiObject.class, instance.getKofiValue(new KofiObject()).getClass());
    }

    @Test
    void isDefinedType() {
        assertTrue(instance.isDefinedType(null));
        assertTrue(instance.isDefinedType(instance));
        assertTrue(instance.isDefinedType(new KofiObject()));
        assertTrue(instance.isDefinedType(new KofiArray()));
        assertTrue(instance.isDefinedType("Hello, World!"));
        assertTrue(instance.isDefinedType(123));
        assertTrue(instance.isDefinedType(true));
        assertTrue(instance.isDefinedType('A'));

        assertFalse(instance.isDefinedType(this));
    }

    /**
     * Test implementation to make methods public
     */
    static class KofiValueTestImpl extends KofiValue {

        @Override
        @NotNull
        public String getJavaString(@NotNull final String string) {
            return super.getJavaString(string);
        }

        @Override
        @NotNull
        public String getKofiString(@NotNull final String string) {
            return super.getKofiString(string);
        }

        @Override
        public Object getKofiValue(final Object o) {
            return super.getKofiValue(o);
        }

        @Override
        public void getString(final Object value, @NotNull final StringBuilder sb) throws IllegalArgumentException {
            super.getString(value, sb);
        }

        @Override
        @NotNull
        public String getString() {
            return "";
        }

        @Override
        public boolean isDefinedType(final Object value) {
            return super.isDefinedType(value);
        }

        @Override
        @NotNull
        protected <V> V getJavaValue(final @NotNull Object value, final @NotNull Class<V> type) {
            return super.getJavaValue(value, type);
        }

        @Override
        protected void getString(final @NotNull StringBuilder sb) { }
    }
}
