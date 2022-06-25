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

import dk.martinu.kofi.KofiArray;
import dk.martinu.test.dummy.Dummy;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KofiArrayTest {

    /**
     * Test for {@link KofiArray#construct(Class)}.
     */
    @Test
    void construct() {
        // int
        {
            final int[] i0 = {1, 2, 3, 4};
            assertArrayEquals(i0, KofiArray.reflect(i0).construct(int[].class));
            final int[][] i1 = {{1}, {2}, {3}, {4}};
            assertArrayEquals(i1, KofiArray.reflect(i1).construct(int[][].class));
            final int[][] i2 = {{1, 2}, {3, 4}};
            assertArrayEquals(i2, KofiArray.reflect(i2).construct(int[][].class));
        }

        // long
        {
            final long[] l0 = {1L, 2L, 3L, 4L};
            assertArrayEquals(l0, KofiArray.reflect(l0).construct(long[].class));
            final long[][] l1 = {{1L}, {2L}, {3L}, {4L}};
            assertArrayEquals(l1, KofiArray.reflect(l1).construct(long[][].class));
            final long[][] l2 = {{1L, 2L}, {3L, 4L}};
            assertArrayEquals(l2, KofiArray.reflect(l2).construct(long[][].class));
        }

        // float
        {
            final float[] f0 = {1.1f, 2.2f, 3.3f, 4.4f};
            assertArrayEquals(f0, KofiArray.reflect(f0).construct(float[].class));
            final float[][] f1 = {{1.1f}, {2.2f}, {3.3f}, {4.4f}};
            assertArrayEquals(f1, KofiArray.reflect(f1).construct(float[][].class));
            final float[][] f2 = {{1.1f, 2.2f}, {3.3f, 4.4f}};
            assertArrayEquals(f2, KofiArray.reflect(f2).construct(float[][].class));
        }
        // double
        {
            final double[] d0 = {1.1d, 2.2d, 3.3d, 4.4d};
            assertArrayEquals(d0, KofiArray.reflect(d0).construct(double[].class));
            final double[][] d1 = {{1.1d}, {2.2d}, {3.3d}, {4.4d}};
            assertArrayEquals(d1, KofiArray.reflect(d1).construct(double[][].class));
            final double[][] d2 = {{1.1d, 2.2d}, {3.3d, 4.4d}};
            assertArrayEquals(d2, KofiArray.reflect(d2).construct(double[][].class));
        }

        // byte
        {
            final byte[] b0 = {1, 2, 3, 4};
            assertArrayEquals(b0, KofiArray.reflect(b0).construct(byte[].class));
            final byte[][] b1 = {{1}, {2}, {3}, {4}};
            assertArrayEquals(b1, KofiArray.reflect(b1).construct(byte[][].class));
            final byte[][] b2 = {{1, 2}, {3, 4}};
            assertArrayEquals(b2, KofiArray.reflect(b2).construct(byte[][].class));
        }

        // short
        {
            final short[] s0 = {1, 2, 3, 4};
            assertArrayEquals(s0, KofiArray.reflect(s0).construct(short[].class));
            final short[][] s1 = {{1}, {2}, {3}, {4}};
            assertArrayEquals(s1, KofiArray.reflect(s1).construct(short[][].class));
            final short[][] s2 = {{1, 2}, {3, 4}};
            assertArrayEquals(s2, KofiArray.reflect(s2).construct(short[][].class));
        }

        // boolean
        {
            final boolean[] b0 = {true, false, true, false};
            assertArrayEquals(b0, KofiArray.reflect(b0).construct(boolean[].class));
            final boolean[][] b1 = {{true}, {false}, {true}, {false}};
            assertArrayEquals(b1, KofiArray.reflect(b1).construct(boolean[][].class));
            final boolean[][] b2 = {{true, false}, {true, false}};
            assertArrayEquals(b2, KofiArray.reflect(b2).construct(boolean[][].class));
        }

        // char
        {
            final char[] c0 = {'A', 'B', 'C', 'D'};
            assertArrayEquals(c0, KofiArray.reflect(c0).construct(char[].class));
            final char[][] c1 = {{'A'}, {'B'}, {'C'}, {'D'}};
            assertArrayEquals(c1, KofiArray.reflect(c1).construct(char[][].class));
            final char[][] c2 = {{'A', 'B'}, {'C', 'D'}};
            assertArrayEquals(c2, KofiArray.reflect(c2).construct(char[][].class));
        }

        // mixed objects
        {
            final Object[] o0 = {
                    1,
                    true,
                    "Hello",
                    new Dummy<>(4)
            };
            assertArrayEquals(o0, KofiArray.reflect(o0).construct(Object[].class));
            final Object[][] o1 = {
                    {1},
                    {true},
                    {"Hello"},
                    {new Dummy<>(4)}
            };
            assertArrayEquals(o1, KofiArray.reflect(o1).construct(Object[][].class));
            final Object[][] o2 = {
                    {1, true},
                    {"Hello", new Dummy<>(4)}
            };
            assertArrayEquals(o2, KofiArray.reflect(o2).construct(Object[][].class));
        }
    }

    /**
     * Test for {@link KofiArray#reflect(Object)}.
     */
    @Test
    void reflect() {
        final Object[] values = {1, true, 'A'};
        final KofiArray array = KofiArray.reflect(values);
        assertEquals(Object[].class, array.getArrayType());
        assertEquals(values.length, array.length());
        for (int i = 0; i < values.length; i++)
            assertEquals(values[i], array.get(i));
    }
}
