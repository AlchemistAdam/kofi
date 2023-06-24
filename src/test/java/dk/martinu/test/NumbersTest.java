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

import dk.martinu.kofi.Document;
import dk.martinu.kofi.codecs.KofiCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing number parsing implementation of {@link KofiCodec}.
 */
@DisplayName("parsing numbers with KofiCodec")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NumbersTest {

    /**
     * Provides arguments for {@link #numbersValue(Number, String)} test. The
     * String arguments represent an unnamed global property and hence start
     * with {@code =}.
     */
    static Stream<Arguments> numbersValueProvider() {
        return Stream.of(
                Arguments.of(18578282, "=18578282"),
                Arguments.of(623749871478372582L, "=623749871478372582L"),
                Arguments.of(8237.98683F, "=8237.98683F"),
                Arguments.of(1234.5678, "=1234.5678"),
                Arguments.of(183.6254e3F, "=183.6254e3F"),
                Arguments.of(183.6254e+5F, "=183.6254e+5F"),
                Arguments.of(1234.5678e2, "=1234.5678e2"),
                Arguments.of(942597.29723848213d, "=942597.29723848213d"),
                Arguments.of(934.9053234e3d, "=934.9053234e3d"),
                Arguments.of(934.9053234e+5d, "=934.9053234e+5d"),
                Arguments.of(934.9053234e-6d, "=934.9053234e-6d"),
                Arguments.of(Float.NaN, "=NaN"),
                Arguments.of(Float.POSITIVE_INFINITY, "=infinity"),
                Arguments.of(Float.POSITIVE_INFINITY, "=+infinity"),
                Arguments.of(Float.NEGATIVE_INFINITY, "=-infinity")
        );
    }

    /**
     * Test for different representations of numbers.
     */
    @DisplayName("can read numbers from string")
    @ParameterizedTest
    @CsvSource({
            // plain number
            "n = 0",
            // fractional part
            "n = .0",
            "n = 0.",
            "n = 0.0",
            // precision
            "n = 0d ",
            "n = 0d",
            "n = 0.d",
            "n = .0d",
            "n = 0.0d",
            "n = 0e0d",
            "n = 0.e0d",
            "n = .0e0d",
            "n = 0.0e0d",
            // sign
            "n = +0",
            "n = -0",
            "n = +0.",
            "n = -0.",
            "n = +.0",
            "n = -.0",
            "n = +0.0",
            "n = -0.0",
            // exponent part
            "n = 0e0",
            "n = .0e0",
            "n = 0.e0",
            "n = 0.0e0",
            "n = 0e+0",
            "n = 0e-0",
            "n = 0.e+0",
            "n = .0e+0",
            "n = 0.0e+0",
            // constants
            "n = nan",
            "n = NAN",
            "n = infinity",
            "n = INFINITY",
            "n = +infinity",
            "n = +INFINITY",
            "n = -infinity",
            "n = -INFINITY"
    })
    public void numbers(final String s) {
        final KofiCodec codec = new KofiCodec();
        assertDoesNotThrow(() -> codec.readString(s));
    }

    /**
     * Test for erroneous representations of numbers
     */
    @DisplayName("throws exception for invalid number representations")
    @ParameterizedTest
    @CsvSource({
            // empty numbers
            "n = .",
            "n = +",
            "n = -",
            // fractional/exponent part for long
            "n = 0.L",
            "n = .0L",
            "n = 0.0L",
            "n = 0e0L",
            "n = 0.e0L",
            "n = .0e0L",
            "n = 0.0e0L",
            // double operators
            "n = 0..",
            "n = .0.",
            "n = ..0",
            "n = ++0",
            "n = +0+",
            "n = --0",
            "n = -0-",
            // invalid exponents
            "n = 0e",
            "n = 0ee",
            "n = 0e++0",
            "n = 0e+0+",
            "n = 0e--0",
            "n = 0e-0-",
            "n = 0e0.",
            "n = 0e.0",
            "n = 0e0.0",
            // invalid number
            "n = 0a"
    })
    public void numbersError(final String s) {
        final KofiCodec codec = new KofiCodec();
        //noinspection ResultOfMethodCallIgnored
        assertThrows(IOException.class, () -> codec.readString(s));
    }

    /**
     * Test for number representations nested inside an array (KofiValue).
     */
    @DisplayName("can read nested numbers from string")
    @ParameterizedTest
    @CsvSource({
            // plain number
            "n = [ 0 ]",
            "n = [ 1000 ]",
            // fractional part
            "n = [ 0.0 ]",
            // sign
            "n = [ -0 ]",
            "n = [ -0.0 ]",
            // exponent part
            "n = [ 0e0 ]",
            "n = [ 0.0e0 ]",
            "n = [ 0e+0 ]",
            "n = [ 0e-0 ]"
    })
    public void numbersKofi(final String s) {
        final KofiCodec codec = new KofiCodec();
        assertDoesNotThrow(() -> codec.readString(s));
    }

    /**
     * Test for asserting that the value of a number is correct when retrieved
     * from a document.
     */
    @DisplayName("can read correct numbers from string")
    @ParameterizedTest
    @MethodSource("numbersValueProvider")
    public void numbersValue(final Number n, final String s) {
        final KofiCodec codec = new KofiCodec();
        final Document doc = assertDoesNotThrow(() -> codec.readString(s));
        assertEquals(n, doc.getValue("", null), () -> "failed with number type: " + n.getClass());
    }
}
