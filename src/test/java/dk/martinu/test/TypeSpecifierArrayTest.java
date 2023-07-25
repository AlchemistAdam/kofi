/*
 * Copyright (c) 2023, Adam Martinu. All rights reserved. Altering or
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

import dk.martinu.kofi.Document;
import dk.martinu.kofi.KofiArray;
import dk.martinu.kofi.codecs.KofiCodec;
import dk.martinu.test.dummy.Dummy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("parsing type specifiers in arrays with KofiCodec")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TypeSpecifierArrayTest {

    /**
     * Provides a {@code boolean[]} and a KoFi string with a matching array.
     */
    static Stream<Arguments> booleanArrayProvider() {
        return Stream.of(
                Arguments.of(
                        new boolean[0],
                        "array = [$boolean]"),
                Arguments.of(
                        new boolean[] {true},
                        "array = [$boolean, true]"),
                Arguments.of(
                        new boolean[] {true, false},
                        "array = [$boolean, true, false]")
        );
    }

    /**
     * Provides a {@code byte[]} and a KoFi string with a matching array.
     */
    static Stream<Arguments> byteArrayProvider() {
        return Stream.of(
                Arguments.of(
                        new byte[0],
                        "array = [$byte]"),
                Arguments.of(
                        new byte[] {1},
                        "array = [$byte, 1]"),
                Arguments.of(
                        new byte[] {1, 2},
                        "array = [$byte, 1, 2]")
        );
    }

    /**
     * Provides a {@code char[]} and a KoFi string with a matching array.
     */
    static Stream<Arguments> charArrayProvider() {
        return Stream.of(
                Arguments.of(
                        new char[0],
                        "array = [$char]"),
                Arguments.of(
                        new char[] {'A'},
                        "array = [$char, 'A']"),
                Arguments.of(
                        new char[] {'A', 'B'},
                        "array = [$char, 'A', 'B']")
        );
    }

    /**
     * Provides a {@code double[]} and a KoFi string with a matching array.
     */
    static Stream<Arguments> doubleArrayProvider() {
        return Stream.of(
                Arguments.of(
                        new double[0],
                        "array = [$double]"),
                Arguments.of(
                        new double[] {1},
                        "array = [$double, 1]"),
                Arguments.of(
                        new double[] {1, 2},
                        "array = [$double, 1, 2]")
        );
    }

    /**
     * Provides a {@code float[]} and a KoFi string with a matching array.
     */
    static Stream<Arguments> floatArrayProvider() {
        return Stream.of(
                Arguments.of(
                        new float[0],
                        "array = [$float]"),
                Arguments.of(
                        new float[] {1},
                        "array = [$float, 1]"),
                Arguments.of(
                        new float[] {1, 2},
                        "array = [$float, 1, 2]")
        );
    }

    /**
     * Provides aa {@code int[]} and a KoFi string with a matching array.
     */
    static Stream<Arguments> intArrayProvider() {
        return Stream.of(
                Arguments.of(
                        new int[0],
                        "array = [$int]"),
                Arguments.of(
                        new int[] {1},
                        "array = [$int, 1]"),
                Arguments.of(
                        new int[] {1, 2},
                        "array = [$int, 1, 2]")
        );
    }

    /**
     * Provides a {@code long[]} and a KoFi string with a matching array.
     */
    static Stream<Arguments> longArrayProvider() {
        return Stream.of(
                Arguments.of(
                        new long[0],
                        "array = [$long]"),
                Arguments.of(
                        new long[] {1},
                        "array = [$long, 1]"),
                Arguments.of(
                        new long[] {1, 2},
                        "array = [$long, 1, 2]")
        );
    }

    /**
     * Provides a multidimensional object array and a KoFi string with a
     * matching array.
     */
    static Stream<Arguments> multiObjectArrayProvider() {
        final String pck = "dk.martinu.test.dummy.Dummy";
        return Stream.of(
                Arguments.of(
                        new Dummy[0][],
                        "array = [$" + pck + "[]]"),
                Arguments.of(
                        new Dummy[][] {{Dummy.of(1)}},
                        "array = [$" + pck + "[], [{v0: 1}]]"),
                Arguments.of(
                        new Dummy[][] {{Dummy.of(1)}, {Dummy.of(2)}},
                        "array = [$" + pck + "[], [{v0: 1}], [{v0: 2}]]"),
                Arguments.of(
                        new Dummy[][][] {{}, {}},
                        "array = [$" + pck + "[][], [], []]"),
                Arguments.of(
                        new Dummy[][][] {{{Dummy.of(1)}}, {{Dummy.of(2)}}},
                        "array = [$" + pck + "[][], [[{v0: 1}]], [[{v0: 2}]]]"),
                Arguments.of(
                        new Dummy[][][] {{{Dummy.of(1), Dummy.of(3)}}, {{Dummy.of(2)}, {Dummy.of(4)}}},
                        "array = [$" + pck + "[][], [[{v0: 1}, {v0: 3}]], [[{v0: 2}], [{v0: 4}]]]")
        );
    }

    /**
     * Provides a multidimensional {@code int} array and a KoFi string with a
     * matching array.
     */
    static Stream<Arguments> multiPrimitiveArrayProvider() {
        return Stream.of(
                Arguments.of(
                        new int[0][],
                        "array = [$int[]]"),
                Arguments.of(
                        new int[][] {{1}},
                        "array = [$int[], [1]]"),
                Arguments.of(
                        new int[][] {{1}, {2}},
                        "array = [$int[], [1], [2]]"),
                Arguments.of(
                        new int[][][] {{}, {}},
                        "array = [$int[][], [], []]"),
                Arguments.of(
                        new int[][][] {{{1}}, {{2}}},
                        "array = [$int[][], [[1]], [[2]]]"),
                Arguments.of(
                        new int[][][] {{{1}, {3}}, {{2}, {4}}},
                        "array = [$int[][], [[1], [3]], [[2], [4]]]")
        );
    }

    /**
     * Provides a {@code short[]} and a KoFi string with a matching array.
     */
    static Stream<Arguments> shortArrayProvider() {
        return Stream.of(
                Arguments.of(
                        new short[0],
                        "array = [$short]"),
                Arguments.of(
                        new short[] {1},
                        "array = [$short, 1]"),
                Arguments.of(
                        new short[] {1, 2},
                        "array = [$short, 1, 2]")
        );
    }

    /**
     * Test for parsing {@code boolean} type specifier.
     */
    @DisplayName("primitive boolean")
    @ParameterizedTest
    @MethodSource("booleanArrayProvider")
    public void booleanArray(final boolean[] source, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiArray array = doc.getArray("array");
            assertNotNull(array);
            assertEquals(boolean[].class, array.getArrayType());
            final boolean[] booleans = array.construct(boolean[].class);
            assertArrayEquals(source, booleans);
        });
    }

    /**
     * Test for parsing {@code byte} type specifier.
     */
    @ParameterizedTest
    @MethodSource("byteArrayProvider")
    public void byteArray(final byte[] source, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiArray array = doc.getArray("array");
            assertNotNull(array);
            assertEquals(byte[].class, array.getArrayType());
            final byte[] bytes = array.construct(byte[].class);
            assertArrayEquals(source, bytes);
        });
    }

    /**
     * Test for parsing {@code char} type specifier.
     */
    @ParameterizedTest
    @MethodSource("charArrayProvider")
    public void charArray(final char[] source, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiArray kofi = doc.getArray("array");
            assertNotNull(kofi);
            assertEquals(char[].class, kofi.getArrayType());
            final char[] chars = kofi.construct(char[].class);
            assertArrayEquals(source, chars);
        });
    }

    /**
     * Test for parsing {@code double} type specifier.
     */
    @ParameterizedTest
    @MethodSource("doubleArrayProvider")
    public void doubleArray(final double[] source, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiArray kofi = doc.getArray("array");
            assertNotNull(kofi);
            assertEquals(double[].class, kofi.getArrayType());
            final double[] doubles = kofi.construct(double[].class);
            assertArrayEquals(source, doubles);
        });
    }

    /**
     * Test for parsing {@code float} type specifier.
     */
    @ParameterizedTest
    @MethodSource("floatArrayProvider")
    public void floatArray(final float[] source, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiArray kofi = doc.getArray("array");
            assertNotNull(kofi);
            assertEquals(float[].class, kofi.getArrayType());
            final float[] floats = kofi.construct(float[].class);
            assertArrayEquals(source, floats);
        });
    }

    /**
     * Test for parsing {@code int} type specifier.
     */
    @ParameterizedTest
    @MethodSource("intArrayProvider")
    public void intArray(final int[] source, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiArray kofi = doc.getArray("array");
            assertNotNull(kofi);
            assertEquals(int[].class, kofi.getArrayType());
            final int[] ints = kofi.construct(int[].class);
            assertArrayEquals(source, ints);
        });
    }

    /**
     * Test for parsing {@code long} type specifier.
     */
    @ParameterizedTest
    @MethodSource("longArrayProvider")
    public void longArray(final long[] source, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiArray kofi = doc.getArray("array");
            assertNotNull(kofi);
            assertEquals(long[].class, kofi.getArrayType());
            final long[] longs = kofi.construct(long[].class);
            assertArrayEquals(source, longs);
        });
    }

    @ParameterizedTest
    @MethodSource("multiObjectArrayProvider")
    public void multiObjectArray(final Object[] source, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiArray kofi = doc.getArray("array");
            assertNotNull(kofi);
            assertEquals(source.getClass(), kofi.getArrayType());
            final Object[] array = kofi.construct(source.getClass());
            assertArrayEquals(source, array);
        });
    }

    /**
     * Test for parsing a multidiemsnional {@code int} array type specifier.
     */
    @ParameterizedTest
    @MethodSource("multiPrimitiveArrayProvider")
    public void multiPrimitiveArray(final Object[] source, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiArray kofi = doc.getArray("array");
            assertNotNull(kofi);
            assertEquals(source.getClass(), kofi.getArrayType());
            final Object[] array = kofi.construct(source.getClass());
            assertArrayEquals(source, array);
        });
    }

    /**
     * Test for parsing {@code short} type specifier.
     */
    @ParameterizedTest
    @MethodSource("shortArrayProvider")
    public void shortArray(final short[] source, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiArray kofi = doc.getArray("array");
            assertNotNull(kofi);
            assertEquals(short[].class, kofi.getArrayType());
            final short[] shorts = kofi.construct(short[].class);
            assertArrayEquals(source, shorts);
        });
    }
}
