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

import java.awt.Dimension;

import dk.martinu.kofi.Document;
import dk.martinu.kofi.KofiArray;
import dk.martinu.kofi.codecs.KofiCodec;

import static org.junit.jupiter.api.Assertions.*;

public class KofiArrayTest {

    final KofiCodec codec = KofiCodec.provider();
    final Value<Document> value = new Value<>();

    @Test
    void mixedArray() {
        final Object[] objects = {
                1L,
                "Hello, World!",
                true,
                new KofiArray(1L, 2L, 3L)
        };
        final KofiArray array = new KofiArray(objects);

        assertEquals(objects.length, array.length());
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof String s)
                assertEquals('"' + s + '"', array.get(i));
            else
                assertEquals(objects[i], array.get(i));
        }

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + array.getString())));
        final KofiArray parsedArray = value.get().getArray("v");
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
    void nestedKofiArray() {
        final Object[] objects = {
                new KofiArray(1, 2, 3),
                new KofiArray(4, 5, 6),
                new KofiArray(7, 8, 9)
        };
        final KofiArray array = new KofiArray(objects);

        assertEquals(objects.length, array.length());
        for (int i = 0; i < objects.length; i++)
            assertEquals(objects[i], array.get(i));

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + array.getString())));
        final KofiArray parsedArray = value.get().getArray("v");
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
        final KofiArray array = new KofiArray(objects);

        assertEquals(objects.length, array.length());
        for (int i = 0; i < objects.length; i++)
            assertEquals(KofiArray.reflect(objects[i]), array.get(i));

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + array.getString())));
        final KofiArray parsedArray = value.get().getArray("v");
        assertNotNull(parsedArray);
        assertEquals(array, parsedArray);
        assertEquals(objects.length, parsedArray.length());
        for (int i = 0; i < objects.length; i++)
            if (objects[i] instanceof String s)
                assertEquals('"' + s + '"', parsedArray.get(i));
            else
                assertEquals(KofiArray.reflect(objects[i]), parsedArray.get(i));
    }

    @Test
    void primitiveArray() {
        final long[] fibonacci = {1, 1, 2, 3, 5, 8, 13};
        final KofiArray array = KofiArray.reflect(fibonacci);

        assertEquals(fibonacci.length, array.length());
        for (int i = 0; i < fibonacci.length; i++)
            assertEquals(fibonacci[i], array.get(i));

        assertDoesNotThrow(
                () -> value.put(codec.readString("v = " + array.getString())));
        final KofiArray parsedArray = value.get().getArray("v");
        assertNotNull(parsedArray);
        assertEquals(array, parsedArray);
        assertEquals(fibonacci.length, parsedArray.length());
        for (int i = 0; i < fibonacci.length; i++)
            assertEquals(fibonacci[i], parsedArray.get(i));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void constructNestedPrimitive() {
        KofiArray array;

        array = KofiArray.reflect(new int[][] {{1}, {2}, {3}});
        final int[][] ints = array.construct(int[][].class);
        assertEquals(array.length(), ints.length);
        for (int i = 0; i < ints.length; i++) {
            assertArrayEquals(((KofiArray) array.get(i)).construct(int[].class), ints[i]);
        }

        array = KofiArray.reflect(new long[][] {{1L}, {2L}, {3L}, {4L}});
        final long[][] longs = array.construct(long[][].class);
        assertEquals(array.length(), longs.length);
        for (int i = 0; i < ints.length; i++) {
            assertArrayEquals(((KofiArray) array.get(i)).construct(long[].class), longs[i]);
        }

        array = KofiArray.reflect(new float[][] {{1.3f}, {2.6f}, {3.9f}});
        final float[][] floats = array.construct(float[][].class);
        assertEquals(array.length(), floats.length);
        for (int i = 0; i < floats.length; i++) {
            assertArrayEquals(((KofiArray) array.get(i)).construct(float[].class), floats[i]);
        }

        array = KofiArray.reflect(new double[][] {{1.3d}, {2.6d}, {3.9d}, {4.2d}});
        final double[][] doubles = array.construct(double[][].class);
        assertEquals(array.length(), doubles.length);
        for (int i = 0; i < doubles.length; i++) {
            assertArrayEquals(((KofiArray) array.get(i)).construct(double[].class), doubles[i]);
        }

        array = KofiArray.reflect(new boolean[][] {{true}, {false}, {true}, {false}});
        final boolean[][] booleans = array.construct(boolean[][].class);
        assertEquals(array.length(), booleans.length);
        for (int i = 0; i < booleans.length; i++) {
            assertArrayEquals(((KofiArray) array.get(i)).construct(boolean[].class), booleans[i]);
        }

        array = KofiArray.reflect(new byte[][] {{1}, {2}, {3}, {4}});
        final byte[][] bytes = array.construct(byte[][].class);
        assertEquals(array.length(), bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            assertArrayEquals(((KofiArray) array.get(i)).construct(byte[].class), bytes[i]);
        }

        array = KofiArray.reflect(new short[][] {{1}, {2}, {3}, {4}, {5}});
        final short[][] shorts = array.construct(short[][].class);
        assertEquals(array.length(), shorts.length);
        for (int i = 0; i < shorts.length; i++) {
            assertArrayEquals(((KofiArray) array.get(i)).construct(short[].class), shorts[i]);
        }

        array = KofiArray.reflect(new char[][] {{'A'}, {'B'}, {'C'}});
        final char[][] chars = array.construct(char[][].class);
        assertEquals(array.length(), chars.length);
        for (int i = 0; i < chars.length; i++) {
            assertArrayEquals(((KofiArray) array.get(i)).construct(char[].class), chars[i]);
        }
    }

    @Test
    void constructObject() {
        final Object[] sizes0 = {
                new Dimension(10, 10),
                new Dimension(20, 20),
                new Dimension(30, 30)
        };
        final Dimension[] sizes1 = new KofiArray(sizes0).construct(Dimension[].class);
        assertNotNull(sizes1);
        assertEquals(sizes0.length, sizes1.length);
        assertArrayEquals(sizes0, sizes1);
    }

    @Test
    void constructPrimitive() {
        KofiArray array;

        array = new KofiArray(1, 2, 3);
        final int[] ints = array.construct(int[].class);
        assertEquals(array.length(), ints.length);
        for (int i = 0; i < ints.length; i++) {
            assertEquals(array.get(i), ints[i]);
        }

        array = new KofiArray(1L, 2L, 3L, 4L);
        final long[] longs = array.construct(long[].class);
        assertEquals(array.length(), longs.length);
        for (int i = 0; i < longs.length; i++) {
            assertEquals(array.get(i), longs[i]);
        }

        array = new KofiArray(1.3f, 2.6f, 3.9f);
        final float[] floats = array.construct(float[].class);
        assertEquals(array.length(), floats.length);
        for (int i = 0; i < floats.length; i++) {
            assertEquals(array.get(i), floats[i]);
        }

        array = new KofiArray(1.3d, 2.6d, 3.9d, 4.2d);
        final double[] doubles = array.construct(double[].class);
        assertEquals(array.length(), doubles.length);
        for (int i = 0; i < doubles.length; i++) {
            assertEquals(array.get(i), doubles[i]);
        }

        array = new KofiArray(true, false, true, false);
        final boolean[] booleans = array.construct(boolean[].class);
        assertEquals(array.length(), booleans.length);
        for (int i = 0; i < booleans.length; i++) {
            assertEquals(array.get(i), booleans[i]);
        }

        array = new KofiArray(Byte.valueOf("0"), Byte.valueOf("1"), Byte.valueOf("2"), Byte.valueOf("3"), Byte.valueOf("4"));
        final byte[] bytes = array.construct(byte[].class);
        assertEquals(array.length(), bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(array.get(i), bytes[i]);
        }

        array = new KofiArray(Short.valueOf("1"), Short.valueOf("2"), Short.valueOf("3"), Short.valueOf("4"), Short.valueOf("5"));
        final short[] shorts = array.construct(short[].class);
        assertEquals(array.length(), shorts.length);
        for (int i = 0; i < shorts.length; i++) {
            assertEquals(array.get(i), shorts[i]);
        }

        array = new KofiArray('A', 'B', 'C');
        final char[] chars = array.construct(char[].class);
        assertEquals(array.length(), chars.length);
        for (int i = 0; i < chars.length; i++) {
            assertEquals(array.get(i), chars[i]);
        }
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
