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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dk.martinu.kofi.KofiArray;
import dk.martinu.kofi.KofiUtil;
import dk.martinu.test.dummy.Dummy;
import dk.martinu.test.dummy.Dummy2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("KofiArray")
public class KofiArrayTest {

    /**
     * Provides arguments for {@link #constructInt(int[])}
     */
    static Stream<int[]> constructIntProvider() {
        return Stream.of(
                new int[] {},
                new int[] {1},
                new int[] {1, 2},
                new int[] {1, 2, 3, 4},
                new int[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides arguments for {@link #constructLong(long[])}
     */
    static Stream<long[]> constructLongProvider() {
        return Stream.of(
                new long[] {},
                new long[] {1},
                new long[] {1, 2},
                new long[] {1, 2, 3, 4},
                new long[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides arguments for {@link #constructFloat(float[])}
     */
    static Stream<float[]> constructFloatProvider() {
        return Stream.of(
                new float[] {},
                new float[] {1},
                new float[] {1, 2},
                new float[] {1, 2, 3, 4},
                new float[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides arguments for {@link #constructDouble(double[])}
     */
    static Stream<double[]> constructDoubleProvider() {
        return Stream.of(
                new double[] {},
                new double[] {1},
                new double[] {1, 2},
                new double[] {1, 2, 3, 4},
                new double[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides arguments for {@link #constructByte(byte[])}
     */
    static Stream<byte[]> constructByteProvider() {
        return Stream.of(
                new byte[] {},
                new byte[] {1},
                new byte[] {1, 2},
                new byte[] {1, 2, 3, 4},
                new byte[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides arguments for {@link #constructShort(short[])}
     */
    static Stream<short[]> constructShortProvider() {
        return Stream.of(
                new short[] {},
                new short[] {1},
                new short[] {1, 2},
                new short[] {1, 2, 3, 4},
                new short[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides arguments for {@link #constructBoolean(boolean[])}
     */
    static Stream<boolean[]> constructBooleanProvider() {
        return Stream.of(
                new boolean[] {},
                new boolean[] {true},
                new boolean[] {true, false},
                new boolean[] {true, false, true, false},
                new boolean[] {true, false, true, false, true, false, true, false}
        );
    }

    /**
     * Provides arguments for {@link #constructChar(char[])}
     */
    static Stream<char[]> constructCharProvider() {
        return Stream.of(
                new char[] {},
                new char[] {'A'},
                new char[] {'A', 'B'},
                new char[] {'A', 'B', 'C', 'D'},
                new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'}
        );
    }

    /**
     * Provides arguments for {@link #construct(Object[])} and
     * {@link #reflect(Object[])}
     */
    static Stream<Arguments> objectsProvider() {
        return Stream.of(
                Arguments.of((Object) new Object[] {}),
                Arguments.of((Object) new Object[] {1}),
                Arguments.of((Object) new Object[] {1, true}),
                Arguments.of((Object) new Object[] {1, true, "Hello", Dummy.of(4)}),
                Arguments.of((Object) new Object[] {1, true, "Hello", Dummy.of(4), 2, false, "World", Dummy2.of(4, 8)})
        );
    }

    /**
     * Provides arguments for {@link #constructStrings(String[])}.
     */
    static Stream<Arguments> constructStringsProvider() {
        return Stream.of(
                Arguments.of((Object) new String[] {}),
                Arguments.of((Object) new String[] {"Hello"}),
                Arguments.of((Object) new String[] {"Hello", "World"}),
                Arguments.of((Object) new String[] {"Hello", "World", "Foo", "Bar"}),
                Arguments.of((Object) new String[] {"Hello", "World", "Foo", "Bar", "Baz", "Monkey", "likes", "banana"})
        );
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with mixed objects.
     */
    @DisplayName("can construct Object arrays")
    @ParameterizedTest
    @MethodSource("objectsProvider")
    public void construct(final Object[] objects) {
        assertArrayEquals(objects, new KofiArray(objects).construct(Object[].class));

        // 2d array with single element arrays
        final Object[][] o0 = new Object[objects.length][];
        for (int i = 0; i < o0.length; i++)
            o0[i] = new Object[] {objects[i]};
        assertArrayEquals(o0, new KofiArray((Object[]) o0).construct(Object[][].class));

        // 2d array with multiple element arrays
        final Object[][] o1 = new Object[(objects.length + 1) / 2][];
        for (int i = 0, k = 0; i < o1.length; i++, k += 2)
            if (k < objects.length - 1)
                o1[i] = new Object[] {objects[k], objects[k + 1]};
            else
                o1[i] = new Object[] {objects[k]};
        assertArrayEquals(o1, new KofiArray((Object[]) o1).construct(Object[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code boolean} arrays.
     */
    @DisplayName("can construct boolean arrays")
    @ParameterizedTest
    @MethodSource("constructBooleanProvider")
    public void constructBoolean(final boolean[] booleans) {
        assertArrayEquals(booleans, new KofiArray(booleans).construct(boolean[].class));

        // 2d array with single element arrays
        final boolean[][] b0 = new boolean[booleans.length][];
        for (int i = 0; i < b0.length; i++)
            b0[i] = new boolean[] {booleans[i]};
        assertArrayEquals(b0, new KofiArray((Object[]) b0).construct(boolean[][].class));

        // 2d array with multiple element arrays
        final boolean[][] b1 = new boolean[(booleans.length + 1) / 2][];
        for (int i = 0, k = 0; i < b1.length; i++, k += 2)
            if (k < booleans.length - 1)
                b1[i] = new boolean[] {booleans[k], booleans[k + 1]};
            else
                b1[i] = new boolean[] {booleans[k]};
        assertArrayEquals(b1, new KofiArray((Object[]) b1).construct(boolean[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code byte} arrays.
     */
    @DisplayName("can construct byte arrays")
    @ParameterizedTest
    @MethodSource("constructByteProvider")
    public void constructByte(final byte[] bytes) {
        assertArrayEquals(bytes, new KofiArray(bytes).construct(byte[].class));

        // 2d array with single element arrays
        final byte[][] b0 = new byte[bytes.length][];
        for (int i = 0; i < b0.length; i++)
            b0[i] = new byte[] {bytes[i]};
        assertArrayEquals(b0, new KofiArray((Object[]) b0).construct(byte[][].class));

        // 2d array with multiple element arrays
        final byte[][] b1 = new byte[(bytes.length + 1) / 2][];
        for (int i = 0, k = 0; i < b1.length; i++, k += 2)
            if (k < bytes.length - 1)
                b1[i] = new byte[] {bytes[k], bytes[k + 1]};
            else
                b1[i] = new byte[] {bytes[k]};
        assertArrayEquals(b1, new KofiArray((Object[]) b1).construct(byte[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code char} arrays.
     */
    @DisplayName("can construct char arrays")
    @ParameterizedTest
    @MethodSource("constructCharProvider")
    public void constructChar(final char[] chars) {
        assertArrayEquals(chars, new KofiArray(chars).construct(char[].class));

        // 2d array with single element arrays
        final char[][] c0 = new char[chars.length][];
        for (int i = 0; i < c0.length; i++)
            c0[i] = new char[] {chars[i]};
        assertArrayEquals(c0, new KofiArray((Object[]) c0).construct(char[][].class));

        // 2d array with multiple element arrays
        final char[][] c1 = new char[(chars.length + 1) / 2][];
        for (int i = 0, k = 0; i < c1.length; i++, k += 2)
            if (k < chars.length - 1)
                c1[i] = new char[] {chars[k], chars[k + 1]};
            else
                c1[i] = new char[] {chars[k]};
        assertArrayEquals(c1, new KofiArray((Object[]) c1).construct(char[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code double} arrays.
     */
    @DisplayName("can construct double arrays")
    @ParameterizedTest
    @MethodSource("constructDoubleProvider")
    public void constructDouble(final double[] doubles) {
        assertArrayEquals(doubles, new KofiArray(doubles).construct(double[].class));

        // 2d array with single element arrays
        final double[][] d0 = new double[doubles.length][];
        for (int i = 0; i < d0.length; i++)
            d0[i] = new double[] {doubles[i]};
        assertArrayEquals(d0, new KofiArray((Object[]) d0).construct(double[][].class));

        // 2d array with multiple element arrays
        final double[][] d1 = new double[(doubles.length + 1) / 2][];
        for (int i = 0, k = 0; i < d1.length; i++, k += 2)
            if (k < doubles.length - 1)
                d1[i] = new double[] {doubles[k], doubles[k + 1]};
            else
                d1[i] = new double[] {doubles[k]};
        assertArrayEquals(d1, new KofiArray((Object[]) d1).construct(double[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code float} arrays.
     */
    @DisplayName("can construct float arrays")
    @ParameterizedTest
    @MethodSource("constructFloatProvider")
    public void constructFloat(final float[] floats) {
        assertArrayEquals(floats, new KofiArray(floats).construct(float[].class));

        // 2d array with single element arrays
        final float[][] f0 = new float[floats.length][];
        for (int i = 0; i < f0.length; i++)
            f0[i] = new float[] {floats[i]};
        assertArrayEquals(f0, new KofiArray((Object[]) f0).construct(float[][].class));

        // 2d array with multiple element arrays
        final float[][] f1 = new float[(floats.length + 1) / 2][];
        for (int i = 0, k = 0; i < f1.length; i++, k += 2)
            if (k < floats.length - 1)
                f1[i] = new float[] {floats[k], floats[k + 1]};
            else
                f1[i] = new float[] {floats[k]};
        assertArrayEquals(f1, new KofiArray((Object[]) f1).construct(float[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code int} arrays.
     */
    @DisplayName("can construct int arrays")
    @ParameterizedTest
    @MethodSource("constructIntProvider")
    public void constructInt(final int[] ints) {
        assertArrayEquals(ints, new KofiArray(ints).construct(int[].class));

        // 2d array with single element arrays
        final int[][] i0 = new int[ints.length][];
        for (int i = 0; i < i0.length; i++)
            i0[i] = new int[] {ints[i]};
        assertArrayEquals(i0, new KofiArray((Object[]) i0).construct(int[][].class));

        // 2d array with multiple element arrays
        final int[][] i1 = new int[(ints.length + 1) / 2][];
        for (int i = 0, k = 0; i < i1.length; i++, k += 2)
            if (k < ints.length - 1)
                i1[i] = new int[] {ints[k], ints[k + 1]};
            else
                i1[i] = new int[] {ints[k]};
        assertArrayEquals(i1, new KofiArray((Object[]) i1).construct(int[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code long} arrays.
     */
    @DisplayName("can construct long arrays")
    @ParameterizedTest
    @MethodSource("constructLongProvider")
    public void constructLong(final long[] longs) {
        assertArrayEquals(longs, new KofiArray(longs).construct(long[].class));

        // 2d array with single element arrays
        final long[][] L0 = new long[longs.length][];
        for (int i = 0; i < L0.length; i++)
            L0[i] = new long[] {longs[i]};
        assertArrayEquals(L0, new KofiArray((Object[]) L0).construct(long[][].class));

        // 2d array with multiple element arrays
        final long[][] L1 = new long[(longs.length + 1) / 2][];
        for (int i = 0, k = 0; i < L1.length; i++, k += 2)
            if (k < longs.length - 1)
                L1[i] = new long[] {longs[k], longs[k + 1]};
            else
                L1[i] = new long[] {longs[k]};
        assertArrayEquals(L1, new KofiArray((Object[]) L1).construct(long[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code short} arrays.
     */
    @DisplayName("can construct short arrays")
    @ParameterizedTest
    @MethodSource("constructShortProvider")
    public void constructShort(final short[] shorts) {
        assertArrayEquals(shorts, new KofiArray(shorts).construct(short[].class));

        // 2d array with single element arrays
        final short[][] s0 = new short[shorts.length][];
        for (int i = 0; i < s0.length; i++)
            s0[i] = new short[] {shorts[i]};
        assertArrayEquals(s0, new KofiArray((Object[]) s0).construct(short[][].class));

        // 2d array with multiple element arrays
        final short[][] s1 = new short[(shorts.length + 1) / 2][];
        for (int i = 0, k = 0; i < s1.length; i++, k += 2)
            if (k < shorts.length - 1)
                s1[i] = new short[] {shorts[k], shorts[k + 1]};
            else
                s1[i] = new short[] {shorts[k]};
        assertArrayEquals(s1, new KofiArray((Object[]) s1).construct(short[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with mixed objects.
     */
    @DisplayName("can construct String arrays")
    @ParameterizedTest
    @MethodSource("constructStringsProvider")
    public void constructStrings(final String[] strings) {
        assertArrayEquals(strings, new KofiArray(strings).construct(String[].class));

        // 2d array with single element arrays
        final String[][] s0 = new String[strings.length][];
        for (int i = 0; i < s0.length; i++)
            s0[i] = new String[] {strings[i]};
        assertArrayEquals(s0, new KofiArray((Object[]) s0).construct(String[][].class));

        // 2d array with multiple element arrays
        final String[][] s1 = new String[(strings.length + 1) / 2][];
        for (int i = 0, k = 0; i < s1.length; i++, k += 2)
            if (k < strings.length - 1)
                s1[i] = new String[] {strings[k], strings[k + 1]};
            else
                s1[i] = new String[] {strings[k]};
        assertArrayEquals(s1, new KofiArray((Object[]) s1).construct(String[][].class));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)}.
     */
    @DisplayName("can reflect array")
    @ParameterizedTest
    @MethodSource("objectsProvider")
    public void reflect(final Object[] objects) {
        final KofiArray array = KofiArray.reflect(objects);
        assertEquals(Object[].class, array.getArrayType());
        assertEquals(objects.length, array.length());
        for (int i = 0; i < objects.length; i++)
            // need to convert to KoFi value because KoFi array only stores defined types
            assertEquals(KofiUtil.getKofiValue(objects[i]), array.get(i));
    }
}
