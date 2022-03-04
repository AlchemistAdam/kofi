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

import dk.martinu.kofi.Document;
import dk.martinu.kofi.codecs.KofiCodec;

import static org.junit.jupiter.api.Assertions.*;

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
        assertDoesNotThrow(() -> codec.readString("n = 0.0"));

        // precision
        assertDoesNotThrow(() -> codec.readString("n = 0d "));
        assertDoesNotThrow(() -> codec.readString("n = 0d"));
        assertDoesNotThrow(() -> codec.readString("n = 0.d"));
        assertDoesNotThrow(() -> codec.readString("n = .0d"));
        assertDoesNotThrow(() -> codec.readString("n = 0.0d"));
        assertDoesNotThrow(() -> codec.readString("n = 0e0d"));
        assertDoesNotThrow(() -> codec.readString("n = 0.e0d"));
        assertDoesNotThrow(() -> codec.readString("n = .0e0d"));
        assertDoesNotThrow(() -> codec.readString("n = 0.0e0d"));

        // sign
        assertDoesNotThrow(() -> codec.readString("n = +0"));
        assertDoesNotThrow(() -> codec.readString("n = -0"));
        assertDoesNotThrow(() -> codec.readString("n = +0."));
        assertDoesNotThrow(() -> codec.readString("n = -0."));
        assertDoesNotThrow(() -> codec.readString("n = +.0"));
        assertDoesNotThrow(() -> codec.readString("n = -.0"));
        assertDoesNotThrow(() -> codec.readString("n = +0.0"));
        assertDoesNotThrow(() -> codec.readString("n = -0.0"));

        // exponent part
        assertDoesNotThrow(() -> codec.readString("n = 0e0"));
        assertDoesNotThrow(() -> codec.readString("n = .0e0"));
        assertDoesNotThrow(() -> codec.readString("n = 0.e0"));
        assertDoesNotThrow(() -> codec.readString("n = 0.0e0"));
        assertDoesNotThrow(() -> codec.readString("n = 0e+0"));
        assertDoesNotThrow(() -> codec.readString("n = 0e-0"));
        assertDoesNotThrow(() -> codec.readString("n = 0.e+0"));
        assertDoesNotThrow(() -> codec.readString("n = .0e+0"));
        assertDoesNotThrow(() -> codec.readString("n = 0.0e+0"));

        // constants
        assertDoesNotThrow(() -> codec.readString("n = nan"));
        assertDoesNotThrow(() -> codec.readString("n = NAN"));
        assertDoesNotThrow(() -> codec.readString("n = infinity"));
        assertDoesNotThrow(() -> codec.readString("n = INFINITY"));
        assertDoesNotThrow(() -> codec.readString("n = +infinity"));
        assertDoesNotThrow(() -> codec.readString("n = +INFINITY"));
        assertDoesNotThrow(() -> codec.readString("n = -infinity"));
        assertDoesNotThrow(() -> codec.readString("n = -INFINITY"));
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
        assertThrows(IOException.class, () -> codec.readString("n = 0.0L"));
        assertThrows(IOException.class, () -> codec.readString("n = 0e0L"));
        assertThrows(IOException.class, () -> codec.readString("n = 0.e0L"));
        assertThrows(IOException.class, () -> codec.readString("n = .0e0L"));
        assertThrows(IOException.class, () -> codec.readString("n = 0.0e0L"));

        // double operators
        assertThrows(IOException.class, () -> codec.readString("n = 0.."));
        assertThrows(IOException.class, () -> codec.readString("n = .0."));
        assertThrows(IOException.class, () -> codec.readString("n = ..0"));
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
        assertThrows(IOException.class, () -> codec.readString("n = 0e0."));
        assertThrows(IOException.class, () -> codec.readString("n = 0e.0"));
        assertThrows(IOException.class, () -> codec.readString("n = 0e0.0"));

        // invalid number
        assertThrows(IOException.class, () -> codec.readString("n = 0a"));
    }

    @Test
    void numbersKofi() {
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

    @Test
    void numbersValue() {
        final String input = """
                int = 18578282
                long = 623749871478372582L
                float = 8237.98683F
                float_def = 1234.5678
                float_exp = 183.6254e3F
                float_exp_pos = 183.6254e+5F
                float_exp_neg = 183.6254e-6F
                float_exp_def = 1234.5678e2
                double = 942597.29723848213d
                double_exp = 934.9053234e3d
                double_exp_pos = 934.9053234e+5d
                double_exp_neg = 934.9053234e-6d
                nan = NaN
                inf = infinity
                inf_pos = +infinity
                inf_neg = -infinity
                """;
        final Document doc = assertDoesNotThrow(() -> codec.readString(input));

        assertEquals(18578282, doc.getInt("int"));

        assertEquals(623749871478372582L, doc.getLong("long"));

        assertEquals(8237.98683F, doc.getFloat("float"));
        assertEquals(1234.5678F, doc.getFloat("float_def"));
        assertEquals(183.6254e3F, doc.getFloat("float_exp"));
        assertEquals(183.6254e+5F, doc.getFloat("float_exp_pos"));
        assertEquals(183.6254e-6F, doc.getFloat("float_exp_neg"));
        assertEquals((float) 1234.5678e2, doc.getFloat("float_exp_def"));

        assertEquals(942597.29723848213d, doc.getDouble("double"));
        assertEquals(934.9053234e3d, doc.getDouble("double_exp"));
        assertEquals(934.9053234e+5d, doc.getDouble("double_exp_pos"));
        assertEquals(934.9053234e-6d, doc.getDouble("double_exp_neg"));

        assertEquals(Float.NaN, doc.getFloat("nan"));
        assertEquals(Float.POSITIVE_INFINITY, doc.getFloat("inf"));
        assertEquals(Float.POSITIVE_INFINITY, doc.getFloat("inf_pos"));
        assertEquals(Float.NEGATIVE_INFINITY, doc.getFloat("inf_neg"));
    }
}
