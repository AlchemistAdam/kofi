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
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import dk.martinu.kofi.codecs.KofiCodec;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing number parsing implementation of {@link KofiCodec}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NumbersTest {

    final KofiCodec codec = new KofiCodec();

    @Test
    void numbers() {
        // plain number
        assertDoesNotThrow(() -> codec.readString("n = 0"));

        // fractional part
        assertDoesNotThrow(() -> codec.readString("n = .0"));
        assertDoesNotThrow(() -> codec.readString("n = 0."));

        // precision
        assertDoesNotThrow(() -> codec.readString("n = 0d "));
        assertDoesNotThrow(() -> codec.readString("n = 0d"));
        assertDoesNotThrow(() -> codec.readString("n = 0.d"));
        assertDoesNotThrow(() -> codec.readString("n = .0d"));
        assertDoesNotThrow(() -> codec.readString("n = 0e0d"));

        // sign
        assertDoesNotThrow(() -> codec.readString("n = +0"));
        assertDoesNotThrow(() -> codec.readString("n = -0"));
        assertDoesNotThrow(() -> codec.readString("n = +0."));
        assertDoesNotThrow(() -> codec.readString("n = -0."));
        assertDoesNotThrow(() -> codec.readString("n = +.0"));
        assertDoesNotThrow(() -> codec.readString("n = -.0"));

        // exponent part
        assertDoesNotThrow(() -> codec.readString("n = 0e0"));
        assertDoesNotThrow(() -> codec.readString("n = .0e0"));
        assertDoesNotThrow(() -> codec.readString("n = 0.e0"));
        assertDoesNotThrow(() -> codec.readString("n = 0e+0"));
        assertDoesNotThrow(() -> codec.readString("n = 0e-0"));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void numbersError() {
        // empty numbers
        assertThrows(IOException.class, () -> codec.readString("n = ."));
        assertThrows(IOException.class, () -> codec.readString("n = +"));
        assertThrows(IOException.class, () -> codec.readString("n = -"));

        // fractional/exponent part for long
        assertThrows(IOException.class, () -> codec.readString("n = 0.L"));
        assertThrows(IOException.class, () -> codec.readString("n = .0L"));
        assertThrows(IOException.class, () -> codec.readString("n = 0e0L"));

        // double operators
        assertThrows(IOException.class, () -> codec.readString("n = 0.."));
        assertThrows(IOException.class, () -> codec.readString("n = .0."));
        assertThrows(IOException.class, () -> codec.readString("n = ++0"));
        assertThrows(IOException.class, () -> codec.readString("n = +0+"));
        assertThrows(IOException.class, () -> codec.readString("n = --0"));
        assertThrows(IOException.class, () -> codec.readString("n = -0-"));

        // invalid exponents
        assertThrows(IOException.class, () -> codec.readString("n = 0e"));
        assertThrows(IOException.class, () -> codec.readString("n = 0ee"));
        assertThrows(IOException.class, () -> codec.readString("n = 0e++0"));
        assertThrows(IOException.class, () -> codec.readString("n = 0e+0+"));
        assertThrows(IOException.class, () -> codec.readString("n = 0e--0"));
        assertThrows(IOException.class, () -> codec.readString("n = 0e-0-"));
        assertThrows(IOException.class, () -> codec.readString("n = 0e0.0"));

        // invalid number
        assertThrows(IOException.class, () -> codec.readString("n = 0a"));
    }

    @Test
    void numbersJson() {
        // plain number
        assertDoesNotThrow(() -> codec.readString("n = [ 0 ]"));
        assertDoesNotThrow(() -> codec.readString("n = [ 1000 ]"));

        // fractional part
        assertDoesNotThrow(() -> codec.readString("n = [ 0.0 ]"));

        // sign
        assertDoesNotThrow(() -> codec.readString("n = [ -0 ]"));
        assertDoesNotThrow(() -> codec.readString("n = [ -0.0 ]"));

        // exponent part
        assertDoesNotThrow(() -> codec.readString("n = [ 0e0 ]"));
        assertDoesNotThrow(() -> codec.readString("n = [ 0.0e0 ]"));
        assertDoesNotThrow(() -> codec.readString("n = [ 0e+0 ]"));
        assertDoesNotThrow(() -> codec.readString("n = [ 0e-0 ]"));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void numbersJsonError() {
        // invalid number
        assertThrows(IOException.class, () -> codec.readString("n = [ 01 ]"));

        // invalid fractional part
        assertThrows(IOException.class, () -> codec.readString("n = [ 0. ]"));
        assertThrows(IOException.class, () -> codec.readString("n = [ .0 ]"));

        // JSON does not use precision
        assertThrows(IOException.class, () -> codec.readString("n = [ 0d ] "));

        // invalid sign
        assertThrows(IOException.class, () -> codec.readString("n = [ +0 ]"));
    }
}
