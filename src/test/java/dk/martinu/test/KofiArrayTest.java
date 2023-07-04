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
import org.junit.jupiter.api.TestInstance;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KofiArrayTest {

    /**
     * Provides a {@code boolean[]} argument.
     */
    static Stream<boolean[]> booleanArrayProvider() {
        return Stream.of(
                new boolean[] {},
                new boolean[] {true},
                new boolean[] {true, false},
                new boolean[] {true, false, true, false},
                new boolean[] {true, false, true, false, true, false, true, false}
        );
    }

    /**
     * Provides a {@code byte[]} argument.
     */
    static Stream<byte[]> byteArrayProvider() {
        return Stream.of(
                new byte[] {},
                new byte[] {1},
                new byte[] {1, 2},
                new byte[] {1, 2, 3, 4},
                new byte[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides a {@code char[]} argument.
     */
    static Stream<char[]> charArrayProvider() {
        return Stream.of(
                new char[] {},
                new char[] {'A'},
                new char[] {'A', 'B'},
                new char[] {'A', 'B', 'C', 'D'},
                new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'}
        );
    }

    /**
     * Provides a {@code double[]} argument.
     */
    static Stream<double[]> doubleArrayProvider() {
        return Stream.of(
                new double[] {},
                new double[] {1},
                new double[] {1, 2},
                new double[] {1, 2, 3, 4},
                new double[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides a {@code float[]} argument.
     */
    static Stream<float[]> floatArrayProvider() {
        return Stream.of(
                new float[] {},
                new float[] {1},
                new float[] {1, 2},
                new float[] {1, 2, 3, 4},
                new float[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides a {@code int[]} argument.
     */
    static Stream<int[]> intArrayProvider() {
        return Stream.of(
                new int[] {},
                new int[] {1},
                new int[] {1, 2},
                new int[] {1, 2, 3, 4},
                new int[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides a {@code long[]} argument.
     */
    static Stream<long[]> longArrayProvider() {
        return Stream.of(
                new long[] {},
                new long[] {1},
                new long[] {1, 2},
                new long[] {1, 2, 3, 4},
                new long[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides a {@code Object[]} argument.
     */
    static Stream<Arguments> objectArrayProvider() {
        return Stream.of(
                Arguments.of((Object) new Object[] {}),
                Arguments.of((Object) new Object[] {1}),
                Arguments.of((Object) new Object[] {1, true}),
                Arguments.of((Object) new Object[] {1, true, "Hello", Dummy.of(4)}),
                Arguments.of((Object) new Object[] {1, true, "Hello", Dummy.of(4), 2, false, "World", Dummy2.of(4, 8)})
        );
    }

    /**
     * Provides a {@code short[]} argument.
     */
    static Stream<short[]> shortArrayProvider() {
        return Stream.of(
                new short[] {},
                new short[] {1},
                new short[] {1, 2},
                new short[] {1, 2, 3, 4},
                new short[] {1, 2, 3, 4, 5, 6, 7, 8}
        );
    }

    /**
     * Provides a {@code String[]} argument.
     */
    static Stream<Arguments> stringArrayProvider() {
        return Stream.of(
                Arguments.of((Object) new String[] {}),
                Arguments.of((Object) new String[] {"Hello"}),
                Arguments.of((Object) new String[] {"Hello", "World"}),
                Arguments.of((Object) new String[] {"Hello", "World", "Foo", "Bar"}),
                Arguments.of((Object) new String[] {"Hello", "World", "Foo", "Bar", "Baz", "Monkey", "likes", "banana"})
        );
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code boolean} arrays.
     */
    @DisplayName("can construct boolean array")
    @ParameterizedTest
    @MethodSource("booleanArrayProvider")
    public void constructBoolean(final boolean[] booleans) {
        final KofiArray array = new KofiArray(booleans);
        assertEquals(boolean[].class, array.getArrayType());
        assertArrayEquals(booleans, array.construct(boolean[].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with 2 dimensional
     * {@code boolean} arrays.
     */
    @DisplayName("can construct 2 dimensional boolean array")
    @ParameterizedTest
    @MethodSource("booleanArrayProvider")
    public void constructBooleanMulti(final boolean[] booleans) {
        final boolean[][] multi = new boolean[booleans.length][];
        for (int i = 0; i < booleans.length; i++)
            multi[i] = booleans;
        final KofiArray array = new KofiArray((Object[]) multi);
        assertEquals(boolean[][].class, array.getArrayType());
        assertArrayEquals(multi, array.construct(boolean[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code byte} arrays.
     */
    @DisplayName("can construct byte array")
    @ParameterizedTest
    @MethodSource("byteArrayProvider")
    public void constructByte(final byte[] bytes) {
        final KofiArray array = new KofiArray(bytes);
        assertEquals(byte[].class, array.getArrayType());
        assertArrayEquals(bytes, array.construct(byte[].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with 2 dimensional
     * {@code byte} arrays.
     */
    @DisplayName("can construct 2 dimensional byte array")
    @ParameterizedTest
    @MethodSource("byteArrayProvider")
    public void constructByteMulti(final byte[] bytes) {
        final byte[][] multi = new byte[bytes.length][];
        for (int i = 0; i < bytes.length; i++)
            multi[i] = bytes;
        final KofiArray array = new KofiArray((Object[]) multi);
        assertEquals(byte[][].class, array.getArrayType());
        assertArrayEquals(multi, array.construct(byte[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code char} arrays.
     */
    @DisplayName("can construct char array")
    @ParameterizedTest
    @MethodSource("charArrayProvider")
    public void constructChar(final char[] chars) {
        final KofiArray array = new KofiArray(chars);
        assertEquals(char[].class, array.getArrayType());
        assertArrayEquals(chars, array.construct(char[].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with 2 dimensional
     * {@code char} arrays.
     */
    @DisplayName("can construct 2 dimensional char array")
    @ParameterizedTest
    @MethodSource("charArrayProvider")
    public void constructCharMulti(final char[] chars) {
        final char[][] multi = new char[chars.length][];
        for (int i = 0; i < chars.length; i++)
            multi[i] = chars;
        final KofiArray array = new KofiArray((Object[]) multi);
        assertEquals(char[][].class, array.getArrayType());
        assertArrayEquals(multi, array.construct(char[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code double} arrays.
     */
    @DisplayName("can construct double array")
    @ParameterizedTest
    @MethodSource("doubleArrayProvider")
    public void constructDouble(final double[] doubles) {
        final KofiArray array = new KofiArray(doubles);
        assertEquals(double[].class, array.getArrayType());
        assertArrayEquals(doubles, array.construct(double[].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with 2 dimensional
     * {@code double} arrays.
     */
    @DisplayName("can construct 2 dimensional double array")
    @ParameterizedTest
    @MethodSource("doubleArrayProvider")
    public void constructDoubleMulti(final double[] doubles) {
        final double[][] multi = new double[doubles.length][];
        for (int i = 0; i < doubles.length; i++)
            multi[i] = doubles;
        final KofiArray array = new KofiArray((Object[]) multi);
        assertEquals(double[][].class, array.getArrayType());
        assertArrayEquals(multi, array.construct(double[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code float} arrays.
     */
    @DisplayName("can construct float array")
    @ParameterizedTest
    @MethodSource("floatArrayProvider")
    public void constructFloat(final float[] floats) {
        final KofiArray array = new KofiArray(floats);
        assertEquals(float[].class, array.getArrayType());
        assertArrayEquals(floats, array.construct(float[].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with 2 dimensional
     * {@code float} arrays.
     */
    @DisplayName("can construct 2 dimensional float array")
    @ParameterizedTest
    @MethodSource("floatArrayProvider")
    public void constructFloatMulti(final float[] floats) {
        final float[][] multi = new float[floats.length][];
        for (int i = 0; i < floats.length; i++)
            multi[i] = floats;
        final KofiArray array = new KofiArray((Object[]) multi);
        assertEquals(float[][].class, array.getArrayType());
        assertArrayEquals(multi, array.construct(float[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code int} arrays.
     */
    @DisplayName("can construct int array")
    @ParameterizedTest
    @MethodSource("intArrayProvider")
    public void constructInt(final int[] ints) {
        final KofiArray array = new KofiArray(ints);
        assertEquals(int[].class, array.getArrayType());
        assertArrayEquals(ints, array.construct(int[].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with 2 dimensional
     * {@code int} arrays.
     */
    @DisplayName("can construct 2 dimensional int array")
    @ParameterizedTest
    @MethodSource("intArrayProvider")
    public void constructIntMulti(final int[] ints) {
        final int[][] multi = new int[ints.length][];
        for (int i = 0; i < ints.length; i++)
            multi[i] = ints;
        final KofiArray array = new KofiArray((Object[]) multi);
        assertEquals(int[][].class, array.getArrayType());
        assertArrayEquals(multi, array.construct(int[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code long} arrays.
     */
    @DisplayName("can construct long array")
    @ParameterizedTest
    @MethodSource("longArrayProvider")
    public void constructLong(final long[] longs) {
        final KofiArray array = new KofiArray(longs);
        assertEquals(long[].class, array.getArrayType());
        assertArrayEquals(longs, array.construct(long[].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with 2 dimensional
     * {@code long} arrays.
     */
    @DisplayName("can construct 2 dimensional long array")
    @ParameterizedTest
    @MethodSource("longArrayProvider")
    public void constructLongMulti(final long[] longs) {
        final long[][] multi = new long[longs.length][];
        for (int i = 0; i < longs.length; i++)
            multi[i] = longs;
        final KofiArray array = new KofiArray((Object[]) multi);
        assertEquals(long[][].class, array.getArrayType());
        assertArrayEquals(multi, array.construct(long[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code Object} arrays.
     */
    @DisplayName("can construct Object array")
    @ParameterizedTest
    @MethodSource("objectArrayProvider")
    public void constructObject(final Object[] objects) {
        final KofiArray array = new KofiArray(objects);
        assertEquals(Object[].class, array.getArrayType());
        assertArrayEquals(objects, array.construct(Object[].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with 2 dimensional
     * {@code Object} arrays.
     */
    @DisplayName("can construct 2 dimensional Object array")
    @ParameterizedTest
    @MethodSource("objectArrayProvider")
    public void constructObjectMulti(final Object[] objects) {
        final Object[][] multi = new Object[objects.length][];
        for (int i = 0; i < objects.length; i++)
            multi[i] = objects;
        final KofiArray array = new KofiArray((Object[]) multi);
        assertEquals(Object[][].class, array.getArrayType());
        assertArrayEquals(multi, array.construct(Object[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code short} arrays.
     */
    @DisplayName("can construct short array")
    @ParameterizedTest
    @MethodSource("shortArrayProvider")
    public void constructShort(final short[] shorts) {
        final KofiArray array = new KofiArray(shorts);
        assertEquals(short[].class, array.getArrayType());
        assertArrayEquals(shorts, array.construct(short[].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with 2 dimensional
     * {@code short} arrays.
     */
    @DisplayName("can construct 2 dimensional short array")
    @ParameterizedTest
    @MethodSource("shortArrayProvider")
    public void constructShortMulti(final short[] shorts) {
        final short[][] multi = new short[shorts.length][];
        for (int i = 0; i < shorts.length; i++)
            multi[i] = shorts;
        final KofiArray array = new KofiArray((Object[]) multi);
        assertEquals(short[][].class, array.getArrayType());
        assertArrayEquals(multi, array.construct(short[][].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with {@code String} arrays.
     */
    @DisplayName("can construct String array")
    @ParameterizedTest
    @MethodSource("stringArrayProvider")
    public void constructString(final String[] strings) {
        final KofiArray array = new KofiArray(strings);
        assertEquals(String[].class, array.getArrayType());
        assertArrayEquals(strings, array.construct(String[].class));
    }

    /**
     * Test for {@link KofiArray#construct(Class)} with 2 dimensional
     * {@code String} arrays.
     */
    @DisplayName("can construct 2 dimensional String array")
    @ParameterizedTest
    @MethodSource("stringArrayProvider")
    public void constructStringMulti(final String[] strings) {
        final String[][] multi = new String[strings.length][];
        for (int i = 0; i < strings.length; i++)
            multi[i] = strings;
        final KofiArray array = new KofiArray((Object[]) multi);
        assertEquals(String[][].class, array.getArrayType());
        assertArrayEquals(multi, array.construct(String[][].class));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)} with a {@code boolean} array.
     */
    @DisplayName("can reflect boolean array")
    @ParameterizedTest
    @MethodSource("booleanArrayProvider")
    public void reflectBoolean(final boolean[] booleans) {
        final KofiArray array = KofiArray.reflect(booleans);
        assertEquals(boolean[].class, array.getArrayType());
        assertEquals(booleans.length, array.length());
        for (int i = 0; i < booleans.length; i++)
            assertEquals(KofiUtil.getKofiValue(booleans[i]), array.get(i));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)} with a {@code byte} array.
     */
    @DisplayName("can reflect byte array")
    @ParameterizedTest
    @MethodSource("byteArrayProvider")
    public void reflectByte(final byte[] bytes) {
        final KofiArray array = KofiArray.reflect(bytes);
        assertEquals(byte[].class, array.getArrayType());
        assertEquals(bytes.length, array.length());
        for (int i = 0; i < bytes.length; i++)
            assertEquals(KofiUtil.getKofiValue(bytes[i]), array.get(i));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)} with a {@code char} array.
     */
    @DisplayName("can reflect char array")
    @ParameterizedTest
    @MethodSource("charArrayProvider")
    public void reflectChar(final char[] chars) {
        final KofiArray array = KofiArray.reflect(chars);
        assertEquals(char[].class, array.getArrayType());
        assertEquals(chars.length, array.length());
        for (int i = 0; i < chars.length; i++)
            assertEquals(KofiUtil.getKofiValue(chars[i]), array.get(i));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)} with a {@code double} array.
     */
    @DisplayName("can reflect double array")
    @ParameterizedTest
    @MethodSource("doubleArrayProvider")
    public void reflectDouble(final double[] doubles) {
        final KofiArray array = KofiArray.reflect(doubles);
        assertEquals(double[].class, array.getArrayType());
        assertEquals(doubles.length, array.length());
        for (int i = 0; i < doubles.length; i++)
            assertEquals(KofiUtil.getKofiValue(doubles[i]), array.get(i));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)} with a {@code float} array.
     */
    @DisplayName("can reflect float array")
    @ParameterizedTest
    @MethodSource("floatArrayProvider")
    public void reflectFloat(final float[] objects) {
        final KofiArray array = KofiArray.reflect(objects);
        assertEquals(float[].class, array.getArrayType());
        assertEquals(objects.length, array.length());
        for (int i = 0; i < objects.length; i++)
            assertEquals(KofiUtil.getKofiValue(objects[i]), array.get(i));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)} with an {@code int} array.
     */
    @DisplayName("can reflect int array")
    @ParameterizedTest
    @MethodSource("intArrayProvider")
    public void reflectInt(final int[] ints) {
        final KofiArray array = KofiArray.reflect(ints);
        assertEquals(int[].class, array.getArrayType());
        assertEquals(ints.length, array.length());
        for (int i = 0; i < ints.length; i++)
            assertEquals(KofiUtil.getKofiValue(ints[i]), array.get(i));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)} with a {@code long} array.
     */
    @DisplayName("can reflect long array")
    @ParameterizedTest
    @MethodSource("longArrayProvider")
    public void reflectLong(final long[] longs) {
        final KofiArray array = KofiArray.reflect(longs);
        assertEquals(long[].class, array.getArrayType());
        assertEquals(longs.length, array.length());
        for (int i = 0; i < longs.length; i++)
            assertEquals(KofiUtil.getKofiValue(longs[i]), array.get(i));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)} with an {@code Object} array.
     */
    @DisplayName("can reflect Object array")
    @ParameterizedTest
    @MethodSource("objectArrayProvider")
    public void reflectObject(final Object[] objects) {
        final KofiArray array = KofiArray.reflect(objects);
        assertEquals(Object[].class, array.getArrayType());
        assertEquals(objects.length, array.length());
        for (int i = 0; i < objects.length; i++)
            assertEquals(KofiUtil.getKofiValue(objects[i]), array.get(i));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)} with a {@code short} array.
     */
    @DisplayName("can reflect short array")
    @ParameterizedTest
    @MethodSource("shortArrayProvider")
    public void reflectShort(final short[] shorts) {
        final KofiArray array = KofiArray.reflect(shorts);
        assertEquals(short[].class, array.getArrayType());
        assertEquals(shorts.length, array.length());
        for (int i = 0; i < shorts.length; i++)
            assertEquals(KofiUtil.getKofiValue(shorts[i]), array.get(i));
    }

    /**
     * Test for {@link KofiArray#reflect(Object)} with a {@code String} array.
     */
    @DisplayName("can reflect String array")
    @ParameterizedTest
    @MethodSource("stringArrayProvider")
    public void reflectString(final String[] strings) {
        final KofiArray array = KofiArray.reflect(strings);
        assertEquals(String[].class, array.getArrayType());
        assertEquals(strings.length, array.length());
        for (int i = 0; i < strings.length; i++)
            assertEquals(KofiUtil.getKofiValue(strings[i]), array.get(i));
    }
}
