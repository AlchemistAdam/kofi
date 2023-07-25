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
import dk.martinu.kofi.KofiObject;
import dk.martinu.kofi.codecs.KofiCodec;
import dk.martinu.test.dummy.Dummy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("parsing type specifiers in objects with KofiCodec")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TypeSpecifierObjectTest {

    static Stream<Arguments> dummyObjectProvider() {
        final String pck = "dk.martinu.test.dummy.Dummy";
        return Stream.of(
                Arguments.of(
                        Dummy.of(1),
                        "obj = {$" + pck + ", v0: 1}"),
                Arguments.of(
                        Dummy.of(2),
                        "obj = {$" + pck + ", v0: 2}")
        );
    }

    @DisplayName("object type specifier")
    @ParameterizedTest
    @MethodSource("dummyObjectProvider")
    public void dummyObject(final Dummy<?> expected, final String str) {
        assertDoesNotThrow(() -> {
            final Document doc = KofiCodec.provider().readString(str);
            final KofiObject object = doc.getObject("obj");
            assertNotNull(object);
            assertEquals(Dummy.class, object.getObjectType());
            final Dummy<?> dummy = object.construct(Dummy.class);
            assertEquals(expected, dummy);
        });
    }
}
